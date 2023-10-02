package com.hydro.casting.gui.locking.workflow.task;

import com.hydro.casting.common.locking.workflow.LockingWorkflowConstants;
import com.hydro.casting.server.contract.locking.workflow.LockingWorkflowBusiness;

public class LWMoveToContainerTask extends LWAbstractTask
{
    @Override
    public void doWork() throws Exception
    {
        work( LockingWorkflowBusiness.Function.MoveToContainer );
    }

    @Override
    public String getId()
    {
        return LockingWorkflowConstants.ACTION_ID.MOVE_TO_CONTAINER;
    }
}
