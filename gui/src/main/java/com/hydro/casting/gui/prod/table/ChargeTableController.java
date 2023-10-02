package com.hydro.casting.gui.prod.table;

import com.hydro.core.gui.comp.ServerTableView;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

public class ChargeTableController
{
    @FXML
    private ServerTableView table;
    @FXML
    private TableColumn chargeColumn;

    @FXML
    private void initialize()
    {
        chargeColumn.setSortType( TableColumn.SortType.ASCENDING );
        table.getSortOrder().add( chargeColumn );
    }
}
