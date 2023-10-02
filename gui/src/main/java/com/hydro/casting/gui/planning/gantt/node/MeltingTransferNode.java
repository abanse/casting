package com.hydro.casting.gui.planning.gantt.node;

import com.hydro.casting.gui.model.Transfer;
import com.hydro.casting.gui.gantt.BaseGanttChart;
import com.hydro.casting.gui.planning.gantt.model.CGElement;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class MeltingTransferNode extends AGanttChartNode
{
    public MeltingTransferNode( BaseGanttChart chart, CGElement element )
    {
        super( chart, element, true, false );
    }

    @Override
    protected void draw()
    {
        //	if (getUserData() != null &&
        //		getUserData() instanceof String &&
        //		((String)getUserData()).equals("TEST"))
        //	{
        //	    System.out.println("tttt");
        //	}
        final Transfer transfer;
        if ( getElement().getElement() instanceof Transfer )
        {
            transfer = (Transfer) getElement().getElement();
        }
        else
        {
            return;
        }
        if ( transfer.getBatch() == null )
        {
            return;
        }
        final double processWidth = getProcessWidth();
        final double height = getHeight();

        final GraphicsContext gc = getGraphicsContext2D();

        if ( transfer.getFrom() != null && transfer.getFrom().getName() != null && transfer.getFrom().getName().endsWith( "1" ) )
        {
            // unterer Balken Farbverlauf
            paintBackground( gc, BLUE_GRADIENT_PAINT, BLUE_WAIT_GRADIENT_PAINT, true, SetupAfterPresentation.TO_BOTTOM );
        }
        else
        {
            // oberer Balken Farbverlauf
            paintBackground( gc, GREEN_GRADIENT_PAINT, GREEN_WAIT_GRADIENT_PAINT, true, SetupAfterPresentation.TO_TOP );
        }

        if ( processWidth > SMALL_TEXT_MIN_WIDTH )
        {
            gc.setFill( Color.WHITE );
            gc.setFont( getLargeFont() );
            gc.setTextAlign( TextAlignment.CENTER );
            gc.setTextBaseline( VPos.CENTER );
            gc.fillText( getElement().getName(), processWidth / 2.0, height * 0.35 );

            gc.setFont( getSmallFont() );
            gc.fillText( transfer.getBatch().getName() + " " + transfer.getName(), processWidth / 2.0, height * 0.75 );
        }
    }
}
