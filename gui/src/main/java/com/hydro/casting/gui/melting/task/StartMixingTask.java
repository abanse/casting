package com.hydro.casting.gui.melting.task;

import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.common.constant.MelterStep;

public class StartMixingTask extends AbstractMeltingStepTask
{
    public StartMixingTask()
    {
        setMeltingStep( MelterStep.Mixing );
    }

    @Override
    public String getId()
    {
        return SecurityCasting.MELTING.ACTION.MIXING;
    }
}
