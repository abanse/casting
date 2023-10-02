package com.hydro.casting.server.model.inspection;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue( "bit_set" )
public class BitSetIR extends InspectionRule
{
    @Column( name = "target_value" )
    private long targetValue;
    @Column( name = "initial_value" )
    private long initialValue;
    @Column( name = "intervention_description", length = 200 )
    private String interventionDescription;
    @Column( name = "additiona_info_description", length = 200 )
    private String additionalInfoDescription;

    public long getTargetValue()
    {
        return targetValue;
    }

    public void setTargetValue( long targetValue )
    {
        this.targetValue = targetValue;
    }

    public long getInitialValue()
    {
        return initialValue;
    }

    public void setInitialValue( long initialValue )
    {
        this.initialValue = initialValue;
    }

    public String getInterventionDescription()
    {
        return interventionDescription;
    }

    public void setInterventionDescription( String interventionDescription )
    {
        this.interventionDescription = interventionDescription;
    }

    public String getAdditionalInfoDescription()
    {
        return additionalInfoDescription;
    }

    public void setAdditionalInfoDescription( String additionalInfoDescription )
    {
        this.additionalInfoDescription = additionalInfoDescription;
    }
}

