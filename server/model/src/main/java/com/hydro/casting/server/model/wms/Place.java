package com.hydro.casting.server.model.wms;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Entity
@Table( name = "place" )
@NamedQuery( name = "place.name", query = "select p from Place p where p.name = :name")
public class Place extends BaseEntity
{
    @Column( name = "name", unique = true, nullable = false, length = 20 )
    private String name;

    @OneToMany( mappedBy = "place" )
    private Set<HandlingUnit> handlingUnits;

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public Set<HandlingUnit> getHandlingUnits()
    {
        // lazy evaluation; the field might still be null
        if ( handlingUnits == null )
        {
            handlingUnits = new HashSet<HandlingUnit>();
        }
        return handlingUnits;
    }

    public boolean containsInHandlingUnits( HandlingUnit handlingUnit )
    {
        return ( this.handlingUnits != null ) && this.handlingUnits.contains( handlingUnit );
    }

    public int numberOfHandlingUnits()
    {
        // handle uninitialized set, but do not initialize it now
        return ( handlingUnits == null ) ? 0 : handlingUnits.size();
    }

    public void flushHandlingUnits()
    {
        if ( handlingUnits != null )
        {
            Iterator<HandlingUnit> iter = handlingUnits.iterator();
            while ( iter.hasNext() )
            {
                iter.next().oneSided_setPlace( null );
            }
            handlingUnits = null;  // no concurrent modification
        }
    }

    public void addToHandlingUnits( HandlingUnit handlingUnit )
    {
        getHandlingUnits().add( handlingUnit );
        Place oldPlace = handlingUnit.getPlace();
        if ( ( oldPlace != this ) && ( oldPlace != null ) )
        {
            oldPlace.oneSided_removeFromHandlingUnits( handlingUnit );
        }
        handlingUnit.oneSided_setPlace( this );
    }

    public void removeFromHandlingUnits( HandlingUnit handlingUnit )
    {
        getHandlingUnits().remove( handlingUnit );
        handlingUnit.oneSided_setPlace( null );
    }

    public void oneSided_addToHandlingUnits( HandlingUnit handlingUnit )
    {
        getHandlingUnits().add( handlingUnit );
    }

    public void oneSided_removeFromHandlingUnits( HandlingUnit handlingUnit )
    {
        getHandlingUnits().remove( handlingUnit );
    }

    public void removeAllAssociations()
    {
        flushHandlingUnits();
    }

}
