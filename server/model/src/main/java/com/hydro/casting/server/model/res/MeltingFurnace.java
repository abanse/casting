package com.hydro.casting.server.model.res;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;

@Entity
@DiscriminatorValue( "melting_furnace" )
@NamedQuery( name = "meltingFurnace.apk", query = "select mf from MeltingFurnace mf where mf.apk = :apk")
public class MeltingFurnace extends Machine
{
    @Column( name = "max_weight" )
    private double maxWeight;
    @Column( name = "nominal_weight" )
    private double nominalWeight;
    @Column( name = "last_charge" )
    private Integer lastCharge;

    public double getMaxWeight()
    {
        return maxWeight;
    }

    public void setMaxWeight( double newMaxWeight )
    {
        this.maxWeight = newMaxWeight;
    }

    public double getNominalWeight()
    {
        return nominalWeight;
    }

    public void setNominalWeight( double nominalWeight )
    {
        this.nominalWeight = nominalWeight;
    }

    public Integer getLastCharge()
    {
        return lastCharge;
    }

    public void setLastCharge( Integer lastCharge )
    {
        this.lastCharge = lastCharge;
    }
}

