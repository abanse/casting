package com.hydro.casting.gui.downtime.data;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hydro.casting.common.Casting;
import com.hydro.casting.common.downtime.DowntimeConstants;
import com.hydro.casting.gui.reporting.data.ShiftTimeDataProvider;
import com.hydro.casting.server.contract.downtime.DowntimeView;
import com.hydro.casting.server.contract.downtime.dto.DowntimeDTO;
import com.hydro.casting.server.contract.shift.dto.ShiftModelDTO;
import com.hydro.core.common.cache.ClientCache;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.core.common.util.DateTimeUtil;
import com.hydro.core.common.util.StringTools;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.CacheManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Singleton
public class DowntimeDataProvider
{
    @Inject
    private BusinessManager businessManager;

    @Inject
    private CacheManager cacheManager;

    @Inject
    private ShiftTimeDataProvider shiftTimeDataProvider;

    ClientCache<DowntimeDTO> downtimeDataCache;
    ClientCache<List<Long>> downtimeSequenceCache;

    @Inject
    private void init()
    {
        downtimeDataCache = cacheManager.getCache( Casting.CACHE.DOWNTIME_CACHE_NAME );
        downtimeSequenceCache = cacheManager.getCache( Casting.CACHE.DOWNTIME_CACHE_NAME );
    }

    /**
     * This method creates and returns a list with the current DowntimeDTOs in the cache. Sorted by start time
     *
     * @return a list with the DowntimeDTOs
     */
    public List<DowntimeDTO> getAllDataFromCache()
    {
        return getCacheDataForCostCenters( DowntimeConstants.REPLICATION.REPLICATED_COST_CENTERS );
    }

    public List<DowntimeDTO> getCacheDataForCostCenter( String costCenter ) throws BusinessException
    {
        if ( Stream.of( DowntimeConstants.REPLICATION.REPLICATED_COST_CENTERS ).noneMatch( cc -> cc.equals( costCenter ) ) )
        {
            throw new BusinessException( "costCenter not in downtime replication" );
        }

        return getCacheDataForCostCenters( costCenter );
    }

    public List<DowntimeDTO> getCacheDataForCostCenters( String... costCenters )
    {
        List<DowntimeDTO> downtimes = new ArrayList<>();

        for ( String costCenter : costCenters )
        {
            if ( Stream.of( DowntimeConstants.REPLICATION.REPLICATED_COST_CENTERS ).noneMatch( cc -> cc.equals( costCenter ) ) )
            {
                // throw new BusinessException( "costCenter not in downtime replication" );
                continue;
            }
            String cachePath = Casting.CACHE.DOWNTIME_KEY + "/" + costCenter;
            List<Long> downtimeSequence = downtimeSequenceCache.get( cachePath );

            if ( downtimeSequence != null )
            {
                downtimes.addAll( downtimeDataCache.getAll( cachePath + "/data/", downtimeSequence ).values() );
                sortDowntimeList( downtimes );
            }
        }
        return downtimes;
    }

    public List<DowntimeDTO> getDataForInterval( LocalDateTime start, LocalDateTime end )
    {
        List<DowntimeDTO> data;
        LocalDateTime cacheStart = LocalDateTime.of( LocalDate.now().minusDays( DowntimeConstants.REPLICATION.DAYS_TO_READ ), LocalTime.MIN );

        if ( cacheStart.isBefore( start ) || cacheStart.isEqual( start ) )
        {
            data = filterDataForInterval( getAllDataFromCache(), start, end );
        }
        else
        {
            DowntimeView downtimeView = businessManager.getSession( DowntimeView.class );
            data = downtimeView.loadDowntimes( start, end );
        }

        return data;
    }

    private List<DowntimeDTO> filterDataForInterval( List<DowntimeDTO> downtimes, LocalDateTime start, LocalDateTime end )
    {
        List<DowntimeDTO> data = new ArrayList<>();
        for ( DowntimeDTO downtimeDTO : downtimes )
        {
            LocalDateTime downtimeStart = downtimeDTO.getFromTS();
            LocalDateTime downtimeEnd = downtimeDTO.getEndTS();
            if ( downtimeEnd == null )
            {
                downtimeEnd = LocalDateTime.now();
            }
            if ( ( downtimeStart.isAfter( start ) && downtimeStart.isBefore( end ) ) || ( downtimeEnd.isAfter( start ) && downtimeEnd.isBefore( end ) ) || downtimeStart.isEqual( start )
                    || downtimeStart.isEqual( end ) || downtimeEnd.isEqual( start ) || downtimeEnd.isEqual( end ) || ( downtimeStart.isBefore( start ) && downtimeEnd.isAfter( end ) ) )
            {
                data.add( downtimeDTO );
            }
        }

        return data;
    }

