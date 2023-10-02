package com.hydro.casting.gui.stock.table;

import com.hydro.casting.server.contract.dto.CrucibleMaterialDTO;
import com.hydro.core.gui.comp.ClientModelTableView;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;

public class CrucibleMaterialTableController
{
    @FXML
    private ClientModelTableView<CrucibleMaterialDTO> table;
    @FXML
    private TableColumn nameColumn;

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        table.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        nameColumn.setSortType( TableColumn.SortType.ASCENDING );
        table.getSortOrder().add( nameColumn );
    }
}
