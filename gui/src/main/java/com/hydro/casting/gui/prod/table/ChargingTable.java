package com.hydro.casting.gui.prod.table;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.Casting;
import com.hydro.casting.gui.analysis.control.AnalysisTable;
import com.hydro.casting.gui.analysis.util.AnalysisCompositionHelper;
import com.hydro.casting.gui.model.CastingClientModel;
import com.hydro.casting.gui.model.Composition;
import com.hydro.casting.gui.model.Material;
import com.hydro.casting.gui.model.TransferMaterial;
import com.hydro.casting.gui.model.common.EAnalysisElement;
import com.hydro.casting.gui.model.dnd.MaterialDND;
import com.hydro.casting.gui.prod.dialog.ChargingMaterialDetailDialog;
import com.hydro.casting.gui.prod.dialog.result.ChargingMaterialDetailResult;
import com.hydro.casting.gui.prod.table.row.ChargingTableRow;
import com.hydro.casting.server.contract.dto.*;
import com.hydro.core.gui.ApplicationManager;
import com.hydro.core.gui.ClientModelManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.TaskManager;
import com.hydro.core.gui.comp.IntegerTextField;
import com.hydro.core.gui.model.ClientModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.TransferMode;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ChargingTable extends AnalysisTable
{
    @Inject
    private ApplicationManager applicationManager;

    @Inject
    private ClientModelManager clientModelManager;

    @Inject
    private NotifyManager notifyManager;

    @Inject
    private TaskManager taskManager;

    @Inject
    private Injector injector;

    private AnalysisDetailDTO currentAnalysisDetail = null;

    private Integer addLineIndex = null;

    private FurnaceInstructionDTO furnaceInstruction;
    private ChargeSpecificationDTO overwriteChargeSpecificationDTO;

    private final ObservableList<MaterialDTO> furnaceContentMaterials = FXCollections.observableArrayList();
    private final ObservableList<MaterialDTO> chargingMaterials = FXCollections.observableArrayList();

    private final BooleanProperty editing = new SimpleBooleanProperty( false );

    private final BooleanProperty deleteAllowed = new SimpleBooleanProperty( false );

    private final IntegerProperty time = new SimpleIntegerProperty();

    private final BooleanProperty compressAnalysis = new SimpleBooleanProperty();

    public ChargingTable()
    {
        setEditable( true );

        setRowFactory( param -> new ChargingTableRow( currentLineIndex, disableAverageLineColor ) );

        elementFTCF.setColoredRows( new int[] { 1 } );
        elementFTCF.setMinSpecRow( 0 );
        elementFTCF.setMaxSpecRow( 2 );

        weight.setCellValueFactory( new PropertyValueFactory<>( "displayWeight" ) );
        weightFTCF.setEditableCallback( rowIndex -> {
            if ( addLineIndex == null )
            {
                return false;
            }
            return rowIndex > addLineIndex;
        } );

        setOnDragOver( dragEvent -> {
            final String dragboardString = dragEvent.getDragboard().getString();
            if ( !dragboardString.startsWith( MaterialDND.IDENT ) )
            {
                return;
            }
            dragEvent.acceptTransferModes( TransferMode.COPY_OR_MOVE );
            dragEvent.consume();
        } );

        setOnDragDropped( event -> {
            editing.set( true );
            final String dragboardString = event.getDragboard().getString();
            if ( !dragboardString.startsWith( MaterialDND.IDENT ) )
            {
                return;
            }
            final TransferMode transferMode = event.getTransferMode();
            int count = 1;
            if ( transferMode == TransferMode.COPY )
            {
                IntegerTextField integerTextField = new IntegerTextField();
                integerTextField.setIntValue( 2 );
                final ButtonType result = notifyManager.showCompInputMessage( "Anzahl Einsätze", integerTextField, integerTextField );
                if ( result != ButtonType.OK )
                {
                    return;
                }
                count = integerTextField.getIntValue();
            }
            if ( count <= 0 )
            {
                count = 1;
            }

            final ClientModel clientModel = clientModelManager.getClientModel( CastingClientModel.ID );
            final MaterialDND materialDND = new MaterialDND( dragboardString );
            final MaterialDTO sourceMaterial = clientModel.getEntity( MaterialDTO.class, materialDND.getObjid() );

            addChargingMaterial( sourceMaterial, materialDND.getWeight() * count );
            reload();
        } );

        setOnMouseClicked( event -> {
            if ( event.getClickCount() > 1 && getEditingCell() == null )
            {
                int index = getSelectionModel().getSelectedIndex();
                final MaterialDTO materialDTO;
                if ( addLineIndex != null && index > addLineIndex )
                {
                    materialDTO = getChargingMaterial( index );
                }
                else
                {
                    materialDTO = getFurnaceContentMaterial( index );
                }
                if ( materialDTO != null )
                {
                    final ChargingMaterialDetailResult chargingMaterialDetailResult = ChargingMaterialDetailDialog.showDialog( applicationManager.getMainStage(), injector, materialDTO.clone() );
                    if ( chargingMaterialDetailResult != null )
                    {
                        materialDTO.setMaterialAnalysisElements( chargingMaterialDetailResult.getElementDTOList() );
                        editing.set( true );
                        reload();
                    }
                }
            }
        } );

        getSelectionModel().selectedIndexProperty().addListener( ( observable ) -> {
            if ( addLineIndex == null )
            {
                deleteAllowed.set( false );
                return;
            }
            boolean allowed = true;
            for ( Integer selectedIndex : getSelectionModel().getSelectedIndices() )
            {
                if ( selectedIndex <= addLineIndex )
                {
                    allowed = false;
                    break;
                }
            }
            deleteAllowed.set( allowed );
        } );

        time.addListener( ( observable, oldValue, newValue ) -> reload() );
    }

    @Inject
    private void initialize()
    {
        name.setPrefWidth( 200 );

        final ClientModel clientModel = clientModelManager.getClientModel( CastingClientModel.ID );
        clientModel.addRelationListener( CastingClientModel.MATERIAL, observable -> reload() );
    }

    public boolean isCompressAnalysis()
    {
        return compressAnalysis.get();
    }

    public BooleanProperty compressAnalysisProperty()
    {
        return compressAnalysis;
    }

    public void setCompressAnalysis( boolean compressAnalysis )
    {
        this.compressAnalysis.set( compressAnalysis );
    }

    public boolean isEditing()
    {
        return editing.get();
    }

    public BooleanProperty editingProperty()
    {
        return editing;
    }

    public void setEditing( boolean editing )
    {
        this.editing.set( editing );
    }

    public void reload()
    {
        loadData( furnaceInstruction, overwriteChargeSpecificationDTO );
    }

    public void loadData( FurnaceInstructionDTO furnaceInstruction, ChargeSpecificationDTO overwriteChargeSpecificationDTO )
    {
        this.furnaceInstruction = furnaceInstruction;
        this.overwriteChargeSpecificationDTO = overwriteChargeSpecificationDTO;

        if ( furnaceInstruction == null || furnaceInstruction.getChargeSpecification() == null || furnaceInstruction.getChargeSpecification().getElements() == null )
        {
            currentAnalysisDetail = null;
            clear();
            return;
        }

        AnalysisDetailDTO detail = new AnalysisDetailDTO();
        CompositionDTO minComp = new CompositionDTO();
        minComp.setName( furnaceInstruction.getAlloy() + " Min" );
        CompositionDTO maxComp = new CompositionDTO();
        maxComp.setName( furnaceInstruction.getAlloy() + " Max" );

        CompositionElementDTO compositionElementDTOMin;
        CompositionElementDTO compositionElementDTOMax;

        long index = 0;
        for ( SpecificationElementDTO alloyElement : furnaceInstruction.getChargeSpecification().getElements() )
        {
            compositionElementDTOMin = new CompositionElementDTO();
            compositionElementDTOMax = new CompositionElementDTO();

            compositionElementDTOMin.setName( alloyElement.getName() );
            compositionElementDTOMin.setValue( alloyElement.getMinValue() );
            compositionElementDTOMin.setPrecision( alloyElement.getPrecision() );
            compositionElementDTOMin.setSortOrderId( index );
            minComp.addToCompositionElementDTOList( compositionElementDTOMin );

            compositionElementDTOMax.setName( alloyElement.getName() );
            compositionElementDTOMax.setValue( alloyElement.getMaxValue() );
            compositionElementDTOMax.setPrecision( alloyElement.getPrecision() );
            compositionElementDTOMax.setSortOrderId( index );
            maxComp.addToCompositionElementDTOList( compositionElementDTOMax );
            index++;
        }

        detail.setMinComp( minComp );
        detail.setMaxComp( maxComp );

        final List<CompositionDTO> analysis = new ArrayList<>();

        if ( !editing.get() )
        {
            furnaceContentMaterials.clear();
        }
        // Einsatzmaterialien hinzufügen
        final ClientModel clientModel = clientModelManager.getClientModel( CastingClientModel.ID );
        final List<MaterialDTO> materials = clientModel.getView( CastingClientModel.MATERIAL_VIEW_ID );
        final List<MaterialDTO> originalFurnaceMaterials = materials.stream().filter( materialDTO -> ( "OF" + furnaceInstruction.getMachineApk() ).equals( materialDTO.getPlace() ) )
                .collect( Collectors.toList() );
        // check if already added
        for ( MaterialDTO originalFurnaceMaterial : originalFurnaceMaterials )
        {
            final Optional<MaterialDTO> exist = furnaceContentMaterials.stream().filter( materialDTO -> originalFurnaceMaterial.getId() == materialDTO.getId() ).findFirst();
            if ( exist.isEmpty() )
            {
                furnaceContentMaterials.add( originalFurnaceMaterial.clone() );
            }
        }

        addCompositions( analysis, furnaceContentMaterials );

        // Hier die eigentlichen Analysen hinzufügen
        if ( furnaceInstruction.getChargeAnalysisDetail() != null )
        {
            addAnalysisCompositions( analysis, furnaceInstruction.getChargeAnalysisDetail().getAnalysisList() );
        }

        // Horizont filtern
        if ( time.get() > 0 )
        {
            final List<CompositionDTO> furnaceContent = analysis.stream().sorted( Comparator.comparing( CompositionDTO::getSampleTS ) ).collect( Collectors.toList() );
            final double countRows = furnaceContent.size();
            final double showRows = countRows - ( countRows * ( (double) time.get() / 100.0 ) );
            detail.setAnalysisList( furnaceContent.subList( 0, (int) Math.round( showRows ) ) );
        }
        else
        {
            detail.setAnalysisList( analysis.stream().sorted( Comparator.comparing( CompositionDTO::getSampleTS ) ).collect( Collectors.toList() ) );
        }

        prepareMaterialsWithAnalysis( detail.getAnalysisList() );

        // Plan-Materialien replizieren zu charging materials
        final List<MaterialDTO> plannedMaterials = materials.stream().filter( materialDTO -> {
            if ( materialDTO.getShaping() != MaterialDTO.Shaping.PlannedConsumed )
            {
                return false;
            }
            return Objects.equals( materialDTO.getConsumedOperation(), furnaceInstruction.getCastingBatchOID() );
        } ).collect( Collectors.toList() );

        if ( !editing.get() )
        {
            chargingMaterials.clear();
        }
        // check if already added
        for ( MaterialDTO plannedMaterial : plannedMaterials )
        {
            final Optional<MaterialDTO> exist = chargingMaterials.stream().filter( materialDTO -> plannedMaterial.getId() == materialDTO.getId() ).findFirst();
            if ( exist.isEmpty() )
            {
                chargingMaterials.add( plannedMaterial.clone() );
            }
        }

        if ( !chargingMaterials.isEmpty() )
        {
            addLineIndex = detail.getAnalysisList().size() + 2;
        }
        else
        {
            addLineIndex = null;
        }

        if ( !chargingMaterials.isEmpty() )
        {
            addCompositions( detail.getAnalysisList(), chargingMaterials );
        }

        currentAnalysisDetail = detail;

        if ( detail == null )
        {
            clear();
        }
        else
        {
            if ( overwriteChargeSpecificationDTO != null )
            {
                // overwrite warning values
                final List<SpecificationElementDTO> elements = overwriteChargeSpecificationDTO.getElements();
                final Map<String, Double> elementFactors = new HashMap<>();
                elements.forEach( specificationElementDTO -> {
                    if ( specificationElementDTO.getCastingMinValue() != null )
                    {
                        final CompositionDTO min = detail.getMinComp();
                        final Optional<CompositionElementDTO> minElement = min.getCompositionElementDTOList().stream()
                                .filter( compositionElementDTO -> specificationElementDTO.getName().startsWith( compositionElementDTO.getName() ) ).findFirst();
                        minElement.ifPresent( compositionElementDTO -> compositionElementDTO.setWarningValue( specificationElementDTO.getCastingMinValue() ) );
                    }
                    if ( specificationElementDTO.getCastingMaxValue() != null )
                    {
                        final CompositionDTO max = detail.getMaxComp();
                        final Optional<CompositionElementDTO> maxElement = max.getCompositionElementDTOList().stream()
                                .filter( compositionElementDTO -> specificationElementDTO.getName().startsWith( compositionElementDTO.getName() ) ).findFirst();
                        maxElement.ifPresent( compositionElementDTO -> compositionElementDTO.setWarningValue( specificationElementDTO.getCastingMaxValue() ) );
                    }
                    if ( specificationElementDTO.getElementFactor() != 1. )
                    {
                        elementFactors.put( specificationElementDTO.getName(), specificationElementDTO.getElementFactor() );
                    }
                } );
                if ( !elementFactors.isEmpty() && addLineIndex != null)
                {
                    elementFactors.keySet().forEach( elementName -> {
                        final Double newElementFactor = elementFactors.get( elementName );
                        detail.getAnalysisList().subList( addLineIndex - 2, detail.getAnalysisList().size() ).forEach( compositionDTO -> {
                            final List<CompositionElementDTO> elementDTOList = compositionDTO.getCompositionElementDTOList();
                            final Optional<CompositionElementDTO> element = elementDTOList.stream().filter( compositionElementDTO -> compositionElementDTO.getName().equalsIgnoreCase( elementName ) )
                                    .findFirst();
                            element.ifPresent( compositionElementDTO -> compositionElementDTO.setFactor( newElementFactor ) );
                        } );
                    } );
                }
            }

            AnalysisCompositionHelper.switchToPPM( detail );
            if ( addLineIndex != null )
            {
                AnalysisCompositionHelper.setDetailOnAnalysisTable( currentAnalysisDetail, this, "Ø", new int[] { 2, addLineIndex }, compressAnalysis.get() );
            }
            else
            {
                AnalysisCompositionHelper.setDetailOnAnalysisTable( currentAnalysisDetail, this, compressAnalysis.get() );
            }
        }
    }

    private void addCompositions( List<CompositionDTO> compositions, List<MaterialDTO> materials )
    {
        materials.forEach( materialDTO -> {
            if ( materialDTO.getWeight() <= 0 )
            {
                return;
            }
            final CompositionDTO composition = new CompositionDTO();
            composition.setObjid( materialDTO.getId() );
            if ( materialDTO.getShaping() == MaterialDTO.Shaping.Consumed )
            {
                composition.setName( materialDTO.getGenerationSuccessTS().format( DateTimeFormatter.ofPattern( "HH:mm" ) ) + " " + materialDTO.getName() );
            }
            else
            {
                if ( materialDTO.getDeliveryCharge() != null )
                {
                    composition.setName( materialDTO.getMaterialTypeDescription() + " - " + materialDTO.getDeliveryCharge() );
                }
                else
                {
                    composition.setName( materialDTO.getName() );
                }
            }
            composition.setWeight( materialDTO.getWeight() );
            composition.setSampleTS( materialDTO.getGenerationSuccessTS() );
            composition.setSampleNumber( 1 ); // indicator for material
            if ( materialDTO.getMaterialAnalysisElements() != null )
            {
                final List<CompositionElementDTO> elements = new ArrayList<>();
                materialDTO.getMaterialAnalysisElements().stream().forEach( materialAnalysisElementDTO -> {
                    final CompositionElementDTO compositionElementDTO = new CompositionElementDTO();
                    compositionElementDTO.setName( materialAnalysisElementDTO.getName() );
                    compositionElementDTO.setValue( materialAnalysisElementDTO.getValue() );
                    elements.add( compositionElementDTO );
                } );
                composition.setCompositionElementDTOList( elements );
            }
            compositions.add( composition );
        } );
    }

    private void addAnalysisCompositions( List<CompositionDTO> compositions, List<CompositionDTO> analysisCompositions )
    {
        final List<CompositionDTO> addAnalysis = new ArrayList<>();

        analysisCompositions.forEach( compositionDTO -> {
            // Anmeldungen rausfiltern
            if ( compositionDTO.getSampleTS() == null )
            {
                return;
            }
            // Fertig-Analysen filtern
            if ( compositionDTO.getName().startsWith( "1" ) )
            {
                return;
            }
            CompositionDTO viewDTO = compositionDTO.clone();
            viewDTO.setName( viewDTO.getSampleTS().format( DateTimeFormatter.ofPattern( "HH:mm" ) ) + " " + viewDTO.getName() );
            viewDTO.setSampleNumber( 2 ); // indicator for analysis
            addAnalysis.add( viewDTO );
        } );
        compositions.addAll( addAnalysis );
    }

    private void prepareMaterialsWithAnalysis( List<CompositionDTO> compositions )
    {
        double currentWeight = 0;
        for ( CompositionDTO composition : compositions )
        {
            composition.setOriginalWeight( composition.getWeight() );
            if ( composition.getSampleNumber() == 2 )
            {
                composition.setWeight( currentWeight );
            }
            else
            {
                currentWeight = currentWeight + composition.getWeight();
            }
        }
        boolean analysisPresent = false;
        for ( int i = compositions.size(); i-- > 0; )
        {
            final CompositionDTO compositionDTO = compositions.get( i );
            if ( analysisPresent )
            {
                compositionDTO.setWeight( 0 );
            }
            if ( compositionDTO.getSampleNumber() == 2 )
            {
                analysisPresent = true;
            }
        }
    }

    protected void handleWeightUpdate( int row, Number newWeight )
    {
        if ( addLineIndex == null )
        {
            return;
        }
        final MaterialDTO materialDTO = getChargingMaterial( row );
        if ( materialDTO.getWeight() == newWeight.doubleValue() )
        {
            return;
        }
        editing.set( true );
        materialDTO.setWeight( newWeight.doubleValue() );
        reload();
    }

    private MaterialDTO getChargingMaterial( int tableViewIndex )
    {
        final List<MaterialDTO> viewableChargingMaterials = chargingMaterials.stream().filter( materialDTO -> materialDTO.getWeight() > 0 ).collect( Collectors.toList() );
        return viewableChargingMaterials.get( tableViewIndex - addLineIndex - 1 );
    }

    private MaterialDTO getFurnaceContentMaterial( int tableViewIndex )
    {
        final Composition composition = getItems().get( tableViewIndex );
        final Optional<MaterialDTO> furnaceContentMaterial = furnaceContentMaterials.stream().filter( materialDTO -> materialDTO.getId() == composition.getRefOID() ).findFirst();
        return furnaceContentMaterial.orElse( null );
    }

    public void addMaterials( List<TransferMaterial> transferMaterials )
    {
        if ( transferMaterials == null || transferMaterials.isEmpty() )
        {
            return;
        }
        setEditing( true );
        transferMaterials.forEach( transferMaterial -> {
            final Material material = transferMaterial.getSource();
            final ClientModel clientModel = clientModelManager.getClientModel( CastingClientModel.ID );
            final MaterialDTO materialDTO = clientModel.getEntity( MaterialDTO.class, material.getObjid() );
            addChargingMaterial( materialDTO, transferMaterial.getWeight() );
        } );
        reload();
    }

    private CompositionDTO createAverage( String name, List<CompositionDTO> analysis )
    {
        CompositionDTO calced = new CompositionDTO();
        calced.setName( name );

        HashMap<String, Double> calcedSumMap = new HashMap<String, Double>();
        double completeWeight = 0;
        for ( CompositionDTO ana : analysis )
        {
            double weight = ana.getWeight();
            completeWeight = completeWeight + weight;

            List<CompositionElementDTO> matElements = ana.getCompositionElementDTOList();

            for ( CompositionElementDTO compositionElement : matElements )
            {
                if ( compositionElement.getValue() == null )
                {
                    continue;
                }
                Double calcedSumObject = calcedSumMap.get( compositionElement.getName() );
                double calcedSum = 0;
                if ( calcedSumObject != null )
                {
                    calcedSum = calcedSumObject.doubleValue();
                }
                double elementValue = compositionElement.getValue();
                calcedSum = calcedSum + ( weight * ( elementValue / 100.0 ) );
                calcedSumMap.put( compositionElement.getName(), calcedSum );
            }
        }
        calced.setWeight( completeWeight );

        setAverage( calced, calcedSumMap );

        return calced;
    }

    private void setAverage( CompositionDTO analysis, HashMap<String, Double> calcedSumMap )
    {
        for ( String elementName : calcedSumMap.keySet() )
        {
            if ( EAnalysisElement.isCalculatedElement( elementName ) == false )
            {
                final double calcedSum = calcedSumMap.get( elementName );
                final double elementValue = calcedSum / analysis.getWeight() * 100.0;
                CompositionElementDTO compositionElementDTO = new CompositionElementDTO();
                compositionElementDTO.setName( elementName );
                compositionElementDTO.setValue( elementValue );
                analysis.addToCompositionElementDTOList( compositionElementDTO );
            }
        }
    }

    public AnalysisDetailDTO getCurrentAnalysisDetail()
    {
        return currentAnalysisDetail;
    }

    public List<MaterialDTO> getFurnaceContentMaterials()
    {
        return new ArrayList<>( furnaceContentMaterials );
    }

    public List<MaterialDTO> getChargingMaterials()
    {
        return new ArrayList<>( chargingMaterials );
    }

    public void stopEditing()
    {
        editing.set( false );
        reload();
    }

    public boolean isDeleteAllowed()
    {
        return deleteAllowed.get();
    }

    public BooleanProperty deleteAllowedProperty()
    {
        return deleteAllowed;
    }

    public void setDeleteAllowed( boolean deleteAllowed )
    {
        this.deleteAllowed.set( deleteAllowed );
    }

    public void deleteSelectedRow()
    {
        final List<Integer> selectedRowsDesc = getSelectionModel().getSelectedIndices().sorted( Comparator.comparingInt( Integer::intValue ).reversed() );
        for ( Integer selectedIndex : new ArrayList<>( selectedRowsDesc ) )
        {
            handleWeightUpdate( selectedIndex, 0. );
        }
    }

    public int getTime()
    {
        return time.get();
    }

    public IntegerProperty timeProperty()
    {
        return time;
    }

    public void setTime( int time )
    {
        this.time.set( time );
    }

    private void addChargingMaterial( MaterialDTO sourceMaterial, double weight )
    {
        final MaterialDTO material = sourceMaterial.clone();
        material.setShaping( MaterialDTO.Shaping.PlannedConsumed );
        material.setGenerationState( Casting.SCHEDULABLE_STATE.UNPLANNED );
        material.setConsumptionState( Casting.SCHEDULABLE_STATE.UNPLANNED );
        material.setWeight( weight );
        chargingMaterials.add( material );
    }

    public ChargeSpecificationDTO getChargeSpecificationDTO()
    {
        if ( overwriteChargeSpecificationDTO != null )
        {
            return overwriteChargeSpecificationDTO;
        }
        if ( furnaceInstruction != null )
        {
            return furnaceInstruction.getChargeSpecification();
        }
        return null;
    }
}
