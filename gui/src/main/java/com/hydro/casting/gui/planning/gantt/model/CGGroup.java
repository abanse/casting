package com.hydro.casting.gui.planning.gantt.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CGGroup extends CGElement
{
    private List<CGElement> childs;

    public List<CGElement> getChilds()
    {
        if ( childs == null )
        {
            childs = new ArrayList<>();
        }
        return childs;
    }

    public void addChild( CGElement child )
    {
        getChilds().add( child );
    }

    public void removeChild( CGElement child )
    {
        getChilds().remove( child );
    }

    @Override
    public Duration getDuration()
    {
        return Duration.ZERO;
    }

    @Override
    public Duration getWaitDuration()
    {
        if ( childs == null || childs.size() < 2 )
        {
            return Duration.ZERO;
        }
        CGElement last = childs.get( childs.size() - 1 );
        return last.getWaitDuration();
    }

    @Override
    public Duration getSetupAfter()
    {
        return Duration.ZERO;
    }

    @Override
    public LocalDateTime getStart()
    {
        LocalDateTime min = null;
        for ( CGElement child : childs )
        {
            if ( min == null || child.getStart().isBefore( min ) )
            {
                min = child.getStart();
            }
        }
        return min;
    }

    @Override
    public LocalDateTime getEnd()
    {
        LocalDateTime max = null;
        for ( CGElement child : childs )
        {
            if ( max == null || child.getEnd().isAfter( max ) )
            {
                max = child.getEnd();
            }
        }
        return max;
    }
}
