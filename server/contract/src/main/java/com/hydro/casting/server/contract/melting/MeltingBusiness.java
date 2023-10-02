package com.hydro.casting.server.contract.melting;

import com.hydro.casting.common.constant.MelterStep;
import com.hydro.core.common.exception.BusinessException;

import javax.ejb.Remote;
import java.time.LocalDateTime;

@Remote
public interface MeltingBusiness
{
    void activateCharge( String currentUser, String machine, long castingBatchOID ) throws BusinessException;

    boolean activateNextCharge( String currentUser, String machine ) throws BusinessException;

    void deactivateCharge( String currentUser, String machine, long meltingBatchOID ) throws BusinessException;

    void createNewMeltingCharge( String melterApk, String alloy ) throws BusinessException;

    void changeChargeCounterOnMelter( String melterApk, int newChargeCounter ) throws BusinessException;

    void startMeltingStepWithoutChangingMachine( String currentUser, String melterApk, long meltingBatchOID, MelterStep melterStep, LocalDateTime stepStartTS ) throws BusinessException;

    void finishMeltingStepWithoutChangingMachine( String currentUser, String melterApk, long meltingBatchOID, MelterStep melterStep, LocalDateTime stepStartTS ) throws BusinessException;

    void startMeltingStep( String currentUser, String melterApk, long meltingBatchOID, MelterStep melterStep, LocalDateTime stepStartTS ) throws BusinessException;

    void startMeltingStepAndFinishOther( String currentUser, String melterApk, long meltingBatchOID, MelterStep melterStep, MelterStep other, LocalDateTime stepStartTS ) throws BusinessException;

    void finishCharge( String currentUser, String melterApk, long meltingBatchOID ) throws BusinessException;
}
