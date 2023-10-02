package com.hydro.casting.gui.analysis.view;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.analysis.control.AnalysisTable;
import com.hydro.casting.gui.analysis.table.AlloyDetailTableController;
import com.hydro.casting.server.contract.dto.AlloyDTO;
import com.hydro.core.gui.View;
import com.hydro.core.gui.ViewType;
import com.hydro.core.gui.comp.CacheTreeTableView;
import com.hydro.core.gui.comp.SearchBox;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;

@ViewDeclaration( id = AlloyViewController.ID, fxmlFile = "/com/hydro/casting/gui/analysis/view/AlloyView.fxml", type = ViewType.MAIN )
public class AlloyViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.ALLOY.VIEW;
    @Inject
    private Injector injector;
    @FXML
    private CacheTreeTableView<AlloyDTO> masterTable;
    @FXML
    private AnalysisTable detailTable;
    @FXML
    private AlloyDetailTableController detailTableController;
    @FXML
    private SearchBox searchBox;

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        masterTable.setTitle( "Soll-Analysen" );
        masterTable.filterProperty().bindBidirectional( searchBox.textProperty() );

        masterTable.connect( injector );

        masterTable.getSelectionModel().selectedItemProperty().addListener( ( p, o, n ) -> setDetailTableContent( n ) );
    }

    @Override
    public void beforeShown( View view )
    {
        super.beforeShown( view );
    }

    @Override
    public void setStartObject( Object startObject )
    {
        if ( !( startObject instanceof String ) )
        {
            return;
        }
        searchBox.setText( (String) startObject );
    }

    private void setDetailTableContent( TreeItem<AlloyDTO> treeItem )
    {
        AlloyDTO dto = treeItem.getValue();
        detailTableController.setMaster( dto );
    }
}
