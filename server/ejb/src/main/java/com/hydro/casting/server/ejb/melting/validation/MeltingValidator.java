package com.hydro.casting.server.ejb.melting.validation;

import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.common.constant.MelterStep;
import com.hydro.casting.server.contract.dto.MeltingInstructionDTO;
import com.hydro.casting.server.ejb.main.service.ProcessStepService;
import com.hydro.casting.server.ejb.main.service.ResourceService;
import com.hydro.casting.server.model.res.MeltingFurnace;
import com.hydro.casting.server.model.sched.MeltingBatch;
import com.hydro.casting.server.model.sched.dao.MeltingBatchHome;
import com.hydro.casting.server.model.sched.dao.SchedulableHome;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.core.server.contract.workplace.TaskValidation;
import com.hydro.core.server.contract.workplace.ViewModel;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;

@Stateless
public class MeltingValidator
{
    @EJB
    private MeltingBatchHome meltingBatchHome;
    @EJB
    private SchedulableHome schedulableHome;
    @EJB
    private ProcessStepService processStepService;
    @EJB
    private ResourceService resourceService;

    public ViewModel<MeltingInstructionDTO> validate( MeltingInstructionDTO meltingInstructionDTO ) throws BusinessException
    {
        final ViewModel<MeltingInstructionDTO> viewModel = new ViewModel<>();
        viewModel.setCurrentDTOs( meltingInstructionDTO );

        final MeltingBatch activeBatch;
        String melterApk = null;
        if ( meltingInstructionDTO != null && meltingInstructionDTO.getMachine() != null )
        {
            melterApk = meltingInstructionDTO.getMachine();
            activeBatch = meltingBatchHome.findActiveForMelter( melterApk );
        }
        else
        {
            activeBatch = null;
        }

        validateCommon( melterApk, activeBatch, viewModel );

        if ( melterApk != null )
        {
            switch ( melterApk )
            {
            case Casting.MACHINE.MELTING_FURNACE_S1:
                validateMachine_S1( activeBatch, viewModel );
                break;
            case Casting.MACHINE.MELTING_FURNACE_S2:
                validateMachine_S2( activeBatch, viewModel );
            }
        }

        return viewModel;
    }

    private void validateCommon( String melterApk, MeltingBatch activeBatch, ViewModel<MeltingInstructionDTO> viewModel ) throws BusinessException
    {
        // Neue Schmelzcharge erzeugen
        validateCreateNewMeltingCharge( viewModel );
        // Charge aktivieren
        validateActivateCharge( activeBatch, viewModel );
        // Charge deaktivieren
        validateDeactivateCharge( activeBatch, viewModel );
        // Nächste Charge aktivieren
        MeltingFurnace meltingFurnace = resourceService.getMeltingFurnace( melterApk );
        validateActivateNextCharge( meltingFurnace, viewModel );
    }

    private void validateMachine_S2( MeltingBatch activeBatch, ViewModel<MeltingInstructionDTO> viewModel )
    {
        // Abkrätzen Hauptkammer
        validateStepNotActive( MelterStep.Skimming, activeBatch, viewModel, SecurityCasting.MELTING.ACTION.SKIMMING );
        // Ende Abkrätzen Hauptkammer
        validateStepActive( MelterStep.Skimming, activeBatch, viewModel, SecurityCasting.MELTING.ACTION.END_SKIMMING );
        // Abkrätzen Schmelzkammer
        validateStepNotActive( MelterStep.SkimmingMeltingChamber, activeBatch, viewModel, SecurityCasting.MELTING.ACTION.SKIMMING_MELTING_CHAMBER );
        // Ende Abkrätzen Schmelzkammer
        validateStepActive( MelterStep.SkimmingMeltingChamber, activeBatch, viewModel, SecurityCasting.MELTING.ACTION.END_SKIMMING_MELTING_CHAMBER );
        // Materialverteilung
        validateStepNotActive( MelterStep.Mixing, activeBatch, viewModel, SecurityCasting.MELTING.ACTION.MIXING );
        // Ende Materialverteilung
        validateStepActive( MelterStep.Mixing, activeBatch, viewModel, SecurityCasting.MELTING.ACTION.END_MIXING );
        // Abfüllen
        validateStepNotActive( MelterStep.Pouring, activeBatch, viewModel, SecurityCasting.MELTING.ACTION.POURING );
        // Ende Abfüllen
        validateStepActive( MelterStep.Pouring, activeBatch, viewModel, SecurityCasting.MELTING.ACTION.END_POURING );
        // Charge beenden
        validateFinishChargeSimple( activeBatch, viewModel );
    }

