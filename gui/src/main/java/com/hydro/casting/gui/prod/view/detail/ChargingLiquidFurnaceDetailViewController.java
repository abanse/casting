package com.hydro.casting.gui.prod.view.detail;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.common.constant.EMaterialCalcMode;
import com.hydro.casting.gui.model.MaterialGroup;
import com.hydro.casting.gui.model.common.EMaterialGroup;
import com.hydro.casting.gui.prod.control.MaterialBrowser;
import com.hydro.casting.server.contract.dto.FurnaceInstructionDTO;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.*;
import com.hydro.core.gui.comp.TaskButton;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.util.function.Predicate;

@ViewDeclaration( id = ChargingLiquidFurnaceDetailViewController.ID, fxmlFile = "/com/hydro/casting/gui/prod/view/detail/ChargingLiquidFurnaceDetailView.fxml", type = ViewType.DETAIL )
public class ChargingLiquidFurnaceDetailViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.PROD.DETAILS.CHARGING_LIQUID_FURNACE.VIEW;


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
    private MaterialBrowser materialBrowser;

    @FXML
    private TaskButton chargingButton;

    //    @FXML
    //    private ServerTableView<CasterScheduleDTO> table;

    //    @FXML
    //    private ActivateChargeTask activateChargeTask;

    private FurnaceInstructionDTO furnaceInstructionDTO;

    private Predicate<MaterialGroup> liquidPredicate = ( materialGroup ) -> {
        if ( materialGroup.getName().equals( EMaterialGroup.SPEC_MATERIAL.getApk() ) || materialGroup.getName().equals( "Auflegierungen" ) || materialGroup.getName().equals( "Kaltmetalle" ) || materialGroup.getName().equals( "Sägestücke" ) )
        {
            return false;
        }
        return true;
    };

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        injector.injectMembers( materialBrowser );

        chargingButton.setDisable( true, "Momentan noch deaktiviert" );

        materialBrowser.setMaterialCalcMode( EMaterialCalcMode.SINGLE, null );

        //        table.connect( injector, () -> {
        //            final ProductionView productionView = businessManager.getSession( ProductionView.class );
        //            final String machine = Casting.getCasterForMeltingFurnace( furnaceInstructionDTO.getFurnace() );
        //            final List<Integer> executionStates = new ArrayList<>();
        //            executionStates.add( Casting.SCHEDULABLE_STATE.RELEASED );
        //            executionStates.add( Casting.SCHEDULABLE_STATE.PAUSED );
        //            executionStates.add( Casting.SCHEDULABLE_STATE.FAILED );
        //            final Map<String, Object> parameters = new HashMap<>();
        //            parameters.put( "MACHINE", machine );
        //            parameters.put( "EXECUTION_STATES", executionStates );
        //            return productionView.loadBySearchType( SearchType.MACHINE, parameters );
        //        } );
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
        //        activateChargeTask.setLocked( !securityManager.hasRole( activateChargeTask.getId() ) );
        if ( furnaceInstructionDTO != null )
        {
            view.setTitle( "Chargieren Ofen " + furnaceInstructionDTO.getFurnace() + " Charge " + furnaceInstructionDTO.getChargeWithoutYear() );
            //            activateChargeTask.setMachine( furnaceInstructionDTO.getFurnace() );
        }
    }

    @Override
    public void activateView( View view )
    {
        materialBrowser.loadMaterials( liquidPredicate );
    }

    @Override
    public void deactivateView( View view )
    {
    }

    @Override
    public void reloadView( Object source, View view )
    {
        materialBrowser.loadMaterials( liquidPredicate );
    }

    @FXML
    public void charging( ActionEvent actionEvent )
    {
        //taskManager.executeTask( activateChargeTask );
    }
}
