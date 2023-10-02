package com.hydro.casting.server.contract.planning;

import com.hydro.casting.server.contract.dto.CasterDemandDTO;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.CasterSchedulePosDTO;
import com.hydro.casting.server.contract.dto.SetupTypeDTO;
import com.hydro.core.common.exception.BusinessException;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface CasterScheduleBusiness
{
    void setCasterBatchPositions( List<CasterDemandDTO> demands, List<CasterSchedulePosDTO> schedulePoss, Integer count ) throws BusinessException;

    void moveCasterBatchPositions( List<CasterSchedulePosDTO> sourcePositions, List<CasterSchedulePosDTO> targetPositions ) throws BusinessException;

    void deleteCasterBatchPositions( List<CasterSchedulePosDTO> schedulePoss ) throws BusinessException;

    void createCasterBatch( String machineApk, List<CasterDemandDTO> demands, CasterScheduleDTO refCasterSchedule, boolean after, Integer count ) throws BusinessException;

    void copyCasterBatch( CasterScheduleDTO casterSchedule, int countCopies ) throws BusinessException;

    void updateProcessOrder( CasterScheduleDTO casterScheduleDTO, String newProcessOrder ) throws BusinessException;

    void insertSetup( String currentUser, String machineApk, SetupTypeDTO setupType, CasterScheduleDTO beforeEntry ) throws BusinessException;

    void deleteSetups( List<CasterScheduleDTO> setups ) throws BusinessException;

    void updateAnnotation( CasterScheduleDTO casterScheduleDTO, String newAnnotation ) throws BusinessException;

    void moveSchedulables( String currentUser, String machineApk, List<CasterScheduleDTO> sources, CasterScheduleDTO target, boolean afterRow ) throws BusinessException;

    void saveLastCharge( String machineApk, int lastCharge ) throws BusinessException;

    void releaseSchedulables( List<CasterScheduleDTO> schedules ) throws BusinessException;

    void updateAlloySourcePercentage( CasterScheduleDTO casterScheduleDTO, String alloySource, int newPercentage ) throws BusinessException;

    void updateDuration( CasterScheduleDTO schedule, boolean forCasting, Long newDuration ) throws BusinessException;

    long createPlannedConsumedMaterial( CasterScheduleDTO schedule, String source, double weight ) throws BusinessException;

    void deletePlannedConsumedMaterial( CasterScheduleDTO schedule, long plannedConsumedMaterialOID ) throws BusinessException;

    void overwriteChargeValues( CasterScheduleDTO schedule, long castingSequence, String charge, String furnace ) throws BusinessException;

    void changeExecutionState( CasterScheduleDTO schedule, int executionState, Long timestamp ) throws BusinessException;

    void reorganizeCharge( CasterScheduleDTO schedule ) throws BusinessException;
}
