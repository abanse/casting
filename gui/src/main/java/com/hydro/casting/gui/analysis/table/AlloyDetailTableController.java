package com.hydro.casting.gui.analysis.table;

import com.hydro.casting.gui.analysis.control.AnalysisTable;
import com.hydro.casting.gui.analysis.util.AnalysisCompositionHelper;
import com.hydro.casting.gui.model.Specification;
import com.hydro.casting.server.contract.analysis.AlloyManagementView;
import com.hydro.casting.server.contract.dto.AlloyDTO;
import com.hydro.casting.server.contract.dto.AnalysisDetailDTO;
import com.hydro.core.gui.control.DetailController;
import javafx.fxml.FXML;

public class AlloyDetailTableController extends DetailController<AlloyDTO, AnalysisDetailDTO>
{
    @FXML
    private AnalysisTable detailTable;

    public AlloyDetailTableController()
    {
        super( AlloyManagementView.class, AnalysisDetailDTO.class );
    }

    @FXML
    void initialize()
    {

    }

    @Override
    public void loadDetail( AnalysisDetailDTO detail )
    {
        if ( detail != null )
        {
            AnalysisCompositionHelper.setDetailOnAnalysisTable( detail, detailTable, false );
        }
        else
        {
            detailTable.setAnalysis( null, (Specification) null, false );
        }
    }
}
