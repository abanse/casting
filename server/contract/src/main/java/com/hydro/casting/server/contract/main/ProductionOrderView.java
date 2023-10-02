package com.hydro.casting.server.contract.main;

import com.hydro.casting.server.contract.dto.ProductionOrderDTO;
import com.hydro.core.server.contract.workplace.MasterDetailProvider;
import com.hydro.core.server.contract.workplace.SearchType;
import com.hydro.core.server.contract.workplace.ViewModel;

import javax.ejb.Remote;
import java.util.List;
import java.util.Map;

@Remote
public interface ProductionOrderView extends MasterDetailProvider<ProductionOrderDTO>
{
    List<ProductionOrderDTO> loadBySearchType( SearchType searchType, Map<String, Object> parameters );

    ViewModel<ProductionOrderDTO> validate( String currentUser, List<ProductionOrderDTO> productionOrderDTOList );
}
