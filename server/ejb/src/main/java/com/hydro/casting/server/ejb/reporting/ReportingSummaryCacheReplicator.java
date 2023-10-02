package com.hydro.casting.server.ejb.reporting;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.downtime.dto.DowntimeKindDTO;
import com.hydro.casting.server.contract.reporting.dto.*;
import com.hydro.casting.server.ejb.reporting.data.ReportingDataProviderDowntime;
import com.hydro.casting.server.ejb.reporting.data.ShiftTimeDataProvider;
import com.hydro.core.common.cache.ServerCache;
import com.hydro.core.common.cache.ServerCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.inject.Inject;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * This class replicates the ReportingDowntimeSummaryDTOs, ReportingOutputSummaryDTOs and the
 * ReportingGaugeSummaryDTOs into the reporting cache.
 */
@Singleton
public class ReportingSummaryCacheReplicator
{
    private final static Logger log = LoggerFactory.getLogger( ReportingSummaryCacheReplicator.class );

    @Inject
    private ServerCacheManager serverCacheManager;

    //    @PersistenceContext( unitName = "casting" )
    //    private EntityManager entityManager;

    @EJB
    private ReportingDataProviderDowntime reportingDataProviderDowntime;

    //    @EJB
    //    private ReportingDataProviderOutput reportingDataProviderOutput;

    @EJB
    private ShiftTimeDataProvider shiftTimeDataProvider;

    private ReportingUtils reportingUtils = new ReportingUtils();

    private String costCenter;
    private String machine;
    private LocalDateTime start;
    private LocalDateTime end;

    private LocalDateTime getStart()
    {
        if ( start == null )
        {
            return LocalDateTime.now().minusHours( 24 );
        }
        return start;
    }

    private LocalDateTime getEnd()
    {
        if ( end == null )
        {
            return LocalDateTime.now();
        }
        return end;
    }

    @Lock( LockType.READ )
    public void replicateSummaries( String costCenter, String machine ) throws InterruptedException
    {
        this.costCenter = costCenter;
        this.machine = machine;

        //        final ServerCache<Map<String, Integer>> outputTargetsCache = serverCacheManager.getCache( Casting.CACHE.REPORTING_CACHE_NAME );
        //        Map<String, Integer> outputTargets = outputTargetsCache.get( Casting.CACHE.REPORTING_SUMMARY_OUTPUT_TARGETS_KEY + "/" + costCenter );
        //        if ( outputTargets == null )
        //        {
        //            // first read from file system
        //            outputTargets = (Map<String, Integer>) DataFileStore.deserializeFromXML( "kpiOutputTargets-" + costCenter + ".xml" );
        //            if ( outputTargets == null )
        //            {
        //                // build default map
        //                outputTargets = createDefaultKPIOutputTargets( costCenter );
        //            }
        //            outputTargetsCache.put( Casting.CACHE.REPORTING_SUMMARY_OUTPUT_TARGETS_KEY + "/" + costCenter, outputTargets );
        //        }

        shiftTimeDataProvider.loadShiftModels();
        long start = System.currentTimeMillis();
        // log.debug( "reporting summary replication start, cc: " + costCenter + machine );

        String summaryCachePath = Casting.CACHE.REPORTING_SUMMARY_KEY;
        ServerCache<ReportingDTO> reportingSummaryCache = serverCacheManager.getCache( Casting.CACHE.REPORTING_CACHE_NAME );
        ServerCache<Set<String>> reportingSummarySequenceCache = serverCacheManager.getCache( Casting.CACHE.REPORTING_CACHE_NAME );

        // get current cache sequence
        Set<String> reportingSummarySequence = reportingSummarySequenceCache.get( summaryCachePath );
        if ( reportingSummarySequence == null || reportingSummarySequence.isEmpty() )
        {
            reportingSummarySequence = new HashSet<>();
        }

        // current shift
        setTimeToCurrentShift();
        createSummaryDTOs( Casting.REPORTING.CURRENT_SHIFT, periodToString(), "Aktuelle Schicht", reportingSummaryCache, reportingSummarySequence );

        // last shift
        setTimeToLastShift();
        createSummaryDTOs( Casting.REPORTING.LAST_SHIFT, periodToString(), "Letzte Schicht", reportingSummaryCache, reportingSummarySequence );

        // second last shift
        setTimeToSecondLastShift();
        createSummaryDTOs( Casting.REPORTING.SECOND_LAST_SHIFT, periodToString(), "Vorletzte Schicht", reportingSummaryCache, reportingSummarySequence );

        // third last shift
        setTimeToThirdLastShift();
        createSummaryDTOs( Casting.REPORTING.THIRD_LAST_SHIFT, periodToString(), "Vorvorletzte Schicht", reportingSummaryCache, reportingSummarySequence );

        // 24 h
        setTimeTo24Hour();
        createSummaryDTOs( Casting.REPORTING.LAST_24H, "Letzte 24 Stunden", "Letzte 24 Stunden", reportingSummaryCache, reportingSummarySequence );

        // current week
        setTimeToCurrentWeek();
        createSummaryDTOs( Casting.REPORTING.CURRENT_WEEK, "Aktuelle Woche", "Aktuelle Woche", reportingSummaryCache, reportingSummarySequence );

        // current month
        setTimeToCurrentMonth();
        createSummaryDTOs( Casting.REPORTING.CURRENT_MONTH, "Aktueller Monat", "Aktueller Monat", reportingSummaryCache, reportingSummarySequence );

        // last week
        setTimeToLastWeek();
        createSummaryDTOs( Casting.REPORTING.LAST_WEEK, "Letzte Woche", "Letzte Woche", reportingSummaryCache, reportingSummarySequence );

        // last month
        setTimeToLastMonth();
        createSummaryDTOs( Casting.REPORTING.LAST_MONTH, "Letzte Monat", "Letzte Monat", reportingSummaryCache, reportingSummarySequence );

        // day before shift 1
        setTimeToDayBefore( "1" );
        adjustShiftForWeekend();
        createSummaryDTOs( Casting.REPORTING.DAY_BEFORE_SHIFT_1, "Frühschicht", "Vortag Schicht 1", reportingSummaryCache, reportingSummarySequence );

        // day before shift 2
        setTimeToDayBefore( "2" );
        adjustShiftForWeekend();
        createSummaryDTOs( Casting.REPORTING.DAY_BEFORE_SHIFT_2, "Spätschicht", "Vortag Schicht 2", reportingSummaryCache, reportingSummarySequence );

        // day before shift 3
        setTimeToDayBefore( "3" );
        createSummaryDTOs( Casting.REPORTING.DAY_BEFORE_SHIFT_3, "Nachtschicht", "Vortag Schicht 3", reportingSummaryCache, reportingSummarySequence );

        // day before total
        setTimeToEntireDayBefore();
        createSummaryDTOs( Casting.REPORTING.DAY_BEFORE_TOTAL, "Gesamt", "Vortag Gesamt", reportingSummaryCache, reportingSummarySequence );

        reportingSummarySequenceCache.put( summaryCachePath, reportingSummarySequence );

        log.debug( "reporting summary replication end, cc: " + costCenter + machine + ", duration " + ( System.currentTimeMillis() - start ) + "ms" );
    }

