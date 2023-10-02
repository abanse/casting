package com.hydro.casting.server.contract.locking.workflow;

import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;
import com.hydro.core.server.contract.workplace.ViewModel;

import javax.ejb.Remote;
import java.util.List;
import java.util.Map;

@Remote
public interface LockingWorkflowView
{
    List<LockingWorkflowDTO> loadLockingWorkflows( String filter, Map<String, Object> parameters );

    <S> S loadDetail( Class<S> dto, LockingWorkflowDTO currentLockingWorkflow );

    void refreshCache( List<LockingWorkflowDTO> lockingWorkflows );

    ViewModel<LockingWorkflowDTO> validateLockingWorkflow( LockingWorkflowDTO currentLockingWorkflow, boolean hasProdRole, boolean hasAVRole, boolean hasTCSRole, boolean hasQSRole );
}
