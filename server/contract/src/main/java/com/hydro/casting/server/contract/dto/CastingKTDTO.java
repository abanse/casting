package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.util.Objects;

public class CastingKTDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private long id;
    private String caster;
    private String alloy;
    private int prio;
    private double castingSpeed;
    private double handlingTime;
    private double standingTime;

    private int castingTM;
    private int unloadingTM;

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

    public double getCastingSpeed()
    {
        return castingSpeed;
    }

    public void setCastingSpeed( double castingSpeed )
    {
        this.castingSpeed = castingSpeed;
    }

    public double getHandlingTime()
    {
        return handlingTime;
    }

    public void setHandlingTime( double handlingTime )
    {
        this.handlingTime = handlingTime;
    }

    public double getStandingTime()
    {
        return standingTime;
    }

    public void setStandingTime( double standingTime )
    {
        this.standingTime = standingTime;
    }

    public int getCastingTM()
    {
        return castingTM;
    }

    public void setCastingTM( int castingTM )
    {
        this.castingTM = castingTM;
    }

    public int getUnloadingTM()
    {
        return unloadingTM;
    }

    public void setUnloadingTM( int unloadingTM )
    {
        this.unloadingTM = unloadingTM;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        CastingKTDTO that = (CastingKTDTO) o;
        return id == that.id && prio == that.prio && Double.compare( that.castingSpeed, castingSpeed ) == 0 && Double.compare( that.handlingTime, handlingTime ) == 0
                && Double.compare( that.standingTime, standingTime ) == 0 && castingTM == that.castingTM && unloadingTM == that.unloadingTM && Objects.equals( caster, that.caster ) && Objects.equals(
                alloy, that.alloy );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, caster, alloy, prio, castingSpeed, handlingTime, standingTime, castingTM, unloadingTM );
    }

    @Override
    public String toString()
    {
        return "CastingKTDTO{" + "id=" + id + ", caster='" + caster + '\'' + ", alloy='" + alloy + '\'' + ", prio=" + prio + ", castingSpeed=" + castingSpeed + ", handlingTime=" + handlingTime
                + ", standingTime=" + standingTime + ", castingTM=" + castingTM + ", unloadingTM=" + unloadingTM + '}';
    }
}
