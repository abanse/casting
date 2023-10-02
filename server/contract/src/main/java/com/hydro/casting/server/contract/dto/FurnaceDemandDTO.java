package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.time.LocalDateTime;
import java.util.Objects;

public class FurnaceDemandDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private long id;
    private String alloy;
    private String charge;
    private double plannedWeight;
    private double furnaceMaxWeight;
    private double bottomWeight;
    private double plannedELWeight;
    private double plannedRAWeight;
    private double plannedS1Weight;
    private double plannedS2Weight;
    private double plannedS3Weight;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Override
    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

    public String getAlloy()
    {
        return alloy;
    }

    public void setAlloy( String alloy )
    {
        this.alloy = alloy;
    }

    public String getCharge()
    {
        return charge;
    }

    public void setCharge( String charge )
    {
        this.charge = charge;
    }

    public double getPlannedWeight()
    {
        return plannedWeight;
    }

    public void setPlannedWeight( double plannedWeight )
    {
        this.plannedWeight = plannedWeight;
    }

    public double getFurnaceMaxWeight()
    {
        return furnaceMaxWeight;
    }

    public void setFurnaceMaxWeight( double furnaceMaxWeight )
    {
        this.furnaceMaxWeight = furnaceMaxWeight;
    }

    public double getBottomWeight()
    {
        return bottomWeight;
    }

    public void setBottomWeight( double bottomWeight )
    {
        this.bottomWeight = bottomWeight;
    }

    public double getPlannedELWeight()
    {
        return plannedELWeight;
    }

    public void setPlannedELWeight( double plannedELWeight )
    {
        this.plannedELWeight = plannedELWeight;
    }

    public double getPlannedS1Weight()
    {
        return plannedS1Weight;
    }

    public void setPlannedS1Weight( double plannedS1Weight )
    {
        this.plannedS1Weight = plannedS1Weight;
    }

    public double getPlannedS2Weight()
    {
        return plannedS2Weight;
    }

    public void setPlannedS2Weight( double plannedS2Weight )
    {
        this.plannedS2Weight = plannedS2Weight;
    }

    public double getPlannedS3Weight()
    {
        return plannedS3Weight;
    }

    public void setPlannedS3Weight( double plannedS3Weight )
    {
        this.plannedS3Weight = plannedS3Weight;
    }

    public LocalDateTime getStartTime()
    {
        return startTime;
    }

    public void setStartTime( LocalDateTime startTime )
    {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime()
    {
        return endTime;
    }

    public void setEndTime( LocalDateTime endTime )
    {
        this.endTime = endTime;
    }

    public double getPlannedRAWeight()
    {
        return plannedRAWeight;
    }

    public void setPlannedRAWeight( double plannedRAWeight )
    {
        this.plannedRAWeight = plannedRAWeight;
    }

    public double getNeededWeight()
    {
        return furnaceMaxWeight - plannedS1Weight - plannedS2Weight - plannedS3Weight - plannedELWeight - plannedRAWeight - bottomWeight;
    }

    public void setNeededWeight( double neededWeight )
    {
        // Nothing to do, calculated
    }

    public double getPercLiquid()
    {
        final double liquidWeight = plannedS1Weight + plannedS2Weight + plannedS3Weight + plannedELWeight + plannedRAWeight + bottomWeight;
        return liquidWeight / furnaceMaxWeight * 100.0;
    }

    public void setPercLiquid( double percLiquid )
    {
        // Nothing to do, calculated
    }

    public double getPercMetal()
    {
        return 100.0 - getPercLiquid();
    }

    public void setPercMetal( double percMetal )
    {
        // Nothing to do, calculated
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        FurnaceDemandDTO that = (FurnaceDemandDTO) o;
        return id == that.id && Double.compare( that.plannedWeight, plannedWeight ) == 0 && Double.compare( that.furnaceMaxWeight, furnaceMaxWeight ) == 0
                && Double.compare( that.bottomWeight, bottomWeight ) == 0 && Double.compare( that.plannedELWeight, plannedELWeight ) == 0
                && Double.compare( that.plannedRAWeight, plannedRAWeight ) == 0 && Double.compare( that.plannedS1Weight, plannedS1Weight ) == 0
                && Double.compare( that.plannedS2Weight, plannedS2Weight ) == 0 && Double.compare( that.plannedS3Weight, plannedS3Weight ) == 0 && Objects.equals( alloy, that.alloy ) && Objects
                .equals( charge, that.charge ) && Objects.equals( startTime, that.startTime ) && Objects.equals( endTime, that.endTime );
    }

    @Override
    public int hashCode()
    {
        return Objects
                .hash( id, alloy, charge, plannedWeight, furnaceMaxWeight, bottomWeight, plannedELWeight, plannedRAWeight, plannedS1Weight, plannedS2Weight, plannedS3Weight, startTime, endTime );
    }

    @Override
    public String toString()
    {
        return "FurnaceDemandDTO{" + "id=" + id + ", alloy='" + alloy + '\'' + ", charge='" + charge + '\'' + ", plannedWeight=" + plannedWeight + ", furnaceMaxWeight=" + furnaceMaxWeight
                + ", bottomWeight=" + bottomWeight + ", plannedELWeight=" + plannedELWeight + ", plannedRAWeight=" + plannedRAWeight + ", plannedS1Weight=" + plannedS1Weight + ", plannedS2Weight="
                + plannedS2Weight + ", plannedS3Weight=" + plannedS3Weight + ", startTime=" + startTime + ", endTime=" + endTime + '}';
    }
}
