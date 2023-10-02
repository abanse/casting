package com.hydro.casting.server.ejb.main.service;

import com.hydro.casting.server.contract.dto.WorkStepDTO;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class WorkStepService
{
    private final static Logger log = LoggerFactory.getLogger( WorkStepService.class );

    //@formatter:off
    private final static String QUERY =
              "   SELECT ws.objid as id, "
            + "          ws.apk as apk, "
            + "          ws.executionState as executionState, "
            + "          ws.workStepNumber as workStepNumber, "
            + "          ws.startTS as startTS, "
            + "          ws.endTS as endTS, "
            + "          ws.description as description, "
            + "          ws.workPlace as workPlace, "
            + "          ws.workPlaceDescription as workPlaceDescription, "
            + "          ws.productionOrder.apk as productionOrderApk, "
            + "          ws.plant.apk as plantApk "
            + "     FROM WorkStep ws ";

    private final static String PRODUCTION_ORDER_WHERE =
              "    WHERE ws.productionOrder.objid = :productionOrderOID "
            + " ORDER BY ws.apk ";
    //@formatter:on

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public List<WorkStepDTO> loadByProductionOrder( long productionOrderOID )
    {
        final String sql = QUERY + PRODUCTION_ORDER_WHERE;

        @SuppressWarnings( "unchecked" )
        final org.hibernate.query.Query<WorkStepDTO> query = entityManager.unwrap( Session.class ).createQuery( sql );
        query.setParameter( "productionOrderOID", productionOrderOID );
        query.setResultTransformer( new AliasToBeanResultTransformer( WorkStepDTO.class ) );
        query.setMaxResults( 5000 );

        return query.list();
    }
}
