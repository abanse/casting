package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class TimeManagementPhaseDTO implements ViewDTO
{
    private String name;
    private Duration actualDuration;
    private Duration plannedDuration;
    private LocalDateTime start;

    @Override
    public long getId()
    {
        if ( name != null )
        {
            return name.hashCode();
        }
        return 0;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public Duration getActualDuration()
    {
        return actualDuration;
    }

    public void setActualDuration( Duration actualDuration )
    {
        this.actualDuration = actualDuration;
    }

    public Duration getPlannedDuration()
    {
        return plannedDuration;
    }

    public void setPlannedDuration( Duration plannedDuration )
    {
        this.plannedDuration = plannedDuration;
    }

    public LocalDateTime getStart()
    {
        return start;
    }

    public void setStart( LocalDateTime start )
    {
        this.start = start;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        TimeManagementPhaseDTO that = (TimeManagementPhaseDTO) o;
        return Objects.equals( name, that.name ) && Objects.equals( actualDuration, that.actualDuration ) && Objects.equals( plannedDuration, that.plannedDuration ) && Objects.equals( start,
                that.start );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, actualDuration, plannedDuration, start );
    }
}
