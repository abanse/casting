package com.hydro.casting.server.ejb.prod.service;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.CastingKTDTO;
import com.hydro.casting.server.contract.planning.CasterScheduleBusiness;
import com.hydro.casting.server.ejb.av.ActualValuesManagementService;
import com.hydro.casting.server.ejb.av.ActualValuesManagementService.Measurement;
import com.hydro.casting.server.ejb.downtime.service.DowntimeRequestService;
import com.hydro.casting.server.ejb.planning.service.CastingKTService;
import com.hydro.casting.server.ejb.util.JSONUtil;
import com.hydro.casting.server.model.av.ActualValuesHeader;
import com.hydro.casting.server.model.sched.dao.CastingBatchHome;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.core.common.util.DateTimeUtil;
import com.hydro.core.common.util.TimePeriod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

@Stateless
public class CastingMachineSignalService
{
    private final static Logger log = LoggerFactory.getLogger( CastingMachineSignalService.class );
    private final static String PHASE_CASTING = "Giessen";
    private final static Function<String, String> descCastingInterruption = ( s ) -> s + " Gussabbruch";
    private final static Function<String, String> descDelay = ( s ) -> s + " Verzögerung";

    @EJB
    private ActualValuesManagementService actualValuesManagementService;
    @EJB
    private ActualValuesProductionService actualValuesProductionService;
    @EJB
    private DowntimeRequestService downtimeRequestService;
    @EJB
    private CastingKTService castingKTService;
    @EJB
    private CastingBatchHome castingBatchHome;
    @EJB
    private CasterScheduleBusiness casterScheduleBusiness;

    /**
     * This method processes a machine signal for the casting area. The machine signal is received from kafka.
     *
     * @param actualValues   The json array that contains the machine signal data to be processed.
     * @param definitionName The definition name of the kafka message with the machine signal.
     * @param machine        The machine the signal arrived for.
     * @throws BusinessException in case of inconsistent database state causing inability to continue the business process.
     */
    public void processMachineSignal( JsonArray actualValues, String definitionName, String machine ) throws BusinessException
    {
        final Map<String, Measurement> measurements = new HashMap<>();
        String charge = null;
        long timestamp = 0L;
        double currentTiltAngle = 0;

        for ( JsonValue actualValue : actualValues )
        {
            // Entries in the array are always JSONObjects again
            if ( actualValue.getValueType() != JsonValue.ValueType.OBJECT )
            {
                continue;
            }

            JsonObject actualValueObject = (JsonObject) actualValue;
            // In the current format, there is exactly one key per JSONObject
            String key = actualValueObject.keySet().stream().findFirst().orElse( null );

            // Key should always be !null, otherwise there would be no object
            if ( key != null )
            {
                final JsonValue value = actualValueObject.get( key );

                // Charge number is stored and used for processing, but not part of the measurements
                if ( "Chargennummer".equals( key ) )
                {
                    charge = value.toString();
                    continue;
                }
                // Timestamp is stored and used for processing, because each actual value needs a timestamp, but the JSON only contains one general timestamp
                else if ( "timestamp".equals( key ) )
                {
                    timestamp = JSONUtil.parseTimestampToLong( ( (JsonString) value ).getString() );
                    continue;
                }

                if ( "Kippwinkel".equals( key ) )
                {
                    currentTiltAngle = JSONUtil.getDoubleValue( value );
                }

                // Storing if the value is binary (a boolean) or not is relevant for later parsing of the binary value back to either double or boolean
                boolean binary = value.getValueType() == JsonValue.ValueType.TRUE || value.getValueType() == JsonValue.ValueType.FALSE;
                measurements.put( key, new Measurement( binary, JSONUtil.getDoubleValue( value ) ) );
            }
        }

        if ( "99999".equals( charge ) )
        {
            log.info( "ignore machine signal with charge 99999" );
            return;
        }

        log.debug( "Processing casting machine signal with definition name '" + definitionName + "' and timestamp '" + DateTimeUtil.asLocalDateTime( timestamp ) + "'" );

        // Store the machine signal data in the database using the ActualValues framework
        ActualValuesHeader header = actualValuesManagementService.findOrCreateActualValuesHeader( machine, definitionName, charge );
        actualValuesManagementService.createOrUpdateActualValuesDefinition( header, measurements );
        actualValuesManagementService.createOrUpdateActualValues( header, measurements, timestamp );

        // Execute casting area specific business steps based on the machine signal
        executeValidationsOnHeader( header, currentTiltAngle, machine );
        startAutoProgressOnBatch( charge, machine, timestamp );
    }

