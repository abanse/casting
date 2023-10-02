package com.hydro.casting.gui.melting.task;

import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.common.constant.MelterStep;

public class StartDredgingTask extends AbstractMeltingStepTask
{
    public StartDredgingTask()
    {
        setMachineApk( Casting.MACHINE.MELTING_FURNACE_S1 );
        setMeltingStep( MelterStep.Dredging );
    }

    @Override
    public String getId()
    {
        return SecurityCasting.MELTING.ACTION.DREDGING;
    }
}
