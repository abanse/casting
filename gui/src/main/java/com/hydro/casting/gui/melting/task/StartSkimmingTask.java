package com.hydro.casting.gui.melting.task;

import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.common.constant.MelterStep;

public class StartSkimmingTask extends AbstractMeltingStepTask
{
    public StartSkimmingTask()
    {
        setMeltingStep( MelterStep.Skimming );
    }

    @Override
    public String getId()
    {
        return SecurityCasting.MELTING.ACTION.SKIMMING;
    }
}
