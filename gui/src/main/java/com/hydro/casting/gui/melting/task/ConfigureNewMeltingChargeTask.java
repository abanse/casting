package com.hydro.casting.gui.melting.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.melting.dialog.ConfigureMeltingChargeDialog;
import com.hydro.casting.gui.melting.dialog.result.ConfigureMeltingChargeResult;
import com.hydro.casting.gui.model.CastingClientModel;
import com.hydro.casting.server.contract.dto.MachineDTO;
import com.hydro.casting.server.contract.melting.MeltingBusiness;
import com.hydro.core.common.util.StringTools;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.ClientModelManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.model.ClientModel;
import com.hydro.core.gui.task.AbstractTask;

public class ConfigureNewMeltingChargeTask extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;
    @Inject
    private NotifyManager notifyManager;
    @Inject
    private ClientModelManager clientModelManager;

    private ConfigureMeltingChargeResult configureMeltingChargeResult;
    private String machine;

    @Override
    public String getId()
    {
        return SecurityCasting.MELTING.ACTION.CONFIGURE_NEW_MELTING_CHARGE;
    }

    public String getMachine()
    {
        return machine;
    }

    public void setMachine( String machine )
    {
        this.machine = machine;
    }

    @Override
    public boolean beforeStart() throws Exception
    {
        ClientModel clientModel = clientModelManager.getClientModel( CastingClientModel.ID );
        MachineDTO melterMachineDTO = clientModel.getEntity( MachineDTO.class, machine );

        configureMeltingChargeResult = ConfigureMeltingChargeDialog.showDialog( machine, melterMachineDTO );
        return configureMeltingChargeResult != null;
    }

    @Override
    public void doWork() throws Exception
    {
        final MeltingBusiness meltingBusiness = businessManager.getSession( MeltingBusiness.class );
        meltingBusiness.changeChargeCounterOnMelter( machine, configureMeltingChargeResult.getChargeCounter() );

        notifyManager.showSplashMessage( "Chargen-Zähler wurde zu " + StringTools.N4F.format( configureMeltingChargeResult.getChargeCounter() ) + " geändert!" );
    }
}
