package com.hydro.casting.gui.prod.table;

import com.hydro.core.gui.comp.ServerTableView;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

public class UnloadSlabTableController
{
    @FXML
    private ServerTableView table;
    @FXML
    private TableColumn slab;

    @FXML
    private void initialize()
    {
        slab.setSortType( TableColumn.SortType.ASCENDING );
        table.getSortOrder().add( slab );
    }
}
