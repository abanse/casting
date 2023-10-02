package com.hydro.casting.gui.prod.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.analysis.util.AnalysisCompositionHelper;
import com.hydro.casting.gui.model.*;
import com.hydro.casting.gui.model.common.AnalysisCalculator;
import com.hydro.casting.gui.prod.table.ChargingTable;
import com.hydro.casting.server.contract.dto.ChargeSpecificationDTO;
import com.hydro.casting.server.contract.dto.SpecificationElementDTO;
import com.hydro.core.gui.ClientModelManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.model.ClientModel;
import com.hydro.core.gui.task.AbstractTask;
import javafx.application.Platform;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ChargingWizardTask extends AbstractTask
{
    private final static Logger log = LoggerFactory.getLogger( ChargingWizardTask.class );

    @Inject
    private NotifyManager notifyManager;

    @Inject
    private ClientModelManager clientModelManager;

    private ChargingTable chargingTable;

    public ChargingTable getChargingTable()
    {
        return chargingTable;
    }

    public void setChargingTable( ChargingTable chargingTable )
    {
        this.chargingTable = chargingTable;
    }

    @Override
    public void doWork() throws Exception
    {
        final Analysis furnaceContent = chargingTable.getAverageAnalyse();
        AnalysisCompositionHelper.switchBackFromPPM( furnaceContent );
        final ChargeSpecificationDTO chargeSpecificationDTO = chargingTable.getChargeSpecificationDTO();
        final ClientModel casterClientModel = clientModelManager.getClientModel( CastingClientModel.ID );

        // Finde Elemente mit Zielfenster
        final List<SpecificationElementDTO> toCheckElements = chargeSpecificationDTO.getElements().stream().filter( specificationElementDTO -> {
            if ( specificationElementDTO.getMinValue() != null && specificationElementDTO.getMinValue() > 0 )
            {
                return true;
            }
            if ( specificationElementDTO.getCastingMinValue() != null && specificationElementDTO.getCastingMinValue() > 0 )
            {
                return true;
            }
            return false;
        } ).collect( Collectors.toList() );

        // Finde passende Einsatzmaterialien für diese Elemente
        // sortiert nach Einlagerungszeitpunkt
        final MultiValuedMap<String, Material> possibleMaterials = new ArrayListValuedHashMap<>();

        final List<MaterialGroup> materialGroups = casterClientModel.getView( CastingClientModel.CHARGING_MATERIAL_GROUP_VIEW_ID );

        toCheckElements.forEach( specificationElementDTO -> materialGroups.forEach( materialGroup -> materialGroup.getMaterials().forEach( material -> {
            if ( material.getAnalysis() != null )
            {
                final Analysis materialAnalysis = material.getAnalysis();
                if ( materialAnalysis.getCompositionElementValue( specificationElementDTO.getName() ) > 0 )
                {
                    possibleMaterials.put( specificationElementDTO.getName(), material );
                }
            }
        } ) ) );

        possibleMaterials.keySet().forEach( elementName -> log.info( "possibleMaterials elementName " + elementName + " " + possibleMaterials.get( elementName ) ) );

        final Map<String, Material> bestElementMaterials = new HashMap<>();
        possibleMaterials.keySet().forEach( elementName -> {
            final List<Material> sortedCountMatElements = possibleMaterials.get( elementName ).stream().sorted( Comparator.comparing( Material::getCountAnalysisElements ) )
                    .collect( Collectors.toList() );
            final Optional<Material> bestElementMaterial = sortedCountMatElements.stream().min( Comparator.comparing( Material::getGenerationSuccessTS ) );
            bestElementMaterial.ifPresent( material -> bestElementMaterials.put( elementName, material ) );
        } );

        // Liste der elemente sortiert nach mehrfachen Elementen
        final List<String> elementNames = bestElementMaterials.keySet().stream().sorted( ( elementName1, elementName2 ) -> {
            final Material mat1 = bestElementMaterials.get( elementName1 );
            final Material mat2 = bestElementMaterials.get( elementName2 );
            int countMatElements1 = 0;
            int countMatElements2 = 0;
            if ( mat1.getAnalysis() != null && mat1.getAnalysis().getCompositionElements() != null )
            {
                countMatElements1 = mat1.getAnalysis().getCompositionElements().size();
            }
            if ( mat2.getAnalysis() != null && mat2.getAnalysis().getCompositionElements() != null )
            {
                countMatElements2 = mat2.getAnalysis().getCompositionElements().size();
            }
            return -Integer.compare( countMatElements1, countMatElements2 );
        } ).collect( Collectors.toList() );

        log.info( "fill materials in this order " + elementNames );

        final Specification specification = AnalysisCompositionHelper.getSpecification( chargeSpecificationDTO );

        final List<TransferMaterial> addedMaterials = new ArrayList<>();

        // Genauigkeit erhöhen durch mehrfache Berechnungen
        final Map<String, TransferMaterial> addedMaterialIndex = new HashMap<>();
        for ( int i = 0; i < 10; i++ )
        {
            for ( String elementName : elementNames )
            {
                final List<Analysis> actualAnalysis = new ArrayList<>();
                actualAnalysis.add( furnaceContent );
                addedMaterials.forEach( transferMaterial -> {
                    final Analysis transferMaterialAnalysis = transferMaterial.getAnalysis().clone();
                    transferMaterialAnalysis.setWeight( transferMaterial.getWeight() );
                    actualAnalysis.add( transferMaterialAnalysis );
                } );
                final Analysis actualFurnaceContent = AnalysisCalculator.createAverageFromAnalysis( "Ø", actualAnalysis, specification );
                final SpecificationElementDTO specificationElementDTO = chargeSpecificationDTO.getElement( elementName );
                if ( elementName.startsWith( "Ti" ))
                {
                    System.out.println("###");
                }
                double minValue = 0.;
                if ( specificationElementDTO.getMinValue() != null )
                {
                    minValue = specificationElementDTO.getMinValue();
                }
                if ( specificationElementDTO.getCastingMinValue() != null )
                {
                    minValue = specificationElementDTO.getCastingMinValue();
                }
                double maxValue = 0.;
                if ( specificationElementDTO.getMaxValue() != null )
                {
                    maxValue = specificationElementDTO.getMaxValue();
                }
                if ( specificationElementDTO.getCastingMaxValue() != null )
                {
                    maxValue = specificationElementDTO.getCastingMaxValue();
                }
                final double targetValue;
                if ( maxValue > 0. )
                {
                    targetValue = ( minValue + maxValue ) / 2.;
                }
                else
                {
                    targetValue = minValue;
                }
                log.info( "search elementName " + elementName + " -> " + targetValue );

                double elementValue = actualFurnaceContent.getCompositionElementValue( elementName );
                if ( elementValue > targetValue )
                {
                    log.info( "element " + elementName + " is too high" );
                    continue;
                }
                double neededElementValue = targetValue - elementValue;
                final Material materialToAdd = bestElementMaterials.get( elementName );
                double materialElementValue = materialToAdd.getAnalysis().getCompositionElementValue( elementName );
                double materialWeight = actualFurnaceContent.getWeight() * neededElementValue / materialElementValue;
                // Hier noch den Factor von der Konfiguration beachten
                if ( specificationElementDTO.getElementFactor() != 1.0 )
                {
                    materialWeight = materialWeight / specificationElementDTO.getElementFactor();
                }
                log.info( "add material " + materialWeight + " " + materialToAdd );
                TransferMaterial transferMaterial = addedMaterialIndex.get( elementName );
                if ( transferMaterial == null )
                {
                    transferMaterial = new TransferMaterial();
                    transferMaterial.setSource( materialToAdd );
                    transferMaterial.setWeight( materialWeight );
                    addedMaterials.add( transferMaterial );
                    addedMaterialIndex.put( elementName, transferMaterial );
                }
                else
                {
                    transferMaterial.addWeight( materialWeight );
                }
            }
        }

        final List<TransferMaterial> cleanedTransferMaterials = addedMaterials.stream().filter( transferMaterial -> transferMaterial.getWeight() > 0.1 ).collect( Collectors.toList() );

        if ( cleanedTransferMaterials.isEmpty() )
        {
            notifyManager.showSplashMessage( "Es konnten keine Materialien gefunden werden" );
            return;
        }

        Platform.runLater( () -> {
            chargingTable.addMaterials( cleanedTransferMaterials );
        } );

        notifyManager.showSplashMessage( "Die Berechnung wurde erfolgreich durchgeführt" );

        //        FXUtilities.runAndWait( () -> {
        //            progressDialog = new Dialog<>();
        //            progressDialog.initOwner( applicationManager.getMainStage() );
        //            progressDialog.getDialogPane().setHeaderText( "Mengen berechnen" );
        //            ProgressBar progressBar = new ProgressBar();
        //            progressBar.setPrefWidth( 500 );
        //            progressDialog.getDialogPane().setContent( progressBar );
        //            progressDialog.getDialogPane().getButtonTypes().add( ButtonType.CANCEL );
        //            solvingWorker.restart();
        //            progressDialog.setOnCloseRequest( new EventHandler<DialogEvent>()
        //            {
        //                @Override
        //                public void handle( DialogEvent event )
        //                {
        //                    if ( solvingWorker.isRunning() )
        //                    {
        //                        log.info( "cancel worker" );
        //                        solvingWorker.cancel();
        //                    }
        //                }
        //            } );
        //            progressDialog.showAndWait();
        //        } );
    }

    //    public class SolvingWorker extends Service<List<SolverVariable>>
    //    {
    //        @Override
    //        protected Task<List<SolverVariable>> createTask()
    //        {
    //            return new Task<List<SolverVariable>>()
    //            {
    //                @Override
    //                protected List<SolverVariable> call() throws Exception
    //                {
    //                    List<SolverVariable> solverVariables = null;
    //                    try
    //                    {
    //                        solverVariables = solver.solve( transfer, chargeSpecificationDTO );
    //                    }
    //                    catch ( NoSolutionFoundException nsfex )
    //                    {
    ////                        final BooleanProperty retryWithCu = new SimpleBooleanProperty( false );
    ////                        FXUtilities.runAndWait( () -> {
    ////                            Dialog<ButtonType> dialog = new Dialog<>();
    ////                            dialog.initOwner( applicationManager.getMainStage() );
    ////                            dialog.getDialogPane().setHeaderText( "Mengen berechnen" );
    ////                            dialog.getDialogPane().setContentText( "Es konnte keine Menge ermittelt werden, die in die vorgegebene Spezikation passen würde. "
    ////                                    + "Wollen Sie eine weitere Berechnung mit CU als Korrekturmaterial durchführen? " );
    ////                            dialog.getDialogPane().getButtonTypes().addAll( ButtonType.YES, ButtonType.NO );
    ////                            Optional<ButtonType> result = dialog.showAndWait();
    ////                            if ( result.isPresent() && ButtonType.YES.equals( result.get() ) )
    ////                            {
    ////                                retryWithCu.set( true );
    ////                            }
    ////                        } );
    ////
    ////                        if ( retryWithCu.get() )
    ////                        {
    ////                            try
    ////                            {
    ////                                solverVariables = solver.solve( transfer, true );
    ////                            }
    ////                            catch ( NoSolutionFoundException nsfex2 )
    ////                            {
    //                                solverVariables = null;
    ////                            }
    ////                        }
    ////                        else
    ////                        {
    ////                            solverVariables = Collections.emptyList();
    ////                        }
    //                    }
    //
    //                    return solverVariables;
    //                }
    //
    //                @Override
    //                protected void cancelled()
    //                {
    //                    solver.cancel();
    //                }
    //
    //                @Override
    //                protected void succeeded()
    //                {
    //                    List<SolverVariable> solverVariables = null;
    //                    try
    //                    {
    //                        solverVariables = get();
    //                    }
    //                    catch ( InterruptedException e )
    //                    {
    //                        e.printStackTrace();
    //                    }
    //                    catch ( ExecutionException e )
    //                    {
    //                        e.printStackTrace();
    //                    }
    //                    if ( progressDialog.isShowing() )
    //                    {
    //                        progressDialog.hide();
    //                    }
    //                    if ( solverVariables == null )
    //                    {
    //                        Platform.runLater( () -> {
    //                            Dialog<ButtonType> dialog = new Dialog<>();
    //                            dialog.initOwner( applicationManager.getMainStage() );
    //                            dialog.getDialogPane().setHeaderText( "Mengen berechnen" );
    //                            dialog.getDialogPane().setContentText( "Es konnte keine Menge ermittelt werden, die in die vorgegebene Spezikation passen würde." );
    //                            dialog.getDialogPane().getButtonTypes().add( ButtonType.OK );
    //                            dialog.showAndWait();
    //                        } );
    //                        return;
    //                    }
    //                    if ( solverVariables.isEmpty() )
    //                    {
    //                        return;
    //                    }
    //
    //                    if ( chargingTable != null )
    //                    {
    //                        FXUtilities.flash( chargingTable, "Die Daten wurden berechnet" );
    //                    }
    //
    //                    for ( SolverVariable solverVariable : solverVariables )
    //                    {
    //                        Set<TransferMaterial> transferMaterials = solverVariable.getTransferMaterials();
    //                        // Legierungsmaterialien
    //                        if ( transferMaterials == null )
    //                        {
    //                            continue;
    //                        }
    //                        double newWeight = solverVariable.getWeight();
    //                        if ( transferMaterials.size() == 1 )
    //                        {
    //                            TransferMaterial transferMaterial = transferMaterials.iterator().next();
    //                            log.info( "update transfer material weight " + transferMaterial.getSource() + " " + transferMaterial.getWeight() );
    ////                            UpdateTransferMaterialWeight updateTransferMaterialWeight = new UpdateTransferMaterialWeight( transfer, transferMaterial, newWeight );
    ////                            commandManager.executeCommand( updateTransferMaterialWeight );
    //                        }
    //                        else
    //                        {
    //                            double currentFixedWeight = 0;
    //                            double currentVariableWeight = 0;
    //                            for ( TransferMaterial transferMaterial : transferMaterials )
    //                            {
    //                                if ( transferMaterial.getType() == ETransferMaterialType.VARIABLE.getApk() )
    //                                {
    //                                    currentVariableWeight = currentVariableWeight + transferMaterial.getWeight();
    //                                }
    //                                else
    //                                {
    //                                    currentFixedWeight = currentFixedWeight + transferMaterial.getWeight();
    //                                }
    //                            }
    //                            double newVariableWeight = newWeight - currentFixedWeight;
    //                            if ( newVariableWeight <= 0.0 )
    //                            {
    //                                newVariableWeight = 0.0001;
    //                            }
    //                            double newFixedWeight = newWeight - newVariableWeight;
    //                            if ( newFixedWeight <= 0.0 )
    //                            {
    //                                newFixedWeight = 0.0001;
    //                            }
    //                            for ( TransferMaterial transferMaterial : transferMaterials )
    //                            {
    //                                if ( transferMaterial.getType() == ETransferMaterialType.VARIABLE.getApk() )
    //                                {
    //                                    log.info( "update variable transfer material weight " + transferMaterial.getSource() + " " + newVariableWeight );
    ////                                    UpdateTransferMaterialWeight updateTransferMaterialWeight = new UpdateTransferMaterialWeight( transfer, transferMaterial, newVariableWeight );
    ////                                    commandManager.executeCommand( updateTransferMaterialWeight );
    //                                }
    //                                else
    //                                {
    //                                    log.info( "update fixed transfer material weight " + transferMaterial.getSource() + " " + newFixedWeight );
    ////                                    UpdateTransferMaterialWeight updateTransferMaterialWeight = new UpdateTransferMaterialWeight( transfer, transferMaterial, newFixedWeight );
    ////                                    commandManager.executeCommand( updateTransferMaterialWeight );
    //                                }
    //                            }
    //                        }
    //                    }
    //                    // Legierungsmaterialien hinzufügen
    //                    double addedWeight = 0;
    //                    final List<TransferMaterial> newTransferMaterials = new ArrayList<>();
    //                    for ( SolverVariable solverVariable : solverVariables )
    //                    {
    //                        Material material = solverVariable.getMaterial();
    //                        if ( material == null )
    //                        {
    //                            continue;
    //                        }
    //                        double newWeight = solverVariable.getWeight();
    //                        if ( newWeight < 0.1 )
    //                        {
    //                            continue;
    //                        }
    //                        log.info( "add alloy material " + material + " " + newWeight );
    //                        final TransferMaterial newTransferMaterial = new TransferMaterial();
    //                        newTransferMaterial.setName( material.getName() );
    //                        newTransferMaterial.setSource( material );
    //                        newTransferMaterial.setWeight( newWeight );
    //                        newTransferMaterials.add( newTransferMaterial );
    ////                        AddMaterialToTransfer addMaterialToTransfer = new AddMaterialToTransfer( owner, material, transfer, newWeight, false, ETransferMaterialType.VARIABLE );
    ////                        commandManager.executeCommand( addMaterialToTransfer );
    //                        addedWeight = addedWeight + newWeight;
    //                    }
    //                    // Füllmaterial menge berechnen
    //                    final List<CompositionDTO> analysis =  chargingTable.getCurrentAnalysisDetail().getAnalysisList();
    //                    for ( CompositionDTO compositionDTO : analysis )
    //                    {
    //                        addedWeight = addedWeight + compositionDTO.getWeight();
    //                    }
    //                    final TransferMaterial fillmentMaterial = new TransferMaterial();
    //                    fillmentMaterial.setName( transfer.getFillMaterial().getName() );
    //                    fillmentMaterial.setSource( transfer.getFillMaterial() );
    //                    fillmentMaterial.setWeight( transfer.getTargetWeight() - addedWeight );
    //                    newTransferMaterials.add(0, fillmentMaterial );
    //
    //                    chargingTable.addMaterials( newTransferMaterials );
    //                    resultExist = true;
    //                }
    //
    //                @Override
    //                protected void failed()
    //                {
    //                    if ( progressDialog.isShowing() )
    //                    {
    //                        progressDialog.hide();
    //                    }
    //                    if ( getException() != null )
    //                    {
    //                        getException().printStackTrace();
    //                        Platform.runLater( () -> {
    //                            Alert alert = new Alert( AlertType.ERROR, "Fehler beim Berechnen: " + getException().getMessage(), ButtonType.CLOSE );
    //                            alert.initOwner( applicationManager.getMainStage() );
    //                            alert.getDialogPane().setGraphic( null );
    //                            alert.show();
    //                        } );
    //                    }
    //                }
    //            };
    //        }
    //    }
    //
    //    public boolean isResultExist()
    //    {
    //        return resultExist;
    //    }

    @Override
    public String getId()
    {
        return SecurityCasting.PROD.ACTION.CHARGING_WIZARD;
    }

}
