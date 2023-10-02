package com.hydro.casting.gui.melting.view;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.melting.task.*;
import com.hydro.casting.gui.prod.table.ChargingAnalysisTable;
import com.hydro.casting.gui.task.ValidateMachineTask;
import com.hydro.casting.gui.util.TaskButtonUtil;
import com.hydro.casting.server.contract.dto.MeltingInstructionDTO;
import com.hydro.core.common.cache.ClientCache;
import com.hydro.core.gui.CacheManager;
import com.hydro.core.gui.TaskManager;
import com.hydro.core.gui.View;
import com.hydro.core.gui.ViewType;
import com.hydro.core.gui.comp.TaskButton;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ViewDeclaration( id = MobileSkimmingViewController.ID, fxmlFile = MobileSkimmingViewController.FXML_FILE, type = ViewType.MAIN )
public class MobileSkimmingViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.MELTING.MOBILE.SKIMMING.VIEW;
    public final static String FXML_FILE = "/com/hydro/casting/gui/melting/view/MobileSkimmingView.fxml";
    private final static String BIG_BUTTON_STYLE = "-fx-max-height: Infinity";

    private String currentFurnaceSelection;
    private List<TaskButton> furnaceButtons;
    private ClientCache<MeltingInstructionDTO> meltingInstructionCache;

    @Inject
    private Injector injector;
    @Inject
    private CacheManager cacheManager;
    @Inject
    private TaskManager taskManager;
    @Inject
    private ValidateMachineTask<MeltingInstructionDTO> validateFurnaceTask;

    @FXML
    private TaskButton furnaceS1;
    @FXML
    private TaskButton furnaceS2;
    @FXML
    private TaskButton startSkimming;
    @FXML
    private TaskButton finishSkimming;
    @FXML
    private TaskButton startSkimmingMeltingChamber;
    @FXML
    private TaskButton finishSkimmingMeltingChamber;
    @FXML
    private TaskButton startMixing;
    @FXML
    private TaskButton finishMixing;
    @FXML
    private StartSkimmingTask startSkimmingTask;
    @FXML
    private StartSkimmingMeltingChamberTask startSkimmingMeltingChamberTask;
    @FXML
    private EndSkimmingMeltingChamberTask endSkimmingMeltingChamberTask;
    @FXML
    private StartMixingTask startMixingTask;
    @FXML
    private EndMixingTask endMixingTask;
    @FXML
    private ChargingAnalysisTable chargingAnalysisTable;

    private EndSkimmingTask endSkimmingTask;
    private StartTreatingOnlyWhenSkimmingIsActiveTask startTreatingOnlyWhenSkimmingIsActiveTask;

    @FXML
    public void initialize()
    {
        currentFurnaceSelection = "S1";
        furnaceButtons = new ArrayList<>();
        meltingInstructionCache = cacheManager.getCache( Casting.CACHE.PROD_CACHE_NAME );

        setupFurnace( furnaceS1 );
        setupFurnace( furnaceS2 );

        startSkimming.setButtonStyle( BIG_BUTTON_STYLE );
        finishSkimming.setButtonStyle( BIG_BUTTON_STYLE );
        startSkimmingMeltingChamber.setButtonStyle( BIG_BUTTON_STYLE );
        finishSkimmingMeltingChamber.setButtonStyle( BIG_BUTTON_STYLE );
        startMixing.setButtonStyle( BIG_BUTTON_STYLE );
        finishMixing.setButtonStyle( BIG_BUTTON_STYLE );

        // Registering a listener on the MeltingInstruction cache because the instruction is updated by the server when the active process step(s) change
        furnaceButtons.forEach( furnaceButton -> {
            String machine = (String) furnaceButton.getUserData();
            cacheManager.addCacheListener( Casting.CACHE.PROD_CACHE_NAME, Casting.CACHE.MELTING_INSTRUCTION_DATA_PATH + Objects.hash( machine ), key -> reloadFurnaceInstruction() );
        } );

        // Creating the two possible tasks that will be assigned to the FinishSkimming task button depending on the furnace selection
        startTreatingOnlyWhenSkimmingIsActiveTask = new StartTreatingOnlyWhenSkimmingIsActiveTask();
        endSkimmingTask = new EndSkimmingTask();

        injector.injectMembers( chargingAnalysisTable );
        chargingAnalysisTable.connect( cacheManager );

        reloadFurnaceInstruction();
        updateButtons();
    }

    @Override
    public void beforeShown( View view )
    {
        validateFurnaceTask.setViewContext( view.getViewContext() );
        TaskButtonUtil.registerTasks( view, injector, taskManager, validateFurnaceTask );
    }

    @Override
    public void activateView( View view )
    {
        chargingAnalysisTable.setActive( true );
        reloadFurnaceInstruction();
    }

    @Override
    public void deactivateView( View view )
    {
        chargingAnalysisTable.setActive( false );
    }

    private void reloadFurnaceInstruction()
    {
        MeltingInstructionDTO meltingInstructionDTO = meltingInstructionCache.get( Casting.CACHE.MELTING_INSTRUCTION_DATA_PATH + Objects.hash( currentFurnaceSelection ) );

        chargingAnalysisTable.setMeltingInstructionDTO( meltingInstructionDTO );
        startSkimmingTask.setMachineApk( currentFurnaceSelection );
        startSkimmingTask.setWithoutMachine( true );
        startSkimmingTask.setMeltingInstructionDTO( meltingInstructionDTO );
        startSkimmingMeltingChamberTask.setMachineApk( currentFurnaceSelection );
        startSkimmingMeltingChamberTask.setWithoutMachine( true );
        startSkimmingMeltingChamberTask.setMeltingInstructionDTO( meltingInstructionDTO );
        startTreatingOnlyWhenSkimmingIsActiveTask.setMachineApk( currentFurnaceSelection );
        startTreatingOnlyWhenSkimmingIsActiveTask.setMeltingInstructionDTO( meltingInstructionDTO );
        endSkimmingTask.setMachineApk( currentFurnaceSelection );
        endSkimmingTask.setWithoutMachine( true );
        endSkimmingTask.setMeltingInstructionDTO( meltingInstructionDTO );
        endSkimmingMeltingChamberTask.setMachineApk( currentFurnaceSelection );
        endSkimmingMeltingChamberTask.setWithoutMachine( true );
        endSkimmingMeltingChamberTask.setMeltingInstructionDTO( meltingInstructionDTO );
        startMixingTask.setMachineApk( currentFurnaceSelection );
        startMixingTask.setWithoutMachine( true );
        startMixingTask.setMeltingInstructionDTO( meltingInstructionDTO );
        endMixingTask.setMachineApk( currentFurnaceSelection );
        endMixingTask.setWithoutMachine( true );
        endMixingTask.setMeltingInstructionDTO( meltingInstructionDTO );

        // Furnace selection implies the task that is used for finishing a step, since for S1, this should result in starting the next step opposed to S2
        if ( currentFurnaceSelection.equals( "S1" ) )
        {
            finishSkimming.setTask( startTreatingOnlyWhenSkimmingIsActiveTask );
        }
        else
        {
            finishSkimming.setTask( endSkimmingTask );
        }
        TaskButtonUtil.registerTask( finishSkimming, injector, taskManager, validateFurnaceTask );

        validateFurnaceTask.setData( meltingInstructionDTO );
        taskManager.executeTask( validateFurnaceTask );
    }

    private void setupFurnace( TaskButton furnaceButton )
    {
        furnaceButtons.add( furnaceButton );
        furnaceButton.setButtonStyle( BIG_BUTTON_STYLE );
    }

    @FXML
    private void selectFurnace( ActionEvent actionEvent )
    {
        currentFurnaceSelection = (String) ( (TaskButton) actionEvent.getSource() ).getUserData();
        reloadFurnaceInstruction();
        updateButtons();
    }

    private void updateButtons()
    {
        for ( TaskButton furnaceButton : furnaceButtons )
        {
            furnaceButton.setDisable( Objects.equals( furnaceButton.getUserData(), this.currentFurnaceSelection ) );
        }
    }
}
