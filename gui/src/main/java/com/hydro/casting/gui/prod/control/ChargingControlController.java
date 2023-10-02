package com.hydro.casting.gui.prod.control;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.gui.prod.dialog.ChargingChangeSpecificationDialog;
import com.hydro.casting.gui.prod.dialog.result.ChargingChangeSpecificationResult;
import com.hydro.casting.gui.prod.table.ChargingTable;
import com.hydro.casting.server.contract.dto.ChargeSpecificationDTO;
import com.hydro.casting.server.contract.dto.FurnaceInstructionDTO;
import com.hydro.casting.server.contract.dto.MaterialDTO;
import com.hydro.core.gui.ApplicationManager;
import com.hydro.core.gui.TaskManager;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class ChargingControlController
{
    private final static Logger log = LoggerFactory.getLogger( ChargingControlController.class );

    @FXML
    private Slider timeSlider;

    @Inject
    private TaskManager taskManager;

    @Inject
    private ApplicationManager applicationManager;

    @Inject
    private Injector injector;

    @FXML
    private ChargingTable chargingTable;

    private FurnaceInstructionDTO furnaceInstruction;

    private ChargeSpecificationDTO overwriteChargeSpecificationDTO;

    /**
     * Initializes the controller class.
     */
    @FXML
    private void initialize()
    {
        injector.injectMembers( chargingTable );

        timeSlider.setMin( 0 );
        timeSlider.setMax( 100.0 );
        timeSlider.setBlockIncrement( 1 );
        timeSlider.setMinorTickCount( 5 );
        timeSlider.setMajorTickUnit( 1 );
        chargingTable.timeProperty().bind( timeSlider.valueProperty() );
        timeSlider.valueProperty().addListener( ( observable, oldValue, newValue ) -> {
            if ( newValue != null && newValue.doubleValue() > 0. )
            {
                timeSlider.getStyleClass().add( "sliderRed" );
            }
            else
            {
                timeSlider.getStyleClass().setAll( "slider" );
            }
        } );
    }

    public BooleanProperty compressAnalysisProperty()
    {
        return chargingTable.compressAnalysisProperty();
    }

    public IntegerProperty timeProperty()
    {
        return chargingTable.timeProperty();
    }

    public void configure()
    {
        if ( furnaceInstruction != null && furnaceInstruction.getChargeSpecification() != null )
        {
            try
            {
                ChargeSpecificationDTO chargeSpecificationDTO = furnaceInstruction.getChargeSpecification();
                if ( overwriteChargeSpecificationDTO != null )
                {
                    chargeSpecificationDTO = overwriteChargeSpecificationDTO;
                }
                final ChargingChangeSpecificationResult chargingChangeSpecificationResult = ChargingChangeSpecificationDialog.showDialog( applicationManager.getMainStage(),
                        (ChargeSpecificationDTO) chargeSpecificationDTO.clone() );
                if ( chargingChangeSpecificationResult != null )
                {
                    overwriteChargeSpecificationDTO = chargingChangeSpecificationResult.getChargeSpecificationDTO();
                    reload();
                }
            }
            catch ( CloneNotSupportedException e )
            {
                log.error( "error cloning object", e );
            }
        }
    }

    public void reload()
    {
        loadData( furnaceInstruction );
    }

    public void loadData( FurnaceInstructionDTO furnaceInstruction )
    {
        this.furnaceInstruction = furnaceInstruction;
        if ( overwriteChargeSpecificationDTO != null && ( furnaceInstruction == null || !Objects.equals( overwriteChargeSpecificationDTO.getCharge(), furnaceInstruction.getCharge() ) ) )
        {
            overwriteChargeSpecificationDTO = null;
        }
        chargingTable.loadData( furnaceInstruction, overwriteChargeSpecificationDTO );
    }

    public List<MaterialDTO> getFurnaceContentMaterials()
    {
        return chargingTable.getFurnaceContentMaterials();
    }

    public List<MaterialDTO> getChargingMaterials()
    {
        return chargingTable.getChargingMaterials();
    }

    public void stopEditing()
    {
        chargingTable.stopEditing();
    }

    public BooleanProperty editingProperty()
    {
        return chargingTable.editingProperty();
    }

    public boolean isEditing()
    {
        return chargingTable.isEditing();
    }

    public boolean isDeleteAllowed()
    {
        return chargingTable.isDeleteAllowed();
    }

    public BooleanProperty deleteAllowedProperty()
    {
        return chargingTable.deleteAllowedProperty();
    }

    public void setDeleteAllowed( boolean deleteAllowed )
    {
        chargingTable.setDeleteAllowed( deleteAllowed );
    }

    public void deleteSelectedRow()
    {
        chargingTable.deleteSelectedRow();
    }

    public ChargingTable getChargingTable()
    {
        return chargingTable;
    }
}
