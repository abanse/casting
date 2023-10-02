package com.hydro.casting.gui.model;

import com.google.inject.Inject;
import com.hydro.casting.common.Casting;
import com.hydro.casting.gui.model.propagate.CasterSchedulePropagator;
import com.hydro.casting.gui.model.propagate.MaterialPropagator;
import com.hydro.casting.server.contract.dto.*;
import com.hydro.core.gui.CacheListener;
import com.hydro.core.gui.CacheManager;
import com.hydro.core.gui.model.ClientModel;
import com.hydro.core.gui.model.ClientModelDeclaration;
import javafx.beans.InvalidationListener;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.util.Callback;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@ClientModelDeclaration( id = CastingClientModel.ID )
public class CastingClientModel implements ClientModel, CacheListener
{
    private final static Logger log = LoggerFactory.getLogger( CastingClientModel.class );

    public final static String ID = "casting.model";

    public final static String SLAB_VIEW_ID = "casting.model.slab";

    public final static String MATERIAL_VIEW_ID = "casting.model.material";
    public final static String CRUCIBLE_MATERIAL_VIEW_ID = "casting.model.crucible-material";
    public final static String CASTER50_VIEW_ID = "casting.model.caster50";
    public final static String CASTER60_VIEW_ID = "casting.model.caster60";
    public final static String CASTER70_VIEW_ID = "casting.model.caster70";
    public final static String CASTER80_VIEW_ID = "casting.model.caster80";
    public final static String MELTING_KT_VIEW_ID = "casting.model.meltingKT";
    public final static String CASTING_KT_VIEW_ID = "casting.model.castingKT";
    public static final String CASTER50 = "caster50";
    public static final String CASTER60 = "caster60";
    public static final String CASTER70 = "caster70";
    public static final String CASTER80 = "caster80";
    public static final String DEMAND_ASSIGNMENT = "demandAssignment";
    public static final String SLAB_ASSIGNMENT = "slabAssignment";
    public static final String SCHEDULE_ASSIGNMENT = "scheduleAssignment";
    // Relationen
    public static final String CURRENT_FURNACE = "currentFurnace";
    public static final String CURRENT_CASTER = "currentCaster";
    public static final String MACHINE = "machine";
    public static final String MATERIAL = "material";
    // Refresh
    public static final String CASTER_REFRESH = "casterRefresh";
    // Charging Materials
    public static final String CHARGING_MATERIAL_GROUP_VIEW_ID = "casting.model.chargingMaterialGroup";
    //    public static final String CHARGING_MATERIAL_VIEW_ID = "casting.model.chargingMaterial";

    @Inject
    private CacheManager cacheManager;

    private final Map<Long, CasterDemandDTO> demandIndex = new HashMap<>();
    private final ObservableList<CasterDemandDTO> demandList = FXCollections.synchronizedObservableList( FXCollections.observableArrayList() );
    private CastingModelUpdater<CasterDemandDTO> demandModelUpdater;

    private final Map<Long, CasterScheduleDTO> casterIndex = new HashMap<>();
    private final ObservableList<CasterScheduleDTO> casterList = FXCollections.synchronizedObservableList( FXCollections.observableArrayList() );
    private final ObservableList<CasterScheduleDTO> caster50List = casterList.filtered( casterScheduleDTO -> Casting.MACHINE.CASTER_50.equals( casterScheduleDTO.getMachine() ) )
            .sorted( Comparator.comparing( CasterScheduleDTO::getExecutingSequenceIndex ) );
    private final ObservableList<CasterScheduleDTO> caster60List = casterList.filtered( casterScheduleDTO -> Casting.MACHINE.CASTER_60.equals( casterScheduleDTO.getMachine() ) )
            .sorted( Comparator.comparing( CasterScheduleDTO::getExecutingSequenceIndex ) );
    private final ObservableList<CasterScheduleDTO> caster70List = casterList.filtered( casterScheduleDTO -> Casting.MACHINE.CASTER_70.equals( casterScheduleDTO.getMachine() ) )
            .sorted( Comparator.comparing( CasterScheduleDTO::getExecutingSequenceIndex ) );
    private final ObservableList<CasterScheduleDTO> caster80List = casterList.filtered( casterScheduleDTO -> Casting.MACHINE.CASTER_80.equals( casterScheduleDTO.getMachine() ) )
            .sorted( Comparator.comparing( CasterScheduleDTO::getExecutingSequenceIndex ) );
    private CastingModelUpdater<CasterScheduleDTO> casterModelUpdater;

