package com.hydro.casting.gui.main.detail;

import com.hydro.casting.gui.main.control.WorkStepListDetailControlController;
import com.hydro.casting.server.contract.dto.ProductionOrderDTO;
import com.hydro.core.gui.detail.DetailTabsController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;

public class ProductionOrderDetailController extends DetailTabsController<ProductionOrderDTO>
{
    @FXML
    private Label tabTitle;

    @FXML
    private TabPane details;

    @FXML
    private Node workStepList;

    @FXML
    private WorkStepListDetailControlController workStepListController;

    /**
     * Initializes the controller class.
     */
    @FXML
    protected void initialize()
    {
        super.initialize( details );

        registerDetail( workStepList, workStepListController );
    }

    @Override
    protected void setTitle( ProductionOrderDTO viewDTO )
    {
        if ( viewDTO == null )
        {
            tabTitle.setText( null );
        }
        else
        {
            tabTitle.setText( viewDTO.getApk() );
        }
    }
}
