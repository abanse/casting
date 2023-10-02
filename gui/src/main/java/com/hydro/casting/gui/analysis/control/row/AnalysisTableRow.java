package com.hydro.casting.gui.analysis.control.row;

import com.hydro.casting.gui.model.Composition;
import javafx.scene.control.TableRow;

import java.util.List;

public class AnalysisTableRow extends TableRow<Composition>
{
    private List<Integer> lineIndex;
    private boolean disableAverageLineColor = false;

    public AnalysisTableRow( List<Integer> lineIndex, boolean disableAverageLineColor )
    {
        this.lineIndex = lineIndex;
        this.disableAverageLineColor = disableAverageLineColor;
    }

    @Override
    public void updateItem( Composition item, boolean empty )
    {
        super.updateItem( item, empty );

        if ( empty )
        {
            setStyle( null );
            return;
        }

        String style = null;
        if ( item != null && item.getName() != null && item.getName().endsWith( ".LG" ) )
        {
            if ( ( getIndex() % 2 ) == 0 )
            {
                style = "-fx-background-color: #e3c3c3;";
            }
            else
            {
                style = "-fx-background-color: #e3a4a4;";
            }
        }
        else if ( getIndex() == 1 && !disableAverageLineColor )
        {
            style = "-fx-background-color: #d4cda6;";
        }

        boolean paintLine = false;
        if ( lineIndex != null )
        {
            for ( int line : lineIndex )
            {
                if ( getIndex() == line )
                {
                    paintLine = true;
                    break;
                }
            }
        }
        if ( paintLine )
        {
            if ( style != null )
            {
                // style = style + "-fx-border-color: -fx-table-cell-border-color -fx-table-cell-border-color
                // grey -fx-table-cell-border-color;";
                style = style + "-fx-border-color: transparent -fx-table-cell-border-color grey transparent; -fx-border-width: 0 0 3 0;";
            }
            else
            {
                // style = "-fx-border-color: -fx-table-cell-border-color -fx-table-cell-border-color grey
                // -fx-table-cell-border-color;";
                style = "-fx-border-color: transparent -fx-table-cell-border-color grey transparent; -fx-border-width: 0 0 3 0;";
            }
        }

        setStyle( style );
    }
}