    private final Map<Long, MachineDTO> machineIndex = new HashMap<>();
    private final Map<String, MachineDTO> machineApkIndex = new HashMap<>();
    private final ObservableList<MachineDTO> machineList = FXCollections.observableArrayList();
    private final Callback<Collection<MachineDTO>, Object> machinePropagator = machineDTOs -> {
        machineApkIndex.clear();
        machineDTOs.forEach( machineDTO -> {
            machineApkIndex.put( machineDTO.getApk(), machineDTO );
        } );
        return null;
    };
    private CastingModelUpdater<MachineDTO> machineModelUpdater;

    private final Map<Long, SlabDTO> slabIndex = new HashMap<>();
    private final ObservableList<SlabDTO> slabList = FXCollections.synchronizedObservableList( FXCollections.observableArrayList() );
    private CastingModelUpdater<SlabDTO> slabModelUpdater;

    private final Map<Long, MaterialDTO> materialIndex = new HashMap<>();
    private final MultiValuedMap<Long, MaterialDTO> plannedConsumedMaterialScheduleIndex = new ArrayListValuedHashMap<>();
    private final ObservableList<MaterialDTO> materialList = FXCollections.observableArrayList();
    private CastingModelUpdater<MaterialDTO> materialModelUpdater;
    private final MaterialPropagator<MaterialDTO> materialPropagator = new MaterialPropagator<>( this, "Lagermaterial" );

    private final Map<Long, CrucibleMaterialDTO> crucibleMaterialIndex = new HashMap<>();
    private final ObservableList<CrucibleMaterialDTO> crucibleMaterialList = FXCollections.observableArrayList();
    private CastingModelUpdater<CrucibleMaterialDTO> crucibleMaterialModelUpdater;
    private final MaterialPropagator<CrucibleMaterialDTO> crucibleMaterialPropagator = new MaterialPropagator<>( this, "Tiegel" );

    private final ObservableMap<SlabDTO, Long> slabDemandAssignment = FXCollections.synchronizedObservableMap( FXCollections.observableHashMap() );
    private final ObservableMap<Long, List<SlabDTO>> demandslabAssignment = FXCollections.synchronizedObservableMap( FXCollections.observableHashMap() );
    private final ObservableMap<Long, List<CasterSchedulePosDTO>> demandCasterSchedulePosAssignment = FXCollections.synchronizedObservableMap( FXCollections.observableHashMap() );
    private MaterialAssignmentUpdater materialAssignmentUpdater;

    private final Map<Long, MaterialGroup> materialGroupIndex = new HashMap<>();
    private final ObservableList<MaterialGroup> materialGroupList = FXCollections.observableArrayList();

    private final Map<Long, Caster> casterModelIndex = new HashMap<>();
    private final Map<Long, Furnace> furnaceIndex = new HashMap<>();
    private final LongProperty casterRefresh = new SimpleLongProperty();
    private final Callback<Collection<CasterScheduleDTO>, Object> casterPropagator = new CasterSchedulePropagator( this );

    private final Map<Long, MeltingKTDTO> meltingKTIndex = new HashMap<>();
    private final ObservableList<MeltingKTDTO> meltingKTList = FXCollections.observableArrayList();
    private CastingModelUpdater<MeltingKTDTO> meltingKTModelUpdater;

    private final Map<Long, CastingKTDTO> castingKTIndex = new HashMap<>();
    private final ObservableList<CastingKTDTO> castingKTList = FXCollections.observableArrayList();
    private CastingModelUpdater<CastingKTDTO> castingKTModelUpdater;

    private final Map<Long, MachineCalendarDTO> machineCalendarIndex = new HashMap<>();
    private final MultiValuedMap<String, MachineCalendarDTO> machineCalendarMachineIndex = new ArrayListValuedHashMap<>();
    private final ObservableList<MachineCalendarDTO> machineCalendarList = FXCollections.observableArrayList();
    private CastingModelUpdater<MachineCalendarDTO> machineCalendarModelUpdater;

    //    private final Map<Long, Material> chargingMaterialIndex = new HashMap<>();
    //    private final ObservableList<Material> chargingMaterialList = FXCollections.observableArrayList();

