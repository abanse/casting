package com.hydro.casting.server.ejb.main.service;

import com.hydro.casting.server.contract.dto.ProcessDocuDTO;
import com.hydro.casting.server.contract.dto.ProductionOrderDTO;
import com.hydro.casting.server.ejb.planning.service.CasterScheduleService;
import com.hydro.casting.server.model.mat.MaterialType;
import com.hydro.casting.server.model.po.ProductionOrder;
import com.hydro.casting.server.model.po.WorkStep;
import com.hydro.casting.server.model.sched.Batch;
import com.hydro.casting.server.model.sched.CastingBatch;
import com.hydro.casting.server.model.sched.Demand;
import com.hydro.casting.server.model.sched.Schedulable;
import com.hydro.casting.server.model.sched.dao.DemandHome;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.core.server.contract.workplace.SearchType;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Stateless
public class ProductionOrderService
{
    private final static Logger log = LoggerFactory.getLogger( ProductionOrderService.class );

    //@formatter:off
    private final static String QUERY =
              "   SELECT po.objid as id, "
            + "          po.apk as apk, "
            + "          po.executionState as executionState, "
            + "          po.kind as kind, "
            + "          po.amount as amount, "
            + "          po.unit as unit, "
            + "          po.erpCharge as erpCharge, "
            + "          po.startTS as startTS, "
            + "          po.endTS as endTS, "
            + "          po.description as description, "
            + "          po.materialType.apk as materialTypeApk, "
            + "          po.materialType.description as materialTypeDescription, "
            + "          (select max(op.batch.charge) from Operation op where op.demand.workStep.productionOrder = po) as charge "
            + "     FROM ProductionOrder po ";
    private final static String CHARGE_QUERY =
              "   SELECT po.objid as id, "
            + "          po.apk as apk, "
            + "          po.executionState as executionState, "
            + "          po.kind as kind, "
            + "          po.amount as amount, "
            + "          po.unit as unit, "
            + "          po.erpCharge as erpCharge, "
            + "          po.startTS as startTS, "
            + "          po.endTS as endTS, "
            + "          po.description as description, "
            + "          po.materialType.apk as materialTypeApk, "
            + "          po.materialType.description as materialTypeDescription, "
            + "          cb.charge as charge "
            + "     FROM Operation op "
            + "          join CastingBatch cb on op.batch = cb "
            + "          join op.demand d "
            + "          join d.workStep ws "
            + "          join ws.productionOrder po "
            + "    WHERE cb.charge LIKE :charge "
            + " ORDER BY po.objid";
    private final static String PA_WHERE =
              "    WHERE po.apk LIKE :pa "
            + " ORDER BY po.objid";
    private final static String TIME_RANGE_WHERE =
              "    WHERE po.startTS >= :fromDateTime "
            + "      AND po.startTS <= :toDateTime "
            + " ORDER BY po.objid";
    //@formatter:on

    @EJB
    private DemandHome demandHome;

    @EJB
    private CasterScheduleService casterScheduleService;

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void assignDemand( final WorkStep workStep ) throws BusinessException
    {
        if ( workStep == null )
        {
            log.info( "WorkStep is null" );
            return;
        }
        final ProductionOrder productionOrder = workStep.getProductionOrder();
        if ( productionOrder == null )
        {
            log.info( "ProductionOrder is null" );
            return;
        }

        final String workPlace = workStep.getWorkPlace();
        if ( workPlace == null || !workPlace.startsWith( "ASBA" ) )
        {
            log.info( "WorkPlace not readable " + workPlace );
            return;
        }

        final MaterialType intermediateType = productionOrder.getMaterialType();
        if ( intermediateType == null )
        {
            log.info( "IntermediateType is null" );
            return;
        }

        final int position = Integer.parseInt( workPlace.substring( workPlace.length() - 1 ) );
        final LocalDateTime plannedSuccessTs = productionOrder.getStartTS();

        final Demand demand = demandHome.findByPLANValues( intermediateType, position, plannedSuccessTs );
        if ( demand == null )
        {
            log.info( "No Demand found for " + intermediateType + " " + position + " " + plannedSuccessTs );
            return;
        }
        demand.setWorkStep( workStep );

        // Wenn alle Positionen des CastingBatches besetzt sind ProzessAuftrag besetzen und replizieren
        final Optional<Schedulable> sched = demand.getSchedulables().stream().findFirst();
        if ( sched.isPresent() )
        {
            final Batch batch = sched.get().getBatch();
            if ( batch != null )
            {
                String lastPO = null;
                boolean allWorkStepsSet = true;
                for ( Schedulable member : batch.getMembers() )
                {
                    if ( member.getDemand() == null || member.getDemand().getWorkStep() == null || member.getDemand().getWorkStep().getProductionOrder() == null )
                    {
                        allWorkStepsSet = false;
                        break;
                    }
                    final String po = member.getDemand().getWorkStep().getProductionOrder().getApk();
                    if ( lastPO == null || po.compareTo( po ) > 0 )
                    {
                        lastPO = po;
                    }
                }
                if ( allWorkStepsSet && lastPO != null && lastPO.length() > 4 && batch instanceof CastingBatch )
                {
                    final CastingBatch castingBatch = (CastingBatch) batch;
                    castingBatch.setProcessOrder( lastPO.substring( lastPO.length() - 4 ) );

                    casterScheduleService.replicateCache( castingBatch );
                }
            }
        }
    }

    public List<ProductionOrderDTO> loadBySearchType( SearchType searchType, Map<String, Object> searchValues )
    {
        final String sql;
        if ( searchType == SearchType.CHARGE )
        {
            sql = CHARGE_QUERY;
        }
        else if ( searchType == SearchType.LOT )
        {
            sql = QUERY + PA_WHERE;
        }
        else if ( searchType == SearchType.TIME_RANGE )
        {
            LocalDateTime to = (LocalDateTime) searchValues.get( "toDateTime" );
            if ( to == null )
            {
                final LocalDateTime fromLDT = (LocalDateTime) searchValues.get( "fromDateTime" );
                searchValues.put( "toDateTime", fromLDT.toLocalDate().atStartOfDay().plusHours( 24 ) );
            }
            sql = QUERY + TIME_RANGE_WHERE;
        }
        else
        {
            return Collections.emptyList();
        }

        @SuppressWarnings( "unchecked" )
        final org.hibernate.query.Query<ProductionOrderDTO> query = entityManager.unwrap( Session.class ).createQuery( sql );
        for ( String parameterKey : searchValues.keySet() )
        {
            query.setParameter( parameterKey, searchValues.get( parameterKey ) );
        }
        query.setResultTransformer( new AliasToBeanResultTransformer( ProductionOrderDTO.class ) );
        query.setMaxResults( 5000 );

        return query.list();
    }
}
