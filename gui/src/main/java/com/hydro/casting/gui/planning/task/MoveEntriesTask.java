package com.hydro.casting.gui.planning.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.planning.CasterScheduleBusiness;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.task.AbstractTask;

import java.util.List;

public class MoveEntriesTask extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;

    @Inject
    private NotifyManager notifyManager;

    @Inject
    private SecurityManager securityManager;

    private String costCenter;

    private List<CasterScheduleDTO> sources;
    private CasterScheduleDTO target;
    private boolean afterRow;

    public String getCostCenter()
    {
        return costCenter;
    }

    public void setCostCenter( String costCenter )
    {
        this.costCenter = costCenter;
    }

    public void setData( List<CasterScheduleDTO> sources, CasterScheduleDTO target, boolean afterRow )
    {
        this.sources = sources;
        this.target = target;
        this.afterRow = afterRow;
    }

    @Override
    public void doWork() throws Exception
    {
        final CasterScheduleBusiness casterScheduleBusiness = businessManager.getSession( CasterScheduleBusiness.class );

        if ( sources.isEmpty() )
        {
            notifyManager.showInfoMessage( "Einträge verschieben", "Keine Daten gefunden" );
            return;
        }
        casterScheduleBusiness.moveSchedulables( securityManager.getCurrentUser(), costCenter, sources, target, afterRow );
    }

    @Override
    public String getId()
    {
        return SecurityCasting.MABE.ACTION.EDIT_ENTRY;
    }
}