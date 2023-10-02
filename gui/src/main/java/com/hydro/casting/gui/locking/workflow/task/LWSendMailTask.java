package com.hydro.casting.gui.locking.workflow.task;

import com.google.inject.Inject;
import com.hydro.casting.common.locking.workflow.LockingWorkflowConstants;
import com.hydro.casting.server.contract.locking.workflow.LockingWorkflowBusiness;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.ViewManager;

public class LWSendMailTask extends LWAbstractTask
{
    @Inject
    private BusinessManager businessManager;

    @Inject
    private ViewManager viewManager;

    @Override
    public void doWork() throws Exception
    {
        LockingWorkflowBusiness lockingWorkflowBusiness = businessManager.getSession( LockingWorkflowBusiness.class );
        lockingWorkflowBusiness.print( workOutlook( getTable().getSelectedValue() ) );
        viewManager.reloadCurrentView( getId() );
    }

    @Override
    public String getId()
    {
        return LockingWorkflowConstants.ACTION_ID.SEND_MAIL;
    }
}
