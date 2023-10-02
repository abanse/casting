package com.hydro.casting.gui.planning.table;

import com.hydro.core.gui.comp.ServerTreeTableView;
import javafx.fxml.FXML;
import javafx.scene.control.TreeTableColumn;

public class FurnaceDemandTableController
{
    @FXML
    private ServerTreeTableView demandTable;
    @FXML
    private TreeTableColumn startTimeColumn;
    @FXML
    private TreeTableColumn chargeColumn;

    @FXML
    private void initialize()
    {
        startTimeColumn.setSortType( TreeTableColumn.SortType.ASCENDING );
        chargeColumn.setSortType( TreeTableColumn.SortType.ASCENDING );
        demandTable.getTreeTableView().getSortOrder().add( startTimeColumn );
        demandTable.getTreeTableView().getSortOrder().add( chargeColumn );

        demandTable.setExpanded( false );
    }
}
