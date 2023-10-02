package com.hydro.casting.gui.model.undo;

import javafx.collections.ModifiableObservableListBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UndoObservableList<E> extends ModifiableObservableListBase<E> implements IUndoable
{
    private final static Logger LOG = LoggerFactory.getLogger( UndoObservableList.class );

    private final List<E> delegate = Collections.synchronizedList( new ArrayList<>() );
    private final List<E> savedList = Collections.synchronizedList( new ArrayList<>() );

    private final String attributeName;

    public UndoObservableList( String attributeName )
    {
        this.attributeName = attributeName;
    }

    public E get( int index )
    {
        return delegate.get( index );
    }

    public int size()
    {
        return delegate.size();
    }

    protected void doAdd( int index, E element )
    {
        delegate.add( index, element );
    }

    protected E doSet( int index, E element )
    {
        return delegate.set( index, element );
    }

    protected E doRemove( int index )
    {
        return delegate.remove( index );
    }

    @Override
    public void saveState()
    {
        savedList.clear();
        savedList.addAll( delegate );

        for ( E e : this )
        {
            if ( e instanceof IUndoable )
            {
                ( (IUndoable) e ).saveState();
            }
        }
    }

    @Override
    public void undo()
    {
        if ( delegate.equals( savedList ) == false )
        {
            if ( LOG.isTraceEnabled() )
            {
                LOG.trace( "undo list value " + attributeName );
            }

            clear();
            addAll( savedList );
        }

        for ( E e : this )
        {
            if ( e instanceof IUndoable )
            {
                ( (IUndoable) e ).undo();
            }
        }
    }

    @Override
    public boolean isChanged()
    {
        if ( delegate.equals( savedList ) == false )
        {
            return true;
        }

        for ( E e : this )
        {
            if ( e instanceof IUndoable )
            {
                if ( ( (IUndoable) e ).isChanged() )
                {
                    return true;
                }
            }
        }

        return false;
    }
}