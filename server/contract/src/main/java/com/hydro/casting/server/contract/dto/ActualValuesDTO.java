package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

public class ActualValuesDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private long id;
    private String charge;

    private byte[] values;
    private byte[] additionalValues;

    @Override
    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

    public String getCharge()
    {
        return charge;
    }

    public void setCharge( String charge )
    {
        this.charge = charge;
    }

    public byte[] getValues()
    {
        return values;
    }

    public void setValues( byte[] values )
    {
        this.values = values;
    }

    public byte[] getAdditionalValues()
    {
        return additionalValues;
    }

    public void setAdditionalValues( byte[] additionalValues )
    {
        this.additionalValues = additionalValues;
    }
}
