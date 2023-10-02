package com.hydro.casting.server.contract.melting;

import com.hydro.casting.server.contract.dto.MeltingProcessDocuDTO;
import com.hydro.core.server.contract.workplace.SearchType;

import javax.ejb.Remote;
import java.util.List;
import java.util.Map;

@Remote
public interface MeltingProcessDocuView
{
    List<MeltingProcessDocuDTO> loadBySearchType( SearchType searchType, Map<String, Object> parameters );
}
