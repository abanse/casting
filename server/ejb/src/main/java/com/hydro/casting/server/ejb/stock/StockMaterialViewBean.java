package com.hydro.casting.server.ejb.stock;

import com.hydro.casting.server.contract.dto.MaterialCharacteristicsDTO;
import com.hydro.casting.server.contract.dto.MaterialDTO;
import com.hydro.casting.server.contract.stock.StockMaterialView;
import com.hydro.core.server.contract.workplace.DetailProvider;
import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.Map;

@Stateless
public class StockMaterialViewBean implements StockMaterialView
{
    private Map<Class<?>, DetailProvider<MaterialDTO, ?>> detailProviders = new HashMap<>();

    @EJB( beanName = "MaterialCharacteristicsDetailProvider" )
    private DetailProvider<MaterialDTO, MaterialCharacteristicsDTO> materialCharacteristicsDetailProvider;

    @PostConstruct
    private void postConstruct()
    {
        detailProviders.put( MaterialCharacteristicsDTO.class, materialCharacteristicsDetailProvider );
    }

    @Override
    public <D extends ViewDTO> D loadDetail( Class<D> dto, MaterialDTO master, Map<String, String> context )
    {
        @SuppressWarnings( "unchecked" )
        DetailProvider<MaterialDTO, D> provider = (DetailProvider<MaterialDTO, D>) detailProviders.get( dto );
        if ( provider != null )
        {
            return provider.loadDetail( master, context );
        }
        return null;
    }
}