package com.hydro.casting.gui.locking.workflow.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.task.AbstractTask;
import com.hydro.casting.server.contract.locking.workflow.LockingWorkflowBusiness;
import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;

public class LWUpdateMaterialLockTypeTask extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;
    
    @Inject
    private NotifyManager notifyManager;

    private LockingWorkflowDTO lockingWorkflowDTO;
    private String newDefectTypeCat;

    @Override
    public void doWork() throws Exception
    {
        LockingWorkflowBusiness lockingWorkflowBusiness = businessManager.getSession(LockingWorkflowBusiness.class);
        lockingWorkflowBusiness.updateDefectTypeCat(lockingWorkflowDTO, newDefectTypeCat);
        
        notifyManager.showSplashMessage( "Der neue Sperrcode wurde eingetragen" );
    }

    public String getNewDefectTypeCat()
    {
        return newDefectTypeCat;
    }

    public void setNewDefectTypeCat(String newDefectTypeCat)
    {
        this.newDefectTypeCat = newDefectTypeCat;
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
        return SecurityCasting.LOCKING.ACTION.CHANGE_SCRAP_CODE;
    }
}
