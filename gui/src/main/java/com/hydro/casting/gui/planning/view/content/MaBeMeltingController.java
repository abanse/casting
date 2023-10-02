package com.hydro.casting.gui.planning.view.content;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.gui.model.CastingClientModel;
import com.hydro.casting.server.contract.dto.CasterDemandDTO;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.CasterSchedulePosDTO;
import com.hydro.casting.server.contract.dto.MachineDTO;
import com.hydro.core.gui.*;
import com.hydro.core.gui.comp.AutoHideSidesPane;
import com.hydro.core.gui.model.ClientModel;
import com.hydro.core.gui.skin.AutoHideSidesPaneSkin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import org.controlsfx.control.MasterDetailPane;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class MaBeMeltingController
{
    private final ProgressIndicator loadingInstructionIndicator = new ProgressIndicator();

    private final ObservableList<CasterDemandDTO> selectedDemands = FXCollections.observableArrayList();
    private final ObservableList<CasterScheduleDTO> selectedSchedules = FXCollections.observableArrayList();
    private final ObservableList<CasterSchedulePosDTO> selectedSchedulePoss = FXCollections.observableArrayList();

    private PlanningMeltingDemandController planningMeltingDemandController;
    @FXML
    private AutoHideSidesPane mainAutoHideSidePane;
    @FXML
    private GridPane rightSidePane;
    @FXML
    private Label functionTitle;
    @FXML
    private Label tabTitle;
    @FXML
    private TabPane details;
    @FXML
    private TitledPane standard;
    @FXML
    private MachineMeltingScheduleController scheduleController;
    @FXML
    private BorderPane pane;
    @FXML
    private MasterDetailPane hMDPane;
    @FXML
    private TabPane selectionInfos;
    @FXML
    private TabPane selectionInfosSidebar;
    @FXML
    private BorderPane instructionPane;
    @FXML
    private TabPane planningToolTabPane;
    @FXML
    private Tab planningToolTab;
    @FXML
    private BorderPane planningToolPane;
    @FXML
    private CheckBox pinSelection;
    @Inject
    private CacheManager cacheManager;
    @Inject
    private ApplicationManager applicationManager;
    @Inject
    private ClientModelManager clientModelManager;
    private String costCenter;
    private MachineDTO machineDTO;
    final MaBeMeltingHandle handle = new MaBeMeltingHandle()
    {
        @Override
        public MachineDTO getMachineDTO()
        {
            return machineDTO;
        }

        @Override
        public String getCostCenter()
        {
            return costCenter;
        }

        @Override
        public MachineMeltingScheduleController getMachineMeltingScheduleController()
        {
            return scheduleController;
        }

        @Override
        public PlanningMeltingDemandController getPlanningMeltingDemandController()
        {
            return planningMeltingDemandController;
        }

        @Override
        public void postValidationMessage( String validationMessage )
        {
            // TODO muss noch
        }

        @Override
        public void clearValidationMessage()
        {
            // TODO muss noch
        }
    };
    private ScheduledFuture<Void> demandSelectionFuture;
    private ScheduledFuture<Void> scheduleSelectionFuture;
    private boolean browserIsLoading = false;
    private boolean viewActive = false;

    public static String getDemandDragboardString( List<CasterDemandDTO> demands )
    {
        final StringBuilder dragBoardStringBuilder = new StringBuilder( "CASTER_DEMANDS:" );

        JSONArray demandsList = new JSONArray();

        for ( CasterDemandDTO demand : demands )
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put( "id", demand.getId() );

            demandsList.add( jsonObject );
        }

        dragBoardStringBuilder.append( demandsList.toJSONString() );

        return dragBoardStringBuilder.toString();
    }

    public static String getMaterialTypeDragboardString( List<CasterDemandDTO> demands )
    {
        final StringBuilder dragBoardStringBuilder = new StringBuilder( "CASTER_MATERIAL_TYPES:" );

        JSONArray demandsList = new JSONArray();

        for ( CasterDemandDTO demand : demands )
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put( "id", demand.getId() );

            demandsList.add( jsonObject );
        }

        dragBoardStringBuilder.append( demandsList.toJSONString() );

        return dragBoardStringBuilder.toString();
    }

    public static String getSchedulePosDragboardString( List<CasterSchedulePosDTO> schedulePositions )
    {
        final StringBuilder dragBoardStringBuilder = new StringBuilder( "SCHEDULE_POSITIONS:" );

        JSONArray schedulePositionList = new JSONArray();

        for ( CasterSchedulePosDTO casterSchedulePos : schedulePositions )
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put( "casterSchedule", casterSchedulePos.getCasterSchedule().getId() );
            jsonObject.put( "pos", casterSchedulePos.getPosition() );

            schedulePositionList.add( jsonObject );
        }

        dragBoardStringBuilder.append( schedulePositionList.toJSONString() );

        return dragBoardStringBuilder.toString();
    }

    public static boolean isCasterDemandsContent( final String dragboardString )
    {
        if ( dragboardString == null )
        {
            return false;
        }
        return dragboardString.startsWith( "CASTER_DEMANDS:" );
    }

    public static boolean isCasterMaterialTypeContent( final String dragboardString )
    {
        if ( dragboardString == null )
        {
            return false;
        }
        return dragboardString.startsWith( "CASTER_MATERIAL_TYPES:" );
    }

    public static boolean isCasterSchedulePosContent( final String dragboardString )
    {
        if ( dragboardString == null )
        {
            return false;
        }
        return dragboardString.startsWith( "SCHEDULE_POSITIONS:" );
    }

    public static List<Long> fromDragboardString( String dragboardString ) throws ParseException
    {
        if ( isCasterDemandsContent( dragboardString ) == false || isCasterMaterialTypeContent( dragboardString ) )
        {
            return null;
        }
        JSONParser parser = new JSONParser();
        final JSONArray jsonArray;
        if ( isCasterDemandsContent( dragboardString ) )
        {
            jsonArray = (JSONArray) parser.parse( dragboardString.substring( "CASTER_DEMANDS:".length() ) );
        }
        else
        {
            jsonArray = (JSONArray) parser.parse( dragboardString.substring( "CASTER_MATERIAL_TYPES:".length() ) );
        }

        final List<Long> ids = new ArrayList<>();
        for ( Object object : jsonArray )
        {
            if ( object instanceof JSONObject )
            {
                ids.add( (Long) ( (JSONObject) object ).get( "id" ) );
            }
        }

        return ids;
    }

    public static CasterSchedulePosDTO findPosFromDragboardString( List<CasterScheduleDTO> casterSchedules, String dragboardString ) throws ParseException
    {
        if ( isCasterSchedulePosContent( dragboardString ) == false )
        {
            return null;
        }
        JSONParser parser = new JSONParser();
        final JSONArray jsonArray = (JSONArray) parser.parse( dragboardString.substring( "SCHEDULE_POSITIONS:".length() ) );

        final List<Long> ids = new ArrayList<>();
        for ( Object object : jsonArray )
        {
            if ( object instanceof JSONObject )
            {
                final Number casterSchedule = (Number) ( (JSONObject) object ).get( "casterSchedule" );
                final Number pos = (Number) ( (JSONObject) object ).get( "pos" );
                if ( casterSchedule != null && pos != null )
                {
                    for ( CasterScheduleDTO schedule : casterSchedules )
                    {
                        if ( schedule.getId() == casterSchedule.longValue() )
                        {
                            if ( pos.intValue() == 1 )
                            {
                                return schedule.getPos1();
                            }
                            if ( pos.intValue() == 2 )
                            {
                                return schedule.getPos2();
                            }
                            if ( pos.intValue() == 3 )
                            {
                                return schedule.getPos3();
                            }
                            if ( pos.intValue() == 4 )
                            {
                                return schedule.getPos4();
                            }
                            if ( pos.intValue() == 5 )
                            {
                                return schedule.getPos5();
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    public void initialize( String costCenter, Injector injector )
    {
        this.costCenter = costCenter;

        /*
        topItem.addListener( ( observable, oldValue, newValue ) -> {
            topItemChanged = System.currentTimeMillis();
            Platform.runLater( () -> revalidateTabs() );
        } );
        bottomItem.addListener( ( observable, oldValue, newValue ) -> {
            bottomItemChanged = System.currentTimeMillis();
            Platform.runLater( () -> revalidateTabs() );
        } );
         */

        // load planning demand
        try
        {
            final String planningDemandFXML = "/com/hydro/casting/gui/planning/view/content/PlanningMeltingDemand.fxml";
            FXMLLoader loader = new FXMLLoader( getClass().getResource( planningDemandFXML ) );
            loader.setControllerFactory( injector::getInstance );
            final Node planningDemand = loader.load();
            pane.setCenter( planningDemand );
            planningMeltingDemandController = loader.getController();
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "error loading planningDemand", e );
        }

        loadingInstructionIndicator.setPrefSize( 20, 20 );

        /*
        if ( planningToolPane != null && planningToolTab != null )
        {
            planningToolBrowser = webBrowserEngineManager.createBrowser();

            loadingPlanningToolIndicator.setPrefSize( 16, 16 );

            planningToolBrowser.navigation().on( LoadStarted.class, event -> {
                Platform.runLater( () -> planningToolTab.setGraphic( loadingPlanningToolIndicator ) );
            } );
            planningToolBrowser.navigation().on( LoadFinished.class, event -> {
                Platform.runLater( () -> planningToolTab.setGraphic( null ) );
            } );

            BrowserView planningToolBrowserView = BrowserView.newInstance( planningToolBrowser );
            planningToolPane.setCenter( planningToolBrowserView );

            if ( !( PP.COST_CENTER.HUBHERDOFEN_1.equals( costCenter ) || PP.COST_CENTER.HUBHERDOFEN_2.equals( costCenter ) ) )
            {
                cacheManager.addCacheListener( PP.CACHE.PLANNING, "/planning/" + costCenter + "/schedule/version", ( key ) -> {
                    reloadPlanningTool();
                } );
            }

            if ( planningToolTabPane != null )
            {
                planningToolTabPane.getSelectionModel().selectedIndexProperty().addListener( ( observable, oldValue, newValue ) -> {
                    if ( newValue != null && newValue.intValue() == 1 )
                    {
                        reloadPlanningTool();
                    }
                } );
            }
        }

         */

        planningMeltingDemandController.initialize( handle, selectedDemands, selectedSchedules, selectedSchedulePoss );//, scheduleController.getCreateScheduleEntryTask() );

        /*
        planningDemandController.getSelectedItems().addListener( (InvalidationListener) e -> {
            triggerDemandSelection();
        } );
         */

        scheduleController.initialize( handle, selectedDemands, selectedSchedules, selectedSchedulePoss );

        /*
        scheduleController.getSelectedItems().addListener( (InvalidationListener) e -> {
            triggerScheduleSelection();
        } );

         */

        /*
        pane.connect();
        pane.addSingleActions( hMDPane, ActionType.MAXIMIZE_VERTICAL_TO_BOTTOM );
        pane2.connect();
        pane2.addSingleActions( hMDPane, ActionType.MAXIMIZE_VERTICAL_TO_TOP, ActionType.MOVE_OUT );
         */

        /*
        selectedItems.addListener( (InvalidationListener) e -> {
            // Anzahl Label
            final List<PlanningScheduleDTO> selectedSchedules = new ArrayList<>();
            for ( SelectedItem selectedItem : selectedItems )
            {
                final List<? extends ViewDTO> dtoList = selectedItem.getItems();
                for ( ViewDTO viewDTO : dtoList )
                {
                    if ( viewDTO instanceof PlanningScheduleDTO )
                    {
                        selectedSchedules.add( (PlanningScheduleDTO) viewDTO );
                    }
                }
            }
            if ( selectedSchedules.isEmpty() )
            {
                counterLabel.setText( "Anzahl: (keine Selektion)" );
            }
            else
            {
                int countPlannedInput = 0;
                for ( PlanningScheduleDTO selectedSchedule : selectedSchedules )
                {
                    countPlannedInput = countPlannedInput + NumberTools.getNullSafe( selectedSchedule.getPlannedInputQuantity() );
                }
                counterLabel.setText( "Anzahl: " + countPlannedInput + " Stück" );
            }

        } );

         */

        selectionInfos.getSelectionModel().selectedItemProperty().addListener( ( p, o, n ) -> {
            if ( mainAutoHideSidePane.isRightShowing() )
            {
                //loadDetails( n );
            }
        } );

        rightSidePane.prefWidthProperty().bind( mainAutoHideSidePane.widthProperty().multiply( 0.6666 ) );

        mainAutoHideSidePane.rightShowingProperty().addListener( ( observable, oldValue, newValue ) -> {
            if ( newValue != null && newValue )
            {
                //loadDetails( selectionInfos.getSelectionModel().getSelectedItem() );
            }
        } );

        selectionInfosSidebar.setOnMouseClicked( event -> {
            AutoHideSidesPaneSkin skin = (AutoHideSidesPaneSkin) mainAutoHideSidePane.getSkin();
            skin.show( Side.RIGHT );
        } );

        final ClientModel castingModel = clientModelManager.getClientModel( CastingClientModel.ID );
        machineDTO = castingModel.getEntity( MachineDTO.class, handle.getCostCenter() );
    }

    private void revalidateTabs()
    {
        if ( selectionInfos.getTabs().size() > 1 )
        {
            selectionInfos.getTabs().get( 0 ).setUserData( null );
            selectionInfos.getSelectionModel().clearAndSelect( 0 );
            for ( int i = 1; i < selectionInfos.getTabs().size(); i++ )
            {
                selectionInfos.getTabs().remove( 1 );
            }
        }
        if ( selectionInfosSidebar.getTabs().size() > 1 )
        {
            selectionInfosSidebar.getTabs().get( 0 ).setUserData( null );
            selectionInfosSidebar.getSelectionModel().clearAndSelect( 0 );
            for ( int i = 1; i < selectionInfosSidebar.getTabs().size(); i++ )
            {
                selectionInfosSidebar.getTabs().remove( 1 );
            }
        }
        /*
        if ( topItem.get() != null || bottomItem.get() != null )
        {
            if ( topItem.get() != null && bottomItem.get() == null )
            {
                selectionInfos.getTabs().get( 0 ).setUserData( topItem.get() );
                selectionInfos.getTabs().get( 0 ).setText( topItem.get().getName() );
                selectionInfosSidebar.getTabs().get( 0 ).setText( topItem.get().getName() );
            }
            else if ( bottomItem.get() != null && topItem.get() == null )
            {
                selectionInfos.getTabs().get( 0 ).setUserData( bottomItem.get() );
                selectionInfos.getTabs().get( 0 ).setText( bottomItem.get().getName() );
                selectionInfosSidebar.getTabs().get( 0 ).setText( bottomItem.get().getName() );
            }
            else
            {
                if ( topItemChanged > bottomItemChanged )
                {
                    selectionInfos.getTabs().get( 0 ).setUserData( topItem.get() );
                    selectionInfos.getTabs().get( 0 ).setText( topItem.get().getName() );
                    selectionInfosSidebar.getTabs().get( 0 ).setText( topItem.get().getName() );
                    final Tab newTab = new Tab( bottomItem.get().getName() );
                    newTab.setUserData( bottomItem.get() );
                    selectionInfos.getTabs().add( newTab );
                    final Tab newTabSidebar = new Tab( bottomItem.get().getName() );
                    selectionInfosSidebar.getTabs().add( newTabSidebar );
                }
                else
                {
                    selectionInfos.getTabs().get( 0 ).setUserData( bottomItem.get() );
                    selectionInfos.getTabs().get( 0 ).setText( bottomItem.get().getName() );
                    selectionInfosSidebar.getTabs().get( 0 ).setText( bottomItem.get().getName() );
                    final Tab newTab = new Tab( topItem.get().getName() );
                    newTab.setUserData( topItem.get() );
                    selectionInfos.getTabs().add( newTab );
                    final Tab newTabSidebar = new Tab( topItem.get().getName() );
                    selectionInfosSidebar.getTabs().add( newTabSidebar );
                }
            }
        }
        else
        {
            selectionInfos.getTabs().get( 0 ).setGraphic( null );
            selectionInfos.getTabs().get( 0 ).setText( "keine Selektion" );
            selectionInfos.getTabs().get( 0 ).setUserData( null );
            selectionInfosSidebar.getTabs().get( 0 ).setGraphic( null );
            selectionInfosSidebar.getTabs().get( 0 ).setText( "keine Selektion" );
        }
        if ( mainAutoHideSidePane.isRightShowing() )
        {
            loadDetails( selectionInfos.getSelectionModel().getSelectedItem() );
        }

         */
    }

    public void reload( ActionEvent actionEvent )
    {
        //reloadPlanningTool();
    }

    public void activateView( View view )
    {
        viewActive = true;
        planningMeltingDemandController.reload();
        scheduleController.reload();
    }

    public void deactivateView( View view )
    {
        viewActive = false;
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

}