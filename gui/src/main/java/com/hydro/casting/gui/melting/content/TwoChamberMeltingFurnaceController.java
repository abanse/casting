package com.hydro.casting.gui.melting.content;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.Casting;
import com.hydro.casting.common.constant.MelterStep;
import com.hydro.casting.gui.downtime.DowntimeUtil;
import com.hydro.casting.gui.downtime.control.DowntimeChart;
import com.hydro.casting.gui.downtime.dialog.DowntimePopOver;
import com.hydro.casting.gui.downtime.dialog.ResolveDowntimePopOver;
import com.hydro.casting.gui.melting.control.MeltingFurnaceInstructionControlController;
import com.hydro.casting.gui.melting.control.MeltingProcessStepStatus;
import com.hydro.casting.gui.melting.detail.MelterDetailController;
import com.hydro.casting.gui.melting.task.*;
import com.hydro.casting.gui.prod.control.DowntimeRequestButton;
import com.hydro.casting.gui.prod.control.EquipmentStatus;
import com.hydro.casting.gui.prod.table.ChargingAnalysisTable;
import com.hydro.casting.gui.task.ValidateMachineTask;
import com.hydro.casting.gui.util.TaskButtonUtil;
import com.hydro.casting.server.contract.dto.MeltingInstructionDTO;
import com.hydro.core.common.cache.ClientCache;
import com.hydro.core.gui.CacheManager;
import com.hydro.core.gui.ImagesCore;
import com.hydro.core.gui.TaskManager;
import com.hydro.core.gui.View;
import com.hydro.core.gui.comp.TaskButton;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import org.controlsfx.control.MasterDetailPane;
import org.controlsfx.control.ToggleSwitch;

import java.util.Objects;

public class TwoChamberMeltingFurnaceController
{
    private final static String ALTERNATIVE_ALLOY = "10/05";
    // FXML elements
    @FXML
    private MeltingFurnaceInstructionControlController meltingFurnaceInstructionControlController;
    @FXML
    private MasterDetailPane detailPane;
    @FXML
    private TaskButton showDetails;
    @FXML
    private ToggleSwitch altAnalysisToggle;
    @FXML
    private EquipmentStatus mainChamberStatus;
    @FXML
    private MeltingProcessStepStatus meltingStatus;
    @FXML
    private MeltingProcessStepStatus mainChamberSkimmingStatus;
    @FXML
    private MeltingProcessStepStatus meltingChamberSkimmingStatus;
    @FXML
    private MeltingProcessStepStatus mixingStatus;
    @FXML
    private MeltingProcessStepStatus pouringStatus;
    @FXML
    private ChargingAnalysisTable chargingAnalysisTable;
    @FXML
    private DowntimeChart downtimeChart;
    @FXML
    private DowntimeRequestButton downtimeRequestButton;
    @FXML
    private TitledPane standard;
    @FXML
    private CreateNewMeltingChargeTask createNewMeltingChargeTask;
    @FXML
    private ActivateNextMeltingChargeTask activateNextMeltingChargeTask;
    @FXML
    private StartSkimmingTask startMainChamberSkimmingTask;
    @FXML
    private EndSkimmingTask endMainChamberSkimmingTask;
    @FXML
    private StartSkimmingMeltingChamberTask startSkimmingMeltingChamberTask;
    @FXML
    private EndSkimmingMeltingChamberTask endSkimmingMeltingChamberTask;
    @FXML
    private StartMixingTask startMixingTask;
    @FXML
    private EndMixingTask endMixingTask;
    @FXML
    private FinishChargeTask finishChargeTask;
    @FXML
    private DeactivateChargeTask deactivateChargeTask;
    @FXML
    private ConfigureNewMeltingChargeTask configureNewMeltingChargeTask;
    @FXML
    private MelterDetailController detailsController;

    // Injections
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

    private String machine;
    private boolean initialized = false;
    private ClientCache<MeltingInstructionDTO> meltingInstructionCache;
    private final BooleanProperty showDetailsSelected = new SimpleBooleanProperty( false );

    public void initialize( String machine )
    {
        this.machine = machine;

        // Injecting and initializing sub-elements
        injector.injectMembers( chargingAnalysisTable );

        // Connecting elements if necessary
        mainChamberStatus.connect( injector, this.machine );
        meltingStatus.connect( injector, MelterStep.Melting );
        mainChamberSkimmingStatus.connect( injector, MelterStep.Skimming );
        meltingChamberSkimmingStatus.connect( injector, MelterStep.SkimmingMeltingChamber );
        mixingStatus.connect( injector, MelterStep.Mixing );
        pouringStatus.connect( injector, MelterStep.Pouring );
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

        // Setting task data
        createNewMeltingChargeTask.setMachineApk( machine );
        activateNextMeltingChargeTask.setMachineApk( machine );

        startMainChamberSkimmingTask.setMachineApk( machine );
        startMainChamberSkimmingTask.setWithoutMachine( true );
        endMainChamberSkimmingTask.setMachineApk( machine );
        endMainChamberSkimmingTask.setWithoutMachine( true );
        startSkimmingMeltingChamberTask.setMachineApk( machine );
        startSkimmingMeltingChamberTask.setWithoutMachine( true );
        endSkimmingMeltingChamberTask.setMachineApk( machine );
        endSkimmingMeltingChamberTask.setWithoutMachine( true );
        startMixingTask.setMachineApk( machine );
        startMixingTask.setWithoutMachine( true );
        endMixingTask.setMachineApk( machine );
        endMixingTask.setWithoutMachine( true );
        deactivateChargeTask.setMachine( machine );
        configureNewMeltingChargeTask.setMachine( machine );
        finishChargeTask.setMachine( machine );

        detailsController.activeProperty().bind( showDetailsSelected );
        altAnalysisToggle.selectedProperty().addListener( ( observable, oldValue, newValue ) -> {
            if ( newValue )
            {
                chargingAnalysisTable.altAlloyProperty().set( ALTERNATIVE_ALLOY );
            }
            else
            {
                chargingAnalysisTable.altAlloyProperty().set( null );
            }
            chargingAnalysisTable.reload();
        } );

        reloadMeltingInstructionDTO();
    }

