package com.hydro.casting.server.model.mat;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;

@Entity
@DiscriminatorValue( "slab" )
@NamedQuery( name = "slab.activeByName", query = "select s from Slab s where s.name = :name and s.generationState = 400 and s.consumptionState < 400" )
public class Slab extends Material
{
    @Column( name = "width" )
    private double width;
    @Column( name = "height" )
    private double height;
    @Column( name = "length" )
    private double length;

    public double getWidth()
    {
        return width;
    }

    public void setWidth( double width )
    {
        this.width = width;
    }

    public double getHeight()
    {
        return height;
    }

    public void setHeight( double height )
    {
        this.height = height;
    }

    public double getLength()
    {
        return length;
    }

    public void setLength( double length )
    {
        this.length = length;
    }

    public void assignChild( Material child )
    {
        super.assignChild( child );
        if ( !( child instanceof Slab ) )
        {
            return;
        }
        final Slab sumpMaterial = (Slab) child;
    }

}