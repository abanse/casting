package com.hydro.casting.gui.prod.control;

import com.google.common.primitives.Longs;
import com.hydro.casting.server.contract.dto.ActualValuesDTO;
import com.hydro.casting.server.contract.dto.ProcessDocuDTO;
import com.hydro.casting.server.contract.prod.ProcessDocuView;
import com.hydro.core.gui.comp.chart.ChartZoomManager;
import com.hydro.core.gui.comp.chart.LineChartWithUnit;
import com.hydro.core.gui.control.DetailController;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ActualValuesDetailController extends DetailController<ProcessDocuDTO, ActualValuesDTO>
{
    @FXML
    private LineChartWithUnit<Number, Number> actualValueChart;

    @FXML
    private ImageView info;

    @FXML
    private BorderPane mainPane;

    private String actualValueName;
    private String actualValueDisplayName;
    private String additionalValueName;
    private String additionalValueDisplayName;

    private ChartZoomManager<Number, Number> zoomManager;

    public ActualValuesDetailController()
    {
        super( ProcessDocuView.class, ActualValuesDTO.class );
    }

    public String getActualValueName()
    {
        return actualValueName;
    }

    public void setActualValueName( String actualValueName )
    {
        this.actualValueName = actualValueName;
    }

    public String getAdditionalValueName()
    {
        return additionalValueName;
    }

    public void setAdditionalValueName( String additionalValueName )
    {
        this.additionalValueName = additionalValueName;
    }

    public String getActualValueDisplayName()
    {
        return actualValueDisplayName;
    }

    public void setActualValueDisplayName( String actualValueDisplayName )
    {
        this.actualValueDisplayName = actualValueDisplayName;
    }

    public String getAdditionalValueDisplayName()
    {
        return additionalValueDisplayName;
    }

    public void setAdditionalValueDisplayName( String additionalValueDisplayName )
    {
        this.additionalValueDisplayName = additionalValueDisplayName;
    }

    public String getUnit()
    {
        return actualValueChart.getUnit();
    }

    public void setUnit( String unit )
    {
        actualValueChart.setUnit( unit );
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        setContextProvider( param -> {
            if ( actualValueName == null )
            {
                return null;
            }

            if ( additionalValueName == null ) {
                return Map.of( "actualValueName", actualValueName );
            }

            return Map.of( "actualValueName", actualValueName, "additionalValueName", additionalValueName );
        } );

        final Tooltip infoTooltip = new Tooltip( "Zum Zoomen mit der rechten Maustaste ein Rechteck zeichnen\nZurück zur kompletten Ansicht mit einem Doppelklick rechten Maustaste" );
        Tooltip.install( info, infoTooltip );
        info.setOnMouseClicked( event -> {
            infoTooltip.show( info, event.getScreenX(), event.getScreenY() );
            PauseTransition pt = new PauseTransition( Duration.millis( 10000 ) );
            pt.setOnFinished( e -> infoTooltip.hide() );
            pt.play();
        } );
        zoomManager = new ChartZoomManager<>( mainPane, actualValueChart );
    }

    @Override
    public void loadDetail( ActualValuesDTO data )
    {
        actualValueChart.getData().clear();
        if ( data == null || data.getValues() == null )
        {
            return;
        }

        final XYChart.Series<Number, Number> dataSeries = new XYChart.Series<>();
        final XYChart.Series<Number, Number> additionalDataSeries = new XYChart.Series<>();
        byte[] values = data.getValues();
        byte[] additionalValues = data.getAdditionalValues();

        List<XYChart.Data<Number, Number>> points = prepareDataFromBytes( values );
        addDataPointsToSeries( dataSeries, points );

        if ( additionalValues != null )
        {
            points = prepareDataFromBytes( additionalValues );
            addDataPointsToSeries( additionalDataSeries, points );

            dataSeries.setName( actualValueDisplayName != null ? actualValueDisplayName : actualValueName );
            additionalDataSeries.setName( additionalValueDisplayName != null ? additionalValueDisplayName : additionalValueName );
            actualValueChart.setLegendVisible( true );
        }

        zoomManager.setSeries( dataSeries, additionalDataSeries );
    }

    private void addDataPointsToSeries( XYChart.Series<Number, Number> series, List<XYChart.Data<Number, Number>> points )
    {
        // Skipping first data point, since it contains invalid data that leads to misshaped plots
        final long minTimeStamp = points.stream().mapToLong( datapoint -> (long) datapoint.getXValue() ).min().orElse( System.currentTimeMillis() );
        series.getData().addAll( points.stream().filter( numberNumberData -> !numberNumberData.getXValue().equals( minTimeStamp ) ).collect( Collectors.toList() ) );
    }

    private List<XYChart.Data<Number, Number>> prepareDataFromBytes( byte[] data )
    {
        int readPos = 0;
        final byte[] timeBytes = new byte[8];
        final byte[] valueBytes = new byte[8];
        final List<XYChart.Data<Number, Number>> points = new ArrayList<>();
        while ( data.length > readPos )
        {
            System.arraycopy( data, readPos, timeBytes, 0, timeBytes.length );
            readPos = readPos + timeBytes.length;
            System.arraycopy( data, readPos, valueBytes, 0, valueBytes.length );
            readPos = readPos + valueBytes.length;
            final long timestamp = Longs.fromByteArray( timeBytes );
            final double actualValueNumber = Double.longBitsToDouble( Longs.fromByteArray( valueBytes ) );

            points.add( new XYChart.Data<>( timestamp, actualValueNumber ) );
        }

        return points;
    }
}
