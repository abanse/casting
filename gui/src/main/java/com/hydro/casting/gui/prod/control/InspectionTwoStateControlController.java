package com.hydro.casting.gui.prod.control;

import com.google.inject.Inject;
import com.hydro.casting.common.Casting;
import com.hydro.casting.gui.prod.view.ProcessDocuViewController;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.InspectionRuleDTO;
import com.hydro.casting.server.contract.dto.InspectionValueDTO;
import com.hydro.core.common.util.StringTools;
import com.hydro.core.gui.SecurityManager;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import static com.hydro.casting.server.contract.dto.InspectionValueDTO.*;

public class InspectionTwoStateControlController implements InspectionController, InvalidationListener
{
    @Inject
    SecurityManager securityManager;
    @FXML
    protected Label interventionLabel;
    @FXML
    protected TextField intervention;
    @FXML
    protected CheckBox checkBoxYes;
    @FXML
    protected CheckBox checkBoxNo;
    @FXML
    protected Label descriptionLine;
    @FXML
    protected Label titleComment;
    @FXML
    protected Label result;
    @FXML
    protected TextArea commentContent;
    @FXML
    protected VBox commentBox;
    @FXML
    protected VBox interventionBox;
    @FXML
    protected VBox additionalInfoBox;
    @FXML
    protected Label additionalInfoLabel;
    @FXML
    protected TextField additionalInfo;

    private InspectionRuleDTO inspectionRule;
    private InspectionValueDTO inspectionValue;
    private boolean oneOptionState = false;

    private final BooleanProperty editable = new SimpleBooleanProperty( false );
    private final StringProperty updateUser = new SimpleStringProperty();

    @FXML
    private void initialize()
    {
        checkBoxYes.mouseTransparentProperty().bind( editable.not() );
        checkBoxNo.mouseTransparentProperty().bind( editable.not() );
        intervention.editableProperty().bind( editable );
        additionalInfo.editableProperty().bind( editable );
        commentBox.managedProperty().bind( commentBox.visibleProperty() );
        interventionBox.managedProperty().bind( interventionBox.visibleProperty() );
        additionalInfoBox.managedProperty().bind( additionalInfoBox.visibleProperty() );

        checkBoxYes.selectedProperty().addListener( this );
        checkBoxNo.selectedProperty().addListener( this );
        intervention.textProperty().addListener( this );
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

        descriptionLine.setText( inspectionRule.getDescription() );
        additionalInfoBox.setVisible( false );

        if ( Casting.INSPECTION.TYPE.OK_NOK.equals( inspectionRule.getType() ) || Casting.INSPECTION.TYPE.OK_NOK_WITH_INTERVENTION.equals( inspectionRule.getType() ) )
        {
            checkBoxYes.setText( "in Ordnung" );
            checkBoxNo.setText( "nicht in Ordnung" );
        }
        else if ( Casting.INSPECTION.TYPE.YES_SIGNED.equals( inspectionRule.getType() ) )
        {
            oneOptionState = true;
            checkBoxNo.setVisible( false );
            intervention.editableProperty().unbind();
            intervention.setEditable( false );
            intervention.textProperty().bindBidirectional( updateUser );
            additionalInfoLabel.setText( inspectionRule.getAdditionalInfoDescription() );
            additionalInfoBox.setVisible( true );
        }

        if ( !oneOptionState && inspectionRule.getTargetValue() == 2 )
        {
            titleComment.disableProperty().bind( checkBoxYes.selectedProperty().not() );
            commentContent.editableProperty().bind( editable.and( checkBoxYes.selectedProperty() ) );
            commentBox.visibleProperty().bind( checkBoxYes.selectedProperty() );
            checkBoxYes.selectedProperty().addListener( ( observable, oldValue, newValue ) -> {
                if ( newValue != null && newValue )
                {
                    commentContent.requestFocus();
                }
            } );
        }
        else if ( !oneOptionState && inspectionRule.getTargetValue() == 1 )
        {
            titleComment.disableProperty().bind( checkBoxNo.selectedProperty().not() );
            commentContent.editableProperty().bind( editable.and( checkBoxNo.selectedProperty() ) );
            commentBox.visibleProperty().bind( checkBoxNo.selectedProperty() );
            checkBoxNo.selectedProperty().addListener( ( observable, oldValue, newValue ) -> {
                if ( newValue != null && newValue )
                {
                    commentContent.requestFocus();
                }
            } );
        }
        else
        {
            commentBox.setVisible( false );
        }

        if ( Casting.INSPECTION.TYPE.OK_NOK_WITH_INTERVENTION.equals( inspectionRule.getType() ) || Casting.INSPECTION.TYPE.YES_NO_WITH_INTERVENTION.equals( inspectionRule.getType() )
                || Casting.INSPECTION.TYPE.YES_SIGNED.equals( inspectionRule.getType() ) )
        {
            interventionBox.setVisible( true );
            interventionLabel.setText( inspectionRule.getInterventionDescription() );
        }
        else
        {
            interventionBox.setVisible( false );
        }

        if ( inspectionValue != null )
        {
            if ( inspectionValue.getValue() == 1 )
            {
                checkBoxYes.setSelected( true );
            }
            else if ( inspectionValue.getValue() == 2 )
            {
                checkBoxNo.setSelected( true );
            }
            commentContent.setText( inspectionValue.getRemark() );
            intervention.setText( inspectionValue.getIntervention() );
            additionalInfo.setText( inspectionValue.getAdditionalInfo() );
        }

        invalidated( null );
    }

