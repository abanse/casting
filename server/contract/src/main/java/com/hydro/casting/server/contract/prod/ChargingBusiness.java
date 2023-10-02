package com.hydro.casting.server.contract.prod;

import com.hydro.casting.server.contract.dto.MaterialAnalysisElementDTO;
import com.hydro.casting.server.contract.dto.MaterialDTO;
import com.hydro.core.common.exception.BusinessException;

import javax.ejb.Remote;
import java.util.List;
@Remote
public interface ChargingBusiness
{
    void saveChargingMaterials( String user, long castingBatchOID, List<MaterialDTO> furnaceContentMaterials, List<MaterialDTO> chargingMaterials ) throws BusinessException;

    void sendChargingSpecification( String user, long castingBatchOID ) throws BusinessException;
}
