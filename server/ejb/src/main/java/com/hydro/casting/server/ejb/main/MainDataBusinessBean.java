package com.hydro.casting.server.ejb.main;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.dto.InspectionValueDTO;
import com.hydro.casting.server.contract.main.MainDataBusiness;
import com.hydro.casting.server.model.inspection.*;
import com.hydro.casting.server.model.inspection.dao.InspectionCategoryHome;
import com.hydro.casting.server.model.inspection.dao.InspectionRuleHome;
import com.hydro.casting.server.model.res.Caster;
import com.hydro.casting.server.model.res.CostCenter;
import com.hydro.casting.server.model.res.MeltingFurnace;
import com.hydro.casting.server.model.res.Plant;
import com.hydro.casting.server.model.res.dao.CasterHome;
import com.hydro.casting.server.model.res.dao.CostCenterHome;
import com.hydro.casting.server.model.res.dao.MeltingFurnaceHome;
import com.hydro.casting.server.model.res.dao.PlantHome;
import com.hydro.casting.server.model.sched.CastingBatch;
import com.hydro.casting.server.model.sched.dao.CastingBatchHome;
import com.hydro.core.common.util.StringTools;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDateTime;

@Stateless
public class MainDataBusinessBean implements MainDataBusiness
{
    @EJB
    private PlantHome plantHome;

    @EJB
    private CostCenterHome costCenterHome;

    @EJB
    private CasterHome casterHome;

    @EJB
    private MeltingFurnaceHome meltingFurnaceHome;

    @EJB
    private CastingBatchHome castingBatchHome;

    @EJB
    private InspectionCategoryHome inspectionCategoryHome;

    @EJB
    private InspectionRuleHome inspectionRuleHome;

    @Override
    public String replicateMachines()
    {
        final StringBuilder result = new StringBuilder();

        Plant plant = plantHome.findByApk( Casting.PLANT.APK );
        if ( plant == null )
        {
            plant = new Plant();
            plant.setApk( Casting.PLANT.APK );
            plantHome.persist( plant );
            StringTools.appendln( result, "Werk " + Casting.PLANT.APK + " wurde erzeugt" );
        }

        final MeltingFurnace o51 = replicateMeltingFurnace( result, plant, Casting.MACHINE.MELTING_FURNACE_51, "115450", "Anlage 50", "Ofen 51", 50000, 46000 );
        final MeltingFurnace o52 = replicateMeltingFurnace( result, plant, Casting.MACHINE.MELTING_FURNACE_52, "115450", "Anlage 50", "Ofen 52", 50000, 46000 );
        replicateCaster( result, plant, Casting.MACHINE.CASTER_50, "115450", "Anlage 50", "Gießanlage 50", 4, 5100, 3125, o51, o52 );
        final MeltingFurnace o61 = replicateMeltingFurnace( result, plant, Casting.MACHINE.MELTING_FURNACE_61, "115460", "Anlage 60", "Ofen 61", 50000, 46000 );
        final MeltingFurnace o62 = replicateMeltingFurnace( result, plant, Casting.MACHINE.MELTING_FURNACE_62, "115460", "Anlage 60", "Ofen 62", 50000, 46000 );
        replicateCaster( result, plant, Casting.MACHINE.CASTER_60, "115460", "Anlage 60", "Gießanlage 60", 4, 5100, 4385, o61, o62 );
        final MeltingFurnace o71 = replicateMeltingFurnace( result, plant, Casting.MACHINE.MELTING_FURNACE_71, "115470", "Anlage 70", "Ofen 71", 65000, 55000 );
        final MeltingFurnace o72 = replicateMeltingFurnace( result, plant, Casting.MACHINE.MELTING_FURNACE_72, "115470", "Anlage 70", "Ofen 72", 65000, 55000 );
        replicateCaster( result, plant, Casting.MACHINE.CASTER_70, "115470", "Anlage 70", "Gießanlage 70", 4, 5100, 9994, o71, o72 );
        final MeltingFurnace o81 = replicateMeltingFurnace( result, plant, Casting.MACHINE.MELTING_FURNACE_81, "115480", "Anlage 80", "Ofen 81", 120000, 110000 );
        final MeltingFurnace o82 = replicateMeltingFurnace( result, plant, Casting.MACHINE.MELTING_FURNACE_82, "115480", "Anlage 80", "Ofen 82", 120000, 110000 );
        replicateCaster( result, plant, Casting.MACHINE.CASTER_80, "115480", "Anlage 80", "Gießanlage 80", 5, 8900, 1067, o81, o82 );

        replicateMeltingFurnace( result, plant, Casting.MACHINE.MELTING_FURNACE_S1, "S1", "Schmelzofen S1", "Schmelzofen S1", 0, 0 );
        replicateMeltingFurnace( result, plant, Casting.MACHINE.MELTING_FURNACE_S2, "S2", "Schmelzofen S2", "Schmelzofen S2", 0, 0 );

        StringTools.appendln( result, "Replikation abgeschlossen" );

        return result.toString();
    }

