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
import java.util.Date;
import java.util.List;

@Stateless
public class CasterDemandService extends BaseService<Object, CasterDemandDTO>
{
    private final static Logger log = LoggerFactory.getLogger( CasterDemandService.class );

    //@formatter:off
    private final static String QUERY =
            "   SELECT ka.buchkreis || "
          + "          ka.auftragsid || "
          + "          ka.auftragsart || "
          + "          ka.posnr as combinedKey, "
          + "          ka.auftragsid as orderId, "
          + "          ka.posnr as orderPosition, "
          + "          ka.kundenbestellnr as purchaseOrder, "
          + "          ka.kundenid as customerId, "
          + "          ka.kundenname as customerName, "
          + "          ka.artikelnr as materialType, "
          + "          ka.artikelkurztext as materialTypeDescription, "
          + "          ka.legid as alloy, "
          + "          ka.qualitaetstyp as quality, "
          + "          ka.dicke as height, "
          + "          ka.breite as width, "
          + "          ka.laenge as length, "
          + "          (select a.trohLaenge from Artikel a where ka.breite = a.breite AND ka.dicke = a.dicke AND ka.laenge = a.laenge and ka.legid = a.legierung and ka.qualitaetstyp = a.qualitaet) as doubleLength, "
          + "          ka.auftragsdatum as orderDate, "
          + "          ka.lieferterminvon as deliveryDateFrom, "
          + "          ka.lieferterminbis as deliveryDateTo, "
          + "          ka.mengestueck as amount, "
          + "          0 as planned, "
          + "          NVL(ka.abgerufen, 0) as retrieved, "
          + "          NVL(ka.geliefert, 0) as delivered, "
          + "          ka.versuchnr as experimentNumber "
          + "     FROM Kundenauftrag ka ";

    private final static String SELECT_ALL =
            "    WHERE (ka.geliefert < ka.mengestueck OR (ka.geliefert IS NULL AND ka.mengestueck > 0)) "
          + "      AND ka.sapsdbstatus < 9";
    private final static String SELECT_WHERE =
            "    WHERE ka in (:demands)";
    //@formatter:on

    @Inject
    private ServerCacheManager serverCacheManager;

    @Resource
    private TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    @PersistenceContext( unitName = "cms" )
    private EntityManager entityManager;

    public CasterDemandService()
    {
        super( Casting.CACHE.PLANNING_CACHE_NAME, Casting.CACHE.PLANNING_VERSION_CACHE_NAME, Casting.CACHE.CASTER_DEMAND_DATA_PATH );
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
        query.setParameter( "demands", entities );
        query.setResultTransformer( new AliasToBeanResultTransformer( CasterDemandDTO.class ) );

        return query.list();
    }
}