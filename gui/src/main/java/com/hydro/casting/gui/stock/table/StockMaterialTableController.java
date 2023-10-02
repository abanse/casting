package com.hydro.casting.gui.stock.table;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.dto.MaterialDTO;
import com.hydro.core.gui.comp.ClientModelTableView;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;

import java.util.Objects;

public class StockMaterialTableController
{
    @FXML
    private ClientModelTableView<MaterialDTO> table;
    @FXML
    private TableColumn nameColumn;

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        table.setAppFilter( materialDTO -> Objects.equals( materialDTO.getPlace(), Casting.PLACE.STOCKS ) );
        table.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        nameColumn.setSortType( TableColumn.SortType.ASCENDING );
        table.getSortOrder().add( nameColumn );
    }
}
