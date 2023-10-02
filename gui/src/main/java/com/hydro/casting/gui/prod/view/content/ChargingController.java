package com.hydro.casting.gui.prod.view.content;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.Casting;
import com.hydro.casting.gui.ImagesCasting;
import com.hydro.casting.gui.prod.control.ChargeStatus;
import com.hydro.casting.gui.prod.control.ChargingControlController;
import com.hydro.casting.gui.prod.control.FurnaceInstructionControlController;
import com.hydro.casting.gui.prod.control.LiquidLevelCanvas;
import com.hydro.casting.gui.prod.table.ChargingAnalysisTable;
import com.hydro.casting.gui.prod.task.*;
import com.hydro.casting.gui.prod.view.MaterialsViewController;
import com.hydro.casting.gui.prod.view.content.context.ChargingContext;
import com.hydro.casting.server.contract.dto.FurnaceInstructionDTO;
import com.hydro.core.common.cache.ClientCache;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.*;
import com.hydro.core.gui.comp.AutoHideSidesPane;
import com.hydro.core.gui.comp.TaskButton;
import com.hydro.core.gui.comp.TaskProgressPane;
import com.hydro.core.gui.skin.AutoHideSidesPaneSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import jfxtras.scene.menu.CornerMenu;
import org.controlsfx.control.MasterDetailPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChargingController
{
    private final static Logger log = LoggerFactory.getLogger( ChargingController.class );

    @Inject
    private ViewManager viewManager;
    @Inject
    private CacheManager cacheManager;
    @Inject
    private SecurityManager securityManager;
    @Inject
    private TaskManager taskManager;
    @Inject
    private Injector injector;

    @FXML
    private TaskProgressPane chargingProgressPane;
    @FXML
    private GridPane workArea;
    @FXML
    private AutoHideSidesPane mainAutoHideSidePane;
    @FXML
    private CheckBox pinSelection;
    @FXML
    private TabPane selectionInfosSidebar;
    @FXML
    private TaskButton furnace51;
    @FXML
    private TaskButton furnace52;
    @FXML
    private TaskButton furnace61;
    @FXML
    private TaskButton furnace62;
    @FXML
    private TaskButton furnace71;
    @FXML
    private TaskButton furnace72;
    @FXML
    private TaskButton furnace81;
    @FXML
    private TaskButton furnace82;

    @FXML
    private ChargeStatus chargeStatus51;
    @FXML
    private ChargeStatus chargeStatus52;
    @FXML
    private ChargeStatus chargeStatus61;
    @FXML
    private ChargeStatus chargeStatus62;
    @FXML
    private ChargeStatus chargeStatus71;
    @FXML
    private ChargeStatus chargeStatus72;
    @FXML
    private ChargeStatus chargeStatus81;
    @FXML
    private ChargeStatus chargeStatus82;

    @FXML
    private LiquidLevelCanvas liquidLevel51;
    @FXML
    private LiquidLevelCanvas liquidLevel52;
    @FXML
    private LiquidLevelCanvas liquidLevel61;
    @FXML
    private LiquidLevelCanvas liquidLevel62;
    @FXML
    private LiquidLevelCanvas liquidLevel71;
    @FXML
    private LiquidLevelCanvas liquidLevel72;
    @FXML
    private LiquidLevelCanvas liquidLevel81;
    @FXML
    private LiquidLevelCanvas liquidLevel82;

    @FXML
    private TitledPane standard;
    @FXML
    private TaskButton showDetails;
    @FXML
    private TaskButton toggleCompressAnalysis;
    @FXML
    private MasterDetailPane detailPane;
    @FXML
    private FurnaceInstructionControlController instructionController;
    @FXML
    private ChargingAnalysisTable chargingAnalysisTable;
    @FXML
    private ChargingControlController chargingController;

    @FXML
    private GridPane rightSidePane;
    @FXML
    private ChargingActivateChargeTask chargingActivateChargeTask;
    @FXML
    private SendChargingSpecificationTask sendChargingSpecificationTask;
    @FXML
    private SaveChargingMaterialsTask saveChargingMaterialsTask;
    @FXML
    private StopEditingChargingMaterialsTask stopEditingChargingMaterialsTask;
    @FXML
    private DeleteChargingMaterialsTask deleteChargingMaterialsTask;
    @FXML
    private ChargingWizardTask chargingWizardTask;
    private FurnaceInstructionDTO furnaceInstruction = null;

    private ClientCache<FurnaceInstructionDTO> furnaceInstructionCache;

    private String machine;

    private List<TaskButton> furnaceButtons = new ArrayList<>();
    private BooleanProperty showDetailsSelected = new SimpleBooleanProperty( false );
    private BooleanProperty compressAnalysis = new SimpleBooleanProperty( true );

    private CornerMenu cornerMenu;

    public void initialize( String machine )
    {
        this.machine = machine;

        addCornerMenu();

        injector.injectMembers( chargingAnalysisTable );
        injector.injectMembers( chargingActivateChargeTask );
        injector.injectMembers( sendChargingSpecificationTask );
        injector.injectMembers( saveChargingMaterialsTask );
        injector.injectMembers( stopEditingChargingMaterialsTask );
        injector.injectMembers( deleteChargingMaterialsTask );
        injector.injectMembers( chargingWizardTask );

        chargingProgressPane.addTask( sendChargingSpecificationTask );
        chargingProgressPane.addTask( saveChargingMaterialsTask );
        chargingProgressPane.addTask( stopEditingChargingMaterialsTask );
        chargingProgressPane.addTask( deleteChargingMaterialsTask );
        chargingProgressPane.addTask( chargingWizardTask );

        chargingActivateChargeTask.setMachine( machine );
        saveChargingMaterialsTask.setChargingController( chargingController );
        stopEditingChargingMaterialsTask.setChargingController( chargingController );
        deleteChargingMaterialsTask.setChargingController( chargingController );

        deleteChargingMaterialsTask.disabledProperty().bind( chargingController.deleteAllowedProperty().not() );

        chargingController.editingProperty().addListener( observable -> revalidateActions() );
        chargingController.timeProperty().addListener( observable -> revalidateActions() );

        chargingWizardTask.setChargingTable( chargingController.getChargingTable() );

        showDetailsSelected.addListener( ( observable, oldValue, newValue ) -> {
            final ImageView imageView;
            if ( newValue )
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
        } );
        compressAnalysis.addListener( ( observable, oldValue, newValue ) -> {
            final ImageView imageView;
            if ( newValue )
            {
                imageView = new ImageView( ImagesCasting.DECOMPRESS.load() );
            }
            else
            {
                imageView = new ImageView( ImagesCasting.COMPRESS.load() );
            }
            imageView.setFitHeight( 21.0 );
            imageView.setFitWidth( 21.0 );
            toggleCompressAnalysis.setGraphic( imageView );
            reloadFurnaceInstruction();
        } );
        chargingAnalysisTable.compressAnalysisProperty().bind( compressAnalysis );
        chargingController.compressAnalysisProperty().bind( compressAnalysis );

        furnaceButtons.add( furnace51 );
        furnaceButtons.add( furnace52 );
        furnaceButtons.add( furnace61 );
        furnaceButtons.add( furnace62 );
        furnaceButtons.add( furnace71 );
        furnaceButtons.add( furnace72 );
        furnaceButtons.add( furnace81 );
        furnaceButtons.add( furnace82 );

        for ( TaskButton furnaceButton : furnaceButtons )
        {
            if ( Objects.equals( furnaceButton.getUserData(), machine ) )
            {
                furnaceButton.setDisable( true, null );
            }
            else
            {
                furnaceButton.setDisable( false, null );
            }
        }

        chargeStatus51.connect( injector, Casting.MACHINE.MELTING_FURNACE_51 );
        chargeStatus52.connect( injector, Casting.MACHINE.MELTING_FURNACE_52 );
        chargeStatus61.connect( injector, Casting.MACHINE.MELTING_FURNACE_61 );
        chargeStatus62.connect( injector, Casting.MACHINE.MELTING_FURNACE_62 );
        chargeStatus71.connect( injector, Casting.MACHINE.MELTING_FURNACE_71 );
        chargeStatus72.connect( injector, Casting.MACHINE.MELTING_FURNACE_72 );
        chargeStatus81.connect( injector, Casting.MACHINE.MELTING_FURNACE_81 );
        chargeStatus82.connect( injector, Casting.MACHINE.MELTING_FURNACE_82 );

        liquidLevel51.connect( injector, Casting.MACHINE.MELTING_FURNACE_51 );
        liquidLevel52.connect( injector, Casting.MACHINE.MELTING_FURNACE_52 );
        liquidLevel61.connect( injector, Casting.MACHINE.MELTING_FURNACE_61 );
        liquidLevel62.connect( injector, Casting.MACHINE.MELTING_FURNACE_62 );
        liquidLevel71.connect( injector, Casting.MACHINE.MELTING_FURNACE_71 );
        liquidLevel72.connect( injector, Casting.MACHINE.MELTING_FURNACE_72 );
        liquidLevel81.connect( injector, Casting.MACHINE.MELTING_FURNACE_81 );
        liquidLevel82.connect( injector, Casting.MACHINE.MELTING_FURNACE_82 );

        chargingAnalysisTable.connect( cacheManager );

        rightSidePane.prefWidthProperty().bind( mainAutoHideSidePane.widthProperty().multiply( 0.6666 ) );

        selectionInfosSidebar.setOnMouseClicked( event -> {
            AutoHideSidesPaneSkin skin = (AutoHideSidesPaneSkin) mainAutoHideSidePane.getSkin();
            skin.show( Side.RIGHT );
        } );

        furnaceInstructionCache = cacheManager.getCache( Casting.CACHE.PROD_CACHE_NAME );
        cacheManager.addCacheListener( Casting.CACHE.PROD_CACHE_NAME, Casting.CACHE.FURNACE_INSTRUCTION_DATA_PATH + "/data/" + Objects.hash( machine ), key -> reloadFurnaceInstruction() );
        cacheManager.addCacheListener( Casting.CACHE.PLANNING_CACHE_NAME, Casting.CACHE.MACHINE_DATA_PATH + "/data/" + Objects.hash( machine ), key -> reloadFurnaceInstruction() );
        reloadFurnaceInstruction();
    }

    public void setStartObject( Object startObject )
    {
        if ( !( startObject instanceof ChargingContext ) )
        {
            return;
        }
        final ChargingContext chargingContext = (ChargingContext) startObject;

        detailPane.setAnimated( false );
        showDetailsSelected.set( chargingContext.isShowDetails() );
        compressAnalysis.set( chargingContext.isCompressAnalysis() );
        detailPane.setAnimated( true );
    }

    public void activateView( View view )
    {
        chargingAnalysisTable.setActive( true );

        chargingActivateChargeTask.setLocked( !securityManager.hasRole( chargingActivateChargeTask.getId() ) );
        chargingActivateChargeTask.setDisabled( true );
        chargingActivateChargeTask.setRemark( "automatische Aktivierung aktiv" );

        sendChargingSpecificationTask.setLocked( !securityManager.hasRole( sendChargingSpecificationTask.getId() ) );
        sendChargingSpecificationTask.setDisabled( true );

        saveChargingMaterialsTask.setLocked( !securityManager.hasRole( saveChargingMaterialsTask.getId() ) );
        saveChargingMaterialsTask.setDisabled( true );

        stopEditingChargingMaterialsTask.setLocked( !securityManager.hasRole( stopEditingChargingMaterialsTask.getId() ) );
        stopEditingChargingMaterialsTask.setDisabled( true );

        deleteChargingMaterialsTask.setLocked( !securityManager.hasRole( deleteChargingMaterialsTask.getId() ) );
        chargingWizardTask.setLocked( !securityManager.hasRole( chargingWizardTask.getId() ) );
        chargingWizardTask.setDisabled( false );

        reloadFurnaceInstruction();
    }

    public void deactivateView( View view )
    {
        chargingAnalysisTable.setActive( false );
    }

    @FXML
    private void openFurnace( ActionEvent actionEvent )
    {
        final ChargingContext chargingContext = new ChargingContext( showDetailsSelected.get(), compressAnalysis.get() );
        viewManager.openView( "casting.prod.charging-" + ( (TaskButton) actionEvent.getSource() ).getUserData() + ".view", chargingContext );
    }

    @FXML
    public void showDetails( ActionEvent actionEvent )
    {
        showDetailsSelected.set( !showDetailsSelected.get() );
    }

    @FXML
    public void toggleCompressAnalysis( ActionEvent actionEvent )
    {
        compressAnalysis.set( !compressAnalysis.get() );
    }

    @FXML
    public void pinSelection( ActionEvent actionEvent )
    {
        if ( pinSelection.isSelected() )
        {
            mainAutoHideSidePane.setPinnedSide( Side.RIGHT );
        }
        else
        {
            mainAutoHideSidePane.setPinnedSide( null );
        }
    }

    @FXML
    public void openFEVOWindow( ActionEvent actionEvent )
    {
        //viewManager.openView( PPSecurity.PLANNING.FEVO_BROWSER.VIEW );
    }

    private void reloadFurnaceInstruction()
    {
        log.info( "reloadFurnaceInstruction" );

        final FurnaceInstructionDTO furnaceInstructionDTO = furnaceInstructionCache.get( Casting.CACHE.FURNACE_INSTRUCTION_DATA_PATH + "/data/" + Objects.hash( machine ) );
        instructionController.load( furnaceInstructionDTO );

        chargingAnalysisTable.setCastingInstruction( furnaceInstructionDTO );
        chargingController.loadData( furnaceInstructionDTO );
        if ( furnaceInstructionDTO != null && furnaceInstructionDTO.getCastingBatchOID() != null )
        {
            sendChargingSpecificationTask.setCastingBatchOID( furnaceInstructionDTO.getCastingBatchOID() );
            saveChargingMaterialsTask.setCastingBatchOID( furnaceInstructionDTO.getCastingBatchOID() );
        }
        this.furnaceInstruction = furnaceInstructionDTO;
        revalidateActions();
    }

    private void revalidateActions()
    {
        if ( furnaceInstruction != null && furnaceInstruction.getCastingBatchOID() != null )
        {
            if ( chargingController.isEditing() )
            {
                if ( chargingController.timeProperty().get() > 0 )
                {
                    saveChargingMaterialsTask.setDisabled( true );
                    saveChargingMaterialsTask.setRemark( "Daten sind nicht aktuell (Zeit-Shift aktiviert, Slider) " );
                }
                else
                {
                    saveChargingMaterialsTask.setDisabled( false );
                    saveChargingMaterialsTask.setRemark( null );
                }
                stopEditingChargingMaterialsTask.setDisabled( false );
                stopEditingChargingMaterialsTask.setRemark( null );
                sendChargingSpecificationTask.setDisabled( true );
                sendChargingSpecificationTask.setRemark( "Daten wurden noch nicht gespeichert" );
            }
            else
            {
                saveChargingMaterialsTask.setDisabled( true );
                saveChargingMaterialsTask.setRemark( null );
                stopEditingChargingMaterialsTask.setDisabled( true );
                stopEditingChargingMaterialsTask.setRemark( null );
                sendChargingSpecificationTask.setDisabled( false );
                sendChargingSpecificationTask.setRemark( null );
            }
        }
        else
        {
            saveChargingMaterialsTask.setDisabled( true );
            saveChargingMaterialsTask.setRemark( null );
            stopEditingChargingMaterialsTask.setDisabled( true );
            stopEditingChargingMaterialsTask.setRemark( null );
            sendChargingSpecificationTask.setDisabled( true );
            sendChargingSpecificationTask.setRemark( null );
        }
    }

    @FXML
    public void activateCharge( ActionEvent actionEvent )
    {
        taskManager.executeTask( chargingActivateChargeTask );
    }

    @FXML
    public void saveChargingMaterials( ActionEvent actionEvent )
    {
        saveChargingMaterialsTask.setFurnaceContentMaterials( chargingController.getFurnaceContentMaterials() );
        saveChargingMaterialsTask.setChargingMaterials( chargingController.getChargingMaterials() );
        taskManager.executeTask( saveChargingMaterialsTask );
    }

    public void stopEditingChargingMaterials( ActionEvent actionEvent )
    {
        taskManager.executeTask( stopEditingChargingMaterialsTask );
    }

    @FXML
    public void sendChargingSpecification( ActionEvent actionEvent )
    {
        taskManager.executeTask( sendChargingSpecificationTask );
    }

    @FXML
    public void configure( ActionEvent actionEvent )
    {
        chargingController.configure();
    }

    @FXML
    public void deleteChargingMaterials( ActionEvent actionEvent )
    {
        taskManager.executeTask( deleteChargingMaterialsTask );
    }

    private void addCornerMenu()
    {
        // add corner menu
        ImageView hotSpot = new ImageView( ImagesCasting.HOT_SPOT.load() );
        hotSpot.setFitWidth( 16.0 );
        hotSpot.setFitHeight( 16.0 );
        hotSpot.setLayoutX( -3.0 );
        hotSpot.setLayoutY( -3.0 );
        workArea.getChildren().add( hotSpot );

        cornerMenu = new CornerMenu( CornerMenu.Location.TOP_LEFT, workArea, false );
        cornerMenu.setAutoShowAndHide( true );
        cornerMenu.setAnimationDuration( Duration.millis( 200 ) );
        ImageView actionIV = new ImageView( ImagesCasting.MAXIMIZE_V.load() );
        actionIV.setFitWidth( 35.0 );
        actionIV.setFitHeight( 35.0 );
        MenuItem actionMI = new MenuItem( "Maximieren", actionIV );
        actionMI.setOnAction( ( event ) -> {
            if ( workArea.getParent() != null && workArea.getParent().getParent() != null && workArea.getParent().getParent().getParent() instanceof MasterDetailPane )
            {
                final MasterDetailPane masterDetailPane = (MasterDetailPane) workArea.getParent().getParent().getParent();
                if ( masterDetailPane.getDividerPosition() < 0.01 )
                {
                    if ( actionMI.getUserData() instanceof Double )
                    {
                        Double oldValue = (Double) actionMI.getUserData();
                        masterDetailPane.setDividerPosition( oldValue );
                    }
                }
                else
                {
                    actionMI.setUserData( masterDetailPane.getDividerPosition() );
                    masterDetailPane.setDividerPosition( 0.0 );
                }
            }
        } );
        cornerMenu.getItems().add( actionMI );
    }

    @FXML
    public void openMaterials( ActionEvent actionEvent )
    {
        viewManager.openView( MaterialsViewController.ID );
    }

    @FXML
    public void wizard( ActionEvent actionEvent )
    {
        taskManager.executeTask( chargingWizardTask );
    }
}
