package com.hydro.casting.server.ejb.prod.service;

import com.hydro.casting.server.contract.dto.ProcessDocuDTO;
import com.hydro.core.server.contract.workplace.SearchType;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Stateless
public class ProcessDocuService
{
    //@formatter:off
    private final static String QUERY =
              "   SELECT cb.objid as id, "
            + "          cb.charge as charge, "
            + "          (select max(op.demand.materialType.alloy.name) from Operation op where op.batch = cb) as alloy, "
            + "          cb.successTS as castingTS, "
            + "          (select max(iv.addTS) from InspectionValue iv where iv.inspectionRule.inspectionCategory.apk = 'equipmentCondition' and iv.schedulable = cb) as equipmentConditionTS, "
            + "          (select max(iv.result) from InspectionValue iv where iv.inspectionRule.inspectionCategory.apk = 'equipmentCondition' and iv.schedulable = cb) as equipmentConditionSummary, "
            + "          (select max(iv.addTS) from InspectionValue iv where iv.inspectionRule.inspectionCategory.apk = 'visualInspection' and iv.schedulable = cb) as visualInspectionTS, "
            + "          (select max(iv.result) from InspectionValue iv where iv.inspectionRule.inspectionCategory.apk = 'visualInspection' and iv.schedulable = cb) as visualInspectionSummary, "
            + "          (select max(iv.addTS) from InspectionValue  iv where iv.inspectionRule.inspectionCategory.apk = 'castingPreparation' and iv.schedulable = cb) as castingPreparationTS, "
            + "          (select max(iv.result) from InspectionValue iv where iv.inspectionRule.inspectionCategory.apk = 'castingPreparation' and iv.schedulable = cb) as castingPreparationSummary "
            + "     FROM CastingBatch cb ";

    private final static String CHARGE_WHERE =
              "    WHERE cb.charge LIKE :charge "
            + " ORDER BY cb.objid";
    private final static String TIME_RANGE_WHERE =
              "    WHERE cb.successTS >= :fromDateTime "
            + "      AND cb.successTS <= :toDateTime "
            + " ORDER BY cb.objid";
    //@formatter:on

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public List<ProcessDocuDTO> loadBySearchType( SearchType searchType, Map<String, Object> searchValues )
    {
        final String sql;
        if ( searchType == SearchType.CHARGE )
        {
            sql = QUERY + CHARGE_WHERE;
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
        final org.hibernate.query.Query<ProcessDocuDTO> query = entityManager.unwrap( Session.class ).createQuery( sql );
        for ( String parameterKey : searchValues.keySet() )
        {
            query.setParameter( parameterKey, searchValues.get( parameterKey ) );
        }
        query.setResultTransformer( new AliasToBeanResultTransformer( ProcessDocuDTO.class ) );
        query.setMaxResults( 5000 );

        return query.list();
    }
}
