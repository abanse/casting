package com.hydro.casting.gui.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class BatchInstruction extends NamedModelElement
{
    private static final long serialVersionUID = 1L;

    private final SimpleIntegerProperty amount;
    private final SimpleDoubleProperty width;
    private final SimpleDoubleProperty height;
    private final SimpleDoubleProperty length;
    private final SimpleDoubleProperty minLength;
    private final SimpleDoubleProperty density;
    private final SimpleDoubleProperty lengthSurcharge;

    public BatchInstruction()
    {
        amount = new SimpleIntegerProperty();
        width = new SimpleDoubleProperty();
        height = new SimpleDoubleProperty();
        length = new SimpleDoubleProperty();
        minLength = new SimpleDoubleProperty();
        density = new SimpleDoubleProperty();
        lengthSurcharge = new SimpleDoubleProperty();
    }

    public SimpleIntegerProperty amountProperty()
    {
        return this.amount;
    }

    public int getAmount()
    {
        return this.amountProperty().get();
    }

    public void setAmount( final int amount )
    {
        this.amountProperty().set( amount );
    }

    public SimpleDoubleProperty widthProperty()
    {
        return this.width;
    }

    public double getWidth()
    {
        return this.widthProperty().get();
    }

    public void setWidth( final double width )
    {
        this.widthProperty().set( width );
    }

    public SimpleDoubleProperty heightProperty()
    {
        return this.height;
    }

    public double getHeight()
    {
        return this.heightProperty().get();
    }

    public void setHeight( final double height )
    {
        this.heightProperty().set( height );
    }

    public SimpleDoubleProperty lengthProperty()
    {
        return this.length;
    }

    public double getLength()
    {
        return this.lengthProperty().get();
    }

    public void setLength( final double length )
    {
        this.lengthProperty().set( length );
    }

    public SimpleDoubleProperty minLengthProperty()
    {
        return this.minLength;
    }

    public double getMinLength()
    {
        return this.minLengthProperty().get();
    }

    public void setMinLength( final double minLength )
    {
        this.minLengthProperty().set( minLength );
    }

    public SimpleDoubleProperty densityProperty()
    {
        return this.density;
    }

    public double getDensity()
    {
        return this.densityProperty().get();
    }

    public void setDensity( final double density )
    {
        this.densityProperty().set( density );
    }

    public SimpleDoubleProperty lengthSurchargeProperty()
    {
        return this.lengthSurcharge;
    }

    public double getLengthSurcharge()
    {
        return this.lengthSurchargeProperty().get();
    }

    public void setLengthSurcharge( final double lengthSurcharge )
    {
        this.lengthSurchargeProperty().set( lengthSurcharge );
    }

    @Override
    public void invalidate()
    {
    }
}
