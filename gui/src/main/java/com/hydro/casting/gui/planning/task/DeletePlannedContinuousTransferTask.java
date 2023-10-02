package com.hydro.casting.gui.planning.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.model.Batch;
import com.hydro.casting.gui.model.CastingClientModel;
import com.hydro.casting.gui.model.ModelElement;
import com.hydro.casting.gui.model.TransferMaterial;
import com.hydro.casting.gui.planning.gantt.model.CGElement;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.planning.CasterScheduleBusiness;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.ClientModelManager;
import com.hydro.core.gui.model.ClientModel;
import com.hydro.core.gui.task.AbstractTask;

public class DeletePlannedContinuousTransferTask extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;

    @Inject
    private ClientModelManager clientModelManager;

    //private BaseGanttChart ganttChart;

    private CGElement transferMaterialElement;

    @Override
    public String getId()
    {
        return SecurityCasting.MABE.ACTION.EDIT_ENTRY;
    }

    /*
    public BaseGanttChart getGanttChart()
    {
        return ganttChart;
    }

    public void setGanttChart( BaseGanttChart ganttChart )
    {
        this.ganttChart = ganttChart;
    }

     */

    public void setData( CGElement transferMaterialElement )
    {
        this.transferMaterialElement = transferMaterialElement;
    }

    @Override
    public void doWork() throws Exception
    {
        final ModelElement targetModelElement = transferMaterialElement.getElement();
        if ( !( targetModelElement instanceof TransferMaterial ) )
        {
            return;
        }
        final TransferMaterial transferMaterial = (TransferMaterial) targetModelElement;
        final Batch batch = transferMaterial.getTransfer().getBatch();

        final ClientModel casterModel = clientModelManager.getClientModel( CastingClientModel.ID );
        final CasterScheduleDTO casterScheduleDTO = casterModel.getEntity( CasterScheduleDTO.class, batch.getObjid() );

        final CasterScheduleBusiness casterScheduleBusiness = businessManager.getSession( CasterScheduleBusiness.class );
        casterScheduleBusiness.deletePlannedConsumedMaterial( casterScheduleDTO, transferMaterial.getObjid() );

        /*
        Platform.runLater( () -> {
            transferMaterial.getTransfer().removeTransferMaterial( transferMaterial );
            ganttChart.rebuild();
        } );
         */
    }
}
