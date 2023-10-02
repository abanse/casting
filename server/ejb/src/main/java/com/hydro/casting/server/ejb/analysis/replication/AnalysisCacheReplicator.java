package com.hydro.casting.server.ejb.analysis.replication;

import com.hydro.casting.server.contract.dto.AnalysisDTO;
import com.hydro.casting.server.ejb.analysis.service.AnalysisService;
import com.hydro.casting.server.ejb.analysis.service.LimsAnalysisService;
import com.hydro.casting.server.ejb.main.replication.BaseCacheReplicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import java.util.List;

@Singleton
public class AnalysisCacheReplicator extends BaseCacheReplicator
{
    private final static Logger log = LoggerFactory.getLogger( AnalysisCacheReplicator.class );

    @EJB
    private LimsAnalysisService limsAnalysisService;

    @EJB
    private AnalysisService analysisService;

    @Override
    protected String getReplicationName()
    {
        return "analysis";
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

        final List<AnalysisDTO> analysisDTOs;
        if ( isMESAnalysisEnabled() )
        {
            analysisDTOs = analysisService.load();
        }
        else
        {
            analysisDTOs = limsAnalysisService.load();
            limsAnalysisService.compareDTOsWithCache( analysisDTOs );
        }

        limsAnalysisService.writeCompleteCache( analysisDTOs );

        log.debug( "replication end for " + getReplicationName() + " duration " + ( System.currentTimeMillis() - start ) + "ms" );
    }

    private boolean isMESAnalysisEnabled()
    {
        final String mesAnalysisEnabled = System.getProperty( "casting.analysis.mes.enabled", "false" );
        return "true".equalsIgnoreCase( mesAnalysisEnabled );
    }
}
