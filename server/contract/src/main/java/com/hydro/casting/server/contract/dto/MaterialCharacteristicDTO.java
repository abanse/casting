package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.util.Objects;

public class MaterialCharacteristicDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private double value;
    private String unit;
    private String valueFormat;

    @Override
    public long getId()
    {
        return Objects.hash( name );
    }

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

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( !( o instanceof MaterialCharacteristicDTO ) )
            return false;
        MaterialCharacteristicDTO that = (MaterialCharacteristicDTO) o;
        return Double.compare( that.value, value ) == 0 && Objects.equals( name, that.name ) && Objects.equals( description, that.description ) && Objects.equals( unit, that.unit ) && Objects.equals(
                valueFormat, that.valueFormat );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, description, value, unit, valueFormat );
    }

    @Override
    public String toString()
    {
        return "MaterialCharacteristicsDTO{" + "name='" + name + '\'' + ", description='" + description + '\'' + ", value=" + value + ", unit='" + unit + '\'' + ", valueFormat='" + valueFormat + '\''
                + '}';
    }
}