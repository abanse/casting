package com.hydro.casting.server.contract.main;

import com.hydro.casting.server.contract.dto.MaterialTypeDTO;
import com.hydro.core.server.contract.workplace.TableMaintenanceProvider;

import javax.ejb.Remote;

@Remote
public interface MaterialTypeTMP extends TableMaintenanceProvider<MaterialTypeDTO>
{
}
