package com.hydro.casting.server.ejb.stock.replication;

import com.hydro.casting.server.ejb.main.replication.BaseCacheReplicator;
import com.hydro.casting.server.ejb.stock.service.CrucibleMaterialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;

@Singleton
public class CrucibleMaterialReplicator extends BaseCacheReplicator
{
    private final static Logger log = LoggerFactory.getLogger( CrucibleMaterialReplicator.class );

    @EJB
    private CrucibleMaterialService crucibleMaterialService;

    @Override
    protected String getReplicationName()
    {
        return "crucibleMaterial";
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

        crucibleMaterialService.writeCompleteCache();

        log.debug( "replication end for " + getReplicationName() + " duration " + ( System.currentTimeMillis() - start ) + "ms" );
    }
}