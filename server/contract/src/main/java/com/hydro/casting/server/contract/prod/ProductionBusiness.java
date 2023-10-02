package com.hydro.casting.server.contract.prod;

import com.hydro.casting.common.constant.CasterStep;
import com.hydro.casting.common.constant.FurnaceStep;
import com.hydro.core.common.exception.BusinessException;

import javax.ejb.Remote;
import java.time.LocalDateTime;

@Remote
public interface ProductionBusiness
{
    void activateCharge( String currentUser, String machine, long castingBatchOID ) throws BusinessException;

    void deactivateCharge( String currentUser, String machine, long castingBatchOID ) throws BusinessException;

    void startFurnaceStep( String currentUser, String machine, long castingBatchOID, FurnaceStep furnaceStep, LocalDateTime startTS ) throws BusinessException;

    void releaseFurnace( String currentUser, String machine, long castingBatchOID, LocalDateTime startTS ) throws BusinessException;

    void startCasting( String currentUser, String machine, long castingBatchOID, LocalDateTime startTS ) throws BusinessException;

    void endCasting( String currentUser, String machine, long castingBatchOID, LocalDateTime startTS ) throws BusinessException;

    void unloadSlabs( String currentUser, long castingBatchOID, int castingLength ) throws BusinessException;
}
