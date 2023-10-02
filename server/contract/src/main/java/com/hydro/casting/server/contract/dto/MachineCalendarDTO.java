package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.time.LocalDateTime;
import java.util.Objects;

public class MachineCalendarDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private long id;

    private LocalDateTime startTS;
    private int duration;
    private String description;
    private String machine;

    @Override
    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

    public LocalDateTime getStartTS()
    {
        return startTS;
    }

    public void setStartTS( LocalDateTime startTS )
    {
        this.startTS = startTS;
    }

    public int getDuration()
    {
        return duration;
    }

    public void setDuration( int duration )
    {
        this.duration = duration;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getMachine()
    {
        return machine;
    }

    public void setMachine( String machine )
    {
        this.machine = machine;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        MachineCalendarDTO that = (MachineCalendarDTO) o;
        return id == that.id && duration == that.duration && Objects.equals( startTS, that.startTS ) && Objects.equals( description, that.description ) && Objects.equals( machine, that.machine );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, startTS, duration, description, machine );
    }

    @Override
    public String toString()
    {
        return "MachineCalendarDTO{" + "id=" + id + ", startTS=" + startTS + ", duration=" + duration + ", description='" + description + '\'' + ", machine='" + machine + '\'' + '}';
    }
}
