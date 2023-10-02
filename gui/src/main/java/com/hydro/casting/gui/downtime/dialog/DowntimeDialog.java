package com.hydro.casting.gui.downtime.dialog;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.downtime.DowntimeConstants;
import com.hydro.casting.gui.downtime.control.DowntimeDetailControlController;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.core.gui.ErrorManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.validation.MESValidationSupport;
import impl.org.controlsfx.skin.DecorationPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DowntimeDialog extends DecorationPane
{
    @Inject
    private Injector injector;

    @Inject
    private NotifyManager notifyManager;

    @Inject
    private ErrorManager errorManager;

    @FXML
    private DowntimeDetailControlController downtimeController;

    private MESValidationSupport validationSupport = new MESValidationSupport();
    private ButtonBar buttonBar;
    private Button saveButton;
    private Button closeButton;

    @Inject
    public void initialize()
    {
        BorderPane borderPane = new BorderPane();
        Node downtimeDetailNode = null;
        try
        {
            FXMLLoader loader = new FXMLLoader( getClass().getResource( "/com/hydro/casting/gui/downtime/control/DowntimeDetailControl.fxml" ) );
            loader.setControllerFactory( injector::getInstance );
            downtimeDetailNode = loader.load();
            downtimeController = loader.getController();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        borderPane.setCenter( downtimeDetailNode );

        saveButton = new Button( "Speichern" );
        saveButton.setPrefWidth( 100 );
        closeButton = new Button( "Schließen" );
        closeButton.setCancelButton( true );
        closeButton.setPrefWidth( 100 );
        buttonBar = new ButtonBar();
        buttonBar.setPadding( new Insets( 5, 5, 5, 5 ) );
        buttonBar.getButtons().add( saveButton );
        buttonBar.getButtons().add( closeButton );

        borderPane.setBottom( buttonBar );

        downtimeController.setValidationSupport( validationSupport );

        validationSupport.validationResultProperty().addListener( ( p, o, n ) -> {
            if ( n.getErrors().isEmpty() )
            {
                saveButton.setDisable( false );
            }
            else
            {
                saveButton.setDisable( true );
            }
        } );
        saveButton.setOnAction( event -> {
            saveButton.setDisable( true );
            try
            {
                downtimeController.createDowntime( DowntimeConstants.DOWNTIME_TYPE.MANUAL );
            }
            catch ( BusinessException e )
            {
                errorManager.handleException( "Fehler beim Speichern", e );
                saveButton.setDisable( false );
                return;
            }
            Stage stage = (Stage) getScene().getWindow();
            stage.close();
        } );
        closeButton.setOnAction( event -> {
            Stage stage = (Stage) getScene().getWindow();
            stage.close();
        } );
        this.getChildren().add( borderPane );
    }

    public DowntimeDetailControlController getDowntimeController()
    {
        return downtimeController;
    }

//    public Button getSaveButton()
//    {
//        return saveButton;
//    }

//    public Button getCloseButton()
//    {
//        return closeButton;
//    }

    //    private void handleInputError( int inputError )
    //    {
    //        switch ( inputError )
    //        {
    //        case DowntimeConstants.INPUT_ERRORS.TIME_OVERLAP_ERROR:
    //            notifyManager.showErrorMessage( "Fehler beim Erfassen der Störzeit", "Die Störzeiten überschneiden sich mit einer anderen Störzeit. Bitte überprüfen Sie die Eingaben.\n\nInfo: Die Störzeit darf nicht zur gleichen Zeit starten/enden wie eine andere Störzeit." );
    //            break;
    //
    //        case DowntimeConstants.INPUT_ERRORS.BUSINESS_EXCEPTION:
    //            notifyManager.showErrorMessage( "Fehler beim Erfassen der Störzeit", "Fehler beim Laden der Daten aus dem Cache" );
    //            break;
    //
    ////        case DowntimeConstants.INPUT_ERRORS.SHIFT_BORDER_VIOLATION:
    ////            notifyManager.showErrorMessage( "Fehler beim Erfassen der Störzeit", "Die Störzeit darf maximal bis zum Schichtende andauern" );
    ////            break;
    //
    //        default:
    //            notifyManager.showErrorMessage( "Fehler beim Erfassen der Störzeit", "Es ist ein unbekannter Fehler aufgetreten" );
    //            break;
    //        }
    //    }
}
