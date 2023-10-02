package com.hydro.casting.gui.prod.control;

import com.hydro.casting.server.contract.dto.FurnaceInstructionDTO;
import com.hydro.core.gui.binding.BeanPathAdapter;
import com.hydro.core.gui.comp.DoubleTextField;
import com.hydro.core.gui.comp.StringTextField;
import javafx.fxml.FXML;

public class FurnaceInstructionControlController
{
    private static FurnaceInstructionDTO EMPTY_BEAN = new FurnaceInstructionDTO();

    @FXML
    private StringTextField charge;
    @FXML
    private StringTextField alloy;
    @FXML
    private DoubleTextField plannedWeight;
    @FXML
    private DoubleTextField utilization;
    @FXML
    private DoubleTextField bottomWeight;
    @FXML
    private DoubleTextField solidMetal;
    @FXML
    private DoubleTextField liquidMetal;

    private BeanPathAdapter<FurnaceInstructionDTO> beanPathAdapter;

    /**
     * Initializes the controller class.
     */
    @FXML
    private void initialize()
    {
        beanPathAdapter = new BeanPathAdapter<>( EMPTY_BEAN );

        beanPathAdapter.bindBidirectional( "chargeWithoutYear", charge.textProperty() );
        beanPathAdapter.bindBidirectional( "alloy", alloy.textProperty() );
        beanPathAdapter.bindBidirectional( "plannedWeightT", plannedWeight.doubleValueProperty() );
        beanPathAdapter.bindBidirectional( "utilization", utilization.doubleValueProperty() );
        //beanPathAdapter.bindBidirectional( "bottomWeight", bottomWeight.doubleValueProperty() );
        //beanPathAdapter.bindBidirectional( "solidMetal", solidMetal.doubleValueProperty() );
        //beanPathAdapter.bindBidirectional( "liquidMetal", liquidMetal.doubleValueProperty() );
    }

    public void load( FurnaceInstructionDTO data )
    {
        if ( data != null )
        {
            beanPathAdapter.setBean( data );
        }
        else
        {
            beanPathAdapter.setBean( EMPTY_BEAN );
        }
    }
}
