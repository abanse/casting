package com.hydro.casting.gui.locking.workflow.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.server.contract.locking.workflow.LockingWorkflowBusiness;
import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.task.AbstractTask;

public class LWCauserUpdateTask extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;

    @Inject
    private NotifyManager notifyManager;

    private LockingWorkflowDTO lockingWorkflowDTO;
    private String newCauser;

    @Override
    public void doWork() throws Exception
    {
        LockingWorkflowBusiness lockingWorkflowBusiness = businessManager.getSession( LockingWorkflowBusiness.class );
        lockingWorkflowBusiness.updateCauser( lockingWorkflowDTO, newCauser );

        notifyManager.showSplashMessage( "Der neue Verursacher wurde eingetragen" );
    }

    public String getNewCauser()
    {
        return newCauser;
    }

    public void setNewCauser( String newCauser )
    {
        this.newCauser = newCauser;
    }

    public LockingWorkflowDTO getLockingWorkflowDTO()
    {
        return lockingWorkflowDTO;
    }

    public void setLockingWorkflowDTO( LockingWorkflowDTO lockingWorkflowDTO )
    {
        this.lockingWorkflowDTO = lockingWorkflowDTO;
    }

    @Override
    public String getId()
    {
        return SecurityCasting.LOCKING.ACTION.CHANGE_INITIATOR;
    }
}
