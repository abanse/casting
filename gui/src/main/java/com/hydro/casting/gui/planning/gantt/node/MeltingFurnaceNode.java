package com.hydro.casting.gui.planning.gantt.node;

import com.hydro.casting.gui.model.Batch;
import com.hydro.casting.gui.model.Transfer;
import com.hydro.casting.gui.model.TransferMaterial;
import com.hydro.casting.gui.gantt.BaseGanttChart;
import com.hydro.casting.gui.planning.gantt.common.GanttColors;
import com.hydro.casting.gui.planning.gantt.model.CGElement;
import com.hydro.core.common.util.StringTools;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;

import java.util.List;

public class MeltingFurnaceNode extends AGanttChartNode
{
    public MeltingFurnaceNode( BaseGanttChart chart, CGElement element )
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

        final Paint background;
        final Paint waitBackground;
        final SetupAfterPresentation setupAfterPresentation;
        if ( transfer.getFrom() != null && transfer.getFrom().getName() != null && transfer.getFrom().getName().endsWith( "1" ) )
        {
            // unterer Balken Farbverlauf
            background = BLUE_GRADIENT_PAINT;
            waitBackground = BLUE_WAIT_GRADIENT_PAINT;
            setupAfterPresentation = SetupAfterPresentation.TO_BOTTOM;
        }
        else
        {
            // oberer Balken Farbverlauf
            background = GREEN_GRADIENT_PAINT;
            waitBackground = GREEN_WAIT_GRADIENT_PAINT;
            setupAfterPresentation = SetupAfterPresentation.TO_TOP;
        }
        paintBackground( gc, background, waitBackground, true, setupAfterPresentation );

        if ( processWidth > SMALL_TEXT_MIN_WIDTH )
        {
            gc.setFill( Color.WHITE );
            gc.setFont( getLargeFont() );
            gc.setTextAlign( TextAlignment.CENTER );
            gc.setTextBaseline( VPos.CENTER );
            gc.fillText( transfer.getName(), processWidth / 2.0, height * 0.20 );

            final double targetWeight = transfer.getTargetWeight();
            final double bottomWeight = transfer.getBottomWeight();
            double continuesWeight = 0;
            final List<TransferMaterial> transferMaterials = transfer.getTransferMaterials();
            for ( TransferMaterial transferMaterial : transferMaterials )
            {
                continuesWeight = continuesWeight + transferMaterial.getWeight();
            }
            gc.setFill( Color.YELLOW );
            final double openWeight = ( targetWeight - bottomWeight - continuesWeight ) / 1000.0;
            final String openWeightText = StringTools.N01F.format( openWeight ) + "t";
            gc.fillText( openWeightText, processWidth / 2.0 + 60, height * 0.20 );

            if ( processWidth > LARGE_TEXT_MIN_WIDTH )
            {
                gc.setFill( Color.WHITE );
                gc.setFont( getSmallFont() );
                gc.fillText( transfer.getBatch().getDetail(), processWidth / 2.0, height * 0.5 );

                // Paint Füllstand
                gc.setFill( Color.LIGHTGRAY );
                gc.fillRect( processWidth / 2.0 - ( LARGE_TEXT_MIN_WIDTH / 2.0 ), height * 0.65, LARGE_TEXT_MIN_WIDTH, height * 0.30 );
                // Links Sumpf
                final double bottomFactor = bottomWeight / targetWeight;
                gc.setFill( background );
                gc.fillRect( processWidth / 2.0 - ( LARGE_TEXT_MIN_WIDTH / 2.0 ), height * 0.65, LARGE_TEXT_MIN_WIDTH * bottomFactor, height * 0.30 );
                // Rechts Flüssigeinsatz
                final double continuesFactor = continuesWeight / targetWeight;
                final double continuesWidth = LARGE_TEXT_MIN_WIDTH * continuesFactor;
                gc.setFill( ALLOY_SOURCE );
                gc.fillRect( processWidth / 2.0 + ( LARGE_TEXT_MIN_WIDTH / 2.0 ) - continuesWidth, height * 0.65, continuesWidth, height * 0.30 );

                // Rest noch offen
                gc.setStroke( GanttColors.LIGHT_BLACK );
                gc.strokeRect( processWidth / 2.0 - ( LARGE_TEXT_MIN_WIDTH / 2.0 ), height * 0.65, LARGE_TEXT_MIN_WIDTH, height * 0.30 );
            }
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
