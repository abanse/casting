package com.hydro.casting.gui.model.undo;

import javafx.beans.property.SimpleStringProperty;

import java.util.Objects;

public class UndoStringProperty extends SimpleStringProperty implements IUndoable
{
    private String savedValue;

    public UndoStringProperty()
    {
        super();
    }

    public UndoStringProperty( Object bean, String name, String initialValue )
    {
        super( bean, name, initialValue );
    }

    public UndoStringProperty( Object bean, String name )
    {
        super( bean, name );
    }

    public UndoStringProperty( String initialValue )
    {
        super( initialValue );
    }

    @Override
    public void saveState()
    {
        savedValue = get();
    }

    @Override
    public void undo()
    {
        set( savedValue );
    }

    @Override
    public boolean isChanged()
    {
        return Objects.equals( get(), savedValue );
    }
}
