package com.hydro.casting.gui.gantt;

import com.google.inject.Inject;
import com.hydro.casting.common.constant.MachineStep;
import com.hydro.casting.common.constant.MelterStep;
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
import java.util.function.BiFunction;

public class ProcessStepsGanttChart extends BorderPane
{
    private final static String DOWNTIME_CATEGORY = "Störzeiten";
    private final static String DATE_TIME_FORMAT = "dd.MM.yy\nHH:mm:ss";
    private final static String RED_COLOR = "#D41715";
    private static final BiFunction<String, String, String> categoryConverter = ( machine, machineStepDesc ) -> machine + " " + machineStepDesc;
    @Inject
    private TaskManager taskManager;
    @Inject
    private BusinessManager businessManager;

    private final DateAxis310 xAxis;
    private final CategoryAxis yAxis;
    private final GanttChartWithDuration<String> chart;
    private List<String> machines;
    private final Map<String, MachineSeries> machineSeriesMap = new HashMap<>();
    private final LoadTimeManagementsTask loadTimeManagementsTask = new LoadTimeManagementsTask();
    private List<TimeManagementDTO> timeManagements;
    private Map<String, List<DowntimeDTO>> downtimesMap;

    public ProcessStepsGanttChart()
    {
        xAxis = new DateAxis310( LocalDateTime.now().minusHours( 1 ), LocalDateTime.now(), false );
        yAxis = new CategoryAxis();

        chart = new GanttChartWithDuration<>( xAxis, yAxis );
        chart.setAnimated( false );
        xAxis.setTickLabelFormatter( new LocalDateTimeStringConverter( DATE_TIME_FORMAT ) );
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

        TaskProgressPane taskProgressPane = new TaskProgressPane();
        taskProgressPane.setMainNode( chart );
        taskProgressPane.addTask( loadTimeManagementsTask );
        setCenter( taskProgressPane );
    }

    public void initialize( List<String> machines )
    {
        this.machines = machines;
        final ObservableList<String> categories = FXCollections.observableArrayList();
        for ( String machine : machines )
        {
            final MachineSeries machineSeries = new MachineSeries( machine );
            machineSeriesMap.put( machine, machineSeries );
            for ( Map.Entry<MelterStep, XYChart.Series<LocalDateTime, String>> seriesEntry : machineSeries.valuesMap.entrySet() )
            {
                chart.getData().add( seriesEntry.getValue() );
                categories.add( categoryConverter.apply( machine, seriesEntry.getKey().getDescription() ) );
            }
            chart.getData().add( machineSeries.downtimeSeries );
            categories.add( categoryConverter.apply( machine, DOWNTIME_CATEGORY ) );
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

            for ( Map.Entry<MelterStep, XYChart.Series<LocalDateTime, String>> seriesEntry : machineSeries.valuesMap.entrySet() )
            {
                handleSeries( timeManagement, seriesEntry.getKey(), seriesEntry.getValue() );
            }
        }

        handleDowntimes();

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

    private void handleSeries( TimeManagementDTO timeManagement, MachineStep machineStep, XYChart.Series<LocalDateTime, String> series )
    {
        LocalDateTime start = timeManagement.getPlannedStart();

        for ( TimeManagementPhaseDTO phase : timeManagement.getPhases() )
        {
            if ( !phase.getName().equals( machineStep.getShortName() ) || ( start == null && phase.getStart() == null ) )
            {
                continue;
            }

            start = phase.getStart();
            final Color color = GanttChartUtil.calcColor( timeManagement, phase );
            final String tooltip = GanttChartUtil.calcTooltip( timeManagement, phase, start, phase.getPlannedDuration() );
            GanttChartUtil.updateXYChartData( series, start, categoryConverter.apply( timeManagement.getMachine(), machineStep.getDescription() ), phase.getName(), phase.getPlannedDuration(), color,
                    tooltip, phase );
        }
    }

    private void handleDowntimes()
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
                    Color color = Color.web( RED_COLOR );
                    final String tooltip = GanttChartUtil.calcTooltip( downtime );
                    GanttChartUtil.updateXYChartData( machineSeries.downtimeSeries, downtime.getFromTS(), categoryConverter.apply( machine, DOWNTIME_CATEGORY ), downtime.getDescription(),
                            downtimeDuration, color, tooltip, downtime );
                }
            }
        }
    }

    private static class MachineSeries
    {
        String machine;
        Map<MelterStep, XYChart.Series<LocalDateTime, String>> valuesMap = new HashMap<>();
        private final XYChart.Series<LocalDateTime, String> mainChamberSkimmingSeries = new XYChart.Series<>();
        private final XYChart.Series<LocalDateTime, String> meltingChamberSkimmingSeries = new XYChart.Series<>();
        private final XYChart.Series<LocalDateTime, String> mixingSeries = new XYChart.Series<>();
        private final XYChart.Series<LocalDateTime, String> meltingSeries = new XYChart.Series<>();
        private final XYChart.Series<LocalDateTime, String> pouringSeries = new XYChart.Series<>();
        final XYChart.Series<LocalDateTime, String> downtimeSeries = new XYChart.Series<>();

        public MachineSeries( String machine )
        {
            this.machine = machine;

            valuesMap.put( MelterStep.Skimming, mainChamberSkimmingSeries );
            valuesMap.put( MelterStep.SkimmingMeltingChamber, meltingChamberSkimmingSeries );
            valuesMap.put( MelterStep.Mixing, mixingSeries );
            valuesMap.put( MelterStep.Melting, meltingSeries );
            valuesMap.put( MelterStep.Pouring, pouringSeries );
        }

        public void clear()
        {
            mainChamberSkimmingSeries.getData().clear();
            meltingChamberSkimmingSeries.getData().clear();
            mixingSeries.getData().clear();
            meltingSeries.getData().clear();
            pouringSeries.getData().clear();
            downtimeSeries.getData().clear();
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
         * @throws Exception if something goes wrong when contacting the server
         */
        @Override
        public void doWork() throws Exception
        {
            final DowntimeView downtimeView = businessManager.getSession( DowntimeView.class );
            final List<TimeManagementDTO> timeManagements = downtimeView.loadTimeManagementsForMeltingArea( machines, fromTS, toTS, false );
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
