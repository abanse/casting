package com.hydro.casting.gui.planning.view.content;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.ImagesCasting;
import com.hydro.casting.gui.model.CastingClientModel;
import com.hydro.casting.gui.planning.grid.Caster50_60_70GridConfig;
import com.hydro.casting.gui.planning.grid.Caster80GridConfig;
import com.hydro.casting.gui.planning.grid.node.CasterSchedulePosNode;
import com.hydro.casting.gui.planning.task.*;
import com.hydro.casting.server.contract.dto.CasterDemandDTO;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.CasterSchedulePosDTO;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.*;
import com.hydro.core.gui.comp.IntegerTextField;
import com.hydro.core.gui.comp.TaskButton;
import com.hydro.core.gui.comp.TaskProgressPane;
import com.hydro.core.gui.comp.grid.GridContextMenuProvider;
import com.hydro.core.gui.comp.grid.GridDragAndDropProvider;
import com.hydro.core.gui.comp.grid.GridView;
import com.hydro.core.gui.comp.grid.model.GridModel;
import com.hydro.core.gui.model.ClientModel;
import impl.org.controlsfx.spreadsheet.CellView;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;
import jfxtras.scene.menu.CornerMenu;
import org.controlsfx.control.MasterDetailPane;
import org.controlsfx.control.spreadsheet.CellGraphicFactory;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MachineScheduleController
{
    private final static Logger log = LoggerFactory.getLogger( MachineScheduleController.class );

    @FXML
    public BorderPane container;
    @FXML
    public TaskButton targetSelection;
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
    private NotifyManager notifyManager;
    @Inject
    private SecurityManager securityManager;
    @Inject
    private EditProcessOrderTask editProcessOrderTask;
    @Inject
    private EditAlloySourcePercentageTask editAlloySourcePercentageTask;

    @Inject
    private TaskManager taskManager;
    @FXML
    private GridView<CasterScheduleDTO> gridView;
    @FXML
    private TaskButton delete;
    @FXML
    private TaskButton copy;
    @FXML
    private Label remarkDnDComponent;
    @FXML
    private Label setupDnDComponent;
    @FXML
    private TaskProgressPane taskProgressPane;
    @FXML
    private VBox functionBox;

    private MaBeHandle handle;
    private ObservableList<CasterDemandDTO> selectedDemands;
    private ObservableList<CasterScheduleDTO> selectedSchedules;
    private ObservableList<CasterSchedulePosDTO> selectedSchedulePoss;

    private ClientModel castingModel;

    @Inject
    private DeleteCasterBatchPositionTask deleteCasterBatchPositionTask;

    @Inject
    private CopyCasterBatchTask copyCasterBatchTask;

    @Inject
    private SetCasterBatchPositionTask setCasterBatchPositionTask;

    @Inject
    private MoveCasterBatchPositionTask moveCasterBatchPositionTask;

    @Inject
    private ReleaseSchedulableTask releaseSchedulableTask;

    @Inject
    private CreateSetupTask createSetup;
    @Inject
    private DeleteSetupTask deleteSetupTask;
    @Inject
    private EditAnnotationTask editAnnotationTask;
    @Inject
    private MoveEntriesTask moveEntries;
    @Inject
    private ConfigureTask configureTask;
    @Inject
    private ReorganizeChargeTask reorganizeChargeTask;

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

    /*
    private Callback<List<PlanningScheduleDTO>, Boolean> modifyPossibleCallback = new Callback<List<PlanningScheduleDTO>, Boolean>()
    {
        @Override
        public Boolean call( List<PlanningScheduleDTO> schedules )
        {
            if ( schedules == null || schedules.isEmpty() )
            {
                return false;
            }
            boolean oneIsNotDeletable = false;
            for ( PlanningScheduleDTO schedule : schedules )
            {
                // Setupeinträge
                if ( "SETUP".equals( schedule.getObjid() ) )
                {
                    continue;
                }
                if ( schedule.getExecutionState() >= PP.ACTION_STATE.IN_PROGRESS )
                {
                    oneIsNotDeletable = true;
                    break;
                }
            }
            return !oneIsNotDeletable;
        }
    };
    private Callback<List<PlanningScheduleDTO>, Boolean> movePossibleCallback = new Callback<List<PlanningScheduleDTO>, Boolean>()
    {
        @Override
        public Boolean call( List<PlanningScheduleDTO> schedules )
        {
            return true;
        }
    };

     */
    private Callback<List<CasterScheduleDTO>, Boolean> deletePossibleCallback = new Callback<List<CasterScheduleDTO>, Boolean>()
    {
        @Override
        public Boolean call( List<CasterScheduleDTO> schedules )
        {
            return !delete.isDisable();
        }
    };

    private CornerMenu cornerMenu;

    public void initialize( MaBeHandle handle, ObservableList<CasterDemandDTO> selectedDemands, ObservableList<CasterScheduleDTO> selectedSchedules,
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
        taskProgressPane.addTask( deleteSetupTask );
        taskProgressPane.addTask( editAnnotationTask );
        taskProgressPane.addTask( deleteCasterBatchPositionTask );
        taskProgressPane.addTask( setCasterBatchPositionTask );
        taskProgressPane.addTask( moveCasterBatchPositionTask );
        taskProgressPane.addTask( moveEntries );
        taskProgressPane.addTask( configureTask );
        taskProgressPane.addTask( releaseSchedulableTask );
        taskProgressPane.addTask( copyCasterBatchTask );
        taskProgressPane.addTask( reorganizeChargeTask );

        targetSelection.setDisable( false );

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

        delete.setLocked( !securityManager.hasRole( SecurityCasting.MABE.ACTION.EDIT_ENTRY ) );
        copy.setLocked( !securityManager.hasRole( SecurityCasting.MABE.ACTION.EDIT_ENTRY ) );

        FilteredList<CasterScheduleDTO> selectedSetups = selectedSchedules.filtered( casterScheduleDTO -> {
            if ( "setup".equals( casterScheduleDTO.getType() ) )
            {
                return true;
            }
            return false;
        } );
        copy.disableProperty().bind( Bindings.size( selectedSchedules ).isNotEqualTo( 1 ).or( Bindings.isNotEmpty( selectedSetups ) ) );

        /*
        selectedItems.addListener( (InvalidationListener) ( p ) -> {
            revalidateButtonStates();
        } );
         */

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
                        if ( !handle.getMachineScheduleController().isDropAllowed( dragEvent.getDragboard().getString(), casterSchedulePosNode.getCasterSchedulePos() ) )
                        {
                            dragEvent.consume();
                            dropTargetNode.set( null );
                            return true;
                        }
                        dragEvent.acceptTransferModes( TransferMode.ANY );
                        dropTargetNode.set( casterSchedulePosNode );
                        dragEvent.consume();
                        return true;
                    }
                    dragEvent.consume();
                    dropTargetNode.set( null );
                    return true;
                }
                else
                {
                    if ( MaBeController.isCasterMaterialTypeContent( dragEvent.getDragboard().getString() ) || MaBeController.isCasterDemandsContent( dragEvent.getDragboard().getString() ) )
                    {
                        final CasterScheduleDTO casterScheduleDTO = gridModel.getRows().get( cellView.getTableRow().getIndex() );
                        if ( casterScheduleDTO == null )
                        {
                            handle.postValidationMessage( "Es wurde keine Zeile gefunden" );
                            dragEvent.consume();
                            dropTargetNode.set( null );
                            return true;
                        }
                        final CasterSchedulePosDTO firstEmptyCasterSchedulePosDTO = findFirstEmptyPos( casterScheduleDTO );
                        if ( firstEmptyCasterSchedulePosDTO == null )
                        {
                            handle.postValidationMessage( "Es wurde keine freie Position gefunden" );
                            dragEvent.consume();
                            dropTargetNode.set( null );
                            return true;
                        }

                        if ( !handle.getMachineScheduleController().isDropAllowed( dragEvent.getDragboard().getString(), firstEmptyCasterSchedulePosDTO ) )
                        {
                            dragEvent.consume();
                            dropTargetNode.set( null );
                            return true;
                        }
                        dragEvent.acceptTransferModes( TransferMode.ANY );
                        dragEvent.consume();
                        return true;
                    }
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
                        boolean isShiftDown = dragEvent.getTransferMode() == TransferMode.COPY;
                        handle.getMachineScheduleController().handleDrop( dragEvent.getDragboard().getString(), casterSchedulePosNode.getCasterSchedulePos(), isShiftDown );
                    }
                    dragEvent.consume();
                    dropTargetNode.set( null );
                    return true;
                }
                else if ( MaBeController.isCasterMaterialTypeContent( dragEvent.getDragboard().getString() ) || MaBeController.isCasterDemandsContent( dragEvent.getDragboard().getString() ) )
                {
                    final CasterScheduleDTO casterScheduleDTO = gridModel.getRows().get( cellView.getTableRow().getIndex() );
                    if ( casterScheduleDTO != null )
                    {
                        final CasterSchedulePosDTO firstEmptyCasterSchedulePosDTO = findFirstEmptyPos( casterScheduleDTO );
                        if ( firstEmptyCasterSchedulePosDTO != null )
                        {
                            boolean isShiftDown = dragEvent.getTransferMode() == TransferMode.COPY;
                            handle.getMachineScheduleController().handleDrop( dragEvent.getDragboard().getString(), firstEmptyCasterSchedulePosDTO, isShiftDown );
                        }
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

        gridView.setGridContextMenuProvider( new GridContextMenuProvider<CasterScheduleDTO>()
        {
            MenuItem reorganizeChargeMenuItem = new MenuItem( "Chargennummer reorganisieren \u2193" );

            {
                reorganizeChargeMenuItem.setOnAction( event -> {
                    final List<TablePosition> positions = gridView.getSelectionModel().getSelectedCells();
                    final Optional<TablePosition> positionOptional = positions.stream().findFirst();
                    if ( !positionOptional.isPresent() )
                    {
                        return;
                    }
                    final TablePosition position = positionOptional.get();
                    final CasterScheduleDTO casterScheduleDTO = gridModel.getRows().get( position.getRow() );
                    reorganizeChargeTask.setData( casterScheduleDTO );
                    taskManager.executeTask( reorganizeChargeTask );
                } );
            }

            @Override
            public List<MenuItem> getExtMenuItems()
            {
                final List<MenuItem> extMenuItems = new ArrayList<>();
                extMenuItems.add( new SeparatorMenuItem() );
                extMenuItems.add( reorganizeChargeMenuItem );
                return extMenuItems;
            }

            @Override
            public void handleOnShown( WindowEvent event )
            {
                final List<TablePosition> positions = gridView.getSelectionModel().getSelectedCells();
                final Optional<TablePosition> positionOptional = positions.stream().findFirst();
                if ( !positionOptional.isPresent() )
                {
                    reorganizeChargeMenuItem.setDisable( true );
                }
                final TablePosition position = positionOptional.get();
                boolean reorganizePossible = true;
                if ( Casting.MACHINE.CASTER_80.equals( handle.getCostCenter() ) )
                {
                    if ( position.getColumn() != 9 )
                    {
                        reorganizePossible = false;
                    }
                }
                else
                {
                    if ( position.getColumn() != 8 )
                    {
                        reorganizePossible = false;
                    }
                }
                final CasterScheduleDTO casterScheduleDTO = gridModel.getRows().get( position.getRow() );
                if ( casterScheduleDTO == null || casterScheduleDTO.getExecutionState() != Casting.SCHEDULABLE_STATE.RELEASED )
                {
                    reorganizePossible = false;
                }
                reorganizeChargeMenuItem.setDisable( !reorganizePossible );
            }
        } );

        gridView.setCellGraphicFactory( new CellGraphicFactory()
        {
            @Override
            public Node getNode( SpreadsheetCell spreadsheetCell )
            {
                return new CasterSchedulePosNode( handle );
            }

            @Override
            public void load( Node node, SpreadsheetCell spreadsheetCell )
            {
                CasterSchedulePosNode casterSchedulePosNode = (CasterSchedulePosNode) node;
                casterSchedulePosNode.setCasterSchedulePos( (CasterSchedulePosDTO) spreadsheetCell.getItem() );
            }

            @Override
            public void loadStyle( Node node, SpreadsheetCell spreadsheetCell, Font font, Paint paint, Pos pos, Background background )
            {
            }

            @Override
            public void setUnusedNode( Node node )
            {
            }

            @Override
            public Class getType()
            {
                return CasterSchedulePosNode.class;
            }
        } );


        /*
        gridView.setEditable( false );

        gridView.setMovePossibleCallback( movePossibleCallback );


        */
        gridView.setDeletePossibleCallback( deletePossibleCallback );
        gridView.setDeleteCallback( ( schedules ) -> {
            delete( null );
            return true;
        } );

        if ( Casting.MACHINE.CASTER_80.equals( handle.getCostCenter() ) )
        {
            new Caster80GridConfig( castingModel ).configure( gridModel );
            castingModel.addRelationListener( CastingClientModel.CASTER80, this::invalidated );
        }
        else if ( Casting.MACHINE.CASTER_50.equals( handle.getCostCenter() ) )
        {
            new Caster50_60_70GridConfig( castingModel ).configure( gridModel );
            castingModel.addRelationListener( CastingClientModel.CASTER50, this::invalidated );
        }
        else if ( Casting.MACHINE.CASTER_60.equals( handle.getCostCenter() ) )
        {
            new Caster50_60_70GridConfig( castingModel ).configure( gridModel );
            castingModel.addRelationListener( CastingClientModel.CASTER60, this::invalidated );
        }
        else if ( Casting.MACHINE.CASTER_70.equals( handle.getCostCenter() ) )
        {
            new Caster50_60_70GridConfig( castingModel ).configure( gridModel );
            castingModel.addRelationListener( CastingClientModel.CASTER70, this::invalidated );
        }

        //setupTypeContextMenu.initialize( gridView, costCenter );

        loadData();

        gridView.setFixingColumnsAllowed( false );
        gridView.setFixingRowsAllowed( false );

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
                    int newIntValue = -1;
                    try
                    {
                        newIntValue = Integer.parseInt( (String) newValue.getNewValue() );
                    }
                    catch ( NumberFormatException nfex )
                    {
                        // ignore
                    }
                    if ( newIntValue < 0 )
                    {
                        return;
                    }

                    editAlloySourcePercentageTask.setData( casterScheduleDTO, propertyName.substring( 10 ), newIntValue );
                    taskManager.executeTask( editAlloySourcePercentageTask );
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
        validateSchedules();
        validateSchedulePositions();
    }

    private void validateSchedules()
    {
        if ( selectedSchedules.isEmpty() )
        {
            release.setDisable( true, null );
            configure.setDisable( true, null );
            return;
        }
        boolean oneFixed = false;
        for ( CasterScheduleDTO selectedSchedule : selectedSchedules )
        {
            if ( selectedSchedule.getExecutionState() > Casting.SCHEDULABLE_STATE.PLANNED )
            {
                oneFixed = true;
                break;
            }
        }
        if ( oneFixed )
        {
            release.setDisable( true, "Ein oder mehrere Zeilen sind bereits an die Produktion übergeben (fixiert oder größer)" );
        }
        else
        {
            release.setDisable( false, null );
        }
        if ( selectedSchedules.size() > 1 )
        {
            configure.setDisable( true, "Es sind mehr als eine Zeile selektiert" );
        }
        else
        {
            configure.setDisable( false, null );
        }
    }

    private void validateSchedulePositions()
    {
        if ( selectedSchedulePoss.isEmpty() )
        {
            delete.setDisable( true, null );
            return;
        }
        boolean oneFixed = false;
        for ( CasterSchedulePosDTO selectedSchedulePos : selectedSchedulePoss )
        {
            if ( selectedSchedulePos.getCasterSchedule().getExecutionState() > Casting.SCHEDULABLE_STATE.PLANNED )
            {
                oneFixed = true;
                break;
            }
        }
        if ( oneFixed )
        {
            delete.setDisable( true, "Ein oder mehrere Zeilen sind bereits an die Produktion übergeben (fixiert oder größer)" );
        }
        else
        {
            delete.setDisable( false, null );
        }
    }

    void loadData()
    {
        if ( gridView.getEditingCell() != null )
        {
            log.info( "ignore reload when editing" );
            return;
        }

        final List<CasterScheduleDTO> casterSchedules;
        if ( Casting.MACHINE.CASTER_50.equals( handle.getCostCenter() ) )
        {
            casterSchedules = castingModel.getView( CastingClientModel.CASTER50_VIEW_ID );
        }
        else if ( Casting.MACHINE.CASTER_60.equals( handle.getCostCenter() ) )
        {
            casterSchedules = castingModel.getView( CastingClientModel.CASTER60_VIEW_ID );
        }
        else if ( Casting.MACHINE.CASTER_70.equals( handle.getCostCenter() ) )
        {
            casterSchedules = castingModel.getView( CastingClientModel.CASTER70_VIEW_ID );
        }
        else
        {
            casterSchedules = castingModel.getView( CastingClientModel.CASTER80_VIEW_ID );
        }

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

    @FXML
    void delete( ActionEvent event )
    {
        if ( !selectedSchedules.isEmpty() )
        {
            // prüfen auf setups
            final List<CasterScheduleDTO> setups = new ArrayList<>();
            for ( CasterScheduleDTO selectedSchedule : selectedSchedules )
            {
                if ( "setup".equals( selectedSchedule.getType() ) )
                {
                    setups.add( selectedSchedule );
                }
            }
            if ( !setups.isEmpty() )
            {
                deleteSetupTask.setData( setups );
                taskManager.executeTask( deleteSetupTask );
                return;
            }
        }
        deleteCasterBatchPositionTask.setData( selectedSchedulePoss );
        taskManager.executeTask( deleteCasterBatchPositionTask );
    }

    @FXML
    void copy( ActionEvent event )
    {
        if ( selectedSchedules.isEmpty() )
        {
            return;
        }
        copyCasterBatchTask.setData( selectedSchedules.get( 0 ) );
        taskManager.executeTask( copyCasterBatchTask );
    }

    @FXML
    void release( ActionEvent event )
    {
        releaseSchedulableTask.setData( selectedSchedules );
        taskManager.executeTask( releaseSchedulableTask );
    }

    public GridView<CasterScheduleDTO> getGridView()
    {
        return gridView;
    }

    public void handleSetPositions()
    {
        handleSetPositions( null );
    }

    public void handleSetPositions( Integer count )
    {
        setCasterBatchPositionTask.setData( selectedDemands, selectedSchedulePoss, count );
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
                if ( selectedPos.getCasterSchedule() != null && selectedPos.getCasterSchedule().getExecutionState() > Casting.SCHEDULABLE_STATE.PLANNED )
                {
                    handle.postValidationMessage( "Die Ziel-Position/en sind bereits der Produktion übergeben (fixiert oder weiter)" );
                    return false;
                }
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

        if ( targetPosition.getCasterSchedule() != null && targetPosition.getCasterSchedule().getExecutionState() > Casting.SCHEDULABLE_STATE.PLANNED )
        {
            handle.postValidationMessage( "Die Ziel-Position ist bereits an die Produktion übergeben (fixiert oder weiter)" );
            return false;
        }

        if ( !Objects.equals( sourceAlloy, targetAlloy ) )
        {
            handle.postValidationMessage( "Die Legierung der Ziel-Position/en passen nicht zur ausgewählten Legierung" );
            return false;
        }
        handle.clearValidationMessage();
        return true;
    }

    public void handleDrop( String dragboardString, CasterSchedulePosDTO targetPosition, boolean withShift )
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
                Integer countValue = null;
                if ( withShift )
                {
                    final HBox countPane = new HBox();
                    countPane.setAlignment( Pos.CENTER );
                    countPane.setSpacing( 5.0 );
                    final IntegerTextField count = new IntegerTextField();
                    count.setPrefWidth( 80 );
                    count.setIntValue( 2 );
                    countPane.getChildren().addAll( new Label( "Anzahl" ), count );
                    final ButtonType result = notifyManager.showCompInputMessage( "Mehrere Einträge einfügen (selektierte Position)", "Bitte geben Sie die Anzahl der Einträge an", countPane, count );
                    if ( result != ButtonType.OK )
                    {
                        return;
                    }
                    countValue = count.getIntValue();
                }
                setCasterBatchPositionTask.setData( selectedDemands, selectedSchedulePoss, countValue );
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
                Integer countValue = null;
                if ( withShift )
                {
                    final HBox countPane = new HBox();
                    countPane.setAlignment( Pos.CENTER );
                    countPane.setSpacing( 5.0 );
                    final IntegerTextField count = new IntegerTextField();
                    count.setPrefWidth( 80 );
                    count.setIntValue( 2 );
                    countPane.getChildren().addAll( new Label( "Anzahl" ), count );
                    final ButtonType result = notifyManager.showCompInputMessage( "Mehrere Einträge einfügen (selektierte Position)", "Bitte geben Sie die Anzahl der Einträge an", countPane, count );
                    if ( result != ButtonType.OK )
                    {
                        return;
                    }
                    countValue = count.getIntValue();
                }
                setCasterBatchPositionTask.setData( selectedDemands, Collections.singletonList( targetPosition ), countValue );
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
    public void targetSelection( ActionEvent actionEvent )
    {
        final List<String> selectedMaterialTypes = new ArrayList<>();
        for ( CasterDemandDTO selectedDemand : selectedDemands )
        {
            selectedMaterialTypes.add( selectedDemand.getMaterialType() );
        }

        if ( selectedMaterialTypes.isEmpty() )
        {
            return;
        }

        final List<CasterScheduleDTO> collectedItems = new ArrayList<>();
        for ( CasterScheduleDTO planningSchedule : gridModel.getRows() )
        {
            for ( String selectedMaterialType : selectedMaterialTypes )
            {
                if ( Objects.equals( selectedMaterialType, planningSchedule.getPos1MaterialType() ) || Objects.equals( selectedMaterialType, planningSchedule.getPos2MaterialType() ) || Objects.equals(
                        selectedMaterialType, planningSchedule.getPos3MaterialType() ) || Objects.equals( selectedMaterialType, planningSchedule.getPos4MaterialType() ) || Objects.equals(
                        selectedMaterialType, planningSchedule.getPos5MaterialType() ) )
                {
                    collectedItems.add( planningSchedule );
                    break;
                }
            }
        }

        gridView.getSelectionModel().clearSelection();

        int firstSelectedIndex = -1;
        if ( !collectedItems.isEmpty() )
        {
            final List<Integer> toSelectedRows = new ArrayList<>();
            for ( CasterScheduleDTO collectedItem : collectedItems )
            {
                final int row = gridModel.getRows().indexOf( collectedItem );
                if ( row < 0 )
                {
                    continue;
                }
                if ( firstSelectedIndex < 0 )
                {
                    firstSelectedIndex = row;
                }
                toSelectedRows.add( row );
            }
            if ( !toSelectedRows.isEmpty() )
            {
                gridView.selectRows( toSelectedRows );
            }
        }
        if ( firstSelectedIndex >= 0 )
        {
            gridView.scrollToRow( firstSelectedIndex );
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

    private CasterSchedulePosDTO findFirstEmptyPos( CasterScheduleDTO casterScheduleDTO )
    {
        CasterSchedulePosDTO firstEmptyCasterSchedulePosDTO = null;
        if ( casterScheduleDTO.getPos1().getMaterialType() == null )
        {
            firstEmptyCasterSchedulePosDTO = casterScheduleDTO.getPos1();
        }
        else if ( casterScheduleDTO.getPos2().getMaterialType() == null )
        {
            firstEmptyCasterSchedulePosDTO = casterScheduleDTO.getPos2();
        }
        else if ( casterScheduleDTO.getPos3().getMaterialType() == null )
        {
            firstEmptyCasterSchedulePosDTO = casterScheduleDTO.getPos3();
        }
        else if ( casterScheduleDTO.getPos4().getMaterialType() == null )
        {
            firstEmptyCasterSchedulePosDTO = casterScheduleDTO.getPos4();
        }
        else if ( Casting.MACHINE.CASTER_80.equals( casterScheduleDTO.getMachine() ) && casterScheduleDTO.getPos5().getMaterialType() == null )
        {
            firstEmptyCasterSchedulePosDTO = casterScheduleDTO.getPos5();
        }
        return firstEmptyCasterSchedulePosDTO;
    }
}
