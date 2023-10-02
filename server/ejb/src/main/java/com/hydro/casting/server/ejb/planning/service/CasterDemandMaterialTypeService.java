package com.hydro.casting.server.ejb.planning.service;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.dto.CasterDemandDTO;
import com.hydro.core.common.cache.ServerCacheManager;
import com.hydro.core.server.common.service.BaseService;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.TransactionSynchronizationRegistry;
import java.util.Collection;
import java.util.List;

@Stateless
public class CasterDemandMaterialTypeService extends BaseService<Object, CasterDemandDTO>
{
    private final static Logger log = LoggerFactory.getLogger( CasterDemandMaterialTypeService.class );

    //@formatter:off
    private final static String QUERY =
            "   SELECT a.breite || "
          + "          a.dicke || "
          + "          a.laenge || "
          + "          a.qualitaet || "
          + "          a.legierung as combinedKey, "
          + "          (SELECT MAX(k.artikelnr) FROM Kundenauftrag k WHERE k.breite = a.breite AND k.dicke = a.dicke AND k.laenge = a.laenge and k.legid = a.legierung and k.qualitaetstyp = a.qualitaet) as materialType, "
          + "          (SELECT MAX(k.artikelkurztext) FROM Kundenauftrag k WHERE k.breite = a.breite AND k.dicke = a.dicke AND k.laenge = a.laenge and k.legid = a.legierung and k.qualitaetstyp = a.qualitaet) as materialTypeDescription, "
          + "          a.legierung as alloy, "
          + "          a.qualitaet as quality, "
          + "          a.dicke as height, "
          + "          a.breite as width, "
          + "          a.laenge as length, "
          + "          a.trohLaenge as doubleLength "
          + "     FROM Artikel a ";

    private final static String SELECT_ALL =
            " ORDER BY a.legierung, a.qualitaet, a.breite, a.laenge ";
    private final static String SELECT_WHERE =
            "    WHERE a in (:artikels)";
    //@formatter:on

    @Inject
    private ServerCacheManager serverCacheManager;

    @Resource
    private TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    @PersistenceContext( unitName = "cms" )
    private EntityManager entityManager;

    public CasterDemandMaterialTypeService()
    {
        super( Casting.CACHE.PLANNING_CACHE_NAME, Casting.CACHE.PLANNING_VERSION_CACHE_NAME, Casting.CACHE.CASTER_DEMAND_MATERIAL_TYPE_DATA_PATH );
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
    public List<CasterDemandDTO> load()
    {
        final String sql = QUERY + SELECT_ALL;
        @SuppressWarnings( "unchecked" )
        final org.hibernate.query.Query<CasterDemandDTO> query = entityManager.unwrap( Session.class ).createQuery( sql );
        query.setResultTransformer( new AliasToBeanResultTransformer( CasterDemandDTO.class ) );

        return query.list();
    }

    @Override
    public List<CasterDemandDTO> load( Collection<Object> entities )
    {
        final String sql = QUERY + SELECT_WHERE;
        @SuppressWarnings( "unchecked" )
        final org.hibernate.query.Query<CasterDemandDTO> query = entityManager.unwrap( Session.class ).createQuery( sql );
        query.setParameter( "artikels", entities );
        query.setResultTransformer( new AliasToBeanResultTransformer( CasterDemandDTO.class ) );

        return query.list();
    }
}