    private void createSummaryDTOs( String dtoPath, String outputTitle, String gaugeTitle, ServerCache<ReportingDTO> reportingSummaryCache, Set<String> reportingSummarySequence )
    {
        String dtoPathDowntime = Casting.CACHE.REPORTING_SUMMARY_DOWNTIME_KEY + "/" + costCenter + machine + dtoPath;
        //        String dtoPathOutput = Casting.CACHE.REPORTING_SUMMARY_OUTPUT_KEY + "/" + costCenter + machine + dtoPath;
        String dtoPathGauge = Casting.CACHE.REPORTING_SUMMARY_GAUGE_KEY + "/" + costCenter + machine + dtoPath;

        ReportingDowntimeSummaryDTO downtimeSummaryDTO = downtimeSummary();
        reportingSummaryCache.put( dtoPathDowntime, downtimeSummaryDTO );
        reportingSummarySequence.add( dtoPathDowntime );

        //        ReportingOutputSummaryDTO outputSummaryDTO = outputSummary( outputTitle, downtimeSummaryDTO );
        //        reportingSummaryCache.put( dtoPathOutput, outputSummaryDTO );
        //        reportingSummarySequence.add( dtoPathOutput );

        ReportingGaugeSummaryDTO gaugeSummaryDTO = gaugeSummary( gaugeTitle );
        //        log.debug( "###### " + gaugeSummaryDTO );
        reportingSummaryCache.put( dtoPathGauge, gaugeSummaryDTO );
        reportingSummarySequence.add( dtoPathGauge );
    }

    private String periodToString()
    {
        return this.getStart().format( DateTimeFormatter.ofPattern( "dd.MM.yyyy HH:mm" ) ).concat( " - " + getEnd().format( DateTimeFormatter.ofPattern( "HH:mm" ) ) );
    }

    private void setTimeToCurrentShift()
    {
        start = shiftTimeDataProvider.getCurrentShiftStart( costCenter );
        end = null;
    }

    private void setTimeToLastShift()
    {
        String currentShift = shiftTimeDataProvider.getCurrentShift( costCenter );
        String lastShift = null;

        switch ( currentShift )
        {
        case "1":
            lastShift = "3";
            break;
        case "2":
            lastShift = "1";
            break;
        case "3":
            lastShift = "2";
            break;

        default:
            break;
        }
        end = shiftTimeDataProvider.getLastFinishedShiftEnd( costCenter, lastShift );
        start = LocalDateTime.of( getEnd().toLocalDate(), shiftTimeDataProvider.getStartTimeForShift( costCenter, lastShift ) );
        if ( lastShift.equals( "3" ) )
        {
            start = start.minusDays( 1 );
        }
    }

    private void setTimeToSecondLastShift()
    {
        String currentShift = shiftTimeDataProvider.getCurrentShift( costCenter );
        String secondLastShift = null;

        switch ( currentShift )
        {
        case "1":
            secondLastShift = "2";
            break;
        case "2":
            secondLastShift = "3";
            break;
        case "3":
            secondLastShift = "1";
            break;

        default:
            break;
        }
        end = shiftTimeDataProvider.getLastFinishedShiftEnd( costCenter, secondLastShift );
        start = LocalDateTime.of( getEnd().toLocalDate(), shiftTimeDataProvider.getStartTimeForShift( costCenter, secondLastShift ) );
        if ( secondLastShift.equals( "3" ) )
        {
            start = start.minusDays( 1 );
        }
    }

    private void setTimeToThirdLastShift()
    {
        String thirdLastShift = shiftTimeDataProvider.getCurrentShift( costCenter );

        end = shiftTimeDataProvider.getLastFinishedShiftEnd( costCenter, thirdLastShift );
        start = LocalDateTime.of( getEnd().toLocalDate(), shiftTimeDataProvider.getStartTimeForShift( costCenter, thirdLastShift ) );
        if ( thirdLastShift.equals( "3" ) )
        {
            start = start.minusDays( 1 );
        }
    }

    private void setTimeTo24Hour()
    {
        start = null;
        end = null;
    }

    private void setTimeToCurrentWeek()
    {
        start = ReportingUtils.getFirstDayOfWeek();
        end = null;
    }

    private void setTimeToCurrentMonth()
    {
        start = LocalDateTime.now();
        start = ReportingUtils.setToFirstDayOfMonth( start );
        start = ReportingUtils.setToStartOfDay( start );
        end = null;
    }

    private void setTimeToLastWeek()
    {
        start = ReportingUtils.getFirstDayOfLastWeek();
        end = ReportingUtils.getLastDayOfLastWeek();
    }

    private void setTimeToLastMonth()
    {
        start = ReportingUtils.getFirstDayOfLastMonth();
        end = ReportingUtils.getLastDayOfLastMonth();
    }

