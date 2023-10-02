package com.hydro.casting.gui.planning.gantt.node;

import com.hydro.casting.gui.model.Batch;
import com.hydro.casting.gui.model.Transfer;
import com.hydro.casting.gui.gantt.BaseGanttChart;
import com.hydro.casting.gui.planning.gantt.model.CGElement;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class MeltingNode extends AGanttChartNode
{
    public MeltingNode( BaseGanttChart chart, CGElement element )
    {
        super( chart, element, true, true );

        setOnDragDetected( event -> {
            if ( manipulationActive )
            {
                return;
            }
            final Batch batch = getBatch();
            if ( batch == null || batch.isEditable() == false )
            {
                return;
            }

            ClipboardContent content = new ClipboardContent();

            content.putString( "MN" + element.getElement().getObjid() );

            Dragboard dragBoard = startDragAndDrop( TransferMode.MOVE );
            // dragBoard.setDragView(TransferBrowser.DND_TRANSFER_BOOKMARK_IMAGE);
            dragBoard.setContent( content );

            event.consume();
        } );
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
        // Phasen anzeigen

        /*
        // 1.Phase Chargieren 0.4
        paintPhase(gc, 0.5, "Chargieren", true);
        // 2.Phase Legieren 0.20
        paintPhase(gc, 0.7, "Legieren", true);
        // 3.Phase Abkrätzen 0.20
        paintPhase(gc, 0.9, "Abkrätzen", true);
        // 4.Phase Abstehen 0.20
        paintPhase(gc, 1.0, "Abstehen", false);
         */

        if ( processWidth > SMALL_TEXT_MIN_WIDTH )
        {
            gc.setFill( Color.WHITE );
            gc.setFont( getLargeFont() );
            gc.setTextAlign( TextAlignment.CENTER );
            gc.setTextBaseline( VPos.CENTER );
            gc.fillText( transfer.getName(), processWidth / 2.0, height * 0.25 );

            if ( processWidth > LARGE_TEXT_MIN_WIDTH )
            {
                gc.setFont( getSmallFont() );
                gc.fillText( transfer.getBatch().getDetail(), processWidth / 2.0, height * 0.75 );
            }
        }
    }

    private void paintPhase(GraphicsContext gc, double relPos, String name, boolean paintArrow )
    {
        final double processWidth = getProcessWidth();
        final double height = getHeight();
        if (paintArrow)
        {
            gc.setStroke( Color.color( 1, 1, 1, 0.3 ) );
            gc.strokeLine( ( processWidth * relPos ) - ( height / 3 ), 0, ( processWidth * relPos ) + ( height / 3 ), height / 2.0 );
            gc.strokeLine( ( processWidth * relPos ) + ( height / 3 ), height / 2.0, ( processWidth * relPos ) - ( height / 3 ), height );
        }
        gc.setFill(Color.color( 1,1,1,0.7 ));
        gc.setFont( getSmallestFont() );
        gc.setTextAlign( TextAlignment.RIGHT );
        if (paintArrow)
        {
            gc.fillText( name, ( processWidth * relPos ) - ( height / 3 ), height * 0.5 );
        }
        else
        {
            gc.fillText( name, ( processWidth * relPos ), height * 0.5 );
        }
    }

    // protected void paintBackground(GraphicsContext gc, Paint fill, Paint
    // waitFill, Transfer transfer)
    // {
    // super.paintBackground(gc, fill, waitFill);
    //
    // if (transfer == null || transfer.getBatch() == null)
    // {
    // return;
    // }
    // if (transfer.getBatch().getIndex() % 2 == 0)
    // {
    // gc.setFill(BROWN_GRADIENT_PAINT);
    // gc.setStroke(BROWN_GRADIENT_PAINT);
    // }
    // else
    // {
    // gc.setFill(BLACK_GRADIENT_PAINT);
    // gc.setStroke(BLACK_GRADIENT_PAINT);
    // }
    //
    // final double width =
    // getDisplayLength(getElement().getCompleteDuration());
    // final double height = getHeight();
    //
    // gc.fillRect(0, height * 0.9, width, height * 0.1);
    // gc.strokeRect(0, height * 0.9, width, height * 0.1);
    // }
}
