package com.hydro.casting.gui.prod.control;

import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.ProcessDocuDTO;
import com.hydro.casting.server.contract.prod.ProcessDocuView;
import com.hydro.core.gui.binding.BeanPathAdapter;
import com.hydro.core.gui.comp.StringTextField;
import com.hydro.core.gui.control.DetailController;
import javafx.fxml.FXML;

public class ChargeDetailControlController extends DetailController<ProcessDocuDTO, CasterScheduleDTO>
{
    private static CasterScheduleDTO EMPTY_BEAN = new CasterScheduleDTO();

    @FXML
    private StringTextField charge;
    @FXML
    private StringTextField furnace;
    @FXML
    private StringTextField caster;
    @FXML
    private StringTextField alloy;
    @FXML
    private ChargeDetailPositionsControlController positionsController;

    private BeanPathAdapter<CasterScheduleDTO> beanPathAdapter;

    public ChargeDetailControlController()
    {
        super( ProcessDocuView.class, CasterScheduleDTO.class );
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    private void initialize()
    {
        beanPathAdapter = new BeanPathAdapter<CasterScheduleDTO>( EMPTY_BEAN );

        beanPathAdapter.bindBidirectional( "chargeWithoutYear", charge.textProperty() );
        beanPathAdapter.bindBidirectional( "machine", caster.textProperty() );
        beanPathAdapter.bindBidirectional( "meltingFurnace", furnace.textProperty() );
        beanPathAdapter.bindBidirectional( "alloy", alloy.textProperty() );
    }

    @Override
    public void loadDetail( CasterScheduleDTO data )
    {
        if ( data != null )
        {
            beanPathAdapter.setBean( data );
        }
        else
        {
            beanPathAdapter.setBean( EMPTY_BEAN );
        }
        positionsController.setCasterScheduleDTO( data );
    }
}
