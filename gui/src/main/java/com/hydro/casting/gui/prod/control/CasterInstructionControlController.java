package com.hydro.casting.gui.prod.control;

import com.hydro.casting.server.contract.dto.CasterInstructionDTO;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.core.gui.binding.BeanPathAdapter;
import com.hydro.core.gui.comp.DoubleTextField;
import com.hydro.core.gui.comp.StringTextField;
import javafx.fxml.FXML;

public class CasterInstructionControlController
{
    private static CasterInstructionDTO EMPTY_BEAN = new CasterInstructionDTO();

    @FXML
    private StringTextField charge;
    @FXML
    private StringTextField furnace;
    @FXML
    private StringTextField alloy;
    @FXML
    private StringTextField castingProgram;
    @FXML
    private DoubleTextField castingLength;
    @FXML
    private DoubleTextField plannedWeight;
    @FXML
    private DoubleTextField netWeight;

    private BeanPathAdapter<CasterInstructionDTO> beanPathAdapter;

    /**
     * Initializes the controller class.
     */
    @FXML
    private void initialize()
    {
        beanPathAdapter = new BeanPathAdapter<>( EMPTY_BEAN );

        beanPathAdapter.bindBidirectional( "chargeWithoutYear", charge.textProperty() );
        beanPathAdapter.bindBidirectional( "furnace", furnace.textProperty() );
        beanPathAdapter.bindBidirectional( "alloy", alloy.textProperty() );
        beanPathAdapter.bindBidirectional( "castingProgram", castingProgram.textProperty() );
        beanPathAdapter.bindBidirectional( "plannedWeightT", plannedWeight.doubleValueProperty() );
    }

    public void load( CasterInstructionDTO data, CasterScheduleDTO casterScheduleDTO )
    {
        if ( data != null )
        {
            beanPathAdapter.setBean( data );
        }
        else
        {
            beanPathAdapter.setBean( EMPTY_BEAN );
        }
        if ( casterScheduleDTO != null )
        {
            netWeight.setDoubleValue( casterScheduleDTO.getNetWeight() / 1000.0 );
            castingLength.setDoubleValue( casterScheduleDTO.getPlannedLength() );
        }
        else
        {
            netWeight.setDoubleValue( 0 );
            castingLength.setDoubleValue( 0 );
        }
    }
}
