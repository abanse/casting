package com.hydro.casting.server.ejb.main.service;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.model.sched.Batch;
import com.hydro.casting.server.model.sched.CastingBatch;
import com.hydro.casting.server.model.sched.Schedulable;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class SchedulableService
{
    @EJB
    ProcessStepService processStepService;

    public void setExecutionState( Schedulable schedulable, int executionState )
    {
        schedulable.setExecutionState( executionState );
        if ( schedulable instanceof Batch )
        {
            final List<Schedulable> members = ( (Batch) schedulable ).getMembers();
            for ( Schedulable member : members )
            {
                setExecutionState( member, executionState );
            }
        }
        if ( Casting.SCHEDULABLE_STATE.PLANNED == executionState )
        {
            schedulable.setPlannedTS( LocalDateTime.now() );
        }
        else if ( Casting.SCHEDULABLE_STATE.IN_PROGRESS == executionState )
        {
            schedulable.setInProgressTS( LocalDateTime.now() );
        }
        else if ( Casting.SCHEDULABLE_STATE.SUCCESS == executionState )
        {
            schedulable.setSuccessTS( LocalDateTime.now() );
        }
        else if ( Casting.SCHEDULABLE_STATE.FAILED == executionState )
        {
            schedulable.setFailedTS( LocalDateTime.now() );
        }
        else if ( Casting.SCHEDULABLE_STATE.CANCELED == executionState )
        {
            schedulable.setCanceledTS( LocalDateTime.now() );
        }
    }
}
