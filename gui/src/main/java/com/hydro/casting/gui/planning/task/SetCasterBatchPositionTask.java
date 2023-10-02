package com.hydro.casting.gui.planning.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.server.contract.dto.CasterDemandDTO;
import com.hydro.casting.server.contract.dto.CasterSchedulePosDTO;
import com.hydro.casting.server.contract.planning.CasterScheduleBusiness;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.task.AbstractTask;

import java.util.ArrayList;
import java.util.List;

public class SetCasterBatchPositionTask extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;

    private List<CasterDemandDTO> demands;
    private List<CasterSchedulePosDTO> schedulePoss;
    private Integer count;

    @Override
    public String getId()
    {
        return SecurityCasting.MABE.ACTION.EDIT_ENTRY;
    }

    public void setData( List<CasterDemandDTO> demands, List<CasterSchedulePosDTO> schedulePoss, Integer count )
    {
        this.demands = demands;
        this.schedulePoss = schedulePoss;
        this.count = count;
    }

    @Override
    public void doWork() throws Exception
    {
        final CasterScheduleBusiness casterScheduleBusiness = businessManager.getSession( CasterScheduleBusiness.class );
        casterScheduleBusiness.setCasterBatchPositions( new ArrayList<>(demands), new ArrayList<>(schedulePoss), count );
    }
}
