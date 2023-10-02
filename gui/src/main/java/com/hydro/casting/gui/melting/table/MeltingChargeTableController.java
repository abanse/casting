package com.hydro.casting.gui.melting.table;

import com.hydro.casting.gui.util.CastingUtil;
import com.hydro.casting.server.contract.dto.MeltingScheduleDTO;
import com.hydro.core.gui.comp.ServerTableView;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
public class MeltingChargeTableController
{
    @FXML
    private TableColumn<String, Number> schedulableStateColumn;
    @FXML
    private ServerTableView<MeltingScheduleDTO> table;
    @FXML
    private TableColumn<MeltingScheduleDTO, String> chargeColumn;

    @FXML
    private void initialize()
    {
        schedulableStateColumn.setCellFactory( TextFieldTableCell.forTableColumn( CastingUtil.meltingExecutionStatePropertyConverter ) );
        chargeColumn.setSortType( TableColumn.SortType.ASCENDING );
        table.getSortOrder().add( chargeColumn );
    }
}
