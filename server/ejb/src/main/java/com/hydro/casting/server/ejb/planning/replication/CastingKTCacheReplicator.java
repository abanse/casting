package com.hydro.casting.server.ejb.planning.replication;

import com.hydro.casting.server.ejb.main.replication.BaseCacheReplicator;
import com.hydro.casting.server.ejb.planning.service.CastingKTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;

@Singleton
public class CastingKTCacheReplicator extends BaseCacheReplicator
{
    private final static Logger log = LoggerFactory.getLogger( CastingKTCacheReplicator.class );

    @EJB
    private CastingKTService castingKTService;

    @Override
    protected String getReplicationName()
    {
        return "castingKT";
    }

    @Lock( LockType.READ )
    @Schedule( second = "45", minute = "*", hour = "*", persistent = false )
    public void replicate() throws InterruptedException
    {
        if ( !shouldRun() )
        {
            return;
        }

        long start = System.currentTimeMillis();
        log.debug( "replication start for " + getReplicationName() );

        castingKTService.writeCompleteCache();

        log.debug( "replication end for " + getReplicationName() + " duration " + ( System.currentTimeMillis() - start ) + "ms" );
    }
}