    public void filterDataForCostCenter( List<DowntimeDTO> downtimes, String costCenter )
    {
        List<DowntimeDTO> tempDowntimes = new ArrayList<>( downtimes );
        tempDowntimes.forEach( downtimeDTO -> {
            if ( !downtimeDTO.getCostCenter().equals( costCenter ) )
            {
                downtimes.remove( downtimeDTO );
            }
        } );
    }

    public List<DowntimeDTO> getShiftData( LocalDate date, List<String> costCenters )
    {
        List<DowntimeDTO> allDowntimes;
        List<DowntimeDTO> data = new ArrayList<>();
        if ( date.isAfter( LocalDate.now().minusDays( DowntimeConstants.REPLICATION.DAYS_TO_READ + 1 ) ) )
        {
            allDowntimes = getAllDataFromCache();
        }
        else
        {
            DowntimeView downtimeView = businessManager.getSession( DowntimeView.class );
            allDowntimes = downtimeView.loadDowntimes( LocalDateTime.of( date, LocalTime.MIN ), LocalDateTime.of( date.plusDays( 1 ), LocalTime.MAX ) );
        }

        costCenters.forEach( cc -> {
            ShiftModelDTO shiftModel = shiftTimeDataProvider.getShiftModelForCostCenter( cc );
            if ( shiftModel != null )
            {
                LocalDateTime start = LocalDateTime.of( date, shiftModel.getShift1Start() );
                LocalDateTime end = LocalDateTime.of( date.plusDays( 1 ), shiftModel.getShift3End() );
                List<DowntimeDTO> downtimes = filterDataForInterval( allDowntimes, start, end );
                filterDataForCostCenter( downtimes, cc );
                data.addAll( downtimes );
            }
        } );

        return data;
    }

    /**
     * Sorts the DowntimeDTOs based on the downtime start time.
     *
     * @param downtimes the list to sort
     */
    private void sortDowntimeList( List<DowntimeDTO> downtimes )
    {
        if ( downtimes.size() > 1 )
        {
            Comparator<DowntimeDTO> comp = ( dto1, dto2 ) -> {
                int result = dto1.getCostCenter().compareTo( dto2.getCostCenter() );
                if ( result == 0 )
                {
                    if ( dto1.getFromTS().isBefore( dto2.getFromTS() ) )
                    {
                        result = -1;
                    }
                    else if ( dto1.getFromTS().isAfter( dto2.getFromTS() ) )
                    {
                        result = 1;
                    }
                    else
                    {
                        if ( dto1.getId() < dto2.getId() )
                        {
                            result = -1;
                        }
                        else if ( dto1.getId() > dto2.getId() )
                        {
                            result = 1;
                        }
                    }
                }
                return result;
            };
            downtimes.sort( comp );
        }
    }

    public LocalDateTime findLastDowntimeEnd( String costCenter )
    {
        LocalDateTime lastDowntimeEnd = null;
        String cachePath = Casting.CACHE.DOWNTIME_KEY + "/" + costCenter;
        List<Long> downtimeSequence = downtimeSequenceCache.get( cachePath );

        if ( downtimeSequence != null && !downtimeSequence.isEmpty() )
        {
            for ( Long serial : downtimeSequence )
            {
                final String cacheDowntimeKey = cachePath + "/data/" + serial;
                LocalDateTime downtimeEnd = downtimeDataCache.get( cacheDowntimeKey ).getEndTS();
                if ( downtimeEnd != null && ( lastDowntimeEnd == null || lastDowntimeEnd.isBefore( downtimeEnd ) ) )
                {
                    lastDowntimeEnd = downtimeEnd;
                }
            }
        }
        return lastDowntimeEnd;
    }

    public boolean isDowntimeWithinCacheRange( DowntimeDTO downtimeDTO )
    {
        boolean result;
        LocalDateTime cacheTime = LocalDateTime.of( LocalDate.now(), LocalTime.MIN ).minusDays( DowntimeConstants.REPLICATION.DAYS_TO_READ );
        if ( downtimeDTO.getEndTS() != null )
        {
            result = cacheTime.isBefore( downtimeDTO.getEndTS() );
        }
        else
        {
            result = cacheTime.isBefore( downtimeDTO.getFromTS() );
        }
        return result;
    }

