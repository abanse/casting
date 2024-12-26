package com.hydro.casting.server.ejb.analysis.service;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.dto.LimsLabelPreregistrationDTO;
import com.hydro.core.common.cache.ServerCacheManager;
import com.hydro.core.server.common.service.BaseService;
import com.hydro.eai.lims.model.LimsLabelPreregistration;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.TransactionSynchronizationRegistry;
import java.util.Collection;
import java.util.List;

@Stateless

public class LimsLabelPreregistrationService extends BaseService<LimsLabelPreregistration, LimsLabelPreregistrationDTO> {

    private final static Logger log = LoggerFactory.getLogger( LimsLabelPreregistrationService.class );

    //@formatter:off
    private final static String QUERY = "select limsLabelPreregistration.rwsampleid as rwsampleid, "
            + "limsLabelPreregistration.ofen as ofen, "
            + "limsLabelPreregistration.charge as charge, "
            + "limsLabelPreregistration.legnr as legnr, "
            + "limsLabelPreregistration.probe as probe, "
            + "limsLabelPreregistration.datum as datum, "
            + "limsLabelPreregistration.herkunft as herkunft, "
            + "limsLabelPreregistration.mangel as mangel "
            + "from LimsLabelPreregistration limsLabelPreregistration ";



 //   private final static String SELECT_WHERE = "where limsLabelPreregistration in (:limsLabelPreregistration)";
    //@formatter:on

    @PersistenceContext( unitName = "lims" )
    private EntityManager entityManager;

    protected LimsLabelPreregistrationService(String cache, String versionCache, String cachePath) {
        super(cache, versionCache, cachePath);
    }

    public LimsLabelPreregistrationService() {
        super( Casting.CACHE.PLANNING_CACHE_NAME, Casting.CACHE.PLANNING_VERSION_CACHE_NAME, Casting.CACHE.CASTING_KT_DATA_PATH );
    }

    @Override
    protected ServerCacheManager getServerCacheManager() {
        return null;
    }

    @Override
    protected TransactionSynchronizationRegistry getTransactionSynchronizationRegistry() {
        return null;
    }

    @Override
    public List<LimsLabelPreregistrationDTO> load() {
        final String sql = QUERY;
        @SuppressWarnings( "unchecked" )
        final org.hibernate.query.Query<LimsLabelPreregistrationDTO> query = entityManager.unwrap( Session.class ).createQuery( sql );
        query.setResultTransformer( new AliasToBeanResultTransformer( LimsLabelPreregistrationDTO.class ) );

        return query.list();
    }

    @Override
    public List<LimsLabelPreregistrationDTO> load(Collection<LimsLabelPreregistration> entities) {
        return List.of();
    }
}
