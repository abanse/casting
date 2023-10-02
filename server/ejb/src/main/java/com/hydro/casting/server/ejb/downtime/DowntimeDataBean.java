package com.hydro.casting.server.ejb.downtime;

import com.hydro.casting.server.contract.downtime.DowntimeData;
import com.hydro.casting.server.model.downtime.DowntimeKind;
import com.hydro.casting.server.model.downtime.DowntimeModule;
import com.hydro.casting.server.model.downtime.dao.DowntimeKindHome;
import com.hydro.casting.server.model.downtime.dao.DowntimeModuleHome;
import com.hydro.core.common.util.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class DowntimeDataBean implements DowntimeData
{
    private final static Logger log = LoggerFactory.getLogger( DowntimeDataBean.class );

    @EJB
    DowntimeKindHome downtimeKindHome;
    @EJB
    DowntimeModuleHome downtimeModuleHome;

    @Override
    public String replicateDowntimeData()
    {
        log.info( "Replicating downtime data" );
        final StringBuilder sb = new StringBuilder();

        // Create downtime kinds first, since they have to be added to multiple downtime modules
        // 01-70-XXX
        createDowntimeKind( sb, "S1", "Anlagenvorbereitung", "01", "70", "101", null );
        createDowntimeKind( sb, "S1", "Abkrätzen", "01", "70", "102", null );
        createDowntimeKind( sb, "S1", "Einsetzen", "01", "70", "103", null );
        // 05-01-XXX
        createDowntimeKind( sb, "S1", "Störung Mechanik", "05", "01", "101", null );
        createDowntimeKind( sb, "S1", "Feuerfest defekt", "05", "01", "102", null );
        createDowntimeKind( sb, "S1", "Verstopft", "05", "01", "103", null );
        createDowntimeKind( sb, "S1", "Stahlbau defekt", "05", "01", "104", null );
        createDowntimeKind( sb, "S1", "Lagerschaden", "05", "01", "105", null );
        createDowntimeKind( sb, "S1", "Verschmutzt", "05", "01", "106", null );
        createDowntimeKind( sb, "S1", "Hydraulik defekt", "05", "01", "107", null );
        createDowntimeKind( sb, "S1", "Eingefroren", "05", "01", "108", null );
        createDowntimeKind( sb, "S1", "Undicht", "05", "01", "109", null );
        createDowntimeKind( sb, "S1", "Brenner defekt/keine Leistung", "05", "01", "110", null );
        // 05-02-XXX
        createDowntimeKind( sb, "S1", "Störung Elektrik", "05", "02", "101", null );
        createDowntimeKind( sb, "S1", "Warten auf Instandhaltung", "05", "02", "102", null );
        // 05-03-XXX
        createDowntimeKind( sb, "S1", "Werkzeugmangel", "05", "03", "101", null );
        createDowntimeKind( sb, "S1", "kein Lieferfahrzeug", "05", "03", "102", null );
        createDowntimeKind( sb, "S1", "Kran defekt", "05", "03", "103", null );
        createDowntimeKind( sb, "S1", "keine Fahrzeuge", "05", "03", "104", null );
        // 05-04-XXX
        createDowntimeKind( sb, "S1", "Qualitätsmangel/Prüfungen", "05", "04", "101", null );
        createDowntimeKind( sb, "S1", "Einsatz nach Plan nicht möglich Analyse", "05", "04", "102", null );
        createDowntimeKind( sb, "S1", "Warten auf Labor", "05", "04", "103", null );
        // 05-05-XXX
        createDowntimeKind( sb, "S1", "Wartezeit Hilfs-/Betriebsstoffe", "05", "05", "101", null );
        // 05-06-XXX
        createDowntimeKind( sb, "S1", "Störung Handling", "05", "06", "101", null );
        // 05-07-XXX
        createDowntimeKind( sb, "S1", "Personal", "05", "07", "101", null );
        // 05-08-XXX
        createDowntimeKind( sb, "S1", "Auftrags-/Materialmangel", "05", "08", "101", null );
        createDowntimeKind( sb, "S1", "keine Abnahme", "05", "08", "102", null );
        // 05-09-XXX
        createDowntimeKind( sb, "S1", "HSE", "05", "09", "101", null );
        // 05-10-XXX
        createDowntimeKind( sb, "S1", "Störung Materialfluss", "05", "10", "101", null );
        createDowntimeKind( sb, "S1", "kein Einsatzplan vorhanden", "05", "10", "102", null );
        // 99-01-XXX
        createDowntimeKind( sb, "S1", "geplante Stillstände", "99", "01", "101", null );
        createDowntimeKind( sb, "S1", "Check-Up", "99", "01", "102", null );
        createDowntimeKind( sb, "S1", "Großreparatur / Modernisierung", "99", "01", "103", null );
        createDowntimeKind( sb, "S1", "Inventur", "99", "01", "104", null );
        createDowntimeKind( sb, "S1", "Wartung", "99", "01", "105", null );

        // Same downtime kinds for S2
        // 01-70-XXX
        createDowntimeKind( sb, "S2", "Anlagenvorbereitung", "01", "70", "101", null );
        createDowntimeKind( sb, "S2", "Abkrätzen", "01", "70", "102", null );
        createDowntimeKind( sb, "S2", "Einsetzen", "01", "70", "103", null );
        // 05-01-XXX
        createDowntimeKind( sb, "S2", "Störung Mechanik", "05", "01", "101", null );
        createDowntimeKind( sb, "S2", "Feuerfest defekt", "05", "01", "102", null );
        createDowntimeKind( sb, "S2", "Verstopft", "05", "01", "103", null );
        createDowntimeKind( sb, "S2", "Stahlbau defekt", "05", "01", "104", null );
        createDowntimeKind( sb, "S2", "Lagerschaden", "05", "01", "105", null );
        createDowntimeKind( sb, "S2", "Verschmutzt", "05", "01", "106", null );
        createDowntimeKind( sb, "S2", "Hydraulik defekt", "05", "01", "107", null );
        createDowntimeKind( sb, "S2", "Eingefroren", "05", "01", "108", null );
        createDowntimeKind( sb, "S2", "Undicht", "05", "01", "109", null );
        createDowntimeKind( sb, "S2", "Brenner defekt/keine Leistung", "05", "01", "110", null );
        // 05-02-XXX
        createDowntimeKind( sb, "S2", "Störung Elektrik", "05", "02", "101", null );
        createDowntimeKind( sb, "S2", "Warten auf Instandhaltung", "05", "02", "102", null );
        // 05-03-XXX
        createDowntimeKind( sb, "S2", "Werkzeugmangel", "05", "03", "101", null );
        createDowntimeKind( sb, "S2", "kein Lieferfahrzeug", "05", "03", "102", null );
        createDowntimeKind( sb, "S2", "Kran defekt", "05", "03", "103", null );
        createDowntimeKind( sb, "S2", "keine Fahrzeuge", "05", "03", "104", null );
        // 05-04-XXX
        createDowntimeKind( sb, "S2", "Qualitätsmangel/Prüfungen", "05", "04", "101", null );
        createDowntimeKind( sb, "S2", "Einsatz nach Plan nicht möglich Analyse", "05", "04", "102", null );
        createDowntimeKind( sb, "S2", "Warten auf Labor", "05", "04", "103", null );
        // 05-05-XXX
        createDowntimeKind( sb, "S2", "Wartezeit Hilfs-/Betriebsstoffe", "05", "05", "101", null );
        // 05-06-XXX
        createDowntimeKind( sb, "S2", "Störung Handling", "05", "06", "101", null );
        // 05-07-XXX
        createDowntimeKind( sb, "S2", "Personal", "05", "07", "101", null );
        // 05-08-XXX
        createDowntimeKind( sb, "S2", "Auftrags-/Materialmangel", "05", "08", "101", null );
        createDowntimeKind( sb, "S2", "keine Abnahme", "05", "08", "102", null );
        // 05-09-XXX
        createDowntimeKind( sb, "S2", "HSE", "05", "09", "101", null );
        // 05-10-XXX
        createDowntimeKind( sb, "S2", "Störung Materialfluss", "05", "10", "101", null );
        createDowntimeKind( sb, "S2", "kein Einsatzplan vorhanden", "05", "10", "102", null );
        // 99-01-XXX
        createDowntimeKind( sb, "S2", "geplante Stillstände", "99", "01", "101", null );
        createDowntimeKind( sb, "S2", "Check-Up", "99", "01", "102", null );
        createDowntimeKind( sb, "S2", "Großreparatur / Modernisierung", "99", "01", "103", null );
        createDowntimeKind( sb, "S2", "Inventur", "99", "01", "104", null );
        createDowntimeKind( sb, "S2", "Wartung", "99", "01", "105", null );

        // Replicate downtime modules next
        // 2010 Elektrische Installation
        createDowntimeModules( sb, "S1", "Elektrische Installation", "4110-30-5310-2010", "2010", null );
        // 2050 Schmelzofen S1
        createDowntimeModules( sb, "S1", "Schmelzofen S1", "4110-30-5310-2050", "2050", null );
        createDowntimeModules( sb, "S1", "Ofengehäuse", "4110-30-5310-2050-100", "2050", "100" );
        createDowntimeModules( sb, "S1", "Chargiertüre", "4110-30-5310-2050-110", "2050", "110" );
        // 2060 Chargiermulden
        createDowntimeModules( sb, "S1", "Chargiermulden", "4110-30-5310-2060", "2060", null );
        // 2070 Rinne
        createDowntimeModules( sb, "S1", "Rinne", "4110-30-5310-2070", "2070", null );
        // 2250 Brenneranlage S1
        createDowntimeModules( sb, "S1", "Brenneranlage S1", "4110-30-5310-2250", "2250", null );
        createDowntimeModules( sb, "S1", "Rauchgas und Rezierkulation", "4110-30-5310-2250-150", "2250", "150" );
        createDowntimeModules( sb, "S1", "Brennerpaar A", "4110-30-5310-2250-170", "2250", "170" );
        createDowntimeModules( sb, "S1", "Brennerpaar B", "4110-30-5310-2250-180", "2250", "180" );
        createDowntimeModules( sb, "S1", "Brennerpaar C", "4110-30-5310-2250-190", "2250", "190" );
        // 2300 Induktiver Rührer
        createDowntimeModules( sb, "S1", "Induktiver Rührer", "4110-30-5310-2300", "2300", null );

        // S2 downtime modules
        // 2500 Schmelzofen S2
        createDowntimeModules( sb, "S2", "Schmelzofen S2", "4110-30-5620-2500", "2500", null );
        createDowntimeModules( sb, "S2", "Ofengehäuse", "4110-30-5620-2500-200", "2500", "200" );
        createDowntimeModules( sb, "S2", "Hauptkammer", "4110-30-5620-2500-200-100", "2500", "200-100" );
        createDowntimeModules( sb, "S2", "Hauptkammer Tür", "4110-30-5620-2500-200-100-100", "2500", "200-100-100" );
        createDowntimeModules( sb, "S2", "Schmelzkammer", "4110-30-5620-2500-200-200", "2500", "200-200" );
        createDowntimeModules( sb, "S2", "Schmelzkammer Tür", "4110-30-5620-2500-200-200-100", "2500", "200-200-100" );
        createDowntimeModules( sb, "S2", "Lambdasonde", "4110-30-5620-2500-200-200-600", "2500", "200-200-600" );
        createDowntimeModules( sb, "S2", "Schachtdeckel", "4110-30-5620-2500-200-200-700-200", "2500", "200-200-700-200" );
        createDowntimeModules( sb, "S2", "Füllstandsmessung Schmelzkammer", "4110-30-5620-2500-200-200-500", "2500", "200-200-500" );
        createDowntimeModules( sb, "S2", "Pumpenschacht", "4110-30-5620-2500-300", "2500", "300" );
        createDowntimeModules( sb, "S2", "Pumpe 1", "4110-30-5620-2500-300-300", "2500", "300-300" );
        createDowntimeModules( sb, "S2", "Pumpe 2", "4110-30-5620-2500-300-400", "2500", "300-400" );
        // 2550 Metallsaugstation
        createDowntimeModules( sb, "S2", "Metallsaugstation", "4110-30-5620-2550", "2550", null );
        createDowntimeModules( sb, "S2", "Saugglocke", "4110-30-5620-2550-300-200", "2550", "300-200" );
        createDowntimeModules( sb, "S2", "Saugrohre", "4110-30-5620-2550-300-300", "2550", "300-300" );
        createDowntimeModules( sb, "S2", "Verfahrwagen", "4110-30-5620-2550-600-100", "2550", "600-100" );
        // 2600 Beheizungsanlage
        createDowntimeModules( sb, "S2", "Beheizungsanlage", "4110-30-5620-2600", "2600", null );
        createDowntimeModules( sb, "S2", "Brenner 1 A (B)", "4110-30-5620-2600-200", "2600", "200" );
        createDowntimeModules( sb, "S2", "Regeneratoren", "4110-30-5620-2600-200-300", "2600", "200-300" );
        // 2400 Chargiersystem
        createDowntimeModules( sb, "S2", "Chargiersystem", "4110-30-5620-2400", "2400", null );
        createDowntimeModules( sb, "S2", "Chargierkran", "4110-30-5620-2400-400", "2400", "400" );
        createDowntimeModules( sb, "S2", "Container", "4110-30-5620-2400-700", "2400", "700" );
        createDowntimeModules( sb, "S2", "Transferwagen 1", "4110-30-5620-2400-100", "2400", "100" );
        createDowntimeModules( sb, "S2", "Transferwagen 2", "4110-30-5620-2400-200", "2400", "200" );

        // Create the mapping / add the downtime kinds to the downtime modules
        // 2050
        createDowntimeMapping( sb, "S1", "2050", null, "01", "70", "101" );
        createDowntimeMapping( sb, "S1", "2050", null, "01", "70", "102" );
        createDowntimeMapping( sb, "S1", "2050", null, "01", "70", "103" );
        createDowntimeMapping( sb, "S1", "2050", null, "05", "01", "101" );
        createDowntimeMapping( sb, "S1", "2050", null, "05", "02", "101" );
        createDowntimeMapping( sb, "S1", "2050", null, "05", "02", "102" );
        createDowntimeMapping( sb, "S1", "2050", null, "05", "03", "101" );
        createDowntimeMapping( sb, "S1", "2050", null, "05", "03", "102" );
        createDowntimeMapping( sb, "S1", "2050", null, "05", "03", "103" );
        createDowntimeMapping( sb, "S1", "2050", null, "05", "03", "104" );
        createDowntimeMapping( sb, "S1", "2050", null, "05", "04", "101" );
        createDowntimeMapping( sb, "S1", "2050", null, "05", "04", "102" );
        createDowntimeMapping( sb, "S1", "2050", null, "05", "04", "103" );
        createDowntimeMapping( sb, "S1", "2050", null, "05", "05", "101" );
        createDowntimeMapping( sb, "S1", "2050", null, "05", "06", "101" );
        createDowntimeMapping( sb, "S1", "2050", null, "05", "07", "101" );
        createDowntimeMapping( sb, "S1", "2050", null, "05", "08", "101" );
        createDowntimeMapping( sb, "S1", "2050", null, "05", "08", "102" );
        createDowntimeMapping( sb, "S1", "2050", null, "05", "09", "101" );
        createDowntimeMapping( sb, "S1", "2050", null, "05", "10", "101" );
        createDowntimeMapping( sb, "S1", "2050", null, "05", "10", "102" );
        createDowntimeMapping( sb, "S1", "2050", null, "99", "01", "101" );
        createDowntimeMapping( sb, "S1", "2050", null, "99", "01", "102" );
        createDowntimeMapping( sb, "S1", "2050", null, "99", "01", "103" );
        createDowntimeMapping( sb, "S1", "2050", null, "99", "01", "104" );
        createDowntimeMapping( sb, "S1", "2050", null, "99", "01", "105" );
        // 2050-100
        createDowntimeMapping( sb, "S1", "2050", "100", "05", "01", "102" );
        createDowntimeMapping( sb, "S1", "2050", "100", "05", "01", "103" );
        createDowntimeMapping( sb, "S1", "2050", "100", "05", "01", "104" );
        createDowntimeMapping( sb, "S1", "2050", "100", "05", "01", "109" );
        // 2050-110
        createDowntimeMapping( sb, "S1", "2050", "110", "05", "01", "101" );
        createDowntimeMapping( sb, "S1", "2050", "110", "05", "01", "102" );
        createDowntimeMapping( sb, "S1", "2050", "110", "05", "01", "104" );
        createDowntimeMapping( sb, "S1", "2050", "110", "05", "01", "107" );
        createDowntimeMapping( sb, "S1", "2050", "110", "05", "02", "101" );
        // 2010
        createDowntimeMapping( sb, "S1", "2010", null, "05", "01", "110" );
        createDowntimeMapping( sb, "S1", "2010", null, "05", "02", "101" );
        // 2060
        createDowntimeMapping( sb, "S1", "2060", null, "01", "70", "103" );
        createDowntimeMapping( sb, "S1", "2060", null, "05", "01", "101" );
        createDowntimeMapping( sb, "S1", "2060", null, "05", "01", "104" );
        // 2070
        createDowntimeMapping( sb, "S1", "2070", null, "05", "01", "101" );
        createDowntimeMapping( sb, "S1", "2070", null, "05", "01", "102" );
        createDowntimeMapping( sb, "S1", "2070", null, "05", "01", "103" );
        createDowntimeMapping( sb, "S1", "2070", null, "05", "01", "104" );
        createDowntimeMapping( sb, "S1", "2070", null, "05", "01", "105" );
        createDowntimeMapping( sb, "S1", "2070", null, "05", "01", "106" );
        createDowntimeMapping( sb, "S1", "2070", null, "05", "01", "107" );
        createDowntimeMapping( sb, "S1", "2070", null, "05", "01", "109" );
        // 2250
        createDowntimeMapping( sb, "S1", "2250", null, "05", "01", "106" );
        createDowntimeMapping( sb, "S1", "2250", null, "05", "01", "110" );
        createDowntimeMapping( sb, "S1", "2250", null, "05", "02", "101" );
        // 2250-150
        createDowntimeMapping( sb, "S1", "2250", "150", "05", "01", "101" );
        createDowntimeMapping( sb, "S1", "2250", "150", "05", "01", "103" );
        createDowntimeMapping( sb, "S1", "2250", "150", "05", "01", "105" );
        createDowntimeMapping( sb, "S1", "2250", "150", "05", "01", "106" );
        createDowntimeMapping( sb, "S1", "2250", "150", "05", "01", "109" );
        createDowntimeMapping( sb, "S1", "2250", "150", "05", "02", "101" );
        // 2250-170
        createDowntimeMapping( sb, "S1", "2250", "170", "05", "01", "101" );
        createDowntimeMapping( sb, "S1", "2250", "170", "05", "01", "105" );
        createDowntimeMapping( sb, "S1", "2250", "170", "05", "01", "106" );
        createDowntimeMapping( sb, "S1", "2250", "170", "05", "01", "110" );
        createDowntimeMapping( sb, "S1", "2250", "170", "05", "02", "101" );
        // 2250-180
        createDowntimeMapping( sb, "S1", "2250", "180", "05", "01", "101" );
        createDowntimeMapping( sb, "S1", "2250", "180", "05", "01", "105" );
        createDowntimeMapping( sb, "S1", "2250", "180", "05", "01", "106" );
        createDowntimeMapping( sb, "S1", "2250", "180", "05", "01", "110" );
        createDowntimeMapping( sb, "S1", "2250", "180", "05", "02", "101" );
        // 2250-190
        createDowntimeMapping( sb, "S1", "2250", "190", "05", "01", "101" );
        createDowntimeMapping( sb, "S1", "2250", "190", "05", "01", "105" );
        createDowntimeMapping( sb, "S1", "2250", "190", "05", "01", "106" );
        createDowntimeMapping( sb, "S1", "2250", "190", "05", "01", "110" );
        createDowntimeMapping( sb, "S1", "2250", "190", "05", "02", "101" );
        // 2300
        createDowntimeMapping( sb, "S1", "2300", null, "05", "01", "101" );
        createDowntimeMapping( sb, "S1", "2300", null, "05", "02", "101" );

        // S2 mappings
        // 2500
        createDowntimeMapping( sb, "S2", "2500", null, "01", "70", "101" );
        createDowntimeMapping( sb, "S2", "2500", null, "01", "70", "102" );
        createDowntimeMapping( sb, "S2", "2500", null, "01", "70", "103" );
        createDowntimeMapping( sb, "S2", "2500", null, "05", "01", "101" );
        createDowntimeMapping( sb, "S2", "2500", null, "05", "02", "101" );
        createDowntimeMapping( sb, "S2", "2500", null, "05", "02", "102" );
        createDowntimeMapping( sb, "S2", "2500", null, "05", "03", "101" );
        createDowntimeMapping( sb, "S2", "2500", null, "05", "03", "102" );
        createDowntimeMapping( sb, "S2", "2500", null, "05", "03", "103" );
        createDowntimeMapping( sb, "S2", "2500", null, "05", "03", "104" );
        createDowntimeMapping( sb, "S2", "2500", null, "05", "04", "101" );
        createDowntimeMapping( sb, "S2", "2500", null, "05", "04", "102" );
        createDowntimeMapping( sb, "S2", "2500", null, "05", "04", "103" );
        createDowntimeMapping( sb, "S2", "2500", null, "05", "05", "101" );
        createDowntimeMapping( sb, "S2", "2500", null, "05", "06", "101" );
        createDowntimeMapping( sb, "S2", "2500", null, "05", "07", "101" );
        createDowntimeMapping( sb, "S2", "2500", null, "05", "08", "101" );
        createDowntimeMapping( sb, "S2", "2500", null, "05", "08", "102" );
        createDowntimeMapping( sb, "S2", "2500", null, "05", "09", "101" );
        createDowntimeMapping( sb, "S2", "2500", null, "05", "10", "101" );
        createDowntimeMapping( sb, "S2", "2500", null, "05", "10", "102" );
        createDowntimeMapping( sb, "S2", "2500", null, "99", "01", "101" );
        createDowntimeMapping( sb, "S2", "2500", null, "99", "01", "102" );
        createDowntimeMapping( sb, "S2", "2500", null, "99", "01", "103" );
        createDowntimeMapping( sb, "S2", "2500", null, "99", "01", "104" );
        createDowntimeMapping( sb, "S2", "2500", null, "99", "01", "105" );
        // 2500-200
        createDowntimeMapping( sb, "S2", "2500", "200", "05", "01", "102" );
        createDowntimeMapping( sb, "S2", "2500", "200", "05", "01", "103" );
        createDowntimeMapping( sb, "S2", "2500", "200", "05", "01", "104" );
        createDowntimeMapping( sb, "S2", "2500", "200", "05", "01", "109" );
        // 2500-200-100
        createDowntimeMapping( sb, "S2", "2500", "200-100", "01", "70", "102" );
        createDowntimeMapping( sb, "S2", "2500", "200-100", "01", "70", "103" );
        createDowntimeMapping( sb, "S2", "2500", "200-100", "05", "01", "101" );
        createDowntimeMapping( sb, "S2", "2500", "200-100", "05", "01", "102" );
        createDowntimeMapping( sb, "S2", "2500", "200-100", "05", "01", "104" );
        createDowntimeMapping( sb, "S2", "2500", "200-100", "05", "02", "101" );
        // 2500-200-100-100
        createDowntimeMapping( sb, "S2", "2500", "200-100-100", "05", "01", "101" );
        createDowntimeMapping( sb, "S2", "2500", "200-100-100", "05", "01", "102" );
        createDowntimeMapping( sb, "S2", "2500", "200-100-100", "05", "01", "103" );
        createDowntimeMapping( sb, "S2", "2500", "200-100-100", "05", "01", "104" );
        createDowntimeMapping( sb, "S2", "2500", "200-100-100", "05", "01", "105" );
        createDowntimeMapping( sb, "S2", "2500", "200-100-100", "05", "01", "106" );
        createDowntimeMapping( sb, "S2", "2500", "200-100-100", "05", "01", "107" );
        createDowntimeMapping( sb, "S2", "2500", "200-100-100", "05", "01", "109" );
        // 2500-200-200
        createDowntimeMapping( sb, "S2", "2500", "200-200", "01", "70", "102" );
        createDowntimeMapping( sb, "S2", "2500", "200-200", "01", "70", "103" );
        createDowntimeMapping( sb, "S2", "2500", "200-200", "05", "01", "102" );
        createDowntimeMapping( sb, "S2", "2500", "200-200", "05", "01", "108" );
        // 2500-200-200-100
        createDowntimeMapping( sb, "S2", "2500", "200-200-100", "05", "01", "101" );
        createDowntimeMapping( sb, "S2", "2500", "200-200-100", "05", "01", "102" );
        createDowntimeMapping( sb, "S2", "2500", "200-200-100", "05", "01", "103" );
        createDowntimeMapping( sb, "S2", "2500", "200-200-100", "05", "01", "104" );
        createDowntimeMapping( sb, "S2", "2500", "200-200-100", "05", "01", "105" );
        createDowntimeMapping( sb, "S2", "2500", "200-200-100", "05", "01", "106" );
        createDowntimeMapping( sb, "S2", "2500", "200-200-100", "05", "01", "107" );
        createDowntimeMapping( sb, "S2", "2500", "200-200-100", "05", "01", "109" );
        // 2500-200-200-600
        createDowntimeMapping( sb, "S2", "2500", "200-200-600", "05", "01", "103" );
        createDowntimeMapping( sb, "S2", "2500", "200-200-600", "05", "01", "106" );
        createDowntimeMapping( sb, "S2", "2500", "200-200-600", "05", "02", "101" );
        // 2500-200-200-700-200
        createDowntimeMapping( sb, "S2", "2500", "200-200-700-200", "05", "01", "101" );
        createDowntimeMapping( sb, "S2", "2500", "200-200-700-200", "05", "01", "103" );
        createDowntimeMapping( sb, "S2", "2500", "200-200-700-200", "05", "01", "104" );
        createDowntimeMapping( sb, "S2", "2500", "200-200-700-200", "05", "01", "105" );
        createDowntimeMapping( sb, "S2", "2500", "200-200-700-200", "05", "01", "106" );
        createDowntimeMapping( sb, "S2", "2500", "200-200-700-200", "05", "01", "107" );
        createDowntimeMapping( sb, "S2", "2500", "200-200-700-200", "05", "01", "109" );
        createDowntimeMapping( sb, "S2", "2500", "200-200-700-200", "05", "02", "101" );
        // 2500-200-200-500
        createDowntimeMapping( sb, "S2", "2500", "200-200-500", "05", "01", "101" );
        createDowntimeMapping( sb, "S2", "2500", "200-200-500", "05", "01", "103" );
        createDowntimeMapping( sb, "S2", "2500", "200-200-500", "05", "01", "106" );
        createDowntimeMapping( sb, "S2", "2500", "200-200-500", "05", "02", "101" );
        // 2500-300
        createDowntimeMapping( sb, "S2", "2500", "300", "05", "01", "102" );
        createDowntimeMapping( sb, "S2", "2500", "300", "05", "01", "103" );
        createDowntimeMapping( sb, "S2", "2500", "300", "05", "01", "104" );
        createDowntimeMapping( sb, "S2", "2500", "300", "05", "01", "106" );
        createDowntimeMapping( sb, "S2", "2500", "300", "05", "01", "108" );
        // 2500-300-300
        createDowntimeMapping( sb, "S2", "2500", "300-300", "05", "01", "103" );
        createDowntimeMapping( sb, "S2", "2500", "300-300", "05", "02", "101" );
        // 2500-300-400
        createDowntimeMapping( sb, "S2", "2500", "300-400", "05", "01", "103" );
        createDowntimeMapping( sb, "S2", "2500", "300-400", "05", "02", "101" );
        // 2550
        createDowntimeMapping( sb, "S2", "2550", null, "05", "01", "101" );
        createDowntimeMapping( sb, "S2", "2550", null, "05", "01", "104" );
        createDowntimeMapping( sb, "S2", "2550", null, "05", "02", "101" );
        // 2550-300-200
        createDowntimeMapping( sb, "S2", "2550", "300-200", "05", "01", "102" );
        createDowntimeMapping( sb, "S2", "2550", "300-200", "05", "01", "103" );
        // 2550-300-300
        createDowntimeMapping( sb, "S2", "2550", "300-300", "05", "01", "103" );
        // 2550-600-100
        createDowntimeMapping( sb, "S2", "2550", "600-100", "05", "01", "101" );
        createDowntimeMapping( sb, "S2", "2550", "600-100", "05", "01", "105" );
        createDowntimeMapping( sb, "S2", "2550", "600-100", "05", "02", "101" );
        // 2600
        createDowntimeMapping( sb, "S2", "2600", null, "05", "01", "101" );
        createDowntimeMapping( sb, "S2", "2600", null, "05", "01", "104" );
        createDowntimeMapping( sb, "S2", "2600", null, "05", "01", "105" );
        createDowntimeMapping( sb, "S2", "2600", null, "05", "01", "106" );
        createDowntimeMapping( sb, "S2", "2600", null, "05", "02", "101" );
        // 2600-200
        createDowntimeMapping( sb, "S2", "2600", "200", "05", "01", "103" );
        createDowntimeMapping( sb, "S2", "2600", "200", "05", "01", "110" );
        createDowntimeMapping( sb, "S2", "2600", "200", "05", "02", "101" );
        // 2600-200-300
        createDowntimeMapping( sb, "S2", "2600", "200-300", "05", "01", "103" );
        createDowntimeMapping( sb, "S2", "2600", "200-300", "05", "01", "110" );
        createDowntimeMapping( sb, "S2", "2600", "200-300", "05", "02", "101" );
        // 2400
        createDowntimeMapping( sb, "S2", "2400", null, "01", "70", "103" );
        createDowntimeMapping( sb, "S2", "2400", null, "05", "01", "103" );
        createDowntimeMapping( sb, "S2", "2400", null, "05", "01", "104" );
        createDowntimeMapping( sb, "S2", "2400", null, "05", "01", "105" );
        createDowntimeMapping( sb, "S2", "2400", null, "05", "01", "106" );
        createDowntimeMapping( sb, "S2", "2400", null, "05", "01", "107" );
        createDowntimeMapping( sb, "S2", "2400", null, "05", "02", "101" );
        createDowntimeMapping( sb, "S2", "2400", null, "05", "03", "103" );
        // 2400-400
        createDowntimeMapping( sb, "S2", "2400", "400", "01", "70", "103" );
        createDowntimeMapping( sb, "S2", "2400", "400", "05", "01", "103" );
        createDowntimeMapping( sb, "S2", "2400", "400", "05", "01", "104" );
        createDowntimeMapping( sb, "S2", "2400", "400", "05", "01", "105" );
        createDowntimeMapping( sb, "S2", "2400", "400", "05", "01", "106" );
        createDowntimeMapping( sb, "S2", "2400", "400", "05", "01", "107" );
        createDowntimeMapping( sb, "S2", "2400", "400", "05", "02", "101" );
        createDowntimeMapping( sb, "S2", "2400", "400", "05", "03", "103" );
        // 2400-700
        createDowntimeMapping( sb, "S2", "2400", "700", "05", "01", "101" );
        createDowntimeMapping( sb, "S2", "2400", "700", "05", "01", "103" );
        createDowntimeMapping( sb, "S2", "2400", "700", "05", "01", "104" );
        createDowntimeMapping( sb, "S2", "2400", "700", "05", "01", "105" );
        createDowntimeMapping( sb, "S2", "2400", "700", "05", "01", "106" );
        createDowntimeMapping( sb, "S2", "2400", "700", "05", "10", "101" );
        // 2400-100
        createDowntimeMapping( sb, "S2", "2400", "100", "01", "70", "103" );
        createDowntimeMapping( sb, "S2", "2400", "100", "05", "01", "104" );
        createDowntimeMapping( sb, "S2", "2400", "100", "05", "01", "105" );
        createDowntimeMapping( sb, "S2", "2400", "100", "05", "01", "107" );
        // 2400-200
        createDowntimeMapping( sb, "S2", "2400", "200", "01", "70", "103" );
        createDowntimeMapping( sb, "S2", "2400", "200", "05", "01", "104" );
        createDowntimeMapping( sb, "S2", "2400", "200", "05", "01", "105" );
        createDowntimeMapping( sb, "S2", "2400", "200", "05", "01", "107" );


        log.info( "Finished replication of downtime data" );
        return sb.toString();
    }

    private void createDowntimeMapping( StringBuilder sb, String costCenter, String module, String component, String kind1, String kind2, String kind3 )
    {
        // If component is null, add an empty string instead, since the DB won't accept a null value
        component = component == null ? " " : component;

        DowntimeModule downtimeModule = downtimeModuleHome.findByApk( costCenter, module, component );
        DowntimeKind downtimeKind = downtimeKindHome.findByApk( costCenter, kind1, kind2, kind3 );

        if ( downtimeKind != null && downtimeModule != null )
        {
            if ( !downtimeModule.getDowntimeKinds().contains( downtimeKind ) && !downtimeKind.getDowntimeModules().contains( downtimeModule ) )
            {
                downtimeModule.getDowntimeKinds().add( downtimeKind );
                downtimeKind.getDowntimeModules().add( downtimeModule );

                log.info( "Created mapping: " + downtimeModule + " <-> " + downtimeKind );
                StringTools.appendln( sb, "Created mapping: " + downtimeModule + " <-> " + downtimeKind );
            }
            else
            {
                log.info( "Mapping already present; nothing to create: " + downtimeModule + " <-> " + downtimeKind );
                StringTools.appendln( sb, "Mapping already present; nothing to create: " + downtimeModule + " <-> " + downtimeKind );
            }
        }
        else
        {
            log.info( "Mapping cannot be created; either DowntimeKind or DowntimeModule was not found: " + downtimeModule + " <-> " + downtimeKind );
            StringTools.appendln( sb, "Mapping cannot be created; either DowntimeKind or DowntimeModule was not found: " + downtimeModule + " <-> " + downtimeKind );
        }

    }

    private void createDowntimeModules( StringBuilder sb, String costCenter, String description, String erpIdent, String module, String component )
    {
        // If component is null, add an empty string instead, since the DB won't accept a null value
        component = component == null ? " " : component;

        DowntimeModule downtimeModule = downtimeModuleHome.findByApk( costCenter, module, component );

        if ( downtimeModule == null )
        {
            downtimeModule = new DowntimeModule();
            downtimeModule.setCostCenter( costCenter );
            downtimeModule.setDescription( description );
            downtimeModule.setErpIdent( erpIdent );
            downtimeModule.setModule( module );
            downtimeModule.setComponent( component );
            downtimeModuleHome.persist( downtimeModule );

            log.info( "Created new DowntimeModule: " + downtimeModule );
            StringTools.appendln( sb, "Created new DowntimeModule: " + downtimeModule );
        }
        else
        {
            log.info( "DowntimeModule already present, nothing to create: " + downtimeModule );
            StringTools.appendln( sb, "DowntimeModule already present, nothing to create: " + downtimeModule );
        }
    }

    private void createDowntimeKind( StringBuilder sb, String costCenter, String description, String kind1, String kind2, String kind3, String phase )
    {
        DowntimeKind downtimeKind = downtimeKindHome.findByApk( costCenter, kind1, kind2, kind3 );

        if ( downtimeKind == null )
        {
            downtimeKind = new DowntimeKind();
            downtimeKind.setCostCenter( costCenter );
            downtimeKind.setDescription( description );
            downtimeKind.setKind1( kind1 );
            downtimeKind.setKind2( kind2 );
            downtimeKind.setKind3( kind3 );
            downtimeKind.setPhase( phase );
            downtimeKindHome.persist( downtimeKind );

            log.info( "Created new DowntimeKind: " + downtimeKind );
            StringTools.appendln( sb, "Created new DowntimeKind: " + downtimeKind );
        }
        else
        {
            log.info( "DowntimeKind already present, nothing to create: " + downtimeKind );
            StringTools.appendln( sb, "DowntimeKind already present, nothing to create: " + downtimeKind );
        }
    }
}
