package com.hydro.casting.server.ejb.planning.service;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.ejb.main.service.MaterialTypeService;
import com.hydro.casting.server.ejb.main.service.ProductionOrderService;
import com.hydro.casting.server.ejb.main.service.SchedulableService;
import com.hydro.casting.server.model.inspection.InspectionValue;
import com.hydro.casting.server.model.inspection.dao.InspectionValueHome;
import com.hydro.casting.server.model.mat.MaterialType;
import com.hydro.casting.server.model.po.CustomerOrderItem;
import com.hydro.casting.server.model.po.ProductionOrder;
import com.hydro.casting.server.model.po.WorkStep;
import com.hydro.casting.server.model.res.Caster;
import com.hydro.casting.server.model.res.MeltingFurnace;
import com.hydro.casting.server.model.res.dao.CasterHome;
import com.hydro.casting.server.model.res.dao.MeltingFurnaceHome;
import com.hydro.casting.server.model.sched.*;
import com.hydro.casting.server.model.sched.dao.CastingBatchHome;
import com.hydro.casting.server.model.sched.dao.CastingOperationHome;
import com.hydro.casting.server.model.sched.dao.DemandHome;
import com.hydro.casting.server.model.sched.dao.SchedulableHome;
import com.hydro.core.common.cache.ServerCacheManager;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.core.common.exception.BusinessMessageException;
import com.hydro.core.common.util.StringTools;
import com.hydro.core.server.common.service.BaseService;
import com.hydro.core.server.contract.workplace.SearchType;
import com.hydro.eai.cms.model.Artikel;
import com.hydro.eai.cms.model.dao.ArtikelHome;
import com.hydro.eai.cms.model.id.ArtikelId;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.TransactionSynchronizationRegistry;
import java.time.LocalDateTime;
import java.util.*;

@Stateless
public class CasterScheduleService extends BaseService<Schedulable, CasterScheduleDTO>
{
    private final static Logger log = LoggerFactory.getLogger( CasterScheduleService.class );

