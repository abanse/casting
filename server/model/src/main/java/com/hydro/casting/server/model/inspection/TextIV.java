package com.hydro.casting.server.model.inspection;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue( "text" )
public class TextIV extends InspectionValue
{
    @Column( name = "text", length = 200 )
    private String text;

    public String getText()
    {
        return text;
    }

    public void setText( String text )
    {
        this.text = text;
    }
}

