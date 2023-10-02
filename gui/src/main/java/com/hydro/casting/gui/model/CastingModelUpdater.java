package com.hydro.casting.gui.model;

import com.hydro.casting.common.Casting;
import com.hydro.core.common.cache.ClientCache;
import com.hydro.core.gui.CacheManager;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CastingModelUpdater<S>
{
    private final static Logger log = LoggerFactory.getLogger( CastingModelUpdater.class );
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private Future<?> mainFuture;

    private boolean started = false;
    private UpdaterState engineState = UpdaterState.MUST_RUN;

    private final CacheManager cacheManager;

    private final String cacheName;
    private final Map<Long, S> index;
    private final ObservableList<S> list;
    private final String cachePath;
    private final Callback<Collection<S>, Object> propagationCallback;
    private Callback<Object, Void> finishedCallback;

    public CastingModelUpdater( final CacheManager cacheManager, final String cacheName, final Map<Long, S> index, final ObservableList<S> list, final String cachePath,
            final Callback<Collection<S>, Object> propagationCallback )
    {
        this.cacheManager = cacheManager;
        this.cacheName = cacheName;
        this.index = index;
        this.list = list;
        this.cachePath = cachePath;
        this.propagationCallback = propagationCallback;
    }

    public void start()
    {
        log.debug( "WMSModelUpdater Started: " + cachePath );
        started = true;
        mainFuture = executor.submit( new ModelUpdateWorker() );
    }

    public void stop()
    {
        started = false;
        mainFuture.cancel( true );
        log.debug( "WMSModelUpdater shut down: " + cachePath );
    }

    public synchronized void trigger()
    {
        if ( engineState == UpdaterState.RUNNING )
        {
            engineState = UpdaterState.MUST_RUN;
            return;
        }
        notifyAll();
    }

    public Callback<Object, Void> getFinishedCallback()
    {
        return finishedCallback;
    }

    public void setFinishedCallback( Callback<Object, Void> finishedCallback )
    {
        this.finishedCallback = finishedCallback;
    }

    private synchronized void shouldRun()
    {
        if ( engineState != UpdaterState.MUST_RUN )
        {
            engineState = UpdaterState.WAIT;
            try
            {
                wait();
            }
            catch ( InterruptedException e )
            {
                // ignore
            }
        }
        engineState = UpdaterState.RUNNING;
    }

    private enum UpdaterState
    {WAIT, RUNNING, MUST_RUN}

    private class ModelUpdateWorker implements Runnable
    {
        @Override
        public void run()
        {
            while ( started )
            {
                shouldRun();
                log.trace( "running: " + cachePath );

                final ClientCache<List<Long>> sequenceCache = cacheManager.getCache( cacheName );
                final List<Long> sequence = sequenceCache.get( cachePath );
                if ( sequence == null )
                {
                    return;
                }
                final ClientCache<S> dataCache = cacheManager.getCache( cacheName );
                final Map<Long, S> cacheData = dataCache.getAll( cachePath + "/data/", sequence );
                Platform.runLater( () -> {
                    Object propagationResult = null;
                    if ( index != null )
                    {
                        index.clear();
                        index.putAll( cacheData );
                        if ( propagationCallback != null )
                        {
                            propagationResult = propagationCallback.call( index.values() );
                        }
                        list.setAll( index.values() );
                    }
                    else
                    {
                        if ( propagationCallback != null )
                        {
                            propagationResult = propagationCallback.call( null );
                        }
                        list.setAll( cacheData.values() );
                    }
                    if ( finishedCallback != null)
                    {
                        finishedCallback.call( propagationResult );
                    }
                } );

                log.trace( "finished: " + cachePath );
            }
        }
    }
}
