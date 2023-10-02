package com.hydro.casting.gui.reporting.view;

import com.google.inject.Inject;
import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.reporting.data.ReportingCacheDataProvider;
import com.hydro.casting.gui.reporting.util.ReportingViewUtils;
import com.hydro.casting.server.contract.downtime.dto.DowntimeKindDTO;
import com.hydro.casting.server.contract.reporting.dto.ReportingDowntimeSummaryDTO;
import com.hydro.casting.server.ejb.reporting.ReportingUtils;
import com.hydro.core.gui.*;
import com.hydro.core.gui.view.ViewDeclaration;
import com.hydro.core.gui.view.comp.ViewParent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@ViewDeclaration( id = DetailReportingViewController.ID, fxmlFile = "/com/hydro/casting/gui/reporting/view/DetailReportingView.fxml", type = ViewType.MAIN )
public class DetailReportingViewController implements ViewController
{
    public final static String ID = SecurityCasting.REPORTING.DETAIL.VIEW;

    @FXML
    private ViewParent detailReporting;

    @FXML
    private StackedBarChart<String, Double> barChart;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private VBox outputBox;

    @FXML
    private Button legendButton;

    @Inject
    private CacheManager cacheManager;

    @Inject
    private ReportingCacheDataProvider dataProvider;

    @Inject
    private PreferencesManager preferencesManager;

    @Inject
    private ReportingViewUtils reportingViewUtils;

    private String costCenter = null;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool( 1 );
    private ScheduledFuture<Void> scheduleFuture;

    /**
     * Initializes the controller class.
     */
    @FXML
    private void initialize()
    {
        detailReporting.setTitle( "" );
        barChart.setLegendVisible( true );
        barChart.setLegendSide( Side.BOTTOM );
        barChart.setMaxWidth( 1200 );
        barChart.prefWidth( 1000 );
        barChart.setMinHeight( 400 );
        barChart.setPrefHeight( 600 );
        barChart.setMaxHeight( Double.MAX_VALUE );
        yAxis.setAutoRanging( false );
        yAxis.setLowerBound( 0 );
        yAxis.setTickUnit( 10 );

        cacheManager.addCacheListener( Casting.CACHE.REPORTING_CACHE_NAME, Casting.CACHE.REPORTING_SUMMARY_KEY, ( key ) -> {
            if ( scheduleFuture != null && !scheduleFuture.isDone() )
            {
                return;
            }
            scheduleFuture = scheduler.schedule( () -> {
                Platform.runLater( this::loadData );
                return null;
            }, 1, TimeUnit.SECONDS );
        } );
    }

    @FXML
    private void zoomOut( ActionEvent event )
    {
        if ( yAxis.getUpperBound() < 100 )
        {
            yAxis.setUpperBound( yAxis.getUpperBound() + 10 );
        }
    }

    @FXML
    private void zoomIn( ActionEvent event )
    {
        if ( yAxis.getUpperBound() > 10 )
        {
            yAxis.setUpperBound( yAxis.getUpperBound() - 10 );
        }
    }

    @FXML
    private void showLegend()
    {
        if ( barChart.isLegendVisible() )
        {
            barChart.setLegendVisible( false );
            legendButton.setText( "Legende einblenden" );
        }
        else
        {
            barChart.setLegendVisible( true );
            legendButton.setText( "Legende ausblenden" );
        }
    }

    private void loadData()
    {
        if ( outputBox.getChildren().size() > 1 )
        {
            outputBox.getChildren().remove( 1, outputBox.getChildren().size() );
        }

        createDowntimeChart();
        createOutputSummaries();
    }

