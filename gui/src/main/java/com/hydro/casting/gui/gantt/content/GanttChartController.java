package com.hydro.casting.gui.gantt.content;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.gui.gantt.GeneralGanttChart;
import com.hydro.core.common.util.DateTimeUtil;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.View;
import com.hydro.core.gui.comp.DateTimePicker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class GanttChartController
{
    private final static Logger log = LoggerFactory.getLogger( GanttChartController.class );

    @Inject
    private NotifyManager notifyManager;
    @Inject
    private Injector injector;

    @FXML
    public ToggleButton last24h;
    @FXML
    public ToggleButton yesterday;
    @FXML
    public ToggleButton timeRange;
    @FXML
    public Label fromLabel;
    @FXML
    public DateTimePicker from;
    @FXML
    public Label toLabel;
    @FXML
    public DateTimePicker to;

    @FXML
    private GeneralGanttChart generalGanttChart;

    private List<String> machines;
    private boolean initialized = false;

    private ToggleGroup toggleGroup = new ToggleGroup();

    @FXML
    void initialize()
    {
        injector.injectMembers( generalGanttChart );

        last24h.setToggleGroup( toggleGroup );
        yesterday.setToggleGroup( toggleGroup );
        timeRange.setToggleGroup( toggleGroup );
        fromLabel.disableProperty().bind( timeRange.selectedProperty().not() );
        from.disableProperty().bind( timeRange.selectedProperty().not() );
        toLabel.disableProperty().bind( timeRange.selectedProperty().not() );
        to.disableProperty().bind( timeRange.selectedProperty().not() );
    }

    public void initialize( List<String> machines )
    {
        this.machines = machines;

        generalGanttChart.initialize( machines );
    }

    public void beforeShown( View view )
    {
        if ( !initialized )
        {
            initialized = true;
        }
    }

    public void activateView( View view )
    {
    }

    public void deactivateView( View view )
    {
    }

    public boolean handleFunctionKey( KeyCode keyCode, boolean isShiftDown )
    {
        return false;
    }

    @FXML
    public void reload( ActionEvent actionEvent )
    {
        LocalDateTime fromTS = null;
        LocalDateTime toTS = null;
        if ( last24h.isSelected() )
        {
            fromTS = LocalDateTime.now().minusHours( 24 );
            toTS = LocalDateTime.now();
        }
        else if ( yesterday.isSelected() )
        {
            fromTS = LocalDate.now().minusDays( 1 ).atStartOfDay();
            toTS = LocalDate.now().atStartOfDay();
        }
        else if ( timeRange.isSelected() )
        {
            fromTS = from.getDateTime();
            if ( fromTS == null )
            {
                fromTS = DateTimeUtil.asLocalDateTime( from.getDate() );
            }
            toTS = to.getDateTime();
            if ( toTS == null )
            {
                toTS = DateTimeUtil.asLocalDateTime( to.getDate() );
            }
            if ( toTS == null && fromTS != null )
            {
                toTS = fromTS.plusDays( 1 );
            }
        }
        if ( fromTS == null )
        {
            notifyManager.showSplashMessage( "Suchkriterium nicht besetzt" );
            return;
        }
        generalGanttChart.doLoad( fromTS, toTS );
    }
}
