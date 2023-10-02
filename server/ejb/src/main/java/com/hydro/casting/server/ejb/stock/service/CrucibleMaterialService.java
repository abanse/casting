package com.hydro.casting.server.ejb.stock.service;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.dto.CrucibleMaterialDTO;
import com.hydro.casting.server.ejb.main.service.BaseMaterialService;
import com.hydro.casting.server.model.mat.CrucibleMaterial;
import com.hydro.core.common.cache.ServerCacheManager;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.transaction.TransactionSynchronizationRegistry;
import java.util.*;

@Stateless
public class CrucibleMaterialService extends BaseMaterialService<CrucibleMaterial, CrucibleMaterialDTO>
{

    private String analysisName;
    private Map<String, Double> analysis;

    //@formatter:off
    private final static String QUERY =
              "SELECT cm.objid as id, "
            + "       cm.name as name, "
            + "       cm.crucibleName as crucibleName, "
            + "       cm.weight as weight, "
            + "       cm.grossWeight as grossWeight, "
            + "       cm.generationSuccessTS as generationSuccessTS, "
            + "       cm.source as source, "
            + "       ana.objid as analysisObjid, "
            + "       ana.name as analysisName "
            + "  FROM CrucibleMaterial cm left outer join cm.analysis ana";
    private final static String ANALYSIS_QUERY =
              "SELECT ae.analysis.objid as analysisObjid, "
            + "       ae.name as name, "
            + "       ae.standardValue as standardValue "
            + "  FROM AnalysisElement ae ";

    private final static String SELECT_ALL_WHERE =
              "  WHERE cm.generationState = 400 "
            + "    AND cm.consumptionState < 300 "
            + " ORDER BY cm.objid";
    private final static String SELECT_ANALYSIS_ALL_WHERE =
              "  WHERE ae.analysis in (select cm.analysis from CrucibleMaterial cm where cm.generationState = 400 and cm.consumptionState < 300)";
    private final static String SELECT_WHERE =
            "    WHERE cm in (:materials) "
          + " ORDER BY cm.objid";
    private final static String SELECT_ANALYSIS_WHERE =
            "    WHERE ae.analysis in (select cm.analysis from CrucibleMaterial cm where cm in :materials)";

    //@formatter:on

    @Inject
    private ServerCacheManager serverCacheManager;

    @Resource
    private TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public CrucibleMaterialService()
    {
        super( Casting.CACHE.PROD_CACHE_NAME, Casting.CACHE.PROD_VERSION_CACHE_NAME, Casting.CACHE.CRUCIBLE_MATERIAL_PATH );
    }

    @Override
    protected ServerCacheManager getServerCacheManager()
    {
        return serverCacheManager;
    }

    @Override
    protected TransactionSynchronizationRegistry getTransactionSynchronizationRegistry()
    {
        return transactionSynchronizationRegistry;
    }

    @Override
    public List<CrucibleMaterialDTO> load()
    {
        final String sql = QUERY + SELECT_ALL_WHERE;
        final String analysisSql = ANALYSIS_QUERY + SELECT_ANALYSIS_ALL_WHERE;

        final Session session = entityManager.unwrap( Session.class );
        final org.hibernate.query.Query<CrucibleMaterialDTO> query = session.createQuery( sql );
        query.setResultTransformer( new AliasToBeanResultTransformer( CrucibleMaterialDTO.class ) );

        final List<CrucibleMaterialDTO> crucibleMaterials = query.list();
        final Query analysisQuery = session.createQuery( analysisSql, Tuple.class );

        final List<Tuple> analysisRows = analysisQuery.getResultList();

        mergeAnalysis( crucibleMaterials, analysisRows );

        return crucibleMaterials;
    }

    @Override
    public List<CrucibleMaterialDTO> load( Collection<CrucibleMaterial> entities )
    {
        final String sql = QUERY + SELECT_WHERE;
        final String analysisSql = ANALYSIS_QUERY + SELECT_ANALYSIS_WHERE;
        @SuppressWarnings( "unchecked" )
        final Session session = entityManager.unwrap( Session.class );
        final org.hibernate.query.Query<CrucibleMaterialDTO> query = session.createQuery( sql );
        query.setParameter( "materials", entities );
        query.setResultTransformer( new AliasToBeanResultTransformer( CrucibleMaterialDTO.class ) );

        final List<CrucibleMaterialDTO> crucibleMaterials = query.list();

        final Query analysisQuery = session.createQuery( analysisSql, Tuple.class );
        analysisQuery.setParameter( "materials", entities );

        final List<Tuple> analysisRows = analysisQuery.getResultList();

        mergeAnalysis( crucibleMaterials, analysisRows );

        return query.list();
    }

}