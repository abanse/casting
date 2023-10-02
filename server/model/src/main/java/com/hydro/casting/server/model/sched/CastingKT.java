package com.hydro.casting.server.model.sched;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table( name = "casting_kt" )
public class CastingKT extends BaseEntity
{
    @Column( name = "caster", length = 10 )
    private String caster;

    @Column( name = "alloy", length = 20 )
    private String alloy;

    @Column( name = "prio" )
    private int prio;

    @Column( name = "casting_speed" )
    private double castingSpeed;

    @Column( name = "handling_time" )
    private double handlingTime;

    @Column( name = "standing_time" )
    private double standingTime;

    @Column( name = "casting_tm" )
    private int castingTM;

    @Column( name = "unloading_tm" )
    private int unloadingTM;

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
}