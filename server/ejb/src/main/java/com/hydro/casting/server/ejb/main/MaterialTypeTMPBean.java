package com.hydro.casting.server.ejb.main;

import com.hydro.casting.server.contract.dto.MaterialTypeDTO;
import com.hydro.casting.server.contract.main.MaterialTypeTMP;
import com.hydro.casting.server.ejb.main.service.MaterialTypeService;
import com.hydro.casting.server.model.mat.MaterialType;
import com.hydro.casting.server.model.mat.dao.MaterialTypeHome;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.core.common.exception.BusinessObjectNotFoundException;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Stateless
public class MaterialTypeTMPBean implements MaterialTypeTMP
{
    private final static Logger log = LoggerFactory.getLogger( MaterialTypeTMPBean.class );

    @EJB
    private MaterialTypeService meltingKTService;

    @EJB
    private MaterialTypeHome materialTypeHome;

    @Override
    public List<MaterialTypeDTO> list( Map<String, Object> context )
    {
        if ( context != null && context.containsKey( "category" ) )
        {
            return meltingKTService.load( (String) context.get( "category" ) );
        }
        return meltingKTService.load();
    }

    @Override
    public MaterialTypeDTO update( Map<String, Object> context, MaterialTypeDTO viewDTO, String attribute, Object oldValue, Object newValue ) throws BusinessException
    {
        log.info( "set value " + attribute + " " + newValue );

        final MaterialType meltingKT = materialTypeHome.findById( viewDTO.getId() );

        try
        {
            MethodUtils.invokeMethod( meltingKT, "set" + attribute.substring( 0, 1 ).toUpperCase() + attribute.substring( 1 ), newValue );
        }
        catch ( Exception e )
        {
            log.error( "error setting attribute " + attribute, e );
            throw new BusinessException( "Allgemeiner Fehler", e );
        }

        return meltingKTService.load( meltingKT );
    }

    @Override
    public void delete( Map<String, Object> context, List<MaterialTypeDTO> viewDTOs ) throws BusinessException
    {
        final List<Long> removedIds = new ArrayList<>();
        for ( MaterialTypeDTO viewDTO : viewDTOs )
        {
            final MaterialType materialType = materialTypeHome.findById( viewDTO.getId() );

            if ( materialType == null )
            {
                throw new BusinessObjectNotFoundException( "MaterialType", viewDTO.getId() );
            }

            materialTypeHome.remove( materialType );
            removedIds.add( viewDTO.getId() );
        }
    }

    @Override
    public MaterialTypeDTO create( Map<String, Object> context )
    {
        final MaterialType meltingKT = new MaterialType();
        materialTypeHome.persist( meltingKT );

        return meltingKTService.load( meltingKT );
    }

    @Override
    public List<MaterialTypeDTO> copy( Map<String, Object> context, List<MaterialTypeDTO> sourceViewDTOs )
    {
        List<MaterialTypeDTO> targetViewDTOs = new ArrayList<>();
        final List<MaterialType> targetMeltingKTs = new ArrayList<>();
        for ( MaterialTypeDTO sourceViewDTO : sourceViewDTOs )
        {
            final MaterialType source = materialTypeHome.findById( sourceViewDTO.getId() );

            final MaterialType copy = materialTypeHome.detach( source );
            materialTypeHome.persist( copy );

            targetViewDTOs.add( meltingKTService.load( copy ) );
            targetMeltingKTs.add( copy );
        }

        return targetViewDTOs;
    }
}
