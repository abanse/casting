package com.hydro.casting.gui.analysis.util;

import com.hydro.casting.gui.analysis.control.AnalysisTable;
import com.hydro.casting.gui.model.*;
import com.hydro.casting.gui.model.common.EAnalysisElement;
import com.hydro.casting.server.contract.dto.AnalysisDetailDTO;
import com.hydro.casting.server.contract.dto.ChargeSpecificationDTO;
import com.hydro.casting.server.contract.dto.CompositionDTO;
import com.hydro.casting.server.contract.dto.CompositionElementDTO;
import com.hydro.core.common.util.NumberTools;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Helper class containing generalized functionality relevant to the analysis and alloy features in regard to the AnalysisTable-class, and it's composition.
 */
public abstract class AnalysisCompositionHelper
{
    public static void setDetailOnAnalysisTable( AnalysisDetailDTO detail, AnalysisTable analysisTable, boolean compressAnalysis )
    {
        setDetailOnAnalysisTable( detail, analysisTable, "Ø", compressAnalysis );
    }

    public static void setDetailOnAnalysisTable( AnalysisDetailDTO detail, AnalysisTable analysisTable, String averageLineName, boolean compressAnalysis )
    {
        setDetailOnAnalysisTable( detail, analysisTable, averageLineName, new int[] { 2 }, compressAnalysis );
    }

    public static void setDetailOnAnalysisTable( AnalysisDetailDTO detail, AnalysisTable analysisTable, String averageLineName, int[] lineIndex, boolean compressAnalysis )
    {
        // Every case (header or leaf for analysis, or alloy) contains the specification, which is always present in the AnalysisDetailDTO, so the setup is generalized
        final Specification specification = getSpecification( detail );

        // Setup for analysis header detail, analysis leaf detail and alloy detail is different
        if ( detail.isLeaf() )
        {
            setDetailOnAnalysisTableLeaf( detail.getName(), detail.getAnalysisList(), specification, analysisTable, compressAnalysis );
        }
        else
        {
            setDetailOnAnalysisTableHeader( detail.getAnalysisList(), specification, analysisTable, averageLineName, lineIndex, compressAnalysis );
        }
    }

    public static Specification getSpecification( AnalysisDetailDTO detail )
    {
        if ( detail.getMinComp() == null || detail.getMaxComp() == null )
        {
            return null;
        }
        final Specification specification = new Specification();
        final Composition minComp = new Composition();
        final Composition maxComp = new Composition();

        minComp.setName( detail.getMinComp().getName() );
        maxComp.setName( detail.getMaxComp().getName() );
        specification.setMin( minComp );
        specification.setMax( maxComp );

        populateCompositionFromDTO( minComp, detail.getMinComp() );
        populateCompositionFromDTO( maxComp, detail.getMaxComp() );

        return specification;
    }

    public static Specification getSpecification( ChargeSpecificationDTO chargeSpecificationDTO )
    {
        final Specification specification = new Specification();
        final Composition minComp = new Composition();
        final Composition maxComp = new Composition();

        minComp.setName( "min" );
        maxComp.setName( "max" );
        specification.setMin( minComp );
        specification.setMax( maxComp );

        chargeSpecificationDTO.getElements().forEach( specificationElementDTO -> {
            minComp.setCompositionElementValue( specificationElementDTO.getName(), NumberTools.getNullSafe( specificationElementDTO.getMinValue() ) );
            maxComp.setCompositionElementValue( specificationElementDTO.getName(), NumberTools.getNullSafe( specificationElementDTO.getMaxValue() ) );
        } );

        return specification;
    }

    private static void setDetailOnAnalysisTableHeader( List<CompositionDTO> compositionDTOList, Specification specification, AnalysisTable analysisTable, String averageLineName, int[] lineIndex,
            boolean compressAnalysis )
    {
        List<Analysis> analysisList = new ArrayList<>();
        Analysis analysis;

        for ( CompositionDTO compositionDTO : compositionDTOList )
        {
            analysis = new Analysis();
            analysis.setRefOID( compositionDTO.getObjid() );
            analysis.setName( compositionDTO.getName() );
            analysis.setWeight( compositionDTO.getWeight() );
            analysis.setOriginalWeight( compositionDTO.getOriginalWeight() );
            populateCompositionFromDTO( analysis, compositionDTO );
            analysisList.add( analysis );
        }

        Batch batch = new Batch();
        batch.setSpecification( specification );

        analysisTable.setBatch( batch, analysisList, averageLineName, lineIndex, compressAnalysis );
    }