    @SuppressWarnings( "unchecked" )
    private void createDowntimeChart()
    {
        barChart.setTitle( "Störzeiten Kostenstelle: " + costCenter );
        double aggregationValue = 0.04;
        String[] strings = { "letzte 24 Std", "letzte Woche", "dieser Monat", "letzter Monat" };
        Map<DowntimeKindDTO, Double> lastMonthData = calculateReportingForDowntimeKinds( dataProvider.getDowntimeSummary( costCenter, Casting.REPORTING.LAST_MONTH ), aggregationValue );
        Map<DowntimeKindDTO, Double> thisMonthData = calculateReportingForDowntimeKinds( dataProvider.getDowntimeSummary( costCenter, Casting.REPORTING.CURRENT_MONTH ), aggregationValue );
        Map<DowntimeKindDTO, Double> lastWeekData = calculateReportingForDowntimeKinds( dataProvider.getDowntimeSummary( costCenter, Casting.REPORTING.LAST_WEEK ), aggregationValue );
        Map<DowntimeKindDTO, Double> last24Data = calculateReportingForDowntimeKinds( dataProvider.getDowntimeSummary( costCenter, Casting.REPORTING.LAST_24H ), aggregationValue );

        barChart.getData().clear();
        barChart.getData().addAll( ReportingUtils.createStackedBarChartContent( true, strings, last24Data, lastWeekData, thisMonthData, lastMonthData ) );
        setTooltips();
    }

    private Map<DowntimeKindDTO, Double> calculateReportingForDowntimeKinds( ReportingDowntimeSummaryDTO summaryDTO, double aggregationValue )
    {
        Map<DowntimeKindDTO, Double> filteredDetail = new HashMap<>();
        Map<DowntimeKindDTO, Double> sortedMap = new LinkedHashMap<>();

        if ( summaryDTO != null )
        {
            LocalDateTime start = summaryDTO.getStart();
            LocalDateTime end = summaryDTO.getEnd();

            long intervalDurationInMinutes = excludePlannedDowntimes( calculateIntervalDuration( start, end ), start, end );

            summaryDTO.getDowntimeKindSummary().forEach( kindSummary -> {
                double dataValue = ( 1.0 / intervalDurationInMinutes ) * kindSummary.getDurationInMinutes();
                if ( filteredDetail.containsKey( kindSummary.getDowntimeKind() ) )
                {
                    filteredDetail.put( kindSummary.getDowntimeKind(), filteredDetail.get( kindSummary.getDowntimeKind() ) + dataValue );
                }
                else
                {
                    filteredDetail.put( kindSummary.getDowntimeKind(), dataValue );
                }
            } );

            sortedMap = filteredDetail.entrySet().stream().sorted( Entry.comparingByValue( Collections.reverseOrder() ) )
                    .collect( Collectors.toMap( Entry::getKey, Entry::getValue, ( e1, e2 ) -> e1, LinkedHashMap::new ) );

            if ( aggregationValue > 0 )
            {
                aggregateData( sortedMap, aggregationValue );
            }
        }

        return sortedMap;
    }

    private long calculateIntervalDuration( LocalDateTime start, LocalDateTime end )
    {
        return start.until( end, ChronoUnit.MINUTES );
    }

