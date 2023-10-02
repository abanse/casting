package com.hydro.casting.gui.prod.control;

import com.hydro.casting.server.contract.dto.InspectionValueDTO;
import com.hydro.core.common.util.BitSetTools;
import com.hydro.core.common.util.StringTools;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.BitSet;

public class CasterPositionControlController
{
    @FXML
    private Label width;
    @FXML
    private CheckBox kokIO;
    @FXML
    private CheckBox kokNIO;
    @FXML
    private CheckBox agsIO;
    @FXML
    private CheckBox agsNIO;
    @FXML
    private CheckBox hklIO;
    @FXML
    private CheckBox hklNIO;
    @FXML
    private CheckBox rebuild;
    @FXML
    private GridPane mainPane;
    @FXML
    private Label position;

    private BooleanProperty editable = new SimpleBooleanProperty( false );
    private LongProperty value = new SimpleLongProperty();

    @FXML
    private void initialize()
    {
        kokIO.mouseTransparentProperty().bind( editable.not() );
        kokNIO.mouseTransparentProperty().bind( editable.not() );
        agsIO.mouseTransparentProperty().bind( editable.not() );
        agsNIO.mouseTransparentProperty().bind( editable.not() );
        hklIO.mouseTransparentProperty().bind( editable.not() );
        hklNIO.mouseTransparentProperty().bind( editable.not() );
        rebuild.mouseTransparentProperty().bind( editable.not() );

        addToggleListener( kokIO, kokNIO );
        addToggleListener( agsIO, agsNIO );
        addToggleListener( hklIO, hklNIO );

        value.addListener( ( observable, oldValue, newValue ) -> {
            long bitSetValue = 0;
            if ( newValue != null )
            {
                bitSetValue = newValue.longValue();
            }
            final BitSet bitSet = BitSetTools.toBitSet( bitSetValue );
            kokIO.setSelected( bitSet.get( 0 ) );
            kokNIO.setSelected( bitSet.get( 1 ) );
            agsIO.setSelected( bitSet.get( 2 ) );
            agsNIO.setSelected( bitSet.get( 3 ) );
            hklIO.setSelected( bitSet.get( 4 ) );
            hklNIO.setSelected( bitSet.get( 5 ) );
            rebuild.setSelected( bitSet.get( 6 ) );
        } );
        addSelectionListener( kokIO, 0 );
        addSelectionListener( kokNIO, 1 );
        addSelectionListener( agsIO, 2 );
        addSelectionListener( agsNIO, 3 );
        addSelectionListener( hklIO, 4 );
        addSelectionListener( hklNIO, 5 );
        addSelectionListener( rebuild, 6 );
    }

    private void addToggleListener( CheckBox first, CheckBox second )
    {
        first.setOnAction( event -> second.setSelected( !first.isSelected() ) );
        second.setOnAction( event -> first.setSelected( !second.isSelected() ) );
    }

    private void addSelectionListener( CheckBox cbx, int bitSetIndex )
    {
        cbx.selectedProperty().addListener( ( observable, oldValue, newValue ) -> {
            final BitSet bitSet = BitSetTools.toBitSet( getValue() );
            if ( newValue != null && newValue )
            {
                bitSet.set( bitSetIndex );
            }
            else
            {
                bitSet.clear( bitSetIndex );
            }
            setValue( BitSetTools.toLong( bitSet ) );
        } );
    }

    public void setPosition( int position )
    {
        this.position.setText( "Pos.:0" + position );
    }

    public void setWidth( Double width )
    {
        if ( width == null )
        {
            mainPane.setDisable( true );
            this.width.setText( null );
        }
        else
        {
            mainPane.setDisable( false );
            this.width.setText( StringTools.N1F.format( width ) );
        }
    }

    public long getValue()
    {
        return value.get();
    }

    public boolean isRelevant()
    {
        return isVisible() && !mainPane.isDisable();
    }

    public boolean isFilled()
    {
        final BitSet bitSet = BitSetTools.toBitSet( getValue() );
        if ( ( bitSet.get( 0 ) || bitSet.get( 1 ) ) && ( bitSet.get( 2 ) || bitSet.get( 3 ) ) && ( bitSet.get( 4 ) || bitSet.get( 5 ) ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public int calcResult()
    {
        final BitSet bitSet = BitSetTools.toBitSet( getValue() );
        if ( bitSet.get( 0 ) && bitSet.get( 2 ) && bitSet.get( 4 ) )
        {
            return InspectionValueDTO.RESULT_OK;
        }
        if ( bitSet.get( 6 ) )
        {
            return InspectionValueDTO.RESULT_OK_WITH_LIMITATIONS;
        }

        return InspectionValueDTO.RESULT_FAILED;
    }

    public LongProperty valueProperty()
    {
        return value;
    }

    public void setValue( long value )
    {
        this.value.set( value );
    }

    public boolean isEditable()
    {
        return editable.get();
    }

    public BooleanProperty editableProperty()
    {
        return editable;
    }

    public void setEditable( boolean editable )
    {
        this.editable.set( editable );
    }

    public boolean isVisible()
    {
        return mainPane.isVisible();
    }

    public BooleanProperty visibleProperty()
    {
        return mainPane.visibleProperty();
    }

    public void setVisible( boolean visible )
    {
        mainPane.setVisible( visible );
    }

}
