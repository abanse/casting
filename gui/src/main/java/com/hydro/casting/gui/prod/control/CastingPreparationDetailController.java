package com.hydro.casting.gui.prod.control;

import com.hydro.casting.server.contract.dto.CastingPreparationDTO;
import com.hydro.casting.server.contract.dto.ProcessDocuDTO;
import com.hydro.casting.server.contract.prod.ProcessDocuView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class CastingPreparationDetailController extends InspectionDetailController<ProcessDocuDTO, CastingPreparationDTO>
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

    public CastingPreparationDetailController()
    {
        super( ProcessDocuView.class, CastingPreparationDTO.class );
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
    public void loadDetail( CastingPreparationDTO detail )
    {
        dto = detail;
        loadBeanPathAdapter();
        loadInspectionDetails();

        rulesBox.getChildren().clear();
        ruleNodes.forEach( ruleNode -> rulesBox.getChildren().add( ruleNode ) );
        if ( detail != null )
        {
            ruleHeader.setText( "Angussvorbereitung (" + detail.getRules().size() + ")" );
        }
    }
}
