package com.hydro.casting.server.ejb.melting;

import com.hydro.casting.server.contract.dto.MeltingInstructionDTO;
import com.hydro.casting.server.contract.dto.MeltingScheduleDTO;
import com.hydro.casting.server.contract.melting.MeltingView;
import com.hydro.casting.server.ejb.melting.service.MeltingScheduleService;
import com.hydro.casting.server.ejb.melting.validation.MeltingValidator;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.core.server.contract.workplace.SearchType;
import com.hydro.core.server.contract.workplace.ViewModel;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

@Stateless
public class MeltingViewBean implements MeltingView
{
    @EJB
    private MeltingScheduleService meltingScheduleService;
    @EJB
    private MeltingValidator meltingValidator;

    @Override
    public List<MeltingScheduleDTO> loadBySearchType( SearchType searchType, Map<String, Object> parameters )
    {
        return meltingScheduleService.loadBySearchType( searchType, parameters );
    }

    @Override
    public ViewModel<MeltingInstructionDTO> validate( String currentUser, MeltingInstructionDTO meltingInstructionDTO ) throws BusinessException
    {
        return meltingValidator.validate( meltingInstructionDTO );
    }
}
