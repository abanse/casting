package com.hydro.casting.server.ejb.melting;

import com.hydro.casting.common.Casting;
import com.hydro.casting.common.constant.MelterStep;
import com.hydro.casting.server.contract.dto.MeltingFurnaceKTDTO;
import com.hydro.casting.server.contract.melting.MeltingBusiness;
import com.hydro.casting.server.ejb.downtime.service.DowntimeRequestService;
import com.hydro.casting.server.ejb.main.service.MachineService;
import com.hydro.casting.server.ejb.main.service.ProcessStepService;
import com.hydro.casting.server.ejb.main.service.ResourceService;
import com.hydro.casting.server.ejb.main.service.SchedulableService;
import com.hydro.casting.server.ejb.melting.service.MeltingFurnaceKTService;
import com.hydro.casting.server.ejb.melting.service.MeltingInstructionService;
import com.hydro.casting.server.model.log.dao.ProductionLogHome;
import com.hydro.casting.server.model.res.MeltingFurnace;
import com.hydro.casting.server.model.sched.MeltingBatch;
import com.hydro.casting.server.model.sched.dao.MeltingBatchHome;
import com.hydro.casting.server.model.sched.dao.SchedulableHome;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.core.common.util.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Stateless
public class MeltingBusinessBean implements MeltingBusiness
{
    private final static Logger log = LoggerFactory.getLogger( MeltingBusinessBean.class );
    @EJB
    private MeltingBatchHome meltingBatchHome;
    @EJB
    private SchedulableHome schedulableHome;
    @EJB
    private ProductionLogHome productionLogHome;
    @EJB
    private ResourceService resourceService;
    @EJB
    private MachineService machineService;
    @EJB
    private MeltingInstructionService meltingInstructionService;
    @EJB
    private MeltingFurnaceKTService meltingFurnaceKTService;
    @EJB
    private DowntimeRequestService downtimeRequestService;
    @EJB
    private ProcessStepService processStepService;
    @EJB
    private SchedulableService schedulableService;

    @Override
    public void activateCharge( String currentUser, String machine, long meltingBatchOID ) throws BusinessException
    {
        changeFurnaceActivation( currentUser, machine, meltingBatchOID, true );
    }

    public boolean activateNextCharge( String currentUser, String machine ) throws BusinessException
    {
        MeltingFurnace meltingFurnace = resourceService.getMeltingFurnace( machine );
        MeltingBatch nextBatch = (MeltingBatch) schedulableHome.findNextAvailableSchedulableForExecution( meltingFurnace );
        if ( nextBatch != null )
        {
            activateCharge( currentUser, machine, nextBatch.getObjid() );
            return true;
        }

        return false;
    }

    @Override
    public void deactivateCharge( String currentUser, String machine, long meltingBatchOID ) throws BusinessException
    {
        changeFurnaceActivation( currentUser, machine, meltingBatchOID, false );
    }

    @Override
    public void createNewMeltingCharge( String melterApk, String alloy ) throws BusinessException
    {
        MeltingFurnace meltingFurnace = resourceService.getMeltingFurnace( melterApk );
        long lastUsedExecutingSequenceIndex = schedulableHome.findLastExecutingSequenceIndex( meltingFurnace );

        MeltingBatch meltingBatch = new MeltingBatch();
        meltingBatch.setExecutingMachine( meltingFurnace );
        meltingBatch.setExecutingSequenceIndex( lastUsedExecutingSequenceIndex + 1 );
        meltingBatch.setAlloyName( alloy );
        // TODO: Change when integration with SAP is done; should only be released once SAP created the process order; default should be PLANNED
        schedulableService.setExecutionState( meltingBatch, Casting.SCHEDULABLE_STATE.RELEASED );
        setCharge( meltingBatch, meltingFurnace );
        // lastCharge stores the variable part of the charge for this machine, thus only the last 4 digits are stored
        meltingFurnace.setLastCharge( Integer.parseInt( meltingBatch.getCharge().substring( 3 ) ) );

        meltingBatchHome.persist( meltingBatch );

        machineService.replicateCache( meltingFurnace );
    }

    @Override
    public void changeChargeCounterOnMelter( String melterApk, int newChargeCounter ) throws BusinessException
    {
        MeltingFurnace meltingFurnace = resourceService.getMeltingFurnace( melterApk );
        meltingFurnace.setLastCharge( newChargeCounter );
        machineService.replicateCache( meltingFurnace );
    }

