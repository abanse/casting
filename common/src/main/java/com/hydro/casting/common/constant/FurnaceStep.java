package com.hydro.casting.common.constant;

import java.util.Objects;
public enum FurnaceStep implements MachineStep
{
    //@formatter:off
    Preparing("OR", "Ofenreinigung", "#E3C567"),
    Charging("CH", "Chargieren", "#75CFB6"),
    Treating("GA", "Gattieren", "#8A8270"),
    Skimming("AK", "Abkrätzen", "#2790B0"),
    Resting("AS", "Abstehen", "#F58723"),
    ReadyToCast("OK", "Freigabe Gießen", "#62c739"),
    Casting("GI", "Gießen", "#8B84B7");
    //@formatter:on

    private String shortName;
    private String description;
    private String color;

    FurnaceStep( String shortName, String description, String color )
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

    public static FurnaceStep findByShortName( String shortName )
    {
        for ( FurnaceStep value : FurnaceStep.values() )
        {
            if ( Objects.equals( value.getShortName(), shortName ) )
            {
                return value;
            }
        }
        return null;
    }
}
