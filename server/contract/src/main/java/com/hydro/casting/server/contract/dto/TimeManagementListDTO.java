package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TimeManagementListDTO implements ViewDTO
{
    private List<TimeManagementDTO> timeManagements;

    public TimeManagementListDTO()
    {
    }

    public TimeManagementListDTO( List<TimeManagementDTO> timeManagements )
    {
        this.timeManagements = timeManagements;
    }

    @Override
    public long getId()
    {
        return Objects.hash( timeManagements );
    }

    public List<TimeManagementDTO> getTimeManagements()
    {
        if ( timeManagements == null )
        {
            timeManagements = new ArrayList<>();
        }

        return timeManagements;
    }

    public void setTimeManagements( List<TimeManagementDTO> timeManagements )
    {
        this.timeManagements = timeManagements;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        TimeManagementListDTO that = (TimeManagementListDTO) o;
        return Objects.equals( timeManagements, that.timeManagements );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( timeManagements );
    }
}
