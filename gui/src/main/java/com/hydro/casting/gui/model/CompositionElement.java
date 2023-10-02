package com.hydro.casting.gui.model;

import javafx.beans.property.SimpleDoubleProperty;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class CompositionElement extends NamedModelElement
{
    public final static NumberFormat ELEMENT_FORMATTER = new DecimalFormat( "0.##########" );
    private static final long serialVersionUID = 1L;
    private int decimalPlaces;
    private String type;

    private double originalElementValue = 0.0;
    private double warningElementValue = -1.0;
    private double errorElementValue = -1.0;
    private double statisticElementValue = -1.0;

    private double elementFactor = 1.0;

    private Long sortOrderId;

    private boolean useStatisticElementValue = false;
    private boolean useExtendedValidation = true;

    private final SimpleDoubleProperty elementValue;

    public CompositionElement()
    {
        elementValue = new SimpleDoubleProperty();
    }

    public double getElementValue()
    {
        return elementValue.get();
    }

    public SimpleDoubleProperty elementValueProperty()
    {
        return elementValue;
    }

    public int getDecimalPlaces()
    {
        return decimalPlaces;
    }

    public void setDecimalPlaces( int decimalPlaces )
    {
        this.decimalPlaces = decimalPlaces;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public double getElementFactor()
    {
        return elementFactor;
    }

    public final void setElementFactor( final double elementFactor )
    {
        this.elementFactor = elementFactor;
        revalidate();
    }

    public Long getSortOrderId()
    {
        return sortOrderId;
    }

    public void setSortOrderId( Long sortOrderId )
    {
        this.sortOrderId = sortOrderId;
    }

    public double getOriginalElementValue()
    {
        return originalElementValue;
    }

    public void setOriginalElementValue( double originalElementValue )
    {
        this.originalElementValue = originalElementValue;
        revalidate();
    }

    public double getWarningElementValue()
    {
        return warningElementValue;
    }

    public void setWarningElementValue( double warningElementValue )
    {
        this.warningElementValue = warningElementValue;
        revalidate();
    }

    public double getErrorElementValue()
    {
        return errorElementValue;
    }

    public void setErrorElementValue( double errorElementValue )
    {
        this.errorElementValue = errorElementValue;
        revalidate();
    }

    public double getStatisticElementValue()
    {
        return statisticElementValue;
    }

    public void setStatisticElementValue( double statisticElementValue )
    {
        this.statisticElementValue = statisticElementValue;
        revalidate();
    }

    public void setUseExtendedValidation( final boolean useExtendedValidation )
    {
        this.useExtendedValidation = useExtendedValidation;
        revalidate();
    }

    public void setUseStatisticElementValue( boolean useStatisticElementValue )
    {
        this.useStatisticElementValue = useStatisticElementValue;
        revalidate();
    }

    public void revalidate()
    {
        if ( useExtendedValidation && useStatisticElementValue && statisticElementValue >= 0 )
        {
            elementValueProperty().set( statisticElementValue * elementFactor );
        }
        else if ( useExtendedValidation )
        {
            double elementValue = originalElementValue;
            if ( errorElementValue >= 0 )
            {
                elementValue = errorElementValue;
            }
            if ( warningElementValue >= 0 )
            {
                elementValue = warningElementValue;
            }

            elementValueProperty().set( elementValue * elementFactor );
        }
        else
        {
            elementValueProperty().set( originalElementValue * elementFactor );
        }
    }

    public CompositionElement clone()
    {
        CompositionElement copy = new CompositionElement();

        copy.setName( getName() );

        copy.decimalPlaces = this.decimalPlaces;
        copy.type = this.type;

        copy.originalElementValue = this.originalElementValue;
        copy.warningElementValue = this.warningElementValue;
        copy.errorElementValue = this.errorElementValue;
        copy.statisticElementValue = this.statisticElementValue;

        copy.elementFactor = this.elementFactor;

        copy.useStatisticElementValue = this.useStatisticElementValue;
        copy.useExtendedValidation = this.useExtendedValidation;

        copy.revalidate();

        return copy;
    }

    @Override
    public void invalidate()
    {
    }
}
