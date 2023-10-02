package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

public interface CastingInstructionDTO extends ViewDTO
{
    String getMachineApk();

    Long getCastingBatchOID();

    String getCharge();
}
