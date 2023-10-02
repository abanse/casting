package com.hydro.casting.gui.stock.table;

import com.hydro.casting.server.contract.dto.SlabDTO;
import com.hydro.core.gui.comp.ClientModelTableView;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;

public class SlabTableController
{
    @FXML
    private ClientModelTableView<SlabDTO> table;
    @FXML
    private TableColumn slabColumn;

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        table.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        slabColumn.setSortType( TableColumn.SortType.ASCENDING );
        table.getSortOrder().add( slabColumn );
    }
}