    @Override
    public void start()
    {
        log.trace( "start" );

        cacheManager.addCacheListener( Casting.CACHE.PLANNING_VERSION_CACHE_NAME, Casting.CACHE.MACHINE_DATA_PATH + "/version", this );
        machineModelUpdater = new CastingModelUpdater<>( cacheManager, Casting.CACHE.PLANNING_CACHE_NAME, machineIndex, machineList, Casting.CACHE.MACHINE_DATA_PATH, machinePropagator );
        machineModelUpdater.start();
        machineModelUpdater.setFinishedCallback( param -> {
            casterModelUpdater.trigger();
            return null;
        } );

        materialAssignmentUpdater = new MaterialAssignmentUpdater( slabList, demandList, casterList, slabDemandAssignment, demandslabAssignment, demandCasterSchedulePosAssignment );
        materialAssignmentUpdater.start();

        cacheManager.addCacheListener( Casting.CACHE.PLANNING_VERSION_CACHE_NAME, Casting.CACHE.CASTER_DEMAND_DATA_PATH + "/version", this );
        demandModelUpdater = new CastingModelUpdater<>( cacheManager, Casting.CACHE.PLANNING_CACHE_NAME, demandIndex, demandList, Casting.CACHE.CASTER_DEMAND_DATA_PATH, null );
        demandModelUpdater.start();
        demandModelUpdater.setFinishedCallback( ( nothing ) -> {
            materialAssignmentUpdater.trigger();
            return null;
        } );

        cacheManager.addCacheListener( Casting.CACHE.PLANNING_VERSION_CACHE_NAME, Casting.CACHE.CASTER_SCHEDULE_DATA_PATH + "/version", this );
        casterModelUpdater = new CastingModelUpdater<>( cacheManager, Casting.CACHE.PLANNING_CACHE_NAME, casterIndex, casterList, Casting.CACHE.CASTER_SCHEDULE_DATA_PATH, casterPropagator );
        casterModelUpdater.start();
        casterModelUpdater.setFinishedCallback( ( nothing ) -> {
            materialAssignmentUpdater.trigger();
            return null;
        } );

        cacheManager.addCacheListener( Casting.CACHE.PLANNING_VERSION_CACHE_NAME, Casting.CACHE.SLAB_PATH + "/version", this );
        slabModelUpdater = new CastingModelUpdater<>( cacheManager, Casting.CACHE.PLANNING_CACHE_NAME, slabIndex, slabList, Casting.CACHE.SLAB_PATH, null );
        slabModelUpdater.start();
        slabModelUpdater.setFinishedCallback( ( nothing ) -> {
            materialAssignmentUpdater.trigger();
            return null;
        } );

        cacheManager.addCacheListener( Casting.CACHE.PROD_VERSION_CACHE_NAME, Casting.CACHE.MATERIAL_PATH + "/version", this );
        materialModelUpdater = new CastingModelUpdater<>( cacheManager, Casting.CACHE.PROD_CACHE_NAME, materialIndex, materialList, Casting.CACHE.MATERIAL_PATH, materialList -> {
            materialPropagator.call( materialList );
            final MultiValuedMap<Long, MaterialDTO> oldPlannedConsumedMaterialScheduleIndex = new ArrayListValuedHashMap<>( plannedConsumedMaterialScheduleIndex );
            plannedConsumedMaterialScheduleIndex.clear();

            materialList.forEach( materialDTO -> {
                if ( materialDTO.getShaping() == MaterialDTO.Shaping.PlannedConsumed )
                {
                    plannedConsumedMaterialScheduleIndex.put( materialDTO.getConsumedOperation(), materialDTO );
                }
            } );

            return !Objects.equals( oldPlannedConsumedMaterialScheduleIndex, plannedConsumedMaterialScheduleIndex );
        } );
        materialModelUpdater.setFinishedCallback( param -> {
            if ( param instanceof Boolean && (Boolean) param )
            {
                casterModelUpdater.trigger();
            }
            return null;
        } );
        materialModelUpdater.start();

        cacheManager.addCacheListener( Casting.CACHE.PROD_VERSION_CACHE_NAME, Casting.CACHE.CRUCIBLE_MATERIAL_PATH + "/version", this );
        crucibleMaterialModelUpdater = new CastingModelUpdater<>( cacheManager, Casting.CACHE.PROD_CACHE_NAME, crucibleMaterialIndex, crucibleMaterialList, Casting.CACHE.CRUCIBLE_MATERIAL_PATH,
                crucibleMaterialPropagator );
        crucibleMaterialModelUpdater.start();

        cacheManager.addCacheListener( Casting.CACHE.PLANNING_VERSION_CACHE_NAME, Casting.CACHE.MELTING_KT_DATA_PATH + "/version", this );
        meltingKTModelUpdater = new CastingModelUpdater<>( cacheManager, Casting.CACHE.PLANNING_CACHE_NAME, meltingKTIndex, meltingKTList, Casting.CACHE.MELTING_KT_DATA_PATH, null );
        meltingKTModelUpdater.start();
        meltingKTModelUpdater.setFinishedCallback( param -> {
            casterModelUpdater.trigger();
            return null;
        } );

        cacheManager.addCacheListener( Casting.CACHE.PLANNING_VERSION_CACHE_NAME, Casting.CACHE.CASTING_KT_DATA_PATH + "/version", this );
        castingKTModelUpdater = new CastingModelUpdater<>( cacheManager, Casting.CACHE.PLANNING_CACHE_NAME, castingKTIndex, castingKTList, Casting.CACHE.CASTING_KT_DATA_PATH, null );
        castingKTModelUpdater.start();
        castingKTModelUpdater.setFinishedCallback( param -> {
            casterModelUpdater.trigger();
            return null;
        } );

        cacheManager.addCacheListener( Casting.CACHE.PLANNING_VERSION_CACHE_NAME, Casting.CACHE.MACHINE_CALENDAR_DATA_PATH + "/version", this );
        machineCalendarModelUpdater = new CastingModelUpdater<>( cacheManager, Casting.CACHE.PLANNING_CACHE_NAME, machineCalendarIndex, machineCalendarList, Casting.CACHE.MACHINE_CALENDAR_DATA_PATH,
                param -> {
                    machineCalendarMachineIndex.clear();
                    param.forEach( machineCalendarDTO -> {
                        machineCalendarMachineIndex.put( machineCalendarDTO.getMachine(), machineCalendarDTO );
                    } );
                    return null;
                } );
        machineCalendarModelUpdater.start();
        machineCalendarModelUpdater.setFinishedCallback( param -> {
            casterModelUpdater.trigger();
            return null;
        } );
    }

