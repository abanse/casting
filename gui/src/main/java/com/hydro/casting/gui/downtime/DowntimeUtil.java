package com.hydro.casting.gui.downtime;

import com.hydro.casting.common.Casting;
public abstract class DowntimeUtil
{
    public static String[] getDowntimeCachePaths( String machine ) {
        final String[] downtimeCostCenters = Casting.getDowntimeCostCenters( machine );
        if ( downtimeCostCenters == null ) {
            return new String[0];
        }
        final String[] downtimeCachePaths = new String[downtimeCostCenters.length];
        for ( int i = 0; i < downtimeCostCenters.length; i++ )
        {
            downtimeCachePaths[i] = "/downtime/" + downtimeCostCenters[i];
        }
        return downtimeCachePaths;
    }
}
