package com.hydro.casting.gui.model.undo;

import javafx.beans.property.SimpleObjectProperty;

import java.util.Objects;

public class UndoObjectProperty<O> extends SimpleObjectProperty<O> implements IUndoable
{
    private O savedValue;

    public UndoObjectProperty()
    {
        super();
    }

    public UndoObjectProperty( O initialValue )
    {
        super( initialValue );
    }

    public UndoObjectProperty( Object bean, String name, O initialValue )
    {
        super( bean, name, initialValue );
    }

    public UndoObjectProperty( Object bean, String name )
    {
        super( bean, name );
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
