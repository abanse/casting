package com.hydro.casting.gui.downtime.dialog;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.downtime.DowntimeConstants;
import com.hydro.casting.gui.downtime.control.DowntimeDetailControlController;
import com.hydro.casting.gui.downtime.task.DowntimeLoaderTask;
import com.hydro.casting.server.contract.downtime.dto.DowntimeDTO;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.core.gui.ApplicationManager;
import com.hydro.core.gui.ErrorManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.TaskManager;
import com.hydro.core.gui.comp.ZoomingPane;
import com.hydro.core.gui.validation.MESValidationSupport;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.controlsfx.control.PopOver;

public class DowntimePopOver extends PopOver
{
    @Inject
    private Injector injector;

    @Inject
    private NotifyManager notifyManager;

    @Inject
    private ApplicationManager applicationManager;

    @Inject
    private DowntimeLoaderTask downtimeLoaderTask;

    @Inject
    private TaskManager taskManager;

    @Inject
    private ErrorManager errorManager;

    @FXML
    private DowntimeDetailControlController downtimeController;

    private ZoomingPane zoomingPane;
    private MESValidationSupport validationSupport = new MESValidationSupport();
    private String[] costCenters;
    private String machine;
    private DowntimeDTO suggestionDowntimeDTO;
    private DowntimeDTO editDowntimeDTO;
    private ButtonBar buttonBar;
    private Button saveButton;
    private Button closeButton;

    private static double DEFAULT_HEIGHT = 400;
    private static double DEFAULT_WIDTH = 320;

    @Inject
    public void initialize()
    {
        setTitle( "Störzeit hinzufügen" );
        setDetachable( true );
        setArrowLocation( ArrowLocation.BOTTOM_RIGHT );
        setHeaderAlwaysVisible( true );

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

        BorderPane mainBorderPane = new BorderPane();
        mainBorderPane.setCenter( downtimeDetailNode );

        saveButton = new Button( "Hinzufügen" );
        saveButton.setPrefWidth( 100 );
        closeButton = new Button( "Schließen" );
        closeButton.setCancelButton( true );
        closeButton.setPrefWidth( 100 );
        buttonBar = new ButtonBar();
        buttonBar.setPadding( new Insets( 5, 5, 5, 5 ) );
        buttonBar.getButtons().add( saveButton );
        buttonBar.getButtons().add( closeButton );

        revalidateButtonBar();
        mainBorderPane.setBottom( buttonBar );

        StackPane stackPane = new StackPane();
        VBox piBox = new VBox();
        piBox.setAlignment( Pos.CENTER );
        ProgressIndicator pi = new ProgressIndicator();
        // pi.setVisible( false );
        pi.setMaxSize( 75, 75 );
        piBox.getChildren().add( pi );
        piBox.getChildren().add( new Text( "Lade Daten" ) );
        stackPane.getChildren().add( mainBorderPane );
        stackPane.getChildren().add( piBox );

        zoomingPane = new ZoomingPane( stackPane );
        zoomingPane.setZoomFactor( applicationManager.getZoomFactor() );
        setContentNode( zoomingPane );

        // setContentNode( stackPane );
        // setContentNode( mainBorderPane );

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

        detachedProperty().addListener( ( source, oldValue, newValue ) -> {
            revalidateButtonBar();
        } );

        downtimeLoaderTask.setController( downtimeController );

        saveButton.setOnAction( ( event ) -> {
            saveButton.setDisable( true );
            try
            {
                downtimeController.createDowntime( DowntimeConstants.DOWNTIME_TYPE.MANUAL );
            }
            catch ( BusinessException e )
            {
                errorManager.handleException( "Störzeit erfassen", e );
                return;
            }

            if ( !isDetached() )
            {
                hide();
            }
            notifyManager.showSplashMessage( "Störzeit wurde erfasst." );
        } );
        closeButton.setOnAction( ( event ) -> {
            hide();
        } );

        downtimeController.getWorking().addListener( ( p, o, n ) -> {
            if ( n )
            {
                piBox.setVisible( true );
                mainBorderPane.setDisable( true );
            }
            else
            {
                piBox.setVisible( false );
                mainBorderPane.setDisable( false );
            }
        } );
    }

    public final void showPopOver( Node owner )
    {
        showPopOver( owner, true );
    }

    public final void showPopOver( Node owner, boolean timeEditAllowed )
    {
        downtimeController.setTimeEditAllowed( timeEditAllowed );
        zoomingPane.setZoomFactor( applicationManager.getZoomFactor() );
        zoomingPane.setPrefHeight( DEFAULT_HEIGHT * zoomingPane.getZoomFactor() );
        zoomingPane.setPrefWidth( DEFAULT_WIDTH * zoomingPane.getZoomFactor() );
        downtimeController.adjustComboboxFontSize( applicationManager.getZoomFactor() );
        Platform.runLater( () -> show( owner ) );
    }

    public void setDataForCostCenter( String currentCostCenter, String[] costCenters )
    {
        setDataForCostCenter( currentCostCenter, costCenters, null, null );
    }

    public void setDataForCostCenter( String currentCostCenter, String[] costCenters, DowntimeDTO downtimeSuggestion )
    {
        setDataForCostCenter( currentCostCenter, costCenters, null, downtimeSuggestion );
    }

    public void setDataForCostCenter( String currentCostCenter, String[] costCenters, String machine, DowntimeDTO suggestionDowntimeDTO )
    {
        this.costCenters = costCenters;
        this.machine = machine;
        this.suggestionDowntimeDTO = suggestionDowntimeDTO;
        downtimeController.setCostCenters( currentCostCenter, costCenters );
        downtimeController.setMachine( machine );
        setTitle( "Störzeiterfassung" );
    }

    public DowntimeDTO getEditDowntimeDTO()
    {
        return editDowntimeDTO;
    }

    public void setEditDowntimeDTO( DowntimeDTO editDowntimeDTO )
    {
        this.editDowntimeDTO = editDowntimeDTO;
    }

    public void executeDowntimeLoaderTask()
    {
        downtimeLoaderTask.setCostCenters( costCenters );
        downtimeLoaderTask.setMachine( machine );
        downtimeLoaderTask.setSuggestionDowntimeDTO( suggestionDowntimeDTO );
        if ( editDowntimeDTO != null )
        {
            downtimeLoaderTask.setEditDowntimeDTO( editDowntimeDTO );
        }
        taskManager.executeTask( downtimeLoaderTask );
    }

    private void revalidateButtonBar()
    {
        if ( isDetached() )
        {
            saveButton.setText( "Hinzufügen" );
        }
        else
        {
            saveButton.setText( "Speichern" );
        }
        buttonBar.layout();
    }

    public DowntimeDetailControlController getDowntimeController()
    {
        return downtimeController;
    }
}
