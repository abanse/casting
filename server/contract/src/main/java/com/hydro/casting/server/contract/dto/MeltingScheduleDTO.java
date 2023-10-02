package com.hydro.casting.server.contract.dto;

import com.hydro.core.common.util.StringTools;
import com.hydro.core.server.contract.workplace.dto.ViewDTO;

public class MeltingScheduleDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private long id;
    private String machine;
    private String charge;
    private String alloy;
    private int executionState;

    @Override
    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

    public String getMachine()
    {
        return machine;
    }

    public void setMachine( String machine )
    {
        this.machine = machine;
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

    public int getExecutionState()
    {
        return executionState;
    }

    public void setExecutionState( int executionState )
    {
        this.executionState = executionState;
    }

    public String getChargeWithoutYear()
    {
        if ( StringTools.isFilled( charge ) && charge.length() > 2 )
        {
            return charge.substring( 2 );
        }
        return null;
    }

    public void setChargeWithoutYear( String chargeWithoutYear )
    {
        // only getter needed
    }
}
