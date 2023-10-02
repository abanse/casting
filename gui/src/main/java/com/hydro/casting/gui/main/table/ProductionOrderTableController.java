package com.hydro.casting.gui.main.table;

import com.hydro.core.gui.comp.ServerTableView;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;

public class ProductionOrderTableController
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

        table.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
    }
}
