package com.hydro.casting.server.ejb.prod.detail;

import com.hydro.casting.common.constant.CasterStep;
import com.hydro.casting.common.constant.FurnaceStep;
import com.hydro.casting.server.contract.dto.*;
import com.hydro.casting.server.ejb.main.service.ProcessStepService;
import com.hydro.casting.server.ejb.planning.service.CastingKTService;
import com.hydro.casting.server.ejb.planning.service.MeltingKTService;
import com.hydro.casting.server.model.sched.CastingBatch;
import com.hydro.casting.server.model.sched.ProcessStep;
import com.hydro.casting.server.model.sched.dao.CastingBatchHome;
import com.hydro.core.server.contract.workplace.DetailProvider;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Stateless( name = "TimeManagementListDetailProvider" )
public class TimeManagementListDetailProvider implements DetailProvider<CastingInstructionDTO, TimeManagementListDTO>
{
    @EJB
    private CastingBatchHome castingBatchHome;

    @EJB
    private CastingKTService castingKTService;

    @EJB
    private MeltingKTService meltingKTService;

    @EJB
    private ProcessStepService processStepService;

    @Override
    public TimeManagementListDTO loadDetail( CastingInstructionDTO master, Map<String, String> context )
    {
        if ( master == null || master.getCastingBatchOID() == null || master.getMachineApk() == null )
        {
            return null;
        }
        final CastingBatch castingBatch = castingBatchHome.findById( master.getCastingBatchOID() );
        if ( castingBatch == null )
        {
            return null;
        }

        final TimeManagementListDTO timeManagementListDTO = new TimeManagementListDTO();

        if ( master.getMachineApk().endsWith( "0" ) )
        {
            final List<CastingBatch> castingBatches = castingBatchHome.findLastCastingBatches( master.getMachineApk(), LocalDateTime.now().minusHours( 16 ) );
            castingBatches.add( castingBatch );
            final List<CastingKTDTO> castingKTs = castingKTService.load();
            for ( CastingBatch cb : castingBatches )
            {
                addCastingTimeManagements( timeManagementListDTO, master.getMachineApk(), cb, castingKTs );
            }
        }
        else
        {
            final List<CastingBatch> castingBatches = castingBatchHome.findLastCastingBatchesForFurnace( master.getMachineApk(), LocalDateTime.now().minusHours( 16 ) );
            castingBatches.add( castingBatch );
            final List<MeltingKTDTO> meltingKTs = meltingKTService.load();
            for ( CastingBatch cb : castingBatches )
            {
                addFurnaceTimeManagements( timeManagementListDTO, master.getMachineApk(), cb, meltingKTs );
            }
        }

        return timeManagementListDTO;
    }