    //@formatter:off
    private final static String QUERY =
            "   SELECT cb.objid as id, "
          + "          cb.class as type, "
          + "          cb.executingMachine.apk as machine, "
          + "          cb.executingSequenceIndex as executingSequenceIndex, "
          + "          cb.executionState as executionState, "
          + "          cb.duration as duration, "
          + "          cb.annotation as annotation, "
          + "          (select max(op.demand.materialType.alloy.name) from Operation op where op.batch = cb) as alloy, "
          + "          (select max(op.demand.materialType.quality) from Operation op where op.batch = cb) as alloyQuality, "
          + "          cb.processOrder as processOrder, "
          + "          d1.amount as pos1Amount, "
          + "          mt1.apk as pos1MaterialType, "
          + "          coi1.apk as pos1CustomerOrderItem, "
          + "          coi1.experimentNumber as pos1ExperimentNumber, "
          + "          mt1.width as pos1Width, "
          + "          mt1.length as pos1Length, "
          + "          cop1.lengthBonus as pos1LengthBonus, "
          + "          d2.amount as pos2Amount, "
          + "          mt2.apk as pos2MaterialType, "
          + "          coi2.apk as pos2CustomerOrderItem, "
          + "          coi2.experimentNumber as pos2ExperimentNumber, "
          + "          mt2.width as pos2Width, "
          + "          mt2.length as pos2Length, "
          + "          cop2.lengthBonus as pos2LengthBonus, "
          + "          d3.amount as pos3Amount, "
          + "          mt3.apk as pos3MaterialType, "
          + "          coi3.apk as pos3CustomerOrderItem, "
          + "          coi3.experimentNumber as pos3ExperimentNumber, "
          + "          mt3.width as pos3Width, "
          + "          mt3.length as pos3Length, "
          + "          cop3.lengthBonus as pos3LengthBonus, "
          + "          d4.amount as pos4Amount, "
          + "          mt4.apk as pos4MaterialType, "
          + "          coi4.apk as pos4CustomerOrderItem, "
          + "          coi4.experimentNumber as pos4ExperimentNumber, "
          + "          mt4.width as pos4Width, "
          + "          mt4.length as pos4Length, "
          + "          cop4.lengthBonus as pos4LengthBonus, "
          + "          d5.amount as pos5Amount, "
          + "          mt5.apk as pos5MaterialType, "
          + "          coi5.apk as pos5CustomerOrderItem, "
          + "          coi5.experimentNumber as pos5ExperimentNumber, "
          + "          mt5.width as pos5Width, "
          + "          mt5.length as pos5Length, "
          + "          cop5.lengthBonus as pos5LengthBonus, "
          + "          cb.charge as charge, "
          + "          cb.plannedLength as plannedLength, "
          + "          cb.plannedWeight * 1.0 as plannedWeight, "
          + "          mf.apk as meltingFurnace, "
          + "          cb.percentageS1 as percentageS1, "
          + "          cb.percentageS2 as percentageS2, "
          + "          cb.percentageS3 as percentageS3, "
          + "          cb.percentageEL as percentageEL, "
          + "          cb.percentageRA as percentageRA, "
          + "          cb.plannedMeltingDuration as plannedMeltingDuration, "
          + "          cb.plannedCastingDuration as plannedCastingDuration, "
          + "          cb.castingSequence as castingSequence "
          + "     FROM Schedulable cb "
          + "          LEFT OUTER JOIN cb.meltingFurnace mf "
          + "          LEFT OUTER JOIN CastingOperation cop1 ON cop1.batch = cb AND cop1.position = 1 "
          + "          LEFT OUTER JOIN cop1.demand d1 "
          + "          LEFT OUTER JOIN d1.customerOrderItem coi1 "
          + "          LEFT OUTER JOIN d1.materialType mt1 "
          + "          LEFT OUTER JOIN CastingOperation cop2 ON cop2.batch = cb AND cop2.position = 2 "
          + "          LEFT OUTER JOIN cop2.demand d2 "
          + "          LEFT OUTER JOIN d2.customerOrderItem coi2 "
          + "          LEFT OUTER JOIN d2.materialType mt2 "
          + "          LEFT OUTER JOIN CastingOperation cop3 ON cop3.batch = cb AND cop3.position = 3 "
          + "          LEFT OUTER JOIN cop3.demand d3 "
          + "          LEFT OUTER JOIN d3.customerOrderItem coi3 "
          + "          LEFT OUTER JOIN d3.materialType mt3 "
          + "          LEFT OUTER JOIN CastingOperation cop4 ON cop4.batch = cb AND cop4.position = 4 "
          + "          LEFT OUTER JOIN cop4.demand d4 "
          + "          LEFT OUTER JOIN d4.customerOrderItem coi4 "
          + "          LEFT OUTER JOIN d4.materialType mt4 "
          + "          LEFT OUTER JOIN CastingOperation cop5 ON cop5.batch = cb AND cop5.position = 5 "
          + "          LEFT OUTER JOIN cop5.demand d5 "
          + "          LEFT OUTER JOIN d5.customerOrderItem coi5 "
          + "          LEFT OUTER JOIN d5.materialType mt5 ";

    private final static String SELECT_ALL =
            "    WHERE cb.executionState = 200 "
          + "       OR cb.executionState = 250 "
          + "       OR cb.executionState = 290 "
          + "       OR cb.executionState = 300 "
          + "       OR cb.executionState = 310 "
          + "       OR cb.executionState = 320 "
          + "       OR cb.executionState = 330 "
          + "       OR cb.executionState = 350 "
          + "       OR cb.executionState = 500 ";
    private final static String SELECT_MACHINE =
            "    WHERE cb.executingMachine.apk = :machine "
          + "      AND cb.executionState IN (:executionStates) ";
    private final static String SELECT_WHERE =
            "    WHERE cb in (:schedulables)";
    //@formatter:on

    @Inject
    private ServerCacheManager serverCacheManager;

    @Resource
    private TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    @EJB
    private CastingBatchHome castingBatchHome;

    @EJB
    private DemandHome demandHome;

    @EJB
    private CastingOperationHome castingOperationHome;

    @EJB
    private SchedulableHome schedulableHome;

    @EJB
    private ArtikelHome artikelHome;

    @EJB
    private MeltingFurnaceHome meltingFurnaceHome;

    @EJB
    private CasterHome casterHome;

    @EJB
    private InspectionValueHome inspectionValueHome;

    @EJB
    private SchedulableService schedulableService;

