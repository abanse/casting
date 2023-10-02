package com.hydro.casting.server.ejb.reporting.data;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.shift.dto.ShiftModelDTO;
import com.hydro.casting.server.model.shift.ShiftRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This class holds and provides the ShiftModelDTOs for the different cost centers.
 */
@Singleton
public class ShiftTimeDataProvider
{
    private final static Logger log = LoggerFactory.getLogger( ShiftTimeDataProvider.class );

    @EJB
    ShiftCalculator shiftCalculator;

    private ShiftModelDTO caster50ShiftModel = null;
    private ShiftModelDTO caster60ShiftModel = null;
    private ShiftModelDTO caster70ShiftModel = null;
    private ShiftModelDTO caster80ShiftModel = null;
    private ShiftModelDTO meltingFurnace51ShiftModel = null;
    private ShiftModelDTO meltingFurnace52ShiftModel = null;
    private ShiftModelDTO meltingFurnace61ShiftModel = null;
    private ShiftModelDTO meltingFurnace62ShiftModel = null;
    private ShiftModelDTO meltingFurnace71ShiftModel = null;
    private ShiftModelDTO meltingFurnace72ShiftModel = null;
    private ShiftModelDTO meltingFurnace81ShiftModel = null;
    private ShiftModelDTO meltingFurnace82ShiftModel = null;

    private boolean dataLoaded = false;

    public ShiftTimeDataProvider()
    {
    }

    public void loadShiftModels()
    {
        if ( !dataLoaded )
        {
            List<String> costCenters = new ArrayList<>();
            costCenters.add( Casting.MACHINE.CASTER_50 );
            costCenters.add( Casting.MACHINE.CASTER_60 );
            costCenters.add( Casting.MACHINE.CASTER_70 );
            costCenters.add( Casting.MACHINE.CASTER_80 );
            costCenters.add( Casting.MACHINE.MELTING_FURNACE_51 );
            costCenters.add( Casting.MACHINE.MELTING_FURNACE_52 );
            costCenters.add( Casting.MACHINE.MELTING_FURNACE_61 );
            costCenters.add( Casting.MACHINE.MELTING_FURNACE_62 );
            costCenters.add( Casting.MACHINE.MELTING_FURNACE_71 );
            costCenters.add( Casting.MACHINE.MELTING_FURNACE_72 );
            costCenters.add( Casting.MACHINE.MELTING_FURNACE_81 );
            costCenters.add( Casting.MACHINE.MELTING_FURNACE_82 );
            List<ShiftModelDTO> data = loadShiftModels( costCenters );
            loadData( data );
            dataLoaded = true;
        }
    }

    public List<ShiftModelDTO> loadShiftModels( List<String> costCenters )
    {
        List<ShiftModelDTO> shiftModels = new ArrayList<ShiftModelDTO>();
        for ( String costCenter : costCenters )
        {
            List<ShiftRecord> shiftRecords = shiftCalculator.getShiftRecordsForCostCenter( costCenter );
            if ( shiftRecords == null )
            {
                log.warn( "no shift configuration for " + costCenter );
                continue;
            }

            ShiftModelDTO shiftModel = new ShiftModelDTO();
            shiftModel.setCostCenter( costCenter );

            for ( ShiftRecord shiftRecord : shiftRecords )
            {
                LocalTime startTime = getLocalTimeOfHoursAndMinutes( shiftRecord.getStartTime() );
                LocalTime endTime = getLocalTimeOfHoursAndMinutes( shiftRecord.getEndTime() );
                switch ( shiftRecord.getShiftNumber() )
                {
                case "1":
                    shiftModel.setShift1Start( startTime );
                    shiftModel.setShift1End( endTime );
                    break;
                case "2":
                    shiftModel.setShift2Start( startTime );
                    shiftModel.setShift2End( endTime );
                    break;
                case "3":
                    shiftModel.setShift3Start( startTime );
                    shiftModel.setShift3End( endTime );
                    break;

                default:
                    break;
                }
            }
            if ( !isDTOValid( shiftModel ) )
            {
                shiftModel = null;
            }
            shiftModels.add( shiftModel );
        }
        return shiftModels;
    }

    private LocalTime getLocalTimeOfHoursAndMinutes( int hoursAndMinutes )
    {
        return LocalTime.of( hoursAndMinutes / 100, hoursAndMinutes % 100 );
    }

    private boolean isDTOValid( ShiftModelDTO shiftModel )
    {
        boolean valid = true;
        if ( shiftModel.getShift1Start() == null || shiftModel.getShift1End() == null || shiftModel.getShift2Start() == null || shiftModel.getShift2End() == null || shiftModel.getShift3Start() == null
                || shiftModel.getShift3End() == null )
        {
            valid = false;
        }
        return valid;
    }

