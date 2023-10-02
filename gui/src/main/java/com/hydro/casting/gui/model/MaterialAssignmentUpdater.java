package com.hydro.casting.gui.model;

import com.hydro.casting.server.contract.dto.CasterDemandDTO;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.CasterSchedulePosDTO;
import com.hydro.casting.server.contract.dto.SlabDTO;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MaterialAssignmentUpdater
{
    private final static Logger log = LoggerFactory.getLogger( MaterialAssignmentUpdater.class );
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private Future<?> mainFuture;

    private boolean started = false;
    private UpdaterState engineState = UpdaterState.MUST_RUN;

    private final ObservableList<SlabDTO> slabList;
    private final ObservableList<CasterDemandDTO> demandList;
    private final ObservableList<CasterScheduleDTO> casterList;
    private final ObservableMap<SlabDTO, Long> slabDemandAssignment;
    private final ObservableMap<Long, List<SlabDTO>> demandSlabAssignment;
    private final ObservableMap<Long, List<CasterSchedulePosDTO>> demandCasterSchedulePosAssignment;

    public MaterialAssignmentUpdater( final ObservableList<SlabDTO> slabList, final ObservableList<CasterDemandDTO> demandList, final ObservableList<CasterScheduleDTO> casterList,
            final ObservableMap<SlabDTO, Long> slabDemandAssignment, final ObservableMap<Long, List<SlabDTO>> demandSlabAssignment,
            final ObservableMap<Long, List<CasterSchedulePosDTO>> demandCasterSchedulePosAssignment )
    {
        this.slabList = slabList;
        this.demandList = demandList;
        this.casterList = casterList;
        this.slabDemandAssignment = slabDemandAssignment;
        this.demandSlabAssignment = demandSlabAssignment;
        this.demandCasterSchedulePosAssignment = demandCasterSchedulePosAssignment;
    }

    public void start()
    {
        log.debug( "MaterialAssignmentUpdater Started" );
        started = true;
        mainFuture = executor.submit( new ModelUpdateWorker() );
    }

    public void stop()
    {
        started = false;
        mainFuture.cancel( true );
        log.debug( "MaterialAssignmentUpdater shut down" );
    }

    public synchronized void trigger()
    {
        if ( engineState == UpdaterState.RUNNING )
        {
            engineState = UpdaterState.MUST_RUN;
            return;
        }
        notifyAll();
    }

    private synchronized void shouldRun()
    {
        if ( engineState != UpdaterState.MUST_RUN )
        {
            engineState = UpdaterState.WAIT;
            try
            {
                wait();
            }
            catch ( InterruptedException e )
            {
                // ignore
            }
        }
        engineState = UpdaterState.RUNNING;
    }

    private enum UpdaterState
    {WAIT, RUNNING, MUST_RUN}

    private class ModelUpdateWorker implements Runnable
    {
        @Override
        public void run()
        {
            while ( started )
            {
                shouldRun();
                log.trace( "running" );

                try
                {
                    // passende Slabs heraussuchen
                    final MultiValuedMap<CasterDemandDTO, SlabDTO> demandSuitableMaterials = new ArrayListValuedHashMap<>();
                    final List<CasterDemandDTO> demandsWithSuitableMaterials = new ArrayList<>();
                    final MultiValuedMap<CasterDemandDTO, CasterSchedulePosDTO> demandSuitablePositions = new ArrayListValuedHashMap<>();
                    final List<CasterDemandDTO> demandsWithSuitableSchedules = new ArrayList<>();
                    for ( CasterDemandDTO demandDTO : new ArrayList<>( demandList ) )
                    {
                        final List<SlabDTO> slabListClone = new ArrayList<>( slabList );
                        for ( SlabDTO slabDTO : slabListClone )
                        {
                            // Bereits zugeordnete entfernen
                            if ( slabDTO.getErrorCode() != null )
                            {
                                continue;
                            }
                            boolean allowed = false;
                            if ( slabDTO.getCustomerId() != null && Objects.equals( demandDTO.getCustomerId(), slabDTO.getCustomerId() ) && Objects.equals( demandDTO.getAlloy(), slabDTO.getAlloy() )
                                    && Objects.equals( demandDTO.getQuality(), slabDTO.getQuality() ) && demandDTO.getWidth() == slabDTO.getWidth() && demandDTO.getLength() == slabDTO.getLength() )
                            {
                                allowed = true;
                            }
                            else if ( Objects.equals( demandDTO.getAlloy(), slabDTO.getAlloy() ) && Objects.equals( demandDTO.getQuality(), slabDTO.getQuality() )
                                    && demandDTO.getWidth() == slabDTO.getWidth() && demandDTO.getLength() == slabDTO.getLength() )
                            {
                                allowed = true;
                            }
                            if ( allowed )
                            {
                                demandSuitableMaterials.put( demandDTO, slabDTO );
                                if ( !demandsWithSuitableMaterials.contains( demandDTO ) )
                                {
                                    demandsWithSuitableMaterials.add( demandDTO );
                                }
                            }
                        }
                        for ( CasterScheduleDTO casterScheduleDTO : new ArrayList<>( casterList ) )
                        {
                            boolean oneAdded = false;
                            if ( suitable( demandDTO, casterScheduleDTO.getPos1() ) )
                            {
                                demandSuitablePositions.put( demandDTO, casterScheduleDTO.getPos1() );
                                oneAdded = true;
                            }
                            if ( suitable( demandDTO, casterScheduleDTO.getPos2() ) )
                            {
                                demandSuitablePositions.put( demandDTO, casterScheduleDTO.getPos2() );
                                oneAdded = true;
                            }
                            if ( suitable( demandDTO, casterScheduleDTO.getPos3() ) )
                            {
                                demandSuitablePositions.put( demandDTO, casterScheduleDTO.getPos3() );
                                oneAdded = true;
                            }
                            if ( suitable( demandDTO, casterScheduleDTO.getPos4() ) )
                            {
                                demandSuitablePositions.put( demandDTO, casterScheduleDTO.getPos4() );
                                oneAdded = true;
                            }
                            if ( suitable( demandDTO, casterScheduleDTO.getPos5() ) )
                            {
                                demandSuitablePositions.put( demandDTO, casterScheduleDTO.getPos5() );
                                oneAdded = true;
                            }
                            if ( oneAdded )
                            {
                                if ( !demandsWithSuitableSchedules.contains( demandDTO ) )
                                {
                                    demandsWithSuitableSchedules.add( demandDTO );
                                }
                            }
                        }
                    }
                    // Demand sortieren, erst die ältesten
                    demandsWithSuitableMaterials.sort( Comparator.comparing( CasterDemandDTO::getDeliveryDateTo ) );
                    final Map<SlabDTO, Long> newSlabDemandAssignment = new HashMap<>();
                    final Map<Long, List<SlabDTO>> newDemandSlabAssignment = new HashMap<>();
                    for ( CasterDemandDTO demandsWithSuitableMaterial : demandsWithSuitableMaterials )
                    {
                        final int countToAssign = demandsWithSuitableMaterial.getToProduce();
                        for ( int i = 0; i < countToAssign; i++ )
                        {
                            final List<SlabDTO> suitableMaterials = (List<SlabDTO>) demandSuitableMaterials.get( demandsWithSuitableMaterial );
                            Comparator<SlabDTO> comparator = Comparator.comparing( SlabDTO::getYardTS, Comparator.nullsLast( Comparator.naturalOrder() ) );
                            suitableMaterials.sort( comparator );
                            for ( SlabDTO suitableMaterial : suitableMaterials )
                            {
                                if ( newSlabDemandAssignment.containsKey( suitableMaterial ) )
                                {
                                    continue;
                                }
                                newSlabDemandAssignment.put( suitableMaterial, demandsWithSuitableMaterial.getId() );
                                List<SlabDTO> demandSlabs = newDemandSlabAssignment.computeIfAbsent( demandsWithSuitableMaterial.getId(), k -> new ArrayList<>() );
                                demandSlabs.add( suitableMaterial );
                                break;
                            }
                        }
                    }

                    demandsWithSuitableSchedules.sort( Comparator.comparing( CasterDemandDTO::getDeliveryDateTo ) );
                    final List<CasterSchedulePosDTO> assignedPositions = new ArrayList<>();
                    final Map<Long, List<CasterSchedulePosDTO>> newDemandCasterSchedulePosAssignment = new HashMap<>();
                    for ( CasterDemandDTO demandsWithSuitableSchedule : demandsWithSuitableSchedules )
                    {
                        int countToSchedule = demandsWithSuitableSchedule.getToProduce();
                        if ( newDemandSlabAssignment.containsKey( demandsWithSuitableSchedule.getId() ) )
                        {
                            countToSchedule = countToSchedule - newDemandSlabAssignment.get( demandsWithSuitableSchedule.getId() ).size();
                        }
                        for ( int i = 0; i < countToSchedule; i++ )
                        {
                            final List<CasterSchedulePosDTO> suitablePositions = (List<CasterSchedulePosDTO>) demandSuitablePositions.get( demandsWithSuitableSchedule );
                            suitablePositions.sort( Comparator.comparing( o -> o.getCasterSchedule().getCharge() ) );
                            for ( CasterSchedulePosDTO suitablePosition : suitablePositions )
                            {
                                if ( assignedPositions.contains( suitablePosition ) )
                                {
                                    continue;
                                }
                                assignedPositions.add( suitablePosition );
                                List<CasterSchedulePosDTO> demandPositions = newDemandCasterSchedulePosAssignment.computeIfAbsent( demandsWithSuitableSchedule.getId(), k -> new ArrayList<>() );
                                demandPositions.add( suitablePosition );
                                break;
                            }
                        }
                    }

                    slabDemandAssignment.clear();
                    slabDemandAssignment.putAll( newSlabDemandAssignment );
                    demandSlabAssignment.clear();
                    demandSlabAssignment.putAll( newDemandSlabAssignment );
                    demandCasterSchedulePosAssignment.clear();
                    demandCasterSchedulePosAssignment.putAll( newDemandCasterSchedulePosAssignment );

                    log.trace( "finished" );
                }
                catch ( Exception ex )
                {
                    log.error( "exception in material assignment", ex );
                }
            }
        }
    }

    private boolean suitable( CasterDemandDTO casterDemandDTO, CasterSchedulePosDTO casterSchedulePosDTO )
    {
        if ( casterDemandDTO.getExperimentNumber() != null )
        {
            return Objects.equals( casterDemandDTO.getExperimentNumber(), casterSchedulePosDTO.getExperimentNumber() );
        }
        if ( casterSchedulePosDTO.getExperimentNumber() != null )
        {
            return false;
        }
        return Objects.equals( casterDemandDTO.getMaterialType(), casterSchedulePosDTO.getMaterialType() );
    }
}
