package com.hydro.casting.server.model.sched;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table( name = "melting_furnace_kt" )
public class MeltingFurnaceKT extends BaseEntity
{
    @Column( name = "melter", length = 10)
    private String melter;
    @Column( name = "alloy", length = 20 )
    private String alloy;
    @Column( name = "prio" )
    private int prio;

    @Column( name = "charging_tm" )
    private int chargingTM;
    @Column( name = "melting_tm" )
    private int meltingTM;
    @Column( name = "skimming_tm" )
    private int skimmingTM;
    @Column( name = "treating_tm" )
    private int treatingTM;
    @Column( name = "heating_tm" )
    private int heatingTM;
    @Column( name = "pouring_tm" )
    private int pouringTM;
    @Column( name = "dredging_tm" )
    private int dredgingTM;

    public String getMelter()
    {
        return melter;
    }

    public void setMelter( String melter )
    {
        this.melter = melter;
    }

    public String getAlloy()
    {
        return alloy;
    }

    public void setAlloy( String alloy )
    {
        this.alloy = alloy;
    }

    public int getPrio()
    {
        return prio;
    }

    public void setPrio( int prio )
    {
        this.prio = prio;
    }

    public int getChargingTM()
    {
        return chargingTM;
    }

    public void setChargingTM( int chargingTM )
    {
        this.chargingTM = chargingTM;
    }

    public int getMeltingTM()
    {
        return meltingTM;
    }

    public void setMeltingTM( int meltingTM )
    {
        this.meltingTM = meltingTM;
    }

    public int getSkimmingTM()
    {
        return skimmingTM;
    }

    public void setSkimmingTM( int skimmingTM )
    {
        this.skimmingTM = skimmingTM;
    }

    public int getTreatingTM()
    {
        return treatingTM;
    }

    public void setTreatingTM( int treatingTM )
    {
        this.treatingTM = treatingTM;
    }

    public int getHeatingTM()
    {
        return heatingTM;
    }

    public void setHeatingTM( int heatingTM )
    {
        this.heatingTM = heatingTM;
    }

    public int getPouringTM()
    {
        return pouringTM;
    }

    public void setPouringTM( int pouringTM )
    {
        this.pouringTM = pouringTM;
    }

    public int getDredgingTM()
    {
        return dredgingTM;
    }

    public void setDredgingTM( int dredgingTM )
    {
        this.dredgingTM = dredgingTM;
    }
}
