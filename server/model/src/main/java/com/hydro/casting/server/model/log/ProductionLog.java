package com.hydro.casting.server.model.log;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table( name = "production_log" )
public class ProductionLog extends BaseEntity
{
    @Column( name = "event_ts" )
    private LocalDateTime eventTS;
    @Column( name = "machine_apk", length = 20 )
    private String machineApk;
    @Column( name = "ref_name", length = 30 )
    private String refName;
    @Column( name = "user_name", length = 30 )
    private String userName;
    @Column( name = "message", length = 200 )
    private String message;

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
}

