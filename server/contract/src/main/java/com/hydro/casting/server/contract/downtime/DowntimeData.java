package com.hydro.casting.server.contract.downtime;

import com.hydro.core.common.exception.BusinessException;

import javax.ejb.Remote;

@Remote
public interface DowntimeData
{
    String replicateDowntimeData() throws BusinessException;
}
