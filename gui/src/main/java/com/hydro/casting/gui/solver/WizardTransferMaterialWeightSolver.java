package com.hydro.casting.gui.solver;

import com.google.inject.Inject;
import com.hydro.casting.gui.model.*;
import com.hydro.casting.gui.model.common.ETransferMaterialType;
import com.hydro.casting.gui.solver.exception.NoSolutionFoundException;
import com.hydro.casting.gui.solver.impl.JacopWeightPercentSolver;
import com.hydro.casting.gui.solver.model.SolverVariable;
import com.hydro.casting.server.contract.dto.ChargeSpecificationDTO;
import com.hydro.casting.server.contract.dto.SpecificationElementDTO;
import com.hydro.core.gui.ClientModelManager;
import com.hydro.core.gui.model.ClientModel;

import java.util.*;

public class WizardTransferMaterialWeightSolver
{
    @Inject
    private ClientModelManager clientModelManager;

    private ISolver solver = null;

    private List<SolverVariable> solverVariables;

    public List<SolverVariable> solve( Transfer transfer, ChargeSpecificationDTO chargeSpecificationDTO ) throws NoSolutionFoundException
    {
        ClientModel castingModel = clientModelManager.getClientModel( CastingClientModel.ID );

        final List<TransferMaterial> transferMaterials = transfer.getTransferMaterials();

        List<TransferMaterial> targetMaterials = new ArrayList<>();

        // Füge alle Transfermaterialien hinzu
        for ( TransferMaterial transferMaterial : transferMaterials )
        {
            ETransferMaterialType type = ETransferMaterialType.findType( transferMaterial.getType() );
            if ( type == ETransferMaterialType.SPEC_MATERIAL || type == ETransferMaterialType.NONE_SPEC_MATERIAL || type == ETransferMaterialType.FILL_MATERIAL || type == ETransferMaterialType.FIXED )
            {
                continue;
            }
            targetMaterials.add( transferMaterial );
        }

        final List<SolverVariable> fixedTransferMaterials = new ArrayList<>();
        final TransferMaterial fillmentTransferMaterial = transfer.getFillmentTransferMaterial();
        Hashtable<Material, Set<TransferMaterial>> materialTransferMaterials = new Hashtable<>();
        double varWeights = 0;
        for ( TransferMaterial transferMaterial : transferMaterials )
        {
            if ( transferMaterial.getSource() != null && targetMaterials.contains( transferMaterial ) )
            {
                Material sourceMaterial = transferMaterial.getSource();
                Set<TransferMaterial> sourceMaterialTransferMaterials = materialTransferMaterials.get( sourceMaterial );
                if ( sourceMaterialTransferMaterials == null )
                {
                    sourceMaterialTransferMaterials = new HashSet<>();
                    materialTransferMaterials.put( sourceMaterial, sourceMaterialTransferMaterials );
                }
                sourceMaterialTransferMaterials.add( transferMaterial );
                varWeights = varWeights + transferMaterial.getWeight();
            }
            else
            {
                fixedTransferMaterials.add( new SolverVariable( transferMaterial ) );
            }
        }

        solverVariables = new ArrayList<>();
        Collection<Set<TransferMaterial>> materialTransferMaterialValues = materialTransferMaterials.values();

        for ( Set<TransferMaterial> materialTransferMaterialValue : materialTransferMaterialValues )
        {
            SolverVariable materialTransferSV = new SolverVariable( materialTransferMaterialValue );
            solverVariables.add( materialTransferSV );
        }

        Specification spec = transfer.getBatch().getSpecification();
        //        if ( transfer.isUseStandardSpec() )
        //        {
        //            spec = MeltingPlan.getInstance().getStandardSpecMainData().getSpecification();
        //        }
        // Problem CU-AG umgehen für den Solver indem CU-AG auf CU gesetzt wird
        //        spec = MeltingPlanUtil.correctSpecification( spec );

        List<SolverVariable> variableMaterials = findSpecInputMaterials( castingModel, chargeSpecificationDTO, spec, false );

        // Fügt alle Legierungsmaterialien hinzu die nicht bereits hinzugefügt
        // worden
        SolverVariable fillmentTransferMaterialSV = null;
        if ( fillmentTransferMaterial != null && fillmentTransferMaterial.getSource() != null )
        {
            fillmentTransferMaterialSV = new SolverVariable( fillmentTransferMaterial );
        }
        for ( SolverVariable solverVariable : variableMaterials )
        {
            if ( solverVariables.contains( solverVariable ) )
            {
                SolverVariable existingVarMaterial = solverVariables.get( solverVariables.indexOf( solverVariable ) );
                existingVarMaterial.setMinimize( true );
                existingVarMaterial.setMaximumValue( solverVariable.getMaximumValue() );
                continue;
            }
            if ( fillmentTransferMaterialSV != null && fillmentTransferMaterialSV.equals( solverVariable ) )
            {
                continue;
            }
            solverVariable.setMinimize( true );
            solverVariables.add( solverVariable );
        }

        solver = new JacopWeightPercentSolver();
        solver.solve( solverVariables, transfer, fixedTransferMaterials, fillmentTransferMaterialSV, varWeights, ISolver.SolverHint.FIND_TARGETS );
        //solver.solve( solverVariables, transfer, fixedTransferMaterials, fillmentTransferMaterialSV, varWeights, ISolver.SolverHint.FIND_WINDOW_MIN_TO_MIDDLE );

        solver = null;

        return solverVariables;
    }

