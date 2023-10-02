package com.hydro.casting.gui.planning.dialog;

import com.hydro.casting.common.Casting;
import com.hydro.casting.gui.planning.dialog.result.ConfigureResult;
import com.hydro.casting.gui.util.CastingUtil;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.MachineDTO;
import com.hydro.core.gui.ApplicationManager;
import com.hydro.core.gui.comp.IntegerTextField;
import com.hydro.core.gui.validation.MESValidationSupport;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.util.Optional;
import java.util.concurrent.FutureTask;

public class ConfigureDialog extends Dialog<ConfigureResult>
{
    private IntegerTextField chargeCounter = new IntegerTextField();

    private IntegerTextField castingSequence = new IntegerTextField();
    private TextField charge = new TextField();
    private ComboBox<String> furnace = new ComboBox<>();
    private ComboBox<Number> schedulableState = new ComboBox<>();

    public ConfigureDialog( String title, MachineDTO machineDTO, CasterScheduleDTO scheduleDTO )
    {
        final DialogPane dialogPane = getDialogPane();

        setTitle( title );
        dialogPane.setHeaderText( "Folgende Daten können geändert werden" );
        dialogPane.getButtonTypes().addAll( ButtonType.CANCEL, ButtonType.OK );

        final GridPane gridPane = new GridPane();
        gridPane.setHgap( 5.0 );
        gridPane.setVgap( 5.0 );
        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setHalignment( HPos.RIGHT );
        labelColumn.setHgrow( Priority.ALWAYS );
        labelColumn.setPrefWidth( 300 );
        ColumnConstraints textFieldColumn = new ColumnConstraints();
        textFieldColumn.setFillWidth( false );
        textFieldColumn.setHgrow( Priority.ALWAYS );
        textFieldColumn.setHalignment( HPos.LEFT );
        textFieldColumn.setPrefWidth( 300 );

        gridPane.getColumnConstraints().add( labelColumn );
        gridPane.getColumnConstraints().add( textFieldColumn );

        chargeCounter.setPrefWidth( 80 );
        chargeCounter.setIntValue( machineDTO.getLastCharge() );

        Label chargeLabel = new Label( "Chargenzähler:" );

        GridPane.setColumnIndex( chargeCounter, 1 );

        gridPane.getChildren().add( chargeLabel );
        gridPane.getChildren().add( chargeCounter );

        if ( scheduleDTO != null )
        {
            final Separator separator = new Separator( Orientation.HORIZONTAL );
            gridPane.add( separator, 0, 1, 2, 1 );

            int rowIndex = 2;
            if ( scheduleDTO.getExecutionState() == Casting.SCHEDULABLE_STATE.RELEASED )
            {
                final Label schedCastingSequenceLBL = new Label( "Guss-Nr." );
                gridPane.add( schedCastingSequenceLBL, 0, rowIndex );
                castingSequence.setPrefWidth( 80 );
                if ( scheduleDTO.getCastingSequence() != null )
                {
                    castingSequence.setIntValue( scheduleDTO.getCastingSequence().intValue() );
                }
                gridPane.add( castingSequence, 1, rowIndex );

                rowIndex++;

                final Label schedChargeLBL = new Label( "Charge" );
                gridPane.add( schedChargeLBL, 0, rowIndex );
                charge.setPrefWidth( 80 );
                charge.setText( scheduleDTO.getCharge() );
                gridPane.add( charge, 1, rowIndex );

                rowIndex++;

                final Label furnaceLBL = new Label( "Ofen" );
                gridPane.add( furnaceLBL, 0, rowIndex );
                final ObservableList<String> possibleFurnaces = FXCollections.observableArrayList();
                final String casterGroup = machineDTO.getApk().substring( 0, 1 );
                possibleFurnaces.add( casterGroup + "1" );
                possibleFurnaces.add( casterGroup + "2" );
                furnace.setItems( possibleFurnaces );
                furnace.getSelectionModel().select( scheduleDTO.getMeltingFurnace() );
                gridPane.add( furnace, 1, rowIndex );

                rowIndex++;
            }
            if ( scheduleDTO.getExecutionState() == Casting.SCHEDULABLE_STATE.RELEASED || scheduleDTO.getExecutionState() == Casting.SCHEDULABLE_STATE.IN_PROGRESS || scheduleDTO.getExecutionState() == Casting.SCHEDULABLE_STATE.CASTING_IN_PROGRESS || scheduleDTO.getExecutionState() == Casting.SCHEDULABLE_STATE.UNLOADING_IN_PROGRESS
                    || scheduleDTO.getExecutionState() == Casting.SCHEDULABLE_STATE.FAILED )
            {
                final Label schedulableStateLBL = new Label( "Status" );
                gridPane.add( schedulableStateLBL, 0, rowIndex );
                final ObservableList<Number> possibleStates = FXCollections.observableArrayList();
                possibleStates.add( Casting.SCHEDULABLE_STATE.RELEASED );
                possibleStates.add( Casting.SCHEDULABLE_STATE.IN_PROGRESS );
                possibleStates.add( Casting.SCHEDULABLE_STATE.CASTING_IN_PROGRESS );
                possibleStates.add( Casting.SCHEDULABLE_STATE.UNLOADING_IN_PROGRESS );
                possibleStates.add( Casting.SCHEDULABLE_STATE.SUCCESS );
                possibleStates.add( Casting.SCHEDULABLE_STATE.FAILED );
                possibleStates.add( Casting.SCHEDULABLE_STATE.CANCELED );
                schedulableState.setItems( possibleStates );
                schedulableState.getSelectionModel().select( (Integer) scheduleDTO.getExecutionState() );
                schedulableState.setConverter( CastingUtil.executionStatePropertyConverter );
                gridPane.add( schedulableState, 1, rowIndex );
                final Label schedulableStateInfoLBL = new Label();
                schedulableStateInfoLBL.setAlignment( Pos.CENTER );
                schedulableStateInfoLBL.setMaxWidth( Double.MAX_VALUE );
                schedulableStateInfoLBL.setStyle( "-fx-text-fill: #ff2f00" );
                schedulableState.getSelectionModel().selectedItemProperty().addListener( ( observable, oldValue, newValue ) -> {
                    if ( newValue != null && ( newValue.intValue() == Casting.SCHEDULABLE_STATE.SUCCESS || newValue.intValue() == Casting.SCHEDULABLE_STATE.CANCELED ) )
                    {
                        schedulableStateInfoLBL.setText( "Achtung: Der Eintrag kann danach nur in der Chargenhistorie geändert werden" );
                    }
                    else
                    {
                        schedulableStateInfoLBL.setText( null );
                    }
                } );

                rowIndex++;

                gridPane.add( schedulableStateInfoLBL, 0, rowIndex, 2, 1 );
            }
        }

        final MESValidationSupport validationSupport = new MESValidationSupport();

        chargeCounter.addValidationGreaterThan( validationSupport, 0, 9999, "Charge" );

        validationSupport.initInitialDecoration();
        validationSupport.validationResultProperty().addListener( ( p, o, n ) -> {
            final Node okButton = getDialogPane().lookupButton( ButtonType.OK );
            if ( okButton == null )
            {
                return;
            }
            if ( n != null && n.getErrors() != null && n.getErrors().isEmpty() == false )
            {
                okButton.setDisable( true );
            }
            else
            {
                okButton.setDisable( false );
            }
        } );

        dialogPane.setContent( gridPane );

        setResultConverter( buttonType -> {

            if ( buttonType != ButtonType.OK )
            {
                return null;
            }
            final ConfigureResult configureResult = new ConfigureResult();
            configureResult.setChargeCounter( chargeCounter.getIntValue() );
            if ( castingSequence != null )
            {
                configureResult.setCastingSequence( castingSequence.getIntValue() );
            }
            if ( charge != null )
            {
                configureResult.setCharge( charge.getText() );
            }
            if ( furnace != null )
            {
                configureResult.setFurnace( furnace.getSelectionModel().getSelectedItem() );
            }
            if ( schedulableState != null && schedulableState.getSelectionModel() != null && schedulableState.getSelectionModel().getSelectedItem() != null )
            {
                configureResult.setSchedulableState( schedulableState.getSelectionModel().getSelectedItem().intValue() );
            }

            return configureResult;
        } );

        Platform.runLater( () -> chargeCounter.requestFocus() );
    }

    public static ConfigureResult showDialog( ApplicationManager applicationManager, String title, MachineDTO machineDTO, CasterScheduleDTO scheduleDTO )
    {
        if ( Platform.isFxApplicationThread() )
        {
            final ConfigureDialog dialog = new ConfigureDialog( title, machineDTO, scheduleDTO );
            dialog.initOwner( applicationManager.getMainStage() );
            final Optional<ConfigureResult> result = dialog.showAndWait();
            if ( result == null || result.isPresent() == false )
            {
                return null;
            }
            return result.get();
        }
        else
        {
            FutureTask<Optional<ConfigureResult>> future = new FutureTask<>( () -> {
                final ConfigureDialog dialog = new ConfigureDialog( title, machineDTO, scheduleDTO );
                dialog.initOwner( applicationManager.getMainStage() );
                return dialog.showAndWait();
            } );
            Platform.runLater( future );
            Optional<ConfigureResult> result = null;
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
