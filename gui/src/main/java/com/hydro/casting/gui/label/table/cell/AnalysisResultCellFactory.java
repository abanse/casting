package com.hydro.casting.gui.label.table.cell;

import com.hydro.casting.gui.analysis.view.AnalysisViewController;
import com.hydro.casting.server.contract.dto.AnalysisDTO;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Custom factory that returns a TreeTableCell that displays information on the "analysis-ok" status.
 *
 * @param <S> The class that is used in the table that includes this cell for each row
 * @param <D> The value that is used and passed into the cell
 */
public class AnalysisResultCellFactory<S, D> implements Callback<TreeTableColumn<S, D>, TreeTableCell<S, D>>
{
    private final static int ICON_SIZE = 18;

    private final String dateFormat = "dd.MM.yy HH:mm";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern( dateFormat );

    @Override
    public TreeTableCell<S, D> call( TreeTableColumn<S, D> param )
    {
        return new AnalysisResultTableCell();
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
     * The actual custom TreeTableCell class that is returned by this factory
     */
    private class AnalysisResultTableCell extends TreeTableCell<S, D>
    {
        private final static String TOOLTIP_TEXT = "Grüner Haken: Analyse OK\nRoter Pfeil: Max-Wert(e) überschritten\nBlauer Pfeil: Min-Wert(e) unterschritten\n"
                + "Zeitstempel: Datum Fertigstellung der 90er-Proben (nur falls Analyse ok)";
        private final HBox iconContainer;

        private AnalysisResultTableCell()
        {
            iconContainer = new HBox();
        }

        @Override
        protected void updateItem( D value, boolean empty )
        {
            super.updateItem( value, empty );

            // Clearing previous UI elements on data update
            iconContainer.getChildren().clear();
            String text = null;
            Tooltip tooltip = null;

            TreeTableRow<S> row = this.getTreeTableRow();
            TreeItem<S> treeItem = row.getTreeItem();

            // Only process leaf items. Header items define a set of analyses for an entire charge, and thus should not display an "analysis ok" status
            if ( treeItem != null )
            {
                AnalysisDTO dto = (AnalysisDTO) treeItem.getValue();

                if ( !empty && value != null )
                {
                    if ( value instanceof Integer )
                    {
                        Integer analysisOk = (Integer) value;

                        switch ( analysisOk )
                        {
                        case AnalysisDTO.ANALYSIS_OK:
                            iconContainer.getChildren().add( createImageView( AnalysisViewController.ANALYSIS_OK ) );
                            break;
                        case AnalysisDTO.ANALYSIS_LOW:
                            iconContainer.getChildren().add( createImageView( AnalysisViewController.ANALYSIS_LOW ) );
                            break;
                        case AnalysisDTO.ANALYSIS_HIGH:
                            iconContainer.getChildren().add( createImageView( AnalysisViewController.ANALYSIS_HIGH ) );
                            break;
                        case AnalysisDTO.ANALYSIS_LOW_HIGH:
                            iconContainer.getChildren().add( createImageView( AnalysisViewController.ANALYSIS_LOW ) );
                            iconContainer.getChildren().add( createImageView( AnalysisViewController.ANALYSIS_HIGH ) );
                            break;
                        }

                        // Casting samples (90 and above) need a timestamp, since their "analysis ok" status "expires" after a certain time
                        if ( analysisOk == 0 && Integer.parseInt( dto.getSampleNumber() ) >= 90 && dto.getApprovalTime() != null)
                        {
                            text = formatter.format( dto.getApprovalTime() );
                        }

                        tooltip = new Tooltip( TOOLTIP_TEXT );
                        tooltip.setShowDelay( new javafx.util.Duration( 500 ) );
                    }
                }

                // Change coloring on the row in case a recent change was detected
                if ( dto.getLastChanged() != null )
                {
                    Duration durationSinceLastChange = Duration.between( dto.getLastChanged(), LocalDateTime.now() );
                    if ( durationSinceLastChange.compareTo( Duration.ofMinutes( 5 ) ) < 0 )
                    {
                        row.styleProperty()
                                .bind( Bindings.when( row.selectedProperty() ).then( new SimpleStringProperty( null ) ).otherwise( new SimpleStringProperty( "-fx-background-color:yellow" ) ) );
                    }
                    else
                    {
                        row.styleProperty().unbind();
                        row.setStyle( null );
                    }
                }
            }

            setText( text );
            setGraphic( iconContainer );
            setTooltip( tooltip );
        }
    }
}
