package com.hydro.casting.server.ejb.analysis.service;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.dto.AnalysisDTO;
import com.hydro.core.common.cache.ServerCacheManager;
import com.hydro.core.server.common.service.BaseService;
import com.hydro.eai.lims.model.LimsAnalysisElement;
import com.hydro.eai.lims.model.LimsAnalysisPreregistration;
import com.hydro.eai.lims.model.dao.LimsAnalysisElementHome;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.TransactionSynchronizationRegistry;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Stateless
public class LimsAnalysisService extends BaseService<LimsAnalysisPreregistration, AnalysisDTO>
{
    private final static Logger log = LoggerFactory.getLogger( LimsAnalysisService.class );
    //@formatter:off
    private final static String QUERY = "select limsAnalysisPrereg.pvRwsampleid as rwSampleId, "
            + "limsAnalysisPrereg.pvOfen as melter, "
            + "limsAnalysisPrereg.pvCharge as charge, "
            + "limsAnalysisPrereg.pvLegnr as alloyName, "
            + "limsAnalysisPrereg.pvProbe as sampleNumber, "
            + "limsAnalysisPrereg.pvDatum as preregistrationTime, "
            + "limsAnalysis.sampleNo as analysisNo, "
            + "limsAnalysis.sampleStatus as status, "
            + "limsAnalysis.regOn as registrationTime, "
            + "limsAnalysis.appOn as approvalTime, "
            + "limsAnalysis.scanOn as scanTime, "
            + "(select distinct(lae.sampletypeId) from LimsAnalysisElement lae where lae.sampleId = limsAnalysis.sampleId) as alloyId, "
            + "(select distinct(lae.stVers) from LimsAnalysisElement lae where lae.sampleId = limsAnalysis.sampleId) as alloyVersion "
            + "from LimsAnalysisPreregistration limsAnalysisPrereg "
            + "left join LimsAnalysis limsAnalysis on extract(year from limsAnalysisPrereg.pvDatum) = extract(year from limsAnalysis.regOn) "
            + "and limsAnalysisPrereg.pvCharge = limsAnalysis.text1 "
            + "and limsAnalysisPrereg.pvOfen = limsAnalysis.text2 "
            + "and limsAnalysisPrereg.pvProbe = limsAnalysis.text3 "
            + "where limsAnalysisPrereg.pvDatum != null and limsAnalysisPrereg.pvCharge != null "
            + "and limsAnalysisPrereg.pvOfen != null and limsAnalysisPrereg.pvProbe != null";


    private final static String SELECT_WHERE = "where limsAnalysisPrereg in (:limsAnalysisPreregistrations)";
    //@formatter:on

    @EJB
    LimsAnalysisElementHome limsAnalysisElementHome;

    @EJB
    AnalysisService analysisService;

    @Inject
    private ServerCacheManager serverCacheManager;

    @Resource
    private TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    @PersistenceContext( unitName = "lims" )
    private EntityManager entityManager;

    public LimsAnalysisService()
    {
        super( Casting.CACHE.PLANNING_CACHE_NAME, Casting.CACHE.PLANNING_VERSION_CACHE_NAME, Casting.CACHE.ANALYSIS_PATH );
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
    public List<AnalysisDTO> load()
    {
        @SuppressWarnings( "unchecked" )
        final org.hibernate.query.Query<AnalysisDTO> query = entityManager.unwrap( Session.class ).createQuery( QUERY );
        query.setResultTransformer( new AliasToBeanResultTransformer( AnalysisDTO.class ) );
        return query.getResultList();
    }

    @Override
    public List<AnalysisDTO> load( Collection<LimsAnalysisPreregistration> limsAnalysisPreregistrations )
    {
        @SuppressWarnings( "unchecked" )
        final org.hibernate.query.Query<AnalysisDTO> query = entityManager.unwrap( Session.class ).createQuery( QUERY + SELECT_WHERE );
        query.setParameter( "limsAnalysisPreregistrations", limsAnalysisPreregistrations );
        query.setResultTransformer( new AliasToBeanResultTransformer( AnalysisDTO.class ) );
        return query.getResultList();
    }

    public void compareDTOsWithCache( List<AnalysisDTO> analysisDTOs )
    {
        for ( AnalysisDTO newDTO : analysisDTOs )
        {
            AnalysisDTO cacheDTO = getCacheEntry( newDTO.getId() );
            // Works even if cacheDTO is null
            if ( !Objects.equals( newDTO, cacheDTO ) )
            {
                analysisService.createOrUpdateAnalysisFromDTO( newDTO, cacheDTO != null ? cacheDTO.getAnalysisId() : null );
                newDTO.setLastChanged( LocalDateTime.now() );
            }
        }
    }

    public List<LimsAnalysisElement> findBySampleNo( String sampleNo )
    {
        return limsAnalysisElementHome.findBySampleNo( sampleNo );
    }
}
