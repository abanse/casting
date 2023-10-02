package com.hydro.casting.gui.melting.task;

import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.common.constant.MelterStep;

public class EndPouringTask extends AbstractMeltingStepTask
{
    public EndPouringTask()
    {
        // Only applicable for S2
        setMachineApk( Casting.MACHINE.MELTING_FURNACE_S2 );
        setFinishTask( true );
        setMeltingStep( MelterStep.Pouring );
    }

    @Override
    public String getId()
    {
        return SecurityCasting.MELTING.ACTION.END_POURING;
    }
}
