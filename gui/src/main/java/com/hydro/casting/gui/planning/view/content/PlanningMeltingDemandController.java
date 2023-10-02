package com.hydro.casting.gui.planning.view.content;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.Casting;
import com.hydro.casting.gui.model.*;
import com.hydro.casting.gui.gantt.BaseGanttChart;
import com.hydro.casting.gui.planning.gantt.model.CGDependency;
import com.hydro.casting.gui.planning.gantt.model.CGElement;
import com.hydro.casting.gui.planning.gantt.model.CGResource;
import com.hydro.casting.gui.planning.gantt.solver.CastingGanttSolver;
import com.hydro.casting.gui.planning.table.grouping.FurnaceDemandTableGroup;
import com.hydro.casting.gui.planning.task.CreateCasterBatchTask;
import com.hydro.casting.server.contract.dto.CasterDemandDTO;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.CasterSchedulePosDTO;
import com.hydro.casting.server.contract.dto.FurnaceDemandDTO;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.*;
import com.hydro.core.gui.comp.SearchBox;
import com.hydro.core.gui.comp.ServerDTOListProvider;
import com.hydro.core.gui.comp.ServerTreeTableView;
import com.hydro.core.gui.model.ClientModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class PlanningMeltingDemandController
{
    private final static Logger log = LoggerFactory.getLogger( PlanningMeltingDemandController.class );

    private final static String PREF_PATH_PREFIX = "/mes/planningDemand";
    private final static String PREF_KEY = "lastDemandAvailableSelection";

    @FXML
    private ComboBox<String> grouping;
    @FXML
    private BorderPane mainPane;
    //@FXML
    //private TaskButton targetSelection;
    @Inject
    private Injector injector;
    @Inject
    private ClientModelManager clientModelManager;
    @Inject
    private PreferencesManager preferencesManager;
    @Inject
    private SecurityManager securityManager;
    @Inject
    private TaskManager taskManager;
    @Inject
    private ApplicationManager applicationManager;
    @Inject
    private CreateCasterBatchTask createCasterBatchTask;
    @FXML
    private SearchBox searchBox;
    @FXML
    private HBox summaryDemand;
    //@FXML
    //private TaskButton add;
    //@FXML
    //private TaskButton append;
    @FXML
    private ServerTreeTableView<FurnaceDemandDTO> tableDemand;

    private final Map<String, CGResource> resources = new HashMap<>();
    private final CastingGanttSolver solver = new CastingGanttSolver( resources );
    private final Map<Transfer, CGElement> transferIndex = new HashMap<>();
    private final Map<Batch, CGElement> batchIndex = new HashMap<>();

    private MaBeMeltingHandle handle;
    private ObservableList<CasterDemandDTO> selectedDemands;
    private ObservableList<CasterScheduleDTO> selectedSchedules;
    private ObservableList<CasterSchedulePosDTO> selectedSchedulePoss;
    private ServerDTOListProvider<FurnaceDemandDTO> serverDTOListProvider;

    private ObservableList<FurnaceDemandDTO> furnaceDemands = FXCollections.observableArrayList();

    public void initialize( MaBeMeltingHandle handle, ObservableList<CasterDemandDTO> selectedDemands, ObservableList<CasterScheduleDTO> selectedSchedules,
            ObservableList<CasterSchedulePosDTO> selectedSchedulePoss )
    {
        this.handle = handle;
        this.selectedDemands = selectedDemands;
        this.selectedSchedules = selectedSchedules;
        this.selectedSchedulePoss = selectedSchedulePoss;

        grouping.getItems().addAll( "keine", "Legierung" );
        grouping.getSelectionModel().clearAndSelect( 0 );
        grouping.getSelectionModel().selectedIndexProperty().addListener( ( observable, oldValue, newValue ) -> reload() );

        //tableDemand.setAppFilter( this::filter );
        tableDemand.setSummary( summaryDemand );
        tableDemand.setGroupTreeItemProvider( new FurnaceDemandTableGroup( grouping.getSelectionModel() ) );
        //loadFurnaceModel();
        serverDTOListProvider = new ServerDTOListProvider<>()
        {
            @Override
            public List<FurnaceDemandDTO> getDTOList() throws Exception
            {
                return furnaceDemands;
            }
        };
        tableDemand.connect( injector, serverDTOListProvider );
        tableDemand.filterProperty().bind( searchBox.textProperty() );
        tableDemand.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        /*
        tableDemand.getSelectionModel().getSelectedItems().addListener( (InvalidationListener) ( observable ) -> {
            final List<CasterDemandDTO> selectedCasterDemandDTOs = new ArrayList<>();
            for ( TreeItem<CasterDemandDTO> selectedItem : tableDemand.getSelectionModel().getSelectedItems() )
            {
                selectedCasterDemandDTOs.add( selectedItem.getValue() );
            }
            selectedDemands.setAll( selectedCasterDemandDTOs );
        } );
         */

        /*
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
            Dragboard dragboard = tableDemand.startDragAndDrop( TransferMode.MOVE );
            dragboard.setDragView( null );

            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString( MaBeController.getDemandDragboardString( selectedDemands ) );
            dragboard.setContent( clipboardContent );

            event.consume();
        } );
         */

        createCasterBatchTask.setMachineApk( handle.getCostCenter() );

        //add.disableProperty().bind( Bindings.isEmpty( selectedDemands ).or( Bindings.isEmpty( selectedSchedulePoss ) ) );
        //append.disableProperty().bind( Bindings.isEmpty( selectedDemands ) );

        //targetSelection.setDisable( false );
        //add.setLocked( !securityManager.hasRole( SecurityCasting.MABE.ACTION.CREATE_ENTRY ) );
        //append.setLocked( !securityManager.hasRole( SecurityCasting.MABE.ACTION.CREATE_ENTRY ) );

    }

    private double getPercentageWeight( CastingClientModel castingClientModel, String source, Batch batch, Transfer transfer )
    {
        final CasterScheduleDTO casterScheduleDTO = castingClientModel.getEntity( CasterScheduleDTO.class, batch.getObjid() );
        Double concreteValue = null;
        if ( casterScheduleDTO != null )
        {
            if ( Casting.ALLOY_SOURCES.MELTING_FURNACE_S1.equals( source ) )
            {
                concreteValue = casterScheduleDTO.getPercentageS1();
            }
            else if ( Casting.ALLOY_SOURCES.MELTING_FURNACE_S2.equals( source ) )
            {
                concreteValue = casterScheduleDTO.getPercentageS2();
            }
            else if ( Casting.ALLOY_SOURCES.UBC_S3.equals( source ) )
            {
                concreteValue = casterScheduleDTO.getPercentageS3();
            }
            else if ( Casting.ALLOY_SOURCES.ELEKTROLYSE.equals( source ) )
            {
                concreteValue = casterScheduleDTO.getPercentageEL();
            }
            else if ( Casting.ALLOY_SOURCES.REAL_ALLOY.equals( source ) )
            {
                concreteValue = casterScheduleDTO.getPercentageRA();
            }
        }
        double percentage = 0;
        if ( concreteValue != null )
        {
            percentage = concreteValue;
        }
        else
        {
            percentage = castingClientModel.getContentPercentage( batch.getCaster().getName(), transfer.getFrom().getName(), batch.getAlloy(), source );
        }

        return transfer.getTargetWeight() * ( percentage / 100.0 );
    }

    @FXML
    void add( ActionEvent event )
    {
        handle.getMachineMeltingScheduleController().handleSetPositions();
    }

    @FXML
    void append( ActionEvent event )
    {
        createCasterBatchTask.setData( selectedDemands, null, true, null );
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

    @FXML
    public void targetSelection( ActionEvent actionEvent )
    {
        /*
        final List<TreeItem<CasterDemandDTO>> collectedItems = new ArrayList<>();
        final TreeItem<CasterDemandDTO> root;
        if ( tableTapPane.getSelectionModel().getSelectedIndex() == 0 )
        {
            root = tableDemand.getRoot();
        }
        else
        {
            root = tableMaterialType.getRoot();
        }

        collectItems( collectedItems, root, selectedSchedulePoss );

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

         */
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

    private void loadFurnaceModel()
    {
        loadModel();

        final ClientModel casterModel = clientModelManager.getClientModel( CastingClientModel.ID );
        final CastingClientModel castingClientModel = (CastingClientModel) casterModel;

        final List<FurnaceDemandDTO> furnaceDemandDTOList = new ArrayList<>();
        for ( CGResource resource : resources.values() )
        {
            for ( CGElement element : resource.getElements() )
            {
                if ( element.getElement() instanceof Batch )
                {
                    final Batch batch = (Batch) element.getElement();

                    // Finde Ofen-Element
                    CGElement furnaceElement = null;
                    final List<CGDependency> dependencies = element.getDependencies();
                    if ( dependencies != null && !dependencies.isEmpty() )
                    {
                        furnaceElement = dependencies.get( 0 ).getOtherSide();
                    }

                    final FurnaceDemandDTO furnaceDemandDTO = new FurnaceDemandDTO();
                    furnaceDemandDTO.setId( batch.getName().hashCode() );
                    furnaceDemandDTO.setAlloy( batch.getAlloy() );
                    furnaceDemandDTO.setCharge( batch.getName() );
                    furnaceDemandDTO.setFurnaceMaxWeight( batch.getFurnaceTargetWeight() );
                    furnaceDemandDTO.setPlannedWeight( batch.getCastingWeight() );
                    furnaceDemandDTO.setBottomWeight( batch.getBottomWeight() );
                    if ( furnaceElement != null )
                    {
                        final Duration dura = Duration.between( furnaceElement.getStart(), furnaceElement.getEnd() );
                        furnaceDemandDTO.setStartTime( furnaceElement.getStart() );
                        furnaceDemandDTO.setEndTime( furnaceElement.getStart().plus( dura.dividedBy( 2 ) ) );
                    }
                    else
                    {
                        furnaceDemandDTO.setStartTime( element.getStart() );
                        furnaceDemandDTO.setEndTime( element.getEnd() );
                    }

                    double s1Weight = 0;
                    double s2Weight = 0;
                    double s3Weight = 0;
                    double elWeight = 0;
                    double raWeight = 0;
                    if ( batch.getTransfers() != null && !batch.getTransfers().isEmpty() )
                    {
                        final Transfer transfer = batch.getTransfers().iterator().next();
                        if ( transfer != null )
                        {
                            for ( TransferMaterial transferMaterial : transfer.getTransferMaterials() )
                            {
                                if ( Casting.ALLOY_SOURCES.MELTING_FURNACE_S1.equals( transferMaterial.getType() ) )
                                {
                                    s1Weight = s1Weight + transferMaterial.getWeight();
                                }
                                if ( Casting.ALLOY_SOURCES.MELTING_FURNACE_S2.equals( transferMaterial.getType() ) )
                                {
                                    s2Weight = s2Weight + transferMaterial.getWeight();
                                }
                                if ( Casting.ALLOY_SOURCES.UBC_S3.equals( transferMaterial.getType() ) )
                                {
                                    s3Weight = s3Weight + transferMaterial.getWeight();
                                }
                                if ( Casting.ALLOY_SOURCES.ELEKTROLYSE.equals( transferMaterial.getType() ) )
                                {
                                    elWeight = elWeight + transferMaterial.getWeight();
                                }
                                if ( Casting.ALLOY_SOURCES.REAL_ALLOY.equals( transferMaterial.getType() ) )
                                {
                                    raWeight = raWeight + transferMaterial.getWeight();
                                }
                            }

                            double percentageWeight = getPercentageWeight( castingClientModel, Casting.ALLOY_SOURCES.MELTING_FURNACE_S1, batch, transfer );
                            if ( percentageWeight > s1Weight )
                            {
                                s1Weight = percentageWeight;
                            }
                            percentageWeight = getPercentageWeight( castingClientModel, Casting.ALLOY_SOURCES.MELTING_FURNACE_S2, batch, transfer );
                            if ( percentageWeight > s2Weight )
                            {
                                s2Weight = percentageWeight;
                            }
                            percentageWeight = getPercentageWeight( castingClientModel, Casting.ALLOY_SOURCES.UBC_S3, batch, transfer );
                            if ( percentageWeight > s3Weight )
                            {
                                s3Weight = percentageWeight;
                            }
                            percentageWeight = getPercentageWeight( castingClientModel, Casting.ALLOY_SOURCES.ELEKTROLYSE, batch, transfer );
                            if ( percentageWeight > elWeight )
                            {
                                elWeight = percentageWeight;
                            }
                            percentageWeight = getPercentageWeight( castingClientModel, Casting.ALLOY_SOURCES.REAL_ALLOY, batch, transfer );
                            if ( percentageWeight > raWeight )
                            {
                                raWeight = percentageWeight;
                            }
                        }
                    }

                    furnaceDemandDTO.setPlannedS1Weight( s1Weight );
                    furnaceDemandDTO.setPlannedS2Weight( s2Weight );
                    furnaceDemandDTO.setPlannedS3Weight( s3Weight );
                    furnaceDemandDTO.setPlannedELWeight( elWeight );
                    furnaceDemandDTO.setPlannedRAWeight( raWeight );

                    furnaceDemandDTOList.add( furnaceDemandDTO );
                }
            }
        }
        furnaceDemands.setAll( furnaceDemandDTOList );
    }

    private void loadModel()
    {
        resources.clear();
        transferIndex.clear();
        batchIndex.clear();

        final ClientModel casterModel = clientModelManager.getClientModel( CastingClientModel.ID );
        doLoadModel( casterModel );

        solver.solve();
    }

    protected void doLoadModel( ClientModel casterModel )
    {
        addCaster( casterModel, Casting.MACHINE.CASTER_50 );
        addCaster( casterModel, Casting.MACHINE.CASTER_60 );
        addCaster( casterModel, Casting.MACHINE.CASTER_70 );
        addCaster( casterModel, Casting.MACHINE.CASTER_80 );
    }

    private void addCaster( ClientModel casterModel, String casterName )
    {
        final Caster caster = casterModel.getEntity( Caster.class, casterName );

        final CGResource casterResource = getResource( casterName );

        List<Batch> allBatches = caster.getBatches();
        Map<String, CGElement> furnaceCastingGE = new HashMap<>();
        int index = 0;
        for ( Batch batch : allBatches )
        {
            final List<Transfer> transfers = batch.getTransfers();
            for ( Transfer transfer : transfers )
            {
                if ( transfer.isRemoved() )
                {
                    continue;
                }

                Resource fromFurnace = transfer.getFrom();

                final CGResource furnaceResource = getResource( fromFurnace.getName() );

                CGElement castingGE = furnaceCastingGE.get( fromFurnace.getName() );
                CGDependency meltingSequenceCGD = null;
                if ( castingGE != null )
                {
                    meltingSequenceCGD = new CGDependency();
                    meltingSequenceCGD.setDependencyType( CGDependency.DependencyType.START_IMMEDIATE_AFTER );
                    meltingSequenceCGD.setOtherSide( castingGE );
                }
                CGElement meltingGE = new CGElement();
                meltingGE.setName( transfer.getName() );
                meltingGE.setElement( transfer );
                if ( transfer.getFurnaceTransferMaterial() != null )
                {
                    meltingGE.setDuration( getDuration( "melting", meltingGE, 180 / 2, ChronoUnit.MINUTES ) );
                }
                else
                {
                    meltingGE.setDuration( getDuration( "melting", meltingGE, 180, ChronoUnit.MINUTES ) );
                }
                meltingGE.setSetupAfter( getDuration( "melting.busy", meltingGE, 30, ChronoUnit.MINUTES ) );
                meltingGE.setResource( furnaceResource );
                if ( meltingSequenceCGD != null )
                {
                    meltingGE.addDependency( meltingSequenceCGD );
                }
                addTransferToIndex( transfer, meltingGE );

                castingGE = new CGElement();
                castingGE.setName( transfer.getBatch().getDescription() + " - " + transfer.getName() );
                castingGE.setElement( transfer.getBatch() );
                castingGE.setDuration( getDuration( "casting", castingGE, 30, ChronoUnit.MINUTES ) );
                castingGE.setSetupAfter( getDuration( "casting.busy", castingGE, 90, ChronoUnit.MINUTES ) );
                castingGE.setResource( casterResource );

                CGDependency castingMeltingCGD = new CGDependency();
                castingMeltingCGD.setDependencyType( CGDependency.DependencyType.START_AFTER );
                castingMeltingCGD.setOtherSide( meltingGE );
                castingGE.addDependency( castingMeltingCGD );

                furnaceCastingGE.put( fromFurnace.getName(), castingGE );

                // Prüfen ob CR Material eingesetzt wurde
                /*
                List<TransferMaterial> transferMaterials = transfer.getTransferMaterials();
                for ( TransferMaterial transferMaterial : transferMaterials )
                {
                    // Schachtofenbehandlung
                    addContinousTransferTo( chart, meltingGE, transferMaterial.getType(), transferMaterial.getWeight() );
                }

                 */
            }

            index++;
        }
    }

    protected CGResource getResource( String name )
    {
        CGResource resource = resources.get( name );
        if ( resource == null )
        {
            resource = new CGResource();
            resource.setName( name );
            resources.put( name, resource );
        }
        return resource;
    }

    protected void addTransferToIndex( Transfer transfer, CGElement cgElement )
    {
        transferIndex.put( transfer, cgElement );
    }

    protected Duration getDuration( String category, CGElement cgElement, int defaultValue, ChronoUnit timeUnit )
    {
        final String name = BaseGanttChart.calcDurationName( category, cgElement );
        if ( category.contains( "busy" ) )
        {
            cgElement.setDurationSetupAfterName( name );
        }
        else
        {
            cgElement.setDurationName( name );
        }
        final CastingClientModel casterModel = (CastingClientModel) clientModelManager.getClientModel( CastingClientModel.ID );

        final Duration dura;
        if ( defaultValue == 0 )
        {
            dura = Duration.ZERO;
        }
        else
        {
            dura = Duration.of( defaultValue, timeUnit );
        }
        final Batch batch = BaseGanttChart.getBatch( cgElement );
        final Transfer transfer = BaseGanttChart.getTransfer( cgElement );
        return Duration.ofMinutes( (long) casterModel.getDuration(batch, transfer, category, dura.toMinutes() ) );
    }

    public void reload()
    {
        loadFurnaceModel();
        tableDemand.reload();
    }

    public List<CasterScheduleDTO> loadCalculatedCasterSchedules()
    {
        final List<CasterScheduleDTO> schedules = new ArrayList<>();

        // aktuellen Schichanfang finden
        final LocalDate today = LocalDate.now();
        final LocalDateTime lastNachtSchichtStart = today.minusDays( 1 ).atTime( 22, 0 );
        final LocalDateTime fruehSchichtStart = today.atTime( 6, 0 );
        final LocalDateTime spaetSchichtStart = today.atTime( 14, 0 );
        final LocalDateTime nachtSchichtStart = today.atTime( 22, 0 );
        final LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = null;
        String shift = null;
        if ( now.isBefore( fruehSchichtStart ) )
        {
            start = lastNachtSchichtStart;
            shift = "C";
        }
        else if ( now.isBefore( spaetSchichtStart ) )
        {
            start = fruehSchichtStart;
            shift = "A";
        }
        else if ( now.isBefore( nachtSchichtStart ) )
        {
            start = spaetSchichtStart;
            shift = "B";
        }
        else
        {
            start = nachtSchichtStart;
            shift = "C";
        }

        int index = 0;
        CasterScheduleDTO oldCasterScheduleDTO = null;
        while ( true )
        {
            final CasterScheduleDTO casterScheduleDTO = new CasterScheduleDTO();
            casterScheduleDTO.setId( 0L );
            casterScheduleDTO.setMachine( handle.getCostCenter() );
            casterScheduleDTO.setExecutingSequenceIndex( index );
            casterScheduleDTO.setExecutionState( 0 );
            casterScheduleDTO.setAlloy( "10/05" );
            casterScheduleDTO.setAlloyQuality( "" );
            casterScheduleDTO.setProcessOrder( "" );
            casterScheduleDTO.setType( shift );
            casterScheduleDTO.setAnnotation( "" );
            //casterScheduleDTO.setSpecWeight( 0.0D );
            if ( oldCasterScheduleDTO != null )
            {
                if ( casterScheduleDTO.getCharge() == null )
                {
                    casterScheduleDTO.setCharge( Casting.getNextCharge( handle.getCostCenter(), oldCasterScheduleDTO.getCharge() ) );
                }
            }
            else
            {
                casterScheduleDTO.setInProgressTS( start );
                if ( casterScheduleDTO.getCharge() == null )
                {
                    casterScheduleDTO.setCharge( Casting.getNextCharge( handle.getCostCenter(), null ) );
                }
            }
            casterScheduleDTO.setDuration( 8 * 60 );
            casterScheduleDTO.setMeltingFurnace( handle.getCostCenter() );
            casterScheduleDTO.setPlannedLength( 0 );
            casterScheduleDTO.setNetWeight( 0.0D );

            casterScheduleDTO.setPrevious( oldCasterScheduleDTO );
            if ( oldCasterScheduleDTO != null )
            {
                oldCasterScheduleDTO.setNext( casterScheduleDTO );
            }
            oldCasterScheduleDTO = casterScheduleDTO;

            final LocalDateTime inProgressTS = casterScheduleDTO.getInProgressTS();
            double plannedWeight = 0;

            LocalDateTime lastEndTS = null;
            for ( FurnaceDemandDTO furnaceDemand : furnaceDemands )
            {
                lastEndTS = furnaceDemand.getEndTime();
                if ( furnaceDemand.getStartTime().isAfter( inProgressTS ) && furnaceDemand.getStartTime().isBefore( inProgressTS.plusHours( 8 ) ) )
                {
                    if ( Casting.MACHINE.MELTING_FURNACE_S1.equals( handle.getCostCenter() ) )
                    {
                        plannedWeight = plannedWeight + furnaceDemand.getPlannedS1Weight();
                    }
                    else if ( Casting.MACHINE.MELTING_FURNACE_S2.equals( handle.getCostCenter() ) )
                    {
                        plannedWeight = plannedWeight + furnaceDemand.getPlannedS2Weight();
                    }
                }
            }
            casterScheduleDTO.setPlannedWeight( plannedWeight );

            schedules.add( casterScheduleDTO );

            if ( lastEndTS == null || lastEndTS.isBefore( inProgressTS.plusHours( 8 ) ) )
            {
                break;
            }
            index++;
            if ( shift.equals( "A" ) )
            {
                shift = "B";
            }
            else if ( shift.equals( "B" ) )
            {
                shift = "C";
            }
            else
            {
                shift = "A";
            }
        }

        return schedules;
    }
}
