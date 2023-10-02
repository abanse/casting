package com.hydro.casting.server.contract.dto;

import java.io.Serializable;
import java.util.Objects;

public class MaterialAnalysisElementDTO implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String name;
    private double value;

    public MaterialAnalysisElementDTO()
    {
    }

    public MaterialAnalysisElementDTO( String name, double value )
    {
        this.name = name;
        this.value = value;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public double getValue()
    {
        return value;
    }

    public void setValue( double value )
    {
        this.value = value;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        MaterialAnalysisElementDTO that = (MaterialAnalysisElementDTO) o;
        return Double.compare( that.value, value ) == 0 && Objects.equals( name, that.name );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, value );
    }

    @Override
    public String toString()
    {
        return "MaterialAnalysisElementDTO{" + "name='" + name + '\'' + ", value=" + value + '}';
    }

    public MaterialAnalysisElementDTO clone()
    {
        final MaterialAnalysisElementDTO clone = new MaterialAnalysisElementDTO();
        clone.setName( getName() );
        clone.setValue( getValue() );

        return clone;
    }
}