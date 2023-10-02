package com.hydro.casting.server.model.res;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Entity
@Table( name = "cost_center" )
@NamedQuery( name = "costCenter.apk", query = "select cc from CostCenter cc where cc.apk = :apk")
public class CostCenter extends BaseEntity
{
    @Column( name = "apk", length = 20 )
    private String apk;
    @Column( name = "description", length = 200 )
    private String description;
    @OneToMany( mappedBy = "costCenter" )
    private Set<Machine> machines;

    public String getApk()
    {
        return apk;
    }

    public void setApk( String newApk )
    {
        this.apk = newApk;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String newDescription )
    {
        this.description = newDescription;
    }

    public Set<Machine> getMachines()
    {
        if ( machines == null )
        {
            machines = new HashSet<Machine>();
        }
        return machines;
    }

    public boolean containsInMachines( Machine machine )
    {
        return ( this.machines != null ) && this.machines.contains( machine );
    }

    public int numberOfMachines()
    {
        return ( machines == null ) ? 0 : machines.size();
    }

    public void flushMachines()
    {
        if ( machines != null )
        {
            Iterator<Machine> iter = machines.iterator();
            while ( iter.hasNext() )
            {
                iter.next().oneSided_setCostCenter( null );
            }
            machines = null;
        }
    }

    public void addToMachines( Machine machine )
    {
        getMachines().add( machine );
        CostCenter oldCostCenter = machine.getCostCenter();
        if ( ( oldCostCenter != this ) && ( oldCostCenter != null ) )
        {
            oldCostCenter.oneSided_removeFromMachines( machine );
        }
        machine.oneSided_setCostCenter( this );
    }

    public void removeFromMachines( Machine machine )
    {
        getMachines().remove( machine );
        machine.oneSided_setCostCenter( null );
    }

    public void oneSided_addToMachines( Machine machine )
    {
        getMachines().add( machine );
    }

    public void oneSided_removeFromMachines( Machine machine )
    {
        getMachines().remove( machine );
    }

    public void removeAllAssociations()
    {
        flushMachines();
    }
}