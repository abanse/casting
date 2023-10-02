package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.time.LocalDateTime;
import java.util.Objects;

public class LimsAnalysisDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;
    private static final String STATUS_PREREGISTERED = "V";

    private LocalDateTime preregistrationDate;
    private String origin;
    private Long analysisId;
    private String analysisNo;
    private LocalDateTime registrationDate;
    private LocalDateTime approvalDate;
    private String sampleStatus;
    private String alloy;
    private String charge;
    private String melter;
    private String sampleNumber;
    private String elementName;
    private Double result;
    private Integer resultPrecision;
    private String scientificResult;
    private Double limitLow;
    private Double limitHigh;

    @Override
    public long getId()
    {
        return this.hashCode();
    }

    public LocalDateTime getPreregistrationDate()
    {
        return preregistrationDate;
    }

    public void setPreregistrationDate( LocalDateTime preregistrationDate )
    {
        this.preregistrationDate = preregistrationDate;
    }

    public String getOrigin()
    {
        return origin;
    }

    public void setOrigin( String origin )
    {
        this.origin = origin;
    }

    public Long getAnalysisId()
    {
        return analysisId;
    }

    public void setAnalysisId( Long analysisId )
    {
        this.analysisId = analysisId;
    }

    public String getAnalysisNo()
    {
        return analysisNo;
    }

    public void setAnalysisNo( String analysisNo )
    {
        this.analysisNo = analysisNo;
    }

    public LocalDateTime getRegistrationDate()
    {
        return registrationDate;
    }

    public void setRegistrationDate( LocalDateTime registrationDate )
    {
        this.registrationDate = registrationDate;
    }

    public LocalDateTime getApprovalDate()
    {
        return approvalDate;
    }

    public void setApprovalDate( LocalDateTime approvalDate )
    {
        this.approvalDate = approvalDate;
    }

    public String getSampleStatus()
    {
        if ( sampleStatus == null )
        {
            setSampleStatus( STATUS_PREREGISTERED );
        }
        return sampleStatus;
    }

    public void setSampleStatus( String sampleStatus )
    {
        this.sampleStatus = sampleStatus;
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

    public String getElementName()
    {
        return elementName;
    }

    public void setElementName( String elementName )
    {
        this.elementName = elementName;
    }

    public Double getResult()
    {
        return result;
    }

    public void setResult( Double result )
    {
        this.result = result;
    }

    public Integer getResultPrecision()
    {
        return resultPrecision;
    }

    public void setResultPrecision( Integer resultPrecision )
    {
        this.resultPrecision = resultPrecision;
    }

    public String getScientificResult()
    {
        return scientificResult;
    }

    public void setScientificResult( String scientificResult )
    {
        this.scientificResult = scientificResult;
    }

    public Double getLimitLow()
    {
        return limitLow;
    }

    public void setLimitLow( Double limitLow )
    {
        this.limitLow = limitLow;
    }

    public Double getLimitHigh()
    {
        return limitHigh;
    }

    public void setLimitHigh( Double limitHigh )
    {
        this.limitHigh = limitHigh;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        LimsAnalysisDTO that = (LimsAnalysisDTO) o;
        return Objects.equals( preregistrationDate, that.preregistrationDate ) && Objects.equals( origin, that.origin ) && Objects.equals( analysisId, that.analysisId ) && Objects.equals( analysisNo,
                that.analysisNo ) && Objects.equals( registrationDate, that.registrationDate ) && Objects.equals( approvalDate, that.approvalDate ) && Objects.equals( sampleStatus, that.sampleStatus )
                && Objects.equals( alloy, that.alloy ) && Objects.equals( charge, that.charge ) && Objects.equals( melter, that.melter ) && Objects.equals( sampleNumber, that.sampleNumber )
                && Objects.equals( elementName, that.elementName ) && Objects.equals( result, that.result ) && Objects.equals( resultPrecision, that.resultPrecision ) && Objects.equals(
                scientificResult, that.scientificResult ) && Objects.equals( limitLow, that.limitLow ) && Objects.equals( limitHigh, that.limitHigh );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( preregistrationDate, origin, analysisId, analysisNo, registrationDate, approvalDate, sampleStatus, alloy, charge, melter, sampleNumber, elementName, result,
                resultPrecision, scientificResult, limitLow, limitHigh );
    }

    @Override
    public String toString()
    {
        return "LimsAnalysisDTO{" + "preregistrationDate=" + preregistrationDate + ", origin='" + origin + '\'' + ", analysisId=" + analysisId + ", analysisNumber='" + analysisNo + '\''
                + ", registrationDate=" + registrationDate + ", approvalDate=" + approvalDate + ", sampleStatus='" + sampleStatus + '\'' + ", alloy='" + alloy + '\'' + ", batch='" + charge + '\''
                + ", melter='" + melter + '\'' + ", sampleNumber='" + sampleNumber + '\'' + ", elementName='" + elementName + '\'' + ", absoluteResult=" + result + ", limitResult='" + scientificResult
                + '\'' + ", limitLow=" + limitLow + ", limitHigh=" + limitHigh + '}';
    }
}
