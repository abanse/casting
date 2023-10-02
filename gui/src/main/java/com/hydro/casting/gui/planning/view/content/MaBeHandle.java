package com.hydro.casting.gui.planning.view.content;

import com.hydro.casting.server.contract.dto.MachineDTO;
public interface MaBeHandle
{
    MachineDTO getMachineDTO();

    String getCostCenter();

    MachineScheduleController getMachineScheduleController();

    PlanningDemandController getPlanningDemandController();

    void postValidationMessage( String validationMessage );

    void clearValidationMessage();
}
