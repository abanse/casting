package com.hydro.casting.server.ejb.prod.service;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.dto.AnalysisDetailDTO;
import com.hydro.casting.server.contract.dto.ChargeSpecificationDTO;
import com.hydro.casting.server.contract.dto.FurnaceInstructionDTO;
import com.hydro.casting.server.contract.dto.SpecificationElementDTO;
import com.hydro.casting.server.ejb.analysis.service.AnalysisService;
import com.hydro.casting.server.model.mat.Analysis;
import com.hydro.casting.server.model.mat.dao.AnalysisHome;
import com.hydro.casting.server.model.res.MeltingFurnace;
import com.hydro.core.common.cache.ServerCacheManager;
import com.hydro.core.server.common.service.BaseService;
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
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.transaction.TransactionSynchronizationRegistry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Stateless
public class FurnaceInstructionService extends BaseService<MeltingFurnace, FurnaceInstructionDTO>
{
    private final static Logger log = LoggerFactory.getLogger( FurnaceInstructionService.class );
    //@formatter:off
    private final static String QUERY =
            "   SELECT mf.apk as furnace, "
          + "          cb.objid as castingBatchOID, "
          + "          cb.charge as charge, "
          + "          (select max(op.demand.materialType.alloy.name) from Operation op where op.batch = cb) as alloy, "
          + "          cb.plannedWeight as plannedWeight, "
          + "          mf.maxWeight as maxWeight "
          //+ "          cb. as utilization, "
//          + "          cb.charge as bottomWeight, "
//          + "          cb.charge as solidMetal, "
//          + "          cb.charge as liquidMetal "
//          + "     FROM MeltingFurnace mf left join CastingBatch cb on cb.meltingFurnace = mf and (cb.executionState = 300 or cb.executionState = 310)";
          + "     FROM MeltingFurnace mf left join CastingBatch cb on cb.meltingFurnace = mf and cb.executionState = 300";
    private final static String SELECT_WHERE =
            "    WHERE mf in (:machines)";
    //@formatter:on
    @Inject
    private ServerCacheManager serverCacheManager;

    @Resource
    private TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    @EJB
    private AnalysisService analysisService;

    @EJB
    private AnalysisHome analysisHome;

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public FurnaceInstructionService()
    {
        super( Casting.CACHE.PROD_CACHE_NAME, Casting.CACHE.PROD_VERSION_CACHE_NAME, Casting.CACHE.FURNACE_INSTRUCTION_DATA_PATH );
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
    public List<FurnaceInstructionDTO> load()
    {
        @SuppressWarnings( "unchecked" )
        final org.hibernate.query.Query<FurnaceInstructionDTO> query = entityManager.unwrap( Session.class ).createQuery( QUERY );
        query.setResultTransformer( new AliasToBeanResultTransformer( FurnaceInstructionDTO.class ) );

        final List<FurnaceInstructionDTO> furnaceInstructions = query.list();

        furnaceInstructions.forEach( this::mergeAlloy );
        furnaceInstructions.forEach( this::mergeAnalysis );

        return furnaceInstructions;
    }

    @Override
    public List<FurnaceInstructionDTO> load( Collection<MeltingFurnace> entities )
    {
        final String sql = QUERY + SELECT_WHERE;
        @SuppressWarnings( "unchecked" )
        final org.hibernate.query.Query<FurnaceInstructionDTO> query = entityManager.unwrap( Session.class ).createQuery( sql );
        query.setParameter( "machines", entities );
        query.setResultTransformer( new AliasToBeanResultTransformer( FurnaceInstructionDTO.class ) );

        final List<FurnaceInstructionDTO> furnaceInstructions = query.list();

        furnaceInstructions.forEach( this::mergeAlloy );
        furnaceInstructions.forEach( this::mergeAnalysis );

        return furnaceInstructions;
    }

    private void mergeAlloy( FurnaceInstructionDTO furnaceInstruction )
    {
        if ( furnaceInstruction.getAlloy() == null )
        {
            return;
        }

        final ChargeSpecificationDTO chargeSpecificationDTO = new ChargeSpecificationDTO();
        chargeSpecificationDTO.setCharge( furnaceInstruction.getCharge() );
        chargeSpecificationDTO.setName( furnaceInstruction.getCharge() );

        final List<SpecificationElementDTO> elements = new ArrayList<>();

        //@formatter:off
        final TypedQuery<Tuple> query = entityManager.createQuery(
                "select ae.name as name, "
                        + "ae.elementUnit as elementUnit, "
                        + "ae.minValue as minValue, "
                        + "ae.maxValue as maxValue, "
                        + "ae.precision as precision "
                        + "from AlloyElement ae "
                        + "where ae.alloy.objid = (select max(a.objid) from Alloy a where a.active = true and a.name = :alloyName) "
                        + "order by ae.elementIndex",
                Tuple.class );
        //@formatter:on
        query.setParameter( "alloyName", furnaceInstruction.getAlloy() );
        log.trace( "getAlloyElements" );
        final List<Tuple> analysisElements = query.getResultList();
        for ( Tuple analysisElement : analysisElements )
        {
            SpecificationElementDTO specificationElementDTO = new SpecificationElementDTO();
            specificationElementDTO.setName( analysisElement.get( "name", String.class ) );
            specificationElementDTO.setUnit( analysisElement.get( "elementUnit", String.class ) );
            specificationElementDTO.setMinValue( analysisElement.get( "minValue", Double.class ) );
            specificationElementDTO.setCastingMinValue( null );
            specificationElementDTO.setCastingMaxValue( null );
            specificationElementDTO.setMaxValue( analysisElement.get( "maxValue", Double.class ) );
            specificationElementDTO.setElementFactor( 1.0 );
            specificationElementDTO.setPrecision( analysisElement.get( "precision", Integer.class ) );

            elements.add( specificationElementDTO );
        }

        chargeSpecificationDTO.setElements( elements );

        furnaceInstruction.setChargeSpecification( chargeSpecificationDTO );
    }

    private void mergeAnalysis( FurnaceInstructionDTO furnaceInstruction )
    {
        if ( furnaceInstruction.getCastingBatchOID() == null )
        {
            furnaceInstruction.setChargeAnalysisDetail( null );
            return;
        }
        final List<Analysis> analysis = analysisHome.findBySchedulableOID( furnaceInstruction.getCastingBatchOID() );

        final AnalysisDetailDTO analysisDetailDTO = new AnalysisDetailDTO();
        analysisDetailDTO.setName( furnaceInstruction.getCharge() );
        analysisDetailDTO.setIsLeaf( false );

        analysisService.populateDtoFromAnalysisDetail( analysis, analysisDetailDTO, null, false );

        furnaceInstruction.setChargeAnalysisDetail( analysisDetailDTO );
    }
}