    private void setTimeToDayBefore( String shift )
    {
        LocalDate dayBefore = LocalDate.now().minusDays( 1 );
        LocalTime startTime = shiftTimeDataProvider.getStartTimeForShift( costCenter, shift );
        LocalTime endTime = shiftTimeDataProvider.getEndTimeForShift( costCenter, shift );

        start = LocalDateTime.of( dayBefore, startTime );
        if ( shift.equals( "3" ) )
        {
            end = LocalDateTime.of( dayBefore.plusDays( 1 ), endTime );
        }
        else
        {
            end = LocalDateTime.of( dayBefore, endTime );
        }
    }

    private void setTimeToEntireDayBefore()
    {
        LocalDate localDateToday = LocalDate.now();
        LocalTime startTime = shiftTimeDataProvider.getStartTimeForShift( costCenter, "1" );
        LocalTime endTime = shiftTimeDataProvider.getEndTimeForShift( costCenter, "3" );

        start = LocalDateTime.of( localDateToday.minusDays( 1 ), startTime );
        end = LocalDateTime.of( localDateToday, endTime );
    }

    private void adjustShiftForWeekend()
    {
        if ( LocalDate.now().getDayOfWeek().equals( DayOfWeek.MONDAY ) )
        {
            start = getStart().minusDays( 1 );
            end = getEnd().minusDays( 1 );
        }
    }

    private ReportingDowntimeSummaryDTO downtimeSummary()
    {
        ReportingDowntimeSummaryDTO summary = new ReportingDowntimeSummaryDTO();

        summary.setCostCenter( costCenter );
        summary.setMachine( machine );
        summary.setStart( start );
        summary.setEnd( end );
        summary.setDowntimeInMinutes( reportingDataProviderDowntime.sumUpDowntimes( getStart(), getEnd(), costCenter, machine, false ).intValue() );
        summary.setAvailability( 1 - ( Math.round( reportingDataProviderDowntime.sumUpDowntimes( getStart(), getEnd(), costCenter, machine, true ) * 1000 ) / 1000 ) );
        Map<DowntimeKindDTO, Double> downtimeKindSummaryDuration = reportingDataProviderDowntime.calculateDowntimeKindDurationSummary( costCenter, machine, getStart(), getEnd(), false, 0 );
        Map<DowntimeKindDTO, Integer> downtimeKindSummaryAmount = reportingDataProviderDowntime.calculateDowntimeKindAmountSummary( costCenter, machine, getStart(), getEnd() );
        summary.setDowntimeKindSummary( new ArrayList<>() );

        downtimeKindSummaryDuration.forEach( ( kind, duration ) -> {
            ReportingDowntimeKindSummaryDTO summaryDTO = new ReportingDowntimeKindSummaryDTO();
            summaryDTO.setDowntimeKind( kind );
            summaryDTO.setDurationInMinutes( duration.intValue() );
            summaryDTO.setAmount( downtimeKindSummaryAmount.get( kind ) );
            summary.getDowntimeKindSummary().add( summaryDTO );
        } );

        return summary;
    }

    //    private ReportingOutputSummaryDTO outputSummary( String title, ReportingDowntimeSummaryDTO downtimeSummaryDTO )
    //    {
    //        ReportingOutputSummaryDTO summaryDTO = new ReportingOutputSummaryDTO();
    //        List<ReportingOutputDetailDTO> data = reportingDataProviderOutput.getDataForInterval( getStart(), getEnd(), costCenter, machine );
    //        summaryDTO.setCostCenter( costCenter );
    //        summaryDTO.setMachine( machine );
    //        summaryDTO.setStart( start );
    //        summaryDTO.setEnd( end );
    //        summaryDTO.setTitle( title );
    //        if ( costCenter.equals( BDE.COST_CENTER.SLITTER_1 ) || costCenter.equals( BDE.COST_CENTER.SLITTER_2 ) || costCenter.equals( BDE.COST_CENTER.SLITTER_3 ) || costCenter
    //                .equals( BDE.COST_CENTER.TENSION_LEVELLER ) )
    //        {
    //            addOutputEntriesSlitter( summaryDTO, data );
    //        }
    //        else if ( costCenter.equals( BDE.COST_CENTER.CUTLENGTH_1 ) || costCenter.equals( BDE.COST_CENTER.CUTLENGTH_2 ) )
    //        {
    //            addOutputEntriesCutLength( summaryDTO, data );
    //        }
    //        else if ( costCenter.equals( BDE.COST_CENTER.ANNEALING_EKG ) || costCenter.equals( BDE.COST_CENTER.ANNEALING_74 ) || costCenter.equals( BDE.COST_CENTER.ANNEALING_75 ) || costCenter.equals( BDE.COST_CENTER.ANNEALING_76 ) || costCenter
    //                .equals( BDE.COST_CENTER.ANNEALING_77 ) )
    //        {
    //            addOutputEntriesAnnealing( summaryDTO, data );
    //        }
    //        else if ( costCenter.equals( BDE.COST_CENTER.COLDMILL_1 ) || costCenter.equals( BDE.COST_CENTER.COLDMILL_2 ) )
    //        {
    //            addOutputEntriesColdmill( summaryDTO, data, downtimeSummaryDTO );
    //        }
    //        else if ( costCenter.equals( BDE.COST_CENTER.HOTMILL ) )
    //        {
    //            addOutputEntriesHotmill( summaryDTO, data );
    //        }
    //        else if ( costCenter.equals( BDE.COST_CENTER.SCALPER ) )
    //        {
    //            addOutputEntriesScalper( summaryDTO, data );
    //        }
    //        else if ( costCenter.equals( BDE.COST_CENTER.PACKING ) && StringTools.isNullOrEmpty( machine ) )
    //        {
    //            addOutputEntriesPacking( summaryDTO, data );
    //        }
    //
    //        return summaryDTO;
    //    }

