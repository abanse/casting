package com.hydro.casting.gui.control;

import com.hydro.casting.common.constant.MachineStep;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public abstract class StatusBox extends HBox
{
    private final static Color RED = Color.web( "#E8373E" );
    private final static Color ORANGE = Color.web( "#fca92a" );
    private final static Color GREY = Color.web( "#bababa" );
    private final Circle status = new Circle( 5 );
    private final StackPane stepInfoPane = new StackPane();
    private final Circle stepInfo = new Circle( 11 );
    private final Text stepInfoText = new Text( "AK" );
    private boolean active = true;

    public StatusBox()
    {
        setSpacing( 2 );
        setAlignment( Pos.CENTER_LEFT );

        stepInfoText.setFill( Color.WHITE );

        stepInfoPane.getChildren().add( stepInfo );
        stepInfoPane.getChildren().add( stepInfoText );

        status.setVisible( false );
        status.setTranslateX( 10 );
        status.setTranslateY( 8 );
        stepInfoPane.getChildren().add( status );

        getChildren().add( stepInfoPane );
    }

    protected void set( MachineStep machineStep, String downtimeText, String currentStepDowntime )
    {
        if ( machineStep == null )
        {
            setEmptyStatus();
            return;
        }

        stepInfoText.setText( machineStep.getShortName() );
        if ( this.active )
        {
            stepInfo.setFill( Color.web( machineStep.getColor() ) );
        }
        else
        {
            stepInfo.setFill( GREY );
        }

        if ( currentStepDowntime != null )
        {
            status.setFill( ORANGE );
            status.setVisible( true );
            Tooltip.install( stepInfoPane, new Tooltip( machineStep.getDescription() + "\nStörzeit:\n" + currentStepDowntime ) );
        }
        else if ( downtimeText != null )
        {
            status.setFill( RED );
            status.setVisible( true );
            Tooltip.install( stepInfoPane, new Tooltip( machineStep.getDescription() + "\nStörzeit:\n" + downtimeText ) );
        }
        else
        {
            status.setVisible( false );
            Tooltip.install( stepInfoPane, new Tooltip( machineStep.getDescription() ) );
        }
    }

    protected void setEmptyStatus()
    {
        stepInfoText.setText( "??" );
        stepInfo.setFill( Color.TRANSPARENT );
        status.setVisible( false );
    }

    protected void setActive( boolean active )
    {
        this.active = active;
    }
}
