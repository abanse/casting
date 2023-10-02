package com.hydro.casting.server.ejb.main.service;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.dto.AlloyDTO;
import com.hydro.casting.server.model.mat.Alloy;
import com.hydro.casting.server.model.mat.AlloyElement;
import com.hydro.casting.server.model.mat.dao.AlloyHome;
import com.hydro.core.common.cache.ServerCacheManager;
import com.hydro.core.server.common.service.BaseService;
import com.hydro.eai.lims.model.LimsAlloy;
import com.hydro.eai.lims.model.LimsAlloyElement;
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

import static com.hydro.casting.server.ejb.analysis.service.AnalysisService.CALCULATED_ELEMENT;

@Stateless
public class AlloyService extends BaseService<Object, AlloyDTO>
{
    private static final Logger log = LoggerFactory.getLogger( AlloyService.class );

    private static final String QUERY = "select a.name as name, a.version as version, a.description as description, a.active as active, a.activationTime as activationTime, a.meltingRelevant as meltingRelevant from Alloy a";

    private final static String SELECT_WHERE = " where a in (:entities)";

    @EJB
    private AlloyHome alloyHome;

    @Inject
    private ServerCacheManager serverCacheManager;

    @Resource
    private TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public AlloyService()
    {
        super( Casting.CACHE.PLANNING_CACHE_NAME, Casting.CACHE.PLANNING_VERSION_CACHE_NAME, Casting.CACHE.ALLOY_PATH );
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
    public List<AlloyDTO> load()
    {
        @SuppressWarnings( "unchecked" )
        final org.hibernate.query.Query<AlloyDTO> query = entityManager.unwrap( Session.class ).createQuery( QUERY );
        query.setResultTransformer( new AliasToBeanResultTransformer( AlloyDTO.class ) );

        return query.getResultList();
    }

    @Override
    public List<AlloyDTO> load( Collection<Object> entities )
    {
        @SuppressWarnings( "unchecked" )
        final org.hibernate.query.Query<AlloyDTO> query = entityManager.unwrap( Session.class ).createQuery( QUERY + SELECT_WHERE );
        query.setParameter( "entities", entities );
        query.setResultTransformer( new AliasToBeanResultTransformer( AlloyDTO.class ) );

        return query.getResultList();
    }

    /**
     * Temporary function called from AlloyCacheReplicator to create the calculated elements as long as alloy management functionality is not present.
     * <p>
     * TODO: Remove when alloy management feature is introduced!
     *
     * @param dtos List of dto's specifying the alloys that should be processed in this function
     */
    public void loadCalculatedElementsTemp( List<AlloyDTO> dtos )
    {
        for ( AlloyDTO dto : dtos )
        {
            // Definitely works at this point, since the list of DTO's is retrieved from the MES-DB
            Alloy alloy = alloyHome.findActiveByName( dto.getName() );

            if ( alloy != null )
            {
                switch ( alloy.getName() )
                {
                case "1100-L":
                    createCalculatedAlloyElementTemp( alloy, "SI_FE_sum", 0.25, 0.35 );
                    break;
                case "3003-G":
                    createCalculatedAlloyElementTemp( alloy, "FE_MN_sum", -1.0, 1.65 );
                    break;
                case "3104-E":
                    createCalculatedAlloyElementTemp( alloy, "SI_FE", 2.2, 5.0 );
                    break;
                case "3104-F":
                case "3104-L":
                case "3104-R":
                    createCalculatedAlloyElementTemp( alloy, "PB_CD_sum", -1.0, 0.0093 );
                    break;
                case "3105-E":
                    createCalculatedAlloyElementTemp( alloy, "CU_MN_MG_sum", 1.3, 1.5 );
                    createCalculatedAlloyElementTemp( alloy, "MN_SI", 1.0, 5.0 );
                    break;
                case "99/00":
                case "99/01":
                    createCalculatedAlloyElementTemp( alloy, "SI_FE_sum", -1.0, 1.0 );
                    break;
                case "N8021":
                    createCalculatedAlloyElementTemp( alloy, "PB_CD_sum", -1.0, 0.009 );
                    break;
                case "X19/10":
                    createCalculatedAlloyElementTemp( alloy, "SI_FE_sum", -1.0, 1.0 );
                    createCalculatedAlloyElementTemp( alloy, "V_TI_sum", -1.0, 0.03 );
                    createCalculatedAlloyElementTemp( alloy, "ZR_TI_sum", -1.0, 0.1 );
                    break;
                case "Z99/52":
                    createCalculatedAlloyElementTemp( alloy, "V_TI_sum", -1.0, 0.03 );
                    break;
                }
            }
        }
    }

    /**
     * Temporary function called from AlloyService to create a new alloy elements as long as alloy management functionality is not present.
     * <p>
     * TODO: Remove when alloy management feature is introduced!
     *
     * @param alloy    The alloy that should be modified
     * @param name     The name of the new alloy element
     * @param minValue The min value for the new alloy element
     * @param maxValue The max value for the new alloy element
     */
    private void createCalculatedAlloyElementTemp( Alloy alloy, String name, double minValue, double maxValue )
    {
        // Make sure that no element with this name exists yet
        if ( alloy.getAlloyElements().stream().noneMatch( alloyElement -> alloyElement.getName().equals( name ) ) )
        {
            AlloyElement alloyElement = new AlloyElement();
            alloyElement.setName( name );
            // Identifies calculated element that is not retrieved from the DB
            alloyElement.setElementType( CALCULATED_ELEMENT );
            // Placeholder, will be modifiable in the alloy management view
            alloyElement.setPrecision( 5 );
            // Values below 0 are used as placeholders for "no value" in function call
            if ( minValue >= 0.0 )
            {
                alloyElement.setMinValue( minValue );
            }
            if ( maxValue >= 0.0 )
            {
                alloyElement.setMaxValue( maxValue );
            }
            alloy.addToAlloyElements( alloyElement );
        }
    }

    public Alloy findOrCreateAlloy( final String alloyName )
    {
        Alloy alloy = alloyHome.findActiveByName( alloyName );
        if ( alloy == null )
        {
            alloy = new Alloy();
            alloy.setName( alloyName );
            alloy.setActive( true );
            alloy.setActivationTime( LocalDateTime.now() );
            alloyHome.persist( alloy );
        }
        return alloy;
    }

    public Alloy findAlloy( final String alloyName )
    {
        return alloyHome.findActiveByName( alloyName );
    }

    public Alloy findByAlloyId( Long alloyId )
    {
        return alloyHome.findByAlloyId( alloyId );
    }

    public Alloy createOrUpdateAlloyFromLimsData( LimsAlloy limsAlloy )
    {
        log.info( "Creating new alloy (or alloy version) from LIMS alloy: " + limsAlloy );

        // Find previous alloy - there is always max 1 active alloy
        Alloy prevAlloy = alloyHome.findActiveByName( limsAlloy.getStName() );
        // Deactivate old alloy if the new one is active
        if ( prevAlloy != null && limsAlloy.getStatus().equals( "F" ) )
        {
            prevAlloy.setActive( false );
            // Deactivation time of the old version is the time when the new version was entered
            prevAlloy.setDeactivationTime( limsAlloy.getEnteredOn() );
        }

        Alloy alloy = createAlloyFromLimsData( limsAlloy );
        // meltingRelevant field is only captured on MES side, so it has to be copied to the new alloy version
        alloy.setMeltingRelevant( prevAlloy != null && prevAlloy.getMeltingRelevant() );
        alloyHome.persist( alloy );

        return alloy;
    }

    private Alloy createAlloyFromLimsData( LimsAlloy limsAlloy )
    {
        Alloy alloy = new Alloy();
        alloy.setName( limsAlloy.getStName() );
        alloy.setAlloyId( limsAlloy.getSampletypeId() );
        alloy.setVersion( limsAlloy.getStVers() );
        // Status F indicates active in LIMS, inactive is U
        alloy.setActive( limsAlloy.getStatus().equals( "F" ) );
        // Activation time of the version is the time it was entered in LIMS
        alloy.setActivationTime( limsAlloy.getEnteredOn() );
        alloy.setDescription( limsAlloy.getStDesc() );

        for ( LimsAlloyElement limsAlloyElement : limsAlloy.getLimsAlloyElements() )
        {
            AlloyElement alloyElement = createAlloyElementFromLimsData( limsAlloyElement );
            alloy.addToAlloyElements( alloyElement );
        }

        return alloy;
    }

    private AlloyElement createAlloyElementFromLimsData( LimsAlloyElement limsAlloyElement )
    {
        AlloyElement alloyElement = new AlloyElement();
        alloyElement.setName( limsAlloyElement.getPaName() );
        alloyElement.setElementIndex( limsAlloyElement.getPaIndx() );
        alloyElement.setMinValue( limsAlloyElement.getLimit1Lo() );
        alloyElement.setMaxValue( limsAlloyElement.getLimit1Hi() );
        alloyElement.setTargetValue( limsAlloyElement.getTargetValue() );
        // DEC_PREC_CUST stores the actual relevant precision for clients, DEC_PREC is only LIMS internal
        alloyElement.setPrecision( limsAlloyElement.getDecPrecCust() != null ? limsAlloyElement.getDecPrecCust() : limsAlloyElement.getDecPrec() );
        alloyElement.setElementUnit( limsAlloyElement.getUnits() );

        return alloyElement;
    }

    public List<Alloy> loadAllAlloys()
    {
        return alloyHome.findAll();
    }
}
