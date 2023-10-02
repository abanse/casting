package com.hydro.casting.server.contract.dto;

import com.hydro.core.common.util.StringTools;
import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.time.LocalDateTime;
import java.util.Objects;

public class ProcessDocuDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private long id;

    private String charge;
    private String alloy;
    private LocalDateTime castingTS;
    private LocalDateTime equipmentConditionTS;
    private Integer equipmentConditionSummary;
    private LocalDateTime visualInspectionTS;
    private Integer visualInspectionSummary;
    private LocalDateTime castingPreparationTS;
    private Integer castingPreparationSummary;
    private LocalDateTime analysisTS;
    private Integer analysisSummary;

    public ProcessDocuDTO()
    {
    }

    public ProcessDocuDTO( long id )
    {
        this.id = id;
    }

    @Override
    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

    public String getCharge()
    {
        return charge;
    }

    public void setCharge( String charge )
    {
        this.charge = charge;
    }

    public String getChargeWithoutYear()
    {
        final String charge = getCharge();
        if ( StringTools.isFilled( charge ) && charge.length() > 2 )
        {
            return charge.substring( 2 );
        }
        return null;
    }

    public void setChargeWithoutYear( String chargeWithoutYear )
    {
        // only getter needed
    }

    public String getAlloy()
    {
        return alloy;
    }

    public void setAlloy( String alloy )
    {
        this.alloy = alloy;
    }

    public LocalDateTime getCastingTS()
    {
        return castingTS;
    }

    public void setCastingTS( LocalDateTime castingTS )
    {
        this.castingTS = castingTS;
    }

    public LocalDateTime getEquipmentConditionTS()
    {
        return equipmentConditionTS;
    }

    public void setEquipmentConditionTS( LocalDateTime equipmentConditionTS )
    {
        this.equipmentConditionTS = equipmentConditionTS;
    }

    public Integer getEquipmentConditionSummary()
    {
        return equipmentConditionSummary;
    }

    public void setEquipmentConditionSummary( Integer equipmentConditionSummary )
    {
        this.equipmentConditionSummary = equipmentConditionSummary;
    }

    public Object[] getEquipmentCondition()
    {
        if ( equipmentConditionTS == null )
        {
            return null;
        }
        return new Object[] { equipmentConditionTS, equipmentConditionSummary };
    }

    public void setEquipmentCondition( Object[] equipmentCondition )
    {
        // only for table, ignore
    }

    public LocalDateTime getVisualInspectionTS()
    {
        return visualInspectionTS;
    }

    public void setVisualInspectionTS( LocalDateTime visualInspectionTS )
    {
        this.visualInspectionTS = visualInspectionTS;
    }

    public Integer getVisualInspectionSummary()
    {
        return visualInspectionSummary;
    }

    public void setVisualInspectionSummary( Integer visualInspectionSummary )
    {
        this.visualInspectionSummary = visualInspectionSummary;
    }

    public Object[] getVisualInspection()
    {
        if ( visualInspectionTS == null )
        {
            return null;
        }
        return new Object[] { visualInspectionTS, visualInspectionSummary };
    }

    public void setVisualInspection( Object[] visualInspection )
    {
        // only for table, ignore
    }

    public LocalDateTime getCastingPreparationTS()
    {
        return castingPreparationTS;
    }

    public void setCastingPreparationTS( LocalDateTime castingPreparationTS )
    {
        this.castingPreparationTS = castingPreparationTS;
    }

    public Integer getCastingPreparationSummary()
    {
        return castingPreparationSummary;
    }

    public void setCastingPreparationSummary( Integer castingPreparationSummary )
    {
        this.castingPreparationSummary = castingPreparationSummary;
    }

    public Object[] getCastingPreparation()
    {
        if ( castingPreparationTS == null )
        {
            return null;
        }
        return new Object[] { castingPreparationTS, castingPreparationSummary };
    }

    public void setCastingPreparation( Object[] castingPreparation )
    {
        // only for table, ignore
    }

    public LocalDateTime getAnalysisTS()
    {
        return analysisTS;
    }

    public void setAnalysisTS( LocalDateTime analysisTS )
    {
        this.analysisTS = analysisTS;
    }

    public Integer getAnalysisSummary()
    {
        return analysisSummary;
    }

    public void setAnalysisSummary( Integer analysisSummary )
    {
        this.analysisSummary = analysisSummary;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        ProcessDocuDTO that = (ProcessDocuDTO) o;
        return getId() == that.getId() && Objects.equals( getCharge(), that.getCharge() ) && Objects.equals( getAlloy(), that.getAlloy() ) && Objects.equals( getCastingTS(), that.getCastingTS() )
                && Objects.equals( getEquipmentConditionTS(), that.getEquipmentConditionTS() ) && Objects.equals( getEquipmentConditionSummary(), that.getEquipmentConditionSummary() )
                && Objects.equals( getVisualInspectionTS(), that.getVisualInspectionTS() ) && Objects.equals( getVisualInspectionSummary(), that.getVisualInspectionSummary() ) && Objects.equals(
                getCastingPreparationTS(), that.getCastingPreparationTS() ) && Objects.equals( getCastingPreparationSummary(), that.getCastingPreparationSummary() ) && Objects.equals( getAnalysisTS(),
                that.getAnalysisTS() ) && Objects.equals( getAnalysisSummary(), that.getAnalysisSummary() );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( getId(), getCharge(), getAlloy(), getCastingTS(), getEquipmentConditionTS(), getEquipmentConditionSummary(), getVisualInspectionTS(), getVisualInspectionSummary(),
                getCastingPreparationTS(), getCastingPreparationSummary(), getAnalysisTS(), getAnalysisSummary() );
    }

    @Override
    public String toString()
    {
        return "ProcessDocuDTO{" + "id=" + id + ", charge='" + charge + '\'' + ", alloy='" + alloy + '\'' + ", castingTS=" + castingTS + ", equipmentConditionTS=" + equipmentConditionTS
                + ", equipmentConditionSummary=" + equipmentConditionSummary + ", visualInspectionTS=" + visualInspectionTS + ", visualInspectionSummary=" + visualInspectionSummary
                + ", castingPreparationTS=" + castingPreparationTS + ", castingPreparationSummary=" + castingPreparationSummary + ", analysisTS=" + analysisTS + ", analysisSummary=" + analysisSummary
                + '}';
    }
}