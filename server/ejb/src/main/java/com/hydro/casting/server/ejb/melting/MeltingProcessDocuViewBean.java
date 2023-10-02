package com.hydro.casting.server.ejb.melting;

import com.hydro.casting.server.contract.dto.MeltingProcessDocuDTO;
import com.hydro.casting.server.contract.melting.MeltingProcessDocuView;
import com.hydro.casting.server.ejb.melting.service.MeltingProcessDocuService;
import com.hydro.core.server.contract.workplace.SearchType;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

@Stateless
public class MeltingProcessDocuViewBean implements MeltingProcessDocuView
{
    @EJB
    private MeltingProcessDocuService meltingProcessDocuService;

    @Override
    public List<MeltingProcessDocuDTO> loadBySearchType( SearchType searchType, Map<String, Object> parameters )
    {
        return meltingProcessDocuService.loadBySearchType( searchType, parameters );
    }
}
