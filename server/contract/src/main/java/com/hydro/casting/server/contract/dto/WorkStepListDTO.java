package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.util.List;
import java.util.Objects;

public class WorkStepListDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private List<WorkStepDTO> workSteps;

    public WorkStepListDTO()
    {
    }

    public WorkStepListDTO( List<WorkStepDTO> workSteps )
    {
        this.workSteps = workSteps;
    }

    @Override
    public long getId()
    {
        return hashCode();
    }

    public List<WorkStepDTO> getWorkSteps()
    {
        return workSteps;
    }

    public void setWorkSteps( List<WorkStepDTO> workSteps )
    {
        this.workSteps = workSteps;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        WorkStepListDTO that = (WorkStepListDTO) o;
        return Objects.equals( workSteps, that.workSteps );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( workSteps );
    }

    @Override
    public String toString()
    {
        return "WorkStepListDTO{" + "workSteps=" + workSteps + '}';
    }
}
