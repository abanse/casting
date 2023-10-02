package com.hydro.casting.server.ejb.shift.service;


import com.hydro.casting.server.model.shift.ShiftAssignment;
import com.hydro.casting.server.model.shift.ShiftRecord;
import com.hydro.casting.server.model.shift.dao.ShiftAssignmentHome;
import com.hydro.casting.server.model.shift.dao.ShiftRecordHome;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class ShiftService
{

    @EJB
    private ShiftAssignmentHome shiftAssignmentHome;

    @EJB
    private ShiftRecordHome shiftRecordHome;

    public String calculateShift( String costCenter, int time )
    {
        List<ShiftRecord> shiftRecords = getShiftRecordsForCostCenter( costCenter );
        if ( shiftRecords == null )
        {
            return null;
        }
        for ( ShiftRecord shiftRecord : shiftRecords )
        {
            if ( shiftRecord.getStartTime() < shiftRecord.getEndTime() )
            {
                if ( shiftRecord.getStartTime() <= time && shiftRecord.getEndTime() >= time )
                {
                    return String.valueOf( shiftRecord.getShiftNumber() );
                }
            }
            else
            {
                if ( shiftRecord.getStartTime() <= time || shiftRecord.getEndTime() >= time )
                {
                    return String.valueOf( shiftRecord.getShiftNumber() );
                }
            }
        }

        return null;
    }

    public List<ShiftRecord> getShiftRecordsForCostCenter( String costCenter )
    {
        List<ShiftRecord> result = null;
        final ShiftAssignment shiftAssignment = shiftAssignmentHome.findByCostCenter( costCenter );
        if ( shiftAssignment != null )
        {
            final String shiftType = shiftAssignment.getShiftType();
            result = shiftRecordHome.findShiftRecordsByShiftType( shiftType );
        }
        return result;
    }
}
