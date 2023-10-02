package com.hydro.casting.server.model.mat;

import com.hydro.casting.server.model.sched.Schedulable;
import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

@Entity
@Table( name = "analysis" )
@NamedQuery( name = "analysis.byParameters", query = Analysis.QUERY_BY_PARAMETERS )
@NamedQuery( name = "analysis.byChargeAndYear", query = Analysis.QUERY_BY_CHARGE_AND_YEAR )
@NamedQuery( name = "analysis.findLastActiveByName", query = "select a from Analysis a where a.name = :name and a.status = 'active' order by a.registrationTime desc" )
@NamedQuery( name = "analysis.findLastFurnaceAnalysis", query = "select a from Analysis a where a.charge = :charge and a.status = 'F' and a.registrationTime is not null order by a.registrationTime desc" )
@NamedQuery( name = "analysis.findBySchedulableOID", query = "select a from Analysis a where a.schedulable.objid = :schedulableOID order by a.registrationTime desc" )
public class Analysis extends BaseEntity
{
    //@formatter:off
    protected static final String QUERY_BY_PARAMETERS = "select a from Analysis a " +
            "where a.preregistrationTime > (:date) " +
            "and a.charge = (:charge) " +
            "and a.melter = (:melter) " +
            "and a.sampleNumber = (:sampleNumber)";
    //@formatter:on

    //@formatter:off
    protected static final String QUERY_BY_CHARGE_AND_YEAR = "select a from Analysis a " +
            "where (a.charge = (:charge) or a.charge = (:chargeWithLeadingZero)) " +
            "and a.preregistrationTime > (:beginningOfPreviousYear)";
    //@formatter:on

    @Column( name = "name", length = 30 )
    private String name;
    @Column( name = "analysis_no" )
    private String analysisNo;
    @Column( name = "charge", length = 20 )
    private String charge;
    @Column( name = "original_preregistration_date" )
    private LocalDateTime originalPreregistrationTime;
    @Column( name = "preregistration_date" )
    private LocalDateTime preregistrationTime;
    @Column( name = "registration_date" )
    private LocalDateTime registrationTime;
    @Column( name = "application_date" )
    private LocalDateTime approvalTime;
    @Column( name = "scan_date" )
    private LocalDateTime scanTime;
    @Column( name = "sample_number" )
    private String sampleNumber;
    @Column( name = "melter" )
    private String melter;
    @Column( name = "status" )
    private String status;
    @Column( name = "analysis_ok" )
    private Integer analysisOk;

    @OneToMany( cascade = CascadeType.ALL, mappedBy = "analysis" )
    private Set<AnalysisElement> analysisElements;
    @ManyToOne
    @JoinColumn( name = "alloy_oid" )
    private Alloy alloy;

    @ManyToOne
    @JoinColumn( name = "schedulable_oid" )
    private Schedulable schedulable;

    public String getName()
    {
        return name;
    }

    public void setName( String newName )
    {
        this.name = newName;
    }

    public String getAnalysisNo()
    {
        return analysisNo;
    }

    public void setAnalysisNo( String newAnalysisNo )
    {
        this.analysisNo = newAnalysisNo;
    }

    public String getCharge()
    {
        return charge;
    }

