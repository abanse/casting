package com.hydro.casting.server.contract.locking.material.dto;

import com.hydro.core.server.contract.workplace.dto.KeyDescriptionDTO;
import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.util.List;

public class LockMaterialRequestDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private long id;

    private LockMaterialDTO newLock;

    private List<KeyDescriptionDTO> materialLockTypes;
    private List<KeyDescriptionDTO> materialLockLocations;
    private List<KeyDescriptionDTO> imposationMachines;

    @Override
    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

    public List<KeyDescriptionDTO> getMaterialLockTypes()
    {
        return materialLockTypes;
    }

    public void setMaterialLockTypes( List<KeyDescriptionDTO> materialLockTypes )
    {
        this.materialLockTypes = materialLockTypes;
    }

    public List<KeyDescriptionDTO> getMaterialLockLocations()
    {
        return materialLockLocations;
    }

    public void setMaterialLockLocations( List<KeyDescriptionDTO> materialLockLocations )
    {
        this.materialLockLocations = materialLockLocations;
    }

    public List<KeyDescriptionDTO> getImposationMachines()
    {
        return imposationMachines;
    }

    public void setImposationMachines( List<KeyDescriptionDTO> imposationMachines )
    {
        this.imposationMachines = imposationMachines;
    }

    public LockMaterialDTO getNewLock()
    {
        return newLock;
    }

    public void setNewLock( LockMaterialDTO newLock )
    {
        this.newLock = newLock;
    }
}
