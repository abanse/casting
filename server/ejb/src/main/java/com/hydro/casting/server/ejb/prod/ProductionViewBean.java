package com.hydro.casting.server.ejb.prod;

import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.common.constant.CasterStep;
import com.hydro.casting.common.constant.FurnaceStep;
import com.hydro.casting.server.contract.dto.*;
import com.hydro.casting.server.contract.prod.ProductionView;
import com.hydro.casting.server.ejb.main.service.ProcessStepService;
import com.hydro.casting.server.ejb.planning.service.CasterScheduleService;
import com.hydro.casting.server.ejb.prod.service.ProductionLogService;
import com.hydro.casting.server.model.downtime.Downtime;
import com.hydro.casting.server.model.downtime.dao.DowntimeHome;
import com.hydro.casting.server.model.inspection.dao.InspectionValueHome;
import com.hydro.casting.server.model.res.Caster;
import com.hydro.casting.server.model.res.MeltingFurnace;
import com.hydro.casting.server.model.res.dao.CasterHome;
import com.hydro.casting.server.model.res.dao.MeltingFurnaceHome;
import com.hydro.casting.server.model.sched.CastingBatch;
import com.hydro.casting.server.model.sched.dao.CastingBatchHome;
import com.hydro.core.common.util.StringTools;
import com.hydro.core.server.contract.workplace.DetailProvider;
import com.hydro.core.server.contract.workplace.SearchType;
import com.hydro.core.server.contract.workplace.TaskValidation;
import com.hydro.core.server.contract.workplace.ViewModel;
import com.hydro.core.server.contract.workplace.dto.ViewDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDateTime;
import java.util.*;

import static com.hydro.casting.server.contract.dto.InspectionValueDTO.RESULT_NOT_FILLED;

@Stateless
public class ProductionViewBean implements ProductionView
{
    private final static Logger log = LoggerFactory.getLogger( ProductionViewBean.class );

    @EJB
    private CasterScheduleService casterScheduleService;

    @EJB
    private ProductionLogService productionLogService;

    @EJB
    private ProcessStepService processStepService;

    @EJB
    private CastingBatchHome castingBatchHome;

    @EJB
    private MeltingFurnaceHome meltingFurnaceHome;

    @EJB
    private CasterHome casterHome;

    @EJB
    private DowntimeHome downtimeHome;

    @EJB
    private InspectionValueHome inspectionValueHome;

    @EJB( beanName = "TimeManagementListDetailProvider" )
    private DetailProvider<CastingInstructionDTO, TimeManagementListDTO> timeManagementListDetailProvider;

    @EJB( beanName = "ChargeDetailProvider" )
    private DetailProvider<ProcessDocuDTO, CasterScheduleDTO> chargeDetailProvider;

    @EJB( beanName = "VisualInspectionDetailProvider" )
    private DetailProvider<ProcessDocuDTO, VisualInspectionDTO> visualInspectionDetailProvider;

    private Map<Class<?>, DetailProvider<CastingInstructionDTO, ?>> detailProviders = new HashMap<>();

    @PostConstruct
    private void postConstruct()
    {
        detailProviders.put( TimeManagementListDTO.class, timeManagementListDetailProvider );
    }

    @Override
    public <D extends ViewDTO> D loadDetail( Class<D> dto, CastingInstructionDTO master, Map<String, String> context )
    {
        @SuppressWarnings( "unchecked" )
        DetailProvider<CastingInstructionDTO, D> provider = (DetailProvider<CastingInstructionDTO, D>) detailProviders.get( dto );
        if ( provider != null )
        {
            return provider.loadDetail( master, context );
        }
        return null;
    }

    @Override
    public List<CasterScheduleDTO> loadBySearchType( SearchType searchType, Map<String, Object> parameters )
    {
        return casterScheduleService.loadBySearchType( searchType, parameters );
    }

    @Override
    public List<ProductionLogDTO> loadCurrentProductionLog( String machineApk )
    {
        return productionLogService.loadCurrentProductionLog( machineApk );
    }

