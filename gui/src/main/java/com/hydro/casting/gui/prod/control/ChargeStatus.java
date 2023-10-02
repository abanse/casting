package com.hydro.casting.gui.prod.control;

import com.google.inject.Injector;
import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.dto.FurnaceInstructionDTO;
import com.hydro.core.common.cache.ClientCache;
import com.hydro.core.gui.CacheManager;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.Objects;

public class ChargeStatus extends HBox
{
    private String machineApk;

    private final Text chargeInfoText = new Text();

    private ClientCache<FurnaceInstructionDTO> furnaceInstructionCache;

    public ChargeStatus()
    {
        setMouseTransparent( true );
        setSpacing( 2 );
        setAlignment( Pos.TOP_LEFT );

        chargeInfoText.setFill( Color.DARKGRAY );

        getChildren().add( chargeInfoText );
    }

    public void connect( Injector injector, String machineApk )
    {
        this.machineApk = machineApk;

        final CacheManager cacheManager = injector.getInstance( CacheManager.class );
        furnaceInstructionCache = cacheManager.getCache( Casting.CACHE.PROD_CACHE_NAME );
        cacheManager.addCacheListener( Casting.CACHE.PROD_CACHE_NAME, Casting.CACHE.FURNACE_INSTRUCTION_DATA_PATH + "/data/" + Objects.hash( machineApk ), key -> load() );
        load();
    }

    private void load()
    {
        final FurnaceInstructionDTO furnaceInstructionDTO = furnaceInstructionCache.get( Casting.CACHE.FURNACE_INSTRUCTION_DATA_PATH + "/data/" + Objects.hash( machineApk ) );
        if ( furnaceInstructionDTO == null || furnaceInstructionDTO.getCharge() == null )
        {
            chargeInfoText.setText( null );
        }
        else
        {
            chargeInfoText.setText( furnaceInstructionDTO.getCharge().substring( furnaceInstructionDTO.getCharge().length() - 2 ) );
        }
    }
}
