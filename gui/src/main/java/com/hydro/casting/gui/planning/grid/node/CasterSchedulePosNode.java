package com.hydro.casting.gui.planning.grid.node;

import com.hydro.casting.gui.planning.view.content.MaBeHandle;
import com.hydro.casting.server.contract.dto.CasterSchedulePosDTO;
import com.hydro.core.common.util.StringTools;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class CasterSchedulePosNode extends GridPane
{
    private ObjectProperty<CasterSchedulePosDTO> casterSchedulePos = new SimpleObjectProperty<>();

    public CasterSchedulePosNode( final MaBeHandle handle )
    {
        setTranslateX( -1 );

        //setGridLinesVisible( true );
        final ColumnConstraints centerMiddleCC = new ColumnConstraints();
        centerMiddleCC.setFillWidth( true );
        centerMiddleCC.setHgrow( Priority.ALWAYS );
        centerMiddleCC.setPercentWidth( 50.0 );
        centerMiddleCC.setHalignment( HPos.CENTER );
        getColumnConstraints().addAll( centerMiddleCC, centerMiddleCC );
        final Label widthLBL = new Label();
        final Label lengthLBL = new Label();
        GridPane.setRowIndex( widthLBL, 1 );
        GridPane.setRowIndex( lengthLBL, 1 );
        GridPane.setColumnIndex( lengthLBL, 1 );
        final Label experimentNumberLBL = new Label();
        experimentNumberLBL.setTextFill( Color.INDIANRED );
        experimentNumberLBL.setFont( Font.font( null, FontWeight.EXTRA_BOLD, 10.0 ) );
        GridPane.setRowIndex( experimentNumberLBL, 2 );
        GridPane.setColumnSpan( experimentNumberLBL, 2 );
        GridPane.setHalignment( experimentNumberLBL, HPos.CENTER );
        GridPane.setValignment( experimentNumberLBL, VPos.BOTTOM );
        final Label lengthBonusLBL = new Label();
        lengthBonusLBL.setFont( Font.font( 10.0 ) );
        GridPane.setRowIndex( lengthBonusLBL, 2 );
        GridPane.setColumnIndex( lengthBonusLBL, 1 );
        GridPane.setHalignment( lengthBonusLBL, HPos.RIGHT );
        GridPane.setValignment( lengthBonusLBL, VPos.BOTTOM );
        GridPane.setMargin( lengthBonusLBL, new Insets( 0, 5, 0, 0 ) );

        final Label doubleLengthLBL = new Label();
        doubleLengthLBL.setFont( Font.font( null, FontWeight.BOLD, 10.0 ) );
        GridPane.setRowIndex( doubleLengthLBL, 2 );
        GridPane.setColumnIndex( doubleLengthLBL, 0 );
        GridPane.setHalignment( doubleLengthLBL, HPos.LEFT );
        GridPane.setValignment( doubleLengthLBL, VPos.BOTTOM );
        GridPane.setMargin( doubleLengthLBL, new Insets( 0, 0, 0, 5 ) );

        getChildren().addAll( widthLBL, lengthLBL, experimentNumberLBL, lengthBonusLBL, doubleLengthLBL );

        casterSchedulePos.addListener( ( observable, oldValue, newValue ) -> {
            if ( newValue == null || newValue.getMaterialType() == null )
            {
                widthLBL.setText( null );
                lengthLBL.setText( null );
                experimentNumberLBL.setText( null );
                lengthBonusLBL.setText( null );
                doubleLengthLBL.setText( null );
            }
            else
            {
                widthLBL.setText( StringTools.N1F.format( newValue.getWidth() ) );
                experimentNumberLBL.setText( StringTools.getNullSafe( newValue.getExperimentNumber() ) );
                lengthBonusLBL.setText( "+" + StringTools.N1F.format( newValue.getLengthBonus() ) );
                if ( newValue.getAmount() == 2 )
                {
                    lengthLBL.setText( StringTools.N1F.format( ( newValue.getLength() * 2.0 ) + newValue.getLengthBonus() ) );
                    doubleLengthLBL.setText( "2*" + StringTools.N1F.format( newValue.getLength() ) );
                }
                else
                {
                    lengthLBL.setText( StringTools.N1F.format( newValue.getLength() + newValue.getLengthBonus() ) );
                    doubleLengthLBL.setText( null );
                }
            }
        } );
    }

    public CasterSchedulePosDTO getCasterSchedulePos()
    {
        return casterSchedulePos.get();
    }

    public ObjectProperty<CasterSchedulePosDTO> casterSchedulePosProperty()
    {
        return casterSchedulePos;
    }

    public void setCasterSchedulePos( CasterSchedulePosDTO casterSchedulePos )
    {
        this.casterSchedulePos.set( casterSchedulePos );
    }
}
