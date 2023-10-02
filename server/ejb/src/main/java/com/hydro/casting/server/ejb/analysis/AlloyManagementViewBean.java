package com.hydro.casting.server.ejb.analysis;

import com.hydro.casting.server.contract.analysis.AlloyManagementView;
import com.hydro.casting.server.contract.dto.AlloyDTO;
import com.hydro.casting.server.contract.dto.AnalysisDetailDTO;
import com.hydro.core.server.contract.workplace.DetailProvider;
import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Map;

@Stateless
public class AlloyManagementViewBean implements AlloyManagementView
{
    @EJB( beanName = "AlloyDetailProvider" )
    private DetailProvider<AlloyDTO, AnalysisDetailDTO> alloyDetailProvider;

    @Override
    public <D extends ViewDTO> D loadDetail( Class<D> dto, AlloyDTO master, Map<String, String> context )
    {
        @SuppressWarnings( "unchecked" )
        DetailProvider<AlloyDTO, D> provider = (DetailProvider<AlloyDTO, D>) alloyDetailProvider;
        return provider.loadDetail( master, context );
    }
}
