package com.hydro.casting.server.model.mat;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;

@Entity
@Table( name = "alloy_element" )
public class AlloyElement extends BaseEntity
{
    @Column( name = "name", length = 5 )
    private String name;
    @Column( name = "element_type", length = 10 )
    private String elementType;
    @Column( name = "element_index" )
    private Long elementIndex;
    @Column( name = "min_value" )
    private Double minValue;
    @Column( name = "max_value" )
    private Double maxValue;
    @Column( name = "target_value" )
    private Double targetValue;
    @Column( name = "precision" )
    private Integer precision;
    @Column( name = "element_unit", length = 5 )
    private String elementUnit;
    @Column( name = "presentation", length = 10 )
    private String presentation;
    @ManyToOne
    @JoinColumn( name = "alloy_oid" )
    private Alloy alloy;

    public String getName()
    {
        return name;
    }

    public void setName( String newName )
    {
        this.name = newName;
    }

    public String getElementType()
    {
        return elementType;
    }

    public void setElementType( String newElementType )
    {
        this.elementType = newElementType;
    }

    public Long getElementIndex()
    {
        return elementIndex;
    }

    public void setElementIndex( Long newElementindex )
    {
        this.elementIndex = newElementindex;
    }

    public Double getMinValue()
    {
        return minValue;
    }

    public void setMinValue( Double newMinValue )
    {
        this.minValue = newMinValue;
    }

    public Double getMaxValue()
    {
        return maxValue;
    }

    public void setMaxValue( Double newMaxValue )
    {
        this.maxValue = newMaxValue;
    }

    public Double getTargetValue()
    {
        return targetValue;
    }

    public void setTargetValue( Double newTargetValue )
    {
        this.targetValue = newTargetValue;
    }

    public Integer getPrecision()
    {
        return precision;
    }

    public void setPrecision( Integer precision )
    {
        this.precision = precision;
    }

    public String getElementUnit()
    {
        return elementUnit;
    }

    public void setElementUnit( String newElementunit )
    {
        this.elementUnit = newElementunit;
    }

    public String getPresentation()
    {
        return presentation;
    }

    public void setPresentation( String newPresentation )
    {
        this.presentation = newPresentation;
    }

    public Alloy getAlloy()
    {
        return alloy;
    }

    public void setAlloy( Alloy newAlloy )
    {
        Alloy oldAlloy = this.alloy;
        this.alloy = newAlloy;
        if ( oldAlloy != newAlloy )
        {
            if ( oldAlloy != null )
            {
                oldAlloy.oneSided_removeFromAlloyElements( this );
            }
            if ( newAlloy != null )
            {
                newAlloy.oneSided_addToAlloyElements( this );
            }
        }
    }

    public void oneSided_setAlloy( Alloy newAlloy )
    {
        this.alloy = newAlloy;
    }

    public void removeAllAssociations()
    {
        setAlloy( null );
    }

    @Override
    public String toString()
    {
        return "AlloyElement{" + "name='" + name + '\'' + ", minValue=" + minValue + ", maxValue=" + maxValue + '}';
    }
}

