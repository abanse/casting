package com.hydro.casting.server.ejb.downtime;

import com.hydro.casting.common.Casting;
import com.hydro.casting.common.constant.CasterStep;
import com.hydro.casting.common.constant.FurnaceStep;
import com.hydro.casting.common.constant.MelterStep;
import com.hydro.casting.common.downtime.DowntimeConstants;
import com.hydro.casting.server.contract.downtime.DowntimeView;
import com.hydro.casting.server.contract.downtime.dto.DowntimeDTO;
import com.hydro.casting.server.contract.dto.*;
import com.hydro.casting.server.ejb.melting.service.MeltingFurnaceKTService;
import com.hydro.casting.server.ejb.planning.service.CastingKTService;
import com.hydro.casting.server.ejb.planning.service.MeltingKTService;
import com.hydro.casting.server.ejb.prod.service.ActualValuesProductionService;
import com.hydro.casting.server.model.av.ActualValues;
import com.hydro.casting.server.model.av.ActualValuesDefinition;
import com.hydro.casting.server.model.av.ActualValuesHeader;
import com.hydro.casting.server.model.av.dao.ActualValuesHeaderHome;
import com.hydro.casting.server.model.sched.MeltingBatch;
import com.hydro.casting.server.model.sched.ProcessStep;
import com.hydro.casting.server.model.sched.dao.MeltingBatchHome;
import com.hydro.core.common.util.DateTimeUtil;
import com.hydro.core.common.util.TimePeriod;
import com.hydro.core.server.common.model.AliasToBeanTrimCharResultTransformer;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.sql.Blob;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class provides data required in the frontend / UI relating to downtimes and time managements.
 */
@Stateless
public class DowntimeViewBean implements DowntimeView
{
    private final static Logger log = LoggerFactory.getLogger( DowntimeViewBean.class );

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    @EJB
    private ActualValuesHeaderHome actualValuesHeaderHome;
    @EJB
    private ActualValuesProductionService actualValuesProductionService;
    @EJB
    private MeltingBatchHome meltingBatchHome;

    @EJB
    private CastingKTService castingKTService;
    @EJB
    private MeltingKTService meltingKTService;
    @EJB
    private MeltingFurnaceKTService meltingFurnaceKTService;

    //@formatter:off
    private final static String DOWNTIME_QUERY =
            "SELECT dt.objid as id, "
            + "dt.costCenter as costCenter, "
            + "dt.machine as machine, "
            + "dt.fromTS as fromTS, "
            + "dt.endTS as endTS, "
            + "dt.shift as shift, "
            + "dt.amount as amount, "
            + "dt.description as description, "
            + "dt.remark as remark, "
            + "dtk.kind1 as downtimeKind1, "
            + "dtk.kind2 as downtimeKind2, "
            + "dtk.kind3 as downtimeKind3, "
            + "dtk.description as downtimeDescription, "
            + "dtm.module as module, "
            + "dtm.component as component, "
            + "dtm.description as moduleDescription, "
            + "dtm.erpIdent as moduleErpIdent, "
            + "dt.userId as userId, "
            + "dt.type as type "
            + "FROM Downtime dt left outer join dt.downtimeKind as dtk "
            + "left outer join dt.downtimeModule as dtm ";

    private final static String WHERE_AUTO_MACHINES_RANGE = "WHERE dt.fromTS >= :startDate AND dt.fromTS <= :endDate "
            + "AND dt.costCenter in :costCenters "
            + "AND dt.type = :type "
            + "ORDER BY dt.costCenter, dt.fromTS, dt.endTS";

    private final static String WHERE_RANGE = "WHERE ( dt.fromTS >= :startDate AND dt.fromTS <= :endDate ) "
            + "OR ( dt.endTS >= :startDate AND dt.endTS <= :endDate ) "
            + "ORDER BY dt.costCenter, dt.fromTS, dt.endTS";
    //@formatter:on

