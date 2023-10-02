package com.hydro.casting.gui.planning.gantt.node;

import com.hydro.casting.gui.model.Batch;
import com.hydro.casting.gui.model.Transfer;
import com.hydro.casting.gui.gantt.BaseGanttChart;
import com.hydro.casting.gui.planning.gantt.model.CGElement;
import com.hydro.casting.gui.planning.gantt.model.CGGroup;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class HoldingNode extends AGanttChartNode
{
    public HoldingNode( BaseGanttChart chart, CGElement element )
    {
        super( chart, element, true, true );
    }

    @Override
    protected void draw()
    {
        final Batch batch;
        final boolean isGroup = getElement() instanceof CGGroup;
        if ( getElement().getElement() instanceof Batch )
        {
            batch = (Batch) getElement().getElement();
        }
        else if ( getElement().getElement() instanceof Transfer )
        {
            batch = ( (Transfer) getElement().getElement() ).getBatch();
        }
        else
        {
            return;
        }
        if ( batch == null )
        {
            return;
        }
        final double processWidth = getProcessWidth();
        final double height = getHeight();

        final GraphicsContext gc = getGraphicsContext2D();
        if ( isGroup )
        {
            paintBackground( gc, ORANGE_BOTTOM_GRADIENT_PAINT, ORANGE_BOTTOM_WAIT_GRADIENT_PAINT, true, SetupAfterPresentation.TO_BOTTOM );
        }
        else
        {
            paintBackground( gc, ORANGE_TOP_GRADIENT_PAINT, ORANGE_TOP_WAIT_GRADIENT_PAINT, true, SetupAfterPresentation.TO_BOTTOM );
        }

        if ( processWidth > SMALL_TEXT_MIN_WIDTH )
        {
            gc.setFill( Color.WHITE );
            gc.setTextAlign( TextAlignment.CENTER );
            gc.setTextBaseline( VPos.CENTER );
            if ( isGroup )
            {
                if ( processWidth > LARGE_TEXT_MIN_WIDTH )
                {
                    gc.setFont( getSmallFont() );
                    gc.fillText( batch.getDetail(), processWidth / 2.0, height * 0.5 );
                }
            }
            else
            {
                gc.setFont( getLargeFont() );
                gc.fillText( batch.getName(), processWidth / 2.0, height * 0.5 );
            }
        }
    }
}
