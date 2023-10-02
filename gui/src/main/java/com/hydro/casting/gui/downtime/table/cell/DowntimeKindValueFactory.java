package com.hydro.casting.gui.downtime.table.cell;

import com.hydro.casting.server.contract.downtime.dto.DowntimeDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.util.Callback;

public class DowntimeKindValueFactory implements Callback<CellDataFeatures<DowntimeDTO, String>, ObservableValue<String>>
{
    @Override
    public ObservableValue<String> call( CellDataFeatures<DowntimeDTO, String> param )
    {
        if ( param == null || param.getValue() == null )
        {
            return null;
        }
        DowntimeDTO downtimeDTO = param.getValue().getValue();
        StringProperty value = null;
        if ( downtimeDTO.getDowntimeKind1() != null && downtimeDTO.getDowntimeKind2() != null )
        {
            value = new SimpleStringProperty( downtimeDTO.getDowntimeKind1() + "/" + downtimeDTO.getDowntimeKind2() + " " + downtimeDTO.getDowntimeDescription() );
        }
        else if ( downtimeDTO.getId() > 0 )
        {
            int count = 0;
            if ( downtimeDTO.getChilds() != null )
            {
                if ( !downtimeDTO.getChilds().isEmpty() && downtimeDTO.getChilds().get( 0 ).getChilds() == null )
                {
                    count = downtimeDTO.getChilds().size();
                }
                else
                {
                    for ( DowntimeDTO shiftDowntimeDTO : downtimeDTO.getChilds() )
                    {
                        if ( shiftDowntimeDTO.getChilds() != null )
                        {
                            count += shiftDowntimeDTO.getChilds().size();
                        }
                    }
                }
            }
            value = new SimpleStringProperty( "Anzahl: " + count );
        }
        return value;
    }
}
