package com.hydro.casting.server.ejb.main.service;

import com.hydro.casting.server.contract.dto.MaterialTypeDTO;
import com.hydro.casting.server.model.mat.Alloy;
import com.hydro.casting.server.model.mat.MaterialType;
import com.hydro.casting.server.model.mat.dao.MaterialTypeHome;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
@Stateless
public class MaterialTypeService
{
    //@formatter:off
    private final static String QUERY =
              "SELECT mt.objid as id, "
            + "       mt.apk as apk, "
            + "       mt.amount as amount, "
            + "       mt.category as category, "
            + "       mt.description as description, "
            + "       mt.height as height, "
            + "       mt.length as length, "
            + "       mt.quality as quality, "
            + "       mt.qualityCode as qualityCode, "
            + "       mt.tags as tags, "
            + "       mt.width as width, "
            + "       a.name as alloyName "
            + "  FROM MaterialType mt left outer join mt.alloy a";
    private final static String SELECT_CATEGORY_WHERE =
              " WHERE mt.category = :category "
            + " ORDER BY mt.apk";
    private final static String SELECT_ALL_WHERE =
              " ORDER BY mt.apk";
    private final static String SELECT_WHERE =
            "    WHERE mt in (:materialTypes) "
          + " ORDER BY mt.apk";
    //@formatter:on

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    @EJB
    private MaterialTypeHome materialTypeHome;

    public List<MaterialTypeDTO> load()
    {
        return load( (String) null );
    }

    public List<MaterialTypeDTO> load( String category )
    {
        final String sql;
        if ( category != null )
        {
            sql = QUERY + SELECT_CATEGORY_WHERE;
        }
        else
        {
            sql = QUERY + SELECT_ALL_WHERE;
        }

        final Session session = entityManager.unwrap( Session.class );
        final org.hibernate.query.Query<MaterialTypeDTO> query = session.createQuery( sql );
        query.setResultTransformer( new AliasToBeanResultTransformer( MaterialTypeDTO.class ) );
        if ( category != null )
        {
            query.setParameter( "category", category );
        }

        return query.list();
    }

    public MaterialTypeDTO load( MaterialType entity )
    {
        return load( Collections.singletonList( entity ) ).get( 0 );
    }

    public List<MaterialTypeDTO> load( Collection<MaterialType> entities )
    {
        final String sql = QUERY + SELECT_WHERE;
        final Session session = entityManager.unwrap( Session.class );
        final org.hibernate.query.Query<MaterialTypeDTO> query = session.createQuery( sql );
        query.setParameter( "materialTypes", entities );
        query.setResultTransformer( new AliasToBeanResultTransformer( MaterialTypeDTO.class ) );

        return query.list();
    }

    public MaterialType findGWBMaterialType( MaterialType rohMaterialType, int amount )
    {
        // Suche passenden GWB-Barren
        final Alloy alloy = rohMaterialType.getAlloy();
        final double height = rohMaterialType.getHeight();
        final String quality = rohMaterialType.getQuality();
        final String qualityCode = rohMaterialType.getQualityCode();
        final double width = rohMaterialType.getWidth();
        final String category = "GWB";
        final double length = ( rohMaterialType.getLength() - 500 ) / amount;

        final List<MaterialType> gwbMaterialTypes = materialTypeHome.findByCategoryAttributes( category, quality, qualityCode, height, width, length, alloy.getName() );
        if ( gwbMaterialTypes.isEmpty() )
        {
            return null;
        }
        return gwbMaterialTypes.get( 0 );
    }
}