    @Override
    public void stop()
    {
        log.trace( "stop" );

        try
        {
            cacheManager.removeCacheListener( this );

            demandModelUpdater.stop();
            casterModelUpdater.stop();
            machineModelUpdater.stop();
            slabModelUpdater.stop();
            materialModelUpdater.stop();
            crucibleMaterialModelUpdater.stop();
            materialAssignmentUpdater.stop();
            meltingKTModelUpdater.stop();
            castingKTModelUpdater.stop();
            machineCalendarModelUpdater.stop();

            unload( demandIndex, demandList );
            unload( casterIndex, casterList );
            unload( machineIndex, machineList );
            unload( slabIndex, slabList );
            unload( materialIndex, materialList );
            unload( crucibleMaterialIndex, crucibleMaterialList );
            unload( meltingKTIndex, meltingKTList );
            unload( castingKTIndex, castingKTList );
            unload( machineCalendarIndex, machineCalendarList );

            machineApkIndex.clear();
            materialGroupIndex.clear();
            materialGroupList.clear();
            casterModelIndex.clear();
            slabDemandAssignment.clear();
            demandslabAssignment.clear();
            demandCasterSchedulePosAssignment.clear();
            machineCalendarMachineIndex.clear();

            //            chargingMaterialIndex.clear();
            //            chargingMaterialList.clear();
        }
        catch ( Exception ex )
        {
            log.info( "error in close", ex );
        }
    }

    @Override
    public void restart()
    {
        stop();
        start();
    }

