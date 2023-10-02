package com.hydro.casting.gui.solver.model;

import com.hydro.casting.gui.model.Analysis;
import com.hydro.casting.gui.model.Material;
import com.hydro.casting.gui.model.TransferMaterial;
import com.hydro.casting.gui.model.common.AnalysisCalculator;
import com.hydro.casting.gui.model.common.ETransferMaterialType;
import org.jacop.core.IntVar;

import java.util.Collections;
import java.util.Set;

public class SolverVariable
{
    private Analysis analysis;
    private Material material;
    private Set<TransferMaterial> transferMaterials;
    private double weight;
    private boolean minimize = false;
    private double minimizeFactor = 1.0;
    private double minimumValue = 0.0;
    private double maximumValue = 0.0;
    private IntVar calculatedValue;

    public SolverVariable( Analysis analysis )
    {
        this.analysis = analysis;
    }

    public SolverVariable( Material material )
    {
        this.material = material;
    }

    public SolverVariable( Analysis analysis, Material material )
    {
        this.analysis = analysis;
        this.material = material;
    }

    public SolverVariable( TransferMaterial transferMaterials )
    {
        this( Collections.singleton( transferMaterials ) );
    }

    public SolverVariable( Set<TransferMaterial> transferMaterials )
    {
        this.transferMaterials = transferMaterials;
        this.minimize = false;
        if ( transferMaterials != null && transferMaterials.isEmpty() == false )
        {
            double tmWeight = 0;
            for ( TransferMaterial transferMaterial : transferMaterials )
            {
                ETransferMaterialType type = ETransferMaterialType.findType( transferMaterial.getType() );
                if ( type == ETransferMaterialType.VARIABLE )
                {
                    this.minimize = true;
                }
                tmWeight = tmWeight + transferMaterial.getWeight();
            }
            this.weight = tmWeight;
        }
    }

    public Analysis getAnalysis()
    {
        if ( analysis != null )
        {
            return analysis;
        }
        if ( material != null )
        {
            return material.getAnalysis();
        }
        if ( transferMaterials != null && transferMaterials.isEmpty() == false )
        {
            return transferMaterials.iterator().next().getAnalysis();
        }
        return null;
    }

    public String getName()
    {
        if ( analysis != null )
        {
            return analysis.getName();
        }
        if ( material != null )
        {
            return material.getName();
        }
        if ( transferMaterials != null && transferMaterials.isEmpty() == false )
        {
            return transferMaterials.iterator().next().getName();
        }
        return null;
    }

    public double getWeight()
    {
        return weight;
    }

    public void setWeight( double weight )
    {
        this.weight = weight;
    }

    public boolean isMinimize()
    {
        return minimize;
    }

    public void setMinimize( boolean minimize )
    {
        this.minimize = minimize;
    }

    public double getMinimizeFactor()
    {
        return minimizeFactor;
    }

    public void setMinimizeFactor( double minimizeFactor )
    {
        this.minimizeFactor = minimizeFactor;
    }

    public Material getMaterial()
    {
        return material;
    }

    public double getMinimumValue()
    {
        return minimumValue;
    }

    public void setMinimumValue( double minimumValue )
    {
        this.minimumValue = minimumValue;
    }

    public double getMaximumValue()
    {
        return maximumValue;
    }

    public void setMaximumValue( double maximumValue )
    {
        this.maximumValue = maximumValue;
    }

    public Set<TransferMaterial> getTransferMaterials()
    {
        return transferMaterials;
    }

    public IntVar getCalculatedValue()
    {
        return calculatedValue;
    }

    public void setCalculatedValue( IntVar calculatedValue )
    {
        this.calculatedValue = calculatedValue;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( analysis == null ) ? 0 : analysis.hashCode() );
        result = prime * result + ( ( material == null ) ? 0 : material.hashCode() );
        result = prime * result + ( ( transferMaterials == null ) ? 0 : transferMaterials.hashCode() );
        long temp;
        temp = Double.doubleToLongBits( weight );
        result = prime * result + (int) ( temp ^ ( temp >>> 32 ) );
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;

        if ( obj instanceof SolverVariable && getAnalysis() != null && ( (SolverVariable) obj ).getAnalysis() != null )
        {
            Analysis thisAnalysis = getAnalysis();
            Analysis compAnalysis = ( (SolverVariable) obj ).getAnalysis();
            if ( AnalysisCalculator.compare( thisAnalysis, compAnalysis ) )
            {
                return true;
            }
        }

        if ( getClass() != obj.getClass() )
            return false;
        SolverVariable other = (SolverVariable) obj;
        if ( material == null )
        {
            if ( other.material != null )
                return false;
        }
        else if ( !material.equals( other.material ) )
            return false;
        if ( analysis == null )
        {
            if ( other.analysis != null )
                return false;
        }
        else if ( !analysis.equals( other.analysis ) )
            return false;
        if ( transferMaterials == null )
        {
            if ( other.transferMaterials != null )
                return false;
        }
        else if ( !transferMaterials.equals( other.transferMaterials ) )
            return false;
        if ( Double.doubleToLongBits( weight ) != Double.doubleToLongBits( other.weight ) )
            return false;
        return true;
    }
}
