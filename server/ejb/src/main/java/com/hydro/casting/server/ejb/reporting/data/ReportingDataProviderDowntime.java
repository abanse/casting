package com.hydro.casting.server.ejb.reporting.data;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.downtime.dto.DowntimeKindDTO;
import com.hydro.casting.server.contract.reporting.dto.ReportingDowntimeDetailDTO;
import com.hydro.core.common.cache.ServerCache;
import com.hydro.core.common.cache.ServerCacheManager;

import javax.ejb.Singleton;
import javax.inject.Inject;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * This class holds and provides the reporting downtime data.
 * In addition is contains various methods with calculations for the reporting.
 */
@Singleton
public class ReportingDataProviderDowntime extends ReportingDataProvider<ReportingDowntimeDetailDTO>
{
    @Inject
    private ServerCacheManager serverCacheManager;

    private LocalDateTime lastDowntimeStart = null;
    private ReportingDowntimeDetailDTO lastDowntimeDetailDTO = null;

    public void loadAllDetailDTOsFromCache()
    {
        allDetailDTOs.clear();
        String cachePath = Casting.CACHE.REPORTING_DOWNTIME_DETAIL_KEY;
        ServerCache<ReportingDowntimeDetailDTO> reportingDowntimeDataCache = serverCacheManager.getCache( Casting.CACHE.REPORTING_DATA_DOWNTIME_CACHE_NAME );
        ServerCache<List<Long>> reportingDowntimeSequenceCache = serverCacheManager.getCache( Casting.CACHE.REPORTING_DATA_DOWNTIME_CACHE_NAME );
        List<Long> reportingDowntimeSequence = reportingDowntimeSequenceCache.get( cachePath );
        Set<String> cacheDowntimeKeys = new HashSet<>();
        reportingDowntimeSequence.forEach( serial -> cacheDowntimeKeys.add( cachePath + "/data/" + serial ) );
        Map<String, ReportingDowntimeDetailDTO> data = reportingDowntimeDataCache.getAll( cacheDowntimeKeys );

        if ( data != null && !data.isEmpty() )
        {
            data.forEach( ( key, detail ) -> allDetailDTOs.add( detail ) );
        }
        assignDataToCostCenters();
    }

    public ReportingDowntimeDetailDTO getLastDowntime( String costCenter, String machine )
    {
        List<ReportingDowntimeDetailDTO> data = getDataForCostCenter( costCenter, machine );
        data.sort( Comparator.comparing( ReportingDowntimeDetailDTO::getStart ) );
        Collections.reverse( data );
        if ( data.size() > 0 )
        {
            return data.get( 0 );
        }
        else
        {
            return null;
        }
    }

    public ReportingDowntimeDetailDTO getCurrentDowntime( String costCenter, String machine )
    {
        List<ReportingDowntimeDetailDTO> data = getDataForCostCenter( costCenter, machine );
        data.sort( Comparator.comparing( ReportingDowntimeDetailDTO::getStart ) );
        Collections.reverse( data );
        LocalDateTime now = LocalDateTime.now();
        ReportingDowntimeDetailDTO result = null;

        for ( ReportingDowntimeDetailDTO detail : data )
        {
            if ( detail.getEnd() != null && detail.getEnd().isBefore( now ) )
            {
                break;
            }
            else if ( detail.getStart().isBefore( now ) && ( detail.getEnd() == null || detail.getEnd().isAfter( now ) ) )
            {
                result = detail;
                break;
            }
        }

        return result;
    }

