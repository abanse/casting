package com.hydro.casting.server.contract.locking.material.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;
public interface LockableMaterialDTO extends ViewDTO
{
    String getMaterialName();
}
