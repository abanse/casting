package com.hydro.casting.gui.planning.gantt.solver;

import com.hydro.casting.common.Casting;
import com.hydro.casting.gui.planning.gantt.model.CGDependency;
import com.hydro.casting.gui.planning.gantt.model.CGElement;
import com.hydro.casting.gui.planning.gantt.model.CGResource;
import com.hydro.core.common.util.TimePeriod;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class CastingGanttSolver
{
    private final Map<String, CGResource> resources;

    public CastingGanttSolver( Map<String, CGResource> resources )
    {
        this.resources = resources;
    }

    public void solve()
    {
        final LocalDateTime now = LocalDateTime.now();
        // Setze alle Zeiten auf null
        resources.values().forEach( ( resource ) -> {
            resource.setStartTime( now );
            resource.getElements().forEach( ( element ) -> {
                if ( !element.isFixedTimeRange() )
                {
                    element.setStart( null );
                    element.setEnd( null );
                    element.setWaitDuration( null );
                }
            } );
        } );

        // Berechne alle Caster
        setEPST( resources.get( Casting.MACHINE.CASTER_50 ), false );
        setEPST( resources.get( Casting.MACHINE.CASTER_60 ), false );
        setEPST( resources.get( Casting.MACHINE.CASTER_70 ), false );
        setEPST( resources.get( Casting.MACHINE.CASTER_80 ), false );
        for ( String alloySource : Casting.ALLOY_SOURCES.ALL )
        {
            if ( resources.get( alloySource ) != null )
            {
                setEPST( resources.get( alloySource ), false );
            }
        }

        // Listen sortieren
        resources.values().forEach( ( resource ) -> {
            Collections.sort( resource.getElements(), new Comparator<CGElement>()
            {
                @Override
                public int compare( CGElement o1, CGElement o2 )
                {
                    if ( o1.getStart() == null || o2.getStart() == null )
                    {
                        return 0;
                    }
                    return o1.getStart().compareTo( o2.getStart() );
                }
            } );
        } );

        // Setzen restrictions für resource sequence
        resources.values().forEach( ( resource ) -> {
            setTemporaryResourceRestriction( resource );
        } );

        // Setze alle Zeiten auf null
        resources.values().forEach( ( resource ) -> {
            resource.getElements().forEach( ( element ) -> {
                if ( !element.isFixedTimeRange() )
                {
                    element.setStart( null );
                    element.setEnd( null );
                    element.setWaitDuration( null );
                }
            } );
        } );

        setEPST( resources.get( Casting.MACHINE.CASTER_50 ), true );
        setEPST( resources.get( Casting.MACHINE.CASTER_60 ), true );
        setEPST( resources.get( Casting.MACHINE.CASTER_70 ), true );
        setEPST( resources.get( Casting.MACHINE.CASTER_80 ), true );
        for ( String alloySource : Casting.ALLOY_SOURCES.ALL )
        {
            if ( resources.get( alloySource ) != null )
            {
                setEPST( resources.get( alloySource ), true );
            }
        }

        // Setzen restrictions für resource sequence
        resources.values().forEach( ( resource ) -> {
            removeTemporaryResourceRestriction( resource );
        } );

        // Listen sortieren
        resources.values().forEach( ( resource ) -> {
            Collections.sort( resource.getElements(), new Comparator<CGElement>()
            {
                @Override
                public int compare( CGElement o1, CGElement o2 )
                {
                    return o1.getStart().compareTo( o2.getStart() );
                }
            } );
        } );

        // waiting duration setzen
        resources.values().forEach( resource -> setWaitDuration( resource ) );
    }

    private void setTemporaryResourceRestriction( CGResource resource )
    {
        CGElement lastElement = null;
        for ( CGElement element : resource.getElements() )
        {
            if ( element.isParallel() )
            {
                continue;
            }
            if ( lastElement != null )
            {
                CGDependency resD = new CGDependency();
                resD.setOtherSide( lastElement );
                resD.setDependencyType( CGDependency.DependencyType.START_AFTER );
                resD.setTemp( true );
                element.addDependency( resD );
            }
            lastElement = element;
        }
    }

    private void removeTemporaryResourceRestriction( CGResource resource )
    {
        for ( CGElement element : resource.getElements() )
        {
            List<CGDependency> dependencies = new ArrayList<>( element.getDependencies() );
            for ( CGDependency dependency : dependencies )
            {
                if ( dependency.isTemp() )
                {
                    element.removeDependency( dependency );
                    dependency.setOtherSide( null );
                }
            }
        }
    }

    private boolean setEPST( CGResource caster, boolean withResourceRestrictions )
    {
        if ( caster == null || caster.getElements() == null )
        {
            return false;
        }
        boolean conflicts = false;
        for ( CGElement element : caster.getElements() )
        {
            if ( element.isFixedTimeRange() )
            {
                continue;
            }
            boolean innerConflicts = setTimes( element, withResourceRestrictions );
            if ( innerConflicts )
            {
                conflicts = true;
            }
        }
        return conflicts;
    }

    private boolean setTimes( CGElement element, boolean withResourceRestrictions )
    {
        boolean conflicts = false;
        LocalDateTime latestPossibleStartTime = null;
        if ( element.hasDependencies() )
        {
            for ( CGDependency dependency : element.getDependencies() )
            {
                final CGElement otherSide = dependency.getOtherSide();
                if ( otherSide.getStart() == null )
                {
                    boolean innerConflicts = setTimes( otherSide, withResourceRestrictions );
                    if ( innerConflicts )
                    {
                        conflicts = true;
                    }
                }
                if ( dependency.getDependencyType() == CGDependency.DependencyType.START_IMMEDIATE_AFTER )
                {
                    final LocalDateTime otherSideEnd = otherSide.getStart().plus( otherSide.getDuration() );
                    if ( latestPossibleStartTime == null || otherSideEnd.isAfter( latestPossibleStartTime ) )
                    {
                        latestPossibleStartTime = otherSideEnd;
                    }
                }
                if ( dependency.getDependencyType() == CGDependency.DependencyType.FINISHED_IMMEDIATE_BEFORE )
                {
                    final LocalDateTime otherSideEnd = otherSide.getStart().plus( otherSide.getDuration() ).minus( element.getDuration() );
                    if ( latestPossibleStartTime == null || otherSideEnd.isAfter( latestPossibleStartTime ) )
                    {
                        latestPossibleStartTime = otherSideEnd;
                    }
                }
                if ( dependency.getDependencyType() == CGDependency.DependencyType.FINISHED_BEFORE )
                {
                    final LocalDateTime otherSideEnd = otherSide.getStart().plus( otherSide.getDuration() ).minus( element.getDuration() ).minus( element.getSetupAfter() );
                    if ( latestPossibleStartTime == null || otherSideEnd.isAfter( latestPossibleStartTime ) )
                    {
                        latestPossibleStartTime = otherSideEnd;
                    }
                }
                if ( dependency.getDependencyType() == CGDependency.DependencyType.START_AFTER )
                {
                    final LocalDateTime otherSideEnd = otherSide.getStart().plus( otherSide.getDuration() ).plus( otherSide.getSetupAfter() );
                    if ( latestPossibleStartTime == null || otherSideEnd.isAfter( latestPossibleStartTime ) )
                    {
                        latestPossibleStartTime = otherSideEnd;
                    }
                }
            }
        }
        if ( latestPossibleStartTime == null )
        {
            latestPossibleStartTime = element.getResource().getStartTime();
        }

        // Auf jeden Fall immer die fixedElements prüfen
        final CGResource resource = element.getResource();
        if ( resource != null )
        {
            List<CGElement> elements = resource.getElements();
            CGElement conflictedElement = null;
            LocalDateTime currentEnd = latestPossibleStartTime.plus( element.getDuration() ).plus( element.getSetupAfter() );
            TimePeriod currentPeriod = TimePeriod.between( latestPossibleStartTime, currentEnd );
            for ( CGElement resElement : elements )
            {
                if ( resElement == element )
                {
                    continue;
                }
                if ( !resElement.isFixedTimeRange() )
                {
                    continue;
                }
                TimePeriod elementPeriod = TimePeriod.between( resElement.getStart(), resElement.getEnd() );
                if ( currentPeriod.intersect( elementPeriod ) )
                {
                    conflictedElement = resElement;
                    break;
                }
            }
            if ( conflictedElement != null )
            {
                latestPossibleStartTime = conflictedElement.getStart().plus( conflictedElement.getDuration() ).plus( conflictedElement.getSetupAfter() );
            }
        }

        // check resource
        if ( withResourceRestrictions )
        {
            //final CGResource resource = element.getResource();
            if ( resource != null )
            {
                List<CGElement> elements = resource.getElements();
                CGElement conflictedElement = null;
                LocalDateTime currentEnd = latestPossibleStartTime.plus( element.getDuration() ).plus( element.getSetupAfter() );
                TimePeriod currentPeriod = TimePeriod.between( latestPossibleStartTime, currentEnd );
                for ( CGElement resElement : elements )
                {
                    if ( resElement == element )
                    {
                        continue;
                    }
                    if ( resElement.getStart() == null )
                    {
                        continue;
                    }
                    if ( resElement.isParallel() )
                    {
                        continue;
                    }
                    TimePeriod elementPeriod = TimePeriod.between( resElement.getStart(), resElement.getEnd() );
                    if ( currentPeriod.intersect( elementPeriod ) )
                    {
                        conflictedElement = resElement;
                        conflicts = true;
                        break;
                    }
                }
                if ( conflictedElement != null )
                {
                    latestPossibleStartTime = conflictedElement.getStart().plus( conflictedElement.getDuration() ).plus( conflictedElement.getSetupAfter() );
                }
            }
        }

        element.setStart( latestPossibleStartTime );
        element.setEnd( latestPossibleStartTime.plus( element.getDuration() ).plus( element.getSetupAfter() ) );
        element.setWaitDuration( Duration.ZERO );

        return conflicts;
    }

    private void setWaitDuration( CGResource timeLine )
    {
        List<CGElement> elements = timeLine.getElements();
        for ( CGElement element : elements )
        {
            if ( element.isFixedTimeRange() )
            {
                continue;
            }
            if ( element.hasDestDependencies() == false )
            {
                continue;
            }
            List<CGDependency> destDependencies = element.getDestDependencies();
            LocalDateTime minStart = null;
            for ( CGDependency destDependency : destDependencies )
            {
                if ( minStart == null || destDependency.getElement().getStart().isBefore( minStart ) )
                {
                    minStart = destDependency.getElement().getStart();
                }
            }
            if ( minStart.isAfter( element.getEnd() ) )
            {
                element.setWaitDuration( Duration.between( element.getEnd(), minStart ) );
            }
        }
    }

    public LocalDateTime getMaxTime()
    {
        final List<CGElement> lastElements = new ArrayList<>();
        if ( resources.get( Casting.MACHINE.CASTER_50 ) != null )
        {
            lastElements.add( resources.get( Casting.MACHINE.CASTER_50 ).getLastElement() );
        }
        if ( resources.get( Casting.MACHINE.CASTER_60 ) != null )
        {
            lastElements.add( resources.get( Casting.MACHINE.CASTER_60 ).getLastElement() );
        }
        if ( resources.get( Casting.MACHINE.CASTER_70 ) != null )
        {
            lastElements.add( resources.get( Casting.MACHINE.CASTER_70 ).getLastElement() );
        }
        if ( resources.get( Casting.MACHINE.CASTER_80 ) != null )
        {
            lastElements.add( resources.get( Casting.MACHINE.CASTER_80 ).getLastElement() );
        }

        LocalDateTime max = null;
        for ( CGElement lastElement : lastElements )
        {
            if ( lastElement == null )
            {
                continue;
            }
            if ( lastElement.isFixedTimeRange() )
            {
                continue;
            }
            if ( max == null || lastElement.getEnd().isAfter( max ) )
            {
                max = lastElement.getEnd();
            }
        }
        return max;
    }
}
