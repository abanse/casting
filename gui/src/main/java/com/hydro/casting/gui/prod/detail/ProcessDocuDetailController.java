package com.hydro.casting.gui.prod.detail;

import com.hydro.casting.gui.prod.control.*;
import com.hydro.casting.server.contract.dto.ProcessDocuDTO;
import com.hydro.core.gui.detail.DetailTabsController;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;

public class ProcessDocuDetailController extends DetailTabsController<ProcessDocuDTO>
{
    @FXML
    private Label tabTitle;

    @FXML
    private TabPane details;

    @FXML
    private Node detail;

    @FXML
    private ChargeDetailControlController detailController;

    @FXML
    private Node visualInspectionDetail;

    @FXML
    private VisualInspectionDetailController visualInspectionDetailController;

    @FXML
    private Node castingPreparationDetail;

    @FXML
    private CastingPreparationDetailController castingPreparationDetailController;

    @FXML
    private Node castingPreparationExaminationDetail;

    @FXML
    private CastingPreparationExaminationDetailController castingPreparationExaminationDetailController;

    @FXML
    private Node equipmentCondition;

    @FXML
    private EquipmentConditionDetailController equipmentConditionController;

    @FXML
    private Node analysisDetail;

    @FXML
    private AnalysisDetailController analysisDetailController;

    @FXML
    private Node bathTempAV;

    @FXML
    private ActualValuesDetailController bathTempAVController;

    @FXML
    private Node wireAmountAV;

    @FXML
    private ActualValuesDetailController wireAmountAVController;

    @FXML
    private Node tiltAngleAV;

    @FXML
    private ActualValuesDetailController tiltAngleAVController;

    @FXML
    private Node levelOvenChannelAV;

    @FXML
    private ActualValuesDetailController levelOvenChannelAVController;

    @FXML
    private Node levelInletChannelAV;

    @FXML
    private ActualValuesDetailController levelInletChannelAVController;

    @FXML
    private Node amountOfWaterAV;

    @FXML
    private ActualValuesDetailController amountOfWaterAVController;

    @FXML
    private Node sirNiveauReaktorAV;

    @FXML
    private ActualValuesDetailController sirNiveauReaktorAVController;

    @FXML
    private Node niveauSensor1AV;

    @FXML
    private ActualValuesDetailController niveauSensor1AVController;

    @FXML
    private Node niveauSensor2AV;

    @FXML
    private ActualValuesDetailController niveauSensor2AVController;

    @FXML
    private Node niveauSensor3AV;

    @FXML
    private ActualValuesDetailController niveauSensor3AVController;

    @FXML
    private Node niveauSensor4AV;

    @FXML
    private ActualValuesDetailController niveauSensor4AVController;

    @FXML
    private Node positionBetaetiger1AV;

    @FXML
    private ActualValuesDetailController positionBetaetiger1AVController;

    @FXML
    private Node positionBetaetiger2AV;

    @FXML
    private ActualValuesDetailController positionBetaetiger2AVController;

    @FXML
    private Node positionBetaetiger3AV;

    @FXML
    private ActualValuesDetailController positionBetaetiger3AVController;

    @FXML
    private Node positionBetaetiger4AV;

    @FXML
    private ActualValuesDetailController positionBetaetiger4AVController;

    @FXML
    private Node castingGutterTempAV;

    @FXML
    private ActualValuesDetailController castingGutterTempAVController;

    @FXML
    private Node castingLengthAV;

    @FXML
    private ActualValuesDetailController castingLengthAVController;

