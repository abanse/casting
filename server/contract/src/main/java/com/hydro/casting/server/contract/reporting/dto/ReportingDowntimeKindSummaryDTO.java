package com.hydro.casting.server.contract.reporting.dto;

import com.hydro.casting.server.contract.downtime.dto.DowntimeKindDTO;
import com.hydro.core.server.contract.workplace.dto.ViewDTO;

public class ReportingDowntimeKindSummaryDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private long id;

    private DowntimeKindDTO downtimeKind;
    private int amount;
    private int durationInMinutes;

    @Override
    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

    public DowntimeKindDTO getDowntimeKind()
    {
        return downtimeKind;
    }

    public void setDowntimeKind( DowntimeKindDTO downtimeKind )
    {
        this.downtimeKind = downtimeKind;
    }

    public int getAmount()
    {
        return amount;
    }

    public void setAmount( int amount )
    {
        this.amount = amount;
    }

    public int getDurationInMinutes()
    {
        return durationInMinutes;
    }

    public void setDurationInMinutes( int durationInMinutes )
    {
        this.durationInMinutes = durationInMinutes;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + amount;
        result = prime * result + ( ( downtimeKind == null ) ? 0 : downtimeKind.hashCode() );
        result = prime * result + durationInMinutes;
        result = prime * result + (int) ( id ^ ( id >>> 32 ) );
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        ReportingDowntimeKindSummaryDTO other = (ReportingDowntimeKindSummaryDTO) obj;
        if ( amount != other.amount )
            return false;
        if ( downtimeKind == null )
        {
            if ( other.downtimeKind != null )
                return false;
        }
        else if ( !downtimeKind.equals( other.downtimeKind ) )
            return false;
        if ( durationInMinutes != other.durationInMinutes )
            return false;
        if ( id != other.id )
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "ReportingDowntimeKindSummaryDTO [id=" + id + ", downtimeKind=" + downtimeKind + ", amount=" + amount + ", durationInMinutes=" + durationInMinutes + ", toString()=" + super.toString()
                + "]";
    }
}