    @Override
    public <S> ObservableList<S> getView( String viewId )
    {
        if ( CASTER50_VIEW_ID.equals( viewId ) )
        {
            return (ObservableList<S>) caster50List;
        }
        if ( CASTER60_VIEW_ID.equals( viewId ) )
        {
            return (ObservableList<S>) caster60List;
        }
        if ( CASTER70_VIEW_ID.equals( viewId ) )
        {
            return (ObservableList<S>) caster70List;
        }
        if ( CASTER80_VIEW_ID.equals( viewId ) )
        {
            return (ObservableList<S>) caster80List;
        }
        if ( SLAB_VIEW_ID.equals( viewId ) )
        {
            return (ObservableList<S>) slabList;
        }
        if ( MATERIAL_VIEW_ID.equals( viewId ) )
        {
            return (ObservableList<S>) materialList;
        }
        if ( CRUCIBLE_MATERIAL_VIEW_ID.equals( viewId ) )
        {
            return (ObservableList<S>) crucibleMaterialList;
        }
        if ( MELTING_KT_VIEW_ID.equals( viewId ) )
        {
            return (ObservableList<S>) meltingKTList;
        }
        if ( CASTING_KT_VIEW_ID.equals( viewId ) )
        {
            return (ObservableList<S>) castingKTList;
        }
        if ( CHARGING_MATERIAL_GROUP_VIEW_ID.equals( viewId ) )
        {
            return (ObservableList<S>) materialGroupList;
        }
        //        if ( CHARGING_MATERIAL_VIEW_ID.equals( viewId ) )
        //        {
        //            return (ObservableList<S>) chargingMaterialList;
        //        }
        return null;
    }

    @Override
    public <E> E getEntity( Class<E> type, String identString )
    {
        if ( identString == null )
        {
            return null;
        }
        if ( type == MaterialGroup.class )
        {
            MaterialGroup materialGroup = materialGroupIndex.get( (long) identString.hashCode() );
            if ( materialGroup == null )
            {
                materialGroup = new MaterialGroup();
                materialGroup.setName( identString );
                materialGroupIndex.put( (long) identString.hashCode(), materialGroup );
                materialGroupList.add( materialGroup );
            }
            return (E) materialGroup;
        }
        if ( type == Caster.class )
        {
            Caster caster = casterModelIndex.get( (long) identString.hashCode() );
            if ( caster == null )
            {
                caster = new Caster( this );
                caster.setName( identString );
                casterModelIndex.put( (long) identString.hashCode(), caster );
            }
            return (E) caster;
        }
        if ( type == Furnace.class )
        {
            Furnace furnace = furnaceIndex.get( (long) identString.hashCode() );
            if ( furnace == null )
            {
                furnace = new Furnace( identString );
                furnace.setName( identString );
                furnaceIndex.put( (long) identString.hashCode(), furnace );
            }
            return (E) furnace;
        }
        if ( type == MachineDTO.class )
        {
            return (E) machineApkIndex.get( identString );
        }
        if ( type == MaterialDTO.class )
        {
            return (E) materialIndex.get( identString.hashCode() );
        }
        return getEntity( type, identString.hashCode() );
    }

    @Override
    public <E> E getEntity( Class<E> type, long id )
    {
        if ( type == CasterScheduleDTO.class )
        {
            return (E) casterIndex.get( id );
        }
        if ( type == Material.class )
        {
            Material material = null;
            for ( MaterialGroup materialGroup : materialGroupIndex.values() )
            {
                final Optional<Material> materialOptional = materialGroup.getMaterials().stream().filter( m -> m.getObjid() == id ).findFirst();
                if ( materialOptional.isPresent() )
                {
                    material = materialOptional.get();
                    break;
                }
            }
            return (E) material;
        }
        if ( type == MaterialDTO.class )
        {
            E m = (E) materialIndex.get( id );
            if ( m == null )
            {
                m = (E) crucibleMaterialIndex.get( id );
            }
            return m;
        }
        return null;
    }

    @Override
    public <E, R> R getRelatedEntity( E source, String path )
    {
        if ( source == null )
        {
            return null;
        }
        if ( source instanceof CasterScheduleDTO )
        {
            if ( CURRENT_FURNACE.equals( path ) )
            {
                return (R) machineApkIndex.get( ( (CasterScheduleDTO) source ).getMeltingFurnace() );
            }
            if ( CURRENT_CASTER.equals( path ) )
            {
                return (R) machineApkIndex.get( ( (CasterScheduleDTO) source ).getMachine() );
            }
        }
        if ( source instanceof SlabDTO )
        {
            if ( DEMAND_ASSIGNMENT.equals( path ) )
            {
                final Long demandAssignment = slabDemandAssignment.get( source );
                if ( demandAssignment != null )
                {
                    return (R) demandIndex.get( demandAssignment );
                }
            }
        }
        if ( source instanceof CasterDemandDTO )
        {
            if ( SLAB_ASSIGNMENT.equals( path ) )
            {
                return (R) demandslabAssignment.get( ( (CasterDemandDTO) source ).getId() );
            }
            if ( SCHEDULE_ASSIGNMENT.equals( path ) )
            {
                return (R) demandCasterSchedulePosAssignment.get( ( (CasterDemandDTO) source ).getId() );
            }
        }
        return null;
    }

