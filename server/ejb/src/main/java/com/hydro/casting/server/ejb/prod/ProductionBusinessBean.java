package com.hydro.casting.server.ejb.prod;

import com.hydro.casting.common.Casting;
import com.hydro.casting.common.constant.CasterStep;
import com.hydro.casting.common.constant.FurnaceStep;
import com.hydro.casting.server.contract.downtime.DowntimeBusiness;
import com.hydro.casting.server.contract.dto.CastingKTDTO;
import com.hydro.casting.server.contract.dto.MeltingKTDTO;
import com.hydro.casting.server.contract.prod.ProductionBusiness;
import com.hydro.casting.server.ejb.main.service.MachineService;
import com.hydro.casting.server.ejb.main.service.ProcessStepService;
import com.hydro.casting.server.ejb.main.service.ResourceService;
import com.hydro.casting.server.ejb.planning.service.CasterScheduleService;
import com.hydro.casting.server.ejb.planning.service.CastingKTService;
import com.hydro.casting.server.ejb.planning.service.MeltingKTService;
import com.hydro.casting.server.ejb.prod.service.CasterInstructionService;
import com.hydro.casting.server.ejb.prod.service.FurnaceInstructionService;
import com.hydro.casting.server.model.log.dao.ProductionLogHome;
import com.hydro.casting.server.model.res.Caster;
import com.hydro.casting.server.model.res.Machine;
import com.hydro.casting.server.model.res.MeltingFurnace;
import com.hydro.casting.server.model.sched.CastingBatch;
import com.hydro.casting.server.model.sched.dao.CastingBatchHome;
import com.hydro.core.common.exception.BusinessException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Stateless
public class ProductionBusinessBean implements ProductionBusiness
{
    @EJB
    private CasterScheduleService casterScheduleService;

    @EJB
    private ResourceService resourceService;

    @EJB
    private MachineService machineService;

    @EJB
    private FurnaceInstructionService furnaceInstructionService;

    @EJB
    private CasterInstructionService casterInstructionService;

    @EJB
    private ProductionLogHome productionLogHome;

    @EJB
    private DowntimeBusiness downtimeBusiness;

    @EJB
    private CastingBatchHome castingBatchHome;

    @EJB
    private MeltingKTService meltingKTService;

    @EJB
    private CastingKTService castingKTService;

    @EJB
    private ProcessStepService processStepService;

    @Override
    public void activateCharge( String currentUser, String machine, long castingBatchOID ) throws BusinessException
    {
        final CastingBatch castingBatch = changeFurnaceActivation( machine, castingBatchOID, true );

        productionLogHome.createEntry( machine, castingBatch.getCharge(), currentUser, "Die Charge " + castingBatch.getCharge().substring( 2 ) + " wurde am Ofen " + machine + " aktiviert" );
    }

    @Override
    public void deactivateCharge( String currentUser, String machine, long castingBatchOID ) throws BusinessException
    {
        final CastingBatch castingBatch = changeFurnaceActivation( machine, castingBatchOID, false );

        productionLogHome.createEntry( machine, castingBatch.getCharge(), currentUser, "Die Charge " + castingBatch.getCharge().substring( 2 ) + " wurde am Ofen " + machine + " deaktiviert" );
    }

    @Override
    public void startFurnaceStep( String currentUser, String machine, long castingBatchOID, FurnaceStep furnaceStep, LocalDateTime startTS ) throws BusinessException
    {
        final CastingBatch castingBatch = casterScheduleService.findCastingBatch( castingBatchOID );

        processStepService.startProcessStep( castingBatch, furnaceStep, startTS );

        final MeltingFurnace meltingFurnace = resourceService.getMeltingFurnace( machine );
        meltingFurnace.setCurrentStep( furnaceStep.getShortName() );
        meltingFurnace.setCurrentStepStartTS( startTS );
        meltingFurnace.setCurrentStepDuration( calcDuration( castingBatch, furnaceStep ) );

        downtimeBusiness.closeOpenDowntime( machine, startTS );

        casterScheduleService.replicateCache( castingBatch );
        machineService.replicateCache( meltingFurnace );

        productionLogHome.createEntry( machine, castingBatch.getCharge(), currentUser,
                "Die Charge " + castingBatch.getCharge().substring( 2 ) + " am Ofen " + machine + " wechselt in den Status " + furnaceStep.getDescription() + " (" + startTS.format(
                        DateTimeFormatter.ofPattern( "dd.MM.yy HH:mm:ss" ) ) + ")" );
    }

    @Override
    public void releaseFurnace( String currentUser, String machine, long castingBatchOID, LocalDateTime startTS ) throws BusinessException
    {
        final CastingBatch castingBatch = casterScheduleService.findCastingBatch( castingBatchOID );
        castingBatch.setReleaseTS( startTS );
        castingBatch.setReleaseResponsible( currentUser );
        startFurnaceStep( currentUser, machine, castingBatchOID, FurnaceStep.ReadyToCast, startTS );

        // Aktiviere an der Gießanlage, wenn keiner aktiv ist
        final CastingBatch casterCastingBatch = castingBatchHome.findActiveForCaster( Casting.getCasterForMeltingFurnace( machine ) );
        if ( casterCastingBatch == null )
        {
            activateCaster( castingBatch );
        }

        productionLogHome.createEntry( machine, castingBatch.getCharge(), currentUser,
                "Freigabe für Charge " + castingBatch.getCharge().substring( 2 ) + " am Ofen " + machine + " (" + startTS.format( DateTimeFormatter.ofPattern( "dd.MM.yy HH:mm:ss" ) ) + ")" );

    }

