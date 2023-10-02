package com.hydro.casting.gui.planning.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.planning.CasterScheduleBusiness;
import com.hydro.core.common.util.StringTools;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.task.AbstractTask;

public class EditProcessOrderTask extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;

    private CasterScheduleDTO casterScheduleDTO;
    private String newProcessOrder;

    @Override
    public String getId()
    {
        return SecurityCasting.MABE.ACTION.EDIT_ENTRY;
    }

    public void setData( CasterScheduleDTO casterScheduleDTO, String newProcessOrder )
    {
        this.casterScheduleDTO = casterScheduleDTO;
        this.newProcessOrder = newProcessOrder;
    }

    @Override
    public void doWork() throws Exception
    {
        final CasterScheduleBusiness casterScheduleBusiness = businessManager.getSession( CasterScheduleBusiness.class );
        casterScheduleBusiness.updateProcessOrder( casterScheduleDTO, newProcessOrder );
        if ( StringTools.isNullOrEmpty( newProcessOrder ) )
        {
            casterScheduleDTO.setProcessOrder( null );
        }
        else
        {
            casterScheduleDTO.setProcessOrder( newProcessOrder );
        }
    }
}
