package com.hydro.casting.gui.prod.control;

import com.google.inject.Injector;
import com.hydro.casting.common.Casting;
import com.hydro.casting.gui.model.CastingClientModel;
import com.hydro.casting.server.contract.dto.FurnaceInstructionDTO;
import com.hydro.casting.server.contract.dto.MachineDTO;
import com.hydro.casting.server.contract.dto.MaterialDTO;
import com.hydro.core.common.cache.ClientCache;
import com.hydro.core.gui.CacheManager;
import com.hydro.core.gui.ClientModelManager;
import com.hydro.core.gui.model.ClientModel;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.infinispan.client.hotrod.exceptions.RemoteCacheManagerNotStartedException;

import java.util.List;
import java.util.Objects;

public class LiquidLevelCanvas extends Canvas implements InvalidationListener
{
    private final DoubleProperty liquidLevel = new SimpleDoubleProperty();
    private final DoubleProperty liquidLevelHeight = new SimpleDoubleProperty( 10.0 );
    private final ObjectProperty<Insets> insets = new SimpleObjectProperty<>( Insets.EMPTY );
    private final ObjectProperty<Color> liquidLevelColor = new SimpleObjectProperty<>( Color.DARKSEAGREEN );

    private ClientModel clientModel;
    private ClientCache<FurnaceInstructionDTO> furnaceInstructionCache;

    private String machineApk;

    public LiquidLevelCanvas()
    {
        setMouseTransparent( true );

        liquidLevel.addListener( this );
        liquidLevelHeight.addListener( this );
        liquidLevelColor.addListener( this );
        revalidate();
    }

    public void connect( Injector injector, String machineApk )
    {
        this.machineApk = machineApk;
        final ClientModelManager clientModelManager = injector.getInstance( ClientModelManager.class );
        clientModel = clientModelManager.getClientModel( CastingClientModel.ID );
        clientModel.addRelationListener( CastingClientModel.MATERIAL, observable -> load() );
        final CacheManager cacheManager = injector.getInstance( CacheManager.class );
        furnaceInstructionCache = cacheManager.getCache( Casting.CACHE.PROD_CACHE_NAME );
        cacheManager.addCacheListener( Casting.CACHE.PROD_CACHE_NAME, Casting.CACHE.FURNACE_INSTRUCTION_DATA_PATH + "/data/" + Objects.hash( machineApk ), key -> load() );

        load();
    }

    private void load()
    {
        MachineDTO machineDTO = null;
        FurnaceInstructionDTO furnaceInstructionDTO = null;
        Double currentWeight = null;
        if ( clientModel != null )
        {
            machineDTO = clientModel.getEntity( MachineDTO.class, machineApk );
            try
            {
                furnaceInstructionDTO = furnaceInstructionCache.get( Casting.CACHE.FURNACE_INSTRUCTION_DATA_PATH + "/data/" + Objects.hash( machineApk ) );
            }
            catch ( RemoteCacheManagerNotStartedException stopException )
            {
                // nothing to do, catch shutdown exception
            }
            final List<MaterialDTO> allMaterials = clientModel.getView( CastingClientModel.MATERIAL_VIEW_ID );
            currentWeight = allMaterials.stream().filter( materialDTO -> ( "OF" + machineApk ).equals( materialDTO.getPlace() ) ).mapToDouble( MaterialDTO::getWeight ).sum();
        }
        loadLiquidLevel( machineDTO, furnaceInstructionDTO, currentWeight );
    }

    private void loadLiquidLevel( MachineDTO machineDTO, FurnaceInstructionDTO furnaceInstructionDTO, Double currentWeight )
    {
        boolean levelPresent = true;
        if ( machineDTO == null || currentWeight == null )
        {
            levelPresent = false;
        }
        if ( furnaceInstructionDTO == null || furnaceInstructionDTO.getCharge() == null )
        {
            levelPresent = false;
        }

        if ( !levelPresent )
        {
            liquidLevel.set( 0 );
        }
        else
        {
            double currentMax = 0;
            if ( machineDTO.getMaxWeight() != null )
            {
                currentMax = machineDTO.getMaxWeight();
            }
            if ( furnaceInstructionDTO != null && furnaceInstructionDTO.getCharge() != null && furnaceInstructionDTO.getPlannedWeight() != null )
            {
                currentMax = furnaceInstructionDTO.getPlannedWeight();
            }
            liquidLevelColor.set( Color.DARKSEAGREEN );
            double liquidLevelValue = currentWeight / currentMax;
            if ( liquidLevelValue > 1. )
            {
                liquidLevelValue = 1;
            }
            liquidLevel.set( liquidLevelValue );
        }
    }

    @Override
    public double minHeight( double width )
    {
        return 10;
    }

    @Override
    public double maxHeight( double width )
    {
        return 10000;
    }

    @Override
    public double prefHeight( double width )
    {
        return minHeight( width );
    }

    @Override
    public double minWidth( double minWidth )
    {
        return 10;
    }

    @Override
    public double maxWidth( double maxWidth )
    {
        return 10000;
    }

    @Override
    public double prefWidth( double width )
    {
        return minWidth( width );
    }

    @Override
    public boolean isResizable()
    {
        return true;
    }

    @Override
    public void resize( double width, double height )
    {
        super.setWidth( width );
        super.setHeight( height );
        revalidate();
    }

    @Override
    public void invalidated( Observable observable )
    {
        revalidate();
    }

    public double getLiquidLevelHeight()
    {
        return liquidLevelHeight.get();
    }

    public DoubleProperty liquidLevelHeightProperty()
    {
        return liquidLevelHeight;
    }

    public void setLiquidLevelHeight( double liquidLevelHeight )
    {
        this.liquidLevelHeight.set( liquidLevelHeight );
    }

    public Insets getInsets()
    {
        return insets.get();
    }

    public ObjectProperty<Insets> insetsProperty()
    {
        return insets;
    }

    public void setInsets( Insets insets )
    {
        this.insets.set( insets );
    }

    private void revalidate()
    {
        final GraphicsContext gc = getGraphicsContext2D();

        gc.clearRect( 0, 0, getWidth(), getHeight() );

        final double progressWidth = ( getWidth() - insets.get().getLeft() - insets.get().getRight() ) * liquidLevel.doubleValue();
        gc.setFill( liquidLevelColor.get() );
        gc.fillRect( insets.get().getLeft(), getHeight() - insets.get().getBottom() - liquidLevelHeight.doubleValue(), progressWidth, liquidLevelHeight.doubleValue() );
    }
}