package com.hydro.casting.server.contract.downtime.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.util.Objects;

public class DowntimeKindDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private long id;

    private String costCenter;
    private String downtimeKind1;
    private String downtimeKind2;
    private String downtimeKind3;
    private String description;
    private String phase;

    public DowntimeKindDTO()
    {
    }

    public DowntimeKindDTO( long id, String costCenter, String downtimeKind1, String downtimeKind2, String downtimeKind3, String description, String phase )
    {
        this.id = id;
        this.costCenter = costCenter;
        this.downtimeKind1 = downtimeKind1;
        this.downtimeKind2 = downtimeKind2;
        this.downtimeKind3 = downtimeKind3;
        this.description = description;
        this.phase = phase;
    }

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

    public String getDowntimeKind1()
    {
        return downtimeKind1;
    }

    public void setDowntimeKind1( String downtimeKind1 )
    {
        this.downtimeKind1 = downtimeKind1;
    }

    public String getDowntimeKind2()
    {
        return downtimeKind2;
    }

    public void setDowntimeKind2( String downtimeKind2 )
    {
        this.downtimeKind2 = downtimeKind2;
    }

    public String getDowntimeKind3()
    {
        return downtimeKind3;
    }

    public void setDowntimeKind3( String downtimeKind3 )
    {
        this.downtimeKind3 = downtimeKind3;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getPhase()
    {
        return phase;
    }

    public void setPhase( String phase )
    {
        this.phase = phase;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( !( o instanceof DowntimeKindDTO ) )
            return false;
        DowntimeKindDTO that = (DowntimeKindDTO) o;
        return id == that.id && Objects.equals( costCenter, that.costCenter ) && Objects.equals( downtimeKind1, that.downtimeKind1 ) && Objects.equals( downtimeKind2, that.downtimeKind2 )
                && Objects.equals( downtimeKind3, that.downtimeKind3 ) && Objects.equals( description, that.description ) && Objects.equals( phase, that.phase );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, costCenter, downtimeKind1, downtimeKind2, downtimeKind3, description, phase );
    }

    @Override
    public String toString()
    {
        return "DowntimeKindDTO{" + "id=" + id + ", costCenter='" + costCenter + '\'' + ", downtimeKind1='" + downtimeKind1 + '\'' + ", downtimeKind2='" + downtimeKind2 + '\'' + ", downtimeKind3='"
                + downtimeKind3 + '\'' + ", description='" + description + '\'' + ", phase='" + phase + '\'' + '}';
    }
}
