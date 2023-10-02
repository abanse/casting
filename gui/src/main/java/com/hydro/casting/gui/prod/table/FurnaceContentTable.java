package com.hydro.casting.gui.prod.table;

import com.google.inject.Inject;
import com.hydro.casting.gui.analysis.control.AnalysisTable;
import com.hydro.casting.gui.analysis.util.AnalysisCompositionHelper;
import com.hydro.casting.gui.model.CastingClientModel;
import com.hydro.casting.server.contract.dto.*;
import com.hydro.core.gui.ClientModelManager;
import com.hydro.core.gui.model.ClientModel;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FurnaceContentTable extends AnalysisTable
{
    private final static Logger log = LoggerFactory.getLogger( FurnaceContentTable.class );

    @Inject
    private ClientModelManager clientModelManager;

    private DoubleProperty amount = new SimpleDoubleProperty();

    private FurnaceInstructionDTO furnaceInstruction;

    public FurnaceContentTable()
    {
        setEditable( false );

        elementFTCF.setColoredRows( new int[] { 1 } );
        elementFTCF.setMinSpecRow( 0 );
        elementFTCF.setMaxSpecRow( 2 );

        //setDisableAverageLineColor( true );
    }

    @Inject
    private void initialize()
    {
        name.setPrefWidth( 200 );

        final ClientModel clientModel = clientModelManager.getClientModel( CastingClientModel.ID );
        clientModel.addRelationListener( CastingClientModel.MATERIAL, observable -> loadData( furnaceInstruction ) );
    }

    public double getAmount()
    {
        return amount.get();
    }

    public DoubleProperty amountProperty()
    {
        return amount;
    }

    public void setAmount( double amount )
    {
        this.amount.set( amount );
    }

    public void loadData( FurnaceInstructionDTO furnaceInstruction )
    {
        this.furnaceInstruction = furnaceInstruction;
        setAmount( 0 );
        if ( furnaceInstruction == null || furnaceInstruction.getChargeSpecification() == null || furnaceInstruction.getChargeSpecification().getElements() == null )
        {
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
        // Einsatzmaterialien hinzufügen
        final ClientModel clientModel = clientModelManager.getClientModel( CastingClientModel.ID );
        final List<MaterialDTO> materials = clientModel.getView( CastingClientModel.MATERIAL_VIEW_ID );
        final List<MaterialDTO> furnaceMaterials = materials.stream().filter( materialDTO -> ( "OF" + furnaceInstruction.getMachineApk() ).equals( materialDTO.getPlace() ) )
                .collect( Collectors.toList() );
        furnaceMaterials.stream().forEach( materialDTO -> {
            final CompositionDTO composition = new CompositionDTO();
            composition.setName( materialDTO.getGenerationSuccessTS().format( DateTimeFormatter.ofPattern( "HH:mm" ) ) + " " + materialDTO.getName() );
            composition.setWeight( materialDTO.getWeight() );
            composition.setSampleTS( materialDTO.getGenerationSuccessTS() );
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
            setAmount( getAmount() + materialDTO.getWeight() );
            analysis.add( composition );
        } );

        detail.setAnalysisList( analysis.stream().sorted( Comparator.comparing( CompositionDTO::getSampleTS ) ).collect( Collectors.toList() ) );

        //        final InputStream resourceAsStream = ChargingTable.class.getResourceAsStream( "/com/hydro/casting/gui/data/chargingTestData.json" );
        //        Gson gson = new Gson();
        //        final AnalysisDetailDTO detail = gson.fromJson( new InputStreamReader( resourceAsStream ), AnalysisDetailDTO.class );
        //
        //        if ( testStep < 3 )
        //        {
        //            final List<CompositionDTO> newAnalysis = new ArrayList<>();
        //            final CompositionDTO firstAnalysis = detail.getAnalysisList().get( 0 );
        //            firstAnalysis.setName( "Sumpf " + ( charge - 2 ) );
        //            newAnalysis.add( firstAnalysis );
        //            if ( testStep > 0 )
        //            {
        //                final CompositionDTO coldMetal1 = detail.getAnalysisList().get( 1 );
        //                coldMetal1.setName( coldMetal1.getName() + "     " + testStepTimes.get( 1 ) );
        //                newAnalysis.add( coldMetal1 );
        //            }
        //            if ( testStep == 2 )
        //            {
        //                final CompositionDTO coldMetal2 = detail.getAnalysisList().get( 2 );
        //                coldMetal2.setName( coldMetal2.getName() + "     " + testStepTimes.get( 2 ) );
        //                newAnalysis.add( coldMetal2 );
        //            }
        //            detail.getAnalysisList().clear();
        //            detail.getAnalysisList().addAll( newAnalysis );
        //        }
        //        else if ( testStep < 5 )
        //        {
        //            final List<CompositionDTO> newAnalysis = new ArrayList<>();
        //            final List<CompositionDTO> furnaceContentList = new ArrayList<>();
        //            furnaceContentList.add( detail.getAnalysisList().get( 0 ) );
        //            furnaceContentList.add( detail.getAnalysisList().get( 1 ) );
        //            furnaceContentList.add( detail.getAnalysisList().get( 2 ) );
        //            final CompositionDTO furnaceContent = createAverage( "Ofeninhalt     " + testStepTimes.get( 3 ), furnaceContentList );
        //            newAnalysis.add( furnaceContent );
        //
        //            if ( testStep > 3 )
        //            {
        //                final CompositionDTO coldMetal3 = detail.getAnalysisList().get( 3 );
        //                coldMetal3.setName( coldMetal3.getName() + "     " + testStepTimes.get( 4 ) );
        //                newAnalysis.add( coldMetal3 );
        //            }
        //
        //            detail.getAnalysisList().clear();
        //            detail.getAnalysisList().addAll( newAnalysis );
        //        }
        //        else if ( testStep < 9 )
        //        {
        //            final List<CompositionDTO> newAnalysis = new ArrayList<>();
        //            final List<CompositionDTO> furnaceContentList = new ArrayList<>();
        //            furnaceContentList.add( detail.getAnalysisList().get( 0 ) );
        //            furnaceContentList.add( detail.getAnalysisList().get( 1 ) );
        //            furnaceContentList.add( detail.getAnalysisList().get( 2 ) );
        //            furnaceContentList.add( detail.getAnalysisList().get( 3 ) );
        //            final CompositionDTO furnaceContent = createAverage( "Ofeninhalt     " + testStepTimes.get( 5 ), furnaceContentList );
        //            newAnalysis.add( furnaceContent );
        //
        //            if ( testStep > 5 )
        //            {
        //                final CompositionDTO coldMetal3 = detail.getAnalysisList().get( 4 );
        //                coldMetal3.setName( coldMetal3.getName() + "     " + testStepTimes.get( 6 ) );
        //                newAnalysis.add( coldMetal3 );
        //            }
        //            if ( testStep > 6 )
        //            {
        //                final CompositionDTO coldMetal3 = detail.getAnalysisList().get( 5 );
        //                coldMetal3.setName( coldMetal3.getName() + "     " + testStepTimes.get( 7 ) );
        //                newAnalysis.add( coldMetal3 );
        //            }
        //            if ( testStep > 7 )
        //            {
        //                final CompositionDTO coldMetal3 = detail.getAnalysisList().get( 6 );
        //                coldMetal3.setName( coldMetal3.getName() + "     " + testStepTimes.get( 8 ) );
        //                newAnalysis.add( coldMetal3 );
        //            }
        //
        //            detail.getAnalysisList().clear();
        //            detail.getAnalysisList().addAll( newAnalysis );
        //        }
        //        else
        //        {
        //            final List<CompositionDTO> newAnalysis = new ArrayList<>();
        //            final List<CompositionDTO> furnaceContentList = new ArrayList<>();
        //            furnaceContentList.add( detail.getAnalysisList().get( 0 ) );
        //            furnaceContentList.add( detail.getAnalysisList().get( 1 ) );
        //            furnaceContentList.add( detail.getAnalysisList().get( 2 ) );
        //            furnaceContentList.add( detail.getAnalysisList().get( 3 ) );
        //            furnaceContentList.add( detail.getAnalysisList().get( 4 ) );
        //            furnaceContentList.add( detail.getAnalysisList().get( 5 ) );
        //            furnaceContentList.add( detail.getAnalysisList().get( 6 ) );
        //            final CompositionDTO furnaceContent = createAverage( "Ofeninhalt     " + testStepTimes.get( 9 ), furnaceContentList );
        //            newAnalysis.add( furnaceContent );
        //
        //            if ( testStep > 9 )
        //            {
        //                final CompositionDTO coldMetal3 = detail.getAnalysisList().get( 7 );
        //                coldMetal3.setName( coldMetal3.getName() + "     " + testStepTimes.get( 10 ) );
        //                newAnalysis.add( coldMetal3 );
        //            }
        //            if ( testStep > 10 )
        //            {
        //                final CompositionDTO coldMetal3 = detail.getAnalysisList().get( 8 );
        //                coldMetal3.setName( coldMetal3.getName() + "     " + testStepTimes.get( 11 ) );
        //                newAnalysis.add( coldMetal3 );
        //            }
        //
        //            detail.getAnalysisList().clear();
        //            detail.getAnalysisList().addAll( newAnalysis );
        //        }
        //
        //        currentAnalysisDetail = detail;
        //        addLineIndex = null;
        //
        //        if ( chargeSpecificationDTO != null )
        //        {
        //            // overwrite warning values
        //            final List<SpecificationElementDTO> elements = chargeSpecificationDTO.getElements();
        //            final Map<String, Double> elementFactors = new HashMap<>();
        //            elements.stream().forEach( specificationElementDTO -> {
        //                if ( specificationElementDTO.getCastingMinValue() != null )
        //                {
        //                    final CompositionDTO min = detail.getMinComp();
        //                    final Optional<CompositionElementDTO> minElement = min.getCompositionElementDTOList().stream()
        //                            .filter( compositionElementDTO -> compositionElementDTO.getName().equalsIgnoreCase( specificationElementDTO.getName() ) ).findFirst();
        //                    if ( minElement.isPresent() )
        //                    {
        //                        minElement.get().setWarningValue( specificationElementDTO.getCastingMinValue() );
        //                    }
        //                }
        //                if ( specificationElementDTO.getCastingMaxValue() != null )
        //                {
        //                    final CompositionDTO max = detail.getMaxComp();
        //                    final Optional<CompositionElementDTO> maxElement = max.getCompositionElementDTOList().stream()
        //                            .filter( compositionElementDTO -> compositionElementDTO.getName().equalsIgnoreCase( specificationElementDTO.getName() ) ).findFirst();
        //                    if ( maxElement.isPresent() )
        //                    {
        //                        maxElement.get().setWarningValue( specificationElementDTO.getCastingMaxValue() );
        //                    }
        //                }
        //                if ( specificationElementDTO.getElementFactor() != 1. )
        //                {
        //                    elementFactors.put( specificationElementDTO.getName(), specificationElementDTO.getElementFactor() );
        //                }
        //            } );
        //            if ( !elementFactors.isEmpty() )
        //            {
        //                elementFactors.keySet().stream().forEach( elementName -> {
        //                    final Double newElementFactor = elementFactors.get( elementName );
        //                    detail.getAnalysisList().stream().forEach( compositionDTO -> {
        //                        final List<CompositionElementDTO> elementDTOList = compositionDTO.getCompositionElementDTOList();
        //                        final Optional<CompositionElementDTO> element = elementDTOList.stream().filter( compositionElementDTO -> compositionElementDTO.getName().equalsIgnoreCase( elementName ) )
        //                                .findFirst();
        //                        if ( element.isPresent() )
        //                        {
        //                            element.get().setFactor( newElementFactor );
        //                        }
        //                    } );
        //                } );
        //            }
        //        }

        //AnalysisCompositionHelper.compress( detail );
        AnalysisCompositionHelper.switchToPPM( detail );
        AnalysisCompositionHelper.setDetailOnAnalysisTable( detail, this, true );
    }
}
