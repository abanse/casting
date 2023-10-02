package com.hydro.casting.server.model.inspection;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue( "caster_positions" )
public class CasterPositionsIV extends InspectionValue
{
    @Column( name = "pos1_value" )
    private long pos1Value;
    @Column( name = "pos2_value" )
    private long pos2Value;
    @Column( name = "pos3_value" )
    private long pos3Value;
    @Column( name = "pos4_value" )
    private long pos4Value;
    @Column( name = "pos5_value" )
    private long pos5Value;

    public long getPos1Value()
    {
        return pos1Value;
    }

    public void setPos1Value( long pos1Value )
    {
        this.pos1Value = pos1Value;
    }

    public long getPos2Value()
    {
        return pos2Value;
    }

    public void setPos2Value( long pos2Value )
    {
        this.pos2Value = pos2Value;
    }

    public long getPos3Value()
    {
        return pos3Value;
    }

    public void setPos3Value( long pos3Value )
    {
        this.pos3Value = pos3Value;
    }

    public long getPos4Value()
    {
        return pos4Value;
    }

    public void setPos4Value( long pos4Value )
    {
        this.pos4Value = pos4Value;
    }

    public long getPos5Value()
    {
        return pos5Value;
    }

    public void setPos5Value( long pos5Value )
    {
        this.pos5Value = pos5Value;
    }
}

