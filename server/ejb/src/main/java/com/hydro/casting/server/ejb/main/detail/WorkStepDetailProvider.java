package com.hydro.casting.server.ejb.main.detail;

import com.hydro.casting.server.contract.dto.ProductionOrderDTO;
import com.hydro.casting.server.contract.dto.WorkStepDTO;
import com.hydro.casting.server.contract.dto.WorkStepListDTO;
import com.hydro.casting.server.ejb.main.service.WorkStepService;
import com.hydro.core.server.contract.workplace.DetailProvider;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

@Stateless( name = "WorkStepDetailProvider" )
public class WorkStepDetailProvider implements DetailProvider<ProductionOrderDTO, WorkStepListDTO>
{
    @EJB
    private WorkStepService workStepService;

    @Override
    public WorkStepListDTO loadDetail( ProductionOrderDTO master, Map<String, String> context )
    {
        if ( master == null )
        {
            return null;
        }
        final List<WorkStepDTO> workStepDTOList = workStepService.loadByProductionOrder( master.getId() );
        if ( workStepDTOList == null || workStepDTOList.isEmpty() )
        {
            return null;
        }
        return new WorkStepListDTO( workStepDTOList );
    }
}
