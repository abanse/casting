package com.hydro.casting.server.ejb.prod.service;

import com.hydro.casting.server.contract.dto.ProductionLogDTO;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ProductionLogService
{
    //@formatter:off
    private final static String QUERY =
              "   SELECT pl.objid as objid, "
            + "          pl.eventTS as eventTS, "
            + "          pl.machineApk as machineApk, "
            + "          pl.refName as refName, "
            + "          pl.userName as userName, "
            + "          pl.message as message "
            + "     FROM ProductionLog pl ";

    private final static String CURRENT_WHERE =
              "    WHERE pl.machineApk = :machineApk "
            + " ORDER BY pl.eventTS DESC";
    //@formatter:on

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public List<ProductionLogDTO> loadCurrentProductionLog( String machineApk )
    {
        final String sql = QUERY + CURRENT_WHERE;

        @SuppressWarnings( "unchecked" )
        final org.hibernate.query.Query<ProductionLogDTO> query = entityManager.unwrap( Session.class ).createQuery( sql );
        query.setParameter( "machineApk", machineApk );
        query.setResultTransformer( new AliasToBeanResultTransformer( ProductionLogDTO.class ) );
        query.setMaxResults( 500 );

        return query.list();
    }
}
