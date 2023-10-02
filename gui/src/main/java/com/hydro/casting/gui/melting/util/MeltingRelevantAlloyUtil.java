package com.hydro.casting.gui.melting.util;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.dto.AlloyDTO;
import com.hydro.core.common.cache.ClientCache;
import com.hydro.core.gui.CacheManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public abstract class MeltingRelevantAlloyUtil
{
    public static List<String> getMeltingRelevantAlloys( CacheManager cacheManager )
    {
        List<String> alloyList = new ArrayList<>();
        final ClientCache<AlloyDTO> clientCache = cacheManager.getCache( Casting.CACHE.PLANNING_CACHE_NAME );
        final List<Long> sequence = (List<Long>) clientCache.get( Casting.CACHE.ALLOY_PATH );
        if ( sequence != null )
        {
            final Map<Long, AlloyDTO> rawData = clientCache.getAll( Casting.CACHE.ALLOY_PATH + "/data/", sequence );
            for ( AlloyDTO alloyDTO : rawData.values() )
            {
                if ( alloyDTO.isActive() && alloyDTO.isMeltingRelevant() )
                {
                    alloyList.add( alloyDTO.getName() );
                }
            }
        }
        return alloyList;
    }
}
