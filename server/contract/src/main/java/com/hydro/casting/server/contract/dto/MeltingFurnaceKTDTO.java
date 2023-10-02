package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.util.Objects;

public class MeltingFurnaceKTDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private long id;
    private String melter;
    private String alloy;
    private int prio;

    private int chargingTM;
    private int meltingTM;
    private int skimmingTM;
    private int treatingTM;
    private int heatingTM;
    private int pouringTM;
    private int dredgingTM;

    @Override
    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

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

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        MeltingFurnaceKTDTO that = (MeltingFurnaceKTDTO) o;
        return getId() == that.getId() && getPrio() == that.getPrio() && getChargingTM() == that.getChargingTM() && getMeltingTM() == that.getMeltingTM() && getSkimmingTM() == that.getSkimmingTM()
                && getTreatingTM() == that.getTreatingTM() && getHeatingTM() == that.getHeatingTM() && getPouringTM() == that.getPouringTM() && getDredgingTM() == that.getDredgingTM()
                && Objects.equals( getMelter(), that.getMelter() ) && Objects.equals( getAlloy(), that.getAlloy() );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( getId(), getMelter(), getAlloy(), getPrio(), getChargingTM(), getMeltingTM(), getSkimmingTM(), getTreatingTM(), getHeatingTM(), getPouringTM(), getDredgingTM() );
    }

    @Override
    public String toString()
    {
        return "MeltingFurnaceKTDTO{" + "id=" + id + ", melter='" + melter + '\'' + ", alloy='" + alloy + '\'' + ", prio=" + prio + ", chargingTM=" + chargingTM + ", meltingTM=" + meltingTM
                + ", skimmingTM=" + skimmingTM + ", treatingTM=" + treatingTM + ", heatingTM=" + heatingTM + ", pouringTM=" + pouringTM + ", dredgingTM=" + dredgingTM + '}';
    }
}