    @Override
    public List<DowntimeDTO> loadDowntimes( LocalDateTime start, LocalDateTime end )
    {
        Query downtimeQuery = entityManager.unwrap( Session.class ).createQuery( DOWNTIME_QUERY + WHERE_RANGE );
        downtimeQuery.setParameter( "startDate", start );
        downtimeQuery.setParameter( "endDate", end );

        downtimeQuery.setResultTransformer( new AliasToBeanTrimCharResultTransformer( DowntimeDTO.class ) );

        return downtimeQuery.list();
    }

    /**
     * Returns all downtimes for a range of time for a number of machines, sorted in a map by machine.
     *
     * @param machines The machines to return the downtimes for
     * @param fromTS   Start timestamp of the time range
     * @param toTS     End timestamp of the time range
     * @return Map containing a mapping from machine to the machines list of downtimes
     */
    @Override
    public Map<String, List<DowntimeDTO>> loadDowntimes( Collection<String> machines, LocalDateTime fromTS, LocalDateTime toTS )
    {
        Map<String, List<DowntimeDTO>> resultMap = new HashMap<>();

        for ( String machine : machines )
        {
            String[] costCenters = Casting.getDowntimeCostCenters( machine );
            Query downtimeQuery = entityManager.unwrap( Session.class ).createQuery( DOWNTIME_QUERY + WHERE_AUTO_MACHINES_RANGE );
            downtimeQuery.setParameter( "costCenters", costCenters != null ? List.of( costCenters ) : List.of( machine ) );
            downtimeQuery.setParameter( "startDate", fromTS );
            downtimeQuery.setParameter( "endDate", toTS );
            downtimeQuery.setParameter( "type", DowntimeConstants.DOWNTIME_TYPE.AUTO );

            downtimeQuery.setResultTransformer( new AliasToBeanTrimCharResultTransformer( DowntimeDTO.class ) );

            resultMap.put( machine, downtimeQuery.list() );
        }

        return resultMap;
    }

