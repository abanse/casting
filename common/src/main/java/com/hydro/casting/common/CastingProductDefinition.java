package com.hydro.casting.common;

import com.hydro.core.common.product.ProductDefinition;
import com.hydro.core.common.product.client.ExplorerContent;
import com.hydro.core.common.product.client.SplashExtension;
import com.hydro.core.common.product.client.impl.ExplorerContentImpl;
import com.hydro.core.common.product.client.impl.SplashExtensionImpl;
import com.hydro.core.common.product.server.ConnectionDefinition;
import com.hydro.core.common.product.server.impl.ConnectionDefinitionImpl;

import java.util.ArrayList;
import java.util.List;

public class CastingProductDefinition implements ProductDefinition
{
    private static CastingProductDefinition instance;

    private String name = "Gießerei";
    private String version = "1.0.0";
    private String pakage = "com.hydro.casting";
    private String appName = "rheinwerk.server.ear";
    private String moduleName = "casting.server.ejb";
    private String clientPackage = "com.hydro.casting.gui";
    private String styleSheet = "/com/hydro/casting/gui/casting.css";
    private String splashImageURL = "/com/hydro/casting/gui/images/splashCasting.png";

    private ConnectionDefinition connectionDefinition;

    private List<ExplorerContent> explorerContent = new ArrayList<>();

    private SplashExtension splashExtension;

