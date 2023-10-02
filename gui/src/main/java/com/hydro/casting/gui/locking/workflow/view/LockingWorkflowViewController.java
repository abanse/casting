package com.hydro.casting.gui.locking.workflow.view;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.control.CostCenterCheckComboBox;
import com.hydro.casting.gui.locking.workflow.control.LockingWorkflowDetailControlController;
import com.hydro.casting.gui.locking.workflow.control.LockingWorkflowStepsControlController;
import com.hydro.casting.gui.locking.workflow.control.OwnerCheckComboBox;
import com.hydro.casting.gui.locking.workflow.table.LockingWorkflowGroupTreeItemProvider;
import com.hydro.casting.gui.locking.workflow.task.ValidateLockingWorkflowTask;
import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;
import com.hydro.core.gui.PreferencesManager;
import com.hydro.core.gui.TaskManager;
import com.hydro.core.gui.View;
import com.hydro.core.gui.ViewType;
import com.hydro.core.gui.comp.CacheTreeTableView;
import com.hydro.core.gui.comp.SearchBox;
import com.hydro.core.gui.comp.TaskButton;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import org.controlsfx.control.MasterDetailPane;

import java.util.ArrayList;
import java.util.List;

@ViewDeclaration( id = LockingWorkflowViewController.ID, fxmlFile = "/com/hydro/casting/gui/locking/workflow/view/LockingWorkflowView.fxml", type = ViewType.MAIN )
public class LockingWorkflowViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.LOCKING.LOCKING_WORKFLOW.VIEW;

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
    private CacheTreeTableView<LockingWorkflowDTO> table;

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
    private SearchBox searchBox;

    @FXML
    private TabPane details;

    @FXML
    private LockingWorkflowDetailControlController detailController;

    @FXML
    private LockingWorkflowStepsControlController stepsController;

    @FXML
    private TitledPane standard;

    @FXML
    private OwnerCheckComboBox owner;

    @FXML
    private CostCenterCheckComboBox costCenter;

    @FXML
    private HBox summary;

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        owner.activatePreferences( preferencesManager, "/mes/lockingWorkflow", "lastOwnerSelection" );
        costCenter.activatePreferences( preferencesManager, "/mes/lockingWorkflow", "lastCostCenterSelection", new int[] { 0 } );

        final LockingWorkflowGroupTreeItemProvider lockingWorkflowGroupTreeItemProvider = new LockingWorkflowGroupTreeItemProvider();
        lockingWorkflowGroupTreeItemProvider.setOwnerModel( owner.getCheckModel() );
        lockingWorkflowGroupTreeItemProvider.setCostCenterModel( costCenter.getCheckModel() );

        owner.setOnAction( event -> table.reload() );
        costCenter.setOnAction( event -> table.reload() );

        table.setSummary( summary );
        table.connect( injector, lockingWorkflowGroupTreeItemProvider );

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

        table.filterProperty().bind( searchBox.textProperty() );

        expand.disableProperty().bind( detailPane.showDetailNodeProperty().not() );
    }

    @FXML
    void expand( ActionEvent event )
    {
        table.getSelectionModel().clearSelection();
    }

    @Override
    public void beforeShown( View view )
    {
        registerTasks( view );
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
