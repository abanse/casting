package com.hydro.casting.gui.gantt;

import com.hydro.casting.common.Casting;
import com.hydro.casting.common.constant.CasterStep;
import com.hydro.casting.common.constant.FurnaceStep;
import com.hydro.casting.common.constant.MelterStep;
import com.hydro.casting.server.contract.downtime.dto.DowntimeDTO;
import com.hydro.casting.server.contract.dto.TimeManagementDTO;
import com.hydro.casting.server.contract.dto.TimeManagementPhaseDTO;
import com.hydro.core.common.util.DateTimeUtil;
import com.hydro.core.gui.comp.chart.GanttChartWithDuration;
import com.hydro.core.server.contract.workplace.dto.ViewDTO;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
public abstract class GanttChartUtil
{
    public static void updateXYChartData( XYChart.Series<LocalDateTime, String> series, LocalDateTime startTime, String category, String name, Duration duration, Color color, String tooltip,
            ViewDTO modelElement )
    {
        XYChart.Data<LocalDateTime, String> xyChartData;
        for ( XYChart.Data<LocalDateTime, String> datum : series.getData() )
        {
            GanttChartWithDuration.ExtraData extraData = (GanttChartWithDuration.ExtraData) datum.getExtraValue();
            if ( extraData.getModelElement().equals( modelElement ) && extraData.getName().endsWith( name ) && extraData.getTooltip().equals( tooltip ) && extraData.getColor().equals( color ) )
            {
                extraData.setDuration( duration );
                datum.setXValue( startTime );
                return;
            }
        }
        xyChartData = new XYChart.Data<>( startTime, category, new GanttChartWithDuration.ExtraData( duration, color, name, tooltip, modelElement ) );
        series.getData().add( xyChartData );
    }

    public static String calcTooltip( DowntimeDTO downtime )
    {
        final LocalDateTime start = downtime.getFromTS();
        final LocalDateTime end = downtime.getEndTS();

        final StringBuilder stb = new StringBuilder();
        stb.append( downtime.getDescription() );
        stb.append( System.lineSeparator() );
        stb.append( "Start " ).append( start.format( DateTimeFormatter.ofPattern( "dd.MM.yy HH:mm:ss" ) ) );
        stb.append( System.lineSeparator() );
        stb.append( "Ende " ).append( end.format( DateTimeFormatter.ofPattern( "dd.MM.yy HH:mm:ss" ) ) );
        stb.append( System.lineSeparator() );
        stb.append( "Dauer " ).append( DateTimeUtil.formatDuration( start, end ) );

        return stb.toString();
    }

    public static String calcTooltip( TimeManagementDTO timeManagement, TimeManagementPhaseDTO phase, LocalDateTime start, Duration duration )
    {
        return calcTooltip( timeManagement, phase, start, duration, null );
    }

    public static String calcTooltip( TimeManagementDTO timeManagement, TimeManagementPhaseDTO phase, LocalDateTime start, Duration duration, String prefix )
    {
        String phaseName = phase.getName();
        if ( timeManagement.getMachine() != null && Arrays.asList( Casting.ALL_MELTERS ).contains( timeManagement.getMachine() ) )
        {
            final MelterStep melterStep = MelterStep.findByShortName( phase.getName() );
            if ( melterStep != null )
            {
                phaseName = melterStep.getDescription();
            }
        }
        else if ( timeManagement.getMachine() != null && Arrays.asList( Casting.ALL_MELTING_FURNACES ).contains( timeManagement.getMachine() ) )
        {
            final FurnaceStep furnaceStep = FurnaceStep.findByShortName( phase.getName() );
            if ( furnaceStep != null )
            {
                phaseName = furnaceStep.getDescription();
            }
        }
        else
        {
            final CasterStep casterStep = CasterStep.findByShortName( phase.getName() );
            if ( casterStep != null )
            {
                phaseName = casterStep.getDescription();
            }
        }
        final StringBuilder stb;
        if ( prefix != null )
        {
            stb = new StringBuilder( prefix + " " + timeManagement.getCharge() + " " + phaseName );
        }
        else
        {
            stb = new StringBuilder( timeManagement.getCharge() + " " + phaseName );
        }
        if ( start != null )
        {
            stb.append( "\n" );
            stb.append( "Start " ).append( start.format( DateTimeFormatter.ofPattern( "dd.MM.yy HH:mm:ss" ) ) );

            if ( duration != null )
            {
                stb.append( "\n" );
                stb.append( "Ende " ).append( start.plus( duration ).format( DateTimeFormatter.ofPattern( "dd.MM.yy HH:mm:ss" ) ) );
                stb.append( "\n" );
                stb.append( "Dauer " ).append( DateTimeUtil.formatDuration( start, start.plus( duration ) ) );
            }
        }
        return stb.toString();
    }

    public static Color calcColor( TimeManagementDTO timeManagement, TimeManagementPhaseDTO phase )
    {
        Color color = Color.ORANGE;

        if ( timeManagement.getMachine() != null && Arrays.asList( Casting.ALL_MELTERS ).contains( timeManagement.getMachine() ) )
        {
            final MelterStep melterStep = MelterStep.findByShortName( phase.getName() );
            if ( melterStep != null )
            {
                color = Color.web( melterStep.getColor() );
            }
        }
        else if ( timeManagement.getMachine() != null && Arrays.asList( Casting.ALL_MELTING_FURNACES ).contains( timeManagement.getMachine() ) )
        {
            final FurnaceStep furnaceStep = FurnaceStep.findByShortName( phase.getName() );
            if ( furnaceStep != null )
            {
                color = Color.web( furnaceStep.getColor() );
            }
        }
        else
        {
            final CasterStep casterStep = CasterStep.findByShortName( phase.getName() );
            if ( casterStep != null )
            {
                color = Color.web( casterStep.getColor() );
            }
        }

        return color;
    }
}
