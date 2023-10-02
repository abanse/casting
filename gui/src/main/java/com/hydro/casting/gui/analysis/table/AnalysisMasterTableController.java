package com.hydro.casting.gui.analysis.table;

import com.hydro.casting.server.contract.dto.AnalysisDTO;
import com.hydro.core.gui.comp.CacheTreeTableView;
import javafx.fxml.FXML;

public class AnalysisMasterTableController
{
    @FXML
    CacheTreeTableView<AnalysisDTO> masterTable;

    @FXML
    void initialize()
    {
        masterTable.setExpanded( false );
        masterTable.startRegularRefresh();
    }
}
