package com.hydro.casting.server.ejb.locking.material.service;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.model.mat.Material;
import com.hydro.casting.server.model.mat.MaterialLock;
import com.hydro.casting.server.model.mat.MaterialLockLocation;
import com.hydro.casting.server.model.mat.MaterialLockType;
import com.hydro.casting.server.model.mat.dao.MaterialLockHome;
import com.hydro.casting.server.model.mat.dao.MaterialLockLocationHome;
import com.hydro.casting.server.model.mat.dao.MaterialLockTypeHome;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDateTime;
@Stateless
public class MaterialLockService
{
    @EJB
    private MaterialLockHome materialLockHome;
    @EJB
    private MaterialLockTypeHome materialLockTypeHome;
    @EJB
    private MaterialLockLocationHome materialLockLocationHome;

    public void lockMaterial( Material material, String materialLockTypeApk, String materialLockLocationApk, String imposationMachine, String imposator, String comment )
    {
        final MaterialLockType materialLockType = materialLockTypeHome.findByApk( materialLockTypeApk );
        final MaterialLockLocation materialLockLocation = materialLockLocationHome.findByApk( materialLockLocationApk );
        lockMaterial( material, materialLockType, materialLockLocation, imposationMachine, imposator, comment );
    }

    public void lockMaterial( Material material, MaterialLockType materialLockType, MaterialLockLocation materialLockLocation, String imposationMachine, String imposator, String comment )
    {
        final MaterialLock materialLock = new MaterialLock();
        materialLock.setName( material.getName() );
        materialLock.setMaterial( material );
        materialLock.setActive( true );
        materialLock.setMaterialLockType( materialLockType );
        materialLock.setMaterialLockLocation( materialLockLocation );
        materialLock.setImposationMachine( imposationMachine );
        materialLock.setImposator( imposator );
        materialLock.setImposationComment( comment );
        materialLock.setImposation( LocalDateTime.now() );
        materialLock.setProdStartTs( LocalDateTime.now() );
        materialLock.setMaterialStatus( Casting.LOCKING_WORKFLOW.BLOCK_MARK );

        materialLockHome.persist( materialLock );
    }
}
