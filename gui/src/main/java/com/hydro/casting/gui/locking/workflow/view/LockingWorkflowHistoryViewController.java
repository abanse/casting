package com.hydro.casting.gui.locking.workflow.view;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.control.CostCenterCheckComboBox;
import com.hydro.casting.gui.locking.workflow.control.LockingWorkflowDetailControlController;
import com.hydro.casting.gui.locking.workflow.control.LockingWorkflowStepsControlController;
import com.hydro.casting.gui.locking.workflow.table.LockingWorkflowGroupTreeItemProvider;
import com.hydro.casting.gui.locking.workflow.task.ValidateLockingWorkflowTask;
import com.hydro.casting.server.contract.locking.workflow.LockingWorkflowView;
import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;
import com.hydro.core.common.util.StringTools;
import com.hydro.core.gui.*;
import com.hydro.core.gui.comp.*;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.controlsfx.control.MasterDetailPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ViewDeclaration( id = LockingWorkflowHistoryViewController.ID, fxmlFile = "/com/hydro/casting/gui/locking/workflow/view/LockingWorkflowHistoryView.fxml", type = ViewType.MAIN )
public class LockingWorkflowHistoryViewController extends ViewControllerBase
{
    private final static Logger log = LoggerFactory.getLogger( LockingWorkflowHistoryViewController.class );

    public final static String ID = SecurityCasting.LOCKING.LOCKING_WORKFLOW_HISTORY.VIEW;

    @Inject
    private BusinessManager businessManager;

    @Inject
    private NotifyManager notifyManager;

    @Inject
    private Injector injector;

    @Inject
    private PreferencesManager preferencesManager;

    @Inject
    private ValidateLockingWorkflowTask validateLockingWorkflowTask;

    @Inject
    private TaskManager taskManager;

    @FXML
    @Inject
    private ServerTreeTableView<LockingWorkflowDTO> table;

    @FXML
    private MasterDetailPane detailPane;

    @FXML
    private MasterDetailPane functionPane;

    @FXML
    private Label functionTitle;

    @FXML
    private Label tabTitle;

    @FXML
    private Button expand;

    @FXML
    private TabPane details;

    @FXML
    private LockingWorkflowDetailControlController detailController;

    @FXML
    private LockingWorkflowStepsControlController stepsController;

    @FXML
    private TitledPane standard;

    // @FXML
    // private OwnerCheckComboBox owner;

    @FXML
    private CostCenterCheckComboBox costCenter;

    @FXML
    private HBox summary;

    @FXML
    private Button reload;

    @FXML
    private ComboBox<String> filterSelection;

    @FXML
    private ToolBar filterToolBar;

    @FXML
    private HBox durationFilter;

    @FXML
    private HBox productionOrderFilter;

    @FXML
    private HBox customerNumberFilter;

    @FXML
    private DateTimePicker from;

    @FXML
    private DateTimePicker to;

    @FXML
    private SearchBox productionOrder;

    @FXML
    private SearchBox customerNumber;

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        from.clear();
        to.clear();

        reload.setDefaultButton( true );

