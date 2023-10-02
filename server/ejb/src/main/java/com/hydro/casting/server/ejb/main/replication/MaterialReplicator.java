package com.hydro.casting.server.ejb.main.replication;

import com.hydro.casting.server.ejb.main.service.MaterialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;

@Singleton
public class MaterialReplicator extends BaseCacheReplicator
{
    private final static Logger log = LoggerFactory.getLogger( MaterialReplicator.class );

    @EJB
    private MaterialService materialService;

    @Override
    protected String getReplicationName()
    {
        return "material";
    }

    @Lock( LockType.READ )
    @Schedule( second = "2,12,22,32,42,52", minute = "*", hour = "*", persistent = false )
    public void replicate() throws InterruptedException
    {
        if ( !shouldRun() )
        {
            return;
        }

        long start = System.currentTimeMillis();
        log.debug( "replication start for " + getReplicationName() );

        materialService.writeCompleteCache();

        log.debug( "replication end for " + getReplicationName() + " duration " + ( System.currentTimeMillis() - start ) + "ms" );
    }
}