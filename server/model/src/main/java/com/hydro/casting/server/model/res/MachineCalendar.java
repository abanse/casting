package com.hydro.casting.server.model.res;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Entity
@Table( name = "machine_calendar" )
public class MachineCalendar extends BaseEntity
{
    @Column( name = "start_ts" )
    private LocalDateTime startTS;
    @Column( name = "duration" )
    private int duration;
    @Column( name = "end_ts" )
    private LocalDateTime endTS;
    @Column( name = "description", length = 200)
    private String description;
    @ManyToOne
    @JoinColumn( name = "machine_oid" )
    private Machine machine;

    public LocalDateTime getStartTS()
    {
        return startTS;
    }

    public void setStartTS( LocalDateTime startTS )
    {
        this.startTS = startTS;
    }

    public int getDuration()
    {
        return duration;
    }

    public void setDuration( int duration )
    {
        this.duration = duration;
    }

    public LocalDateTime getEndTS()
    {
        return endTS;
    }

    public void setEndTS( LocalDateTime endTS )
    {
        this.endTS = endTS;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public Machine getMachine()
    {
        return machine;
    }

    public void setMachine( Machine machine )
    {
        this.machine = machine;
    }

    public void removeAllAssociations()
    {
        setMachine( null );
    }
}