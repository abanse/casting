package com.hydro.casting.gui.melting.view.detail;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.melting.task.ActivateChargeTask;
import com.hydro.casting.server.contract.dto.MeltingInstructionDTO;
import com.hydro.casting.server.contract.dto.MeltingScheduleDTO;
import com.hydro.casting.server.contract.melting.MeltingView;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.*;
import com.hydro.core.gui.comp.ServerTableView;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import com.hydro.core.server.contract.workplace.SearchType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ViewDeclaration( id = ActivateChargeDetailViewController.ID, fxmlFile = "/com/hydro/casting/gui/melting/view/detail/ActivateChargeDetailView.fxml", type = ViewType.DETAIL )
public class ActivateChargeDetailViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.MELTING.DETAILS.ACTIVATE_CHARGE.VIEW;

    @Inject
    private TaskManager taskManager;
    @Inject
    private Injector injector;
    @Inject
    private ViewManager viewManager;
    @Inject
    private SecurityManager securityManager;
    @Inject
    private BusinessManager businessManager;

    @FXML
    private ServerTableView<MeltingScheduleDTO> table;
    @FXML
    private ActivateChargeTask activateChargeTask;

    private MeltingInstructionDTO meltingInstructionDTO;

    @FXML
    @SuppressWarnings( "Duplicates" )
    private void initialize()
    {
        injector.injectMembers( activateChargeTask );
        table.connect( injector, () -> {
            final MeltingView meltingView = businessManager.getSession( MeltingView.class );
            final List<Integer> executionStates = new ArrayList<>();
            executionStates.add( Casting.SCHEDULABLE_STATE.PLANNED );
            executionStates.add( Casting.SCHEDULABLE_STATE.PAUSED );
            executionStates.add( Casting.SCHEDULABLE_STATE.FAILED );
            final Map<String, Object> parameters = new HashMap<>();
            parameters.put( "MACHINE", meltingInstructionDTO.getMachine() );
            parameters.put( "EXECUTION_STATES", executionStates );
            return meltingView.loadBySearchType( SearchType.MACHINE, parameters );
        } );
    }

    @FXML
    void cancel()
    {
        viewManager.backward();
    }

    @Override
    public void setStartObject( Object startObject )
    {
        if ( startObject instanceof MeltingInstructionDTO )
        {
            meltingInstructionDTO = (MeltingInstructionDTO) startObject;
        }
        else
        {
            meltingInstructionDTO = null;
        }
    }

    @Override
    public void beforeShown( View view )
    {
        activateChargeTask.setLocked( !securityManager.hasRole( activateChargeTask.getId() ) );
        if ( meltingInstructionDTO != null )
        {
            view.setTitle( "Charge aktivieren für Schmelzofen " + meltingInstructionDTO.getMachine() );
            activateChargeTask.setMachine( meltingInstructionDTO.getMachine() );
        }
    }

    @Override
    public void activateView( View view )
    {
        table.loadData();
    }

    @Override
    public void deactivateView( View view )
    {
    }

    @Override
    public void reloadView( Object source, View view )
    {
        table.loadData();
    }

    @FXML
    public void activateMeltingCharge( ActionEvent actionEvent )
    {
        taskManager.executeTask( activateChargeTask );
    }
}
