package com.hydro.casting.server.model.res;

import com.hydro.casting.server.model.mat.Alloy;

import javax.persistence.*;

@Entity
@DiscriminatorValue( "caster" )
@NamedQuery( name = "caster.apk", query = "select c from Caster c where c.apk = :apk")
public class Caster extends Machine
{
    @Column( name = "max_molds" )
    private int maxMolds;
    @Column( name="last_charge")
    private int lastCharge;
    @Column( name="max_casting_length" )
    private int maxCastingLength;

    @ManyToOne
    @JoinColumn( name = "left_furnace_oid" )
    private MeltingFurnace leftFurnace;
    @ManyToOne
    @JoinColumn( name = "right_furnace_oid" )
    private MeltingFurnace rightFurnace;

    public int getMaxMolds()
    {
        return maxMolds;
    }

    public void setMaxMolds( int maxMolds )
    {
        this.maxMolds = maxMolds;
    }

    public int getLastCharge()
    {
        return lastCharge;
    }

    public void setLastCharge( int lastCharge )
    {
        this.lastCharge = lastCharge;
    }

    public int getMaxCastingLength()
    {
        return maxCastingLength;
    }

    public void setMaxCastingLength( int maxCastingLength )
    {
        this.maxCastingLength = maxCastingLength;
    }

    public MeltingFurnace getLeftFurnace()
    {
        return leftFurnace;
    }

    public void setLeftFurnace( MeltingFurnace leftFurnace )
    {
        this.leftFurnace = leftFurnace;
    }

    public MeltingFurnace getRightFurnace()
    {
        return rightFurnace;
    }

    public void setRightFurnace( MeltingFurnace rightFurnace )
    {
        this.rightFurnace = rightFurnace;
    }

    public void removeAllAssociations()
    {
        super.removeAllAssociations();
        setLeftFurnace( null );
        setRightFurnace( null );
    }
}