    /**
     * Function to load the time managements by machines. Applicable only for casting area, since the base data are ActualValues (data retrieved from machine signals), which only exist
     * for casting area.
     * Function loads all ActualValues with lastMeasuredTS inside the timeframe defined by fromTS and toTS and transforms them to TimeManagements and TimeManagementPhases.
     *
     * @param machines   List of machines to load the time managements for
     * @param fromTS     Starting timestamp for the timeframe
     * @param toTS       Ending timestamp for the timeframe
     * @param withFuture Determines, if the future (planned) casting batches should be included
     * @return A list of TimeManagements with their TimeManagementPhases
     */
    @Override
    public List<TimeManagementDTO> loadTimeManagements( Collection<String> machines, LocalDateTime fromTS, LocalDateTime toTS, boolean withFuture )
    {
        log.info( "loadTimeManagements " + fromTS + "<->" + toTS );

        final List<ActualValuesHeader> headers = actualValuesHeaderHome.findByMachinesAndTimeRange( "PLC_SIGNALS", machines, fromTS, toTS );

        boolean changeFurnace = false;
        final List<TimeManagementVO> timeManagementsVO = new ArrayList<>();
        for ( ActualValuesHeader header : headers )
        {
            final ActualValues actualValues = header.getValues();
            final ActualValuesDefinition definition = header.getDefinition();
            int posVorwahlOfen = definition.getPos( "Vorwahl_Ofen" );
            if ( posVorwahlOfen < 0 )
            {
                posVorwahlOfen = definition.getPos( "Vorwahlofen" ); // Anlage 70 benutzt einen anderen Namen
            }

            final TimeManagementVO timeManagementVO = new TimeManagementVO();
            timeManagementVO.charge = header.getRefName();
            timeManagementVO.machine = header.getSource();
            final TimePeriod castingStartAndEnd = actualValuesProductionService.findCastingStartAndEnd( header );
            if ( castingStartAndEnd == null )
            {
                log.error( "charge " + header.getRefName() + " not parseable for start and end" );
                continue;
            }
            final LocalDateTime startGI = castingStartAndEnd.getStartDateInclusive();
            timeManagementVO.castingStartAndEnd = castingStartAndEnd;
            double ofenvorwahl = 0;
            if ( posVorwahlOfen >= 0 )
            {
                final byte[] vorwahlOfenAllBytes = getBytesFromBlob( actualValues.getValue( posVorwahlOfen ) );
                if ( vorwahlOfenAllBytes == null )
                {
                    continue;
                }
                int readPos = 0;
                final byte[] timeBytes = new byte[8];
                final byte[] valueBytes = new byte[8];

                while ( vorwahlOfenAllBytes.length > readPos )
                {
                    System.arraycopy( vorwahlOfenAllBytes, readPos, timeBytes, 0, timeBytes.length );
                    readPos = readPos + timeBytes.length;
                    System.arraycopy( vorwahlOfenAllBytes, readPos, valueBytes, 0, valueBytes.length );
                    readPos = readPos + valueBytes.length;
                    final long timestamp = fromByteArray( timeBytes );
                    final double actualValueNumber = Double.longBitsToDouble( fromByteArray( valueBytes ) );
                    final LocalDateTime signalStart = DateTimeUtil.asLocalDateTime( timestamp );
                    if ( signalStart.isAfter( startGI ) )
                    {
                        continue;
                    }
                    ofenvorwahl = actualValueNumber;
                }
            }
            else
            {
                if ( changeFurnace )
                {
                    ofenvorwahl = 1;
                }
                changeFurnace = !changeFurnace;
            }
            timeManagementVO.ofenvorwahl = ofenvorwahl;

            timeManagementsVO.add( timeManagementVO );
        }

        final List<CastingKTDTO> castingKTDTOs = castingKTService.load();
        final List<MeltingKTDTO> meltingKTDTOs = meltingKTService.load();

        if ( withFuture )
        {
            machines.forEach( machine -> {
                log.info( "machine " + machine );
                final Optional<CastingKTDTO> castingKT = castingKTService.findCastingKnowledge( castingKTDTOs, machine, null );
                int castingTM = 100;
                int unloadingTM = 90;
                if ( castingKT.isPresent() )
                {
                    castingTM = Math.abs( castingKT.get().getCastingTM() );
                    unloadingTM = Math.abs( castingKT.get().getUnloadingTM() );
                }

                LocalDateTime currentCastingTS = LocalDateTime.now();
                double currentFurnace = 0;
                final Optional<TimeManagementVO> lastMachineTimeManagementVO = timeManagementsVO.stream().filter( timeManagementVO -> Objects.equals( timeManagementVO.machine, machine ) )
                        .max( Comparator.comparing( o -> o.castingStartAndEnd.getEndDateExclusive() ) );
                if ( lastMachineTimeManagementVO.isPresent() )
                {
                    if ( lastMachineTimeManagementVO.get().castingStartAndEnd.getStartDateInclusive().plusMinutes( castingTM + unloadingTM ).isAfter( currentCastingTS ) )
                    {
                        currentCastingTS = lastMachineTimeManagementVO.get().castingStartAndEnd.getStartDateInclusive().plusMinutes( castingTM + unloadingTM );
                    }
                    if ( lastMachineTimeManagementVO.get().ofenvorwahl == 0 )
                    {
                        currentFurnace = 1;
                    }
                }

                // Finde CastingBatches die produziert werden sollen
                final TypedQuery<String> castingChargesQuery = entityManager.createQuery(
                        "select cb.charge from CastingBatch cb where cb.executingMachine.apk = :machine and cb.executionState = 250 order by cb.executingSequenceIndex", String.class );
                castingChargesQuery.setParameter( "machine", machine );
                final List<String> castingCharges = castingChargesQuery.getResultList();
                if ( castingCharges == null )
                {
                    log.info( "castingCharges is null" );
                }
                else
                {
                    log.info( "castingCharges " + castingCharges.size() );
                }

                if ( castingCharges != null && !castingCharges.isEmpty() )
                {
                    for ( String charge : castingCharges )
                    {
                        final TimeManagementVO timeManagementVO = new TimeManagementVO();
                        timeManagementVO.charge = charge.substring( 2 );
                        timeManagementVO.machine = machine;
                        timeManagementVO.castingStartAndEnd = TimePeriod.between( currentCastingTS, currentCastingTS.plusMinutes( castingTM ) );
                        timeManagementVO.ofenvorwahl = currentFurnace;

                        timeManagementsVO.add( timeManagementVO );

                        currentCastingTS = currentCastingTS.plusMinutes( castingTM + unloadingTM );
                        if ( currentFurnace == 0 )
                        {
                            currentFurnace = 1;
                        }
                        else
                        {
                            currentFurnace = 0;
                        }
                    }
                }
            } );
        }

        // Stores the time managements per machine; key is the name of the machine
        Map<String, List<TimeManagementDTO>> machineTimeManagements = new HashMap<>();

        for ( TimeManagementVO timeManagementVO : timeManagementsVO )
        {
            final TimeManagementDTO castingTM = new TimeManagementDTO();
            castingTM.setCharge( timeManagementVO.charge );
            castingTM.setMachine( timeManagementVO.machine );

            final Optional<CastingKTDTO> castingKT = castingKTService.findCastingKnowledge( castingKTDTOs, castingTM.getMachine(), null );
            final TimePeriod castingStartAndEnd = timeManagementVO.castingStartAndEnd;

            final List<TimeManagementPhaseDTO> castingPhases = new ArrayList<>();

            // Gießen
            final TimeManagementPhaseDTO castingGIPhase = new TimeManagementPhaseDTO();
            final LocalDateTime startGI = castingStartAndEnd.getStartDateInclusive();
            final LocalDateTime endGI = castingStartAndEnd.getEndDateExclusive();
            castingGIPhase.setName( CasterStep.Casting.getShortName() );
            castingGIPhase.setStart( startGI );
            castingGIPhase.setActualDuration( Duration.between( startGI, endGI ) );
            if ( castingKT.isPresent() )
            {
                castingGIPhase.setPlannedDuration( Duration.ofMinutes( Math.abs( castingKT.get().getCastingTM() ) ) );
            }
            else
            {
                castingGIPhase.setPlannedDuration( Duration.between( startGI, endGI ) );
            }
            castingPhases.add( castingGIPhase );
            //            // Anlage abfahren und reinigen
            //            final TimeManagementPhaseDTO castingAFPhase = new TimeManagementPhaseDTO();
            //            castingAFPhase.setName( CasterStep.Resting.getShortName() );
            //            castingAFPhase.setStart( endGI );
            //            castingAFPhase.setActualDuration( Duration.ofMinutes( 93 ) );
            //            castingAFPhase.setPlannedDuration( Duration.ofMinutes( 93 ) );
            //            castingPhases.add( castingAFPhase );

            castingTM.setPhases( castingPhases );
            addTimeManagementToMachine( machineTimeManagements, castingTM );

            final TimeManagementDTO furnaceTM = new TimeManagementDTO();
            furnaceTM.setCharge( timeManagementVO.charge );
            final Optional<MeltingKTDTO> meltingKT;
            if ( timeManagementVO.ofenvorwahl == 1 )
            {
                furnaceTM.setMachine( timeManagementVO.machine.charAt( 0 ) + "1" );
                meltingKT = meltingKTService.findMeltingKnowledge( meltingKTDTOs, castingTM.getMachine(), timeManagementVO.machine.charAt( 0 ) + "1", null );
            }
            else
            {
                furnaceTM.setMachine( timeManagementVO.machine.charAt( 0 ) + "2" );
                meltingKT = meltingKTService.findMeltingKnowledge( meltingKTDTOs, castingTM.getMachine(), timeManagementVO.machine.charAt( 0 ) + "2", null );
            }

            addTimeManagementToMachine( machineTimeManagements, furnaceTM );

            final List<TimeManagementPhaseDTO> furnacePhases = new ArrayList<>();

            final TimeManagementPhaseDTO furnaceCHPhase = new TimeManagementPhaseDTO();
            furnaceCHPhase.setName( FurnaceStep.Charging.getShortName() );
            if ( meltingKT.isPresent() )
            {
                furnaceCHPhase.setStart( startGI.minusMinutes( meltingKT.get().getRestingTM() + meltingKT.get().getSkimmingTM() + meltingKT.get().getTreatingTM() + meltingKT.get().getChargingTM() ) );
                furnaceCHPhase.setActualDuration( Duration.ofMinutes( Math.abs( meltingKT.get().getChargingTM() ) ) );
                furnaceCHPhase.setPlannedDuration( Duration.ofMinutes( Math.abs( meltingKT.get().getChargingTM() ) ) );
            }
            else
            {
                furnaceCHPhase.setStart( startGI.minusMinutes( 60 + 30 + 60 + 180 ) );
                furnaceCHPhase.setActualDuration( Duration.ofMinutes( 180 ) );
                furnaceCHPhase.setPlannedDuration( Duration.ofMinutes( 180 ) );
            }
            furnacePhases.add( furnaceCHPhase );

            final TimeManagementPhaseDTO furnaceGAPhase = new TimeManagementPhaseDTO();
            furnaceGAPhase.setName( FurnaceStep.Treating.getShortName() );
            if ( meltingKT.isPresent() )
            {
                furnaceGAPhase.setStart( startGI.minusMinutes( meltingKT.get().getRestingTM() + meltingKT.get().getSkimmingTM() + meltingKT.get().getTreatingTM() ) );
                furnaceGAPhase.setActualDuration( Duration.ofMinutes( Math.abs( meltingKT.get().getTreatingTM() ) ) );
                furnaceGAPhase.setPlannedDuration( Duration.ofMinutes( Math.abs( meltingKT.get().getTreatingTM() ) ) );
            }
            else
            {
                furnaceGAPhase.setStart( startGI.minusMinutes( 60 + 30 + 60 ) );
                furnaceGAPhase.setActualDuration( Duration.ofMinutes( 60 ) );
                furnaceGAPhase.setPlannedDuration( Duration.ofMinutes( 60 ) );
            }
            furnacePhases.add( furnaceGAPhase );

            final TimeManagementPhaseDTO furnaceAKPhase = new TimeManagementPhaseDTO();
            furnaceAKPhase.setName( FurnaceStep.Skimming.getShortName() );
            if ( meltingKT.isPresent() )
            {
                furnaceAKPhase.setStart( startGI.minusMinutes( meltingKT.get().getRestingTM() + meltingKT.get().getSkimmingTM() ) );
                furnaceAKPhase.setActualDuration( Duration.ofMinutes( Math.abs( meltingKT.get().getSkimmingTM() ) ) );
                furnaceAKPhase.setPlannedDuration( Duration.ofMinutes( Math.abs( meltingKT.get().getSkimmingTM() ) ) );
            }
            else
            {
                furnaceAKPhase.setStart( startGI.minusMinutes( 60 + 30 ) );
                furnaceAKPhase.setActualDuration( Duration.ofMinutes( 30 ) );
                furnaceAKPhase.setPlannedDuration( Duration.ofMinutes( 30 ) );
            }
            furnacePhases.add( furnaceAKPhase );

            final TimeManagementPhaseDTO furnaceASPhase = new TimeManagementPhaseDTO();
            furnaceASPhase.setName( FurnaceStep.Resting.getShortName() );
            if ( meltingKT.isPresent() )
            {
                furnaceASPhase.setStart( startGI.minusMinutes( meltingKT.get().getRestingTM() ) );
                furnaceASPhase.setActualDuration( Duration.ofMinutes( Math.abs( meltingKT.get().getRestingTM() ) ) );
                furnaceASPhase.setPlannedDuration( Duration.ofMinutes( Math.abs( meltingKT.get().getRestingTM() ) ) );
            }
            else
            {
                furnaceASPhase.setStart( startGI.minusMinutes( 60 ) );
                furnaceASPhase.setActualDuration( Duration.ofMinutes( 60 ) );
                furnaceASPhase.setPlannedDuration( Duration.ofMinutes( 60 ) );
            }
            furnacePhases.add( furnaceASPhase );

            final TimeManagementPhaseDTO furnaceGIPhase = new TimeManagementPhaseDTO();
            furnaceGIPhase.setName( FurnaceStep.Casting.getShortName() );
            furnaceGIPhase.setStart( startGI );
            furnaceGIPhase.setActualDuration( Duration.between( startGI, endGI ) );
            if ( meltingKT.isPresent() )
            {
                furnaceGIPhase.setPlannedDuration( Duration.ofMinutes( Math.abs( meltingKT.get().getCastingTM() ) ) );
            }
            else
            {
                furnaceGIPhase.setPlannedDuration( Duration.between( startGI, endGI ) );
            }
            furnacePhases.add( furnaceGIPhase );

            furnaceTM.setPhases( furnacePhases );
        }

        machineTimeManagements.values().forEach( this::correctOverlapping );

        machineTimeManagements.forEach( ( machine, machineTMs ) -> {
            if ( machine.endsWith( "0" ) )
            {
                final Optional<CastingKTDTO> castingKT = castingKTService.findCastingKnowledge( castingKTDTOs, machine, null );
                if ( castingKT.isPresent() )
                {
                    fillAfter( machineTMs, CasterStep.Unloading.getShortName(), Math.abs( castingKT.get().getUnloadingTM() ) );
                }
                else
                {
                    fillAfter( machineTMs, CasterStep.Unloading.getShortName(), 188 );
                }
            }
            else
            {
                final Optional<MeltingKTDTO> meltingKT = meltingKTService.findMeltingKnowledge( meltingKTDTOs, machine.charAt( 0 ) + "0", machine, null );
                if ( meltingKT.isPresent() )
                {
                    fillAfter( machineTMs, FurnaceStep.Preparing.getShortName(), Math.abs( meltingKT.get().getPreparingTM() ) );
                }
                else
                {
                    fillAfter( machineTMs, FurnaceStep.Preparing.getShortName(), 45 );
                }
            }
        } );

        //        setPlannedStarts( casterTMs );
        //        setPlannedStarts( furnace1TMs );
        //        setPlannedStarts( furnace2TMs );

        return machineTimeManagements.values().stream().flatMap( Collection::stream ).collect( Collectors.toList() );
    }

