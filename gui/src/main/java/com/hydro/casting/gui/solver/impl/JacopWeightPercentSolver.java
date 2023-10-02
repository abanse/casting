package com.hydro.casting.gui.solver.impl;

import com.hydro.casting.gui.model.*;
import com.hydro.casting.gui.model.common.AnalysisCalculator;
import com.hydro.casting.gui.model.common.EAnalysisElement;
import com.hydro.casting.gui.solver.exception.NoSolutionFoundException;
import com.hydro.casting.gui.solver.model.MiddleDistance;
import com.hydro.casting.gui.solver.model.SolverVariable;
import org.jacop.constraints.LinearInt;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.floats.constraints.LinearFloat;
import org.jacop.floats.constraints.XeqP;
import org.jacop.floats.core.FloatDomain;
import org.jacop.floats.core.FloatVar;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.IndomainMin;
import org.jacop.search.SelectChoicePoint;
import org.jacop.search.SimpleSelect;

import java.util.*;

public class JacopWeightPercentSolver extends ASolver
{
    @Override
    public void solve( List<SolverVariable> solverVariables, Transfer transfer, List<SolverVariable> fixedTransferMaterials, SolverVariable fillmentTransferMaterial, double varWeights,
            SolverHint... solverHints ) throws NoSolutionFoundException
    {
        FloatDomain.setPrecision( 1.0e-12 );

        Store solver = new Store();

        final Batch batch = transfer.getBatch();

        Specification spec = batch.getSpecification();
        //        if ( transfer.isUseStandardSpec() )
        //        {
        //            spec = MeltingPlan.getInstance().getStandardSpecMainData().getSpecification();
        //        }

        final Composition min = spec.getMin();

        ArrayList<IntVar> allWeightList = new ArrayList<>();
        ArrayList<Analysis> allAnalysisList = new ArrayList<>();

        int targetWeight = (int) Math.round( transfer.getTargetWeight() );
        int bottomWeight = (int) Math.round( transfer.getBottomWeight() );
        Analysis bottomAnalysis = transfer.getBottomAnalysis();
        //        if ( transfer.getFrom() != null && EFurnace.CF_86240.getApk().equals( transfer.getFrom().getName() ) )
        //        {
        //            bottomWeight = (int) Math.round( transfer.getBatch().getBottomWeight() );
        //            bottomAnalysis = transfer.getBatch().getBottomAnalysis();
        //            targetWeight = (int) Math.round( transfer.getBatch().getFurnaceTargetWeight() );
        //        }

        if ( bottomAnalysis != null )
        {
            IntVar bottomWeightIV = new IntVar( solver, "bottomWeight", bottomWeight, bottomWeight );
            allWeightList.add( bottomWeightIV );
            allAnalysisList.add( AnalysisCalculator.computeCalculatedElements( batch, true, bottomAnalysis, bottomWeight ) );
        }

        IntVar[] fixedMaterialWeights = new IntVar[fixedTransferMaterials.size()];
        int index = 0;
        for ( SolverVariable transferMaterial : fixedTransferMaterials )
        {
            int transferMaterialWeight = (int) Math.round( transferMaterial.getWeight() );
            fixedMaterialWeights[index] = new IntVar( solver, transferMaterial.getName(), transferMaterialWeight, transferMaterialWeight );
            allWeightList.add( fixedMaterialWeights[index] );

            allAnalysisList.add( AnalysisCalculator.computeCalculatedElements( batch, true, transferMaterial.getAnalysis(), transferMaterial.getWeight() ) );
            index++;
        }

        int variableMatWeight = (int) Math.round( varWeights );
        if ( fillmentTransferMaterial != null )
        {
            variableMatWeight = variableMatWeight + (int) Math.round( fillmentTransferMaterial.getWeight() );
        }
        List<IntVar> variableMaterialWeights = new ArrayList<>();
        List<Analysis> variableMatAnalysiss = new ArrayList<>();

        ArrayList<IntVar> sumVariableWeights = new ArrayList<>();
        ArrayList<Integer> sumVariableFactors = new ArrayList<>();
        ArrayList<IntVar> minimizeVariableWeights = new ArrayList<>();
        Map<IntVar, Double> minimizeVariableWeightFactory = new HashMap<>();

        for ( SolverVariable solverVariable : solverVariables )
        {
            int maxValue = variableMatWeight;
            if ( solverVariable.getMaximumValue() > 0 && maxValue > solverVariable.getMaximumValue() )
            {
                maxValue = (int) Math.round( solverVariable.getMaximumValue() );
            }

            IntVar variableMaterialWeight = new IntVar( solver, solverVariable.getName(), (int) Math.round( solverVariable.getMinimumValue() ), maxValue );
            variableMaterialWeights.add( variableMaterialWeight );
            allWeightList.add( variableMaterialWeight );

            sumVariableWeights.add( variableMaterialWeight );
            sumVariableFactors.add( new Integer( 1 ) );

            allAnalysisList.add( AnalysisCalculator.computeCalculatedElements( batch, true, solverVariable.getAnalysis(), 1 ) );
            variableMatAnalysiss.add( solverVariable.getAnalysis() );

            if ( solverVariable.isMinimize() )
            {
                minimizeVariableWeights.add( variableMaterialWeight );
                minimizeVariableWeightFactory.put( variableMaterialWeight, solverVariable.getMinimizeFactor() );
            }
        }

        IntVar specWeight = null;
        if ( fillmentTransferMaterial != null )
        {
            specWeight = new IntVar( solver, "specWeight", 1, variableMatWeight );
            sumVariableWeights.add( specWeight );
            sumVariableFactors.add( new Integer( 1 ) );

            allWeightList.add( specWeight );
            allAnalysisList.add( AnalysisCalculator.computeCalculatedElements( batch, true, fillmentTransferMaterial.getAnalysis(), 1 ) );
        }

        LinearInt sumVariableWeight = new LinearInt( solver, sumVariableWeights, sumVariableFactors, "==", variableMatWeight );
        solver.impose( sumVariableWeight );

        IntVar[] allWeights = allWeightList.toArray( new IntVar[allWeightList.size()] );
        Analysis[] allAnalysis = allAnalysisList.toArray( new Analysis[allAnalysisList.size()] );

        FloatVar[] allWeightsFV = new FloatVar[allWeights.length];
        FloatVar[] minimizeVariableWeightsFV = new FloatVar[minimizeVariableWeights.size()];
        Double[] minimizeVariableFactors = new Double[minimizeVariableWeights.size()];
        int minimizeVariableIndex = 0;
        for ( int i = 0; i < allWeights.length; i++ )
        {
            IntVar weightIV = allWeights[i];
            FloatVar weightFV = new FloatVar( solver, weightIV.min(), weightIV.max() );
            solver.impose( new XeqP( weightIV, weightFV ) );
            allWeightsFV[i] = weightFV;

            if ( minimizeVariableWeights.contains( weightIV ) )
            {
                minimizeVariableWeightsFV[minimizeVariableIndex] = weightFV;
                minimizeVariableFactors[minimizeVariableIndex] = minimizeVariableWeightFactory.get( weightIV );
                minimizeVariableIndex++;
            }
        }

        List<MiddleDistance> middleDistances = new ArrayList<>();

        List<CompositionElement> minElements = min.getCompositionElements();
        for ( CompositionElement minElement : minElements )
        {
            int minDigits = min.getCompositionElementDecimalPlaces( minElement.getName() );
            if ( minDigits <= 0 )
            {
                minDigits = 2;
            }
            int maxDigits = spec.getMax().getCompositionElementDecimalPlaces( minElement.getName() );
            if ( maxDigits <= 0 )
            {
                maxDigits = minDigits;
            }

            double minElementValue = 0;
            double minSpecValue = minElement.getElementValue();
            if ( minSpecValue > 0 )
            {
                minElementValue = minSpecValue;
                minElementValue = AnalysisCalculator.round( minElementValue, minDigits );
                minElementValue = AnalysisCalculator.minus0dot5( minElementValue, minDigits );
            }
            double maxElementValue = 100.0;
            double maxSpecValue = spec.getMax().getCompositionElementValue( minElement.getName() );
            // BugFixing FP min min und max wert
            if ( minSpecValue <= 0 && maxSpecValue <= 0 || EAnalysisElement.isCalculatedElement( minElement.getName() ) )
            {
                continue;
            }
            // Berechne Zielwert bei Elementen mit 100% Kennzeichen
            if ( minSpecValue <= 0 && maxSpecValue >= 100.0 )
            {
                // Dabei gillt Mittelwert besetzen und doppelte von Mittelwert
                // ist Max Value
                double targetValue = getMaxFillElementValue( spec );
                maxElementValue = targetValue * 2.0;
                maxElementValue = AnalysisCalculator.round( maxElementValue, maxDigits );
                maxElementValue = AnalysisCalculator.plus0dot5( maxElementValue, maxDigits );
            }
            else if ( maxSpecValue > 0 && maxSpecValue < 100.0 )
            {
                maxElementValue = maxSpecValue;
                maxElementValue = AnalysisCalculator.round( maxElementValue, maxDigits );
                maxElementValue = AnalysisCalculator.plus0dot5( maxElementValue, maxDigits );
            }

            double middleElementValue = 0;
            if ( ( minSpecValue > 0 && maxSpecValue > 0 ) || maxSpecValue >= 100 )
            {
                // Prüfen ob dieses Element in variablen Felder enthalten ist
                boolean exist = false;
                List<Analysis> toCheckedAnalysis = new ArrayList<>();
                toCheckedAnalysis.addAll( variableMatAnalysiss );
                if ( fillmentTransferMaterial != null )
                {
                    toCheckedAnalysis.add( fillmentTransferMaterial.getAnalysis() );
                }
                for ( Analysis toCheckAnalyse : toCheckedAnalysis )
                {
                    List<CompositionElement> elements = toCheckAnalyse.getCompositionElements();
                    for ( CompositionElement element : elements )
                    {
                        if ( element.getName().equals( minElement.getName() ) )
                        {
                            exist = true;
                            break;
                        }
                    }
                    if ( exist )
                    {
                        break;
                    }
                }

                if ( exist )
                {
                    middleElementValue = ( minElementValue + maxElementValue ) / 2.0;
                }
            }

            index = 0;
            ArrayList<FloatVar> elementWeights = new ArrayList<>();
            ArrayList<Double> elementFactors = new ArrayList<>();
            for ( Analysis analysis : allAnalysis )
            {
                double value = analysis.getCompositionElementValue( minElement.getName() );

                if ( value > 0 )
                {
                    elementWeights.add( allWeightsFV[index] );
                    elementFactors.add( new Double( value / targetWeight ) );
                }

                index++;
            }

            if ( elementWeights.isEmpty() )
            {
                continue;
            }

            if ( containHint( solverHints, SolverHint.FIND_CONCRETE_TARGET ) && middleElementValue > 0 )
            {
                LinearFloat middleConstraint = new LinearFloat( solver, elementWeights, elementFactors, "==", middleElementValue );
                solver.impose( middleConstraint );
            }
            else
            {
                if ( minElementValue > 0 )
                {
                    LinearFloat minConstraint = new LinearFloat( solver, elementWeights, elementFactors, ">=", minElementValue );
                    solver.impose( minConstraint );
                }
                if ( containHint( solverHints, SolverHint.FIND_WINDOW_MIN_TO_MIDDLE ) && middleElementValue > 0 )
                {
                    LinearFloat midConstraint = new LinearFloat( solver, elementWeights, elementFactors, "<=", middleElementValue );
                    solver.impose( midConstraint );
                }
                else
                {
                    if ( maxElementValue < 100 )
                    {
                        LinearFloat maxConstraint = new LinearFloat( solver, elementWeights, elementFactors, "<=", maxElementValue );
                        solver.impose( maxConstraint );
                    }

                    if ( middleElementValue > 0 )
                    {
                        middleDistances.add( new MiddleDistance( minElement.getName(), minElementValue, maxElementValue, elementWeights, elementFactors, false ) );
                    }
                }
            }
        }

        List<FloatVar> optimumValues = new ArrayList<>();
        for ( MiddleDistance middleDistance : middleDistances )
        {
            optimumValues.add( middleDistance.impose( solver ) );
        }

        final boolean foundSolution;
        IntVar[] result = null;

        FloatVar optimum = null;
        if ( containHint( solverHints, SolverHint.FIND_TARGETS ) )
        {
            if ( optimumValues.size() == 1 )
            {
                optimum = optimumValues.get( 0 );
            }
            else if ( !optimumValues.isEmpty() )
            {
                optimum = new FloatVar( solver, "analysisOptimum", 0, Float.MAX_VALUE );

                ArrayList<FloatVar> optWeights = new ArrayList<>( optimumValues );
                ArrayList<Double> optFactors = new ArrayList<>();
                for ( int i = 0; i < optimumValues.size(); i++ )
                {
                    optFactors.add( new Double( ( i + 1 ) * 100 ) );
                }
                optWeights.add( optimum );
                optFactors.add( new Double( -1.0 ) );

                LinearFloat optConstraint = new LinearFloat( solver, optWeights, optFactors, "==", 0 );
                solver.impose( optConstraint );
            }
        }

        if ( minimizeVariableWeightsFV.length > 0 )
        {
            ArrayList<FloatVar> minWeights = new ArrayList<>( Arrays.asList( minimizeVariableWeightsFV ) );
            ArrayList<Double> minFactors = new ArrayList<>( Arrays.asList( minimizeVariableFactors ) );
            if ( optimum != null )
            {
                minWeights.add( optimum );
                minFactors.add( new Double( 1.0 ) );
            }
            optimum = new FloatVar( solver, "minimizeMatOptimum", 0, Float.MAX_VALUE );
            minWeights.add( optimum );
            minFactors.add( new Double( -1.0 ) );

            LinearFloat optConstraint = new LinearFloat( solver, minWeights, minFactors, "==", 0 );
            solver.impose( optConstraint );
        }

        if ( containHint( solverHints, SolverHint.FIND_SPEC_MATERIAL_WEIGHT ) )
        {
            boolean consistent = solver.consistency();
            // System.out.println("===================================");
            // System.out.println("consistent: " + consistent);
            // if (solverHints != null)
            // {
            // for (SolverHint solverHint : solverHints)
            // {
            // System.out.println("Hint: " + solverHint.name());
            // }
            // }
            // System.out.println("Weight: " + variableMatWeight);
            // for (IntVar variableWeights : sumVariableWeights)
            // {
            // System.out.println(variableWeights.toString());
            // }

            // // berechne Menge aus max Auflegierungsmaterialien +
            // -variableMatWeight
            // double sumVarWeights = 0;
            // for (IntVar variableWeights : sumVariableWeights)
            // {
            // if ("AS".equals(variableWeights.id) ||
            // "specWeight".equals(variableWeights.id))
            // {
            // continue;
            // }
            // sumVarWeights = sumVarWeights + variableWeights.max();
            // }

            if ( consistent )
            {
                // fillmentTransferMaterial.setWeight(variableMatWeight -
                // sumVarWeights);
                fillmentTransferMaterial.setWeight( specWeight.max() );
                return;
            }
            else
            {
                throw new NoSolutionFoundException();
            }
        }

        SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>( sumVariableWeights.toArray( new IntVar[sumVariableWeights.size()] ), null, new IndomainMin<IntVar>() );

        DepthFirstSearch<IntVar> depthFirstSearch = new DepthFirstSearch<IntVar>();
        depthFirstSearch.setTimeOut( 20 );
        search = depthFirstSearch;

        if ( optimum == null )
        {
            foundSolution = depthFirstSearch.labeling( solver, select );
        }
        else
        {
            foundSolution = depthFirstSearch.labeling( solver, select, optimum );
        }

        // solver.print();
        if ( foundSolution )
        {
            result = variableMaterialWeights.toArray( new IntVar[variableMaterialWeights.size()] );
        }
        else
        {
            throw new NoSolutionFoundException();
        }

        if ( result != null )
        {
            int i = 0;
            for ( IntVar resultInterval : result )
            {
                if ( resultInterval.max() <= 0.0 )
                {
                    solverVariables.get( i ).setWeight( 0.0001 );
                }
                else
                {
                    solverVariables.get( i ).setWeight( ( resultInterval.min() + resultInterval.max() ) / 2.0 );
                }
                i++;
            }
        }
        if ( specWeight.max() <= 0.0 )
        {
            fillmentTransferMaterial.setWeight( 0.0001 );
        }
        else
        {
            fillmentTransferMaterial.setWeight( ( specWeight.min() + specWeight.max() ) / 2.0 );
        }
    }

    private double getMaxFillElementValue( Specification specification )
    {
        Composition min = specification.getMin();
        Composition max = specification.getMax();

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
                continue;
            }

            if ( minValue > 0 )
            {
                if ( maxValue > minValue )
                {
                    currentPercentWeight = currentPercentWeight + ( ( minValue + maxValue ) / 2.0 );
                }
                else
                {
                    currentPercentWeight = currentPercentWeight + minValue;
                }
            }
        }
        return ( 100.0 - currentPercentWeight );
    }

}