    @EJB
    private MaterialTypeService materialTypeService;

    @EJB
    private ProductionOrderService productionOrderService;

    public CasterScheduleService()
    {
        super( Casting.CACHE.PLANNING_CACHE_NAME, Casting.CACHE.PLANNING_VERSION_CACHE_NAME, Casting.CACHE.CASTER_SCHEDULE_DATA_PATH );
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
    public List<CasterScheduleDTO> load()
    {
        final String sql = QUERY + SELECT_ALL;
        @SuppressWarnings( "unchecked" )
        final org.hibernate.query.Query<CasterScheduleDTO> query = entityManager.unwrap( Session.class ).createQuery( sql );
        query.setResultTransformer( new AliasToBeanResultTransformer( CasterScheduleDTO.class ) );

        return query.list();
    }

    @Override
    public List<CasterScheduleDTO> load( Collection<Schedulable> entities )
    {
        final String sql = QUERY + SELECT_WHERE;
        @SuppressWarnings( "unchecked" )
        final org.hibernate.query.Query<CasterScheduleDTO> query = entityManager.unwrap( Session.class ).createQuery( sql );
        query.setParameter( "schedulables", entities );
        query.setResultTransformer( new AliasToBeanResultTransformer( CasterScheduleDTO.class ) );

        return query.list();
    }

    public List<CasterScheduleDTO> loadBySearchType( SearchType searchType, Map<String, Object> parameters )
    {
        if ( searchType == SearchType.MACHINE && parameters != null && !parameters.isEmpty() )
        {
            final String machine = (String) parameters.get( "MACHINE" );
            final List<Integer> executionStates = (List<Integer>) parameters.get( "EXECUTION_STATES" );
            if ( machine != null && executionStates != null )
            {
                final String sql = QUERY + SELECT_MACHINE;
                @SuppressWarnings( "unchecked" )
                final org.hibernate.query.Query<CasterScheduleDTO> query = entityManager.unwrap( Session.class ).createQuery( sql );
                query.setParameter( "machine", machine );
                query.setParameter( "executionStates", executionStates );
                query.setResultTransformer( new AliasToBeanResultTransformer( CasterScheduleDTO.class ) );

                return query.list();

            }
        }
        return Collections.emptyList();
    }

    public CastingBatch findCastingBatch( long objid )
    {
        return castingBatchHome.findById( objid );
    }

    public void unplan( CastingOperation castingOperation ) throws BusinessException
    {
        final Demand demand = castingOperation.getDemand();
        demand.removeAllAssociations();
        demandHome.remove( demand );

        castingOperation.removeAllAssociations();
        castingOperationHome.remove( castingOperation );
    }

    public void unplan( CastingBatch castingBatch ) throws BusinessException
    {
        // Inpsection Values auch löschen
        final List<InspectionValue> inspectionValues = inspectionValueHome.findBySchedulable( castingBatch );
        for ( InspectionValue inspectionValue : inspectionValues )
        {
            inspectionValue.removeAllAssociations();
            inspectionValueHome.remove( inspectionValue );
        }

        castingBatch.removeAllAssociations();
        castingBatchHome.remove( castingBatch );
    }

    public CastingBatch plan( Caster caster, MaterialType materialType, int position )
    {
        return plan( caster, materialType, null, position );
    }

    public CastingBatch plan( Caster caster, CustomerOrderItem customerOrderItem, int position )
    {
        return plan( caster, customerOrderItem.getMaterialType(), customerOrderItem, position );
    }

    public CastingBatch plan( Caster caster, MaterialType materialType, CustomerOrderItem customerOrderItem, int position )
    {
        long lastUsedExecutingSequenceIndex = schedulableHome.findLastExecutingSequenceIndex( caster );

        final CastingBatch castingBatch = new CastingBatch();
        castingBatch.setExecutingSequenceIndex( lastUsedExecutingSequenceIndex + 1 );
        setExecutionState( castingBatch, Casting.SCHEDULABLE_STATE.PLANNED );
        castingBatch.setExecutingMachine( caster );
        castingBatchHome.persist( castingBatch );

        plan( castingBatch, materialType, customerOrderItem, position );

        return castingBatch;
    }

    public void plan( CastingBatch castingBatch, MaterialType materialType, int position )
    {
        plan( castingBatch, materialType, null, position );
    }

    public void plan( CastingBatch castingBatch, CustomerOrderItem customerOrderItem, int position )
    {
        plan( castingBatch, customerOrderItem.getMaterialType(), customerOrderItem, position );
    }

    public void plan( CastingBatch castingBatch, MaterialType materialType, CustomerOrderItem customerOrderItem, int position )
    {
        final Demand demand;
        if ( customerOrderItem != null )
        {
            demand = new Demand();
            demand.setDueDate( customerOrderItem.getRequestedDateFrom() );
            if ( Casting.MACHINE.CASTER_80.equals( castingBatch.getExecutingMachine().getApk() ) )
            {
                demand.setAmount( materialType.getAmount() );
            }
            else
            {
                demand.setAmount( 1 );
            }
            demand.setName( customerOrderItem.getApk() );
            demand.setExecutionState( Casting.SCHEDULABLE_STATE.PLANNED );
            demand.setCustomerOrderItem( customerOrderItem );
            demand.setMaterialType( materialType );
            demandHome.persist( demand );
        }
        else
        {
            demand = new Demand();
            demand.setDueDate( LocalDateTime.now() );
            if ( Casting.MACHINE.CASTER_80.equals( castingBatch.getExecutingMachine().getApk() ) )
            {
                demand.setAmount( materialType.getAmount() );
            }
            else
            {
                demand.setAmount( 1 );
            }
            demand.setName( materialType.getApk() + StringTools.FULLDF.format( new Date() ) );
            demand.setExecutionState( Casting.SCHEDULABLE_STATE.PLANNED );
            demand.setMaterialType( materialType );
            demandHome.persist( demand );
        }

        final CastingOperation castingOperation = new CastingOperation();
        castingOperation.setPosition( position );
        castingOperation.setBatch( castingBatch );
        castingOperation.setDemand( demand );
        setExecutionState( castingOperation, Casting.SCHEDULABLE_STATE.PLANNED );
        castingOperation.setExecutingSequenceIndex( position );
        castingOperation.setName( demand.getName() );
        // Finde length_bonus aus Wissenstabellen
        final ArtikelId artikelId = new ArtikelId();
        artikelId.setBreite( (int) materialType.getWidth() );
        artikelId.setDicke( (int) materialType.getHeight() );
        artikelId.setLaenge( (int) materialType.getLength() );
        artikelId.setQualitaet( materialType.getQualityCode() );
        artikelId.setLegierung( materialType.getAlloy().getName() );
        try
        {
            final Artikel artikel = artikelHome.findById( artikelId );
            if ( artikel != null )
            {
                if ( materialType.getAmount() == 2 )
                {
                    castingOperation.setLengthBonus( artikel.getTgiesslaenge() - ( materialType.getLength() * 2 ) );
                }
                else
                {
                    castingOperation.setLengthBonus( artikel.getGiesslaenge() - materialType.getLength() );
                }
            }
        }
        catch ( NoResultException nrex )
        {
            log.warn( "Kein Artikel für " + artikelId + " gefunden" );
        }

        castingOperationHome.persist( castingOperation );

        setExecutionState( castingBatch, Casting.SCHEDULABLE_STATE.PLANNED );
    }

    public List<Schedulable> changeCastingSequence( Caster caster, long newIndex, List<CastingBatch> sources )
    {
        final List<Schedulable> nextSchedulables = schedulableHome.findEqualsOrGreaterExecutingSequenceIndex( caster, newIndex );
        final List<Schedulable> changedSchedulables = new ArrayList<>();
        for ( Schedulable nextSchedulable : nextSchedulables )
        {
            boolean isOneOfSource = false;
            for ( CastingBatch source : sources )
            {
                if ( source.getObjid() == nextSchedulable.getObjid() )
                {
                    isOneOfSource = true;
                    break;
                }
            }
            if ( isOneOfSource )
            {
                continue;
            }
            nextSchedulable.setExecutingSequenceIndex( nextSchedulable.getExecutingSequenceIndex() + sources.size() );
            changedSchedulables.add( nextSchedulable );
        }
        for ( CastingBatch schedulable : sources )
        {
            schedulable.setExecutingSequenceIndex( newIndex );
            newIndex++;
            changedSchedulables.add( schedulable );
        }
        return changedSchedulables;
    }

    public void releaseCastingBatch( CastingBatch castingBatch, CasterScheduleDTO casterScheduleDTO ) throws BusinessException
    {
        setExecutionState( castingBatch, Casting.SCHEDULABLE_STATE.RELEASED );
        // CastingSequence besetzen
        final Long maxCastingSequence = castingBatchHome.findMaxCastingSequence( castingBatch.getExecutingMachine() );
        long newCastingSequence = 1;
        if ( maxCastingSequence != null )
        {
            newCastingSequence = maxCastingSequence + 1;
        }
        castingBatch.setCastingSequence( newCastingSequence );
        castingBatch.setCharge( casterScheduleDTO.getCharge().substring( 0, 7 ) );
        final MeltingFurnace meltingFurnace = meltingFurnaceHome.findByApk( casterScheduleDTO.getMeltingFurnace() );
        castingBatch.setMeltingFurnace( meltingFurnace );
        castingBatch.setPlannedLength( casterScheduleDTO.getPlannedLength() );
        castingBatch.setPlannedWeight( (int) casterScheduleDTO.getPlannedWeight() );
        castingBatch.setPlannedSuccessTs( casterScheduleDTO.getInProgressTS().withNano( 0 ) );

        final Caster caster = casterHome.findByApk( casterScheduleDTO.getMachine() );
        int lastCharge = Integer.parseInt( casterScheduleDTO.getCharge().substring( 3, 7 ) ) + 1;
        if ( lastCharge > 9999 )
        {
            lastCharge = 0;
        }
        caster.setLastCharge( lastCharge );
    }

    public void setExecutionState( Schedulable schedulable, int executionState )
    {
        schedulableService.setExecutionState( schedulable, executionState );
    }

    public void changeFurnace( CastingBatch castingBatch, String furnaceApk )
    {
        final MeltingFurnace meltingFurnace = meltingFurnaceHome.findByApk( furnaceApk );
        if ( meltingFurnace != null )
        {
            castingBatch.setMeltingFurnace( meltingFurnace );
        }
    }

    public void changeCharge( CastingBatch castingBatch, String charge )
    {
        // Prüfen, ob bereits eine Charge mit diesem Namen existiert
        // dann müssen die Chargennummern getauscht werden
        final CastingBatch existingCharge = castingBatchHome.findByCharge( charge );
        if ( existingCharge != null )
        {
            existingCharge.setCharge( castingBatch.getCharge() );
        }
        castingBatch.setCharge( charge );
    }

    @TransactionAttribute( TransactionAttributeType.REQUIRES_NEW )
    public CastingBatch createChargeBackstage( List<ProductionOrder> productionOrders, Integer moveBeforeExecutionState )
    {
        CastingBatch castingBatch = null;
        try
        {
            castingBatch = createCharge( productionOrders, moveBeforeExecutionState );
        }
        catch ( BusinessException e )
        {
            log.warn( "error creating Charge in Background", e );
        }
        return castingBatch;
    }

    public CastingBatch createCharge( List<ProductionOrder> productionOrders, Integer moveBeforeExecutionState ) throws BusinessException
    {
        String casterApk = null;
        Map<Integer, ProductionOrder> positionIndex = new HashMap<>();
        for ( ProductionOrder productionOrder : productionOrders )
        {
            final List<WorkStep> workSteps = productionOrder.getWorkSteps();
            for ( WorkStep workStep : workSteps )
            {
                if ( workStep.getWorkPlace() != null && workStep.getWorkPlace().startsWith( "ASBA" ) )
                {
                    casterApk = workStep.getWorkPlace().substring( 4, 6 );
                    try
                    {
                        int position = Integer.parseInt( workStep.getWorkPlace().substring( 7 ) );
                        positionIndex.put( position, productionOrder );

                    }
                    catch ( NumberFormatException nfex )
                    {
                        throw new BusinessMessageException( "Die Positionen von den Aufträgen sind nicht lesbar" );
                    }
                    break;
                }
            }
        }
        if ( productionOrders.size() != positionIndex.size() )
        {
            throw new BusinessMessageException( "Die Positionen der Aufträge sind nicht eindeutig" );
        }
        if ( casterApk == null )
        {
            throw new BusinessMessageException( "Es konnte keine Gießanlage gefunden werden" );
        }
        final Caster caster = casterHome.findByApk( casterApk );
        CastingBatch castingBatch = null;
        int productionOrdersMaxLength = 0;
        double productionOrdersSumWeight = 0;
        LocalDateTime productionOrderStartTS = null;
        Map<MaterialType, MaterialType> gwbRohMaterialTypeMap = new HashMap<>();
        for ( Integer position : positionIndex.keySet() )
        {
            final ProductionOrder posProductionOrder = positionIndex.get( position );
            MaterialType gwbMaterialType = materialTypeService.findGWBMaterialType( posProductionOrder.getMaterialType(), 1 );
            if ( Casting.MACHINE.CASTER_80.equals( casterApk ) && gwbMaterialType == null )
            {
                gwbMaterialType = materialTypeService.findGWBMaterialType( posProductionOrder.getMaterialType(), 2 );
            }
            if ( gwbMaterialType == null )
            {
                throw new BusinessMessageException( "Kein GWB-Material gefunden für " + posProductionOrder.getMaterialType().getApk() );
            }
            gwbRohMaterialTypeMap.put( gwbMaterialType, posProductionOrder.getMaterialType() );
            if ( castingBatch == null )
            {
                castingBatch = plan( caster, gwbMaterialType, position );
            }
            else
            {
                plan( castingBatch, gwbMaterialType, position );
            }
            if ( posProductionOrder.getMaterialType().getLength() > productionOrdersMaxLength )
            {
                productionOrdersMaxLength = (int) posProductionOrder.getMaterialType().getLength();
            }
            productionOrdersSumWeight = productionOrdersSumWeight + posProductionOrder.getAmount();
            if ( productionOrderStartTS == null )
            {
                productionOrderStartTS = posProductionOrder.getStartTS();
            }
            else
            {
                if ( !productionOrderStartTS.isEqual( posProductionOrder.getStartTS() ) )
                {
                    throw new BusinessMessageException( "Die Aufträge haben unterschiedliche Start-Zeitpunkte" );
                }
            }
        }

        // Jetzt den Eintrag 'Fixieren', ohne SAP Telegramme zu versenden
        final CasterScheduleDTO casterScheduleDTO = new CasterScheduleDTO();
        int lastCharge = caster.getLastCharge();
        casterScheduleDTO.setCharge( Casting.buildCharge( casterApk, lastCharge ) );
        casterScheduleDTO.setMeltingFurnace( Objects.requireNonNull( Casting.getMeltingFurnacesForCaster( casterApk ) )[0] );
        casterScheduleDTO.setPlannedLength( productionOrdersMaxLength );
        casterScheduleDTO.setPlannedWeight( productionOrdersSumWeight );
        casterScheduleDTO.setInProgressTS( productionOrderStartTS );
        casterScheduleDTO.setMachine( casterApk );
        releaseCastingBatch( castingBatch, casterScheduleDTO );

        // Jetzt die Zuordnung zu den Aufträgen
        // IntermediateType besetzen
        for ( Schedulable member : castingBatch.getMembers() )
        {
            if ( !( member instanceof Operation ) )
            {
                continue;
            }
            final Operation operation = (Operation) member;
            final Demand demand = operation.getDemand();
            if ( demand != null )
            {
                demand.setIntermediateType( gwbRohMaterialTypeMap.get( demand.getMaterialType() ) );
            }
        }
        for ( ProductionOrder productionOrder : productionOrders )
        {
            for ( WorkStep workStep : productionOrder.getWorkSteps() )
            {
                if ( "0030".equals( workStep.getWorkStepNumber() ) && workStep.getWorkPlace() != null && workStep.getWorkPlace().startsWith( "ASBA" ) )
                {
                    productionOrderService.assignDemand( workStep );
                    break;
                }
            }
        }

        if ( moveBeforeExecutionState != null )
        {
            Long firstSequenceIndex = castingBatchHome.findFirstCastingSequence( caster, moveBeforeExecutionState );
            if ( firstSequenceIndex != null )
            {
                final List<Schedulable> changedSchedulables = changeCastingSequence( caster, firstSequenceIndex, Collections.singletonList( castingBatch ) );
                replicateCache( changedSchedulables );
            }
        }

        return castingBatch;
    }
}