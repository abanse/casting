package com.hydro.casting.gui.prod.table.row;

import com.hydro.casting.gui.analysis.control.row.AnalysisTableRow;
import com.hydro.casting.gui.model.Composition;

import java.util.List;

public class ChargingTableRow extends AnalysisTableRow
{
    public ChargingTableRow( List<Integer> lineIndex, boolean disableAverageLineColor )
    {
        super( lineIndex, disableAverageLineColor );
    }

    @Override
    public void updateItem( Composition item, boolean empty )
    {
        super.updateItem( item, empty );

        if ( empty )
        {
            return;
        }

        if ( item.getWeight() <= 0 && item.getOriginalWeight() > 0 )
        {
            setStyle( "-fx-opacity: 0.5;"  );
        }
    }
}