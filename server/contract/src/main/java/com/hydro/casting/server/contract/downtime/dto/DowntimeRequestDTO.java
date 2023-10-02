package com.hydro.casting.server.contract.downtime.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.time.LocalDateTime;
import java.util.Objects;

public class DowntimeRequestDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private long id;

    private String costCenter;
    private String machine;
    private LocalDateTime fromTS;
    private LocalDateTime endTS;
    private String description;
    private String phase;
    private LocalDateTime createTS;
    private String releaseUser;
    private LocalDateTime releaseTS;

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

    public LocalDateTime getFromTS()
    {
        return fromTS;
    }

    public void setFromTS( LocalDateTime fromTS )
    {
        this.fromTS = fromTS;
    }

    public LocalDateTime getEndTS()
    {
        return endTS;
    }

    public void setEndTS( LocalDateTime endTS )
    {
        this.endTS = endTS;
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

    public LocalDateTime getCreateTS()
    {
        return createTS;
    }

    public void setCreateTS( LocalDateTime createTS )
    {
        this.createTS = createTS;
    }

    public String getReleaseUser()
    {
        return releaseUser;
    }

    public void setReleaseUser( String releaseUser )
    {
        this.releaseUser = releaseUser;
    }

    public LocalDateTime getReleaseTS()
    {
        return releaseTS;
    }

    public void setReleaseTS( LocalDateTime releaseTS )
    {
        this.releaseTS = releaseTS;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        DowntimeRequestDTO that = (DowntimeRequestDTO) o;
        return getId() == that.getId() && Objects.equals( getCostCenter(), that.getCostCenter() ) && Objects.equals( getMachine(), that.getMachine() ) && Objects.equals( getFromTS(),
                that.getFromTS() ) && Objects.equals( getEndTS(), that.getEndTS() ) && Objects.equals( getDescription(), that.getDescription() ) && Objects.equals( getPhase(), that.getPhase() )
                && Objects.equals( getCreateTS(), that.getCreateTS() ) && Objects.equals( getReleaseUser(), that.getReleaseUser() ) && Objects.equals( getReleaseTS(), that.getReleaseTS() );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( getId(), getCostCenter(), getMachine(), getFromTS(), getEndTS(), getDescription(), getPhase(), getCreateTS(), getReleaseUser(), getReleaseTS() );
    }

    @Override
    public String toString()
    {
        return "DowntimeRequestDTO{" + "id=" + id + ", costCenter='" + costCenter + '\'' + ", machine='" + machine + '\'' + ", fromTS=" + fromTS + ", endTS=" + endTS + ", description='" + description
                + '\'' + ", phase='" + phase + '\'' + ", createTS=" + createTS + ", releaseUser='" + releaseUser + '\'' + ", releaseTS=" + releaseTS + '}';
    }
}