        filterSelection.getItems().addAll( "Zeitraum", "Fertigungsauftrag", "Kundenauftrag" );
        filterSelection.getSelectionModel().selectedIndexProperty().addListener( ( p, o, n ) -> {
            preferencesManager.setIntValue( PreferencesManager.SCOPE_USER, "/mes/lockingWorkflow", "lastFilterSelectionIndex", n.intValue() );
            if ( n.intValue() == 0 )
            {
                if ( filterToolBar.getItems().contains( durationFilter ) == false )
                {
                    filterToolBar.getItems().add( 2, durationFilter );
                    from.requestFocus();
                }
                // if ( filterToolBar.getItems().contains( owner ) == false )
                // {
                // filterToolBar.getItems().add( owner );
                // }
                if ( filterToolBar.getItems().contains( costCenter ) == false )
                {
                    filterToolBar.getItems().add( costCenter );
                }
                if ( filterToolBar.getItems().contains( productionOrderFilter ) )
                {
                    filterToolBar.getItems().remove( productionOrderFilter );
                }
                if ( filterToolBar.getItems().contains( customerNumberFilter ) )
                {
                    filterToolBar.getItems().remove( customerNumberFilter );
                }
            }
            else if ( n.intValue() == 1 )
            {
                if ( filterToolBar.getItems().contains( durationFilter ) )
                {
                    filterToolBar.getItems().remove( durationFilter );
                }
                if ( filterToolBar.getItems().contains( productionOrderFilter ) == false )
                {
                    filterToolBar.getItems().add( 2, productionOrderFilter );
                    productionOrder.requestFocus();
                }
                if ( filterToolBar.getItems().contains( customerNumberFilter ) )
                {
                    filterToolBar.getItems().remove( customerNumberFilter );
                }
                // if ( filterToolBar.getItems().contains( owner ) )
                // {
                // filterToolBar.getItems().remove( owner );
                // }
                if ( filterToolBar.getItems().contains( costCenter ) )
                {
                    filterToolBar.getItems().remove( costCenter );
                }
            }
            else
            {
                if ( filterToolBar.getItems().contains( durationFilter ) )
                {
                    filterToolBar.getItems().remove( durationFilter );
                }
                if ( filterToolBar.getItems().contains( productionOrderFilter ) )
                {
                    filterToolBar.getItems().remove( productionOrderFilter );
                }
                if ( filterToolBar.getItems().contains( customerNumberFilter ) == false )
                {
                    filterToolBar.getItems().add( 2, customerNumberFilter );
                    customerNumber.requestFocus();
                }
                // if ( filterToolBar.getItems().contains( owner ) )
                // {
                // filterToolBar.getItems().remove( owner );
                // }
                if ( filterToolBar.getItems().contains( costCenter ) )
                {
                    filterToolBar.getItems().remove( costCenter );
                }
            }
        } );
        final int filterSelectionIndex = preferencesManager.getIntValue( PreferencesManager.SCOPE_USER, "/mes/lockingWorkflow", "lastFilterSelectionIndex" );
        filterSelection.getSelectionModel().clearAndSelect( filterSelectionIndex );

        // owner.activatePreferences( preferencesManager, "/mes/lockingWorkflowHistory", "lastOwnerSelection"
        // );
        costCenter.activatePreferences( preferencesManager, "/mes/lockingWorkflowHistory", "lastCostCenterSelection", new int[] { 0 } );

        LockingWorkflowGroupTreeItemProvider lockingWorkflowGroupTreeItemProvider = new LockingWorkflowGroupTreeItemProvider();

        // owner.setOnAction( event -> {
        // table.reload();
        // Platform.runLater( () -> reload.requestFocus() );
        // } );
        costCenter.setOnAction( event -> {
            table.reload();
            Platform.runLater( () -> reload.requestFocus() );
        } );

        table.connect( injector, new ServerDTOListProvider<LockingWorkflowDTO>()
        {
            @Override
            public List<LockingWorkflowDTO> getDTOList() throws Exception
            {
                LockingWorkflowView lockingWorkflowView = businessManager.getSession( LockingWorkflowView.class );

                Map<String, Object> parameters = new HashMap<>();
                String filter = null;
                if ( filterSelection.getSelectionModel().getSelectedIndex() == 0 )
                {
                    // get from
                    LocalDateTime fromLDT = from.getLocalDateTime();
                    if ( fromLDT == null )
                    {
                        notifyManager.showInfoMessage( "Suchfehler", "Die 'Von' Zeit ist nicht besetzt" );
                        return null;
                    }
                    LocalDateTime toLDT = to.getLocalDateTime();
                    filter = " AND LR.LOCK_DATE >= :fromTime";
                    parameters.put( "fromTime", fromLDT );
                    if ( toLDT != null )
                    {
                        filter = filter + " AND LR.LOCK_DATE < :toLDT";
                        parameters.put( "toLDT", toLDT );
                    }
                    // if ( owner.isFiltered() )
                    // {
                    // filter = filter + " AND " + owner.createFilter();
                    // }
                    if ( costCenter.isFiltered() )
                    {
                        filter = filter + " AND " + costCenter.createFilter( "LR.KST" );
                    }
                }
                else if ( filterSelection.getSelectionModel().getSelectedIndex() == 1 && StringTools.isFilled( productionOrder.getText() ) )
                {
                    filter = " AND LR.LOT = :productionOrder";
                    parameters.put( "productionOrder", productionOrder.getText() );
                }
                else if ( filterSelection.getSelectionModel().getSelectedIndex() == 2 && StringTools.isFilled( customerNumber.getText() ) )
                {
                    filter = " AND CS.CUSTOMER_ORDER_NR LIKE :customerNumber ";
                    parameters.put( "customerNumber", customerNumber.getText() + "%" );
                }
                // filter = filter + " AND LR.MATERIAL_STATUS <> 'B'";

                List<LockingWorkflowDTO> entries = null;
                if ( filter != null )
                {
                    entries = lockingWorkflowView.loadLockingWorkflows( filter, parameters );
                }

                if ( entries == null || entries.isEmpty() )
                {
                    notifyManager.showSplashMessage( "Es wurde keine Daten gefunden" );
                }

                return entries;
            }
        } );
        table.setSummary( summary );
        table.setReloadButton( reload );
        table.setGroupTreeItemProvider( lockingWorkflowGroupTreeItemProvider );

