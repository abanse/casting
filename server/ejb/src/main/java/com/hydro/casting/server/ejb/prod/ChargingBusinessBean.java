package com.hydro.casting.server.ejb.prod;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.dto.MaterialDTO;
import com.hydro.casting.server.contract.prod.ChargingBusiness;
import com.hydro.casting.server.ejb.analysis.service.AnalysisService;
import com.hydro.casting.server.ejb.main.service.MaterialService;
import com.hydro.casting.server.model.mat.Material;
import com.hydro.casting.server.model.mat.MaterialType;
import com.hydro.casting.server.model.mat.dao.MaterialHome;
import com.hydro.casting.server.model.sched.CastingBatch;
import com.hydro.casting.server.model.sched.dao.CastingBatchHome;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.core.common.exception.BusinessMessageException;
import com.hydro.eai.kafka.contract.KafkaSender;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class ChargingBusinessBean implements ChargingBusiness
{
    @EJB
    private KafkaSender kafkaSender;

    @EJB
    private MaterialHome materialHome;

    @EJB
    private CastingBatchHome castingBatchHome;
    @EJB
    private MaterialService materialService;
    @EJB
    private AnalysisService analysisService;

    @Override
    public void saveChargingMaterials( String user, long castingBatchOID, List<MaterialDTO> furnaceContentMaterials, List<MaterialDTO> chargingMaterials ) throws BusinessException
    {
        final CastingBatch castingBatch = castingBatchHome.findById( castingBatchOID );

        final List<Material> changedMaterials = new ArrayList<>();
        final List<Long> deletedMaterialOIDs = new ArrayList<>();
        chargingMaterials.forEach( materialDTO -> {
            final Material material = materialHome.findById( materialDTO.getId() );
            // löschen
            if ( materialDTO.getWeight() <= 0 )
            {
                // Neu hinzugefügtes wurde wieder gelöscht, nichts zu tun
                if ( !( materialDTO.getConsumptionState() == Casting.SCHEDULABLE_STATE.UNPLANNED && materialDTO.getGenerationState() == Casting.SCHEDULABLE_STATE.UNPLANNED ) )
                {
                    materialService.deleteMaterial( material );
                    deletedMaterialOIDs.add( material.getObjid() );
                }
            }
            // neu hinzugefügte Materialien
            else if ( materialDTO.getConsumptionState() == Casting.SCHEDULABLE_STATE.UNPLANNED && materialDTO.getGenerationState() == Casting.SCHEDULABLE_STATE.UNPLANNED )
            {
                final Material plannedMaterial = materialService.unloadMaterial( material, castingBatch.getCharge() + "-" + material.getName(), materialDTO.getWeight() );
                materialService.setGenerationState( plannedMaterial, Casting.SCHEDULABLE_STATE.PLANNED, LocalDateTime.now() );
                materialService.setConsumptionState( plannedMaterial, Casting.SCHEDULABLE_STATE.PLANNED, LocalDateTime.now() );
                plannedMaterial.setConsumingOperation( castingBatch );

                analysisService.replicateMaterialAnalysis( material, materialDTO.getMaterialAnalysisElements() );
                changedMaterials.add( plannedMaterial );
            }
            // bereits hinzugefügte
            else
            {
                boolean changed = analysisService.replicateMaterialAnalysis( material, materialDTO.getMaterialAnalysisElements() );
                if ( materialDTO.getWeight() != material.getWeight() )
                {
                    material.setWeight( materialDTO.getWeight() );
                    changed = true;
                }
                if ( changed )
                {
                    changedMaterials.add( material );
                }
            }
        } );

        // Update Analysen
        furnaceContentMaterials.forEach( materialDTO -> {
            final Material material = materialHome.findById( materialDTO.getId() );
            boolean changed = analysisService.replicateMaterialAnalysis( material, materialDTO.getMaterialAnalysisElements() );
            if ( changed )
            {
                changedMaterials.add( material );
            }
        } );

        materialService.replicateCache( changedMaterials );
        if ( !deletedMaterialOIDs.isEmpty() )
        {
            materialService.removeFromCache( deletedMaterialOIDs );
        }
    }

    /*
        {
          "AlCr80_Tabletten_ist": 0.0,
          "AlCr80_Tabletten_soll": 80.0,
          "ALMg50_ist": 0.0,
          "ALMg50_soll": 0.0,
          "AlMn90_Minitablets_ist": 0.0,
          "AlMn90_Minitablets_soll": 99.0,
          "AlNi20_Waffelplatten_ist": 0.0,
          "AlNi20_Waffelplatten_soll": 20.0,
          "AlTi80_Minitablets_ist": 0.0,
          "AlTi80_Minitablets_soll": 80.0,
          "Charge_AlCr80Tabletten": "ACL80",
          "Charge_ALMg50": "ALmg50",
          "Charge_AlMn90_Minitablets": "ALMN90Mini",
          "Charge_AlNi20_Waffelplatten": "AlNi20Waff",
          "Charge_AlTi80_Minitablets": "ALTI80Mini",
          "Charge_Cu99,5Granulat": "CU995Granu",
          "Charge_Fe90Pulver": "FE90Pulver",
          "Charge_Fe95%": "Fe95%",
          "Charge_Huettenmagnesium99,8": "HuettenMag99",
          "Charge_Zinkmin99,995%": "ZinkMin99",
          "Chargennummer": 534254,
          "ChargeSilizium98,5": "Silizium98",
          "Cu99,5_Granulat_ist": 0.0,
          "Cu99,5_Granulat_soll": 25.0,
          "DatenVorhanden": false,
          "Fe90Pulver_ist": 0.0,
          "Fe90Pulver_soll": 90.0,
          "Fe95%_ist": 0.0,
          "Fe95%_soll": 0.0,
          "Hüttenmagnesium_99,8_ist": 1345.0,
          "Hüttenmagnesium_99,8_soll": 80.0,
          "Silizium98,5_ist": 0.0,
          "Silizium98,5_soll": 20.0,
          "Zinkmin99,995%_ist": 1345.0,
          "Zinkmin99,995%_soll": 90.0
        }

        MES2GATA51

        MES2GATA52

        MES2GATA61

        MES2GATA62

        MES2GATA71

        MES2GATA72

        MES2GATA81

        MES2GATA82

     */

    @Override
    public void sendChargingSpecification( String user, long castingBatchOID ) throws BusinessException
    {
        final CastingBatch castingBatch = castingBatchHome.findById( castingBatchOID );

        final List<Material> plannedMaterials = materialHome.findByConsumingOperation( castingBatchOID );

        final Map<ChargingStationSpecification, Double> targetMap = new HashMap<>();
        final Map<ChargingStationSpecification, String> deliveryChargeMap = new HashMap<>();
        for ( ChargingStationSpecification chargingStationSpecification : ChargingStationSpecification.values() )
        {
            for ( Material plannedMaterial : plannedMaterials )
            {
                final MaterialType materialType = plannedMaterial.getMaterialType();
                if ( materialType == null || materialType.getTags() == null )
                {
                    continue;
                }
                if ( materialType.getTags().contains( chargingStationSpecification.getMaterialTypeTag() ) )
                {
                    Double target = targetMap.get( chargingStationSpecification );
                    if ( target != null )
                    {
                        target = target.doubleValue() + plannedMaterial.getWeight();
                    }
                    else
                    {
                        target = plannedMaterial.getWeight();
                    }
                    targetMap.put( chargingStationSpecification, target );
                    // 000000000070075000-3022389 // 19
                    String deliveryCharge = null;
                    if ( plannedMaterial.getName() != null && plannedMaterial.getName().length() > 19 )
                    {
                        deliveryCharge = plannedMaterial.getName().substring( 19 );
                    }
                    if ( deliveryCharge != null )
                    {
                        deliveryChargeMap.put( chargingStationSpecification, deliveryCharge );
                    }
                }
            }
        }

        final StringWriter jsonString = new StringWriter();
        final JsonGenerator jsonGenerator = Json.createGenerator( jsonString );
        jsonGenerator.writeStartObject();
        jsonGenerator.write( "Chargennummer", castingBatch.getCharge().substring( 2 ) );
        jsonGenerator.write( "DatenVorhanden", true );
        for ( ChargingStationSpecification chargingStationSpecification : ChargingStationSpecification.values() )
        {
            jsonGenerator.write( chargingStationSpecification.getMessageName() + "_ist", 0. );
            final Double target = targetMap.get( chargingStationSpecification );
            if ( target != null )
            {
                jsonGenerator.write( chargingStationSpecification.getMessageName() + "_soll", target );
            }
            else
            {
                jsonGenerator.write( chargingStationSpecification.getMessageName() + "_soll", 0. );
            }
            final String deliveryCharge = deliveryChargeMap.get( chargingStationSpecification );
            if ( deliveryCharge != null )
            {
                jsonGenerator.write( "Charge_" + chargingStationSpecification.getMessageName(), deliveryCharge );
            }
            else
            {
                jsonGenerator.write( "Charge_" + chargingStationSpecification.getMessageName(), "" );
            }
        }
        jsonGenerator.writeEnd();
        jsonGenerator.close();

        final String furnaceApk;
        if ( castingBatch.getMeltingFurnace() != null )
        {
            furnaceApk = castingBatch.getMeltingFurnace().getApk();
        }
        else
        {
            throw new BusinessMessageException( "Charge " + castingBatch.getCharge().substring( 2 ) + " ist keinem Ofen zugeordnet" );
        }

        final String topicName = "MES2GATA" + furnaceApk;

        kafkaSender.sendMessage( topicName, jsonString.toString() );
    }

    //    @Override
    //    public void saveMaterialAnalysis( String user, MaterialDTO materialDTO, List<MaterialAnalysisElementDTO> newAnalysis ) throws BusinessException
    //    {
    //        final Material material = materialHome.findById( materialDTO.getId() );
    //        analysisService.updateMaterialAnalysis( material, newAnalysis );
    //        materialService.replicateCache( material );
    //    }
}
