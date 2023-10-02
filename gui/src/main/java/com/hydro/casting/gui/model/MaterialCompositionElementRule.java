package com.hydro.casting.gui.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class MaterialCompositionElementRule extends NamedModelElement
{
    private static final long serialVersionUID = 1L;

    private final SimpleStringProperty type;
    private final SimpleStringProperty furnaceType;
    private final SimpleStringProperty materialRefOID;
    private final SimpleStringProperty alloy;
    private final SimpleStringProperty compositionElementName;
    private final SimpleDoubleProperty factor;

    public MaterialCompositionElementRule()
    {
        type = new SimpleStringProperty();
        furnaceType = new SimpleStringProperty();
        materialRefOID = new SimpleStringProperty();
        alloy = new SimpleStringProperty();
        compositionElementName = new SimpleStringProperty();
        factor = new SimpleDoubleProperty();
    }

    public final SimpleStringProperty typeProperty()
    {
        return this.type;
    }

    public final String getType()
    {
        return this.typeProperty().get();
    }

    public final void setType( final String type )
    {
        this.typeProperty().set( type );
    }

    public final SimpleStringProperty furnaceTypeProperty()
    {
        return this.furnaceType;
    }

    public final String getFurnaceType()
    {
        return this.furnaceTypeProperty().get();
    }

    public final void setFurnaceType( final String furnaceType )
    {
        this.furnaceTypeProperty().set( furnaceType );
    }

    public final SimpleStringProperty materialRefOIDProperty()
    {
        return this.materialRefOID;
    }

    public final String getMaterialRefOID()
    {
        return this.materialRefOIDProperty().get();
    }

    public final void setMaterialRefOID( final String materialRefOID )
    {
        this.materialRefOIDProperty().set( materialRefOID );
    }

    public final SimpleStringProperty alloyProperty()
    {
        return this.alloy;
    }

    public final String getAlloy()
    {
        return this.alloyProperty().get();
    }

    public final void setAlloy( final String alloy )
    {
        this.alloyProperty().set( alloy );
    }

    public final SimpleStringProperty compositionElementNameProperty()
    {
        return this.compositionElementName;
    }

    public final String getCompositionElementName()
    {
        return this.compositionElementNameProperty().get();
    }

    public final void setCompositionElementName( final String compositionElementName )
    {
        this.compositionElementNameProperty().set( compositionElementName );
    }

    public final SimpleDoubleProperty factorProperty()
    {
        return this.factor;
    }

    public final double getFactor()
    {
        return this.factorProperty().get();
    }

    public final void setFactor( final double factor )
    {
        this.factorProperty().set( factor );
    }

    @Override
    public void invalidate()
    {
    }
}
