package com.hydro.casting.server.ejb.melting.service;

import com.hydro.casting.common.Casting;
import com.hydro.casting.common.constant.MelterStep;
import com.hydro.casting.server.contract.dto.MeltingInstructionDTO;
import com.hydro.casting.server.ejb.av.ActualValuesManagementService;
import com.hydro.casting.server.ejb.av.ActualValuesManagementService.Measurement;
import com.hydro.casting.server.ejb.downtime.service.DowntimeRequestService;
import com.hydro.casting.server.ejb.main.service.ProcessStepService;
import com.hydro.casting.server.ejb.main.service.ResourceService;
import com.hydro.casting.server.ejb.util.JSONUtil;
import com.hydro.casting.server.model.av.ActualValuesHeader;
import com.hydro.casting.server.model.res.MeltingFurnace;
import com.hydro.casting.server.model.sched.MeltingBatch;
import com.hydro.casting.server.model.sched.dao.MeltingBatchHome;
import com.hydro.core.common.cache.ServerCache;
import com.hydro.core.common.cache.ServerCacheManager;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.core.common.util.DateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Stateless
public class MeltingMachineSignalService
{
    private final static Logger log = LoggerFactory.getLogger( MeltingMachineSignalService.class );
    private final static String DOWNTIME_DESC_SUFFIX = " Schmelzen nicht aktiv";
    @EJB
    private ActualValuesManagementService actualValuesManagementService;
    @EJB
    private ProcessStepService processStepService;
    @EJB
    private ResourceService resourceService;
    @EJB
    private MeltingInstructionService meltingInstructionService;
    @EJB
    private MeltingBatchHome meltingBatchHome;
    @EJB
    private DowntimeRequestService downtimeRequestService;
    @Inject
    ServerCacheManager serverCacheManager;

    /**
     * This method processes a machine signal for the melting area. The method is called and the data passed by the kafka receiver consuming the message.
     *
     * @param actualValues   A json array containing the data from the kafka message
     * @param definitionName The message type
     * @param machine        The machine the data belongs to
     */
    public void processMachineSignal( JsonArray actualValues, String definitionName, String machine )
    {
        // Get melting instruction since it contains data about the charge active on the machine
        ServerCache<MeltingInstructionDTO> cache = serverCacheManager.getCache( Casting.CACHE.PROD_CACHE_NAME );
        MeltingInstructionDTO meltingInstructionDTO = cache.get( Casting.CACHE.MELTING_INSTRUCTION_DATA_PATH + Objects.hash( machine ) );

        // Only process machine signals if a charge is currently active on the machine
        if ( meltingInstructionDTO != null && meltingInstructionDTO.getCharge() != null && meltingInstructionDTO.getMeltingBatchOID() != null )
        {
            // Default value for timestamp, if not provided in message, is time of processing
            long timestamp = DateTimeUtil.asEpoch( LocalDateTime.now() );
            final Map<String, Measurement> measurements = new HashMap<>();
            for ( JsonValue actualValue : actualValues )
            {
                // Entries in the array are always JSONObjects again
                if ( actualValue.getValueType() != JsonValue.ValueType.OBJECT )
                {
                    continue;
                }

                // In the current format, there is exactly one key per JSONObject
                JsonObject actualValueObject = (JsonObject) actualValue;
                String key = actualValueObject.keySet().stream().findFirst().orElse( null );
                if ( key != null )
                {
                    final JsonValue value = actualValueObject.get( key );

                    if ( "timestamp".equals( key ) )
                    {
                        timestamp = JSONUtil.parseTimestampToLong( ( (JsonString) value ).getString() );
                        continue;
                    }

                    // Storing if the value is binary (a boolean) or not is relevant for later parsing of the binary value back to either double or boolean
                    boolean binary = value.getValueType() == JsonValue.ValueType.TRUE || value.getValueType() == JsonValue.ValueType.FALSE;
                    measurements.put( key, new Measurement( binary, JSONUtil.getDoubleValue( value ) ) );
                }
            }
            log.debug( "Processing melting machine signal with definition name '" + definitionName + "' and timestamp '" + DateTimeUtil.asLocalDateTime( timestamp ) + "'" );

            MeltingBatch meltingBatch = meltingBatchHome.findById( meltingInstructionDTO.getMeltingBatchOID() );
            String charge = meltingInstructionDTO.getChargeWithoutYear();
            LocalDateTime messageReceiveTime = DateTimeUtil.asLocalDateTime( timestamp );
            // Only process signal if it was sent during current charge; otherwise, an old signal irrelevant for the current charge is processed
            if ( messageReceiveTime.isAfter( meltingBatch.getInProgressTS() ) )
            {
                // Store the machine signal data in the database using the ActualValues framework
                ActualValuesHeader header = actualValuesManagementService.findOrCreateActualValuesHeader( machine, definitionName, charge );
                actualValuesManagementService.createOrUpdateActualValuesDefinition( header, measurements );
                actualValuesManagementService.createOrUpdateActualValues( header, measurements, timestamp );

                // Execute melting area specific business steps based on the machine signal
                try
                {
                    handleProcessSteps( machine, charge, meltingInstructionDTO.getMeltingBatchOID(), messageReceiveTime, measurements );
                }
                catch ( Exception e )
                {
                    log.error( "Processing melting machine signal failed with an exception", e );
                }
            }
            else
            {
                log.debug( "Finished processing early without database changes because machine signal was sent before start of current charge (" + charge + ")." );
            }
        }
        else
        {
            log.debug( "Skipped processing for melting machine signal with definition name '" + definitionName + "' because not charge is active on machine " + machine );
        }

    }

