package com.hydro.casting.common;

import com.hydro.core.common.util.StringTools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Casting
{
    public static String getOppositeMeltingFurnace( final String machine, final String currentMeltingFurnace )
    {
        if ( MACHINE.CASTER_50.equals( machine ) )
        {
            if ( MACHINE.MELTING_FURNACE_51.equals( currentMeltingFurnace ) )
            {
                return MACHINE.MELTING_FURNACE_52;
            }
            else
            {
                return MACHINE.MELTING_FURNACE_51;
            }
        }
        if ( MACHINE.CASTER_60.equals( machine ) )
        {
            if ( MACHINE.MELTING_FURNACE_61.equals( currentMeltingFurnace ) )
            {
                return MACHINE.MELTING_FURNACE_62;
            }
            else
            {
                return MACHINE.MELTING_FURNACE_61;
            }
        }
        if ( MACHINE.CASTER_70.equals( machine ) )
        {
            if ( MACHINE.MELTING_FURNACE_71.equals( currentMeltingFurnace ) )
            {
                return MACHINE.MELTING_FURNACE_72;
            }
            else
            {
                return MACHINE.MELTING_FURNACE_71;
            }
        }
        if ( MACHINE.CASTER_80.equals( machine ) )
        {
            if ( MACHINE.MELTING_FURNACE_81.equals( currentMeltingFurnace ) )
            {
                return MACHINE.MELTING_FURNACE_82;
            }
            else
            {
                return MACHINE.MELTING_FURNACE_81;
            }
        }
        return null;
    }

    public static String getNextCharge( final String machine, final String previousCharge )
    {
        // Format YYCXXXX
        // Year   ^^
        // Caster   ^
        // Ident     ^^^^
        char caster = machine.charAt( 0 );
        if ( machine.equals( MACHINE.MELTING_FURNACE_S1 ) )
        {
            caster = '1';
        }
        else if ( machine.equals( MACHINE.MELTING_FURNACE_S2 ) )
        {
            caster = '2';
        }

        if ( previousCharge == null )
        {
            final StringBuilder chargeSB = new StringBuilder();
            chargeSB.append( StringTools.YEARDF.format( new Date() ) );
            chargeSB.append( caster );
            chargeSB.append( "0001" );

            return chargeSB.toString();
        }
        else
        {
            final StringBuilder chargeSB = new StringBuilder();
            chargeSB.append( StringTools.YEARDF.format( new Date() ) );
            chargeSB.append( caster );
            int currentNumber = Integer.parseInt( previousCharge.substring( 3 ) );
            if ( currentNumber >= 9999 )
            {
                currentNumber = -1;
            }
            chargeSB.append( StringTools.N4F.format( currentNumber + 1 ) );

            return chargeSB.toString();
        }
    }

    public static String buildCharge( final String machine, int chargeNumber )
    {
        char caster = machine.charAt( 0 );
        if ( machine.equals( MACHINE.MELTING_FURNACE_S1 ) )
        {
            caster = '1';
        }
        else if ( machine.equals( MACHINE.MELTING_FURNACE_S2 ) )
        {
            caster = '2';
        }
        final StringBuilder chargeSB = new StringBuilder();
        chargeSB.append( StringTools.YEARDF.format( new Date() ) );
        chargeSB.append( caster );
        if ( chargeNumber > 9999 )
        {
            chargeNumber = 0;
        }
        chargeSB.append( StringTools.N4F.format( chargeNumber ) );
        return chargeSB.toString();
    }

    public static Integer getDefaultDuration( String machine )
    {
        return 252; // ca 40 Güsse in der Woche sind pro Guss 4,2 Stunden
    }

    public static double getFurnaceWeight( String machine )
    {
        if ( MACHINE.MELTING_FURNACE_51.equals( machine ) || MACHINE.MELTING_FURNACE_52.equals( machine ) )
        {
            return 46000;
        }
        if ( MACHINE.MELTING_FURNACE_61.equals( machine ) || MACHINE.MELTING_FURNACE_62.equals( machine ) )
        {
            return 46000;
        }
        if ( MACHINE.MELTING_FURNACE_71.equals( machine ) || MACHINE.MELTING_FURNACE_72.equals( machine ) )
        {
            return 55000;
        }
        else
        {
            return 110000;
        }
    }

    public static boolean isAlloySource( String name )
    {
        for ( String alloySource : ALLOY_SOURCES.ALL )
        {
            if ( Objects.equals( alloySource, name ) )
            {
                return true;
            }
        }
        return false;
    }

    public interface CACHE
    {
        // TODO neue Kategorie NAME
        String PLANNING_CACHE_NAME = "casting-planning";
        String PLANNING_VERSION_CACHE_NAME = "casting-planning-version";
        String PROD_CACHE_NAME = "casting-prod";
        String PROD_VERSION_CACHE_NAME = "casting-prod-version";
        String DOWNTIME_CACHE_NAME = "downtime";
        String REPORTING_CACHE_NAME = "reporting";
        String LOCKING_CACHE_NAME = "locking";
        String REPORTING_DATA_DOWNTIME_CACHE_NAME = "reportingDataDowntime";
        String MACHINE_DATA_PATH = "/machine";
        String FURNACE_INSTRUCTION_DATA_PATH = "/furnace-instruction";
        String CASTER_INSTRUCTION_DATA_PATH = "/caster-instruction";
        String SLAB_PATH = "/slab";
        String MATERIAL_PATH = "/material";
        String CRUCIBLE_MATERIAL_PATH = "/crucibleMaterial";
        String ANALYSIS_PATH = "/analysis";
        String ALLOY_PATH = "/alloy";
        String CASTER_DEMAND_DATA_PATH = "/caster-demand";
        String CASTER_DEMAND_MATERIAL_TYPE_DATA_PATH = "/caster-demand-material-type";
        String CASTER_SCHEDULE_DATA_PATH = "/caster-schedule";
        String MELTING_KT_DATA_PATH = "/melting-kt";
        String CASTING_KT_DATA_PATH = "/casting-kt";
        String MACHINE_CALENDAR_DATA_PATH = "/machine-calendar";
        String DOWNTIME_KEY = "/downtime";
        String DOWNTIME_REQUEST_KEY = "/downtimeRequest";
        String REPORTING_KEY = "/reporting";
        String REPORTING_SUMMARY_KEY = REPORTING_KEY + "/" + "summary";
        String REPORTING_SUMMARY_DOWNTIME_KEY = REPORTING_SUMMARY_KEY + "/" + "downtime";
        String REPORTING_SUMMARY_GAUGE_KEY = REPORTING_SUMMARY_KEY + "/" + "gauge";
        String REPORTING_SUMMARY_OUTPUT_KEY = REPORTING_SUMMARY_KEY + "/" + "output";
        String REPORTING_SUMMARY_OUTPUT_TARGETS_KEY = REPORTING_SUMMARY_KEY + "/" + "outputTargets";
        String REPORTING_DOWNTIME_KEY = REPORTING_KEY + "/" + "downtime";
        String REPORTING_DOWNTIME_DETAIL_KEY = REPORTING_DOWNTIME_KEY + "/" + "detail";
        String LOCKING_WORKFLOW_KEY = "/locking-workflow";

        // Melting area
        String DATA_PATH = "/data/";
        String MELTING_INSTRUCTION_PATH = "/melting-instruction";
        String MELTING_INSTRUCTION_DATA_PATH = MELTING_INSTRUCTION_PATH + DATA_PATH;
        String MELTING_FURNACE_KT_PATH = "/melting-furnace-kt";
        String MELTING_FURNACE_KT_DATA_PATH = MELTING_FURNACE_KT_PATH + DATA_PATH;
        // MACHINE_DATA_PATH does not contain /data/ suffix
        String MELTING_MACHINE_DATA_PATH = MACHINE_DATA_PATH + DATA_PATH;
    }

    public interface PLANT
    {
        String APK = "4110";
    }

    public interface MACHINE
    {
        String CASTER_50 = "50";
        String MELTING_FURNACE_51 = "51";
        String MELTING_FURNACE_52 = "52";
        String CASTER_60 = "60";
        String MELTING_FURNACE_61 = "61";
        String MELTING_FURNACE_62 = "62";
        String CASTER_70 = "70";
        String MELTING_FURNACE_71 = "71";
        String MELTING_FURNACE_72 = "72";
        String CASTER_80 = "80";
        String MELTING_FURNACE_81 = "81";
        String MELTING_FURNACE_82 = "82";
        String MELTING_FURNACE_S1 = "S1";
        String MELTING_FURNACE_S2 = "S2";
        String MOLD_DEPARTMENT = "MD";
    }

    public static String[] ALL_CASTERS = new String[] { MACHINE.CASTER_50, MACHINE.CASTER_60, MACHINE.CASTER_70, MACHINE.CASTER_80 };
    public static String[] ALL_MELTING_FURNACES = new String[] { MACHINE.MELTING_FURNACE_51, MACHINE.MELTING_FURNACE_52, MACHINE.MELTING_FURNACE_61, MACHINE.MELTING_FURNACE_62,
            MACHINE.MELTING_FURNACE_71, MACHINE.MELTING_FURNACE_72, MACHINE.MELTING_FURNACE_81, MACHINE.MELTING_FURNACE_82, MACHINE.MELTING_FURNACE_S1, MACHINE.MELTING_FURNACE_S2 };
    public static String[] ALL_MELTERS = new String[] { MACHINE.MELTING_FURNACE_S1, MACHINE.MELTING_FURNACE_S2 };

    public static String[] getMeltingFurnacesForCaster( String caster )
    {
        if ( MACHINE.CASTER_50.equals( caster ) )
        {
            return new String[] { MACHINE.MELTING_FURNACE_51, MACHINE.MELTING_FURNACE_52 };
        }
        if ( MACHINE.CASTER_60.equals( caster ) )
        {
            return new String[] { MACHINE.MELTING_FURNACE_61, MACHINE.MELTING_FURNACE_62 };
        }
        if ( MACHINE.CASTER_70.equals( caster ) )
        {
            return new String[] { MACHINE.MELTING_FURNACE_71, MACHINE.MELTING_FURNACE_72 };
        }
        if ( MACHINE.CASTER_80.equals( caster ) )
        {
            return new String[] { MACHINE.MELTING_FURNACE_81, MACHINE.MELTING_FURNACE_82 };
        }
        return null;
    }

    public static String getCasterForMeltingFurnace( String meltingFurnace )
    {
        if ( MACHINE.MELTING_FURNACE_51.equals( meltingFurnace ) || MACHINE.MELTING_FURNACE_52.equals( meltingFurnace ) )
        {
            return MACHINE.CASTER_50;
        }
        if ( MACHINE.MELTING_FURNACE_61.equals( meltingFurnace ) || MACHINE.MELTING_FURNACE_62.equals( meltingFurnace ) )
        {
            return MACHINE.CASTER_60;
        }
        if ( MACHINE.MELTING_FURNACE_71.equals( meltingFurnace ) || MACHINE.MELTING_FURNACE_72.equals( meltingFurnace ) )
        {
            return MACHINE.CASTER_70;
        }
        if ( MACHINE.MELTING_FURNACE_81.equals( meltingFurnace ) || MACHINE.MELTING_FURNACE_82.equals( meltingFurnace ) )
        {
            return MACHINE.CASTER_80;
        }
        return null;
    }

    public interface ALLOY_SOURCES
    {
        String MELTING_FURNACE_S1 = "S1";
        String MELTING_FURNACE_S2 = "S2";
        String UBC_S3 = "S3";
        String REAL_ALLOY = "RA";
        String ELEKTROLYSE = "EL";
        String[] ALL = new String[] { MELTING_FURNACE_S1, MELTING_FURNACE_S2, UBC_S3, REAL_ALLOY, ELEKTROLYSE };
    }

    public interface SCHEDULABLE_STATE
    {
        int HISTORY = 0;
        int UNPLANNED = 100;
        int PLANNED = 200;
        int RELEASED = 250;
        int IN_PROGRESS = 300;
        int CASTING_IN_PROGRESS = 310;
        int UNLOADING_IN_PROGRESS = 320;
        int PAUSED = 350;
        int SUCCESS = 400;
        int FAILED = 500;
        int CANCELED = 600;
    }

    public interface GANTT
    {
        int ALLOY_SOURCE_TRANSFER_TIME = 15;
        int ALLOY_SOURCE_SETUP_AFTER_TIME = 10;
        int EL_TRANSFER_TIME = 15;
        int EL_SETUP_AFTER_TIME = 5;
    }

    public interface INSPECTION
    {
        interface TYPE
        {
            String YES_NO = "yesNo";
            String YES_NO_WITH_INTERVENTION = "yesNoWithIv";
            String OK_NOK = "okNok";
            String OK_NOK_WITH_INTERVENTION = "okNokWithIv";
            String YES_SIGNED = "yesSigned";
            String CASTER_POSITIONS = "casterPositions";
            String TEXT = "text";
            String VISUAL_INSPECTION = "visualInspection";
        }
    }

    public interface ACTUAL_VALUES
    {
        interface APPLICATION
        {
            String PLC_SIGNALS = "PLC_SIGNALS";
        }
    }

    public interface REPORTING
    {
        String CURRENT_SHIFT = "/currentShift";
        String LAST_SHIFT = "/lastShift";
        String SECOND_LAST_SHIFT = "/secondLastShift";
        String THIRD_LAST_SHIFT = "/thirdLastShift";
        String LAST_24H = "/24h";
        String CURRENT_WEEK = "/currentWeek";
        String CURRENT_MONTH = "/currentMonth";
        String LAST_WEEK = "/lastWeek";
        String LAST_MONTH = "/lastMonth";
        String DAY_BEFORE_TOTAL = "/dayBeforeTotal";
        String DAY_BEFORE_SHIFT_1 = "/dayBeforeShift1";
        String DAY_BEFORE_SHIFT_2 = "/dayBeforeShift2";
        String DAY_BEFORE_SHIFT_3 = "/dayBeforeShift3";
    }

    public interface OUTPUT_TYPE
    {
        String INGOT = "0";
        String PLATE = "1";
        String COIL = "2";
        String SHEET = "3";
    }

    public interface MATERIAL_TYPE_TAGS
    {
        String CHARGING_MATERIAL = "CM";
    }

    public interface MATERIAL_TYPE_CATEGORY
    {
        String STOCK_MATERIAL = "SM";
    }

    public interface PLACE
    {
        String CASTER_50 = "GA50";
        String FURNACE_51 = "OF51";
        String FURNACE_52 = "OF52";
        String CASTER_60 = "GA60";
        String FURNACE_61 = "OF61";
        String FURNACE_62 = "OF62";
        String CASTER_70 = "GA70";
        String FURNACE_71 = "OF71";
        String FURNACE_72 = "OF72";
        String CASTER_80 = "GA80";
        String FURNACE_81 = "OF81";
        String FURNACE_82 = "OF82";
        String CRUCIBLES = "TIEGEL";
        String STOCKS = "LAGER";

        //@formatter:off
        String[] ALL = new String[] {
                CASTER_50,
                FURNACE_51,
                FURNACE_52,
                CASTER_60,
                FURNACE_61,
                FURNACE_62,
                CASTER_70,
                FURNACE_71,
                FURNACE_72,
                CASTER_80,
                FURNACE_81,
                FURNACE_82,
                CRUCIBLES,
                STOCKS,
        };
        //@formatter:on
    }

    public interface DOWNTIME_COST_CENTERS
    {
        String COMMON = "-00";

        String[] CASTER_50 = new String[] { MACHINE.CASTER_50, MACHINE.MELTING_FURNACE_51, MACHINE.MELTING_FURNACE_52, MACHINE.CASTER_50 + COMMON };
        String[] CASTER_60 = new String[] { MACHINE.CASTER_60, MACHINE.MELTING_FURNACE_61, MACHINE.MELTING_FURNACE_62, MACHINE.CASTER_60 + COMMON };
        String[] CASTER_70 = new String[] { MACHINE.CASTER_70, MACHINE.MELTING_FURNACE_71, MACHINE.MELTING_FURNACE_72, MACHINE.CASTER_70 + COMMON };
        String[] CASTER_80 = new String[] { MACHINE.CASTER_80, MACHINE.MELTING_FURNACE_81, MACHINE.MELTING_FURNACE_82, MACHINE.CASTER_80 + COMMON };
        String[] MELTER_S1 = new String[] { MACHINE.MELTING_FURNACE_S1, MACHINE.MELTING_FURNACE_S1 + COMMON };
        String[] MELTER_S2 = new String[] { MACHINE.MELTING_FURNACE_S2, MACHINE.MELTING_FURNACE_S2 + COMMON };
    }

    public interface LOCKING_WORKFLOW
    {
        String PDF_PATH = "C:\\temp\\";
        int COMMENT_LENGTH = 4000;
        DateFormat DF = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        String COMMENT_MARK_OLD = "\n\n";
        String COMMENT_MARK = "$1";
        String OK = "${ok}";
        String AV_MARK = "{AV}";
        String FREE_MARK = "F";
        String SCRAP_MARK = "S";
        String CONTAINER_MARK = "D";
        String BLOCK_MARK = "B";
        String TABG_MARK = "T";
        String ERROR_MARK = "#";
        String FREE_OK = "Eine Freigabe an SAP wurde versendet";
        String CONTAINER_OK = "Eine Containerbuchung an SAP wurde versendet";
        String SCRAP_OK = "Eine Verschrottung an SAP wurde versendet";
        String CONTAINER_TABG_OK = "Eine Containerbuchung und ein TABG-Telegramm an SAP wurden versendet";
        String SCRAP_TABG_OK = "Eine Verschrottung und ein TABG-Telegramm an SAP wurden versendet";
        String CONTAINER_CC = "10";
        String CONTAINER_OUTPUT_LOC = "CONT.SPMS";
        String TO_CONTAINER_USER = "SPMS";
        String CONTAINER_MAT_NO = "80468682";
        String DEFECT_TYPE_CAT_DESCRIPTION_DELIMITER = "=";
        int FG_NOT_EXISTS = 0;
        int FG_PACKING_LOT_IS_EMPTY = 1;
        int FG_PACKING_LOT_IS_NOT_EMPTY = 2;
    }

    public static String[] getDowntimeCostCenters( String costCenter )
    {
        if ( MACHINE.CASTER_50.equals( costCenter ) || MACHINE.MELTING_FURNACE_51.equals( costCenter ) || MACHINE.MELTING_FURNACE_52.equals( costCenter ) )
        {
            return DOWNTIME_COST_CENTERS.CASTER_50;
        }
        if ( MACHINE.CASTER_60.equals( costCenter ) || MACHINE.MELTING_FURNACE_61.equals( costCenter ) || MACHINE.MELTING_FURNACE_62.equals( costCenter ) )
        {
            return DOWNTIME_COST_CENTERS.CASTER_60;
        }
        if ( MACHINE.CASTER_70.equals( costCenter ) || MACHINE.MELTING_FURNACE_71.equals( costCenter ) || MACHINE.MELTING_FURNACE_72.equals( costCenter ) )
        {
            return DOWNTIME_COST_CENTERS.CASTER_70;
        }
        if ( MACHINE.CASTER_80.equals( costCenter ) || MACHINE.MELTING_FURNACE_81.equals( costCenter ) || MACHINE.MELTING_FURNACE_82.equals( costCenter ) )
        {
            return DOWNTIME_COST_CENTERS.CASTER_80;
        }
        if ( MACHINE.MELTING_FURNACE_S1.equals( costCenter ) )
        {
            return DOWNTIME_COST_CENTERS.MELTER_S1;
        }
        if ( MACHINE.MELTING_FURNACE_S2.equals( costCenter ) )
        {
            return DOWNTIME_COST_CENTERS.MELTER_S2;
        }
        return null;
    }
}