        table.selectedValueProperty().addListener( ( observableValue, oldValue, newValue ) -> {

            LockingWorkflowDTO selectedValue = newValue;

            validateLockingWorkflowTask.setData( selectedValue );

            taskManager.executeTask( validateLockingWorkflowTask );

            if ( selectedValue != null )
            {
                detailPane.setShowDetailNode( true );
                functionPane.setShowDetailNode( true );

                final String title;
                if ( selectedValue.getParent() != null )
                {
                    title = selectedValue.getParent().getWorkStepId();
                }
                else
                {
                    title = selectedValue.getWorkStepId();
                }
                functionTitle.setText( title );
                tabTitle.setText( title );
                detailController.setLockingWorkflow( selectedValue );
                stepsController.setLockingWorkflow( selectedValue );
            }
            else
            {
                detailPane.setShowDetailNode( false );
                functionPane.setShowDetailNode( false );
                detailController.setLockingWorkflow( null );
                stepsController.setLockingWorkflow( null );
            }
        } );

        expand.disableProperty().bind( detailPane.showDetailNodeProperty().not() );
    }

    @FXML
    void expand( ActionEvent event )
    {
        table.getSelectionModel().clearSelection();
    }

    @FXML
    void reload( ActionEvent event )
    {
        table.reload();
    }

    @Override
    public void beforeShown( View view )
    {
        registerTasks( view );
    }

    @Override
    public void reloadView( Object source, View view )
    {
        log.info( "reload from action " + source );
        table.reload();
    }

    private void registerTasks( View view )
    {
        List<TaskButton> taskButtons = new ArrayList<>();
        if ( view instanceof Node )
        {
            recursiveSearchTasks( taskButtons, (Node) view );
        }
        taskButtons.stream().forEach( taskButton -> {
            if ( taskButton.getTask() != null )
            {
                injector.injectMembers( taskButton.getTask() );
                validateLockingWorkflowTask.addTask( taskButton.getTask() );
                taskButton.setOnAction( event -> {
                    taskManager.executeTask( taskButton.getTask() );
                } );
            }
        } );
    }

    private void recursiveSearchTasks( List<TaskButton> taskButtons, Node node )
    {
        if ( node instanceof TaskButton )
        {
            taskButtons.add( (TaskButton) node );
        }
        if ( node instanceof Parent )
        {
            Parent parent = (Parent) node;
            List<Node> childs = parent.getChildrenUnmodifiable();
            for ( Node child : childs )
            {
                recursiveSearchTasks( taskButtons, child );
            }
        }
        if ( node instanceof MasterDetailPane )
        {
            MasterDetailPane masterDetailPane = (MasterDetailPane) node;
            recursiveSearchTasks( taskButtons, masterDetailPane.getMasterNode() );
            recursiveSearchTasks( taskButtons, masterDetailPane.getDetailNode() );
        }
    }
}
