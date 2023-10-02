package com.hydro.casting.server.contract.dto;

import com.hydro.casting.server.contract.downtime.dto.DowntimeRequestDTO;
import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.time.LocalDateTime;
import java.util.Objects;

public class TimeManagementViolationDTO implements ViewDTO
{
    private String charge;
    private String name;
    private String type;
    private LocalDateTime plannedStart;
    private LocalDateTime plannedEnd;
    private boolean checked;
    private DowntimeRequestDTO downtimeRequest;

    @Override
    public long getId()
    {
        return Objects.hash( charge, name );
    }

    public String getCharge()
    {
        return charge;
    }

    public void setCharge( String charge )
    {
        this.charge = charge;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public LocalDateTime getPlannedStart()
    {
        return plannedStart;
    }

    public void setPlannedStart( LocalDateTime plannedStart )
    {
        this.plannedStart = plannedStart;
    }

    public LocalDateTime getPlannedEnd()
    {
        return plannedEnd;
    }

    public void setPlannedEnd( LocalDateTime plannedEnd )
    {
        this.plannedEnd = plannedEnd;
    }

    public boolean isChecked()
    {
        return checked;
    }

    public void setChecked( boolean checked )
    {
        this.checked = checked;
    }

    public DowntimeRequestDTO getDowntimeRequest()
    {
        return downtimeRequest;
    }

    public void setDowntimeRequest( DowntimeRequestDTO downtimeRequest )
    {
        this.downtimeRequest = downtimeRequest;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( !( o instanceof TimeManagementViolationDTO ) )
            return false;
        TimeManagementViolationDTO that = (TimeManagementViolationDTO) o;
        return checked == that.checked && Objects.equals( charge, that.charge ) && Objects.equals( name, that.name ) && Objects.equals( type, that.type ) && Objects.equals( plannedStart,
                that.plannedStart ) && Objects.equals( plannedEnd, that.plannedEnd ) && Objects.equals( downtimeRequest, that.downtimeRequest );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( charge, name, type, plannedStart, plannedEnd, checked, downtimeRequest );
    }
}
