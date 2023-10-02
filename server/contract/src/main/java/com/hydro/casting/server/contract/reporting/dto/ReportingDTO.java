package com.hydro.casting.server.contract.reporting.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

public abstract class ReportingDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private long id;

    private String costCenter;
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

    public String getCostCenter()
    {
        return costCenter;
    }

    public void setCostCenter( String costCenter )
    {
        this.costCenter = costCenter;
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
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( costCenter == null ) ? 0 : costCenter.hashCode() );
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
        ReportingDTO other = (ReportingDTO) obj;
        if ( costCenter == null )
        {
            if ( other.costCenter != null )
                return false;
        }
        else if ( !costCenter.equals( other.costCenter ) )
            return false;
        if ( id != other.id )
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "ReportingDTO [id=" + id + ", costCenter=" + costCenter + "]";
    }
}
