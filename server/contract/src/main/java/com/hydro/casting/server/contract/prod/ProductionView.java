package com.hydro.casting.server.contract.prod;

import com.hydro.casting.server.contract.dto.*;
import com.hydro.core.server.contract.workplace.MasterDetailProvider;
import com.hydro.core.server.contract.workplace.SearchType;
import com.hydro.core.server.contract.workplace.ViewModel;

import javax.ejb.Remote;
import java.util.List;
import java.util.Map;

@Remote
public interface ProductionView extends MasterDetailProvider<CastingInstructionDTO>
{
    List<CasterScheduleDTO> loadBySearchType( SearchType searchType, Map<String, Object> parameters );

    List<ProductionLogDTO> loadCurrentProductionLog( String machineApk );

    List<UnloadSlabDTO> loadUnloadSlabs( long castingBatchOID);

    ViewModel<FurnaceInstructionDTO> validate( String currentUser, FurnaceInstructionDTO furnaceInstructionDTO );

    ViewModel<CasterInstructionDTO> validate( String currentUser, CasterInstructionDTO casterInstructionDTO );
}
