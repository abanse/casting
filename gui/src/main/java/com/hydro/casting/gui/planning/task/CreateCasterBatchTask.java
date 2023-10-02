package com.hydro.casting.gui.planning.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.server.contract.dto.CasterDemandDTO;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.CasterSchedulePosDTO;
import com.hydro.casting.server.contract.planning.CasterScheduleBusiness;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.task.AbstractTask;

import java.util.ArrayList;
import java.util.List;

public class CreateCasterBatchTask extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;

    private String machineApk;

    private List<CasterDemandDTO> demands;
    private CasterScheduleDTO refCasterScheduleDTO;
    private boolean after;
    private Integer count;

    @Override
    public String getId()
    {
        return SecurityCasting.MABE.ACTION.EDIT_ENTRY;
    }

    public String getMachineApk()
    {
        return machineApk;
    }

    public void setMachineApk( String machineApk )
    {
        this.machineApk = machineApk;
    }

    public void setData( List<CasterDemandDTO> demands, CasterScheduleDTO refCasterScheduleDTO, boolean after, Integer count )
    {
        this.demands = demands;
        this.refCasterScheduleDTO = refCasterScheduleDTO;
        this.after = after;
        this.count = count;
    }

    @Override
    public void doWork() throws Exception
    {
        final CasterScheduleBusiness casterScheduleBusiness = businessManager.getSession( CasterScheduleBusiness.class );
        casterScheduleBusiness.createCasterBatch( machineApk, new ArrayList<>(demands), refCasterScheduleDTO, after, count );
    }
}
