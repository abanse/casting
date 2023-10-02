package com.hydro.casting.server.ejb.shift;

import com.hydro.casting.server.contract.shift.ShiftBusiness;
import com.hydro.casting.server.contract.shift.dto.ShiftModelDTO;
import com.hydro.casting.server.ejb.shift.service.ShiftService;
import com.hydro.casting.server.model.shift.ShiftRecord;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class ShiftBusinessBean implements ShiftBusiness
{
    @EJB
    private ShiftService shiftService;

    @Override
    public List<ShiftModelDTO> loadShiftModels( List<String> costCenters )
    {
        List<ShiftModelDTO> shiftModels = new ArrayList<>();
        for ( String costCenter : costCenters )
        {
            List<ShiftRecord> shiftRecords = shiftService.getShiftRecordsForCostCenter( costCenter );
            ShiftModelDTO shiftModel = new ShiftModelDTO();
            shiftModel.setCostCenter( costCenter );

            if ( shiftRecords != null )
            {
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
}
