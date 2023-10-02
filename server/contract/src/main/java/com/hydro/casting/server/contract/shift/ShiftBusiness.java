package com.hydro.casting.server.contract.shift;

import com.hydro.casting.server.contract.shift.dto.ShiftModelDTO;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface ShiftBusiness
{
    List<ShiftModelDTO> loadShiftModels( List<String> costCenters );
}
