package com.hydro.casting.server.ejb.downtime.replication;

import com.hydro.casting.common.Casting;
import com.hydro.casting.common.downtime.DowntimeConstants;
import com.hydro.casting.server.contract.downtime.dto.DowntimeDTO;
import com.hydro.core.common.cache.ServerCache;
import com.hydro.core.common.cache.ServerCacheManager;
import com.hydro.core.server.common.model.AliasToBeanTrimCharResultTransformer;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class fills the downtime cache with all downtimes from the last week. It is called by the
 * ReplicateDowntimeTimerJob class.
 */
@Singleton
public class DowntimeCacheReplicator
{
    private final static Logger log = LoggerFactory.getLogger( DowntimeCacheReplicator.class );

    //@formatter:off
    private final static String DOWNTIME_QUERY = 
              "   SELECT dt.objid as id, "
            + "          dt.costCenter as costCenter, "
            + "          dt.machine as machine, "
            + "          dt.fromTS as fromTS, "
            + "          dt.endTS as endTS, "
            + "          dt.shift as shift, "
            + "          dt.amount as amount, "
            + "          dt.description as description, "
            + "          dt.userId as userId, "
            + "          dt.remark as remark, "
            + "          dt.type as type, "
            + "          dtk.kind1 as downtimeKind1, "
            + "          dtk.kind2 as downtimeKind2, "
            + "          dtk.kind3 as downtimeKind3, "
            + "          dtk.description as downtimeDescription, "
            + "          dtm.module as module, "
            + "          dtm.component as component, "
            + "          dtm.description as moduleDescription, "
            + "          dtm.erpIdent as moduleErpIdent "
            + "     FROM Downtime dt left outer join dt.downtimeKind as dtk left outer join dt.downtimeModule as dtm "
            + "    WHERE dt.costCenter = :costCenter"
            + "      AND dt.fromTS > :beginTS ";
    //@formatter:on
    private final static String DOWNTIME_QUERY_MACHINE = " AND dt.machine = :machine";
    private final static String DOWNTIME_QUERY_ORDER_BY = " ORDER BY dt.fromTS ";

    @Inject
    private ServerCacheManager serverCacheManager;

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    @Lock( LockType.READ )
    public void replicate( String costCenter ) throws InterruptedException
    {
        //        String machine = "";
        if ( costCenter == null )
        {
            log.info( "costCenter is null (no replication)", new Exception( "from here" ) );
            return;
        }

        //        if ( costCenter.length() > 2 )
        //        {
        //            machine = costCenter.substring( 2 );
        //            costCenter = costCenter.substring( 0, 2 );
        //        }

        long start = System.currentTimeMillis();
        log.debug( "Downtime replication start for " + costCenter );

        StringBuilder queryBuilder = new StringBuilder( DOWNTIME_QUERY );
        //        if ( StringTools.isFilled( machine ) )
        //        {
        //            queryBuilder.append( DOWNTIME_QUERY_MACHINE );
        //        }
        queryBuilder.append( DOWNTIME_QUERY_ORDER_BY );

        final Query downtimeQuery = ( (Session) entityManager.getDelegate() ).createQuery( queryBuilder.toString() );
        downtimeQuery.setParameter( "costCenter", costCenter );
        //        if ( StringTools.isFilled( machine ) )
        //        {
        //            downtimeQuery.setParameter( "machine", machine );
        //        }
        downtimeQuery.setParameter( "beginTS", LocalDateTime.now().minusDays( DowntimeConstants.REPLICATION.DAYS_TO_READ ) );

        downtimeQuery.setResultTransformer( new AliasToBeanTrimCharResultTransformer( DowntimeDTO.class ) );

        @SuppressWarnings( "unchecked" )
        List<DowntimeDTO> downtimes = downtimeQuery.list();

        //        String cachePath = Casting.CACHE.DOWNTIME_KEY + "/" + costCenter + machine;
        String cachePath = Casting.CACHE.DOWNTIME_KEY + "/" + costCenter;
        ServerCache<DowntimeDTO> downtimeDataCache = serverCacheManager.getCache( Casting.CACHE.DOWNTIME_CACHE_NAME );
        ServerCache<List<Long>> downtimeSequenceCache = serverCacheManager.getCache( Casting.CACHE.DOWNTIME_CACHE_NAME );

        // get current cache sequence
        final List<Long> cacheDowntimeSequence = downtimeSequenceCache.get( cachePath );
        final List<Long> downtimeSequence;
        if ( cacheDowntimeSequence != null )
        {
            downtimeSequence = new ArrayList<>( cacheDowntimeSequence );
        }
        else
        {
            downtimeSequence = new ArrayList<>();
        }

        for ( DowntimeDTO downtimeDTO : downtimes )
        {
            final String cacheDowntimeKey = cachePath + "/data/" + downtimeDTO.getId();
            if ( downtimeSequence != null && downtimeSequence.contains( downtimeDTO.getId() ) )
            {
                downtimeSequence.remove( downtimeDTO.getId() );
            }

            if ( downtimeDataCache.containsKey( cacheDowntimeKey ) )
            {
                if ( !downtimeDataCache.get( cacheDowntimeKey ).equals( downtimeDTO ) )
                {
                    downtimeDataCache.put( cacheDowntimeKey, downtimeDTO );
                }
            }
            else
            {
                downtimeDataCache.put( cacheDowntimeKey, downtimeDTO );
            }
        }

        // delete old cache entries
        if ( downtimeSequence != null && !downtimeSequence.isEmpty() )
        {
            for ( Long serial : downtimeSequence )
            {
                final String cacheDowntimeKey = cachePath + "/data/" + serial;
                downtimeDataCache.remove( cacheDowntimeKey );
            }
        }

        // put new cache sequence
        List<Long> oldDowntimeSequence = downtimeSequenceCache.get( cachePath );
        List<Long> newDowntimeSequence = createDowntimeSequence( downtimes );
        if ( oldDowntimeSequence == null || Arrays.equals( oldDowntimeSequence.toArray(), newDowntimeSequence.toArray() ) == false )
        {
            downtimeSequenceCache.put( cachePath, newDowntimeSequence );
        }

        log.debug( "Downtime replication end for " + costCenter + " duration " + ( System.currentTimeMillis() - start ) + "ms" );
    }

    /**
     * Creates a list with the serials of the elements in downtimes.
     *
     * @param downtimes the list with the DowntimeDTOs
     * @return a list with the serials from downtimes
     */
    private List<Long> createDowntimeSequence( List<DowntimeDTO> downtimes )
    {
        List<Long> downtimeSequence = new ArrayList<>();
        if ( downtimes != null && !downtimes.isEmpty() )
        {
            for ( DowntimeDTO downtimeDTO : downtimes )
            {
                downtimeSequence.add( downtimeDTO.getId() );
            }
        }

        return downtimeSequence;
    }
}