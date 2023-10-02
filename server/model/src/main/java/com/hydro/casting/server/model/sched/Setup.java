package com.hydro.casting.server.model.sched;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Entity
@DiscriminatorValue( "setup" )
public class Setup extends Schedulable
{
    @Column( name = "duration" )
    private int duration;

    public int getDuration()
    {
        return duration;
    }

    public void setDuration( int duration )
    {
        this.duration = duration;
    }
}

