package com.hydro.casting.server.contract.melting;

import com.hydro.casting.server.contract.dto.MeltingInstructionDTO;
import com.hydro.casting.server.contract.dto.MeltingScheduleDTO;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.core.server.contract.workplace.SearchType;
import com.hydro.core.server.contract.workplace.ViewModel;

import javax.ejb.Remote;
import java.util.List;
import java.util.Map;

@Remote
public interface MeltingView
{
    List<MeltingScheduleDTO> loadBySearchType( SearchType searchType, Map<String, Object> parameters );

    ViewModel<MeltingInstructionDTO> validate( String currentUser, MeltingInstructionDTO meltingInstructionDTO ) throws BusinessException;
}
