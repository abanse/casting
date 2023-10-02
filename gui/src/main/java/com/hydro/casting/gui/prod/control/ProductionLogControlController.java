package com.hydro.casting.gui.prod.control;

import com.hydro.casting.gui.prod.table.ProductionLogTableController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ProductionLogControlController
{
    @FXML
    private ProductionLogTableController productionLogTableController;

    /**
     * Initializes the controller class.
     */
    @FXML
    private void initialize()
    {
    }

    public String getMachineApk()
    {
        return productionLogTableController.getMachineApk();
    }

    public void setMachineApk( String machineApk )
    {
        productionLogTableController.setMachineApk( machineApk );
    }

    public void reload()
    {
        productionLogTableController.reload();
    }

    @FXML
    private void reload( ActionEvent actionEvent )
    {
        reload();
    }
}
