package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.util.Objects;

public class SpecificationElementDTO implements ViewDTO, Cloneable
{
    private static final long serialVersionUID = 1L;
    private String name;
    private String unit;

    private Double minValue;
    private Double castingMinValue;
    private Double castingMaxValue;
    private Double maxValue;

    private double elementFactor = 1.0;
    private int precision;

    @Override
    public long getId()
    {
        if ( name == null )
        {
            return 0;
        }
        return name.hashCode();
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getUnit()
    {
        return unit;
    }

    public void setUnit( String unit )
    {
        this.unit = unit;
    }

    public Double getMinValue()
    {
        return minValue;
    }

    public void setMinValue( Double minValue )
    {
        this.minValue = minValue;
    }

    public Double getCastingMinValue()
    {
        return castingMinValue;
    }

    public void setCastingMinValue( Double castingMinValue )
    {
        this.castingMinValue = castingMinValue;
    }

    public Double getCastingMaxValue()
    {
        return castingMaxValue;
    }

    public void setCastingMaxValue( Double castingMaxValue )
    {
        this.castingMaxValue = castingMaxValue;
    }

    public Double getMaxValue()
    {
        return maxValue;
    }

    public void setMaxValue( Double maxValue )
    {
        this.maxValue = maxValue;
    }

    public double getElementFactor()
    {
        return elementFactor;
    }

    public void setElementFactor( double elementFactor )
    {
        this.elementFactor = elementFactor;
    }

    public int getPrecision()
    {
        return precision;
    }

    public void setPrecision( int precision )
    {
        this.precision = precision;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        SpecificationElementDTO that = (SpecificationElementDTO) o;
        return Double.compare( that.elementFactor, elementFactor ) == 0 && precision == that.precision && Objects.equals( name, that.name ) && Objects.equals( unit, that.unit ) && Objects.equals(
                minValue, that.minValue ) && Objects.equals( castingMinValue, that.castingMinValue ) && Objects.equals( castingMaxValue, that.castingMaxValue ) && Objects.equals( maxValue,
                that.maxValue );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, unit, minValue, castingMinValue, castingMaxValue, maxValue, elementFactor, precision );
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        final SpecificationElementDTO clone = new SpecificationElementDTO();
        clone.setName(name);
        clone.setUnit(unit);
        clone.setMinValue(minValue);
        clone.setCastingMinValue(castingMinValue);
        clone.setCastingMaxValue(castingMaxValue);
        clone.setMaxValue(maxValue);
        clone.setElementFactor(elementFactor);
        clone.setPrecision(precision);

        return clone;
    }

    @Override
    public String toString()
    {
        return "SpecificationElementDTO{" + "name='" + name + '\'' + ", unit='" + unit + '\'' + ", minValue=" + minValue + ", castingMinValue=" + castingMinValue + ", castingMaxValue="
                + castingMaxValue + ", maxValue=" + maxValue + ", elementFactor=" + elementFactor + ", precision=" + precision + '}';
    }
}
