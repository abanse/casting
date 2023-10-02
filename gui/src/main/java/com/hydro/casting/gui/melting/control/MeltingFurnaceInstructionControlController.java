package com.hydro.casting.gui.melting.control;

import com.hydro.casting.server.contract.dto.MeltingInstructionDTO;
import com.hydro.core.gui.binding.BeanPathAdapter;
import com.hydro.core.gui.comp.DoubleTextField;
import com.hydro.core.gui.comp.StringTextField;
import javafx.fxml.FXML;

import java.util.Objects;
public class MeltingFurnaceInstructionControlController
{
    private final static MeltingInstructionDTO EMPTY_BEAN = new MeltingInstructionDTO();

    @FXML
    private StringTextField charge;
    @FXML
    private StringTextField alloy;
    @FXML
    private DoubleTextField utilization;

    private BeanPathAdapter<MeltingInstructionDTO> beanPathAdapter;

    @FXML
    public void initialize()
    {
        beanPathAdapter = new BeanPathAdapter<>( EMPTY_BEAN );

        beanPathAdapter.bindBidirectional( "chargeWithoutYear", charge.textProperty() );
        beanPathAdapter.bindBidirectional( "alloy", alloy.textProperty() );
        // beanPathAdapter.bindBidirectional( "utilization", utilization.doubleValueProperty() );
    }

    public void load( MeltingInstructionDTO data )
    {
        beanPathAdapter.setBean( Objects.requireNonNullElse( data, EMPTY_BEAN ) );
    }
}
