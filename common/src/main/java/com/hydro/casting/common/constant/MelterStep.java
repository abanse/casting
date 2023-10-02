package com.hydro.casting.common.constant;

import java.util.Objects;

public enum MelterStep implements MachineStep
{
    //@formatter:off
    Charging( "CH", "Chargieren", "#75CFB6", 0),
    Melting( "SC", "Schmelzen", "#62C739", 1),
    Skimming( "AK", "Abkrätzen", "#2790B0", 2),
    SkimmingMeltingChamber( "AK2", "Abkrätzen Schmelzkammer", "#2790B0", 3 ),
    Treating( "GA", "Gattieren", "#8A8270", 4),
    Heating( "AH", "Aufheizen", "#F58723", 5),
    Pouring( "AF", "Abfüllen", "#8B84B7", 6),
    Dredging( "AB", "Ausbaggern", "#E3C567", 7),
    Mixing( "MV", "Materialverteilung", "#E3C567", 8);
    //@formatter:on

    private final String shortName;
    private final String description;
    private final String color;
    private final int orderId;

    MelterStep( String shortName, String description, String color, int orderId )
    {
        this.shortName = shortName;
        this.description = description;
        this.color = color;
        this.orderId = orderId;
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

    public int getOrderId()
    {
        return orderId;
    }

    public static MelterStep findByShortName( String shortName )
    {
        for ( MelterStep value : MelterStep.values() )
        {
            if ( Objects.equals( value.getShortName(), shortName ) )
            {
                return value;
            }
        }
        return null;
    }
}