    /**
     * Function to load the time managements by machines. Applicable only for melting area, since the base data are MeltingBatches, which store the process data for
     * melting in batch from and only exist for melting area.
     * Function loads all MeltingBatches with inProgressTS (start of melting process for the batch) inside the timeframe defined by fromTS and toTS and transforms them to
     * TimeManagements and TimeManagementPhases.
     *
     * @param machines List of machines to load the time managements for
     * @param fromTS   Starting timestamp for the timeframe
     * @param toTS     Ending timestamp for the timeframe
     * @return A list of TimeManagements with their TimeManagementPhases
     */
    @Override
    public List<TimeManagementDTO> loadTimeManagementsForMeltingArea( Collection<String> machines, LocalDateTime fromTS, LocalDateTime toTS, boolean correctOverlapping )
    {
        List<TimeManagementDTO> result = new ArrayList<>();

        for ( String machine : machines )
        {
            List<TimeManagementDTO> machineTMs = new ArrayList<>();
            List<MeltingBatch> meltingBatches = meltingBatchHome.findBatchesForPeriod( Collections.singletonList( machine ), fromTS, toTS );
            // Sort by executing sequence index to process melting batches in the correct order - earliest to latest
            meltingBatches.sort( Comparator.comparing( MeltingBatch::getInProgressTS ) );

            for ( MeltingBatch meltingBatch : meltingBatches )
            {
                // Create time management dto object per charge
                TimeManagementDTO timeManagementDTO = new TimeManagementDTO();
                timeManagementDTO.setMachine( meltingBatch.getExecutingMachine().getApk() );
                timeManagementDTO.setCharge( meltingBatch.getCharge() );
                machineTMs.add( timeManagementDTO );

                // Retrieve and sort process steps first to ensure correct ordering of the phases list
                List<ProcessStep> processSteps = meltingBatch.getProcessSteps();
                if ( processSteps == null || processSteps.isEmpty() )
                {
                    continue;
                }
                // Filter out process steps that can run in parallel
                processSteps.sort( Comparator.comparing( ProcessStep::getStart ) );

                timeManagementDTO.setPlannedStart( processSteps.get( 0 ).getStart() );
                Optional<MeltingFurnaceKTDTO> meltingFurnaceKT = meltingFurnaceKTService.findMeltingFurnaceKnowledge( meltingFurnaceKTService.load(), meltingBatch );

                // Create one phase for each actual melting step
                for ( ProcessStep processStep : processSteps )
                {
                    // Skip process step if it does not overlap with the timeframe defined by fromTS and toTS
                    if ( processStep.getStart().isAfter( toTS ) || ( processStep.getEnd() != null && processStep.getEnd().isBefore( fromTS ) ) )
                    {
                        continue;
                    }
                    Duration actualDuration;
                    // Only reason endTS is null is that the current batch / step is still ongoing
                    if ( processStep.getEnd() != null )
                    {
                        actualDuration = Duration.between( processStep.getStart(), processStep.getEnd() );
                    }
                    else
                    {
                        actualDuration = Duration.between( processStep.getStart(), LocalDateTime.now() );
                    }

                    Duration plannedDuration = actualDuration;
                    MelterStep melterStep = MelterStep.findByShortName( processStep.getName() );
                    if ( meltingFurnaceKT.isPresent() )
                    {
                        plannedDuration = meltingFurnaceKTService.getPlannedDuration( melterStep, meltingFurnaceKT.get() );
                    }

                    TimeManagementPhaseDTO timeManagementPhaseDTO = new TimeManagementPhaseDTO();
                    timeManagementPhaseDTO.setName( processStep.getName() );
                    timeManagementPhaseDTO.setStart( processStep.getStart() );
                    timeManagementPhaseDTO.setActualDuration( actualDuration );
                    timeManagementPhaseDTO.setPlannedDuration( plannedDuration );
                    timeManagementDTO.getPhases().add( timeManagementPhaseDTO );
                }
            }

            // This function assumes that the list of time managements and the list of time management phases for each is sorted chronologically!
            if ( correctOverlapping )
            {
                correctOverlapping( machineTMs );
            }
            result.addAll( machineTMs );
        }

        return result;
    }

