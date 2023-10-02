package com.hydro.casting.gui.prod.table;

import com.hydro.core.gui.comp.ServerTableView;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

public class MoldDepartmentTableController
{
    @FXML
    private ServerTableView table;
    @FXML
    private TableColumn castingTS;

    @FXML
    private void initialize()
    {
        castingTS.setSortType( TableColumn.SortType.ASCENDING );
        table.getSortOrder().add( castingTS );
    }
}
