package com.hydro.casting.server.ejb.planning.replication;

import com.hydro.casting.server.ejb.main.replication.BaseCacheReplicator;
import com.hydro.casting.server.ejb.planning.service.CasterDemandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;

@Singleton
public class CasterDemandCacheReplicator extends BaseCacheReplicator
{
    private final static Logger log = LoggerFactory.getLogger( CasterDemandCacheReplicator.class );

    @EJB
    private CasterDemandService casterDemandService;

    @Override
    protected String getReplicationName()
    {
        return "casterDemand";
    }

    @Lock( LockType.READ )
    @Schedule( second = "1,11,21,31,41,51", minute = "*", hour = "*", persistent = false )
    public void replicate() throws InterruptedException
    {
        if ( !shouldRun() )
        {
            return;
        }

        long start = System.currentTimeMillis();
        log.debug( "replication start for " + getReplicationName() );

        casterDemandService.writeCompleteCache();

        log.debug( "replication end for " + getReplicationName() + " duration " + ( System.currentTimeMillis() - start ) + "ms" );
    }
}