package com.hydro.casting.gui.melting.control;

import com.google.inject.Injector;
import com.hydro.casting.common.constant.MelterStep;
import com.hydro.casting.gui.control.StatusBox;
import com.hydro.casting.server.contract.dto.MeltingInstructionDTO;
public class MeltingProcessStepStatus extends StatusBox
{
    private MelterStep melterStep;
    private MeltingInstructionDTO meltingInstructionDTO;

    public void connect( Injector injector, MelterStep melterStep )
    {
        injector.injectMembers( this );
        this.melterStep = melterStep;
        load();
    }

    private void load()
    {
        if ( meltingInstructionDTO != null && meltingInstructionDTO.getMeltingBatchOID() != null )
        {
            switch ( melterStep )
            {
            case Melting:
                setActive( meltingInstructionDTO.getMeltingStartTS() != null );
                break;
            case Skimming:
                setActive( meltingInstructionDTO.getSkimmingStartTS() != null );
                break;
            case SkimmingMeltingChamber:
                setActive( meltingInstructionDTO.getSkimmingMeltingChamberStartTS() != null );
                break;
            case Mixing:
                setActive( meltingInstructionDTO.getMixingStartTS() != null );
                break;
            case Pouring:
                setActive( meltingInstructionDTO.getPouringStartTS() != null );
                break;
            default:
                setActive( false );
                break;
            }
            set( melterStep, null, null );
        }
        else
        {
            setEmptyStatus();
        }
    }

    public void reload()
    {
        load();
    }

    public void setMeltingInstructionDTO( MeltingInstructionDTO meltingInstructionDTO )
    {
        this.meltingInstructionDTO = meltingInstructionDTO;
    }
}
