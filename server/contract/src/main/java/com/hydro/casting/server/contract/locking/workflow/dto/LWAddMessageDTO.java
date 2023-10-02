package com.hydro.casting.server.contract.locking.workflow.dto;

import java.io.Serializable;

public class LWAddMessageDTO implements Serializable
{
    private static final long serialVersionUID = 1L;

    private LockingWorkflowDTO dto;
    private String message;

    public LWAddMessageDTO( LockingWorkflowDTO dto, String message )
    {
        super();
        this.dto = dto;
        this.message = message;
    }

    public LockingWorkflowDTO getDto()
    {
        return dto;
    }

    public void setDto( LockingWorkflowDTO dto )
    {
        this.dto = dto;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }

}
