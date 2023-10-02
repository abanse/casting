package com.hydro.casting.gui.prod.dialog;

import com.google.inject.Injector;
import com.hydro.casting.common.Casting;
import com.hydro.casting.gui.model.CastingClientModel;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.core.gui.ClientModelManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

public class SelectSchedulableDialog extends Dialog<CasterScheduleDTO>
{

    public SelectSchedulableDialog( String machineApk, Injector injector )
    {
        final DialogPane dialogPane = getDialogPane();

        setTitle( "Charge aktivieren" );
        dialogPane.setHeaderText( "Bitte selektieren Sie die Charge, die Sie am Ofen " + machineApk + " aktivieren wollen" );
        dialogPane.getButtonTypes().addAll( ButtonType.OK, ButtonType.CANCEL );

        final TableView<CasterScheduleDTO> table = new TableView<>();
        table.setEditable( true );

        final CastingClientModel castingModel = (CastingClientModel) injector.getInstance( ClientModelManager.class ).getClientModel( CastingClientModel.ID );
        final List<CasterScheduleDTO> casterSchedules;
        final String casterApk = Casting.getCasterForMeltingFurnace( machineApk );
        if ( Casting.MACHINE.CASTER_50.equals( casterApk ) )
        {
            casterSchedules = castingModel.getView( CastingClientModel.CASTER50_VIEW_ID );
        }
        else if ( Casting.MACHINE.CASTER_60.equals( casterApk ) )
        {
            casterSchedules = castingModel.getView( CastingClientModel.CASTER60_VIEW_ID );
        }
        else if ( Casting.MACHINE.CASTER_70.equals( casterApk ) )
        {
            casterSchedules = castingModel.getView( CastingClientModel.CASTER70_VIEW_ID );
        }
        else if ( Casting.MACHINE.CASTER_80.equals( casterApk ) )
        {
            casterSchedules = castingModel.getView( CastingClientModel.CASTER80_VIEW_ID );
        }
        else
        {
            casterSchedules = Collections.emptyList();
        }

        final List<CasterScheduleDTO> filteredEntries = casterSchedules.stream().filter( casterScheduleDTO -> {
            if ( casterScheduleDTO.getExecutionState() == Casting.SCHEDULABLE_STATE.RELEASED || casterScheduleDTO.getExecutionState() == Casting.SCHEDULABLE_STATE.IN_PROGRESS
                    || casterScheduleDTO.getExecutionState() == Casting.SCHEDULABLE_STATE.PAUSED )
            {
                return true;
            }
            return false;
        } ).collect( Collectors.toList() );

        table.setItems( FXCollections.observableArrayList( filteredEntries ) );

        final TableColumn<CasterScheduleDTO, String> chargeColumn = new TableColumn<>( "Charge" );
        chargeColumn.setCellValueFactory( new PropertyValueFactory( "chargeWithoutYear" ) );

        final TableColumn<CasterScheduleDTO, String> furnaceColumn = new TableColumn<>( "Ofen" );
        furnaceColumn.setCellValueFactory( new PropertyValueFactory( "meltingFurnace" ) );

        table.getColumns().addAll( chargeColumn, furnaceColumn );

        dialogPane.setContent( table );

        table.getSelectionModel().selectedIndexProperty().addListener( ( o, a, n ) -> {
            boolean saveEnabled = n != null;
            final Node okButton = getDialogPane().lookupButton( ButtonType.OK );
            if ( okButton == null )
            {
                return;
            }
            okButton.setDisable( !saveEnabled );
        } );

        setResultConverter( buttonType -> {

            if ( buttonType != ButtonType.OK )
            {
                return null;
            }
            return table.getSelectionModel().getSelectedItem();
        } );
    }

    public static CasterScheduleDTO showDialog( String machineApk, Injector injector )
    {
        if ( Platform.isFxApplicationThread() )
        {
            final SelectSchedulableDialog dialog = new SelectSchedulableDialog( machineApk, injector );
            final Optional<CasterScheduleDTO> result = dialog.showAndWait();
            if ( result == null || result.isPresent() == false )
            {
                return null;
            }
            return result.get();
        }
        else
        {
            FutureTask<Optional<CasterScheduleDTO>> future = new FutureTask<>( () -> {
                final SelectSchedulableDialog dialog = new SelectSchedulableDialog( machineApk, injector );
                return dialog.showAndWait();
            } );
            Platform.runLater( future );
            Optional<CasterScheduleDTO> result = null;
            try
            {
                result = future.get();
            }
            catch ( Exception ex )
            {
                // do nothing
            }
            if ( result == null || result.isPresent() == false )
            {
                return null;
            }
            return result.get();
        }
    }
}