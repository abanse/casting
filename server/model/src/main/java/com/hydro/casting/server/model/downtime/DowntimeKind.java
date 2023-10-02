package com.hydro.casting.server.model.downtime;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table( name = "downtime_kind", uniqueConstraints = @UniqueConstraint( columnNames = { "cost_center", "kind1", "kind2", "kind3" } ) )
@NamedQuery( name = "downtimeKind.apk", query = "SELECT dtk FROM DowntimeKind dtk WHERE dtk.costCenter = :costCenter AND dtk.kind1 = :kind1 AND dtk.kind2 = :kind2 AND dtk.kind3 = :kind3" )
@NamedQuery( name = "downtimeKind.costCenter", query = "SELECT dtk FROM DowntimeKind dtk WHERE dtk.costCenter = :costCenter" )
@NamedQuery( name = "downtimeKind.costCenters", query = "SELECT dtk FROM DowntimeKind dtk WHERE dtk.costCenter IN :costCenters" )
public class DowntimeKind extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Column( name = "cost_center", nullable = false, length = 10 )
    private String costCenter;

    @Column( name = "kind1", nullable = false, length = 5 )
    private String kind1;

    @Column( name = "kind2", nullable = false, length = 5 )
    private String kind2;

    @Column( name = "kind3", nullable = false, length = 5 )
    private String kind3;

    @Column( name = "description" )
    private String description;

    @Column( name = "phase", length = 100 )
    private String phase;

    @ManyToMany( mappedBy = "downtimeKinds" )
    private Set<DowntimeModule> downtimeModules = new HashSet<>();

    public String getCostCenter()
    {
        return costCenter;
    }

    public void setCostCenter( String kostenstelle )
    {
        this.costCenter = kostenstelle;
    }

    public String getKind1()
    {
        return kind1;
    }

    public void setKind1( String stillstandsArt1 )
    {
        this.kind1 = stillstandsArt1;
    }

    public String getKind2()
    {
        return kind2;
    }

    public void setKind2( String stillstandsArt2 )
    {
        this.kind2 = stillstandsArt2;
    }

    public String getKind3()
    {
        return kind3;
    }

    public void setKind3( String stillstandsArt3 )
    {
        this.kind3 = stillstandsArt3;
    }

    public String getDescription()
    {
        return this.description;
    }

    public void setDescription( String bezeichnung )
    {
        this.description = bezeichnung;
    }

    public String getPhase()
    {
        return phase;
    }

    public void setPhase( String phase )
    {
        this.phase = phase;
    }

    public Set<DowntimeModule> getDowntimeModules()
    {
        if ( downtimeModules == null )
        {
            downtimeModules = new HashSet<>();
        }

        return downtimeModules;
    }

    public void setDowntimeModules( Set<DowntimeModule> downtimeModules )
    {
        this.downtimeModules = downtimeModules;
    }

    @Override
    public String toString()
    {
        return "DowntimeKind{" + "costCenter='" + costCenter + '\'' + ", kind1='" + kind1 + '\'' + ", kind2='" + kind2 + '\'' + ", kind3='" + kind3 + '\'' + ", description='" + description + '\''
                + '}';
    }
}