    @Override
    public String createCasterTestData()
    {
        final StringBuilder result = new StringBuilder();

        createTestData( result, Casting.MACHINE.CASTER_50 );
        createTestData( result, Casting.MACHINE.CASTER_60 );
        createTestData( result, Casting.MACHINE.CASTER_70 );
        createTestData( result, Casting.MACHINE.CASTER_80 );

        return result.toString();
    }

    private void replicateCaster( final StringBuilder result, final Plant plant, final String apk, final String costCenterApk, final String costCenterDescription, final String description,
            int maxMolds, int maxCastingLength, int lastCharge, MeltingFurnace leftFurnace, MeltingFurnace rightFurnace )
    {
        CostCenter costCenter = replicateCostCenter( costCenterApk, costCenterDescription, result );

        Caster caster = casterHome.findByApk( apk );
        if ( caster == null )
        {
            caster = new Caster();
            caster.setApk( apk );
            casterHome.persist( caster );
            StringTools.appendln( result, "Gießanlage " + apk + " wurde erzeugt" );
        }
        caster.setDescription( description );
        caster.setMaxMolds( maxMolds );
        caster.setLastCharge( lastCharge );
        caster.setMaxCastingLength( maxCastingLength );
        caster.setCostCenter( costCenter );
        caster.setPlant( plant );
        caster.setLeftFurnace( leftFurnace );
        caster.setRightFurnace( rightFurnace );
    }

    private MeltingFurnace replicateMeltingFurnace( final StringBuilder result, final Plant plant, final String apk, final String costCenterApk, final String costCenterDescription,
            final String description, double nominalWeight, double maxWeight )
    {
        CostCenter costCenter = replicateCostCenter( costCenterApk, costCenterDescription, result );

        MeltingFurnace meltingFurnace = meltingFurnaceHome.findByApk( apk );
        if ( meltingFurnace == null )
        {
            meltingFurnace = new MeltingFurnace();
            meltingFurnace.setApk( apk );
            meltingFurnaceHome.persist( meltingFurnace );
            StringTools.appendln( result, "Ofen " + apk + " wurde erzeugt" );
        }
        meltingFurnace.setDescription( description );
        meltingFurnace.setMaxWeight( maxWeight );
        meltingFurnace.setNominalWeight( nominalWeight );
        meltingFurnace.setCostCenter( costCenter );
        meltingFurnace.setPlant( plant );

        return meltingFurnace;
    }

    private CostCenter replicateCostCenter( String costCenterApk, String costCenterDescription, StringBuilder result )
    {
        CostCenter costCenter = costCenterHome.findByApk( costCenterApk );
        if ( costCenter == null )
        {
            costCenter = new CostCenter();
            costCenter.setApk( costCenterApk );
            costCenterHome.persist( costCenter );
            StringTools.appendln( result, "Kostenstelle " + costCenterApk + " wurde erzeugt" );
        }
        costCenter.setDescription( costCenterDescription );

        return costCenter;
    }

    private void createTestData( final StringBuilder result, final String machineApk )
    {
        final Caster caster = casterHome.findByApk( machineApk );
        if ( caster == null )
        {
            StringTools.appendln( result, "Gießanlage " + machineApk + " nicht gefunden" );
            return;
        }
        for ( int i = 0; i < 9; i++ )
        {
            final CastingBatch castingBatch = new CastingBatch();
            castingBatch.setCharge( "2103000" + machineApk + i );
            castingBatch.setCastingLength( 6000 );
            castingBatch.setName( "2103000" + machineApk + i );
            castingBatch.setExecutionState( Casting.SCHEDULABLE_STATE.PLANNED );
            castingBatch.setExecutingSequenceIndex( i );
            castingBatch.setPlannedTS( LocalDateTime.now() );
            castingBatch.setExecutingMachine( caster );

            castingBatchHome.persist( castingBatch );

            StringTools.appendln( result, "Batch " + castingBatch.getName() + " erzeugt" );
        }
    }

