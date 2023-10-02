package com.hydro.casting.gui.model.common;

public enum ETransferMaterialType
{
    FIXED( "Einsatz" ), VARIABLE( "Auflegieren" ), SPEC_MATERIAL( "Artgleicher Schrott" ), NONE_SPEC_MATERIAL( "Füllmaterial nicht definiert" ), FILL_MATERIAL( "Füllmaterial" ), FURNACE_TRANSFER(
        "Überführung" );

    private final String apk;

    private ETransferMaterialType( final String apk )
    {
        this.apk = apk;
    }

    public static ETransferMaterialType findType( String type )
    {
        if ( type == null )
        {
            return FIXED;
        }
        ETransferMaterialType[] allTypes = ETransferMaterialType.values();
        for ( ETransferMaterialType eTransferMaterialType : allTypes )
        {
            if ( type.equals( eTransferMaterialType.getApk() ) )
            {
                return eTransferMaterialType;
            }
        }
        return FIXED;
    }

    public String getApk()
    {
        return apk;
    }
}
