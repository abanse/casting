package com.hydro.casting.gui.label.view;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.analysis.table.AnalysisDetailTableController;
import com.hydro.casting.gui.analysis.table.grouping.AnalysisMasterTableGroup;
import com.hydro.casting.gui.label.control.AnalysisTable;
import com.hydro.casting.server.contract.dto.AnalysisDTO;
import com.hydro.core.gui.ImagesCore;
import com.hydro.core.gui.View;
import com.hydro.core.gui.ViewType;
import com.hydro.core.gui.comp.CacheTreeTableView;
import com.hydro.core.gui.comp.SearchBox;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;

@ViewDeclaration( id = LabelViewController.ID, fxmlFile = "/com/hydro/casting/gui/label/view/LabelView.fxml", type = ViewType.MAIN )
public class LabelViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.LABEL.VIEW;


    @Inject
    private Injector injector;
    @FXML
    private CacheTreeTableView<AnalysisDTO> masterTable;
    @FXML
    private AnalysisTable detailTable;
    @FXML
    private AnalysisDetailTableController detailTableController;
    @FXML
    private SearchBox searchBox;

    @FXML
    public ComboBox fiterprinter;
    public final static Image ANALYSIS_OK = ImagesCore.READY.load();
    public final static Image ANALYSIS_LOW = ImagesCore.BLUE_DOWN_ARROW.load();
    public final static Image ANALYSIS_HIGH = ImagesCore.RED_UP_ARROW.load();

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        masterTable.setTitle( "Etiketten" );
        masterTable.filterProperty().bindBidirectional( searchBox.textProperty() );

        masterTable.connect( injector, new AnalysisMasterTableGroup() );

      //  masterTable.getSelectionModel().selectedItemProperty().addListener( ( p, o, n ) -> setDetailTableContent( n ) );
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

    private void setDetailTableContent( TreeItem<AnalysisDTO> treeItem )
    {
        AnalysisDTO dto = null;
        if ( treeItem != null )
        {
            dto = treeItem.getValue();
        }
        detailTableController.setMaster( dto );
    }
}
