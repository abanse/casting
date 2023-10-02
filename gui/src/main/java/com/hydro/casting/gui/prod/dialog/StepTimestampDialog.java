package com.hydro.casting.gui.prod.dialog;

import com.hydro.casting.gui.prod.dialog.result.StepTimestampResult;
import com.hydro.core.common.product.COREProductDefinition;
import com.hydro.core.gui.comp.DateTimePicker;
import com.hydro.core.gui.comp.numpad.NumPadFonts;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.FutureTask;

public class StepTimestampDialog extends Dialog<StepTimestampResult>
{
    enum ChangeType
    {plusDay, plusHour, plusMinute, minusDay, minusHour, minusMinute}

    ;

    private DateTimePicker dateTimePicker = new DateTimePicker();

    private EventHandler<ActionEvent> changeTimeActionEventEventHandler = event -> {
        final ChangeType changeType = (ChangeType) ( (Button) event.getSource() ).getUserData();
        if ( changeType == ChangeType.plusDay )
        {
            dateTimePicker.setLocalDateTime( dateTimePicker.getLocalDateTime().plusDays( 1 ) );
        }
        else if ( changeType == ChangeType.plusHour )
        {
            dateTimePicker.setLocalDateTime( dateTimePicker.getLocalDateTime().plusHours( 1 ) );
        }
        else if ( changeType == ChangeType.plusMinute )
        {
            dateTimePicker.setLocalDateTime( dateTimePicker.getLocalDateTime().plusMinutes( 1 ) );
        }
        else if ( changeType == ChangeType.minusMinute )
        {
            dateTimePicker.setLocalDateTime( dateTimePicker.getLocalDateTime().minusMinutes( 1 ) );
        }
        else if ( changeType == ChangeType.minusHour )
        {
            dateTimePicker.setLocalDateTime( dateTimePicker.getLocalDateTime().minusHours( 1 ) );
        }
        else if ( changeType == ChangeType.minusDay )
        {
            dateTimePicker.setLocalDateTime( dateTimePicker.getLocalDateTime().minusDays( 1 ) );
        }
    };

    public StepTimestampDialog( String stepName, LocalDateTime lastStepStartTS )
    {
        final DialogPane dialogPane = getDialogPane();
        dialogPane.getStylesheets().add( getClass().getResource( COREProductDefinition.getInstance().getStylesheet() ).toExternalForm() );

        setTitle( "Startzeit für Vorgang '" + stepName + "'" );
        dialogPane.setHeaderText( "Bitte geben Sie die Startzeit für den Vorgang '" + stepName + "' an" );
        dialogPane.getButtonTypes().addAll( ButtonType.OK, ButtonType.CANCEL );

        dateTimePicker.setDateTime( LocalDateTime.now() );
        dateTimePicker.setOrientation( Orientation.HORIZONTAL );

        final GridPane mainPane = new GridPane();
        mainPane.setAlignment( Pos.CENTER );
        mainPane.setHgap( 5 );
        mainPane.setVgap( 5 );
        mainPane.getColumnConstraints().add( new ColumnConstraints( 120 ) );
        mainPane.getColumnConstraints().add( new ColumnConstraints( 40 ) );
        mainPane.getColumnConstraints().add( new ColumnConstraints( 40 ) );
        mainPane.getColumnConstraints().add( new ColumnConstraints( 32 ) );

        mainPane.add( createButton( "+", ChangeType.plusDay ), 0, 0 );
        mainPane.add( createButton( "+", ChangeType.plusHour ), 1, 0 );
        mainPane.add( createButton( "+", ChangeType.plusMinute ), 2, 0 );

        mainPane.add( dateTimePicker, 0, 1, 4, 1 );

        mainPane.add( createButton( "-", ChangeType.minusDay ), 0, 2 );
        mainPane.add( createButton( "-", ChangeType.minusHour ), 1, 2 );
        mainPane.add( createButton( "-", ChangeType.minusMinute ), 2, 2 );

        final Label infoLabel = new Label();
        infoLabel.setStyle( "-fx-text-fill: indianred" );
        mainPane.add( infoLabel, 0, 3, 4, 1 );
        GridPane.setHalignment( infoLabel, HPos.CENTER );

        final BorderPane mainContainerPane = new BorderPane();
        mainContainerPane.setCenter( mainPane );

        dateTimePicker.dateTimeProperty().addListener( ( o, a, n ) -> {
            boolean saveEnabled = true;
            if ( n == null )
            {
                infoLabel.setText( "Die Zeit muss eingetragen werden" );
            }
            else if ( n.isAfter( LocalDateTime.now() ) )
            {
                infoLabel.setText( "Die Zeit darf nicht in der Zukunft liegen" );
                saveEnabled = false;
            }
            else if ( lastStepStartTS != null && n.isBefore( lastStepStartTS ) )
            {
                infoLabel.setText( "Die Zeit darf nicht vor der letzten Stempelzeit liegen" );
                saveEnabled = false;
            }
            else
            {
                infoLabel.setText( null );
            }
            final Node okButton = getDialogPane().lookupButton( ButtonType.OK );
            if ( okButton == null )
            {
                return;
            }
            okButton.setDisable( !saveEnabled );
        } );

        dialogPane.setContent( mainContainerPane );

        setResultConverter( buttonType -> {

            if ( buttonType != ButtonType.OK )
            {
                return null;
            }
            StepTimestampResult result = new StepTimestampResult();
            result.setStepTime( dateTimePicker.getLocalDateTime() );

            return result;
        } );
    }

    private Button createButton( String buttonText, ChangeType changeType )
    {
        final Button addMinusButton = new Button( buttonText );
        addMinusButton.setFont( NumPadFonts.robotoMonoRegular( 16 ) );
        addMinusButton.getStyleClass().add( "num-pad-key" );
        addMinusButton.setUserData( changeType );
        addMinusButton.setOnAction( changeTimeActionEventEventHandler );
        addMinusButton.setMaxWidth( Double.MAX_VALUE );
        return addMinusButton;
    }

    public static StepTimestampResult showDialog( String stepName, LocalDateTime lastStepStartTS )
    {
        if ( Platform.isFxApplicationThread() )
        {
            final StepTimestampDialog dialog = new StepTimestampDialog( stepName, lastStepStartTS );
            final Optional<StepTimestampResult> result = dialog.showAndWait();
            if ( result == null || result.isPresent() == false )
            {
                return null;
            }
            return result.get();
        }
        else
        {
            FutureTask<Optional<StepTimestampResult>> future = new FutureTask<>( () -> {
                final StepTimestampDialog dialog = new StepTimestampDialog( stepName, lastStepStartTS );
                return dialog.showAndWait();
            } );
            Platform.runLater( future );
            Optional<StepTimestampResult> result = null;
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