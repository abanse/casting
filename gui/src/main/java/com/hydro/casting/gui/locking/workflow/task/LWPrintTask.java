package com.hydro.casting.gui.locking.workflow.task;

import com.google.inject.Inject;
import com.hydro.casting.common.locking.workflow.LockingWorkflowConstants;
import com.hydro.casting.server.contract.locking.workflow.LockingWorkflowBusiness;
import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.ViewManager;

import java.util.Objects;

public class LWPrintTask extends LWAbstractTask
{
    @Inject
    private BusinessManager businessManager;

    @Inject
    private ViewManager viewManager;

    @Override
    public void doWork() throws Exception
    {
        String costCenter = getTable().getSelectedValue().getKst();
        LockingWorkflowDTO dtoPrint = getTable().getSelectedValue();
        if ( Objects.equals( costCenter, "52" ) || Objects.equals( costCenter, "62" ) || Objects.equals( costCenter, "64" ) || Objects.equals( costCenter, "84" ) )
        {
            dtoPrint.setWeight( dtoPrint.getWeightOut() );
        }
        LockingWorkflowBusiness lockingWorkflowBusiness = businessManager.getSession( LockingWorkflowBusiness.class );
        lockingWorkflowBusiness.print( workJasper( "", dtoPrint, false, LockingWorkflowBusiness.Function.PrintJasper ) );
        viewManager.reloadCurrentView( getId() );
    }

    @Override
    public String getId()
    {
        return LockingWorkflowConstants.ACTION_ID.PRINT;
    }
}