    @FXML
    public void checkboxClickedYes( Event e )
    {
        boolean state = checkBoxYes.isSelected();
        checkBoxNo.setSelected( !state );
    }

    @FXML
    public void checkboxClickedNo( Event e )
    {
        boolean state = checkBoxNo.isSelected();
        checkBoxYes.setSelected( !state );
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
        if ( inspectionRule == null )
        {
            return;
        }

        if ( oneOptionState )
        {
            if ( checkBoxYes.isSelected() )
            {
                updateUser.set( securityManager.getCurrentUser() );
            }
            else
            {
                updateUser.set( null );
            }
        }

        setSummary( calcResult( getBitSetValue() ) );
    }

    // value = 1 -> yes selected, value = 2 -> no selected
    private int calcResult( long value )
    {
        if ( oneOptionState )
        {
            if ( checkBoxYes.isSelected() )
            {
                return RESULT_OK;
            }
            return RESULT_NOT_FILLED;
        }
        else if ( !checkBoxYes.isSelected() && !checkBoxNo.isSelected() )
        {
            return RESULT_NOT_FILLED;
        }
        else if ( inspectionRule.getTargetValue() == 0 || inspectionRule.getTargetValue() == value )
        {
            return RESULT_OK;
        }
        else if ( ( Casting.INSPECTION.TYPE.OK_NOK_WITH_INTERVENTION.equals( inspectionRule.getType() ) || Casting.INSPECTION.TYPE.YES_NO_WITH_INTERVENTION.equals( inspectionRule.getType() ) )
                && StringTools.isFilled( intervention.getText() ) )
        {
            return RESULT_OK_WITH_LIMITATIONS;
        }
        else
        {
            return RESULT_FAILED;
        }
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
        long bitSetValue = getBitSetValue();
        value.setRuleId( inspectionRule.getId() );
        value.setValue( bitSetValue );
        value.setRemark( commentContent.getText() );
        value.setIntervention( intervention.getText() );
        value.setAdditionalInfo( additionalInfo.getText() );
        value.setResult( calcResult( bitSetValue ) );
        return value;
    }

    private long getBitSetValue()
    {
        if ( checkBoxYes.isSelected() )
        {
            return 1;
        }
        else if ( checkBoxNo.isSelected() )
        {
            return 2;
        }

        return 0;
    }
}
