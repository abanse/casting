package com.hydro.casting.gui.main.table;

import com.hydro.core.gui.comp.ServerTableView;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

public class WorkStepTableController
{
    @FXML
    private ServerTableView table;
    @FXML
    private TableColumn apkColumn;

    @FXML
    private void initialize()
    {
        apkColumn.setSortType( TableColumn.SortType.ASCENDING );
        table.getSortOrder().add( apkColumn );
    }
}
