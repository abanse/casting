package com.hydro.casting.server.contract.planning;

import com.hydro.casting.server.contract.dto.CastingKTDTO;
import com.hydro.casting.server.contract.dto.MeltingKTDTO;
import com.hydro.core.server.contract.workplace.TableMaintenanceProvider;

import javax.ejb.Remote;

@Remote
public interface CastingKTTMP extends TableMaintenanceProvider<CastingKTDTO>
{
}
