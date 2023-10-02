package com.hydro.casting.server.ejb.planning.replication;

import com.hydro.casting.server.ejb.main.replication.BaseCacheReplicator;
import com.hydro.casting.server.ejb.planning.service.CasterDemandMaterialTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;

@Singleton
public class CasterDemandMaterialTypeCacheReplicator extends BaseCacheReplicator
{
    private final static Logger log = LoggerFactory.getLogger( CasterDemandMaterialTypeCacheReplicator.class );

    @EJB
    private CasterDemandMaterialTypeService casterDemandMaterialTypeService;

    @Override
    protected String getReplicationName()
    {
        return "casterDemandMaterialType";
    }

    @Lock( LockType.READ )
    @Schedule( second = "6,26,46", minute = "*", hour = "*", persistent = false )
    public void replicate() throws InterruptedException
    {
        if ( !shouldRun() )
        {
            return;
        }

        long start = System.currentTimeMillis();
        log.debug( "replication start for " + getReplicationName() );

        casterDemandMaterialTypeService.writeCompleteCache();

        log.debug( "replication end for " + getReplicationName() + " duration " + ( System.currentTimeMillis() - start ) + "ms" );
    }
}