    private <S> void unload( Map<Long, S> index, ObservableList<S> list )
    {
        if ( index != null )
        {
            index.clear();
        }
        list.clear();
    }

    @Override
    public void cacheEntryModified( String key )
    {
        // Achtung!!!: Namen die gleich anfangen, den konkreten als erstes abfragen
        // Beispiel: machine-calender <-> machine
        if ( key.startsWith( Casting.CACHE.CASTER_DEMAND_DATA_PATH ) )
        {
            demandModelUpdater.trigger();
        }
        if ( key.startsWith( Casting.CACHE.CASTER_SCHEDULE_DATA_PATH ) )
        {
            casterModelUpdater.trigger();
        }
        else if ( key.startsWith( Casting.CACHE.MACHINE_CALENDAR_DATA_PATH ) )
        {
            machineCalendarModelUpdater.trigger();
        }
        else if ( key.startsWith( Casting.CACHE.MACHINE_DATA_PATH ) )
        {
            machineModelUpdater.trigger();
        }
        else if ( key.startsWith( Casting.CACHE.SLAB_PATH ) )
        {
            slabModelUpdater.trigger();
        }
        else if ( key.startsWith( Casting.CACHE.MATERIAL_PATH ) )
        {
            materialModelUpdater.trigger();
        }
        else if ( key.startsWith( Casting.CACHE.CRUCIBLE_MATERIAL_PATH ) )
        {
            crucibleMaterialModelUpdater.trigger();
        }
        else if ( key.startsWith( Casting.CACHE.MELTING_KT_DATA_PATH ) )
        {
            meltingKTModelUpdater.trigger();
        }
        else if ( key.startsWith( Casting.CACHE.CASTING_KT_DATA_PATH ) )
        {
            castingKTModelUpdater.trigger();
        }
        else
        {
            log.warn( "replication key not known " + key );
        }
    }

    @Override
    public void addRelationListener( String relation, InvalidationListener listener )
    {
        if ( relation == null )
        {
            return;
        }
        switch ( relation )
        {
        case CASTER50:
            caster50List.addListener( listener );
            break;
        case CASTER60:
            caster60List.addListener( listener );
            break;
        case CASTER70:
            caster70List.addListener( listener );
            break;
        case CASTER80:
            caster80List.addListener( listener );
            break;
        case DEMAND_ASSIGNMENT:
            slabDemandAssignment.addListener( listener );
            break;
        case SLAB_ASSIGNMENT:
            demandslabAssignment.addListener( listener );
            break;
        case SCHEDULE_ASSIGNMENT:
            demandCasterSchedulePosAssignment.addListener( listener );
            break;
        case CASTER_REFRESH:
            casterRefresh.addListener( listener );
            break;
        case MACHINE:
            machineList.addListener( listener );
            break;
        case MATERIAL:
            materialList.addListener( listener );
            break;
        default:
            log.warn( "relation " + relation + " not found" );
        }
    }

    @Override
    public void removeRelationListener( InvalidationListener listener )
    {
        caster50List.removeListener( listener );
        caster60List.removeListener( listener );
        caster70List.removeListener( listener );
        caster80List.removeListener( listener );
        slabDemandAssignment.removeListener( listener );
        demandslabAssignment.removeListener( listener );
        demandCasterSchedulePosAssignment.removeListener( listener );
        casterRefresh.removeListener( listener );
        materialList.removeListener( listener );
    }

