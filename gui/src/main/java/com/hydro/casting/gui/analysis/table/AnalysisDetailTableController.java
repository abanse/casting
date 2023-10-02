package com.hydro.casting.gui.analysis.table;

import com.hydro.casting.gui.analysis.control.AnalysisTable;
import com.hydro.casting.gui.analysis.util.AnalysisCompositionHelper;
import com.hydro.casting.gui.analysis.util.CompositionDTOComparator;
import com.hydro.casting.gui.model.Analysis;
import com.hydro.casting.gui.model.Specification;
import com.hydro.casting.server.contract.analysis.AnalysisManagementView;
import com.hydro.casting.server.contract.dto.AnalysisDTO;
import com.hydro.casting.server.contract.dto.AnalysisDetailDTO;
import com.hydro.core.gui.control.DetailController;
import javafx.fxml.FXML;

public class AnalysisDetailTableController extends DetailController<AnalysisDTO, AnalysisDetailDTO>
{
    @FXML
    private AnalysisTable detailTable;

    public AnalysisDetailTableController()
    {
        super( AnalysisManagementView.class, AnalysisDetailDTO.class );
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
            // Sorting the analyses / samples by their logical sample order
            detail.getAnalysisList().sort( new CompositionDTOComparator() );
            AnalysisCompositionHelper.setDetailOnAnalysisTable( detail, detailTable, false );
        }
        else
        {
            detailTable.setAnalysis( null, (Specification) null, false );
        }
    }
}
