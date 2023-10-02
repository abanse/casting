package com.hydro.casting.server.contract.analysis;

import com.hydro.casting.server.contract.dto.AnalysisDTO;
import com.hydro.core.server.contract.workplace.MasterDetailProvider;

import javax.ejb.Remote;

@Remote
public interface AnalysisManagementView extends MasterDetailProvider<AnalysisDTO>
{
}
