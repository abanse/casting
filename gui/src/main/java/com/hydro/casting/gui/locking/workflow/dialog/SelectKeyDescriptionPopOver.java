package com.hydro.casting.gui.locking.workflow.dialog;

import com.hydro.core.server.contract.workplace.dto.KeyDescriptionDTO;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.PopOver;

import java.util.List;

public class SelectKeyDescriptionPopOver extends PopOver
{
    public SelectKeyDescriptionPopOver( String title, List<KeyDescriptionDTO> values, KeyDescriptionDTO currentValue, Node sourceNode, Callback<String, Boolean> okCallback )
    {
        setTitle( title );
        setDetachable( false );
        setArrowLocation( ArrowLocation.BOTTOM_LEFT );
        setHeaderAlwaysVisible( true );

        ComboBox<KeyDescriptionDTO> comboBox = new ComboBox<>( FXCollections.observableArrayList( values ) );
        if ( currentValue != null )
        {
            comboBox.getSelectionModel().select( currentValue );
        }
        comboBox.setConverter( new StringConverter<KeyDescriptionDTO>()
        {
            @Override
            public String toString( KeyDescriptionDTO object )
            {
                if ( object == null )
                {
                    return null;
                }
                return object.getKey() + " - " + object.getDescription();
            }

            @Override
            public KeyDescriptionDTO fromString( String string )
            {
                if ( string == null )
                {
                    return null;
                }
                for ( KeyDescriptionDTO keyDescriptionDTO : values )
                {
                    if ( string.startsWith( keyDescriptionDTO.getKey() ) )
                    {
                        return keyDescriptionDTO;
                    }
                }
                return null;
            }
        } );

        // funktioniert leider nicht
        // AutoCompleteComboBox.autoCompleteComboBoxPlus( comboBox, ( typedText, itemToCompare ) -> {
        // if ( itemToCompare == null )
        // {
        // return false;
        // }
        // final String toSearch = itemToCompare.getKey() + itemToCompare.getDescription();
        // return toSearch.toLowerCase().contains( typedText.toLowerCase() );
        // } );

        BorderPane mainBorderPane = new BorderPane();
        BorderPane.setMargin( comboBox, new Insets( 5 ) );
        mainBorderPane.setCenter( comboBox );

        Button saveButton = new Button( "OK" );
        saveButton.setPrefWidth( 100 );
        saveButton.setDefaultButton( true );
        Button closeButton = new Button( "Abbrechen" );
        closeButton.setCancelButton( true );
        closeButton.setPrefWidth( 100 );
        ButtonBar buttonBar = new ButtonBar();
        buttonBar.setPadding( new Insets( 5, 5, 5, 5 ) );
        buttonBar.getButtons().add( saveButton );
        buttonBar.getButtons().add( closeButton );

        mainBorderPane.setBottom( buttonBar );

        setContentNode( mainBorderPane );
        saveButton.setOnAction( ( event ) -> {
            if ( okCallback.call( comboBox.getSelectionModel().getSelectedItem().getKey() ) )
            {
                hide();
            }
        } );
        closeButton.setOnAction( ( event ) -> {
            hide();
        } );

        show( sourceNode );
    }
}
