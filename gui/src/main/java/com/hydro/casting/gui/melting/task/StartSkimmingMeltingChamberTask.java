package com.hydro.casting.gui.melting.task;

import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.common.constant.MelterStep;
public class StartSkimmingMeltingChamberTask extends AbstractMeltingStepTask
{
    public StartSkimmingMeltingChamberTask()
    {
        setMeltingStep( MelterStep.SkimmingMeltingChamber );
    }

    @Override
    public String getId()
    {
        return SecurityCasting.MELTING.ACTION.SKIMMING_MELTING_CHAMBER;
    }
}
