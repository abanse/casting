package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.util.Objects;

public class CompositionElementDTO implements ViewDTO
{
    private static final long serialVersionUID = 1;

    private String name;
    private Double value;
    private Double warningValue;
    private Integer precision;
    private Double factor;
    private Long sortOrderId;

    @Override
    public long getId()
    {
        return this.hashCode();
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public Double getValue()
    {
        return value;
    }

    public void setValue( Double value )
    {
        this.value = value;
    }

    public Double getWarningValue()
    {
        return warningValue;
    }

    public void setWarningValue( Double warningValue )
    {
        this.warningValue = warningValue;
    }

    public Integer getPrecision()
    {
        return precision;
    }

    public void setPrecision( Integer precision )
    {
        this.precision = precision;
    }

    public Double getFactor()
    {
        return factor;
    }

    public void setFactor( Double factor )
    {
        this.factor = factor;
    }

    public Long getSortOrderId()
    {
        return sortOrderId;
    }

    public void setSortOrderId( Long sortOrderId )
    {
        this.sortOrderId = sortOrderId;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        CompositionElementDTO that = (CompositionElementDTO) o;
        return Objects.equals( name, that.name ) && Objects.equals( value, that.value ) && Objects.equals( warningValue, that.warningValue ) && Objects.equals( precision, that.precision )
                && Objects.equals( factor, that.factor ) && Objects.equals( sortOrderId, that.sortOrderId );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, value, warningValue, precision, factor, sortOrderId );
    }

    @Override
    public String toString()
    {
        return "CompositionElementDTO{" + "name='" + name + '\'' + ", value=" + value + ", warningValue=" + warningValue + ", precision=" + precision + ", factor=" + factor + ", sortOrderId="
                + sortOrderId + '}';
    }

    @Override
    public CompositionElementDTO clone()
    {
        final CompositionElementDTO clone = new CompositionElementDTO();
        clone.setName( getName() );
        clone.setValue( getValue() );
        clone.setWarningValue( getWarningValue() );
        clone.setPrecision( getPrecision() );
        clone.setFactor( getFactor() );
        clone.setSortOrderId( getSortOrderId() );

        return clone;
    }
}
