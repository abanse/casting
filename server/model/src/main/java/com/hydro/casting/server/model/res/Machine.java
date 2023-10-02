package com.hydro.casting.server.model.res;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table( name = "machine" )
@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
@DiscriminatorColumn( name = "discriminator" )
@NamedQuery( name = "machine.apk", query = "select m from Machine m where m.apk = :apk")
public class Machine extends BaseEntity
{
    @Column( name = "apk", length = 20 )
    private String apk;
    @Column( name = "description", length = 200 )
    private String description;
    @Column( name = "current_step", length = 5)
    private String currentStep;
    @Column( name = "current_step_start_ts")
    private LocalDateTime currentStepStartTS;
    @Column( name = "current_step_duration")
    private long currentStepDuration;
    @ManyToOne
    @JoinColumn( name = "plant_oid" )
    private Plant plant;
    @ManyToOne
    @JoinColumn( name = "cost_center_oid" )
    private CostCenter costCenter;

    public String getApk()
    {
        return apk;
    }

    public void setApk( String newApk )
    {
        this.apk = newApk;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String newDescription )
    {
        this.description = newDescription;
    }

    public String getCurrentStep()
    {
        return currentStep;
    }

    public void setCurrentStep( String currentStep )
    {
        this.currentStep = currentStep;
    }

    public LocalDateTime getCurrentStepStartTS()
    {
        return currentStepStartTS;
    }

    public void setCurrentStepStartTS( LocalDateTime currentStepStartTS )
    {
        this.currentStepStartTS = currentStepStartTS;
    }

    public long getCurrentStepDuration()
    {
        return currentStepDuration;
    }

    public void setCurrentStepDuration( long currentStepDuration )
    {
        this.currentStepDuration = currentStepDuration;
    }

    public Plant getPlant()
    {
        return plant;
    }

    public void setPlant( Plant newPlant )
    {
        Plant oldPlant = this.plant;
        this.plant = newPlant;
        if ( oldPlant != newPlant )
        {
            if ( oldPlant != null )
            {
                oldPlant.oneSided_removeFromMachines( this );
            }
            if ( newPlant != null )
            {
                newPlant.oneSided_addToMachines( this );
            }
        }
    }

    public void oneSided_setPlant( Plant newPlant )
    {
        this.plant = newPlant;
    }

    public CostCenter getCostCenter()
    {
        return costCenter;
    }

    public void setCostCenter( CostCenter newCostCenter )
    {
        CostCenter oldCostCenter = this.costCenter;
        this.costCenter = newCostCenter;
        if ( oldCostCenter != newCostCenter )
        {
            if ( oldCostCenter != null )
            {
                oldCostCenter.oneSided_removeFromMachines( this );
            }
            if ( newCostCenter != null )
            {
                newCostCenter.oneSided_addToMachines( this );
            }
        }
    }

    public void oneSided_setCostCenter( CostCenter newCostCenter )
    {
        this.costCenter = newCostCenter;
    }

    public void removeAllAssociations()
    {
        setPlant( null );
        setCostCenter( null );
    }
}

