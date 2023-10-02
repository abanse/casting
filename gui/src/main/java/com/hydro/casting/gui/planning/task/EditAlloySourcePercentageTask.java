package com.hydro.casting.gui.planning.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.planning.CasterScheduleBusiness;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.task.AbstractTask;

public class EditAlloySourcePercentageTask extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;

    private CasterScheduleDTO casterScheduleDTO;
    private String alloySource;
    private int newPercentage;

    @Override
    public String getId()
    {
        return SecurityCasting.MABE.ACTION.EDIT_ENTRY;
    }

    public void setData( CasterScheduleDTO casterScheduleDTO, String alloySource, int newPercentage )
    {
        this.casterScheduleDTO = casterScheduleDTO;
        this.alloySource = alloySource;
        this.newPercentage = newPercentage;
    }

    @Override
    public void doWork() throws Exception
    {
        final CasterScheduleBusiness casterScheduleBusiness = businessManager.getSession( CasterScheduleBusiness.class );
        casterScheduleBusiness.updateAlloySourcePercentage( casterScheduleDTO, alloySource, newPercentage );
    }
}
