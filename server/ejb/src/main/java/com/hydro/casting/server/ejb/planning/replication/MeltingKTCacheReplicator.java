package com.hydro.casting.server.ejb.planning.replication;

import com.hydro.casting.server.ejb.main.replication.BaseCacheReplicator;
import com.hydro.casting.server.ejb.planning.service.MeltingKTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;

@Singleton
public class MeltingKTCacheReplicator extends BaseCacheReplicator
{
    private final static Logger log = LoggerFactory.getLogger( MeltingKTCacheReplicator.class );

    @EJB
    private MeltingKTService meltingKTService;

    @Override
    protected String getReplicationName()
    {
        return "meltingKT";
    }

    @Lock( LockType.READ )
    @Schedule( second = "15", minute = "*", hour = "*", persistent = false )
    public void replicate() throws InterruptedException
    {
        if ( !shouldRun() )
        {
            return;
        }

        long start = System.currentTimeMillis();
        log.debug( "replication start for " + getReplicationName() );

        meltingKTService.writeCompleteCache();

        log.debug( "replication end for " + getReplicationName() + " duration " + ( System.currentTimeMillis() - start ) + "ms" );
    }
}