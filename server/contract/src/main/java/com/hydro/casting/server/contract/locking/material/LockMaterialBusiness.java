package com.hydro.casting.server.contract.locking.material;

import com.hydro.casting.server.contract.locking.material.dto.LockMaterialDTO;
import com.hydro.casting.server.contract.locking.material.dto.LockMaterialRequestDTO;
import com.hydro.core.common.exception.BusinessException;

import javax.ejb.Remote;

@Remote
public interface LockMaterialBusiness
{
    void lockMaterial( LockMaterialDTO lockMaterial ) throws BusinessException;
}