    private CastingProductDefinition()
    {
        connectionDefinition = new ConnectionDefinitionImpl( pakage, appName, moduleName );

        ExplorerContentImpl allgemein = new ExplorerContentImpl( 1000, "Allgemein" );
        allgemein.addChild( new ExplorerContentImpl( 1100, "Prozess-Aufträge", SecurityCasting.MAIN.PRODUCTION_ORDER.VIEW ) );
        allgemein.addChild( new ExplorerContentImpl( 1200, "Material Stammdaten", SecurityCasting.MAIN.MATERIAL_TYPE_MAINTENANCE.VIEW ) );
        explorerContent.add( allgemein );

        ExplorerContentImpl lims = new ExplorerContentImpl( 2000, "LIMS" );
        lims.addChild( new ExplorerContentImpl( 2100, "Analysen", SecurityCasting.ANALYSIS.VIEW ) );
        lims.addChild( new ExplorerContentImpl( 2200, "Soll-Analysen", SecurityCasting.ALLOY.VIEW ) );
        lims.addChild( new ExplorerContentImpl( 2300, "Spezifikations-Verwaltung", "TODO" ) );

        explorerContent.add( lims );

        ExplorerContentImpl prod = new ExplorerContentImpl( 4000, "Produktion" );
        prod.addChild( new ExplorerContentImpl( 4100, "Produktion Gantt(GA)", SecurityCasting.PROD.CASTING_PRODUCTION_GANTT.VIEW ) );
        prod.addChild( new ExplorerContentImpl( 4200, "Chargen-Dokumentation", SecurityCasting.PROD.PROCESS_DOCU.VIEW ) );
        prod.addChild( new ExplorerContentImpl( 4300, "Gießanlage 50", SecurityCasting.PROD.CASTER_50.VIEW ) );
        prod.addChild( new ExplorerContentImpl( 4400, "Gießanlage 60", SecurityCasting.PROD.CASTER_60.VIEW ) );
        prod.addChild( new ExplorerContentImpl( 4500, "Gießanlage 70", SecurityCasting.PROD.CASTER_70.VIEW ) );
        prod.addChild( new ExplorerContentImpl( 4600, "Gießanlage 80", SecurityCasting.PROD.CASTER_80.VIEW ) );
        prod.addChild( new ExplorerContentImpl( 4700, "Kokillenbau", SecurityCasting.PROD.MOLD_DEPARTMENT.VIEW ) );
        prod.addChild( new ExplorerContentImpl( 4800, "Chargenrechner", SecurityCasting.PROD.CHARGING_51.VIEW ) );
        prod.addChild( new ExplorerContentImpl( 4900, "BDE Abkrätzen", SecurityCasting.PROD.MOBILE.SKIMMING.VIEW ) );
        explorerContent.add( prod );

        ExplorerContentImpl melting = new ExplorerContentImpl( 5000, "Schmelzbereich" );
        melting.addChild( new ExplorerContentImpl( 5100, "Schmelzofen S1", SecurityCasting.PROD.MELTER_S1.VIEW ) );
        melting.addChild( new ExplorerContentImpl( 5200, "Schmelzofen S2", SecurityCasting.PROD.MELTER_S2.VIEW ) );
        melting.addChild( new ExplorerContentImpl( 5300, "Gantt S1", SecurityCasting.MELTING.GANTT_S1.VIEW ) );
        melting.addChild( new ExplorerContentImpl( 5400, "Gantt S2", SecurityCasting.MELTING.GANTT_S2.VIEW ) );
        melting.addChild( new ExplorerContentImpl( 5500, "Fahrzeug BDE", SecurityCasting.MELTING.MOBILE.SKIMMING.VIEW ) );
        melting.addChild( new ExplorerContentImpl( 5600, "Chargen-Dokumentation", SecurityCasting.MELTING.PROCESS_DOCU.VIEW ) );
        melting.addChild( new ExplorerContentImpl( 5700, "Schmelzchargen S2", SecurityCasting.MELTING.CHARGES_S2.VIEW ) );
        melting.addChild( new ExplorerContentImpl( 5800, "Wissenstabellen", SecurityCasting.MELTING.KNOWLEDGE.VIEW ) );
        // melting.addChild( new ExplorerContentImpl( 5800, "MABE S1", SecurityCasting.MABE.MELTING_S1.VIEW ) );
        // melting.addChild( new ExplorerContentImpl( 5900, "MABE S2", SecurityCasting.MABE.MELTING_S2.VIEW ) );
        explorerContent.add( melting );

        ExplorerContentImpl stock = new ExplorerContentImpl( 6000, "Lager" );
        stock.addChild( new ExplorerContentImpl( 6100, "Barrenbestand", SecurityCasting.STOCK.SLAB.VIEW ) );
        stock.addChild( new ExplorerContentImpl( 6200, "Materialbestand", SecurityCasting.STOCK.STOCK_MATERIAL.VIEW ) );
        stock.addChild( new ExplorerContentImpl( 6300, "Tiegelbestand", SecurityCasting.STOCK.CRUCIBLE_MATERIAL.VIEW ) );
        explorerContent.add( stock );

        ExplorerContentImpl downtime = new ExplorerContentImpl( 7000, "Störzeiten" );
        downtime.addChild( new ExplorerContentImpl( 7100, "Störzeiten", SecurityCasting.DOWNTIME.HISTORY.VIEW ) );
        downtime.addChild( new ExplorerContentImpl( 7200, "Dashboard", SecurityCasting.DOWNTIME.DASHBOARD.VIEW ) );
        explorerContent.add( downtime );

        ExplorerContentImpl planning = new ExplorerContentImpl( 9000, "Arbeitsvorbereitung" );
        planning.addChild( new ExplorerContentImpl( 9100, "Gantt Gießanlagen", SecurityCasting.GANTT.CASTER.VIEW ) );
        planning.addChild( new ExplorerContentImpl( 9200, "Gantt Öfen", SecurityCasting.GANTT.FURNACE.VIEW ) );
        planning.addChild( new ExplorerContentImpl( 9300, "MABE Gießanlage 50", SecurityCasting.MABE.CASTER_50.VIEW ) );
        planning.addChild( new ExplorerContentImpl( 9400, "MABE Gießanlage 60", SecurityCasting.MABE.CASTER_60.VIEW ) );
        planning.addChild( new ExplorerContentImpl( 9500, "MABE Gießanlage 70", SecurityCasting.MABE.CASTER_70.VIEW ) );
        planning.addChild( new ExplorerContentImpl( 9600, "MABE Gießanlage 80", SecurityCasting.MABE.CASTER_80.VIEW ) );
        planning.addChild( new ExplorerContentImpl( 9900, "MABE Barrensäge", "TODO" ) );
        planning.addChild( new ExplorerContentImpl( 9910, "Gießerei Wissenstabelle", SecurityCasting.KNOWLEDGE.VIEW ) );
        planning.addChild( new ExplorerContentImpl( 9920, "Ressource-Kalender", SecurityCasting.MAIN.MACHINE_CALENDAR.VIEW ) );
        explorerContent.add( planning );

        ExplorerContentImpl qs = new ExplorerContentImpl( 10000, "Qualitätssicherung" );
        qs.addChild( new ExplorerContentImpl( 10100, "Sperrabwicklung", SecurityCasting.LOCKING.LOCKING_WORKFLOW.VIEW ) );
        //qs.addChild( new ExplorerContentImpl( 10200, "Sperrabwicklung (mit Historie)", SecurityCasting.LOCKING.LOCKING_WORKFLOW_HISTORY.VIEW ) );
        explorerContent.add( qs );

        /*
        ExplorerContentImpl einsatzplanung = new ExplorerContentImpl( 10000, "Einsatzplanung" );
        einsatzplanung.addChild( new ExplorerContentImpl( 10100, "Einsatzplanung (50)", SecurityCasting.CHARGING.CASTER_50.VIEW ) );
        einsatzplanung.addChild( new ExplorerContentImpl( 10200, "Einsatzplanung (60)", SecurityCasting.CHARGING.CASTER_60.VIEW ) );
        einsatzplanung.addChild( new ExplorerContentImpl( 10300, "Einsatzplanung (70)", SecurityCasting.CHARGING.CASTER_70.VIEW ) );
        einsatzplanung.addChild( new ExplorerContentImpl( 10400, "Einsatzplanung (80)", SecurityCasting.CHARGING.CASTER_80.VIEW ) );
        explorerContent.add( einsatzplanung );
         */


        /*

        ExplorerContentImpl brazing = new ExplorerContentImpl( 2000, "Brazing-Bau" );
        brazing.addChild( new ExplorerContentImpl( 2001, "Barrenbestand & Brazingpakete", "scheduling.ingot.view" ) );
        brazing.addChild( new ExplorerContentImpl( 2100, "Platinen-Säge (60)", "scheduling.60.view" ) );
        brazing.addChild( new ExplorerContentImpl( 2200, "Platinen-Säge Anzeige(60)", "scheduling.60.history.view" ) );
        brazing.addChild( new ExplorerContentImpl( 2300, "Brazing (61)", "scheduling.61.view" ) );
        brazing.addChild( new ExplorerContentImpl( 2400, "Brazing Anzeige(61)", "scheduling.61.history.view" ) );
        explorerContent.add( brazing );
        
        ExplorerContentImpl warmwalzen = new ExplorerContentImpl( 3000, "Warmwalzen" );
        warmwalzen.addChild( new ExplorerContentImpl( 3001, "Barrenbestand & Brazingpakete", "scheduling.ingot.view" ) );
        warmwalzen.addChild( new ExplorerContentImpl( 3100, "Fräse (52)", "scheduling.52.view" ) );
        warmwalzen.addChild( new ExplorerContentImpl( 3200, "Fräse Anzeige(52)", "scheduling.52.history.view" ) );
        warmwalzen.addChild( new ExplorerContentImpl( 3300, "Vorwärmofen 1 (62)", "scheduling.62.view" ) );
        warmwalzen.addChild( new ExplorerContentImpl( 3400, "Vorwärmofen 1 Anzeige (62)", "scheduling.62.history.view" ) );
        warmwalzen.addChild( new ExplorerContentImpl( 3500, "Vorwärmofen 4 (64)", "scheduling.64.view" ) );
        warmwalzen.addChild( new ExplorerContentImpl( 3600, "Vorwärmofen 4 Anzeige (64)", "scheduling.64.history.view" ) );
        warmwalzen.addChild( new ExplorerContentImpl( 3700, "Warmwalze (66)", "scheduling.66.view" ) );
        warmwalzen.addChild( new ExplorerContentImpl( 3800, "Warmwalze Anzeige (66)", "scheduling.66.history.view" ) );
        warmwalzen.addChild( new ExplorerContentImpl( 3900, "Warmwalzplanung", "scheduling.66.time.table.view" ) );
        explorerContent.add( warmwalzen );
        
        ExplorerContentImpl kaltwalzen = new ExplorerContentImpl( 4000, "Kaltwalzen" );
        kaltwalzen.addChild( new ExplorerContentImpl( 4100, "Hamburg 1 (69)", "scheduling.69.view" ) );
        kaltwalzen.addChild( new ExplorerContentImpl( 4200, "Hamburg 1 1.Walzer (69)", "scheduling.69.cabin1.view" ) );
        kaltwalzen.addChild( new ExplorerContentImpl( 4300, "Hamburg 1 2.Walzer (69)", "scheduling.69.cabin2.view" ) );
        kaltwalzen.addChild( new ExplorerContentImpl( 4400, "Hamburg 1 Waagenmann (69)", "scheduling.69.wm.view" ) );
        kaltwalzen.addChild( new ExplorerContentImpl( 4500, "Hamburg 1 Anzeige (69)", "scheduling.69.history.view" ) );
        kaltwalzen.addChild( new ExplorerContentImpl( 4600, "Hamburg 2 (72)", "scheduling.72.view" ) );
        kaltwalzen.addChild( new ExplorerContentImpl( 4700, "Hamburg 2 Anzeige (72)", "scheduling.72.history.view" ) );
        explorerContent.add( kaltwalzen );
        
        ExplorerContentImpl bundglüherei = new ExplorerContentImpl( 5000, "Bundglüherei" );
        bundglüherei.addChild( new ExplorerContentImpl( 5100, "Bundglühöfen Übersicht", "scheduling.74.overview" ) );
        bundglüherei.addChild( new ExplorerContentImpl( 5150, "Bundglüherei", "annealing.overview.view" ) );
        bundglüherei.addChild( new ExplorerContentImpl( 5200, "Bundglühofen 1 (74)", "scheduling.74.view" ) );
        bundglüherei.addChild( new ExplorerContentImpl( 5300, "GL1 Anzeige (74)", "scheduling.74.history.view" ) );
        bundglüherei.addChild( new ExplorerContentImpl( 5400, "Bundglühofen 2 (75)", "scheduling.75.view" ) );
        bundglüherei.addChild( new ExplorerContentImpl( 5500, "GL2 Anzeige (75)", "scheduling.75.history.view" ) );
        bundglüherei.addChild( new ExplorerContentImpl( 5600, "Bundglühofen 3 (77)", "scheduling.77.view" ) );
        bundglüherei.addChild( new ExplorerContentImpl( 5700, "GL3 Anzeige (77)", "scheduling.77.history.view" ) );
        bundglüherei.addChild( new ExplorerContentImpl( 5800, "Bundglühofen 4 (76)", "scheduling.76.view" ) );
        bundglüherei.addChild( new ExplorerContentImpl( 5900, "GL4 Anzeige (76)", "scheduling.76.history.view" ) );        
        bundglüherei.addChild( new ExplorerContentImpl( 5950, "Coil-Extern (67)", "scheduling.67.view" ) );
        explorerContent.add( bundglüherei );
        
        ExplorerContentImpl adjustage = new ExplorerContentImpl( 6000, "Adjustage" );        
        adjustage.addChild( new ExplorerContentImpl( 6100, "Längsteilanlage 1 (78)", "scheduling.78.view" ) );
        adjustage.addChild( new ExplorerContentImpl( 6150, "LT1 Anzeige (78)", "scheduling.78.history.view" ) );
        adjustage.addChild( new ExplorerContentImpl( 6200, "Längsteilanlage 2 (79)", "scheduling.79.view" ) );
        adjustage.addChild( new ExplorerContentImpl( 6250, "LT2 Anzeige (79)", "scheduling.79.history.view" ) );
        adjustage.addChild( new ExplorerContentImpl( 6300, "Längsteilanlage 3 (80)", "scheduling.80.view" ) );
        adjustage.addChild( new ExplorerContentImpl( 6350, "LT3 Anzeige (80)", "scheduling.80.history.view" ) );
        adjustage.addChild( new ExplorerContentImpl( 6400, "Querteilanlage 1 (81)", "scheduling.81.view" ) );
        adjustage.addChild( new ExplorerContentImpl( 6450, "QT1 Anzeige (81)", "scheduling.81.history.view" ) );
        adjustage.addChild( new ExplorerContentImpl( 6500, "Querteilanlage 2 (82)", "scheduling.82.view" ) );
        adjustage.addChild( new ExplorerContentImpl( 6550, "QT2 Anzeige (82)", "scheduling.82.history.view" ) );
        adjustage.addChild( new ExplorerContentImpl( 6600, "Reckanlage (92)", "scheduling.92.view" ) );
        adjustage.addChild( new ExplorerContentImpl( 6650, "RE Anzeige (92)", "scheduling.92.history.view" ) );
        explorerContent.add( adjustage );
        
        ExplorerContentImpl packerei = new ExplorerContentImpl( 7000, "Packerei" );
        packerei.addChild( new ExplorerContentImpl( 7100, "Packerei Packlinie (85-PL)", "scheduling.85PL.view" ) );
        packerei.addChild( new ExplorerContentImpl( 7200, "Packerei Stretcher (85-P1)", "scheduling.85P1.view" ) );
        packerei.addChild( new ExplorerContentImpl( 7300, "Packerei Waage-1 (85-P2)", "scheduling.85P2.view" ) );
        packerei.addChild( new ExplorerContentImpl( 7400, "Packerei Waage-2 (85-P3)", "scheduling.85P3.view" ) );
        packerei.addChild( new ExplorerContentImpl( 7500, "Barren verpacken (85-PB)", "scheduling.85PB.view" ) );
        packerei.addChild( new ExplorerContentImpl( 7600, "Packereiplanung HRL", "scheduling.hbs.view" ) );
        packerei.addChild( new ExplorerContentImpl( 7700, "Packerei Anzeige (85)", "scheduling.85.history.view" ) );
        packerei.addChild( new ExplorerContentImpl( 7800, "Palettencodes", "scheduling.palette-typ.view" ) );
        explorerContent.add( packerei );
        
        ExplorerContentImpl visualisierung = new ExplorerContentImpl( 8000, "Visualisierung" );
        visualisierung.addChild( new ExplorerContentImpl( 8100, "LT3 Scherenbau", "slitter.80.visu.view" ) );
        explorerContent.add( visualisierung );
        


        ExplorerContentImpl walzenschleiferei = new ExplorerContentImpl( 11000, "Walzenschleiferei" );
        walzenschleiferei.addChild( new ExplorerContentImpl( 11100, "Naxos (30)", "equipment.30.view" ) );
        walzenschleiferei.addChild( new ExplorerContentImpl( 11200, "Giustina (31)", "equipment.31.view" ) );
        walzenschleiferei.addChild( new ExplorerContentImpl( 11300, "Lagerbau (32)", "equipment.32.view" ) );
        walzenschleiferei.addChild( new ExplorerContentImpl( 11400, "Walzen-Verwaltung", "equipment.maintenance.view" ) );
        walzenschleiferei.addChild( new ExplorerContentImpl( 11500, "Walzen-Historie", "equipment.history.view" ) );
        walzenschleiferei.addChild( new ExplorerContentImpl( 11600, "Walzen-Einsatz", "equipment.usage.view" ) );
        explorerContent.add( walzenschleiferei );
        
        ExplorerContentImpl verwaltung = new ExplorerContentImpl( 12000, "Verwaltung" );
        verwaltung.addChild( new ExplorerContentImpl( 12100, "QS-Hinweise", "scheduling.qs_comment.view" ) );
        verwaltung.addChild( new ExplorerContentImpl( 12200, "Sperrabwicklung", "locking-workflow.view" ) );
        verwaltung.addChild( new ExplorerContentImpl( 12300, "Sperrabwicklung (mit Historie)", "locking-workflow-history.view" ) );
        verwaltung.addChild( new ExplorerContentImpl( 12400, "Störzeitübersicht", "downtime.history.view" ) );
        verwaltung.addChild( new ExplorerContentImpl( 12500, "Produktionshinweise", "prodinstructions.view" ) );
        verwaltung.addChild( new ExplorerContentImpl( 12600, "Qualitätsrichtlinien", "qrl.view" ) );
        explorerContent.add( verwaltung );
        
        ExplorerContentImpl wartung = new ExplorerContentImpl( 13000, "Wartung" );
        wartung.addChild( new ExplorerContentImpl( 13200, "OIS-Daten", "maintenance.ois.view" ) );
        wartung.addChild( new ExplorerContentImpl( 13300, "Administration", "administration.view" ) );
        explorerContent.add( wartung );

         */

        splashExtension = new SplashExtensionImpl( splashImageURL, 100 );
    }

    public final static CastingProductDefinition getInstance()
    {
        if ( instance == null )
        {
            instance = new CastingProductDefinition();
        }
        return instance;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getVersion()
    {
        return version;
    }

    @Override
    public ConnectionDefinition getConnectionDefinition()
    {
        return connectionDefinition;
    }

    @Override
    public List<ExplorerContent> getExplorerContent()
    {
        return explorerContent;
    }

    @Override
    public String getClientPackage()
    {
        return clientPackage;
    }

    @Override
    public String getStylesheet()
    {
        return styleSheet;
    }

    @Override
    public SplashExtension getSplashExtension()
    {
        return splashExtension;
    }
}
