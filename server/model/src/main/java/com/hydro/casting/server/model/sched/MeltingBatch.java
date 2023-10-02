package com.hydro.casting.server.model.sched;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;

@Entity
@DiscriminatorValue( "melting_batch" )
@NamedQuery( name = "meltingBatch.findActiveForMelter", query = "select mb from MeltingBatch mb where (mb.executionState = 290 or mb.executionState = 300) and mb.executingMachine.apk = :melterApk" )
@NamedQuery( name = "meltingBatch.findForPeriod", query = "select mb from MeltingBatch mb where mb.executingMachine.apk in :machines and ( ( :fromTS >= mb.inProgressTS and :fromTS <= mb.successTS ) or ( :toTS >= mb.inProgressTS and :toTS <= mb.successTS ) or ( mb.inProgressTS <= :toTS and mb.successTS >= :fromTS ) or ( mb.inProgressTS <= :toTS and mb.successTS is null ) ) " )
public class MeltingBatch extends Batch
{
    @Column( name = "charge", length = 20 )
    private String charge;
    @Column( name = "alloy_name", length = 10 )
    private String alloyName;

    public String getCharge()
    {
        return charge;
    }

    public void setCharge( String charge )
    {
        this.charge = charge;
    }

    public String getAlloyName()
    {
        return alloyName;
    }

    public void setAlloyName( String alloyName )
    {
        this.alloyName = alloyName;
    }
}
