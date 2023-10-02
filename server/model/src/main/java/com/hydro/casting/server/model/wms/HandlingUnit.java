package com.hydro.casting.server.model.wms;

import com.hydro.casting.server.model.mat.Material;
import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Entity
@Table( name = "handling_unit" )
@NamedQuery( name = "handlingUnit.name", query = "select hu from HandlingUnit hu where hu.name = :name")
public class HandlingUnit extends BaseEntity
{
    @Column( name = "name", unique = true, nullable = false, length = 20 )
    private String name;

    @OneToMany( mappedBy = "handlingUnit" )
    private Set<Material> materials;

    @ManyToOne
    @JoinColumn( name = "place_oid", nullable = false )
    private Place place;

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public Set<Material> getMaterials()
    {
        // lazy evaluation; the field might still be null
        if ( materials == null )
        {
            materials = new HashSet<Material>();
        }
        return materials;
    }

    public boolean containsInMaterials( Material material )
    {
        return ( this.materials != null ) && this.materials.contains( material );
    }

    public int numberOfMaterials()
    {
        // handle uninitialized set, but do not initialize it now
        return ( materials == null ) ? 0 : materials.size();
    }

    public void flushMaterials()
    {
        if ( materials != null )
        {
            Iterator<Material> iter = materials.iterator();
            while ( iter.hasNext() )
            {
                iter.next().oneSided_setHandlingUnit( null );
            }
            materials = null;  // no concurrent modification
        }
    }

    public void addToMaterials( Material material )
    {
        getMaterials().add( material );
        HandlingUnit oldHandlingUnit = material.getHandlingUnit();
        if ( ( oldHandlingUnit != this ) && ( oldHandlingUnit != null ) )
        {
            oldHandlingUnit.oneSided_removeFromMaterials( material );
        }
        material.oneSided_setHandlingUnit( this );
    }

    public void removeFromMaterials( Material material )
    {
        getMaterials().remove( material );
        material.oneSided_setHandlingUnit( null );
    }

    public void oneSided_addToMaterials( Material material )
    {
        getMaterials().add( material );
    }

    public void oneSided_removeFromMaterials( Material material )
    {
        getMaterials().remove( material );
    }

    public Place getPlace()
    {
        return place;
    }

    public void setPlace( Place newPlace )
    {
        Place oldPlace = this.place;
        this.place = newPlace;
        if ( oldPlace != newPlace )
        {
            if ( oldPlace != null )
            {
                oldPlace.oneSided_removeFromHandlingUnits( this );
            }
            if ( newPlace != null )
            {
                newPlace.oneSided_addToHandlingUnits( this );
            }
        }
    }

    public void oneSided_setPlace( Place newPlace )
    {
        this.place = newPlace;
    }

    public void removeAllAssociations()
    {
        flushMaterials();
        setPlace( null );
    }
}
