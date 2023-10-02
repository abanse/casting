package com.hydro.casting.gui.prod.view.content;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.Casting;
import com.hydro.casting.gui.downtime.control.DowntimeChart;
import com.hydro.casting.gui.prod.control.KPIOutputControl;
import com.hydro.core.gui.CacheManager;
import com.hydro.core.gui.ImagesCore;
import com.hydro.core.gui.View;
import com.hydro.core.gui.ViewManager;
import com.hydro.core.gui.comp.TaskButton;
import com.hydro.core.gui.comp.gauge.KPIGaugeView;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import org.controlsfx.control.MasterDetailPane;

public class MoldDepartmentController
{
    @Inject
    private ViewManager viewManager;
    @Inject
    private CacheManager cacheManager;
    @Inject
    private Injector injector;

    @FXML
    private KPIOutputControl outputGauge;
    @FXML
    private KPIGaugeView downtimeGauge;
    @FXML
    private DowntimeChart downtimeChart;
    @FXML
    private TitledPane standard;
    @FXML
    private TaskButton showDetails;
    @FXML
    private MasterDetailPane detailPane;

    private BooleanProperty showDetailsSelected = new SimpleBooleanProperty( false );

    public void initialize()
    {
        outputGauge.init( Casting.MACHINE.MOLD_DEPARTMENT, null, injector );

        downtimeChart.setCachePaths( new String[] { "/downtime/" + Casting.MACHINE.MOLD_DEPARTMENT } );
        downtimeChart.connect( cacheManager );
    }

    public void activateView( View view )
    {
        downtimeChart.start();
    }

    public void deactivateView( View view )
    {
        downtimeChart.stop();
    }

    @FXML
    public void showDetails( ActionEvent actionEvent )
    {
        showDetailsSelected.set( !showDetailsSelected.get() );
        ImageView imageView;
        if ( showDetailsSelected.get() )
        {
            detailPane.setShowDetailNode( true );
            imageView = new ImageView( ImagesCore.EXPAND.load() );
        }
        else
        {
            detailPane.setShowDetailNode( false );
            imageView = new ImageView( ImagesCore.COLLAPSE.load() );
        }
        imageView.setFitHeight( 21.0 );
        imageView.setFitWidth( 21.0 );
        showDetails.setGraphic( imageView );
    }

    @FXML
    public void createDowntime( ActionEvent actionEvent )
    {
    }
}
