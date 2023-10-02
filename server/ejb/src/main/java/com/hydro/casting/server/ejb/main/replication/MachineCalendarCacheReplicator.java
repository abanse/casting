package com.hydro.casting.server.ejb.main.replication;

import com.hydro.casting.server.ejb.main.service.MachineCalendarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;

@Singleton
public class MachineCalendarCacheReplicator extends BaseCacheReplicator
{
    private final static Logger log = LoggerFactory.getLogger( MachineCalendarCacheReplicator.class );

    @EJB
    private MachineCalendarService machineCalendarService;

    @Override
    protected String getReplicationName()
    {
        return "machineCalendar";
    }

    @Lock( LockType.READ )
    @Schedule( second = "25", minute = "*", hour = "*", persistent = false )
    public void replicate() throws InterruptedException
    {
        if ( !shouldRun() )
        {
            return;
        }

        long start = System.currentTimeMillis();
        log.debug( "replication start for " + getReplicationName() );

        machineCalendarService.writeCompleteCache();

        log.debug( "replication end for " + getReplicationName() + " duration " + ( System.currentTimeMillis() - start ) + "ms" );
    }
}
