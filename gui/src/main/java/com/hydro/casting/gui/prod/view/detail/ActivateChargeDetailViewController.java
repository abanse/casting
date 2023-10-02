package com.hydro.casting.gui.prod.view.detail;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.prod.task.ActivateChargeTask;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.FurnaceInstructionDTO;
import com.hydro.casting.server.contract.prod.ProductionView;
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

@ViewDeclaration( id = ActivateChargeDetailViewController.ID, fxmlFile = "/com/hydro/casting/gui/prod/view/detail/ActivateChargeDetailView.fxml", type = ViewType.DETAIL )
public class ActivateChargeDetailViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.PROD.DETAILS.ACTIVATE_CHARGE.VIEW;

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
    private ServerTableView<CasterScheduleDTO> table;

    @FXML
    private ActivateChargeTask activateChargeTask;

    private FurnaceInstructionDTO furnaceInstructionDTO;

    /**
     * Initializes the controller class.
     */
    @FXML
    @SuppressWarnings("Duplicates")
    void initialize()
    {
        injector.injectMembers( activateChargeTask );

        table.connect( injector, () -> {
            final ProductionView productionView = businessManager.getSession( ProductionView.class );
            final String machine = Casting.getCasterForMeltingFurnace( furnaceInstructionDTO.getFurnace() );
            final List<Integer> executionStates = new ArrayList<>();
            executionStates.add( Casting.SCHEDULABLE_STATE.RELEASED );
            executionStates.add( Casting.SCHEDULABLE_STATE.PAUSED );
            executionStates.add( Casting.SCHEDULABLE_STATE.FAILED );
            final Map<String, Object> parameters = new HashMap<>();
            parameters.put( "MACHINE", machine );
            parameters.put( "EXECUTION_STATES", executionStates );
            return productionView.loadBySearchType( SearchType.MACHINE, parameters );
        } );
    }

    @FXML
    void cancel( ActionEvent event )
    {
        viewManager.backward();
    }

    @Override
    public void setStartObject( Object startObject )
    {
        if ( startObject instanceof FurnaceInstructionDTO )
        {
            furnaceInstructionDTO = (FurnaceInstructionDTO) startObject;
        }
        else
        {
            furnaceInstructionDTO = null;
        }
    }

    @Override
    public void beforeShown( View view )
    {
        activateChargeTask.setLocked( !securityManager.hasRole( activateChargeTask.getId() ) );
        if ( furnaceInstructionDTO != null )
        {
            view.setTitle( "Charge aktivieren Ofen " + furnaceInstructionDTO.getFurnace() );
            activateChargeTask.setMachine( furnaceInstructionDTO.getFurnace() );
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
    public void activateCharge( ActionEvent actionEvent )
    {
        taskManager.executeTask( activateChargeTask );
    }
}
