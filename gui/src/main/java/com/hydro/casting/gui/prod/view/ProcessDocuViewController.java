package com.hydro.casting.gui.prod.view;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.control.ChargeTimeFilterToolbar;
import com.hydro.casting.gui.prod.detail.ProcessDocuDetailController;
import com.hydro.casting.server.contract.dto.*;
import com.hydro.casting.server.contract.prod.ProcessDocuView;
import com.hydro.core.gui.*;
import com.hydro.core.gui.comp.ServerTableView;
import com.hydro.core.gui.comp.TaskButton;
import com.hydro.core.gui.task.OpenDetailViewTask;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import com.hydro.core.server.contract.workplace.SearchType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.controlsfx.control.MasterDetailPane;

import java.util.List;
import java.util.Map;

@ViewDeclaration( id = ProcessDocuViewController.ID, fxmlFile = "/com/hydro/casting/gui/prod/view/ProcessDocuView.fxml", type = ViewType.MAIN )
public class ProcessDocuViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.PROD.PROCESS_DOCU.VIEW;

    public final static Image EXPAND_I = ImagesCore.EXPAND.load();
    public final static Image COLLAPSE_I = ImagesCore.COLLAPSE.load();

    public final static Image RESULT_MISSED_I = ImagesCore.MISSING.load();
    public final static Image RESULT_OK_I = ImagesCore.READY.load();
    public final static Image RESULT_OK_WITH_LIMITATIONS_I = ImagesCore.READY_ORANGE.load();
    public final static Image RESULT_FAILED_I = ImagesCore.FAILED.load();

    @Inject
    private Injector injector;
    @Inject
    private TaskManager taskManager;
    @Inject
    private BusinessManager businessManager;
    @Inject
    private NotifyManager notifyManager;

    @FXML
    private MasterDetailPane detailPane;
    @FXML
    private TaskButton showDetails;
    @FXML
    private ChargeTimeFilterToolbar filterToolBar;
    @FXML
    private OpenDetailViewTask<ProcessDocuDTO> openEditTask;
    @FXML
    private ServerTableView<ProcessDocuDTO> table;
    @FXML
    private ProcessDocuDetailController detailsController;
    @FXML
    private Button reload;

    private final BooleanProperty showDetailsSelected = new SimpleBooleanProperty( true );

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        injector.injectMembers( filterToolBar );
        filterToolBar.initialize();
        filterToolBar.bindButtonToCharge( reload );

        detailsController.activeProperty().bind( showDetailsSelected );

        table.selectedValueProperty().addListener( ( observableValue, oldValue, newValue ) -> {
            detailsController.setSelectedValue( newValue );
            if ( newValue != null )
            {
                if ( showDetailsSelected.get() )
                {
                    detailPane.setShowDetailNode( true );
                }
            }
            else
            {
                detailPane.setShowDetailNode( false );
            }
        } );
        //table.filterProperty().bind( searchBox.textProperty() );
        table.connect( injector, () -> {
            final ProcessDocuView deliveryView = businessManager.getSession( ProcessDocuView.class );

            Map<String, Object> parameters = filterToolBar.getParameters();
            SearchType searchType = filterToolBar.getSearchType();
            List<ProcessDocuDTO> entries = null;
            if ( searchType != null )
            {
                entries = deliveryView.loadBySearchType( searchType, parameters );
            }

            if ( entries == null || entries.isEmpty() )
            {
                if ( searchType == SearchType.CHARGE )
                {
                    if ( filterToolBar.isChargeFieldFilled() )
                    {
                        notifyManager.showSplashMessage( "Es wurde keine Charge '" + filterToolBar.getChargeValue() + "' gefunden" );
                    }
                }
                else
                {
                    notifyManager.showSplashMessage( "Es wurde keine Daten gefunden" );
                }
            }
            return entries;
        } );

        injector.injectMembers( openEditTask );

        detailsController.selectedTabIndex().addListener( ( observable, oldValue, newValue ) -> openEditTask.setDisabled(
                newValue == null || ( newValue.intValue() != 1 && newValue.intValue() != 3 && newValue.intValue() != 4 && newValue.intValue() != 5 ) ) );
        openEditTask.setDisabled( true );
    }

    @Override
    public void setStartObject( Object startObject )
    {
        if ( "REFRESH".equals( startObject ) )
        {
            reload( null );
            detailsController.reload();
        }
    }

    @Override
    public void activateView( View view )
    {
        super.activateView( view );
    }

    @Override
    public void deactivateView( View view )
    {
        super.deactivateView( view );
    }

    @FXML
    void showDetails( ActionEvent event )
    {
        showDetailsSelected.set( !showDetailsSelected.get() );
        ImageView imageView;
        if ( showDetailsSelected.get() )
        {
            detailPane.setShowDetailNode( true );
            imageView = new ImageView( EXPAND_I );
        }
        else
        {
            detailPane.setShowDetailNode( false );
            imageView = new ImageView( COLLAPSE_I );
        }
        imageView.setFitHeight( 21.0 );
        imageView.setFitWidth( 21.0 );
        showDetails.setGraphic( imageView );
    }

    @FXML
    public void reload( ActionEvent actionEvent )
    {
        table.loadData();
    }

    public void edit( ActionEvent actionEvent )
    {
        String viewIdToOpen;
        Class dtoClass;
        int detailTabIndex = detailsController.getSelectedTabIndex();
        switch ( detailTabIndex )
        {
        case 1:
            viewIdToOpen = SecurityCasting.PROD.DETAILS.EQUIPMENT_CONDITION.VIEW;
            dtoClass = EquipmentConditionDTO.class;
            break;
        case 3:
            viewIdToOpen = SecurityCasting.PROD.DETAILS.VISUAL_INSPECTION.VIEW;
            dtoClass = VisualInspectionDTO.class;
            break;
        case 4:
            viewIdToOpen = SecurityCasting.PROD.DETAILS.CASTING_PREPARATION.VIEW;
            dtoClass = CastingPreparationDTO.class;
            break;
        case 5:
            viewIdToOpen = SecurityCasting.PROD.DETAILS.CASTING_PREPARATION_EXAMINATION.VIEW;
            dtoClass = CastingPreparationExaminationDTO.class;
            break;
        default:
            viewIdToOpen = "";
            dtoClass = null;
        }

        openEditTask.setViewIdToOpen( viewIdToOpen );
        openEditTask.setDetailProvider( ProcessDocuView.class, dtoClass );
        taskManager.executeTask( openEditTask );
    }
}