    private void addCastingTimeManagements( TimeManagementListDTO timeManagementListDTO, String machineApk, CastingBatch castingBatch, List<CastingKTDTO> castingKTs )
    {
        final CastingKTDTO castingKT = castingKTService.findCastingKnowledge( castingKTs, castingBatch ).orElse( null );
        final TimeManagementDTO timeManagementDTO = new TimeManagementDTO();
        timeManagementDTO.setMachine( machineApk );
        timeManagementDTO.setCharge( castingBatch.getCharge().substring( 2 ) );
        //timeManagementDTO.setPlannedStart( castingBatch.getReleaseTS() );

        final List<TimeManagementPhaseDTO> phases = new ArrayList<>();

        // There should only be one process step casting for a finished batch
        ProcessStep castingPS = processStepService.getProcessStepUnique( castingBatch, CasterStep.Casting );
        final TimeManagementPhaseDTO casting = new TimeManagementPhaseDTO();
        if ( castingPS != null )
        {
            // Korrektur planned start
            timeManagementDTO.setPlannedStart( castingPS.getStart() );
            casting.setName( castingPS.getName() );
            casting.setStart( castingPS.getStart() );
            casting.setPlannedDuration( getPlannedDuration( CasterStep.Casting, castingKT ) );
            if ( castingPS.getEnd() != null )
            {
                casting.setActualDuration( Duration.between( castingPS.getStart(), castingPS.getEnd() ) );
            }
        }
        else
        {
            casting.setName( CasterStep.Casting.getShortName() );
            casting.setPlannedDuration( getPlannedDuration( CasterStep.Casting, castingKT ) );
        }
        phases.add( casting );

        // There should only be one process step unloading for a finished batch
        ProcessStep unloadingPS = processStepService.getProcessStepUnique( castingBatch, CasterStep.Unloading );
        final TimeManagementPhaseDTO unloading = new TimeManagementPhaseDTO();
        if ( unloadingPS != null )
        {
            unloading.setName( unloadingPS.getName() );
            unloading.setStart( unloadingPS.getStart() );
            unloading.setPlannedDuration( getPlannedDuration( CasterStep.Unloading, castingKT ) );
            if ( unloadingPS.getEnd() != null )
            {
                unloading.setActualDuration( Duration.between( unloadingPS.getStart(), unloadingPS.getEnd() ) );
            }
        }
        else
        {
            unloading.setName( CasterStep.Unloading.getShortName() );
            unloading.setPlannedDuration( getPlannedDuration( CasterStep.Unloading, castingKT ) );
        }
        phases.add( unloading );

        timeManagementDTO.setPhases( phases );
        timeManagementListDTO.getTimeManagements().add( timeManagementDTO );
    }

    private void addFurnaceTimeManagements( TimeManagementListDTO timeManagementListDTO, String machineApk, CastingBatch castingBatch, List<MeltingKTDTO> meltingKTs )
    {
        final MeltingKTDTO meltingKT = meltingKTService.findMeltingKnowledge( meltingKTs, castingBatch ).orElse( null );

        final TimeManagementDTO timeManagementDTO = new TimeManagementDTO();
        timeManagementDTO.setMachine( machineApk );
        timeManagementDTO.setCharge( castingBatch.getCharge().substring( 2 ) );
        timeManagementDTO.setPlannedStart( castingBatch.getInProgressTS() );

        final List<TimeManagementPhaseDTO> phases = new ArrayList<>();

        List<ProcessStep> preparingPS = processStepService.getProcessSteps( castingBatch, FurnaceStep.Preparing );
        List<ProcessStep> chargingPS = processStepService.getProcessSteps( castingBatch, FurnaceStep.Charging );
        List<ProcessStep> treatingPS = processStepService.getProcessSteps( castingBatch, FurnaceStep.Treating );
        List<ProcessStep> skimmingPS = processStepService.getProcessSteps( castingBatch, FurnaceStep.Skimming );
        List<ProcessStep> restingPS = processStepService.getProcessSteps( castingBatch, FurnaceStep.Resting );
        List<ProcessStep> readyToCastPS = processStepService.getProcessSteps( castingBatch, FurnaceStep.ReadyToCast );
        List<ProcessStep> castingPS = processStepService.getProcessSteps( castingBatch, FurnaceStep.Casting );

        Duration plannedDuration = getPlannedDuration( FurnaceStep.Preparing, meltingKT );
        if ( isStepPresent( preparingPS ) )
        {
            processProcessSteps( preparingPS, plannedDuration, phases );

            // Korrektur planned start
            timeManagementDTO.setPlannedStart( preparingPS.get( 0 ).getStart() );
        }

        plannedDuration = getPlannedDuration( FurnaceStep.Charging, meltingKT );
        if ( isStepPresent( chargingPS ) )
        {
            processProcessSteps( chargingPS, plannedDuration, phases );

            // Korrektur planned start
            if ( !isStepPresent( preparingPS ) )
            {
                timeManagementDTO.setPlannedStart( chargingPS.get( 0 ).getStart() );
            }
        }
        else if ( !isStepPresent( treatingPS ) && !isStepPresent( skimmingPS ) && !isStepPresent( restingPS ) )
        {
            processPlannedStep( FurnaceStep.Charging, plannedDuration, phases );
        }

        plannedDuration = getPlannedDuration( FurnaceStep.Treating, meltingKT );
        if ( isStepPresent( treatingPS ) )
        {
            processProcessSteps( treatingPS, plannedDuration, phases );
        }
        else if ( !isStepPresent( skimmingPS ) && !isStepPresent( restingPS ) )
        {
            processPlannedStep( FurnaceStep.Treating, plannedDuration, phases );
        }

        plannedDuration = getPlannedDuration( FurnaceStep.Skimming, meltingKT );
        if ( isStepPresent( skimmingPS ) )
        {
            processProcessSteps( skimmingPS, plannedDuration, phases );
        }
        else if ( !isStepPresent( restingPS ) )
        {
            processPlannedStep( FurnaceStep.Skimming, plannedDuration, phases );
        }

        plannedDuration = getPlannedDuration( FurnaceStep.Resting, meltingKT );
        if ( isStepPresent( restingPS ) )
        {
            processProcessSteps( restingPS, plannedDuration, phases );
        }
        else
        {
            processPlannedStep( FurnaceStep.Resting, plannedDuration, phases );
        }

        plannedDuration = getPlannedDuration( FurnaceStep.ReadyToCast, meltingKT );
        if ( isStepPresent( readyToCastPS ) )
        {
            processProcessSteps( readyToCastPS, plannedDuration, phases );
        }
        else
        {
            processPlannedStep( FurnaceStep.ReadyToCast, plannedDuration, phases );
        }

        plannedDuration = getPlannedDuration( FurnaceStep.Casting, meltingKT );
        if ( isStepPresent( castingPS ) )
        {
            processProcessSteps( castingPS, plannedDuration, phases );
        }
        else
        {
            processPlannedStep( FurnaceStep.Casting, plannedDuration, phases );
        }

        timeManagementDTO.setPhases( phases );
        timeManagementListDTO.getTimeManagements().add( timeManagementDTO );
    }

