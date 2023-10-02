package com.hydro.casting.common.downtime;

import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;

public interface DowntimeConstants
{
    interface DOWNTIME_TYPE
    {
        String AUTO = "Automatisch";
        String MANUAL = "Manuell";
    }

    interface INPUT_ERRORS
    {
        int NO_ERROR = 0;
        int REQUIRED_FIELD_ERROR = 1;
        int TIME_OVERLAP_ERROR = 2;
        int DATE_SEQUENCE_ERROR = 3;
        int TOO_LATE_ERROR = 4;
        int BUSINESS_EXCEPTION = 5;
//        int SHIFT_BORDER_VIOLATION = 6;
    }

    interface REPLICATION
    {
        int HOURS_TO_READ = 48;
        int DAYS_TO_READ = 7;
        //@formatter:off
        String[] REPLICATED_COST_CENTERS = new String[] {
                Casting.MACHINE.CASTER_50 + "-00", // Allgemein
                Casting.MACHINE.CASTER_50,
                Casting.MACHINE.MELTING_FURNACE_51,
                Casting.MACHINE.MELTING_FURNACE_52,
                Casting.MACHINE.CASTER_60 + "-00", // Allgemein
                Casting.MACHINE.CASTER_60,
                Casting.MACHINE.MELTING_FURNACE_61,
                Casting.MACHINE.MELTING_FURNACE_62,
                Casting.MACHINE.CASTER_70 + "-00", // Allgemein
                Casting.MACHINE.CASTER_70,
                Casting.MACHINE.MELTING_FURNACE_71,
                Casting.MACHINE.MELTING_FURNACE_72,
                Casting.MACHINE.CASTER_80 + "-00", // Allgemein
                Casting.MACHINE.CASTER_80,
                Casting.MACHINE.MELTING_FURNACE_81,
                Casting.MACHINE.MELTING_FURNACE_82,
                Casting.MACHINE.MELTING_FURNACE_S1,
                Casting.MACHINE.MELTING_FURNACE_S2
        };
        //@formatter:off
    }

    interface ACTION_ID
    {
        String CREATE = SecurityCasting.DOWNTIME.ACTION.CREATE;
        String ADD = SecurityCasting.DOWNTIME.HISTORY.ADD;
        String EDIT = SecurityCasting.DOWNTIME.HISTORY.EDIT;
        String DELETE = SecurityCasting.DOWNTIME.HISTORY.DELETE;
        String ADDITIONAL_DESCRIPTION = SecurityCasting.DOWNTIME.HISTORY.ADDITIONAL_DESCRIPTION;
    }
}
