package com.hydro.casting.gui.stock.detail;

import com.hydro.casting.gui.stock.control.MaterialAnalysisDetailControlController;
import com.hydro.casting.server.contract.dto.MaterialDTO;
import com.hydro.core.gui.detail.DetailTabsController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;

public class CrucibleMaterialDetailController extends DetailTabsController<MaterialDTO>
{
    @FXML
    private Label tabTitle;

    @FXML
    private TabPane details;

    @FXML
    private Node analysisDetail;

    @FXML
    private MaterialAnalysisDetailControlController analysisDetailController;

    /**
     * Initializes the controller class.
     */
    @FXML
    protected void initialize()
    {
        super.initialize( details );

        registerDetail( analysisDetail, analysisDetailController );
    }

    @Override
    protected void setTitle( MaterialDTO viewDTO )
    {
        if ( viewDTO == null || viewDTO.getName() == null )
        {
            tabTitle.setText( null );
        }
        else
        {
            tabTitle.setText( viewDTO.getName() );
        }
    }
}
