package com.hydro.casting.gui.reporting.view;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.Map;

public class OutputDetailSummaryController
{
    @FXML
    private TitledPane pane;

    @FXML
    private GridPane content;

    @FXML
    private VBox descriptionBox;

    @FXML
    private VBox valueBox;

    @FXML
    private VBox descriptionBoxDetail;

    @FXML
    private VBox valueBoxDetail;

    @FXML
    private Hyperlink displayDetails;

    @FXML
    private GridPane detailPane;

    private boolean detailsVisible = false;
    private Map<Node, Node> summaries = new LinkedHashMap<>();
    private Map<Node, Node> summariesDetail = new LinkedHashMap<>();

    @FXML
    private void initialize()
    {
        //        descriptionBox.setPadding( new Insets( 0, 2.5, 0, 0 ) );
        //        content.getChildren().remove( displayDetails );
        //        detailPane.setVisible( false );
        content.getChildren().remove( detailPane );
    }

    public void addNewOutputSummary( String description, String value, boolean isDetail )
    {
        Label descriptionLabel = new Label( description );
        descriptionLabel.setAlignment( Pos.CENTER_RIGHT );
        descriptionLabel.setMaxWidth( Double.MAX_VALUE );
        Label valueLabel = new Label( value );
        valueLabel.setAlignment( Pos.CENTER );
        valueLabel.setMaxWidth( Double.MAX_VALUE );

        if ( !isDetail )
        {
            summaries.put( descriptionLabel, valueLabel );
        }
        else
        {
            summariesDetail.put( descriptionLabel, valueLabel );
        }
        addSummaries();
    }

    private void addSummaries()
    {
        descriptionBox.getChildren().clear();
        valueBox.getChildren().clear();
        descriptionBoxDetail.getChildren().clear();
        valueBoxDetail.getChildren().clear();
        summaries.forEach( ( desc, value ) -> {
            descriptionBox.getChildren().add( desc );
            valueBox.getChildren().add( value );
        } );
        if ( !summariesDetail.isEmpty() )
        {
            summariesDetail.forEach( ( desc, value ) -> {
                descriptionBoxDetail.getChildren().add( desc );
                valueBoxDetail.getChildren().add( value );
            } );
            if ( !content.getChildren().contains( displayDetails ) )
            {
                content.getChildren().add( displayDetails );
            }
        }
        else
        {
            content.getChildren().remove( displayDetails );
        }
    }

    @FXML
    private void displayDetails()
    {
        if ( detailsVisible )
        {
            //            detailPane.setVisible( false );
            content.getChildren().remove( detailPane );
            displayDetails.setText( "Details anzeigen" );
            detailsVisible = false;
        }
        else
        {
            //            detailPane.setVisible( true );
            content.getChildren().add( detailPane );
            displayDetails.setText( "Details ausblenden" );
            detailsVisible = true;
        }
    }

    public void setText( String text )
    {
        pane.setText( text );
    }

    public void setPrefWidth( double prefWidth )
    {
        pane.setPrefWidth( prefWidth );
    }
}
