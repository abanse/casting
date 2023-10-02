package com.hydro.casting.gui.planning.gantt.node;

import com.hydro.casting.common.Casting;
import com.hydro.casting.gui.ImagesCasting;
import com.hydro.casting.gui.model.Batch;
import com.hydro.casting.gui.model.ModelElement;
import com.hydro.casting.gui.model.Transfer;
import com.hydro.casting.gui.gantt.BaseGanttChart;
import com.hydro.casting.gui.planning.gantt.FurnaceGanttChart;
import com.hydro.casting.gui.planning.gantt.common.EGanttColorScheme;
import com.hydro.casting.gui.planning.gantt.common.GanttColors;
import com.hydro.casting.gui.planning.gantt.model.CGElement;
import com.hydro.casting.gui.planning.gantt.model.CGGroup;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AGanttChartNode extends Canvas
{
    public enum SetupAfterPresentation
    {TO_TOP, TO_BOTTOM, CENTERED}

    public final static Duration DEFAULT_SO_TRANSFER_DURATION = Duration.of( 20, ChronoUnit.MINUTES );

    protected static double SMALL_TEXT_MIN_WIDTH = 30.0;
    protected static double LARGE_TEXT_MIN_WIDTH = 200.0;

    protected static Paint BLACK_GRADIENT_PAINT = new LinearGradient( 0, 0, 0, 1, true, CycleMethod.NO_CYCLE, GanttColors.BLACK_GRADIENT_STOPS );
    protected static Paint BLACK_WAIT_GRADIENT_PAINT = new LinearGradient( 0, 0, 0, 1, true, CycleMethod.NO_CYCLE, GanttColors.BLACK_TRANSPARENT_GRADIENT_STOPS );
    protected static Paint GREEN_GRADIENT_PAINT = new LinearGradient( 0, 0, 0, 1, true, CycleMethod.NO_CYCLE, GanttColors.GREEN_GRADIENT_STOPS );
    protected static Paint GREEN_WAIT_GRADIENT_PAINT = new LinearGradient( 0, 0, 0, 1, true, CycleMethod.NO_CYCLE, GanttColors.GREEN_TRANSPARENT_GRADIENT_STOPS );
    protected static Paint BLUE_GRADIENT_PAINT = new LinearGradient( 0, 0, 0, 1, true, CycleMethod.NO_CYCLE, GanttColors.BLUE_GRADIENT_STOPS );
    protected static Paint BLUE_WAIT_GRADIENT_PAINT = new LinearGradient( 0, 0, 0, 1, true, CycleMethod.NO_CYCLE, GanttColors.BLUE_TRANSPARENT_GRADIENT_STOPS );
    protected static Paint ORANGE_GRADIENT_PAINT = new LinearGradient( 0, 0, 0, 1, true, CycleMethod.NO_CYCLE, GanttColors.ORANGE_GRADIENT_STOPS );
    protected static Paint ORANGE_WAIT_GRADIENT_PAINT = new LinearGradient( 0, 0, 0, 1, true, CycleMethod.NO_CYCLE, GanttColors.ORANGE_TRANSPARENT_GRADIENT_STOPS );
    protected static Paint ORANGE_TOP_GRADIENT_PAINT = new LinearGradient( 0, 0, 0, 2, true, CycleMethod.NO_CYCLE, GanttColors.ORANGE_GRADIENT_STOPS );
    protected static Paint ORANGE_TOP_WAIT_GRADIENT_PAINT = new LinearGradient( 0, 0, 0, 2, true, CycleMethod.NO_CYCLE, GanttColors.ORANGE_TRANSPARENT_GRADIENT_STOPS );
    protected static Paint ORANGE_BOTTOM_GRADIENT_PAINT = new LinearGradient( 0, -1, 0, 1, true, CycleMethod.NO_CYCLE, GanttColors.ORANGE_GRADIENT_STOPS );
    protected static Paint ORANGE_BOTTOM_WAIT_GRADIENT_PAINT = new LinearGradient( 0, -1, 0, 1, true, CycleMethod.NO_CYCLE, GanttColors.ORANGE_TRANSPARENT_GRADIENT_STOPS );
    protected static Paint COPPER_GRADIENT_PAINT = new LinearGradient( 0, 0, 0, 1, true, CycleMethod.NO_CYCLE, GanttColors.COPPER_GRADIENT_STOPS );
    protected static Paint COPPER_WAIT_GRADIENT_PAINT = new LinearGradient( 0, 0, 0, 1, true, CycleMethod.NO_CYCLE, GanttColors.COPPER_TRANSPARENT_GRADIENT_STOPS );
    protected static Paint RED_PAINT = new Color( 0.8039216f, 0.36078432f, 0.36078432f, 0.4 );

    protected static Image LOCKED_IMAGE = ImagesCasting.EDIT_LOCKED.load( 12, 12, true, true );
    private final boolean dropAllowed;
    protected Paint GA50_EVEN = Color.web( "025D8C" );
    protected Paint GA50_ODD = Color.web( "1693A5" );
    protected Paint GA60_EVEN = Color.web( "588A35" );
    protected Paint GA60_ODD = Color.web( "EDAC03" );
    protected Paint GA70_EVEN = Color.web( "F07818" );
    protected Paint GA70_ODD = Color.web( "CC3917" );
    protected Paint GA80_EVEN = Color.web( "855B1F" );
    protected Paint GA80_ODD = Color.web( "53515B" );
    protected Paint ALLOY_SOURCE = Color.SLATEGRAY;
    public final static Color ALLOY_SOURCE_S1 = Color.rgb( 109, 116, 114 );
    public final static Color ALLOY_SOURCE_S1_TRANSPARENT = Color.rgb( 109, 116, 114, 0.25 );
    public final static Color ALLOY_SOURCE_S2 = Color.rgb( 151, 10, 16 );
    public final static Color ALLOY_SOURCE_S2_TRANSPARENT = Color.rgb( 151, 10, 16, 0.25 );
    public final static Color ALLOY_SOURCE_S3 = Color.rgb( 102, 117, 141 );
    public final static Color ALLOY_SOURCE_S3_TRANSPARENT = Color.rgb( 102, 117, 141, 0.25 );
    public final static Color ALLOY_SOURCE_RA = Color.rgb( 206, 133, 40 );
    public final static Color ALLOY_SOURCE_RA_TRANSPARENT = Color.rgb( 206, 133, 40, 0.25 );
    public final static Color ALLOY_SOURCE_EL = Color.rgb( 47, 53, 65 );
    public final static Color ALLOY_SOURCE_EL_TRANSPARENT = Color.rgb( 47, 53, 65, 0.25 );
    protected BaseGanttChart chart;
    protected boolean manipulationActive;
    private CGElement element;
    private AGanttChartNode groupParent;

    private Duration oldDuration = null;

    //private FurnaceBatchWindowCreator furnaceBatchWindowCreator;

    public AGanttChartNode( BaseGanttChart chart, CGElement element, boolean durationChangable, boolean dropAllowed )
    {
        this.chart = chart;
        this.element = element;
        this.dropAllowed = dropAllowed;
        element.setNode( this );
        // Redraw canvas when size changes.
        widthProperty().addListener( evt -> revalidate() );
        heightProperty().addListener( evt -> revalidate() );

        setOnMouseClicked( new EventHandler<MouseEvent>()
        {
            @Override
            public void handle( MouseEvent event )
            {
                onMouseClicked( event );
            }
        } );

        setOnMouseEntered( event -> chart.setFocusedElement( element ) );
        //setOnMouseExited( event -> chart.setFocusedElement( null ) );

        if ( durationChangable )
        {
            setOnMouseMoved( event -> {
                if ( element instanceof CGGroup )
                {
                    return;
                }
                double durationPoint = getDisplayLength( element.getCompleteDuration() );
                if ( element.getWaitDuration().isZero() == false )
                {
                    durationPoint = durationPoint - getWaitWidth();
                }

                if ( event.getX() > ( durationPoint - 10.0 ) )
                {
                    setCursor( Cursor.H_RESIZE );
                    manipulationActive = true;
                    oldDuration = element.getDuration();
                }
                else
                {
                    setCursor( null );
                }
            } );
            setOnMouseExited( mouseExited -> {
                if ( mouseExited.isPrimaryButtonDown() )
                {
                    return;
                }
                setCursor( null );
                manipulationActive = false;
                oldDuration = null;
            } );
            setOnMouseDragged( mouseDragged -> {
                if ( manipulationActive )
                {
                    double durationPoint = getDisplayLength( element.getCompleteDuration() );
                    if ( element.getWaitDuration().isZero() == false )
                    {
                        durationPoint = durationPoint - getWaitWidth();
                    }
                    double newDuration = (double) element.getDuration().toMillis() / durationPoint * mouseDragged.getX();
                    if ( newDuration < 5 )
                    {
                        newDuration = 5;
                    }
                    element.setDuration( Duration.ofMillis( Math.round( newDuration ) ) );
                    chart.solve();
                    revalidate();
                    if ( groupParent != null )
                    {
                        groupParent.revalidate();
                    }
                }
            } );
            setOnMouseReleased( mouseReleased -> {
                setCursor( null );
                System.out.println("#### duration changed " + oldDuration + " to " + element.getDuration());
                if (!Objects.equals(oldDuration, element.getDuration()))
                {
                    chart.setConcreteDuration( element, element.getDuration() );
                }
                manipulationActive = false;
            } );
        }

        if ( dropAllowed )
        {
            setOnDragOver( dragEvent -> {
                final String dragboardString = dragEvent.getDragboard().getString();
                if ( !dragboardString.startsWith( "CR" ) )
                {
                    return;
                }
                if ( element == null || element instanceof CGGroup )
                {
                    return;
                }
                // check target furnace
                // if (dragboardString.startsWith("MN"))
                // {
                // final Transfer sourceTransfer =
                // MeltingPlan.getInstance().findTransfer(Long.parseLong(dragboardString.substring(2)));
                // final Batch batch;
                // if (getElement().getElement() instanceof Batch)
                // {
                // batch = (Batch) getElement().getElement();
                // }
                // else if (getElement().getElement() instanceof Transfer)
                // {
                // batch = ((Transfer) getElement().getElement()).getBatch();
                // }
                // else
                // {
                // batch = null;
                // }
                // if (batch != null && sourceTransfer != null)
                // {
                // if (batch.getCaster().getObjid() ==
                // sourceTransfer.getBatch().getCaster().getObjid())
                // {
                // return;
                // }
                // }
                // }
                // TODO Editierbar
			/*
		final Batch batch = getBatch();
		if (batch == null || batch.isEditable() == false)
		{
		    return;
		}
			 */

                setEffect( new GaussianBlur() );
                dragEvent.acceptTransferModes( TransferMode.MOVE );
            } );

            setOnDragDropped( dragEvent -> {
                String dragboardString = dragEvent.getDragboard().getString();

                if ( chart instanceof FurnaceGanttChart )
                {
                    setEffect( null );
                    ( (FurnaceGanttChart) chart ).createContinuousTransferTo( element, dragboardString );
                    dragEvent.consume();
                }

                /*
                if ( dragboardString.equals( Casting.MACHINE.MELTING_FURNACE_S1 ) )
                {
                }
                if ( dragboardString.startsWith( "MN" ) )
                {
                    setEffect( null );
                    final Transfer sourceTransfer = MeltingPlan.getInstance().findTransfer( Long.parseLong( dragboardString.substring( 2 ) ) );
                    chart.createFurnaceTransferTo( sourceTransfer, 1000, element.getElement() );
                    dragEvent.consume();
                }

                 */
            } );

            setOnDragExited( dragEvent -> {
                setEffect( null );
            } );
        }
    }

    protected double getDisplayLength( Duration duration )
    {
        return chart.getXAxis().getDisplayLength( duration.toMillis() );
    }

    protected double getDisplayPosition( LocalDateTime dateTime )
    {
        return chart.getXAxis().getDisplayPosition( dateTime );
    }

    protected double getProcessWidth()
    {
        final double setupAfterWidth = getDisplayLength( element.getSetupAfter() );
        return getWidth() - setupAfterWidth;
    }

    protected double getWaitWidth()
    {
        return getDisplayLength( element.getWaitDuration() );
    }

    @Override
    public boolean isResizable()
    {
        return true;
    }

    @Override
    public double prefWidth( double height )
    {
        return getWidth();
    }

    @Override
    public double prefHeight( double width )
    {
        return getHeight();
    }

    public CGElement getElement()
    {
        return element;
    }

    public void setElement( CGElement element )
    {
        this.element = element;
    }

    public AGanttChartNode getGroupParent()
    {
        return groupParent;
    }

    public void setGroupParent( AGanttChartNode groupParent )
    {
        this.groupParent = groupParent;
    }

    public void revalidate()
    {
        draw();
    }

    protected abstract void draw();

    protected void paintBackground( GraphicsContext gc, Paint fill, Paint waitFill, boolean supportBatchColorScheme, SetupAfterPresentation setupAfterPresentation )
    {
        paintBackground( gc, 0, getHeight(), fill, waitFill, supportBatchColorScheme, setupAfterPresentation );
    }

    protected void paintBackground( GraphicsContext gc, double y, double height, Paint fill, Paint waitFill, boolean supportBatchColorScheme, SetupAfterPresentation setupAfterPresentation )
    {
        boolean editable = false;
        Batch batch = null;
        if ( element != null && element.getElement() instanceof Batch )
        {
            batch = (Batch) element.getElement();
        }
        else if ( element != null && element.getElement() instanceof Transfer )
        {
            batch = ( (Transfer) element.getElement() ).getBatch();
        }

        if ( batch != null )
        {
            editable = batch.isEditable();
        }

        if ( supportBatchColorScheme && chart.getColorScheme() == EGanttColorScheme.BATCH )
        {
            if ( batch != null )
            {
                editable = batch.isEditable();
                if ( Casting.MACHINE.CASTER_50.equals( batch.getCaster().getName() ) )
                {
                    if ( batch.getIndex() % 2 == 0 )
                    {
                        fill = GA50_EVEN;
                    }
                    else
                    {
                        fill = GA50_ODD;
                    }
                }
                else if ( Casting.MACHINE.CASTER_60.equals( batch.getCaster().getName() ) )
                {
                    if ( batch.getIndex() % 2 == 0 )
                    {
                        fill = GA60_EVEN;
                    }
                    else
                    {
                        fill = GA60_ODD;
                    }
                }
                else if ( Casting.MACHINE.CASTER_70.equals( batch.getCaster().getName() ) )
                {
                    if ( batch.getIndex() % 2 == 0 )
                    {
                        fill = GA70_EVEN;
                    }
                    else
                    {
                        fill = GA70_ODD;
                    }
                }
                else
                {
                    if ( batch.getIndex() % 2 == 0 )
                    {
                        fill = GA80_EVEN;
                    }
                    else
                    {
                        fill = GA80_ODD;
                    }
                }
                Color fillColor = (Color) fill;
                waitFill = new Color( fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), 0.5 );
            }
        }

        final double width = getDisplayLength( element.getCompleteDuration() );
        final double processWidth = getProcessWidth();
        gc.clearRect( 0, 0, width, getHeight() );

        gc.setFill( fill );
        //gc.setStroke( null );
        gc.setLineWidth( 0.0 );

        if ( element.getSetupAfter().isZero() )
        {
            if ( element.getWaitDuration().isZero() )
            {
                gc.fillRect( 0, y, width, height );
                //gc.strokeRect( 0, y, width, height );
            }
            else
            {
                final double waitLength = getWaitWidth();
                gc.fillRect( 0, y, width - waitLength, height );
                gc.strokeRect( 0, y, width - waitLength, height );
                gc.setFill( waitFill );
                //gc.setStroke( waitFill );
                gc.fillRect( width - waitLength, y, waitLength, height );
                //gc.strokeRect( width - waitLength, y, waitLength, height );
            }
        }
        else
        {
            if ( setupAfterPresentation == SetupAfterPresentation.TO_TOP )
            {
                // Zeichnen des Pfeiles nach unten
                final double[] xPoints = new double[] { processWidth, width, processWidth };
                final double[] yPoints = new double[] { y, y, y + height };
                gc.fillPolygon( xPoints, yPoints, 3 );
                //gc.strokePolygon( xPoints, yPoints, 3 );
            }
            else if ( setupAfterPresentation == SetupAfterPresentation.CENTERED )
            {
                // Zeichnen des Pfeiles nach unten
                final double[] xPoints = new double[] { processWidth, width, processWidth };
                final double[] yPoints = new double[] { y, y + height, y + height };
                gc.fillPolygon( xPoints, yPoints, 3 );
                //gc.strokePolygon( xPoints, yPoints, 3 );
            }
            else
            {
                // Zeichnen des Pfeiles nach unten
                final double[] xPoints = new double[] { processWidth, width, processWidth };
                final double[] yPoints = new double[] { y, y + height, y + height };
                gc.fillPolygon( xPoints, yPoints, 3 );
                //gc.strokePolygon( xPoints, yPoints, 3 );
            }

            final double waitLength = getWaitWidth();
            if ( element.getWaitDuration().isZero() || ( width - waitLength ) > processWidth )
            {
                gc.fillRect( 0, y, processWidth, height );
                //gc.rect( 0, y, processWidth, height );
            }
            else
            {
                gc.fillRect( 0, y, width - waitLength, height );
                gc.rect( 0, y, width - waitLength, height );
                gc.setFill( waitFill );
                //gc.setStroke( waitFill );
                gc.fillRect( width - waitLength, y, waitLength - ( width - processWidth ), height );
                //gc.strokeRect( width - waitLength, y, waitLength - ( width - processWidth ), height );
            }
        }

        if ( editable && dropAllowed && ( getElement() instanceof CGGroup ) == false )
        {
            gc.drawImage( LOCKED_IMAGE, 5, y + height - 17 );
        }

        if ( element.hasInnerElements() )
        {
            // finde Ende von den Inner Elements
            // zeichne kleinen Füllmarke
            final double thisPosition = getDisplayPosition( element.getStart() );
            final double defaultWidth = getDisplayLength( DEFAULT_SO_TRANSFER_DURATION );
            final double innerHeight;
            if ( getElement().getGroup() != null )
            {
                innerHeight = height;
            }
            else
            {
                innerHeight = height * 0.5;
            }
            final List<BoundingBox> usedPositions = new ArrayList<>();
            for ( CGElement innerElement : element.getInnerElements() )
            {
                final LocalDateTime end = innerElement.getEnd();
                final double innerElementPosition = getDisplayPosition( end );

                final double center = innerElementPosition - thisPosition;
                final BoundingBox currentPosition = new BoundingBox( center - ( defaultWidth * 0.1 ), y, defaultWidth * 0.2, innerHeight * 0.5 );

                double yOffset = 0;
                for ( BoundingBox usedPosition : usedPositions )
                {
                    if ( currentPosition.intersects( usedPosition ) )
                    {
                        yOffset = yOffset + 2;
                    }
                }

                final double[] xPoints = new double[] { center - ( defaultWidth * 0.2 ), center + ( defaultWidth * 0.2 ), center };
                final double[] yPoints = new double[] { y - yOffset, y - yOffset, y + ( innerHeight * 0.7 ) - yOffset };
                usedPositions.add( currentPosition );

                if ( chart.getColorScheme() == EGanttColorScheme.BATCH )
                {
                    if ( innerElement.getResource() != null && Casting.isAlloySource( innerElement.getResource().getName() ) )
                    {
                        gc.setFill( ALLOY_SOURCE );
                    }
                    else
                    {
                        Batch innerBatch = null;
                        if ( innerElement != null && innerElement.getElement() instanceof Batch )
                        {
                            innerBatch = (Batch) innerElement.getElement();
                        }
                        else if ( innerElement != null && innerElement.getElement() instanceof Transfer )
                        {
                            innerBatch = ( (Transfer) innerElement.getElement() ).getBatch();
                        }
                        if ( innerBatch != null )
                        {
                            if ( Casting.MACHINE.CASTER_50.equals( innerBatch.getCaster().getName() ) )
                            {
                                if ( innerBatch.getIndex() % 2 == 0 )
                                {
                                    fill = GA50_EVEN;
                                }
                                else
                                {
                                    fill = GA50_ODD;
                                }
                            }
                            else if ( Casting.MACHINE.CASTER_60.equals( innerBatch.getCaster().getName() ) )
                            {
                                if ( innerBatch.getIndex() % 2 == 0 )
                                {
                                    fill = GA60_EVEN;
                                }
                                else
                                {
                                    fill = GA60_ODD;
                                }
                            }
                            else if ( Casting.MACHINE.CASTER_70.equals( innerBatch.getCaster().getName() ) )
                            {
                                if ( innerBatch.getIndex() % 2 == 0 )
                                {
                                    fill = GA70_EVEN;
                                }
                                else
                                {
                                    fill = GA70_ODD;
                                }
                            }
                            else
                            {
                                if ( innerBatch.getIndex() % 2 == 0 )
                                {
                                    fill = GA80_EVEN;
                                }
                                else
                                {
                                    fill = GA80_ODD;
                                }
                            }
                            gc.setFill( fill );
                        }
                    }
                    // gc.fillRect(innerElementPosition - thisPosition -
                    // (defaultWidth / 2.0), 0, defaultWidth, innerHeight);
                    gc.fillPolygon( xPoints, yPoints, xPoints.length );
                }
                else
                {
                    if ( innerElement.getResource() != null && Casting.isAlloySource( innerElement.getResource().getName() ) )
                    {
                        final String resourceName = innerElement.getResource().getName();
                        if ( resourceName.equals( Casting.ALLOY_SOURCES.MELTING_FURNACE_S1 ) )
                        {
                            gc.setFill( ALLOY_SOURCE_S1 );
                        }
                        else if ( resourceName.equals( Casting.ALLOY_SOURCES.MELTING_FURNACE_S2 ) )
                        {
                            gc.setFill( ALLOY_SOURCE_S2 );
                        }
                        else if ( resourceName.equals( Casting.ALLOY_SOURCES.UBC_S3 ) )
                        {
                            gc.setFill( ALLOY_SOURCE_S3 );
                        }
                        else if ( resourceName.equals( Casting.ALLOY_SOURCES.REAL_ALLOY ) )
                        {
                            gc.setFill( ALLOY_SOURCE_RA );
                        }
                        else if ( resourceName.equals( Casting.ALLOY_SOURCES.ELEKTROLYSE ) )
                        {
                            gc.setFill( ALLOY_SOURCE_EL );
                        }
                        else
                        {
                            gc.setFill( COPPER_GRADIENT_PAINT );
                        }
                        gc.fillPolygon( xPoints, yPoints, xPoints.length );
                    }
                    else
                    {
                        gc.setFill( Color.WHITE );
                        gc.fillPolygon( xPoints, yPoints, xPoints.length );
                    }
                }

            }
        }
    }

    protected Batch getBatch()
    {
        if ( getElement() != null && getElement().getElement() != null )
        {
            ModelElement sourceME = getElement().getElement();
            if ( sourceME instanceof Batch )
            {
                return (Batch) sourceME;
            }
            else if ( sourceME instanceof Transfer )
            {
                return ( (Transfer) sourceME ).getBatch();
            }
        }
        return null;
    }

    protected void onMouseClicked( MouseEvent event )
    {
        if ( event.getClickCount() == 2 )
        {
            Batch batch = getBatch();
            if ( batch == null )
            {
                return;
            }

            // TODO Öffnen Detail-Fenster
		/*
	    if (furnaceBatchWindowCreator == null)
	    {
		furnaceBatchWindowCreator = new FurnaceBatchWindowCreator(batch);
	    }
	    WindowManager.getInstance().openView(furnaceBatchWindowCreator, WindowManager.TYPE_BATCH, null);
		 */
        }
    }

    protected Font getLargeFont()
    {
        // 900 20
        if ( chart.getHeight() > 900 )
        {
            return Font.font( 20 );
        }
        // 500 14
        if ( chart.getHeight() > 500 )
        {
            return Font.font( 14 );
        }
        return Font.font( 10 );
    }

    protected Font getSmallFont()
    {
        // 900 14
        if ( chart.getHeight() > 900 )
        {
            return Font.font( 12 );
        }
        // 500 10
        if ( chart.getHeight() > 500 )
        {
            return Font.font( 10 );
        }
        return Font.font( 6 );
    }

    protected Font getSmallestFont()
    {
        // 900 14
        if ( chart.getHeight() > 900 )
        {
            return Font.font( 12 );
        }
        // 500 10
        if ( chart.getHeight() > 500 )
        {
            return Font.font( 8 );
        }
        return Font.font( 4 );
    }
}
