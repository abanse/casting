package com.hydro.casting.server.ejb.prod.detail;

import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.ProcessDocuDTO;
import com.hydro.casting.server.ejb.planning.service.CasterScheduleService;
import com.hydro.casting.server.model.sched.CastingBatch;
import com.hydro.casting.server.model.sched.dao.CastingBatchHome;
import com.hydro.core.server.contract.workplace.DetailProvider;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Map;

@Stateless( name = "ChargeDetailProvider" )
public class ChargeDetailProvider implements DetailProvider<ProcessDocuDTO, CasterScheduleDTO>
{
    @EJB
    private CastingBatchHome castingBatchHome;

    @EJB
    private CasterScheduleService casterScheduleService;

    @Override
    public CasterScheduleDTO loadDetail( ProcessDocuDTO master, Map<String, String> context )
    {
        if ( master == null )
        {
            return null;
        }
        final CastingBatch castingBatch = castingBatchHome.findById( master.getId() );
        if ( castingBatch == null )
        {
            return null;
        }
        return casterScheduleService.load( castingBatch );
    }
}
