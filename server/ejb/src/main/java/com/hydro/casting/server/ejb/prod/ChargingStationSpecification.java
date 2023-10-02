package com.hydro.casting.server.ejb.prod;

public enum ChargingStationSpecification
{
    //@formatter:off
    CR80( "AlCr80_Tabletten", "CS_CR80" ),
    MG50( "ALMg50", "CS_MG50" ),
    MN90( "AlMn90_Minitablets", "CS_MN90" ),
    NI20( "AlNi20_Waffelplatten", "CS_NI20" ),
    TI80( "AlTi80_Minitablets", "CS_TI80" ),
    CU99( "Cu99,5_Granulat", "CS_CU99" ),
    FE90( "Fe90Pulver", "CS_FE90" ),
    FE95( "Fe95%", "CS_FE95" ),
    MG99( "Hüttenmagnesium_99,8", "CS_MG99" ),
    SI98( "Silizium98,5","CS_SI98" ),
    ZN99( "Zinkmin99,995%", "CS_ZN99" );
    //@formatter:on

    private String messageName;
    private String materialTypeTag;

    ChargingStationSpecification( String messageName, String materialTypeTag )
    {
        this.messageName = messageName;
        this.materialTypeTag = materialTypeTag;
    }

    public String getMessageName()
    {
        return messageName;
    }

    public String getMaterialTypeTag()
    {
        return materialTypeTag;
    }
}