    /**
     * Initializes the controller class.
     */
    @FXML
    protected void initialize()
    {
        super.initialize( details );

        bathTempAVController.setActualValueName( "Badtemperatur" );
        bathTempAVController.setUnit( "°C" );
        wireAmountAVController.setActualValueName( "Drahtmenge" );
        wireAmountAVController.setUnit( "cm/m" );
        tiltAngleAVController.setActualValueName( "Kippwinkel" );
        tiltAngleAVController.setUnit( "°" );
        levelOvenChannelAVController.setActualValueName( "Niveau_Ofenrinne" );
        levelOvenChannelAVController.setUnit( "mm" );
        levelInletChannelAVController.setActualValueName( "Niveau_Zulaufrinne" );
        levelInletChannelAVController.setUnit( "mm" );
        sirNiveauReaktorAVController.setActualValueName( "SIR_Niveau_Reaktor" );
        sirNiveauReaktorAVController.setUnit("mm");
        amountOfWaterAVController.setActualValueName( "Wassermenge" );
        amountOfWaterAVController.setUnit( "m³/h" );
        niveauSensor1AVController.setActualValueName( "NiveauSensor1" );
        niveauSensor1AVController.setUnit( "mm" );
        niveauSensor2AVController.setActualValueName( "NiveauSensor2" );
        niveauSensor2AVController.setUnit( "mm" );
        niveauSensor3AVController.setActualValueName( "NiveauSensor3" );
        niveauSensor3AVController.setUnit( "mm" );
        niveauSensor4AVController.setActualValueName( "NiveauSensor4" );
        niveauSensor4AVController.setUnit( "mm" );
        positionBetaetiger1AVController.setActualValueName( "PositionBetaetiger1" );
        positionBetaetiger1AVController.setUnit( "?" );
        positionBetaetiger2AVController.setActualValueName( "PositionBetaetiger2" );
        positionBetaetiger2AVController.setUnit( "?" );
        positionBetaetiger3AVController.setActualValueName( "PositionBetaetiger3" );
        positionBetaetiger3AVController.setUnit( "?" );
        positionBetaetiger4AVController.setActualValueName( "PositionBetaetiger4" );
        positionBetaetiger4AVController.setUnit( "?" );
        castingGutterTempAVController.setActualValueName( "Giessrinne_Temperatur1" );
        castingGutterTempAVController.setActualValueDisplayName( "Temperatur Gießrinne 1" );
        castingGutterTempAVController.setAdditionalValueName( "Giessrinne_Temperatur2" );
        castingGutterTempAVController.setAdditionalValueDisplayName( "Temperatur Gießrinne 2" );
        castingGutterTempAVController.setUnit( "°C" );
        castingLengthAVController.setActualValueName( "Giesslaenge" );
        castingLengthAVController.setUnit( "mm" );

        registerDetail( detail, detailController );
        registerDetail( visualInspectionDetail, visualInspectionDetailController );
        registerDetail( castingPreparationDetail, castingPreparationDetailController );
        registerDetail( castingPreparationExaminationDetail, castingPreparationExaminationDetailController );
        registerDetail( equipmentCondition, equipmentConditionController );
        registerDetail( analysisDetail, analysisDetailController );
        registerDetail( bathTempAV, bathTempAVController );
        registerDetail( wireAmountAV, wireAmountAVController );
        registerDetail( tiltAngleAV, tiltAngleAVController );
        registerDetail( levelOvenChannelAV, levelOvenChannelAVController );
        registerDetail( levelInletChannelAV, levelInletChannelAVController );
        registerDetail( sirNiveauReaktorAV, sirNiveauReaktorAVController );
        registerDetail( amountOfWaterAV, amountOfWaterAVController );
        registerDetail( niveauSensor1AV, niveauSensor1AVController );
        registerDetail( niveauSensor2AV, niveauSensor2AVController );
        registerDetail( niveauSensor3AV, niveauSensor3AVController );
        registerDetail( niveauSensor4AV, niveauSensor4AVController );
        registerDetail( positionBetaetiger1AV, positionBetaetiger1AVController );
        registerDetail( positionBetaetiger2AV, positionBetaetiger2AVController );
        registerDetail( positionBetaetiger3AV, positionBetaetiger3AVController );
        registerDetail( positionBetaetiger4AV, positionBetaetiger4AVController );
        registerDetail( castingGutterTempAV, castingGutterTempAVController );
        registerDetail( castingLengthAV, castingLengthAVController );
    }

    @Override
    protected void setTitle( ProcessDocuDTO viewDTO )
    {
        if ( viewDTO == null )
        {
            tabTitle.setText( null );
        }
        else
        {
            tabTitle.setText( viewDTO.getChargeWithoutYear() );
        }
    }

    public ReadOnlyIntegerProperty selectedTabIndex()
    {
        return details.getSelectionModel().selectedIndexProperty();
    }

    public int getSelectedTabIndex()
    {
        return this.details.getSelectionModel().getSelectedIndex();
    }
}
