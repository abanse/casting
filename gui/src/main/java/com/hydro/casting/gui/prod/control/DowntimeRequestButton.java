package com.hydro.casting.gui.prod.control;

import com.google.inject.Injector;
import com.hydro.casting.common.Casting;
import com.hydro.casting.gui.ImagesCasting;
import com.hydro.casting.server.contract.downtime.dto.DowntimeRequestDTO;
import com.hydro.core.common.cache.ClientCache;
import com.hydro.core.gui.CacheListener;
import com.hydro.core.gui.CacheManager;
import com.hydro.core.gui.comp.InfoMarker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DowntimeRequestButton extends GridPane implements CacheListener
{
    private InfoMarker infoMarker = new InfoMarker();
    private Button openListButton = new Button();

    private String machine;

    private CacheManager cacheManager;

    public DowntimeRequestButton()
    {
        getColumnConstraints().add( new ColumnConstraints() );
        final ColumnConstraints spaceCC = new ColumnConstraints();
        spaceCC.setPrefWidth( 12 );
        getColumnConstraints().add( spaceCC );
        final RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setFillHeight( true );
        rowConstraints.setVgrow( Priority.ALWAYS );
        getRowConstraints().add( rowConstraints );
        final Image downtimeImage = ImagesCasting.DOWNTIME.load( 21, 21, true, true );
        openListButton.setGraphic( new ImageView( downtimeImage ) );
        GridPane.setValignment( openListButton, VPos.CENTER );
        GridPane.setHalignment( openListButton, HPos.CENTER );
        add( openListButton, 0, 0 );

        GridPane.setValignment( infoMarker, VPos.TOP );
        GridPane.setHalignment( infoMarker, HPos.RIGHT );
        //infoMarker.setCount( 10 );
        add( infoMarker, 0, 0, 2, 1 );
    }

    public void connect( final Injector injector, final String machine )
    {
        this.machine = machine;

        cacheManager = injector.getInstance( CacheManager.class );

        cacheManager.addCacheListener( Casting.CACHE.PROD_VERSION_CACHE_NAME, Casting.CACHE.DOWNTIME_REQUEST_KEY + "/version", this );
        cacheEntryModified( null );
    }

    @Override
    public void cacheEntryModified( String key )
    {
        final ClientCache<List<Long>> sequenceCache = cacheManager.getCache( Casting.CACHE.PROD_CACHE_NAME );
        final List<Long> sequence = sequenceCache.get( Casting.CACHE.DOWNTIME_REQUEST_KEY );
        if ( sequence == null )
        {
            setStatus( null );
            return;
        }
        final ClientCache<DowntimeRequestDTO> dataCache = cacheManager.getCache( Casting.CACHE.PROD_CACHE_NAME );
        final Map<Long, DowntimeRequestDTO> cacheData = dataCache.getAll( Casting.CACHE.DOWNTIME_REQUEST_KEY + "/data/", sequence );
        long count = cacheData.values().stream().filter( downtimeRequest -> Objects.equals( machine, downtimeRequest.getCostCenter() ) ).count();
        setStatus( count );
    }

    private void setStatus( Long countEntries )
    {
        if ( countEntries == null || countEntries <= 0 )
        {
            infoMarker.setCount( 0 );
            openListButton.setDisable( true );
        }
        else
        {
            infoMarker.setCount( countEntries.intValue() );
            openListButton.setDisable( false );
        }
    }

    public void setOnAction( EventHandler<ActionEvent> value)
    {
        openListButton.setOnAction( value );
    }

    public EventHandler<ActionEvent> getOnAction()
    {
        return openListButton.getOnAction();
    }
}
