package com.hydro.casting.gui.melting.view;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.control.ChargeTimeFilterToolbar;
import com.hydro.casting.gui.melting.detail.MeltingProcessDocuDetailController;
import com.hydro.casting.server.contract.dto.MeltingProcessDocuDTO;
import com.hydro.casting.server.contract.melting.MeltingProcessDocuView;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.ViewType;
import com.hydro.core.gui.comp.ServerTableView;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import com.hydro.core.server.contract.workplace.SearchType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.controlsfx.control.MasterDetailPane;

import java.util.List;
import java.util.Map;
@ViewDeclaration( id = MeltingProcessDocuViewController.ID, fxmlFile = "/com/hydro/casting/gui/melting/view/MeltingProcessDocuView.fxml", type = ViewType.MAIN )
public class MeltingProcessDocuViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.MELTING.PROCESS_DOCU.VIEW;

    @Inject
    private Injector injector;
    @Inject
    private BusinessManager businessManager;
    @Inject
    private NotifyManager notifyManager;

    @FXML
    private ChargeTimeFilterToolbar filterToolbar;
    @FXML
    private Button reload;
    @FXML
    private ServerTableView<MeltingProcessDocuDTO> table;
    @FXML
    private MasterDetailPane detailPane;
    @FXML
    private MeltingProcessDocuDetailController detailsController;
    private final BooleanProperty showDetailsSelected = new SimpleBooleanProperty( true );

    @FXML
    private void initialize()
    {
        injector.injectMembers( filterToolbar );
        filterToolbar.initialize();
        filterToolbar.bindButtonToCharge( reload );

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

        table.connect( injector, () -> {
            final MeltingProcessDocuView deliveryView = businessManager.getSession( MeltingProcessDocuView.class );

            Map<String, Object> parameters = filterToolbar.getParameters();
            SearchType searchType = filterToolbar.getSearchType();
            List<MeltingProcessDocuDTO> entries = null;
            if ( searchType != null )
            {
                entries = deliveryView.loadBySearchType( searchType, parameters );
            }

            if ( entries == null || entries.isEmpty() )
            {
                if ( searchType == SearchType.CHARGE )
                {
                    if ( filterToolbar.isChargeFieldFilled() )
                    {
                        notifyManager.showSplashMessage( "Es wurde keine Charge '" + filterToolbar.getChargeValue() + "' gefunden" );
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

    @FXML
    public void reload( ActionEvent actionEvent )
    {
        table.loadData();
    }
}