    private ReportingGaugeSummaryDTO gaugeSummary( String title )
    {
        ReportingGaugeSummaryDTO summaryDTO = new ReportingGaugeSummaryDTO();

        summaryDTO.setCostCenter( costCenter );
        summaryDTO.setMachine( machine );
        summaryDTO.setStart( start );
        summaryDTO.setEnd( end );
        summaryDTO.setTitle( title );
        summaryDTO.setDowntimeValue( Math.round( reportingDataProviderDowntime.sumUpDowntimes( getStart(), getEnd(), costCenter, machine, true ) * 100 ) );

        //        List<ReportingOutputDetailDTO> data = reportingDataProviderOutput.getDataForInterval( getStart(), getEnd(), costCenter, machine );
        //        if ( BDE.COST_CENTER.SCALPER.equals( costCenter ) || BDE.COST_CENTER.BRAZING.equals( costCenter ) )
        //        {
        //            summaryDTO.setOutputValue( reportingUtils.calculateAmountOfProducedOutputType( data, BDE.OUTPUT_TYPE.INGOT ) );
        //        }
        //        else if ( !BDE.COST_CENTER.PACKING.equals( costCenter ) )
        //        {
        //            summaryDTO.setOutputValue( reportingUtils.calculateAmountOfProcessedCoils( data ) );
        //        }
        //        else
        //        {
        //            summaryDTO.setOutputValue( reportingUtils.calculatePackingWeightNet( data ) + reportingUtils.calculatePackingExternWeight( data ) );
        //        }
        //
        //        if ( BDE.COST_CENTER.SLITTER_1.equals( costCenter ) || BDE.COST_CENTER.SLITTER_2.equals( costCenter ) || BDE.COST_CENTER.SLITTER_3.equals( costCenter ) || BDE.COST_CENTER.TENSION_LEVELLER.equals( costCenter ) || BDE.COST_CENTER.CUTLENGTH_1.equals( costCenter ) || BDE.COST_CENTER.CUTLENGTH_2.equals( costCenter ) )
        //        {
        //            summaryDTO.setScrapValue( reportingUtils.calculateAdditionalScrap( data ) );
        //        }

        gaugeLastDowntimeAnalysis( summaryDTO );

        return summaryDTO;
    }

    private void gaugeLastDowntimeAnalysis( ReportingGaugeSummaryDTO summaryDTO )
    {
        ReportingDowntimeDetailDTO lastDowntime = reportingDataProviderDowntime.getCurrentDowntime( costCenter, machine );
        if ( lastDowntime == null )
        {
            lastDowntime = reportingDataProviderDowntime.getLastDowntime( costCenter, machine );
        }
        Set<String> additionalTexts = new LinkedHashSet<>();
        final LocalDateTime now = LocalDateTime.now();

        if ( lastDowntime == null )
        {
            summaryDTO.setHasDowntime( false );
            additionalTexts.add( "Keine Störzeit gefunden" );
        }
        else if ( ( lastDowntime.getEnd() == null || lastDowntime.getEnd().isAfter( now ) ) && !lastDowntime.getStart().isAfter( now ) )
        {
            summaryDTO.setHasDowntime( true );
            additionalTexts.add( "Aktuelle Störzeit:" );
            additionalTexts.add( lastDowntime.getDescription() );
            additionalTexts.add( "seit: " + lastDowntime.getStart().format( DateTimeFormatter.ofPattern( "HH:mm" ) ) );
        }
        else if ( lastDowntime.getStart().isAfter( now ) )
        {
            summaryDTO.setHasDowntime( false );
            additionalTexts.add( "Geplante Störzeit:" );
            additionalTexts.add( lastDowntime.getDescription() );
            final String from = lastDowntime.getStart().format( DateTimeFormatter.ofPattern( "HH:mm" ) );
            final String to;
            if ( lastDowntime.getEnd() != null )
            {
                to = lastDowntime.getEnd().format( DateTimeFormatter.ofPattern( "HH:mm" ) );
            }
            else
            {
                to = "?";
            }
            additionalTexts.add( "Zeitraum: " + from + " - " + to );
        }
        else
        {
            summaryDTO.setHasDowntime( false );
            additionalTexts.add( "Letzte Störzeit:" );
            additionalTexts.add( lastDowntime.getDescription() );
            additionalTexts.add(
                    "Zeitraum: " + lastDowntime.getStart().format( DateTimeFormatter.ofPattern( "HH:mm" ) ) + " - " + lastDowntime.getEnd().format( DateTimeFormatter.ofPattern( "HH:mm" ) ) );
        }
        summaryDTO.setAdditionalTexts( additionalTexts );
    }

    //    private void addOutputEntriesSlitter( ReportingOutputSummaryDTO summaryDTO, List<ReportingOutputDetailDTO> data )
    //    {
    //        Map<String, String> entries = new LinkedHashMap<>();
    //        entries.put( "Bearbeitete Coils:", processedCoils( data ) );
    //        entries.put( "Anzahl Schichtprogramme:", differentScheduleNumbers( data ) );
    //        entries.put( "Produzierte Streifen:", producedMaterialSlitter( data ) );
    //        entries.put( "Produzierte Tonnen:", producedWeight( data ) );
    //        entries.put( "\u00D8 Dicke Eingangsmaterial:", averageThicknessIn( data ) );
    //        entries.put( "\u00D8 Breite der Streifen:", averageWidth( data ) );
    //        entries.put( "Mehrschrott:", additionalScrap( data ) );
    //        summaryDTO.setEntries( entries );
    //    }

    //    private void addOutputEntriesAnnealing( ReportingOutputSummaryDTO summaryDTO, List<ReportingOutputDetailDTO> data )
    //    {
    //        Map<String, String> entries = new LinkedHashMap<>();
    //        entries.put( "Bearbeitete Coils:", processedCoils( data ) );
    //        entries.put( "Anzahl Schichtprogramme:", differentScheduleNumbers( data ) );
    //        entries.put( "Produzierte Streifen:", producedMaterialSlitter( data ) );
    //        entries.put( "Produzierte Tonnen:", producedWeight( data ) );
    //        entries.put( "\u00D8 Dicke Eingangsmaterial:", averageThicknessIn( data ) );
    //        entries.put( "\u00D8 Breite der Streifen:", averageWidth( data ) );
    //        summaryDTO.setEntries( entries );
    //    }

