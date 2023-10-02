package com.hydro.casting.gui.downtime.control;

import com.hydro.casting.gui.reporting.view.DetailReportingViewController;
import com.hydro.core.gui.ViewManager;
import com.hydro.core.gui.comp.gauge.KPIGaugeView;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;

public class GaugeOverview extends Pane
{
    private ViewManager viewManager;

    @FXML
    private TitledPane pane;

    @FXML
    private HBox gaugeBox;

    @FXML
    private TextFlow textFlow;

    private String paneTitle = "";
    private String outputGaugeTitle = "gegossene Barren [Stk]";
    private String downtimeGaugeTitle = "Störzeiten [%]";

    private final VBox downtimeVBox = new VBox();
    private final VBox outputVBox = new VBox();
    private KPIGaugeView downtimeGauge = new KPIGaugeView();
    private KPIGaugeView outputGauge = new KPIGaugeView( Color.RED, Color.YELLOW, Color.GREEN );
    private boolean isMouseOver = false;

    public GaugeOverview()
    {
        FXMLLoader fxmlLoader = new FXMLLoader( getClass().getResource( "GaugeOverview.fxml" ) );
        fxmlLoader.setController( this );
        try
        {
            fxmlLoader.load();
        }
        catch ( IOException exception )
        {
            throw new RuntimeException( exception );
        }
    }

    @FXML
    void initialize()
    {
        pane.setText( paneTitle );
        setMaxHeight( 250 );
        setMaxWidth( 600 );
        getChildren().add( pane );

        downtimeGauge.setPrefSize( 100, 100 );
        downtimeGauge.setMinSize( 50, 50 );
        downtimeGauge.setRightArcMin( 0.4 );
        downtimeGauge.setLeftArcMax( 0.3 );

        outputGauge.setPrefSize( 100, 100 );

        gaugeBox.getChildren().add( downtimeVBox );
        gaugeBox.getChildren().add( outputVBox );

    }

    public void addGauges()
    {
        addGauge( downtimeVBox, downtimeGauge, downtimeGaugeTitle );
        addGauge( outputVBox, outputGauge, outputGaugeTitle );
    }

    private void addGauge( VBox box, KPIGaugeView gauge, String title )
    {
        Label titleLabel = new Label( title );
        titleLabel.setAlignment( Pos.CENTER );
        titleLabel.setMaxWidth( Double.MAX_VALUE );
        titleLabel.setStyle( "-fx-font-weight: bold" );
        box.getChildren().add( titleLabel );
        box.getChildren().add( gauge );
    }

    public void setPaneEffect( Effect effect )
    {
        pane.setEffect( effect );
    }

    protected void adjustPaneEffect()
    {
        if ( isMouseOver )
        {
            setPaneEffect( new InnerShadow() );
        }
        else
        {
            setPaneEffect( null );
        }
    }

    @FXML
    private void openDetail( MouseEvent mouseEvent )
    {
        if ( this instanceof GaugeOverviewMachine )
        {
            viewManager.openView( DetailReportingViewController.ID, ((GaugeOverviewMachine) this).getCostCenter() );
        }
    }

    @FXML
    private void mouseEnteredNode( Event event )
    {
        setMouseOver( true );
    }

    @FXML
    private void mouseExitedNode( Event event )
    {
        setMouseOver( false );
    }

    public void setDowntimeValue( double value )
    {
        downtimeGauge.setValue( value );
    }

    public void setOutputValue( double value )
    {
        outputGauge.setValue( value );
    }

    public void setDowntimeTextAreaText( Text... texts )
    {
        textFlow.getChildren().clear();
        if ( texts != null )
        {
            textFlow.getChildren().addAll( texts );
        }
    }

    public void addTextToDowntimeAreaText( Text text )
    {
        textFlow.getChildren().add( text );
    }

    public void clearTextArea()
    {
        textFlow.getChildren().clear();
    }

    public ViewManager getViewManager()
    {
        return viewManager;
    }

    public void setViewManager( ViewManager viewManager )
    {
        this.viewManager = viewManager;
    }

    public String getPaneTitle()
    {
        return paneTitle;
    }

    public void setPaneTitle( String paneTitle )
    {
        this.paneTitle = paneTitle;
        if ( pane != null )
        {
            pane.setText( paneTitle );
        }
    }

    public String getOutputGaugeTitle()
    {
        return outputGaugeTitle;
    }

    public void setOutputGaugeTitle( String outputGaugeTitle )
    {
        this.outputGaugeTitle = outputGaugeTitle;
    }

    public String getDowntimeGaugeTitle()
    {
        return downtimeGaugeTitle;
    }

    public void setDowntimeGaugeTitle( String downtimeGaugeTitle )
    {
        this.downtimeGaugeTitle = downtimeGaugeTitle;
    }

    public KPIGaugeView getDowntimeGauge()
    {
        return downtimeGauge;
    }

    public void setDowntimeGauge( KPIGaugeView downtimeGauge )
    {
        this.downtimeGauge = downtimeGauge;
    }

    public KPIGaugeView getOutputGauge()
    {
        return outputGauge;
    }

    public void setOutputGauge( KPIGaugeView outputGauge )
    {
        this.outputGauge = outputGauge;
    }

    public boolean isMouseOver()
    {
        return isMouseOver;
    }

    public void setMouseOver( boolean isMouseOver )
    {
        this.isMouseOver = isMouseOver;
        adjustPaneEffect();
    }
}
