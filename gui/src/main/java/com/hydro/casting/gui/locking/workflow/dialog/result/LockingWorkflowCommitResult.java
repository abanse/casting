package com.hydro.casting.gui.locking.workflow.dialog.result;

import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;

import java.util.List;

public class LockingWorkflowCommitResult
{
    private List<LockingWorkflowDTO> selectedLockingWorkflows;
    private String message;

    public List<LockingWorkflowDTO> getSelectedLockingWorkflows()
    {
        return selectedLockingWorkflows;
    }

    public void setSelectedLockingWorkflows( List<LockingWorkflowDTO> selectedLockingWorkflows )
    {
        this.selectedLockingWorkflows = selectedLockingWorkflows;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }
}
