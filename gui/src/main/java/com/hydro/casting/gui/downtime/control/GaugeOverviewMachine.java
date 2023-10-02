package com.hydro.casting.gui.downtime.control;

import javafx.fxml.FXML;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

public class GaugeOverviewMachine extends GaugeOverview
{
    @FXML
    private TitledPane pane;
    
    @FXML
    private TextFlow textFlow;
    
    @FXML 
    private VBox vBox;
    
    private boolean hasDowntime;
    private String costCenter;

    public GaugeOverviewMachine()
    {
        super();
        adjustBackgroundColor();
    }

    public String getCostCenter()
    {
        return costCenter;
    }

    public void setCostCenter( String costCenter )
    {
        this.costCenter = costCenter;
    }

    public boolean hasDowntime()
    {
        return hasDowntime;
    }

    public void setHasDowntime( boolean hasDowntime )
    {
        this.hasDowntime = hasDowntime;
        adjustBackgroundColor();
    }

    private void adjustBackgroundColor()
    {
        if ( hasDowntime )
        {
            vBox.setStyle( "-fx-background-color:#ffcfd1" );
        }
        else
        {
            vBox.setStyle( "-fx-background-color:#d1ffcf" );
        }
    }
}
