package com.hydro.casting.gui.model.undo;

import javafx.beans.property.SimpleBooleanProperty;

public class UndoBooleanProperty extends SimpleBooleanProperty implements IUndoable
{
    private boolean savedValue;

    public UndoBooleanProperty()
    {
        super();
    }

    public UndoBooleanProperty( boolean initialValue )
    {
        super( initialValue );
    }

    public UndoBooleanProperty( Object bean, String name, boolean initialValue )
    {
        super( bean, name, initialValue );
    }

    public UndoBooleanProperty( Object bean, String name )
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
        return get() != savedValue;
    }
}
