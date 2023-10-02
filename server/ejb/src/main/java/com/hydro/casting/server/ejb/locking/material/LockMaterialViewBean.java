package com.hydro.casting.server.ejb.locking.material;

import com.hydro.casting.server.contract.locking.material.LockMaterialView;
import com.hydro.casting.server.contract.locking.material.dto.LockMaterialRequestDTO;
import com.hydro.casting.server.contract.locking.material.dto.LockableMaterialDTO;
import com.hydro.core.server.contract.workplace.DetailListProvider;
import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class LockMaterialViewBean implements LockMaterialView
{
    @EJB( beanName = "LockMaterialRequestProvider" )
    private DetailListProvider<LockableMaterialDTO, LockMaterialRequestDTO> lockMaterialRequestProvider;

    private Map<Class<?>, DetailListProvider<LockableMaterialDTO, ?>> detailProviders = new HashMap<>();

    @PostConstruct
    private void postConstruct()
    {
        detailProviders.put( LockMaterialRequestDTO.class, lockMaterialRequestProvider );
    }

    @Override
    public <D extends ViewDTO> D loadDetail( Class<D> dto, List<LockableMaterialDTO> masterList, Map<String, String> context )
    {
        DetailListProvider<LockableMaterialDTO, D> provider = (DetailListProvider<LockableMaterialDTO, D>) detailProviders.get( dto );
        if ( provider != null )
        {
            return provider.loadDetail( masterList, context );
        }
        return null;
    }
}
