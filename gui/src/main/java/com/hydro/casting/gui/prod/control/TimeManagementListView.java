package com.hydro.casting.gui.prod.control;

import com.hydro.casting.server.contract.dto.TimeManagementViolationDTO;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class TimeManagementListView extends ListView<TimeManagementViolationDTO>
{

    public TimeManagementListView()
    {
        setCellFactory( e -> new TimeManagementViolationCell() );
    }

    public ObservableList<TimeManagementViolationDTO> getTimeManagementViolations()
    {
        return getItems();
    }

    public void setTimeManagementViolations( ObservableList<TimeManagementViolationDTO> timeManagementViolations )
    {
        setItems( timeManagementViolations );
    }

    public class TimeManagementViolationCell extends ListCell<TimeManagementViolationDTO>
    {
        private TimeManagementViolationControl timeManagementViolationControl = new TimeManagementViolationControl();

        public TimeManagementViolationCell()
        {
            getStyleClass().setAll( "time-management-violation-cell" );
        }

        @Override
        protected void updateItem( TimeManagementViolationDTO item, boolean empty )
        {
            super.updateItem( item, empty );

            if ( empty )
            {
                setGraphic( null );
            }
            else
            {
                if ( item.isChecked() )
                {
                    getStyleClass().setAll( "time-management-violation-checked-cell" );
                }
                else
                {
                    getStyleClass().setAll( "time-management-violation-cell" );
                }
                timeManagementViolationControl.setTimeManagementViolation( item );
                setGraphic( timeManagementViolationControl );
            }
        }

    }

}
