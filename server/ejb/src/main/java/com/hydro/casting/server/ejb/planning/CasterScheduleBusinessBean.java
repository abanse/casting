package com.hydro.casting.server.ejb.planning;

import com.hydro.casting.common.Casting;
import com.hydro.casting.common.constant.CasterStep;
import com.hydro.casting.server.contract.dto.CasterDemandDTO;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.CasterSchedulePosDTO;
import com.hydro.casting.server.contract.dto.SetupTypeDTO;
import com.hydro.casting.server.contract.planning.CasterScheduleBusiness;
import com.hydro.casting.server.ejb.main.service.*;
import com.hydro.casting.server.ejb.planning.service.CasterScheduleService;
import com.hydro.casting.server.ejb.sap.service.SAPService;
import com.hydro.casting.server.model.mat.Material;
import com.hydro.casting.server.model.mat.MaterialType;
import com.hydro.casting.server.model.mat.dao.MaterialHome;
import com.hydro.casting.server.model.po.CustomerOrderItem;
import com.hydro.casting.server.model.res.Caster;
import com.hydro.casting.server.model.res.MeltingFurnace;
import com.hydro.casting.server.model.res.dao.MeltingFurnaceHome;
import com.hydro.casting.server.model.sched.CastingBatch;
import com.hydro.casting.server.model.sched.CastingOperation;
import com.hydro.casting.server.model.sched.Schedulable;
import com.hydro.casting.server.model.sched.Setup;
import com.hydro.casting.server.model.sched.dao.CastingBatchHome;
import com.hydro.casting.server.model.sched.dao.SchedulableHome;
import com.hydro.casting.server.model.sched.dao.SetupHome;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.core.common.exception.BusinessValueNotValidException;
import com.hydro.core.common.util.DateTimeUtil;
import com.hydro.core.common.util.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class CasterScheduleBusinessBean implements CasterScheduleBusiness
{
    private final static Logger log = LoggerFactory.getLogger( CasterScheduleBusinessBean.class );

    @EJB
    private ResourceService resourceService;

    @EJB
    private CustomerOrderService customerOrderService;

    @EJB
    private MaterialService materialService;

    @EJB
    private CasterScheduleService casterScheduleService;

    @EJB
    private SetupHome setupHome;

    @EJB
    private SchedulableHome schedulableHome;

    @EJB
    private MachineService machineService;

    @EJB
    private MaterialHome materialHome;

    @EJB
    private CastingBatchHome castingBatchHome;

    @EJB
    private MeltingFurnaceHome meltingFurnaceHome;

    @EJB
    private SAPService sapService;

    @EJB
    private ProcessStepService processStepService;

    @Override
    public void setCasterBatchPositions( List<CasterDemandDTO> demands, List<CasterSchedulePosDTO> schedulePoss, Integer count ) throws BusinessException
    {
        int[] allowedPositions = new int[schedulePoss.size()];
        int index = 0;
        for ( CasterSchedulePosDTO schedulePos : schedulePoss )
        {
            allowedPositions[index] = schedulePos.getPosition();
            index++;
        }
        final List<Schedulable> changedBatches = new ArrayList<>();
        for ( CasterDemandDTO demandDTO : demands )
        {
            // 2 Arten der Erzeugung MaterialType oder CustomerOrderItem
            CustomerOrderItem customerOrderItem = null;
            MaterialType materialType = null;
            int countToSchedule = 0;
            if ( demandDTO.getOrderId() != null && demandDTO.getOrderPosition() != null )
            {
                // Finde CustomerOrderItem (mit Replikation)
                customerOrderItem = customerOrderService.findOrCreateCustomerOrderItem( demandDTO.getOrderId() + "-" + demandDTO.getOrderPosition() );
                countToSchedule = demandDTO.getToProduce();
            }
            else if ( demandDTO.getMaterialType() != null )
            {
                materialType = materialService.findOrCreateMaterialType( demandDTO.getMaterialType() );
                countToSchedule = Integer.MAX_VALUE;
            }
            else
            {
                throw new BusinessValueNotValidException( "CasterDemandDTO", demandDTO.getId(), "materialType", null );
            }

            for ( int i = 0; i < countToSchedule && !schedulePoss.isEmpty(); i++ )
            {
                final CasterSchedulePosDTO targetPos = schedulePoss.remove( 0 );

                final CastingBatch castingBatch;
                if ( targetPos.getCasterSchedule().getId() >= 0 )
                {
                    // Finde Batch
                    castingBatch = casterScheduleService.findCastingBatch( targetPos.getCasterSchedule().getId() );

                    // Gibt es eine Operation an dieser Position diesen Ausplanen
                    final List<Schedulable> members = new ArrayList<>( castingBatch.getMembers() );
                    for ( Schedulable member : members )
                    {
                        if ( member instanceof CastingOperation && ( (CastingOperation) member ).getPosition() == targetPos.getPosition() )
                        {
                            casterScheduleService.unplan( (CastingOperation) member );
                            break;
                        }
                    }

                    // Operation dem Batch hinzufügen
                    if ( customerOrderItem != null )
                    {
                        casterScheduleService.plan( castingBatch, customerOrderItem, targetPos.getPosition() );
                    }
                    else
                    {
                        casterScheduleService.plan( castingBatch, materialType, targetPos.getPosition() );
                    }
                }
                else
                {
                    final Caster caster = resourceService.getCaster( targetPos.getCasterSchedule().getMachine() );
                    if ( customerOrderItem != null )
                    {
                        castingBatch = casterScheduleService.plan( caster, customerOrderItem, targetPos.getPosition() );
                    }
                    else
                    {
                        castingBatch = casterScheduleService.plan( caster, materialType, targetPos.getPosition() );
                    }
                    targetPos.getCasterSchedule().setId( castingBatch.getObjid() );
                }

                if ( !changedBatches.contains( castingBatch ) )
                {
                    changedBatches.add( castingBatch );
                }
            }
        }

        // Falls die Anzahl besetzt ist und größer 1 diese Zeilen kopieren
        if ( count != null && count.intValue() > 1 && changedBatches.size() == 1 )
        {
            changedBatches.addAll( copyCasterBatch( (CastingBatch) changedBatches.get( 0 ), count - 1, allowedPositions ) );
        }

        casterScheduleService.replicateCache( changedBatches );
    }

    @Override
    public void moveCasterBatchPositions( List<CasterSchedulePosDTO> sourcePositions, List<CasterSchedulePosDTO> targetPositions ) throws BusinessException
    {
        if ( sourcePositions.size() != targetPositions.size() )
        {
            throw new BusinessValueNotValidException( "CasterSchedulePos", null, "size", null );
        }

        final List<Schedulable> changedBatches = new ArrayList<>();
        int index = 0;
        for ( CasterSchedulePosDTO sourcePos : sourcePositions )
        {
            final CasterSchedulePosDTO targetPos = targetPositions.get( index );

            CustomerOrderItem customerOrderItem = null;
            MaterialType materialType = null;
            if ( sourcePos.getCustomerOrderItem() != null )
            {
                customerOrderItem = customerOrderService.find( sourcePos.getCustomerOrderItem() );
            }
            else
            {
                materialType = materialService.find( sourcePos.getMaterialType() );
            }

            final CastingBatch castingBatch;
            if ( targetPos.getCasterSchedule().getId() >= 0 )
            {
                castingBatch = casterScheduleService.findCastingBatch( targetPos.getCasterSchedule().getId() );

                // Gibt es eine Operation an dieser Position diesen Ausplanen
                final List<Schedulable> members = new ArrayList<>( castingBatch.getMembers() );
                for ( Schedulable member : members )
                {
                    if ( member instanceof CastingOperation && ( (CastingOperation) member ).getPosition() == targetPos.getPosition() )
                    {
                        casterScheduleService.unplan( (CastingOperation) member );
                        break;
                    }
                }

                if ( customerOrderItem != null )
                {
                    casterScheduleService.plan( castingBatch, customerOrderItem, targetPos.getPosition() );
                }
                else
                {
                    casterScheduleService.plan( castingBatch, materialType, targetPos.getPosition() );
                }
            }
            else
            {
                final Caster caster = resourceService.getCaster( targetPos.getCasterSchedule().getMachine() );
                if ( customerOrderItem != null )
                {
                    castingBatch = casterScheduleService.plan( caster, customerOrderItem, targetPos.getPosition() );
                }
                else
                {
                    castingBatch = casterScheduleService.plan( caster, materialType, targetPos.getPosition() );
                }
                targetPos.getCasterSchedule().setId( castingBatch.getObjid() );
            }

            if ( !changedBatches.contains( castingBatch ) )
            {
                changedBatches.add( castingBatch );
            }

            index++;
        }

        // Source pos löschen
        for ( CasterSchedulePosDTO sourcePos : sourcePositions )
        {
            boolean isSetBefore = false;
            for ( CasterSchedulePosDTO targetPosition : targetPositions )
            {
                if ( sourcePos.getCasterSchedule().getId() == targetPosition.getCasterSchedule().getId() && sourcePos.getPosition() == targetPosition.getPosition() )
                {
                    isSetBefore = true;
                    break;
                }
            }
            if ( isSetBefore )
            {
                continue;
            }

            final CastingBatch castingBatch = casterScheduleService.findCastingBatch( sourcePos.getCasterSchedule().getId() );
            final List<Schedulable> members = new ArrayList<>( castingBatch.getMembers() );
            for ( Schedulable member : members )
            {
                if ( member instanceof CastingOperation && ( (CastingOperation) member ).getPosition() == sourcePos.getPosition() )
                {
                    casterScheduleService.unplan( (CastingOperation) member );
                    break;
                }
            }
            if ( !changedBatches.contains( castingBatch ) )
            {
                changedBatches.add( castingBatch );
            }
        }

        // Prüfen ob komplett gelöscht werden kann
        final List<Long> deletedCastingBatchOids = new ArrayList<>();
        final List<Long> deletedMaterialOids = new ArrayList<>();
        for ( Schedulable changedBatch : new ArrayList<>( changedBatches ) )
        {
            if ( changedBatch instanceof CastingBatch && ( (CastingBatch) changedBatch ).getMembers().isEmpty() )
            {
                final CastingBatch castingBatch = (CastingBatch) changedBatch;
                if ( castingBatch.getConsumedMaterials() != null && !castingBatch.getConsumedMaterials().isEmpty() )
                {
                    for ( Material consumedMaterial : new ArrayList<>( castingBatch.getConsumedMaterials() ) )
                    {
                        consumedMaterial.removeAllAssociations();
                        materialHome.remove( consumedMaterial );
                        deletedMaterialOids.add( consumedMaterial.getObjid() );
                    }
                }
                casterScheduleService.unplan( castingBatch );
                changedBatches.remove( changedBatch );
                deletedCastingBatchOids.add( changedBatch.getObjid() );
            }
        }

        casterScheduleService.replicateCache( changedBatches );
        casterScheduleService.removeFromCache( deletedCastingBatchOids );
        materialService.removeFromCache( deletedMaterialOids );
    }

    @Override
    public void deleteCasterBatchPositions( List<CasterSchedulePosDTO> schedulePoss ) throws BusinessException
    {
        final List<Schedulable> changedBatches = new ArrayList<>();
        for ( CasterSchedulePosDTO pos : schedulePoss )
        {
            // Finde Batch
            final CastingBatch castingBatch = casterScheduleService.findCastingBatch( pos.getCasterSchedule().getId() );

            // Gibt es eine Operation an dieser Position diesen Ausplanen
            final List<Schedulable> members = new ArrayList<>( castingBatch.getMembers() );
            for ( Schedulable member : members )
            {
                if ( member instanceof CastingOperation && ( (CastingOperation) member ).getPosition() == pos.getPosition() )
                {
                    casterScheduleService.unplan( (CastingOperation) member );
                    break;
                }
            }

            if ( !changedBatches.contains( castingBatch ) )
            {
                changedBatches.add( castingBatch );
            }
        }

        // Prüfen ob komplett gelöscht werden kann
        final List<Long> deletedCastingBatchOids = new ArrayList<>();
        final List<Long> deletedMaterialOids = new ArrayList<>();
        for ( Schedulable changedBatch : new ArrayList<>( changedBatches ) )
        {
            if ( changedBatch instanceof CastingBatch && ( (CastingBatch) changedBatch ).getMembers().isEmpty() )
            {
                final CastingBatch castingBatch = (CastingBatch) changedBatch;
                if ( castingBatch.getConsumedMaterials() != null && !castingBatch.getConsumedMaterials().isEmpty() )
                {
                    for ( Material consumedMaterial : new ArrayList<>( castingBatch.getConsumedMaterials() ) )
                    {
                        consumedMaterial.removeAllAssociations();
                        materialHome.remove( consumedMaterial );
                        deletedMaterialOids.add( consumedMaterial.getObjid() );
                    }
                }
                casterScheduleService.unplan( castingBatch );
                changedBatches.remove( changedBatch );
                deletedCastingBatchOids.add( changedBatch.getObjid() );
            }
        }

        casterScheduleService.replicateCache( changedBatches );
        casterScheduleService.removeFromCache( deletedCastingBatchOids );
        materialService.removeFromCache( deletedMaterialOids );
    }

    @Override
    public void createCasterBatch( String machineApk, List<CasterDemandDTO> demands, CasterScheduleDTO refCasterSchedule, boolean after, Integer count ) throws BusinessException
    {
        final Caster caster = resourceService.getCaster( machineApk );

        final List<Schedulable> addedBatches = new ArrayList<>();
        int countToAdd = 1;
        if ( count != null && count > 1 )
        {
            countToAdd = count;
        }

        for ( int i = 0; i < countToAdd; i++ )
        {
            for ( CasterDemandDTO demandDTO : demands )
            {
                final CastingBatch castingBatch;
                if ( demandDTO.getOrderId() != null && demandDTO.getOrderPosition() != null )
                {
                    final CustomerOrderItem customerOrderItem = customerOrderService.findOrCreateCustomerOrderItem( demandDTO.getOrderId() + "-" + demandDTO.getOrderPosition() );
                    castingBatch = casterScheduleService.plan( caster, customerOrderItem, 1 );
                }
                else if ( demandDTO.getMaterialType() != null )
                {
                    final MaterialType materialType = materialService.findOrCreateMaterialType( demandDTO.getMaterialType() );
                    castingBatch = casterScheduleService.plan( caster, materialType, 1 );
                }
                else
                {
                    throw new BusinessValueNotValidException( "CasterDemandDTO", demandDTO.getId(), "materialType", null );
                }
                addedBatches.add( castingBatch );
            }
        }

        casterScheduleService.replicateCache( addedBatches );
    }

    @Override
    public void copyCasterBatch( CasterScheduleDTO casterSchedule, int countCopies ) throws BusinessException
    {
        final CastingBatch castingBatch = casterScheduleService.findCastingBatch( casterSchedule.getId() );

        final List<Schedulable> copies = copyCasterBatch( castingBatch, countCopies, null );

        casterScheduleService.replicateCache( copies );
    }

    private List<Schedulable> copyCasterBatch( CastingBatch source, int countCopies, int[] allowedPositions ) throws BusinessException
    {
        final List<Schedulable> changedSchedulables = new ArrayList<>();
        final Caster caster = (Caster) source.getExecutingMachine();

        long newIndex = source.getExecutingSequenceIndex() + 1;
        // vorher lücke schaffen
        final List<Schedulable> nextSchedulables = schedulableHome.findEqualsOrGreaterExecutingSequenceIndex( caster, newIndex );
        for ( Schedulable nextSchedulable : nextSchedulables )
        {
            nextSchedulable.setExecutingSequenceIndex( nextSchedulable.getExecutingSequenceIndex() + countCopies );
            changedSchedulables.add( nextSchedulable );
        }

        for ( int i = 0; i < countCopies; i++ )
        {
            CastingBatch copy = null;
            final List<Schedulable> members = source.getMembers();
            for ( Schedulable member : members )
            {
                if ( !( member instanceof CastingOperation ) )
                {
                    continue;
                }
                final CastingOperation castingOperation = (CastingOperation) member;
                if ( allowedPositions != null )
                {
                    boolean posAllowed = false;
                    for ( int allowedPosition : allowedPositions )
                    {
                        if ( castingOperation.getPosition() == allowedPosition )
                        {
                            posAllowed = true;
                            break;
                        }
                    }
                    if ( !posAllowed )
                    {
                        continue;
                    }
                }
                final CustomerOrderItem customerOrderItem = castingOperation.getDemand().getCustomerOrderItem();
                final MaterialType materialType = castingOperation.getDemand().getMaterialType();
                if ( copy == null )
                {
                    if ( customerOrderItem != null )
                    {
                        copy = casterScheduleService.plan( caster, customerOrderItem, castingOperation.getPosition() );
                    }
                    else
                    {
                        copy = casterScheduleService.plan( caster, materialType, castingOperation.getPosition() );
                    }
                }
                else
                {
                    if ( customerOrderItem != null )
                    {
                        casterScheduleService.plan( copy, customerOrderItem, castingOperation.getPosition() );
                    }
                    else
                    {
                        casterScheduleService.plan( copy, materialType, castingOperation.getPosition() );
                    }
                }
            }
            copy.setExecutingSequenceIndex( newIndex );
            changedSchedulables.add( copy );
            newIndex++;
        }

        return changedSchedulables;
    }

    @Override
    public void updateProcessOrder( CasterScheduleDTO casterScheduleDTO, String newProcessOrder ) throws BusinessException
    {
        if ( casterScheduleDTO == null || casterScheduleDTO.getId() < 0 )
        {
            return;
        }
        final CastingBatch castingBatch = casterScheduleService.findCastingBatch( casterScheduleDTO.getId() );
        if ( castingBatch == null )
        {
            return;
        }
        castingBatch.setProcessOrder( newProcessOrder );

        casterScheduleService.replicateCache( castingBatch );
    }

    @Override
    public void insertSetup( String currentUser, String machineApk, SetupTypeDTO setupType, CasterScheduleDTO beforeEntry ) throws BusinessException
    {
        final Caster caster = resourceService.getCaster( machineApk );

        final Setup setup = new Setup();
        setup.setExecutingMachine( caster );
        setup.setExecutionState( Casting.SCHEDULABLE_STATE.PLANNED );
        setup.setAnnotation( setupType.getInstruction() );
        setup.setDuration( setupType.getDuration() );

        final List<Schedulable> changedSchedulables = new ArrayList<>();
        if ( beforeEntry == null )
        {
            long lastUsedExecutingSequenceIndex = schedulableHome.findLastExecutingSequenceIndex( caster );
            setup.setExecutingSequenceIndex( lastUsedExecutingSequenceIndex + 1 );
        }
        else
        {
            final List<Schedulable> nextSchedulables = schedulableHome.findEqualsOrGreaterExecutingSequenceIndex( caster, beforeEntry.getExecutingSequenceIndex() );
            for ( Schedulable nextSchedulable : nextSchedulables )
            {
                nextSchedulable.setExecutingSequenceIndex( nextSchedulable.getExecutingSequenceIndex() + 1 );
                changedSchedulables.add( nextSchedulable );
            }
            setup.setExecutingSequenceIndex( beforeEntry.getExecutingSequenceIndex() );
        }
        changedSchedulables.add( setup );

        setupHome.persist( setup );

        casterScheduleService.replicateCache( changedSchedulables );
    }

    @Override
    public void deleteSetups( List<CasterScheduleDTO> setups ) throws BusinessException
    {
        final List<Long> deletedSetupOids = new ArrayList<>();
        for ( CasterScheduleDTO setupDTO : setups )
        {
            final Setup setup = setupHome.findById( setupDTO.getId() );
            setup.removeAllAssociations();
            setupHome.remove( setup );
            deletedSetupOids.add( setup.getObjid() );
        }
        casterScheduleService.removeFromCache( deletedSetupOids );
    }

    @Override
    public void updateAnnotation( CasterScheduleDTO casterScheduleDTO, String newAnnotation ) throws BusinessException
    {
        final Schedulable schedulable = schedulableHome.findById( casterScheduleDTO.getId() );
        schedulable.setAnnotation( newAnnotation );

        casterScheduleService.replicateCache( schedulable );
    }

    @Override
    public void updateAlloySourcePercentage( CasterScheduleDTO casterScheduleDTO, String alloySource, int newPercentage ) throws BusinessException
    {
        final CastingBatch castingBatch = casterScheduleService.findCastingBatch( casterScheduleDTO.getId() );
        if ( Casting.ALLOY_SOURCES.MELTING_FURNACE_S1.equals( alloySource ) )
        {
            castingBatch.setPercentageS1( (double) newPercentage );
        }
        else if ( Casting.ALLOY_SOURCES.MELTING_FURNACE_S2.equals( alloySource ) )
        {
            castingBatch.setPercentageS2( (double) newPercentage );
        }
        else if ( Casting.ALLOY_SOURCES.UBC_S3.equals( alloySource ) )
        {
            castingBatch.setPercentageS3( (double) newPercentage );
        }
        else if ( Casting.ALLOY_SOURCES.ELEKTROLYSE.equals( alloySource ) )
        {
            castingBatch.setPercentageEL( (double) newPercentage );
        }
        else if ( Casting.ALLOY_SOURCES.REAL_ALLOY.equals( alloySource ) )
        {
            castingBatch.setPercentageRA( (double) newPercentage );
        }
        casterScheduleService.replicateCache( castingBatch );
    }

    @Override
    public void moveSchedulables( String currentUser, String machineApk, List<CasterScheduleDTO> sources, CasterScheduleDTO target, boolean afterRow ) throws BusinessException
    {
        log.info( "currentUser = " + currentUser + ", machineApk = " + machineApk + ", sources = " + sources + ", target = " + target + ", afterRow = " + afterRow );
        final Caster caster = resourceService.getCaster( machineApk );
        long newIndex;
        if ( afterRow )
        {
            newIndex = target.getExecutingSequenceIndex() + 1;
        }
        else
        {
            newIndex = target.getExecutingSequenceIndex();
        }
        final List<CastingBatch> sourceBatches = new ArrayList<>();
        for ( CasterScheduleDTO source : sources )
        {
            sourceBatches.add( castingBatchHome.findById( source.getId() ) );
        }

        final List<Schedulable> changedSchedulables = casterScheduleService.changeCastingSequence( caster, newIndex, sourceBatches );

        casterScheduleService.replicateCache( changedSchedulables );
    }

    @Override
    public void saveLastCharge( String machineApk, int lastCharge ) throws BusinessException
    {
        final Caster caster = resourceService.getCaster( machineApk );
        caster.setLastCharge( lastCharge );
        machineService.replicateCache( caster );
    }

    @Override
    public void releaseSchedulables( List<CasterScheduleDTO> schedules ) throws BusinessException
    {
        final List<Schedulable> changedSchedulables = new ArrayList<>();
        Caster caster = null;

        for ( CasterScheduleDTO casterScheduleDTO : schedules )
        {
            final CastingBatch castingBatch = casterScheduleService.findCastingBatch( casterScheduleDTO.getId() );
            casterScheduleService.releaseCastingBatch( castingBatch, casterScheduleDTO );
            if ( caster == null )
            {
                caster = resourceService.getCaster( casterScheduleDTO.getMachine() );
            }

            sapService.sendPLAN( castingBatch );

            changedSchedulables.add( castingBatch );
        }

        casterScheduleService.replicateCache( changedSchedulables );
        if ( caster != null )
        {
            machineService.replicateCache( caster );
        }
    }

    @Override
    public void updateDuration( CasterScheduleDTO schedule, boolean forCasting, Long newDuration ) throws BusinessException
    {
        final CastingBatch castingBatch = casterScheduleService.findCastingBatch( schedule.getId() );
        if ( castingBatch == null )
        {
            return;
        }
        if ( forCasting )
        {
            if ( newDuration != null )
            {
                castingBatch.setPlannedCastingDuration( newDuration.intValue() );
            }
            else
            {
                castingBatch.setPlannedCastingDuration( null );
            }
        }
        else
        {
            if ( newDuration != null )
            {
                castingBatch.setPlannedMeltingDuration( newDuration.intValue() );
            }
            else
            {
                castingBatch.setPlannedMeltingDuration( null );
            }
        }
        casterScheduleService.replicateCache( castingBatch );
    }

    @Override
    public long createPlannedConsumedMaterial( CasterScheduleDTO schedule, String source, double weight ) throws BusinessException
    {
        final CastingBatch castingBatch = casterScheduleService.findCastingBatch( schedule.getId() );
        if ( castingBatch == null )
        {
            return -1;
        }
        final Material plannedConsumedMaterial = new Material();
        plannedConsumedMaterial.setName( source + " " + StringTools.N01F.format( weight / 1000.0 ) );
        plannedConsumedMaterial.setSource( source );
        plannedConsumedMaterial.setWeight( weight );
        plannedConsumedMaterial.setGenerationState( Casting.SCHEDULABLE_STATE.PLANNED );
        plannedConsumedMaterial.setConsumptionState( Casting.SCHEDULABLE_STATE.PLANNED );
        plannedConsumedMaterial.setConsumingOperation( castingBatch );

        materialHome.persist( plannedConsumedMaterial );

        // replicate Cache
        materialService.replicateCache( plannedConsumedMaterial );

        return plannedConsumedMaterial.getObjid();
    }

    @Override
    public void deletePlannedConsumedMaterial( CasterScheduleDTO schedule, long plannedConsumedMaterialOID ) throws BusinessException
    {
        final Material plannedConsumedMaterial = materialHome.findById( plannedConsumedMaterialOID );
        plannedConsumedMaterial.removeAllAssociations();
        materialHome.remove( plannedConsumedMaterial );

        materialService.removeFromCache( plannedConsumedMaterial.getObjid() );
    }

    @Override
    public void overwriteChargeValues( CasterScheduleDTO schedule, long castingSequence, String charge, String furnace ) throws BusinessException
    {
        final CastingBatch castingBatch = casterScheduleService.findCastingBatch( schedule.getId() );
        if ( castingBatch == null )
        {
            return;
        }
        castingBatch.setCastingSequence( castingSequence );
        castingBatch.setCharge( charge );
        final MeltingFurnace meltingFurnace = meltingFurnaceHome.findByApk( furnace );
        castingBatch.setMeltingFurnace( meltingFurnace );
        casterScheduleService.replicateCache( castingBatch );
    }

    @Override
    public void changeExecutionState( CasterScheduleDTO schedule, int executionState, Long timestamp ) throws BusinessException
    {
        final CastingBatch castingBatch = casterScheduleService.findCastingBatch( schedule.getId() );
        if ( castingBatch == null )
        {
            return;
        }
        casterScheduleService.setExecutionState( castingBatch, executionState );

        LocalDateTime changeTime = LocalDateTime.now();
        if ( timestamp != null )
        {
            changeTime = DateTimeUtil.asLocalDateTime( timestamp );
        }
        // Start process step according to the execution state
        switch ( executionState )
        {
        case Casting.SCHEDULABLE_STATE.CASTING_IN_PROGRESS:
            processStepService.startProcessStep( castingBatch, CasterStep.Casting, changeTime );
            break;
        case Casting.SCHEDULABLE_STATE.UNLOADING_IN_PROGRESS:
            castingBatch.setUnloadingTS( changeTime );
            processStepService.startProcessStep( castingBatch, CasterStep.Unloading, changeTime );
            break;
        case Casting.SCHEDULABLE_STATE.FAILED:
        case Casting.SCHEDULABLE_STATE.CANCELED:
        case Casting.SCHEDULABLE_STATE.SUCCESS:
            processStepService.finishCurrentProcessStep( castingBatch, changeTime );
        }

        if ( Casting.SCHEDULABLE_STATE.SUCCESS == executionState || Casting.SCHEDULABLE_STATE.CANCELED == executionState )
        {
            casterScheduleService.removeFromCache( schedule.getId() );
        }
        else
        {
            casterScheduleService.replicateCache( castingBatch );
        }
    }

    @Override
    public void reorganizeCharge( CasterScheduleDTO schedule ) throws BusinessException
    {
        // aktuelle Charge als Basis
        final CastingBatch castingBatch = casterScheduleService.findCastingBatch( schedule.getId() );
        if ( castingBatch == null )
        {
            return;
        }

        final String casterApk = castingBatch.getExecutingMachine().getApk();

        // alle folgenden Batches in der richtigen Reihenfolge suchen
        final List<CastingBatch> nextFixedEntries = castingBatchHome.findNextInSchedule( casterApk, castingBatch.getExecutingSequenceIndex(), Casting.SCHEDULABLE_STATE.RELEASED );
        // Folgechargen auf 'Null' setzen, damit Prüfung von vergebenen Nummern funktioniert
        for ( CastingBatch nextFixedEntry : nextFixedEntries )
        {
            nextFixedEntry.setCharge( null );
        }
        // jetzt Liste abarbeiten
        final List<Schedulable> changedSchedulables = new ArrayList<>();
        String charge = castingBatch.getCharge();
        for ( CastingBatch nextFixedEntry : nextFixedEntries )
        {
            // nächste Nummer berechnen
            String nextCharge = charge;
            boolean nextChargeUsable = false;
            do
            {
                nextCharge = Casting.getNextCharge( casterApk, nextCharge );
                log.info( "check next charge " + nextCharge );
                // Prüfen, ob bereits vergeben
                nextChargeUsable = !castingBatchHome.existCharge( nextCharge );
            }
            while ( !nextChargeUsable );
            // Charge besetzen
            log.info( "use next charge " + nextCharge );
            nextFixedEntry.setCharge( nextCharge );
            changedSchedulables.add( nextFixedEntry );
            charge = nextCharge;
        }
        // Letzte vergebene Nummer noch mal prüfen
        int lastChargeInt = Integer.parseInt( charge.substring( 3, 7 ) ) + 1;
        saveLastCharge( casterApk, lastChargeInt );

        // geänderte Chargen Cache aktualisieren
        casterScheduleService.replicateCache( changedSchedulables );
    }
}
