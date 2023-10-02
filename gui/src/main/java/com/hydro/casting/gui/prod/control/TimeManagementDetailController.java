package com.hydro.casting.gui.prod.control;

import com.hydro.casting.gui.prod.gantt.TimeManagementGanttChart;
import com.hydro.casting.server.contract.dto.CastingInstructionDTO;
import com.hydro.casting.server.contract.dto.TimeManagementListDTO;
import com.hydro.casting.server.contract.prod.ProductionView;
import com.hydro.core.gui.control.DetailController;
import javafx.fxml.FXML;

import java.util.Collections;

public class TimeManagementDetailController extends DetailController<CastingInstructionDTO, TimeManagementListDTO>
{
    @FXML
    private TimeManagementGanttChart timeManagementGanttChart;

    public TimeManagementDetailController()
    {
        super( ProductionView.class, TimeManagementListDTO.class );
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
    }

    @Override
    public void loadDetail( TimeManagementListDTO timeManagements )
    {
        if ( timeManagements == null || timeManagements.getTimeManagements() == null )
        {
            timeManagementGanttChart.loadData( Collections.emptyList() );
        }
        else
        {
            timeManagementGanttChart.loadData( timeManagements.getTimeManagements() );
        }
    }

    public void start()
    {
        timeManagementGanttChart.start();
    }

    public void stop()
    {
        timeManagementGanttChart.stop();
    }
}
