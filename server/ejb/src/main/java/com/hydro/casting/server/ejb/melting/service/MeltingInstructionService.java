package com.hydro.casting.server.ejb.melting.service;

import com.hydro.casting.common.Casting;
import com.hydro.casting.common.constant.MelterStep;
import com.hydro.casting.server.contract.dto.MeltingInstructionDTO;
import com.hydro.casting.server.ejb.main.service.ProcessStepService;
import com.hydro.casting.server.model.res.MeltingFurnace;
import com.hydro.casting.server.model.sched.MeltingBatch;
import com.hydro.casting.server.model.sched.dao.MeltingBatchHome;
import com.hydro.core.common.cache.ServerCacheManager;
import com.hydro.core.server.common.service.BaseService;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.transform.AliasToBeanResultTransformer;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.TransactionSynchronizationRegistry;
import java.util.Collection;
import java.util.List;

@Stateless
public class MeltingInstructionService extends BaseService<MeltingFurnace, MeltingInstructionDTO>
{
    //@formatter:off
    // executionState = 300 -> Casting.SCHEDULABLE_STATE.IN_PROGRESS -> schedulable (melting batch) is active in a furnace
    private final static String QUERY =
            "   select mf.apk as machine,"
            + " mb.objid as meltingBatchOID, "
            + " mb.charge as charge, "
            + " mb.alloyName as alloy "
            + " from MeltingFurnace mf left join MeltingBatch mb on mb.executingMachine = mf and mb.executionState = 300 ";
    private final static String SELECT_WHERE =
            "   where mf in (:machines)";
    //@formatter:on

    @Inject
    ServerCacheManager serverCacheManager;

    @EJB
    private MeltingBatchHome meltingBatchHome;
    @EJB
    private ProcessStepService processStepService;

    @Resource
    private TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public MeltingInstructionService()
    {
        super( Casting.CACHE.PROD_CACHE_NAME, Casting.CACHE.PROD_VERSION_CACHE_NAME, Casting.CACHE.MELTING_INSTRUCTION_PATH );
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
    public List<MeltingInstructionDTO> load()
    {
        @SuppressWarnings( "unchecked" )
        final Query<MeltingInstructionDTO> query = entityManager.unwrap( Session.class ).createQuery( QUERY );

        query.setResultTransformer( new AliasToBeanResultTransformer( MeltingInstructionDTO.class ) );

        List<MeltingInstructionDTO> result = query.list();
        populateProcessStepData( result );
        return result;
    }

    @Override
    public List<MeltingInstructionDTO> load( Collection<MeltingFurnace> entities )
    {
        @SuppressWarnings( "unchecked" )
        final Query<MeltingInstructionDTO> query = entityManager.unwrap( Session.class ).createQuery( QUERY + SELECT_WHERE );
        query.setParameter( "machines", entities );
        query.setResultTransformer( new AliasToBeanResultTransformer( MeltingInstructionDTO.class ) );

        List<MeltingInstructionDTO> result = query.list();
        populateProcessStepData( result );
        return result;
    }

    private void populateProcessStepData( List<MeltingInstructionDTO> meltingInstructionDTOList )
    {
        for ( MeltingInstructionDTO meltingInstructionDTO : meltingInstructionDTOList )
        {
            if ( meltingInstructionDTO.getMeltingBatchOID() != null )
            {
                MeltingBatch meltingBatch = meltingBatchHome.findById( meltingInstructionDTO.getMeltingBatchOID() );
                meltingInstructionDTO.setMeltingStartTS( processStepService.getActiveStepStartTS( meltingBatch, MelterStep.Melting ) );
                meltingInstructionDTO.setSkimmingStartTS( processStepService.getActiveStepStartTS( meltingBatch, MelterStep.Skimming ) );
                meltingInstructionDTO.setSkimmingMeltingChamberStartTS( processStepService.getActiveStepStartTS( meltingBatch, MelterStep.SkimmingMeltingChamber ) );
                meltingInstructionDTO.setMixingStartTS( processStepService.getActiveStepStartTS( meltingBatch, MelterStep.Mixing ) );
                meltingInstructionDTO.setPouringStartTS( processStepService.getActiveStepStartTS( meltingBatch, MelterStep.Pouring ) );
            }
        }
    }
}
