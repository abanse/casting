package com.hydro.casting.server.model.mat;

import com.hydro.casting.common.Casting;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue( "stock_material" )
@NamedQuery( name = "stockMaterial.name", query = "select sm from StockMaterial sm where sm.name = :name" )
@NamedQuery( name = "stockMaterial.findNewestReplicationTS", query = "select max(sm.replicationTS) from StockMaterial sm where sm.materialType.category = '"
        + Casting.MATERIAL_TYPE_CATEGORY.STOCK_MATERIAL + "' and sm.consumingOperation is null and sm.consumptionState < " + Casting.SCHEDULABLE_STATE.SUCCESS )
@NamedQuery( name = "stockMaterial.findConsumedStockMaterials", query = "select sm from StockMaterial sm where sm.materialType.category = '" + Casting.MATERIAL_TYPE_CATEGORY.STOCK_MATERIAL
        + "' and sm.consumingOperation is null and (sm.replicationTS is null or sm.replicationTS < :timeHorizont) and sm.consumptionState < " + Casting.SCHEDULABLE_STATE.SUCCESS )
public class StockMaterial extends Material
{
    @Column( name = "delivery_charge", length = 100 )
    private String deliveryCharge;
    @Column( name = "replication_ts" )
    private LocalDateTime replicationTS;

    public String getDeliveryCharge()
    {
        return deliveryCharge;
    }

    public void setDeliveryCharge( String deliveryCharge )
    {
        this.deliveryCharge = deliveryCharge;
    }

    public LocalDateTime getReplicationTS()
    {
        return replicationTS;
    }

    public void setReplicationTS( LocalDateTime replicationTS )
    {
        this.replicationTS = replicationTS;
    }

    @Override
    public void assignChild( Material child )
    {
        super.assignChild( child );
        if ( !( child instanceof StockMaterial ) )
        {
            return;
        }
        final StockMaterial stockMaterial = (StockMaterial) child;
        stockMaterial.setDeliveryCharge( getDeliveryCharge() );
        // Nur bei Anlieferung relevant
        //stockMaterial.setReplicationTS( replicationTS );
    }

}