    @Override
    public List<UnloadSlabDTO> loadUnloadSlabs( long castingBatchOID )
    {
        final CasterScheduleDTO casterScheduleDTO = chargeDetailProvider.loadDetail( new ProcessDocuDTO( castingBatchOID ), null );
        if ( casterScheduleDTO == null )
        {
            return null;
        }
        final VisualInspectionDTO visualInspectionDTO = visualInspectionDetailProvider.loadDetail( new ProcessDocuDTO( castingBatchOID ), null );
        String[] issues = null;
        if ( visualInspectionDTO != null )
        {
            final List<InspectionValueDTO> values = visualInspectionDTO.getValues();
            for ( InspectionValueDTO value : values )
            {
                if ( value.getVisualInspectionIssue() != null )
                {
                    issues = value.getVisualInspectionIssue().split( "\\|" );
                }
            }
        }

        final List<UnloadSlabDTO> unloadSlabs = new ArrayList<>();
        int netIndex = 0;
        int slabIndex = 1;
        for ( int i = 1; i < 6; i++ )
        {
            final CasterSchedulePosDTO pos = casterScheduleDTO.getPos( i );
            if ( pos.getMaterialType() == null )
            {
                continue;
            }
            final UnloadSlabDTO unloadSlabDTO = new UnloadSlabDTO();
            unloadSlabDTO.setSlab( "" + casterScheduleDTO.getChargeWithoutYear() + "1" + i );
            unloadSlabDTO.setOrder( casterScheduleDTO.getProcessOrder() );
            unloadSlabDTO.setHeight( 600 );
            unloadSlabDTO.setWidth( pos.getWidth() );
            unloadSlabDTO.setLength( pos.getLength() );
            unloadSlabDTO.setLengthBonus( pos.getLengthBonus() );
            if ( issues != null && issues.length > netIndex && StringTools.isFilled( issues[netIndex] ) && !Objects.equals( "null", issues[netIndex] ) )
            {
                unloadSlabDTO.setNetzeSP( "Sperre: " + issues[netIndex] );
            }
            else
            {
                unloadSlabDTO.setNetzeSP( "i.O." );
            }
            if ( issues != null && issues.length > slabIndex && StringTools.isFilled( issues[slabIndex] ) && !Objects.equals( "null", issues[slabIndex] ) )
            {
                unloadSlabDTO.setBarrenSP( "Sperre: " + issues[slabIndex] );
            }
            else
            {
                unloadSlabDTO.setBarrenSP( "i.O." );
            }

            unloadSlabs.add( unloadSlabDTO );
            netIndex = netIndex + 3;
            slabIndex = slabIndex + 3;
        }
        return unloadSlabs;
    }

