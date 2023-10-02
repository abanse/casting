package com.hydro.casting.server.model.sched;

import com.hydro.casting.server.model.res.MeltingFurnace;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue( "casting_batch" )
@NamedQuery( name = "castingBatch.findPrev", query = "select cb from CastingBatch cb where cb.executingMachine = :executingMachine and cb.executingSequenceIndex < :executingSequenceIndex order by cb.executingSequenceIndex desc" )
@NamedQuery( name = "castingBatch.exist", query = "select max(cb.objid) from CastingBatch cb where cb.charge like :charge and cb.executionState = :executionState and cb.plannedTS > :plannedTS" )
@NamedQuery( name = "castingBatch.existCharge", query = "select max(cb.objid) from CastingBatch cb where cb.charge = :charge" )
@NamedQuery( name = "castingBatch.findInProgressForMeltingFurnace", query = "select cb from CastingBatch cb where cb.executionState = 300 and cb.meltingFurnace.apk = :meltingFurnaceApk" )
@NamedQuery( name = "castingBatch.findActiveForMeltingFurnace", query = "select cb from CastingBatch cb where (cb.executionState = 300 or cb.executionState = 310) and cb.meltingFurnace.apk = :meltingFurnaceApk" )
@NamedQuery( name = "castingBatch.findActiveForCaster", query = "select cb from CastingBatch cb where (cb.executionState = 310 or cb.executionState = 320) and cb.executingMachine.apk = :casterApk" )
@NamedQuery( name = "castingBatch.findLastCastingBatches", query = "select cb from CastingBatch cb where cb.executionState = 400 and cb.executingMachine.apk = :casterApk and cb.releaseTS > :from order by cb.releaseTS" )
@NamedQuery( name = "castingBatch.findLastCastingBatchesForFurnace", query = "select cb from CastingBatch cb where (cb.executionState = 400 or cb.executionState = 320) and cb.meltingFurnace.apk = :furnaceApk and cb.unloadingTS > :from order by cb.unloadingTS" )
@NamedQuery( name = "castingBatch.findByMachineAndState", query = "select cb.objid from CastingBatch cb where cb.executionState = :executionState and cb.executingMachine.apk = :executingResource" )
@NamedQuery( name = "castingBatch.findMaxCastingSequence", query = "select max(cb.castingSequence) from CastingBatch cb where cb.executingMachine = :executingMachine" )
@NamedQuery( name = "castingBatch.findActiveForMeltingFurnaces", query = "select cb from CastingBatch cb where (cb.executionState = 300 or cb.executionState = 310)" )
@NamedQuery( name = "castingBatch.findNextInSchedule", query = "select cb from CastingBatch cb where cb.executingMachine.apk = :casterApk and cb.executingSequenceIndex > :refExecutingSequenceIndex and cb.executionState = :executionState order by cb.executingSequenceIndex" )
@NamedQuery( name = "castingBatch.findByChargeWithoutYear", query = "select cb from CastingBatch cb where cb.charge like :charge and cb.plannedTS > :timeHorizont" )
@NamedQuery( name = "castingBatch.findByProductionOrder", query = "select cb from Operation op join CastingBatch cb on op.batch = cb where op.demand.workStep.productionOrder.apk = :productionOrderApk" )
@NamedQuery( name = "castingBatch.findByCharge", query = "select cb from CastingBatch cb where cb.charge = :charge" )
@NamedQuery( name = "castingBatch.findFirstCastingSequence", query = "select min(cb.executingSequenceIndex) from CastingBatch cb where cb.executingMachine = :machine and cb.executionState = :executionState" )
public class CastingBatch extends Batch
{
    @Column( name = "charge", length = 20 )
    private String charge;
    @Column( name = "holding_end" )
    private LocalDateTime holdingEnd;
    @Column( name = "waiting_end" )
    private LocalDateTime waitingEnd;
    @Column( name = "treatment_end" )
    private LocalDateTime treatmentEnd;
    @Column( name = "head_length_bonus" )
    private int headLengthBonus;
    @Column( name = "feed_length_bonus" )
    private int feedLengthBonus;
    @Column( name = "total_weight" )
    private int totalWeight;
    @Column( name = "casting_length" )
    private int castingLength;
    @Column( name = "analysis_creation_time" )
    private LocalDateTime analysisCreationTime;
    @Column( name = "lw_alloy_full_name", length = 20 )
    private String lwAlloyFullName;
    @Column( name = "release_ts" )
    private LocalDateTime releaseTS;
    @Column( name = "release_responsible", length = 20 )
    private String releaseResponsible;
    @Column( name = "release_shift", length = 20 )
    private String releaseShift;
    @Column( name = "planned_length" )
    private int plannedLength;
    @Column( name = "planned_weight" )
    private int plannedWeight;
    @Column( name = "process_order" )
    private String processOrder;
    @Column( name = "percentage_s1" )
    private Double percentageS1;
    @Column( name = "percentage_s2" )
    private Double percentageS2;
    @Column( name = "percentage_s3" )
    private Double percentageS3;
    @Column( name = "percentage_el" )
    private Double percentageEL;
    @Column( name = "percentage_ra" )
    private Double percentageRA;
    @Column( name = "planned_melting_duration" )
    private Integer plannedMeltingDuration;
    @Column( name = "planned_casting_duration" )
    private Integer plannedCastingDuration;
    @Column( name = "casting_sequence" )
    private Long castingSequence;
    @Column( name = "planned_success_ts" )
    private LocalDateTime plannedSuccessTs;
    @Column( name = "preparing_ts" )
    private LocalDateTime preparingTS;
    @Column( name = "charging_ts" )
    private LocalDateTime chargingTS;
    @Column( name = "treating_ts" )
    private LocalDateTime treatingTS;
    @Column( name = "skimming_ts" )
    private LocalDateTime skimmingTS;
    @Column( name = "resting_ts" )
    private LocalDateTime restingTS;
    @Column( name = "casting_ts" )
    private LocalDateTime castingTS;
    @Column( name = "unloading_ts" )
    private LocalDateTime unloadingTS;

