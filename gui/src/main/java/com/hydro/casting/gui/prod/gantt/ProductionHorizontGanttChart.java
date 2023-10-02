package com.hydro.casting.gui.prod.gantt;

import com.hydro.casting.gui.gantt.GeneralGanttChart;
import com.hydro.core.common.util.DateTimeUtil;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;

import java.time.Duration;
import java.time.LocalDateTime;
public class ProductionHorizontGanttChart extends GeneralGanttChart
{
    private int horizontHours = 24;
    private ScrollBar horizontScrollBar = new ScrollBar();

    public ProductionHorizontGanttChart()
    {
        setWithFuture( true );
        chart.setShowCurrentTime( true );
        horizontScrollBar.setOrientation( Orientation.HORIZONTAL );
        horizontScrollBar.valueProperty().addListener( ( observable, oldValue, newValue ) -> {
            if ( newValue != null )
            {
                final LocalDateTime horizontCenter = DateTimeUtil.asLocalDateTime( newValue.longValue() );

                final LocalDateTime[] horizontRange = new LocalDateTime[] { horizontCenter.minusHours( horizontHours / 2 ), horizontCenter.plusHours( horizontHours / 2 ) };
                xAxis.setRange( horizontRange, false );
            }
        } );

        setBottom( horizontScrollBar );
    }

    public int getHorizontHours()
    {
        return horizontHours;
    }

    public void setHorizontHours( int horizontHours )
    {
        this.horizontHours = horizontHours;
    }

    public void reload()
    {
        final LocalDateTime[] range = reloadInternal();
        if ( range != null )
        {
            final LocalDateTime from = range[0];
            final LocalDateTime to = LocalDateTime.now().plusHours( 6 );

            horizontScrollBar.setMin( DateTimeUtil.asTimeMillis( from ) );
            horizontScrollBar.setMax( DateTimeUtil.asTimeMillis( to ) );
            horizontScrollBar.setValue( DateTimeUtil.asTimeMillis( LocalDateTime.now().minusHours( 10 ) ) );
            horizontScrollBar.setVisibleAmount( Duration.ofHours( 24 ).toMillis() );
        }
    }

}
