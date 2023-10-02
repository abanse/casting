package com.hydro.casting.gui.prod.detail;

import com.hydro.casting.gui.prod.control.CastingPreparationDetailController;
import com.hydro.casting.gui.prod.control.ProductionLogControlController;
import com.hydro.casting.gui.prod.control.TimeManagementDetailController;
import com.hydro.casting.server.contract.dto.CastingInstructionDTO;
import com.hydro.casting.server.contract.dto.ProcessDocuDTO;
import com.hydro.core.gui.detail.DetailTabsController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.util.Callback;

public class FurnaceDetailController extends DetailTabsController<CastingInstructionDTO>
{
    @FXML
    private TabPane details;

    @FXML
    private ProductionLogControlController productionLogController;

    @FXML
    private Node timeManagementDetail;
    @FXML
    private TimeManagementDetailController timeManagementDetailController;

    @FXML
    private Node castingPreparationDetail;

    @FXML
    private CastingPreparationDetailController castingPreparationDetailController;

    private Callback<CastingInstructionDTO, ProcessDocuDTO> processDocuConverter = castingInstructionDTO -> {
        if ( castingInstructionDTO == null || castingInstructionDTO.getCastingBatchOID() == null )
        {
            return null;
        }
        return new ProcessDocuDTO( castingInstructionDTO.getCastingBatchOID() );
    };

    /**
     * Initializes the controller class.
     */
    @FXML
    protected void initialize()
    {
        super.initialize( details );

        registerDetail( timeManagementDetail, timeManagementDetailController );
        registerDetail( castingPreparationDetail, castingPreparationDetailController, processDocuConverter );

        activeProperty().addListener( ( observable, oldValue, newValue ) -> {
            if ( newValue != null && newValue.booleanValue() )
            {
                timeManagementDetailController.start();
            }
            else
            {
                timeManagementDetailController.stop();
            }
        } );
    }

    public String getMachineApk()
    {
        return productionLogController.getMachineApk();
    }

    public void setMachineApk( String machineApk )
    {
        productionLogController.setMachineApk( machineApk );
    }

    public void reloadProductionLog()
    {
        productionLogController.reload();
    }

    @Override
    protected void setTitle( CastingInstructionDTO viewDTO )
    {
    }
}
