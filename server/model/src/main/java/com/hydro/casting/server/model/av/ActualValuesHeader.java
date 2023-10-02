package com.hydro.casting.server.model.av;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table( name = "actual_values_header" )
@NamedQuery( name = "actualValuesHeader.measurement", query = "select avh from ActualValuesHeader avh where avh.application = :application and avh.source = :source and avh.type = :type and avh.refName = :refName" )
@NamedQuery( name = "actualValuesHeader.machinesAndTimeRange", query = "select avh from ActualValuesHeader avh where avh.application = :application and avh.source in :machines and avh.lastMeasuredTS >= :fromTS and avh.lastMeasuredTS <= :toTS order by avh.source, avh.lastMeasuredTS" )
@NamedQuery( name = "actualValuesHeader.lastHeaderBefore", query = "select avh from ActualValuesHeader avh where avh.application = :application and avh.source = :machine and avh <> :currentHeader order by avh.lastMeasuredTS desc" )
public class ActualValuesHeader extends BaseEntity
{
    @Column( name = "application", length = 30 )
    private String application;
    @Column( name = "source", length = 20 )
    private String source;
    @Column( name = "type", length = 20 )
    private String type;
    @Column( name = "ref_name", length = 50 )
    private String refName;
    @Column( name = "last_measured_ts" )
    private LocalDateTime lastMeasuredTS;
    @ManyToOne
    @JoinColumn( name = "definition_oid" )
    private ActualValuesDefinition definition;
    @ManyToOne
    @JoinColumn( name = "values_oid" )
    private ActualValues values;
    @Column( name = "validations", length = 10 )
    private String validations;

    public String getApplication()
    {
        return application;
    }

    public void setApplication( String application )
    {
        this.application = application;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource( String source )
    {
        this.source = source;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public String getRefName()
    {
        return refName;
    }

    public void setRefName( String refName )
    {
        this.refName = refName;
    }

    public LocalDateTime getLastMeasuredTS()
    {
        return lastMeasuredTS;
    }

    public void setLastMeasuredTS( LocalDateTime lastMeasuredTS )
    {
        this.lastMeasuredTS = lastMeasuredTS;
    }

    public ActualValuesDefinition getDefinition()
    {
        return definition;
    }

    public void setDefinition( ActualValuesDefinition definition )
    {
        this.definition = definition;
    }

    public ActualValues getValues()
    {
        return values;
    }

    public void setValues( ActualValues values )
    {
        this.values = values;
    }

    public String getValidations()
    {
        return validations;
    }

    public void setValidations( String validations )
    {
        this.validations = validations;
    }

    public void removeAllAssociations()
    {
        setDefinition( null );
        setValues( null );
    }
}
