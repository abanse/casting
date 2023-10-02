package com.hydro.casting.gui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

public class Analysis extends Composition
{
    private static final long serialVersionUID = 1L;

    private String type;

    private final SimpleStringProperty apk;

    public Analysis()
    {
        this.apk = new SimpleStringProperty();
    }

    public final SimpleStringProperty apkProperty()
    {
        return this.apk;
    }

    public final String getApk()
    {
        return this.apkProperty().get();
    }

    public final void setApk( final String apk )
    {
        this.apkProperty().set( apk );
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public Analysis clone()
    {
        Analysis copy = new Analysis();

        copy.setName( getName() );
        copy.setWeight( getWeight() );
        copy.setOriginalWeight( getOriginalWeight() );
        copy.setRefOID( getRefOID() );
        copy.setApk( getApk() );

        ObservableList<CompositionElement> elements = getCompositionElements();

        for ( CompositionElement compositionElement : elements )
        {
            copy.addCompositionElement( compositionElement.clone() );
        }

        return copy;
    }
}
