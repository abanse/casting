package com.hydro.casting.gui.planning.gantt.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CGResource
{
    private String name;
    private List<CGElement> elements;
    private LocalDateTime startTime;

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public List<CGElement> getElements()
    {
        if ( elements == null )
        {
            elements = new ArrayList<>();
        }
        return elements;
    }

    public void addElement( CGElement element )
    {
        getElements().add( element );
    }

    public void removeElement( CGElement element )
    {
        getElements().remove( element );
    }

    public LocalDateTime getStartTime()
    {
        return startTime;
    }

    public void setStartTime( LocalDateTime startTime )
    {
        this.startTime = startTime;
    }

    public CGElement getLastElement()
    {
        if ( elements == null || elements.isEmpty() )
        {
            return null;
        }
        return getElements().get( getElements().size() - 1 );
    }

    @Override
    public String toString()
    {
        return "CGResource [name=" + name + "]";
    }

}
