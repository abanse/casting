package com.hydro.casting.gui.melting.view;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.melting.task.CreateNewMeltingChargeTask;
import com.hydro.casting.gui.task.ValidateMachineTask;
import com.hydro.casting.gui.util.TaskButtonUtil;
import com.hydro.casting.server.contract.dto.MeltingInstructionDTO;
import com.hydro.casting.server.contract.dto.MeltingScheduleDTO;
import com.hydro.casting.server.contract.melting.MeltingView;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.TaskManager;
import com.hydro.core.gui.View;
import com.hydro.core.gui.ViewType;
import com.hydro.core.gui.comp.ServerTableView;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import com.hydro.core.server.contract.workplace.SearchType;
import javafx.fxml.FXML;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ViewDeclaration( id = MeltingChargesS2ViewController.ID, fxmlFile = "/com/hydro/casting/gui/melting/view/MeltingChargesS2View.fxml", type = ViewType.MAIN )
public class MeltingChargesS2ViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.MELTING.CHARGES_S2.VIEW;

    @Inject
    private Injector injector;
    @Inject
    private BusinessManager businessManager;
    @Inject
    private ValidateMachineTask<MeltingInstructionDTO> validateMachineTask;
    @Inject
    private TaskManager taskManager;

    @FXML
    private ServerTableView<MeltingScheduleDTO> table;
    @FXML
    private CreateNewMeltingChargeTask createNewMeltingChargeTask;

    private boolean initialized = false;

    @FXML
    private void initialize()
    {
        // Define how the server table loads the data
        table.connect( injector, () -> {
            final MeltingView meltingView = businessManager.getSession( MeltingView.class );
            final List<Integer> executionStates = new ArrayList<>();
            executionStates.add( Casting.SCHEDULABLE_STATE.PLANNED );
            executionStates.add( Casting.SCHEDULABLE_STATE.RELEASED );
            executionStates.add( Casting.SCHEDULABLE_STATE.IN_PROGRESS );
            executionStates.add( Casting.SCHEDULABLE_STATE.PAUSED );
            final Map<String, Object> parameters = new HashMap<>();
            parameters.put( "MACHINE", "S2" );
            parameters.put( "EXECUTION_STATES", executionStates );
            return meltingView.loadBySearchType( SearchType.MACHINE, parameters );
        } );

        // Tell the CreateNewMeltingChargeTask to reload the table data after creating a new charge
        createNewMeltingChargeTask.setCallbackAfterFinish( ( param ) -> {
            reloadTable();
            return null;
        } );

        // Define an artificial melting instruction to pass the machine to the ValidateMachineTask
        final MeltingInstructionDTO meltingInstructionDTO = new MeltingInstructionDTO();
        meltingInstructionDTO.setMachine( "S2" );
        validateMachineTask.setData( meltingInstructionDTO );
    }

    private void reloadTable()
    {
        table.loadData();
        taskManager.executeTask( validateMachineTask );
    }

    @Override
    public void activateView( View view )
    {
        reloadTable();
    }

    @Override
    public void reloadView( Object source, View view )
    {
        reloadTable();
    }

    @Override
    public void beforeShown( View view )
    {
        // Initialization, but wee need the view as a parameter, so it's done here instead of initialize()
        if ( !initialized )
        {
            validateMachineTask.setViewContext( view.getViewContext() );
            TaskButtonUtil.registerTasks( view, injector, taskManager, validateMachineTask );
            initialized = true;
        }
    }
}