    public void loadData( List<ShiftModelDTO> shiftModelDTOs )
    {
        if ( shiftModelDTOs == null )
        {
            caster50ShiftModel = null;
            caster60ShiftModel = null;
            caster70ShiftModel = null;
            caster80ShiftModel = null;
            meltingFurnace51ShiftModel = null;
            meltingFurnace52ShiftModel = null;
            meltingFurnace61ShiftModel = null;
            meltingFurnace62ShiftModel = null;
            meltingFurnace71ShiftModel = null;
            meltingFurnace72ShiftModel = null;
            meltingFurnace81ShiftModel = null;
            meltingFurnace82ShiftModel = null;
        }
        else
        {
            for ( ShiftModelDTO shiftModel : shiftModelDTOs )
            {
                setShiftModel( shiftModel );
            }
        }
    }

    public ShiftModelDTO getShiftModelForCostCenter( String costCenter )
    {
        ShiftModelDTO shiftModel = null;
        switch ( costCenter )
        {
        case Casting.MACHINE.CASTER_50:
            shiftModel = caster50ShiftModel;
            break;

        case Casting.MACHINE.CASTER_60:
            shiftModel = caster60ShiftModel;
            break;

        case Casting.MACHINE.CASTER_70:
            shiftModel = caster70ShiftModel;
            break;

        case Casting.MACHINE.CASTER_80:
            shiftModel = caster80ShiftModel;
            break;

        case Casting.MACHINE.MELTING_FURNACE_51:
            shiftModel = meltingFurnace51ShiftModel;
            break;

        case Casting.MACHINE.MELTING_FURNACE_52:
            shiftModel = meltingFurnace52ShiftModel;
            break;

        case Casting.MACHINE.MELTING_FURNACE_61:
            shiftModel = meltingFurnace61ShiftModel;
            break;

        case Casting.MACHINE.MELTING_FURNACE_62:
            shiftModel = meltingFurnace62ShiftModel;
            break;

        case Casting.MACHINE.MELTING_FURNACE_71:
            shiftModel = meltingFurnace71ShiftModel;
            break;

        case Casting.MACHINE.MELTING_FURNACE_72:
            shiftModel = meltingFurnace72ShiftModel;
            break;

        case Casting.MACHINE.MELTING_FURNACE_81:
            shiftModel = meltingFurnace81ShiftModel;
            break;

        case Casting.MACHINE.MELTING_FURNACE_82:
            shiftModel = meltingFurnace82ShiftModel;
            break;

        }
        return shiftModel;
    }

    private void setShiftModel( ShiftModelDTO shiftModel )
    {
        switch ( shiftModel.getCostCenter().trim() )
        {
        case Casting.MACHINE.CASTER_50:
            caster50ShiftModel = shiftModel;
            break;

        case Casting.MACHINE.CASTER_60:
            caster60ShiftModel = shiftModel;
            break;

        case Casting.MACHINE.CASTER_70:
            caster70ShiftModel = shiftModel;
            break;

        case Casting.MACHINE.CASTER_80:
            caster80ShiftModel = shiftModel;
            break;

        case Casting.MACHINE.MELTING_FURNACE_51:
            meltingFurnace51ShiftModel = shiftModel;
            break;

        case Casting.MACHINE.MELTING_FURNACE_52:
            meltingFurnace52ShiftModel = shiftModel;
            break;

        case Casting.MACHINE.MELTING_FURNACE_61:
            meltingFurnace61ShiftModel = shiftModel;
            break;

        case Casting.MACHINE.MELTING_FURNACE_62:
            meltingFurnace62ShiftModel = shiftModel;
            break;

        case Casting.MACHINE.MELTING_FURNACE_71:
            meltingFurnace71ShiftModel = shiftModel;
            break;

        case Casting.MACHINE.MELTING_FURNACE_72:
            meltingFurnace72ShiftModel = shiftModel;
            break;

        case Casting.MACHINE.MELTING_FURNACE_81:
            meltingFurnace81ShiftModel = shiftModel;
            break;

        case Casting.MACHINE.MELTING_FURNACE_82:
            meltingFurnace82ShiftModel = shiftModel;
            break;
        }
    }

    public String getCurrentShift( String costCenter )
    {
        String shift = "3";
        ShiftModelDTO shiftModel = getShiftModelForCostCenter( costCenter );
        if ( shiftModel == null )
        {
            return "1";
        }
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
        if ( shiftModel == null )
        {
            return LocalDateTime.of( LocalDate.now(), LocalTime.MIN );
        }
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
        if ( shiftModel == null )
        {
            return LocalTime.MIN;
        }
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
        if ( shiftModel == null )
        {
            return LocalTime.MAX;
        }
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
