package com.hydro.casting.gui.locking.workflow.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.server.contract.locking.workflow.LockingWorkflowBusiness;
import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.task.AbstractTask;

public class LWUpdateMaterialLockLocationTask extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;
    
    @Inject
    private NotifyManager notifyManager;

    private LockingWorkflowDTO lockingWorkflowDTO;
    private String newMaterialLockLocation;

    @Override
    public void doWork() throws Exception
    {
        LockingWorkflowBusiness lockingWorkflowBusiness = businessManager.getSession(LockingWorkflowBusiness.class);
        lockingWorkflowBusiness.updateMaterialLockLocation(lockingWorkflowDTO, newMaterialLockLocation);
        
        notifyManager.showSplashMessage( "Der neue Fehlerort wurde eingetragen" );
    }

    public String getNewMaterialLockLocation()
    {
        return newMaterialLockLocation;
    }

    public void setNewMaterialLockLocation( String newMaterialLockLocation )
    {
        this.newMaterialLockLocation = newMaterialLockLocation;
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
        return SecurityCasting.LOCKING.ACTION.CHANGE_MATERIAL_LOCK_LOCATION;
    }
}
