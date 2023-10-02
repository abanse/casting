package com.hydro.casting.server.contract.reporting.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class ReportingOutputSummaryDTO extends ReportingDTO
{
    private static final long serialVersionUID = 1L;

    private LocalDateTime start;
    private LocalDateTime end;
    private String title;
    private Map<String, String> entries;
    private Map<String, String> entriesDetail;

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

    public Map<String, String> getEntries()
    {
        return entries;
    }

    public void setEntries( Map<String, String> entries )
    {
        this.entries = entries;
    }

    public Map<String, String> getEntriesDetail()
    {
        return entriesDetail;
    }

    public void setEntriesDetail( Map<String, String> entriesDetail )
    {
        this.entriesDetail = entriesDetail;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ( ( end == null ) ? 0 : end.hashCode() );
        result = prime * result + ( ( entries == null ) ? 0 : entries.hashCode() );
        result = prime * result + ( ( entriesDetail == null ) ? 0 : entriesDetail.hashCode() );
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
        ReportingOutputSummaryDTO other = (ReportingOutputSummaryDTO) obj;
        if ( end == null )
        {
            if ( other.end != null )
                return false;
        }
        else if ( !end.equals( other.end ) )
            return false;
        if ( entries == null )
        {
            if ( other.entries != null )
                return false;
        }
        else if ( !entries.equals( other.entries ) )
            return false;
        if ( entriesDetail == null )
        {
            if ( other.entriesDetail != null )
                return false;
        }
        else if ( !entriesDetail.equals( other.entriesDetail ) )
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
        return "ReportingOutputSummaryDTO [start=" + start + ", end=" + end + ", title=" + title + ", entries=" + entries + ", entriesDetail=" + entriesDetail + "]";
    }
}
