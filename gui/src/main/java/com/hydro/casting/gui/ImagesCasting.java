package com.hydro.casting.gui;

import com.hydro.core.gui.ImagesCore;
import javafx.scene.image.Image;
public enum ImagesCasting
{
    //@formatter:off
    ADD( "/com/hydro/casting/gui/images/add.png" ),
    ADD_ROW( "/com/hydro/casting/gui/images/addRow.png" ),
    APPEND( "/com/hydro/casting/gui/images/append.png" ),
    APPEND_N( "/com/hydro/casting/gui/images/appendN.png" ),
    BIGGER( "/com/hydro/casting/gui/images/bigger.png" ),
    COMMENT( "/com/hydro/casting/gui/images/comment.png" ),
    COMPRESS( "/com/hydro/casting/gui/images/compress.png" ),
    DECOMPRESS( "/com/hydro/casting/gui/images/decompress.png" ),
    DOWNTIME( "/com/hydro/casting/gui/images/downtime.png" ),
    EDIT_COMMENT( "/com/hydro/casting/gui/images/editComment.png" ),
    EDIT_LOCKED( "/com/hydro/casting/gui/images/editLocked.png" ),
    ERROR( "/com/hydro/casting/gui/images/error.png" ),
    HOT_SPOT( "/com/hydro/casting/gui/images/hotSpot.png" ),
    INSERT( "/com/hydro/casting/gui/images/insert.png" ),
    INSERT_POS( "/com/hydro/casting/gui/images/insertPos.png" ),
    INSERT_POS_N( "/com/hydro/casting/gui/images/insertPosN.png" ),
    LIST( "/com/hydro/casting/gui/images/list.png" ),
    MATERIAL( "/com/hydro/casting/gui/images/material.png" ),
    MATERIALS( "/com/hydro/casting/gui/images/materials.png" ),
    MAXIMIZE_V( "/com/hydro/casting/gui/images/maximizeV.png" ),
    SETUP( "/com/hydro/casting/gui/images/setup.png" ),
    SLAB( "/com/hydro/casting/gui/images/slab.png" ),
    SLAB_LOCKED( "/com/hydro/casting/gui/images/slabLocked.png" ),
    SLAB_UNLOCKED( "/com/hydro/casting/gui/images/slabUnlocked.png" ),
    SPLASH_CASTING( "/com/hydro/casting/gui/images/splashCasting.png" ),
    TARGET( "/com/hydro/casting/gui/images/target.png" ),
    WARNING( "/com/hydro/casting/gui/images/warning.png" ),
    WARNING_DISABLED( "/com/hydro/casting/gui/images/warningDisabled.png" ),
    WIZARD( "/com/hydro/casting/gui/images/wizard.png" ),
    WIZARD_MULTIPLE( "/com/hydro/casting/gui/images/wizardMultiple.png" );
    //@formatter:on

    private String url;

    ImagesCasting( java.lang.String url )
    {
        this.url = url;
    }

    public Image load()
    {
        return new Image( ImagesCore.class.getResourceAsStream( url ) );
    }

    public Image load( double requestedWidth, double requestedHeight, boolean preserveRatio, boolean smooth )
    {
        return new Image( ImagesCore.class.getResourceAsStream( url ), requestedWidth, requestedHeight, preserveRatio, smooth );
    }

}
