package com.hydro.casting.gui.control;

import com.google.inject.Inject;
import com.hydro.core.common.util.StringTools;
import com.hydro.core.common.util.TextFieldUtil;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.PreferencesManager;
import com.hydro.core.gui.comp.DateTimePicker;
import com.hydro.core.server.contract.workplace.SearchType;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;
import org.controlsfx.control.textfield.CustomTextField;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ChargeTimeFilterToolbar extends ToolBar
{
    @Inject
    private PreferencesManager preferencesManager;
    @Inject
    private NotifyManager notifyManager;

    private ComboBox<String> filterSelection;
    private HBox durationFilter;
    private DateTimePicker from;
    private DateTimePicker to;
    private HBox chargeDataFilter;
    private CustomTextField charge;

    public void initialize()
    {
        filterSelection = new ComboBox<>();

        durationFilter = new HBox();
        durationFilter.setAlignment( Pos.CENTER );
        durationFilter.setSpacing( 5.0 );
        Label fromLabel = new Label( "from" );
        from = new DateTimePicker();
        from.setOrientation( Orientation.HORIZONTAL );
        Label toLabel = new Label( "to" );
        to = new DateTimePicker();
        to.setOrientation( Orientation.HORIZONTAL );
        durationFilter.getChildren().addAll( fromLabel, from, toLabel, to );

        chargeDataFilter = new HBox();
        chargeDataFilter.setAlignment( Pos.CENTER );
        chargeDataFilter.setSpacing( 5.0 );
        Label chargeLabel = new Label( "Charge" );
        chargeLabel.setTextAlignment( TextAlignment.RIGHT );
        charge = new CustomTextField();
        charge.setPromptText( "Bitte eingeben" );
        chargeDataFilter.getChildren().addAll( chargeLabel, charge );

        this.getItems().addAll( filterSelection, durationFilter, chargeDataFilter );

        TextFieldUtil.setupClearButtonField( charge, charge.rightProperty() );
        // TextFieldUtil.installEnterButtonNode( charge, reload );

        from.clear();
        to.clear();

        filterSelection.getItems().addAll( "Zeitraum", "Charge" );
        filterSelection.getSelectionModel().selectedIndexProperty().addListener( ( p, o, n ) -> {
            preferencesManager.setIntValue( PreferencesManager.SCOPE_USER, "/casting/prod/processDocu", "lastFilterSelectionIndex", n.intValue() );
            if ( n.intValue() == 0 )
            {
                if ( !this.getItems().contains( durationFilter ) )
                {
                    this.getItems().add( durationFilter );
                    from.requestFocus();
                }
                this.getItems().remove( chargeDataFilter );
            }
            else if ( n.intValue() == 1 )
            {
                if ( !this.getItems().contains( chargeDataFilter ) )
                {
                    this.getItems().add( chargeDataFilter );
                }
                this.getItems().remove( durationFilter );
            }
        } );
        final int filterSelectionIndex = preferencesManager.getIntValue( PreferencesManager.SCOPE_USER, "/casting/prod/processDocu", "lastFilterSelectionIndex" );
        filterSelection.getSelectionModel().clearAndSelect( filterSelectionIndex );
    }

    public Map<String, Object> getParameters()
    {
        Map<String, Object> parameters = new HashMap<>();

        if ( filterSelection.getSelectionModel().getSelectedIndex() == 0 )
        {
            // get from
            LocalDateTime fromLDT = from.getLocalDateTime();
            if ( fromLDT == null )
            {
                LocalDate fromLD = from.getLocalDate();
                if ( fromLD != null )
                {
                    fromLDT = fromLD.atStartOfDay();
                }
                if ( fromLDT == null )
                {
                    notifyManager.showInfoMessage( "Suchfehler", "Die 'Von' Zeit ist nicht besetzt" );
                    return null;
                }
            }
            parameters.put( "fromDateTime", fromLDT );

            LocalDateTime toLDT = to.getLocalDateTime();
            if ( toLDT == null )
            {
                LocalDate toLD = to.getLocalDate();
                if ( toLD != null )
                {
                    toLDT = toLD.atStartOfDay().plusHours( 24 );
                }
            }
            if ( toLDT != null )
            {
                parameters.put( "toDateTime", toLDT );
            }
        }
        else
        {
            if ( StringTools.isFilled( charge.getText() ) )
            {
                parameters.put( "charge", "%" + charge.getText() + "%" );
            }
        }

        return parameters;
    }

    public SearchType getSearchType()
    {
        SearchType searchType = null;

        if ( filterSelection.getSelectionModel().getSelectedIndex() == 0 )
        {
            searchType = SearchType.TIME_RANGE;
        }
        else if ( StringTools.isFilled( charge.getText() ) )
        {
            searchType = SearchType.CHARGE;
        }

        return searchType;
    }

    public boolean isChargeFieldFilled()
    {
        return StringTools.isFilled( charge.getText() );
    }

    public String getChargeValue()
    {
        return charge.getText();
    }

    public void bindButtonToCharge( Button button )
    {
        TextFieldUtil.installEnterButtonNode( charge, button );
    }
}
