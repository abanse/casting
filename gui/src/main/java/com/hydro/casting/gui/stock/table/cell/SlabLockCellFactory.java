package com.hydro.casting.gui.stock.table.cell;

import com.hydro.casting.gui.ImagesCasting;
import com.hydro.casting.server.contract.dto.SlabDTO;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

/**
 * Custom factory that returns a TableCell that displays locked information.
 */
public class SlabLockCellFactory<D extends SlabDTO> implements Callback<TableColumn<D, String>, TableCell<D, String>>
{
    private final static int ICON_SIZE = 18;

    @Override
    public TableCell<D, String> call( TableColumn<D, String> param )
    {
        return new SlabLockTableCell();
    }

    private ImageView createImageView( Image image )
    {
        final ImageView imageView = new ImageView( image );
        imageView.setPickOnBounds( true );
        imageView.setFitWidth( ICON_SIZE );
        imageView.setFitHeight( ICON_SIZE );

        return imageView;
    }

    /**
     * The actual custom TableCell class that is returned by this factory
     */
    private class SlabLockTableCell extends TableCell<D, String>
    {
        @Override
        protected void updateItem( String value, boolean empty )
        {
            super.updateItem( value, empty );

            if ( empty )
            {
                setGraphic( null );
                setText( null );
                return;
            }

            final SlabDTO slabDTO = getTableRow().getItem();
            if ( slabDTO != null && slabDTO.getCountLocks() > 0 )
            {
                if ( slabDTO.getCountActiveLocks() > 0 )
                {
                    setGraphic( createImageView( ImagesCasting.SLAB_LOCKED.load() ) );
                    setText( "" + slabDTO.getCountActiveLocks() + " (" + slabDTO.getCountLocks() + ")" );
                }
                else
                {
                    setGraphic( createImageView( ImagesCasting.SLAB_UNLOCKED.load() ) );
                    setText( "(" + slabDTO.getCountLocks() + ")" );
                }
            }
            else
            {
                setGraphic( null );
                setText( null );
            }
        }
    }
}
