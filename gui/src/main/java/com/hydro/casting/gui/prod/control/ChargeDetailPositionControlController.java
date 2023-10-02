package com.hydro.casting.gui.prod.control;

import com.hydro.casting.server.contract.dto.CasterSchedulePosDTO;
import com.hydro.core.common.util.StringTools;
import com.hydro.core.gui.comp.StringTextField;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class ChargeDetailPositionControlController
{
    @FXML
    private Label width;
    @FXML
    private StringTextField length;
    @FXML
    private StringTextField lengthBonus;
    @FXML
    private GridPane mainPane;
    @FXML
    private Label position;

    @FXML
    private void initialize()
    {
    }

    public void setPosition( int position )
    {
        this.position.setText( "Pos.:0" + position );
    }

    public void setData( CasterSchedulePosDTO casterSchedulePosDTO )
    {
        if ( casterSchedulePosDTO == null || casterSchedulePosDTO.getLength() <= 0 )
        {
            mainPane.setDisable( true );
            width.setText( null );
            length.setText( null );
            lengthBonus.setText( null );
        }
        else
        {
            mainPane.setDisable( false );
            width.setText( StringTools.N1F.format( casterSchedulePosDTO.getWidth() ) );
            length.setText( StringTools.N1F.format( casterSchedulePosDTO.getLength() ) );
            lengthBonus.setText( StringTools.N1F.format( casterSchedulePosDTO.getLengthBonus() ) );
        }
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
