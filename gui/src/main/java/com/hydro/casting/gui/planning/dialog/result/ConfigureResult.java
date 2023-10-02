package com.hydro.casting.gui.planning.dialog.result;

public class ConfigureResult
{
    private Integer chargeCounter;
    private Integer castingSequence;
    private String charge;
    private String furnace;
    private Integer schedulableState;

    public Integer getChargeCounter()
    {
        return chargeCounter;
    }

    public void setChargeCounter( Integer chargeCounter )
    {
        this.chargeCounter = chargeCounter;
    }

    public Integer getCastingSequence()
    {
        return castingSequence;
    }

    public void setCastingSequence( Integer castingSequence )
    {
        this.castingSequence = castingSequence;
    }

    public String getCharge()
    {
        return charge;
    }

    public void setCharge( String charge )
    {
        this.charge = charge;
    }

    public String getFurnace()
    {
        return furnace;
    }

    public void setFurnace( String furnace )
    {
        this.furnace = furnace;
    }

    public Integer getSchedulableState()
    {
        return schedulableState;
    }

    public void setSchedulableState( Integer schedulableState )
    {
        this.schedulableState = schedulableState;
    }
}