    private long excludePlannedDowntimes( long durationInMinutes, LocalDateTime start, LocalDateTime end )
    {
        LocalDateTime saturday = start.with( TemporalAdjusters.next( DayOfWeek.SATURDAY ) ).withHour( 22 ).withMinute( 0 ).withSecond( 0 ).withNano( 0 );
        LocalDateTime sunday = start.with( TemporalAdjusters.next( DayOfWeek.SUNDAY ) ).withHour( 21 ).withMinute( 59 ).withSecond( 59 ).withNano( 999999999 );

        while ( end.isAfter( saturday ) )
        {
            if ( start.isBefore( saturday ) )
            {
                if ( end.isAfter( sunday ) )
                {
                    durationInMinutes -= saturday.until( sunday, ChronoUnit.MINUTES );
                }
                else if ( end.isAfter( saturday ) )
                {
                    durationInMinutes -= saturday.until( end, ChronoUnit.MINUTES );
                }
            }
            else if ( start.isAfter( saturday ) && start.isBefore( sunday ) )
            {
                if ( end.isAfter( sunday ) )
                {
                    durationInMinutes -= start.until( sunday, ChronoUnit.MINUTES );
                }
                else
                {
                    durationInMinutes = 0;
                }
            }
            saturday = saturday.plusWeeks( 1 );
            sunday = sunday.plusWeeks( 1 );
        }
        return durationInMinutes;
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

    private void createOutputSummaries()
    {
        HBox shiftSummaries = new HBox();
        shiftSummaries.setSpacing( 5.0 );
        HBox intervalSummaries = new HBox();
        intervalSummaries.setSpacing( 5.0 );
        HBox dayBeforeSummaries = new HBox();
        dayBeforeSummaries.setSpacing( 5.0 );

        addOutputSummaryToBox( shiftSummaries, Casting.REPORTING.THIRD_LAST_SHIFT );
        addOutputSummaryToBox( shiftSummaries, Casting.REPORTING.SECOND_LAST_SHIFT );
        addOutputSummaryToBox( shiftSummaries, Casting.REPORTING.LAST_SHIFT );
        addOutputSummaryToBox( shiftSummaries, Casting.REPORTING.CURRENT_SHIFT );

        addOutputSummaryToBox( intervalSummaries, Casting.REPORTING.LAST_MONTH );
        addOutputSummaryToBox( intervalSummaries, Casting.REPORTING.CURRENT_MONTH );
        addOutputSummaryToBox( intervalSummaries, Casting.REPORTING.LAST_WEEK );
        addOutputSummaryToBox( intervalSummaries, Casting.REPORTING.LAST_24H );

        addOutputSummaryToBox( dayBeforeSummaries, Casting.REPORTING.DAY_BEFORE_SHIFT_1 );
        addOutputSummaryToBox( dayBeforeSummaries, Casting.REPORTING.DAY_BEFORE_SHIFT_2 );
        addOutputSummaryToBox( dayBeforeSummaries, Casting.REPORTING.DAY_BEFORE_SHIFT_3 );
        addOutputSummaryToBox( dayBeforeSummaries, Casting.REPORTING.DAY_BEFORE_TOTAL );

        Label label = new Label( "Vortag" );
        label.setStyle( "-fx-font-weight: bold;-fx-font-size: 18px;" );

        outputBox.getChildren().add( shiftSummaries );
        outputBox.getChildren().add( intervalSummaries );
        outputBox.getChildren().add( label );
        outputBox.getChildren().add( dayBeforeSummaries );
    }

    private void addOutputSummaryToBox( HBox hBox, String interval )
    {
        Node node = reportingViewUtils.createOutputSummary( dataProvider.getOutputSummary( costCenter, interval ) );
        if ( node != null )
        {
            hBox.getChildren().add( node );
        }
    }

    private void setTooltips()
    {
        for ( StackedBarChart.Series<String, Double> s : barChart.getData() )
        {
            for ( StackedBarChart.Data<String, Double> d : s.getData() )
            {
                String tooltipText = s.getName() + "\n Anteil: " + String.format( "%.2f", d.getYValue() ) + " %";
                Tooltip.install( d.getNode(), new Tooltip( tooltipText ) );

                // Adding class on hover
                InnerShadow innerShadow = new InnerShadow();
                innerShadow.setOffsetX( 2 );
                innerShadow.setColor( Color.GREY );

                d.getNode().setOnMouseEntered( event -> d.getNode().setEffect( innerShadow ) );

                // Removing class on exit
                d.getNode().setOnMouseExited( event -> d.getNode().setEffect( null ) );
            }
        }
    }

    @Override
    public void setStartObject( Object startObject )
    {
        if ( startObject instanceof String )
        {
            this.costCenter = (String) startObject;
            preferencesManager.setValue( PreferencesManager.SCOPE_USER, "/mes/reporting", "lastCostCenter", costCenter );
        }
    }

    @Override
    public void beforeShown( View view )
    {
        if ( this.costCenter == null )
        {
            // load cost center from registry
            this.costCenter = preferencesManager.getValue( PreferencesManager.SCOPE_USER, "/mes/reporting", "lastCostCenter", "80" );
        }
        detailReporting.setTitle( "Reporting Kostenstelle: " + costCenter );
        loadData();
    }

    @Override
    public void activateView( View view )
    {
    }

    @Override
    public void deactivateView( View view )
    {
    }

    @Override
    public void reloadView( Object source, View view )
    {
    }
}