    public List<ReportingDowntimeDetailDTO> getDataForInterval( LocalDateTime start, LocalDateTime end, List<ReportingDowntimeDetailDTO> detailDTOs )
    {
        List<ReportingDowntimeDetailDTO> data = new ArrayList<>();
        lastDowntimeStart = null;
        lastDowntimeDetailDTO = null;
        LocalDateTime now = LocalDateTime.now();
        if ( start.isAfter( now ) )
        {
            start = now;
        }
        if ( end.isAfter( now ) )
        {
            end = now;
        }

        for ( ReportingDowntimeDetailDTO detail : detailDTOs )
        {
            LocalDateTime downtimeStart = detail.getStart();
            LocalDateTime downtimeEnd = null;

            if ( detail.getEnd() != null )
            {
                downtimeEnd = detail.getEnd();
            }

            if ( ( downtimeEnd != null && downtimeEnd.isAfter( start ) && !downtimeEnd.isAfter( end ) ) || ( !downtimeStart.isBefore( start ) && !downtimeStart.isAfter( end ) ) )
            {
                if ( lastDowntimeStart == null || downtimeStart.isAfter( lastDowntimeStart ) )
                {
                    lastDowntimeStart = downtimeStart;
                    lastDowntimeDetailDTO = detail;
                }
                if ( downtimeEnd != null )
                {
                    data.add( detail );
                }
            }
        }

        if ( lastDowntimeDetailDTO != null )
        {
            if ( !data.contains( lastDowntimeDetailDTO ) )
            {
                data.add( lastDowntimeDetailDTO );
            }
        }

        return data;
    }

    /**
     * @param start             the start of the interval
     * @param end               the end of the interval
     * @param costCenter        the cost center
     * @param factorCalculation specifies whether the result should be the factor or the sum of all downtimes
     * @return Returns either the downtime factor from 0 to 1 compared to the interval duration or the added
     * duration of all downtimes in minutes.
     */
    public Double sumUpDowntimes( LocalDateTime start, LocalDateTime end, String costCenter, String machine, boolean factorCalculation )
    {
        List<ReportingDowntimeDetailDTO> detailDTOs = getDataForInterval( start, end, getDataForCostCenter( costCenter, machine ) );
        long intervalDurationinMillis = excludePlannedDowntimes( calculateIntervalDuration( start, end ), start, end );
        long durationInMillis = 0;

        for ( ReportingDowntimeDetailDTO detail : detailDTOs )
        {
            LocalDateTime downtimeStart = detail.getStart();
            LocalDateTime downtimeEnd = null;
            if ( detail.getEnd() != null )
            {
                downtimeEnd = detail.getEnd();
            }

            if ( downtimeStart.isBefore( end ) && ( downtimeEnd == null || downtimeEnd.isAfter( end ) ) )
            {
                durationInMillis += downtimeStart.until( end, ChronoUnit.MILLIS );
            }
            else if ( downtimeStart.isBefore( start ) )
            {
                durationInMillis += start.until( downtimeEnd, ChronoUnit.MILLIS );
            }
            else
            {
                durationInMillis += downtimeStart.until( downtimeEnd, ChronoUnit.MILLIS );
            }
        }
        double value;
        if ( factorCalculation )
        {
            value = ( 1.0 / intervalDurationinMillis ) * durationInMillis;
        }
        else
        {
            value = TimeUnit.MILLISECONDS.toMinutes( durationInMillis );
        }
        return value;
    }

    public Map<DowntimeKindDTO, Double> calculateDowntimeKindDurationSummary( String costCenter, String machine, LocalDateTime start, LocalDateTime end, boolean factorCalculation,
            double aggregationValue )
    {
        List<ReportingDowntimeDetailDTO> detailDTOs = getDataForInterval( start, end, getDataForCostCenter( costCenter, machine ) );
        long intervalDurationinMillis = excludePlannedDowntimes( calculateIntervalDuration( start, end ), start, end );

        Map<DowntimeKindDTO, Double> filteredDetail = new HashMap<>();

        detailDTOs.forEach( detail -> {
            LocalDateTime downtimeStart = detail.getStart();
            LocalDateTime downtimeEnd = null;
            if ( detail.getEnd() != null )
            {
                downtimeEnd = detail.getEnd();
            }

            long durationInMillis;

            if ( downtimeStart.isBefore( end ) && ( downtimeEnd == null || downtimeEnd.isAfter( end ) ) )
            {
                durationInMillis = downtimeStart.until( end, ChronoUnit.MILLIS );
            }
            else if ( downtimeStart.isBefore( start ) )
            {
                durationInMillis = start.until( downtimeEnd, ChronoUnit.MILLIS );
            }
            else
            {
                durationInMillis = downtimeStart.until( downtimeEnd, ChronoUnit.MILLIS );
            }

            double dataValue;
            if ( factorCalculation )
            {
                dataValue = ( 1.0 / intervalDurationinMillis ) * durationInMillis;
            }
            else
            {
                dataValue = TimeUnit.MILLISECONDS.toMinutes( durationInMillis );
            }

            if ( filteredDetail.containsKey( detail.getDowntimeKind() ) )
            {
                filteredDetail.put( detail.getDowntimeKind(), filteredDetail.get( detail.getDowntimeKind() ) + dataValue );
            }
            else
            {
                filteredDetail.put( detail.getDowntimeKind(), dataValue );
            }
        } );

        Map<DowntimeKindDTO, Double> sortedMap = filteredDetail.entrySet().stream().sorted( Entry.comparingByValue( Collections.reverseOrder() ) )
                .collect( Collectors.toMap( Entry::getKey, Entry::getValue, ( e1, e2 ) -> e1, LinkedHashMap::new ) );

        if ( aggregationValue > 0 )
        {
            aggregateData( sortedMap, aggregationValue );
        }

        return sortedMap;
    }

