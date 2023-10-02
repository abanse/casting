package com.hydro.casting.server.model.mat;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue( "sump_material" )
public class SumpMaterial extends Material
{
    @Override
    public void assignChild( Material child )
    {
        super.assignChild( child );
        if ( !( child instanceof SumpMaterial ) )
        {
            return;
        }
        final SumpMaterial sumpMaterial = (SumpMaterial) child;
    }

}