package com.hydro.casting.server.ejb.analysis.replication;

import com.hydro.casting.server.contract.dto.AlloyDTO;
import com.hydro.casting.server.ejb.main.replication.BaseCacheReplicator;
import com.hydro.casting.server.ejb.main.service.AlloyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import java.util.List;

@Singleton
public class AlloyCacheReplicator extends BaseCacheReplicator
{
    private final static Logger log = LoggerFactory.getLogger( AnalysisCacheReplicator.class );

    @EJB
    private AlloyService alloyService;

    @Override
    protected String getReplicationName()
    {
        return "alloy";
    }

    @Lock( LockType.READ )
    @Schedule( second = "7,17,27,37,47,57", minute = "*", hour = "*", persistent = false )
    public void replicate()
    {
        if ( !shouldRun() )
        {
            return;
        }
        long start = System.currentTimeMillis();
        log.debug( "replication start for " + getReplicationName() );

        /*
         * Temporary solution to add caclulated alloy elements when the alloys are loaded into cache
         *
         * TODO: Remove when alloy management feature is introduced
         */
        List<AlloyDTO> dtos = alloyService.load();
        alloyService.loadCalculatedElementsTemp( dtos );
        alloyService.writeCompleteCache( dtos );

        log.debug( "replication end for " + getReplicationName() + " duration " + ( System.currentTimeMillis() - start ) + "ms" );
    }
}
