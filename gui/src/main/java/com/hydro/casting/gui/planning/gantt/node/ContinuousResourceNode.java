package com.hydro.casting.gui.planning.gantt.node;

import com.hydro.casting.gui.gantt.BaseGanttChart;
import com.hydro.casting.gui.planning.gantt.model.CGElement;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;

public class ContinuousResourceNode extends AGanttChartNode
{
    private boolean dragActive = false;

    private final ContextMenu contextMenu = new ContextMenu();

    public ContinuousResourceNode( BaseGanttChart chart, final CGElement element )
    {
        super( chart, element, false, false );

        MenuItem createSerie = new MenuItem( element.getName() + " erzeuge Serie" );
        createSerie.setOnAction( event -> {
            System.out.println( "#### createSerie" );
        } );
        MenuItem deleteAll = new MenuItem( element.getName() + " lösche Alle" );
        createSerie.setOnAction( event -> {
            System.out.println( "#### deleteAll" );
        } );
        contextMenu.getItems().addAll( createSerie, deleteAll );

        setOnMouseMoved( event -> {
            draw( event.getX() );
        } );
        setOnMouseExited( event -> {
            if ( dragActive )
            {
                return;
            }
            draw( null );
        } );

        setOnDragDetected( event -> {
            ClipboardContent content = new ClipboardContent();

            content.putString( "CR" + element.getName() );

            Dragboard dragBoard = startDragAndDrop( TransferMode.MOVE );
            // dragBoard.setDragView(TransferBrowser.DND_TRANSFER_BOOKMARK_IMAGE);
            dragBoard.setContent( content );

            event.consume();

            dragActive = true;
        } );

        setOnDragDone( event -> {
            dragActive = false;
        } );

        setOnMousePressed( event -> {
            if ( event.isSecondaryButtonDown() )
            {
                contextMenu.show( this, event.getScreenX(), event.getScreenY() );
            }
        } );

    }

    @Override
    protected void draw()
    {
        draw( null );
    }

    protected void draw( Double xPoint )
    {
        final GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect( 0, 0, getWidth(), getHeight() );

        gc.setStroke( Color.GRAY );
        gc.setLineWidth( 2.0 );
        gc.strokeRect( 0, 0, getWidth() + 2.0, getHeight() );

        if ( xPoint != null )
        {
            final double defaultWidth = getDisplayLength( DEFAULT_SO_TRANSFER_DURATION );
            gc.strokeRect( xPoint - ( defaultWidth / 2.0 ), 3, defaultWidth, getHeight() - 6 );

            final double r = defaultWidth * 0.15;
            final double[] arrowX = new double[] { xPoint - r, xPoint + r, xPoint + r, xPoint + ( 2 * r ), xPoint, xPoint - ( 2 * r ), xPoint - r };
            final double h = 5;
            final double ah = getHeight() * 0.6;
            final double[] arrowY = new double[] { h, h, ah, ah, getHeight() - h, ah, ah };
            gc.strokePolygon( arrowX, arrowY, arrowX.length );
        }
    }
}
