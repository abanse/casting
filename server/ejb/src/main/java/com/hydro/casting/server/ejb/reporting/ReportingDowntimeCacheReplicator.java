package com.hydro.casting.server.ejb.reporting;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.downtime.dto.DowntimeKindDTO;
import com.hydro.casting.server.contract.reporting.dto.ReportingDowntimeDetailDTO;
import com.hydro.casting.server.ejb.reporting.data.ReportingDataProviderDowntime;
import com.hydro.casting.server.model.downtime.Downtime;
import com.hydro.casting.server.model.downtime.dao.DowntimeHome;
import com.hydro.core.common.cache.ServerCache;
import com.hydro.core.common.cache.ServerCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class replicates the ReportingDowntimeDetailDTOs of the last two months into the reporting cache.
 */
@Singleton
public class ReportingDowntimeCacheReplicator
{
    private final static Logger log = LoggerFactory.getLogger( ReportingDowntimeCacheReplicator.class );

    @EJB
    private ReportingDataProviderDowntime reportingDataProviderDowntime;

    private final static int DETAILED_DAYS_DOWNTIME = 62;

    //@formatter:off
    private final static String DETAILED_DOWNTIME_QUERY = 
              "   SELECT dt.objid as id, "
            + "          dt.costCenter as costCenter, "
            + "          dt.machine as machine, "
            + "          dt.shift as shift, "
            + "          dt.description as description, "
            + "          dt.fromTS as start, "
            + "          dt.endTS as end, "
            + "          dt.downtimeKind.objid as downtimeKindObjid, "
            + "          dt.downtimeKind.kind1 as downtimeKindKind1, "
            + "          dt.downtimeKind.kind2 as downtimeKindKind2, "
            + "          dt.downtimeKind.kind3 as downtimeKindKind3, "
            + "          dt.downtimeKind.description as downtimeKindDescription "
            + "     FROM Downtime dt "
            + "    WHERE dt.createTS >= :detailedDaysDowntime "
            + "      AND dt.objid > :highestObjid";
    //@formatter:on

    @Inject
    private ServerCacheManager serverCacheManager;

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    @EJB
    private DowntimeHome stillstaendeHome;

    private boolean clearFlag;

