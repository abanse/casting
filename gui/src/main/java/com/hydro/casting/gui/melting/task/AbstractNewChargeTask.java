package com.hydro.casting.gui.melting.task;

import com.google.inject.Inject;
import com.hydro.casting.gui.melting.dialog.NewMeltingChargeDialog;
import com.hydro.casting.gui.melting.dialog.result.NewMeltingChargeResult;
import com.hydro.casting.gui.melting.util.MeltingRelevantAlloyUtil;
import com.hydro.core.gui.CacheManager;
import com.hydro.core.gui.task.AbstractTask;

import java.util.List;
public abstract class AbstractNewChargeTask extends AbstractTask
{
    @Inject
    private CacheManager cacheManager;
    private NewMeltingChargeResult newMeltingChargeResult;

    private String machineApk;

    protected NewMeltingChargeResult getNewMeltingChargeResult()
    {
        return newMeltingChargeResult;
    }

    public String getMachineApk()
    {
        return machineApk;
    }

    public void setMachineApk( String machineApk )
    {
        this.machineApk = machineApk;
    }

    @Override
    public boolean beforeStart() throws Exception
    {
        List<String> alloyList = MeltingRelevantAlloyUtil.getMeltingRelevantAlloys( cacheManager );
        newMeltingChargeResult = NewMeltingChargeDialog.showDialog( alloyList );
        return newMeltingChargeResult != null;
    }
}
