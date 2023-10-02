package com.hydro.casting.gui.prod.control;

import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.InspectionRuleDTO;
import com.hydro.casting.server.contract.dto.InspectionValueDTO;
import javafx.beans.property.BooleanProperty;

public interface InspectionController
{
    void setInspection( CasterScheduleDTO casterScheduleDTO, InspectionRuleDTO inspectionRule, InspectionValueDTO inspectionValue );

    BooleanProperty editableProperty();

    InspectionValueDTO getValue();
}
