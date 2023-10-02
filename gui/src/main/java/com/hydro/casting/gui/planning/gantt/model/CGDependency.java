package com.hydro.casting.gui.planning.gantt.model;

public class CGDependency
{
    private CGElement element;

    private CGElement otherSide;
    private DependencyType dependencyType;
    private boolean temp = false;

    public CGElement getElement()
    {
        return element;
    }

    public void setElement( CGElement element )
    {
        this.element = element;
    }

    public CGElement getOtherSide()
    {
        return otherSide;
    }

    public void setOtherSide( CGElement otherSide )
    {
        if ( this.otherSide != null )
        {
            this.otherSide.removeDestDependencies( this );
        }
        this.otherSide = otherSide;
        if ( this.otherSide != null )
        {
            this.otherSide.addDestDependencies( this );
        }
    }

    public DependencyType getDependencyType()
    {
        return dependencyType;
    }

    public void setDependencyType( DependencyType dependencyType )
    {
        this.dependencyType = dependencyType;
    }

    public boolean isTemp()
    {
        return temp;
    }

    public void setTemp( boolean temp )
    {
        this.temp = temp;
    }

    public CGDependency copy()
    {
        CGDependency copy = new CGDependency();
        copy.setOtherSide( otherSide );
        copy.setDependencyType( dependencyType );
        return copy;
    }

    public enum DependencyType
    {
        START_AFTER, START_IMMEDIATE_AFTER, FINISHED_BEFORE, FINISHED_IMMEDIATE_BEFORE
    }

}