    @Lock( LockType.READ )
    public void replicateDetailedDays() throws InterruptedException
    {
        if ( clearFlag )
        {
            log.debug( "clear reporting downtime cache" );
            clearFlag = false;
            clearCache();
        }

        long start = System.currentTimeMillis();
        log.debug( "reporting (detailed downtime) replication start" );

        String cachePath = Casting.CACHE.REPORTING_DOWNTIME_DETAIL_KEY;
        String cachePathOpenDowntimes = cachePath + "/openDowntimes";
        String cachePathModifiedDowntimes = cachePath + "/modifiedDowntimes";
        ServerCache<ReportingDowntimeDetailDTO> reportingDowntimeDataCache = serverCacheManager.getCache( Casting.CACHE.REPORTING_DATA_DOWNTIME_CACHE_NAME );
        ServerCache<List<Long>> reportingDowntimeSequenceCache = serverCacheManager.getCache( Casting.CACHE.REPORTING_DATA_DOWNTIME_CACHE_NAME );

        // get current cache sequence
        List<Long> reportingDowntimeSequence = reportingDowntimeSequenceCache.get( cachePath );
        List<Long> reportingOpenDowntimeSequence = reportingDowntimeSequenceCache.get( cachePathOpenDowntimes );
        List<Long> reportingModifiedDowntimeSequence = reportingDowntimeSequenceCache.get( cachePathModifiedDowntimes );
        long highestObjid = 0;
        if ( reportingDowntimeSequence == null || reportingDowntimeSequence.isEmpty() )
        {
            reportingDowntimeSequence = new ArrayList<Long>();
        }
        else
        {
            highestObjid = Collections.max( reportingDowntimeSequence );
        }
        if ( reportingOpenDowntimeSequence == null || reportingOpenDowntimeSequence.isEmpty() )
        {
            reportingOpenDowntimeSequence = new ArrayList<Long>();
        }
        if ( reportingModifiedDowntimeSequence == null || reportingModifiedDowntimeSequence.isEmpty() )
        {
            reportingModifiedDowntimeSequence = new ArrayList<Long>();
        }

        final LocalDateTime minimalDate = LocalDateTime.now().minusDays( DETAILED_DAYS_DOWNTIME );

        final TypedQuery<Tuple> detailedDowntimeQuery = entityManager.createQuery( DETAILED_DOWNTIME_QUERY, Tuple.class );
        detailedDowntimeQuery.setParameter( "detailedDaysDowntime", minimalDate );
        detailedDowntimeQuery.setParameter( "highestObjid", highestObjid );

        @SuppressWarnings( "unchecked" )
        final List<Tuple> downtimeReportTuples = detailedDowntimeQuery.getResultList();
        for ( Tuple downtimeReportTuple : downtimeReportTuples )
        {
            final ReportingDowntimeDetailDTO reportingDowntimeDetailDTO = new ReportingDowntimeDetailDTO();
            reportingDowntimeDetailDTO.setId( downtimeReportTuple.get( "id", Long.class ) );
            reportingDowntimeDetailDTO.setStart( downtimeReportTuple.get( "start", LocalDateTime.class ) );
            reportingDowntimeDetailDTO.setEnd( downtimeReportTuple.get( "end", LocalDateTime.class ) );
            reportingDowntimeDetailDTO.setShift( downtimeReportTuple.get( "shift", String.class ) );
            reportingDowntimeDetailDTO.setDescription( downtimeReportTuple.get( "description", String.class ) );
            reportingDowntimeDetailDTO.setCostCenter( downtimeReportTuple.get( "costCenter", String.class ) );
            reportingDowntimeDetailDTO.setMachine( downtimeReportTuple.get( "machine", String.class ) );

            final DowntimeKindDTO downtimeKindDTO = new DowntimeKindDTO();
            downtimeKindDTO.setId( downtimeReportTuple.get( "downtimeKindObjid", Long.class ) );
            downtimeKindDTO.setDowntimeKind1( downtimeReportTuple.get( "downtimeKindKind1", String.class ) );
            downtimeKindDTO.setDowntimeKind2( downtimeReportTuple.get( "downtimeKindKind2", String.class ) );
            downtimeKindDTO.setDowntimeKind3( downtimeReportTuple.get( "downtimeKindKind3", String.class ) );
            downtimeKindDTO.setDescription( downtimeReportTuple.get( "downtimeKindDescription", String.class ) );
            reportingDowntimeDetailDTO.setDowntimeKind( downtimeKindDTO );

            final String cacheDowntimeKey = cachePath + "/data/" + reportingDowntimeDetailDTO.getId();

            if ( reportingDowntimeDetailDTO.getEnd() == null || reportingDowntimeDetailDTO.getEnd().isAfter( minimalDate ) )
            {
                if ( reportingDowntimeDetailDTO.getEnd() == null )
                {
                    if ( !reportingOpenDowntimeSequence.contains( reportingDowntimeDetailDTO.getId() ) )
                    {
                        reportingOpenDowntimeSequence.add( reportingDowntimeDetailDTO.getId() );
                    }
                }

                if ( reportingDowntimeDataCache.containsKey( cacheDowntimeKey ) )
                {
                    if ( !reportingDowntimeDataCache.get( cacheDowntimeKey ).equals( reportingDowntimeDetailDTO ) )
                    {
                        reportingDowntimeDataCache.put( cacheDowntimeKey, reportingDowntimeDetailDTO );
                    }
                }
                else
                {
                    reportingDowntimeDataCache.put( cacheDowntimeKey, reportingDowntimeDetailDTO );
                    reportingDowntimeSequence.add( reportingDowntimeDetailDTO.getId() );
                }
            }
        }

        List<Long> currentReportingDowntimeSequence = reportingDowntimeSequenceCache.get( cachePath );
        List<Long> currentReportingOpenDowntimeSequence = reportingDowntimeSequenceCache.get( cachePathOpenDowntimes );

        if ( currentReportingDowntimeSequence == null || !currentReportingDowntimeSequence.equals( reportingDowntimeSequence ) )
        {
            reportingDowntimeSequenceCache.put( cachePath, reportingDowntimeSequence );
        }
        if ( currentReportingOpenDowntimeSequence == null || !currentReportingOpenDowntimeSequence.equals( reportingOpenDowntimeSequence ) )
        {
            reportingDowntimeSequenceCache.put( cachePathOpenDowntimes, reportingOpenDowntimeSequence );
        }

        if ( !reportingOpenDowntimeSequence.isEmpty() )
        {
            checkOpenDowntimes();
        }
        if ( !reportingModifiedDowntimeSequence.isEmpty() )
        {
            updateModifiedDowntimes( reportingModifiedDowntimeSequence );
            reportingDowntimeSequenceCache.put( cachePathModifiedDowntimes, new ArrayList<>() );
        }

        log.debug( "reporting (detailed downtime) replication end duration " + ( System.currentTimeMillis() - start ) + "ms" );
    }

