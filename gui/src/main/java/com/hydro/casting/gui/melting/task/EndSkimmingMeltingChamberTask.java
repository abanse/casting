package com.hydro.casting.gui.melting.task;

import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.common.constant.MelterStep;
public class EndSkimmingMeltingChamberTask extends AbstractMeltingStepTask
{
    public EndSkimmingMeltingChamberTask()
    {
        // Only applicable for S2
        setMachineApk( Casting.MACHINE.MELTING_FURNACE_S2 );
        setWithoutMachine( true );
        setFinishTask( true );
        setMeltingStep( MelterStep.SkimmingMeltingChamber );
    }

    @Override
    public String getId()
    {
        return SecurityCasting.MELTING.ACTION.END_SKIMMING_MELTING_CHAMBER;
    }
}
