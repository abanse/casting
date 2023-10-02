package com.hydro.casting.server.contract.prod;

import com.hydro.casting.server.contract.dto.ProcessDocuDTO;
import com.hydro.core.server.contract.workplace.MasterDetailProvider;
import com.hydro.core.server.contract.workplace.SearchType;
import com.hydro.core.server.contract.workplace.ViewModel;

import javax.ejb.Remote;
import java.util.List;
import java.util.Map;

@Remote
public interface ProcessDocuView extends MasterDetailProvider<ProcessDocuDTO>
{
    List<ProcessDocuDTO> loadBySearchType( SearchType searchType, Map<String, Object> parameters );

    ViewModel<ProcessDocuDTO> validate( String currentUser, List<ProcessDocuDTO> deliveryDTOList );
}
