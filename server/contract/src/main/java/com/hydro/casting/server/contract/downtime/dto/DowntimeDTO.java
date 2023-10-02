package com.hydro.casting.server.contract.downtime.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class DowntimeDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private long id;

    private String costCenter;
    private String machine;
    private LocalDateTime fromTS;
    private LocalDateTime endTS;
    private String shift;
    private Integer amount;
    private String description;
    private String remark;
    private String downtimeKind1;
    private String downtimeKind2;
    private String downtimeKind3;
    private String downtimeDescription;
    private String module;
    private String component;
    private String moduleDescription;
    private String moduleErpIdent;
    private String userId;
    private String type;

    private transient List<DowntimeDTO> childs;
    private transient DowntimeDTO parent;

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

    public String getShift()
    {
        return shift;
    }

    public void setShift( String shift )
    {
        this.shift = shift;
    }

    public Integer getAmount()
    {
        return amount;
    }

    public void setAmount( Integer amount )
    {
        this.amount = amount;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark( String remark )
    {
        this.remark = remark;
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

    public String getDowntimeDescription()
    {
        return downtimeDescription;
    }

    public void setDowntimeDescription( String downtimeDescription )
    {
        this.downtimeDescription = downtimeDescription;
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

    public String getModuleDescription()
    {
        return moduleDescription;
    }

    public void setModuleDescription( String moduleDescription )
    {
        this.moduleDescription = moduleDescription;
    }

    public String getModuleErpIdent()
    {
        return moduleErpIdent;
    }

    public void setModuleErpIdent( String moduleErpIdent )
    {
        this.moduleErpIdent = moduleErpIdent;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId( String userId )
    {
        this.userId = userId;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public List<DowntimeDTO> getChilds()
    {
        return childs;
    }

    public void setChilds( List<DowntimeDTO> childs )
    {
        this.childs = childs;
    }

    public DowntimeDTO getParent()
    {
        return parent;
    }

    public void setParent( DowntimeDTO parent )
    {
        this.parent = parent;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( !( o instanceof DowntimeDTO ) )
            return false;
        DowntimeDTO that = (DowntimeDTO) o;
        return id == that.id && Objects.equals( costCenter, that.costCenter ) && Objects.equals( machine, that.machine ) && Objects.equals( fromTS, that.fromTS ) && Objects.equals( endTS, that.endTS )
                && Objects.equals( shift, that.shift ) && Objects.equals( amount, that.amount ) && Objects.equals( description, that.description ) && Objects.equals( remark, that.remark )
                && Objects.equals( downtimeKind1, that.downtimeKind1 ) && Objects.equals( downtimeKind2, that.downtimeKind2 ) && Objects.equals( downtimeKind3, that.downtimeKind3 ) && Objects.equals(
                downtimeDescription, that.downtimeDescription ) && Objects.equals( module, that.module ) && Objects.equals( component, that.component ) && Objects.equals( moduleDescription,
                that.moduleDescription ) && Objects.equals( moduleErpIdent, that.moduleErpIdent ) && Objects.equals( userId, that.userId ) && Objects.equals( type, that.type );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, costCenter, machine, fromTS, endTS, shift, amount, description, remark, downtimeKind1, downtimeKind2, downtimeKind3, downtimeDescription, module, component,
                moduleDescription, moduleErpIdent, userId, type );
    }

    @Override
    public String toString()
    {
        return "DowntimeDTO{" + "id=" + id + ", costCenter='" + costCenter + '\'' + ", machine='" + machine + '\'' + ", fromTS=" + fromTS + ", endTS=" + endTS + ", shift='" + shift + '\''
                + ", amount=" + amount + ", description='" + description + '\'' + ", remark='" + remark + '\'' + ", downtimeKind1='" + downtimeKind1 + '\'' + ", downtimeKind2='" + downtimeKind2 + '\''
                + ", downtimeKind3='" + downtimeKind3 + '\'' + ", downtimeDescription='" + downtimeDescription + '\'' + ", module='" + module + '\'' + ", component='" + component + '\''
                + ", moduleDescription='" + moduleDescription + '\'' + ", moduleErpIdent='" + moduleErpIdent + '\'' + ", userId='" + userId + '\'' + ", type='" + type + '\'' + '}';
    }
}
