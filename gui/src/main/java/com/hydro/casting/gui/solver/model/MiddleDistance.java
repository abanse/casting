package com.hydro.casting.gui.solver.model;

import org.jacop.core.Store;
import org.jacop.floats.constraints.AbsPeqR;
import org.jacop.floats.constraints.LinearFloat;
import org.jacop.floats.constraints.PmulQeqR;
import org.jacop.floats.core.FloatVar;

import java.util.ArrayList;
import java.util.List;

public class MiddleDistance
{
    private String name;
    private double minElementValue;
    private double maxElementValue;
    private List<FloatVar> elementWeights;
    private List<Double> elementFactors;
    private boolean useSquare;

    public MiddleDistance( String name, double minElementValue, double maxElementValue, List<FloatVar> elementWeights, List<Double> elementFactors, boolean useSquare )
    {
        this.name = name;
        this.minElementValue = minElementValue;
        this.maxElementValue = maxElementValue;
        this.elementWeights = elementWeights;
        this.elementFactors = elementFactors;
        this.useSquare = useSquare;
    }

    public FloatVar impose( Store solver )
    {
        FloatVar distance = new FloatVar( solver, "distance" + name, -Float.MAX_VALUE, Float.MAX_VALUE );
        FloatVar posDistance = new FloatVar( solver, "posDistance" + name, 0, Float.MAX_VALUE );
        solver.impose( new AbsPeqR( distance, posDistance ) );

        ArrayList<FloatVar> optWeights = new ArrayList<>( elementWeights );
        ArrayList<Double> optFactors = new ArrayList<>( elementFactors );
        optWeights.add( distance );

        // Faktor berechnet sich anhand der distance
        double difference = maxElementValue - minElementValue;
        optFactors.add( difference / 100.0 );
        System.out.println( "### optFactors " + name + " factor " + ( difference / 100.0 ) + " target " + ( ( maxElementValue + minElementValue ) / 2.0 ) );

        LinearFloat optConstraint = new LinearFloat( solver, optWeights, optFactors, "==", ( ( maxElementValue + minElementValue ) / 2.0 ) );
        solver.impose( optConstraint );

        if ( useSquare )
        {
            FloatVar posDistanceSquare = new FloatVar( solver, "posDistanceSquare" + name, 0, Float.MAX_VALUE );
            solver.impose( new PmulQeqR( posDistance, posDistance, posDistanceSquare ) );
            return posDistanceSquare;
        }
        else
        {
            return posDistance;
        }
    }
}