    private void addTimeManagementToMachine( Map<String, List<TimeManagementDTO>> machineTimeManagements, TimeManagementDTO timeManagement )
    {
        if ( !machineTimeManagements.containsKey( timeManagement.getMachine() ) )
        {
            machineTimeManagements.put( timeManagement.getMachine(), new ArrayList<>() );
        }
        machineTimeManagements.get( timeManagement.getMachine() ).add( timeManagement );
    }

    private void correctOverlapping( List<TimeManagementDTO> machineTMs )
    {
        TimeManagementPhaseDTO lastPhase = null;
        for ( TimeManagementDTO machineTM : machineTMs )
        {
            for ( TimeManagementPhaseDTO phase : machineTM.getPhases() )
            {
                if ( lastPhase != null && phase.getStart().isBefore( lastPhase.getStart().plus( lastPhase.getActualDuration() ) ) )
                {
                    final Duration diff = Duration.between( phase.getStart(), lastPhase.getStart().plus( lastPhase.getActualDuration() ) );
                    // Schauen welcher Giessen ist, den nicht verändern
                    if ( !phase.getName().equals( CasterStep.Casting.getShortName() ) )
                    {
                        phase.setStart( lastPhase.getStart().plus( lastPhase.getActualDuration() ) );
                        phase.setActualDuration( phase.getActualDuration().minus( diff ) );
                        phase.setPlannedDuration( phase.getActualDuration() );
                    }
                    else
                    {
                        lastPhase.setActualDuration( lastPhase.getActualDuration().minus( diff ) );
                        lastPhase.setPlannedDuration( lastPhase.getActualDuration() );
                    }
                }
                lastPhase = phase;
            }
        }
    }