    private static void setDetailOnAnalysisTableLeaf( String analysisName, List<CompositionDTO> compositionDTOList, Specification specification, AnalysisTable analysisTable, boolean compressAnalysis )
    {
        Analysis analysis = null;

        // Analysis does not have to be present, e.g. if detail for alloy (specification) is displayed
        if ( compositionDTOList != null )
        {
            analysis = new Analysis();
            analysis.setName( analysisName );
            populateCompositionFromDTO( analysis, compositionDTOList.get( 0 ) );
        }

        // If no analysis is present, only the specification is displayed
        if ( analysis != null )
        {
            analysisTable.setAnalysis( analysis, specification, compressAnalysis );
        }
        else
        {
            analysisTable.setSpecification( specification );
        }
    }

    public static void populateCompositionFromDTO( Composition composition, CompositionDTO compositionDTO )
    {
        CompositionElement compositionElement;
        for ( CompositionElementDTO compositionElementDTO : compositionDTO.getCompositionElementDTOList() )
        {
            compositionElement = createCompositionElementFromDto( compositionElementDTO );
            composition.addCompositionElement( compositionElement );
        }
    }

    public static void switchToPPM( ChargeSpecificationDTO chargeSpecificationDTO )
    {
        if ( chargeSpecificationDTO == null || chargeSpecificationDTO.getElements() == null )
        {
            return;
        }

        chargeSpecificationDTO.getElements().forEach( specificationElementDTO -> {
            if ( EAnalysisElement.isPPMElement( specificationElementDTO.getName() ) )
            {
                specificationElementDTO.setName( specificationElementDTO.getName() + "[ppm]" );
                specificationElementDTO.setUnit( "ppm" );
                if ( specificationElementDTO.getMinValue() != null )
                {
                    specificationElementDTO.setMinValue( specificationElementDTO.getMinValue() * 10000. );
                }
                if ( specificationElementDTO.getCastingMinValue() != null )
                {
                    specificationElementDTO.setCastingMinValue( specificationElementDTO.getCastingMinValue() * 10000. );
                }
                if ( specificationElementDTO.getMaxValue() != null )
                {
                    specificationElementDTO.setMaxValue( specificationElementDTO.getMaxValue() * 10000. );
                }
                if ( specificationElementDTO.getCastingMaxValue() != null )
                {
                    specificationElementDTO.setCastingMaxValue( specificationElementDTO.getCastingMaxValue() * 10000. );
                }
                int newPrecision = specificationElementDTO.getPrecision() - 3;
                if ( newPrecision < 0 )
                {
                    newPrecision = 0;
                }
                specificationElementDTO.setPrecision( newPrecision );
            }
        } );
    }

    public static void switchBackFromPPM( Analysis analysis )
    {
        if ( analysis == null || analysis.getCompositionElements() == null )
        {
            return;
        }
        analysis.getCompositionElements().forEach( compositionElement -> {

            if ( compositionElement.getName().endsWith( "[ppm]" ) )
            {
                compositionElement.setName( compositionElement.getName().substring( 0, compositionElement.getName().length() - 5 ) );
                if ( compositionElement.getOriginalElementValue() != 0 )
                {
                    compositionElement.setOriginalElementValue( compositionElement.getOriginalElementValue() / 10000. );
                }
                if ( compositionElement.getWarningElementValue() != 0 )
                {
                    compositionElement.setWarningElementValue( compositionElement.getWarningElementValue() / 10000. );
                }
                int newPrecision = compositionElement.getDecimalPlaces() - 3;
                if ( newPrecision < 0 )
                {
                    newPrecision = 0;
                }
                compositionElement.setDecimalPlaces( newPrecision );
            }
        } );

    }

