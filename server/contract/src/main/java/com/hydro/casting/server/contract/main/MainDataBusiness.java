package com.hydro.casting.server.contract.main;

import com.hydro.core.common.exception.BusinessException;

import javax.ejb.Remote;

@Remote
public interface MainDataBusiness
{
    String replicateMachines() throws BusinessException;

    String createCasterTestData() throws BusinessException;

    String createInspectionCategories() throws BusinessException;
}