    //    private void addOutputEntriesCutLength( ReportingOutputSummaryDTO summaryDTO, List<ReportingOutputDetailDTO> data )
    //    {
    //        Map<String, String> entries = new LinkedHashMap<>();
    //        entries.put( "Bearbeitete Coils:", processedCoils( data ) );
    //        entries.put( "Anzahl Schichtprogramme:", differentScheduleNumbers( data ) );
    //        entries.put( "Produzierte Stückzahl:", producedMaterialCutLength( data ) );
    //        entries.put( "Produzierte Tonnen:", producedWeight( data ) );
    //        entries.put( "\u00D8 Dicke Eingangsmaterial:", averageThicknessIn( data ) );
    //        entries.put( "Mehrschrott:", additionalScrap( data ) );
    //        summaryDTO.setEntries( entries );
    //    }

    //    private void addOutputEntriesColdmill( ReportingOutputSummaryDTO summaryDTO, List<ReportingOutputDetailDTO> data, ReportingDowntimeSummaryDTO downtimeSummaryDTO )
    //    {
    //        Map<String, String> entries = new LinkedHashMap<>();
    //        entries.put( "Bearbeitete Coils:", processedCoils( data ) );
    //        entries.put( "Produzierte Tonnen:", producedWeight( data ) );
    //        entries.put( "Fertigstiche:", lastPasses( data ) );
    //        entries.put( "Zusatzstiche:", additionalPasses( data ) );
    //        entries.put( "Störzeiten: [h (Anz.)]", reportingUtils.createHoursAndMinutesString( downtimeSummaryDTO.getDowntimeInMinutes() ) + " (" + reportingUtils.getAmountOfAllDowntimes( downtimeSummaryDTO.getDowntimeKindSummary() ) + ")" );
    //        entries.put( "Walzenwechsel geplant:", reportingUtils.createHoursAndMinutesString( reportingUtils.getDurationOfDowntimeKinds( downtimeSummaryDTO.getDowntimeKindSummary(), getDowntimeKindsMapChangeMillPlanned() ) ) + " (" + reportingUtils.getAmountOfDowntimes( downtimeSummaryDTO.getDowntimeKindSummary(), getDowntimeKindsMapChangeMillPlanned() ) + ")" );
    //        entries.put( "Walzenwechsel ungeplant:", reportingUtils.createHoursAndMinutesString( reportingUtils.getDurationOfDowntimeKinds( downtimeSummaryDTO.getDowntimeKindSummary(), getDowntimeKindsMapChangeMillUnplanned( summaryDTO.getCostCenter() ) ) ) + " (" + reportingUtils.getAmountOfDowntimes( downtimeSummaryDTO.getDowntimeKindSummary(), getDowntimeKindsMapChangeMillUnplanned( summaryDTO.getCostCenter() ) ) + ")" );
    //        entries.put( "Bandvorbereitung:", reportingUtils.createHoursAndMinutesString( reportingUtils.getDurationOfDowntimeKinds( downtimeSummaryDTO.getDowntimeKindSummary(), Collections.singletonList( new ReportingCustomPair("27", "5") ) ) ) + " (" + reportingUtils.getAmountOfDowntimes( downtimeSummaryDTO.getDowntimeKindSummary(), Collections.singletonList( new ReportingCustomPair("27", "5" ) ) ) + ")" );
    //        entries.put( "Probennahme:", reportingUtils.createHoursAndMinutesString( reportingUtils.getDurationOfDowntimeKinds( downtimeSummaryDTO.getDowntimeKindSummary(), Collections.singletonList( new ReportingCustomPair("27", "2") ) ) ) + " (" + reportingUtils.getAmountOfDowntimes( downtimeSummaryDTO.getDowntimeKindSummary(), Collections.singletonList( new ReportingCustomPair("27", "2" ) ) ) + ")" );
    //        entries.put( "Logistikstörungen:", reportingUtils.createHoursAndMinutesString( reportingUtils.getDurationOfDowntimeKinds( downtimeSummaryDTO.getDowntimeKindSummary(), getDowntimeKindsMapLogistics( summaryDTO.getCostCenter() ) ) ) + " (" + reportingUtils.getAmountOfDowntimes( downtimeSummaryDTO.getDowntimeKindSummary(), getDowntimeKindsMapLogistics( summaryDTO.getCostCenter() ) ) + ")" );
    //        entries.put( "Rollen putzen:", reportingUtils.createHoursAndMinutesString( reportingUtils.getDurationOfDowntimeKinds( downtimeSummaryDTO.getDowntimeKindSummary(), Collections.singletonList( new ReportingCustomPair("12", "0") ) ) ) + " (" + reportingUtils.getAmountOfDowntimes( downtimeSummaryDTO.getDowntimeKindSummary(), Collections.singletonList( new ReportingCustomPair("12", "0" ) ) ) + ")" );
    //        summaryDTO.setEntries( entries );
    //    }

    //    private List<ReportingCustomPair> getDowntimeKindsMapChangeMillPlanned()
    //    {
    //        List<ReportingCustomPair> downtimeKinds = new ArrayList<>();
    //        downtimeKinds.add(new ReportingCustomPair( "16", "0" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "18", "0" ) );
    //        return downtimeKinds;
    //    }

    //    private List<ReportingCustomPair> getDowntimeKindsMapChangeMillUnplanned( String costCenter )
    //    {
    //        List<ReportingCustomPair> downtimeKinds = new ArrayList<>();
    //        downtimeKinds.add(new ReportingCustomPair( "17", "0" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "19", "0" ) );
    //        if ( BDE.COST_CENTER.COLDMILL_2.equals( costCenter ) )
    //        {
    //            downtimeKinds.add(new ReportingCustomPair( "17", "1" ) );
    //            downtimeKinds.add(new ReportingCustomPair( "17", "2" ) );
    //            downtimeKinds.add(new ReportingCustomPair( "17", "3" ) );
    //        }
    //        return downtimeKinds;
    //    }

