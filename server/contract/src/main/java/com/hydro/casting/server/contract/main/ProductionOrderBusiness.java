package com.hydro.casting.server.contract.main;

import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.ProductionOrderDTO;
import com.hydro.core.common.exception.BusinessException;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface ProductionOrderBusiness
{
    CasterScheduleDTO assignCharge( String currentUser, List<ProductionOrderDTO> productionOrders ) throws BusinessException;
}
