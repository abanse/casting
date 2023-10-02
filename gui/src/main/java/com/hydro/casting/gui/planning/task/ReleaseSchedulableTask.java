package com.hydro.casting.gui.planning.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.planning.CasterScheduleBusiness;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.task.AbstractTask;

import java.util.ArrayList;
import java.util.List;

public class ReleaseSchedulableTask extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;

    private List<CasterScheduleDTO> schedules;

    @Override
    public String getId()
    {
        return SecurityCasting.MABE.ACTION.RELEASE_ENTRY;
    }

    public void setData( List<CasterScheduleDTO> schedules )
    {
        this.schedules = schedules;
    }

    @Override
    public void doWork() throws Exception
    {
        final CasterScheduleBusiness casterScheduleBusiness = businessManager.getSession( CasterScheduleBusiness.class );
        casterScheduleBusiness.releaseSchedulables( new ArrayList<>( schedules ) );
    }
}