    //    private List<ReportingCustomPair> getDowntimeKindsMapLogistics( String costCenter )
    //    {
    //        List<ReportingCustomPair> downtimeKinds = new ArrayList<>();
    //        downtimeKinds.add(new ReportingCustomPair( "21", "0" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "21", "1" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "21", "3" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "22", "0" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "22", "1" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "22", "2" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "22", "3" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "22", "4" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "25", "0" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "26", "0" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "27", "0" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "27", "1" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "27", "3" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "27", "4" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "27", "5" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "27", "6" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "27", "7" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "52", "3" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "52", "4" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "58", "3" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "62", "0" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "64", "0" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "65", "0" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "69", "0" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "76", "0" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "76", "1" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "76", "2" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "76", "3" ) );
    //        downtimeKinds.add(new ReportingCustomPair( "88", "0" ) );
    //        if ( BDE.COST_CENTER.COLDMILL_1.equals( costCenter ) )
    //        {
    //            downtimeKinds.add(new ReportingCustomPair( "21", "2" ) );
    //            downtimeKinds.add(new ReportingCustomPair( "24", "0" ) );
    //            downtimeKinds.add(new ReportingCustomPair( "27", "8" ) );
    //            downtimeKinds.add(new ReportingCustomPair( "27", "9" ) );
    //        } else if ( BDE.COST_CENTER.COLDMILL_2.equals( costCenter ) )
    //        {
    //            downtimeKinds.add(new ReportingCustomPair( "25", "1" ) );
    //            downtimeKinds.add(new ReportingCustomPair( "25", "2" ) );
    //        }
    //        return downtimeKinds;
    //    }

    //    private void addOutputEntriesHotmill( ReportingOutputSummaryDTO summaryDTO, List<ReportingOutputDetailDTO> data )
    //    {
    //        Map<String, String> entries = new LinkedHashMap<>();
    //        entries.put( "Gewalzte Barren:", processedCoils( data ) );
    //        entries.put( "Davon Brazing:", processedBrazingIngots( data ) );
    //        entries.put( "Produzierte Tonnen:", producedWeight( data ) );
    //        entries.put( "Produzierte Bänder:", producedMaterialByOutputType( data, BDE.OUTPUT_TYPE.COIL ) );
    //        entries.put( "Produzierte Schalen:", producedMaterialByOutputType( data, BDE.OUTPUT_TYPE.PLATE ) );
    //        entries.put( "\u00D8 Dicke Coils:", averageThicknessHotmillCoils( data ) );
    //        summaryDTO.setEntries( entries );
    //    }

    //    private void addOutputEntriesScalper( ReportingOutputSummaryDTO summaryDTO, List<ReportingOutputDetailDTO> data )
    //    {
    //        Map<String, String> entries = new LinkedHashMap<>();
    //        entries.put( "Gefräste Barren:", producedMaterialByOutputType( data, BDE.OUTPUT_TYPE.INGOT ) );
    //        entries.put( "Produzierte Tonnen:", producedWeight( data ) );
    //        entries.put( "\u00D8 Dicke Eingangsmaterial:", averageThicknessIn( data ) );
    //        entries.put( "\u00D8 Dicke Ausgangsmaterial:", averageThicknessOut( data ) );
    //        entries.put( "\u00D8 Dickenabnahme:", thicknessDecreaseRatio( data ) );
    //        entries.put( "angespitze Barren:", sharpenedIngots( data ) );
    //        entries.put( "Anzahl Fräsungen:", multipleScalpedIngots( data ) );
    //        summaryDTO.setEntries( entries );
    //    }

    //    private void addOutputEntriesPacking( ReportingOutputSummaryDTO summaryDTO, List<ReportingOutputDetailDTO> data )
    //    {
    //        Map<String, String> entries = new LinkedHashMap<>();
    //        Map<String, String> entriesDetail = new LinkedHashMap<>();
    //        entries.put( "Packmenge brutto:", packingWeightGross( data ) );
    //        entries.put( "Packmenge netto:", packingWeightNet( data ) );
    //        entries.put( "zum Umarbeiter:", packingExternWeight( data ) );
    //        entries.put( "Anzahl Packstücke:", packingCount( data ) );
    //        entriesDetail.put( "Warmband (66): ", packingWeightForCostCenter( data, "66" ) );
    //        entriesDetail.put( "Folie (69): ", packingWeightForCostCenter( data, "69" ) );
    //        entriesDetail.put( "Spaltband (78): ", packingWeightForCostCenter( data, "78" ) );
    //        entriesDetail.put( "Besäumt (79): ", packingWeightForCostCenter( data, "79" ) );
    //        entriesDetail.put( "Plattiert (80): ", packingWeightForCostCenter( data, "80" ) );
    //        entriesDetail.put( "Bleche (81): ", packingWeightForCostCenter( data, "81" ) );
    //        entriesDetail.put( "Bleche (82): ", packingWeightForCostCenter( data, "82" ) );
    //        entriesDetail.put( "Gereckt (92): ", packingWeightForCostCenter( data, "92" ) );
    //        entriesDetail.put( "GV-plattiert (96): ", packingWeightForCostCenter( data, "96" ) );
    //        entriesDetail.put( "GV-unplattiert (98): ", packingWeightForCostCenter( data, "98" ) );
    //        summaryDTO.setEntries( entries );
    //        summaryDTO.setEntriesDetail( entriesDetail );
    //    }

