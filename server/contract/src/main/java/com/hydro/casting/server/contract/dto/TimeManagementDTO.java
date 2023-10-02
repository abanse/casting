package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TimeManagementDTO implements ViewDTO
{
    private String machine;
    private String charge;
    private LocalDateTime plannedStart;
    private List<TimeManagementPhaseDTO> phases;

    @Override
    public long getId()
    {
        return Objects.hash( machine, charge );
    }

    public String getMachine()
    {
        return machine;
    }

    public void setMachine( String machine )
    {
        this.machine = machine;
    }

    public String getCharge()
    {
        return charge;
    }

    public void setCharge( String charge )
    {
        this.charge = charge;
    }

    public LocalDateTime getPlannedStart()
    {
        return plannedStart;
    }

    public void setPlannedStart( LocalDateTime plannedStart )
    {
        this.plannedStart = plannedStart;
    }

    public List<TimeManagementPhaseDTO> getPhases()
    {
        if ( phases == null )
        {
            phases = new ArrayList<>();
        }

        return phases;
    }

    public void setPhases( List<TimeManagementPhaseDTO> phases )
    {
        this.phases = phases;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        TimeManagementDTO that = (TimeManagementDTO) o;
        return Objects.equals( charge, that.charge ) && Objects.equals( plannedStart, that.plannedStart ) && Objects.equals( phases, that.phases );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( charge, plannedStart, phases );
    }
}
