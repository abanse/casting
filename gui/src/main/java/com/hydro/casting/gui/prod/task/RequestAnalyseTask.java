package com.hydro.casting.gui.prod.task;

import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.prod.dialog.result.StepTimestampResult;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.FurnaceInstructionDTO;
import com.hydro.core.gui.task.AbstractMultiSelectTask;

public class RequestAnalyseTask extends AbstractMultiSelectTask<CasterScheduleDTO>
{
    private String machine;
    private FurnaceInstructionDTO furnaceInstructionDTO;

    private StepTimestampResult stepTimestampResult;

    public String getMachine()
    {
        return machine;
    }

    public void setMachine( String machine )
    {
        this.machine = machine;
    }

    public FurnaceInstructionDTO getFurnaceInstructionDTO()
    {
        return furnaceInstructionDTO;
    }

    public void setFurnaceInstructionDTO( FurnaceInstructionDTO furnaceInstructionDTO )
    {
        this.furnaceInstructionDTO = furnaceInstructionDTO;
    }

    @Override
    public String getId()
    {
        return SecurityCasting.PROD.ACTION.REQUEST_ANALYSE;
    }

    @Override
    public void doWork() throws Exception
    {
    }
}
