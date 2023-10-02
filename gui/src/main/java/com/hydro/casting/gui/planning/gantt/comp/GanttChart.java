package com.hydro.casting.gui.planning.gantt.comp;

import com.hydro.casting.gui.planning.gantt.model.CGElement;
import com.hydro.casting.gui.planning.gantt.model.CGGroup;
import com.hydro.casting.gui.planning.gantt.node.AGanttChartNode;
import com.hydro.core.gui.comp.chart.DateAxis310;
import javafx.animation.FadeTransition;
import javafx.beans.NamedArg;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GanttChart extends XYChart<LocalDateTime, String>
{
    private static final Duration FADE_DURATION = Duration.millis( 250 );

    private Color markerColor;
    private CGElement markerSource;
    private CGElement markerTarget;

    private Line markerLine = new Line();
    private FadeTransition fader = new FadeTransition( FADE_DURATION, markerLine );

    public GanttChart( @NamedArg( "xAxis" ) Axis<LocalDateTime> xAxis, @NamedArg( "yAxis" ) Axis<String> yAxis )
    {
        this( xAxis, yAxis, FXCollections.observableArrayList() );
    }

    public GanttChart( @NamedArg( "xAxis" ) Axis<LocalDateTime> xAxis, @NamedArg( "yAxis" ) Axis<String> yAxis, @NamedArg( "data" ) ObservableList<Series<LocalDateTime, String>> data )
    {
        super( xAxis, yAxis );
        setData( data );

        markerLine.setStrokeLineCap( StrokeLineCap.ROUND );
        markerLine.setOpacity( 0.0 );
        getPlotChildren().add( markerLine );

        fader.setCycleCount( 1 );
    }

    public void showMarker( Color color, CGElement source, CGElement target )
    {
        this.markerColor = color;
        this.markerSource = source;
        this.markerTarget = target;
        refresh();
        setMarkerVisible( true );
    }

    public void hideMarker()
    {
        setMarkerVisible( false );
        this.markerSource = null;
        this.markerTarget = null;
        refresh();
    }

    public void refresh()
    {
        requestChartLayout();
    }

    @Override
    protected void layoutPlotChildren()
    {
        double markerStartX = -1;
        double markerStartY = -1;
        double markerEndY = -1;
        for ( int seriesIndex = 0; seriesIndex < getData().size(); seriesIndex++ )
        {
            Series<LocalDateTime, String> series = getData().get( seriesIndex );

            Iterator<Data<LocalDateTime, String>> iter = getDisplayedDataIterator( series );

            final double height = getYAxis().getHeight();
            final double singleLineHeight = height / ( (CategoryAxis) getYAxis() ).getCategories().size();
            final double blockHeight = singleLineHeight * 0.9;

            while ( iter.hasNext() )
            {
                Data<LocalDateTime, String> item = iter.next();
                if ( item.getExtraValue() instanceof CGElement == false )
                {
                    continue;
                }
                CGElement element = (CGElement) item.getExtraValue();
                double x = getXAxis().getDisplayPosition( element.getStart() );
                double y = getYAxis().getDisplayPosition( item.getYValue() );
                double length = 10.0;
                if ( getXAxis() instanceof DateAxis310 )
                {
                    length = ( (DateAxis310) getXAxis() ).getDisplayLength( element.getCompleteDuration().toMillis() );
                }
                if ( Double.isNaN( x ) || Double.isNaN( y ) )
                {
                    continue;
                }
                Node block = item.getNode();
                if ( block != null )
                {
                    double h = blockHeight;
                    if ( element instanceof CGGroup )
                    {
                        h = blockHeight * 0.33333;
                        y += blockHeight * 0.16666;
                    }
                    else
                    {
                        y -= blockHeight / 2.0;
                        if ( element.getGroup() != null )
                        {
                            h = blockHeight * 0.66666;
                        }
                    }
                    if ( block instanceof Canvas )
                    {
                        Canvas canvas = (Canvas) block;
                        canvas.setWidth( length );
                        canvas.setHeight( h );
                    }
                    block.setLayoutX( x );
                    block.setLayoutY( y );
                    if ( markerSource != null && markerSource.equals( element ) )
                    {
                        markerStartX = x + length;
                        markerStartY = y + h;
                    }
                    if ( markerTarget != null && markerTarget.equals( element ) )
                    {
                        markerEndY = y;
                    }
                    if ( block instanceof AGanttChartNode )
                    {
                        AGanttChartNode node = (AGanttChartNode) block;
                        if ( node.getElement() != null && node.getElement().getInnerElementParent() != null )
                        {
                            node.getElement().getInnerElementParent().getNode().revalidate();
                        }
                    }
                }
            }
        }

        if ( markerStartX >= 0 && markerStartY >= 0 && markerEndY >= 0 )
        {
            markerLine.setStroke( markerColor );
            markerLine.setStrokeWidth( 4.0 );
            markerLine.setStartX( markerStartX );
            markerLine.setStartY( markerStartY );
            markerLine.setEndX( markerStartX );
            markerLine.setEndY( markerEndY );
        }
    }

    @Override
    protected void dataItemAdded( Series<LocalDateTime, String> series, int itemIndex, Data<LocalDateTime, String> item )
    {
        Node block = createContainer( series, getData().indexOf( series ), item, itemIndex );
        getPlotChildren().add( block );
        if ( getPlotChildren().contains( markerLine ) )
        {
            getPlotChildren().remove( markerLine );
            getPlotChildren().add( markerLine );
        }
    }

    @Override
    protected void dataItemRemoved( final Data<LocalDateTime, String> item, final Series<LocalDateTime, String> series )
    {
        final Node block = item.getNode();
        getPlotChildren().remove( block );
        removeDataItemFromDisplay( series, item );
    }

    @Override
    protected void dataItemChanged( Data<LocalDateTime, String> item )
    {
    }

    @Override
    protected void seriesAdded( Series<LocalDateTime, String> series, int seriesIndex )
    {
        for ( int j = 0; j < series.getData().size(); j++ )
        {
            Data<LocalDateTime, String> item = series.getData().get( j );
            Node container = createContainer( series, seriesIndex, item, j );
            getPlotChildren().add( container );
            if ( getPlotChildren().contains( markerLine ) )
            {
                getPlotChildren().remove( markerLine );
                getPlotChildren().add( markerLine );
            }
        }
    }

    @Override
    protected void seriesRemoved( final Series<LocalDateTime, String> series )
    {
        for ( Data<LocalDateTime, String> d : series.getData() )
        {
            final Node container = d.getNode();
            getPlotChildren().remove( container );
        }
        removeSeriesFromDisplay( series );

    }

    private Node createContainer( Series<LocalDateTime, String> series, int seriesIndex, final Data<LocalDateTime, String> item, int itemIndex )
    {

        Node container = item.getNode();

        if ( container == null )
        {
            StackPane stackPane = new StackPane();
            container = stackPane;
            item.setNode( container );
        }

        return container;
    }

    @Override
    protected void updateAxisRange()
    {
        final Axis<LocalDateTime> xa = getXAxis();
        final Axis<String> ya = getYAxis();
        List<LocalDateTime> xData = null;
        List<String> yData = null;
        if ( xa.isAutoRanging() )
            xData = new ArrayList<LocalDateTime>();
        if ( ya.isAutoRanging() )
            yData = new ArrayList<String>();
        if ( xData != null || yData != null )
        {
            for ( Series<LocalDateTime, String> series : getData() )
            {
                for ( Data<LocalDateTime, String> data : series.getData() )
                {
                    if ( xData != null )
                    {
                        xData.add( data.getXValue() );
                    }
                    if ( yData != null )
                    {
                        yData.add( data.getYValue() );
                    }
                }
            }
            if ( xData != null )
                xa.invalidateRange( xData );
            if ( yData != null )
                ya.invalidateRange( yData );
        }
    }

    private void setMarkerVisible( boolean visible )
    {
        if ( ( markerLine.getOpacity() == 0.0 && visible ) || ( markerLine.getOpacity() == 1.0 && !visible ) )
        {
            fader.setFromValue( visible ? 0.0 : 1.0 );
            fader.setToValue( visible ? 1.0 : 0.0 );
            fader.play();
        }
    }

}
