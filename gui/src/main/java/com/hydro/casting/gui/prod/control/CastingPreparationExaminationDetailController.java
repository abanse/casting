package com.hydro.casting.gui.prod.control;

import com.hydro.casting.server.contract.dto.CastingPreparationExaminationDTO;
import com.hydro.casting.server.contract.dto.ProcessDocuDTO;
import com.hydro.casting.server.contract.prod.ProcessDocuView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class CastingPreparationExaminationDetailController extends InspectionDetailController<ProcessDocuDTO, CastingPreparationExaminationDTO>
{
    @FXML
    private Text caster;

    @FXML
    private Label alloy;

    @FXML
    private Label furnace;

    @FXML
    private Label charge;

    @FXML
    private Label ruleHeader;

    @FXML
    private VBox rulesBox;

    public CastingPreparationExaminationDetailController()
    {
        super( ProcessDocuView.class, CastingPreparationExaminationDTO.class );
    }

    @FXML
    private void initialize()
    {
        beanPathAdapter.bindBidirectional( "machine", caster.textProperty() );
        beanPathAdapter.bindBidirectional( "chargeWithoutYear", charge.textProperty() );
        beanPathAdapter.bindBidirectional( "alloy", alloy.textProperty() );
        beanPathAdapter.bindBidirectional( "meltingFurnace", furnace.textProperty() );
    }

    @Override
    public void loadDetail( CastingPreparationExaminationDTO detail )
    {
        dto = detail;
        loadBeanPathAdapter();
        loadInspectionDetails();

        rulesBox.getChildren().clear();
        ruleNodes.forEach( ruleNode -> rulesBox.getChildren().add( ruleNode ) );
        if ( detail != null )
        {
            ruleHeader.setText( "Prüfung Angussvorbereitung (" + detail.getRules().size() + ")" );
        }
    }
}
