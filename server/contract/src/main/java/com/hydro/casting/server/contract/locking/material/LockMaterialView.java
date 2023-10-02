package com.hydro.casting.server.contract.locking.material;

import com.hydro.casting.server.contract.locking.material.dto.LockableMaterialDTO;
import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;
import com.hydro.core.server.contract.workplace.MasterDetailProvider;
import com.hydro.core.server.contract.workplace.MasterListDetailProvider;
import com.hydro.core.server.contract.workplace.ViewModel;

import javax.ejb.Remote;
import java.util.List;
import java.util.Map;

@Remote
public interface LockMaterialView extends MasterListDetailProvider<LockableMaterialDTO>
{
}
