package com.hydro.casting.gui.planning.gantt.node;

import com.hydro.casting.common.Casting;
import com.hydro.casting.gui.model.Batch;
import com.hydro.casting.gui.model.Transfer;
import com.hydro.casting.gui.model.TransferMaterial;
import com.hydro.casting.gui.gantt.BaseGanttChart;
import com.hydro.casting.gui.planning.gantt.FurnaceGanttChart;
import com.hydro.casting.gui.planning.gantt.model.CGElement;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class ContinuousTransferNode extends AGanttChartNode
{
    private final ContextMenu contextMenu = new ContextMenu();

    public ContinuousTransferNode( BaseGanttChart chart, CGElement element )
    {
        super( chart, element, false, false );

        String batchName;
        if ( getElement().getElement() instanceof Batch )
        {
            final Batch batch = (Batch) getElement().getElement();
            batchName = batch.getFrom().getName();
        }
        else if ( getElement().getElement() instanceof Transfer )
        {
            final Transfer transfer = (Transfer) getElement().getElement();
            final Batch batch = transfer.getBatch();
            batchName = batch.getName();
        }
        else if ( getElement().getElement() instanceof TransferMaterial )
        {
            final TransferMaterial transferMaterial = (TransferMaterial) getElement().getElement();
            final Batch batch = transferMaterial.getTransfer().getBatch();
            batchName = batch.getName();
        }
        else
        {
            batchName = element.getName();
        }

        MenuItem delete = new MenuItem( "Überführung " + batchName + " löschen" );
        delete.setOnAction( event -> {
            if ( chart instanceof FurnaceGanttChart )
            {
                ( (FurnaceGanttChart) chart ).deleteContinousTransferTo( element );
            }
        } );
        contextMenu.getItems().addAll( delete );
        contextMenu.setOnShowing( event -> {
            delete.setDisable( getElement().isTheoretical() );
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
        final double processWidth = getProcessWidth();
        final double height = getHeight();

        final GraphicsContext gc = getGraphicsContext2D();
        final String resourceName = getElement().getResource().getName();
        if ( resourceName.equals( Casting.ALLOY_SOURCES.MELTING_FURNACE_S1 ) )
        {
            if ( getElement().isTheoretical() )
            {
                paintBackground( gc, ALLOY_SOURCE_S1_TRANSPARENT, ALLOY_SOURCE_S1_TRANSPARENT, false, SetupAfterPresentation.TO_BOTTOM );
            }
            else
            {
                paintBackground( gc, ALLOY_SOURCE_S1, ALLOY_SOURCE_S1, false, SetupAfterPresentation.TO_BOTTOM );
            }
        }
        else if ( resourceName.equals( Casting.ALLOY_SOURCES.MELTING_FURNACE_S2 ) )
        {
            if ( getElement().isTheoretical() )
            {
                paintBackground( gc, ALLOY_SOURCE_S2_TRANSPARENT, ALLOY_SOURCE_S2_TRANSPARENT, false, SetupAfterPresentation.TO_BOTTOM );
            }
            else
            {
                paintBackground( gc, ALLOY_SOURCE_S2, ALLOY_SOURCE_S2, false, SetupAfterPresentation.TO_BOTTOM );
            }
        }
        else if ( resourceName.equals( Casting.ALLOY_SOURCES.UBC_S3 ) )
        {
            if ( getElement().isTheoretical() )
            {
                paintBackground( gc, ALLOY_SOURCE_S3_TRANSPARENT, ALLOY_SOURCE_S3_TRANSPARENT, false, SetupAfterPresentation.TO_BOTTOM );
            }
            else
            {
                paintBackground( gc, ALLOY_SOURCE_S3, ALLOY_SOURCE_S3, false, SetupAfterPresentation.TO_BOTTOM );
            }
        }
        else if ( resourceName.equals( Casting.ALLOY_SOURCES.REAL_ALLOY ) )
        {
            if ( getElement().isTheoretical() )
            {
                paintBackground( gc, ALLOY_SOURCE_RA_TRANSPARENT, ALLOY_SOURCE_RA_TRANSPARENT, false, SetupAfterPresentation.TO_BOTTOM );
            }
            else
            {
                paintBackground( gc, ALLOY_SOURCE_RA, ALLOY_SOURCE_RA, false, SetupAfterPresentation.TO_BOTTOM );
            }
        }
        else if ( resourceName.equals( Casting.ALLOY_SOURCES.ELEKTROLYSE ) )
        {
            if ( getElement().isTheoretical() )
            {
                paintBackground( gc, ALLOY_SOURCE_EL_TRANSPARENT, ALLOY_SOURCE_EL_TRANSPARENT, false, SetupAfterPresentation.TO_BOTTOM );
            }
            else
            {
                paintBackground( gc, ALLOY_SOURCE_EL, ALLOY_SOURCE_EL, false, SetupAfterPresentation.TO_BOTTOM );
            }
        }
        else
        {
            paintBackground( gc, ALLOY_SOURCE, ALLOY_SOURCE, false, SetupAfterPresentation.TO_BOTTOM );
        }

        if ( processWidth > SMALL_TEXT_MIN_WIDTH )
        {
            gc.setFill( Color.WHITE );
            gc.setTextAlign( TextAlignment.CENTER );
            gc.setTextBaseline( VPos.CENTER );
            gc.setFont( getLargeFont() );
            gc.fillText( getElement().getName(), processWidth * 0.5, height * 0.25 );

            gc.setFont( getSmallFont() );
            // 2.Zeile O02.22/2229
            if ( getElement().getElement() instanceof Batch )
            {
                final Batch batch = (Batch) getElement().getElement();
                gc.fillText( batch.getName() + " " + batch.getFrom().getName(), processWidth * 0.5, height * 0.75 );
            }
            else if ( getElement().getElement() instanceof Transfer )
            {
                final Transfer transfer = (Transfer) getElement().getElement();
                final Batch batch = transfer.getBatch();
                if ( batch != null )
                {
                    gc.fillText( batch.getName(), processWidth * 0.5, height * 0.75 );
                }
            }
            else if ( getElement().getElement() instanceof TransferMaterial )
            {
                final TransferMaterial transferMaterial = (TransferMaterial) getElement().getElement();
                final Batch batch = transferMaterial.getTransfer().getBatch();
                if ( batch != null )
                {
                    gc.fillText( batch.getName(), processWidth * 0.5, height * 0.75 );
                }
            }
        }
    }
}
