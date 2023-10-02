package com.hydro.casting.gui.prod.view.content;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.Casting;
import com.hydro.casting.gui.downtime.DowntimeUtil;
import com.hydro.casting.gui.downtime.control.DowntimeChart;
import com.hydro.casting.gui.downtime.dialog.DowntimePopOver;
import com.hydro.casting.gui.downtime.dialog.ResolveDowntimePopOver;
import com.hydro.casting.gui.model.CastingClientModel;
import com.hydro.casting.gui.prod.control.CasterInstructionControlController;
import com.hydro.casting.gui.prod.control.CasterOccupancyControlController;
import com.hydro.casting.gui.prod.control.DowntimeRequestButton;
import com.hydro.casting.gui.prod.detail.CasterDetailController;
import com.hydro.casting.gui.prod.table.ChargingAnalysisTable;
import com.hydro.casting.gui.task.ValidateMachineTask;
import com.hydro.casting.gui.util.TaskButtonUtil;
import com.hydro.casting.server.contract.dto.*;
import com.hydro.casting.server.contract.prod.ProcessDocuView;
import com.hydro.core.common.cache.ClientCache;
import com.hydro.core.gui.*;
import com.hydro.core.gui.comp.SimpleSelectionProvider;
import com.hydro.core.gui.comp.TaskButton;
import com.hydro.core.gui.model.ClientModel;
import com.hydro.core.gui.task.OpenDetailViewTask;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.controlsfx.control.MasterDetailPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class CasterController
{
    private final static Logger log = LoggerFactory.getLogger( CasterController.class );
    private final static DateFormat ANALYSIS_TF = new SimpleDateFormat( "HH:mm:ss" );

    @Inject
    private ViewManager viewManager;
    @Inject
    private CacheManager cacheManager;
    @Inject
    private ClientModelManager clientModelManager;
    @Inject
    private Injector injector;
    @Inject
    private DowntimePopOver downtimePopOver;
    @Inject
    private ResolveDowntimePopOver resolveDowntimePopOver;
    //    @Inject
    //    private ReportingCacheDataProvider dataProvider;
    @Inject
    private TaskManager taskManager;
    @Inject
    private ValidateMachineTask<CasterInstructionDTO> validateCasterTask;

    //    @FXML
    //    private KPIOutputControl outputGauge;
    //    @FXML
    //    private KPIGaugeView downtimeGauge;
    //    @FXML
    //    private DigitalCounterView counter;
    @FXML
    private DowntimeChart downtimeChart;
    @FXML
    private TitledPane standard;
    @FXML
    private TaskButton showDetails;
    @FXML
    private MasterDetailPane detailPane;
    //    @FXML
    //    private TaskButton furnaceLeftTaskButton;
    //    @FXML
    //    private TaskButton casterTaskButton;
    //    @FXML
    //    private TaskButton furnaceRightTaskButton;

    //    @FXML
    //    private EquipmentStatus furnaceLeftStatus;
    //    @FXML
    //    private EquipmentStatus casterStatus;
    //    @FXML
    //    private EquipmentStatus furnaceRightStatus;

    //    @FXML
    //    private ProgressCanvas furnaceLeftProgress;
    //    @FXML
    //    private ProgressCanvas casterProgress;
    //    @FXML
    //    private ProgressCanvas furnaceRightProgress;

    @FXML
    private ChargingAnalysisTable chargingAnalysisTable;

    @FXML
    private CasterInstructionControlController casterInstructionController;

    @FXML
    private GridPane centerPane;

    @FXML
    private CasterDetailController detailsController;

    //    @FXML
    //    private TimeManagementListView timeManagementListView;

    //    @FXML
    //    private TaskButton startCasting;

    @FXML
    private OpenDetailViewTask openMabeInfoTask;

    @FXML
    private OpenDetailViewTask openCastingPreparationTask;

    @FXML
    private OpenDetailViewTask openVisualInspectionTask;

    //    @FXML
    //    private OpenDetailViewTask openUnloadCasterTask;

    //    @FXML
    //    private StartCastingTask startCastingTask;
    //    @FXML
    //    private EndCastingTask endCastingTask;
    //    @FXML
    //    private TaskButton cancelCastingButton;
    //    @FXML
    //    private TaskButton emergencyUnloadButton;
    @FXML
    private DowntimeRequestButton downtimeRequestButton;

    private CasterOccupancyControlController casterOccupancyControlController;

    private String machine;
    private String currentCharge;
    private boolean initialized = false;

    private ClientCache<CasterInstructionDTO> casterInstructionCache;
    private ClientModel castingModel;

    private BooleanProperty showDetailsSelected = new SimpleBooleanProperty( false );

    private final ScheduledExecutorService repScheduler = Executors.newScheduledThreadPool( 1 );
    private ScheduledFuture<Void> repScheduleFuture;

    private ObservableList<TimeManagementDTO> timeManagements = FXCollections.observableArrayList();
    //    private int testStep = 0;
    //    private int charge = 0;
    //    private List<String> testStepTimes = new ArrayList<>();
    //private ObservableList<TimeManagementViolationDTO> timeManagementViolations = FXCollections.observableArrayList();

    //    private CasterOccupancyDTO casterOccupancyDTO;

    private ClientModel clientModel;

    private ProcessDocuDTO processDocuDTO = new ProcessDocuDTO();
    private CasterOccupancyDTO casterOccupancyDTO = new CasterOccupancyDTO();

    //    private Timer timer;
    //    private Runnable timeManagementRunnable = () -> reloadTimeManagement();

    private CasterInstructionDTO casterInstructionDTO;

    public void initialize( String machine )
    {
        this.machine = machine;

        injector.injectMembers( chargingAnalysisTable );

        //        furnaceLeftTaskButton.setText( "Ofen " + machine.substring( 0, 1 ) + "1" );
        //        furnaceLeftTaskButton.setDisable( false, null );
        //
        //        casterTaskButton.setText( "Gießanlage " + machine );
        //        casterTaskButton.setDisable( true, null );
        //
        //        furnaceRightTaskButton.setText( "Ofen " + machine.substring( 0, 1 ) + "2" );
        //        furnaceRightTaskButton.setDisable( false, null );
        //
        //        furnaceLeftStatus.connect( injector, machine.substring( 0, 1 ) + "1" );
        //        casterStatus.connect( injector, machine );
        //        furnaceRightStatus.connect( injector, machine.substring( 0, 1 ) + "2" );
        //
        //        furnaceLeftProgress.connect( injector, machine.substring( 0, 1 ) + "1" );
        //        casterProgress.connect( injector, machine );
        //        furnaceRightProgress.connect( injector, machine.substring( 0, 1 ) + "2" );

        //        outputGauge.init( machine, null, injector );

        downtimeChart.setCachePaths( DowntimeUtil.getDowntimeCachePaths( machine ) );
        downtimeChart.connect( cacheManager );

        chargingAnalysisTable.connect( cacheManager );

        downtimeRequestButton.connect( injector, machine );

        // CasterMoldsOccupancy
        final String fxmlFileName;
        if ( machine.startsWith( "8" ) )
        {
            fxmlFileName = "/com/hydro/casting/gui/prod/control/CasterOccupancy5Control.fxml";
        }
        else
        {
            fxmlFileName = "/com/hydro/casting/gui/prod/control/CasterOccupancy4Control.fxml";
        }
        final FXMLLoader loader = new FXMLLoader( getClass().getResource( fxmlFileName ) );
        loader.setControllerFactory( injector::getInstance );
        Node node;
        try
        {
            node = loader.load();
            casterOccupancyControlController = loader.getController();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            node = new Label( "Fehler:" + e.getMessage() );
        }
        GridPane.setHalignment( node, HPos.CENTER );
        GridPane.setFillWidth( node, false );
        //((GridPane)node).setGridLinesVisible( true );
        final ScrollPane scrollPane = new ScrollPane( node );
        scrollPane.setFitToWidth( true );
        scrollPane.getStyleClass().clear();
        centerPane.add( scrollPane, 0, 5, 3, 1 );
        //centerPane.setGridLinesVisible( true );

        //        if ( outputGauge != null && downtimeGauge != null )
        //        {
        //            String dtoPath = Casting.CACHE.REPORTING_SUMMARY_GAUGE_KEY + "/" + machine + Casting.REPORTING.CURRENT_SHIFT;
        //            cacheManager.addCacheListener( Casting.CACHE.REPORTING_CACHE_NAME, dtoPath, ( key ) -> {
        //                if ( repScheduleFuture != null && repScheduleFuture.isDone() == false )
        //                {
        //                    return;
        //                }
        //                repScheduleFuture = repScheduler.schedule( () -> {
        //                    Platform.runLater( () -> {
        //                        setGaugeValues();
        //                    } );
        //                    return null;
        //                }, 1, TimeUnit.SECONDS );
        //            } );
        //        }

        /*
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
         */

        castingModel = clientModelManager.getClientModel( CastingClientModel.ID );

        casterInstructionCache = cacheManager.getCache( Casting.CACHE.PROD_CACHE_NAME );
        cacheManager.addCacheListener( Casting.CACHE.PROD_CACHE_NAME, Casting.CACHE.CASTER_INSTRUCTION_DATA_PATH + "/data/" + Objects.hash( machine ), key -> reloadCasterInstruction() );
        cacheManager.addCacheListener( Casting.CACHE.PLANNING_CACHE_NAME, Casting.CACHE.MACHINE_DATA_PATH + "/data/" + Objects.hash( machine ), key -> reloadCasterInstruction() );
        reloadCasterInstruction();

        openMabeInfoTask.setStartObjectCallback( param -> machine );

        openCastingPreparationTask.setDetailProvider( ProcessDocuView.class, CastingPreparationDTO.class );
        openCastingPreparationTask.setTable( new SimpleSelectionProvider<ProcessDocuDTO>()
        {
            @Override
            public ProcessDocuDTO getSelectedValue()
            {
                return processDocuDTO;
            }
        } );

        openVisualInspectionTask.setDetailProvider( ProcessDocuView.class, VisualInspectionDTO.class );
        openVisualInspectionTask.setTable( new SimpleSelectionProvider<ProcessDocuDTO>()
        {
            @Override
            public ProcessDocuDTO getSelectedValue()
            {
                return processDocuDTO;
            }
        } );

        //openUnloadCasterTask.setStartObjectCallback( param -> casterInstructionDTO );

        detailsController.setMachineApk( machine );
        //startCastingTask.setMachine( machine );
        //endCastingTask.setMachine( machine );

        //        cancelCastingButton.setDisable( true, "Funktion noch nicht zur Verfügung" );
        //        emergencyUnloadButton.setDisable( true, "Funktion noch nicht zur Verfügung" );

        detailsController.activeProperty().bind( showDetailsSelected );

        final ClientModelManager clientModelManager = injector.getInstance( ClientModelManager.class );
        clientModel = clientModelManager.getClientModel( CastingClientModel.ID );
        //        clientModel.addRelationListener( CastingClientModel.MACHINE, observable -> reloadTimeManagement() );
        //        reloadTimeManagement();

        //        // Build Test-Data
        //        final CasterInstructionDTO testCasterInstructionDTO = new CasterInstructionDTO();
        //        testCasterInstructionDTO.setFurnace( machine );
        //        testCasterInstructionDTO.setFurnace( machine.substring( 0, 1 ) + "1" );
        //        testCasterInstructionDTO.setCharge( machine + StringTools.N3F.format( Math.random() * 999 ) );
        //        charge = Integer.parseInt( testCasterInstructionDTO.getCharge() );
        //        testCasterInstructionDTO.setAlloy( "99/01" );
        //        testCasterInstructionDTO.setPlannedWeight( 36.49 );
        //        testCasterInstructionDTO.setCastingLength( 4750. );
        //        testCasterInstructionDTO.setCastingProgram( "GI-32" );
        //        testCasterInstructionDTO.setNetWeight( 32.74 );
        //
        //        casterInstructionController.load( testCasterInstructionDTO );

        //        casterOccupancyDTO = new CasterOccupancyDTO();
        //
        //        final CasterOccupancyPosDTO pos1 = new CasterOccupancyPosDTO();
        //        pos1.setPosition( 1 );
        //        pos1.setSlab( "" + charge + "11" );
        //        pos1.setOrder( "001120837392" );
        //        pos1.setAmount( 1 );
        //        pos1.setMaterialType( "" );
        //        pos1.setExperimentNumber( "" );
        //        pos1.setWidth( 1600 );
        //        pos1.setLength( 4250 );
        //        pos1.setLengthBonus( 500 );
        //        pos1.setWeight( 12053 );
        //        pos1.setMold( "K-046" );
        //        pos1.setAgs( "AGS-036" );
        //        casterOccupancyDTO.setPos1( pos1 );
        //
        //        final CasterOccupancyPosDTO pos2 = new CasterOccupancyPosDTO();
        //        pos2.setPosition( 2 );
        //        pos2.setSlab( "" + charge + "12" );
        //        pos2.setOrder( "001120837396" );
        //        pos2.setAmount( 1 );
        //        pos2.setMaterialType( "" );
        //        pos2.setExperimentNumber( "" );
        //        pos2.setWidth( 1600 );
        //        pos2.setLength( 4250 );
        //        pos2.setLengthBonus( 500 );
        //        pos2.setWeight( 12053 );
        //        pos2.setMold( "K-036" );
        //        pos2.setAgs( "AGS-023" );
        //        casterOccupancyDTO.setPos2( pos2 );
        //
        //        final CasterOccupancyPosDTO pos4 = new CasterOccupancyPosDTO();
        //        pos4.setPosition( 2 );
        //        pos4.setSlab( "" + charge + "14" );
        //        pos4.setOrder( "001120837394" );
        //        pos4.setAmount( 1 );
        //        pos4.setMaterialType( "" );
        //        pos4.setExperimentNumber( "" );
        //        pos4.setWidth( 1600 );
        //        pos4.setLength( 4250 );
        //        pos4.setLengthBonus( 500 );
        //        pos4.setWeight( 12053 );
        //        pos4.setMold( "K-041" );
        //        pos4.setAgs( "AGS-001" );
        //        casterOccupancyDTO.setPos4( pos4 );
        //
        //        casterOccupancyControlController.load( casterOccupancyDTO );
        //
        //        testStepTimes.add( ANALYSIS_TF.format( new Date() ) );
        //        handleTestStep();
    }

    public void beforeShown( View view )
    {
        if ( !initialized )
        {
            validateCasterTask.setViewContext( view.getViewContext() );
            TaskButtonUtil.registerTasks( view, injector, taskManager, validateCasterTask );
            downtimeChart.start();
            //            initGauges();
            initialized = true;
        }
    }

    public void activateView( View view )
    {
        downtimeChart.start();
        //        furnaceLeftProgress.start();
        //        casterProgress.start();
        //        furnaceRightProgress.start();

        chargingAnalysisTable.setActive( true );

        //reloadTimeManagement();
        reloadCasterInstruction();

        //        if ( timer != null )
        //        {
        //            // is running
        //            return;
        //        }
        //        timer = new Timer( "timeManagement", true );
        //        timer.schedule( new TimeManagementTimerTask(), 1000, 1000 );
    }

    public void deactivateView( View view )
    {
        downtimeChart.stop();
        //        furnaceLeftProgress.stop();
        //        casterProgress.stop();
        //        furnaceRightProgress.stop();
        chargingAnalysisTable.setActive( false );

        //        if ( timer != null )
        //        {
        //            timer.cancel();
        //            timer = null;
        //        }
    }

    //    private void initGauges()
    //    {
    //        if ( downtimeGauge == null || outputGauge == null )
    //        {
    //            return;
    //        }
    //        Tooltip.install( downtimeGauge, new Tooltip( "Störzeiten [%]\naktuelle Schicht" ) );
    //        outputGauge.init( machine, dataProvider, injector );
    //        setGaugeValues();
    //    }

    //    private void setGaugeValues()
    //    {
    //        ReportingGaugeSummaryDTO summaryDTO = dataProvider.getGaugeSummary( machine, Casting.REPORTING.CURRENT_SHIFT );
    //        if ( summaryDTO != null )
    //        {
    //            downtimeGauge.setValue( summaryDTO.getDowntimeValue() );
    //            outputGauge.setGaugeValues();
    //        }
    //    }

    //    @FXML
    //    private void openFurnaceLeft( ActionEvent actionEvent )
    //    {
    //        viewManager.openView( "casting.prod.furnace-" + machine.substring( 0, 1 ) + "1.view" );
    //    }

    //    @FXML
    //    private void openCaster( ActionEvent actionEvent )
    //    {
    //        viewManager.openView( "casting.prod.caster-" + machine + ".view" );
    //    }

    //    @FXML
    //    private void openFurnaceRight( ActionEvent actionEvent )
    //    {
    //        viewManager.openView( "casting.prod.furnace-" + machine.substring( 0, 1 ) + "2.view" );
    //    }

    @FXML
    public void showDetails( ActionEvent actionEvent )
    {
        showDetailsSelected.set( !showDetailsSelected.get() );
        ImageView imageView;
        if ( showDetailsSelected.get() )
        {
            detailPane.setShowDetailNode( true );
            imageView = new ImageView( ImagesCore.EXPAND.load() );

            detailsController.reloadProductionGantt();
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

    //    private void reloadTimeManagement()
    //    {
    //        MachineDTO machineDTO = null;
    //        if ( clientModel != null )
    //        {
    //            machineDTO = clientModel.getEntity( MachineDTO.class, machine );
    //        }
    //        if ( machineDTO == null || machineDTO.getCurrentStep() == null || machineDTO.getCurrentStepStartTS() == null || machineDTO.getCurrentStepDuration() <= 0 )
    //        {
    //            timeManagementViolations.clear();
    //            return;
    //        }
    //        final LocalDateTime start = machineDTO.getCurrentStepStartTS();
    //        final LocalDateTime normalEnd = start.plusMinutes( machineDTO.getCurrentStepDuration() );
    //        final List<TimeManagementViolationDTO> currentTimeManagementViolations = new ArrayList<>();
    //        if ( normalEnd.isBefore( LocalDateTime.now() ) )
    //        {
    //            TimeManagementViolationDTO timeManagementViolationDTO = new TimeManagementViolationDTO();
    //            timeManagementViolationDTO.setName( machineDTO.getCurrentStep() );
    //            timeManagementViolationDTO.setCharge( currentCharge );
    //            timeManagementViolationDTO.setPlannedEnd( normalEnd );
    //            timeManagementViolationDTO.setType( "Ende" );
    //            if ( StringTools.isFilled( machineDTO.getActiveDowntime() ) || StringTools.isFilled( machineDTO.getCurrentStepDowntime() ) )
    //            {
    //                timeManagementViolationDTO.setChecked( true );
    //            }
    //            currentTimeManagementViolations.add( timeManagementViolationDTO );
    //        }
    //        if ( !CollectionUtils.isEqualCollection( timeManagementViolations, currentTimeManagementViolations ) )
    //        {
    //            timeManagementViolations.setAll( currentTimeManagementViolations );
    //            if ( !timeManagementViolations.isEmpty() )
    //            {
    //                reloadCasterInstruction();
    //            }
    //        }
    //    }

    private void reloadCasterInstruction()
    {
        casterInstructionDTO = casterInstructionCache.get( Casting.CACHE.CASTER_INSTRUCTION_DATA_PATH + "/data/" + Objects.hash( machine ) );

        final CasterScheduleDTO casterScheduleDTO;
        if ( casterInstructionDTO != null && casterInstructionDTO.getCastingBatchOID() != null )
        {
            casterScheduleDTO = castingModel.getEntity( CasterScheduleDTO.class, casterInstructionDTO.getCastingBatchOID() );
        }
        else
        {
            casterScheduleDTO = null;
        }

        casterInstructionController.load( casterInstructionDTO, casterScheduleDTO );

        //startCastingTask.setCasterInstructionDTO( casterInstructionDTO );
        //endCastingTask.setCasterInstructionDTO( casterInstructionDTO );
        //        cleanFurnaceTask.setFurnaceInstructionDTO( furnaceInstructionDTO );
        //        requestAnalyseTask.setFurnaceInstructionDTO( furnaceInstructionDTO );
        //        openChargingFurnaceLiquidTask.setFurnaceInstructionDTO( furnaceInstructionDTO );
        //        openChargingFurnaceNonLiquidTask.setFurnaceInstructionDTO( furnaceInstructionDTO );
        //        openTreatingFurnaceTask.setFurnaceInstructionDTO( furnaceInstructionDTO );
        //        openSkimmingFurnaceTask.setFurnaceInstructionDTO( furnaceInstructionDTO );
        //        restingFurnaceTask.setFurnaceInstructionDTO( furnaceInstructionDTO );
        //        releaseFurnaceTask.setFurnaceInstructionDTO( furnaceInstructionDTO );
        //        deactivateChargeTask.setFurnaceInstructionDTO( furnaceInstructionDTO );
        //        detailsController.setMachineApk( furnaceInstructionDTO.getFurnace() );
        //        detailsController.setSelectedValue( furnaceInstructionDTO );
        //
        //        currentCharge = furnaceInstructionDTO.getCharge();
        if ( casterInstructionDTO != null && casterInstructionDTO.getCastingBatchOID() != null )
        {
            processDocuDTO.setId( casterInstructionDTO.getCastingBatchOID() );
            currentCharge = casterInstructionDTO.getCharge();
        }
        else
        {
            processDocuDTO.setId( 0 );
        }
        detailsController.setSelectedValue( casterInstructionDTO );

        if ( casterInstructionDTO != null )
        {
            loadOccupancy( casterInstructionDTO.getCastingBatchOID() );
        }

        chargingAnalysisTable.setCastingInstruction( casterInstructionDTO );

        validateCasterTask.setData( casterInstructionDTO );
        taskManager.executeTask( validateCasterTask );
    }

    private void loadOccupancy( Long castingBatchOID )
    {
        if ( castingBatchOID == null )
        {
            casterOccupancyControlController.load( null );
            return;
        }
        final CasterScheduleDTO casterScheduleDTO = castingModel.getEntity( CasterScheduleDTO.class, castingBatchOID );
        if ( casterScheduleDTO == null )
        {
            casterOccupancyControlController.load( null );
            return;
        }

        setOccupancyPos( casterScheduleDTO, casterOccupancyDTO, 1 );
        setOccupancyPos( casterScheduleDTO, casterOccupancyDTO, 2 );
        setOccupancyPos( casterScheduleDTO, casterOccupancyDTO, 3 );
        setOccupancyPos( casterScheduleDTO, casterOccupancyDTO, 4 );
        if ( machine.startsWith( "8" ) )
        {
            setOccupancyPos( casterScheduleDTO, casterOccupancyDTO, 5 );
        }
        casterOccupancyControlController.load( casterOccupancyDTO );
    }

    private void setOccupancyPos( CasterScheduleDTO casterScheduleDTO, CasterOccupancyDTO casterOccupancyDTO, int pos )
    {
        final CasterSchedulePosDTO casterSchedulePos = casterScheduleDTO.getPos( pos );
        if ( casterSchedulePos.getMaterialType() == null )
        {
            casterOccupancyDTO.setPos( pos, null );
            return;
        }

        CasterOccupancyPosDTO casterOccupancyPos = casterOccupancyDTO.getPos( pos );
        if ( casterOccupancyPos == null )
        {
            casterOccupancyPos = new CasterOccupancyPosDTO();
            casterOccupancyDTO.setPos( pos, casterOccupancyPos );
        }
        casterOccupancyPos.setPosition( pos );
        if ( casterOccupancyPos.getAmount() > 1 )
        {
            casterOccupancyPos.setSlab( "" + casterScheduleDTO.getChargeWithoutYear() + "2" + pos );
        }
        else
        {
            casterOccupancyPos.setSlab( "" + casterScheduleDTO.getChargeWithoutYear() + "1" + pos );
        }
        casterOccupancyPos.setOrder( casterScheduleDTO.getProcessOrder() + "1" );
        casterOccupancyPos.setAmount( casterSchedulePos.getAmount() );
        casterOccupancyPos.setMaterialType( casterSchedulePos.getMaterialType() );
        casterOccupancyPos.setExperimentNumber( casterSchedulePos.getExperimentNumber() );
        casterOccupancyPos.setWidth( casterSchedulePos.getWidth() );
        casterOccupancyPos.setLength( casterSchedulePos.getLength() );
        casterOccupancyPos.setLengthBonus( casterSchedulePos.getLengthBonus() );
        casterOccupancyPos.setWeight( 0 );
        casterOccupancyPos.setMold( "?????" );
        casterOccupancyPos.setAgs( "?????" );
    }

    //    private class TimeManagementTimerTask extends TimerTask
    //    {
    //        @Override
    //        public void run()
    //        {
    //            Platform.runLater( timeManagementRunnable );
    //        }
    //    }

    //    public boolean handleFunctionKey( KeyCode keyCode, boolean isShiftDown )
    //    {
    //        if ( keyCode == KeyCode.F1 )
    //        {
    //            testStep++;
    //            testStepTimes.add( ANALYSIS_TF.format( new Date() ) );
    //            handleTestStep();
    //            return true;
    //        }
    //        if ( keyCode == KeyCode.F2 )
    //        {
    //            testStep--;
    //            if ( testStep < 0 )
    //            {
    //                testStep = 0;
    //            }
    //            if ( testStep == 0 )
    //            {
    //                testStepTimes.clear();
    //                testStepTimes.add( ANALYSIS_TF.format( new Date() ) );
    //            }
    //            handleTestStep();
    //            return true;
    //        }
    //        return false;
    //    }
    //
    //    private void handleTestStep()
    //    {
    //        log.info( "###### testStep " + testStep );
    //        chargingAnalysisTable.loadCasterData( testStep, testStepTimes );
    //
    //        //        final TimeManagementDTO charge = new TimeManagementDTO();
    //        //        charge.setCharge( "12345" );
    //        //        charge.setPlannedStart( LocalDateTime.now() );
    //        //        final List<TimeManagementPhaseDTO> phases = new ArrayList<>();
    //        //        final TimeManagementPhaseDTO phase1 = new TimeManagementPhaseDTO();
    //        //        phase1.setName( CasterStep.Casting.getShortName() );
    //        //        phase1.setPlannedDuration( Duration.ofHours(2) );
    //        //        phases.add( phase1 );
    //        //        final TimeManagementPhaseDTO phase2 = new TimeManagementPhaseDTO();
    //        //        phase2.setName( CasterStep.Resting.getShortName() );
    //        //        phase2.setPlannedDuration( Duration.ofHours(3) );
    //        //        phases.add( phase2 );
    //        //        charge.setPhases( phases );
    //        //        final List<TimeManagementDTO> timeManagements = new ArrayList<>();
    //        //        timeManagements.add( charge );
    //
    //        final InputStream resourceAsStream = CasterController.class.getResourceAsStream( "/com/hydro/casting/gui/data/timeManagementCasterTestData.json" );
    //        Gson gson = new Gson();
    //        Type timeManagementListType = new TypeToken<ArrayList<TimeManagementDTO>>()
    //        {
    //        }.getType();
    //        final List<TimeManagementDTO> newTimeManagements = gson.fromJson( new InputStreamReader( resourceAsStream ), timeManagementListType );
    //        newTimeManagements.get( 0 ).setCharge( "" + ( charge - 2 ) );
    //        newTimeManagements.get( 0 ).setPlannedStart( LocalDateTime.now().minusHours( 3 ) );
    //        final List<TimeManagementPhaseDTO> phases0 = newTimeManagements.get( 0 ).getPhases();
    //        phases0.get( 0 ).setStart( LocalDateTime.now().minusHours( 3 ).minusMinutes( 30 ) );
    //        phases0.get( 1 ).setStart( LocalDateTime.now().minusHours( 2 ).minusMinutes( 30 ) );
    //        newTimeManagements.get( 1 ).setCharge( "" + charge );
    //        newTimeManagements.get( 1 ).setPlannedStart( LocalDateTime.now() );
    //        newTimeManagements.get( 2 ).setCharge( "" + ( charge + 2 ) );
    //        newTimeManagements.get( 2 ).setPlannedStart( LocalDateTime.now().plusHours( 3 ) );
    //
    //        timeManagements.clear();
    //        timeManagements.addAll( newTimeManagements );
    //        reloadTimeManagement();
    //
    //        casterDetailController.loadData( timeManagements );
    //        //chargingTable.loadData( testStep, testStepTimes, charge, null );
    //        //double weight = furnaceContentTable.loadData( testStep, testStepTimes, charge, null );
    //        //amount.setText( StringTools.N1F.format( weight ) + "kg" );
    //        //
    //        //        if ( testStep == 0 )
    //        //        {
    //        //            furnaceStatus51.set( FurnaceStep.Casting, null );
    //        //        }
    //        //        else if ( testStep < 6)
    //        //        {
    //        //            furnaceStatus51.set( FurnaceStep.Charging, null );
    //        //        }
    //        //        else if ( testStep < 10)
    //        //        {
    //        //            furnaceStatus51.set( FurnaceStep.ChargingLiquid, null );
    //        //        }
    //        //        else if ( testStep < 12)
    //        //        {
    //        //            furnaceStatus51.set( FurnaceStep.Treating, null );
    //        //        }
    //        //        else
    //        //        {
    //        //            furnaceStatus51.set( FurnaceStep.Resting, null );
    //        //        }
    //    }
}
