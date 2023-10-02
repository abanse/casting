package com.hydro.casting.gui.melting.content;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.Casting;
import com.hydro.casting.gui.downtime.DowntimeUtil;
import com.hydro.casting.gui.downtime.control.DowntimeChart;
import com.hydro.casting.gui.downtime.dialog.DowntimePopOver;
import com.hydro.casting.gui.downtime.dialog.ResolveDowntimePopOver;
import com.hydro.casting.gui.melting.control.MeltingFurnaceInstructionControlController;
import com.hydro.casting.gui.melting.task.*;
import com.hydro.casting.gui.prod.control.DowntimeRequestButton;
import com.hydro.casting.gui.prod.control.EquipmentStatus;
import com.hydro.casting.gui.prod.control.ProgressCanvas;
import com.hydro.casting.gui.prod.control.TimeManagementListView;
import com.hydro.casting.gui.prod.table.ChargingAnalysisTable;
import com.hydro.casting.gui.task.ValidateMachineTask;
import com.hydro.casting.gui.util.TaskButtonUtil;
import com.hydro.casting.server.contract.dto.MeltingInstructionDTO;
import com.hydro.core.common.cache.ClientCache;
import com.hydro.core.gui.CacheManager;
import com.hydro.core.gui.TaskManager;
import com.hydro.core.gui.View;
import com.hydro.core.gui.task.OpenDetailViewTask;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class MeltingFurnaceController
{
    private final static Logger log = LoggerFactory.getLogger( MeltingFurnaceController.class );

    @Inject
    private CacheManager cacheManager;
    @Inject
    private TaskManager taskManager;
    @Inject
    private Injector injector;
    @Inject
    private DowntimePopOver downtimePopOver;
    @Inject
    private ResolveDowntimePopOver resolveDowntimePopOver;
    @Inject
    private ValidateMachineTask<MeltingInstructionDTO> validateMachineTask;

    @FXML
    private MeltingFurnaceInstructionControlController meltingFurnaceInstructionControlController;
    @FXML
    private TitledPane standard;
    @FXML
    private EquipmentStatus currentStep;
    @FXML
    private ProgressCanvas currentStepProgress;
    @FXML
    private ChargingAnalysisTable chargingAnalysisTable;
    @FXML
    private DowntimeChart downtimeChart;
    @FXML
    private TimeManagementListView timeManagementListView;
    @FXML
    private OpenDetailViewTask<MeltingInstructionDTO> openActivateChargeTask;
    @FXML
    private DeactivateChargeTask deactivateChargeTask;
    @FXML
    private ConfigureNewMeltingChargeTask configureNewMeltingChargeTask;
    @FXML
    private StartChargingTask startChargingTask;
    @FXML
    private StartMeltingTask startMeltingTask;
    @FXML
    private StartSkimmingTask startSkimmingTask;
    @FXML
    private StartTreatingTask startTreatingTask;
    @FXML
    private StartHeatingTask startHeatingTask;
    @FXML
    private StartPouringTask startPouringTask;
    @FXML
    private StartDredgingTask startDredgingTask;
    @FXML
    private FinishChargeTask finishChargeTask;
    @FXML
    private DowntimeRequestButton downtimeRequestButton;

    private String machine;
    private ClientCache<MeltingInstructionDTO> meltingInstructionCache;
    private boolean initialized = false;

    public void initialize( String machine )
    {
        this.machine = machine;

        // Injecting sub-elements
        injector.injectMembers( chargingAnalysisTable );
        
        // Connecting elements if necessary
        currentStepProgress.connect( injector, this.machine );
        currentStep.connect( injector, this.machine );
        downtimeRequestButton.connect( injector, machine );
        chargingAnalysisTable.connect( cacheManager );
        // downtimeChart needs the cache paths before connecting to the caches
        downtimeChart.setCachePaths( DowntimeUtil.getDowntimeCachePaths( machine ) );
        downtimeChart.connect( cacheManager );

        meltingInstructionCache = cacheManager.getCache( Casting.CACHE.PROD_CACHE_NAME );
        // Listener on melting instruction cache to reload the melting instruction when it changes or is replaced (e.g. when a new melting instruction is created)
        cacheManager.addCacheListener( Casting.CACHE.PROD_CACHE_NAME, Casting.CACHE.MELTING_INSTRUCTION_DATA_PATH + Objects.hash( machine ), key -> reloadMeltingInstructionDTO() );
        // Listener on machine cache to rerun the validations (included in melting instruction reload) when the machine entity changes (e.g. new process step set)
        cacheManager.addCacheListener( Casting.CACHE.PLANNING_CACHE_NAME, Casting.CACHE.MELTING_MACHINE_DATA_PATH + Objects.hash( machine ), key -> reloadMeltingInstructionDTO() );

        openActivateChargeTask.setStartObjectCallback( param -> {
            final MeltingInstructionDTO meltingInstructionDTO = new MeltingInstructionDTO();
            meltingInstructionDTO.setMachine( machine );
            return meltingInstructionDTO;
        } );

        startSkimmingTask.setMachineApk( machine );
        startPouringTask.setMachineApk( machine );
        deactivateChargeTask.setMachine( machine );
        configureNewMeltingChargeTask.setMachine( machine );
        finishChargeTask.setMachine( machine );

        // Initial load of the local furnace instruction copy
        reloadMeltingInstructionDTO();
    }

    public void beforeShown( View view )
    {
        if ( !initialized )
        {
            validateMachineTask.setViewContext( view.getViewContext() );
            TaskButtonUtil.registerTasks( view, injector, taskManager, validateMachineTask );
            downtimeChart.start();
            currentStepProgress.start();
            initialized = true;
        }
    }

    public void activateView( View view )
    {
        downtimeChart.start();
        currentStepProgress.start();
        chargingAnalysisTable.setActive( true );
    }

    public void deactivateView( View view )
    {
        downtimeChart.stop();
        currentStepProgress.stop();
        chargingAnalysisTable.setActive( false );
    }

    @FXML
    public void createDowntime( ActionEvent actionEvent )
    {
        final String[] availableCostCenters = Casting.getDowntimeCostCenters( machine );
        downtimePopOver.setDataForCostCenter( machine, availableCostCenters );
        downtimePopOver.executeDowntimeLoaderTask();
        downtimePopOver.showPopOver( (Node) actionEvent.getSource() );
    }

    private void reloadMeltingInstructionDTO()
    {
        MeltingInstructionDTO meltingInstructionDTO = meltingInstructionCache.get( Casting.CACHE.MELTING_INSTRUCTION_DATA_PATH + Objects.hash( machine ) );

        chargingAnalysisTable.setMeltingInstructionDTO( meltingInstructionDTO );
        deactivateChargeTask.setMeltingInstructionDTO( meltingInstructionDTO );
        finishChargeTask.setMeltingInstructionDTO( meltingInstructionDTO );
        startChargingTask.setMeltingInstructionDTO( meltingInstructionDTO );
        startMeltingTask.setMeltingInstructionDTO( meltingInstructionDTO );
        startSkimmingTask.setMeltingInstructionDTO( meltingInstructionDTO );
        startTreatingTask.setMeltingInstructionDTO( meltingInstructionDTO );
        startHeatingTask.setMeltingInstructionDTO( meltingInstructionDTO );
        startPouringTask.setMeltingInstructionDTO( meltingInstructionDTO );
        startDredgingTask.setMeltingInstructionDTO( meltingInstructionDTO );

        meltingFurnaceInstructionControlController.load( meltingInstructionDTO );

        validateMachineTask.setData( meltingInstructionDTO );
        taskManager.executeTask( validateMachineTask );
    }

    public void resolveDowntimes( ActionEvent actionEvent )
    {
        final String[] availableCostCenters = Casting.getDowntimeCostCenters( machine );
        resolveDowntimePopOver.setDataForCostCenter( machine, availableCostCenters );
        resolveDowntimePopOver.loadDowntimeRequests();
        resolveDowntimePopOver.showPopOver( (Node) actionEvent.getSource() );
    }
}