    public List<SolverVariable> getResult()
    {
        return solverVariables;
    }

    public void cancel()
    {
        if ( solver != null )
        {
            solver.cancel();
        }
        solver = null;
    }

    private List<SolverVariable> findSpecInputMaterials( ClientModel castingModel, ChargeSpecificationDTO chargeSpecificationDTO, Specification spec, boolean withCu )
    {
        return findSpecInputMaterials( castingModel, chargeSpecificationDTO, spec, withCu, true );
    }

    private List<SolverVariable> findSpecInputMaterials( ClientModel castingModel, ChargeSpecificationDTO chargeSpecificationDTO, Specification spec, boolean withCu, boolean with100MaxMaterial )
    {
        List<SolverVariable> variableMaterials = new ArrayList<>();

        Composition maxComp = spec.getMax();
        List<CompositionElement> maxElements = maxComp.getCompositionElements();
        String maxElementName = null;
        for ( CompositionElement maxElement : maxElements )
        {
            if ( maxElement.getElementValue() >= 100.0 )
            {
                maxElementName = maxElement.getName();
                break;
            }
        }
        // Gibt es ein MAX=100 Element, dann Auflegierungmetall hinzufügen
        if ( maxElementName != null && with100MaxMaterial )
        {
            SolverVariable maxElementMaterial = findBestInputMaterial( castingModel, maxElementName, spec.getSpecId(), withCu );
            if ( maxElementMaterial != null )
            {
                variableMaterials.add( maxElementMaterial );
            }
        }
        // Jetzt alle Materialien hinzufügen die in Spec MIN vorkommen
        Composition minComp = spec.getMin();
        List<CompositionElement> minElements = minComp.getCompositionElements();
        for ( CompositionElement minElement : minElements )
        {
            if ( minElement.getElementValue() > 0.0 )
            {
                SolverVariable minElementMaterial = findBestInputMaterial( castingModel, minElement.getName(), spec.getSpecId(), withCu );
                if ( minElementMaterial != null )
                {
                    variableMaterials.add( minElementMaterial );
                }
            }
        }

        // Wenn gar nichts gefunden werden kann, immer CU hinzufügen
        if ( variableMaterials.isEmpty() )
        {
            SolverVariable emergencyMaterial = findBestInputMaterial( castingModel, "CU", null, true );
            variableMaterials.add( emergencyMaterial );
        }

        // Analysen ändern mit möglichen factor werten
        final Map<String, Double> elementFactors = new HashMap<>();
        if ( chargeSpecificationDTO != null )
        {
            if ( chargeSpecificationDTO != null )
            {
                // overwrite warning values
                final List<SpecificationElementDTO> elements = chargeSpecificationDTO.getElements();

                elements.stream().forEach( specificationElementDTO -> {
                    if ( specificationElementDTO.getElementFactor() != 1. )
                    {
                        elementFactors.put( specificationElementDTO.getName(), specificationElementDTO.getElementFactor() );
                    }
                } );
            }
        }

        if ( !elementFactors.isEmpty() )
        {
            final List<SolverVariable> changedVariableMaterials = new ArrayList<>();
            variableMaterials.stream().forEach( solverVariable -> {
                final Analysis currentAnalysis = solverVariable.getAnalysis();
                final Analysis newAnalysis = currentAnalysis.clone();
                newAnalysis.setName( solverVariable.getName() );
                newAnalysis.getCompositionElements().stream().forEach( compositionElement -> {
                    final Double newFactor = elementFactors.get( compositionElement.getName() );
                    if ( newFactor != null )
                    {
                        compositionElement.setElementFactor( newFactor );
                    }
                } );
                final SolverVariable changedSolverVariable = new SolverVariable( newAnalysis, solverVariable.getMaterial() );
                changedVariableMaterials.add( changedSolverVariable );
            } );
            return changedVariableMaterials;
        }
        else
        {
            return variableMaterials;
        }
    }

