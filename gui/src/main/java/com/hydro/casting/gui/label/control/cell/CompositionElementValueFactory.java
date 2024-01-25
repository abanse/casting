package com.hydro.casting.gui.label.control.cell;

import com.hydro.casting.gui.model.Composition;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompositionElementValueFactory implements Callback<CellDataFeatures<Composition, Number>, ObservableValue<Number>>
{
    private final static Logger log = LoggerFactory.getLogger( CompositionElementValueFactory.class );

    private final String elementName;

    public CompositionElementValueFactory( String elementName )
    {
        this.elementName = elementName;
    }

    @Override
    public ObservableValue<Number> call( CellDataFeatures<Composition, Number> param )
    {
        if ( param == null )
        {
            log.error( "param is null" );
            return null;
        }
        if ( param.getValue() == null )
        {
            log.error( "param.getValue is null" );
            return null;
        }
        return param.getValue().compositionElementValueProperty( elementName );
    }
}
