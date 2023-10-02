package com.hydro.casting.gui.planning.gantt.node;

import com.hydro.casting.gui.model.ResourceCalendarEntry;
import com.hydro.casting.gui.gantt.BaseGanttChart;
import com.hydro.casting.gui.planning.gantt.model.CGElement;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class ResourceCalendarNode extends AGanttChartNode
{
    public ResourceCalendarNode( BaseGanttChart chart, CGElement element )
    {
        super( chart, element, false, false );
    }

    @Override
    protected void draw()
    {
        final ResourceCalendarEntry resourceCalendarEntry;
        if ( getElement().getElement() instanceof ResourceCalendarEntry )
        {
            resourceCalendarEntry = (ResourceCalendarEntry) getElement().getElement();
        }
        else
        {
            return;
        }
        final double processWidth = getProcessWidth();
        final double height = getHeight();
        double yOffset = 0;

        final GraphicsContext gc = getGraphicsContext2D();
        paintBackground( gc, yOffset, height, RED_PAINT, RED_PAINT, false, SetupAfterPresentation.TO_BOTTOM );

        if ( processWidth > SMALL_TEXT_MIN_WIDTH )
        {
            gc.setFont( getSmallFont() );
            gc.setFill( Color.GRAY );
            gc.setTextAlign( TextAlignment.CENTER );
            gc.setTextBaseline( VPos.CENTER );
            gc.fillText( resourceCalendarEntry.getDescription(), processWidth / 2.0, yOffset + height * 0.5 );
        }
    }
}
