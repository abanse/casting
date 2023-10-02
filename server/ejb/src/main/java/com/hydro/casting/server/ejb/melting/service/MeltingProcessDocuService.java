package com.hydro.casting.server.ejb.melting.service;

import com.hydro.casting.server.contract.dto.MeltingProcessDocuDTO;
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
public class MeltingProcessDocuService
{
    //@formatter:off
    private final static String QUERY =
            "   SELECT mb.objid as id, "
                    + "          mb.charge as charge, "
                    + "          mb.alloyName as alloy, "
                    + "          mb.inProgressTS as inProgressTS, "
                    + "          mb.successTS as successTS "
                    + "     FROM MeltingBatch mb ";

    private final static String CHARGE_WHERE =
            "    WHERE mb.charge LIKE :charge "
                    + " ORDER BY mb.objid";
    private final static String TIME_RANGE_WHERE =
            "    WHERE mb.successTS >= :fromDateTime "
                    + "      AND mb.successTS <= :toDateTime "
                    + " ORDER BY mb.objid";
    //@formatter:on
    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public List<MeltingProcessDocuDTO> loadBySearchType( SearchType searchType, Map<String, Object> searchValues )
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
        final org.hibernate.query.Query<MeltingProcessDocuDTO> query = entityManager.unwrap( Session.class ).createQuery( sql );
        for ( String parameterKey : searchValues.keySet() )
        {
            query.setParameter( parameterKey, searchValues.get( parameterKey ) );
        }
        query.setResultTransformer( new AliasToBeanResultTransformer( MeltingProcessDocuDTO.class ) );
        query.setMaxResults( 5000 );

        return query.list();
    }
}
