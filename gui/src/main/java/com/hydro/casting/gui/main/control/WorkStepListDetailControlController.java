package com.hydro.casting.gui.main.control;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.server.contract.dto.ProductionOrderDTO;
import com.hydro.casting.server.contract.dto.WorkStepDTO;
import com.hydro.casting.server.contract.dto.WorkStepListDTO;
import com.hydro.casting.server.contract.main.ProductionOrderView;
import com.hydro.core.gui.comp.ServerTableView;
import com.hydro.core.gui.control.DetailController;
import javafx.fxml.FXML;

import java.util.Collections;

public class WorkStepListDetailControlController extends DetailController<ProductionOrderDTO, WorkStepListDTO>
{

    @Inject
    private Injector injector;

    @FXML
    private ServerTableView<WorkStepDTO> table;

    private WorkStepListDTO workStepListDTO;

    public WorkStepListDetailControlController()
    {
        super( ProductionOrderView.class, WorkStepListDTO.class );
    }

    @FXML
    protected void initialize()
    {
        table.connect( injector, () -> {
            if ( workStepListDTO == null || workStepListDTO.getWorkSteps() == null )
            {
                return Collections.emptyList();
            }
            return workStepListDTO.getWorkSteps();
        } );
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void loadDetail( WorkStepListDTO data )
    {
        this.workStepListDTO = data;
        table.loadData();
    }
}
