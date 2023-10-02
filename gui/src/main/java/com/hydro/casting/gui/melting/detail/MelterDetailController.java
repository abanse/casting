package com.hydro.casting.gui.melting.detail;

import com.hydro.casting.gui.prod.control.ActualValuesDetailController;
import com.hydro.casting.server.contract.dto.MeltingInstructionDTO;
import com.hydro.casting.server.contract.dto.ProcessDocuDTO;
import com.hydro.core.gui.detail.DetailTabsController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.util.Callback;
public class MelterDetailController extends DetailTabsController<MeltingInstructionDTO>
{
    @FXML
    private TabPane details;
    @FXML
    private Node chargingAmountAV;
    @FXML
    private ActualValuesDetailController chargingAmountAVController;
    @FXML
    private Node shaftFillLevelAV;
    @FXML
    private ActualValuesDetailController shaftFillLevelAVController;
    @FXML
    private Node metalPump1SpeedAV;
    @FXML
    private ActualValuesDetailController metalPump1SpeedAVController;
    @FXML
    private Node metalPump2SpeedAV;
    @FXML
    private ActualValuesDetailController metalPump2SpeedAVController;
    @FXML
    private Node metalLevelInOvenAV;
    @FXML
    private ActualValuesDetailController metalLevelInOvenAVController;
    @FXML
    private Node shaftWeightAV;
    @FXML
    private ActualValuesDetailController shaftWeightAVController;
    @FXML
    private Node shaftOpenAV;
    @FXML
    private ActualValuesDetailController shaftOpenAVController;
    @FXML
    private Node bathTempAV;
    @FXML
    private ActualValuesDetailController bathTempAVController;
    @FXML
    private Node crucibleWeightAV;
    @FXML
    private ActualValuesDetailController crucibleWeightAVController;
    @FXML
    private Node pouringPumpSpeedAV;
    @FXML
    private ActualValuesDetailController pouringPumpSpeedAVController;

    private final Callback<MeltingInstructionDTO, ProcessDocuDTO> processDocuConverter = meltingInstructionDTO -> {
        if ( meltingInstructionDTO == null || meltingInstructionDTO.getMeltingBatchOID() == null )
        {
            return null;
        }
        final ProcessDocuDTO processDocuDTO = new ProcessDocuDTO( meltingInstructionDTO.getMeltingBatchOID() );
        processDocuDTO.setCharge( meltingInstructionDTO.getCharge() );
        return processDocuDTO;
    };

    @FXML
    protected void initialize()
    {
        super.initialize( details );

        chargingAmountAVController.setActualValueName( "Chargiermenge" );
        chargingAmountAVController.setUnit( "kg" );
        shaftFillLevelAVController.setActualValueName( "SchachtFuellstand" );
        shaftFillLevelAVController.setUnit( "mm" );
        metalPump1SpeedAVController.setActualValueName( "GeschwindigkeitMetallpumpe1" );
        metalPump1SpeedAVController.setUnit( "Hz" );
        metalPump2SpeedAVController.setActualValueName( "GeschwindigkeitMetallpumpe2" );
        metalPump2SpeedAVController.setUnit( "Hz" );
        metalLevelInOvenAVController.setActualValueName( "MetallstandImOfen" );
        metalLevelInOvenAVController.setUnit( "mm" );
        shaftWeightAVController.setActualValueName( "SchachtGewicht" );
        shaftWeightAVController.setUnit( "kg" );
        shaftOpenAVController.setActualValueName( "SchachtdeckelOffen" );
        shaftOpenAVController.setUnit( "T/F" );
        bathTempAVController.setActualValueName( "Badtemperatur" );
        bathTempAVController.setUnit( "°C" );
        crucibleWeightAVController.setActualValueName( "Tiegelgewicht" );
        crucibleWeightAVController.setUnit( "kg" );
        pouringPumpSpeedAVController.setActualValueName( "Ausförderpumpe" );
        pouringPumpSpeedAVController.setUnit( "%" );

        registerDetail( chargingAmountAV, chargingAmountAVController, processDocuConverter );
        registerDetail( shaftFillLevelAV, shaftFillLevelAVController, processDocuConverter );
        registerDetail( metalPump1SpeedAV, metalPump1SpeedAVController, processDocuConverter );
        registerDetail( metalPump2SpeedAV, metalPump2SpeedAVController, processDocuConverter );
        registerDetail( metalLevelInOvenAV, metalLevelInOvenAVController, processDocuConverter );
        registerDetail( shaftWeightAV, shaftWeightAVController, processDocuConverter );
        registerDetail( shaftOpenAV, shaftOpenAVController, processDocuConverter );
        registerDetail( bathTempAV, bathTempAVController, processDocuConverter );
        registerDetail( crucibleWeightAV, crucibleWeightAVController, processDocuConverter );
        registerDetail( pouringPumpSpeedAV, pouringPumpSpeedAVController, processDocuConverter );
    }

    @Override
    protected void setTitle( MeltingInstructionDTO viewDTO )
    {

    }
}