    //    private String processedCoils( List<ReportingOutputDetailDTO> data )
    //    {
    //        return StringTools.NFSEP.format( reportingUtils.calculateAmountOfProcessedCoils( data ) );
    //    }
    //
    //    private String processedBrazingIngots( List<ReportingOutputDetailDTO> data )
    //    {
    //        return StringTools.NFSEP.format( reportingUtils.calculateAmountOfProcessedBrazingIngots( data ) );
    //    }
    //
    //    private String differentScheduleNumbers( List<ReportingOutputDetailDTO> data )
    //    {
    //        return String.valueOf( reportingUtils.calculateAmountOfDifferentScheduleNbrs( data ) );
    //    }
    //
    //    private String producedMaterialSlitter( List<ReportingOutputDetailDTO> data )
    //    {
    //        return String.valueOf( reportingUtils.calculateAmountOfProducedMaterialSlitter( data ) );
    //    }
    //
    //    private String producedMaterialCutLength( List<ReportingOutputDetailDTO> data )
    //    {
    //        return String.valueOf( reportingUtils.calculateAmountOfProducedMaterialCutLength( data ) );
    //    }
    //
    //    private String producedMaterialByOutputType( List<ReportingOutputDetailDTO> data, String outputType )
    //    {
    //        return StringTools.NFSEP.format( reportingUtils.calculateAmountOfProducedOutputType( data, outputType ) );
    //    }
    //
    //    private String producedWeight( List<ReportingOutputDetailDTO> data )
    //    {
    //        return StringTools.N02F.format( reportingUtils.calculateProducedWeight( data ) ) + " t";
    //    }
    //
    //    private String averageThicknessIn( List<ReportingOutputDetailDTO> data )
    //    {
    //        return StringTools.N02F.format( reportingUtils.calculateAverageThickness( data, true ) ) + " mm";
    //    }
    //
    //    private String averageThicknessOut( List<ReportingOutputDetailDTO> data )
    //    {
    //        return StringTools.N02F.format( reportingUtils.calculateAverageThickness( data, false ) ) + " mm";
    //    }
    //
    //    private String averageThicknessHotmillCoils( List<ReportingOutputDetailDTO> data )
    //    {
    //        return StringTools.N02F.format( reportingUtils.calculateAverageThichnessForHotmillCoils( data ) ) + " mm";
    //    }
    //
    //    private String averageWidth( List<ReportingOutputDetailDTO> data )
    //    {
    //        return StringTools.N02F.format( reportingUtils.calculateAverageWidthPerSlit( data ) ) + " mm";
    //    }
    //
    //    private String thicknessDecreaseRatio( List<ReportingOutputDetailDTO> data )
    //    {
    //        return StringTools.N02FSEP.format( reportingUtils.calculateAverageThicknessDecreaseRatio( data ) ) + " %";
    //    }
    //
    //    private String sharpenedIngots( List<ReportingOutputDetailDTO> data )
    //    {
    //        return StringTools.NFSEP.format( reportingUtils.calculateAmountOfSharpenedIngots( data ) );
    //    }
    //
    //    private String multipleScalpedIngots( List<ReportingOutputDetailDTO> data )
    //    {
    //        return StringTools.NFSEP.format( reportingUtils.calculateAmountOfMultipleScalpedIngots( data ) );
    //    }
    //
    //    private String lastPasses( List<ReportingOutputDetailDTO> data )
    //    {
    //        return StringTools.NFSEP.format( reportingUtils.calculateAmountOfLastPasses( data ) );
    //    }
    //
    //    private String additionalPasses( List<ReportingOutputDetailDTO> data )
    //    {
    //        return StringTools.NFSEP.format( reportingUtils.calculateAmountOfAdditionalPasses( data ) );
    //    }
    //
    //    private String packingCount( List<ReportingOutputDetailDTO> data )
    //    {
    //        return StringTools.NFSEP.format( reportingUtils.calculatePackingCount( data ) );
    //    }
    //
    //    private String packingWeightGross( List<ReportingOutputDetailDTO> data )
    //    {
    //        return StringTools.NFSEP.format( reportingUtils.calculatePackingWeightNet( data ) + reportingUtils.calculatePackingExternWeight( data ) ) + " t";
    //    }
    //
    //    private String packingWeightNet( List<ReportingOutputDetailDTO> data )
    //    {
    //        return StringTools.NFSEP.format( reportingUtils.calculatePackingWeightNet( data ) ) + " t";
    //    }
    //
    //    private String packingExternWeight( List<ReportingOutputDetailDTO> data )
    //    {
    //        return StringTools.NFSEP.format( reportingUtils.calculatePackingExternWeight( data ) ) + " t";
    //    }
    //
    //    private String packingWeightForCostCenter( List<ReportingOutputDetailDTO> data, String costCenter )
    //    {
    //        return StringTools.NFSEP.format( reportingUtils.calculatePackingWeightForCostCenter( data, costCenter ) ) + " t";
    //    }
    //
    //    private String additionalScrap( List<ReportingOutputDetailDTO> data ) { return StringTools.N1F.format( reportingUtils.calculateAdditionalScrap( data ) ) + " kg"; }
    //
    //    public void clearCache()
    //    {
    //        serverCacheManager.getCache( BDE.CACHE.REPORTING ).clear();
    //    }

