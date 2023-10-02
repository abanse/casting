package com.hydro.casting.server.ejb.melting.replication;

import com.hydro.casting.server.ejb.main.replication.BaseCacheReplicator;
import com.hydro.casting.server.ejb.melting.service.MeltingInstructionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;

@Singleton
public class MeltingInstructionCacheReplicator extends BaseCacheReplicator
{
    private final static Logger log = LoggerFactory.getLogger( MeltingInstructionCacheReplicator.class );

    @EJB
    MeltingInstructionService meltingInstructionService;

    @Override
    protected String getReplicationName()
    {
        return "meltingInstruction";
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

        meltingInstructionService.writeCompleteCache();

        log.debug( "replication end for " + getReplicationName() + " duration " + ( System.currentTimeMillis() - start ) + "ms" );
    }
}
