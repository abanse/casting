package com.hydro.casting.server.model.sched;

import com.hydro.casting.server.model.res.Machine;
import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table( name = "schedulable" )
@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
@DiscriminatorColumn( name = "discriminator" )
@NamedQuery( name = "schedulable.lastExecutingSequenceIndex", query = "select max(s.executingSequenceIndex) from Schedulable s where s.executingMachine = :executingMachine" )
@NamedQuery( name = "schedulable.nextAvailableBatch", query = "select s from Schedulable s where s.executingMachine = :executingMachine and s.executingSequenceIndex = (select min(s.executingSequenceIndex) from Schedulable s where s.executingMachine = :executingMachine and ( s.executionState = 250 or s.executionState = 350 ) )" )
@NamedQuery( name = "schedulable.equalsOrGreaterExecutingSequenceIndex", query = "select s from Schedulable s where s.executingMachine = :executingMachine and s.executingSequenceIndex >= :executingSequenceIndex order by s.executingSequenceIndex" )
public abstract class Schedulable extends BaseEntity
{
    @Column( name = "name", length = 50 )
    private String name;
    @Column( name = "execution_state" )
    private int executionState = 100;
    @Column( name = "info", length = 200 )
    private String info;
    @Column( name = "annotation" )
    private String annotation;
    @Column( name = "executing_sequence_index" )
    private long executingSequenceIndex;
    @Column( name = "planned_ts" )
    private LocalDateTime plannedTS;
    @Column( name = "in_progress_ts" )
    private LocalDateTime inProgressTS;
    @Column( name = "success_ts" )
    private LocalDateTime successTS;
    @Column( name = "failed_ts" )
    private LocalDateTime failedTS;
    @Column( name = "canceled_ts" )
    private LocalDateTime canceledTS;
    @ManyToOne
    @JoinColumn( name = "batch_oid" )
    private Batch batch;
    @ManyToOne
    @JoinColumn( name = "executing_machine_oid" )
    private Machine executingMachine;
    @ManyToOne
    @JoinColumn( name = "demand_oid" )
    private Demand demand;

    public String getName()
    {
        return name;
    }

    public void setName( String newName )
    {
        this.name = newName;
    }

    public int getExecutionState()
    {
        return executionState;
    }

    public void setExecutionState( int newExecutionState )
    {
        this.executionState = newExecutionState;
    }

    public String getInfo()
    {
        return info;
    }

    public void setInfo( String newInfo )
    {
        this.info = newInfo;
    }

    public String getAnnotation()
    {
        return annotation;
    }

    public void setAnnotation( String newAnnotation )
    {
        this.annotation = newAnnotation;
    }

    public long getExecutingSequenceIndex()
    {
        return executingSequenceIndex;
    }

    public void setExecutingSequenceIndex( long newExecutingSequenceIndex )
    {
        this.executingSequenceIndex = newExecutingSequenceIndex;
    }

    public LocalDateTime getPlannedTS()
    {
        return plannedTS;
    }

    public void setPlannedTS( LocalDateTime newPlannedTS )
    {
        this.plannedTS = newPlannedTS;
    }

    public LocalDateTime getInProgressTS()
    {
        return inProgressTS;
    }

    public void setInProgressTS( LocalDateTime newInProgressTS )
    {
        this.inProgressTS = newInProgressTS;
    }

    public LocalDateTime getSuccessTS()
    {
        return successTS;
    }

    public void setSuccessTS( LocalDateTime newSuccessTS )
    {
        this.successTS = newSuccessTS;
    }

    public LocalDateTime getFailedTS()
    {
        return failedTS;
    }

    public void setFailedTS( LocalDateTime newFailedTS )
    {
        this.failedTS = newFailedTS;
    }

    public LocalDateTime getCanceledTS()
    {
        return canceledTS;
    }

    public void setCanceledTS( LocalDateTime newCanceledTS )
    {
        this.canceledTS = newCanceledTS;
    }

    public Batch getBatch()
    {
        return batch;
    }

    public void setBatch( Batch newBatch )
    {
        Batch oldBatch = this.batch;
        this.batch = newBatch;
        if ( oldBatch != newBatch )
        {
            if ( oldBatch != null )
            {
                oldBatch.oneSided_removeFromMembers( this );
            }
            if ( newBatch != null )
            {
                newBatch.oneSided_addToMembers( this );
            }
        }
    }

    public void oneSided_setBatch( Batch newBatch )
    {
        this.batch = newBatch;
    }

    public Machine getExecutingMachine()
    {
        return executingMachine;
    }

    public void setExecutingMachine( Machine newExecutingMachine )
    {
        this.executingMachine = newExecutingMachine;
    }

    public Demand getDemand()
    {
        return demand;
    }

    public void setDemand( Demand newDemand )
    {
        Demand oldDemand = this.demand;
        this.demand = newDemand;
        if ( oldDemand != newDemand )
        {
            if ( oldDemand != null )
            {
                oldDemand.oneSided_removeFromSchedulables( this );
            }
            if ( newDemand != null )
            {
                newDemand.oneSided_addToSchedulables( this );
            }
        }
    }

    public void oneSided_setDemand( Demand newDemand )
    {
        this.demand = newDemand;
    }

    public void removeAllAssociations()
    {
        setBatch( null );
        setDemand( null );
        setExecutingMachine( null );
    }
}