package com.hydro.casting.gui.stock.control;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.gui.analysis.control.AnalysisTable;
import com.hydro.casting.gui.model.Analysis;
import com.hydro.casting.server.contract.dto.CrucibleMaterialDTO;
import com.hydro.casting.server.contract.dto.MaterialAnalysisElementDTO;
import com.hydro.casting.server.contract.dto.MaterialDTO;
import com.hydro.core.gui.control.DetailController;
import javafx.fxml.FXML;

public class MaterialAnalysisDetailControlController extends DetailController<MaterialDTO, MaterialDTO>
{
    @FXML
    private AnalysisTable analysisTable;

    public MaterialAnalysisDetailControlController()
    {
        super( null, MaterialDTO.class );
    }

    @Inject
    private Injector injector;

    /**
     * Initializes the controller class.
     */
    @FXML
    private void initialize()
    {
        analysisTable.setDisableAverageLineColor( true );
    }

    @Override
    public void loadDetail( MaterialDTO data )
    {
        analysisTable.clear();
        if ( data != null && data.getMaterialAnalysisElements() != null )
        {
            final Analysis analysis = new Analysis();
            analysis.setName( data.getName() );
            for ( MaterialAnalysisElementDTO materialAnalysisElementDTO : data.getMaterialAnalysisElements() )
            {
                analysis.setCompositionElementValue( materialAnalysisElementDTO.getName(), materialAnalysisElementDTO.getValue() );
            }
            analysisTable.setAnalysis( analysis, null, false );
        }
    }
}
