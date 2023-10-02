package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.Objects;

public class ProductionLogDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private long objid;
    private LocalDateTime eventTS;
    private String machineApk;
    private String refName;
    private String userName;
    private String message;

    @Override
    public long getId()
    {
        return objid;
    }

    public long getObjid()
    {
        return objid;
    }

    public void setObjid( long objid )
    {
        this.objid = objid;
    }

    public LocalDateTime getEventTS()
    {
        return eventTS;
    }

    public void setEventTS( LocalDateTime eventTS )
    {
        this.eventTS = eventTS;
    }

    public String getMachineApk()
    {
        return machineApk;
    }

    public void setMachineApk( String machineApk )
    {
        this.machineApk = machineApk;
    }

    public String getRefName()
    {
        return refName;
    }

    public void setRefName( String refName )
    {
        this.refName = refName;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName( String userName )
    {
        this.userName = userName;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        ProductionLogDTO that = (ProductionLogDTO) o;
        return objid == that.objid && Objects.equals( eventTS, that.eventTS ) && Objects.equals( machineApk, that.machineApk ) && Objects.equals( refName, that.refName ) && Objects.equals( userName,
                that.userName ) && Objects.equals( message, that.message );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( objid, eventTS, machineApk, refName, userName, message );
    }

    @Override
    public String toString()
    {
        return "ProductionLogDTO{" + "objid=" + objid + ", eventTS=" + eventTS + ", machineApk='" + machineApk + '\'' + ", refName='" + refName + '\'' + ", userName='" + userName + '\''
                + ", message='" + message + '\'' + '}';
    }
}
