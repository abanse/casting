package com.hydro.casting.gui.prod.control;

import com.google.inject.Injector;
import com.hydro.casting.gui.model.CastingClientModel;
import com.hydro.casting.server.contract.dto.MachineDTO;
import com.hydro.core.gui.ClientModelManager;
import com.hydro.core.gui.model.ClientModel;
import javafx.application.Platform;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class ProgressCanvas extends Canvas implements InvalidationListener
{
    private DoubleProperty progress = new SimpleDoubleProperty();
    private DoubleProperty progressHeight = new SimpleDoubleProperty( 10.0 );
    private ObjectProperty<Insets> insets = new SimpleObjectProperty<>( Insets.EMPTY );
    private ObjectProperty<Color> progressColor = new SimpleObjectProperty<>( Color.DARKSEAGREEN );

    private ClientModel clientModel;

    private String machineApk;
    private MachineDTO machineDTO;

    private Timer timer;

    public ProgressCanvas()
    {
        setMouseTransparent( true );

        progress.addListener( this );
        progressHeight.addListener( this );
        progressColor.addListener( this );
        revalidate();
    }

    public void connect( Injector injector, String machineApk )
    {
        this.machineApk = machineApk;
        final ClientModelManager clientModelManager = injector.getInstance( ClientModelManager.class );
        clientModel = clientModelManager.getClientModel( CastingClientModel.ID );
        clientModel.addRelationListener( CastingClientModel.MACHINE, observable -> load() );
        load();
    }

    public void start()
    {
        if ( timer != null )
        {
            // is running
            return;
        }
        timer = new Timer( "progress" + machineApk, true );
        timer.schedule( new ProgressTimerTask(), 0, 1000 );
    }

    public void stop()
    {
        timer.cancel();
        timer = null;
    }

    private void load()
    {
        if ( clientModel != null )
        {
            machineDTO = clientModel.getEntity( MachineDTO.class, machineApk );
        }
        loadProgress();
    }

    private void loadProgress()
    {
        if ( machineDTO == null || machineDTO.getCurrentStepStartTS() == null || machineDTO.getCurrentStepDuration() == 0 )
        {
            progress.set( 0 );
        }
        else
        {
            final LocalDateTime now = LocalDateTime.now();
            final LocalDateTime start = machineDTO.getCurrentStepStartTS();
            final LocalDateTime end = start.plusMinutes( Math.abs( machineDTO.getCurrentStepDuration() ) );
            if ( end.isBefore( now ) )
            {
                // negative Werte werden nicht als Überschreitung ausgewiesen
                if ( machineDTO.getCurrentStepDuration() < 0 )
                {
                    progressColor.set( Color.PERU );
                }
                else
                {
                    progressColor.set( Color.INDIANRED );
                }
                progress.set( 1.0 );
            }
            else
            {
                progressColor.set( Color.DARKSEAGREEN );
                final long delta = Duration.between( start, now ).toSeconds();
                final double progressValue = ( (double) delta ) / ( (double) (Math.abs( machineDTO.getCurrentStepDuration() ) * 60) );
                progress.set( progressValue );
            }
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
    public double minWidth( double height )
    {
        return 10;
    }

    @Override
    public double maxWidth( double height )
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

    public double getProgressHeight()
    {
        return progressHeight.get();
    }

    public DoubleProperty progressHeightProperty()
    {
        return progressHeight;
    }

    public void setProgressHeight( double progressHeight )
    {
        this.progressHeight.set( progressHeight );
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

        //gc.setFill( Color.WHITESMOKE );
        //gc.fillRect( insets.get().getLeft(), getHeight() - insets.get().getBottom() - progressHeight.doubleValue(), getWidth() - insets.get().getLeft() - insets.get().getRight(), progressHeight.doubleValue() );

        final double progressWidth = ( getWidth() - insets.get().getLeft() - insets.get().getRight() ) * progress.doubleValue();
        gc.setFill( progressColor.get() );
        gc.fillRect( insets.get().getLeft(), getHeight() - insets.get().getBottom() - progressHeight.doubleValue(), progressWidth, progressHeight.doubleValue() );
    }

    public class ProgressTimerTask extends TimerTask
    {
        @Override
        public void run()
        {
            Platform.runLater( () -> loadProgress() );
        }
    }

}