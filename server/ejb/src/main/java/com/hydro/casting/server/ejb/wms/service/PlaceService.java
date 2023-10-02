package com.hydro.casting.server.ejb.wms.service;

import com.hydro.casting.server.model.mat.Material;
import com.hydro.casting.server.model.wms.HandlingUnit;
import com.hydro.casting.server.model.wms.Place;
import com.hydro.casting.server.model.wms.dao.HandlingUnitHome;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class PlaceService
{
    @EJB
    private HandlingUnitHome handlingUnitHome;

    public void assignPlace( Material material, Place place )
    {
        HandlingUnit handlingUnit = null;
        if ( place.numberOfHandlingUnits() <= 0 )
        {
            handlingUnit = new HandlingUnit();
            handlingUnit.setName( place.getName() );
            handlingUnit.setPlace( place );
            handlingUnitHome.persist( handlingUnit );
        }
        else
        {
            handlingUnit = place.getHandlingUnits().iterator().next();
        }
        material.setHandlingUnit( handlingUnit );
    }

    public void unassignPlace( Material material )
    {
        material.setHandlingUnit( null );
    }
}
