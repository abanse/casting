package com.hydro.casting.server.ejb.main.service;

import com.hydro.casting.common.constant.MachineStep;
import com.hydro.casting.server.model.sched.Batch;
import com.hydro.casting.server.model.sched.ProcessStep;
import com.hydro.casting.server.model.sched.dao.ProcessStepHome;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class ProcessStepService
{
    @EJB
    private ProcessStepHome processStepHome;

    /**
     * Starts a process step on a batch. If another process step is currently running, the running step is finished properly.
     *
     * @param batch       The batch to start a process step on
     * @param machineStep The information about the step that should be started
     * @param startTS     The timestamp for the start of the step
     */
    public void startProcessStep( Batch batch, MachineStep machineStep, LocalDateTime startTS )
    {
        finishCurrentProcessStep( batch, startTS );
        createNewProcessStep( batch, machineStep.getShortName(), startTS );
    }

    /**
     * Start a new process step without finishing the currently running one. Only starts a new step if no step with the same name is currently running.
     *
     * @param batch       The batch to start a process step on
     * @param machineStep The information about the step that should be started
     * @param startTS     The timestamp for the start of the step
     */
    public void startProcessStepWithoutFinishing( Batch batch, MachineStep machineStep, LocalDateTime startTS )
    {
        // If the step is already running, do nothing
        if ( !isProcessStepActive( batch, machineStep ) )
        {
            createNewProcessStep( batch, machineStep.getShortName(), startTS );
        }
    }

    /**
     * If a process step is currently running for a batch, the step is finished by setting the passed timestamp.
     *
     * @param batch The batch to check for a running process step
     * @param endTS The timestamp for the end of the step
     */
    public void finishCurrentProcessStep( Batch batch, LocalDateTime endTS )
    {
        ProcessStep processStep = getCurrentProcessStep( batch );

        if ( processStep != null )
        {
            processStep.setEnd( endTS );
        }
    }

    /**
     * Finishes all process steps matching the passed machine step.
     *
     * @param batch       The batch to check for running process step s
     * @param machineStep The machine step that should be matched
     * @param endTS       The timestamp for the end of the step
     */
    public void finishProcessStep( Batch batch, MachineStep machineStep, LocalDateTime endTS )
    {
        List<ProcessStep> processSteps = getAllRunningProcessSteps( batch ).stream().filter( ps -> ps.getName().equals( machineStep.getShortName() ) ).collect( Collectors.toList() );
        for ( ProcessStep processStep : processSteps )
        {
            processStep.setEnd( endTS );
        }
    }

    /**
     * Finishes all running process steps. Process steps are running if they do not have an end timestamp, and are finished by setting the end timestamp.
     *
     * @param batch The batch to finish all process steps on
     * @param endTS The end timestamp to set for the finished process steps
     */
    public void finishAllRunningProcessSteps( Batch batch, LocalDateTime endTS )
    {
        getAllRunningProcessSteps( batch ).forEach( ps -> ps.setEnd( endTS ) );
    }

    /**
     * Reset and thus clear all process steps associated with the given batch. This can be used to reset the process on a batch. All process steps will be completely removed from the database.
     *
     * @param batch The batch to clear the process for
     */
    public void clearProcessSteps( Batch batch )
    {
        List<ProcessStep> processSteps = batch.getProcessSteps();

        for ( ProcessStep processStep : processSteps )
        {
            processStepHome.remove( processStep );
        }
    }

    /**
     * Return the timestamp of the given machine step for the given batch.
     * Matching process step is found based on the short name. If multiple matching process steps are found, the earliest one (based on start timestamp) is returned.
     * If the given batch is null or the given machine step is null, return null because no process step can be found then.
     *
     * @param batch       The batch to check for the step
     * @param machineStep The step to find
     * @return The LocalDateTime object defining the start timestamp of the process step, or null if none was found
     */
    public LocalDateTime getStepTS( Batch batch, MachineStep machineStep )
    {
        if ( batch == null || machineStep == null )
        {
            return null;
        }

        // Returns the start date of the earliest found process step with the given name, since that's when the "phase" was first started
        return batch.getProcessSteps().stream().filter( ps -> ps.getName().equals( machineStep.getShortName() ) ).min( Comparator.comparing( ProcessStep::getStart ) ).map( ProcessStep::getStart )
                .orElse( null );
    }

    /**
     * This function returns the starting timestamp of the currently active step for a given step name. Only returns a result if an active process step matching the given machine step is found.
     *
     * @param batch       The batch to check for the process step timestamp
     * @param machineStep The machine step that should match the process step
     * @return The starting timestamp of the currently active process step matching the machine step
     */
    public LocalDateTime getActiveStepStartTS( Batch batch, MachineStep machineStep )
    {
        if ( batch == null || machineStep == null || !isProcessStepActive( batch, machineStep ) )
        {
            return null;
        }

        return batch.getProcessSteps().stream().filter( ps -> ps.getName().equals( machineStep.getShortName() ) && ps.getEnd() == null ).min( Comparator.comparing( ProcessStep::getStart ) )
                .map( ProcessStep::getStart ).orElse( null );
    }

    /**
     * Returns the currently running process step if there is exactly one running process step.
     *
     * @param batch The batch to return the current process step for
     * @return The current process step or null if more or less than one is running
     */
    public ProcessStep getCurrentProcessStep( Batch batch )
    {
        List<ProcessStep> runningProcessSteps = getAllRunningProcessSteps( batch );

        if ( runningProcessSteps.size() != 1 )
        {
            return null;
        }

        return runningProcessSteps.get( 0 );
    }

    /**
     * Returns all process steps matching the given machine step, identified by short name.
     *
     * @param batch       The batch to get the process steps from
     * @param machineStep The machine step the process steps should match
     * @return A list of process steps, or null if any passed parameter was null
     */
    public List<ProcessStep> getProcessSteps( Batch batch, MachineStep machineStep )
    {
        if ( batch == null || machineStep == null )
        {
            return null;
        }

        return batch.getProcessSteps().stream().filter( ps -> ps.getName().equals( machineStep.getShortName() ) ).collect( Collectors.toList() );
    }

    /**
     * This function returns a unique process step, which is always the first step of the ones found for the given machine step on the given batch.
     * Should only be used if it's expected that there is only one matching process step or if the first step should be found.
     *
     * @param batch       The batch to search for the process step
     * @param machineStep The machine step that should be matched by the process step
     * @return The first matching process step, or null
     */
    public ProcessStep getProcessStepUnique( Batch batch, MachineStep machineStep )
    {
        if ( batch == null || machineStep == null )
        {
            return null;
        }

        return batch.getProcessSteps().stream().filter( ps -> ps.getName().equals( machineStep.getShortName() ) ).findFirst().orElse( null );
    }

    /**
     * Returns if the give step is currently active on the batch.
     *
     * @param batch       The batch to check
     * @param machineStep The machine step to check
     * @return True if any step matching the machine step by short name is active on the batch, False otherwise
     */
    public boolean isProcessStepActive( Batch batch, MachineStep machineStep )
    {
        return getAllRunningProcessSteps( batch ).stream().anyMatch( ps -> ps.getName().equals( machineStep.getShortName() ) );
    }

    private List<ProcessStep> getAllRunningProcessSteps( Batch batch )
    {
        return batch.getProcessSteps().stream().filter( ps -> ps.getEnd() == null ).collect( Collectors.toList() );
    }

    private void createNewProcessStep( Batch batch, String name, LocalDateTime startTS )
    {
        ProcessStep processStep = new ProcessStep();
        processStep.setName( name );
        processStep.setStart( startTS );
        processStep.setBatch( batch );
        processStepHome.persist( processStep );
    }
}
