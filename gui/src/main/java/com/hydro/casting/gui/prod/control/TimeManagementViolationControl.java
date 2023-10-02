package com.hydro.casting.gui.prod.control;

import com.hydro.casting.common.constant.CasterStep;
import com.hydro.casting.common.constant.FurnaceStep;
import com.hydro.casting.server.contract.dto.TimeManagementViolationDTO;
import com.hydro.core.gui.comp.StringTextField;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

import java.time.format.DateTimeFormatter;

public class TimeManagementViolationControl extends GridPane
{
    final Label timeLBL = new Label( "Start" );
    final Label chargeLBL = new Label( "Charge" );
    final Label phaseLBL = new Label( "Phase" );
    final StringTextField charge = new StringTextField();
    final StringTextField phase = new StringTextField();
    final StringTextField time = new StringTextField();

    public TimeManagementViolationControl()
    {
        final ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setHalignment( HPos.RIGHT );
        labelColumn.setHgrow( Priority.ALWAYS );
        final ColumnConstraints textColumn = new ColumnConstraints();
        textColumn.setHalignment( HPos.LEFT );
        textColumn.setFillWidth( false );
        textColumn.setHgrow( Priority.ALWAYS );
        getColumnConstraints().addAll( labelColumn, textColumn );

        setHgap( 2 );
        setVgap( 2 );

        charge.setEditable( false );
        charge.setPrefWidth( 70 );
        charge.setMouseTransparent( true );

        phase.setEditable( false );
        phase.setPrefWidth( 100 );
        phase.setMouseTransparent( true );

        time.setEditable( false );
        time.setPrefWidth( 100 );
        time.setMouseTransparent( true );

        add( chargeLBL, 0, 0 );
        add( charge, 1, 0 );
        add( phaseLBL, 0, 1 );
        add( phase, 1, 1 );
        add( timeLBL, 0, 2 );
        add( time, 1, 2 );
    }

    public void setTimeManagementViolation( TimeManagementViolationDTO timeManagementViolationDTO )
    {
        if ( timeManagementViolationDTO.getCharge() != null && timeManagementViolationDTO.getCharge().length() >= 7 )
        {
            charge.setText( timeManagementViolationDTO.getCharge().substring( 2 ) );
        }
        else
        {
            charge.setText( timeManagementViolationDTO.getCharge() );
        }
        String phaseDescription = timeManagementViolationDTO.getName();
        final CasterStep casterStep = CasterStep.findByShortName( timeManagementViolationDTO.getName() );
        if ( casterStep != null )
        {
            phaseDescription = casterStep.getDescription();
        }
        else
        {
            final FurnaceStep furnaceStep = FurnaceStep.findByShortName( timeManagementViolationDTO.getName() );
            if ( furnaceStep != null )
            {
                phaseDescription = furnaceStep.getDescription();
            }
        }
        phase.setText( phaseDescription );
        String timeValue = null;
        if ( "Start".equals( timeManagementViolationDTO.getType() ) )
        {
            timeLBL.setText( "Start" );
            timeValue = timeManagementViolationDTO.getPlannedStart().format( DateTimeFormatter.ofPattern( "dd.MM HH:mm" ) );
        }
        else
        {
            timeLBL.setText( "Ende" );
            if ( timeManagementViolationDTO.getPlannedEnd() != null )
            {
                timeValue = timeManagementViolationDTO.getPlannedEnd().format( DateTimeFormatter.ofPattern( "dd.MM HH:mm" ) );
            }
        }
        time.setText( timeValue );

        if ( timeManagementViolationDTO.isChecked() )
        {
            chargeLBL.setTextFill( Color.BLACK );
            phaseLBL.setTextFill( Color.BLACK );
            timeLBL.setTextFill( Color.BLACK );
            charge.setStyle(
                    "-fx-text-fill: black; -fx-background-color: linear-gradient(to bottom, derive(-fx-text-box-border, -10%), -fx-text-box-border),linear-gradient(from 0px 0px to 0px 5px, derive(#ffb537, -9%), #ffb537);" );
            phase.setStyle(
                    "-fx-text-fill: black; -fx-background-color: linear-gradient(to bottom, derive(-fx-text-box-border, -10%), -fx-text-box-border),linear-gradient(from 0px 0px to 0px 5px, derive(#ffb537, -9%), #ffb537);" );
            time.setStyle(
                    "-fx-text-fill: black; -fx-background-color: linear-gradient(to bottom, derive(-fx-text-box-border, -10%), -fx-text-box-border),linear-gradient(from 0px 0px to 0px 5px, derive(#ffb537, -9%), #ffb537);" );

        }
        else
        {
            chargeLBL.setTextFill( Color.WHITE );
            phaseLBL.setTextFill( Color.WHITE );
            timeLBL.setTextFill( Color.WHITE );
            charge.setStyle(
                    "-fx-text-fill: white; -fx-background-color: linear-gradient(to bottom, derive(-fx-text-box-border, -10%), -fx-text-box-border),linear-gradient(from 0px 0px to 0px 5px, derive(#DE3535FF, -9%), #DE3535FF);" );
            phase.setStyle(
                    "-fx-text-fill: white; -fx-background-color: linear-gradient(to bottom, derive(-fx-text-box-border, -10%), -fx-text-box-border),linear-gradient(from 0px 0px to 0px 5px, derive(#DE3535FF, -9%), #DE3535FF);" );
            time.setStyle(
                    "-fx-text-fill: white; -fx-background-color: linear-gradient(to bottom, derive(-fx-text-box-border, -10%), -fx-text-box-border),linear-gradient(from 0px 0px to 0px 5px, derive(#DE3535FF, -9%), #DE3535FF);" );

        }
    }
}