    public void beforeShown( View view )
    {
        if ( !initialized )
        {
            validateMachineTask.setViewContext( view.getViewContext() );
            TaskButtonUtil.registerTasks( view, injector, taskManager, validateMachineTask );
            downtimeChart.start();
            initialized = true;
        }
    }

    public void activateView( View view )
    {
        downtimeChart.start();
        chargingAnalysisTable.setActive( true );
    }

    public void deactivateView( View view )
    {
        downtimeChart.stop();
        chargingAnalysisTable.setActive( false );
    }

    @FXML
    private void createDowntime( ActionEvent actionEvent )
    {
        final String[] availableCostCenters = Casting.getDowntimeCostCenters( machine );
        downtimePopOver.setDataForCostCenter( machine, availableCostCenters );
        downtimePopOver.executeDowntimeLoaderTask();
        downtimePopOver.showPopOver( (Node) actionEvent.getSource() );
    }

    @FXML
    private void resolveDowntimes( ActionEvent actionEvent )
    {
        final String[] availableCostCenters = Casting.getDowntimeCostCenters( machine );
        resolveDowntimePopOver.setDataForCostCenter( machine, availableCostCenters );
        resolveDowntimePopOver.loadDowntimeRequests();
        resolveDowntimePopOver.showPopOver( (Node) actionEvent.getSource() );
    }

    private void reloadMeltingInstructionDTO()
    {
        MeltingInstructionDTO meltingInstructionDTO = meltingInstructionCache.get( Casting.CACHE.MELTING_INSTRUCTION_DATA_PATH + Objects.hash( machine ) );

        chargingAnalysisTable.setMeltingInstructionDTO( meltingInstructionDTO );
        activateNextMeltingChargeTask.setMeltingInstructionDTO( meltingInstructionDTO );
        startMainChamberSkimmingTask.setMeltingInstructionDTO( meltingInstructionDTO );
        startSkimmingMeltingChamberTask.setMeltingInstructionDTO( meltingInstructionDTO );
        endMainChamberSkimmingTask.setMeltingInstructionDTO( meltingInstructionDTO );
        endSkimmingMeltingChamberTask.setMeltingInstructionDTO( meltingInstructionDTO );
        startMixingTask.setMeltingInstructionDTO( meltingInstructionDTO );
        endMixingTask.setMeltingInstructionDTO( meltingInstructionDTO );
        deactivateChargeTask.setMeltingInstructionDTO( meltingInstructionDTO );
        finishChargeTask.setMeltingInstructionDTO( meltingInstructionDTO );

        meltingFurnaceInstructionControlController.load( meltingInstructionDTO );
        detailsController.setSelectedValue( meltingInstructionDTO );

        meltingStatus.setMeltingInstructionDTO( meltingInstructionDTO );
        mainChamberSkimmingStatus.setMeltingInstructionDTO( meltingInstructionDTO );
        meltingChamberSkimmingStatus.setMeltingInstructionDTO( meltingInstructionDTO );
        mixingStatus.setMeltingInstructionDTO( meltingInstructionDTO );
        pouringStatus.setMeltingInstructionDTO( meltingInstructionDTO );
        reloadProcessStepStatus();

        validateMachineTask.setData( meltingInstructionDTO );
        taskManager.executeTask( validateMachineTask );
    }

    private void reloadProcessStepStatus()
    {
        meltingStatus.reload();
        mainChamberSkimmingStatus.reload();
        meltingChamberSkimmingStatus.reload();
        mixingStatus.reload();
        pouringStatus.reload();
    }

    @FXML
    public void showDetails( ActionEvent actionEvent )
    {
        showDetailsSelected.set( !showDetailsSelected.get() );
        ImageView imageView;
        if ( showDetailsSelected.get() )
        {
            detailPane.setShowDetailNode( true );
            imageView = new ImageView( ImagesCore.EXPAND.load() );
        }
        else
        {
            detailPane.setShowDetailNode( false );
            imageView = new ImageView( ImagesCore.COLLAPSE.load() );
        }
        imageView.setFitHeight( 21.0 );
        imageView.setFitWidth( 21.0 );
        showDetails.setGraphic( imageView );
    }
}