    public long getDuration( Batch batch, Transfer transfer, String category, long defaultDuration )
    {
        if ( batch == null )
        {
            return defaultDuration;
        }
        if ( "melting".equals( category ) )
        {
            final CasterScheduleDTO casterScheduleDTO = getEntity( CasterScheduleDTO.class, batch.getObjid() );
            if ( casterScheduleDTO == null )
            {
                return 0;
            }
            if ( casterScheduleDTO.getPlannedMeltingDuration() != null )
            {
                return casterScheduleDTO.getPlannedMeltingDuration();
            }
            String furnace = "*";
            if ( transfer != null )
            {
                furnace = transfer.getFrom().getName();
            }
            final MeltingKTDTO bestMatch = findMeltingKnowledge( batch.getCaster().getName(), furnace, batch.getAlloy() );
            final MachineDTO machineDTO = getEntity( MachineDTO.class, furnace );
            final Caster caster = getEntity( Caster.class, batch.getCaster().getName() );
            if ( bestMatch != null && machineDTO != null && caster != null && transfer != null && casterScheduleDTO != null )
            {
                // berechne Kaltmetalleinsatzmenge
                final double bottomWeight = transfer.getBottomWeight();
                final double percentageBottomWeight = bottomWeight / machineDTO.getMaxWeight() * 100.0;

                double alloySourcePercentage = percentageBottomWeight;
                for ( String source : Casting.ALLOY_SOURCES.ALL )
                {
                    final double percentage = getPercentage( casterScheduleDTO, source );
                    alloySourcePercentage = alloySourcePercentage + percentage;
                }

                final double coldMetalInputPercentage = 100 - alloySourcePercentage;
                final double coldMetalWeight = machineDTO.getMaxWeight() * coldMetalInputPercentage / 100;
                // berechne Schmelzzeit
                final double durationMelting = ( ( coldMetalWeight / 1000.0 ) / bestMatch.getMeltingCapacity() ) * 60;

                // + handlingsZeit
                final long calcDuration = (long) ( durationMelting + ( bestMatch.getHandlingTime() * 60 ) );
                if ( calcDuration < 1 )
                {
                    return defaultDuration;
                }
                return calcDuration;
            }
        }
        else if ( "melting.busy".equals( category ) )
        {
            String furnace = "*";
            if ( transfer != null )
            {
                furnace = transfer.getFrom().getName();
            }
            final MeltingKTDTO bestMatch = findMeltingKnowledge( batch.getCaster().getName(), furnace, batch.getAlloy() );
            if ( bestMatch != null )
            {
                return (long) ( bestMatch.getStandingTime() * 60 );
            }
            return defaultDuration;
        }
        else if ( "casting".equals( category ) )
        {
            final CasterScheduleDTO casterScheduleDTO = getEntity( CasterScheduleDTO.class, batch.getObjid() );
            if ( casterScheduleDTO == null )
            {
                return 0;
            }
            if ( casterScheduleDTO.getPlannedCastingDuration() != null )
            {
                return casterScheduleDTO.getPlannedCastingDuration();
            }
            final CastingKTDTO bestMatch = findCastingKnowledge( batch.getCaster().getName(), batch.getAlloy() );
            if ( bestMatch != null )
            {
                // berechne giesszeit anhand der giessgeschwindigkeit
                final double castingLength = batch.getCastingLength(); // mm/min
                final double durationCasting = castingLength / bestMatch.getCastingSpeed();
                // + handlingsZeit
                final long calcDuration = (long) ( durationCasting + ( bestMatch.getHandlingTime() * 60 ) );
                if ( calcDuration < 1 )
                {
                    return defaultDuration;
                }
                return calcDuration;
            }
            return defaultDuration;
        }
        else if ( "casting.busy".equals( category ) )
        {
            final CastingKTDTO bestMatch = findCastingKnowledge( batch.getCaster().getName(), batch.getAlloy() );
            if ( bestMatch != null )
            {
                return (long) ( bestMatch.getStandingTime() * 60 );
            }
            return defaultDuration;
        }
        log.error( "TODO calc duration for " + category + " " + batch.getName() );
        return defaultDuration;
    }

