package com.hydro.casting.gui.planning.task;

import com.google.inject.Inject;
import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.model.*;
import com.hydro.casting.gui.planning.gantt.model.CGElement;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.planning.CasterScheduleBusiness;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.ClientModelManager;
import com.hydro.core.gui.model.ClientModel;
import com.hydro.core.gui.task.AbstractTask;

public class CreatePlannedContinuousTransferTask extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;

    @Inject
    private ClientModelManager clientModelManager;

    //private BaseGanttChart ganttChart;

    private CGElement targetElement;
    private String source;

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

    public void setData( CGElement targetElement, String source )
    {
        this.targetElement = targetElement;
        this.source = source;
    }

    @Override
    public void doWork() throws Exception
    {
        final ModelElement targetModelElement = targetElement.getElement();
        if ( !( targetModelElement instanceof Transfer ) )
        {
            return;
        }
        final Transfer transfer = (Transfer) targetModelElement;
        final Batch batch = transfer.getBatch();

        final ClientModel casterModel = clientModelManager.getClientModel( CastingClientModel.ID );
        final CasterScheduleDTO casterScheduleDTO = casterModel.getEntity( CasterScheduleDTO.class, batch.getObjid() );
        final double weight;
        if ( source.equals( Casting.ALLOY_SOURCES.ELEKTROLYSE ) )
        {
            weight = 4500;
        }
        else
        {
            weight = 8500;
        }

        final CasterScheduleBusiness casterScheduleBusiness = businessManager.getSession( CasterScheduleBusiness.class );
        final long plannedConsumedMaterialOID = casterScheduleBusiness.createPlannedConsumedMaterial( casterScheduleDTO, source, weight );

        /*
        Platform.runLater( () -> {
            final TransferMaterial transferMaterial = new TransferMaterial();
            transferMaterial.setObjid( plannedConsumedMaterialOID );
            transferMaterial.setType( source );
            transferMaterial.addWeight( weight );
            transfer.addTransferMaterial( transferMaterial );

            ganttChart.rebuild();
        } );
         */
    }
}