    private void validateMachine_S1( MeltingBatch activeBatch, ViewModel<MeltingInstructionDTO> viewModel )
    {
        // Charge beenden
        validateFinishCharge( activeBatch, viewModel );
        // Chargieren
        validateSequentialAction( MelterStep.Charging, activeBatch, viewModel, SecurityCasting.MELTING.ACTION.CHARGING );
        // Schmelzen
        validateSequentialAction( MelterStep.Melting, activeBatch, viewModel, SecurityCasting.MELTING.ACTION.MELTING );
        // Abkrätzen
        validateSequentialAction( MelterStep.Skimming, activeBatch, viewModel, SecurityCasting.MELTING.ACTION.SKIMMING );
        // Gattieren
        validateSequentialAction( MelterStep.Treating, activeBatch, viewModel, SecurityCasting.MELTING.ACTION.TREATING );
        // Aufheizen
        validateSequentialAction( MelterStep.Heating, activeBatch, viewModel, SecurityCasting.MELTING.ACTION.HEATING );
        // Abfüllen
        validateSequentialAction( MelterStep.Pouring, activeBatch, viewModel, SecurityCasting.MELTING.ACTION.POURING );

        // Special case: Task to start treating should only be executable when skimming is active
        validateStepActive( MelterStep.Skimming, activeBatch, viewModel, SecurityCasting.MELTING.ACTION.TREATING_SKIMMING_ONLY );

        // S2-specific process steps have to always be validated to false for S1
        validateFalse( MelterStep.SkimmingMeltingChamber, viewModel, SecurityCasting.MELTING.ACTION.SKIMMING_MELTING_CHAMBER );
        validateFalse( MelterStep.SkimmingMeltingChamber, viewModel, SecurityCasting.MELTING.ACTION.END_SKIMMING_MELTING_CHAMBER );
        validateFalse( MelterStep.Mixing, viewModel, SecurityCasting.MELTING.ACTION.MIXING );
        validateFalse( MelterStep.Mixing, viewModel, SecurityCasting.MELTING.ACTION.END_MIXING );

        // Ausbaggern
        validateDredging( activeBatch, viewModel );
    }

    private void validateCreateNewMeltingCharge( ViewModel<MeltingInstructionDTO> viewModel )
    {
        TaskValidation taskValidation = new TaskValidation();
        taskValidation.setId( SecurityCasting.MELTING.ACTION.CREATE_NEW_MELTING_CHARGE );

        taskValidation.setDisabled( false );
        taskValidation.setRemark( null );

        viewModel.addTaskValidations( taskValidation );
    }

    private void validateActivateCharge( MeltingBatch meltingBatch, ViewModel<MeltingInstructionDTO> viewModel )
    {
        TaskValidation taskValidation = new TaskValidation();
        taskValidation.setId( SecurityCasting.MELTING.ACTION.ACTIVATE_CHARGE );

        if ( meltingBatch != null )
        {
            taskValidation.setDisabled( true );
            taskValidation.setRemark( "Es ist bereits eine Charge aktiv" );
        }
        else
        {
            taskValidation.setDisabled( false );
            taskValidation.setRemark( null );
        }

        viewModel.addTaskValidations( taskValidation );
    }