    public Map<DowntimeKindDTO, Integer> calculateDowntimeKindAmountSummary( String costCenter, String machine, LocalDateTime start, LocalDateTime end )
    {
        List<ReportingDowntimeDetailDTO> detailDTOs = getDataForInterval( start, end, getDataForCostCenter( costCenter, machine ) );

        Map<DowntimeKindDTO, Integer> data = new HashMap<>();

        detailDTOs.forEach( detail -> {
            DowntimeKindDTO kind = detail.getDowntimeKind();
            Integer value = 1;
            if ( data.containsKey( kind ) )
            {
                value = data.get( kind ) + 1;
            }
            data.put( kind, value );
        } );

        return data;
    }

    private long calculateIntervalDuration( LocalDateTime start, LocalDateTime end )
    {
        return start.until( end, ChronoUnit.MILLIS );
    }

    private long excludePlannedDowntimes( long durationInMillis, LocalDateTime start, LocalDateTime end )
    {
        LocalDateTime saturday = start.with( TemporalAdjusters.next( DayOfWeek.SATURDAY ) ).withHour( 22 ).withMinute( 0 ).withSecond( 0 ).withNano( 0 );
        LocalDateTime sunday = start.with( TemporalAdjusters.next( DayOfWeek.SUNDAY ) ).withHour( 21 ).withMinute( 59 ).withSecond( 59 ).withNano( 999999999 );

        while ( end.isAfter( saturday ) )
        {
            if ( start.isBefore( saturday ) )
            {
                if ( end.isAfter( sunday ) )
                {
                    durationInMillis -= saturday.until( sunday, ChronoUnit.MILLIS );
                }
                else if ( end.isAfter( saturday ) )
                {
                    durationInMillis -= saturday.until( end, ChronoUnit.MILLIS );
                }
            }
            else if ( start.isAfter( saturday ) && start.isBefore( sunday ) )
            {
                if ( end.isAfter( sunday ) )
                {
                    durationInMillis -= start.until( sunday, ChronoUnit.MILLIS );
                }
                else
                {
                    durationInMillis = 0;
                }
            }
            saturday = saturday.plusWeeks( 1 );
            sunday = sunday.plusWeeks( 1 );
        }
        return durationInMillis;
    }

    private void aggregateData( Map<DowntimeKindDTO, Double> data, double value )
    {
        double addedDowntimes = data.values().stream().mapToDouble( Double::doubleValue ).sum();
        double minimumDowntimeDuration = addedDowntimes * value;
        double aggregatedDowntimes = 0;
        int count = 0;
        for ( double duration : data.values() )
        {
            if ( duration < minimumDowntimeDuration )
            {
                aggregatedDowntimes += duration;
                count++;
            }
        }
        if ( count > 2 )
        {
            data.entrySet().removeIf( e -> e.getValue() < minimumDowntimeDuration );
            data.put( null, aggregatedDowntimes );
        }
    }
}