    public void setCharge( String newChargeID )
    {
        this.charge = newChargeID;
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

    public void setApprovalTime( LocalDateTime applicationTime )
    {
        this.approvalTime = applicationTime;
    }

    public LocalDateTime getScanTime()
    {
        return scanTime;
    }

    public void setScanTime( LocalDateTime scanTime )
    {
        this.scanTime = scanTime;
    }

    public String getSampleNumber()
    {
        return sampleNumber;
    }

    public void setSampleNumber( String sampleNumber )
    {
        this.sampleNumber = sampleNumber;
    }

    public String getMelter()
    {
        return melter;
    }

    public void setMelter( String melter )
    {
        this.melter = melter;
    }

    public String getStatus()
    {
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

    public void setAnalysisOk( Integer analysis_ok )
    {
        this.analysisOk = analysis_ok;
    }

    public Set<AnalysisElement> getAnalysisElements()
    {
        if ( analysisElements == null )
        {
            analysisElements = new HashSet<>();
        }
        return analysisElements;
    }

    public boolean containsInAnalysisElements( AnalysisElement analysisElement )
    {
        return ( this.analysisElements != null ) && this.analysisElements.contains( analysisElement );
    }

    public int numberOfAnalysisElements()
    {
        return ( analysisElements == null ) ? 0 : analysisElements.size();
    }

    public void flushAnalysisElements()
    {
        if ( analysisElements != null )
        {
            Iterator<AnalysisElement> iter = analysisElements.iterator();
            while ( iter.hasNext() )
            {
                removeFromAnalysisElements( iter.next() );
                iter = analysisElements.iterator();
            }
        }
    }

    public void addToAnalysisElements( AnalysisElement analysisElement )
    {
        getAnalysisElements().add( analysisElement );
        Analysis oldAnalysis = analysisElement.getAnalysis();
        if ( ( oldAnalysis != this ) && ( oldAnalysis != null ) )
        {
            oldAnalysis.oneSided_removeFromAnalysisElements( analysisElement );
        }
        analysisElement.oneSided_setAnalysis( this );
    }

    public void removeFromAnalysisElements( AnalysisElement analysisElement )
    {
        getAnalysisElements().remove( analysisElement );
        analysisElement.oneSided_setAnalysis( null );
    }

    public void oneSided_addToAnalysisElements( AnalysisElement analysisElement )
    {
        getAnalysisElements().add( analysisElement );
    }

    public void oneSided_removeFromAnalysisElements( AnalysisElement analysisElement )
    {
        getAnalysisElements().remove( analysisElement );
    }

    public Alloy getAlloy()
    {
        return alloy;
    }

    public void setAlloy( Alloy alloy )
    {
        this.alloy = alloy;
    }

    public Schedulable getSchedulable()
    {
        return schedulable;
    }

    public void setSchedulable( Schedulable schedulable )
    {
        this.schedulable = schedulable;
    }

    public void removeAllAssociations()
    {
        flushAnalysisElements();
        setAlloy( null );
        setSchedulable( null );
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( !( o instanceof Analysis ) )
            return false;
        Analysis analysis = (Analysis) o;
        return Objects.equals( name, analysis.name ) && Objects.equals( analysisNo, analysis.analysisNo ) && Objects.equals( charge, analysis.charge ) && Objects.equals( originalPreregistrationTime,
                analysis.originalPreregistrationTime ) && Objects.equals( preregistrationTime, analysis.preregistrationTime ) && Objects.equals( registrationTime, analysis.registrationTime )
                && Objects.equals( approvalTime, analysis.approvalTime ) && Objects.equals( scanTime, analysis.scanTime ) && Objects.equals( sampleNumber, analysis.sampleNumber ) && Objects.equals(
                melter, analysis.melter ) && Objects.equals( status, analysis.status );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, analysisNo, charge, originalPreregistrationTime, preregistrationTime, registrationTime, approvalTime, scanTime, sampleNumber, melter, status );
    }

    @Override
    public String toString()
    {
        return "Analysis{" + "name='" + name + '\'' + ", analysisNo='" + analysisNo + '\'' + ", charge='" + charge + '\'' + ", originalPreregistrationTime=" + originalPreregistrationTime
                + ", preregistrationTime=" + preregistrationTime + ", registrationTime=" + registrationTime + ", approvalTime=" + approvalTime + ", scanTime=" + scanTime + ", sampleNumber='"
                + sampleNumber + '\'' + ", melter='" + melter + '\'' + ", status='" + status + '\'' + ", analysisOk=" + analysisOk + ", analysisElements=" + analysisElements + ", alloy=" + alloy
                + ", schedulable=" + schedulable + '}';
    }
}

