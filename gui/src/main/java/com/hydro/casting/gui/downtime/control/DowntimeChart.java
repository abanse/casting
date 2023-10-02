package com.hydro.casting.gui.downtime.control;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.downtime.dto.DowntimeDTO;
import com.hydro.core.common.cache.ClientCache;
import com.hydro.core.common.util.DateTimeUtil;
import com.hydro.core.gui.CacheManager;
import com.hydro.core.gui.comp.chart.DateAxis310;
import com.hydro.core.gui.comp.chart.GanttChart;
import com.hydro.core.gui.comp.chart.GanttChart.ExtraData;
import com.hydro.core.gui.comp.chart.LocalDateTimeStringConverter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.time.LocalDateTime;
import java.util.*;

public class DowntimeChart extends BorderPane
{
    private long hoursToDisplay = 8;

    private LocalDateTimeStringConverter converter = new LocalDateTimeStringConverter( "HH:mm:ss" );
    private DateAxis310 xAxis;
    private GanttChart<LocalDateTime, String> chart;

    private Timer timer;
    private Runnable downTimeRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            LocalDateTime[] range = new LocalDateTime[] { LocalDateTime.now().minusHours( hoursToDisplay ), LocalDateTime.now() };
            xAxis.setRange( range, false );
        }
    };

    private String[] cachePaths;
    private CacheManager cacheManager;
    String[] xAxisLabel = new String[] { "Störzeiten" };

    private Callback<String, List<DowntimeDTO>> dataProvider = null;

    public DowntimeChart()
    {

    }

    public Callback<String, List<DowntimeDTO>> getDataProvider()
    {
        return dataProvider;
    }

    public void setDataProvider( Callback<String, List<DowntimeDTO>> dataProvider )
    {
        this.dataProvider = dataProvider;
    }

    public String[] getCachePaths()
    {
        return cachePaths;
    }

    public void setCachePaths( String[] cachePaths )
    {
        this.cachePaths = cachePaths;
    }

    public void setXAxisLabel( String[] xAxisLabel )
    {
        this.xAxisLabel = xAxisLabel;
    }

    public void start()
    {
        if ( timer != null )
        {
            // is running
            return;
        }
        timer = new Timer( "downtime", true );
        timer.schedule( new DowntimeTimerTask(), 1000, 1000 );
    }

    public void stop()
    {
        timer.cancel();
        timer = null;
    }

    public void connect( CacheManager cacheManager )
    {
        this.cacheManager = cacheManager;

        Arrays.stream( cachePaths ).forEach( cachePath -> {
            cacheManager.addCacheListener( Casting.CACHE.DOWNTIME_CACHE_NAME, cachePath, ( key ) -> {
                loadData();
            } );
        } );

        loadData();
    }

    public void setHoursToDisplay( long hoursToDisplay )
    {
        this.hoursToDisplay = hoursToDisplay;
    }

    private void loadData()
    {
        final List<DowntimeDTO> downtimes = new ArrayList<>();
        if ( dataProvider != null )
        {
            Arrays.stream( cachePaths ).forEach( cachePath -> {
                downtimes.addAll( dataProvider.call( cachePath ) );
            } );
        }
        else
        {
            addDataFromCache( downtimes );
        }
        downtimes.sort( Comparator.comparing( DowntimeDTO::getFromTS ) );

        xAxis = new DateAxis310( LocalDateTime.now().minusHours( hoursToDisplay ), LocalDateTime.now(), false );
        final CategoryAxis yAxis = new CategoryAxis();
        chart = new GanttChart<>( xAxis, yAxis );
        chart.setAnimated( false );
        xAxis.setTickLabelFormatter( converter );
        xAxis.setLabel( "" );
        xAxis.setTickLabelFill( Color.BLACK );
        xAxis.getStyleClass().add( "downtimeAxis" );

        yAxis.setLabel( "" );
        yAxis.setTickLabelFill( Color.BLACK );
        yAxis.setTickLabelGap( 10 );
        yAxis.setCategories( FXCollections.observableArrayList( Arrays.asList( xAxisLabel ) ) );
        yAxis.getStyleClass().add( "downtimeAxis" );

        XYChart.Series<LocalDateTime, String> series1 = createXYChartForDowntimes( downtimes, xAxisLabel[0] );

        chart.setLegendVisible( false );
        chart.getData().add( series1 );
        chart.setMinHeight( 10.0 );
        chart.setMinWidth( 10.0 );
        chart.getStyleClass().add( "downtimeChart" );

        setCenter( chart );
    }

    /**
     * This method creates and returns a list with the current DowntimeDTOs in the cache
     *
     * @return a list with the DowntimeDTOs
     */
    private void addDataFromCache( final List<DowntimeDTO> downtimes )
    {
        ClientCache<DowntimeDTO> downtimeDataCache = cacheManager.getCache( Casting.CACHE.DOWNTIME_CACHE_NAME );
        ClientCache<List<Long>> downtimeSequenceCache = cacheManager.getCache( Casting.CACHE.DOWNTIME_CACHE_NAME );

        Arrays.stream( cachePaths ).forEach( cachePath -> {
            List<Long> downtimeSequence = downtimeSequenceCache.get( cachePath );

            if ( downtimeSequence != null )
            {
                Map<Long, DowntimeDTO> downtimeMap = downtimeDataCache.getAll( cachePath + "/data/", downtimeSequence );
                for ( Long serial : downtimeSequence )
                {
                    DowntimeDTO downtimeDTO = downtimeMap.get( serial );
                    if ( downtimeDTO == null )
                    {
                        continue;
                    }
                    downtimes.add( downtimeDTO );
                }
            }
        } );
    }

    private XYChart.Series<LocalDateTime, String> createXYChartForDowntimes( List<DowntimeDTO> downtimes, String xAxisLabel )
    {
        XYChart.Series<LocalDateTime, String> xyChart = new XYChart.Series<>();

        if ( !downtimes.isEmpty() )
        {
            final DowntimeDTO firstDowntime = downtimes.get( 0 );
            if ( firstDowntime == null )
            {
                xyChart.getData().add( new XYChart.Data<>( LocalDateTime.now().minusHours( hoursToDisplay ), xAxisLabel,
                        createExtraData( false, null, null, "", LocalDateTime.now().minusHours( hoursToDisplay ), null, null ) ) );
            }
            else
            {
                xyChart.getData().add( new XYChart.Data<>( LocalDateTime.now().minusHours( hoursToDisplay ), xAxisLabel,
                        createExtraData( false, firstDowntime.getCostCenter(), firstDowntime.getDowntimeDescription(), firstDowntime.getDescription(), LocalDateTime.now().minusHours( hoursToDisplay ),
                                firstDowntime.getFromTS(), firstDowntime.getType() ) ) );
                for ( int i = 0; i < downtimes.size(); i++ )
                {
                    DowntimeDTO downtime = downtimes.get( i );
                    if ( downtime == null )
                    {
                        continue;
                    }
                    DowntimeDTO nextDowntime = null;
                    if ( downtimes.size() > i + 1 )
                    {
                        nextDowntime = downtimes.get( i + 1 );
                    }
                    if ( downtime.getEndTS() != null )
                    {
                        xyChart.getData().add( new XYChart.Data<>( downtime.getFromTS(), xAxisLabel,
                                createExtraData( true, downtime.getCostCenter(), downtime.getDowntimeDescription(), downtime.getDescription(), downtime.getFromTS(), downtime.getEndTS(),
                                        downtime.getType() ) ) );
                        if ( nextDowntime != null )
                        {
                            xyChart.getData()
                                    .add( new XYChart.Data<>( downtime.getEndTS(), xAxisLabel, createExtraData( false, null, null, null, downtime.getEndTS(), nextDowntime.getFromTS(), null ) ) );
                        }
                        else
                        {
                            xyChart.getData().add( new XYChart.Data<>( downtime.getEndTS(), xAxisLabel, createExtraData( false, null, null, null, downtime.getEndTS(), null, null ) ) );
                        }
                    }
                    else
                    {
                        xyChart.getData().add( new XYChart.Data<>( downtime.getFromTS(), xAxisLabel,
                                createExtraData( true, downtime.getCostCenter(), downtime.getDowntimeDescription(), downtime.getDescription(), downtime.getFromTS(), null, downtime.getType() ) ) );
                    }
                }
            }
        }
        else
        {
            xyChart.getData().add( new XYChart.Data<>( LocalDateTime.now().minusHours( hoursToDisplay ), xAxisLabel,
                    createExtraData( false, null, null, "", LocalDateTime.now().minusHours( hoursToDisplay ), null, null ) ) );
        }
        return xyChart;
    }

    private ExtraData createExtraData( boolean error, String costCenter, String reason, String description, LocalDateTime from, LocalDateTime to, String type )
    {
        String color;
        StringBuilder descSB = new StringBuilder();
        if ( error )
        {
            if ( costCenter != null )
            {
                descSB.append( "Anlage " + costCenter );
                if ( type != null )
                {
                    descSB.append( " (" + type + ")\n" );
                }
                else
                {
                    descSB.append( "\n" );
                }
            }
            if ( reason != null )
            {
                descSB.append( reason + "\n" );
            }
            descSB.append( description + "\n" );
            color = "downtime-status-red";
        }
        else
        {
            descSB.append( "Produktion\n" );
            color = "downtime-status-green";
        }
        descSB.append( "von: " + converter.toString( from ) + "\n" );
        if ( to != null )
        {
            descSB.append( "bis: " + converter.toString( to ) );
        }
        else
        {
            descSB.append( "bis: jetzt" );
        }
        // Dauer
        if ( to != null )
        {
            descSB.append( "\n" );
            descSB.append( "Dauer: " + DateTimeUtil.formatDuration( from, to ) );
        }
        return new ExtraData( color, descSB.toString() );
    }

    private class DowntimeTimerTask extends TimerTask
    {
        @Override
        public void run()
        {
            Platform.runLater( downTimeRunnable );
        }
    }

}