    private void executeValidationsOnHeader( ActualValuesHeader header, double currentTiltAngle, String machine )
    {
        // Downtime requests for last casting process should be created once the current process started, which is identified by a tilt angle > 5 ("Kippwinkel")
        if ( currentTiltAngle > 5 && header.getValidations() == null )
        {
            // Find previous header (of the machine signals associated with the previous casting process)
            final ActualValuesHeader lastHeader = actualValuesManagementService.findLastHeaderBefore( Casting.ACTUAL_VALUES.APPLICATION.PLC_SIGNALS, machine, header );

            if ( lastHeader != null && lastHeader.getLastMeasuredTS() != null )
            {
                // Find starting time of last casting process using the tilt angle in the associated machine signals
                final TimePeriod castingStartAndEnd = actualValuesProductionService.findCastingStartAndEnd( lastHeader );
                final LocalDateTime lastStart;
                if ( castingStartAndEnd != null )
                {
                    lastStart = castingStartAndEnd.getStartDateInclusive();
                }
                else
                {
                    lastStart = lastHeader.getLastMeasuredTS();
                }

                final LocalDateTime thisStart = header.getLastMeasuredTS();
                log.info( "Charge " + lastHeader.getRefName() + " Start: " + lastStart + " Ende: " + thisStart );

                // Check if there was a casting interruption
                TimePeriod castingInterruptionPeriod = actualValuesProductionService.findCastingInterruptionStartAndEnd( lastHeader );
                if ( castingInterruptionPeriod != null )
                {
                    LocalDateTime interruptionStart = castingInterruptionPeriod.getStartDateInclusive();
                    // Downtime created for the entirety of the past casting process ("Anguss bis Anguss")
                    downtimeRequestService.createDowntimeRequest( machine, lastStart, thisStart, descCastingInterruption.apply( lastHeader.getRefName() ), PHASE_CASTING );
                    log.info( "DowntimeRequest created for casting interruption from " + interruptionStart + " until " + thisStart );
                }
                else
                {
                    final Duration duration = Duration.between( lastStart, thisStart );
                    final long durationMinutes = duration.getSeconds() / 60;
                    log.info( "Charge " + lastHeader.getRefName() + " Dauer in Minuten " + durationMinutes );

                    long targetDuration = 190; // andere Anlagen ca 3 Stunden (default Vorgabe)
                    if ( Objects.equals( "80", machine ) )
                    {
                        targetDuration = 270; // Anlage 80 4,5 Stunden (default Vorgabe)
                    }

                    // Target duration could be defined in the casting knowledge tables
                    final List<CastingKTDTO> castingKTs = castingKTService.load();
                    final Optional<CastingKTDTO> castingKTOptional = castingKTService.findCastingKnowledge( castingKTs, machine, null );
                    if ( castingKTOptional.isPresent() )
                    {
                        final CastingKTDTO castingKT = castingKTOptional.get();
                        targetDuration = Math.abs( castingKT.getCastingTM() ) + Math.abs( castingKT.getUnloadingTM() );
                        log.info( "Charge " + lastHeader.getRefName() + " Überwachungszeit " + targetDuration );
                    }

                    // If target duration was exceeded by the actual duration, a downtime request should be created
                    if ( durationMinutes > targetDuration )
                    {
                        LocalDateTime endOfTargetDuration = lastStart.plusMinutes( targetDuration );
                        // Regular downtime only for the timespan past the target time
                        downtimeRequestService.createDowntimeRequest( machine, endOfTargetDuration, thisStart, descDelay.apply( lastHeader.getRefName() ), PHASE_CASTING );

                        log.info( "DowntimeRequest created from " + endOfTargetDuration + " until " + thisStart );
                    }
                }

            }

            header.setValidations( "checked" );
        }
    }

    /**
     * This function sets the progress of the casting batch object for the given charge number and machine from RELEASED (fixed) to AUTO_PROGRESS (casting).
     * This starts the casting process from technical side when machine signals start arriving, and finishes the old casting process when new signals for the next batch arrive.
     * If the casting batch object was in state UNLOADING before, the same applies.
     *
     * @param charge  The charge number of the batch currently in casting
     * @param machine The machine signals are arriving for
     */
    private void startAutoProgressOnBatch( String charge, String machine, long timestamp ) throws BusinessException
    {
        // Only applies if the casting batch is in status RELEASED, meaning it's ready for casting in the database
        long existReleasedCharge = castingBatchHome.exist( charge, Casting.SCHEDULABLE_STATE.RELEASED );
        // second check if charge in progress
        if ( existReleasedCharge < 0 )
        {
            existReleasedCharge = castingBatchHome.exist( charge, Casting.SCHEDULABLE_STATE.IN_PROGRESS );
        }
        if ( existReleasedCharge >= 0 )
        {
            // If there is any old auto progress (automatically started casting process), the old one is finished successfully since we are now casting the next batch
            final List<Long> oldAutoProgressOIDs = castingBatchHome.findByMachineAndState( machine, Casting.SCHEDULABLE_STATE.CASTING_IN_PROGRESS );

            for ( Long oldOID : oldAutoProgressOIDs )
            {
                final CasterScheduleDTO schedule = new CasterScheduleDTO();
                schedule.setId( oldOID );
                casterScheduleBusiness.changeExecutionState( schedule, Casting.SCHEDULABLE_STATE.SUCCESS, timestamp );
            }

            final CasterScheduleDTO schedule = new CasterScheduleDTO();
            schedule.setId( existReleasedCharge );
            casterScheduleBusiness.changeExecutionState( schedule, Casting.SCHEDULABLE_STATE.CASTING_IN_PROGRESS, timestamp );
        }
    }
}
