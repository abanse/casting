package com.hydro.casting.gui.prod.control;

import com.hydro.casting.common.Casting;
import com.hydro.casting.gui.prod.view.ProcessDocuViewController;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.InspectionRuleDTO;
import com.hydro.casting.server.contract.dto.InspectionValueDTO;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import static com.hydro.casting.server.contract.dto.InspectionValueDTO.*;

public class CasterPositionsControlController implements InspectionController, InvalidationListener
{
    @FXML
    protected Label description;
    @FXML
    protected Label result;
    @FXML
    protected CasterPositionControlController pos1Controller;
    @FXML
    protected CasterPositionControlController pos2Controller;
    @FXML
    protected CasterPositionControlController pos3Controller;
    @FXML
    protected CasterPositionControlController pos4Controller;
    @FXML
    protected CasterPositionControlController pos5Controller;

    private CasterScheduleDTO casterScheduleDTO;
    private InspectionRuleDTO inspectionRule;
    private InspectionValueDTO inspectionValue;

    private BooleanProperty editable = new SimpleBooleanProperty( false );

    @FXML
    private void initialize()
    {
        pos1Controller.setPosition( 1 );
        pos2Controller.setPosition( 2 );
        pos3Controller.setPosition( 3 );
        pos4Controller.setPosition( 4 );
        pos5Controller.setPosition( 5 );

        pos1Controller.editableProperty().bind( editable );
        pos2Controller.editableProperty().bind( editable );
        pos3Controller.editableProperty().bind( editable );
        pos4Controller.editableProperty().bind( editable );
        pos5Controller.editableProperty().bind( editable );
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
        this.casterScheduleDTO = casterScheduleDTO;
        this.inspectionRule = inspectionRule;
        this.inspectionValue = inspectionValue;

        description.setText( inspectionRule.getDescription() );

        pos5Controller.setVisible( Casting.MACHINE.CASTER_80.equals( casterScheduleDTO.getMachine() ) );
        pos1Controller.setWidth( casterScheduleDTO.getPos1Width() );
        pos2Controller.setWidth( casterScheduleDTO.getPos2Width() );
        pos3Controller.setWidth( casterScheduleDTO.getPos3Width() );
        pos4Controller.setWidth( casterScheduleDTO.getPos4Width() );
        pos5Controller.setWidth( casterScheduleDTO.getPos5Width() );

        if ( inspectionValue != null && inspectionValue.getCasterPositionValues() != null && inspectionValue.getCasterPositionValues().length == 5 )
        {
            final long[] casterPosValues = inspectionValue.getCasterPositionValues();
            pos1Controller.setValue( casterPosValues[0] );
            pos2Controller.setValue( casterPosValues[1] );
            pos3Controller.setValue( casterPosValues[2] );
            pos4Controller.setValue( casterPosValues[3] );
            pos5Controller.setValue( casterPosValues[4] );
        }
        else
        {
            pos1Controller.setValue( 0 );
            pos2Controller.setValue( 0 );
            pos3Controller.setValue( 0 );
            pos4Controller.setValue( 0 );
            pos5Controller.setValue( 0 );
        }

        pos1Controller.valueProperty().addListener( this );
        pos2Controller.valueProperty().addListener( this );
        pos3Controller.valueProperty().addListener( this );
        pos4Controller.valueProperty().addListener( this );
        pos5Controller.valueProperty().addListener( this );

        invalidated( null );
    }

    private int calcResult()
    {
        // Prüfen ob noch keine Eingaben gemacht wurden
        //@formatter:off
        boolean notFilled = ( pos1Controller.isRelevant() && !pos1Controller.isFilled() )
                || ( pos2Controller.isRelevant() && !pos2Controller.isFilled() )
                || ( pos3Controller.isRelevant() && !pos3Controller.isFilled() )
                || ( pos4Controller.isRelevant() && !pos4Controller.isFilled() )
                || ( pos5Controller.isRelevant() && !pos5Controller.isFilled() );
        //@formatter:on
        if ( notFilled )
        {
            return RESULT_NOT_FILLED;
        }

        int maxResult = -1;
        int result;
        if ( pos1Controller.isRelevant() )
        {
            result = pos1Controller.calcResult();
            if ( result > maxResult )
            {
                maxResult = result;
            }
        }
        if ( pos2Controller.isRelevant() )
        {
            result = pos2Controller.calcResult();
            if ( result > maxResult )
            {
                maxResult = result;
            }
        }
        if ( pos3Controller.isRelevant() )
        {
            result = pos3Controller.calcResult();
            if ( result > maxResult )
            {
                maxResult = result;
            }
        }
        if ( pos4Controller.isRelevant() )
        {
            result = pos4Controller.calcResult();
            if ( result > maxResult )
            {
                maxResult = result;
            }
        }
        if ( pos5Controller.isRelevant() )
        {
            result = pos5Controller.calcResult();
            if ( result > maxResult )
            {
                maxResult = result;
            }
        }

        return maxResult;
    }

    private void setSummary( int summary )
    {
        final ImageView icon;
        if ( summary == RESULT_OK )
        {
            icon = new ImageView( ProcessDocuViewController.RESULT_OK_I );
        }
        else if ( summary == RESULT_OK_WITH_LIMITATIONS )
        {
            icon = new ImageView( ProcessDocuViewController.RESULT_OK_WITH_LIMITATIONS_I );
        }
        else if ( summary == RESULT_FAILED )
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
        final long[] posValues = new long[5];
        posValues[0] = pos1Controller.getValue();
        posValues[1] = pos2Controller.getValue();
        posValues[2] = pos3Controller.getValue();
        posValues[3] = pos4Controller.getValue();
        posValues[4] = pos5Controller.getValue();
        value.setCasterPositionValues( posValues );

        return value;
    }
}
