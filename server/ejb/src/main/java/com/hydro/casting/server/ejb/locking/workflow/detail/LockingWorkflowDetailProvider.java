package com.hydro.casting.server.ejb.locking.workflow.detail;

import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;

import javax.ejb.Local;

@Local
public interface LockingWorkflowDetailProvider<S>
{
    S loadData( LockingWorkflowDTO lockingWorkflowDTO );
}
