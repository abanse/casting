package com.hydro.casting.gui.planning.view.content;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.Casting;
import com.hydro.casting.gui.ImagesCasting;
import com.hydro.casting.gui.model.CastingClientModel;
import com.hydro.casting.gui.planning.grid.CasterS1_S2GridConfig;
import com.hydro.casting.gui.planning.grid.node.CasterSchedulePosNode;
import com.hydro.casting.gui.planning.task.*;
import com.hydro.casting.server.contract.dto.CasterDemandDTO;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.CasterSchedulePosDTO;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.*;
import com.hydro.core.gui.comp.TaskButton;
import com.hydro.core.gui.comp.TaskProgressPane;
import com.hydro.core.gui.comp.grid.GridDragAndDropProvider;
import com.hydro.core.gui.comp.grid.GridView;
import com.hydro.core.gui.comp.grid.model.GridModel;
import com.hydro.core.gui.model.ClientModel;
import impl.org.controlsfx.spreadsheet.CellView;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TablePosition;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.StringConverter;
import jfxtras.scene.menu.CornerMenu;
import org.controlsfx.control.MasterDetailPane;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MachineMeltingScheduleController
{
    private final static Logger log = LoggerFactory.getLogger( MachineMeltingScheduleController.class );

    @FXML
    public BorderPane container;
    @FXML
    public TaskButton configure;
    @FXML
    private TaskButton release;
    @Inject
    private Injector injector;
    @Inject
    private ClientModelManager clientModelManager;
    @Inject
    private ApplicationManager applicationManager;
    @Inject
    private ErrorManager errorManager;
    @Inject
    private SecurityManager securityManager;
    @Inject
    private EditProcessOrderTask editProcessOrderTask;

    @Inject
    private TaskManager taskManager;
    @FXML
    private GridView<CasterScheduleDTO> gridView;
    @FXML
    private Label remarkDnDComponent;
    @FXML
    private Label setupDnDComponent;
    @FXML
    private TaskProgressPane taskProgressPane;
    @FXML
    private VBox functionBox;

    private MaBeMeltingHandle handle;
    private ObservableList<CasterDemandDTO> selectedDemands;
    private ObservableList<CasterScheduleDTO> selectedSchedules;
    private ObservableList<CasterSchedulePosDTO> selectedSchedulePoss;

    private ClientModel castingModel;

    @Inject
    private SetCasterBatchPositionTask setCasterBatchPositionTask;

    @Inject
    private MoveCasterBatchPositionTask moveCasterBatchPositionTask;

    @Inject
    private CreateSetupTask createSetup;
    @Inject
    private EditAnnotationTask editAnnotationTask;
    @Inject
    private MoveEntriesTask moveEntries;
    @Inject
    private ConfigureTask configureTask;

    private GridModel<CasterScheduleDTO> gridModel = new GridModel<CasterScheduleDTO>()
    {
        @Override
        public void moveRows( List<Long> sourceRowIds, int targetRow, boolean afterRow )
        {
            final List<CasterScheduleDTO> sources = new ArrayList<>();
            final List<CasterScheduleDTO> schedules = gridModel.getRows();
            for ( Long sourceRowId : sourceRowIds )
            {
                for ( CasterScheduleDTO schedule : schedules )
                {
                    if ( schedule.getId() == sourceRowId )
                    {
                        sources.add( schedule );
                        break;
                    }
                }
            }
            final CasterScheduleDTO target = gridModel.getRows().get( targetRow );
            moveEntries.setData( sources, target, afterRow );
            taskManager.executeTask( moveEntries );
        }
    };

    private CornerMenu cornerMenu;

    public void initialize( MaBeMeltingHandle handle, ObservableList<CasterDemandDTO> selectedDemands, ObservableList<CasterScheduleDTO> selectedSchedules,
            ObservableList<CasterSchedulePosDTO> selectedSchedulePoss )
    {
        injector.injectMembers( gridView );

        this.handle = handle;
        this.selectedDemands = selectedDemands;
        this.selectedSchedules = selectedSchedules;
        this.selectedSchedulePoss = selectedSchedulePoss;

        castingModel = clientModelManager.getClientModel( CastingClientModel.ID );

        addCornerMenu();

        createSetup.setCostCenter( handle.getCostCenter() );
        createSetup.setGridView( gridView );
        moveEntries.setCostCenter( handle.getCostCenter() );

        taskProgressPane.addTask( createSetup );
        taskProgressPane.addTask( editAnnotationTask );
        taskProgressPane.addTask( setCasterBatchPositionTask );
        taskProgressPane.addTask( moveCasterBatchPositionTask );
        taskProgressPane.addTask( moveEntries );
        taskProgressPane.addTask( configureTask );

        configure.setDisable( false );

        gridView.setFixingColumnsAllowed( false );
        gridView.setFixingRowsAllowed( false );

        gridView.getSelectionModel().getSelectedCells().addListener( new InvalidationListener()
        {
            @SuppressWarnings( "rawtypes" )
            @Override
            public void invalidated( Observable observable )
            {
                final List<TablePosition> positions = gridView.getSelectionModel().getSelectedCells();
                final List<CasterSchedulePosDTO> casterSchedulePosDTOs = new ArrayList<>();
                final List<CasterScheduleDTO> dtos = new ArrayList<>();
                for ( TablePosition tablePosition : positions )
                {
                    final CasterScheduleDTO data = gridModel.getRows().get( tablePosition.getRow() );
                    final SpreadsheetCell cell = gridView.getGrid().getRows().get( tablePosition.getRow() ).get( tablePosition.getColumn() );
                    Object cellValue = null;
                    if ( cell != null )
                    {
                        cellValue = cell.getItem();
                    }
                    if ( cellValue instanceof CasterSchedulePosDTO )
                    {
                        casterSchedulePosDTOs.add( (CasterSchedulePosDTO) cellValue );
                    }
                    if ( dtos.contains( data ) )
                    {
                        continue;
                    }
                    dtos.add( data );
                }

                selectedSchedulePoss.setAll( casterSchedulePosDTOs );
                selectedSchedules.setAll( dtos );
            }
        } );

        remarkDnDComponent.setOnDragDetected( ( event ) -> {
            gridView.startDragAndDrop( remarkDnDComponent, "NEW REMARK" );

            event.consume();
        } );
        setupDnDComponent.setOnDragDetected( ( event ) -> {
            gridView.startDragAndDrop( setupDnDComponent, "NEW SETUP" );

            event.consume();
        } );

        gridView.setGridDragAndDropProvider( new GridDragAndDropProvider<CasterScheduleDTO>()
        {
            private ObjectProperty<CasterSchedulePosNode> dropTargetNode = new SimpleObjectProperty<>();

            {
                dropTargetNode.addListener( ( observable, oldValue, newValue ) -> {
                    if ( oldValue != null )
                    {
                        oldValue.setBorder( null );
                    }
                    if ( newValue != null )
                    {
                        newValue.setBorder( new Border( new BorderStroke( Color.web( "#0096C9" ), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT ) ) );
                    }
                } );
            }

            @Override
            public boolean handleDragDetected( CellView cellView, MouseEvent mouseEvent )
            {
                if ( cellView.getGraphic() instanceof CasterSchedulePosNode )
                {
                    final CasterSchedulePosNode casterSchedulePosNode = (CasterSchedulePosNode) cellView.getGraphic();
                    if ( casterSchedulePosNode.getCasterSchedulePos() == null || casterSchedulePosNode.getCasterSchedulePos().getMaterialType() == null )
                    {
                        mouseEvent.consume();
                        return true;
                    }
                    Dragboard dragBoard = casterSchedulePosNode.startDragAndDrop( TransferMode.MOVE );
                    dragBoard.setDragView( null );

                    ClipboardContent clipboardContent = new ClipboardContent();
                    clipboardContent.putString( MaBeController.getSchedulePosDragboardString( Collections.singletonList( casterSchedulePosNode.getCasterSchedulePos() ) ) );
                    dragBoard.setContent( clipboardContent );

                    mouseEvent.consume();
                    return true;
                }
                return false;
            }

            @Override
            public boolean handleDragOver( CellView cellView, DragEvent dragEvent )
            {
                if ( "NEW REMARK".equals( dragEvent.getDragboard().getString() ) || "NEW SETUP".equals( dragEvent.getDragboard().getString() ) )
                {
                    return false;
                }
                if ( cellView.getGraphic() instanceof CasterSchedulePosNode )
                {
                    final CasterSchedulePosNode casterSchedulePosNode = (CasterSchedulePosNode) cellView.getGraphic();

                    if ( MaBeController.isCasterMaterialTypeContent( dragEvent.getDragboard().getString() ) || MaBeController.isCasterDemandsContent( dragEvent.getDragboard().getString() )
                            || MaBeController.isCasterSchedulePosContent( dragEvent.getDragboard().getString() ) )
                    {
                        if ( !handle.getMachineMeltingScheduleController().isDropAllowed( dragEvent.getDragboard().getString(), casterSchedulePosNode.getCasterSchedulePos() ) )
                        {
                            dragEvent.consume();
                            dropTargetNode.set( null );
                            return true;
                        }
                        dragEvent.acceptTransferModes( TransferMode.MOVE );
                        dropTargetNode.set( casterSchedulePosNode );
                        dragEvent.consume();
                        return true;
                    }
                    dragEvent.consume();
                    dropTargetNode.set( null );
                    return true;
                }

                return false;
            }

            @Override
            public boolean handleDragExited( CellView cellView, DragEvent dragEvent )
            {
                dropTargetNode.set( null );
                return false;
            }

            @Override
            public boolean handleDragEntered( CellView cellView, DragEvent dragEvent )
            {
                return false;
            }

            @Override
            public boolean handleDragDropped( CellView cellView, DragEvent dragEvent )
            {
                if ( "NEW REMARK".equals( dragEvent.getDragboard().getString() ) || "NEW SETUP".equals( dragEvent.getDragboard().getString() ) )
                {
                    return false;
                }
                if ( cellView.getGraphic() instanceof CasterSchedulePosNode )
                {
                    final CasterSchedulePosNode casterSchedulePosNode = (CasterSchedulePosNode) cellView.getGraphic();
                    if ( MaBeController.isCasterMaterialTypeContent( dragEvent.getDragboard().getString() ) || MaBeController.isCasterDemandsContent( dragEvent.getDragboard().getString() )
                            || MaBeController.isCasterSchedulePosContent( dragEvent.getDragboard().getString() ) )
                    {
                        handle.getMachineMeltingScheduleController().handleDrop( dragEvent.getDragboard().getString(), casterSchedulePosNode.getCasterSchedulePos() );
                    }
                    dragEvent.consume();
                    dropTargetNode.set( null );
                    return true;
                }
                return false;
            }

            @Override
            public boolean isDragAllowed( String dragboardString )
            {
                return true;
            }

            @Override
            public boolean isDropAllowed( String dragboardString, int targetRow )
            {
                if ( "NEW REMARK".equals( dragboardString ) || "NEW SETUP".equals( dragboardString ) )
                {
                    return true;
                }
                if ( MaBeController.isCasterDemandsContent( dragboardString ) || MaBeController.isCasterMaterialTypeContent( dragboardString ) )
                {
                    if ( targetRow < 0 )
                    {
                        return false;
                    }
                    final CasterScheduleDTO targetCasterScheduleDTO = gridModel.getRows().get( targetRow );
                    if ( targetCasterScheduleDTO == null )
                    {
                        return false;
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean isDropAllowedToRow( String dragboardString, int targetRow )
            {
                return true;
            }

            @Override
            public void handleDrop( GridModel<CasterScheduleDTO> gridModel, String dragboardString, int row )
            {
                if ( "NEW REMARK".equals( dragboardString ) || "NEW SETUP".equals( dragboardString ) )
                {
                    CasterScheduleDTO beforeEntry = gridModel.getRows().get( row );

                    createSetup.setData( dragboardString, beforeEntry );
                    Platform.runLater( () -> taskManager.executeTask( createSetup ) );
                }
            }
        } );

        if ( Casting.MACHINE.MELTING_FURNACE_S1.equals( handle.getCostCenter() ) )
        {
            new CasterS1_S2GridConfig( castingModel ).configure( gridModel );
            //castingModel.addRelationListener( CastingClientModel.CASTER, this::invalidated );
        }
        else if ( Casting.MACHINE.MELTING_FURNACE_S2.equals( handle.getCostCenter() ) )
        {
            new CasterS1_S2GridConfig( castingModel ).configure( gridModel );
            //castingModel.addRelationListener( CastingClientModel.CASTER50, this::invalidated );
        }

        loadData();

        gridView.editCommitProperty().addListener( ( observable, oldValue, newValue ) -> {
            final CasterScheduleDTO casterScheduleDTO = gridModel.getRows().get( newValue.getRow() );
            if ( "setup".equals( casterScheduleDTO.getType() ) )
            {
                editAnnotationTask.setData( casterScheduleDTO, (String) newValue.getNewValue() );
                taskManager.executeTask( editAnnotationTask );
            }
            else
            {
                final String propertyName = gridModel.getProperties().get( newValue.getColumn() );
                if ( "annotation".equals( propertyName ) )
                {
                    editAnnotationTask.setData( casterScheduleDTO, (String) newValue.getNewValue() );
                    taskManager.executeTask( editAnnotationTask );
                }
                else if ( propertyName.startsWith( "percentage" ) )
                {
                    int newIntValue = 0;
                    try
                    {
                        newIntValue = Integer.parseInt( (String) newValue.getNewValue() );
                    }
                    catch ( NumberFormatException nfex )
                    {
                        // ignore
                    }
                    if ( newIntValue == 0 )
                    {
                        return;
                    }
                    if ( castingModel instanceof CastingClientModel )
                    {
                        log.error( "TODO speichern der prozentwerte", new Exception() );
                        /*
                        final CastingClientModel castingClientModel = (CastingClientModel) castingModel;
                        final String source = propertyName.substring( 10 );
                        castingClientModel.writeKnowledgeEntry(
                                "percentage." + source + "." + casterScheduleDTO.getMachine() + "." + casterScheduleDTO.getAlloy() + "." + casterScheduleDTO.getCharge().substring( 2 ), newIntValue,
                                "Anteil in Prozent Flüssigeinsatz " + source + " Gießanlage " + casterScheduleDTO.getMachine() + " Legierung " + casterScheduleDTO.getAlloy() + " Charge "
                                        + casterScheduleDTO.getCharge().substring( 2 ) );

                        Platform.runLater( () -> loadData() );
                         */
                    }
                }
                else
                {
                    editProcessOrderTask.setData( casterScheduleDTO, (String) newValue.getNewValue() );
                    taskManager.executeTask( editProcessOrderTask );
                }
            }
        } );

        selectedSchedules.addListener( (InvalidationListener) observable -> validateActions() );

        validateActions();
    }

    private void validateActions()
    {
        if ( selectedSchedules.isEmpty() )
        {
            release.setDisable( true, null );
        }
        boolean oneWithoutPO = false;
        for ( CasterScheduleDTO selectedSchedule : selectedSchedules )
        {
            if ( selectedSchedule.getProcessOrder() == null )
            {
                oneWithoutPO = true;
                break;
            }
        }
        if ( oneWithoutPO )
        {
            release.setDisable( true, "Es sind nicht alle Prozessaufträge(PA) besetzt" );
        }
        else
        {
            release.setDisable( false, null );
        }
    }

    void loadData()
    {
        final List<CasterScheduleDTO> casterSchedules = handle.getPlanningMeltingDemandController().loadCalculatedCasterSchedules();

        gridModel.clearRowConverters();
        gridModel.clearColoredRowStyles();
        // modify setup types
        final ObservableList<CasterScheduleDTO> entries = FXCollections.observableArrayList();
        int row = 0;
        long lastIndex = 0;
        for ( CasterScheduleDTO casterSchedule : casterSchedules )
        {
            if ( "setup".equals( casterSchedule.getType() ) )
            {
                entries.add( createSetupEntry( gridModel, row, casterSchedule ) );
            }
            else
            {
                entries.add( casterSchedule );
            }
            lastIndex = casterSchedule.getExecutingSequenceIndex();
            row++;
        }

        final CasterScheduleDTO emptySchedule = new CasterScheduleDTO();
        emptySchedule.setId( -1 );
        emptySchedule.setMachine( handle.getCostCenter() );
        emptySchedule.setExecutingSequenceIndex( lastIndex );

        entries.add( emptySchedule );

        gridModel.setRows( entries );
        gridView.setGridModel( gridModel );
    }

    private CasterScheduleDTO createSetupEntry( GridModel<CasterScheduleDTO> gridModel, int row, CasterScheduleDTO originalEntry )
    {
        long duration = originalEntry.getDuration();
        if ( duration <= 0 )
        {
            gridModel.addColoredRowStyle( row, "yellow" );
        }
        else
        {
            gridModel.addColoredRowStyle( row, "red" );
        }
        gridModel.addRowConverter( row, new StringConverter<CasterScheduleDTO>()
        {
            @Override
            public String toString( CasterScheduleDTO object )
            {
                if ( object == null )
                {
                    return "Störzeit";
                }
                if ( object.getDuration() > 0 )
                {
                    return "" + object.getDuration() + "min :" + object.getAnnotation();
                }
                return object.getAnnotation();
            }

            @Override
            public CasterScheduleDTO fromString( String string )
            {
                return null;
            }
        } );
        return originalEntry;
    }

    public GridView<CasterScheduleDTO> getGridView()
    {
        return gridView;
    }

    public void handleSetPositions()
    {
        setCasterBatchPositionTask.setData( selectedDemands, selectedSchedulePoss, null );
        taskManager.executeTask( setCasterBatchPositionTask );
    }

    public boolean isDropAllowed( String dragboardString, CasterSchedulePosDTO targetPosition )
    {
        boolean isInSelection = false;
        for ( CasterSchedulePosDTO schedulePoss : selectedSchedulePoss )
        {
            if ( targetPosition.getCasterSchedule().getId() == schedulePoss.getCasterSchedule().getId() && targetPosition.getPosition() == schedulePoss.getPosition() )
            {
                isInSelection = true;
                break;
            }
        }
        String sourceAlloy = null;
        int maxLength = 0;
        if ( MaBeController.isCasterDemandsContent( dragboardString ) || MaBeController.isCasterMaterialTypeContent( dragboardString ) )
        {
            for ( CasterDemandDTO selectedDemand : selectedDemands )
            {
                if ( selectedDemand.getOrderId() == null && selectedDemand.getOrderPosition() == null && selectedDemand.getMaterialType() == null )
                {
                    handle.postValidationMessage( "Es ist eine Gruppe von Kundenaufträgen selektiert worden" );
                    return false;
                }
                int demandLength = selectedDemand.getLength();
                if ( Casting.MACHINE.CASTER_80.equals( handle.getCostCenter() ) && selectedDemand.getDoubleLength() != null )
                {
                    demandLength = selectedDemand.getDoubleLength();
                }
                if ( maxLength < demandLength )
                {
                    maxLength = demandLength;
                }
                if ( sourceAlloy == null )
                {
                    sourceAlloy = selectedDemand.getAlloy();
                    continue;
                }
                if ( !Objects.equals( sourceAlloy, selectedDemand.getAlloy() ) )
                {
                    handle.postValidationMessage( "Es sind unterschiedliche Legierungen selektiert worden" );
                    return false;
                }
            }
        }
        else if ( MaBeController.isCasterSchedulePosContent( dragboardString ) )
        {
            final CasterSchedulePosDTO sourcePos;
            try
            {
                sourcePos = MaBeController.findPosFromDragboardString( gridModel.getRows(), dragboardString );
            }
            catch ( ParseException e )
            {
                return false;
            }
            maxLength = (int) sourcePos.getLength();
            sourceAlloy = sourcePos.getCasterSchedule().getAlloy();
        }
        if ( maxLength > handle.getMachineDTO().getMaxCastingLength() )
        {
            handle.postValidationMessage( "Die Vorgabe-Länge überschreitet die max. Giesslänge" );
            return false;
        }
        // Neue Zeile unten aussondern
        if ( targetPosition.getCasterSchedule().getId() == -1 )
        {
            handle.clearValidationMessage();
            return true;
        }
        String targetAlloy = null;
        if ( isInSelection )
        {
            for ( CasterSchedulePosDTO selectedPos : selectedSchedulePoss )
            {
                if ( targetAlloy == null )
                {
                    targetAlloy = selectedPos.getCasterSchedule().getAlloy();
                    continue;
                }
                if ( !Objects.equals( targetAlloy, selectedPos.getCasterSchedule().getAlloy() ) )
                {
                    handle.postValidationMessage( "Die Ziel-Position/en sind nicht Legierungsrein" );
                    return false;
                }
            }
        }
        else
        {
            targetAlloy = targetPosition.getCasterSchedule().getAlloy();
        }

        if ( !Objects.equals( sourceAlloy, targetAlloy ) )
        {
            handle.postValidationMessage( "Die Legierung der Ziel-Position/en passen nicht zur ausgewählten Legierung" );
            return false;
        }
        handle.clearValidationMessage();
        return true;
    }

    public void handleDrop( String dragboardString, CasterSchedulePosDTO targetPosition )
    {
        // Befindet sich die targetPostion innerhalb der Selektion
        // Selektion als Target benutzen
        boolean isInSelection = false;
        for ( CasterSchedulePosDTO schedulePoss : selectedSchedulePoss )
        {
            if ( targetPosition.getCasterSchedule().getId() == schedulePoss.getCasterSchedule().getId() && targetPosition.getPosition() == schedulePoss.getPosition() )
            {
                isInSelection = true;
                break;
            }
        }
        final CasterSchedulePosDTO sourcePos;
        try
        {
            sourcePos = MaBeController.findPosFromDragboardString( gridModel.getRows(), dragboardString );
        }
        catch ( ParseException e )
        {
            return;
        }
        if ( isInSelection )
        {
            if ( MaBeController.isCasterDemandsContent( dragboardString ) || MaBeController.isCasterMaterialTypeContent( dragboardString ) )
            {
                setCasterBatchPositionTask.setData( selectedDemands, selectedSchedulePoss, null );
                taskManager.executeTask( setCasterBatchPositionTask );
            }
            else if ( MaBeController.isCasterSchedulePosContent( dragboardString ) )
            {
                // TODO mehrfach move
                moveCasterBatchPositionTask.setData( Collections.singletonList( sourcePos ), Collections.singletonList( targetPosition ) );
                taskManager.executeTask( moveCasterBatchPositionTask );
            }
        }
        else
        {
            if ( MaBeController.isCasterDemandsContent( dragboardString ) || MaBeController.isCasterMaterialTypeContent( dragboardString ) )
            {
                setCasterBatchPositionTask.setData( selectedDemands, Collections.singletonList( targetPosition ), null );
                taskManager.executeTask( setCasterBatchPositionTask );
            }
            else if ( MaBeController.isCasterSchedulePosContent( dragboardString ) )
            {
                // TODO mehrfach move
                moveCasterBatchPositionTask.setData( Collections.singletonList( sourcePos ), Collections.singletonList( targetPosition ) );
                taskManager.executeTask( moveCasterBatchPositionTask );
            }
        }
    }

    @FXML
    public void configure( ActionEvent event )
    {
        configureTask.setData( handle.getMachineDTO(), selectedSchedules.get( 0 ) );
        taskManager.executeTask( configureTask );
    }

    private void invalidated( Observable observable )
    {
        loadData();
    }

    private void addCornerMenu()
    {
        // add corner menu
        ImageView hotSpot = new ImageView( ImagesCasting.HOT_SPOT.load() );
        hotSpot.setFitWidth( 16.0 );
        hotSpot.setFitHeight( 16.0 );
        hotSpot.setLayoutX( -3.0 );
        hotSpot.setLayoutY( -3.0 );
        container.getChildren().add( hotSpot );

        cornerMenu = new CornerMenu( CornerMenu.Location.TOP_LEFT, container, false );
        cornerMenu.setAutoShowAndHide( true );
        cornerMenu.setAnimationDuration( Duration.millis( 200 ) );
        ImageView actionIV = new ImageView( ImagesCasting.MAXIMIZE_V.load() );
        actionIV.setFitWidth( 35.0 );
        actionIV.setFitHeight( 35.0 );
        MenuItem actionMI = new MenuItem( "Maximieren", actionIV );
        actionMI.setOnAction( ( event ) -> {
            final List<MasterDetailPane> masterDetailPanes = new ArrayList<>();
            if ( container.getParent() != null && container.getParent().getParent() != null && container.getParent().getParent().getParent() instanceof MasterDetailPane )
            {
                final MasterDetailPane masterDetailPane = (MasterDetailPane) container.getParent().getParent().getParent();
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

    public void reload()
    {
        loadData();
    }
}
