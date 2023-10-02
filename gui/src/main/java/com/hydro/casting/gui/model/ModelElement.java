package com.hydro.casting.gui.model;

import com.hydro.casting.gui.model.id.IDGenerator;

import java.io.Serializable;

public abstract class ModelElement implements Serializable
{
    private static final long serialVersionUID = 1L;

    private long objid;

    public ModelElement()
    {
        objid = IDGenerator.getNext();
    }

    public long getObjid()
    {
        return objid;
    }

    public void setObjid( long objid )
    {
        this.objid = objid;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) ( objid ^ ( objid >>> 32 ) );
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        ModelElement other = (ModelElement) obj;
        return objid == other.objid;
    }

    public abstract void invalidate();

    @Override
    public String toString()
    {
        return "ModelElement [objid=" + objid + "]";
    }
}
