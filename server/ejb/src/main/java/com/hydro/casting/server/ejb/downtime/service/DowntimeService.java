package com.hydro.casting.server.ejb.downtime.service;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.downtime.dto.DowntimeDTO;
import com.hydro.casting.server.contract.downtime.dto.DowntimeKindDTO;
import com.hydro.casting.server.contract.downtime.dto.DowntimeModuleDTO;
import com.hydro.casting.server.model.downtime.Downtime;
import com.hydro.casting.server.model.downtime.DowntimeKind;
import com.hydro.casting.server.model.downtime.DowntimeModule;
import com.hydro.casting.server.model.downtime.dao.DowntimeKindHome;
import com.hydro.casting.server.model.downtime.dao.DowntimeModuleHome;
import com.hydro.core.common.cache.ServerCache;
import com.hydro.core.common.cache.ServerCacheManager;
import com.hydro.core.common.util.StringTools;
import com.hydro.core.server.common.model.AliasToBeanTrimCharResultTransformer;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Stateless
public class DowntimeService
{
    //@formatter:off
    private final static String DOWNTIME_QUERY =
              "   SELECT dt.objid as id, "
            + "          dt.costCenter as costCenter, "
            + "          dt.machine as machine, "
            + "          dt.fromTS as fromTS, "
            + "          dt.endTS as endTS, "
            + "          dt.shift as shift, "
            + "          dt.amount as amount, "
            + "          dt.description as description, "
            + "          dt.remark as remark, "
            + "          dt.userId as userId, "
            + "          dt.type as type, "
            + "          dtk.kind1 as downtimeKind1, "
            + "          dtk.kind2 as downtimeKind2, "
            + "          dtk.kind3 as downtimeKind3, "
            + "          dtk.description as downtimeDescription, "
            + "          dtm.module as module, "
            + "          dtm.component as component, "
            + "          dtm.description as moduleDescription, "
            + "          dtm.erpIdent as moduleErpIdent "
            + "     FROM Downtime dt left outer join dt.downtimeKind as dtk left outer join dt.downtimeModule as dtm "
            + "    WHERE dt  = :downtime";
    //@formatter:on

    @EJB
    private DowntimeKindHome downtimeKindHome;

    @EJB
    private DowntimeModuleHome downtimeModuleHome;

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    @Inject
    private ServerCacheManager serverCacheManager;

    public List<DowntimeKindDTO> findDowntimeKindDTOsByCostCenter( String[] costCenters )
    {
        final List<DowntimeKind> downtimeKinds = downtimeKindHome.findByCostCenters( costCenters );
        final List<DowntimeKindDTO> downtimeKindDTOList = new ArrayList<>();
        for ( DowntimeKind downtimeKind : downtimeKinds )
        {
            final DowntimeKindDTO downtimeKindDTO = new DowntimeKindDTO();
            downtimeKindDTO.setId( downtimeKind.getObjid() );
            downtimeKindDTO.setCostCenter( downtimeKind.getCostCenter() );
            downtimeKindDTO.setDowntimeKind1( downtimeKind.getKind1() );
            downtimeKindDTO.setDowntimeKind2( downtimeKind.getKind2() );
            downtimeKindDTO.setDowntimeKind3( downtimeKind.getKind3() );
            downtimeKindDTO.setDescription( downtimeKind.getDescription() );
            downtimeKindDTO.setPhase( downtimeKind.getPhase() );
            downtimeKindDTOList.add( downtimeKindDTO );
        }

        return downtimeKindDTOList;
    }

    public List<DowntimeModuleDTO> findModuleDTOsByCostCenter( String[] costCenters )
    {
        final List<DowntimeModule> downtimeModules = downtimeModuleHome.findByCostCenters( costCenters );
        final List<DowntimeModuleDTO> downtimeModuleDTOList = new ArrayList<>();
        for ( DowntimeModule downtimeModule : downtimeModules )
        {
            final DowntimeModuleDTO downtimeModuleDTO = new DowntimeModuleDTO();
            downtimeModuleDTO.setId( downtimeModule.getObjid() );
            downtimeModuleDTO.setCostCenter( downtimeModule.getCostCenter() );
            downtimeModuleDTO.setModule( downtimeModule.getModule() );
            downtimeModuleDTO.setComponent( downtimeModule.getComponent() );
            downtimeModuleDTO.setDescription( downtimeModule.getDescription() );
            downtimeModuleDTO.setOrderNumber( downtimeModule.getErpIdent() );
            downtimeModuleDTO.setDowntimeKinds( transformToDTO( downtimeModule.getDowntimeKinds() ) );
            downtimeModuleDTOList.add( downtimeModuleDTO );
        }

        return downtimeModuleDTOList;
    }

