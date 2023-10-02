package com.hydro.casting.gui.planning.gantt.node;

import com.hydro.casting.gui.model.Transfer;
import com.hydro.casting.gui.gantt.BaseGanttChart;
import com.hydro.casting.gui.planning.gantt.model.CGElement;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class TransferNode extends AGanttChartNode
{
    public TransferNode( BaseGanttChart chart, CGElement element )
    {
        super( chart, element, true, false );
    }

    @Override
    protected void draw()
    {
        final Transfer transfer;
        if ( getElement().getElement() instanceof Transfer )
        {
            transfer = (Transfer) getElement().getElement();
        }
        else
        {
            return;
        }
        final double processWidth = getProcessWidth();
        final double height = getHeight() * 0.5;
        double yOffset = 0;

        final GraphicsContext gc = getGraphicsContext2D();
        if ( transfer.getFrom() != null && transfer.getFrom().getName() != null && transfer.getFrom().getName().endsWith( "1" ) )
        {
            // unterer Balken Farbverlauf
            yOffset = height;
            paintBackground( gc, yOffset, height, BLUE_GRADIENT_PAINT, BLUE_WAIT_GRADIENT_PAINT, true, SetupAfterPresentation.TO_TOP );
        }
        else
        {
            // oberer Balken Farbverlauf
            paintBackground( gc, yOffset, height, GREEN_GRADIENT_PAINT, GREEN_WAIT_GRADIENT_PAINT, true, SetupAfterPresentation.TO_BOTTOM );
        }

        if ( processWidth > SMALL_TEXT_MIN_WIDTH )
        {
            gc.setFont( getSmallFont() );
            gc.setFill( Color.WHITE );
            gc.setTextAlign( TextAlignment.CENTER );
            gc.setTextBaseline( VPos.CENTER );
            gc.fillText( transfer.getName(), processWidth / 2.0, yOffset + height * 0.5 );
        }
    }
}