    private void fillBefore( List<TimeManagementDTO> machineTMs, String phaseName, int plannedDuration )
    {
        fill( machineTMs, phaseName, plannedDuration, false );
    }

    private void fillAfter( List<TimeManagementDTO> machineTMs, String phaseName, int plannedDuration )
    {
        fill( machineTMs, phaseName, plannedDuration, true );
    }

    private void fill( List<TimeManagementDTO> machineTMs, String phaseName, int plannedDuration, boolean afterPhase )
    {
        TimeManagementPhaseDTO lastPhase = null;
        TimeManagementDTO lastTM = null;
        for ( TimeManagementDTO machineTM : machineTMs )
        {
            boolean isFirstPhase = true;
            for ( TimeManagementPhaseDTO phase : new ArrayList<>( machineTM.getPhases() ) )
            {
                if ( isFirstPhase && lastPhase != null && phase.getStart().isAfter( lastPhase.getStart().plus( lastPhase.getActualDuration() ) ) )
                {
                    final Duration diff = Duration.between( lastPhase.getStart().plus( lastPhase.getActualDuration() ), phase.getStart() );
                    final TimeManagementPhaseDTO newPhase = new TimeManagementPhaseDTO();
                    newPhase.setName( phaseName );
                    newPhase.setStart( lastPhase.getStart().plus( lastPhase.getActualDuration() ) );
                    newPhase.setActualDuration( diff );
                    newPhase.setPlannedDuration( Duration.ofMinutes( plannedDuration ) );

                    if ( afterPhase )
                    {
                        lastTM.getPhases().add( newPhase );
                    }
                    else
                    {
                        machineTM.getPhases().add( 0, newPhase );
                    }

                }
                isFirstPhase = false;
                lastPhase = phase;
            }
            lastTM = machineTM;
        }
    }