    public static void switchBackFromPPM( ChargeSpecificationDTO chargeSpecificationDTO )
    {
        if ( chargeSpecificationDTO == null || chargeSpecificationDTO.getElements() == null )
        {
            return;
        }

        chargeSpecificationDTO.getElements().forEach( specificationElementDTO -> {
            if ( specificationElementDTO.getName().endsWith( "[ppm]" ) )
            {
                specificationElementDTO.setName( specificationElementDTO.getName().substring( 0, specificationElementDTO.getName().length() - 5 ) );
                specificationElementDTO.setUnit( "%" );
                if ( specificationElementDTO.getMinValue() != null )
                {
                    specificationElementDTO.setMinValue( specificationElementDTO.getMinValue() / 10000. );
                }
                if ( specificationElementDTO.getCastingMinValue() != null )
                {
                    specificationElementDTO.setCastingMinValue( specificationElementDTO.getCastingMinValue() / 10000. );
                }
                if ( specificationElementDTO.getMaxValue() != null )
                {
                    specificationElementDTO.setMaxValue( specificationElementDTO.getMaxValue() / 10000. );
                }
                if ( specificationElementDTO.getCastingMaxValue() != null )
                {
                    specificationElementDTO.setCastingMaxValue( specificationElementDTO.getCastingMaxValue() / 10000. );
                }
                int newPrecision = specificationElementDTO.getPrecision() + 3;
                if ( newPrecision < 0 )
                {
                    newPrecision = 0;
                }
                specificationElementDTO.setPrecision( newPrecision );
            }
        } );
    }

    public static void switchToPPM( AnalysisDetailDTO detail )
    {
        if ( detail == null )
        {
            return;
        }
        //EAnalysisElement
        final Consumer<CompositionElementDTO> ppmConsumer = new Consumer<CompositionElementDTO>()
        {
            @Override
            public void accept( CompositionElementDTO compositionElementDTO )
            {
                if ( EAnalysisElement.isPPMElement( compositionElementDTO.getName() ) )
                {
                    compositionElementDTO.setName( compositionElementDTO.getName() + "[ppm]" );
                    if ( compositionElementDTO.getValue() != null )
                    {
                        compositionElementDTO.setValue( compositionElementDTO.getValue() * 10000. );
                    }
                    if ( compositionElementDTO.getWarningValue() != null )
                    {
                        compositionElementDTO.setWarningValue( compositionElementDTO.getWarningValue() * 10000. );
                    }
                    if ( compositionElementDTO.getPrecision() != null )
                    {
                        int newPrecision = compositionElementDTO.getPrecision() - 3;
                        if ( newPrecision < 0 )
                        {
                            newPrecision = 0;
                        }
                        compositionElementDTO.setPrecision( newPrecision );
                    }
                }
            }
        };

        detail.getMinComp().getCompositionElementDTOList().forEach( ppmConsumer );
        detail.getMaxComp().getCompositionElementDTOList().forEach( ppmConsumer );
        if ( detail.getAnalysisList() != null )
        {
            for ( CompositionDTO analysis : detail.getAnalysisList() )
            {
                analysis.getCompositionElementDTOList().forEach( ppmConsumer );
            }
        }
    }

    public static void appendTimestampToName( AnalysisDetailDTO detail )
    {
        if ( detail == null )
        {
            return;
        }
        if ( detail.getAnalysisList() != null )
        {
            for ( CompositionDTO analysis : detail.getAnalysisList() )
            {
                if ( analysis.getSampleTS() != null )
                {
                    analysis.setName( analysis.getName() + " " + analysis.getSampleTS().format( DateTimeFormatter.ofPattern( "dd.MM HH:mm" ) ) );
                }
            }
        }
    }

