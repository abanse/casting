package com.hydro.casting.gui.melting.table;

import com.hydro.casting.server.contract.dto.MeltingProcessDocuDTO;
import com.hydro.core.gui.comp.ServerTableView;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
public class MeltingProcessDocuTableController
{
    @FXML
    private ServerTableView<MeltingProcessDocuDTO> table;
    @FXML
    private TableColumn<MeltingProcessDocuDTO, String> inProgressTS;

    @FXML
    private void initialize()
    {
        inProgressTS.setSortType( javafx.scene.control.TableColumn.SortType.ASCENDING );
        table.getSortOrder().add( inProgressTS );
    }
}
