package com.hydro.casting.gui.prod.detail;

import com.hydro.casting.server.contract.dto.CasterDTO;
import com.hydro.core.gui.detail.DetailTabsController;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

public class MoldDepartmentDetailController extends DetailTabsController<CasterDTO>
{
    //@FXML
    //private Label tabTitle;

    @FXML
    private TabPane details;

    /*
    @FXML
    private Node detail;

    @FXML
    private ChargeDetailControlController detailController;

    @FXML
    private Node equipmentCondition;

    @FXML
    private EquipmentConditionDetailController equipmentConditionController;
     */

    /**
     * Initializes the controller class.
     */
    @FXML
    protected void initialize()
    {
        super.initialize( details );

        //registerDetail( detail, detailController );
        //registerDetail( equipmentCondition, equipmentConditionController );
    }

    @Override
    protected void setTitle( CasterDTO viewDTO )
    {
        /*
        if ( viewDTO == null )
        {
            tabTitle.setText( null );
        }
        else
        {
            tabTitle.setText( viewDTO.getChargeWithoutYear() );
        }
         */
    }
}
