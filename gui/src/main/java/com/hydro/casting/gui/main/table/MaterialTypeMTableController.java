package com.hydro.casting.gui.main.table;

import com.hydro.core.gui.comp.MaintenanceTableView;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

public class MaterialTypeMTableController
{
    @FXML
    public TableColumn apk;
    @FXML
    public MaintenanceTableView table;

    @FXML
    private void initialize()
    {
        apk.setSortType( TableColumn.SortType.ASCENDING );
        table.getSortOrder().add( apk );
    }
}