    private void checkOpenDowntimes()
    {
        String cachePath = Casting.CACHE.REPORTING_DOWNTIME_DETAIL_KEY;
        String cachePathOpenDowntimes = cachePath + "/openDowntimes";
        ServerCache<ReportingDowntimeDetailDTO> reportingDowntimeDataCache = serverCacheManager.getCache( Casting.CACHE.REPORTING_DATA_DOWNTIME_CACHE_NAME );
        ServerCache<List<Long>> reportingDowntimeSequenceCache = serverCacheManager.getCache( Casting.CACHE.REPORTING_DATA_DOWNTIME_CACHE_NAME );

        // get current cache sequence
        List<Long> reportingOpenDowntimeSequence = new ArrayList<>( reportingDowntimeSequenceCache.get( cachePathOpenDowntimes ) );
        List<Downtime> closedDowntimes = new ArrayList<>();
        List<Long> deletedDowntimes = new ArrayList<>();

        // find downtimes which are closed or deleted
        reportingOpenDowntimeSequence.forEach( serial -> {
            Downtime downtime = stillstaendeHome.findById( serial );
            if ( downtime == null )
            {
                deletedDowntimes.add( serial );
            }
            else if ( downtime.getEndTS() != null )
            {
                closedDowntimes.add( downtime );
                log.info( "Downtime closed: " + downtime.getObjid() );
            }
        } );
        // remove deleted downtimes from cache
        deletedDowntimes.forEach( serial -> {
            String key = cachePath + "/data/" + serial;
            ReportingDowntimeDetailDTO detailDTO = reportingDowntimeDataCache.get( key );
            if ( detailDTO != null )
            {
                log.info( "Downtime deleted: " + detailDTO );
                deleteDetailEntries( new ArrayList<ReportingDowntimeDetailDTO>() );
                List<ReportingDowntimeDetailDTO> entriesToDelete = new ArrayList<>();
                entriesToDelete.add( detailDTO );
                deleteDetailEntries( entriesToDelete );
            }
            reportingOpenDowntimeSequence.remove( serial );
        } );
        // update closed downtimes
        closedDowntimes.forEach( downtime -> {
            reportingOpenDowntimeSequence.remove( downtime.getObjid() );
            String key = cachePath + "/data/" + downtime.getObjid();
            ReportingDowntimeDetailDTO detailDTO = reportingDowntimeDataCache.get( key );
            if ( detailDTO != null )
            {
                detailDTO.setEnd( downtime.getEndTS() );
                reportingDowntimeDataCache.put( key, detailDTO );
            }
        } );
        reportingDowntimeSequenceCache.put( cachePathOpenDowntimes, reportingOpenDowntimeSequence );
    }

    private void updateModifiedDowntimes( List<Long> reportingModifiedDowntimeSequence )
    {
        List<Long> deletedDowntimes = new ArrayList<>();
        List<Downtime> modifiedDowntimes = new ArrayList<>();

        // find downtimes which are closed or modified
        reportingModifiedDowntimeSequence.forEach( serial -> {
            Downtime downtime = stillstaendeHome.findById( serial );
            if ( downtime == null )
            {
                deletedDowntimes.add( serial );
            }
            else if ( downtime.getEndTS() != null )
            {
                modifiedDowntimes.add( downtime );
            }
        } );
        if ( !deletedDowntimes.isEmpty() )
        {
            deleteDetailEntriesBySerial( deletedDowntimes );
        }
        if ( !modifiedDowntimes.isEmpty() )
        {
            updateDetailEntries( modifiedDowntimes );
        }
    }

    public void clearCache()
    {
        serverCacheManager.getCache( Casting.CACHE.REPORTING_DATA_DOWNTIME_CACHE_NAME ).clear();
    }

    //    public void deleteOldDetailedDays()
    //    {
    //        long start = System.currentTimeMillis();
    //        log.info( "reporting (detailed downtime) delete start" );
    //
    //        Calendar minimalDate = Calendar.getInstance();
    //        minimalDate.add( Calendar.DAY_OF_MONTH, -DETAILED_DAYS_DOWNTIME );
    //        List<ReportingDowntimeDetailDTO> entriesToDelete = new ArrayList<ReportingDowntimeDetailDTO>();
    //        getAllDetailDTOsFromCache().forEach( detail -> {
    //            if ( minimalDate.getTime().after( detail.getEnd() ) )
    //            {
    //                entriesToDelete.add( detail );
    //            }
    //        } );
    //        if ( !entriesToDelete.isEmpty() )
    //        {
    //            deleteDetailEntries( entriesToDelete );
    //        }
    //
    //        log.info( "reporting (detailed downtime) delete end duration " + ( System.currentTimeMillis() - start ) + "ms" );
    //    }

