package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.util.Objects;

public class MeltingKTDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private long id;

    private String caster;
    private String furnace;
    private String alloy;
    private int prio;
    private double percentageSolidMetal;
    private double meltingCapacity;
    private double handlingTime;
    private double percentageS1;
    private double percentageS2;
    private double percentageS3;
    private double percentageEL;
    private double percentageRA;
    private double standingTime;

    private int preparingTM;
    private int chargingTM;
    private int treatingTM;
    private int skimmingTM;
    private int restingTM;
    private int readyToCastTM;
    private int castingTM;

    @Override
    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

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

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        MeltingKTDTO that = (MeltingKTDTO) o;
        return id == that.id && prio == that.prio && Double.compare( that.percentageSolidMetal, percentageSolidMetal ) == 0 && Double.compare( that.meltingCapacity, meltingCapacity ) == 0
                && Double.compare( that.handlingTime, handlingTime ) == 0 && Double.compare( that.percentageS1, percentageS1 ) == 0 && Double.compare( that.percentageS2, percentageS2 ) == 0
                && Double.compare( that.percentageS3, percentageS3 ) == 0 && Double.compare( that.percentageEL, percentageEL ) == 0 && Double.compare( that.percentageRA, percentageRA ) == 0
                && Double.compare( that.standingTime, standingTime ) == 0 && preparingTM == that.preparingTM && chargingTM == that.chargingTM && treatingTM == that.treatingTM
                && skimmingTM == that.skimmingTM && restingTM == that.restingTM && readyToCastTM == that.readyToCastTM && castingTM == that.castingTM && Objects.equals( caster, that.caster )
                && Objects.equals( furnace, that.furnace ) && Objects.equals( alloy, that.alloy );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, caster, furnace, alloy, prio, percentageSolidMetal, meltingCapacity, handlingTime, percentageS1, percentageS2, percentageS3, percentageEL, percentageRA, standingTime,
                preparingTM, chargingTM, treatingTM, skimmingTM, restingTM, readyToCastTM, castingTM );
    }

    @Override
    public String toString()
    {
        return "MeltingKTDTO{" + "id=" + id + ", caster='" + caster + '\'' + ", furnace='" + furnace + '\'' + ", alloy='" + alloy + '\'' + ", prio=" + prio + ", percentageSolidMetal="
                + percentageSolidMetal + ", meltingCapacity=" + meltingCapacity + ", handlingTime=" + handlingTime + ", percentageS1=" + percentageS1 + ", percentageS2=" + percentageS2
                + ", percentageS3=" + percentageS3 + ", percentageEL=" + percentageEL + ", percentageRA=" + percentageRA + ", standingTime=" + standingTime + ", preparingTM=" + preparingTM
                + ", chargingTM=" + chargingTM + ", treatingTM=" + treatingTM + ", skimmingTM=" + skimmingTM + ", restingTM=" + restingTM + ", readyToCastTM=" + readyToCastTM + ", castingTM="
                + castingTM + '}';
    }
}
