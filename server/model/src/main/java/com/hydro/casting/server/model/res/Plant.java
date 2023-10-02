package com.hydro.casting.server.model.res;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Entity
@Table( name = "plant" )
@NamedQuery( name = "plant.apk", query = "select p from Plant p where p.apk = :apk")
public class Plant extends BaseEntity
{
    @Column( name = "apk", length = 20 )
    private String apk;
    @OneToMany( mappedBy = "plant" )
    private Set<Machine> machines;

    public String getApk()
    {
        return apk;
    }

    public void setApk( String newApk )
    {
        this.apk = newApk;
    }

    public Set<Machine> getMachines()
    {
        // lazy evaluation; the field might still be null
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
        // handle uninitialized set, but do not initialize it now
        return ( machines == null ) ? 0 : machines.size();
    }

    public void flushMachines()
    {
        if ( machines != null )
        {
            Iterator<Machine> iter = machines.iterator();
            while ( iter.hasNext() )
            {
                iter.next().oneSided_setPlant( null );
            }
            machines = null;  // no concurrent modification
        }
    }

    public void addToMachines( Machine machine )
    {
        getMachines().add( machine );
        Plant oldPlant = machine.getPlant();
        if ( ( oldPlant != this ) && ( oldPlant != null ) )
        {
            oldPlant.oneSided_removeFromMachines( machine );
        }
        machine.oneSided_setPlant( this );
    }

    public void removeFromMachines( Machine machine )
    {
        getMachines().remove( machine );
        machine.oneSided_setPlant( null );
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