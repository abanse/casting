package com.hydro.casting.server.contract.locking.material.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.util.List;

public class LockMaterialDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private long id;
    private String materialLockType;
    private String materialLockLocation;
    private String imposationMachine;
    private String reason;
    private String userId;

    private List<LockableMaterialDTO> lockableMaterials;

    @Override
    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

    public String getMaterialLockType()
    {
        return materialLockType;
    }

    public void setMaterialLockType( String materialLockType )
    {
        this.materialLockType = materialLockType;
    }

    public String getMaterialLockLocation()
    {
        return materialLockLocation;
    }

    public void setMaterialLockLocation( String materialLockLocation )
    {
        this.materialLockLocation = materialLockLocation;
    }

    public String getImposationMachine()
    {
        return imposationMachine;
    }

    public void setImposationMachine( String imposationMachine )
    {
        this.imposationMachine = imposationMachine;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason( String reason )
    {
        this.reason = reason;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId( String userId )
    {
        this.userId = userId;
    }

    public List<LockableMaterialDTO> getLockableMaterials()
    {
        return lockableMaterials;
    }

    public void setLockableMaterials( List<LockableMaterialDTO> lockableMaterials )
    {
        this.lockableMaterials = lockableMaterials;
    }

    @Override
    public String toString()
    {
        return "LockMaterialDTO{" + "id=" + id + ", materialLockType='" + materialLockType + '\'' + ", materialLockLocation='" + materialLockLocation + '\'' + ", imposationMachine='"
                + imposationMachine + '\'' + ", reason='" + reason + '\'' + ", userId='" + userId + '\'' + ", lockableMaterials=" + lockableMaterials + '}';
    }
}
