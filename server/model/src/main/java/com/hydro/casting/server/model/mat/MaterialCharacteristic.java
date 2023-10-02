package com.hydro.casting.server.model.mat;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;

@Entity
@Table( name = "material_characteristic" )
public class MaterialCharacteristic extends BaseEntity
{
    @Column( name = "name", length = 100 )
    private String name;
    @Column( name = "description", length = 300 )
    private String description;
    @Column( name = "value" )
    private double value;
    @Column( name = "unit", length = 10 )
    private String unit;
    @Column( name = "value_format" )
    private String valueFormat;
    @ManyToOne
    @JoinColumn( name = "material_oid" )
    private Material material;

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public double getValue()
    {
        return value;
    }

    public void setValue( double value )
    {
        this.value = value;
    }

    public String getUnit()
    {
        return unit;
    }

    public void setUnit( String unit )
    {
        this.unit = unit;
    }

    public String getValueFormat()
    {
        return valueFormat;
    }

    public void setValueFormat( String valueFormat )
    {
        this.valueFormat = valueFormat;
    }

    public Material getMaterial()
    {
        return material;
    }

    public void setMaterial( Material newMaterial )
    {
        Material oldMaterial = this.material;
        this.material = newMaterial;
        if ( oldMaterial != newMaterial )
        {
            if ( oldMaterial != null )
            {
                oldMaterial.oneSided_removeFromCharacteristics( this );
            }
            if ( newMaterial != null )
            {
                newMaterial.oneSided_addToCharacteristics( this );
            }
        }
    }

    public void oneSided_setMaterial( Material newMaterial )
    {
        this.material = newMaterial;
    }

    public void removeAllAssociations()
    {
        setMaterial( null );
    }
}

