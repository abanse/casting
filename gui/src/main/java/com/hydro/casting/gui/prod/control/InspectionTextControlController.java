package com.hydro.casting.gui.prod.control;

import com.hydro.casting.gui.prod.view.ProcessDocuViewController;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.InspectionRuleDTO;
import com.hydro.casting.server.contract.dto.InspectionValueDTO;
import com.hydro.core.common.util.StringTools;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;

public class InspectionTextControlController implements InspectionController, InvalidationListener
{
    @FXML
    protected Label description;
    @FXML
    protected Label subDescription;
    @FXML
    protected Label result;
    @FXML
    protected TextArea text;

    private InspectionRuleDTO inspectionRule;
    private InspectionValueDTO inspectionValue;

    private BooleanProperty editable = new SimpleBooleanProperty( false );

    @FXML
    private void initialize()
    {
        text.editableProperty().bind( editable );

        text.textProperty().addListener( this );
    }

    public boolean isEditable()
    {
        return editable.get();
    }

    @Override
    public BooleanProperty editableProperty()
    {
        return editable;
    }

    public void setEditable( boolean editable )
    {
        this.editable.set( editable );
    }

    @Override
    public void setInspection( CasterScheduleDTO casterScheduleDTO, InspectionRuleDTO inspectionRule, InspectionValueDTO inspectionValue )
    {
        this.inspectionRule = inspectionRule;
        this.inspectionValue = inspectionValue;

        description.setText( inspectionRule.getDescription() );
        subDescription.setText( inspectionRule.getSubDescription() );

        if ( inspectionValue != null )
        {
            text.setText( inspectionValue.getText() );
        }
        else
        {
            text.setText( null );
        }

        invalidated( null );
    }

    private int calcResult()
    {
        if ( inspectionRule == null )
        {
            return -1;
        }
        if ( StringTools.isFilled( text.getText() ) )
        {
            return inspectionRule.getFilledResult();
        }
        else
        {
            return inspectionRule.getEmptyResult();
        }
    }

    private void setSummary( int summary )
    {
        final ImageView icon;
        if ( summary == InspectionValueDTO.RESULT_OK )
        {
            icon = new ImageView( ProcessDocuViewController.RESULT_OK_I );
        }
        else if ( summary == InspectionValueDTO.RESULT_OK_WITH_LIMITATIONS )
        {
            icon = new ImageView( ProcessDocuViewController.RESULT_OK_WITH_LIMITATIONS_I );
        }
        else if ( summary == InspectionValueDTO.RESULT_FAILED )
        {
            icon = new ImageView( ProcessDocuViewController.RESULT_FAILED_I );
        }
        else
        {
            icon = new ImageView( ProcessDocuViewController.RESULT_MISSED_I );
        }
        icon.setFitHeight( 20 );
        icon.setFitWidth( 20 );
        result.setGraphic( icon );
    }

    @Override
    public void invalidated( Observable observable )
    {
        setSummary( calcResult() );
    }

    @Override
    public InspectionValueDTO getValue()
    {
        final InspectionValueDTO value = new InspectionValueDTO();
        if ( inspectionValue != null )
        {
            value.setId( inspectionValue.getId() );
        }
        else
        {
            value.setId( -1 );
        }
        value.setRuleId( inspectionRule.getId() );
        value.setResult( calcResult() );
        value.setText( text.getText() );

        return value;
    }
}