    @Override
    public String createInspectionCategories()
    {
        final StringBuilder result = new StringBuilder();

        // equipmentCondition
        InspectionCategory equipmentCondition = inspectionCategoryHome.findByApk( "equipmentCondition" );
        if ( equipmentCondition != null )
        {
            StringTools.appendln( result, "Anlagenzustand existiert bereits. Wird als Backup umbenannt." );
            equipmentCondition.setApk( "equipmentCondition.bac" );
        }
        equipmentCondition = new InspectionCategory();
        equipmentCondition.setApk( "equipmentCondition" );
        inspectionCategoryHome.persist( equipmentCondition );
        StringTools.appendln( result, "Neuer Anlagenzustand erzeugt." );

        addBitSetIR( equipmentCondition, Casting.INSPECTION.TYPE.YES_NO, "asi", "ASI, Gefährdung durch technische Störungen oder Beschädigungen der Anlage", null, null, 0, 0, 2, result );
        addBitSetIR( equipmentCondition, Casting.INSPECTION.TYPE.OK_NOK, "metallzulaufrinne", "Zustand der Metallzulaufrinne incl. Bereich Entgaser und Ofenausläufe", null, null, 1, 0, 1, result );
        addBitSetIR( equipmentCondition, Casting.INSPECTION.TYPE.OK_NOK_WITH_INTERVENTION, "giessrinne", "Zustand der Gießrinne (Giessdüsen mittig, Rinnenbogen, Brenner?)", "Düse gewechselt", null, 2,
                0, 1, result );
        addBitSetIR( equipmentCondition, Casting.INSPECTION.TYPE.OK_NOK_WITH_INTERVENTION, "filterkasten", "Zustand des Filterkastens und Vorwärmbrenner", "Kontrolle Schicht./Vorarbeiter", null, 3, 0,
                1, result );
        addBitSetIR( equipmentCondition, Casting.INSPECTION.TYPE.OK_NOK, "giesstisch", "Sauberkeit Giesstisch, Steine und Badthermoelement frei von Metall?", null, null, 4, 0, 1, result );
        addBitSetIR( equipmentCondition, Casting.INSPECTION.TYPE.YES_NO_WITH_INTERVENTION, "notablasskuebel", "Notablasskübel ist gewechselt?", "Wenn Ja, Kübelnummer eintragen!", null, 5, 0, 0,
                result );

        final CasterPositionsIR heisskopfleisten = new CasterPositionsIR();
        heisskopfleisten.setInspectionCategory( equipmentCondition );
        heisskopfleisten.setName( "heisskopfleisten" );
        heisskopfleisten.setType( Casting.INSPECTION.TYPE.CASTER_POSITIONS );
        heisskopfleisten.setPos( 6 );
        heisskopfleisten.setDescription( "Zustand der Heisskopfleisten, Kokillen und Angusssteine" );
        inspectionRuleHome.persist( heisskopfleisten );
        StringTools.appendln( result, "Heisskopfleisten Regel erzeugt" );

        final TextIR sonstiges = new TextIR();
        sonstiges.setInspectionCategory( equipmentCondition );
        sonstiges.setName( "sonstiges" );
        sonstiges.setType( Casting.INSPECTION.TYPE.TEXT );
        sonstiges.setPos( 7 );
        sonstiges.setDescription( "Sonstiges" );
        sonstiges.setSubDescription( "Sonstige Störungen und Abweichungen vom Soll: (Brenner,Wehre,Thermoelemente,usw. ...)" );
        sonstiges.setEmptyResult( InspectionValueDTO.RESULT_OK );
        sonstiges.setFilledResult( InspectionValueDTO.RESULT_FAILED );
        inspectionRuleHome.persist( sonstiges );
        StringTools.appendln( result, "Sonstiges Regel erzeugt" );

        // TODO: Optimize code by moving creation to functions!
        // TODO: Wait for Michael's fix regarding values - probably usage of ID instead of APK in Inspection
        // visualInspection
        InspectionCategory visualInspectionInspectionCategory = inspectionCategoryHome.findByApk( "visualInspection" );
        if ( visualInspectionInspectionCategory != null )
        {
            StringTools.appendln( result, "Kategorie 'Sichtprüfung' existiert bereits. Wird als Backup umbenannt." );
            visualInspectionInspectionCategory.setApk( "visualInspection.bac" );
        }
        visualInspectionInspectionCategory = new InspectionCategory();
        visualInspectionInspectionCategory.setApk( "visualInspection" );
        inspectionCategoryHome.persist( visualInspectionInspectionCategory );
        StringTools.appendln( result, "Neue Kategorie 'Sichtprüfung' erzeugt." );

        final VisualInspectionIR visualInspection = new VisualInspectionIR();
        visualInspection.setInspectionCategory( visualInspectionInspectionCategory );
        visualInspection.setName( "sichtprüfung" );
        visualInspection.setType( Casting.INSPECTION.TYPE.VISUAL_INSPECTION );
        visualInspection.setPos( 0 );
        visualInspection.setDescription( "Sichtprüfung der Barren" );
        inspectionRuleHome.persist( visualInspection );
        StringTools.appendln( result, "Sichtprüfung Regel erzeugt" );

        addBitSetIR( visualInspectionInspectionCategory, Casting.INSPECTION.TYPE.YES_SIGNED, "barrenzange", "Barrenzange geprüft?", "Geprüft durch:", "Kommentar", 1, 0, 0, result );

        final TextIR notes = new TextIR();
        notes.setInspectionCategory( visualInspectionInspectionCategory );
        notes.setName( "bemerkungen" );
        notes.setType( Casting.INSPECTION.TYPE.TEXT );
        notes.setPos( 2 );
        notes.setDescription( "Bemerkungen" );
        notes.setSubDescription( null );
        notes.setEmptyResult( InspectionValueDTO.RESULT_OK );
        notes.setFilledResult( InspectionValueDTO.RESULT_FAILED );
        inspectionRuleHome.persist( notes );

        StringTools.appendln( result, "Notizen Regel erzeugt" );

        // casting preparation
        InspectionCategory castingPreparationInspectionCategory = inspectionCategoryHome.findByApk( "castingPreparation" );
        if ( castingPreparationInspectionCategory != null )
        {
            StringTools.appendln( result, "Kategorie 'Angussvorbereitung' existiert bereits. Wird als Backup umbenannt." );
            castingPreparationInspectionCategory.setApk( "castingPreparation.bac" );
        }
        castingPreparationInspectionCategory = new InspectionCategory();
        castingPreparationInspectionCategory.setApk( "castingPreparation" );
        inspectionCategoryHome.persist( castingPreparationInspectionCategory );
        StringTools.appendln( result, "Neue Kategorie 'Angussvorbereitung' erzeugt." );

        addBitSetIR( castingPreparationInspectionCategory, Casting.INSPECTION.TYPE.OK_NOK, "ofenschnauze", "Ofenschnauze", null, null, 0, 0, 1, result );
        addBitSetIR( castingPreparationInspectionCategory, Casting.INSPECTION.TYPE.OK_NOK, "rinnenwehre", "Rinnen-Wehre", null, null, 1, 0, 1, result );
        addBitSetIR( castingPreparationInspectionCategory, Casting.INSPECTION.TYPE.OK_NOK, "rinnensegmente", "Rinnensegmente & Übergänge", null, null, 2, 0, 1, result );
        addBitSetIR( castingPreparationInspectionCategory, Casting.INSPECTION.TYPE.OK_NOK, "sirbox", "SIR-Box", null, null, 3, 0, 1, result );
        addBitSetIR( castingPreparationInspectionCategory, Casting.INSPECTION.TYPE.OK_NOK, "tiefbettfilter", "Tiefbettfilter (Wehre, Ein-/Auslauf, etc.)", null, null, 4, 0, 1, result );
        addBitSetIR( castingPreparationInspectionCategory, Casting.INSPECTION.TYPE.OK_NOK, "cff_filter", "CFF Filter (sofern ohne Tiefbettfilter gegossen wird)", null, null, 5, 0, 1, result );
        addBitSetIR( castingPreparationInspectionCategory, Casting.INSPECTION.TYPE.OK_NOK, "wasserschleier", "Wasserschleier", null, null, 6, 0, 1, result );
        addBitSetIR( castingPreparationInspectionCategory, Casting.INSPECTION.TYPE.OK_NOK, "andockstelle", "Andockstelle / Gießrinne / Notablasskübel", null, null, 7, 0, 1, result );
        addBitSetIR( castingPreparationInspectionCategory, Casting.INSPECTION.TYPE.OK_NOK, "giessstopfen", "Gießstopfen", null, null, 8, 0, 1, result );
        addBitSetIR( castingPreparationInspectionCategory, Casting.INSPECTION.TYPE.OK_NOK, "giessduesen", "Gießdüsen", null, null, 9, 0, 1, result );
        addBitSetIR( castingPreparationInspectionCategory, Casting.INSPECTION.TYPE.OK_NOK, "skimrahmen", "Skim-Rahmen", null, null, 10, 0, 1, result );
        addBitSetIR( castingPreparationInspectionCategory, Casting.INSPECTION.TYPE.OK_NOK, "combobags", "Combo-Bags und Aufhängung", null, null, 11, 0, 1, result );
        addBitSetIR( castingPreparationInspectionCategory, Casting.INSPECTION.TYPE.OK_NOK, "giessrinnenwehr", "Gießrinnenwehr (PAE)", null, null, 12, 0, 1, result );
        addBitSetIR( castingPreparationInspectionCategory, Casting.INSPECTION.TYPE.OK_NOK, "duesenheizung", "Düsenheizung", null, null, 13, 0, 1, result );
        addBitSetIR( castingPreparationInspectionCategory, Casting.INSPECTION.TYPE.OK_NOK, "giessrinne_thermo", "Gießrinne Thermoelement", null, null, 14, 0, 1, result );
        addBitSetIR( castingPreparationInspectionCategory, Casting.INSPECTION.TYPE.OK_NOK, "angusssteine", "Kontrolle Angusssteine", null, null, 15, 0, 1, result );
        addBitSetIR( castingPreparationInspectionCategory, Casting.INSPECTION.TYPE.OK_NOK, "kokillenlaufflaeche", "Kokillenlauffläche", null, null, 16, 0, 1, result );
        addBitSetIR( castingPreparationInspectionCategory, Casting.INSPECTION.TYPE.OK_NOK, "kokillenausrichtung", "Kokillenausrichtung", null, null, 17, 0, 1, result );
        addBitSetIR( castingPreparationInspectionCategory, Casting.INSPECTION.TYPE.OK_NOK, "limca", "LimCa", null, null, 18, 0, 1, result );
        addBitSetIR( castingPreparationInspectionCategory, Casting.INSPECTION.TYPE.OK_NOK, "ueberfuellsicherung", "Überfüllsicherung, Radarsonde", null, null, 19, 0, 1, result );
        addBitSetIR( castingPreparationInspectionCategory, Casting.INSPECTION.TYPE.OK_NOK, "badelement", "Badelement", null, null, 20, 0, 1, result );
        addBitSetIR( castingPreparationInspectionCategory, Casting.INSPECTION.TYPE.OK_NOK, "temperaturcheck", "Temperaturcheck (Bad, SIR, Tiefbettfilter, Gießrinne, Stopfen)", null, null, 21, 0, 1,
                result );
        addBitSetIR( castingPreparationInspectionCategory, Casting.INSPECTION.TYPE.OK_NOK, "drahtmaschine", "Drahtmaschine (Draht / Funktion)", null, null, 22, 0, 1, result );
        addBitSetIR( castingPreparationInspectionCategory, Casting.INSPECTION.TYPE.OK_NOK, "giesstischposition", "Gießtischposition", null, null, 23, 0, 1, result );
        addBitSetIR( castingPreparationInspectionCategory, Casting.INSPECTION.TYPE.OK_NOK, "sensoren", "Sensoren + Pechiney-Test", null, null, 24, 0, 1, result );
        addBitSetIR( castingPreparationInspectionCategory, Casting.INSPECTION.TYPE.YES_NO, "umbau", "Umbau (ja / nein)", null, null, 25, 0, 0, result );

        // casting preparation examination
        InspectionCategory castingPreparationExaminationInspectionCategory = inspectionCategoryHome.findByApk( "cpExamination" );
        if ( castingPreparationExaminationInspectionCategory != null )
        {
            StringTools.appendln( result, "Kategorie 'Prüfung Angussvorbereitung' existiert bereits. Wird als Backup umbenannt." );
            castingPreparationExaminationInspectionCategory.setApk( "cpExamination.bac" );
        }
        castingPreparationExaminationInspectionCategory = new InspectionCategory();
        castingPreparationExaminationInspectionCategory.setApk( "cpExamination" );
        inspectionCategoryHome.persist( castingPreparationExaminationInspectionCategory );
        StringTools.appendln( result, "Neue Kategorie 'Prüfung Angussvorbereitung' erzeugt." );

        addBitSetIR( castingPreparationExaminationInspectionCategory, Casting.INSPECTION.TYPE.YES_SIGNED, "ofenschnauze", "Ofenschnauze", "Geprüft durch:", "Weitere Beteiligte:", 0, 0, 1, result );
        addBitSetIR( castingPreparationExaminationInspectionCategory, Casting.INSPECTION.TYPE.YES_SIGNED, "rinnenwehre", "Rinnen-Wehre", "Geprüft durch:", "Weitere Beteiligte:", 1, 0, 1, result );
        addBitSetIR( castingPreparationExaminationInspectionCategory, Casting.INSPECTION.TYPE.YES_SIGNED, "rinnensegmente", "Rinnensegmente & Übergänge", "Geprüft durch:", "Weitere Beteiligte:", 2, 0,
                1, result );
        addBitSetIR( castingPreparationExaminationInspectionCategory, Casting.INSPECTION.TYPE.YES_SIGNED, "sirbox", "SIR-Box", "Geprüft durch:", "Weitere Beteiligte:", 3, 0, 1, result );
        addBitSetIR( castingPreparationExaminationInspectionCategory, Casting.INSPECTION.TYPE.YES_SIGNED, "tiefbettfilter", "Tiefbettfilter (Wehre, Ein-/Auslauf, etc.)", "Geprüft durch:",
                "Weitere Beteiligte:", 4, 0, 1, result );
        addBitSetIR( castingPreparationExaminationInspectionCategory, Casting.INSPECTION.TYPE.YES_SIGNED, "cff_filter", "CFF Filter (sofern ohne Tiefbettfilter gegossen wird)", "Geprüft durch:",
                "Weitere Beteiligte:", 5, 0, 1, result );
        addBitSetIR( castingPreparationExaminationInspectionCategory, Casting.INSPECTION.TYPE.YES_SIGNED, "wasserschleier", "Wasserschleier", "Geprüft durch:", "Weitere Beteiligte:", 6, 0, 1,
                result );
        addBitSetIR( castingPreparationExaminationInspectionCategory, Casting.INSPECTION.TYPE.YES_SIGNED, "andockstelle", "Andockstelle / Gießrinne / Notablasskübel", "Geprüft durch:",
                "Weitere Beteiligte:", 7, 0, 1, result );
        addBitSetIR( castingPreparationExaminationInspectionCategory, Casting.INSPECTION.TYPE.YES_SIGNED, "giessstopfen", "Gießstopfen", "Geprüft durch:", "Weitere Beteiligte:", 8, 0, 1, result );
        addBitSetIR( castingPreparationExaminationInspectionCategory, Casting.INSPECTION.TYPE.YES_SIGNED, "giessduesen", "Gießdüsen", "Geprüft durch:", "Weitere Beteiligte:", 9, 0, 1, result );
        addBitSetIR( castingPreparationExaminationInspectionCategory, Casting.INSPECTION.TYPE.YES_SIGNED, "skimrahmen", "Skim-Rahmen", "Geprüft durch:", "Weitere Beteiligte:", 10, 0, 1, result );
        addBitSetIR( castingPreparationExaminationInspectionCategory, Casting.INSPECTION.TYPE.YES_SIGNED, "combobags", "Combo-Bags und Aufhängung", "Geprüft durch:", "Weitere Beteiligte:", 11, 0, 1,
                result );
        addBitSetIR( castingPreparationExaminationInspectionCategory, Casting.INSPECTION.TYPE.YES_SIGNED, "giessrinnenwehr", "Gießrinnenwehr (PAE)", "Geprüft durch:", "Weitere Beteiligte:", 12, 0, 1,
                result );
        addBitSetIR( castingPreparationExaminationInspectionCategory, Casting.INSPECTION.TYPE.YES_SIGNED, "duesenheizung", "Düsenheizung", "Geprüft durch:", "Weitere Beteiligte:", 13, 0, 1, result );
        addBitSetIR( castingPreparationExaminationInspectionCategory, Casting.INSPECTION.TYPE.YES_SIGNED, "giessrinne_thermo", "Gießrinne Thermoelement", "Geprüft durch:", "Weitere Beteiligte:", 14,
                0, 1, result );
        addBitSetIR( castingPreparationExaminationInspectionCategory, Casting.INSPECTION.TYPE.YES_SIGNED, "angusssteine", "Kontrolle Angusssteine", "Geprüft durch:", "Weitere Beteiligte:", 15, 0, 1,
                result );
        addBitSetIR( castingPreparationExaminationInspectionCategory, Casting.INSPECTION.TYPE.YES_SIGNED, "kokillenlaufflaeche", "Kokillenlauffläche", "Geprüft durch:", "Weitere Beteiligte:", 16, 0,
                1, result );
        addBitSetIR( castingPreparationExaminationInspectionCategory, Casting.INSPECTION.TYPE.YES_SIGNED, "kokillenausrichtung", "Kokillenausrichtung", "Geprüft durch:", "Weitere Beteiligte:", 17, 0,
                1, result );
        addBitSetIR( castingPreparationExaminationInspectionCategory, Casting.INSPECTION.TYPE.YES_SIGNED, "limca", "LimCa", "Geprüft durch:", "Weitere Beteiligte:", 18, 0, 1, result );
        addBitSetIR( castingPreparationExaminationInspectionCategory, Casting.INSPECTION.TYPE.YES_SIGNED, "ueberfuellsicherung", "Überfüllsicherung, Radarsonde", "Geprüft durch:",
                "Weitere Beteiligte:", 19, 0, 1, result );
        addBitSetIR( castingPreparationExaminationInspectionCategory, Casting.INSPECTION.TYPE.YES_SIGNED, "badelement", "Badelement", "Geprüft durch:", "Weitere Beteiligte:", 20, 0, 1, result );
        addBitSetIR( castingPreparationExaminationInspectionCategory, Casting.INSPECTION.TYPE.YES_SIGNED, "temperaturcheck", "Temperaturcheck (Bad, SIR, Tiefbettfilter, Gießrinne, Stopfen)",
                "Geprüft durch:", "Weitere Beteiligte:", 21, 0, 1, result );
        addBitSetIR( castingPreparationExaminationInspectionCategory, Casting.INSPECTION.TYPE.YES_SIGNED, "drahtmaschine", "Drahtmaschine (Draht / Funktion)", "Geprüft durch:", "Weitere Beteiligte:",
                22, 0, 1, result );
        addBitSetIR( castingPreparationExaminationInspectionCategory, Casting.INSPECTION.TYPE.YES_SIGNED, "giesstischposition", "Gießtischposition", "Geprüft durch:", "Weitere Beteiligte:", 23, 0, 1,
                result );
        addBitSetIR( castingPreparationExaminationInspectionCategory, Casting.INSPECTION.TYPE.YES_SIGNED, "sensoren", "Sensoren + Pechiney-Test", "Geprüft durch:", "Weitere Beteiligte:", 24, 0, 1,
                result );
        addBitSetIR( castingPreparationExaminationInspectionCategory, Casting.INSPECTION.TYPE.YES_SIGNED, "umbau", "Umbau (ja / nein)", "Geprüft durch:", "Weitere Beteiligte:", 25, 0, 0, result );

        return result.toString();
    }

    private void addBitSetIR( InspectionCategory inspectionCategory, String inspectionType, String name, String description, String interventionDescription, String additionalInfoDescription, int pos,
            int initialValue, int targetValue, StringBuilder result )
    {
        final BitSetIR bitSetIR = new BitSetIR();
        bitSetIR.setInspectionCategory( inspectionCategory );
        bitSetIR.setType( inspectionType );
        bitSetIR.setName( name );
        bitSetIR.setDescription( description );
        bitSetIR.setInterventionDescription( interventionDescription );
        bitSetIR.setAdditionalInfoDescription( additionalInfoDescription );
        bitSetIR.setPos( pos );
        bitSetIR.setInitialValue( initialValue );
        bitSetIR.setTargetValue( targetValue );
        inspectionRuleHome.persist( bitSetIR );
        StringTools.appendln( result, name + " Regel erzeugt" );
    }
}
