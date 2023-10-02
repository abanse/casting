package com.hydro.casting.gui.model.common;

import com.hydro.casting.gui.model.*;
import javafx.beans.property.DoubleProperty;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AnalysisCalculator
{
    private final static Logger LOG = LoggerFactory.getLogger( AnalysisCalculator.class );

    public static Analysis createAverage( String name, Batch batch )
    {
        return createAverage( name, batch, ( Analysis param ) -> {
            return Boolean.TRUE;
        } );
    }

    public static Analysis createAverage( String name, Batch batch, Callback<Analysis, Boolean> includeFilter )
    {
        List<Transfer> transfers = batch.getTransfers();

        List<Analysis> analysis = new ArrayList<Analysis>();
        if ( includeFilter.call( batch.getBottomAnalysis() ) )
        {
            analysis.add( AnalysisCalculator.computeCalculatedElements( batch, false, batch.getBottomAnalysis(), batch.getBottomWeight() ) );
        }
        for ( TransferMaterial batchTransferMaterial : batch.getTransferMaterials() )
        {
            if ( includeFilter.call( batchTransferMaterial.getAnalysis() ) )
            {
                analysis.add( AnalysisCalculator.computeCalculatedElements( batch, false, batchTransferMaterial.getAnalysis(), batchTransferMaterial.getWeight() ) );
            }
        }
        for ( Transfer transfer : transfers )
        {
            if ( transfer.getWeight() > 0 )
            {
                Analysis transferAnalysis = AnalysisCalculator.createAverage( "Ø " + transfer.getName(), transfer, false );
                transferAnalysis.setWeight( transfer.getWeight() );
                if ( includeFilter.call( transferAnalysis ) )
                {
                    analysis.add( transferAnalysis );
                }
            }
        }

        return createAverageFromAnalysis( name, analysis, batch.getSpecification() );
    }

    public static Analysis createAverage( String name, Transfer transfer, boolean singleFurnace )
    {
        Analysis transferBottomAnalysis = null;
        double transferBottomWeight = 0;
        if ( singleFurnace )
        {
            transferBottomAnalysis = transfer.getBatch().getBottomAnalysis();
            transferBottomWeight = transfer.getBatch().getBottomWeight();
        }
        else
        {
            transferBottomAnalysis = transfer.getBottomAnalysis();
            transferBottomWeight = transfer.getBottomWeight();
        }
        return createAverage( name, transfer, transferBottomAnalysis, transferBottomWeight );
    }

    public static Analysis createAverage( String name, Transfer transfer, Analysis transferBottomAnalysis, double transferBottomWeight )
    {
        if ( transfer == null || transfer.getBatch() == null || transfer.getBatch().getSpecification() == null )
        {
            return null;
        }

        if ( transferBottomWeight >= transfer.getTargetWeight() )
        {
            Analysis middledAnalysis = AnalysisCalculator.computeCalculatedElements( transfer.getBatch(), true, transferBottomAnalysis, transfer.getTargetWeight() );
            middledAnalysis.setName( name );
            return middledAnalysis;
        }

        Analysis calced = new Analysis();
        calced.setName( name );

        HashMap<String, Double> calcedSumMap = new HashMap<String, Double>();
        double completeWeight = 0;
        final List<TransferMaterial> allTransferMaterials = new ArrayList<>( transfer.getTransferMaterials() );
        if ( transfer.getFillmentTransferMaterial() != null )
        {
            allTransferMaterials.add( transfer.getFillmentTransferMaterial() );
        }
        for ( TransferMaterial transferMaterial : allTransferMaterials )
        {
            double weight = transferMaterial.getWeight();
            completeWeight = completeWeight + weight;

            List<CompositionElement> matElements = AnalysisCalculator.computeCalculatedElements( transfer.getBatch(), true, transferMaterial.getAnalysis(), weight ).getCompositionElements();

            for ( CompositionElement compositionElement : matElements )
            {
                Double calcedSumObject = calcedSumMap.get( compositionElement.getName() );
                double calcedSum = 0;
                if ( calcedSumObject != null )
                {
                    calcedSum = calcedSumObject.doubleValue();
                }
                double elementValue = compositionElement.getElementValue();
                calcedSum = calcedSum + ( weight * ( elementValue / 100.0 ) );
                calcedSumMap.put( compositionElement.getName(), calcedSum );
            }
        }
        if ( transferBottomWeight > 0 && transferBottomAnalysis != null )
        {
            completeWeight = completeWeight + transferBottomWeight;
            List<CompositionElement> matElements = AnalysisCalculator.computeCalculatedElements( transfer.getBatch(), true, transferBottomAnalysis, transferBottomWeight ).getCompositionElements();

            for ( CompositionElement compositionElement : matElements )
            {
                Double calcedSumObject = calcedSumMap.get( compositionElement.getName() );
                double calcedSum = 0;
                if ( calcedSumObject != null )
                {
                    calcedSum = calcedSumObject.doubleValue();
                }
                double elementValue = compositionElement.getElementValue();
                calcedSum = calcedSum + ( transferBottomWeight * ( elementValue / 100.0 ) );
                calcedSumMap.put( compositionElement.getName(), calcedSum );
            }
        }
        calced.setWeight( completeWeight );

        setAverage( calced, calcedSumMap, transfer.getBatch().getSpecification() );

        return calced;
    }

    private static void setAverage( Analysis analysis, HashMap<String, Double> calcedSumMap, Specification specification )
    {
        for ( String elementName : calcedSumMap.keySet() )
        {
            if ( EAnalysisElement.isCalculatedElement( elementName ) == false )
            {
                final double calcedSum = calcedSumMap.get( elementName );
                final double elementValue = calcedSum / analysis.getWeight() * 100.0;
                analysis.setCompositionElementValue( elementName, elementValue );
            }
        }
        for ( String elementName : calcedSumMap.keySet() )
        {
            if ( EAnalysisElement.isCalculatedElement( elementName ) )
            {
                setCalcedElement( analysis, elementName, specification );
            }
        }
    }

    /**
     * This function adds the values to the calculated elements of the analysis. The calculated elements are present on the analysis as a composition element without a value.
     *
     * @param analysis      The analysis that should be modified to include actual values for composition elements that represent a calculated value
     * @param specification The matching specification of the analysis, which might be necessary during calculation
     */
    public static void addCalculatedElementsToAnalysis( Analysis analysis, Specification specification )
    {
        List<CompositionElement> elements = analysis.getCompositionElements();
        for ( CompositionElement compositionElement : elements )
        {
            final String name = compositionElement.getName();
            setCalcedElement( analysis, name, specification );
        }
    }

    public static Analysis computeCalculatedElements( Batch batch, boolean isTransferMaterial, Analysis analysis, double weight )
    {
        if ( analysis == null )
        {
            return null;
        }
        Specification specification = batch.getSpecification();
        if ( specification == null )
        {
            return null;
        }
        Analysis clone = analysis.clone();
        clone.setWeight( weight );

        // gibt es werte mit einer Ausbringung, dessen Faktor besetzen
        /* TODO Schmelzfaktoren hinzufügen
        final boolean isSump = "Sumpf".equals( clone.getName() );
        final String furnaceType;
        if ( ( EFurnace.CF_86240.getApk() + "GA" ).equals( batch.getCaster().getName() ) )
        {
            furnaceType = "Kombiofen";
        }
        else if ( isTransferMaterial )
        {
            furnaceType = "Schmelzofen";
        }
        else
        {
            furnaceType = "Warmhalteofen";
        }
        final String materialRefOID = clone.getRefOID();
        final String lnr = specification.getSpecId();

        List<MaterialCompositionElementRule> materialCompositionElementRules = MeltingPlan.getInstance().getMaterialCompositionElementRules();
        for ( MaterialCompositionElementRule materialCompositionElementRule : materialCompositionElementRules )
        {
            if ( furnaceType.equals( materialCompositionElementRule.getFurnaceType() ) == false )
            {
                continue;
            }
            if ( "SUMPF".equals( materialCompositionElementRule.getType() ) && isSump )
            {
                assignFactor( clone, materialCompositionElementRule.getCompositionElementName(), materialCompositionElementRule.getFactor() );
            }
            else if ( materialRefOID != null && materialRefOID.equals( materialCompositionElementRule.getMaterialRefOID() ) )
            {
                if ( "*".equals( materialCompositionElementRule.getAlloy() ) || lnr.equals( materialCompositionElementRule.getAlloy() ) )
                {
                    assignFactor( clone, materialCompositionElementRule.getCompositionElementName(), materialCompositionElementRule.getFactor() );
                }
            }
        }
         */

        Composition composition = specification.getMin();
        List<CompositionElement> elements = composition.getCompositionElements();
        for ( CompositionElement compositionElement : elements )
        {
            final String name = compositionElement.getName();
            setCalcedElement( clone, name, specification );
        }

        return clone;
    }

    private static void setCalcedElement( Analysis analysis, String elementName, Specification specification )
    {
        if ( elementName.equalsIgnoreCase( EAnalysisElement.SMAX.name() ) )
        {
            analysis.setCompositionElementValue( EAnalysisElement.SMAX.name(), calcSMAX( specification, analysis ) );
        }
        else if ( elementName.equalsIgnoreCase( EAnalysisElement.SMIN.name() ) )
        {
            analysis.setCompositionElementValue( EAnalysisElement.SMIN.name(), calcSMIN( specification, analysis ) );
        }
        else if ( elementName.equalsIgnoreCase( EAnalysisElement.SVER.name() ) )
        {
            analysis.setCompositionElementValue( EAnalysisElement.SVER.name(), calcSVER( specification, analysis ) );
        }
        else if ( elementName.equalsIgnoreCase( EAnalysisElement.AL_ZN.name() ) )
        {
            analysis.setCompositionElementValue( EAnalysisElement.AL_ZN.name(), calcAL_ZN( specification, analysis ) );
        }
        else if ( elementName.equalsIgnoreCase( EAnalysisElement.CU_AG.name() ) )
        {
            analysis.setCompositionElementValue( EAnalysisElement.CU_AG.name(), calcCU_AG( specification, analysis ) );
        }
        else if ( elementName.equalsIgnoreCase( EAnalysisElement.CU_SN_AG.name() ) )
        {
            analysis.setCompositionElementValue( EAnalysisElement.CU_SN_AG.name(), calcCU_SN_AG( specification, analysis ) );
        }
        else if ( elementName.equalsIgnoreCase( EAnalysisElement.FE_ZN.name() ) )
        {
            analysis.setCompositionElementValue( EAnalysisElement.FE_ZN.name(), calcFE_ZN( specification, analysis ) );
        }
        else if ( elementName.equalsIgnoreCase( EAnalysisElement.CU_SN.name() ) )
        {
            analysis.setCompositionElementValue( EAnalysisElement.CU_SN.name(), calcCU_SN( specification, analysis ) );
        }
        else if ( elementName.equalsIgnoreCase( EAnalysisElement.SN_PB.name() ) )
        {
            analysis.setCompositionElementValue( EAnalysisElement.SN_PB.name(), calcSN_PB( specification, analysis ) );
        }
        else if ( elementName.equalsIgnoreCase( EAnalysisElement.CU_NI_ZN.name() ) )
        {
            analysis.setCompositionElementValue( EAnalysisElement.CU_NI_ZN.name(), calcCU_NI_ZN( specification, analysis ) );
        }
        else if ( elementName.equalsIgnoreCase( EAnalysisElement.MG_P_TI1.name() ) )
        {
            analysis.setCompositionElementValue( EAnalysisElement.MG_P_TI1.name(), calcMG_P_TI1( specification, analysis ) );
        }
        else if ( elementName.equalsIgnoreCase( EAnalysisElement.MN_CO_ZR.name() ) )
        {
            analysis.setCompositionElementValue( EAnalysisElement.MN_CO_ZR.name(), calcMN_CO_ZR( specification, analysis ) );
        }
        else if ( elementName.equalsIgnoreCase( EAnalysisElement.FP.name() ) )
        {
            analysis.setCompositionElementValue( EAnalysisElement.FP.name(), calcFP( specification, analysis ) );
        }
        else if ( elementName.equalsIgnoreCase( EAnalysisElement.NISI.name() ) )
        {
            analysis.setCompositionElementValue( EAnalysisElement.NISI.name(), calcNISI( specification, analysis ) );
        }
        else if ( elementName.equalsIgnoreCase( EAnalysisElement.SI_PB_X.name() ) )
        {
            analysis.setCompositionElementValue( EAnalysisElement.SI_PB_X.name(), calcSI_PB_X( specification, analysis ) );
        }
        else if ( elementName.equalsIgnoreCase( EAnalysisElement.SI_SN_X.name() ) )
        {
            analysis.setCompositionElementValue( EAnalysisElement.SI_SN_X.name(), calcSI_SN_X( specification, analysis ) );
        }
        else if ( elementName.equalsIgnoreCase( EAnalysisElement.SI_FE_sum.name() ) )
        {
            analysis.setCompositionElementValue( EAnalysisElement.SI_FE_sum.name(), calcSI_FE_sum( specification, analysis ) );
        }
        else if ( elementName.equalsIgnoreCase( EAnalysisElement.FE_MN_sum.name() ) )
        {
            analysis.setCompositionElementValue( EAnalysisElement.FE_MN_sum.name(), calcFE_MN_sum( specification, analysis ) );
        }
        else if ( elementName.equalsIgnoreCase( EAnalysisElement.SI_FE.name() ) )
        {
            analysis.setCompositionElementValue( EAnalysisElement.SI_FE.name(), calcSI_FE( specification, analysis ) );
        }
        else if ( elementName.equalsIgnoreCase( EAnalysisElement.PB_CD_sum.name() ) )
        {
            analysis.setCompositionElementValue( EAnalysisElement.PB_CD_sum.name(), calcPB_CD_sum( specification, analysis ) );
        }
        else if ( elementName.equalsIgnoreCase( EAnalysisElement.CU_MN_MG_sum.name() ) )
        {
            analysis.setCompositionElementValue( EAnalysisElement.CU_MN_MG_sum.name(), calcCU_MN_MG_sum( specification, analysis ) );
        }
        else if ( elementName.equalsIgnoreCase( EAnalysisElement.MN_SI.name() ) )
        {
            analysis.setCompositionElementValue( EAnalysisElement.MN_SI.name(), calcMN_SI( specification, analysis ) );
        }
        else if ( elementName.equalsIgnoreCase( EAnalysisElement.V_TI_sum.name() ) )
        {
            analysis.setCompositionElementValue( EAnalysisElement.V_TI_sum.name(), calcV_TI_sum( specification, analysis ) );
        }
        else if ( elementName.equalsIgnoreCase( EAnalysisElement.ZR_TI_sum.name() ) )
        {
            analysis.setCompositionElementValue( EAnalysisElement.ZR_TI_sum.name(), calcZR_TI_sum( specification, analysis ) );
        }
    }

    private static void assignFactor( Composition composition, String elementName, double factor )
    {
        CompositionElement compositionElement = composition.getCompositionElement( elementName );
        if ( compositionElement != null )
        {
            compositionElement.setElementFactor( factor );
        }
    }

    // Summe Si + Fe
    private static double calcSI_FE_sum( Specification specification, Analysis analysis )
    {
        return sumElementValues( analysis, EAnalysisElement.Si, EAnalysisElement.Fe );
    }

    // Summe Fe + Mn
    private static double calcFE_MN_sum( Specification specification, Analysis analysis )
    {
        return sumElementValues( analysis, EAnalysisElement.Fe, EAnalysisElement.Mn );
    }

    // Verhältnis Si / Fe
    private static double calcSI_FE( Specification specification, Analysis analysis )
    {
        double si = analysis.getCompositionElementValue( EAnalysisElement.Si.name() );
        double fe = analysis.getCompositionElementValue( EAnalysisElement.Fe.name() );

        if ( fe == 0.0 )
        {
            return si;
        }
        return si / fe;
    }

    // Summe Pb + Cd
    private static double calcPB_CD_sum( Specification specification, Analysis analysis )
    {
        return sumElementValues( analysis, EAnalysisElement.Pb, EAnalysisElement.Cd );
    }

    // Summe Cu + Mn + Mg
    private static double calcCU_MN_MG_sum( Specification specification, Analysis analysis )
    {
        return sumElementValues( analysis, EAnalysisElement.Cu, EAnalysisElement.Mn, EAnalysisElement.Mg );
    }

    // Verhältnis Mn / Si
    private static double calcMN_SI( Specification specification, Analysis analysis )
    {
        double mn = analysis.getCompositionElementValue( EAnalysisElement.Mn.name() );
        double si = analysis.getCompositionElementValue( EAnalysisElement.Si.name() );

        if ( si == 0.0 )
        {
            return mn;
        }
        return mn / si;
    }

    // Summe V + Ti
    private static double calcV_TI_sum( Specification specification, Analysis analysis )
    {
        return sumElementValues( analysis, EAnalysisElement.V, EAnalysisElement.Ti );
    }

    // Summe Zr + Ti
    private static double calcZR_TI_sum( Specification specification, Analysis analysis )
    {
        return sumElementValues( analysis, EAnalysisElement.Zr, EAnalysisElement.Ti );
    }

    // Summe Si + Sn + Cr + P
    private static double calcSI_SN_X( Specification specification, Analysis analysis )
    {
        return sumElementValues( analysis, EAnalysisElement.Si, EAnalysisElement.SN, EAnalysisElement.CR, EAnalysisElement.P );
    }

    // Summe Si + Pb + Sn + Cr + P
    private static double calcSI_PB_X( Specification specification, Analysis analysis )
    {
        return sumElementValues( analysis, EAnalysisElement.Si, EAnalysisElement.Pb, EAnalysisElement.SN, EAnalysisElement.CR, EAnalysisElement.P );
    }

    // NICKEL / SILIZIUM - Verhältnis
    private static double calcNISI( Specification specification, Analysis analysis )
    {
        double ni = analysis.getCompositionElementValue( EAnalysisElement.NI.name() );
        double si = analysis.getCompositionElementValue( EAnalysisElement.Si.name() );

        if ( si == 0.0 )
        {
            return ni;
        }
        return ni / si;
    }

    // Eisen / Phosphor - Verhältnis
    private static double calcFP( Specification specification, Analysis analysis )
    {
        double fe = analysis.getCompositionElementValue( EAnalysisElement.Fe.name() );
        double p = analysis.getCompositionElementValue( EAnalysisElement.P.name() );

        if ( p == 0.0 )
        {
            return fe;
        }
        return fe / p;
    }

    // Summe Mn + Co + Zr
    private static double calcMN_CO_ZR( Specification specification, Analysis analysis )
    {
        return sumElementValues( analysis, EAnalysisElement.Mn, EAnalysisElement.CO, EAnalysisElement.Zr );
    }

    // Summe Mg + P + Ti + Fe
    private static double calcMG_P_TI1( Specification specification, Analysis analysis )
    {
        return sumElementValues( analysis, EAnalysisElement.Mg, EAnalysisElement.P, EAnalysisElement.Ti, EAnalysisElement.Fe );
    }

    // Summe Cu + Ni + Zn
    private static double calcCU_NI_ZN( Specification specification, Analysis analysis )
    {
        return sumElementValues( analysis, EAnalysisElement.Cu, EAnalysisElement.NI, EAnalysisElement.ZN );
    }

    // Summe SN + PB
    private static double calcSN_PB( Specification specification, Analysis analysis )
    {
        return sumElementValues( analysis, EAnalysisElement.SN, EAnalysisElement.Pb );
    }

    // Summe Cu + Sn
    private static double calcCU_SN( Specification specification, Analysis analysis )
    {
        return sumElementValues( analysis, EAnalysisElement.Cu, EAnalysisElement.SN );
    }

    // Summe Fe + Zn
    private static double calcFE_ZN( Specification specification, Analysis analysis )
    {
        return sumElementValues( analysis, EAnalysisElement.Fe, EAnalysisElement.ZN );
    }

    // Summe Cu + An + Ag
    private static double calcCU_SN_AG( Specification specification, Analysis analysis )
    {
        return sumElementValues( analysis, EAnalysisElement.Cu, EAnalysisElement.SN, EAnalysisElement.AG );
    }

    // Summe Cu + Ag
    private static double calcCU_AG( Specification specification, Analysis analysis )
    {
        return sumElementValues( analysis, EAnalysisElement.Cu, EAnalysisElement.AG );
    }

    // Summe Al + Zn
    private static double calcAL_ZN( Specification specification, Analysis analysis )
    {
        return sumElementValues( analysis, EAnalysisElement.AL, EAnalysisElement.ZN );
    }

    private static double sumElementValues( Analysis analysis, EAnalysisElement... elements )
    {
        double value = 0;

        for ( EAnalysisElement element : elements )
        {
            value = value + analysis.getCompositionElementValue( element.name() );
        }
        return value;
    }

    private static double calcSMAX( Specification specification, Analysis analysis )
    {
        List<CompositionElement> analysisElements = analysis.getCompositionElements();
        Composition specComposition = specification.getMin();

        double value = 0;
        for ( CompositionElement analysisElement : analysisElements )
        {
            final String name = analysisElement.getName();
            final String type = specComposition.getCompositionElementType( name );

            if ( !name.equalsIgnoreCase( EAnalysisElement.Cu.name() ) && EAnalysisElement.isSMAXRelevant( type ) )
            {
                value = value + analysisElement.getElementValue();
            }
        }
        return value;
    }

    private static double calcSMIN( Specification specification, Analysis analysis )
    {
        List<CompositionElement> matElements = analysis.getCompositionElements();
        Composition minComposition = specification.getMin();
        Composition maxComposition = specification.getMax();

        double value = 0;
        for ( CompositionElement compositionElement : matElements )
        {
            final String name = compositionElement.getName();
            final double min = minComposition.getCompositionElementValue( name );
            final double max = maxComposition.getCompositionElementValue( name );

            if ( name.equalsIgnoreCase( EAnalysisElement.Cu.name() ) || ( name.equalsIgnoreCase( EAnalysisElement.ZN.name() ) && ( max == 100.0 || min > 0.0 ) ) || min > 0 )
            {
                value = value + compositionElement.getElementValue();
            }
        }
        return value;
    }

    private static double calcSVER( Specification specification, Analysis analysis )
    {
        List<CompositionElement> matElements = analysis.getCompositionElements();
        Composition minComposition = specification.getMin();
        Composition maxComposition = specification.getMax();

        double value = 0;
        for ( CompositionElement compositionElement : matElements )
        {
            final String name = compositionElement.getName();
            if ( name.equalsIgnoreCase( EAnalysisElement.Cu.name() ) )
            {
                continue;
            }
            final DoubleProperty minProp = minComposition.compositionElementValueProperty( name );
            final DoubleProperty maxProp = maxComposition.compositionElementValueProperty( name );

            if ( minProp != null && maxProp != null )
            {
                final double min = minProp.get();
                final double max = maxProp.get();
                if ( max > 0 && max <= 100.0 )
                {
                    continue;
                }
                if ( min <= 0 )
                {
                    continue;
                }
            }
            value = value + compositionElement.getElementValue();
        }
        return value;
    }

    public static Analysis createAverageFromAnalysis( String name, List<Analysis> analysis, Specification specification )
    {
        Analysis calced = new Analysis();
        calced.setName( name );

        HashMap<String, Double> calcedSumMap = new HashMap<String, Double>();
        double completeWeight = 0;
        for ( Analysis ana : analysis )
        {
            double weight = ana.getWeight();
            completeWeight = completeWeight + weight;

            List<CompositionElement> matElements = ana.getCompositionElements();

            for ( CompositionElement compositionElement : matElements )
            {
                Double calcedSumObject = calcedSumMap.get( compositionElement.getName() );
                double calcedSum = 0;
                if ( calcedSumObject != null )
                {
                    calcedSum = calcedSumObject.doubleValue();
                }
                double elementValue = compositionElement.getElementValue();
                calcedSum = calcedSum + ( weight * ( elementValue / 100.0 ) );
                calcedSumMap.put( compositionElement.getName(), calcedSum );
            }
        }
        calced.setWeight( completeWeight );

        setAverage( calced, calcedSumMap, specification );

        return calced;
    }

    public static String specConform( Transfer transfer )
    {
        if ( transfer == null )
        {
            return null;
        }

        HashMap<String, Double> calcedSumMap = new HashMap<>();

        Specification spec = transfer.getBatch().getSpecification();
        /*
        if ( transfer.isUseStandardSpec() )
        {
            spec = MeltingPlan.getInstance().getStandardSpecMainData().getSpecification();
        }
         */

        if ( spec == null )
        {
            return null;
        }

        Analysis transferBottomAnalysis = transfer.getBottomAnalysis();

        // check only bottom analysis
        if ( transfer.getBottomWeight() >= transfer.getTargetWeight() )
        {
            List<CompositionElement> compositionElements = AnalysisCalculator.computeCalculatedElements( transfer.getBatch(), true, transferBottomAnalysis, transferBottomAnalysis.getWeight() )
                    .getCompositionElements();

            for ( CompositionElement compositionElement : compositionElements )
            {
                calcedSumMap.put( compositionElement.getName(), compositionElement.getElementValue() );
            }

            String conformString = getConformString( calcedSumMap, 100.0, spec );// transferBottomAnalysis.getWeight(),
            // spec);
            return conformString;
        }

        double completeWeight = 0;
        final List<TransferMaterial> allTransferMaterials = new ArrayList<>( transfer.getTransferMaterials() );
        if ( transfer.getFillmentTransferMaterial() != null )
        {
            allTransferMaterials.add( transfer.getFillmentTransferMaterial() );
        }
        for ( TransferMaterial transferMaterial : allTransferMaterials )
        {
            double weight = transferMaterial.getWeight();

            completeWeight = completeWeight + weight;

            if ( transfer.getBatch() == null )
            {
                continue;
            }

            List<CompositionElement> matElements = AnalysisCalculator.computeCalculatedElements( transfer.getBatch(), true, transferMaterial.getAnalysis(), weight ).getCompositionElements();

            for ( CompositionElement compositionElement : matElements )
            {
                Double calcedSumObject = calcedSumMap.get( compositionElement.getName() );
                double calcedSum = 0;
                if ( calcedSumObject != null )
                {
                    calcedSum = calcedSumObject.doubleValue();
                }
                double elementValue = compositionElement.getElementValue();
                calcedSum = calcedSum + ( weight * ( elementValue / 100.0 ) );
                calcedSumMap.put( compositionElement.getName(), calcedSum );
            }
        }
        if ( transfer.getBottomWeight() > 0 && transferBottomAnalysis != null )
        {
            double weight = transfer.getBottomWeight();
            completeWeight = completeWeight + weight;
            List<CompositionElement> matElements = AnalysisCalculator.computeCalculatedElements( transfer.getBatch(), true, transferBottomAnalysis, weight ).getCompositionElements();

            for ( CompositionElement compositionElement : matElements )
            {
                Double calcedSumObject = calcedSumMap.get( compositionElement.getName() );
                double calcedSum = 0;
                if ( calcedSumObject != null )
                {
                    calcedSum = calcedSumObject.doubleValue();
                }
                double elementValue = compositionElement.getElementValue();
                calcedSum = calcedSum + ( weight * ( elementValue / 100.0 ) );
                calcedSumMap.put( compositionElement.getName(), calcedSum );
            }
        }
        if ( completeWeight <= 0 )
        {
            return "";
        }

        return getConformString( calcedSumMap, completeWeight, spec );
    }

    private static String getConformString( HashMap<String, Double> calcedSumMap, double completeWeight, Specification spec )
    {
        final StringBuffer stb = new StringBuffer( "" );

        final Analysis tempAnalysis = new Analysis();
        // 2 stufig bearbeiten, erst die normalen
        for ( String elementName : calcedSumMap.keySet() )
        {
            if ( EAnalysisElement.isCalculatedElement( elementName ) )
            {
                continue;
            }
            final int decimalPlaces = getDecimalPlaces( spec, elementName );

            final double calcedSum = calcedSumMap.get( elementName );
            final double value = round( ( calcedSum / completeWeight * 100.0 ), decimalPlaces );

            tempAnalysis.setCompositionElementValue( elementName, value );

            final double maxValue = round( spec.getMax().getCompositionElementValue( elementName ), decimalPlaces );
            final double minValue = round( spec.getMin().getCompositionElementValue( elementName ), decimalPlaces );
            if ( maxValue > 0 && value > maxValue )
            {
                stb.append( elementName + "\u2191 " );
            }
            if ( minValue > 0 && value < minValue )
            {
                stb.append( elementName + "\u2193 " );
            }
        }
        // dann die berechneten
        for ( String elementName : calcedSumMap.keySet() )
        {
            if ( EAnalysisElement.isCalculatedElement( elementName ) == false )
            {
                continue;
            }
            setCalcedElement( tempAnalysis, elementName, spec );
            final int decimalPlaces = getDecimalPlaces( spec, elementName );

            final double value = round( tempAnalysis.getCompositionElementValue( elementName ), decimalPlaces );
            final double maxValue = round( spec.getMax().getCompositionElementValue( elementName ), decimalPlaces );
            final double minValue = round( spec.getMin().getCompositionElementValue( elementName ), decimalPlaces );
            if ( maxValue > 0 && value > maxValue )
            {
                stb.append( elementName + "\u2191 " );
            }
            if ( minValue > 0 && value < minValue )
            {
                stb.append( elementName + "\u2193 " );
            }
        }

        return stb.toString();
    }

    public static String specConform( Batch batch )
    {
        if ( batch == null || batch.getSpecification() == null || batch.getTransfers().isEmpty() )
        {
            return null;
        }
        List<Analysis> analysis = new ArrayList<Analysis>();
        if ( batch.getBottomWeight() > 0 && batch.getBottomAnalysis() != null )
        {
            analysis.add( AnalysisCalculator.computeCalculatedElements( batch, false, batch.getBottomAnalysis(), batch.getBottomWeight() ) );
        }
        for ( TransferMaterial batchTransferMaterial : batch.getTransferMaterials() )
        {
            analysis.add( AnalysisCalculator.computeCalculatedElements( batch, false, batchTransferMaterial.getAnalysis(), batchTransferMaterial.getWeight() ) );
        }
        for ( Transfer transfer : batch.getTransfers() )
        {
            if ( transfer.getWeight() <= 0 )
            {
                continue;
            }

            Analysis transferAnalyse = createAverage( transfer.getName(), transfer, false );
            if ( transferAnalyse != null )
            {
                transferAnalyse.setWeight( transfer.getWeight() );
                analysis.add( transferAnalyse );
            }
        }
        if ( analysis.isEmpty() )
        {
            return "";
        }

        HashMap<String, Double> calcedSumMap = new HashMap<String, Double>();
        double completeWeight = 0;
        for ( Analysis ana : analysis )
        {
            double weight = ana.getWeight();
            completeWeight = completeWeight + weight;

            List<CompositionElement> matElements = ana.getCompositionElements();

            for ( CompositionElement compositionElement : matElements )
            {
                Double calcedSumObject = calcedSumMap.get( compositionElement.getName() );
                double calcedSum = 0;
                if ( calcedSumObject != null )
                {
                    calcedSum = calcedSumObject.doubleValue();
                }
                double elementValue = compositionElement.getElementValue();
                calcedSum = calcedSum + ( weight * ( elementValue / 100.0 ) );
                calcedSumMap.put( compositionElement.getName(), calcedSum );
            }
        }

        Specification spec = batch.getSpecification();

        return getConformString( calcedSumMap, completeWeight, spec );
    }

    public static int getDecimalPlaces( Specification specification, String elementName )
    {
        int decimalPlaces = 0;
        if ( specification.getMin() != null && specification.getMin().getCompositionElement( elementName ) == null && specification.getMax() != null
                && specification.getMax().getCompositionElement( elementName ) == null )
        {
            return 2;
        }

        if ( specification.getMax() != null )
        {
            decimalPlaces = specification.getMax().getCompositionElementDecimalPlaces( elementName );
        }
        if ( specification.getMin() != null )
        {
            int minDP = specification.getMin().getCompositionElementDecimalPlaces( elementName );
            if ( minDP > decimalPlaces )
            {
                decimalPlaces = minDP;
            }
        }
        return decimalPlaces;
    }

    public static double round( double wert, int stellen )
    {
        if ( Double.isNaN( wert ) )
        {
            return wert;
        }

        return BigDecimal.valueOf( wert ).setScale( stellen, RoundingMode.HALF_EVEN ).doubleValue();
    }

    public static double minus0dot5( double wert, int stellen )
    {
        final double oDot5 = 4.9 * Math.pow( 10, -( stellen + 1 ) );
        final double retValue = round( wert - oDot5, stellen + 2 );
        return retValue;
    }

    public static double plus0dot5( double wert, int stellen )
    {
        final double oDot5 = 4.9 * Math.pow( 10, -( stellen + 1 ) );
        final double retValue = round( wert + oDot5, stellen + 2 );
        return retValue;
    }

    public static boolean isAnalyse( String name )
    {
        if ( name == null )
        {
            return true;
        }
        String[] startsWithNames = new String[] { "Sumpf", "Ø", "AS ", "FM ", "ÜF " };
        String[] endsWithNames = new String[] { " Min", " Max" };
        for ( String part : startsWithNames )
        {
            if ( name.startsWith( part ) )
            {
                return true;
            }
        }
        for ( String part : endsWithNames )
        {
            if ( name.endsWith( part ) )
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isASAnalysis( String name )
    {
        if ( name == null )
        {
            return false;
        }
        String[] startsWithNames = new String[] { "AS ", "FM " };
        for ( String part : startsWithNames )
        {
            if ( name.startsWith( part ) )
            {
                return true;
            }
        }
        return false;
    }

    public static boolean compare( Analysis source, Analysis dest )
    {
        if ( source == null && dest == null )
        {
            return true;
        }
        if ( source == null || dest == null )
        {
            return false;
        }
        if ( source.getCompositionElements().size() != dest.getCompositionElements().size() )
        {
            return false;
        }
        List<CompositionElement> sourceElements = source.getCompositionElements();
        for ( CompositionElement sourceElement : sourceElements )
        {
            CompositionElement destElement = dest.getCompositionElement( sourceElement.getName() );
            if ( destElement == null )
            {
                return false;
            }
            if ( sourceElement.getElementValue() != destElement.getElementValue() )
            {
                return false;
            }
        }

        return true;
    }

    public static int numberOfDigits( double x )
    {
        String[] splitter = ( "" + x ).split( "\\." );
        return splitter[1].length();
    }

    public static void dumpAnalysis( Analysis analysis )
    {
        if ( analysis == null )
            LOG.debug( "========================================================================" );
        LOG.debug( "Analyse: " + analysis.getName() );
        LOG.debug( "Gewicht: " + analysis.getWeight() );
        LOG.debug( "Elemente:" );
        List<CompositionElement> elements = analysis.getCompositionElements();
        for ( CompositionElement element : elements )
        {
            LOG.debug( "\t" + element.getName() + " = " + element.getElementValue() );
        }
    }

    public static void fillAnalysisWithSpecification( Analysis analysis, Specification specification )
    {
        Composition min = specification.getMin();
        Composition max = specification.getMax();

        String maxFillElementName = null;
        double currentPercentWeight = 0;
        List<CompositionElement> elements = min.getCompositionElements();
        for ( CompositionElement element : elements )
        {
            String elementName = element.getName();

            if ( EAnalysisElement.isCalculatedElement( elementName ) )
            {
                continue;
            }

            double minValue = min.getCompositionElementValue( elementName );
            double maxValue = max.getCompositionElementValue( elementName );
            if ( maxValue == 100.0 )
            {
                maxFillElementName = elementName;
            }
            else if ( minValue > 0 )
            {
                if ( maxValue > minValue )
                {
                    analysis.setCompositionElementValue( elementName, ( minValue + maxValue ) / 2.0 );
                }
                else
                {
                    analysis.setCompositionElementValue( elementName, minValue );
                }
            }
            currentPercentWeight = currentPercentWeight + analysis.getCompositionElementValue( elementName );
        }
        if ( maxFillElementName != null )
        {
            analysis.setCompositionElementValue( maxFillElementName, 100.0 - currentPercentWeight );
        }
        else
        {
            final double currentCU = analysis.getCompositionElementValue( EAnalysisElement.Cu.name() );
            analysis.setCompositionElementValue( EAnalysisElement.Cu.name(), currentCU + ( 100.0 - currentPercentWeight ) );
        }
    }
}
