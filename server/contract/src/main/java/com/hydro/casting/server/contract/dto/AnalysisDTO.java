package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class AnalysisDTO implements ViewDTO
{
    // Constants are used both backend- and frontend-side, so they need to be on the DTO
    public final static int ANALYSIS_OK = 0;
    public final static int ANALYSIS_LOW = 1;
    public final static int ANALYSIS_HIGH = 2;
    public final static int ANALYSIS_LOW_HIGH = 3;
    private static final long serialVersionUID = 1L;
    private static final String STATUS_PREREGISTERED = "V";
    private Long rwSampleId;
    private String name;
    private String analysisNo;
    private String alloyName;
    private Integer alloyVersion;
    private Long alloyId;
    private String charge;
    private String melter;
    private String sampleNumber;
    private String status;
    private Integer analysisOk;
    private LocalDateTime originalPreregistrationTime;
    private LocalDateTime preregistrationTime;
    private LocalDateTime registrationTime;
    private LocalDateTime scanTime;
    private LocalDateTime approvalTime;
    private Duration processingTimeLab;
    private Integer year;
    private LocalDateTime lastChanged;
    private Long analysisId;

    @Override
    public long getId()
    {
        if ( rwSampleId != null )
        {
            /*
            ID has to include both rwSampleId and analysisNo, because the following situations can occur:
            1. One analysis maps to 2+ entries in V_VAW_RW_SAMPLE: analysisNo is not unique for the DTO
            2. One entry in V_VAW_RW_SAMPLE maps to two analyses: rwSampleId is not unique for the DTO

            In consequence, only the combination of rwSampleId and analysisNo (if existing) is unique for each DTO.
             */
            return Long.parseLong( rwSampleId + ( analysisNo != null ? analysisNo : "" ) );
        }
        else
        {
            // AnalysisDTO describes a header element -> charge
            return Long.parseLong( charge );
        }
    }

    public Long getRwSampleId()
    {
        return rwSampleId;
    }

    public void setRwSampleId( Long rwSampleId )
    {
        this.rwSampleId = rwSampleId;
    }

    public String getName()
    {
        if ( name == null )
        {
            name = String.join( " ", this.getCharge(), this.getMelter() );
        }
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getAnalysisNo()
    {
        return analysisNo;
    }

    public void setAnalysisNo( String analysisNo )
    {
        this.analysisNo = analysisNo;
    }

    public String getAlloyName()
    {
        return alloyName;
    }

    public void setAlloyName( String alloyName )
    {
        this.alloyName = alloyName;
    }

    public Integer getAlloyVersion()
    {
        return alloyVersion;
    }

    public void setAlloyVersion( Integer alloyVersion )
    {
        this.alloyVersion = alloyVersion;
    }

    public Long getAlloyId()
    {
        return alloyId;
    }

    public void setAlloyId( Long alloyId )
    {
        this.alloyId = alloyId;
    }

    public String getCharge()
    {
        return charge;
    }

    public void setCharge( String charge )
    {
        this.charge = charge;
    }

    public String getMelter()
    {
        return melter;
    }

    public void setMelter( String melter )
    {
        this.melter = melter;
    }

    public String getSampleNumber()
    {
        return sampleNumber;
    }

    public void setSampleNumber( String sampleNumber )
    {
        this.sampleNumber = sampleNumber;
    }

    public String getStatus()
    {
        // Status is set for UI when no status is present in the database. Only applies to child tree items - a header item does not have a preregistration time (stored on sample level)
        if ( status == null && preregistrationTime != null )
        {
            status = STATUS_PREREGISTERED;
        }
        return status;
    }

    public void setStatus( String status )
    {
        this.status = status;
    }

    public Integer getAnalysisOk()
    {
        return analysisOk;
    }

    public void setAnalysisOk( Integer analysisOk )
    {
        this.analysisOk = analysisOk;
    }

    public LocalDateTime getOriginalPreregistrationTime()
    {
        return originalPreregistrationTime;
    }

    public void setOriginalPreregistrationTime( LocalDateTime originalPreregistrationTime )
    {
        this.originalPreregistrationTime = originalPreregistrationTime;
    }

    public LocalDateTime getPreregistrationTime()
    {
        return preregistrationTime;
    }

    public void setPreregistrationTime( LocalDateTime preregistrationTime )
    {
        this.preregistrationTime = preregistrationTime;
    }

    public LocalDateTime getRegistrationTime()
    {
        return registrationTime;
    }

    public void setRegistrationTime( LocalDateTime registrationTime )
    {
        this.registrationTime = registrationTime;
    }

    public LocalDateTime getApprovalTime()
    {
        return approvalTime;
    }

    public void setApprovalTime( LocalDateTime approvalTime )
    {
        this.approvalTime = approvalTime;
    }

    public Duration getProcessingTimeLab()
    {
        if ( processingTimeLab == null && this.getScanTime() != null && this.getApprovalTime() != null )
        {
            processingTimeLab = Duration.between( this.getScanTime(), this.getApprovalTime() );
        }
        return processingTimeLab;
    }

    public void setProcessingTimeLab( Duration processingTimeLab )
    {
        this.processingTimeLab = processingTimeLab;
    }

    public Integer getYear()
    {
        if ( year == null || year == 0 )
        {
            return Objects.requireNonNullElseGet( preregistrationTime, LocalDateTime::now ).getYear();
        }

        return year;
    }

    public void setYear( Integer year )
    {
        this.year = year;
    }

    public LocalDateTime getLastChanged()
    {
        return lastChanged;
    }

    public void setLastChanged( LocalDateTime lastChanged )
    {
        this.lastChanged = lastChanged;
    }

    public Long getAnalysisId()
    {
        return analysisId;
    }

    public void setAnalysisId( Long analysisId )
    {
        this.analysisId = analysisId;
    }

    public LocalDateTime getScanTime()
    {
        return scanTime;
    }

    public void setScanTime( LocalDateTime scanTime )
    {
        this.scanTime = scanTime;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        AnalysisDTO that = (AnalysisDTO) o;
        return Objects.equals( getName(), that.getName() ) && Objects.equals( getAnalysisNo(), that.getAnalysisNo() ) && Objects.equals( getAlloyName(), that.getAlloyName() ) && Objects.equals(
                getAlloyVersion(), that.getAlloyVersion() ) && Objects.equals( getAlloyId(), that.getAlloyId() ) && Objects.equals( getCharge(), that.getCharge() ) && Objects.equals( getMelter(),
                that.getMelter() ) && Objects.equals( getSampleNumber(), that.getSampleNumber() ) && Objects.equals( getStatus(), that.getStatus() ) && Objects.equals( getPreregistrationTime(),
                that.getPreregistrationTime() ) && Objects.equals( getRegistrationTime(), that.getRegistrationTime() ) && Objects.equals( getApprovalTime(), that.getApprovalTime() ) && Objects.equals(
                getScanTime(), that.getScanTime() );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( getName(), getAnalysisNo(), getAlloyName(), getAlloyVersion(), getAlloyId(), getCharge(), getMelter(), getSampleNumber(), getStatus(), getPreregistrationTime(),
                getRegistrationTime(), getApprovalTime(), getScanTime() );
    }

    @Override
    public String toString()
    {
        return "AnalysisDTO{" + "rwSampleId=" + rwSampleId + ", name='" + name + '\'' + ", analysisNo='" + analysisNo + '\'' + ", alloyName='" + alloyName + '\'' + ", alloyVersion=" + alloyVersion
                + ", alloyId=" + alloyId + ", charge='" + charge + '\'' + ", melter='" + melter + '\'' + ", sampleNumber='" + sampleNumber + '\'' + ", status='" + status + '\'' + ", analysisOk="
                + analysisOk + ", originalPreregistrationTime=" + originalPreregistrationTime + ", preregistrationTime=" + preregistrationTime + ", registrationTime=" + registrationTime
                + ", scanTime=" + scanTime + ", approvalTime=" + approvalTime + ", processingTimeLab=" + processingTimeLab + ", year=" + year + ", lastChanged=" + lastChanged + ", analysisId="
                + analysisId + '}';
    }
}
