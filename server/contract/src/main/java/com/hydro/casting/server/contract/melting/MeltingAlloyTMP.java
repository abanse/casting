package com.hydro.casting.server.contract.melting;

import com.hydro.casting.server.contract.dto.AlloyDTO;
import com.hydro.core.server.contract.workplace.TableMaintenanceProvider;

import javax.ejb.Remote;

@Remote
public interface MeltingAlloyTMP extends TableMaintenanceProvider<AlloyDTO>
{
}
