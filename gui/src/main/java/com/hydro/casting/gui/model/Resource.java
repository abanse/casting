package com.hydro.casting.gui.model;

import com.hydro.casting.gui.model.undo.IUndoable;
import com.hydro.casting.gui.model.undo.UndoObservableList;
import javafx.collections.ObservableList;
public abstract class Resource extends NamedModelElement implements IUndoable
{
    public final static String DND_IDENT = "Resource:";
    private static final long serialVersionUID = 1L;

    private final UndoObservableList<ResourceCalendarEntry> resourceCalendarEntries;

    private final IUndoable[] undoables;

    public Resource()
    {
        resourceCalendarEntries = new UndoObservableList<>( "resourceCalendarEntries" );

        undoables = new IUndoable[] { resourceCalendarEntries };
    }

    @Override
    public void saveState()
    {
        for ( IUndoable undoable : undoables )
        {
            undoable.saveState();
        }
    }

    @Override
    public void undo()
    {
        for ( IUndoable undoable : undoables )
        {
            undoable.undo();
        }
    }

    @Override
    public boolean isChanged()
    {
        for ( IUndoable undoable : undoables )
        {
            if ( undoable.isChanged() )
            {
                return true;
            }
        }
        return false;
    }

    public ObservableList<ResourceCalendarEntry> getResourceCalendarEntries()
    {
        return resourceCalendarEntries;
    }

    public void addResourceCalendarEntry( ResourceCalendarEntry resourceCalendarEntry )
    {
        resourceCalendarEntry.setResource( this );
        resourceCalendarEntries.add( resourceCalendarEntry );
    }

    public void removeResourceCalendarEntry( ResourceCalendarEntry resourceCalendarEntry )
    {
        resourceCalendarEntry.setResource( null );
        resourceCalendarEntries.remove( resourceCalendarEntry );
    }

    @Override
    public void invalidate()
    {
        for ( ResourceCalendarEntry resourceCalendarEntry : resourceCalendarEntries )
        {
            resourceCalendarEntry.invalidate();
        }
    }

    @Override
    public String toString()
    {
        return "Resource [name=" + getName() + ", objid=" + getObjid() + "]";
    }
}
