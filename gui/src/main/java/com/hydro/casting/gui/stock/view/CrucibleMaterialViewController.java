package com.hydro.casting.gui.stock.view;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.stock.detail.CrucibleMaterialDetailController;
import com.hydro.casting.server.contract.dto.CrucibleMaterialDTO;
import com.hydro.core.gui.ImagesCore;
import com.hydro.core.gui.View;
import com.hydro.core.gui.ViewType;
import com.hydro.core.gui.comp.ClientModelTableView;
import com.hydro.core.gui.comp.SearchBox;
import com.hydro.core.gui.comp.TaskButton;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import org.controlsfx.control.MasterDetailPane;

@ViewDeclaration( id = CrucibleMaterialViewController.ID, fxmlFile = "/com/hydro/casting/gui/stock/view/CrucibleMaterialView.fxml", type = ViewType.MAIN )
public class CrucibleMaterialViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.STOCK.CRUCIBLE_MATERIAL.VIEW;
    @Inject
    private Injector injector;
    @FXML
    private ClientModelTableView<CrucibleMaterialDTO> table;
    @FXML
    private TaskButton showDetails;
    @FXML
    private SearchBox searchBox;
    @FXML
    private MasterDetailPane detailPane;
    @FXML
    private CrucibleMaterialDetailController detailsController;

    private BooleanProperty showDetailsSelected = new SimpleBooleanProperty( false );

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        detailsController.activeProperty().bind( showDetailsSelected );

        table.setTitle( "Materialbestand" );
        table.filterProperty().bindBidirectional( searchBox.textProperty() );

        table.selectedValueProperty().addListener( ( observable, oldValue, newValue ) -> detailsController.setSelectedValue( newValue ) );

        table.connect( injector );
    }

    @Override
    public void beforeShown( View view )
    {
        super.beforeShown( view );
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

    @FXML
    public void showDetails( ActionEvent actionEvent )
    {
        showDetailsSelected.set( !showDetailsSelected.get() );
        ImageView imageView;
        if ( showDetailsSelected.get() )
        {
            detailPane.setShowDetailNode( true );
            imageView = new ImageView( ImagesCore.EXPAND.load() );
        }
        else
        {
            detailPane.setShowDetailNode( false );
            imageView = new ImageView( ImagesCore.COLLAPSE.load() );
        }
        imageView.setFitHeight( 21.0 );
        imageView.setFitWidth( 21.0 );
        showDetails.setGraphic( imageView );
    }
}
