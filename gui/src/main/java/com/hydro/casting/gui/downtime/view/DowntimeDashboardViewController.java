package com.hydro.casting.gui.downtime.view;

import com.google.inject.Inject;
import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.downtime.control.GaugeOverview;
import com.hydro.casting.gui.downtime.control.GaugeOverviewMachine;
import com.hydro.casting.gui.reporting.data.ReportingCacheDataProvider;
import com.hydro.casting.server.contract.reporting.dto.ReportingGaugeSummaryDTO;
import com.hydro.core.gui.*;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@ViewDeclaration( id = DowntimeDashboardViewController.ID, fxmlFile = "/com/hydro/casting/gui/downtime/view/DowntimeDashboardView.fxml", type = ViewType.MAIN )
public class DowntimeDashboardViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.DOWNTIME.DASHBOARD.VIEW;

    //@formatter:off
    private static final String[] dashboardCostCenters = {
            Casting.MACHINE.CASTER_50,
            Casting.MACHINE.MELTING_FURNACE_51,
            Casting.MACHINE.MELTING_FURNACE_52,
            Casting.MACHINE.CASTER_60,
            Casting.MACHINE.MELTING_FURNACE_61,
            Casting.MACHINE.MELTING_FURNACE_62,
            Casting.MACHINE.CASTER_70,
            Casting.MACHINE.MELTING_FURNACE_71,
            Casting.MACHINE.MELTING_FURNACE_72,
            Casting.MACHINE.CASTER_80,
            Casting.MACHINE.MELTING_FURNACE_81,
            Casting.MACHINE.MELTING_FURNACE_82 };
    //@formatter:on

    @FXML
    private FlowPane machineOverviewContainer;

    @Inject
    private ViewManager viewManager;

    @Inject
    private CacheManager cacheManager;

    @Inject
    private ReportingCacheDataProvider dataProvider;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool( 1 );
    private ScheduledFuture<Void> scheduleFuture;

    private final List<GaugeOverviewMachine> machineOverviewList = new ArrayList<>();
    private CacheListener cacheListener;

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        for ( String costCenter : dashboardCostCenters )
        {
            GaugeOverviewMachine machineOverview = new GaugeOverviewMachine();
            machineOverview.setCostCenter( costCenter );
            machineOverviewList.add( machineOverview );
            machineOverviewContainer.getChildren().add( machineOverview );

            configureMachineOverview( machineOverview, costCenter );
        }

        cacheListener = key -> {
            if ( scheduleFuture != null && !scheduleFuture.isDone() )
            {
                return;
            }
            scheduleFuture = scheduler.schedule( () -> {
                Platform.runLater( () -> beforeShown( null ) );
                return null;
            }, 1, TimeUnit.SECONDS );
        };
    }

    @Override
    public void beforeShown( View view )
    {
        machineOverviewList.forEach( machineOverview -> setDataOnMachineOverview( machineOverview, machineOverview.getCostCenter() ) );
    }

    @Override
    public void activateView( View view )
    {
        cacheManager.addCacheListener( Casting.CACHE.REPORTING_CACHE_NAME, Casting.CACHE.REPORTING_SUMMARY_GAUGE_KEY, cacheListener );
    }

    @Override
    public void deactivateView( View view )
    {
        cacheManager.removeCacheListener( cacheListener );
    }

    private void configureMachineOverview( GaugeOverview gaugeOverview, String machine )
    {
        int goal = 0;
        Map<String, Integer> kpiOutputTargets = dataProvider.getKPIOutputTargets( machine );
        if ( kpiOutputTargets != null && kpiOutputTargets.containsKey( "shift" ) )
        {
            goal = kpiOutputTargets.get( "shift" );
        }

        gaugeOverview.setViewManager( viewManager );
        gaugeOverview.setPaneTitle( ( machine.endsWith( "0" ) ? "Anlage " : "Ofen " ) + machine );
        gaugeOverview.getOutputGauge().initOutputLayout( goal );
        gaugeOverview.addGauges();
    }

    private void setDataOnMachineOverview( GaugeOverview gaugeOverview, String costCenter )
    {
        ReportingGaugeSummaryDTO gaugeSummaryDTO = dataProvider.getGaugeSummaryFinishing( Casting.REPORTING.LAST_24H, new String[] { costCenter } );
        gaugeOverview.setDowntimeValue( gaugeSummaryDTO.getDowntimeValue() );
        gaugeOverview.setOutputValue( gaugeSummaryDTO.getOutputValue() );
        if ( gaugeOverview instanceof GaugeOverviewMachine )
        {
            ( (GaugeOverviewMachine) gaugeOverview ).setHasDowntime( gaugeSummaryDTO.hasDowntime() );
        }
    }
}
