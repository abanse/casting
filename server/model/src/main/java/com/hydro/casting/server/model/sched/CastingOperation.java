package com.hydro.casting.server.model.sched;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue( "casting_operation" )
public class CastingOperation extends Operation
{
    @Column( name = "position" )
    private int position;

    @Column( name = "length_bonus" )
    private double lengthBonus;

    public int getPosition()
    {
        return position;
    }

    public void setPosition( int position )
    {
        this.position = position;
    }

    public double getLengthBonus()
    {
        return lengthBonus;
    }

    public void setLengthBonus( double lengthBonus )
    {
        this.lengthBonus = lengthBonus;
    }
}