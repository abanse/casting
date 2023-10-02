package com.hydro.casting.gui.downtime.dialog;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.Casting;
import com.hydro.casting.common.downtime.DowntimeConstants;
import com.hydro.casting.gui.downtime.control.DowntimeDetailControlController;
import com.hydro.casting.gui.downtime.task.DowntimeLoaderTask;
import com.hydro.casting.gui.prod.control.TimeManagementListView;
import com.hydro.casting.server.contract.downtime.dto.DowntimeDTO;
import com.hydro.casting.server.contract.downtime.dto.DowntimeRequestDTO;
import com.hydro.casting.server.contract.dto.TimeManagementViolationDTO;
import com.hydro.core.common.cache.ClientCache;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.core.gui.*;
import com.hydro.core.gui.comp.ZoomingPane;
import com.hydro.core.gui.validation.MESValidationSupport;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.PopOver;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ResolveDowntimePopOver extends PopOver
{
    @Inject
    private Injector injector;

    @Inject
    private NotifyManager notifyManager;
    @Inject
    private CacheManager cacheManager;

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
    private Button saveButton;
    private Button closeButton;

    private TimeManagementListView downtimeRequestListView;

    private static double DEFAULT_HEIGHT = 540;
    private static double DEFAULT_WIDTH = 520;

    @Inject
    public void initialize()
    {
        setTitle( "Nacherfassung Störzeiten" );
        setDetachable( true );
        setArrowLocation( ArrowLocation.BOTTOM_RIGHT );
        setHeaderAlwaysVisible( true );

        Node downtimeDetailNode = null;
        try
        {
            FXMLLoader loader = new FXMLLoader( getClass().getResource( "/com/hydro/casting/gui/downtime/control/DowntimeResolveDetailControl.fxml" ) );
            loader.setControllerFactory( injector::getInstance );
            downtimeDetailNode = loader.load();
            downtimeController = loader.getController();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        BorderPane mainBorderPane = new BorderPane();

        downtimeRequestListView = new TimeManagementListView();

        downtimeRequestListView.setPrefWidth( 200 );

        final BorderPane downtimeBorderPane = new BorderPane();

        downtimeRequestListView.getSelectionModel().selectedItemProperty().addListener( ( observable, oldValue, newValue ) -> {
            if ( oldValue != null )
            {
                oldValue.setChecked( true );
            }
            if ( newValue != null )
            {
                newValue.setChecked( false );
                final DowntimeRequestDTO requestDTO = newValue.getDowntimeRequest();
                if ( requestDTO != null )
                {
                    final DowntimeDTO suggestionDowntimeDTO = new DowntimeDTO();
                    suggestionDowntimeDTO.setFromTS( requestDTO.getFromTS() );
                    suggestionDowntimeDTO.setEndTS( requestDTO.getEndTS() );
                    suggestionDowntimeDTO.setDescription( requestDTO.getDescription() );
                    executeDowntimeLoaderTask( suggestionDowntimeDTO );
                }
                downtimeBorderPane.setDisable( false );
            }
            else
            {
                downtimeBorderPane.setDisable( true );
            }
        } );
        downtimeBorderPane.setDisable( true );

        mainBorderPane.setLeft( downtimeRequestListView );

        downtimeBorderPane.setTop( new Separator( Orientation.HORIZONTAL ) );
        downtimeBorderPane.setCenter( downtimeDetailNode );

        final ButtonBar downtimeButtonBar = new ButtonBar();
        saveButton = new Button( "Speichern" );
        saveButton.setPrefWidth( 100 );
        downtimeButtonBar.setPadding( new Insets( 5, 5, 5, 5 ) );
        downtimeButtonBar.getButtons().add( saveButton );
        downtimeBorderPane.setBottom( downtimeButtonBar );

        mainBorderPane.setCenter( downtimeBorderPane );

        closeButton = new Button( "Schließen" );
        closeButton.setCancelButton( true );
        closeButton.setPrefWidth( 100 );
        final ButtonBar buttonBar = new ButtonBar();
        buttonBar.setPadding( new Insets( 5, 5, 5, 5 ) );
        buttonBar.getButtons().add( closeButton );

        final BorderPane bottomBorderPane = new BorderPane();
        bottomBorderPane.setTop( new Separator( Orientation.HORIZONTAL ) );
        bottomBorderPane.setCenter( buttonBar );

        mainBorderPane.setBottom( bottomBorderPane );

        zoomingPane = new ZoomingPane( mainBorderPane );
        zoomingPane.setZoomFactor( applicationManager.getZoomFactor() );
        setContentNode( zoomingPane );

        // setContentNode( stackPane );
        // setContentNode( mainBorderPane );

        downtimeController.setValidationSupport( validationSupport );

        validationSupport.validationResultProperty().addListener( ( p, o, n ) -> {
            saveButton.setDisable( !n.getErrors().isEmpty() );
        } );

        downtimeLoaderTask.setController( downtimeController );

        saveButton.setOnAction( ( event ) -> {
            saveButton.setDisable( true );

            DowntimeRequestDTO downtimeRequestDTO = null;
            TimeManagementViolationDTO managementViolationDTO = downtimeRequestListView.getSelectionModel().getSelectedItem();
            if ( managementViolationDTO != null )
            {
                downtimeRequestDTO = managementViolationDTO.getDowntimeRequest();
            }

            try
            {
                downtimeController.createDowntime( downtimeRequestDTO, downtimeController.getSplitTime(), DowntimeConstants.DOWNTIME_TYPE.AUTO );
            }
            catch ( BusinessException e )
            {
                errorManager.handleException( "Störzeit erfassen", e );
                return;
            }

            loadDowntimeRequests();

            if ( downtimeRequestListView.getItems().isEmpty() )
            {
                notifyManager.showSplashMessage( "Störzeit wurde erfasst." );
                hide();
            }
            else
            {
                notifyManager.showSplashMessage( downtimeBorderPane, "Störzeit wurde erfasst." );
            }
        } );
        closeButton.setOnAction( ( event ) -> hide() );
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
        this.costCenters = costCenters;
        downtimeController.setCostCenters( currentCostCenter, costCenters );
        downtimeController.setMachine( null );
    }

    private void executeDowntimeLoaderTask( DowntimeDTO suggestionDowntimeDTO )
    {
        downtimeLoaderTask.setCostCenters( costCenters );
        downtimeLoaderTask.setMachine( null );
        downtimeLoaderTask.setSuggestionDowntimeDTO( suggestionDowntimeDTO );
        taskManager.executeTask( downtimeLoaderTask );
    }

    public void loadDowntimeRequests()
    {
        downtimeRequestListView.getItems().clear();
        final ClientCache<List<Long>> sequenceCache = cacheManager.getCache( Casting.CACHE.PROD_CACHE_NAME );
        final List<Long> sequence = sequenceCache.get( Casting.CACHE.DOWNTIME_REQUEST_KEY );
        if ( sequence == null )
        {
            return;
        }
        final ClientCache<DowntimeRequestDTO> dataCache = cacheManager.getCache( Casting.CACHE.PROD_CACHE_NAME );
        final Map<Long, DowntimeRequestDTO> cacheData = dataCache.getAll( Casting.CACHE.DOWNTIME_REQUEST_KEY + "/data/", sequence );
        final List<DowntimeRequestDTO> currentEntries = cacheData.values().stream().filter( downtimeRequest -> {
            for ( String costCenter : costCenters )
            {
                if ( Objects.equals( costCenter, downtimeRequest.getCostCenter() ) )
                {
                    return true;
                }
            }
            return false;
        } ).sorted( Comparator.comparing( DowntimeRequestDTO::getFromTS ) ).collect( Collectors.toList() );
        for ( DowntimeRequestDTO currentEntry : currentEntries )
        {
            TimeManagementViolationDTO timeManagementViolationDTO = new TimeManagementViolationDTO();
            timeManagementViolationDTO.setName( currentEntry.getPhase() );
            timeManagementViolationDTO.setCharge( currentEntry.getDescription().substring( 0, 5 ) );
            timeManagementViolationDTO.setPlannedEnd( currentEntry.getEndTS() );
            timeManagementViolationDTO.setType( "Ende" );
            timeManagementViolationDTO.setChecked( true );
            timeManagementViolationDTO.setDowntimeRequest( currentEntry );
            downtimeRequestListView.getItems().add( timeManagementViolationDTO );
        }
        downtimeRequestListView.getSelectionModel().clearAndSelect( 0 );
    }
}
