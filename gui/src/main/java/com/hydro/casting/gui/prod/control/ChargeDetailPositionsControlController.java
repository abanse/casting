package com.hydro.casting.gui.prod.control;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import javafx.fxml.FXML;

public class ChargeDetailPositionsControlController
{
    @FXML
    protected ChargeDetailPositionControlController pos1Controller;
    @FXML
    protected ChargeDetailPositionControlController pos2Controller;
    @FXML
    protected ChargeDetailPositionControlController pos3Controller;
    @FXML
    protected ChargeDetailPositionControlController pos4Controller;
    @FXML
    protected ChargeDetailPositionControlController pos5Controller;

    private CasterScheduleDTO casterScheduleDTO;

    @FXML
    private void initialize()
    {
        pos1Controller.setPosition( 1 );
        pos2Controller.setPosition( 2 );
        pos3Controller.setPosition( 3 );
        pos4Controller.setPosition( 4 );
        pos5Controller.setPosition( 5 );
    }

    public void setCasterScheduleDTO( CasterScheduleDTO casterScheduleDTO )
    {
        this.casterScheduleDTO = casterScheduleDTO;

        if ( casterScheduleDTO == null )
        {
            pos1Controller.setData( null );
            pos2Controller.setData( null );
            pos3Controller.setData( null );
            pos4Controller.setData( null );
            pos5Controller.setData( null );
            return;
        }

        if ( Casting.MACHINE.CASTER_80.equals( casterScheduleDTO.getMachine() ) )
        {
            pos5Controller.setVisible( true );
        }
        else
        {
            pos5Controller.setVisible( false );
        }
        pos1Controller.setData( casterScheduleDTO.getPos1() );
        pos2Controller.setData( casterScheduleDTO.getPos2() );
        pos3Controller.setData( casterScheduleDTO.getPos3() );
        pos4Controller.setData( casterScheduleDTO.getPos4() );
        pos5Controller.setData( casterScheduleDTO.getPos5() );
    }
}
