package com.hydro.casting.server.ejb.main;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.ProductionOrderDTO;
import com.hydro.casting.server.contract.main.ProductionOrderBusiness;
import com.hydro.casting.server.ejb.planning.service.CasterScheduleService;
import com.hydro.casting.server.model.po.ProductionOrder;
import com.hydro.casting.server.model.po.dao.ProductionOrderHome;
import com.hydro.casting.server.model.sched.CastingBatch;
import com.hydro.core.common.exception.BusinessException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class ProductionOrderBusinessBean implements ProductionOrderBusiness
{
    @EJB
    private CasterScheduleService casterScheduleService;

    @EJB
    private ProductionOrderHome productionOrderHome;

    @Override
    public CasterScheduleDTO assignCharge( String currentUser, List<ProductionOrderDTO> productionOrders ) throws BusinessException
    {
        final List<ProductionOrder> productionOrderList = new ArrayList<>();
        for ( ProductionOrderDTO productionOrder : productionOrders )
        {
            productionOrderList.add( productionOrderHome.findById( productionOrder.getId() ) );
        }

        final CastingBatch castingBatch = casterScheduleService.createCharge( productionOrderList, Casting.SCHEDULABLE_STATE.PLANNED );

        casterScheduleService.replicateCache( castingBatch );

        return casterScheduleService.load( castingBatch );
    }
}
