package com.hydro.casting.common.constant;

import com.hydro.casting.common.Casting;
public enum CostCenterEnum
{
    //@formatter:off
    ALL( "alle Kostenstellen", "alle Kostenstellen", "*" ),
    ALL_CASTERS("alle Gießanlagen", "alle Gießanlagen", "*1" ),
    ALL_FURNACES("alle Schmelzöfen", "alle Öfen", "*2" ),

    CC50_OVERALL( Casting.MACHINE.CASTER_50 + "-00", "50 Allgemein", "0" ),
    CC50( Casting.MACHINE.CASTER_50, "50 Gießanlage", "1" ),
    CC51( Casting.MACHINE.MELTING_FURNACE_51, "51 Ofen", "2" ),
    CC52( Casting.MACHINE.MELTING_FURNACE_52, "52 Ofen", "2" ),
    CC60_OVERALL( Casting.MACHINE.CASTER_60 + "-00", "60 Allgemein", "0" ),
    CC60( Casting.MACHINE.CASTER_60, "60 Gießanlage", "1" ),
    CC61( Casting.MACHINE.MELTING_FURNACE_61, "61 Ofen", "2" ),
    CC62( Casting.MACHINE.MELTING_FURNACE_62, "62 Ofen", "2" ),
    CC70_OVERALL( Casting.MACHINE.CASTER_70 + "-00", "70 Allgemein", "0" ),
    CC70( Casting.MACHINE.CASTER_70, "70 Gießanlage", "1" ),
    CC71( Casting.MACHINE.MELTING_FURNACE_71, "71 Ofen", "2" ),
    CC72( Casting.MACHINE.MELTING_FURNACE_72, "72 Ofen", "2" ),
    CC80_OVERALL( Casting.MACHINE.CASTER_80 + "-00", "80 Allgemein", "0" ),
    CC80( Casting.MACHINE.CASTER_80, "80 Gießanlage", "1" ),
    CC81( Casting.MACHINE.MELTING_FURNACE_81, "81 Ofen", "2" ),
    CC82( Casting.MACHINE.MELTING_FURNACE_82, "82 Ofen", "2" ),
    CCS1( Casting.MACHINE.MELTING_FURNACE_S1, "S1 Schmelzofen", "2"),
    CCS2( Casting.MACHINE.MELTING_FURNACE_S2, "S2 Schmelzofen", "2");
    //@formatter:on

    private String costCenter;
    private String description;
    private String group;

    CostCenterEnum( String costCenter, String description )
    {
        this( costCenter, description, null );
    }

    CostCenterEnum( String costCenter, String description, String group )
    {
        this.costCenter = costCenter;
        this.description = description;
        this.group = group;
    }

    public String getCostCenter()
    {
        return costCenter;
    }

    public String getDescription()
    {
        return description;
    }

    public String getGroup()
    {
        return group;
    }

    public static CostCenterEnum findByCostCenter( String lwCostCenterEnumCostCenter )
    {
        for ( CostCenterEnum lwCostCenterEnum : values() )
        {
            if ( lwCostCenterEnum.getCostCenter().equals( lwCostCenterEnumCostCenter ) )
            {
                return lwCostCenterEnum;
            }
        }
        return null;
    }

    public static CostCenterEnum findByDescription( String lwCostCenterEnumDescription )
    {
        for ( CostCenterEnum lwCostCenterEnum : values() )
        {
            if ( lwCostCenterEnum.getDescription().equals( lwCostCenterEnumDescription ) )
            {
                return lwCostCenterEnum;
            }
        }
        return null;
    }
}