    private void processProcessSteps( List<ProcessStep> processSteps, Duration plannedDuration, List<TimeManagementPhaseDTO> phases )
    {
        processSteps.sort( Comparator.comparing( ProcessStep::getStart ) );

        for ( ProcessStep processStep : processSteps )
        {
            final TimeManagementPhaseDTO phase = new TimeManagementPhaseDTO();
            phase.setName( processStep.getName() );
            phase.setStart( processStep.getStart() );
            phase.setPlannedDuration( plannedDuration );
            if ( processStep.getEnd() != null )
            {
                phase.setActualDuration( Duration.between( processStep.getStart(), processStep.getEnd() ) );
            }
            phases.add( phase );
        }
    }

    private void processPlannedStep( FurnaceStep furnaceStep, Duration plannedDuration, List<TimeManagementPhaseDTO> phases )
    {
        final TimeManagementPhaseDTO phase = new TimeManagementPhaseDTO();
        phase.setName( furnaceStep.getShortName() );
        phase.setPlannedDuration( plannedDuration );
        phases.add( phase );
    }

    private boolean isStepPresent( List<ProcessStep> processSteps )
    {
        return processSteps != null && !processSteps.isEmpty();
    }

    private Duration getPlannedDuration( CasterStep casterStep, CastingKTDTO castingKTDTO )
    {
        if ( castingKTDTO == null )
        {
            return Duration.ofMinutes( 60 );
        }

        return castingKTService.getPlannedDuration( casterStep, castingKTDTO );
    }

    private Duration getPlannedDuration( FurnaceStep furnaceStep, MeltingKTDTO meltingKTDTO )
    {
        if ( meltingKTDTO == null )
        {
            return Duration.ofMinutes( 60 );
        }

        return meltingKTService.getPlannedDuration( furnaceStep, meltingKTDTO );
    }
}
