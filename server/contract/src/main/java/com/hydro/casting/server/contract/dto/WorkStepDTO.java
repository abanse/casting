package com.hydro.casting.server.contract.dto;

import com.hydro.casting.server.model.po.ProductionOrder;
import com.hydro.casting.server.model.res.Plant;
import com.hydro.casting.server.model.sched.Demand;
import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
public class WorkStepDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private long id;
    private String apk;
    private int executionState = 100;
    private String workStepNumber;
    private LocalDateTime startTS;
    private LocalDateTime endTS;
    private String description;
    private String workPlace;
    private String workPlaceDescription;
    private String productionOrderApk;
    private String plantApk;

    @Override
    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

    public String getApk()
    {
        return apk;
    }

    public void setApk( String apk )
    {
        this.apk = apk;
    }

    public int getExecutionState()
    {
        return executionState;
    }

    public void setExecutionState( int executionState )
    {
        this.executionState = executionState;
    }

    public String getWorkStepNumber()
    {
        return workStepNumber;
    }

    public void setWorkStepNumber( String workStepNumber )
    {
        this.workStepNumber = workStepNumber;
    }

    public LocalDateTime getStartTS()
    {
        return startTS;
    }

    public void setStartTS( LocalDateTime startTS )
    {
        this.startTS = startTS;
    }

    public LocalDateTime getEndTS()
    {
        return endTS;
    }

    public void setEndTS( LocalDateTime endTS )
    {
        this.endTS = endTS;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getWorkPlace()
    {
        return workPlace;
    }

    public void setWorkPlace( String workPlace )
    {
        this.workPlace = workPlace;
    }

    public String getWorkPlaceDescription()
    {
        return workPlaceDescription;
    }

    public void setWorkPlaceDescription( String workPlaceDescription )
    {
        this.workPlaceDescription = workPlaceDescription;
    }

    public String getProductionOrderApk()
    {
        return productionOrderApk;
    }

    public void setProductionOrderApk( String productionOrderApk )
    {
        this.productionOrderApk = productionOrderApk;
    }

    public String getPlantApk()
    {
        return plantApk;
    }

    public void setPlantApk( String plantApk )
    {
        this.plantApk = plantApk;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        WorkStepDTO that = (WorkStepDTO) o;
        return id == that.id && executionState == that.executionState && Objects.equals( apk, that.apk ) && Objects.equals( workStepNumber, that.workStepNumber ) && Objects.equals( startTS,
                that.startTS ) && Objects.equals( endTS, that.endTS ) && Objects.equals( description, that.description ) && Objects.equals( workPlace, that.workPlace ) && Objects.equals(
                workPlaceDescription, that.workPlaceDescription ) && Objects.equals( productionOrderApk, that.productionOrderApk ) && Objects.equals( plantApk, that.plantApk );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, apk, executionState, workStepNumber, startTS, endTS, description, workPlace, workPlaceDescription, productionOrderApk, plantApk );
    }

    @Override
    public String toString()
    {
        return "WorkStepDTO{" + "id=" + id + ", apk='" + apk + '\'' + ", executionState=" + executionState + ", workStepNumber='" + workStepNumber + '\'' + ", startTS=" + startTS + ", endTS=" + endTS
                + ", description='" + description + '\'' + ", workPlace='" + workPlace + '\'' + ", workPlaceDescription='" + workPlaceDescription + '\'' + ", productionOrderApk='" + productionOrderApk
                + '\'' + ", plantApk='" + plantApk + '\'' + '}';
    }
}