    @ManyToOne
    @JoinColumn( name = "melting_furnace_oid" )
    private MeltingFurnace meltingFurnace;

    public String getCharge()
    {
        return charge;
    }

    public void setCharge( String newCharge )
    {
        this.charge = newCharge;
    }

    public LocalDateTime getHoldingEnd()
    {
        return holdingEnd;
    }

    public void setHoldingEnd( LocalDateTime newHoldingEnd )
    {
        this.holdingEnd = newHoldingEnd;
    }

    public LocalDateTime getWaitingEnd()
    {
        return waitingEnd;
    }

    public void setWaitingEnd( LocalDateTime newWaitingEnd )
    {
        this.waitingEnd = newWaitingEnd;
    }

    public LocalDateTime getTreatmentEnd()
    {
        return treatmentEnd;
    }

    public void setTreatmentEnd( LocalDateTime newTreatmentEnd )
    {
        this.treatmentEnd = newTreatmentEnd;
    }

    public int getHeadLengthBonus()
    {
        return headLengthBonus;
    }

    public void setHeadLengthBonus( int newHeadLengthBonus )
    {
        this.headLengthBonus = newHeadLengthBonus;
    }

    public int getFeedLengthBonus()
    {
        return feedLengthBonus;
    }

    public void setFeedLengthBonus( int newFeedLengthBonus )
    {
        this.feedLengthBonus = newFeedLengthBonus;
    }

    public int getTotalWeight()
    {
        return totalWeight;
    }

    public void setTotalWeight( int newTotalWeight )
    {
        this.totalWeight = newTotalWeight;
    }

    public int getCastingLength()
    {
        return castingLength;
    }

    public void setCastingLength( int newCastingLength )
    {
        this.castingLength = newCastingLength;
    }

    public LocalDateTime getAnalysisCreationTime()
    {
        return analysisCreationTime;
    }

    public void setAnalysisCreationTime( LocalDateTime newAnalysisCreationTime )
    {
        this.analysisCreationTime = newAnalysisCreationTime;
    }

    public String getLwAlloyFullName()
    {
        return lwAlloyFullName;
    }

    public void setLwAlloyFullName( String newLwAlloyFullName )
    {
        this.lwAlloyFullName = newLwAlloyFullName;
    }

    public LocalDateTime getReleaseTS()
    {
        return releaseTS;
    }

    public void setReleaseTS( LocalDateTime newReleaseDate )
    {
        this.releaseTS = newReleaseDate;
    }

    public String getReleaseResponsible()
    {
        return releaseResponsible;
    }

    public void setReleaseResponsible( String newReleaseResponsible )
    {
        this.releaseResponsible = newReleaseResponsible;
    }

