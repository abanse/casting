package com.hydro.casting.server.contract.reporting.dto;

import com.hydro.casting.server.contract.downtime.dto.DowntimeKindDTO;

import java.time.LocalDateTime;
import java.util.Date;

public class ReportingDowntimeDetailDTO extends ReportingDTO
{
    private static final long serialVersionUID = 1L;

    private long id;

    private LocalDateTime start;
    private LocalDateTime end;
    private DowntimeKindDTO downtimeKind;
    private String shift;
    private String description;

    @Override
    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

    public LocalDateTime getStart()
    {
        return start;
    }

    public void setStart( LocalDateTime start )
    {
        this.start = start;
    }

    public LocalDateTime getEnd()
    {
        return end;
    }

    public void setEnd( LocalDateTime end )
    {
        this.end = end;
    }

    public DowntimeKindDTO getDowntimeKind()
    {
        return downtimeKind;
    }

    public void setDowntimeKind( DowntimeKindDTO downtimeKind )
    {
        this.downtimeKind = downtimeKind;
    }

    public String getShift()
    {
        return shift;
    }

    public void setShift( String shift )
    {
        this.shift = shift;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ( ( description == null ) ? 0 : description.hashCode() );
        result = prime * result + ( ( downtimeKind == null ) ? 0 : downtimeKind.hashCode() );
        result = prime * result + ( ( end == null ) ? 0 : end.hashCode() );
        result = prime * result + (int) ( id ^ ( id >>> 32 ) );
        result = prime * result + ( ( shift == null ) ? 0 : shift.hashCode() );
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
        ReportingDowntimeDetailDTO other = (ReportingDowntimeDetailDTO) obj;
        if ( description == null )
        {
            if ( other.description != null )
                return false;
        }
        else if ( !description.equals( other.description ) )
            return false;
        if ( downtimeKind == null )
        {
            if ( other.downtimeKind != null )
                return false;
        }
        else if ( !downtimeKind.equals( other.downtimeKind ) )
            return false;
        if ( end == null )
        {
            if ( other.end != null )
                return false;
        }
        else if ( !end.equals( other.end ) )
            return false;
        if ( id != other.id )
            return false;
        if ( shift == null )
        {
            if ( other.shift != null )
                return false;
        }
        else if ( !shift.equals( other.shift ) )
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
        return "ReportingDowntimeDetailDTO [id=" + id + ", start=" + start + ", end=" + end + ", downtimeKind=" + downtimeKind + ", shift=" + shift + ", description="
                + description + ", toString()=" + super.toString() + "]";
    }
}
