package com.hydro.casting.gui.prod.dialog.result;

import com.hydro.casting.server.contract.dto.ChargeSpecificationDTO;
public class ChargingChangeSpecificationResult
{
    private ChargeSpecificationDTO chargeSpecificationDTO;

    public ChargeSpecificationDTO getChargeSpecificationDTO()
    {
        return chargeSpecificationDTO;
    }

    public void setChargeSpecificationDTO( ChargeSpecificationDTO chargeSpecificationDTO )
    {
        this.chargeSpecificationDTO = chargeSpecificationDTO;
    }
}
