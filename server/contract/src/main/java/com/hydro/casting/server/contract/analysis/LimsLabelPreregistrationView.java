package com.hydro.casting.server.contract.analysis;

import com.hydro.casting.server.contract.dto.LimsLabelPreregistrationDTO;
import com.hydro.core.server.contract.workplace.TableMaintenanceProvider;

import javax.ejb.Remote;
import javax.ejb.Stateless;

@Remote
public interface LimsLabelPreregistrationView  extends TableMaintenanceProvider<LimsLabelPreregistrationDTO> {

}
