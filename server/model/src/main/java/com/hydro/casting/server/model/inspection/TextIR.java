package com.hydro.casting.server.model.inspection;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue( "text" )
public class TextIR extends InspectionRule
{
    @Column( name = "sub_description", length = 200)
    private String subDescription;
    @Column( name = "filled_result" )
    private int filledResult;
    @Column( name = "empty_result" )
    private int emptyResult;

    public String getSubDescription()
    {
        return subDescription;
    }

    public void setSubDescription( String subDescription )
    {
        this.subDescription = subDescription;
    }

    public int getFilledResult()
    {
        return filledResult;
    }

    public void setFilledResult( int filledResult )
    {
        this.filledResult = filledResult;
    }

    public int getEmptyResult()
    {
        return emptyResult;
    }

    public void setEmptyResult( int emptyResult )
    {
        this.emptyResult = emptyResult;
    }
}

