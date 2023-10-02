package com.hydro.casting.gui.prod.table.cell;

import com.hydro.casting.gui.prod.view.ProcessDocuViewController;
import com.hydro.casting.server.contract.dto.InspectionValueDTO;
import com.hydro.core.common.util.DateTimeUtil;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

public class InspectionResultCellFactory<S, D> implements Callback<TableColumn<S, D>, TableCell<S, D>>
{
    private final static int ICON_SIZE = 18;

    private String dateFormat = "dd.MM.yy HH:mm:ss";
    private DateFormat formatter = new SimpleDateFormat( dateFormat );

    @Override
    public TableCell<S, D> call( TableColumn<S, D> param )
    {
        return new InspectionResultTableCell();
    }

    private class InspectionResultTableCell extends TableCell<S, D>
    {
        @Override
        protected void updateItem( D value, boolean empty )
        {
            super.updateItem( value, empty );
            if ( empty )
            {
                setText( null );
                setGraphic( null );
            }
            else if ( value == null )
            {
                setText( null );
                setGraphic( createImageView( ProcessDocuViewController.RESULT_MISSED_I ) );
            }
            else
            {
                if ( value instanceof Object[] )
                {
                    final Object[] results = (Object[]) value;
                    final LocalDateTime resultTS = (LocalDateTime) results[0];
                    final Integer resultSummary = (Integer) results[1];
                    setText( formatter.format( DateTimeUtil.asDate( resultTS ) ) );
                    if ( resultSummary == null )
                    {
                        setGraphic( createImageView( ProcessDocuViewController.RESULT_MISSED_I ) );
                    }
                    else if ( resultSummary == InspectionValueDTO.RESULT_FAILED )
                    {
                        setGraphic( createImageView( ProcessDocuViewController.RESULT_FAILED_I ) );
                    }
                    else if ( resultSummary == InspectionValueDTO.RESULT_OK_WITH_LIMITATIONS )
                    {
                        setGraphic( createImageView( ProcessDocuViewController.RESULT_OK_WITH_LIMITATIONS_I ) );
                    }
                    else if ( resultSummary == InspectionValueDTO.RESULT_OK )
                    {
                        setGraphic( createImageView( ProcessDocuViewController.RESULT_OK_I ) );
                    }
                    else
                    {
                        setGraphic( createImageView( ProcessDocuViewController.RESULT_MISSED_I ) );
                    }
                }
                else
                {
                    setText( value.toString() );
                }
            }
        }
    }

    private ImageView createImageView( Image image )
    {
        final ImageView imageView = new ImageView( image );
        imageView.setPickOnBounds( true );
        imageView.setFitWidth( ICON_SIZE );
        imageView.setFitHeight( ICON_SIZE );

        return imageView;
    }

}