    //    private void setPlannedStarts( List<TimeManagementDTO> machineTMs )
    //    {
    //        LocalDateTime lastEnd = null;
    //        for ( TimeManagementDTO machineTM : machineTMs )
    //        {
    //            if ( lastEnd == null )
    //            {
    //                machineTM.setPlannedStart( machineTM.getPhases().get( 0 ).getStart() );
    //            }
    //            else
    //            {
    //                machineTM.setPlannedStart( lastEnd );
    //            }
    //            final TimeManagementPhaseDTO lastPhase = machineTM.getPhases().get( machineTM.getPhases().size() - 1 );
    //            lastEnd = lastPhase.getStart().plus( lastPhase.getActualDuration() );
    //        }
    //    }

    private byte[] getBytesFromBlob( Blob blob )
    {
        if ( blob == null )
        {
            return null;
        }
        try
        {
            return blob.getBinaryStream().readAllBytes();
        }
        catch ( Exception e )
        {
            log.error( "error reading blob", e );
        }
        return null;
    }

    private long fromByteArray( byte[] bytes )
    {
        return fromBytes( bytes[0], bytes[1], bytes[2], bytes[3], bytes[4], bytes[5], bytes[6], bytes[7] );
    }

    private long fromBytes( byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7, byte b8 )
    {
        return ( (long) b1 & 255L ) << 56 | ( (long) b2 & 255L ) << 48 | ( (long) b3 & 255L ) << 40 | ( (long) b4 & 255L ) << 32 | ( (long) b5 & 255L ) << 24 | ( (long) b6 & 255L ) << 16
                | ( (long) b7 & 255L ) << 8 | (long) b8 & 255L;
    }

    private static class TimeManagementVO
    {
        String charge;
        String machine;
        TimePeriod castingStartAndEnd;
        double ofenvorwahl;
    }
}
