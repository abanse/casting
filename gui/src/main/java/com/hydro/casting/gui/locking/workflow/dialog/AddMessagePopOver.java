package com.hydro.casting.gui.locking.workflow.dialog;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.PopOver;

import java.util.concurrent.SynchronousQueue;

public class AddMessagePopOver extends PopOver
{
    private StringProperty message = new SimpleStringProperty();
    private TextArea messageTA;

    @Inject
    public void initilize()
    {
        setTitle( "Maßnahme hinzufügen" );
        setDetachable( false );
        setArrowLocation( ArrowLocation.TOP_RIGHT );
        setHeaderAlwaysVisible( true );

        messageTA = new TextArea();
        messageTA.setPrefWidth( 350 );
        messageTA.setPrefHeight( 250 );
        messageTA.setWrapText( true );

        BorderPane mainBorderPane = new BorderPane();
        BorderPane.setMargin( messageTA, new Insets( 5 ) );
        mainBorderPane.setCenter( messageTA );

        Button saveButton = new Button( "Hinzufügen" );
        saveButton.setPrefWidth( 100 );
        saveButton.setDefaultButton( true );
        Button closeButton = new Button( "Schließen" );
        closeButton.setCancelButton( true );
        closeButton.setPrefWidth( 100 );
        ButtonBar buttonBar = new ButtonBar();
        buttonBar.setPadding( new Insets( 5, 5, 5, 5 ) );
        buttonBar.getButtons().add( saveButton );
        buttonBar.getButtons().add( closeButton );

        mainBorderPane.setBottom( buttonBar );

        setContentNode( mainBorderPane );
        saveButton.setOnAction( ( event ) -> {
            message.set( messageTA.getText() );
            hide();
        } );
        closeButton.setOnAction( ( event ) -> {
            hide();
        } );
    }

    // Achtung läuft im WorkerThread
    public synchronized String getMessage( Node sourceNode )
    {
        final SynchronousQueue<String> synchronousQueue = new SynchronousQueue<>();
        Platform.runLater( () -> {
            message.set( "" );
            messageTA.clear();
            show( sourceNode );
            Platform.runLater( () -> {
                messageTA.requestFocus();                
            });
        } );

        try
        {
            return synchronousQueue.take();
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
        return null;
    }   
    
}
