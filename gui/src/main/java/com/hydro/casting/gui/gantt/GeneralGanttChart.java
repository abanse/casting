package com.hydro.casting.gui.gantt;

import com.google.inject.Inject;
import com.hydro.casting.gui.planning.gantt.comp.LocalDateTimeStringConverter;
import com.hydro.casting.server.contract.downtime.DowntimeView;
import com.hydro.casting.server.contract.downtime.dto.DowntimeDTO;
import com.hydro.casting.server.contract.dto.TimeManagementDTO;
import com.hydro.casting.server.contract.dto.TimeManagementPhaseDTO;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.TaskManager;
import com.hydro.core.gui.comp.TaskProgressPane;
import com.hydro.core.gui.comp.chart.DateAxis310;
import com.hydro.core.gui.comp.chart.GanttChartWithDuration;
import com.hydro.core.gui.task.AbstractTask;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneralGanttChart extends BorderPane
{
    @Inject
    private TaskManager taskManager;

    @Inject
    private BusinessManager businessManager;

    private final LocalDateTimeStringConverter converter = new LocalDateTimeStringConverter( "dd.MM.yy\nHH:mm:ss" );

    protected DateAxis310 xAxis;
    private CategoryAxis yAxis;
    protected GanttChartWithDuration<String> chart;

    private List<String> machines;
    private Map<String, MachineSeries> machineSeriesMap = new HashMap<>();
    private List<TimeManagementDTO> timeManagements;

    private Map<String, List<DowntimeDTO>> downtimesMap;

    private TaskProgressPane taskProgressPane = new TaskProgressPane();
    private LoadTimeManagementsTask loadTimeManagementsTask = new LoadTimeManagementsTask();

    private boolean withFuture = false;

    public GeneralGanttChart()
    {
        xAxis = new DateAxis310( LocalDateTime.now().minusHours( 1 ), LocalDateTime.now(), false );
        yAxis = new CategoryAxis();

        chart = new GanttChartWithDuration( xAxis, yAxis );
        chart.setAnimated( false );
        xAxis.setTickLabelFormatter( converter );
        xAxis.setLabel( "" );
        xAxis.setTickLabelFill( Color.BLACK );
        xAxis.getStyleClass().add( "downtimeAxis" );

        yAxis.setLabel( "" );
        yAxis.setTickLabelFill( Color.BLACK );
        yAxis.setTickLabelGap( 10 );
        yAxis.getStyleClass().add( "downtimeAxis" );

        chart.setLegendVisible( false );
        chart.setMinHeight( 10.0 );
        chart.setMinWidth( 10.0 );
        chart.getStyleClass().add( "downtimeChart" );
        chart.setShowCurrentTime( false );

        taskProgressPane.setMainNode( chart );
        taskProgressPane.addTask( loadTimeManagementsTask );
        setCenter( taskProgressPane );
    }

    public boolean isWithFuture()
    {
        return withFuture;
    }

    public void setWithFuture( boolean withFuture )
    {
        this.withFuture = withFuture;
    }

    public void initialize( List<String> machines )
    {
        this.machines = machines;
        final ObservableList<String> categories = FXCollections.observableArrayList();
        for ( String machine : machines )
        {
            final MachineSeries machineSeries = new MachineSeries( machine );
            machineSeriesMap.put( machine, machineSeries );
            chart.getData().addAll( machineSeries.target, machineSeries.actual, machineSeries.delta );
            categories.addAll( machine + " Delta", machine + " Ist", machine + " Soll" );
        }
        yAxis.setCategories( categories );
    }

    public void doLoad( LocalDateTime fromTS, LocalDateTime toTS )
    {
        loadTimeManagementsTask.setData( fromTS, toTS );
        taskManager.executeTask( loadTimeManagementsTask );
    }

    public void loadData( List<TimeManagementDTO> timeManagements, Map<String, List<DowntimeDTO>> downtimesMap )
    {
        this.timeManagements = timeManagements;
        this.downtimesMap = downtimesMap;
        for ( MachineSeries machineSeries : machineSeriesMap.values() )
        {
            machineSeries.clear();
        }
        reload();
    }

    public void reload()
    {
        final LocalDateTime[] range = reloadInternal();
        if ( range != null )
        {
            xAxis.setRange( range, false );
        }
    }

    protected LocalDateTime[] reloadInternal()
    {
        if ( timeManagements == null || timeManagements.isEmpty() )
        {
            return null;
        }

        // Soll
        for ( TimeManagementDTO timeManagement : timeManagements )
        {
            if ( timeManagement.getMachine() == null )
            {
                continue;
            }
            final MachineSeries machineSeries = machineSeriesMap.get( timeManagement.getMachine() );
            if ( machineSeries == null )
            {
                continue;
            }

            handleTargetChart( timeManagement, machineSeries );
            handleActualChart( timeManagement, machineSeries );
        }

        handleDiffChart();

        LocalDateTime min = LocalDateTime.MAX;
        LocalDateTime max = LocalDateTime.MIN;
        final List<XYChart.Series<LocalDateTime, String>> seriesData = chart.getData();
        for ( XYChart.Series<LocalDateTime, String> seriesDatum : seriesData )
        {
            final List<XYChart.Data<LocalDateTime, String>> data = seriesDatum.getData();
            for ( XYChart.Data<LocalDateTime, String> datum : data )
            {
                final GanttChartWithDuration.ExtraData extraData = (GanttChartWithDuration.ExtraData) datum.getExtraValue();
                if ( datum.getXValue().isBefore( min ) )
                {
                    min = datum.getXValue();
                }
                if ( datum.getXValue().plus( extraData.getDuration() ).isAfter( max ) )
                {
                    max = datum.getXValue().plus( extraData.getDuration() );
                }
            }
        }
        return new LocalDateTime[] { min, max };
    }

    private void handleTargetChart( TimeManagementDTO timeManagement, MachineSeries machineSeries )
    {
        LocalDateTime start = timeManagement.getPlannedStart();

        for ( TimeManagementPhaseDTO phase : timeManagement.getPhases() )
        {
            if ( start == null && phase.getStart() == null )
            {
                continue;
            }
            if ( start == null )
            {
                start = phase.getStart();
            }

            final Color color = GanttChartUtil.calcColor( timeManagement, phase );
            final String tooltip = GanttChartUtil.calcTooltip( timeManagement, phase, start, phase.getPlannedDuration() );
            GanttChartUtil.updateXYChartData( machineSeries.target, start, timeManagement.getMachine() + " Soll", phase.getName(), phase.getPlannedDuration(), color, tooltip, phase );

            start = start.plus( phase.getPlannedDuration() );
        }
    }

    private void handleActualChart( TimeManagementDTO timeManagement, MachineSeries machineSeries )
    {
        final List<TimeManagementPhaseDTO> phases = timeManagement.getPhases();
        int index = 0;
        for ( TimeManagementPhaseDTO phase : phases )
        {
            if ( phase.getStart() == null )
            {
                continue;
            }
            if ( phase.getStart().isAfter( LocalDateTime.now() ) )
            {
                continue;
            }

            Duration duration = phase.getActualDuration();
            if ( phases.size() > index + 1 )
            {
                TimeManagementPhaseDTO next = phases.get( index + 1 );
                if ( next.getStart() != null )
                {
                    duration = Duration.between( phase.getStart(), next.getStart() );
                }
            }

            if ( phase.getStart().plus( duration ).isAfter( LocalDateTime.now() ) )
            {
                duration = Duration.between( phase.getStart(), LocalDateTime.now() );
            }

            final Color color = GanttChartUtil.calcColor( timeManagement, phase );
            final String tooltip = GanttChartUtil.calcTooltip( timeManagement, phase, phase.getStart(), duration );
            GanttChartUtil.updateXYChartData( machineSeries.actual, phase.getStart(), timeManagement.getMachine() + " Ist", phase.getName(), duration, color, tooltip, phase );

            index++;
        }
    }

    private void handleDiffChart()
    {
        for ( String machine : machineSeriesMap.keySet() )
        {
            MachineSeries machineSeries = machineSeriesMap.get( machine );
            List<DowntimeDTO> machineDowntimes = downtimesMap.get( machine );
            if ( machineDowntimes != null )
            {
                for ( DowntimeDTO downtime : machineDowntimes )
                {
                    Duration downtimeDuration = Duration.between( downtime.getFromTS(), downtime.getEndTS() );
                    Color color = Color.web( "#D41715" );
                    final String tooltip = GanttChartUtil.calcTooltip( downtime );
                    GanttChartUtil.updateXYChartData( machineSeries.delta, downtime.getFromTS(), machine + " Delta", downtime.getDescription(), downtimeDuration, color, tooltip, downtime );
                }
            }
        }
    }

    class MachineSeries
    {
        String machine;
        XYChart.Series<LocalDateTime, String> target = new XYChart.Series<>();
        XYChart.Series<LocalDateTime, String> actual = new XYChart.Series<>();
        XYChart.Series<LocalDateTime, String> delta = new XYChart.Series<>();

        public MachineSeries( String machine )
        {
            this.machine = machine;
        }

        public void clear()
        {
            target.getData().clear();
            actual.getData().clear();
            delta.getData().clear();
        }
    }

    class LoadTimeManagementsTask extends AbstractTask
    {
        private LocalDateTime fromTS;
        private LocalDateTime toTS;

        public void setData( LocalDateTime fromTS, LocalDateTime toTS )
        {
            this.fromTS = fromTS;
            this.toTS = toTS;
        }

        /**
         * Loads TimeManagements for the defined machines for the timeframe defined by fromTS and toTS and passes them to the loadData()-function for display in a gantt chart.
         * TimeManagements for casting and melting area are loaded separately, because their base data and retrieval algorithms differ heavily, but then merged into one result list here.
         * Responsibility for correct processing lies within the calling class, which can control the data loading by passing in the correct list of machines.
         *
         * @throws Exception
         */
        @Override
        public void doWork() throws Exception
        {
            final DowntimeView downtimeView = businessManager.getSession( DowntimeView.class );
            final List<TimeManagementDTO> timeManagements = downtimeView.loadTimeManagements( machines, fromTS, toTS, withFuture );
            timeManagements.addAll( downtimeView.loadTimeManagementsForMeltingArea( machines, fromTS, toTS, true ) );
            Map<String, List<DowntimeDTO>> downtimeMap = downtimeView.loadDowntimes( machines, fromTS, toTS );

            Platform.runLater( () -> loadData( timeManagements, downtimeMap ) );
        }

        @Override
        public String getId()
        {
            return null;
        }
    }
}