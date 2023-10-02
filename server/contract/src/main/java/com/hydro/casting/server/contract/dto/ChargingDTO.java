package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

public class ChargingDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private String furnace;
    private String charge;
    private String alloy;
    private double plannedWeight;
    private double utilization;
    private double bottomWeight;
    private double solidMetal;
    private double liquidMetal;

    @Override
    public long getId()
    {
        return 0;
    }

    public String getFurnace()
    {
        return furnace;
    }

    public void setFurnace( String furnace )
    {
        this.furnace = furnace;
    }

    public String getCharge()
    {
        return charge;
    }

    public void setCharge( String charge )
    {
        this.charge = charge;
    }

    public String getAlloy()
    {
        return alloy;
    }

    public void setAlloy( String alloy )
    {
        this.alloy = alloy;
    }

    public double getPlannedWeight()
    {
        return plannedWeight;
    }

    public void setPlannedWeight( double plannedWeight )
    {
        this.plannedWeight = plannedWeight;
    }

    public double getUtilization()
    {
        return utilization;
    }

    public void setUtilization( double utilization )
    {
        this.utilization = utilization;
    }

    public double getBottomWeight()
    {
        return bottomWeight;
    }

    public void setBottomWeight( double bottomWeight )
    {
        this.bottomWeight = bottomWeight;
    }

    public double getSolidMetal()
    {
        return solidMetal;
    }

    public void setSolidMetal( double solidMetal )
    {
        this.solidMetal = solidMetal;
    }

    public double getLiquidMetal()
    {
        return liquidMetal;
    }

    public void setLiquidMetal( double liquidMetal )
    {
        this.liquidMetal = liquidMetal;
    }
}
