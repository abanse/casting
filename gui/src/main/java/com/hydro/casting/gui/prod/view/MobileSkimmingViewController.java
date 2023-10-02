package com.hydro.casting.gui.prod.view;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.prod.control.EquipmentStatus;
import com.hydro.casting.gui.prod.control.ProgressCanvas;
import com.hydro.casting.gui.prod.table.ChargingAnalysisTable;
import com.hydro.casting.gui.prod.task.RestingFurnaceTask;
import com.hydro.casting.gui.prod.task.SkimmingFurnaceTask;
import com.hydro.casting.gui.task.ValidateMachineTask;
import com.hydro.casting.gui.util.TaskButtonUtil;
import com.hydro.casting.server.contract.dto.FurnaceInstructionDTO;
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
    public final static String ID = SecurityCasting.PROD.MOBILE.SKIMMING.VIEW;
    public final static String FXML_FILE = "/com/hydro/casting/gui/prod/view/MobileSkimmingView.fxml";
    private final static String BIG_BUTTON_STYLE = "-fx-max-height: Infinity";

    private String currentFurnaceSelection;
    private List<TaskButton> furnaceButtons;
    private List<ProgressCanvas> furnaceProgresses;
    private ClientCache<FurnaceInstructionDTO> furnaceInstructionCache;

    @Inject
    private Injector injector;
    @Inject
    private CacheManager cacheManager;
    @Inject
    private TaskManager taskManager;
    @Inject
    private ValidateMachineTask<FurnaceInstructionDTO> validateFurnaceTask;

    @FXML
    private TaskButton furnace51;
    @FXML
    private EquipmentStatus furnace51Status;
    @FXML
    private ProgressCanvas furnace51Progress;
    @FXML
    private TaskButton furnace52;
    @FXML
    private EquipmentStatus furnace52Status;
    @FXML
    private ProgressCanvas furnace52Progress;
    @FXML
    private TaskButton furnace61;
    @FXML
    private EquipmentStatus furnace61Status;
    @FXML
    private ProgressCanvas furnace61Progress;
    @FXML
    private TaskButton furnace62;
    @FXML
    private EquipmentStatus furnace62Status;
    @FXML
    private ProgressCanvas furnace62Progress;
    @FXML
    private TaskButton furnace71;
    @FXML
    private EquipmentStatus furnace71Status;
    @FXML
    private ProgressCanvas furnace71Progress;
    @FXML
    private TaskButton furnace72;
    @FXML
    private EquipmentStatus furnace72Status;
    @FXML
    private ProgressCanvas furnace72Progress;
    @FXML
    private TaskButton furnace81;
    @FXML
    private EquipmentStatus furnace81Status;
    @FXML
    private ProgressCanvas furnace81Progress;
    @FXML
    private TaskButton furnace82;
    @FXML
    private EquipmentStatus furnace82Status;
    @FXML
    private ProgressCanvas furnace82Progress;
    @FXML
    private TaskButton startSkimming;
    @FXML
    private TaskButton finishSkimming;
    @FXML
    private SkimmingFurnaceTask skimmingFurnaceTask;
    @FXML
    private RestingFurnaceTask restingFurnaceTask;
    @FXML
    private ChargingAnalysisTable chargingAnalysisTable;

    @FXML
    public void initialize()
    {
        currentFurnaceSelection = "51";
        furnaceButtons = new ArrayList<>();
        furnaceProgresses = new ArrayList<>();
        furnaceInstructionCache = cacheManager.getCache( Casting.CACHE.PROD_CACHE_NAME );

        setupFurnace( furnace51, furnace51Status, furnace51Progress );
        setupFurnace( furnace52, furnace52Status, furnace52Progress );
        setupFurnace( furnace61, furnace61Status, furnace61Progress );
        setupFurnace( furnace62, furnace62Status, furnace62Progress );
        setupFurnace( furnace71, furnace71Status, furnace71Progress );
        setupFurnace( furnace72, furnace72Status, furnace72Progress );
        setupFurnace( furnace81, furnace81Status, furnace81Progress );
        setupFurnace( furnace82, furnace82Status, furnace82Progress );

        startSkimming.setButtonStyle( BIG_BUTTON_STYLE );
        finishSkimming.setButtonStyle( BIG_BUTTON_STYLE );

        furnaceButtons.forEach( furnaceButton -> {
            String machine = (String) furnaceButton.getUserData();
            cacheManager.addCacheListener( Casting.CACHE.PROD_CACHE_NAME, Casting.CACHE.FURNACE_INSTRUCTION_DATA_PATH + "/data/" + Objects.hash( machine ), key -> reloadFurnaceInstruction() );
            cacheManager.addCacheListener( Casting.CACHE.PLANNING_CACHE_NAME, Casting.CACHE.MACHINE_DATA_PATH + "/data/" + Objects.hash( machine ), key -> reloadFurnaceInstruction() );
        } );

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
        furnaceProgresses.forEach( ProgressCanvas::start );
        reloadFurnaceInstruction();
    }

    @Override
    public void deactivateView( View view )
    {
        chargingAnalysisTable.setActive( false );
        furnaceProgresses.forEach( ProgressCanvas::stop );
    }

    private void reloadFurnaceInstruction()
    {
        FurnaceInstructionDTO furnaceInstructionDTO = furnaceInstructionCache.get( Casting.CACHE.FURNACE_INSTRUCTION_DATA_PATH + "/data/" + Objects.hash( currentFurnaceSelection ) );

        chargingAnalysisTable.setCastingInstruction( furnaceInstructionDTO );
        skimmingFurnaceTask.setMachine( currentFurnaceSelection );
        skimmingFurnaceTask.setFurnaceInstructionDTO( furnaceInstructionDTO );
        restingFurnaceTask.setMachine( currentFurnaceSelection );
        restingFurnaceTask.setFurnaceInstructionDTO( furnaceInstructionDTO );

        validateFurnaceTask.setData( furnaceInstructionDTO );
        taskManager.executeTask( validateFurnaceTask );
    }

    private void setupFurnace( TaskButton furnaceButton, EquipmentStatus furnaceStatus, ProgressCanvas furnaceProgress )
    {
        String machineApk = (String) furnaceButton.getUserData();

        furnaceButtons.add( furnaceButton );
        furnaceProgresses.add( furnaceProgress );

        furnaceButton.setButtonStyle( BIG_BUTTON_STYLE );
        furnaceStatus.connect( injector, machineApk );
        furnaceProgress.connect( injector, machineApk );
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
