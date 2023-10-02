package com.hydro.casting.gui.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class Specification extends NamedModelElement
{
    private static final long serialVersionUID = 1L;

    private final SimpleStringProperty specId;
    private final SimpleStringProperty specIdName;

    private final SimpleBooleanProperty useExtendedValidation;

    private final SimpleObjectProperty<Composition> min;
    private final SimpleObjectProperty<Composition> max;

    public Specification()
    {
        min = new SimpleObjectProperty<Composition>();
        max = new SimpleObjectProperty<Composition>();
        specId = new SimpleStringProperty();
        specIdName = new SimpleStringProperty();
        useExtendedValidation = new SimpleBooleanProperty( true );
        useExtendedValidation.addListener( i -> revalidateSpecification() );
    }

    public String getSpecId()
    {
        return specId.get();
    }

    public void setSpecId( String specId )
    {
        this.specId.set( specId );
    }

    public SimpleStringProperty specIdProperty()
    {
        return specId;
    }

    public String getSpecIdName()
    {
        return specIdName.get();
    }

    public void setSpecIdName( String specIdName )
    {
        this.specIdName.set( specIdName );
    }

    public SimpleStringProperty specIdNameProperty()
    {
        return specIdName;
    }

    public Composition getMin()
    {
        return min.get();
    }

    public void setMin( Composition min )
    {
        this.min.set( min );
    }

    public SimpleObjectProperty<Composition> minProperty()
    {
        return min;
    }

    public Composition getMax()
    {
        return max.get();
    }

    public void setMax( Composition max )
    {
        this.max.set( max );
    }

    public SimpleObjectProperty<Composition> maxProperty()
    {
        return max;
    }

    public final SimpleBooleanProperty useExtendedValidationProperty()
    {
        return this.useExtendedValidation;
    }

    public final boolean isUseExtendedValidation()
    {
        return this.useExtendedValidationProperty().get();
    }

    public final void setUseExtendedValidation( final boolean useExtendedValidation )
    {
        this.useExtendedValidationProperty().set( useExtendedValidation );
    }

    public void revalidateSpecification()
    {
        if ( getMin() != null )
        {
            getMin().getCompositionElements().stream().forEach( ( ce ) -> ce.setUseExtendedValidation( isUseExtendedValidation() ) );
        }
        if ( getMax() != null )
        {
            getMax().getCompositionElements().stream().forEach( ( ce ) -> ce.setUseExtendedValidation( isUseExtendedValidation() ) );
        }
    }

    @Override
    public void invalidate()
    {
    }
}
