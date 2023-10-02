package com.hydro.casting.gui.model.common;

public enum EMaterialGroup
{
    SPEC_MATERIAL("Artgleicher Schrott"),
    COPPER_MELT("Schachtofen"),
    SLAB_SCRAP("Spülgüsse");
    
    private final String apk;

    private EMaterialGroup(final String apk)
    {
	this.apk = apk;
    }
    
    public String getApk()
    {
	return apk;
    }
}
