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

public class MoveCasterBatchPositionTask extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;

    private List<CasterSchedulePosDTO> sourcePositions;
    private List<CasterSchedulePosDTO> targetPositions;

    @Override
    public String getId()
    {
        return SecurityCasting.MABE.ACTION.EDIT_ENTRY;
    }

    public void setData( List<CasterSchedulePosDTO> sourcePositions, List<CasterSchedulePosDTO> targetPositions )
    {
        this.sourcePositions = sourcePositions;
        this.targetPositions = targetPositions;
    }

    @Override
    public void doWork() throws Exception
    {
        final CasterScheduleBusiness casterScheduleBusiness = businessManager.getSession( CasterScheduleBusiness.class );
        casterScheduleBusiness.moveCasterBatchPositions( new ArrayList<>(sourcePositions), new ArrayList<>(targetPositions) );
    }
}
