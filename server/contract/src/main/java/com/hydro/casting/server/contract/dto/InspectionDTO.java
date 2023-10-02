package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.util.ArrayList;
import java.util.List;

public abstract class InspectionDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private CasterScheduleDTO casterSchedule;
    private List<InspectionRuleDTO> rules;
    private List<InspectionValueDTO> values;

    public CasterScheduleDTO getCasterSchedule()
    {
        return casterSchedule;
    }

    public void setCasterSchedule( CasterScheduleDTO casterSchedule )
    {
        this.casterSchedule = casterSchedule;
    }

    public List<InspectionRuleDTO> getRules()
    {
        if ( rules == null )
        {
            rules = new ArrayList<>();
        }
        return rules;
    }

    public void setRules( List<InspectionRuleDTO> rules )
    {
        this.rules = rules;
    }

    public List<InspectionValueDTO> getValues()
    {
        if ( values == null )
        {
            values = new ArrayList<>();
        }
        return values;
    }

    public void setValues( List<InspectionValueDTO> values )
    {
        this.values = values;
    }
}
