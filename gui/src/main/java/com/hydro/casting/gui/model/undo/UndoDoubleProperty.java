package com.hydro.casting.gui.model.undo;

import javafx.beans.property.SimpleDoubleProperty;

public class UndoDoubleProperty extends SimpleDoubleProperty implements IUndoable
{
    private double savedValue;

    public UndoDoubleProperty()
    {
        super();
    }

    public UndoDoubleProperty( double initialValue )
    {
        super( initialValue );
    }

    public UndoDoubleProperty( Object bean, String name, double initialValue )
    {
        super( bean, name, initialValue );
    }

    public UndoDoubleProperty( Object bean, String name )
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
