package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

public class CasterDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    @Override
    public long getId()
    {
        return 0;
    }

    public String getChargeWithoutYear()
    {
        return "?12345";
    }
}