    @Override
    public void startMeltingStepWithoutChangingMachine( String currentUser, String melterApk, long meltingBatchOID, MelterStep melterStep, LocalDateTime stepStartTS ) throws BusinessException
    {
        final MeltingBatch meltingBatch = meltingBatchHome.findById( meltingBatchOID );
        processStepService.startProcessStepWithoutFinishing( meltingBatch, melterStep, stepStartTS );

        MeltingFurnace meltingFurnace = resourceService.getMeltingFurnace( melterApk );
        meltingInstructionService.replicateCache( meltingFurnace );

        productionLogHome.createEntry( melterApk, meltingBatch.getCharge(), currentUser,
                String.format( "Der Prozessschritt %s wurde für die Schmelzcharge %s am Schmelzofen %s aktiviert. (%s)", melterStep.getDescription(), meltingBatch.getCharge(), melterApk,
                        stepStartTS.format( DateTimeFormatter.ofPattern( "dd.MM.yy HH:mm:ss" ) ) ) );
    }

    @Override
    public void finishMeltingStepWithoutChangingMachine( String currentUser, String melterApk, long meltingBatchOID, MelterStep melterStep, LocalDateTime stepStartTS ) throws BusinessException
    {
        final MeltingBatch meltingBatch = meltingBatchHome.findById( meltingBatchOID );
        processStepService.finishProcessStep( meltingBatch, melterStep, stepStartTS );

        MeltingFurnace meltingFurnace = resourceService.getMeltingFurnace( melterApk );
        meltingInstructionService.replicateCache( meltingFurnace );

        productionLogHome.createEntry( melterApk, meltingBatch.getCharge(), currentUser,
                String.format( "Der Prozessschritt %s wurde für die Schmelzcharge %s am Schmelzofen %s beendet. (%s)", melterStep.getDescription(), meltingBatch.getCharge(), melterApk,
                        stepStartTS.format( DateTimeFormatter.ofPattern( "dd.MM.yy HH:mm:ss" ) ) ) );
    }

    @Override
    public void startMeltingStepAndFinishOther( String currentUser, String melterApk, long meltingBatchOID, MelterStep melterStep, MelterStep other, LocalDateTime stepStartTS )
            throws BusinessException
    {
        final MeltingBatch meltingBatch = meltingBatchHome.findById( meltingBatchOID );
        processStepService.finishProcessStep( meltingBatch, other, stepStartTS );
        processStepService.startProcessStepWithoutFinishing( meltingBatch, melterStep, stepStartTS );

        startMeltingStepOnMachine( currentUser, melterApk, meltingBatch, melterStep, stepStartTS, false );
    }

    @Override
    public void startMeltingStep( String currentUser, String melterApk, long meltingBatchOID, MelterStep melterStep, LocalDateTime stepStartTS ) throws BusinessException
    {
        final MeltingBatch meltingBatch = meltingBatchHome.findById( meltingBatchOID );
        processStepService.startProcessStep( meltingBatch, melterStep, stepStartTS );

        startMeltingStepOnMachine( currentUser, melterApk, meltingBatch, melterStep, stepStartTS, true );
    }

    @Override
    public void finishCharge( String currentUser, String melterApk, long meltingBatchOID ) throws BusinessException
    {
        final MeltingBatch meltingBatch = meltingBatchHome.findById( meltingBatchOID );
        final MeltingFurnace meltingFurnace = resourceService.getMeltingFurnace( melterApk );

        processStepService.finishAllRunningProcessSteps( meltingBatch, LocalDateTime.now() );
        downtimeRequestService.updateEndTSForAllOpenDowntimeRequests( LocalDateTime.now(), melterApk );
        schedulableService.setExecutionState( meltingBatch, Casting.SCHEDULABLE_STATE.SUCCESS );
        meltingFurnace.setCurrentStep( null );
        meltingFurnace.setCurrentStepStartTS( null );
        meltingFurnace.setCurrentStepDuration( 0 );
        machineService.replicateCache( meltingFurnace );
        meltingInstructionService.replicateCache( meltingFurnace );

        productionLogHome.createEntry( melterApk, meltingBatch.getCharge(), currentUser, "Die Charge " + meltingBatch.getCharge().substring( 2 ) + " wurde erfolgreich abgeschlossen" );
    }