    public boolean isStartValid( DowntimeDTO downtimeDTO ) throws BusinessException
    {
        boolean result = true;
        List<DowntimeDTO> data = getCacheDataForCostCenter( downtimeDTO.getCostCenter() + StringTools.getNullSafe( downtimeDTO.getMachine() ) );
        LocalDateTime downtimeStart = downtimeDTO.getFromTS();

        if ( downtimeDTO.getId() > 0 )
        {
            removeDowntimeFromList( data, downtimeDTO.getId() );
        }

        for ( DowntimeDTO otherDowntime : data )
        {
            LocalDateTime otherDtoStart = otherDowntime.getFromTS();

            if ( ( otherDtoStart.isAfter( downtimeStart ) || otherDtoStart.isEqual( downtimeStart ) ) && otherDtoStart.isBefore( LocalDateTime.now() ) )
            {
                result = false;
                break;
            }
        }

        return result;
    }

    public boolean isDowntimeValid( DowntimeDTO downtimeDTO ) throws BusinessException
    {
        boolean result;

        if ( isDowntimeWithinCacheRange( downtimeDTO ) )
        {
            result = validateDowntimeWithCache( downtimeDTO );
        }
        else
        {
            result = validateDowntimeWithServer( downtimeDTO );
        }

        return result;
    }

    private boolean validateDowntimeWithCache( DowntimeDTO downtimeDTO ) throws BusinessException
    {
        boolean result = true;
        List<DowntimeDTO> data = getCacheDataForCostCenter( downtimeDTO.getCostCenter() + StringTools.getNullSafe( downtimeDTO.getMachine() ) );
        LocalDateTime downtimeStart = downtimeDTO.getFromTS();
        LocalDateTime downtimeEnd = downtimeDTO.getEndTS();

        if ( downtimeDTO.getId() > 0 )
        {
            removeDowntimeFromList( data, downtimeDTO.getId() );
        }

        for ( DowntimeDTO otherDowntime : data )
        {
            LocalDateTime otherDtStart = otherDowntime.getFromTS();
            LocalDateTime otherDtEnd = otherDowntime.getEndTS();
            if ( downtimeEnd != null )
            {
                if ( ( otherDtEnd == null && otherDtStart.isBefore( downtimeStart ) ) || ( otherDtStart.isAfter( downtimeStart ) && otherDtStart.isBefore( downtimeEnd ) ) || ( otherDtEnd != null
                        && otherDtEnd.isAfter( downtimeStart ) && otherDtEnd.isBefore( downtimeEnd ) ) || ( otherDtEnd != null && otherDtStart.isBefore( downtimeStart ) && otherDtEnd.isAfter(
                        downtimeEnd ) ) )
                {
                    result = false;
                    break;
                }
            }
            else
            {
                if ( otherDtStart.isBefore( LocalDateTime.now() ) && ( ( otherDtEnd != null && otherDtEnd.isAfter( downtimeStart ) ) || otherDtStart.isAfter( downtimeStart ) || otherDtStart.isEqual(
                        downtimeStart ) ) )
                {
                    result = false;
                    break;
                }
            }
        }

        return result;
    }

    private void removeDowntimeFromList( List<DowntimeDTO> data, long serial )
    {
        List<DowntimeDTO> toDelete = new ArrayList<>();
        data.forEach( downtimeDTO -> {
            if ( downtimeDTO.getId() == serial )
            {
                toDelete.add( downtimeDTO );
            }
        } );
        toDelete.forEach( data::remove );
    }

    private boolean validateDowntimeWithServer( DowntimeDTO downtimeDTO )
    {
        return true;
    }

    public String getDowntimeSumForInterval( String costCenter, LocalDateTime start, LocalDateTime end )
    {
        List<DowntimeDTO> downtimes = getDataForInterval( start, end );
        filterDataForCostCenter( downtimes, costCenter );
        long completeDowntimeInSeconds = 0;
        for ( DowntimeDTO downtimeDTO : downtimes )
        {
            LocalDateTime downtimeFrom = downtimeDTO.getFromTS();
            LocalDateTime downtimeUntil = downtimeDTO.getEndTS();

            if ( downtimeUntil == null )
            {
                // downtimeUntil = LocalDateTime.now();
                break;
            }
            else if ( downtimeUntil.isAfter( end ) )
            {
                downtimeUntil = end;
            }
            else if ( downtimeFrom.isBefore( start ) )
            {
                downtimeFrom = start;
            }
            completeDowntimeInSeconds += downtimeFrom.until( downtimeUntil, ChronoUnit.SECONDS );
        }

        return DateTimeUtil.formatDuration( completeDowntimeInSeconds );
    }
}