    private void handleProcessSteps( String machine, String charge, long meltingBatchOID, LocalDateTime messageReceiveTime, Map<String, Measurement> measurements ) throws BusinessException
    {

        MeltingBatch meltingBatch = meltingBatchHome.findById( meltingBatchOID );

        // Melting data
        Measurement shaftFill = measurements.get( "SchachtFuellstand" );
        // Pouring data
        Measurement pouringPumpFrequencyInPercent = measurements.get( "Ausförderpumpe" );

        // Process step: melting
        if ( shaftFill.value > 200. )
        {
            log.debug( "Process step 'melting' should be active for charge " + charge + "." );
            if ( !processStepService.isProcessStepActive( meltingBatch, MelterStep.Melting ) )
            {
                log.debug( "Process step 'melting' is not yet active, starting." );
                processStepService.startProcessStepWithoutFinishing( meltingBatch, MelterStep.Melting, messageReceiveTime );
                downtimeRequestService.updateEndTSIfOpenDowntimeRequestExists( messageReceiveTime, machine, MelterStep.Melting.getDescription(), charge + DOWNTIME_DESC_SUFFIX );
            }
        }
        // Finish the step if the condition is no longer matched
        else
        {
            log.debug( "Process step 'melting' should not be active for charge " + charge + "." );
            if ( processStepService.isProcessStepActive( meltingBatch, MelterStep.Melting ) )
            {
                log.debug( "Process step 'melting' is still active, finishing." );
                processStepService.finishProcessStep( meltingBatch, MelterStep.Melting, messageReceiveTime );
                downtimeRequestService.createDowntimeRequest( machine, messageReceiveTime, null, charge + DOWNTIME_DESC_SUFFIX, MelterStep.Melting.getDescription() );
            }
        }

        // Process step: pouring; to avoid precision issues, check if value > 0.1 instead of 0.
        if ( pouringPumpFrequencyInPercent.value > 0.1 )
        {
            log.debug( "Process step 'pouring' should be active for charge " + charge + "." );
            if ( !processStepService.isProcessStepActive( meltingBatch, MelterStep.Pouring ) )
            {
                log.debug( "Process step 'pouring' is not yet active, starting." );
                processStepService.startProcessStepWithoutFinishing( meltingBatch, MelterStep.Pouring, messageReceiveTime );
            }
        }
        // Finish the step if the condition is no longer matched
        else
        {
            log.debug( "Process step 'pouring' should not be active for charge " + charge + "." );
            if ( processStepService.isProcessStepActive( meltingBatch, MelterStep.Pouring ) )
            {
                log.debug( "Process step 'pouring' is still active, finishing." );
                processStepService.finishProcessStep( meltingBatch, MelterStep.Pouring, messageReceiveTime );
            }
        }

        // Replicate cache to update the data in all relevant places
        MeltingFurnace meltingFurnace = resourceService.getMeltingFurnace( machine );
        meltingInstructionService.replicateCache( meltingFurnace );
    }
}
