package com.hydro.casting.gui.fernladung.table.cell;

import com.hydro.casting.server.contract.downtime.dto.DowntimeDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.util.Callback;

import java.time.format.DateTimeFormatter;

public class DowntimeDateTimeFromValueFactory implements Callback<CellDataFeatures<DowntimeDTO, String>, ObservableValue<String>>
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
        if ( downtimeDTO.getFromTS() != null && downtimeDTO.getId() > 0 )
        {
            value = new SimpleStringProperty( downtimeDTO.getFromTS().format( DateTimeFormatter.ofPattern( "dd.MM.yyyy - HH:mm" ) ) );
        }
        else if ( downtimeDTO.getMachine() != null )
        {
            String machine = "";
            switch ( downtimeDTO.getMachine() )
            {
            case "P1":
                machine = "Stretcher";
                break;

            case "P2":
                machine = "Waage 1";
                break;

            case "P3":
                machine = "Waage 2";
                break;

            case "PL":
                machine = "Packlinie";
                break;

            default:
                machine = downtimeDTO.getMachine();
                break;
            }
            value = new SimpleStringProperty( machine );
        }
        return value;
    }
}
