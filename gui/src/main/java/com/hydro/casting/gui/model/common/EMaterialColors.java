package com.hydro.casting.gui.model.common;

public enum EMaterialColors
{
    DEFAULT( "DEFAULT", "#788187ff" ),

    SPEC_MATERIAL_AS_UNDEFINED( "SPEC_MATERIALAS_UNDEFINED", "#e6e6e6ff" ), SPEC_MATERIAL( "SPEC_MATERIAL", "#bbbabaff" ), FILL_MATERIAL( "FILL_MATERIAL", "#beaa95ff" ),

    KUPFER( "Kaltmetalle", "#ba8046ff" ), SOND_KUPFER( "05", "#ba8046ff" ), SOND_CUNI( "07", "#ba8046ff" ), SCHACHTOFEN( "SCHACHTOFEN", "#ba8046ff" ),

    MESSING( "Flüssigmetalle", "#bfa063ff" ), SOND_MESSING( "06", "#bfa063ff" ), SOND_NICKELMESSING( "10", "#bfa063ff" ),

    SOND_BRONZE( "Sägestücke", "#9f8b68ff" ),

    SOND_NEUSILBER( "Auflegierungen", "#9ea8aeff" ),

    SOND_MUENZEN( "08", "#b87661ff" ),

    SLAB_SCRAP( "SLAB_SCRAP", "#788187ff" ), NEUMETALL( "01", "788187ff" ), LEGIERUNGSMETALL( "04", "788187ff" ), KRAETZEPLATTEN( "97", "788187ff" );

    private final String apk;
    private final String color;

    EMaterialColors( final String apk, final String color )
    {
        this.apk = apk;
        this.color = color;
    }

    public static String findColorByApk( String apk )
    {
        if ( apk != null )
        {
            EMaterialColors[] materialColors = EMaterialColors.values();
            for ( EMaterialColors materialColor : materialColors )
            {
                if ( apk.equals( materialColor.getApk() ) )
                {
                    return materialColor.getColor();
                }
            }
        }
        return DEFAULT.getColor();
    }

    public String getApk()
    {
        return apk;
    }

    public String getColor()
    {
        return color;
    }
}