package com.hydro.casting.gui.downtime.view;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.control.CostCenterCheckComboBox;
import com.hydro.casting.gui.downtime.data.DowntimeDataProvider;
import com.hydro.casting.gui.downtime.table.DowntimeGroupTreeItemProvider;
import com.hydro.casting.gui.downtime.task.DowntimeAddTask;
import com.hydro.casting.gui.downtime.task.DowntimeAdditionalDescriptionTask;
import com.hydro.casting.gui.downtime.task.DowntimeDeleteTask;
import com.hydro.casting.gui.downtime.task.DowntimeEditTask;
import com.hydro.casting.gui.util.TaskButtonUtil;
import com.hydro.casting.server.contract.downtime.dto.DowntimeDTO;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.*;
import com.hydro.core.gui.comp.DateTimePicker;
import com.hydro.core.gui.comp.ServerTreeTableView;
import com.hydro.core.gui.comp.TaskButton;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.controlsfx.control.MasterDetailPane;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@ViewDeclaration( id = DowntimeHistoryViewController.ID, fxmlFile = "/com/hydro/casting/gui/downtime/view/DowntimeHistoryView.fxml", type = ViewType.MAIN )
public class DowntimeHistoryViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.DOWNTIME.HISTORY.VIEW;

    private final static String DOWNTIME_HISTORY_PREFERENCES_PATH = "/mes/downtime/history";
    private final static String LAST_FILTER_SELECTION_PREFERENCE_KEY = "lastFilterSelectionIndex";
    private final static String LAST_COST_CENTER_SELECTION_PREFERENCE_KEY = "lastCostCenterSelection";

    @Inject
    private Injector injector;

    @Inject
    private PreferencesManager preferencesManager;

    @Inject
    private TaskManager taskManager;

    @Inject
    private SecurityManager securityManager;

    @Inject
    private NotifyManager notifyManager;

    @Inject
    private DowntimeDataProvider dataProvider;

    @FXML
    private ServerTreeTableView<DowntimeDTO> downtimeHistoryTable;

    @FXML
    private MasterDetailPane operationPane;

    @FXML
    private Label operationAreaTitle;

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
    private DateTimePicker from;

    @FXML
    private DateTimePicker to;

    @FXML
    private HBox shiftFilter;

    @FXML
    private DatePicker date;

    @FXML
    private TaskButton editDowntimeButton;

    @FXML
    private TaskButton deleteDowntimeButton;

    @FXML
    private TaskButton addCommentButton;

    @FXML
    private DowntimeAddTask downtimeAddTask;

    @FXML
    private DowntimeEditTask downtimeEditTask;

    @FXML
    private DowntimeDeleteTask downtimeDeleteTask;

    @FXML
    private DowntimeAdditionalDescriptionTask downtimeAdditionalDescriptionTask;

    private final BooleanProperty buttonsUnavailableProperty = new SimpleBooleanProperty( true );

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        injector.injectMembers( downtimeAddTask );
        injector.injectMembers( downtimeEditTask );
        injector.injectMembers( downtimeDeleteTask );
        injector.injectMembers( downtimeAdditionalDescriptionTask );
        downtimeAddTask.setHistoryController( this );
        downtimeEditTask.setHistoryController( this );
        downtimeDeleteTask.setHistoryController( this );
        downtimeAdditionalDescriptionTask.setHistoryController( this );
        from.setLocalDateTime( LocalDateTime.of( LocalDate.now().minusDays( 2 ), LocalTime.MIN ) );
        to.clear();
        date.setValue( LocalDate.now() );
        reload.setDefaultButton( true );

        DowntimeGroupTreeItemProvider downtimeGroupTreeItemProvider = new DowntimeGroupTreeItemProvider();
        downtimeGroupTreeItemProvider.setCostCenterModel( costCenter.getCheckModel() );
        downtimeHistoryTable.setSummary( summary );
        downtimeHistoryTable.setReloadButton( reload );
        downtimeHistoryTable.setGroupTreeItemProvider( downtimeGroupTreeItemProvider );

        // Set listeners / connect table to data source
        filterSelection.getItems().addAll( "Zeitraum", "Schichtübersicht" );
        filterSelection.getSelectionModel().selectedIndexProperty().addListener( ( p, o, n ) -> {
            preferencesManager.setIntValue( PreferencesManager.SCOPE_USER, DOWNTIME_HISTORY_PREFERENCES_PATH, LAST_FILTER_SELECTION_PREFERENCE_KEY, n.intValue() );

            // duration filter
            if ( n.intValue() == 0 )
            {
                changeToolbarFilter( shiftFilter, durationFilter );
            }
            // shift filter
            else if ( n.intValue() == 1 )
            {
                changeToolbarFilter( durationFilter, shiftFilter );
            }
        } );

        costCenter.setOnAction( event -> {
            downtimeHistoryTable.reload();
            Platform.runLater( () -> reload.requestFocus() );
        } );

        downtimeHistoryTable.connect( injector, () -> {
            List<DowntimeDTO> data = new ArrayList<>();
            if ( filterSelection.getSelectionModel().getSelectedIndex() == 0 )
            {
                LocalDateTime fromLDT = from.getLocalDateTime();
                if ( fromLDT == null )
                {
                    notifyManager.showInfoMessage( "Suchfehler", "Die 'Von' Zeit ist nicht besetzt" );
                    return null;
                }
                LocalDateTime toLDT = to.getLocalDateTime();
                if ( toLDT == null )
                {
                    toLDT = LocalDateTime.now();
                }
                data = dataProvider.getDataForInterval( fromLDT, toLDT );
            }
            else if ( filterSelection.getSelectionModel().getSelectedIndex() == 1 )
            {
                LocalDate shiftDate = date.getValue();
                if ( shiftDate == null )
                {
                    notifyManager.showInfoMessage( "Suchfehler", "Das Datum ist nicht besetzt" );
                    return null;
                }
                // filter valid costCenters to get shift data
                List<String> allowedCostCenters = costCenter.getAllowedCostCenters();
                Predicate<String> ccPredicate = cc -> cc.matches( "alle(.*)" );
                allowedCostCenters.removeIf( ccPredicate );
                data = dataProvider.getShiftData( shiftDate, allowedCostCenters );
            }

            return data;
        } );

        downtimeHistoryTable.selectedValueProperty().addListener( ( observableValue, oldValue, newValue ) -> {

            if ( newValue != null )
            {
                operationPane.setShowDetailNode( true );
                operationAreaTitle.setText( "" );
            }
            else
            {
                operationPane.setShowDetailNode( false );
            }

            validateTasks();
        } );

        downtimeHistoryTable.getSelectionModel().selectedItemProperty().addListener( ( observableValue, oldValue, newValue ) -> {
            // tree item can be a leaf, a header, or an empty header item (which is equal to a leaf item with the table root as its parent) in case there are no downtimes for a cost center yet
            buttonsUnavailableProperty.setValue( newValue != null && ( !newValue.isLeaf() || newValue.getParent() == downtimeHistoryTable.getRoot() ) );
        } );

        // Set bindings
        editDowntimeButton.disableProperty().bind( buttonsUnavailableProperty );
        deleteDowntimeButton.disableProperty().bind( buttonsUnavailableProperty );
        addCommentButton.disableProperty().bind( buttonsUnavailableProperty );

        // Load preferences / settings from last session
        final int filterSelectionIndex = preferencesManager.getIntValue( PreferencesManager.SCOPE_USER, DOWNTIME_HISTORY_PREFERENCES_PATH, LAST_FILTER_SELECTION_PREFERENCE_KEY );
        filterSelection.getSelectionModel().clearAndSelect( filterSelectionIndex );
        costCenter.activatePreferences( preferencesManager, DOWNTIME_HISTORY_PREFERENCES_PATH, LAST_COST_CENTER_SELECTION_PREFERENCE_KEY, new int[] { 0 } );

        downtimeHistoryTable.setRowFactory( tv -> new TreeTableRow<>()
        {
            private final Tooltip tooltip = new Tooltip();

            @Override
            public void updateItem( DowntimeDTO downtimeDTO, boolean empty )
            {
                super.updateItem( downtimeDTO, empty );
                if ( downtimeDTO == null )
                {
                    setTooltip( null );
                }
                else
                {
                    tooltip.setText( downtimeDTO.getDescription() );
                    setTooltip( tooltip );
                }
            }
        } );

        reload( null );
    }

    @FXML
    void expand( ActionEvent event )
    {
        downtimeHistoryTable.getSelectionModel().clearSelection();
    }

    @FXML
    public void reload( ActionEvent event )
    {
        downtimeHistoryTable.reload();
    }

    @Override
    public void beforeShown( View view )
    {
        TaskButtonUtil.registerTasks( view, injector, taskManager, null );
    }

    private void validateTasks()
    {
        downtimeAddTask.setDisabled( false );
        downtimeAddTask.setRemark( null );
        downtimeEditTask.setDisabled( false );
        downtimeEditTask.setRemark( null );
        downtimeDeleteTask.setDisabled( false );
        downtimeDeleteTask.setRemark( null );
        downtimeAdditionalDescriptionTask.setDisabled( false );
        downtimeAdditionalDescriptionTask.setRemark( null );
        if ( downtimeHistoryTable.getSelectedValue() != null )
        {
            downtimeAddTask.setLocked( !securityManager.hasRole( downtimeAddTask.getSecurityId( Collections.singletonMap( "costCenter", downtimeHistoryTable.getSelectedValue().getCostCenter() ) ) ) );
            downtimeEditTask.setLocked(
                    !securityManager.hasRole( downtimeEditTask.getSecurityId( Collections.singletonMap( "costCenter", downtimeHistoryTable.getSelectedValue().getCostCenter() ) ) ) );
            downtimeDeleteTask.setLocked(
                    !securityManager.hasRole( downtimeDeleteTask.getSecurityId( Collections.singletonMap( "costCenter", downtimeHistoryTable.getSelectedValue().getCostCenter() ) ) ) );
            downtimeAdditionalDescriptionTask.setLocked(
                    !securityManager.hasRole( downtimeAdditionalDescriptionTask.getSecurityId( Collections.singletonMap( "costCenter", downtimeHistoryTable.getSelectedValue().getCostCenter() ) ) ) );
        }
    }

    private void changeToolbarFilter( HBox oldFilter, HBox newFilter )
    {
        boolean isShiftFilter = newFilter == shiftFilter;
        // Add correct filter UI elements to the toolbar
        filterToolBar.getItems().remove( oldFilter );
        if ( !filterToolBar.getItems().contains( newFilter ) )
        {
            filterToolBar.getItems().add( 1, newFilter );
            if ( isShiftFilter )
            {
                date.requestFocus();
            }
            else
            {
                from.requestFocus();
            }
        }

        if ( !filterToolBar.getItems().contains( costCenter ) )
        {
            filterToolBar.getItems().add( costCenter );
        }

        DowntimeGroupTreeItemProvider downtimeGroupTreeItemProvider = (DowntimeGroupTreeItemProvider) downtimeHistoryTable.getGroupTreeItemProvider();
        if ( downtimeGroupTreeItemProvider != null )
        {
            downtimeGroupTreeItemProvider.setShiftViewEnabled( isShiftFilter );
        }
    }
}