package com.hydro.casting.server.contract.locking.workflow.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.util.List;

public class LockHistoryDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private long id;

    private List<LockHistoryElementDTO> lockHistoryElements;

    @Override
    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

    public List<LockHistoryElementDTO> getLockHistoryElements()
    {
        return lockHistoryElements;
    }

    public void setLockHistoryElements( List<LockHistoryElementDTO> lockHistoryElements )
    {
        this.lockHistoryElements = lockHistoryElements;
    }

}
