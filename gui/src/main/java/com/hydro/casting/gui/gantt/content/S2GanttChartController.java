package com.hydro.casting.gui.gantt.content;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.gui.gantt.ProcessStepsGanttChart;
import com.hydro.core.common.util.DateTimeUtil;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.View;
import com.hydro.core.gui.comp.DateTimePicker;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
public class S2GanttChartController
{
    @Inject
    private Injector injector;
    @Inject
    private NotifyManager notifyManager;

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
    private ProcessStepsGanttChart processStepsGanttChart;

    private final ToggleGroup toggleGroup = new ToggleGroup();

    @FXML
    void initialize()
    {
        injector.injectMembers( processStepsGanttChart );

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
        processStepsGanttChart.initialize( machines );
    }

    public void beforeShown( View view )
    {
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
    public void reload()
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
        processStepsGanttChart.doLoad( fromTS, toTS );
    }
}
