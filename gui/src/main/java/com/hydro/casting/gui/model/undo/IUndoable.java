package com.hydro.casting.gui.model.undo;

public interface IUndoable
{
    void saveState();

    void undo();

    boolean isChanged();
}