    public double getContentPercentage( final String caster, final String furnace, final String alloy, final String source )
    {
        final MeltingKTDTO bestMatch = findMeltingKnowledge( caster, furnace, alloy );
        if ( bestMatch == null )
        {
            return 0.0;
        }
        if ( Casting.ALLOY_SOURCES.MELTING_FURNACE_S1.equals( source ) )
        {
            return bestMatch.getPercentageS1();
        }
        if ( Casting.ALLOY_SOURCES.MELTING_FURNACE_S2.equals( source ) )
        {
            return bestMatch.getPercentageS2();
        }
        if ( Casting.ALLOY_SOURCES.UBC_S3.equals( source ) )
        {
            return bestMatch.getPercentageS3();
        }
        if ( Casting.ALLOY_SOURCES.ELEKTROLYSE.equals( source ) )
        {
            return bestMatch.getPercentageEL();
        }
        if ( Casting.ALLOY_SOURCES.REAL_ALLOY.equals( source ) )
        {
            return bestMatch.getPercentageRA();
        }
        return 0.0;
    }

    //    public void addChargingMaterial( Material mat )
    //    {
    //        chargingMaterialIndex.put( Long.valueOf( mat.getName().hashCode() ), mat );
    //        chargingMaterialList.add( mat );
    //    }

    public void refreshCaster()
    {
        casterRefresh.setValue( casterRefresh.get() + 1 );
    }

    public MultiValuedMap<String, MachineCalendarDTO> getMachineCalendarMachineIndex()
    {
        return machineCalendarMachineIndex;
    }

    public MultiValuedMap<Long, MaterialDTO> getPlannedConsumedMaterialScheduleIndex()
    {
        return plannedConsumedMaterialScheduleIndex;
    }

    private MeltingKTDTO findMeltingKnowledge( final String caster, final String furnace, final String alloy )
    {
        final Optional<MeltingKTDTO> firstElement = meltingKTList.stream().filter( meltingKTDTO -> {
            if ( !( Objects.equals( meltingKTDTO.getCaster(), "*" ) || Objects.equals( meltingKTDTO.getCaster(), caster ) ) )
            {
                return false;
            }
            if ( !( Objects.equals( meltingKTDTO.getFurnace(), "*" ) || Objects.equals( meltingKTDTO.getFurnace(), furnace ) ) )
            {
                return false;
            }
            if ( !( Objects.equals( meltingKTDTO.getAlloy(), "*" ) || Objects.equals( meltingKTDTO.getAlloy(), alloy ) ) )
            {
                return false;
            }
            return true;
        } ).min( Comparator.comparingInt( MeltingKTDTO::getPrio ) );
        return firstElement.orElse( null );
    }

    private CastingKTDTO findCastingKnowledge( final String caster, final String alloy )
    {
        final Optional<CastingKTDTO> firstElement = castingKTList.stream().filter( castingKTDTO -> {
            if ( !( Objects.equals( castingKTDTO.getCaster(), "*" ) || Objects.equals( castingKTDTO.getCaster(), caster ) ) )
            {
                return false;
            }
            if ( !( Objects.equals( castingKTDTO.getAlloy(), "*" ) || Objects.equals( castingKTDTO.getAlloy(), alloy ) ) )
            {
                return false;
            }
            return true;
        } ).min( Comparator.comparingInt( CastingKTDTO::getPrio ) );
        return firstElement.orElse( null );
    }

    private double getPercentage( CasterScheduleDTO casterScheduleDTO, String source )
    {
        if ( casterScheduleDTO.getId() < 0 )
        {
            return 0;
        }
        Double concreteValue = null;
        if ( Casting.ALLOY_SOURCES.MELTING_FURNACE_S1.equals( source ) )
        {
            concreteValue = casterScheduleDTO.getPercentageS1();
        }
        else if ( Casting.ALLOY_SOURCES.MELTING_FURNACE_S2.equals( source ) )
        {
            concreteValue = casterScheduleDTO.getPercentageS2();
        }
        else if ( Casting.ALLOY_SOURCES.UBC_S3.equals( source ) )
        {
            concreteValue = casterScheduleDTO.getPercentageS3();
        }
        else if ( Casting.ALLOY_SOURCES.ELEKTROLYSE.equals( source ) )
        {
            concreteValue = casterScheduleDTO.getPercentageEL();
        }
        else if ( Casting.ALLOY_SOURCES.REAL_ALLOY.equals( source ) )
        {
            concreteValue = casterScheduleDTO.getPercentageRA();
        }
        return Objects.requireNonNullElseGet( concreteValue,
                () -> getContentPercentage( casterScheduleDTO.getMachine(), casterScheduleDTO.getMeltingFurnace(), casterScheduleDTO.getAlloy(), source ) );
    }
}