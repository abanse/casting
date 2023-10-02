package com.hydro.casting.server.ejb.analysis;

import com.hydro.casting.server.contract.analysis.AnalysisManagementView;
import com.hydro.casting.server.contract.dto.AnalysisDTO;
import com.hydro.casting.server.contract.dto.AnalysisDetailDTO;
import com.hydro.core.server.contract.workplace.DetailProvider;
import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Map;

@Stateless
public class AnalysisManagementViewBean implements AnalysisManagementView
{

    @EJB( beanName = "AnalysisDetailProvider" )
    private DetailProvider<AnalysisDTO, AnalysisDetailDTO> analysisDetailProvider;

    @Override
    public <D extends ViewDTO> D loadDetail( Class<D> dto, AnalysisDTO master, Map<String, String> context )
    {
        @SuppressWarnings( "unchecked" )
        DetailProvider<AnalysisDTO, D> provider = (DetailProvider<AnalysisDTO, D>) analysisDetailProvider;
        return provider.loadDetail( master, context );
    }
}
