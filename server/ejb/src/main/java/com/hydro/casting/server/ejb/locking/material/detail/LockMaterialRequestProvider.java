package com.hydro.casting.server.ejb.locking.material.detail;

import com.hydro.casting.server.contract.locking.material.dto.LockMaterialDTO;
import com.hydro.casting.server.contract.locking.material.dto.LockMaterialRequestDTO;
import com.hydro.casting.server.contract.locking.material.dto.LockableMaterialDTO;
import com.hydro.casting.server.model.mat.dao.MaterialLockLocationHome;
import com.hydro.casting.server.model.mat.dao.MaterialLockTypeHome;
import com.hydro.casting.server.model.res.dao.MachineHome;
import com.hydro.core.server.contract.workplace.DetailListProvider;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

@Stateless( name = "LockMaterialRequestProvider" )
public class LockMaterialRequestProvider implements DetailListProvider<LockableMaterialDTO, LockMaterialRequestDTO>
{
    @EJB
    private MaterialLockTypeHome materialLockTypeHome;

    @EJB
    private MaterialLockLocationHome materialLockLocationHome;

    @EJB
    private MachineHome machineHome;

    @Override
    public LockMaterialRequestDTO loadDetail( List<LockableMaterialDTO> masterList, Map<String, String> context )
    {
        if ( masterList == null || masterList.isEmpty() )
        {
            return null;
        }

        final LockMaterialRequestDTO lockOutputMaterialDTO = new LockMaterialRequestDTO();

        final LockMaterialDTO newLock = new LockMaterialDTO();
        lockOutputMaterialDTO.setNewLock( newLock );

        lockOutputMaterialDTO.setMaterialLockTypes( materialLockTypeHome.keyDescriptions() );
        lockOutputMaterialDTO.setMaterialLockLocations( materialLockLocationHome.keyDescriptions() );
        lockOutputMaterialDTO.setImposationMachines( machineHome.keyDescriptions() );

        newLock.setLockableMaterials( masterList );

        return lockOutputMaterialDTO;
    }

}
