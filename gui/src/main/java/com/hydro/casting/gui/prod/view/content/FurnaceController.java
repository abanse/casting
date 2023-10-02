package com.hydro.casting.gui.prod.view.content;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.Casting;
import com.hydro.casting.common.constant.CasterStep;
import com.hydro.casting.common.constant.FurnaceStep;
import com.hydro.casting.gui.downtime.control.DowntimeChart;
import com.hydro.casting.gui.downtime.dialog.DowntimePopOver;
import com.hydro.casting.gui.downtime.dialog.ResolveDowntimePopOver;
import com.hydro.casting.gui.model.CastingClientModel;
import com.hydro.casting.gui.prod.control.*;
import com.hydro.casting.gui.prod.detail.FurnaceDetailController;
import com.hydro.casting.gui.prod.table.ChargingAnalysisTable;
import com.hydro.casting.gui.prod.table.ChargingTable;
import com.hydro.casting.gui.prod.table.FurnaceContentTable;
import com.hydro.casting.gui.prod.task.*;
import com.hydro.casting.gui.reporting.data.ReportingCacheDataProvider;
import com.hydro.casting.gui.task.ValidateMachineTask;
import com.hydro.casting.gui.util.TaskButtonUtil;
import com.hydro.casting.server.contract.downtime.dto.DowntimeDTO;
import com.hydro.casting.server.contract.dto.*;
import com.hydro.casting.server.contract.prod.ProcessDocuView;
import com.hydro.casting.server.contract.reporting.dto.ReportingGaugeSummaryDTO;
import com.hydro.core.common.cache.ClientCache;
import com.hydro.core.common.util.DateTimeUtil;
import com.hydro.core.common.util.StringTools;
import com.hydro.core.gui.*;
import com.hydro.core.gui.comp.SimpleSelectionProvider;
import com.hydro.core.gui.comp.StringTextField;
import com.hydro.core.gui.comp.TaskButton;
import com.hydro.core.gui.comp.gauge.KPIGaugeView;
import com.hydro.core.gui.model.ClientModel;
import com.hydro.core.gui.task.OpenDetailViewTask;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import org.apache.commons.collections4.CollectionUtils;
import org.controlsfx.control.MasterDetailPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class FurnaceController
{
    private final static Logger log = LoggerFactory.getLogger( FurnaceController.class );
    private final static DateFormat ANALYSIS_TF = new SimpleDateFormat( "HH:mm:ss" );

    @Inject
    private ViewManager viewManager;
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
    private ReportingCacheDataProvider dataProvider;
    @Inject
    private ValidateMachineTask<FurnaceInstructionDTO> validateFurnaceTask;

    @FXML
    private KPIOutputControl outputGauge;
    @FXML
    private KPIGaugeView downtimeGauge;
    @FXML
    private DowntimeChart downtimeChart;
    @FXML
    private TitledPane standard;
    @FXML
    private TaskButton showDetails;
    @FXML
    private MasterDetailPane detailPane;
    @FXML
    private TaskButton furnaceLeftTaskButton;
    @FXML
    private TaskButton casterTaskButton;
    @FXML
    private TaskButton furnaceRightTaskButton;

    @FXML
    private EquipmentStatus furnaceLeftStatus;
    @FXML
    private EquipmentStatus casterStatus;
    @FXML
    private EquipmentStatus furnaceRightStatus;

    @FXML
    private ProgressCanvas furnaceLeftProgress;
    @FXML
    private ProgressCanvas casterProgress;
    @FXML
    private ProgressCanvas furnaceRightProgress;

    @FXML
    private FurnaceInstructionControlController furnaceInstructionController;

    @FXML
    private ChargingAnalysisTable chargingAnalysisTable;
    @FXML
    private ChargingTable chargingTable;
    @FXML
    private FurnaceContentTable furnaceContentTable;
    @FXML
    private StringTextField amount;

    @FXML
    private OpenDetailViewTask openActivateChargeTask;

    @FXML
    private OpenDetailViewTask openMabeInfoTask;

    @FXML
    private CleanFurnaceTask cleanFurnaceTask;

    @FXML
    private RequestAnalyseTask requestAnalyseTask;

    @FXML
    private DeactivateChargeTask deactivateChargeTask;

    @FXML
    private OpenFurnaceStepDetailTask openChargingFurnaceNonLiquidTask;
    @FXML
    private OpenFurnaceStepDetailTask openChargingFurnaceLiquidTask;
    @FXML
    private OpenFurnaceStepDetailTask openTreatingFurnaceTask;
    @FXML
    private SkimmingFurnaceTask skimmingFurnaceTask;
    @FXML
    private RestingFurnaceTask restingFurnaceTask;
    @FXML
    private ReleaseFurnaceTask releaseFurnaceTask;
    @FXML
    private OpenDetailViewTask openCastingPreparationTask;

    @FXML
    private FurnaceDetailController detailsController;

    @FXML
    private TimeManagementListView timeManagementListView;

    @FXML
    private Label restingTimeLabel;

    @FXML
    private DowntimeRequestButton downtimeRequestButton;

    private String machine;
    private String currentCharge;
    private boolean initialized = false;

    private ClientCache<FurnaceInstructionDTO> furnaceInstructionCache;

    private BooleanProperty showDetailsSelected = new SimpleBooleanProperty( false );

    private final ScheduledExecutorService repScheduler = Executors.newScheduledThreadPool( 1 );
    private ScheduledFuture<Void> repScheduleFuture;

    private ObservableList<TimeManagementViolationDTO> timeManagementViolations = FXCollections.observableArrayList();
    private Timer timer;
    private Runnable timeManagementRunnable = this::reloadTimeManagement;

    private ClientModel clientModel;

    private ProcessDocuDTO processDocuDTO = new ProcessDocuDTO();

    public void initialize( String machine )
    {
        this.machine = machine;

        injector.injectMembers( chargingAnalysisTable );
        injector.injectMembers( furnaceContentTable );

        furnaceContentTable.amountProperty().addListener( ( v, o, n ) -> {
            if ( n == null || n.doubleValue() == 0 )
            {
                amount.setText( null );
            }
            else
            {
                amount.setText( StringTools.N02F.format( n.doubleValue() / 1000.0 ) + "t" );
            }
        } );

        furnaceLeftTaskButton.setText( "Ofen " + machine.charAt( 0 ) + "1" );
        furnaceLeftTaskButton.setDisable( machine.endsWith( "1" ), null );

        casterTaskButton.setText( "Gießanlage " + machine.charAt( 0 ) + "0" );
        casterTaskButton.setDisable( false, null );

        furnaceRightTaskButton.setText( "Ofen " + machine.charAt( 0 ) + "2" );
        furnaceRightTaskButton.setDisable( machine.endsWith( "2" ), null );

        furnaceLeftStatus.connect( injector, machine.charAt( 0 ) + "1" );
        casterStatus.connect( injector, machine.charAt( 0 ) + "0" );
        furnaceRightStatus.connect( injector, machine.charAt( 0 ) + "2" );

        furnaceLeftProgress.connect( injector, machine.charAt( 0 ) + "1" );
        casterProgress.connect( injector, machine.charAt( 0 ) + "0" );
        furnaceRightProgress.connect( injector, machine.charAt( 0 ) + "2" );

        outputGauge.init( machine, null, injector );

        downtimeChart.setCachePaths( new String[] { "/downtime/" + machine } );
        downtimeChart.connect( cacheManager );

        chargingAnalysisTable.connect( cacheManager );

        downtimeRequestButton.connect( injector, Casting.getCasterForMeltingFurnace( machine ) );

        if ( outputGauge != null && downtimeGauge != null )
        {
            String dtoPath = Casting.CACHE.REPORTING_SUMMARY_GAUGE_KEY + "/" + machine + Casting.REPORTING.CURRENT_SHIFT;
            cacheManager.addCacheListener( Casting.CACHE.REPORTING_CACHE_NAME, dtoPath, ( key ) -> {
                if ( repScheduleFuture != null && !repScheduleFuture.isDone() )
                {
                    return;
                }
                repScheduleFuture = repScheduler.schedule( () -> {
                    Platform.runLater( this::setGaugeValues );
                    return null;
                }, 1, TimeUnit.SECONDS );
            } );
        }

        openActivateChargeTask.setStartObjectCallback( param -> {
            final FurnaceInstructionDTO furnaceInstructionDTO = new FurnaceInstructionDTO();
            furnaceInstructionDTO.setFurnace( machine );
            return furnaceInstructionDTO;
        } );
        openMabeInfoTask.setStartObjectCallback( param -> machine.charAt( 0 ) + "0" );

        openCastingPreparationTask.setDetailProvider( ProcessDocuView.class, CastingPreparationDTO.class );
        openCastingPreparationTask.setTable( new SimpleSelectionProvider<ProcessDocuDTO>()
        {
            @Override
            public ProcessDocuDTO getSelectedValue()
            {
                return processDocuDTO;
            }
        } );

        timeManagementListView.setTimeManagementViolations( timeManagementViolations );
        timeManagementListView.setOnMouseClicked( event -> {
            MachineDTO machineDTO = null;
            if ( clientModel != null )
            {
                machineDTO = clientModel.getEntity( MachineDTO.class, machine );
            }
            if ( machineDTO != null && ( machineDTO.getActiveDowntime() != null || machineDTO.getCurrentStepDowntime() != null ) )
            {
                return;
            }

            final Optional<TimeManagementViolationDTO> timeManagementViolationOptional = timeManagementViolations.stream().findFirst();
            DowntimeDTO suggestionDTO = null;
            if ( timeManagementViolationOptional.isPresent() )
            {
                suggestionDTO = new DowntimeDTO();
                final TimeManagementViolationDTO timeManagementViolationDTO = timeManagementViolationOptional.get();
                final StringBuilder descriptionSB = new StringBuilder();
                final LocalDateTime downtimeStart;
                if ( "Start".equals( timeManagementViolationDTO.getType() ) )
                {
                    descriptionSB.append( "Verspätung " );
                    downtimeStart = timeManagementViolationDTO.getPlannedStart();
                }
                else
                {
                    descriptionSB.append( "Verzögerung " );
                    downtimeStart = timeManagementViolationDTO.getPlannedEnd();
                }
                String phaseDescription = timeManagementViolationDTO.getName();
                final CasterStep casterStep = CasterStep.findByShortName( timeManagementViolationDTO.getName() );
                if ( casterStep != null )
                {
                    phaseDescription = casterStep.getDescription();
                }
                else
                {
                    final FurnaceStep furnaceStep = FurnaceStep.findByShortName( timeManagementViolationDTO.getName() );
                    if ( furnaceStep != null )
                    {
                        phaseDescription = furnaceStep.getDescription();
                    }
                }
                descriptionSB.append( phaseDescription ).append( " " );
                if ( timeManagementViolationDTO.getCharge() != null && timeManagementViolationDTO.getCharge().length() >= 7 )
                {
                    descriptionSB.append( "Charge " ).append( timeManagementViolationDTO.getCharge().substring( 2 ) );
                }
                suggestionDTO.setFromTS( downtimeStart );
                suggestionDTO.setDescription( descriptionSB.toString() );
            }
            final String[] availableCostCenters = Casting.getDowntimeCostCenters( machine );
            downtimePopOver.setDataForCostCenter( machine, availableCostCenters, suggestionDTO );
            downtimePopOver.executeDowntimeLoaderTask();
            downtimePopOver.showPopOver( timeManagementListView, false );
        } );

        furnaceInstructionCache = cacheManager.getCache( Casting.CACHE.PROD_CACHE_NAME );
        cacheManager.addCacheListener( Casting.CACHE.PROD_CACHE_NAME, Casting.CACHE.FURNACE_INSTRUCTION_DATA_PATH + "/data/" + Objects.hash( machine ), key -> reloadFurnaceInstruction() );
        cacheManager.addCacheListener( Casting.CACHE.PLANNING_CACHE_NAME, Casting.CACHE.MACHINE_DATA_PATH + "/data/" + Objects.hash( machine ), key -> reloadFurnaceInstruction() );
        reloadFurnaceInstruction();

        detailsController.setMachineApk( machine );
        cleanFurnaceTask.setMachine( machine );
        requestAnalyseTask.setMachine( machine );
        openChargingFurnaceLiquidTask.setMachine( machine );
        openChargingFurnaceNonLiquidTask.setMachine( machine );
        openTreatingFurnaceTask.setMachine( machine );
        skimmingFurnaceTask.setMachine( machine );
        restingFurnaceTask.setMachine( machine );
        releaseFurnaceTask.setMachine( machine );
        deactivateChargeTask.setMachine( machine );

        detailsController.activeProperty().bind( showDetailsSelected );

        final ClientModelManager clientModelManager = injector.getInstance( ClientModelManager.class );
        clientModel = clientModelManager.getClientModel( CastingClientModel.ID );
        clientModel.addRelationListener( CastingClientModel.MACHINE, observable -> reloadTimeManagement() );
        reloadTimeManagement();
    }

    public void beforeShown( View view )
    {
        if ( !initialized )
        {
            validateFurnaceTask.setViewContext( view.getViewContext() );
            TaskButtonUtil.registerTasks( view, injector, taskManager, validateFurnaceTask );
            downtimeChart.start();
            initGauges();
            initialized = true;
        }
    }

    public void activateView( View view )
    {
        downtimeChart.start();
        furnaceLeftProgress.start();
        casterProgress.start();
        furnaceRightProgress.start();
        chargingAnalysisTable.setActive( true );

        reloadTimeManagement();
        reloadFurnaceInstruction();

        if ( timer != null )
        {
            // is running
            return;
        }
        timer = new Timer( "timeManagementFurnace" + machine, true );
        timer.schedule( new TimeManagementTimerTask(), 1000, 1000 );
    }

    public void deactivateView( View view )
    {
        downtimeChart.stop();
        furnaceLeftProgress.stop();
        casterProgress.stop();
        furnaceRightProgress.stop();
        chargingAnalysisTable.setActive( false );

        if ( timer != null )
        {
            timer.cancel();
            timer = null;
        }
    }

    private void initGauges()
    {
        if ( downtimeGauge == null || outputGauge == null )
        {
            return;
        }
        Tooltip.install( downtimeGauge, new Tooltip( "Störzeiten [%]\naktuelle Schicht" ) );
        outputGauge.init( machine, dataProvider, injector );
        setGaugeValues();
    }

    private void setGaugeValues()
    {
        ReportingGaugeSummaryDTO summaryDTO = dataProvider.getGaugeSummary( machine, Casting.REPORTING.CURRENT_SHIFT );
        if ( summaryDTO != null )
        {
            downtimeGauge.setValue( summaryDTO.getDowntimeValue() );
            outputGauge.setGaugeValues();
        }
    }

    @FXML
    private void openFurnaceLeft( ActionEvent actionEvent )
    {
        viewManager.openView( "casting.prod.furnace-" + machine.charAt( 0 ) + "1.view" );
    }

    @FXML
    private void openCaster( ActionEvent actionEvent )
    {
        viewManager.openView( "casting.prod.caster-" + machine.charAt( 0 ) + "0.view" );
    }

    @FXML
    private void openFurnaceRight( ActionEvent actionEvent )
    {
        viewManager.openView( "casting.prod.furnace-" + machine.charAt( 0 ) + "2.view" );
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

            detailsController.reloadProductionLog();
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

    @FXML
    public void createDowntime( ActionEvent actionEvent )
    {
        final String[] availableCostCenters = Casting.getDowntimeCostCenters( machine );
        downtimePopOver.setDataForCostCenter( machine, availableCostCenters );
        downtimePopOver.executeDowntimeLoaderTask();
        downtimePopOver.showPopOver( (Node) actionEvent.getSource() );
    }

    @FXML
    public void resolveDowntimes( ActionEvent actionEvent )
    {
        final String[] availableCostCenters = Casting.getDowntimeCostCenters( machine );
        resolveDowntimePopOver.setDataForCostCenter( machine, availableCostCenters );
        resolveDowntimePopOver.loadDowntimeRequests();
        resolveDowntimePopOver.showPopOver( (Node) actionEvent.getSource() );
    }

    private void reloadFurnaceInstruction()
    {
        log.info( "reloadFurnaceInstruction" );

        final FurnaceInstructionDTO furnaceInstructionDTO = furnaceInstructionCache.get( Casting.CACHE.FURNACE_INSTRUCTION_DATA_PATH + "/data/" + Objects.hash( machine ) );
        furnaceInstructionController.load( furnaceInstructionDTO );
        cleanFurnaceTask.setFurnaceInstructionDTO( furnaceInstructionDTO );
        requestAnalyseTask.setFurnaceInstructionDTO( furnaceInstructionDTO );
        openChargingFurnaceLiquidTask.setFurnaceInstructionDTO( furnaceInstructionDTO );
        openChargingFurnaceNonLiquidTask.setFurnaceInstructionDTO( furnaceInstructionDTO );
        openTreatingFurnaceTask.setFurnaceInstructionDTO( furnaceInstructionDTO );
        skimmingFurnaceTask.setFurnaceInstructionDTO( furnaceInstructionDTO );
        restingFurnaceTask.setFurnaceInstructionDTO( furnaceInstructionDTO );
        releaseFurnaceTask.setFurnaceInstructionDTO( furnaceInstructionDTO );
        deactivateChargeTask.setFurnaceInstructionDTO( furnaceInstructionDTO );
        if ( furnaceInstructionDTO != null )
        {
            currentCharge = furnaceInstructionDTO.getCharge();
        }
        detailsController.setSelectedValue( furnaceInstructionDTO );

        if ( furnaceInstructionDTO != null && furnaceInstructionDTO.getCastingBatchOID() != null )
        {
            processDocuDTO.setId( furnaceInstructionDTO.getCastingBatchOID() );
        }
        else
        {
            processDocuDTO.setId( 0 );
        }

        chargingAnalysisTable.setCastingInstruction( furnaceInstructionDTO );

        furnaceContentTable.loadData( furnaceInstructionDTO );

        validateFurnaceTask.setData( furnaceInstructionDTO );
        taskManager.executeTask( validateFurnaceTask );
    }

    private void reloadTimeManagement()
    {
        MachineDTO machineDTO = null;
        if ( clientModel != null )
        {
            machineDTO = clientModel.getEntity( MachineDTO.class, machine );
        }
        if ( machineDTO == null || machineDTO.getCurrentStep() == null || machineDTO.getCurrentStepStartTS() == null || machineDTO.getCurrentStepDuration() <= 0 )
        {
            restingTimeLabel.setVisible( false );
            timeManagementViolations.clear();
            return;
        }
        final LocalDateTime start = machineDTO.getCurrentStepStartTS();
        final LocalDateTime normalEnd = start.plusMinutes( machineDTO.getCurrentStepDuration() );
        if ( FurnaceStep.findByShortName( machineDTO.getCurrentStep() ) == FurnaceStep.Resting && normalEnd.isAfter( LocalDateTime.now() ) )
        {
            long duration = normalEnd.toEpochSecond( ZoneOffset.UTC ) - LocalDateTime.now().toEpochSecond( ZoneOffset.UTC );
            restingTimeLabel.setText( "Abstehzeit noch : " + DateTimeUtil.formatDurationMinutes( duration ) );
            restingTimeLabel.setVisible( true );
        }
        else
        {
            restingTimeLabel.setVisible( false );
        }
        final List<TimeManagementViolationDTO> currentTimeManagementViolations = new ArrayList<>();
        if ( normalEnd.isBefore( LocalDateTime.now() ) )
        {
            TimeManagementViolationDTO timeManagementViolationDTO = new TimeManagementViolationDTO();
            timeManagementViolationDTO.setName( machineDTO.getCurrentStep() );
            timeManagementViolationDTO.setCharge( currentCharge );
            timeManagementViolationDTO.setPlannedEnd( normalEnd );
            timeManagementViolationDTO.setType( "Ende" );
            if ( StringTools.isFilled( machineDTO.getActiveDowntime() ) || StringTools.isFilled( machineDTO.getCurrentStepDowntime() ) )
            {
                timeManagementViolationDTO.setChecked( true );
            }
            currentTimeManagementViolations.add( timeManagementViolationDTO );
        }
        if ( !CollectionUtils.isEqualCollection( timeManagementViolations, currentTimeManagementViolations ) )
        {
            timeManagementViolations.setAll( currentTimeManagementViolations );
            if ( !timeManagementViolations.isEmpty() )
            {
                reloadFurnaceInstruction();
            }
        }
    }

    private class TimeManagementTimerTask extends TimerTask
    {
        @Override
        public void run()
        {
            Platform.runLater( timeManagementRunnable );
        }
    }

}
