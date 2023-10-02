package com.hydro.casting.gui.util;

import com.hydro.casting.common.Casting;
import javafx.util.StringConverter;

public class CastingUtil
{
    public static StringConverter<Number> meltingExecutionStatePropertyConverter = new StringConverter<>()
    {
        @Override
        public String toString( Number object )
        {
            if ( object == null )
            {
                return null;
            }

            switch ( object.intValue() )
            {
            case Casting.SCHEDULABLE_STATE.UNPLANNED: // WHITE
                return "Ungeplant";
            case Casting.SCHEDULABLE_STATE.PLANNED: // BLUE
                return "Geplant";
            case Casting.SCHEDULABLE_STATE.RELEASED: // LIGHT_GRAY
                return "Freigegeben";
            case Casting.SCHEDULABLE_STATE.IN_PROGRESS: // YELLOW
                return "In Bearbeitung";
            case Casting.SCHEDULABLE_STATE.SUCCESS: // GREEN
                return "Erfolgreich";
            case Casting.SCHEDULABLE_STATE.PAUSED: // GREEN
                return "Pausiert";
            case Casting.SCHEDULABLE_STATE.FAILED: // RED
                return "Fehlgeschlagen";
            case Casting.SCHEDULABLE_STATE.CANCELED: // GREY
                return "Abgebrochen";
            default:
                return "";
            }
        }

        @Override
        public Number fromString( String string )
        {
            // not needed
            return 0;
        }
    };

    public static StringConverter<Number> executionStatePropertyConverter = new StringConverter<>()
    {
        @Override
        public String toString( Number object )
        {
            if ( object == null )
            {
                return null;
            }

            switch ( object.intValue() )
            {
            case Casting.SCHEDULABLE_STATE.UNPLANNED: // WHITE
                return "Ungeplant";
            case Casting.SCHEDULABLE_STATE.PLANNED: // BLUE
                return "";
            case Casting.SCHEDULABLE_STATE.RELEASED: // LIGHT_GRAY
                return "Fixiert";
            case Casting.SCHEDULABLE_STATE.IN_PROGRESS: // YELLOW
                return "In Bearbeitung";
            case Casting.SCHEDULABLE_STATE.CASTING_IN_PROGRESS: // YELLOW
                return "In Bearbeitung(Giessen)";
            case Casting.SCHEDULABLE_STATE.UNLOADING_IN_PROGRESS: // YELLOW
                return "In Bearbeitung(Entladen)";
            case Casting.SCHEDULABLE_STATE.SUCCESS: // GREEN
                return "Erfolgreich";
            case Casting.SCHEDULABLE_STATE.PAUSED: // GREEN
                return "Pausiert";
            case Casting.SCHEDULABLE_STATE.FAILED: // RED
                return "Fehlgeschlagen";
            case Casting.SCHEDULABLE_STATE.CANCELED: // GREY
                return "Abgebrochen";
            default:
                return "";
            }
        }

        @Override
        public Number fromString( String string )
        {
            // not needed
            return 0;
        }
    };

}
