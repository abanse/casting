package com.hydro.casting.gui.model.propagate;

import com.hydro.casting.common.Casting;
import com.hydro.casting.gui.model.Analysis;
import com.hydro.casting.gui.model.CastingClientModel;
import com.hydro.casting.gui.model.Material;
import com.hydro.casting.gui.model.MaterialGroup;
import com.hydro.casting.server.contract.dto.CrucibleMaterialDTO;
import com.hydro.casting.server.contract.dto.MaterialAnalysisElementDTO;
import com.hydro.casting.server.contract.dto.MaterialDTO;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
public class MaterialPropagator<M extends MaterialDTO> implements Callback<Collection<M>, Object>
{
    private CastingClientModel model;
    private String materialGroup;

    public MaterialPropagator( CastingClientModel model, String materialGroup )
    {
        this.model = model;
        this.materialGroup = materialGroup;
    }

    @Override
    public Void call( Collection<M> param )
    {
        final MaterialGroup group = model.getEntity( MaterialGroup.class, materialGroup );
        final List<String> allMaterialNames = new ArrayList<>();
        param.stream().forEach( m -> {
            if ( m.getTags() == null || !m.getTags().contains( Casting.MATERIAL_TYPE_TAGS.CHARGING_MATERIAL ) )
            {
                return;
            }
            if ( m.getPlace() == null || !Objects.equals( m.getPlace(), Casting.PLACE.STOCKS ) )
            {
                return;
            }
            Material material = group.findMaterial( m.getName() );
            if ( material == null )
            {
                material = new Material();
                material.setApk( m.getName() );
                material.setObjid( m.getId() );
                group.addMaterial( material );
            }
            material.setWeight( m.getWeight() );
            material.setName( m.getMaterialTypeDescription() );
            material.setDeliveryCharge( m.getDeliveryCharge() );
            material.setGenerationSuccessTS( m.getGenerationSuccessTS() );
            replicateAnalysis( material, m.getMaterialAnalysisElements() );

            allMaterialNames.add( m.getName() );
        } );
        group.getMaterials().removeIf( material -> !allMaterialNames.contains( material.getApk() ) );
        return null;
    }

    private void replicateAnalysis( Material material, List<MaterialAnalysisElementDTO> elements )
    {
        Analysis analysis = material.getAnalysis();
        if ( analysis == null )
        {
            analysis = new Analysis();
            material.setAnalysis( analysis );
        }
        analysis.setName( material.getApk() );
        final Analysis finalAnalysis = analysis;
        final List<String> allElementNames = new ArrayList<>();
        elements.stream().forEach( materialAnalysisElementDTO -> {
            finalAnalysis.setCompositionElementValue( materialAnalysisElementDTO.getName(), materialAnalysisElementDTO.getValue() );
            allElementNames.add( materialAnalysisElementDTO.getName() );
        } );
        finalAnalysis.getCompositionElements().removeIf( compositionElement -> !allElementNames.contains( compositionElement.getName() ) );
    }
}
