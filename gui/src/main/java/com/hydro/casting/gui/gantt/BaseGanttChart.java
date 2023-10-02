package com.hydro.casting.gui.gantt;

import com.google.inject.Inject;
import com.hydro.casting.common.Casting;
import com.hydro.casting.gui.model.Batch;
import com.hydro.casting.gui.model.CastingClientModel;
import com.hydro.casting.gui.model.Transfer;
import com.hydro.casting.gui.planning.gantt.common.EGanttColorScheme;
import com.hydro.casting.gui.planning.gantt.comp.GanttChart;
import com.hydro.casting.gui.planning.gantt.comp.LocalDateTimeStringConverter;
import com.hydro.casting.gui.planning.gantt.model.CGElement;
import com.hydro.casting.gui.planning.gantt.model.CGResource;
import com.hydro.casting.gui.planning.gantt.node.AGanttChartNode;
import com.hydro.casting.gui.planning.gantt.solver.CastingGanttSolver;
import com.hydro.casting.gui.planning.task.SetConcreteDurationTask;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.core.gui.ClientModelManager;
import com.hydro.core.gui.TaskManager;
import com.hydro.core.gui.comp.chart.DateAxis310;
import com.hydro.core.gui.model.ClientModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseGanttChart extends BorderPane
{
    protected static final int BATCHES_TO_LOAD = 10;

    private final long hoursToDisplay = 8;
    private final LocalDateTimeStringConverter converter = new LocalDateTimeStringConverter( "dd.MM.yy\nHH:mm:ss" );
    private final DateAxis310 xAxis;
    private final GanttChart chart;
    private final Slider zoomSlider = new Slider();
    private final ScrollBar timeLineSlider = new ScrollBar();
    private final Map<String, CGResource> resources = new HashMap<>();
    private final CastingGanttSolver solver = new CastingGanttSolver( resources );
    private final ObjectProperty<EGanttColorScheme> colorScheme = new SimpleObjectProperty<EGanttColorScheme>( EGanttColorScheme.TRANSFER );

    private final Map<Transfer, CGElement> transferIndex = new HashMap<>();
    private final Map<Batch, CGElement> batchIndex = new HashMap<>();
    @Inject
    private ClientModelManager clientModelManager;
    @Inject
    private SetConcreteDurationTask setConcreteDurationTask;
    @Inject
    protected TaskManager taskManager;

    private XYChart.Series<LocalDateTime, String> chartContent;

    private ObjectProperty<CGElement> focusedElement = new SimpleObjectProperty<>();

    public BaseGanttChart( final String[] machines )
    {
        xAxis = new DateAxis310( LocalDateTime.now(), LocalDateTime.now().plusHours( hoursToDisplay ), false );
        final CategoryAxis yAxis = new CategoryAxis();
        chart = new GanttChart( xAxis, yAxis );
        chart.setAnimated( false );
        xAxis.setTickLabelFormatter( converter );
        xAxis.setLabel( "" );
        xAxis.setTickLabelFill( Color.BLACK );
        xAxis.getStyleClass().add( "castingGanttAxis" );

        yAxis.setLabel( "" );
        yAxis.setTickLabelFill( Color.BLACK );
        yAxis.setTickLabelGap( 10 );
        yAxis.setCategories( FXCollections.observableArrayList( Arrays.asList( machines ) ) );
        yAxis.getStyleClass().add( "castingGanttAxis" );

        chart.setLegendVisible( false );
        chart.setMinHeight( 10.0 );
        chart.setMinWidth( 10.0 );
        chart.getStyleClass().add( "castingGantt" );

        setCenter( chart );

        zoomSlider.setOrientation( Orientation.VERTICAL );
        zoomSlider.setMin( 4.0 );
        zoomSlider.setMajorTickUnit( 0.5 );
        zoomSlider.setMinorTickCount( 2 );
        zoomSlider.setMax( 15.0 );
        zoomSlider.setValue( 8.0 );
        zoomSlider.valueProperty().addListener( ( p, o, n ) -> {
            refreshTimeLine();
        } );
        zoomSlider.setPadding( new Insets( 5.0 ) );
        setRight( zoomSlider );

        timeLineSlider.setOrientation( Orientation.HORIZONTAL );
        timeLineSlider.setMin( 0.0 );
        timeLineSlider.setMax( 1.0 );
        timeLineSlider.setValue( 0.0 );
        timeLineSlider.valueProperty().addListener( ( p, o, n ) -> {
            refreshTimeLine();
        } );
        setBottom( timeLineSlider );

        colorScheme.addListener( event -> repaintAll() );

        focusedElement.addListener( ( observable, oldValue, newValue ) -> {
            if ( newValue != null && newValue.getResource() != null && Casting.isAlloySource( newValue.getResource().getName() ) )
            {
                final CGElement target = newValue.getInnerElementParent();
                final Color markerColor;
                final String resourceName = newValue.getResource().getName();
                if ( resourceName.equals( Casting.ALLOY_SOURCES.MELTING_FURNACE_S1 ) )
                {
                    markerColor = AGanttChartNode.ALLOY_SOURCE_S1;
                }
                else if ( resourceName.equals( Casting.ALLOY_SOURCES.MELTING_FURNACE_S2 ) )
                {
                    markerColor = AGanttChartNode.ALLOY_SOURCE_S2;
                }
                else if ( resourceName.equals( Casting.ALLOY_SOURCES.UBC_S3 ) )
                {
                    markerColor = AGanttChartNode.ALLOY_SOURCE_S3;
                }
                else if ( resourceName.equals( Casting.ALLOY_SOURCES.REAL_ALLOY ) )
                {
                    markerColor = AGanttChartNode.ALLOY_SOURCE_RA;
                }
                else if ( resourceName.equals( Casting.ALLOY_SOURCES.ELEKTROLYSE ) )
                {
                    markerColor = AGanttChartNode.ALLOY_SOURCE_EL;
                }
                else
                {
                    markerColor = Color.INDIANRED;
                }

                chart.showMarker( markerColor, newValue, target );
            }
            else
            {
                chart.hideMarker();
            }
        } );
    }

    @Inject
    private void initialize()
    {
        refreshTimeLine();
    }

    private void repaintAll()
    {
        chart.getData().stream().forEach( d -> {
            d.getData().stream().forEach( e -> {
                if ( e.getNode() instanceof AGanttChartNode )
                {
                    ( (AGanttChartNode) e.getNode() ).revalidate();
                }
            } );
        } );
    }

    public DateAxis310 getXAxis()
    {
        return xAxis;
    }

    private void refreshTimeLine()
    {
        final double visibleAmount = zoomSlider.getValue() / zoomSlider.getMax();

        timeLineSlider.setVisibleAmount( visibleAmount );

        final double timeShift = ( zoomSlider.getMax() - zoomSlider.getValue() ) * timeLineSlider.getValue();

        final LocalDateTime start = getStartHorizont();
        final LocalDateTime horizontStart = start.plusSeconds( Math.round( timeShift * 3600.0 ) );
        final LocalDateTime horizontEnd = horizontStart.plusSeconds( Math.round( zoomSlider.getValue() * 3600.0 ) );

        doRefreshExtTimeLine( horizontStart, horizontEnd );

        final LocalDateTime[] range = new LocalDateTime[] { horizontStart, horizontEnd };
        xAxis.setRange( range, false );
    }

    protected abstract void doRefreshExtTimeLine( final LocalDateTime horizontStart, final LocalDateTime horizontEnd );

    // Lade vom Server
    public void load()
    {
        loadModel();
    }

    // Standards zurücksetzen
    /*
    public void reset()
    {
        Alert question = new Alert( AlertType.CONFIRMATION, "Wollen Sie wirklich die Daten zurücksetzen? Lokale Zeit-Einstellungen gehen dabei verloren.", ButtonType.YES, ButtonType.NO );
        question.getDialogPane().setGraphic( null );
        question.setHeaderText( "Lade Standards" );
        Optional<ButtonType> result = question.showAndWait();
        if ( result.isPresent() == false || result.get() == null || result.get() != ButtonType.YES )
        {
            return;
        }

        final CastingClientModel casterModel = (CastingClientModel) clientModelManager.getClientModel( CastingClientModel.ID );
        casterModel.clearLocalKnowledgeStore( "duration" );
        loadModel();
    }
     */

    // Aktualisieren
    public void rebuild()
    {
        loadModel();
    }

    private void loadModel()
    {
        chart.getData().clear();
        resources.clear();
        transferIndex.clear();
        batchIndex.clear();

        chartContent = new XYChart.Series<>();

        final ClientModel casterModel = clientModelManager.getClientModel( CastingClientModel.ID );
        doLoadModel( chartContent, casterModel );

        chart.getData().add( chartContent );

        solve();
    }

    protected abstract void doLoadModel( XYChart.Series<LocalDateTime, String> chart, ClientModel casterModel );

    public void solve()
    {
        solver.solve();

        LocalDateTime maxTime = solver.getMaxTime();

        if ( maxTime != null )
        {
            extendGantt( maxTime );
        }
    }

    protected void addTransferToIndex( Transfer transfer, CGElement cgElement )
    {
        transferIndex.put( transfer, cgElement );
    }

    protected Duration getDuration( String category, CGElement cgElement, long defaultValue, ChronoUnit timeUnit )
    {
        final String name = calcDurationName( category, cgElement );
        if ( category.contains( "busy" ) )
        {
            cgElement.setDurationSetupAfterName( name );
        }
        else
        {
            cgElement.setDurationName( name );
        }
        final CastingClientModel casterModel = (CastingClientModel) clientModelManager.getClientModel( CastingClientModel.ID );

        final Duration dura;
        if ( defaultValue == 0 )
        {
            dura = Duration.ZERO;
        }
        else
        {
            dura = Duration.of( defaultValue, timeUnit );
        }
        final Batch batch = getBatch( cgElement );
        final Transfer transfer = getTransfer( cgElement );
        return Duration.ofMinutes( (long) casterModel.getDuration( batch, transfer, category, dura.toMinutes() ) );
    }

    private void extendGantt( LocalDateTime lastEnd )
    {
        if ( lastEnd != null )
        {
            double maxHours = Duration.between( getStartHorizont(), lastEnd ).get( ChronoUnit.SECONDS ) / 3600.0;
            if ( maxHours > zoomSlider.getMax() )
            {
                zoomSlider.setMax( maxHours );
            }
            refreshTimeLine();
        }
    }

    protected CGResource getResource( String name )
    {
        CGResource resource = resources.get( name );
        if ( resource == null )
        {
            resource = new CGResource();
            resource.setName( name );
            resources.put( name, resource );
        }
        return resource;
    }

    private LocalDateTime getStartHorizont()
    {
        LocalDateTime start = null;
        final Collection<CGResource> resEntries = resources.values();
        for ( CGResource resEntry : resEntries )
        {
            if ( start == null || start.isAfter( resEntry.getStartTime() ) )
            {
                start = resEntry.getStartTime();
            }
        }
        if ( start == null )
        {
            return LocalDateTime.now();
        }
        return start;
    }

    public final ObjectProperty<EGanttColorScheme> colorSchemeProperty()
    {
        return this.colorScheme;
    }

    public final EGanttColorScheme getColorScheme()
    {
        return this.colorSchemeProperty().get();
    }

    public final void setColorScheme( final EGanttColorScheme colorScheme )
    {
        this.colorSchemeProperty().set( colorScheme );
    }

    public static String calcDurationName( String category, CGElement cgElement )
    {
        final String name;
        if ( cgElement.getElement() instanceof Batch )
        {
            final Batch batch = (Batch) cgElement.getElement();
            name = "duration." + category + "." + batch.getCaster().getName() + "." + batch.getAlloy() + "." + batch.getName();
        }
        else if ( cgElement.getElement() instanceof Transfer )
        {
            final Transfer transfer = (Transfer) cgElement.getElement();
            if ( category.startsWith( "transfer" ) && cgElement.getResource() != null && Casting.isAlloySource( cgElement.getResource().getName() ) )
            {
                name = "duration." + category + "." + cgElement.getResource().getName() + "." + transfer.getBatch().getAlloy() + "." + transfer.getName();
            }
            else
            {
                name = "duration." + category + "." + transfer.getFrom().getName() + "." + transfer.getBatch().getAlloy() + "." + transfer.getName();
            }
        }
        else
        {
            name = "duration." + category + "." + cgElement.getName();
        }
        return name;
    }

    public static Batch getBatch( CGElement cgElement )
    {
        if ( cgElement.getElement() instanceof Batch )
        {
            return (Batch) cgElement.getElement();
        }
        else if ( cgElement.getElement() instanceof Transfer )
        {
            final Transfer transfer = (Transfer) cgElement.getElement();
            return transfer.getBatch();
        }
        return null;
    }

    public static Transfer getTransfer( CGElement cgElement )
    {
        if ( cgElement.getElement() instanceof Transfer )
        {
            return (Transfer) cgElement.getElement();
        }
        return null;
    }

    public void setFocusedElement( CGElement element )
    {
        focusedElement.setValue( element );
    }

    public void setConcreteDuration( CGElement element, Duration duration )
    {
        if ( element == null || element.getElement() == null )
        {
            return;
        }
        boolean forCasting = true;
        Batch batch = null;
        if ( element.getElement() instanceof Transfer )
        {
            forCasting = false;
            batch = ( (Transfer) element.getElement() ).getBatch();
        }
        else if ( element.getElement() instanceof Batch )
        {
            batch = (Batch) element.getElement();
        }
        else
        {
            return;
        }

        final ClientModel casterModel = clientModelManager.getClientModel( CastingClientModel.ID );
        final CasterScheduleDTO schedule = casterModel.getEntity( CasterScheduleDTO.class, batch.getObjid() );
        if ( schedule == null )
        {
            return;
        }

        setConcreteDurationTask.setData( schedule, duration.toMinutes(), forCasting );
        taskManager.executeTask( setConcreteDurationTask );
    }
}