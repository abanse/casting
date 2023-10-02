package com.hydro.casting.server.model.mat;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table( name = "analysis_element" )
public class AnalysisElement extends BaseEntity
{
    @Column( name = "name", length = 5 )
    private String name;
    @Column( name = "standard_value" )
    private Double standardValue;
    @Column( name = "precision" )
    private Integer precision;

    @ManyToOne
    @JoinColumn( name = "analysis_oid" )
    private Analysis analysis;

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public Double getStandardValue()
    {
        return standardValue;
    }

    public void setStandardValue( Double newStandardValue )
    {
        this.standardValue = newStandardValue;
    }

    public Integer getPrecision()
    {
        return precision;
    }

    public void setPrecision( Integer precision )
    {
        this.precision = precision;
    }

    public Analysis getAnalysis()
    {
        return analysis;
    }

    public void setAnalysis( Analysis newAnalysis )
    {
        Analysis oldAnalysis = this.analysis;
        this.analysis = newAnalysis;
        if ( oldAnalysis != newAnalysis )
        {
            if ( oldAnalysis != null )
            {
                oldAnalysis.oneSided_removeFromAnalysisElements( this );
            }
            if ( newAnalysis != null )
            {
                newAnalysis.oneSided_addToAnalysisElements( this );
            }
        }
    }

    public void oneSided_setAnalysis( Analysis newAnalysis )
    {
        this.analysis = newAnalysis;
    }

    public void removeAllAssociations()
    {
        setAnalysis( null );
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( !( o instanceof AnalysisElement ) )
            return false;
        AnalysisElement that = (AnalysisElement) o;
        return Objects.equals( name, that.name ) && Objects.equals( standardValue, that.standardValue ) && Objects.equals( precision, that.precision ) && Objects.equals( analysis, that.analysis );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, standardValue, precision, analysis );
    }

    @Override
    public String toString()
    {
        return "AnalysisElement{" + "name='" + name + '\'' + ", standardValue=" + standardValue + '}';
    }
}