package com.hydro.casting.server.contract.reporting.dto;

import java.time.LocalDateTime;
import java.util.Set;

public class ReportingGaugeSummaryDTO extends ReportingDTO
{
    private static final long serialVersionUID = 1L;

    private LocalDateTime start;
    private LocalDateTime end;
    private String title;
    private double downtimeValue;
    private double outputValue;
    private boolean hasDowntime;
    private Set<String> additionalTexts;

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

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    public double getDowntimeValue()
    {
        return downtimeValue;
    }

    public void setDowntimeValue( double downtimeValue )
    {
        this.downtimeValue = downtimeValue;
    }

    public double getOutputValue()
    {
        return outputValue;
    }

    public void setOutputValue( double outputValue )
    {
        this.outputValue = outputValue;
    }

    public boolean hasDowntime()
    {
        return hasDowntime;
    }

    public void setHasDowntime( boolean hasDowntime )
    {
        this.hasDowntime = hasDowntime;
    }

    public Set<String> getAdditionalTexts()
    {
        return additionalTexts;
    }

    public void setAdditionalTexts( Set<String> additionalTexts )
    {
        this.additionalTexts = additionalTexts;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ( ( additionalTexts == null ) ? 0 : additionalTexts.hashCode() );
        long temp;
        temp = Double.doubleToLongBits( downtimeValue );
        result = prime * result + (int) ( temp ^ ( temp >>> 32 ) );
        result = prime * result + ( ( end == null ) ? 0 : end.hashCode() );
        result = prime * result + ( hasDowntime ? 1231 : 1237 );
        temp = Double.doubleToLongBits( outputValue );
        result = prime * result + (int) ( temp ^ ( temp >>> 32 ) );
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
        ReportingGaugeSummaryDTO other = (ReportingGaugeSummaryDTO) obj;
        if ( additionalTexts == null )
        {
            if ( other.additionalTexts != null )
                return false;
        }
        else if ( !additionalTexts.equals( other.additionalTexts ) )
            return false;
        if ( Double.doubleToLongBits( downtimeValue ) != Double.doubleToLongBits( other.downtimeValue ) )
            return false;
        if ( end == null )
        {
            if ( other.end != null )
                return false;
        }
        else if ( !end.equals( other.end ) )
            return false;
        if ( hasDowntime != other.hasDowntime )
            return false;
        if ( Double.doubleToLongBits( outputValue ) != Double.doubleToLongBits( other.outputValue ) )
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
        return "ReportingGaugeSummaryDTO [start=" + start + ", end=" + end + ", title=" + title + ", downtimeValue=" + downtimeValue + ", outputValue=" + outputValue + ", hasDowntime=" + hasDowntime
                + ", additionalTexts=" + additionalTexts + ", toString()=" + super.toString() + "]";
    }

}