    private SolverVariable findBestInputMaterial( ClientModel castingModel, String elementName, String specId, boolean withCu )
    {
        //        List<SpecElementMaterial> specElementMaterials = MeltingPlan.getInstance().getSpecElementMainData().getSpecElementMaterials();
        //        // erst mit Legierung suchen
        //        for ( SpecElementMaterial specElementMaterial : specElementMaterials )
        //        {
        //            if ( specElementMaterial.getName().equals( elementName ) && Objects.equals( specId, specElementMaterial.getAlloy() ) )
        //            {
        //                if ( ( withCu && "CU".equals( specElementMaterial.getName() ) ) == false )
        //                {
        //                    if ( specElementMaterial.isActivated() == false )
        //                    {
        //                        return null;
        //                    }
        //                }
        //                Material preferredMaterial = specElementMaterial.getPreferredMaterial();
        //                if ( preferredMaterial == null )
        //                {
        //                    return null;
        //                }
        //                SolverVariable solverVariable = new SolverVariable( preferredMaterial );
        //                if ( specElementMaterial.getMaximum() > 0 )
        //                {
        //                    solverVariable.setMaximumValue( specElementMaterial.getMaximum() );
        //                }
        //                return solverVariable;
        //            }
        //        }
        //        for ( SpecElementMaterial specElementMaterial : specElementMaterials )
        //        {
        //            if ( specElementMaterial.getName().equals( elementName ) && "*".equals( specElementMaterial.getAlloy() ) )
        //            {
        //                if ( ( withCu && "CU".equals( specElementMaterial.getName() ) ) == false )
        //                {
        //                    if ( specElementMaterial.isActivated() == false )
        //                    {
        //                        return null;
        //                    }
        //                }
        //                Material preferredMaterial = specElementMaterial.getPreferredMaterial();
        //                if ( preferredMaterial == null )
        //                {
        //                    return null;
        //                }
        //                SolverVariable solverVariable = new SolverVariable( preferredMaterial );
        //                if ( specElementMaterial.getMaximum() > 0 )
        //                {
        //                    solverVariable.setMaximumValue( specElementMaterial.getMaximum() );
        //                }
        //                return solverVariable;
        //            }
        //        }
        Material bestInputMaterial = searchBestInputMaterial( castingModel, elementName );
        if ( bestInputMaterial == null )
        {
            return null;
        }
        return new SolverVariable( bestInputMaterial );
    }

    public static Material searchBestInputMaterial( ClientModel castingModel, String elementName )
    {
        // Fügt alle Legierungsmaterialien hinzu die nicht bereits hinzugefügt
        // worden
        List<MaterialGroup> materialGroups = castingModel.getView( CastingClientModel.CHARGING_MATERIAL_GROUP_VIEW_ID );
        for ( MaterialGroup materialGroup : materialGroups )
        {
            //            if ( materialGroup.getName().startsWith( "01" ) && "CU".equals( elementName ) )
            //            {
            //                List<Material> materials = materialGroup.getMaterials();
            //                for ( Material material : materials )
            //                {
            //                    if ( material.getAnalysis().getCompositionElementValue( elementName ) >= 100.0 )
            //                    {
            //                        return material;
            //                    }
            //                }
            //            }
            if ( materialGroup.getName().startsWith( "Auflegierungen" ) )
            {
                List<Material> materials = materialGroup.getMaterials();
                // Material mit höchsten Wert ermittel
                double maxValue = 0;
                Material maxMaterial = null;
                for ( Material material : materials )
                {
                    if ( material.getAnalysis().getCompositionElementValue( elementName ) > maxValue )
                    {
                        maxMaterial = material;
                    }
                }
                return maxMaterial;
            }
        }
        return null;
    }

}
