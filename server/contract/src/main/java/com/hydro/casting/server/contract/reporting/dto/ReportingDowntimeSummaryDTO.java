package com.hydro.casting.server.contract.reporting.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ReportingDowntimeSummaryDTO extends ReportingDTO
{
    private static final long serialVersionUID = 1L;

    private LocalDateTime start;
    private LocalDateTime end;
    private int downtimeInMinutes;
    private double availability;
    private List<ReportingDowntimeKindSummaryDTO> downtimeKindSummary;

    public LocalDateTime getStart()
    {
        if ( start == null )
        {
            return LocalDateTime.now().minusHours( 24 );
        }
        return start;
    }

    public void setStart( LocalDateTime start )
    {
        this.start = start;
    }

    public LocalDateTime getEnd()
    {
        if ( end == null )
        {
            return LocalDateTime.now();
        }
        return end;
    }

    public void setEnd( LocalDateTime end )
    {
        this.end = end;
    }

    public int getDowntimeInMinutes()
    {
        return downtimeInMinutes;
    }

    public void setDowntimeInMinutes( int downtimeInMinutes )
    {
        this.downtimeInMinutes = downtimeInMinutes;
    }

    public double getAvailability()
    {
        return availability;
    }

    public void setAvailability( double availability )
    {
        this.availability = availability;
    }

    public List<ReportingDowntimeKindSummaryDTO> getDowntimeKindSummary()
    {
        return downtimeKindSummary;
    }

    public void setDowntimeKindSummary( List<ReportingDowntimeKindSummaryDTO> downtimeKindSummary )
    {
        this.downtimeKindSummary = downtimeKindSummary;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits( availability );
        result = prime * result + (int) ( temp ^ ( temp >>> 32 ) );
        result = prime * result + downtimeInMinutes;
        result = prime * result + ( ( downtimeKindSummary == null ) ? 0 : downtimeKindSummary.hashCode() );
        result = prime * result + ( ( end == null ) ? 0 : end.hashCode() );
        result = prime * result + ( ( start == null ) ? 0 : start.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( !super.equals( obj ) )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        ReportingDowntimeSummaryDTO other = (ReportingDowntimeSummaryDTO) obj;
        if ( Double.doubleToLongBits( availability ) != Double.doubleToLongBits( other.availability ) )
            return false;
        if ( downtimeInMinutes != other.downtimeInMinutes )
            return false;
        if ( downtimeKindSummary == null )
        {
            if ( other.downtimeKindSummary != null )
                return false;
        }
        else if ( !downtimeKindSummary.equals( other.downtimeKindSummary ) )
            return false;
        if ( end == null )
        {
            if ( other.end != null )
                return false;
        }
        else if ( !end.equals( other.end ) )
            return false;
        if ( start == null )
        {
            if ( other.start != null )
                return false;
        }
        else if ( !start.equals( other.start ) )
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "ReportingDowntimeSummaryDTO [start=" + start + ", end=" + end + ", downtimeInMinutes=" + downtimeInMinutes + ", availability=" + availability + ", downtimeKindSummary="
                + downtimeKindSummary + ", toString()=" + super.toString() + "]";
    }
}
