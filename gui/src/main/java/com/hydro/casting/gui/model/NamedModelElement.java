package com.hydro.casting.gui.model;

import javafx.beans.property.SimpleStringProperty;

public abstract class NamedModelElement extends ModelElement
{
    private static final long serialVersionUID = 1L;

    private Long refOID;

    private final SimpleStringProperty name;

    public NamedModelElement()
    {
        name = new SimpleStringProperty();
    }

    public String getName()
    {
        return name.get();
    }

    public void setName( String name )
    {
        this.name.set( name );
    }

    public SimpleStringProperty nameProperty()
    {
        return name;
    }

    public Long getRefOID()
    {
        return refOID;
    }

    public void setRefOID( Long refOID )
    {
        this.refOID = refOID;
    }

    @Override
    public String toString()
    {
        return "NamedModelElement [name=" + name.get() + ", objid=" + getObjid() + "]";
    }
}
