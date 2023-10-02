package com.hydro.casting.gui.reporting.data;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hydro.casting.server.contract.shift.ShiftBusiness;
import com.hydro.casting.server.contract.shift.dto.ShiftModelDTO;
import com.hydro.core.gui.BusinessManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class ShiftTimeDataProvider
{
    @Inject
    private BusinessManager businessManager;

    private Map<String, ShiftModelDTO> shiftModels = new HashMap<>();

    public void loadData( List<String> costCenters )
    {
        if ( costCenters != null )
        {
            ShiftBusiness shiftBusiness = businessManager.getSession( ShiftBusiness.class );
            List<ShiftModelDTO> data = shiftBusiness.loadShiftModels( costCenters );
            for ( ShiftModelDTO shiftModel : data )
            {
                if ( shiftModel != null )
                {
                    shiftModels.put( shiftModel.getCostCenter(), shiftModel );
                }
            }
        }
    }

    public ShiftModelDTO getShiftModelForCostCenter( String costCenter )
    {
        ShiftModelDTO shiftModel = null;
        if ( !shiftModels.containsKey( costCenter ) )
        {
            List<String> costCenters = new ArrayList<>();
            costCenters.add( costCenter );
            loadData( costCenters );
        }
        shiftModel = shiftModels.get( costCenter );
        return shiftModel;
    }

    public String getCurrentShift( String costCenter )
    {
        String shift = "3";
        ShiftModelDTO shiftModel = getShiftModelForCostCenter( costCenter );
        LocalTime now = LocalTime.now();

        if ( now.isAfter( shiftModel.getShift1Start() ) && now.isBefore( shiftModel.getShift1End() ) )
        {
            shift = "1";
        }
        else if ( now.isAfter( shiftModel.getShift2Start() ) && now.isBefore( shiftModel.getShift2End() ) )
        {
            shift = "2";
        }
        return shift;
    }

    public LocalDateTime getCurrentShiftStart( String costCenter )
    {
        LocalDateTime shiftStart = null;
        ShiftModelDTO shiftModel = getShiftModelForCostCenter( costCenter );
        String currentShift = getCurrentShift( costCenter );

        if ( currentShift.equals( "1" ) )
        {
            shiftStart = LocalDateTime.of( LocalDate.now(), shiftModel.getShift1Start() );
        }
        else if ( currentShift.equals( "2" ) )
        {
            shiftStart = LocalDateTime.of( LocalDate.now(), shiftModel.getShift2Start() );
        }
        else if ( currentShift.equals( "3" ) )
        {
            LocalDate date = LocalDate.now();
            if ( LocalTime.now().isBefore( shiftModel.getShift3Start() ) )
            {
                date = date.minusDays( 1 );
            }
            shiftStart = LocalDateTime.of( date, shiftModel.getShift3Start() );
        }

        return shiftStart;
    }

    public LocalTime getStartTimeForShift( String costCenter, String shift )
    {
        LocalTime start;
        ShiftModelDTO shiftModel = getShiftModelForCostCenter( costCenter );
        switch ( shift )
        {
        case "1":
            start = shiftModel.getShift1Start();
            break;
        case "2":
            start = shiftModel.getShift2Start();
            break;
        case "3":
            start = shiftModel.getShift3Start();
            break;

        default:
            start = null;
            break;
        }
        return start;
    }

    public LocalTime getEndTimeForShift( String costCenter, String shift )
    {
        LocalTime end;
        ShiftModelDTO shiftModel = getShiftModelForCostCenter( costCenter );
        switch ( shift )
        {
        case "1":
            end = shiftModel.getShift1End();
            break;
        case "2":
            end = shiftModel.getShift2End();
            break;
        case "3":
            end = shiftModel.getShift3End();
            break;

        default:
            end = null;
            break;
        }
        return end;
    }

    public LocalDateTime getEndDateTimeForShift( String costCenter, LocalDateTime baseTime )
    {
        String shift = getShift( costCenter, LocalTime.from( baseTime ) );
        LocalTime endTime = getEndTimeForShift( costCenter, shift );
        LocalDate endDate = LocalDate.from( baseTime );
        if ( shift.equals( "3" ) && endTime.isBefore( LocalTime.from( baseTime ) ) )
        {
            endDate = endDate.plusDays( 1 );
        }
        return LocalDateTime.of( endDate, endTime );
    }

    private String getShift( String costCenter, LocalTime time )
    {
        String shift = "3";
        ShiftModelDTO shiftModel = getShiftModelForCostCenter( costCenter );

        if ( time.equals( shiftModel.getShift1Start() ) || time.equals( shiftModel.getShift1End() ) || ( time.isAfter( shiftModel.getShift1Start() ) && time.isBefore( shiftModel.getShift1End() ) ) )
        {
            shift = "1";
        }
        else if ( time.equals( shiftModel.getShift2Start() ) || time.equals( shiftModel.getShift2End() ) || ( time.isAfter( shiftModel.getShift2Start() ) && time.isBefore(
                shiftModel.getShift2End() ) ) )
        {
            shift = "2";
        }
        return shift;
    }

    public LocalDateTime getLastFinishedShiftEnd( String costCenter, String shift )
    {
        LocalTime endTime = getEndTimeForShift( costCenter, shift );
        LocalDateTime end = LocalDateTime.of( LocalDate.now(), endTime );

        if ( end.isAfter( LocalDateTime.now() ) )
        {
            end = end.minusDays( 1 );
        }

        return end;
    }
}
