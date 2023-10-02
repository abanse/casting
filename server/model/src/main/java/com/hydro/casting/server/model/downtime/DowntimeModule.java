package com.hydro.casting.server.model.downtime;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table( name = "downtime_module", uniqueConstraints = @UniqueConstraint( columnNames = { "cost_center", "module", "component" } ) )
@NamedQuery( name = "downtimeModule.apk", query = "SELECT dtm FROM DowntimeModule dtm WHERE dtm.costCenter = :costCenter AND dtm.module = :module AND dtm.component = :component" )
@NamedQuery( name = "downtimeModule.costCenter", query = "SELECT dtm FROM DowntimeModule dtm WHERE dtm.costCenter = :costCenter" )
@NamedQuery( name = "downtimeModule.costCenters", query = "SELECT dtm FROM DowntimeModule dtm WHERE dtm.costCenter IN :costCenters" )
public class DowntimeModule extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Column( name = "cost_center", nullable = false, length = 10 )
    private String costCenter;

    @Column( name = "module", nullable = false, length = 10 )
    private String module;

    @Column( name = "component", nullable = false, length = 20 )
    private String component;

    @Column( name = "description" )
    private String description;

    @Column( name = "erp_ident", length = 100 )
    private String erpIdent;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "downtime_module_kinds",
            joinColumns = { @JoinColumn( name = "downtime_module_id" ) },
            inverseJoinColumns = { @JoinColumn( name = "downtime_kind_id" ) }
    )
    private Set<DowntimeKind> downtimeKinds = new HashSet<>();

    public String getCostCenter()
    {
        return costCenter;
    }

    public void setCostCenter( String kostenstelle )
    {
        this.costCenter = kostenstelle;
    }

    public String getModule()
    {
        return module;
    }

    public void setModule( String baugruppe )
    {
        this.module = baugruppe;
    }

    public String getComponent()
    {
        return component;
    }

    public void setComponent( String bauteil )
    {
        this.component = bauteil;
    }

    public String getDescription()
    {
        return this.description;
    }

    public void setDescription( String bezeichnung )
    {
        this.description = bezeichnung;
    }

    public String getErpIdent()
    {
        return this.erpIdent;
    }

    public void setErpIdent( String auftragsNummer )
    {
        this.erpIdent = auftragsNummer;
    }

    public Set<DowntimeKind> getDowntimeKinds()
    {
        if ( downtimeKinds == null )
        {
            downtimeKinds = new HashSet<>();
        }

        return downtimeKinds;
    }

    public void setDowntimeKinds( Set<DowntimeKind> downtimeKinds )
    {
        this.downtimeKinds = downtimeKinds;
    }

    @Override
    public String toString()
    {
        return "DowntimeModule{" + "costCenter='" + costCenter + '\'' + ", module='" + module + '\'' + ", component='" + component + '\'' + '}';
    }
}
