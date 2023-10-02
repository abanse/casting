package com.hydro.casting.server.ejb.prod.replication;

import com.hydro.casting.server.ejb.main.replication.BaseCacheReplicator;
import com.hydro.casting.server.ejb.prod.service.CasterInstructionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;

@Singleton
public class CasterInstructionCacheReplicator extends BaseCacheReplicator
{
    private final static Logger log = LoggerFactory.getLogger( CasterInstructionCacheReplicator.class );

    @EJB
    private CasterInstructionService casterInstructionService;

    @Override
    protected String getReplicationName()
    {
        return "casterInstruction";
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

        casterInstructionService.writeCompleteCache();

        log.debug( "replication end for " + getReplicationName() + " duration " + ( System.currentTimeMillis() - start ) + "ms" );
    }
}