    private Set<DowntimeKindDTO> transformToDTO( Set<DowntimeKind> downtimeKinds )
    {
        final Set<DowntimeKindDTO> resultSet = new HashSet<>();

        for ( DowntimeKind downtimeKind : downtimeKinds )
        {
            final DowntimeKindDTO downtimeKindDTO = new DowntimeKindDTO();
            downtimeKindDTO.setId( downtimeKind.getObjid() );
            downtimeKindDTO.setCostCenter( downtimeKind.getCostCenter() );
            downtimeKindDTO.setDescription( downtimeKind.getDescription() );
            downtimeKindDTO.setDowntimeKind1( downtimeKind.getKind1() );
            downtimeKindDTO.setDowntimeKind2( downtimeKind.getKind2() );
            downtimeKindDTO.setDowntimeKind3( downtimeKind.getKind3() );
            downtimeKindDTO.setPhase( downtimeKind.getPhase() );
            resultSet.add( downtimeKindDTO );
        }

        return resultSet;
    }

    public void replicateCache( Downtime downtime )
    {
        final Query downtimeQuery = ( (Session) entityManager.getDelegate() ).createQuery( DOWNTIME_QUERY );
        downtimeQuery.setParameter( "downtime", downtime );
        downtimeQuery.setResultTransformer( new AliasToBeanTrimCharResultTransformer( DowntimeDTO.class ) );

        DowntimeDTO downtimeDTO = (DowntimeDTO) downtimeQuery.getSingleResult();

        replicateCache( downtimeDTO );
    }

    public void replicateCache( DowntimeDTO downtimeDTO )
    {
        String cachePath = Casting.CACHE.DOWNTIME_KEY + "/" + downtimeDTO.getCostCenter() + StringTools.getNullSafe( downtimeDTO.getMachine() );
        ServerCache<DowntimeDTO> downtimeDataCache = serverCacheManager.getCache( Casting.CACHE.DOWNTIME_CACHE_NAME );

        final String cacheDowntimeKey = cachePath + "/data/" + downtimeDTO.getId();
        downtimeDataCache.put( cacheDowntimeKey, downtimeDTO );

        ServerCache<List<Long>> downtimeSequenceCache = serverCacheManager.getCache( Casting.CACHE.DOWNTIME_CACHE_NAME );

        // get current cache sequence and update if necessary
        List<Long> downtimeSequence = downtimeSequenceCache.get( cachePath );
        if ( downtimeSequence != null && !downtimeSequence.contains( downtimeDTO.getId() ) )
        {
            downtimeSequence.add( downtimeDTO.getId() );
            downtimeSequenceCache.put( cachePath, downtimeSequence );
        }
    }

    public void replicateReportingDataCache( DowntimeDTO downtimeDTO )
    {
        String cachePathModifiedDowntimes = Casting.CACHE.REPORTING_DOWNTIME_DETAIL_KEY + "/modifiedDowntimes";
        ServerCache<List<Long>> reportingDowntimeSequenceCache = serverCacheManager.getCache( Casting.CACHE.REPORTING_DATA_DOWNTIME_CACHE_NAME );
        List<Long> reportingModifiedDowntimeSequence = reportingDowntimeSequenceCache.get( cachePathModifiedDowntimes );
        if ( reportingModifiedDowntimeSequence == null || reportingModifiedDowntimeSequence.isEmpty() )
        {
            reportingModifiedDowntimeSequence = new ArrayList<>();
        }

        if ( !reportingModifiedDowntimeSequence.contains( downtimeDTO.getId() ) )
        {
            reportingModifiedDowntimeSequence.add( downtimeDTO.getId() );
            reportingDowntimeSequenceCache.put( cachePathModifiedDowntimes, reportingModifiedDowntimeSequence );
        }
    }

    public void removeFromCache( DowntimeDTO downtimeDTO )
    {
        String cachePath = Casting.CACHE.DOWNTIME_KEY + "/" + downtimeDTO.getCostCenter();
        ServerCache<DowntimeDTO> downtimeDataCache = serverCacheManager.getCache( Casting.CACHE.DOWNTIME_CACHE_NAME );

        final String cacheDowntimeKey = cachePath + "/data/" + downtimeDTO.getId();
        downtimeDataCache.remove( cacheDowntimeKey );

        ServerCache<List<Long>> downtimeSequenceCache = serverCacheManager.getCache( Casting.CACHE.DOWNTIME_CACHE_NAME );

        // get current cache sequence and update if necessary
        List<Long> downtimeSequence = downtimeSequenceCache.get( cachePath );
        if ( downtimeSequence != null && downtimeSequence.contains( downtimeDTO.getId() ) )
        {
            downtimeSequence.remove( downtimeDTO.getId() );
            downtimeSequenceCache.put( cachePath, downtimeSequence );
        }
    }
}