    private void changeFurnaceActivation( String currentUser, String machine, long meltingBatchOID, boolean activate ) throws BusinessException
    {
        MeltingBatch meltingBatch = meltingBatchHome.findById( meltingBatchOID );
        final MeltingFurnace meltingFurnace = resourceService.getMeltingFurnace( machine );
        meltingBatch.setExecutingMachine( meltingFurnace );

        if ( activate )
        {
            schedulableService.setExecutionState( meltingBatch, Casting.SCHEDULABLE_STATE.IN_PROGRESS );

            // Special case S2: set "base step" to melting
            if ( machine.equals( Casting.MACHINE.MELTING_FURNACE_S2 ) )
            {
                startMeltingStep( currentUser, machine, meltingBatchOID, MelterStep.Charging, LocalDateTime.now() );
            }
        }
        else
        {
            schedulableService.setExecutionState( meltingBatch, Casting.SCHEDULABLE_STATE.PAUSED );
            meltingFurnace.setCurrentStep( null );
            meltingFurnace.setCurrentStepStartTS( null );
            meltingFurnace.setCurrentStepDuration( 0 );
            machineService.replicateCache( meltingFurnace );
        }

        meltingInstructionService.replicateCache( meltingFurnace );
    }

    private void setCharge( MeltingBatch meltingBatch, MeltingFurnace meltingFurnace )
    {
        String lastCharge = null;
        if ( meltingFurnace.getLastCharge() != null )
        {
            lastCharge = StringTools.YEARDF.format( new Date() ) + meltingFurnace.getApk().charAt( 0 ) + StringTools.N4F.format( meltingFurnace.getLastCharge() );
        }
        String nextCharge = Casting.getNextCharge( meltingFurnace.getApk(), lastCharge );
        meltingBatch.setCharge( nextCharge );
    }

    private void startMeltingStepOnMachine( String currentUser, String melterApk, MeltingBatch meltingBatch, MelterStep melterStep, LocalDateTime stepStartTS, boolean runAutomaticDowntimeCreation )
            throws BusinessException
    {
        final MeltingFurnace meltingFurnace = resourceService.getMeltingFurnace( melterApk );

        if ( runAutomaticDowntimeCreation )
        {
            checkTimeViolationAndCreateDowntimeRequestIfNecessary( meltingFurnace, stepStartTS, meltingBatch.getCharge() );
        }

        meltingFurnace.setCurrentStep( melterStep.getShortName() );
        meltingFurnace.setCurrentStepStartTS( stepStartTS );
        meltingFurnace.setCurrentStepDuration( calcDuration( meltingBatch, melterStep ) );

        meltingInstructionService.replicateCache( meltingFurnace );
        machineService.replicateCache( meltingFurnace );

        productionLogHome.createEntry( melterApk, meltingBatch.getCharge(), currentUser,
                "Die Schmelzcharge " + meltingBatch.getCharge().substring( 2 ) + " am Schmelzofen " + melterApk + " wechselt in den Status " + melterStep.getDescription() + " (" + stepStartTS.format(
                        DateTimeFormatter.ofPattern( "dd.MM.yy HH:mm:ss" ) ) + ")" );
    }

    private int calcDuration( MeltingBatch meltingBatch, MelterStep melterStep )
    {
        int duration = 60;
        final MeltingFurnaceKTDTO meltingFurnaceKT = meltingFurnaceKTService.findMeltingFurnaceKnowledge( meltingFurnaceKTService.load(), meltingBatch ).orElse( null );

        if ( meltingFurnaceKT != null )
        {
            duration = (int) meltingFurnaceKTService.getPlannedDuration( melterStep, meltingFurnaceKT ).toMinutes();
        }

        return duration;
    }

    private void checkTimeViolationAndCreateDowntimeRequestIfNecessary( MeltingFurnace meltingFurnace, LocalDateTime stepStartTS, String charge )
    {
        if ( meltingFurnace.getCurrentStep() == null || meltingFurnace.getCurrentStepStartTS() == null )
        {
            return;
        }
        LocalDateTime normalEndTime = meltingFurnace.getCurrentStepStartTS().plusMinutes( meltingFurnace.getCurrentStepDuration() );

        if ( normalEndTime.isBefore( stepStartTS ) )
        {
            MelterStep currentStep = MelterStep.findByShortName( meltingFurnace.getCurrentStep() );
            String currentStepName = currentStep != null ? currentStep.getDescription() : null;
            // Should create new DowntimeRequest because target duration for melting step was exceeded
            downtimeRequestService.createDowntimeRequest( meltingFurnace.getCostCenter().getApk(), meltingFurnace.getApk(), normalEndTime, stepStartTS,
                    charge.substring( 2 ) + ": Verzögerung beim " + currentStepName, currentStepName );

            log.info( "Created new DowntimeRequest for machine " + meltingFurnace.getApk() + " for phase " + currentStepName );
        }
    }
}
