package com.hydro.casting.server.model.sched;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table( name = "process_step" )
public class ProcessStep extends BaseEntity
{
    @ManyToOne
    @JoinColumn( name = "batch_id", nullable = false )
    private Batch batch;
    @Column( name = "name", length = 50, nullable = false )
    private String name;
    @Column( name = "start_ts", nullable = false )
    private LocalDateTime start;
    @Column( name = "end_ts" )
    private LocalDateTime end;

    public Batch getBatch()
    {
        return batch;
    }

    public void setBatch( Batch batch )
    {
        Batch oldBatch = this.batch;
        this.batch = batch;

        if ( oldBatch != null && oldBatch != batch )
        {
            oldBatch.oneSided_removeFromProcessSteps( this );
        }

        if ( batch != null )
        {
            batch.oneSided_addToProcessSteps( this );
        }
    }

    public void oneSided_setBatch( Batch batch )
    {
        this.batch = batch;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
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

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        ProcessStep that = (ProcessStep) o;
        return Objects.equals( getBatch(), that.getBatch() ) && Objects.equals( getName(), that.getName() ) && Objects.equals( getStart(), that.getStart() ) && Objects.equals( getEnd(),
                that.getEnd() );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( getBatch(), getName(), getStart(), getEnd() );
    }

    @Override
    public String toString()
    {
        return "ProcessStep{" + "batch=" + batch + ", name='" + name + '\'' + ", start=" + start + ", end=" + end + '}';
    }
}
