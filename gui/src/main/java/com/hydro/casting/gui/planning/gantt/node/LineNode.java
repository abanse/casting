package com.hydro.casting.gui.planning.gantt.node;

import com.hydro.casting.gui.gantt.BaseGanttChart;
import com.hydro.casting.gui.planning.gantt.model.CGElement;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class LineNode extends AGanttChartNode
{
    public LineNode( BaseGanttChart chart, CGElement element )
    {
        super( chart, element, false, false );
    }

    @Override
    protected void draw()
    {
        final GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect( 0, 0, getWidth(), getHeight() );
        gc.setFill( Color.web( "020009" ) );
        gc.fillRect( 0, ( getHeight() * 0.5 ) - 1.0, getWidth(), 2.0 );
    }
}
