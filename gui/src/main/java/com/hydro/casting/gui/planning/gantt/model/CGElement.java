package com.hydro.casting.gui.planning.gantt.model;

import com.hydro.casting.gui.model.ModelElement;
import com.hydro.casting.gui.planning.gantt.node.AGanttChartNode;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CGElement
{
    private String name;
    private String durationName;
    private String durationSetupAfterName;
    private CGResource resource;
    private CGGroup group;
    private ModelElement element;
    private Duration duration;
    private Duration waitDuration;
    private Duration setupAfter;
    private List<CGDependency> dependencies;
    private List<CGDependency> destDependencies;
    private List<CGElement> innerElements;
    private CGElement innerElementParent;

    private boolean fixedTimeRange = false;
    private LocalDateTime start;
    private LocalDateTime end;

    private AGanttChartNode node;

    private boolean parallel = false;
    private boolean theoretical = false;

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public CGResource getResource()
    {
        return resource;
    }

    public void setResource( CGResource resource )
    {
        if ( this.resource != null )
        {
            this.resource.removeElement( this );
        }
        this.resource = resource;
        if ( this.resource != null )
        {
            this.resource.addElement( this );
        }
    }

    public CGGroup getGroup()
    {
        return group;
    }

    public void setGroup( CGGroup group )
    {
        if ( this.group != null )
        {
            this.group.removeChild( this );
        }
        this.group = group;
        if ( this.group != null )
        {
            this.group.addChild( this );
        }
    }

    public ModelElement getElement()
    {
        return element;
    }

    public void setElement( ModelElement element )
    {
        this.element = element;
    }

    public Duration getDuration()
    {
        return duration;
    }

    public void setDuration( Duration duration )
    {
        this.duration = duration;
    }

    public Duration getWaitDuration()
    {
        return waitDuration;
    }

    public void setWaitDuration( Duration waitDuration )
    {
        this.waitDuration = waitDuration;
    }

    public Duration getSetupAfter()
    {
        return setupAfter;
    }

    public void setSetupAfter( Duration setupAfter )
    {
        this.setupAfter = setupAfter;
    }

    public List<CGDependency> getDependencies()
    {
        if ( dependencies == null )
        {
            dependencies = new ArrayList<>();
        }
        return dependencies;
    }

    public void addDependency( CGDependency dependency )
    {
        dependency.setElement( this );
        getDependencies().add( dependency );
    }

    public void removeDependency( CGDependency dependency )
    {
        dependency.setElement( null );
        getDependencies().remove( dependency );
    }

    public boolean hasDependencies()
    {
        if ( dependencies == null )
        {
            return false;
        }
        return getDependencies().isEmpty() == false;
    }

    public List<CGElement> getInnerElements()
    {
        if ( innerElements == null )
        {
            innerElements = new ArrayList<>();
        }
        return innerElements;
    }

    public CGElement getInnerElementParent()
    {
        return innerElementParent;
    }

    public void setInnerElementParent( CGElement innerElementParent )
    {
        this.innerElementParent = innerElementParent;
    }

    public void addInnerElement( CGElement innerElement )
    {
        if ( innerElement != null )
        {
            innerElement.setInnerElementParent( this );
        }
        getInnerElements().add( innerElement );
    }

    public boolean hasInnerElements()
    {
        if ( innerElements == null )
        {
            return false;
        }
        return getInnerElements().isEmpty() == false;
    }

    public List<CGDependency> getDestDependencies()
    {
        if ( destDependencies == null )
        {
            destDependencies = new ArrayList<>();
        }
        return destDependencies;
    }

    public void addDestDependencies( CGDependency destDependency )
    {
        getDestDependencies().add( destDependency );
    }

    public void removeDestDependencies( CGDependency destDependency )
    {
        if ( destDependencies == null )
        {
            return;
        }
        getDestDependencies().remove( destDependency );
    }

    public boolean hasDestDependencies()
    {
        if ( destDependencies == null )
        {
            return false;
        }
        return getDestDependencies().isEmpty() == false;
    }

    public boolean isFixedTimeRange()
    {
        return fixedTimeRange;
    }

    public void setFixedTimeRange( boolean fixedTimeRange )
    {
        this.fixedTimeRange = fixedTimeRange;
    }

    public LocalDateTime getStart()
    {
        return start;
    }

    public void setStart( LocalDateTime start )
    {
        this.start = start;
    }

    public LocalDateTime getEnd()
    {
        return end;
    }

    public void setEnd( LocalDateTime end )
    {
        this.end = end;
    }

    public Duration getCompleteDuration()
    {
        return Duration.between( getStart(), getEnd() ).plus( getWaitDuration() );
    }

    @Override
    public String toString()
    {
        return "CGElement [name=" + name + ", resource=" + resource + ", start=" + start + ", end=" + end + "]";
    }

    public AGanttChartNode getNode()
    {
        return node;
    }

    public void setNode( AGanttChartNode node )
    {
        this.node = node;
    }

    public boolean isParallel()
    {
        return parallel;
    }

    public void setParallel( boolean parallel )
    {
        this.parallel = parallel;
    }

    public boolean isTheoretical()
    {
        return theoretical;
    }

    public void setTheoretical( boolean theoretical )
    {
        this.theoretical = theoretical;
    }

    public String getDurationName()
    {
        return durationName;
    }

    public void setDurationName( String durationName )
    {
        this.durationName = durationName;
    }

    public String getDurationSetupAfterName()
    {
        return durationSetupAfterName;
    }

    public void setDurationSetupAfterName( String durationSetupAfterName )
    {
        this.durationSetupAfterName = durationSetupAfterName;
    }
}
