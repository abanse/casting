package com.hydro.casting.gui.downtime.table.cell;

import com.hydro.casting.server.contract.downtime.dto.DowntimeDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.util.Callback;

public class DowntimeModuleValueFactory implements Callback<CellDataFeatures<DowntimeDTO, String>, ObservableValue<String>>
{
    @Override
    public ObservableValue<String> call( CellDataFeatures<DowntimeDTO, String> param )
    {
        if ( param == null || param.getValue() == null )
        {
            return null;
        }
        DowntimeDTO downtimeDTO = param.getValue().getValue();
        StringProperty value;
        if ( downtimeDTO.getModule() != null && downtimeDTO.getComponent() != null )
        {
            value = new SimpleStringProperty( downtimeDTO.getModule() + "/" + downtimeDTO.getComponent() + " " + downtimeDTO.getModuleDescription() );
        }
        else
        {
            value = new SimpleStringProperty( "" );
        }
        return value;
    }
}
