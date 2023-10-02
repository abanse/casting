package com.hydro.casting.server.ejb.planning.replication;

import com.hydro.casting.server.ejb.main.replication.BaseCacheReplicator;
import com.hydro.casting.server.ejb.planning.service.CasterScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;

@Singleton
public class CasterScheduleCacheReplicator extends BaseCacheReplicator
{
    private final static Logger log = LoggerFactory.getLogger( CasterScheduleCacheReplicator.class );

    @EJB
    private CasterScheduleService casterScheduleService;

    @Override
    protected String getReplicationName()
    {
        return "casterSchedule";
    }

    @Lock( LockType.READ )
    @Schedule( second = "3,13,23,33,43,53", minute = "*", hour = "*", persistent = false )
    public void replicate() throws InterruptedException
    {
        if ( !shouldRun() )
        {
            return;
        }

        long start = System.currentTimeMillis();
        log.debug( "replication start for " + getReplicationName() );

        casterScheduleService.writeCompleteCache();

        log.debug( "replication end for " + getReplicationName() + " duration " + ( System.currentTimeMillis() - start ) + "ms" );
    }
}