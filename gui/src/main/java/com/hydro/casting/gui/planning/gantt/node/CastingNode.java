package com.hydro.casting.gui.planning.gantt.node;

import com.hydro.casting.gui.model.Batch;
import com.hydro.casting.gui.model.Transfer;
import com.hydro.casting.gui.gantt.BaseGanttChart;
import com.hydro.casting.gui.planning.gantt.common.EGanttColorScheme;
import com.hydro.casting.gui.planning.gantt.common.GanttColors;
import com.hydro.casting.gui.planning.gantt.model.CGElement;
import com.hydro.core.common.util.StringTools;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class CastingNode extends AGanttChartNode
{
    public CastingNode( BaseGanttChart chart, CGElement element )
    {
        super( chart, element, true, false );
    }

    @Override
    protected void draw()
    {
        final Batch batch;
        if ( getElement().getElement() instanceof Batch )
        {
            batch = (Batch) getElement().getElement();
        }
        else
        {
            return;
        }
        final double width = getWidth();
        final double processWidth = getProcessWidth();
        final double height = getHeight();

        final GraphicsContext gc = getGraphicsContext2D();

        SetupAfterPresentation setupAfterPresentation = SetupAfterPresentation.TO_BOTTOM;
        if ( batch.getTransfers() != null && !batch.getTransfers().isEmpty() )
        {
            final Transfer transfer = batch.getTransfers().iterator().next();
            if ( transfer != null && transfer.getFrom() != null && transfer.getFrom().getName() != null && !transfer.getFrom().getName().endsWith( "1" ) )
            {
                setupAfterPresentation = SetupAfterPresentation.TO_TOP;
            }
        }
        paintBackground( gc, BLACK_GRADIENT_PAINT, BLACK_WAIT_GRADIENT_PAINT, true, setupAfterPresentation );

        if ( chart.getColorScheme() == EGanttColorScheme.TRANSFER )
        {
            gc.setFill( ORANGE_GRADIENT_PAINT );
            gc.setStroke( ORANGE_GRADIENT_PAINT );

            if ( setupAfterPresentation == SetupAfterPresentation.TO_TOP )
            {
                gc.fillRect( 0, height / 2.0, processWidth, height / 2.0 );
                gc.strokeRect( 0, height / 2.0, processWidth, height / 2.0 );
            }
            else
            {
                gc.fillRect( 0, 0, processWidth, height / 2.0 );
                gc.strokeRect( 0, 0, processWidth, height / 2.0 );
            }
        }

        if ( processWidth > SMALL_TEXT_MIN_WIDTH )
        {
            gc.setFill( Color.WHITE );
            gc.setFont( getLargeFont() );
            gc.setTextAlign( TextAlignment.CENTER );
            gc.setTextBaseline( VPos.CENTER );

            final double centerFirstLine;
            final double centerSecondLine;
            if ( setupAfterPresentation == SetupAfterPresentation.TO_TOP )
            {
                centerFirstLine = height * 0.75;
                centerSecondLine = height * 0.25;
            }
            else
            {
                centerFirstLine = height * 0.25;
                centerSecondLine = height * 0.75;
            }
            gc.fillText( batch.getName(), processWidth / 2.0, centerFirstLine );

            if ( width > LARGE_TEXT_MIN_WIDTH )
            {
                gc.setFont( getSmallFont() );
                gc.setTextAlign( TextAlignment.LEFT );
                gc.fillText( batch.getAlloy(), processWidth + 2, centerFirstLine );

                // Giesslänge
                gc.fillText( StringTools.N02F.format( batch.getCastingLength() / 1000 ) + "m", 2, centerSecondLine );
                // Alle Positionen mit Abmessungen
                if ( batch.getPosWidths() != null )
                {
                    gc.setStroke( GanttColors.LIGHT_BLACK );
                    gc.strokeRect( 35, centerSecondLine - ( height * 0.2 ), 30, height * 0.4 );
                    gc.strokeRect( 35 + 30, centerSecondLine - ( height * 0.2 ), 30, height * 0.4 );
                    gc.strokeRect( 35 + 60, centerSecondLine - ( height * 0.2 ), 30, height * 0.4 );
                    gc.strokeRect( 35 + 90, centerSecondLine - ( height * 0.2 ), 30, height * 0.4 );
                    if ( batch.getPosWidths().length > 4 )
                    {
                        gc.strokeRect( 35 + 120, centerSecondLine - ( height * 0.2 ), 30, height * 0.4 );
                    }
                    gc.setTextAlign( TextAlignment.CENTER );
                    if ( batch.getPosWidths()[0] != null )
                    {
                        gc.fillText( StringTools.N1F.format( batch.getPosWidths()[0] ), 35 + 15, centerSecondLine );
                    }
                    if ( batch.getPosWidths()[1] != null )
                    {
                        gc.fillText( StringTools.N1F.format( batch.getPosWidths()[1] ), 35 + 30 + 15, centerSecondLine );
                    }
                    if ( batch.getPosWidths()[2] != null )
                    {
                        gc.fillText( StringTools.N1F.format( batch.getPosWidths()[2] ), 35 + 60 + 15, centerSecondLine );
                    }
                    if ( batch.getPosWidths()[3] != null )
                    {
                        gc.fillText( StringTools.N1F.format( batch.getPosWidths()[3] ), 35 + 90 + 15, centerSecondLine );
                    }
                    if ( batch.getPosWidths().length > 4 && batch.getPosWidths()[4] != null )
                    {
                        gc.fillText( StringTools.N1F.format( batch.getPosWidths()[4] ), 35 + 120 + 15, centerSecondLine );
                    }
                }
            }
        }
    }
}
