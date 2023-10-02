package com.hydro.casting.gui.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.Map;

public class Composition extends NamedModelElement
{
    private static final long serialVersionUID = 1L;

    private final SimpleDoubleProperty weight;
    private final SimpleDoubleProperty originalWeight;

    private final ObservableList<CompositionElement> compositionElements;

    private final Map<String, CompositionElement> compositionElementIndex;

    public Composition()
    {
        weight = new SimpleDoubleProperty();
        originalWeight = new SimpleDoubleProperty();
        compositionElements = FXCollections.observableArrayList();
        compositionElementIndex = new CaseInsensitiveMap<String, CompositionElement>();
    }

    public double getWeight()
    {
        return weight.get();
    }

    public void setWeight( double weight )
    {
        this.weight.set( weight );
    }

    public SimpleDoubleProperty weightProperty()
    {
        return weight;
    }

    public double getOriginalWeight()
    {
        return originalWeight.get();
    }

    public SimpleDoubleProperty originalWeightProperty()
    {
        return originalWeight;
    }

    public void setOriginalWeight( double originalWeight )
    {
        this.originalWeight.set( originalWeight );
    }

    public double getDisplayWeight()
    {
        if ( getWeight() <= 0 && getOriginalWeight() > 0 )
        {
            return getOriginalWeight();
        }
        return getWeight();
    }

    public void setDisplayWeight(double displayWeight)
    {
        // ignore, only calculated view value
    }

    public ObservableList<CompositionElement> getCompositionElements()
    {
        return compositionElements;
    }

    public void addCompositionElement( CompositionElement compositionElement )
    {
        this.compositionElements.add( compositionElement );
        compositionElementIndex.put( compositionElement.getName(), compositionElement );
    }

    public void removeCompositionElement( CompositionElement compositionElement )
    {
        compositionElementIndex.remove( compositionElement.getName() );
        this.compositionElements.remove( compositionElement );
    }

    public void setCompositionElementValue( String elementName, double elementValue )
    {
        CompositionElement compositionElement = compositionElementIndex.get( elementName );
        if ( compositionElement == null )
        {
            compositionElement = new CompositionElement();
            compositionElement.setName( elementName );
            addCompositionElement( compositionElement );
        }
        compositionElement.setOriginalElementValue( elementValue );
    }

    public void setCompositionElementDecimalPlaces( String elementName, int decimalPlaces )
    {
        CompositionElement compositionElement = compositionElementIndex.get( elementName );
        if ( compositionElement == null )
        {
            compositionElement = new CompositionElement();
            compositionElement.setName( elementName );
            addCompositionElement( compositionElement );
        }
        compositionElement.setDecimalPlaces( decimalPlaces );
    }

    public int getCompositionElementDecimalPlaces( String elementName )
    {
        CompositionElement compositionElement = compositionElementIndex.get( elementName );
        if ( compositionElement == null )
        {
            return 0;
        }
        return compositionElement.getDecimalPlaces();
    }

    public void setCompositionElementType( String elementName, String type )
    {
        CompositionElement compositionElement = compositionElementIndex.get( elementName );
        if ( compositionElement == null )
        {
            compositionElement = new CompositionElement();
            compositionElement.setName( elementName );
            addCompositionElement( compositionElement );
        }
        compositionElement.setType( type );
    }

    public String getCompositionElementType( String elementName )
    {
        CompositionElement compositionElement = compositionElementIndex.get( elementName );
        if ( compositionElement == null )
        {
            return null;
        }
        return compositionElement.getType();
    }

    public double getCompositionElementValue( String elementName )
    {
        CompositionElement compositionElement = compositionElementIndex.get( elementName );
        if ( compositionElement == null )
        {
            return 0;
        }
        return compositionElement.getElementValue();
    }

    public Double getCompositionWarningElementValue( String elementName )
    {
        CompositionElement compositionElement = compositionElementIndex.get( elementName );
        if ( compositionElement == null )
        {
            return null;
        }
        return compositionElement.getWarningElementValue();
    }

    public Double getCompositionOriginalElementValue( String elementName )
    {
        CompositionElement compositionElement = compositionElementIndex.get( elementName );
        if ( compositionElement == null )
        {
            return null;
        }
        return compositionElement.getOriginalElementValue();
    }

    public SimpleDoubleProperty compositionElementValueProperty( String elementName )
    {
        CompositionElement compositionElement = compositionElementIndex.get( elementName );
        if ( compositionElement == null )
        {
            return null;
        }
        return compositionElement.elementValueProperty();
    }

    public CompositionElement getCompositionElement( String elementName )
    {
        return compositionElementIndex.get( elementName );
    }

    public Composition clone()
    {
        Composition copy = new Composition();

        copy.setName( getName() );
        copy.setWeight( getWeight() );
        copy.setOriginalWeight( getOriginalWeight() );

        ObservableList<CompositionElement> elements = getCompositionElements();

        for ( CompositionElement compositionElement : elements )
        {
            copy.addCompositionElement( compositionElement.clone() );
        }

        return copy;
    }

    @Override
    public void invalidate()
    {
    }
}
