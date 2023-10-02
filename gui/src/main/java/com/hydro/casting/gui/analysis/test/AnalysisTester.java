package com.hydro.casting.gui.analysis.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hydro.casting.gui.analysis.control.AnalysisTable;
import com.hydro.casting.gui.model.Analysis;
import com.hydro.casting.gui.model.Composition;
import com.hydro.casting.gui.model.CompositionElement;
import com.hydro.casting.gui.model.Specification;
import com.hydro.core.common.product.COREProductDefinition;
import com.hydro.core.gui.module.ApplicationModule;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AnalysisTester extends Application
{

    public static void main( String[] args )
    {
        launch( args );
    }

    @Override
    public void start( Stage primaryStage )
    {
        final AnalysisTable analysisTable = new AnalysisTable();

        final Analysis testAnalysis = new Analysis();
        testAnalysis.setName( "3104-E 57543 01" );
        final CompositionElement alCE = new CompositionElement();
        alCE.setName( "AL" );
        alCE.setDecimalPlaces( 2 );
        alCE.setOriginalElementValue( 95.0 );
        testAnalysis.addCompositionElement( alCE );
        final CompositionElement mgCE = new CompositionElement();
        mgCE.setName( "MG" );
        mgCE.setDecimalPlaces( 4 );
        mgCE.setOriginalElementValue( 0.00016543 );
        testAnalysis.addCompositionElement( mgCE );
        final CompositionElement feCE = new CompositionElement();
        feCE.setName( "FE" );
        feCE.setDecimalPlaces( 2 );
        feCE.setOriginalElementValue( 5.324785372689 );
        testAnalysis.addCompositionElement( feCE );

        final Specification testSpec = new Specification();
        testSpec.setName( "3104-E" );
        final Composition minValues = new Composition();
        minValues.setName( "3104-E Min" );
        final CompositionElement alMinCE = new CompositionElement();
        alMinCE.setName( "AL" );
        alMinCE.setDecimalPlaces( 2 );
        alMinCE.setOriginalElementValue( 90 );
        minValues.addCompositionElement( alMinCE );
        final CompositionElement mgMinCE = new CompositionElement();
        mgMinCE.setName( "MG" );
        mgMinCE.setDecimalPlaces( 4 );
        mgMinCE.setOriginalElementValue( 0.01 );
        minValues.addCompositionElement( mgMinCE );
        testSpec.setMin( minValues );
        final Composition maxValues = new Composition();
        maxValues.setName( "3104-E Max" );
        final CompositionElement alMaxCE = new CompositionElement();
        alMaxCE.setName( "AL" );
        alMaxCE.setDecimalPlaces( 2 );
        alMaxCE.setOriginalElementValue( 97 );
        maxValues.addCompositionElement( alMaxCE );
        testSpec.setMax( maxValues );

        analysisTable.setAnalysis( testAnalysis, testSpec, false );

        Injector injector = Guice.createInjector( new ApplicationModule() );
        injector.injectMembers( analysisTable );

        analysisTable.setPrefSize( 1200, 700 );
        Scene scene = new Scene( analysisTable );
        scene.getStylesheets().add( getClass().getResource( COREProductDefinition.getInstance().getStylesheet() ).toExternalForm() );
        primaryStage.setScene( scene );
        primaryStage.show();
    }
}
