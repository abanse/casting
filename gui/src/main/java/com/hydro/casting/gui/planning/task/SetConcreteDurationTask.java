package com.hydro.casting.gui.planning.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.planning.CasterScheduleBusiness;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.task.AbstractTask;

public class SetConcreteDurationTask extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;

    private CasterScheduleDTO schedule;
    private Long newDuration;
    private boolean forCasting;

    @Override
    public String getId()
    {
        return SecurityCasting.MABE.ACTION.EDIT_ENTRY;
    }

    public void setData( CasterScheduleDTO schedule, Long newDuration, boolean forCasting )
    {
        this.schedule = schedule;
        this.newDuration = newDuration;
        this.forCasting = forCasting;
    }

    @Override
    public void doWork() throws Exception
    {
        final CasterScheduleBusiness casterScheduleBusiness = businessManager.getSession( CasterScheduleBusiness.class );
        casterScheduleBusiness.updateDuration( schedule, forCasting, newDuration );
    }
}
