package com.hydro.casting.server.ejb.stock.replication;

import com.hydro.casting.server.ejb.main.replication.BaseCacheReplicator;
import com.hydro.casting.server.ejb.stock.service.SlabService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;

@Singleton
public class SlabReplicator extends BaseCacheReplicator
{
    private final static Logger log = LoggerFactory.getLogger( SlabReplicator.class );

    @EJB
    private SlabService slabService;

    @Override
    protected String getReplicationName()
    {
        return "slab";
    }

    @Lock( LockType.READ )
    @Schedule( second = "7,17,27,37,47,57", minute = "*", hour = "*", persistent = false )
    public void replicate() throws InterruptedException
    {
        if ( !shouldRun() )
        {
            return;
        }

        long start = System.currentTimeMillis();
        log.debug( "replication start for " + getReplicationName() );

        slabService.writeCompleteCache();

        log.debug( "replication end for " + getReplicationName() + " duration " + ( System.currentTimeMillis() - start ) + "ms" );
    }
}