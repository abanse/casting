package com.hydro.casting.gui.main.view;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.main.detail.ProductionOrderDetailController;
import com.hydro.casting.gui.main.task.CreateChargeTask;
import com.hydro.casting.server.contract.dto.ProductionOrderDTO;
import com.hydro.casting.server.contract.main.ProductionOrderView;
import com.hydro.core.common.util.StringTools;
import com.hydro.core.common.util.TextFieldUtil;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.*;
import com.hydro.core.gui.comp.DateTimePicker;
import com.hydro.core.gui.comp.ServerTableView;
import com.hydro.core.gui.comp.TaskButton;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import com.hydro.core.server.contract.workplace.SearchType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.controlsfx.control.MasterDetailPane;
import org.controlsfx.control.textfield.CustomTextField;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ViewDeclaration( id = ProductionOrderViewController.ID, fxmlFile = "/com/hydro/casting/gui/main/view/ProductionOrderView.fxml", type = ViewType.MAIN )
public class ProductionOrderViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.MAIN.PRODUCTION_ORDER.VIEW;

    public final static Image EXPAND_I = ImagesCore.EXPAND.load();
    public final static Image COLLAPSE_I = ImagesCore.COLLAPSE.load();

    @Inject
    private Injector injector;
    @Inject
    private PreferencesManager preferencesManager;
    @Inject
    private TaskManager taskManager;
    @Inject
    private SecurityManager securityManager;
    @Inject
    private BusinessManager businessManager;
    @Inject
    private NotifyManager notifyManager;

    @FXML
    private MasterDetailPane detailPane;
    @FXML
    private TaskButton showDetails;
    @FXML
    private DateTimePicker from;
    @FXML
    private DateTimePicker to;
    @FXML
    private CustomTextField charge;
    @FXML
    private CustomTextField pa;
    @FXML
    private ComboBox<String> filterSelection;
    @FXML
    private ToolBar filterToolBar;
    @FXML
    private HBox durationFilter;
    @FXML
    private HBox chargeDataFilter;
    @FXML
    private HBox paDataFilter;
    @FXML
    private ServerTableView<ProductionOrderDTO> table;
    @FXML
    private ProductionOrderDetailController detailsController;
    @FXML
    private Button reload;
    @FXML
    private CreateChargeTask createChargeTask;

    private BooleanProperty showDetailsSelected = new SimpleBooleanProperty( true );

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        injector.injectMembers( createChargeTask );

        TextFieldUtil.setupClearButtonField( pa, pa.rightProperty() );
        TextFieldUtil.installEnterButtonNode( pa, reload );
        TextFieldUtil.setupClearButtonField( charge, charge.rightProperty() );
        TextFieldUtil.installEnterButtonNode( charge, reload );

        from.clear();
        to.clear();

        filterSelection.getItems().addAll( "Zeitraum", "Prozess-Auftrag", "Charge" );
        filterSelection.getSelectionModel().selectedIndexProperty().addListener( ( p, o, n ) -> {
            preferencesManager.setIntValue( PreferencesManager.SCOPE_USER, "/casting/main/productionOrder", "lastFilterSelectionIndex", n.intValue() );
            if ( n.intValue() == 0 )
            {
                if ( filterToolBar.getItems().contains( durationFilter ) == false )
                {
                    filterToolBar.getItems().add( durationFilter );
                    from.requestFocus();
                }
                if ( filterToolBar.getItems().contains( paDataFilter ) )
                {
                    filterToolBar.getItems().remove( paDataFilter );
                }
                if ( filterToolBar.getItems().contains( chargeDataFilter ) )
                {
                    filterToolBar.getItems().remove( chargeDataFilter );
                }
            }
            else if ( n.intValue() == 1 )
            {
                if ( filterToolBar.getItems().contains( durationFilter ) )
                {
                    filterToolBar.getItems().remove( durationFilter );
                }
                if ( filterToolBar.getItems().contains( paDataFilter ) == false )
                {
                    filterToolBar.getItems().add( paDataFilter );
                }
                if ( filterToolBar.getItems().contains( chargeDataFilter ) )
                {
                    filterToolBar.getItems().remove( chargeDataFilter );
                }
            }
            else if ( n.intValue() == 2 )
            {
                if ( filterToolBar.getItems().contains( durationFilter ) )
                {
                    filterToolBar.getItems().remove( durationFilter );
                }
                if ( filterToolBar.getItems().contains( paDataFilter ) )
                {
                    filterToolBar.getItems().remove( paDataFilter );
                }
                if ( filterToolBar.getItems().contains( chargeDataFilter ) == false )
                {
                    filterToolBar.getItems().add( chargeDataFilter );
                }
            }
        } );
        final int filterSelectionIndex = preferencesManager.getIntValue( PreferencesManager.SCOPE_USER, "/casting/main/productionOrder", "lastFilterSelectionIndex" );
        filterSelection.getSelectionModel().clearAndSelect( filterSelectionIndex );

        detailsController.activeProperty().bind( showDetailsSelected );

        table.selectedValueProperty().addListener( ( observableValue, oldValue, newValue ) -> {
            ProductionOrderDTO selectedValue = newValue;
            detailsController.setSelectedValue( selectedValue );
            if ( selectedValue != null )
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
            final ProductionOrderView productionOrderView = businessManager.getSession( ProductionOrderView.class );

            Map<String, Object> parameters = new HashMap<>();
            SearchType searchType = null;
            if ( filterSelection.getSelectionModel().getSelectedIndex() == 0 )
            {
                searchType = SearchType.TIME_RANGE;
                // get from
                LocalDateTime fromLDT = from.getLocalDateTime();
                if ( fromLDT == null )
                {
                    LocalDate fromLD = from.getLocalDate();
                    if ( fromLD != null )
                    {
                        fromLDT = fromLD.atStartOfDay();
                    }
                    if ( fromLDT == null )
                    {
                        notifyManager.showInfoMessage( "Suchfehler", "Die 'Von' Zeit ist nicht besetzt" );
                        return null;
                    }
                }
                parameters.put( "fromDateTime", fromLDT );

                LocalDateTime toLDT = to.getLocalDateTime();
                if ( toLDT == null )
                {
                    LocalDate toLD = to.getLocalDate();
                    if ( toLD != null )
                    {
                        toLDT = toLD.atStartOfDay().plusHours( 24 );
                    }
                }
                if ( toLDT != null )
                {
                    parameters.put( "toDateTime", toLDT );
                }
            }
            else if ( filterSelection.getSelectionModel().getSelectedIndex() == 1 )
            {
                if ( StringTools.isFilled( pa.getText() ) )
                {
                    searchType = SearchType.LOT;
                    parameters.put( "pa", "%" + pa.getText() + "%" );
                }
            }
            else
            {
                if ( StringTools.isFilled( charge.getText() ) )
                {
                    searchType = SearchType.CHARGE;
                    parameters.put( "charge", "%" + charge.getText() + "%" );
                }
            }

            List<ProductionOrderDTO> entries = null;

            if ( searchType != null )
            {
                entries = productionOrderView.loadBySearchType( searchType, parameters );
            }

            if ( entries == null || entries.isEmpty() )
            {
                if ( filterSelection.getSelectionModel().getSelectedIndex() == 1 )
                {
                    if ( StringTools.isFilled( pa.getText() ) )
                    {
                        notifyManager.showSplashMessage( "Es wurde keine Prozess-Auftrag '" + pa.getText() + "' gefunden" );
                    }
                }
                else if ( filterSelection.getSelectionModel().getSelectedIndex() == 2 )
                {
                    if ( StringTools.isFilled( charge.getText() ) )
                    {
                        notifyManager.showSplashMessage( "Es wurde keine Charge '" + charge.getText() + "' gefunden" );
                    }
                }
                else
                {
                    notifyManager.showSplashMessage( "Es wurde keine Daten gefunden" );
                }
            }
            return entries;
        } );
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
    public void reloadView( Object source, View view )
    {
        table.loadData();
    }

    @Override
    public void activateView( View view )
    {
        super.activateView( view );
        createChargeTask.setLocked( !securityManager.hasRole( createChargeTask.getId() ) );
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

    @FXML
    public void assignCharge( ActionEvent actionEvent )
    {
        taskManager.executeTask( createChargeTask );
    }
}
