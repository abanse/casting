package com.hydro.casting.gui.planning.dialog;

import com.hydro.casting.server.contract.dto.SetupTypeDTO;
import com.hydro.core.gui.ApplicationManager;
import com.hydro.core.gui.comp.IntegerTextField;
import com.hydro.core.gui.validation.MESValidationSupport;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import org.controlsfx.validation.Validator;

import java.util.Optional;
import java.util.concurrent.FutureTask;

public class SetupDialog extends Dialog<SetupTypeDTO>
{
    private IntegerTextField duration = new IntegerTextField();
    private TextField instruction = new TextField();

    public SetupDialog( String title )
    {
        final DialogPane dialogPane = getDialogPane();

        setTitle( title );
        dialogPane.setHeaderText( "Bitte geben Sie die Daten für den Spülguss ein." );
        dialogPane.getButtonTypes().addAll( ButtonType.CANCEL, ButtonType.OK );

        final GridPane gridPane = new GridPane();
        gridPane.setHgap( 5.0 );
        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setPrefWidth( 80 );
        labelColumn.setHalignment( HPos.RIGHT );
        labelColumn.setHgrow( Priority.NEVER );
        ColumnConstraints textFieldColumn = new ColumnConstraints();
        textFieldColumn.setFillWidth( false );
        textFieldColumn.setHgrow( Priority.ALWAYS );
        textFieldColumn.setHalignment( HPos.LEFT );

        gridPane.getColumnConstraints().add( labelColumn );
        gridPane.getColumnConstraints().add( textFieldColumn );
        gridPane.getRowConstraints().add( new RowConstraints( 30.0 ) );
        gridPane.getRowConstraints().add( new RowConstraints( 30.0 ) );
        //gridPane.getRowConstraints().add( new RowConstraints( 100.0 ) );

        instruction.setPromptText( "Bitte einen Namen eintragen" );
        instruction.setPrefWidth( 600 );
        //GridPane.setFillWidth( instruction, true );
        //instruction.setMaxHeight( Double.MAX_VALUE );
        //instruction.setMaxWidth( Double.MAX_VALUE );

        duration.setPrefWidth( 70 );
        duration.setIntValue( 60 );

        Label durationLabel = new Label( "Dauer(Min.):" );
        Label instructionLabel = new Label( "Bemerkung:" );

        GridPane.setColumnIndex( duration, 1 );

        GridPane.setHalignment( instructionLabel, HPos.LEFT );
        GridPane.setRowIndex( instructionLabel, 1 );
        GridPane.setColumnSpan( instructionLabel, 2 );

        GridPane.setRowIndex( instruction, 2 );
        GridPane.setColumnSpan( instruction, 2 );

        gridPane.getChildren().add( durationLabel );
        gridPane.getChildren().add( duration );
        gridPane.getChildren().add( instructionLabel );
        gridPane.getChildren().add( instruction );

        final MESValidationSupport validationSupport = new MESValidationSupport();

        Validator<String> emptyValidator = Validator.createEmptyValidator( "Bemerkung: Eingabe ist erforderlich" );
        validationSupport.registerValidator( instruction, false, emptyValidator );
        duration.addValidationGreaterThan( validationSupport, 2, 10000, "Dauer" );

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
            SetupTypeDTO result = new SetupTypeDTO();

            result.setName( instruction.getText() );
            result.setDuration( duration.getIntValue() );
            result.setInstruction( instruction.getText() );

            return result;
        } );

        Platform.runLater( () -> duration.requestFocus() );
    }

    public static SetupTypeDTO showDialog( ApplicationManager applicationManager, String title )
    {
        if ( Platform.isFxApplicationThread() )
        {
            final SetupDialog dialog = new SetupDialog( title );
            dialog.initOwner( applicationManager.getMainStage() );
            final Optional<SetupTypeDTO> result = dialog.showAndWait();
            if ( result == null || result.isPresent() == false )
            {
                return null;
            }
            return result.get();
        }
        else
        {
            FutureTask<Optional<SetupTypeDTO>> future = new FutureTask<>( () -> {
                final SetupDialog dialog = new SetupDialog( title );
                dialog.initOwner( applicationManager.getMainStage() );
                return dialog.showAndWait();
            } );
            Platform.runLater( future );
            Optional<SetupTypeDTO> result = null;
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
