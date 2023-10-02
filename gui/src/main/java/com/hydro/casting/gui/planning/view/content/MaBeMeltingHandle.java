package com.hydro.casting.gui.planning.view.content;

import com.hydro.casting.server.contract.dto.MachineDTO;

public interface MaBeMeltingHandle
{
    MachineDTO getMachineDTO();

    String getCostCenter();

    MachineMeltingScheduleController getMachineMeltingScheduleController();

    PlanningMeltingDemandController getPlanningMeltingDemandController();

    void postValidationMessage( String validationMessage );

    void clearValidationMessage();
}
