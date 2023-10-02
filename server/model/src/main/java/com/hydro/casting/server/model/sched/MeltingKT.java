package com.hydro.casting.server.model.sched;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table( name = "melting_kt" )
public class MeltingKT extends BaseEntity
{
    @Column( name = "caster", length = 10 )
    private String caster;

    @Column( name = "furnace", length = 10 )
    private String furnace;

    @Column( name = "alloy", length = 20 )
    private String alloy;

    @Column( name = "prio" )
    private int prio;

    @Column( name = "percentage_solid_metal" )
    private double percentageSolidMetal;

    @Column( name = "melting_capacity" )
    private double meltingCapacity;

    @Column( name = "handling_time" )
    private double handlingTime;

    @Column( name = "percentage_s1" )
    private double percentageS1;

    @Column( name = "percentage_s2" )
    private double percentageS2;

    @Column( name = "percentage_s3" )
    private double percentageS3;

    @Column( name = "percentage_el" )
    private double percentageEL;

    @Column( name = "percentage_ra" )
    private double percentageRA;

    @Column( name = "standing_time" )
    private double standingTime;

    @Column( name = "preparing_tm" )
    private int preparingTM;
    @Column( name = "charging_tm" )
    private int chargingTM;
    @Column( name = "treating_tm" )
    private int treatingTM;
    @Column( name = "skimming_tm" )
    private int skimmingTM;
    @Column( name = "resting_tm" )
    private int restingTM;
    @Column( name = "ready_to_cast_tm" )
    private int readyToCastTM;
    @Column( name = "casting_tm" )
    private int castingTM;

    public String getCaster()
    {
        return caster;
    }

    public void setCaster( String caster )
    {
        this.caster = caster;
    }

    public String getFurnace()
    {
        return furnace;
    }

    public void setFurnace( String furnace )
    {
        this.furnace = furnace;
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

    public double getPercentageSolidMetal()
    {
        return percentageSolidMetal;
    }

    public void setPercentageSolidMetal( double percentageSolidMetal )
    {
        this.percentageSolidMetal = percentageSolidMetal;
    }

    public double getMeltingCapacity()
    {
        return meltingCapacity;
    }

    public void setMeltingCapacity( double meltingCapacity )
    {
        this.meltingCapacity = meltingCapacity;
    }

    public double getHandlingTime()
    {
        return handlingTime;
    }

    public void setHandlingTime( double handlingTime )
    {
        this.handlingTime = handlingTime;
    }

    public double getPercentageS1()
    {
        return percentageS1;
    }

    public void setPercentageS1( double percentageS1 )
    {
        this.percentageS1 = percentageS1;
    }

    public double getPercentageS2()
    {
        return percentageS2;
    }

    public void setPercentageS2( double percentageS2 )
    {
        this.percentageS2 = percentageS2;
    }

    public double getPercentageS3()
    {
        return percentageS3;
    }

    public void setPercentageS3( double percentageS3 )
    {
        this.percentageS3 = percentageS3;
    }

    public double getPercentageEL()
    {
        return percentageEL;
    }

    public void setPercentageEL( double percentageEL )
    {
        this.percentageEL = percentageEL;
    }

    public double getPercentageRA()
    {
        return percentageRA;
    }

    public void setPercentageRA( double percentageRA )
    {
        this.percentageRA = percentageRA;
    }

    public double getStandingTime()
    {
        return standingTime;
    }

    public void setStandingTime( double standingTime )
    {
        this.standingTime = standingTime;
    }

    public int getPreparingTM()
    {
        return preparingTM;
    }

    public void setPreparingTM( int preparingTM )
    {
        this.preparingTM = preparingTM;
    }

    public int getChargingTM()
    {
        return chargingTM;
    }

    public void setChargingTM( int chargingTM )
    {
        this.chargingTM = chargingTM;
    }

    public int getTreatingTM()
    {
        return treatingTM;
    }

    public void setTreatingTM( int treatingTM )
    {
        this.treatingTM = treatingTM;
    }

    public int getSkimmingTM()
    {
        return skimmingTM;
    }

    public void setSkimmingTM( int skimmingTM )
    {
        this.skimmingTM = skimmingTM;
    }

    public int getRestingTM()
    {
        return restingTM;
    }

    public void setRestingTM( int restingTM )
    {
        this.restingTM = restingTM;
    }

    public int getReadyToCastTM()
    {
        return readyToCastTM;
    }

    public void setReadyToCastTM( int readyToCastTM )
    {
        this.readyToCastTM = readyToCastTM;
    }

    public int getCastingTM()
    {
        return castingTM;
    }

    public void setCastingTM( int castingTM )
    {
        this.castingTM = castingTM;
    }
}