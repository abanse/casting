package com.hydro.casting.gui.downtime.table.cell;

import com.hydro.casting.server.contract.downtime.dto.DowntimeDTO;
import com.hydro.core.common.util.DateTimeUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.util.Callback;

public class DowntimeDurationValueFactory implements Callback<CellDataFeatures<DowntimeDTO, String>, ObservableValue<String>>
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
        if ( downtimeDTO.getEndTS() != null )
        {
            value = new SimpleStringProperty( DateTimeUtil.formatDuration( downtimeDTO.getFromTS(), downtimeDTO.getEndTS() ) );
        }
        return value;
    }
}
