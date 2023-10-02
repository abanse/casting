package com.hydro.casting.gui.prod.dialog;

import com.google.inject.Inject;
import com.hydro.casting.gui.prod.task.ModifyKPIOutputTargetsTask;
import com.hydro.casting.gui.reporting.data.ReportingCacheDataProvider;
import com.hydro.core.gui.TaskManager;
import com.hydro.core.gui.comp.IntegerTextField;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import org.controlsfx.control.PopOver;

import java.util.Map;

public class ModifyKPIOutputTargetsPopOver extends PopOver
{
    Callback<Boolean, Void> reloadCallback;
    @Inject
    private TaskManager taskManager;
    @Inject
    private ModifyKPIOutputTargetsTask modifyKPIOutputTargetsTask;

    private IntegerTextField shiftTargetOutput = new IntegerTextField();
    private IntegerTextField weekTargetOutput = new IntegerTextField();
    private IntegerTextField monthTargetOutput = new IntegerTextField();
    private ButtonBar buttonBar;
    private Button saveButton;
    private Button closeButton;

    @Inject
    public void initialize()
    {
        setTitle( "KPI Zielvorgaben ändern" );
        setDetachable( false );
        setArrowLocation( ArrowLocation.LEFT_BOTTOM );
        setHeaderAlwaysVisible( true );

        BorderPane mainBorderPane = new BorderPane();

        GridPane dataPane = new GridPane();
        dataPane.setHgap( 5.0 );
        dataPane.setVgap( 5.0 );
        final ColumnConstraints leftColumnC = new ColumnConstraints();
        leftColumnC.setFillWidth( false );
        leftColumnC.setHgrow( Priority.ALWAYS );
        leftColumnC.setHalignment( HPos.RIGHT );
        leftColumnC.setPrefWidth( 150 );
        final ColumnConstraints righColumnC = new ColumnConstraints();
        righColumnC.setFillWidth( false );
        righColumnC.setHgrow( Priority.ALWAYS );
        righColumnC.setHalignment( HPos.LEFT );
        righColumnC.setPrefWidth( 150 );
        dataPane.getColumnConstraints().addAll( leftColumnC, righColumnC );

        final Label shiftLabel = new Label( "Zielvorgabe pro Schicht" );
        shiftTargetOutput.setPrefWidth( 60 );
        GridPane.setColumnIndex( shiftTargetOutput, 1 );
        dataPane.getChildren().addAll( shiftLabel, shiftTargetOutput );
        final Label weekLabel = new Label( "Zielvorgabe pro Woche" );
        weekTargetOutput.setPrefWidth( 60 );
        GridPane.setRowIndex( weekLabel, 1 );
        GridPane.setColumnIndex( weekTargetOutput, 1 );
        GridPane.setRowIndex( weekTargetOutput, 1 );
        dataPane.getChildren().addAll( weekLabel, weekTargetOutput );
        final Label monthLabel = new Label( "Zielvorgabe pro Monat" );
        monthTargetOutput.setPrefWidth( 60 );
        GridPane.setRowIndex( monthLabel, 2 );
        GridPane.setColumnIndex( monthTargetOutput, 1 );
        GridPane.setRowIndex( monthTargetOutput, 2 );
        dataPane.getChildren().addAll( monthLabel, monthTargetOutput );

        BorderPane.setMargin( dataPane, new Insets( 5 ) );
        mainBorderPane.setCenter( dataPane );

        saveButton = new Button( "Speichern" );
        saveButton.setPrefWidth( 100 );
        closeButton = new Button( "Schließen" );
        closeButton.setCancelButton( true );
        closeButton.setPrefWidth( 100 );
        buttonBar = new ButtonBar();
        buttonBar.setPadding( new Insets( 5, 5, 5, 5 ) );
        buttonBar.getButtons().add( closeButton );
        buttonBar.getButtons().add( saveButton );
        saveButton.setDefaultButton( true );

        mainBorderPane.setBottom( buttonBar );

        setContentNode( mainBorderPane );

        modifyKPIOutputTargetsTask.setParentPopOver( this );

        saveButton.setOnAction( ( event ) -> {
            modifyKPIOutputTargetsTask.setData( shiftTargetOutput.getIntValue(), weekTargetOutput.getIntValue(), monthTargetOutput.getIntValue() );
            taskManager.executeTask( modifyKPIOutputTargetsTask );
        } );
        closeButton.setOnAction( ( event ) -> {
            hide();
        } );
    }

    public final void showPopOver( Node owner, Callback<Boolean, Void> reloadCallback )
    {
        this.reloadCallback = reloadCallback;
        modifyKPIOutputTargetsTask.setRefreshCallback( reloadCallback );
        show( owner );
    }

    public void setDataForCostCenter( String costCenter, ReportingCacheDataProvider dataProvider )
    {
        setTitle( "KPI Zielvorgaben ändern (" + costCenter + ")" );
        modifyKPIOutputTargetsTask.setCostCenter( costCenter );
        if ( dataProvider == null )
        {
            return;
        }
        final Map<String, Integer> outputTargets = dataProvider.getKPIOutputTargets( costCenter );
        if ( outputTargets == null )
        {
            return;
        }
        shiftTargetOutput.setIntValue( outputTargets.get( "shift" ) );
        weekTargetOutput.setIntValue( outputTargets.get( "week" ) );
        monthTargetOutput.setIntValue( outputTargets.get( "month" ) );
    }
}
