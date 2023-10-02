package com.hydro.casting.gui.prod.control;

import com.hydro.casting.server.contract.dto.CasterOccupancyDTO;
import javafx.fxml.FXML;

public class CasterOccupancyControlController
{
    @FXML
    protected CasterOccupancyPosControlController pos1Controller;
    @FXML
    protected CasterOccupancyPosControlController pos2Controller;
    @FXML
    protected CasterOccupancyPosControlController pos3Controller;
    @FXML
    protected CasterOccupancyPosControlController pos4Controller;
    @FXML
    protected CasterOccupancyPosControlController pos5Controller;

    @FXML
    private void initialize()
    {
        pos1Controller.setPosition( 1 );
        pos2Controller.setPosition( 2 );
        pos3Controller.setPosition( 3 );
        pos4Controller.setPosition( 4 );
        if ( pos5Controller != null )
        {
            pos5Controller.setPosition( 5 );
        }
    }

    public void load( CasterOccupancyDTO casterOccupancyDTO )
    {
        if ( casterOccupancyDTO == null )
        {
            pos1Controller.load( null );
            pos2Controller.load( null );
            pos3Controller.load( null );
            pos4Controller.load( null );
            if ( pos5Controller != null )
            {
                pos5Controller.load( null );
            }
        }
        else
        {
            pos1Controller.load( casterOccupancyDTO.getPos1() );
            pos2Controller.load( casterOccupancyDTO.getPos2() );
            pos3Controller.load( casterOccupancyDTO.getPos3() );
            pos4Controller.load( casterOccupancyDTO.getPos4() );
            if ( pos5Controller != null )
            {
                pos5Controller.load( casterOccupancyDTO.getPos5() );
            }
        }
    }
}
