package com.hydro.casting.gui.prod.gantt;

import com.hydro.casting.gui.gantt.GanttChartUtil;
import com.hydro.casting.server.contract.dto.TimeManagementDTO;
import com.hydro.casting.server.contract.dto.TimeManagementPhaseDTO;
import com.hydro.core.gui.comp.chart.DateAxis310;
import com.hydro.core.gui.comp.chart.GanttChartWithDuration;
import com.hydro.core.gui.comp.chart.LocalDateTimeStringConverter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TimeManagementGanttChart extends BorderPane
{
    private long hoursToDisplay = 8;

    private LocalDateTimeStringConverter converter = new LocalDateTimeStringConverter( "HH:mm:ss" );
    private DateAxis310 xAxis;
    private GanttChartWithDuration<String> chart;

    private XYChart.Series<LocalDateTime, String> target = new XYChart.Series<>();
    private XYChart.Series<LocalDateTime, String> actual = new XYChart.Series<>();
    private XYChart.Series<LocalDateTime, String> delta = new XYChart.Series<>();

    private List<TimeManagementDTO> timeManagements;

    private Timer timer;
    private Runnable timeManagementRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            reload();
        }
    };

    public TimeManagementGanttChart()
    {
        xAxis = new DateAxis310( LocalDateTime.now().minusHours( hoursToDisplay ), LocalDateTime.now(), false );
        final CategoryAxis yAxis = new CategoryAxis();
        chart = new GanttChartWithDuration( xAxis, yAxis );
        chart.setAnimated( false );
        xAxis.setTickLabelFormatter( converter );
        xAxis.setLabel( "" );
        xAxis.setTickLabelFill( Color.BLACK );
        xAxis.getStyleClass().add( "downtimeAxis" );

        yAxis.setLabel( "" );
        yAxis.setTickLabelFill( Color.BLACK );
        yAxis.setTickLabelGap( 10 );
        yAxis.setCategories( FXCollections.observableArrayList( "Delta", "Ist", "Soll" ) );
        yAxis.getStyleClass().add( "downtimeAxis" );

        chart.setLegendVisible( false );
        chart.setMinHeight( 10.0 );
        chart.setMinWidth( 10.0 );
        chart.getStyleClass().add( "downtimeChart" );
        chart.setShowCurrentTime( true );

        chart.getData().addAll( target, actual, delta );

        setCenter( chart );
    }

    public void start()
    {
        if ( timer != null )
        {
            // is running
            return;
        }
        timer = new Timer( "timeManagement", true );
        timer.schedule( new TimeManagementTimerTask(), 1000, 1000 );
        reload();
    }

    public void stop()
    {
        if ( timer != null )
        {
            timer.cancel();
        }
        timer = null;
    }

    public void loadData( List<TimeManagementDTO> timeManagements )
    {
        this.timeManagements = timeManagements;
        target.getData().clear();
        actual.getData().clear();
        delta.getData().clear();
        reload();
    }

    public void reload()
    {
        LocalDateTime[] range = new LocalDateTime[] { LocalDateTime.now().minusHours( hoursToDisplay / 2 ), LocalDateTime.now().plusHours( hoursToDisplay / 2 ) };
        xAxis.setRange( range, false );

        if ( timeManagements == null )
        {
            return;
        }

        LocalDateTime lastEnd = null;
        for ( TimeManagementDTO timeManagement : timeManagements )
        {
            LocalDateTime start = timeManagement.getPlannedStart();
            if ( start == null )
            {
                if ( lastEnd == null )
                {
                    start = LocalDateTime.now();
                }
                else
                {
                    start = lastEnd;
                }
            }
            for ( TimeManagementPhaseDTO phase : timeManagement.getPhases() )
            {
                final Color color = GanttChartUtil.calcColor( timeManagement, phase );

                if ( phase.getStart() != null )
                {
                    start = phase.getStart();
                }
                else
                {
                    if ( start.isBefore( LocalDateTime.now() ) )
                    {
                        Duration startToNow = Duration.between( start, LocalDateTime.now() );
                        final String tooltip = GanttChartUtil.calcTooltip( timeManagement, phase, null, null );
                        GanttChartUtil.updateXYChartData( target, start, "Soll", phase.getName(), startToNow, new Color( color.getRed(), color.getGreen(), color.getBlue(), 0.5 ), tooltip, phase );
                        start = LocalDateTime.now();
                    }
                }
                Duration duration = phase.getPlannedDuration();
                if ( duration.isNegative() )
                {
                    duration = duration.abs();
                }
                if ( phase.getActualDuration() != null && duration.compareTo( phase.getActualDuration() ) > 0 )
                {
                    duration = phase.getActualDuration();
                }
                final String tooltip = GanttChartUtil.calcTooltip( timeManagement, phase, start, duration );

                GanttChartUtil.updateXYChartData( target, start, "Soll", phase.getName(), duration, color, tooltip, phase );
                start = start.plus( duration );
            }
            lastEnd = start;
        }

        for ( TimeManagementDTO timeManagement : timeManagements )
        {
            final List<TimeManagementPhaseDTO> phases = timeManagement.getPhases();
            int index = 0;
            for ( TimeManagementPhaseDTO phase : phases )
            {
                if ( phase.getStart() == null )
                {
                    continue;
                }
                Duration duration = phase.getActualDuration();
                if ( duration == null )
                {
                    duration = phase.getPlannedDuration();
                    if ( duration.isNegative() )
                    {
                        duration = duration.abs();
                    }
                }
                if ( phases.size() > index + 1 )
                {
                    TimeManagementPhaseDTO next = phases.get( index + 1 );
                    if ( next.getStart() != null )
                    {
                        duration = Duration.between( phase.getStart(), next.getStart() );
                    }
                    else
                    {
                        final LocalDateTime plannedEnd = phase.getStart().plus( duration );
                        if ( plannedEnd.isBefore( LocalDateTime.now() ) )
                        {
                            duration = Duration.between( phase.getStart(), LocalDateTime.now() );
                        }
                    }
                }
                final LocalDateTime plannedEnd = phase.getStart().plus( duration );
                final String tooltip;
                if ( plannedEnd.isAfter( LocalDateTime.now() ) )
                {
                    duration = Duration.between( phase.getStart(), LocalDateTime.now() );
                    tooltip = GanttChartUtil.calcTooltip( timeManagement, phase, phase.getStart(), null );
                }
                else
                {
                    tooltip = GanttChartUtil.calcTooltip( timeManagement, phase, phase.getStart(), duration );
                }
                final Color color = GanttChartUtil.calcColor( timeManagement, phase );

                GanttChartUtil.updateXYChartData( actual, phase.getStart(), "Ist", phase.getName(), duration, color, tooltip, phase );
                index++;
            }
        }

        boolean lastTimeManagement = false;
        for ( TimeManagementDTO timeManagement : timeManagements )
        {
            if ( timeManagements.indexOf( timeManagement ) >= timeManagements.size() - 1 )
            {
                lastTimeManagement = true;
            }
            final List<TimeManagementPhaseDTO> phases = timeManagement.getPhases();
            int index = 0;
            boolean finished = false;
            for ( TimeManagementPhaseDTO phase : phases )
            {
                if ( phase.getStart() == null && index == 0 )
                {
                    if ( timeManagement.getPlannedStart() != null && timeManagement.getPlannedStart().isBefore( LocalDateTime.now() ) )
                    {
                        Color color = Color.web( "#D41715" );
                        final String tooltip = GanttChartUtil.calcTooltip( timeManagement, phase, timeManagement.getPlannedStart(), null, "Startverzögerung" );
                        Duration duration = Duration.between( timeManagement.getPlannedStart(), LocalDateTime.now() );
                        GanttChartUtil.updateXYChartData( delta, timeManagement.getPlannedStart(), "Delta", phase.getName(), duration, color, tooltip, phase );
                    }
                    finished = true;
                    break;
                }
                if ( phase.getStart() != null && phase.getActualDuration() == null )
                {
                    Duration plannedDuration = phase.getPlannedDuration();
                    if ( plannedDuration.isNegative() )
                    {
                        plannedDuration = plannedDuration.abs();
                    }
                    final LocalDateTime plannedEnd = phase.getStart().plus( plannedDuration );
                    if ( plannedEnd.isBefore( LocalDateTime.now() ) && lastTimeManagement )
                    {
                        Color color = Color.web( "#D41715" );
                        final String tooltip = GanttChartUtil.calcTooltip( timeManagement, phase, plannedEnd, null, "Zeitverzögerung" );
                        Duration duration = Duration.between( plannedEnd, LocalDateTime.now() );
                        GanttChartUtil.updateXYChartData( delta, plannedEnd, "Delta", phase.getName(), duration, color, tooltip, phase );
                        finished = true;
                        break;
                    }
                }
                Color color = Color.web( "#D41715" );
                if ( phases.size() > index + 1 )
                {
                    TimeManagementPhaseDTO next = phases.get( index + 1 );
                    if ( next.getStart() != null && !phase.getPlannedDuration().isNegative() )
                    {
                        LocalDateTime deltaStart = phase.getStart().plus( phase.getPlannedDuration().abs() );
                        Duration duration = Duration.between( deltaStart, next.getStart() );
                        final String tooltip = GanttChartUtil.calcTooltip( timeManagement, phase, deltaStart, duration, "Zeitverzögerung" );
                        GanttChartUtil.updateXYChartData( delta, deltaStart, "Delta", phase.getName(), duration, color, tooltip, phase );
                    }
                }
                index++;
            }
            if ( finished )
            {
                break;
            }
        }

    }

    private class TimeManagementTimerTask extends TimerTask
    {
        @Override
        public void run()
        {
            Platform.runLater( timeManagementRunnable );
        }
    }

}