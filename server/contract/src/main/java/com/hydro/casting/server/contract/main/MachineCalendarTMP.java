package com.hydro.casting.server.contract.main;

import com.hydro.casting.server.contract.dto.MachineCalendarDTO;
import com.hydro.core.server.contract.workplace.TableMaintenanceProvider;

import javax.ejb.Remote;

@Remote
public interface MachineCalendarTMP extends TableMaintenanceProvider<MachineCalendarDTO>
{
}
