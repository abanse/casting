package com.hydro.casting.server.ejb.stock.service;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.dto.SlabDTO;
import com.hydro.casting.server.model.mat.Slab;
import com.hydro.core.common.cache.ServerCacheManager;
import com.hydro.core.server.common.service.BaseService;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.type.DoubleType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.transaction.TransactionSynchronizationRegistry;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class SlabService extends BaseService<Slab, SlabDTO>
{
    private final static Logger log = LoggerFactory.getLogger( SlabService.class );

    //@formatter:off
    private final static String DBB_QUERY =
              "SELECT dbb.barrenId as slab, "
            + "       (SELECT MAX(l.vaw) FROM Legierung l WHERE l.norf = dbb.legierung) AS alloy, "
            + "       dbb.qualitaet as quality, "
            + "       dbb.dicke as height, "
            + "       dbb.breite as width, "
            + "       dbb.laenge as length, "
            + "       dbb.versuch as experimentNumber, "
            + "       dbb.gewicht as weight, "
            + "       dbb.effLaenge as effLength, "
            + "       dbb.datum as yardTS, "
            + "       dbb.fehlerCode as errorCode, "
            + "       (SELECT MAX(nkk.kundenid) FROM NorfKundenKey nkk WHERE nkk.kundenKey = dbb.kundenKey) AS customerId, "
            + "       dbb.fehler as error, "
            + "       'Barrenlager' as location, "
            + "       dbb.legierung as norfAlloy "
            + "FROM DispoBarrenBestand dbb";

    private final static String SAW_QUERY =
              "SELECT pa.MATCHGID AS \"slab\", "
            + "       pa.LEGID AS \"alloy\", "
            + "       pa.QUALITAETSTYP AS \"quality\", "
            + "       pa.DICKE AS \"height\", "
            + "       pa.BREITE AS \"width\", "
            + "       pa.LAENGE AS \"length\", "
            + "       pa.VERSUCHSNR AS \"experimentNumber\", "
            + "       pa.MENGE AS \"weight\", "
            + "       pa.EFF_GIESLAENGE AS \"effLength\", "
            + "       spl.STAT as \"location\", "
            + "       (SELECT MAX(l.norf) FROM Legierung l WHERE l.vaw = pa.LEGID) as \"norfAlloy\" "
            + "FROM SAPS.PROZESSAUFTRAG_KOPF pa JOIN SAEGE.PRODLISTE spl ON pa.PROZESSAUFTRAGSID = spl.PANR";

    private final static String LOCK_QUERY =
              "select s.name as name, "
            + "       (select count(*) from MaterialLock ml where ml.material = s) as countLocked, "
            + "       (select count(*) from MaterialLock ml where ml.material = s and ml.active = true) as countActiveLocks "
            + "  from Slab s ";
    private final static String DBB_SELECT_WHERE =
            "    WHERE dbb.barrenId in (:slabNames)";

    private final static String SAW_SELECT_WHERE =
            "    WHERE pa.MATCHGID in (:slabNames)";

    private final static String LOCK_SELECT_ALL =
            "    WHERE s.generationState = 400 and s.consumptionState < 400";
    private final static String LOCK_SELECT_WHERE =
            "    WHERE s.generationState = 400 and s.consumptionState < 400 "
          + "      AND s in (:slabs)";
    //@formatter:on

    @Inject
    private ServerCacheManager serverCacheManager;

    @Resource
    private TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    @PersistenceContext( unitName = "cms" )
    private EntityManager entityManager;

    @PersistenceContext( unitName = "casting" )
    private EntityManager castingEntityManager;

    public SlabService()
    {
        super( Casting.CACHE.PLANNING_CACHE_NAME, Casting.CACHE.PLANNING_VERSION_CACHE_NAME, Casting.CACHE.SLAB_PATH );
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
    public List<SlabDTO> load()
    {
        return load( (Collection<Slab>) null );
    }

    @Override
    public List<SlabDTO> load( Collection<Slab> entities )
    {
        String dbbSql = DBB_QUERY;
        String sawSql = SAW_QUERY;
        String lockSql = LOCK_QUERY + LOCK_SELECT_ALL;
        if ( entities != null )
        {
            dbbSql = DBB_QUERY + DBB_SELECT_WHERE;
            sawSql = SAW_QUERY + SAW_SELECT_WHERE;
            lockSql = LOCK_QUERY + LOCK_SELECT_WHERE;
        }

        final Session session = entityManager.unwrap( Session.class );
        final org.hibernate.query.Query<SlabDTO> query = session.createQuery( dbbSql );
        if ( entities != null )
        {
            query.setParameter( "slabNames", entities.stream().map( Slab::getName ).collect( Collectors.toList() ) );
        }
        query.setResultTransformer( new AliasToBeanResultTransformer( SlabDTO.class ) );

        final org.hibernate.query.NativeQuery<SlabDTO> sawQuery = session.createNativeQuery( sawSql );
        sawQuery.addScalar( "slab", StringType.INSTANCE );
        sawQuery.addScalar( "alloy", StringType.INSTANCE );
        sawQuery.addScalar( "quality", StringType.INSTANCE );
        sawQuery.addScalar( "height", DoubleType.INSTANCE );
        sawQuery.addScalar( "width", DoubleType.INSTANCE );
        sawQuery.addScalar( "length", DoubleType.INSTANCE );
        sawQuery.addScalar( "experimentNumber", StringType.INSTANCE );
        sawQuery.addScalar( "weight", DoubleType.INSTANCE );
        sawQuery.addScalar( "effLength", DoubleType.INSTANCE );
        sawQuery.addScalar( "location", StringType.INSTANCE );
        sawQuery.addScalar( "norfAlloy", StringType.INSTANCE );
        if ( entities != null )
        {
            sawQuery.setParameter( "slabNames", entities.stream().map( Slab::getName ).collect( Collectors.toList() ) );
        }
        sawQuery.setResultTransformer( new AliasToBeanResultTransformer( SlabDTO.class ) );

        final List<SlabDTO> union = new ArrayList();
        union.addAll( sawQuery.list() );
        // correct location
        for ( SlabDTO slabDTO : union )
        {
            if ( slabDTO.getLocation() != null && slabDTO.getLocation().startsWith( "X" ) )
            {
                slabDTO.setLocation( "Säge" );
            }
            else
            {
                slabDTO.setLocation( "Gießbereich" );
            }
        }
        union.addAll( query.list() );

        // merge lock information
        final TypedQuery<Tuple> lockedQuery = castingEntityManager.createQuery( lockSql, Tuple.class );
        if ( entities != null )
        {
            lockedQuery.setParameter( "slabs", entities );
        }
        final List<Tuple> lockedList = lockedQuery.getResultList();
        for ( Tuple lockedTuple : lockedList )
        {
            final String name = lockedTuple.get( "name", String.class );
            final Number countLocked = lockedTuple.get( "countLocked", Number.class );
            final Number countActiveLocks = lockedTuple.get( "countActiveLocks", Number.class );
            final Optional<SlabDTO> slabOptional = union.stream().filter( slabDTO -> Objects.equals( slabDTO.getSlab(), name ) ).findFirst();
            if ( slabOptional.isPresent() )
            {
                final SlabDTO slabDTO = slabOptional.get();
                slabDTO.setCountLocks( countLocked.intValue() );
                slabDTO.setCountActiveLocks( countActiveLocks.intValue() );
            }
        }

        return union;
    }
}