    //    private List<ReportingDowntimeDetailDTO> getAllDetailDTOsFromCache()
    //    {
    //        List<ReportingDowntimeDetailDTO> detailDTOs = new ArrayList<ReportingDowntimeDetailDTO>();
    //        String cachePath = BDE.CACHE.REPORTING_DOWNTIME_DETAIL_KEY;
    //        ServerCache<ReportingDowntimeDetailDTO> reportingDowntimeDataCache = serverCacheManager.getCache( BDE.CACHE.REPORTING_DATA_DOWNTIME );
    //        ServerCache<List<Long>> reportingDowntimeSequenceCache = serverCacheManager.getCache( BDE.CACHE.REPORTING_DATA_DOWNTIME );
    //        List<Long> reportingDowntimeSequence = reportingDowntimeSequenceCache.get( cachePath );
    //
    //        if ( reportingDowntimeSequence != null && !reportingDowntimeSequence.isEmpty() )
    //        {
    //            reportingDowntimeSequence.forEach( serial -> {
    //                final String cacheDowntimeKey = cachePath + "/data/" + serial;
    //                detailDTOs.add( reportingDowntimeDataCache.get( cacheDowntimeKey ) );
    //            } );
    //        }
    //
    //        return detailDTOs;
    //    }

    private void deleteDetailEntries( List<ReportingDowntimeDetailDTO> detailDTOs )
    {
        List<Long> serials = new ArrayList<>();
        detailDTOs.forEach( detailDTO -> serials.add( detailDTO.getId() ) );
        deleteDetailEntriesBySerial( serials );
    }

    private void deleteDetailEntriesBySerial( List<Long> serials )
    {
        String cachePath = Casting.CACHE.REPORTING_DOWNTIME_DETAIL_KEY;
        ServerCache<ReportingDowntimeDetailDTO> reportingDowntimeDataCache = serverCacheManager.getCache( Casting.CACHE.REPORTING_DATA_DOWNTIME_CACHE_NAME );
        ServerCache<List<Long>> reportingDowntimeSequenceCache = serverCacheManager.getCache( Casting.CACHE.REPORTING_DATA_DOWNTIME_CACHE_NAME );
        List<Long> reportingDowntimeSequence = reportingDowntimeSequenceCache.get( cachePath );

        serials.forEach( serial -> {
            final String cacheDowntimeKey = cachePath + "/data/" + serial;
            reportingDowntimeDataCache.remove( cacheDowntimeKey );
            reportingDowntimeSequence.remove( serial );
        } );
        reportingDowntimeSequenceCache.put( cachePath, reportingDowntimeSequence );
        reportingDataProviderDowntime.loadAllDetailDTOsFromCache();
    }

    private void updateDetailEntries( List<Downtime> downtimes )
    {
        String cachePath = Casting.CACHE.REPORTING_DOWNTIME_DETAIL_KEY;
        ServerCache<ReportingDowntimeDetailDTO> reportingDowntimeDataCache = serverCacheManager.getCache( Casting.CACHE.REPORTING_DATA_DOWNTIME_CACHE_NAME );

        List<ReportingDowntimeDetailDTO> detailDTOs = new ArrayList<>();
        downtimes.forEach( downtime -> detailDTOs.add( createDetailDTO( downtime ) ) );
        detailDTOs.forEach( detailDTO -> reportingDowntimeDataCache.put( cachePath + "/data/" + detailDTO.getId(), detailDTO ) );
        System.out.println( "updated " + detailDTOs.size() + " downtimes" );
    }

    private ReportingDowntimeDetailDTO createDetailDTO( Downtime downtime )
    {
        ReportingDowntimeDetailDTO reportingDowntimeDetailDTO = new ReportingDowntimeDetailDTO();
        reportingDowntimeDetailDTO.setId( downtime.getObjid() );
        reportingDowntimeDetailDTO.setCostCenter( downtime.getCostCenter() );
        reportingDowntimeDetailDTO.setMachine( downtime.getMachine() );
        reportingDowntimeDetailDTO.setDescription( downtime.getDescription() );
        reportingDowntimeDetailDTO.setShift( downtime.getShift() );

        DowntimeKindDTO downtimeKindDTO = new DowntimeKindDTO();
        downtimeKindDTO.setId( downtime.getDowntimeKind().getObjid() );
        downtimeKindDTO.setDowntimeKind1( downtime.getDowntimeKind().getKind1() );
        downtimeKindDTO.setDowntimeKind2( downtime.getDowntimeKind().getKind2() );
        downtimeKindDTO.setDowntimeKind3( downtime.getDowntimeKind().getKind3() );
        downtimeKindDTO.setDescription( downtime.getDowntimeKind().getDescription() );
        reportingDowntimeDetailDTO.setDowntimeKind( downtimeKindDTO );
        reportingDowntimeDetailDTO.setStart( downtime.getFromTS() );
        if ( downtime.getEndTS() != null )
        {
            reportingDowntimeDetailDTO.setEnd( downtime.getEndTS() );
        }

        return reportingDowntimeDetailDTO;
    }

    public void setClearFlag( boolean clearFlag )
    {
        this.clearFlag = clearFlag;
    }
}