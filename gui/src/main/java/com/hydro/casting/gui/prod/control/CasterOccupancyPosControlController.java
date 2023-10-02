package com.hydro.casting.gui.prod.control;

import com.hydro.casting.server.contract.dto.CasterOccupancyPosDTO;
import com.hydro.core.common.util.StringTools;
import com.hydro.core.gui.binding.BeanPathAdapter;
import com.hydro.core.gui.comp.DoubleTextField;
import com.hydro.core.gui.comp.StringTextField;
import com.hydro.core.gui.util.converter.StringNumberConverter;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import org.jacop.scala.state;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class CasterOccupancyPosControlController
{
    private static CasterOccupancyPosDTO EMPTY_BEAN = new CasterOccupancyPosDTO();

    @FXML
    private GridPane mainPane;

    @FXML
    private Label width;
    @FXML
    private StringTextField slab;
    @FXML
    private StringTextField order;
    @FXML
    private DoubleTextField castingLength;
    @FXML
    private DoubleTextField lengthBonus;
//    @FXML
//    private StringTextField mold;
//    @FXML
//    private StringTextField ags;
    @FXML
    private Label position;

    private BeanPathAdapter<CasterOccupancyPosDTO> beanPathAdapter;

    private DoubleProperty widthProperty = new SimpleDoubleProperty();

    @FXML
    private void initialize()
    {
        beanPathAdapter = new BeanPathAdapter<>( EMPTY_BEAN );

        beanPathAdapter.bindBidirectional( "width", widthProperty );
        width.textProperty().bindBidirectional( widthProperty, new StringConverter<Number>()
        {
            @Override
            public String toString( Number object )
            {
                if ( object == null || object.intValue() == 0)
                {
                    return "";
                }
                return "" + object.intValue();
            }

            @Override
            public Number fromString( String string )
            {
                return null;
            }
        } );
        beanPathAdapter.bindBidirectional( "slab", slab.textProperty() );
        beanPathAdapter.bindBidirectional( "order", order.textProperty() );
        beanPathAdapter.bindBidirectional( "castingLength", castingLength.doubleValueProperty() );
        beanPathAdapter.bindBidirectional( "lengthBonus", lengthBonus.doubleValueProperty() );
//        beanPathAdapter.bindBidirectional( "mold", mold.textProperty() );
//        beanPathAdapter.bindBidirectional( "ags", ags.textProperty() );
    }

    public void load( CasterOccupancyPosDTO data )
    {
        if ( data != null )
        {
            beanPathAdapter.setBean( data );
            mainPane.setDisable( false );
            castingLength.setStyle( "-fx-text-fill: black" );
            lengthBonus.setStyle( "-fx-text-fill: black" );
        }
        else
        {
            beanPathAdapter.setBean( EMPTY_BEAN );
            mainPane.setDisable( true );
            castingLength.setStyle( "-fx-text-fill: transparent" );
            lengthBonus.setStyle( "-fx-text-fill: transparent" );
        }
    }

    public void setPosition( int position )
    {
        this.position.setText( "Pos. " + position );
    }
}
