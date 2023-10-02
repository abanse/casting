package com.hydro.casting.server.ejb.locking.material;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.dto.SlabDTO;
import com.hydro.casting.server.contract.locking.material.LockMaterialBusiness;
import com.hydro.casting.server.contract.locking.material.dto.LockMaterialDTO;
import com.hydro.casting.server.contract.locking.material.dto.LockableMaterialDTO;
import com.hydro.casting.server.ejb.locking.material.service.MaterialLockService;
import com.hydro.casting.server.ejb.main.service.AlloyService;
import com.hydro.casting.server.ejb.main.service.MaterialService;
import com.hydro.casting.server.ejb.stock.service.SlabService;
import com.hydro.casting.server.model.mat.Alloy;
import com.hydro.casting.server.model.mat.Slab;
import com.hydro.casting.server.model.mat.dao.SlabHome;
import com.hydro.core.common.exception.BusinessException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Stateless
public class LockMaterialBusinessBean implements LockMaterialBusiness
{
    @EJB
    private SlabHome slabHome;

    @EJB
    private MaterialService materialService;

    @EJB
    private MaterialLockService materialLockService;

    @EJB
    private AlloyService alloyService;

    @EJB
    private SlabService slabService;

    @Override
    public void lockMaterial( LockMaterialDTO lockMaterial ) throws BusinessException
    {
        if ( lockMaterial == null || lockMaterial.getLockableMaterials() == null )
        {
            return;
        }
        final List<Slab> lockedSlabs = new ArrayList<>();
        for ( LockableMaterialDTO lockableMaterial : lockMaterial.getLockableMaterials() )
        {
            List<Slab> slabs = slabHome.findActiveByName( lockableMaterial.getMaterialName() );
            if ( slabs.isEmpty() )
            {
                final Slab newSlab = materialService.createSlab( lockableMaterial.getMaterialName(), Casting.SCHEDULABLE_STATE.SUCCESS, Casting.SCHEDULABLE_STATE.UNPLANNED );
                if ( lockableMaterial instanceof SlabDTO )
                {
                    final SlabDTO slabDTO = (SlabDTO) lockableMaterial;
                    newSlab.setWeight( slabDTO.getWeight() );
                    newSlab.setWidth( slabDTO.getWidth() );
                    newSlab.setHeight( slabDTO.getHeight() );
                    newSlab.setLength( slabDTO.getLength() );
                    final Alloy alloy = alloyService.findOrCreateAlloy( slabDTO.getAlloy() );
                    newSlab.setAlloy( alloy );
                }
                slabs = Collections.singletonList( newSlab );
            }
            final Slab slab = slabs.get( 0 );
            materialLockService.lockMaterial( slab, lockMaterial.getMaterialLockType(), lockMaterial.getMaterialLockLocation(), lockMaterial.getImposationMachine(), lockMaterial.getUserId(),
                    lockMaterial.getReason() );
            lockedSlabs.add( slab );
        }
        slabService.replicateCache( lockedSlabs );
    }
}
