package com.hydro.casting.gui.prod.control;

import com.google.inject.Injector;
import com.hydro.casting.common.Casting;
import com.hydro.casting.common.constant.CasterStep;
import com.hydro.casting.common.constant.FurnaceStep;
import com.hydro.casting.common.constant.MelterStep;
import com.hydro.casting.gui.control.StatusBox;
import com.hydro.casting.gui.model.CastingClientModel;
import com.hydro.casting.server.contract.dto.MachineDTO;
import com.hydro.core.gui.ClientModelManager;
import com.hydro.core.gui.model.ClientModel;

public class EquipmentStatus extends StatusBox
{
    private ClientModel clientModel;

    private String machineApk;

    public void connect( Injector injector, String machineApk )
    {
        this.machineApk = machineApk;
        final ClientModelManager clientModelManager = injector.getInstance( ClientModelManager.class );
        clientModel = clientModelManager.getClientModel( CastingClientModel.ID );
        clientModel.addRelationListener( CastingClientModel.MACHINE, observable -> load() );
        load();
    }

    private void load()
    {
        MachineDTO machineDTO = null;
        if ( clientModel != null )
        {
            machineDTO = clientModel.getEntity( MachineDTO.class, machineApk );
        }
        if ( machineDTO == null )
        {
            setEmptyStatus();
        }
        else
        {
            if ( machineApk.equals( Casting.MACHINE.MELTING_FURNACE_S1 ) || machineApk.equals( Casting.MACHINE.MELTING_FURNACE_S2 ) )
            {
                final MelterStep melterStep = MelterStep.findByShortName( machineDTO.getCurrentStep() );
                set( melterStep, machineDTO.getActiveDowntime(), machineDTO.getCurrentStepDowntime() );
            }
            else if ( machineApk.endsWith( "0" ) )
            {
                final CasterStep casterStep = CasterStep.findByShortName( machineDTO.getCurrentStep() );
                set( casterStep, machineDTO.getActiveDowntime(), machineDTO.getCurrentStepDowntime() );
            }
            else
            {
                final FurnaceStep furnaceStep = FurnaceStep.findByShortName( machineDTO.getCurrentStep() );
                set( furnaceStep, machineDTO.getActiveDowntime(), machineDTO.getCurrentStepDowntime() );
            }
        }
    }
}