    //    public static void compress( AnalysisDetailDTO detail )
    //    {
    //        if ( detail == null || detail.getAnalysisList() == null || detail.getAnalysisList().isEmpty() )
    //        {
    //            return;
    //        }
    //        final Map<String, Double> maxElementValues = new HashMap<>();
    //        for ( CompositionDTO compositionDTO : detail.getAnalysisList() )
    //        {
    //            for ( CompositionElementDTO compositionElementDTO : compositionDTO.getCompositionElementDTOList() )
    //            {
    //                if ( compositionElementDTO.getValue() == null )
    //                {
    //                    continue;
    //                }
    //                Double currentValue = maxElementValues.get( compositionElementDTO.getName() );
    //                if ( currentValue == null || compositionElementDTO.getValue() > currentValue )
    //                {
    //                    maxElementValues.put( compositionElementDTO.getName(), compositionElementDTO.getValue() );
    //                }
    //            }
    //        }
    //        // find precisions
    //        final Map<String, Integer> elementPrecisions = new HashMap<>();
    //        for ( CompositionElementDTO minCompositionElementDTO : detail.getMinComp().getCompositionElementDTOList() )
    //        {
    //            elementPrecisions.put( minCompositionElementDTO.getName(), minCompositionElementDTO.getPrecision() );
    //        }
    //        for ( CompositionElementDTO maxCompositionElementDTO : detail.getMaxComp().getCompositionElementDTOList() )
    //        {
    //            elementPrecisions.put( maxCompositionElementDTO.getName(), maxCompositionElementDTO.getPrecision() );
    //        }
    //        final Set<String> removeElements = new HashSet<>();
    //        DecimalFormat specFormat = new DecimalFormat( "0" );
    //        for ( String elementName : maxElementValues.keySet() )
    //        {
    //            if ( EAnalysisElement.isCalculatedElement( elementName ) )
    //            {
    //                continue;
    //            }
    //            if ( !elementPrecisions.containsKey( elementName ) )
    //            {
    //                continue;
    //            }
    //
    //            int precision = elementPrecisions.get( elementName );
    //
    //            Double maxValue = maxElementValues.get( elementName );
    //            if ( maxValue == null )
    //            {
    //                removeElements.add( elementName );
    //                continue;
    //            }
    //            specFormat.setMinimumFractionDigits( precision );
    //            specFormat.setMaximumFractionDigits( precision );
    //            String formattedMaxValue = specFormat.format( maxValue );
    //            Number roundValue = 0;
    //            try
    //            {
    //                roundValue = specFormat.parse( formattedMaxValue );
    //            }
    //            catch ( ParseException e )
    //            {
    //                roundValue = maxValue;
    //            }
    //            if ( roundValue.doubleValue() == 0.0 )
    //            {
    //                removeElements.add( elementName );
    //            }
    //        }
    //        for ( String removeElement : removeElements )
    //        {
    //            detail.getMinComp().setCompositionElementDTOList(
    //                    detail.getMinComp().getCompositionElementDTOList().stream().filter( compositionElementDTO -> !compositionElementDTO.getName().equals( removeElement ) )
    //                            .collect( Collectors.toList() ) );
    //            detail.getMaxComp().setCompositionElementDTOList(
    //                    detail.getMaxComp().getCompositionElementDTOList().stream().filter( compositionElementDTO -> !compositionElementDTO.getName().equals( removeElement ) )
    //                            .collect( Collectors.toList() ) );
    //            for ( CompositionDTO analysis : detail.getAnalysisList() )
    //            {
    //                analysis.setCompositionElementDTOList(
    //                        analysis.getCompositionElementDTOList().stream().filter( compositionElementDTO -> !compositionElementDTO.getName().equals( removeElement ) ).collect( Collectors.toList() ) );
    //            }
    //        }
    //    }

    private static CompositionElement createCompositionElementFromDto( CompositionElementDTO compositionElementDTO )
    {
        final CompositionElement compositionElement = new CompositionElement();
        compositionElement.setName( compositionElementDTO.getName() );
        final Double value = compositionElementDTO.getValue();
        final Double warningValue = compositionElementDTO.getWarningValue();
        final Integer precision = compositionElementDTO.getPrecision();
        final Double factor = compositionElementDTO.getFactor();
        if ( value != null )
        {
            compositionElement.setOriginalElementValue( value );
        }
        if ( warningValue != null )
        {
            compositionElement.setWarningElementValue( warningValue );
        }
        if ( precision != null )
        {
            compositionElement.setDecimalPlaces( precision );
        }
        if ( factor != null )
        {
            compositionElement.setElementFactor( factor );
        }
        compositionElement.setSortOrderId( compositionElementDTO.getSortOrderId() );
        return compositionElement;
    }
}