    @Override
    public ViewModel<FurnaceInstructionDTO> validate( String currentUser, FurnaceInstructionDTO furnaceInstructionDTO )
    {
        final ViewModel<FurnaceInstructionDTO> viewModel = new ViewModel<>();
        viewModel.setCurrentDTOs( furnaceInstructionDTO );

        final CastingBatch activeBatch;
        if ( furnaceInstructionDTO != null && furnaceInstructionDTO.getFurnace() != null )
        {
            final String furnaceApk = furnaceInstructionDTO.getFurnace();
            activeBatch = castingBatchHome.findActiveForMeltingFurnace( furnaceApk );
        }
        else
        {
            activeBatch = null;
        }

        final MeltingFurnace meltingFurnace;
        if ( furnaceInstructionDTO != null )
        {
            meltingFurnace = meltingFurnaceHome.findByApk( furnaceInstructionDTO.getFurnace() );
        }
        else
        {
            meltingFurnace = null;
        }
        boolean timeValidationExists = false;
        if ( meltingFurnace != null && meltingFurnace.getCurrentStepStartTS() != null && meltingFurnace.getCurrentStepDuration() > 0 )
        {
            final LocalDateTime normalEnd = meltingFurnace.getCurrentStepStartTS().plusMinutes( meltingFurnace.getCurrentStepDuration() );
            if ( normalEnd.isBefore( LocalDateTime.now() ) )
            {
                timeValidationExists = true;
            }
        }
        if ( timeValidationExists )
        {
            final Downtime openDowntime = downtimeHome.findOpenDowntimeByCostCenter( furnaceInstructionDTO.getFurnace(), null );
            if ( openDowntime != null )
            {
                timeValidationExists = false;
            }
            else
            {
                final Downtime closedDowntime = downtimeHome.findClosedDowntimeByCostCenter( furnaceInstructionDTO.getFurnace(), null, meltingFurnace.getCurrentStepStartTS() );
                if ( closedDowntime != null )
                {
                    timeValidationExists = false;
                }
            }
        }

        // Charge aktivieren
        TaskValidation activateChargeTV = new TaskValidation();
        activateChargeTV.setId( SecurityCasting.PROD.ACTION.ACTIVATE_CHARGE );
        if ( furnaceInstructionDTO != null && activeBatch != null )
        {
            activateChargeTV.setDisabled( true );
            activateChargeTV.setRemark( null );
        }
        else if ( timeValidationExists )
        {
            activateChargeTV.setDisabled( true );
            activateChargeTV.setRemark( "Aktive Zeitüberschreitung existiert" );
        }
        else
        {
            activateChargeTV.setDisabled( false );
            activateChargeTV.setRemark( null );
        }
        viewModel.addTaskValidations( activateChargeTV );

        // Charge deaktivieren
        TaskValidation deactivateChargeTV = new TaskValidation();
        deactivateChargeTV.setId( SecurityCasting.PROD.ACTION.DEACTIVATE_CHARGE );
        if ( furnaceInstructionDTO == null || activeBatch == null )
        {
            deactivateChargeTV.setDisabled( true );
            deactivateChargeTV.setRemark( null );
        }
        else if ( timeValidationExists )
        {
            deactivateChargeTV.setDisabled( true );
            deactivateChargeTV.setRemark( "Aktive Zeitüberschreitung existiert" );
        }
        else
        {
            deactivateChargeTV.setDisabled( false );
            deactivateChargeTV.setRemark( null );
        }
        viewModel.addTaskValidations( deactivateChargeTV );

        // Get timestamps from batch - getStepTS() is null-safe
        LocalDateTime preparingTS = processStepService.getStepTS( activeBatch, FurnaceStep.Preparing );
        LocalDateTime chargingTS = processStepService.getStepTS( activeBatch, FurnaceStep.Charging );
        LocalDateTime skimmingTS = processStepService.getStepTS( activeBatch, FurnaceStep.Skimming );
        LocalDateTime treatingTS = processStepService.getStepTS( activeBatch, FurnaceStep.Treating );
        LocalDateTime restingTS = processStepService.getStepTS( activeBatch, FurnaceStep.Resting );
        LocalDateTime releaseTS = activeBatch != null ? activeBatch.getReleaseTS() : null;

        // Ofenreinigung
        TaskValidation cleanFurnaceTV = new TaskValidation();
        cleanFurnaceTV.setId( SecurityCasting.PROD.ACTION.CLEAN_FURNACE );
        if ( activeBatch == null )
        {
            cleanFurnaceTV.setDisabled( true );
            cleanFurnaceTV.setRemark( "Keine Charge aktiv" );
        }
        else if ( chargingTS != null || skimmingTS != null || treatingTS != null || restingTS != null || releaseTS != null )
        {
            cleanFurnaceTV.setDisabled( true );
            cleanFurnaceTV.setRemark( null ); // Bereits einen Schritt weiter
        }
        else if ( preparingTS != null )
        {
            cleanFurnaceTV.setDisabled( true );
            cleanFurnaceTV.setRemark( "Reinigung aktiv" );
        }
        else if ( timeValidationExists )
        {
            cleanFurnaceTV.setDisabled( true );
            cleanFurnaceTV.setRemark( "Aktive Zeitüberschreitung existiert" );
        }
        else
        {
            cleanFurnaceTV.setDisabled( false );
            cleanFurnaceTV.setRemark( null );
        }
        viewModel.addTaskValidations( cleanFurnaceTV );

        // Analyse anfordern
        TaskValidation requestAnalyseTV = new TaskValidation();
        requestAnalyseTV.setId( SecurityCasting.PROD.ACTION.REQUEST_ANALYSE );
        if ( activeBatch == null )
        {
            requestAnalyseTV.setDisabled( true );
            requestAnalyseTV.setRemark( "Keine Charge aktiv" );
        }
        else
        {
            requestAnalyseTV.setDisabled( true );
            requestAnalyseTV.setRemark( null ); // not implemented at the moment
        }
        viewModel.addTaskValidations( requestAnalyseTV );

        // Chargieren
        TaskValidation chargingFurnaceTV = new TaskValidation();
        chargingFurnaceTV.setId( SecurityCasting.PROD.ACTION.CHARGING_FURNACE );
        if ( activeBatch == null )
        {
            chargingFurnaceTV.setDisabled( true );
            chargingFurnaceTV.setRemark( "Keine Charge aktiv" );
        }
        else if ( releaseTS != null )
        {
            chargingFurnaceTV.setDisabled( true );
            chargingFurnaceTV.setRemark( "Freigabe Gießen bereits erteilt" );
        }
        else if ( restingTS != null )
        {
            chargingFurnaceTV.setDisabled( true );
            chargingFurnaceTV.setRemark( "Abstehen bereits aktiv" );
        }
        else if ( skimmingTS != null )
        {
            chargingFurnaceTV.setDisabled( true );
            chargingFurnaceTV.setRemark( "Abkrätzen bereits aktiv" );
        }
        else if ( treatingTS != null )
        {
            chargingFurnaceTV.setDisabled( true );
            chargingFurnaceTV.setRemark( "Gattieren bereits aktiv" );
        }
        else if ( timeValidationExists )
        {
            chargingFurnaceTV.setDisabled( true );
            chargingFurnaceTV.setRemark( "Aktive Zeitüberschreitung existiert" );
        }
        else
        {
            chargingFurnaceTV.setDisabled( false );
            chargingFurnaceTV.setRemark( null );
        }
        viewModel.addTaskValidations( chargingFurnaceTV );

        // Gattieren
        TaskValidation treatingFurnaceTV = new TaskValidation();
        treatingFurnaceTV.setId( SecurityCasting.PROD.ACTION.TREATING_FURNACE );
        if ( activeBatch == null )
        {
            treatingFurnaceTV.setDisabled( true );
            treatingFurnaceTV.setRemark( "Keine Charge aktiv" );
        }
        else if ( releaseTS != null )
        {
            treatingFurnaceTV.setDisabled( true );
            treatingFurnaceTV.setRemark( "Freigabe Gießen bereits erteilt" );
        }
        else if ( restingTS != null )
        {
            treatingFurnaceTV.setDisabled( true );
            treatingFurnaceTV.setRemark( "Abstehen bereits aktiv" );
        }
        else if ( skimmingTS != null )
        {
            treatingFurnaceTV.setDisabled( true );
            treatingFurnaceTV.setRemark( "Abkrätzen bereits aktiv" );
        }
        else if ( timeValidationExists )
        {
            treatingFurnaceTV.setDisabled( true );
            treatingFurnaceTV.setRemark( "Aktive Zeitüberschreitung existiert" );
        }
        else
        {
            treatingFurnaceTV.setDisabled( false );
            treatingFurnaceTV.setRemark( null );
        }
        viewModel.addTaskValidations( treatingFurnaceTV );

        // Abkrätzen
        TaskValidation skimmingFurnaceTV = new TaskValidation();
        skimmingFurnaceTV.setId( SecurityCasting.PROD.ACTION.SKIMMING_FURNACE );
        if ( activeBatch == null )
        {
            skimmingFurnaceTV.setDisabled( true );
            skimmingFurnaceTV.setRemark( "Keine Charge aktiv" );
        }
        else if ( releaseTS != null )
        {
            skimmingFurnaceTV.setDisabled( true );
            skimmingFurnaceTV.setRemark( "Freigabe Gießen bereits erteilt" );
        }
        else if ( restingTS != null )
        {
            skimmingFurnaceTV.setDisabled( true );
            skimmingFurnaceTV.setRemark( "Abstehen bereits aktiv" );
        }
        else if ( skimmingTS != null )
        {
            skimmingFurnaceTV.setDisabled( true );
            skimmingFurnaceTV.setRemark( "Abkrätzen bereits aktiv" );
        }
        else if ( timeValidationExists )
        {
            skimmingFurnaceTV.setDisabled( true );
            skimmingFurnaceTV.setRemark( "Aktive Zeitüberschreitung existiert" );
        }
        else
        {
            skimmingFurnaceTV.setDisabled( false );
            skimmingFurnaceTV.setRemark( null );
        }
        viewModel.addTaskValidations( skimmingFurnaceTV );

        // Abstehen
        TaskValidation restingFurnaceTV = new TaskValidation();
        restingFurnaceTV.setId( SecurityCasting.PROD.ACTION.RESTING_FURNACE );
        if ( activeBatch == null )
        {
            restingFurnaceTV.setDisabled( true );
            restingFurnaceTV.setRemark( "Keine Charge aktiv" );
        }
        else if ( restingTS != null )
        {
            restingFurnaceTV.setDisabled( true );
            restingFurnaceTV.setRemark( "Abstehen bereits aktiv" );
        }
        else if ( releaseTS != null )
        {
            restingFurnaceTV.setDisabled( true );
            restingFurnaceTV.setRemark( "Freigabe Gießen bereits erteilt" );
        }
        else if ( timeValidationExists )
        {
            restingFurnaceTV.setDisabled( true );
            restingFurnaceTV.setRemark( "Aktive Zeitüberschreitung existiert" );
        }
        else
        {
            restingFurnaceTV.setDisabled( false );
            restingFurnaceTV.setRemark( null );
        }
        viewModel.addTaskValidations( restingFurnaceTV );

        // Freigabe Gießen
        TaskValidation releaseFurnaceTV = new TaskValidation();
        releaseFurnaceTV.setId( SecurityCasting.PROD.ACTION.RELEASE_FURNACE );
        if ( activeBatch == null )
        {
            releaseFurnaceTV.setDisabled( true );
            releaseFurnaceTV.setRemark( "Keine Charge aktiv" );
        }
        else if ( restingTS == null )
        {
            releaseFurnaceTV.setDisabled( true );
            releaseFurnaceTV.setRemark( "Charge hat Abstehzeit noch nicht erreicht" );
        }
        else if ( releaseTS != null )
        {
            releaseFurnaceTV.setDisabled( true );
            releaseFurnaceTV.setRemark( "Freigabe Gießen bereits erteilt" );
        }
        else if ( timeValidationExists )
        {
            releaseFurnaceTV.setDisabled( true );
            releaseFurnaceTV.setRemark( "Aktive Zeitüberschreitung existiert" );
        }
        else
        {
            releaseFurnaceTV.setDisabled( false );
            releaseFurnaceTV.setRemark( null );
        }
        viewModel.addTaskValidations( releaseFurnaceTV );

        return viewModel;
    }