    @Override
    public void startCasting( String currentUser, String machine, long castingBatchOID, LocalDateTime startTS ) throws BusinessException
    {
        final CastingBatch castingBatch = casterScheduleService.findCastingBatch( castingBatchOID );

        // Auch den Ofen auf Giessen setzen -> startet process step "casting" auf dem batch, könnte auch CastingStep.Casting sein
        startFurnaceStep( currentUser, castingBatch.getMeltingFurnace().getApk(), castingBatchOID, FurnaceStep.Casting, startTS );

        final Caster caster = resourceService.getCaster( machine );
        caster.setCurrentStep( CasterStep.Casting.getShortName() );
        caster.setCurrentStepStartTS( startTS );
        caster.setCurrentStepDuration( calcDuration( castingBatch, CasterStep.Casting ) );

        downtimeBusiness.closeOpenDowntime( machine, startTS );

        casterScheduleService.replicateCache( castingBatch );
        machineService.replicateCache( caster );

        productionLogHome.createEntry( machine, castingBatch.getCharge(), currentUser,
                "Die Charge " + castingBatch.getCharge().substring( 2 ) + " an der Gießanlage " + machine + " wechselt in den Status " + CasterStep.Casting.getDescription() + " (" + startTS.format(
                        DateTimeFormatter.ofPattern( "dd.MM.yy HH:mm:ss" ) ) + ")" );
    }

    @Override
    public void endCasting( String currentUser, String machine, long castingBatchOID, LocalDateTime startTS ) throws BusinessException
    {
        final CastingBatch castingBatch = casterScheduleService.findCastingBatch( castingBatchOID );
        processStepService.startProcessStep( castingBatch, CasterStep.Unloading, startTS );
        castingBatch.setUnloadingTS( startTS );

        casterScheduleService.setExecutionState( castingBatch, Casting.SCHEDULABLE_STATE.UNLOADING_IN_PROGRESS );

        final MeltingFurnace meltingFurnace = castingBatch.getMeltingFurnace();
        meltingFurnace.setCurrentStep( null );
        meltingFurnace.setCurrentStepDuration( 0 );

        final Caster caster = resourceService.getCaster( machine );
        caster.setCurrentStep( CasterStep.Unloading.getShortName() );
        caster.setCurrentStepStartTS( startTS );
        caster.setCurrentStepDuration( calcDuration( castingBatch, CasterStep.Unloading ) );

        downtimeBusiness.closeOpenDowntime( machine, startTS );

        casterScheduleService.replicateCache( castingBatch );
        machineService.replicateCache( meltingFurnace );
        machineService.replicateCache( caster );
        casterInstructionService.replicateCache( caster );
        furnaceInstructionService.replicateCache( meltingFurnace );

        productionLogHome.createEntry( meltingFurnace.getApk(), castingBatch.getCharge(), currentUser,
                "Die Charge " + castingBatch.getCharge().substring( 2 ) + " am Ofen " + meltingFurnace.getApk() + " ist gegossen (" + startTS.format(
                        DateTimeFormatter.ofPattern( "dd.MM.yy HH:mm:ss" ) ) + ")" );

        productionLogHome.createEntry( machine, castingBatch.getCharge(), currentUser,
                "Die Charge " + castingBatch.getCharge().substring( 2 ) + " an der Gießanlage " + machine + " wechselt in den Status " + CasterStep.Unloading.getDescription() + " (" + startTS.format(
                        DateTimeFormatter.ofPattern( "dd.MM.yy HH:mm:ss" ) ) + ")" );
    }