    private void validateActivateNextCharge( MeltingFurnace meltingFurnace, ViewModel<MeltingInstructionDTO> viewModel )
    {
        TaskValidation taskValidation = new TaskValidation();
        taskValidation.setId( SecurityCasting.MELTING.ACTION.ACTIVATE_NEXT_MELTING_CHARGE );

        if ( meltingFurnace != null && schedulableHome.findNextAvailableSchedulableForExecution( meltingFurnace ) != null )
        {
            taskValidation.setDisabled( false );
            taskValidation.setRemark( null );
        }
        else
        {
            taskValidation.setDisabled( true );
            taskValidation.setRemark( "Es existiert keine nächste freigegebene Charge" );
        }

        viewModel.addTaskValidations( taskValidation );
    }

    private void validateDeactivateCharge( MeltingBatch meltingBatch, ViewModel<MeltingInstructionDTO> viewModel )
    {
        // Charge deaktivieren
        TaskValidation taskValidation = new TaskValidation();
        taskValidation.setId( SecurityCasting.MELTING.ACTION.DEACTIVATE_CHARGE );

        if ( isChargeActiveValidationSuccessful( meltingBatch, taskValidation ) )
        {
            taskValidation.setDisabled( false );
            taskValidation.setRemark( null );
        }

        viewModel.addTaskValidations( taskValidation );
    }

    private void validateFinishCharge( MeltingBatch meltingBatch, ViewModel<MeltingInstructionDTO> viewModel )
    {
        // Charge beenden
        TaskValidation taskValidation = new TaskValidation();
        taskValidation.setId( SecurityCasting.MELTING.ACTION.FINISH_CHARGE );

        if ( isChargeActiveValidationSuccessful( meltingBatch, taskValidation ) )
        {
            if ( isFurnaceEmptyValidationSuccessful( meltingBatch, taskValidation ) )
            {
                taskValidation.setDisabled( false );
                taskValidation.setRemark( null );
            }
        }

        viewModel.addTaskValidations( taskValidation );
    }

    private void validateFinishChargeSimple( MeltingBatch meltingBatch, ViewModel<MeltingInstructionDTO> viewModel )
    {
        // Charge beenden
        TaskValidation taskValidation = new TaskValidation();
        taskValidation.setId( SecurityCasting.MELTING.ACTION.FINISH_CHARGE );

        if ( isChargeActiveValidationSuccessful( meltingBatch, taskValidation ) )
        {
            taskValidation.setDisabled( false );
            taskValidation.setRemark( null );
        }

        viewModel.addTaskValidations( taskValidation );
    }

    private void validateDredging( MeltingBatch meltingBatch, ViewModel<MeltingInstructionDTO> viewModel )
    {
        // Abfüllen -> Nur möglich, wenn Ofen schon leer ist, also nach dem Schritt abfüllen
        TaskValidation taskValidation = new TaskValidation();
        taskValidation.setId( SecurityCasting.MELTING.ACTION.DREDGING );

        if ( isChargeActiveValidationSuccessful( meltingBatch, taskValidation ) )
        {
            if ( isFurnaceEmptyValidationSuccessful( meltingBatch, taskValidation ) )
            {
                addTaskValidationForSequentialMeltingStep( MelterStep.Dredging, meltingBatch, taskValidation );
            }
        }

        viewModel.addTaskValidations( taskValidation );
    }

    private void validateSequentialAction( MelterStep melterStep, MeltingBatch meltingBatch, ViewModel<MeltingInstructionDTO> viewModel, String actionId )
    {
        TaskValidation taskValidation = new TaskValidation();
        taskValidation.setId( actionId );

        if ( isChargeActiveValidationSuccessful( meltingBatch, taskValidation ) )
        {
            addTaskValidationForSequentialMeltingStep( melterStep, meltingBatch, taskValidation );
        }

        viewModel.addTaskValidations( taskValidation );
    }

    private void validateStepActive( MelterStep melterStep, MeltingBatch meltingBatch, ViewModel<MeltingInstructionDTO> viewModel, String actionId )
    {
        TaskValidation taskValidation = new TaskValidation();
        taskValidation.setId( actionId );

        if ( isChargeActiveValidationSuccessful( meltingBatch, taskValidation ) )
        {
            if ( isStepActiveValidationSuccessful( melterStep, meltingBatch, taskValidation ) )
            {
                taskValidation.setDisabled( false );
                taskValidation.setRemark( null );
            }
        }

        viewModel.addTaskValidations( taskValidation );
    }

