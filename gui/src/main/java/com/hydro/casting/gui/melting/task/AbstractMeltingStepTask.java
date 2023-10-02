package com.hydro.casting.gui.melting.task;

import com.google.inject.Inject;
import com.hydro.casting.common.Casting;
import com.hydro.casting.common.constant.MelterStep;
import com.hydro.casting.gui.prod.dialog.StepTimestampDialog;
import com.hydro.casting.gui.prod.dialog.result.StepTimestampResult;
import com.hydro.casting.server.contract.dto.MeltingInstructionDTO;
import com.hydro.casting.server.contract.melting.MeltingBusiness;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.task.AbstractTask;

import java.time.LocalDateTime;
public abstract class AbstractMeltingStepTask extends AbstractTask
{
    private final static MelterStep DEFAULT_MELTER_STEP_AFTER_FINISHING = MelterStep.Charging;

    @Inject
    private BusinessManager businessManager;
    @Inject
    private SecurityManager securityManager;
    @Inject
    private NotifyManager notifyManager;

    private String machineApk;
    private MeltingInstructionDTO meltingInstructionDTO;
    private MelterStep melterStep;
    private StepTimestampResult stepTimestampResult;
    private boolean withoutMachine = false;
    private boolean finishTask = false;
    private Runnable runnableAfterWork;

    public String getMachineApk()
    {
        return machineApk;
    }

    public void setMachineApk( String machineApk )
    {
        this.machineApk = machineApk;
    }

    public MeltingInstructionDTO getMeltingInstructionDTO()
    {
        return meltingInstructionDTO;
    }

    public void setMeltingInstructionDTO( MeltingInstructionDTO meltingInstructionDTO )
    {
        this.meltingInstructionDTO = meltingInstructionDTO;
    }

    public MelterStep getMeltingStep()
    {
        return melterStep;
    }

    public void setMeltingStep( MelterStep melterStep )
    {
        this.melterStep = melterStep;
    }

    public StepTimestampResult getStepTimestampResult()
    {
        return stepTimestampResult;
    }

    public void setWithoutMachine( boolean withoutMachine )
    {
        this.withoutMachine = withoutMachine;
    }

    public void setFinishTask( boolean finishTask )
    {
        this.finishTask = finishTask;
    }

    public void setRunnableAfterWork( Runnable runnableAfterWork )
    {
        this.runnableAfterWork = runnableAfterWork;
    }

    @Override
    public boolean beforeStart() throws Exception
    {
        LocalDateTime stepStartTS;

        switch ( melterStep )
        {
        case Melting:
            stepStartTS = meltingInstructionDTO.getMeltingStartTS();
            break;
        case Skimming:
            stepStartTS = meltingInstructionDTO.getSkimmingStartTS();
            break;
        case SkimmingMeltingChamber:
            stepStartTS = meltingInstructionDTO.getSkimmingMeltingChamberStartTS();
            break;
        case Mixing:
            stepStartTS = meltingInstructionDTO.getMixingStartTS();
            break;
        case Pouring:
            stepStartTS = meltingInstructionDTO.getPouringStartTS();
            break;
        default:
            stepStartTS = null;
        }

        stepTimestampResult = StepTimestampDialog.showDialog( "Schmelzofen " + melterStep.getDescription(), stepStartTS );

        return stepTimestampResult != null;
    }

    @Override
    public void doWork() throws Exception
    {
        MeltingBusiness meltingBusiness = businessManager.getSession( MeltingBusiness.class );

        // For S2 (or in general two-chamber-melters), additional parameters and paths are required to ensure proper handling of parallel process steps
        if ( machineApk.equals( Casting.MACHINE.MELTING_FURNACE_S2 ) )
        {
            // Special handling: Starting and stopping steps is possible without affecting the machine, which basically means a step is started in parallel to another
            if ( withoutMachine )
            {
                if ( finishTask )
                {
                    meltingBusiness.finishMeltingStepWithoutChangingMachine( securityManager.getCurrentUser(), getMachineApk(), getMeltingInstructionDTO().getMeltingBatchOID(), getMeltingStep(),
                            getStepTimestampResult().getStepTime() );
                    notifyManager.showSplashMessage( "Ende " + getMeltingStep().getDescription() + " wurde gebucht" );
                }
                else
                {
                    meltingBusiness.startMeltingStepWithoutChangingMachine( securityManager.getCurrentUser(), getMachineApk(), getMeltingInstructionDTO().getMeltingBatchOID(), getMeltingStep(),
                            getStepTimestampResult().getStepTime() );
                    notifyManager.showSplashMessage( "Start " + getMeltingStep().getDescription() + " wurde gebucht" );
                }
            }
            // Normal handling (with changing the machine entity); for S2 that means starting and stopping a default melting step on stopping and starting a process step
            else
            {
                if ( finishTask )
                {
                    meltingBusiness.startMeltingStepAndFinishOther( securityManager.getCurrentUser(), getMachineApk(), getMeltingInstructionDTO().getMeltingBatchOID(),
                            DEFAULT_MELTER_STEP_AFTER_FINISHING, getMeltingStep(), getStepTimestampResult().getStepTime() );

                    notifyManager.showSplashMessage( "Ende " + getMeltingStep().getDescription() + " wurde gebucht" );
                }
                else
                {
                    meltingBusiness.startMeltingStepAndFinishOther( securityManager.getCurrentUser(), getMachineApk(), getMeltingInstructionDTO().getMeltingBatchOID(), getMeltingStep(),
                            DEFAULT_MELTER_STEP_AFTER_FINISHING, getStepTimestampResult().getStepTime() );

                    notifyManager.showSplashMessage( "Start " + getMeltingStep().getDescription() + " wurde gebucht" );
                }
            }
        }
        // Standard handling for every other one-chamber-melter (currently only S1)
        else
        {

            meltingBusiness.startMeltingStep( securityManager.getCurrentUser(), getMachineApk(), getMeltingInstructionDTO().getMeltingBatchOID(), getMeltingStep(),
                    getStepTimestampResult().getStepTime() );

            notifyManager.showSplashMessage( "Start " + getMeltingStep().getDescription() + " wurde gebucht" );

        }

        if ( runnableAfterWork != null )
        {
            runnableAfterWork.run();
        }
    }
}
