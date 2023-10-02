package com.hydro.casting.gui.stock.view;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.server.contract.dto.SlabDTO;
import com.hydro.core.common.util.StringTools;
import com.hydro.core.common.util.TextFieldUtil;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.*;
import com.hydro.core.gui.comp.ClientModelTableView;
import com.hydro.core.gui.comp.SearchBox;
import com.hydro.core.gui.task.OpenDetailViewTask;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.controlsfx.control.textfield.CustomTextField;

@ViewDeclaration( id = SlabViewController.ID, fxmlFile = "/com/hydro/casting/gui/stock/view/SlabView.fxml", type = ViewType.MAIN )
public class SlabViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.STOCK.SLAB.VIEW;

    @Inject
    private Injector injector;
    @Inject
    private SecurityManager securityManager;
    @Inject
    private ViewManager viewManager;
    @Inject
    private TaskManager taskManager;
    @FXML
    private ClientModelTableView<SlabDTO> table;
    @FXML
    private SearchBox searchBox;
    @FXML
    private CustomTextField alloy;
    @FXML
    private CustomTextField width;
    @FXML
    private CustomTextField length;
    @FXML
    private Label countInfo;
    @FXML
    private OpenDetailViewTask<SlabDTO> lockMaterialTask;

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        injector.injectMembers( lockMaterialTask );

        table.setTitle( "Barrenbestand" );
        table.filterProperty().bindBidirectional( searchBox.textProperty() );
        table.setAppFilter( slabDTO -> {
            boolean fits = true;
            if ( StringTools.isFilled( width.getText() ) )
            {
                double searchWidth = 0;
                try
                {
                    searchWidth = Double.parseDouble( width.getText() );
                }
                catch ( NumberFormatException nfex )
                {
                    // ignore
                }
                if ( slabDTO.getWidth() != searchWidth )
                {
                    fits = false;
                }
            }
            if ( StringTools.isFilled( length.getText() ) )
            {
                double searchLength = 0;
                try
                {
                    searchLength = Double.parseDouble( length.getText() );
                }
                catch ( NumberFormatException nfex )
                {
                    // ignore
                }
                if ( slabDTO.getLength() != searchLength )
                {
                    fits = false;
                }
            }
            if ( StringTools.isFilled( alloy.getText() ) )
            {
                if ( slabDTO.getAlloy() == null || !slabDTO.getAlloy().toUpperCase().contains( alloy.getText().toUpperCase() ) )
                {
                    fits = false;
                }
            }
            return fits;
        } );

        table.connect( injector );

        table.selectedValueProperty().addListener( (( observable, oldValue, newValue ) -> lockMaterialTask.setDisabled( newValue == null )) );

        countInfo.textProperty().bind( table.countInfoProperty() );

        TextFieldUtil.setupClearButtonField( width, width.rightProperty() );
        TextFieldUtil.setupClearButtonField( length, length.rightProperty() );
        TextFieldUtil.setupClearButtonField( alloy, alloy.rightProperty() );

        width.textProperty().addListener( ( InvalidationListener ) -> {
            table.reload();
        } );
        length.textProperty().addListener( ( InvalidationListener ) -> {
            table.reload();
        } );
        alloy.textProperty().addListener( ( InvalidationListener ) -> {
            table.reload();
        } );
    }

    @Override
    public void beforeShown( View view )
    {
        super.beforeShown( view );

        lockMaterialTask.setLocked( !securityManager.hasRole( lockMaterialTask.getId() ) );
    }

    @Override
    public void setStartObject( Object startObject )
    {
        if ( startObject == null || !( startObject instanceof String ) )
        {
            return;
        }
        searchBox.setText( (String) startObject );
    }

    public void lock( ActionEvent actionEvent )
    {
        taskManager.executeTask( lockMaterialTask );
    }
}
