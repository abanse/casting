package com.hydro.casting.gui.prod.control;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.gui.prod.gantt.ProductionHorizontGanttChart;
import com.hydro.casting.gui.prod.table.ChargingTable;
import com.hydro.casting.server.contract.dto.TimeManagementDTO;
import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CastingProductionGanttDetailController
{
    private final static Logger log = LoggerFactory.getLogger( CastingProductionGanttDetailController.class );

    @Inject
    private Injector injector;

    @FXML
    private ProductionHorizontGanttChart productionGanttChart;

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        injector.injectMembers( productionGanttChart );
    }

    public String getMachineApk()
    {
        return null;
    }

    public void setMachineApk( String machineApk )
    {
        productionGanttChart.initialize( Collections.singletonList( machineApk ) );
    }

    public void reload()
    {
        log.info( "reload gantt detail" );

//        // FIXME only for testing
//        final InputStream resourceAsStream = ChargingTable.class.getResourceAsStream( "/com/hydro/casting/gui/data/timeManagements.json" );
//        Gson gson = new Gson();
//        Type timeManagementListType = new TypeToken<ArrayList<TimeManagementDTO>>()
//        {
//        }.getType();
//        final List<TimeManagementDTO> timeManagements = gson.fromJson( new InputStreamReader( resourceAsStream ), timeManagementListType );
//        productionGanttChart.loadData( timeManagements );

        LocalDateTime fromTS = LocalDateTime.now().minusDays( 3 );
        LocalDateTime toTS = LocalDateTime.now().plusHours( 1 );
        productionGanttChart.doLoad( fromTS, toTS );
    }
}
