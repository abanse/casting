package com.hydro.casting.gui.prod.control;

import com.hydro.casting.gui.analysis.control.AnalysisTable;
import com.hydro.casting.gui.analysis.util.AnalysisCompositionHelper;
import com.hydro.casting.gui.analysis.util.CompositionDTOComparator;
import com.hydro.casting.gui.model.Specification;
import com.hydro.casting.server.contract.dto.AnalysisDetailDTO;
import com.hydro.casting.server.contract.dto.ProcessDocuDTO;
import com.hydro.casting.server.contract.prod.ProcessDocuView;
import com.hydro.core.gui.control.DetailController;
import javafx.fxml.FXML;
public class AnalysisDetailController extends DetailController<ProcessDocuDTO, AnalysisDetailDTO>
{
    @FXML
    private AnalysisTable analysisDetail;

    public AnalysisDetailController()
    {
        super( ProcessDocuView.class, AnalysisDetailDTO.class );
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
            AnalysisCompositionHelper.setDetailOnAnalysisTable( detail, analysisDetail, false );
        }
        else
        {
            analysisDetail.setAnalysis( null, (Specification) null, false );
        }
    }
}
