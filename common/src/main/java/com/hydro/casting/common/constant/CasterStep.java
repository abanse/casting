package com.hydro.casting.common.constant;

import java.util.Objects;
public enum CasterStep implements MachineStep
{
    //@formatter:off
    Casting("GI", "Gießen", "#8B84B7"),
    Unloading("AF", "Abfahren und Reinigen", "#F58723");
    //@formatter:on

    private String shortName;
    private String description;
    private String color;

    CasterStep( String shortName, String description, String color )
    {
        this.shortName = shortName;
        this.description = description;
        this.color = color;
    }

    public String getShortName()
    {
        return shortName;
    }

    public String getDescription()
    {
        return description;
    }

    public String getColor()
    {
        return color;
    }

    public static CasterStep findByShortName( String shortName )
    {
        for ( CasterStep value : CasterStep.values() )
        {
            if ( Objects.equals( value.getShortName(), shortName ) )
            {
                return value;
            }
        }
        return null;
    }
}
