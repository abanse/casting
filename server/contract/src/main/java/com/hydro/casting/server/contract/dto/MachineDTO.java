package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.time.LocalDateTime;
import java.util.Objects;

public class MachineDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private String apk;
    private Double maxWeight;
    private Integer maxCastingLength;
    private Integer lastCharge;
    private String currentStep;
    private LocalDateTime currentStepStartTS;
    private long currentStepDuration;
    private String activeDowntime;
    private String currentStepDowntime;

    @Override
    public long getId()
    {
        return Objects.hash( apk );
    }

    public String getApk()
    {
        return apk;
    }

    public void setApk( String apk )
    {
        this.apk = apk;
    }

    public Double getMaxWeight()
    {
        return maxWeight;
    }

    public void setMaxWeight( Double maxWeight )
    {
        this.maxWeight = maxWeight;
    }

    public Integer getMaxCastingLength()
    {
        return maxCastingLength;
    }

    public void setMaxCastingLength( Integer maxCastingLength )
    {
        this.maxCastingLength = maxCastingLength;
    }

    public Integer getLastCharge()
    {
        return lastCharge;
    }

    public void setLastCharge( Integer lastCharge )
    {
        this.lastCharge = lastCharge;
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

    public String getActiveDowntime()
    {
        return activeDowntime;
    }

    public void setActiveDowntime( String activeDowntime )
    {
        this.activeDowntime = activeDowntime;
    }

    public String getCurrentStepDowntime()
    {
        return currentStepDowntime;
    }

    public void setCurrentStepDowntime( String currentStepDowntime )
    {
        this.currentStepDowntime = currentStepDowntime;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        MachineDTO that = (MachineDTO) o;
        return currentStepDuration == that.currentStepDuration && Objects.equals( apk, that.apk ) && Objects.equals( maxWeight, that.maxWeight ) && Objects.equals( maxCastingLength,
                that.maxCastingLength ) && Objects.equals( lastCharge, that.lastCharge ) && Objects.equals( currentStep, that.currentStep ) && Objects.equals( currentStepStartTS,
                that.currentStepStartTS ) && Objects.equals( activeDowntime, that.activeDowntime ) && Objects.equals( currentStepDowntime, that.currentStepDowntime );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( apk, maxWeight, maxCastingLength, lastCharge, currentStep, currentStepStartTS, currentStepDuration, activeDowntime, currentStepDowntime );
    }

    @Override
    public String toString()
    {
        return "MachineDTO{" + "apk='" + apk + '\'' + ", maxWeight=" + maxWeight + ", maxCastingLength=" + maxCastingLength + ", lastCharge=" + lastCharge + ", currentStep='" + currentStep + '\''
                + ", currentStepStartTS=" + currentStepStartTS + ", currentStepDuration=" + currentStepDuration + ", activeDowntime='" + activeDowntime + '\'' + ", currentStepDowntime='"
                + currentStepDowntime + '\'' + '}';
    }
}