    //    private Map<String, Integer> createDefaultKPIOutputTargets( final String costCenter )
    //    {
    //        final Map<String, Integer> outputTargets = new HashMap<>();
    //
    //        if ( BDE.COST_CENTER.SCALPER.equals( costCenter ) )
    //        {
    //            outputTargets.put( "shift", BDE.KPI.OUTPUT_52 );
    //            outputTargets.put( "week", BDE.KPI.OUTPUT_52 * 3 * 6 );
    //            outputTargets.put( "month", BDE.KPI.OUTPUT_52 * 3 * 25 );
    //        }
    //        else if ( BDE.COST_CENTER.PLATECUTTER.equals( costCenter ) )
    //        {
    //            outputTargets.put( "shift", BDE.KPI.OUTPUT_60 );
    //            outputTargets.put( "week", BDE.KPI.OUTPUT_60 * 3 * 6 );
    //            outputTargets.put( "month", BDE.KPI.OUTPUT_60 * 3 * 25 );
    //        }
    //        else if ( BDE.COST_CENTER.BRAZING.equals( costCenter ) )
    //        {
    //            outputTargets.put( "shift", BDE.KPI.OUTPUT_61 );
    //            outputTargets.put( "week", BDE.KPI.OUTPUT_61 * 3 * 6 );
    //            outputTargets.put( "month", BDE.KPI.OUTPUT_61 * 3 * 25 );
    //        }
    //        else if ( BDE.COST_CENTER.HOTMILL.equals( costCenter ) )
    //        {
    //            outputTargets.put( "shift", BDE.KPI.OUTPUT_66 );
    //            outputTargets.put( "week", BDE.KPI.OUTPUT_66 * 3 * 6 );
    //            outputTargets.put( "month", BDE.KPI.OUTPUT_66 * 3 * 25 );
    //        }
    //        else if ( BDE.COST_CENTER.COLDMILL_1.equals( costCenter ) )
    //        {
    //            outputTargets.put( "shift", BDE.KPI.OUTPUT_69 );
    //            outputTargets.put( "week", BDE.KPI.OUTPUT_69 * 3 * 6 );
    //            outputTargets.put( "month", BDE.KPI.OUTPUT_69 * 3 * 25 );
    //        }
    //        else if ( BDE.COST_CENTER.COLDMILL_2.equals( costCenter ) )
    //        {
    //            outputTargets.put( "shift", BDE.KPI.OUTPUT_72 );
    //            outputTargets.put( "week", BDE.KPI.OUTPUT_72 * 3 * 6 );
    //            outputTargets.put( "month", BDE.KPI.OUTPUT_72 * 3 * 25 );
    //        }
    //        else if ( BDE.COST_CENTER.ANNEALING_EKG.equals( costCenter ) )
    //        {
    //            outputTargets.put( "shift", BDE.KPI.OUTPUT_EKG );
    //            outputTargets.put( "week", BDE.KPI.OUTPUT_EKG * 3 * 6 );
    //            outputTargets.put( "month", BDE.KPI.OUTPUT_EKG * 3 * 25 );
    //        }
    //        else if ( BDE.COST_CENTER.ANNEALING_74.equals( costCenter ) )
    //        {
    //            outputTargets.put( "shift", BDE.KPI.OUTPUT_74 );
    //            outputTargets.put( "week", BDE.KPI.OUTPUT_74 * 3 * 6 );
    //            outputTargets.put( "month", BDE.KPI.OUTPUT_74 * 3 * 25 );
    //        }
    //        else if ( BDE.COST_CENTER.ANNEALING_75.equals( costCenter ) )
    //        {
    //            outputTargets.put( "shift", BDE.KPI.OUTPUT_75 );
    //            outputTargets.put( "week", BDE.KPI.OUTPUT_75 * 3 * 6 );
    //            outputTargets.put( "month", BDE.KPI.OUTPUT_75 * 3 * 25 );
    //        }
    //        else if ( BDE.COST_CENTER.ANNEALING_76.equals( costCenter ) )
    //        {
    //            outputTargets.put( "shift", BDE.KPI.OUTPUT_76 );
    //            outputTargets.put( "week", BDE.KPI.OUTPUT_76 * 3 * 6 );
    //            outputTargets.put( "month", BDE.KPI.OUTPUT_76 * 3 * 25 );
    //        }
    //        else if ( BDE.COST_CENTER.ANNEALING_77.equals( costCenter ) )
    //        {
    //            outputTargets.put( "shift", BDE.KPI.OUTPUT_77 );
    //            outputTargets.put( "week", BDE.KPI.OUTPUT_77 * 3 * 6 );
    //            outputTargets.put( "month", BDE.KPI.OUTPUT_77 * 3 * 25 );
    //        }
    //        else if ( BDE.COST_CENTER.SLITTER_1.equals( costCenter ) )
    //        {
    //            outputTargets.put( "shift", BDE.KPI.OUTPUT_78 );
    //            outputTargets.put( "week", BDE.KPI.OUTPUT_78 * 3 * 6 );
    //            outputTargets.put( "month", BDE.KPI.OUTPUT_78 * 3 * 25 );
    //        }
    //        else if ( BDE.COST_CENTER.SLITTER_2.equals( costCenter ) )
    //        {
    //            outputTargets.put( "shift", BDE.KPI.OUTPUT_79 );
    //            outputTargets.put( "week", BDE.KPI.OUTPUT_79 * 3 * 6 );
    //            outputTargets.put( "month", BDE.KPI.OUTPUT_79 * 3 * 25 );
    //        }
    //        else if ( BDE.COST_CENTER.SLITTER_3.equals( costCenter ) )
    //        {
    //            outputTargets.put( "shift", BDE.KPI.OUTPUT_80 );
    //            outputTargets.put( "week", BDE.KPI.OUTPUT_80 * 3 * 6 );
    //            outputTargets.put( "month", BDE.KPI.OUTPUT_80 * 3 * 25 );
    //        }
    //        else if ( BDE.COST_CENTER.CUTLENGTH_1.equals( costCenter ) )
    //        {
    //            outputTargets.put( "shift", BDE.KPI.OUTPUT_81 );
    //            outputTargets.put( "week", BDE.KPI.OUTPUT_81 * 3 * 6 );
    //            outputTargets.put( "month", BDE.KPI.OUTPUT_81 * 3 * 25 );
    //        }
    //        else if ( BDE.COST_CENTER.CUTLENGTH_2.equals( costCenter ) )
    //        {
    //            outputTargets.put( "shift", BDE.KPI.OUTPUT_82 );
    //            outputTargets.put( "week", BDE.KPI.OUTPUT_82 * 3 * 6 );
    //            outputTargets.put( "month", BDE.KPI.OUTPUT_82 * 3 * 25 );
    //        }
    //        else if ( BDE.COST_CENTER.PACKING.equals( costCenter ) )
    //        {
    //            outputTargets.put( "shift", BDE.KPI.OUTPUT_85 );
    //            outputTargets.put( "week", BDE.KPI.OUTPUT_85 * 3 * 6 );
    //            outputTargets.put( "month", BDE.KPI.OUTPUT_85 * 3 * 25 );
    //        }
    //        else if ( BDE.COST_CENTER.TENSION_LEVELLER.equals( costCenter ) )
    //        {
    //            outputTargets.put( "shift", BDE.KPI.OUTPUT_92 );
    //            outputTargets.put( "week", BDE.KPI.OUTPUT_92 * 3 * 6 );
    //            outputTargets.put( "month", BDE.KPI.OUTPUT_92 * 3 * 25 );
    //        }
    //        else
    //        {
    //            outputTargets.put( "shift", 1 );
    //            outputTargets.put( "week", 1 * 3 * 6 );
    //            outputTargets.put( "month", 1 * 3 * 25 );
    //        }
    //
    //        return outputTargets;
    //    }
}