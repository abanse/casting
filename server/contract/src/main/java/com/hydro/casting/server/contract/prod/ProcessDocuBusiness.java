package com.hydro.casting.server.contract.prod;

import com.hydro.casting.server.contract.dto.InspectionDTO;
import com.hydro.core.common.exception.BusinessException;

import javax.ejb.Remote;

@Remote
public interface ProcessDocuBusiness
{
    void saveInspection( String currentUser, InspectionDTO inspectionDTO ) throws BusinessException;
}
