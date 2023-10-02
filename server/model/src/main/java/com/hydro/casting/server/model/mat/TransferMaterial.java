package com.hydro.casting.server.model.mat;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue( "transfer_material" )
public class TransferMaterial extends Material
{
    @Override
    public void assignChild( Material child )
    {
        super.assignChild( child );
        if ( !( child instanceof TransferMaterial ) )
        {
            return;
        }
        final TransferMaterial transferMaterial = (TransferMaterial) child;
    }

}