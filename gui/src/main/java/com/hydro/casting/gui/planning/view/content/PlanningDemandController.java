package com.hydro.casting.gui.planning.view.content;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.model.CastingClientModel;
import com.hydro.casting.gui.planning.table.CasterDemandTableController;
import com.hydro.casting.gui.planning.table.grouping.CasterDemandMaterialTypeTableGroup;
import com.hydro.casting.gui.planning.table.grouping.CasterDemandTableGroup;
import com.hydro.casting.gui.planning.task.CreateCasterBatchTask;
import com.hydro.casting.server.contract.dto.CasterDemandDTO;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.CasterSchedulePosDTO;
import com.hydro.casting.server.contract.dto.SlabDTO;
import com.hydro.core.common.util.BitSetTools;
import com.hydro.core.common.util.StringTools;
import com.hydro.core.common.util.TextFieldUtil;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.*;
import com.hydro.core.gui.comp.CacheTreeTableView;
import com.hydro.core.gui.comp.IntegerTextField;
import com.hydro.core.gui.comp.SearchBox;
import com.hydro.core.gui.comp.TaskButton;
import com.hydro.core.gui.model.ClientModel;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.controlsfx.control.textfield.CustomTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class PlanningDemandController
{
    private final static Logger log = LoggerFactory.getLogger( PlanningDemandController.class );

    private final static String PREF_PATH_PREFIX = "/mes/planningDemand";
    private final static String PREF_KEY = "lastDemandAvailableSelection";

    @FXML
    private BorderPane mainPane;
    @FXML
    private CheckBox free;
    @FXML
    private CheckBox planned;
    @FXML
    private CheckBox all;
    @FXML
    private TaskButton targetSelection;
    @FXML
    private CustomTextField alloy;
    @FXML
    private CustomTextField width;
    @FXML
    private CustomTextField length;
    @Inject
    private Injector injector;
    @Inject
    private PreferencesManager preferencesManager;
    @Inject
    private SecurityManager securityManager;
    @Inject
    private TaskManager taskManager;
    @Inject
    private ClientModelManager clientModelManager;
    @Inject
    private CreateCasterBatchTask createCasterBatchTask;
    @Inject
    private NotifyManager notifyManager;
    @FXML
    private SearchBox searchBox;
    @FXML
    private HBox summaryDemand;
    @FXML
    private HBox summaryDemandMaterialType;
    @FXML
    private TaskButton add;
    @FXML
    private TaskButton addN;
    @FXML
    private TaskButton append;
    @FXML
    private TaskButton appendN;
    @FXML
    private TabPane tableTapPane;
    @FXML
    private CacheTreeTableView<CasterDemandDTO> tableDemand;
    @FXML
    private CasterDemandTableController tableDemandController;
    @FXML
    private CacheTreeTableView<CasterDemandDTO> tableMaterialType;

    private MaBeHandle handle;
    private ObservableList<CasterDemandDTO> selectedDemands;
    private ObservableList<CasterScheduleDTO> selectedSchedules;
    private ObservableList<CasterSchedulePosDTO> selectedSchedulePoss;

    private ClientModel clientModel;

    public void initialize( MaBeHandle handle, ObservableList<CasterDemandDTO> selectedDemands, ObservableList<CasterScheduleDTO> selectedSchedules,
            ObservableList<CasterSchedulePosDTO> selectedSchedulePoss )
    {
        this.handle = handle;
        this.selectedDemands = selectedDemands;
        this.selectedSchedules = selectedSchedules;
        this.selectedSchedulePoss = selectedSchedulePoss;

        clientModel = clientModelManager.getClientModel( CastingClientModel.ID );

        loadPreferences();

        TextFieldUtil.setupClearButtonField( width, width.rightProperty() );
        TextFieldUtil.setupClearButtonField( length, length.rightProperty() );
        TextFieldUtil.setupClearButtonField( alloy, alloy.rightProperty() );

        width.textProperty().addListener( ( InvalidationListener ) -> {
            tableDemand.reload();
            tableMaterialType.reload();
        } );
        length.textProperty().addListener( ( InvalidationListener ) -> {
            tableDemand.reload();
            tableMaterialType.reload();
        } );
        alloy.textProperty().addListener( ( InvalidationListener ) -> {
            tableDemand.reload();
            tableMaterialType.reload();
        } );

        tableDemandController.initialize( handle.getCostCenter() );

        tableDemand.setAppFilter( this::filterDemand );
        tableDemand.setSummary( summaryDemand );
        tableDemand.connect( injector, new CasterDemandTableGroup() );
        tableDemand.filterProperty().bind( searchBox.textProperty() );
        tableDemand.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        tableDemand.getSelectionModel().getSelectedItems().addListener( (InvalidationListener) ( observable ) -> {
            final List<CasterDemandDTO> selectedCasterDemandDTOs = new ArrayList<>();
            for ( TreeItem<CasterDemandDTO> selectedItem : tableDemand.getSelectionModel().getSelectedItems() )
            {
                selectedCasterDemandDTOs.add( selectedItem.getValue() );
            }
            selectedDemands.setAll( selectedCasterDemandDTOs );
        } );

        tableMaterialType.setAppFilter( this::filterMaterialType );
        tableMaterialType.setSummary( summaryDemandMaterialType );
        tableMaterialType.connect( injector, new CasterDemandMaterialTypeTableGroup() );
        tableMaterialType.filterProperty().bind( searchBox.textProperty() );
        tableMaterialType.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        tableMaterialType.getSelectionModel().getSelectedItems().addListener( (InvalidationListener) ( observable ) -> {
            final List<CasterDemandDTO> selectedCasterDemandDTOs = new ArrayList<>();
            for ( TreeItem<CasterDemandDTO> selectedItem : tableMaterialType.getSelectionModel().getSelectedItems() )
            {
                selectedCasterDemandDTOs.add( selectedItem.getValue() );
            }
            selectedDemands.setAll( selectedCasterDemandDTOs );
        } );

        tableTapPane.getSelectionModel().selectedIndexProperty().addListener( observable -> {
            if ( tableTapPane.getSelectionModel().getSelectedIndex() == 0 )
            {
                summaryDemandMaterialType.setVisible( false );
                summaryDemand.setVisible( true );
            }
            else
            {
                summaryDemandMaterialType.setVisible( true );
                summaryDemand.setVisible( false );
            }
            tableDemand.getSelectionModel().clearSelection();
            tableMaterialType.getSelectionModel().clearSelection();
        } );

        tableDemand.getTable().setOnDragDetected( ( event ) -> {
            if ( event.getY() < 30 )
            {
                return;
            }
            List<CasterDemandDTO> selectedRows = tableDemand.getSelectedValues();
            if ( selectedRows == null || selectedRows.isEmpty() )
            {
                return;
            }
            final TransferMode transferMode;
            if ( event.isShiftDown() )
            {
                transferMode = TransferMode.COPY;
            }
            else
            {
                transferMode = TransferMode.MOVE;
            }
            Dragboard dragboard = tableDemand.startDragAndDrop( transferMode );
            dragboard.setDragView( null );

            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString( MaBeController.getDemandDragboardString( selectedDemands ) );
            dragboard.setContent( clipboardContent );

            event.consume();
        } );

        tableMaterialType.getTable().setOnDragDetected( ( event ) -> {
            if ( event.getY() < 30 )
            {
                return;
            }
            List<CasterDemandDTO> selectedRows = tableMaterialType.getSelectedValues();
            if ( selectedRows == null || selectedRows.isEmpty() )
            {
                return;
            }
            final TransferMode transferMode;
            if ( event.isShiftDown() )
            {
                transferMode = TransferMode.COPY;
            }
            else
            {
                transferMode = TransferMode.MOVE;
            }
            Dragboard dragboard = tableMaterialType.startDragAndDrop( transferMode );
            dragboard.setDragView( null );

            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString( MaBeController.getMaterialTypeDragboardString( selectedDemands ) );
            dragboard.setContent( clipboardContent );

            event.consume();
        } );

        free.setOnAction( event -> {
            tableDemand.reload();
            savePreferences();
        } );
        planned.setOnAction( event -> {
            tableDemand.reload();
            savePreferences();
        } );

        all.setOnAction( event -> {
            free.setSelected( all.isSelected() );
            planned.setSelected( all.isSelected() );
            tableDemand.reload();
            savePreferences();
        } );

        createCasterBatchTask.setMachineApk( handle.getCostCenter() );

        selectedDemands.addListener( (InvalidationListener) observable -> validateActions() );
        selectedSchedulePoss.addListener( (InvalidationListener) observable -> validateActions() );

        targetSelection.setDisable( false );
        add.setLocked( !securityManager.hasRole( SecurityCasting.MABE.ACTION.CREATE_ENTRY ) );
        addN.setLocked( !securityManager.hasRole( SecurityCasting.MABE.ACTION.CREATE_ENTRY ) );
        append.setLocked( !securityManager.hasRole( SecurityCasting.MABE.ACTION.CREATE_ENTRY ) );
        appendN.setLocked( !securityManager.hasRole( SecurityCasting.MABE.ACTION.CREATE_ENTRY ) );

        validateActions();
    }

    private void validateActions()
    {
        boolean demandsWithDifferentAlloys = false;
        boolean demandNotValid = false;
        String demandAlloy = null;
        int maxLength = 0;
        for ( CasterDemandDTO selectedDemand : selectedDemands )
        {
            if ( selectedDemand.getOrderId() == null && selectedDemand.getOrderPosition() == null && selectedDemand.getMaterialType() == null )
            {
                demandNotValid = true;
            }
            int demandLength = selectedDemand.getLength();
            if ( Casting.MACHINE.CASTER_80.equals( handle.getCostCenter() ) && selectedDemand.getDoubleLength() != null )
            {
                demandLength = selectedDemand.getDoubleLength();
            }
            if ( demandLength > maxLength )
            {
                maxLength = demandLength;
            }
            if ( demandAlloy == null )
            {
                demandAlloy = selectedDemand.getAlloy();
                continue;
            }
            if ( !Objects.equals( demandAlloy, selectedDemand.getAlloy() ) )
            {
                demandsWithDifferentAlloys = true;
                break;
            }
        }

        // Check alloy
        if ( selectedDemands.isEmpty() )
        {
            add.setDisable( true, null );
            addN.setDisable( true, null );
            append.setDisable( true, null );
            appendN.setDisable( true, null );
        }
        else if ( selectedSchedulePoss.isEmpty() )
        {
            add.setDisable( true, null );
            addN.setDisable( true, null );
            if ( demandNotValid )
            {
                append.setDisable( true, "Es ist eine Gruppe von Kundenaufträgen selektiert worden" );
                appendN.setDisable( true, "Es ist eine Gruppe von Kundenaufträgen selektiert worden" );
            }
            else if ( demandsWithDifferentAlloys )
            {
                append.setDisable( true, "Es sind unterschiedliche Legierungen selektiert worden" );
                appendN.setDisable( true, "Es sind unterschiedliche Legierungen selektiert worden" );
            }
            else if ( maxLength > handle.getMachineDTO().getMaxCastingLength() )
            {
                append.setDisable( true, "Die Vorgabe-Länge überschreitet die max. Giesslänge" );
                appendN.setDisable( true, "Die Vorgabe-Länge überschreitet die max. Giesslänge" );
            }
            else
            {
                append.setDisable( false, null );
                appendN.setDisable( false, null );
            }
        }
        else
        {
            if ( demandNotValid )
            {
                add.setDisable( true, "Es ist eine Gruppe von Kundenaufträgen selektiert worden" );
                addN.setDisable( true, "Es ist eine Gruppe von Kundenaufträgen selektiert worden" );
                append.setDisable( true, "Es ist eine Gruppe von Kundenaufträgen selektiert worden" );
                appendN.setDisable( true, "Es ist eine Gruppe von Kundenaufträgen selektiert worden" );
            }
            else if ( demandsWithDifferentAlloys )
            {
                add.setDisable( true, "Es sind unterschiedliche Legierungen selektiert worden" );
                addN.setDisable( true, "Es sind unterschiedliche Legierungen selektiert worden" );
                append.setDisable( true, "Es sind unterschiedliche Legierungen selektiert worden" );
                appendN.setDisable( true, "Es sind unterschiedliche Legierungen selektiert worden" );
            }
            else if ( maxLength > handle.getMachineDTO().getMaxCastingLength() )
            {
                add.setDisable( true, "Die Vorgabe-Länge überschreitet die max. Giesslänge" );
                addN.setDisable( true, "Die Vorgabe-Länge überschreitet die max. Giesslänge" );
                append.setDisable( true, "Die Vorgabe-Länge überschreitet die max. Giesslänge" );
                appendN.setDisable( true, "Die Vorgabe-Länge überschreitet die max. Giesslänge" );
            }
            else
            {
                // Prüfen ob Zielselektion zur Legierung passt
                boolean targetPosWithDifferentAlloys = false;
                String targetAlloy = null;
                boolean oneIsFixed = false;
                for ( CasterSchedulePosDTO targetPos : selectedSchedulePoss )
                {
                    if ( targetPos.getCasterSchedule() != null && targetPos.getCasterSchedule().getExecutionState() > Casting.SCHEDULABLE_STATE.PLANNED )
                    {
                        oneIsFixed = true;
                    }
                    if ( targetAlloy == null )
                    {
                        targetAlloy = targetPos.getCasterSchedule().getAlloy();
                        continue;
                    }
                    if ( !Objects.equals( targetAlloy, targetPos.getCasterSchedule().getAlloy() ) )
                    {
                        targetPosWithDifferentAlloys = true;
                        break;
                    }
                }
                if ( targetAlloy == null )
                {
                    targetAlloy = demandAlloy;
                }
                if ( targetPosWithDifferentAlloys )
                {
                    add.setDisable( true, "Die Ziel-Position/en sind nicht Legierungsrein" );
                    addN.setDisable( true, "Die Ziel-Position/en sind nicht Legierungsrein" );
                }
                else if ( !Objects.equals( demandAlloy, targetAlloy ) )
                {
                    add.setDisable( true, "Die Legierung der Ziel-Position/en passen nicht zur ausgewählten Legierung" );
                    addN.setDisable( true, "Die Legierung der Ziel-Position/en passen nicht zur ausgewählten Legierung" );
                }
                else if ( oneIsFixed )
                {
                    add.setDisable( true, "Die Ziel-Position/en sind bereits der Produktion übergeben (fixiert oder weiter)" );
                    addN.setDisable( true, "Die Ziel-Position/en sind bereits der Produktion übergeben (fixiert oder weiter)" );
                }
                else
                {
                    add.setDisable( false, null );
                    if ( selectedSchedules.size() > 1 )
                    {
                        addN.setDisable( true, "Mehrere Zeilen als Ziel selektiert" );
                    }
                    else
                    {
                        addN.setDisable( false, null );
                    }
                }

                append.setDisable( false, null );
                appendN.setDisable( false, null );
            }
        }
    }

    @FXML
    void add( ActionEvent event )
    {
        handle.getMachineScheduleController().handleSetPositions();
    }

    @FXML
    void addN( ActionEvent event )
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
        handle.getMachineScheduleController().handleSetPositions( count.getIntValue() );
    }

    @FXML
    void append( ActionEvent event )
    {
        createCasterBatchTask.setData( selectedDemands, null, true, null );
        taskManager.executeTask( createCasterBatchTask );
    }

    @FXML
    void appendN( ActionEvent event )
    {
        final HBox countPane = new HBox();
        countPane.setAlignment( Pos.CENTER );
        countPane.setSpacing( 5.0 );
        final IntegerTextField count = new IntegerTextField();
        count.setPrefWidth( 80 );
        count.setIntValue( 2 );
        countPane.getChildren().addAll( new Label( "Anzahl" ), count );
        final ButtonType result = notifyManager.showCompInputMessage( "Mehrere Einträge ans Ende anfügen", "Bitte geben Sie die Anzahl der Einträge an", countPane, count );
        if ( result != ButtonType.OK )
        {
            return;
        }
        createCasterBatchTask.setData( selectedDemands, null, true, count.getIntValue() );
        taskManager.executeTask( createCasterBatchTask );
    }

    /*
    public ObjectProperty<PlanningDemandDTO> selectedValueProperty()
    {
        return table.selectedValueProperty();
    }

    public ObservableList<TreeItem<PlanningDemandDTO>> getSelectedItems()
    {
        return table.getSelectionModel().getSelectedItems();
    }

    public ViewDTOProvider<PlanningDemandDTO> getTable()
    {
        return table;
    }
     */

    private void savePreferences()
    {
        log.info( "save preferences" );
        final BitSet bitSet = new BitSet();
        if ( all.isSelected() )
            bitSet.set( 0 );
        if ( free.isSelected() )
            bitSet.set( 1 );
        if ( planned.isSelected() )
            bitSet.set( 2 );
        preferencesManager.setIntValue( PreferencesManager.SCOPE_USER, PREF_PATH_PREFIX + "/" + handle.getCostCenter(), PREF_KEY, BitSetTools.toInt( bitSet ) );
    }

    private void loadPreferences()
    {
        final CompletableFuture<Integer> future = preferencesManager.getIntValueAsync( PreferencesManager.SCOPE_USER, PREF_PATH_PREFIX + "/" + handle.getCostCenter(), PREF_KEY );
        future.thenRun( () -> {
            Integer prefValue = null;
            try
            {
                prefValue = future.get();
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
            if ( prefValue != null )
            {
                final BitSet bitSet = BitSetTools.toBitSet( prefValue );
                if ( bitSet != null )
                {
                    all.setSelected( bitSet.get( 0 ) );
                    free.setSelected( bitSet.get( 1 ) );
                    planned.setSelected( bitSet.get( 2 ) );
                }
            }
        } );
    }

    private boolean filterDemand( CasterDemandDTO casterDemandDTO )
    {
        return filter( casterDemandDTO, true );
    }

    private boolean filterMaterialType( CasterDemandDTO casterDemandDTO )
    {
        return filter( casterDemandDTO, false );
    }

    private boolean filter( CasterDemandDTO casterDemandDTO, boolean forDemands )
    {
        if ( isFiltered( forDemands ) == false )
        {
            return true;
        }
        boolean hit = true;
        if ( ( free.isSelected() || planned.isSelected() ) && forDemands )
        {
            hit = false;
            if ( free.isSelected() )
            {
                int toPlan = 0;
                final Collection<SlabDTO> assignedSlabs = clientModel.getRelatedEntity( casterDemandDTO, CastingClientModel.SLAB_ASSIGNMENT );
                final Collection<CasterSchedulePosDTO> assignedSchedulePositions = clientModel.getRelatedEntity( casterDemandDTO, CastingClientModel.SCHEDULE_ASSIGNMENT );
                final int toProduce = casterDemandDTO.getToProduce();
                if ( assignedSlabs == null && assignedSchedulePositions == null )
                {
                    toPlan = toProduce;
                }
                else if ( assignedSchedulePositions == null )
                {
                    toPlan = toProduce - assignedSlabs.size();
                }
                else if ( assignedSlabs == null )
                {
                    toPlan = toProduce - assignedSchedulePositions.size();
                }
                else
                {
                    toPlan = toProduce - assignedSlabs.size() - assignedSchedulePositions.size();
                }

                if ( toPlan > 0 )
                {
                    hit = true;
                }
            }
            if ( planned.isSelected() )
            {
                final Collection<CasterSchedulePosDTO> assignedSchedulePositions = clientModel.getRelatedEntity( casterDemandDTO, CastingClientModel.SCHEDULE_ASSIGNMENT );
                int planned = 0;
                if ( assignedSchedulePositions != null )
                {
                    planned = assignedSchedulePositions.size();
                }

                if ( planned > 0 )
                {
                    hit = true;
                }
            }
        }
        if ( hit && StringTools.isFilled( width.getText() ) )
        {
            hit = hitNumberText( casterDemandDTO.getWidth(), width.getText() );
        }
        if ( hit && StringTools.isFilled( length.getText() ) )
        {
            hit = hitNumberText( casterDemandDTO.getLength(), length.getText() );
        }
        if ( hit && StringTools.isFilled( alloy.getText() ) )
        {
            if ( casterDemandDTO.getAlloy() != null && casterDemandDTO.getAlloy().toUpperCase().contains( alloy.getText().toUpperCase() ) )
            {
                hit = true;
            }
            else
            {
                hit = false;
            }
        }
        return hit;
    }

    private boolean hitNumberText( int dtoValue, String searchText )
    {
        boolean hit = false;
        if ( searchText.startsWith( ">" ) && searchText.length() > 1 )
        {
            try
            {
                final int widthNumber = Integer.parseInt( searchText.substring( 1 ) );
                if ( dtoValue > widthNumber )
                {
                    hit = true;
                }
            }
            catch ( NumberFormatException nfex )
            {
                // ignore
            }
        }
        else if ( searchText.startsWith( "<" ) && searchText.length() > 1 )
        {
            try
            {
                final int widthNumber = Integer.parseInt( searchText.substring( 1 ) );
                if ( dtoValue < widthNumber )
                {
                    hit = true;
                }
            }
            catch ( NumberFormatException nfex )
            {
                // ignore
            }
        }
        else if ( ( "" + dtoValue ).equals( searchText ) )
        {
            hit = true;
        }
        return hit;
    }

    private boolean isFiltered( boolean forDemands )
    {
        if ( forDemands )
        {
            return free.isSelected() || planned.isSelected() || StringTools.isFilled( width.getText() ) || StringTools.isFilled( length.getText() ) || StringTools.isFilled( alloy.getText() );
        }
        else
        {
            return StringTools.isFilled( width.getText() ) || StringTools.isFilled( length.getText() ) || StringTools.isFilled( alloy.getText() );
        }
    }

    @FXML
    public void targetSelection( ActionEvent actionEvent )
    {
        final List<TreeItem<CasterDemandDTO>> collectedItems = new ArrayList<>();
        final CacheTreeTableView<CasterDemandDTO> table;
        if ( tableTapPane.getSelectionModel().getSelectedIndex() == 0 )
        {
            table = tableDemand;
        }
        else
        {
            table = tableMaterialType;
        }

        collectItems( collectedItems, table.getRoot(), selectedSchedulePoss );

        table.getSelectionModel().clearSelection();
        int firstSelectedIndex = -1;
        if ( !collectedItems.isEmpty() )
        {
            for ( TreeItem<CasterDemandDTO> collectedItem : collectedItems )
            {
                if ( firstSelectedIndex < 0 )
                {
                    firstSelectedIndex = table.getTreeTableView().getRow( collectedItem );
                }
                table.getSelectionModel().select( collectedItem );
            }
        }
        if ( firstSelectedIndex >= 0 )
        {
            table.getTreeTableView().scrollTo( firstSelectedIndex );
        }
    }

    private void collectItems( final List<TreeItem<CasterDemandDTO>> collectedItems, TreeItem<CasterDemandDTO> item, List<CasterSchedulePosDTO> fittingItems )
    {
        if ( item.getValue() != null && item.getValue().getMaterialType() != null )
        {
            for ( CasterSchedulePosDTO fittingItem : fittingItems )
            {
                if ( Objects.equals( fittingItem.getMaterialType(), item.getValue().getMaterialType() ) )
                {
                    collectedItems.add( item );
                    break;
                }
            }
        }
        if ( item.getChildren() != null )
        {
            for ( TreeItem<CasterDemandDTO> child : item.getChildren() )
            {
                collectItems( collectedItems, child, fittingItems );
            }
        }
    }
}