    @Override
    public ViewModel<CasterInstructionDTO> validate( String currentUser, CasterInstructionDTO casterInstructionDTO )
    {
        final ViewModel<CasterInstructionDTO> viewModel = new ViewModel<>();
        viewModel.setCurrentDTOs( casterInstructionDTO );

        final CastingBatch activeBatch;
        if ( casterInstructionDTO != null && casterInstructionDTO.getCaster() != null )
        {
            final String casterApk = casterInstructionDTO.getCaster();
            activeBatch = castingBatchHome.findActiveForCaster( casterApk );
        }
        else
        {
            activeBatch = null;
        }

        final Caster caster;
        if ( casterInstructionDTO != null )
        {
            caster = casterHome.findByApk( casterInstructionDTO.getCaster() );
        }
        else
        {
            caster = null;
        }
        boolean timeValidationExists = false;
        if ( caster != null && caster.getCurrentStepStartTS() != null && caster.getCurrentStepDuration() > 0 )
        {
            final LocalDateTime normalEnd = caster.getCurrentStepStartTS().plusMinutes( caster.getCurrentStepDuration() );
            if ( normalEnd.isBefore( LocalDateTime.now() ) )
            {
                timeValidationExists = true;
            }
        }
        if ( timeValidationExists )
        {
            final Downtime openDowntime = downtimeHome.findOpenDowntimeByCostCenter( casterInstructionDTO.getCaster(), null );
            if ( openDowntime != null )
            {
                timeValidationExists = false;
            }
            else
            {
                final Downtime closedDowntime = downtimeHome.findClosedDowntimeByCostCenter( casterInstructionDTO.getCaster(), null, caster.getCurrentStepStartTS() );
                if ( closedDowntime != null )
                {
                    timeValidationExists = false;
                }
            }
        }

        // Prüfen ob Angussvorbereitung durchgeführt wurde
        boolean productionDocumentsFilledCheckEnabled = isProductionDocumentsFilledCheckEnabled();
        boolean castingPreparationExist = false;
        if ( activeBatch != null )
        {
            final Integer castingPreparation = inspectionValueHome.getInspectionResult( activeBatch, "castingPreparation" );
            if ( castingPreparation != null )
            {
                castingPreparationExist = !productionDocumentsFilledCheckEnabled || castingPreparation < RESULT_NOT_FILLED;
            }
        }

        // Prüfen ob Sichtprüfung durchgeführt wurde
        boolean visualInspectionExist = false;
        if ( activeBatch != null )
        {
            final Integer visualInspection = inspectionValueHome.getInspectionResult( activeBatch, "visualInspection" );
            if ( visualInspection != null )
            {
                visualInspectionExist = !productionDocumentsFilledCheckEnabled || visualInspection < RESULT_NOT_FILLED;
            }
        }

        LocalDateTime unloadingTS = processStepService.getStepTS( activeBatch, CasterStep.Unloading );
        LocalDateTime castingTS = processStepService.getStepTS( activeBatch, CasterStep.Casting );

        // Start Casting
        TaskValidation startCastingTV = new TaskValidation();
        startCastingTV.setId( SecurityCasting.PROD.ACTION.START_CASTING );
        if ( casterInstructionDTO == null || activeBatch == null )
        {
            startCastingTV.setDisabled( true );
            startCastingTV.setRemark( null );
        }
        else if ( unloadingTS != null )
        {
            startCastingTV.setDisabled( true );
            startCastingTV.setRemark( "Abfahren bereits gestartet" );
        }
        else if ( !castingPreparationExist )
        {
            startCastingTV.setDisabled( true );
            startCastingTV.setRemark( "Die Angussvorbereitung ist noch nicht durchgeführt" );
        }
        else if ( castingTS != null )
        {
            startCastingTV.setDisabled( true );
            startCastingTV.setRemark( "Gießen Start bereits aktiv" );
        }
        else if ( timeValidationExists )
        {
            startCastingTV.setDisabled( true );
            startCastingTV.setRemark( "Aktive Zeitüberschreitung existiert" );
        }
        else
        {
            startCastingTV.setDisabled( false );
            startCastingTV.setRemark( null );
        }
        viewModel.addTaskValidations( startCastingTV );

        // Stop Casting
        TaskValidation coolDownSlabsTV = new TaskValidation();
        coolDownSlabsTV.setId( SecurityCasting.PROD.ACTION.COOL_DOWN_SLABS );
        if ( casterInstructionDTO == null || activeBatch == null )
        {
            coolDownSlabsTV.setDisabled( true );
            coolDownSlabsTV.setRemark( null );
        }
        else if ( castingTS == null )
        {
            coolDownSlabsTV.setDisabled( true );
            coolDownSlabsTV.setRemark( "Gießen Start noch nicht erfolgt" );
        }
        else if ( unloadingTS != null )
        {
            coolDownSlabsTV.setDisabled( true );
            coolDownSlabsTV.setRemark( "Abfahren bereits gestartet" );
        }
        else if ( timeValidationExists )
        {
            coolDownSlabsTV.setDisabled( true );
            coolDownSlabsTV.setRemark( "Aktive Zeitüberschreitung existiert" );
        }
        else
        {
            coolDownSlabsTV.setDisabled( false );
            coolDownSlabsTV.setRemark( null );
        }
        viewModel.addTaskValidations( coolDownSlabsTV );

        // Caster unload
        TaskValidation unloadCasterTV = new TaskValidation();
        unloadCasterTV.setId( SecurityCasting.PROD.ACTION.UNLOAD_CASTER );
        if ( casterInstructionDTO == null || activeBatch == null )
        {
            unloadCasterTV.setDisabled( true );
            unloadCasterTV.setRemark( null );
        }
        else if ( unloadingTS == null )
        {
            unloadCasterTV.setDisabled( true );
            unloadCasterTV.setRemark( "Barren noch nicht Entladen" );
        }
        else if ( !visualInspectionExist )
        {
            unloadCasterTV.setDisabled( true );
            unloadCasterTV.setRemark( "Die Sichtprüfung ist noch nicht durchgeführt" );
        }
        else if ( timeValidationExists )
        {
            unloadCasterTV.setDisabled( true );
            unloadCasterTV.setRemark( "Aktive Zeitüberschreitung existiert" );
        }
        else
        {
            unloadCasterTV.setDisabled( false );
            unloadCasterTV.setRemark( null );
        }
        viewModel.addTaskValidations( unloadCasterTV );

        return viewModel;
    }

    private boolean isProductionDocumentsFilledCheckEnabled()
    {
        final String enabledS = System.getProperty( "bde.validation.documents.filled.enabled", "false" );
        return enabledS != null && enabledS.equalsIgnoreCase( "true" );
    }
}