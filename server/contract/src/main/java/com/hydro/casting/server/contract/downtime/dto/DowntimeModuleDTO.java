package com.hydro.casting.server.contract.downtime.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.util.Objects;
import java.util.Set;

public class DowntimeModuleDTO implements ViewDTO
{

    private static final long serialVersionUID = 1L;

    private long id;

    private String module;
    private String component;
    private String costCenter;
    private String description;
    private String orderNumber;
    private Set<DowntimeKindDTO> downtimeKinds;

    @Override
    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

    public String getModule()
    {
        return module;
    }

    public void setModule( String module )
    {
        this.module = module;
    }

    public String getComponent()
    {
        return component;
    }

    public void setComponent( String component )
    {
        this.component = component;
    }

    public String getCostCenter()
    {
        return costCenter;
    }

    public void setCostCenter( String costCenter )
    {
        this.costCenter = costCenter;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getOrderNumber()
    {
        return orderNumber;
    }

    public void setOrderNumber( String orderNumber )
    {
        this.orderNumber = orderNumber;
    }

    public Set<DowntimeKindDTO> getDowntimeKinds()
    {
        return downtimeKinds;
    }

    public void setDowntimeKinds( Set<DowntimeKindDTO> downtimeKinds )
    {
        this.downtimeKinds = downtimeKinds;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( getId(), getModule(), getComponent(), getCostCenter(), getDescription(), getOrderNumber() );
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        DowntimeModuleDTO that = (DowntimeModuleDTO) o;
        return getId() == that.getId() && Objects.equals( getModule(), that.getModule() ) && Objects.equals( getComponent(), that.getComponent() ) && Objects.equals( getCostCenter(),
                that.getCostCenter() ) && Objects.equals( getDescription(), that.getDescription() ) && Objects.equals( getOrderNumber(), that.getOrderNumber() );
    }

    @Override
    public String toString()
    {
        return "DowntimeModuleDTO{" + "id=" + id + ", module='" + module + '\'' + ", component='" + component + '\'' + ", costCenter='" + costCenter + '\'' + ", description='" + description + '\''
                + ", orderNumber='" + orderNumber + '\'' + '}';
    }
}
