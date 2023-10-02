package com.hydro.casting.server.model.inspection;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue( "bit_set" )
public class BitSetIV extends InspectionValue
{
    @Column( name = "value" )
    private long value;
    @Column( name = "remark", length = 200 )
    private String remark;
    @Column( name = "intervention", length = 200 )
    private String intervention;
    @Column( name = "additional_info", length = 200 )
    private String additionalInfo;

    public long getValue()
    {
        return value;
    }

    public void setValue( long value )
    {
        this.value = value;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark( String remark )
    {
        this.remark = remark;
    }

    public String getIntervention()
    {
        return intervention;
    }

    public void setIntervention( String intervention )
    {
        this.intervention = intervention;
    }

    public String getAdditionalInfo()
    {
        return additionalInfo;
    }

    public void setAdditionalInfo( String additionalInfo )
    {
        this.additionalInfo = additionalInfo;
    }
}