    @Override
    public void unloadSlabs( String currentUser, long castingBatchOID, int castingLength ) throws BusinessException
    {
        final CastingBatch castingBatch = casterScheduleService.findCastingBatch( castingBatchOID );
        final String machine = castingBatch.getExecutingMachine().getApk();
        final Caster caster = resourceService.getCaster( machine );
        final MeltingFurnace meltingFurnace = castingBatch.getMeltingFurnace();

        castingBatch.setCastingLength( castingLength );

        processStepService.finishCurrentProcessStep( castingBatch, LocalDateTime.now() );
        casterScheduleService.setExecutionState( castingBatch, Casting.SCHEDULABLE_STATE.SUCCESS );

        casterScheduleService.replicateCache( castingBatch );
        machineService.replicateCache( caster );
        machineService.replicateCache( meltingFurnace );
        casterInstructionService.replicateCache( caster );
        furnaceInstructionService.replicateCache( meltingFurnace );

        productionLogHome.createEntry( machine, castingBatch.getCharge(), currentUser, "Die Charge " + castingBatch.getCharge().substring( 2 ) + " wurde erfolgreich abgedient" );

        // Prüfen ob nur ein Ofen Status Freigabe hat, dann diesen automatisch aktivieren
        final String[] meltingFurnaces = Casting.getMeltingFurnacesForCaster( machine );
        final CastingBatch leftActiveCastingBatch = castingBatchHome.findActiveForMeltingFurnace( meltingFurnaces[0] );
        final CastingBatch rightActiveCastingBatch = castingBatchHome.findActiveForMeltingFurnace( meltingFurnaces[1] );
        // Beide Öfen sind im Status Freigabe Gießen, dann den ältesten wählen
        if ( leftActiveCastingBatch != null && leftActiveCastingBatch.getReleaseTS() != null && rightActiveCastingBatch != null && rightActiveCastingBatch.getReleaseTS() != null )
        {
            if ( leftActiveCastingBatch.getReleaseTS().isBefore( rightActiveCastingBatch.getReleaseTS() ) )
            {
                activateCaster( leftActiveCastingBatch );
            }
            else
            {
                activateCaster( rightActiveCastingBatch );
            }
        }
        else if ( leftActiveCastingBatch != null && leftActiveCastingBatch.getReleaseTS() != null )
        {
            activateCaster( leftActiveCastingBatch );
        }
        else if ( rightActiveCastingBatch != null && rightActiveCastingBatch.getReleaseTS() != null )
        {
            activateCaster( rightActiveCastingBatch );
        }
    }

    private CastingBatch changeFurnaceActivation( String meltingFurnaceApk, long castingBatchOID, boolean activate ) throws BusinessException
    {
        final CastingBatch castingBatch = casterScheduleService.findCastingBatch( castingBatchOID );
        final MeltingFurnace currentMeltingFurnace = castingBatch.getMeltingFurnace();

        final MeltingFurnace meltingFurnace = resourceService.getMeltingFurnace( meltingFurnaceApk );
        castingBatch.setMeltingFurnace( meltingFurnace );
        if ( activate )
        {
            // Gibt es eine andere Charge, die IN_PROGRESS ist, diese deaktivieren
            final CastingBatch currentInProgressBatch = castingBatchHome.findInProgressForMeltingFurnace( meltingFurnaceApk );
            if ( currentInProgressBatch != null )
            {
                changeFurnaceActivation( meltingFurnaceApk, currentInProgressBatch.getObjid(), false );
            }

            casterScheduleService.setExecutionState( castingBatch, Casting.SCHEDULABLE_STATE.IN_PROGRESS );
            processStepService.clearProcessSteps( castingBatch );
            castingBatch.setUnloadingTS( null );
            castingBatch.setReleaseTS( null );
        }
        else
        {
            casterScheduleService.setExecutionState( castingBatch, Casting.SCHEDULABLE_STATE.PAUSED );
            meltingFurnace.setCurrentStep( null );
            //meltingFurnace.setCurrentStepStartTS( null );
            meltingFurnace.setCurrentStepDuration( 0 );
            machineService.replicateCache( meltingFurnace );
        }
        final Machine caster = castingBatch.getExecutingMachine();

        casterScheduleService.replicateCache( castingBatch );
        if ( caster != null )
        {
            machineService.replicateCache( caster );
            casterInstructionService.replicateCache( (Caster) caster );
        }

        if ( currentMeltingFurnace != null && currentMeltingFurnace.getObjid() != meltingFurnace.getObjid() )
        {
            furnaceInstructionService.replicateCache( currentMeltingFurnace );
        }

        furnaceInstructionService.replicateCache( meltingFurnace );

        return castingBatch;
    }

    private CastingBatch activateCaster( CastingBatch castingBatch ) throws BusinessException
    {
        final Caster caster = (Caster) castingBatch.getExecutingMachine();

        casterScheduleService.setExecutionState( castingBatch, Casting.SCHEDULABLE_STATE.CASTING_IN_PROGRESS );

        casterScheduleService.replicateCache( castingBatch );
        machineService.replicateCache( caster );

        casterInstructionService.replicateCache( caster );

        return castingBatch;
    }

    private int calcDuration( CastingBatch castingBatch, FurnaceStep furnaceStep )
    {
        final List<MeltingKTDTO> meltingKTs = meltingKTService.load();
        final Optional<MeltingKTDTO> meltingKT = meltingKTService.findMeltingKnowledge( meltingKTs, castingBatch );

        return meltingKT.map( meltingKTDTO -> (int) meltingKTService.getPlannedDuration( furnaceStep, meltingKTDTO ).toMinutes() ).orElse( 60 );

    }

    private int calcDuration( CastingBatch castingBatch, CasterStep casterStep )
    {
        final List<CastingKTDTO> castingKTs = castingKTService.load();
        final Optional<CastingKTDTO> castingKT = castingKTService.findCastingKnowledge( castingKTs, castingBatch );

        return castingKT.map( castingKTDTO -> (int) castingKTService.getPlannedDuration( casterStep, castingKTDTO ).toMinutes() ).orElse( 60 );

    }

}