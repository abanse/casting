package com.hydro.casting.gui.prod.control;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

import java.util.ArrayList;
import java.util.List;

import static com.hydro.casting.server.contract.dto.InspectionValueDTO.*;
public class VisualInspectionSlabControlController implements InvalidationListener
{
    @FXML
    public CheckBox checkBoxOk;
    @FXML
    public CheckBox checkBoxNok;
    @FXML
    public ComboBox<String> comboBoxIssueType;

    private final IntegerProperty value = new SimpleIntegerProperty( RESULT_NOT_FILLED );
    private final StringProperty issue = new SimpleStringProperty( null );

    private final ObservableList<String> issueTypes = FXCollections.observableArrayList( "Eckenriss", "Querriss", "Keramikfilterblocker", "Technische Probleme", "Längsriss", "Ausläufer", "Kaltlauf",
            "Walzbarren krumm", "Markierungen", "Düsenprobleme", "Stopfenprobleme", "Analysenprobleme", "Kernriss", "Verteilernetzprobleme", "Sir-Box-Probleme", "Limcamessung n.i.O.",
            "Barren zu kurz", "Metallstand in der Rinne n.i.O.", "Rinnentemperaturprobleme", "Kühlwasserprobleme", "Ti-Bor-Drahtprobleme", "Fremdkörpereinschluss", "Barren mechanisch beschädigt",
            "Versuchscharge", "Gusshalt", "Halt-Bedienungsmann" );

    private final BooleanProperty editable = new SimpleBooleanProperty( false );

    private final List<InvalidationListener> externalInvalidationListeners = new ArrayList<>();

    @FXML
    private void initialize()
    {
        comboBoxIssueType.setItems( issueTypes );

        checkBoxOk.mouseTransparentProperty().bind( editable.not() );
        checkBoxOk.selectedProperty().addListener( this );
        checkBoxNok.mouseTransparentProperty().bind( editable.not() );
        checkBoxNok.selectedProperty().addListener( this );
        comboBoxIssueType.mouseTransparentProperty().bind( editable.not() );
        comboBoxIssueType.disableProperty().bind( checkBoxNok.selectedProperty().not() );
        comboBoxIssueType.valueProperty().bindBidirectional( issue );
    }

    @Override
    public void invalidated( Observable observable )
    {
        if ( checkBoxOk.isSelected() )
        {
            value.set( RESULT_OK );
            comboBoxIssueType.getSelectionModel().clearSelection();
            comboBoxIssueType.setValue( null );
        }
        else if ( checkBoxNok.isSelected() )
        {
            value.set( RESULT_FAILED );
            comboBoxIssueType.requestFocus();
        }
    }

    public void addInvalidationListenerToExternalListeners( InvalidationListener listener )
    {
        externalInvalidationListeners.add( listener );
    }

    public void installExternalListeners()
    {
        externalInvalidationListeners.forEach( value::addListener );
    }

    public void uninstallExternalListeners()
    {
        externalInvalidationListeners.forEach( value::removeListener );
    }

    public void checkboxClickedOk( ActionEvent actionEvent )
    {
        checkBoxNok.setSelected( !checkBoxOk.isSelected() );
    }

    public void checkboxClickedNok( ActionEvent actionEvent )
    {
        checkBoxOk.setSelected( !checkBoxNok.isSelected() );
    }

    private void updateCheckboxes()
    {
        if ( this.value.intValue() == RESULT_OK )
        {
            checkBoxOk.setSelected( true );
            checkBoxNok.setSelected( false );
        }
        else if ( this.value.intValue() == RESULT_FAILED )
        {
            checkBoxOk.setSelected( false );
            checkBoxNok.setSelected( true );
        }
        else
        {
            checkBoxOk.setSelected( false );
            checkBoxNok.setSelected( false );
        }
    }

    public void setValue( int value )
    {
        this.value.set( value );
        updateCheckboxes();
    }

    public IntegerProperty getValueProperty()
    {
        return value;
    }

    public int getValue()
    {
        return value.get();
    }

    public void setIssue( String issue )
    {
        if ( issue.equals( "null" ) )
        {
            this.issue.set( null );
        }
        else
        {
            this.issue.set( issue );
        }
    }

    public StringProperty getIssueProperty()
    {
        return this.issue;
    }

    public String getIssue()
    {
        String issue = this.issue.get();
        if ( issue == null )
        {
            return "null";
        }

        return issue;
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
}
