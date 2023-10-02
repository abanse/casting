package com.hydro.casting.server.model.mat;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;

@Entity
@DiscriminatorValue( "crucible_material" )
@NamedQuery( name = "crucibleMaterial.weightSystemAndReference", query = "select cm from CrucibleMaterial cm where cm.weightSystem = :weightSystem and cm.weightReference = :weightReference" )
@NamedQuery( name = "crucibleMaterial.findActive", query = "select cm from CrucibleMaterial cm where cm.generationState = 400 and cm.consumptionState < 400" )
@NamedQuery( name = "crucibleMaterial.findByCrucibleOrder", query = "select cm from CrucibleMaterial cm where cm.crucibleOrder = :crucibleOrder and cm.parent is null" )
public class CrucibleMaterial extends Material
{
    @Column( name = "crucible_name", length = 20 )
    private String crucibleName;
    @Column( name = "crucible_order", length = 20 )
    private String crucibleOrder;

    @Column( name = "weight_system", length = 20 )
    private String weightSystem;
    @Column( name = "weight_reference" )
    private long weightReference;
    @Column( name = "weight_number" )
    private int weightNumber;
    @Column( name = "weight_equipment" )
    private int weightEquipment;
    @Column( name = "gross_weight" )
    private double grossWeight;

    public String getCrucibleName()
    {
        return crucibleName;
    }

    public void setCrucibleName( String crucibleName )
    {
        this.crucibleName = crucibleName;
    }

    public String getCrucibleOrder()
    {
        return crucibleOrder;
    }

    public void setCrucibleOrder( String crucibleOrder )
    {
        this.crucibleOrder = crucibleOrder;
    }

    public String getWeightSystem()
    {
        return weightSystem;
    }

    public void setWeightSystem( String weightSystem )
    {
        this.weightSystem = weightSystem;
    }

    public long getWeightReference()
    {
        return weightReference;
    }

    public void setWeightReference( long weightReference )
    {
        this.weightReference = weightReference;
    }

    public int getWeightNumber()
    {
        return weightNumber;
    }

    public void setWeightNumber( int weightNumber )
    {
        this.weightNumber = weightNumber;
    }

    public int getWeightEquipment()
    {
        return weightEquipment;
    }

    public void setWeightEquipment( int weightEquipment )
    {
        this.weightEquipment = weightEquipment;
    }

    public double getGrossWeight()
    {
        return grossWeight;
    }

    public void setGrossWeight( double grossWeight )
    {
        this.grossWeight = grossWeight;
    }

    @Override
    public void assignChild( Material child )
    {
        super.assignChild( child );
        if ( !( child instanceof CrucibleMaterial ) )
        {
            return;
        }
        final CrucibleMaterial crucibleMaterial = (CrucibleMaterial) child;
        crucibleMaterial.setCrucibleName( getCrucibleName() );
        crucibleMaterial.setCrucibleOrder( getCrucibleOrder() );
        // weightSystem nur bei Waagenmeldungen richtig
        //crucibleMaterial.setWeightSystem( getWeightSystem() );
        crucibleMaterial.setWeightReference( getWeightReference() );
        crucibleMaterial.setWeightNumber( getWeightNumber() );
        crucibleMaterial.setWeightEquipment( getWeightEquipment() );
        crucibleMaterial.setGrossWeight( getGrossWeight() );
    }

}