    private void validateStepNotActive( MelterStep melterStep, MeltingBatch meltingBatch, ViewModel<MeltingInstructionDTO> viewModel, String actionId )
    {
        TaskValidation taskValidation = new TaskValidation();
        taskValidation.setId( actionId );

        if ( isChargeActiveValidationSuccessful( meltingBatch, taskValidation ) )
        {
            if ( isNotStepActiveValidationSuccessful( melterStep, meltingBatch, taskValidation ) )
            {
                taskValidation.setDisabled( false );
                taskValidation.setRemark( null );
            }
        }

        viewModel.addTaskValidations( taskValidation );
    }

    private void validateFalse( MelterStep melterStep, ViewModel<MeltingInstructionDTO> viewModel, String actionId )
    {
        TaskValidation taskValidation = new TaskValidation();
        taskValidation.setId( actionId );
        taskValidation.setDisabled( true );
        taskValidation.setRemark( melterStep.getDescription() + " für diese Anlage nicht verfügbar" );
        viewModel.addTaskValidations( taskValidation );
    }

    private void addTaskValidationForSequentialMeltingStep( MelterStep melterStep, MeltingBatch meltingBatch, TaskValidation taskValidation )
    {
        boolean validationSuccessful = false;
        MelterStep[] melterSteps = MelterStep.values();
        Arrays.sort( melterSteps, Comparator.comparing( MelterStep::getOrderId ).reversed() );

        for ( MelterStep currentMelterStep : melterSteps )
        {
            LocalDateTime stepTS = processStepService.getStepTS( meltingBatch, currentMelterStep );
            validationSuccessful = isStepTSEmptyValidationSuccessful( currentMelterStep.getDescription(), stepTS, taskValidation );
            if ( !validationSuccessful || melterStep == currentMelterStep )
            {
                break;
            }
        }

        if ( validationSuccessful )
        {
            taskValidation.setDisabled( false );
            taskValidation.setRemark( null );
        }
    }

    private boolean isChargeActiveValidationSuccessful( MeltingBatch meltingBatch, TaskValidation taskValidation )
    {
        if ( meltingBatch == null )
        {
            taskValidation.setDisabled( true );
            taskValidation.setRemark( "Keine Charge aktiv" );
            return false;
        }

        return true;
    }

    private boolean isFurnaceEmptyValidationSuccessful( MeltingBatch meltingBatch, TaskValidation taskValidation )
    {
        LocalDateTime pouringTS = processStepService.getStepTS( meltingBatch, MelterStep.Pouring );
        if ( pouringTS == null )
        {
            taskValidation.setDisabled( true );
            taskValidation.setRemark( "Schmelzofen ist noch gefüllt" );
            return false;
        }

        return true;
    }

    private boolean isStepTSEmptyValidationSuccessful( String stepName, LocalDateTime localDateTime, TaskValidation taskValidation )
    {
        if ( localDateTime != null )
        {
            taskValidation.setDisabled( true );
            taskValidation.setRemark( stepName + " bereits aktiv" );
            return false;
        }

        return true;
    }

    private boolean isNotStepActiveValidationSuccessful( MelterStep melterStep, MeltingBatch meltingBatch, TaskValidation taskValidation )
    {
        if ( processStepService.isProcessStepActive( meltingBatch, melterStep ) )
        {
            taskValidation.setDisabled( true );
            taskValidation.setRemark( "Schritt bereits aktiv" );
            return false;
        }

        return true;
    }

    private boolean isStepActiveValidationSuccessful( MelterStep melterStep, MeltingBatch meltingBatch, TaskValidation taskValidation )
    {
        if ( !processStepService.isProcessStepActive( meltingBatch, melterStep ) )
        {
            taskValidation.setDisabled( true );
            taskValidation.setRemark( melterStep.getDescription() + " nicht aktiv" );
            return false;
        }

        return true;
    }
}
