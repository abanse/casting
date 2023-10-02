package com.hydro.casting.server.ejb.downtime.replication;

import com.hydro.casting.server.ejb.downtime.service.DowntimeRequestService;
import com.hydro.casting.server.ejb.main.replication.BaseCacheReplicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;

@Singleton
public class DowntimeRequestCacheReplicator extends BaseCacheReplicator
{
    private final static Logger log = LoggerFactory.getLogger( DowntimeRequestCacheReplicator.class );

    @EJB
    private DowntimeRequestService downtimeRequestService;

    @Override
    protected String getReplicationName()
    {
        return "downtimeRequest";
    }

    @Lock( LockType.READ )
    @Schedule( second = "5,15,25,35,45,55", minute = "*", hour = "*", persistent = false )
    public void replicate() throws InterruptedException
    {
        if ( !shouldRun() )
        {
            return;
        }

        long start = System.currentTimeMillis();
        log.debug( "replication start for " + getReplicationName() );

        downtimeRequestService.writeCompleteCache();

        log.debug( "replication end for " + getReplicationName() + " duration " + ( System.currentTimeMillis() - start ) + "ms" );
    }
}