    public String getReleaseShift()
    {
        return releaseShift;
    }

    public void setReleaseShift( String newReleaseShift )
    {
        this.releaseShift = newReleaseShift;
    }

    public int getPlannedLength()
    {
        return plannedLength;
    }

    public void setPlannedLength( int newPlannedLength )
    {
        this.plannedLength = newPlannedLength;
    }

    public int getPlannedWeight()
    {
        return plannedWeight;
    }

    public void setPlannedWeight( int plannedWeight )
    {
        this.plannedWeight = plannedWeight;
    }

    public String getProcessOrder()
    {
        return processOrder;
    }

    public void setProcessOrder( String processOrder )
    {
        this.processOrder = processOrder;
    }

    public Double getPercentageS1()
    {
        return percentageS1;
    }

    public void setPercentageS1( Double percentageS1 )
    {
        this.percentageS1 = percentageS1;
    }

    public Double getPercentageS2()
    {
        return percentageS2;
    }

    public void setPercentageS2( Double percentageS2 )
    {
        this.percentageS2 = percentageS2;
    }

    public Double getPercentageS3()
    {
        return percentageS3;
    }

    public void setPercentageS3( Double percentageS3 )
    {
        this.percentageS3 = percentageS3;
    }

    public Double getPercentageEL()
    {
        return percentageEL;
    }

    public void setPercentageEL( Double percentageEL )
    {
        this.percentageEL = percentageEL;
    }

    public Double getPercentageRA()
    {
        return percentageRA;
    }

    public void setPercentageRA( Double percentageRA )
    {
        this.percentageRA = percentageRA;
    }

    public Integer getPlannedMeltingDuration()
    {
        return plannedMeltingDuration;
    }

    public void setPlannedMeltingDuration( Integer plannedMeltingDuration )
    {
        this.plannedMeltingDuration = plannedMeltingDuration;
    }

    public Integer getPlannedCastingDuration()
    {
        return plannedCastingDuration;
    }

    public void setPlannedCastingDuration( Integer plannedCastingDuration )
    {
        this.plannedCastingDuration = plannedCastingDuration;
    }

    public Long getCastingSequence()
    {
        return castingSequence;
    }

    public void setCastingSequence( Long castingSequence )
    {
        this.castingSequence = castingSequence;
    }

    public LocalDateTime getPlannedSuccessTs()
    {
        return plannedSuccessTs;
    }

    public void setPlannedSuccessTs( LocalDateTime plannedSuccessTs )
    {
        this.plannedSuccessTs = plannedSuccessTs;
    }

    public LocalDateTime getPreparingTS()
    {
        return preparingTS;
    }

    public void setPreparingTS( LocalDateTime preparingTS )
    {
        this.preparingTS = preparingTS;
    }

    public LocalDateTime getChargingTS()
    {
        return chargingTS;
    }

    public void setChargingTS( LocalDateTime chargingTS )
    {
        this.chargingTS = chargingTS;
    }

    public LocalDateTime getTreatingTS()
    {
        return treatingTS;
    }

    public void setTreatingTS( LocalDateTime treatingTS )
    {
        this.treatingTS = treatingTS;
    }

    public LocalDateTime getSkimmingTS()
    {
        return skimmingTS;
    }

    public void setSkimmingTS( LocalDateTime skimmingTS )
    {
        this.skimmingTS = skimmingTS;
    }

    public LocalDateTime getRestingTS()
    {
        return restingTS;
    }

    public void setRestingTS( LocalDateTime restingTS )
    {
        this.restingTS = restingTS;
    }

    public LocalDateTime getCastingTS()
    {
        return castingTS;
    }

    public void setCastingTS( LocalDateTime castingTS )
    {
        this.castingTS = castingTS;
    }

    public LocalDateTime getUnloadingTS()
    {
        return unloadingTS;
    }

    public void setUnloadingTS( LocalDateTime unloadingTS )
    {
        this.unloadingTS = unloadingTS;
    }

    public MeltingFurnace getMeltingFurnace()
    {
        return meltingFurnace;
    }

    public void setMeltingFurnace( MeltingFurnace meltingFurnace )
    {
        this.meltingFurnace = meltingFurnace;
    }

    @Override
    public void removeAllAssociations()
    {
        super.removeAllAssociations();
        setMeltingFurnace( null );
    }
}