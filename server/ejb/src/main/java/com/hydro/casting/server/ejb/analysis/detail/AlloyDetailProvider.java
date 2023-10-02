package com.hydro.casting.server.ejb.analysis.detail;

import com.hydro.casting.server.contract.dto.AlloyDTO;
import com.hydro.casting.server.contract.dto.AnalysisDetailDTO;
import com.hydro.casting.server.contract.dto.CompositionDTO;
import com.hydro.casting.server.contract.dto.CompositionElementDTO;
import com.hydro.casting.server.ejb.main.service.AlloyService;
import com.hydro.casting.server.model.mat.Alloy;
import com.hydro.casting.server.model.mat.AlloyElement;
import com.hydro.core.server.contract.workplace.DetailProvider;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Map;

@Stateless
public class AlloyDetailProvider implements DetailProvider<AlloyDTO, AnalysisDetailDTO>
{
    @EJB
    AlloyService alloyService;

    @Override
    public AnalysisDetailDTO loadDetail( AlloyDTO master, Map<String, String> context )
    {
        Alloy alloy = alloyService.findOrCreateAlloy( master.getName() );
        AnalysisDetailDTO analysisDetailDTO = new AnalysisDetailDTO();
        analysisDetailDTO.setName( alloy.getName() );
        analysisDetailDTO.setIsLeaf( true );

        CompositionDTO minComp = new CompositionDTO();
        minComp.setName( alloy.getName() + " Min" );
        CompositionDTO maxComp = new CompositionDTO();
        maxComp.setName( alloy.getName() + " Max" );

        CompositionElementDTO compositionElementDTOMin;
        CompositionElementDTO compositionElementDTOMax;

        for ( AlloyElement alloyElement : alloy.getAlloyElements() )
        {
            compositionElementDTOMin = new CompositionElementDTO();
            compositionElementDTOMax = new CompositionElementDTO();

            compositionElementDTOMin.setName( alloyElement.getName() );
            compositionElementDTOMin.setValue( alloyElement.getMinValue() );
            compositionElementDTOMin.setPrecision( alloyElement.getPrecision() );
            compositionElementDTOMin.setSortOrderId( alloyElement.getElementIndex() );
            minComp.addToCompositionElementDTOList( compositionElementDTOMin );

            compositionElementDTOMax.setName( alloyElement.getName() );
            compositionElementDTOMax.setValue( alloyElement.getMaxValue() );
            compositionElementDTOMax.setPrecision( alloyElement.getPrecision() );
            compositionElementDTOMax.setSortOrderId( alloyElement.getElementIndex() );
            maxComp.addToCompositionElementDTOList( compositionElementDTOMax );
        }

        analysisDetailDTO.setMinComp( minComp );
        analysisDetailDTO.setMaxComp( maxComp );
        return analysisDetailDTO;
    }
}

