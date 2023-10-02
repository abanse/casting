package com.hydro.casting.server.ejb.main.service;

import com.hydro.casting.server.contract.dto.MaterialAnalysisElementDTO;
import com.hydro.casting.server.contract.dto.MaterialDTO;
import com.hydro.core.server.common.service.BaseService;
import com.hydro.core.server.contract.workplace.dto.ViewDTO;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class BaseMaterialService<E, DTO extends ViewDTO> extends BaseService<E, DTO>
{
    protected BaseMaterialService( String cache, String versionCache, String cachePath )
    {
        super( cache, versionCache, cachePath );
    }

    protected void mergeAnalysis( List<? extends MaterialDTO> materials, List<Tuple> analysisRows )
    {
        final MultiValuedMap<Long, MaterialAnalysisElementDTO> analysisValueMap = new ArrayListValuedHashMap<>();
        for ( Tuple analysisRow : analysisRows )
        {
            final Long analysisObjid = (Long) analysisRow.get( "analysisObjid" );
            final String name = (String) analysisRow.get( "name" );
            final Number value = (Number) analysisRow.get( "standardValue" );

            final MaterialAnalysisElementDTO materialAnalysisElementDTO = new MaterialAnalysisElementDTO();
            materialAnalysisElementDTO.setName( name );
            if ( value != null )
            {
                materialAnalysisElementDTO.setValue( value.doubleValue() );
            }
            analysisValueMap.put( analysisObjid, materialAnalysisElementDTO );
        }

        for ( MaterialDTO material : materials )
        {
            final List<MaterialAnalysisElementDTO> materialAnalysisElementDTOS = new ArrayList<>( analysisValueMap.get( material.getAnalysisObjid() ) );
            Collections.sort( materialAnalysisElementDTOS, Comparator.comparing( MaterialAnalysisElementDTO::getName ) );
            material.setMaterialAnalysisElements( materialAnalysisElementDTOS );
        }
    }

}
