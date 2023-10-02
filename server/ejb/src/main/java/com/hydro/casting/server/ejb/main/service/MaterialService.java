package com.hydro.casting.server.ejb.main.service;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.dto.MaterialDTO;
import com.hydro.casting.server.model.mat.*;
import com.hydro.casting.server.model.mat.dao.MaterialHome;
import com.hydro.casting.server.model.mat.dao.MaterialTypeHome;
import com.hydro.casting.server.model.mat.dao.SlabHome;
import com.hydro.core.common.cache.ServerCacheManager;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.core.common.exception.BusinessObjectNotFoundException;
import com.hydro.core.common.exception.BusinessValueNotValidException;
import com.hydro.eai.cms.model.Artikel;
import com.hydro.eai.cms.model.Kundenauftrag;
import com.hydro.eai.cms.model.dao.ArtikelHome;
import com.hydro.eai.cms.model.dao.KundenauftragHome;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.transaction.TransactionSynchronizationRegistry;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
@Stateless
public class MaterialService extends BaseMaterialService<Material, MaterialDTO>
{
    private final static Logger log = LoggerFactory.getLogger( MaterialService.class );

    //@formatter:off
    private final static String QUERY =
              "SELECT m.objid as id, "
            + "       m.name as name, "
            + "       m.weight as weight, "
            + "       m.generationSuccessTS as generationSuccessTS, "
            + "       cop.objid as consumedOperation, "
            + "       m.source as source, "
            + "       mt.tags as tags, "
            + "       ana.objid as analysisObjid, "
            + "       ana.name as analysisName, "
            + "       pl.name as place, "
            + "       m.generationState as generationState, "
            + "       m.consumptionState as consumptionState, "
            + "       mt.apk as materialTypeApk, "
            + "       mt.description as materialTypeDescription, "
            + "       a.name as alloyName, "
            + "       sm.deliveryCharge as deliveryCharge "
            + "  FROM Material m left outer join m.materialType as mt left outer join m.analysis ana left outer join m.handlingUnit hu left outer join hu.place pl left outer join m.consumingOperation cop left outer join m.alloy a left outer join StockMaterial sm on m = sm ";
    private final static String ANALYSIS_QUERY =
              "SELECT ae.analysis.objid as analysisObjid, "
            + "       ae.name as name, "
            + "       ae.standardValue as standardValue "
            + "  FROM AnalysisElement ae ";

    private final static String SELECT_ALL_WHERE =
              "  WHERE hu IS NOT NULL "
            + "     OR (     m.generationState = 200 "
            + "          AND m.consumptionState = 200 "
            + "          AND cop.executionState < 400 "
            + "        )"
            + " ORDER BY m.objid";
    private final static String SELECT_ANALYSIS_ALL_WHERE =
              "  WHERE ae.analysis in ("
            + " select m.analysis "
            + "   from Material m left outer join m.handlingUnit hu left outer join m.consumingOperation cop"
            + "  where hu IS NOT NULL"
            + "     OR (     m.generationState = 200 "
            + "          AND m.consumptionState = 200 "
            + "          AND cop.executionState < 400 "
            + "        )"
            + ")";
    private final static String SELECT_WHERE =
            "    WHERE m in (:materials) "
          + " ORDER BY m.objid";
    private final static String SELECT_ANALYSIS_WHERE =
            "    WHERE ae.analysis in (select m.analysis from Material m where m in :materials)";

    //@formatter:on

    @EJB
    private MaterialTypeHome materialTypeHome;

    @EJB
    private KundenauftragHome kundenauftragHome;

    @EJB
    private ArtikelHome artikelHome;

    @EJB
    private AlloyService alloyService;

    @EJB
    private MaterialHome materialHome;

    @EJB
    private SlabHome slabHome;

    @Inject
    private ServerCacheManager serverCacheManager;

    @Resource
    private TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public MaterialService()
    {
        super( Casting.CACHE.PROD_CACHE_NAME, Casting.CACHE.PROD_VERSION_CACHE_NAME, Casting.CACHE.MATERIAL_PATH );
    }

    @Override
    protected ServerCacheManager getServerCacheManager()
    {
        return serverCacheManager;
    }

    @Override
    protected TransactionSynchronizationRegistry getTransactionSynchronizationRegistry()
    {
        return transactionSynchronizationRegistry;
    }

    @Override
    public List<MaterialDTO> load()
    {
        final long start = System.currentTimeMillis();

        final String sql = QUERY + SELECT_ALL_WHERE;
        final String analysisSql = ANALYSIS_QUERY + SELECT_ANALYSIS_ALL_WHERE;

        final Session session = entityManager.unwrap( Session.class );
        final org.hibernate.query.Query<MaterialDTO> query = session.createQuery( sql );
        query.setResultTransformer( new AliasToBeanResultTransformer( MaterialDTO.class ) );

        final List<MaterialDTO> materials = query.list();
        final long materialLoaded = System.currentTimeMillis();
        log.debug( "load " + materials.size() + " materials in " + ( materialLoaded - start ) + "ms" );

        final Query analysisQuery = session.createQuery( analysisSql, Tuple.class );

        final List<Tuple> analysisRows = analysisQuery.getResultList();

        log.debug( "load " + analysisRows.size() + " analysis in " + ( System.currentTimeMillis() - materialLoaded ) + "ms" );

        mergeAnalysis( materials, analysisRows );

        // set Shaping
        materials.forEach( materialDTO -> {
            if ( materialDTO.getGenerationState() == 200 && materialDTO.getConsumptionState() == 200 )
            {
                materialDTO.setShaping( MaterialDTO.Shaping.PlannedConsumed );
            }
            else
            {
                materialDTO.setShaping( MaterialDTO.Shaping.Consumed );
            }
        } );

        return materials;
    }

    @Override
    public List<MaterialDTO> load( Collection<Material> entities )
    {
        final String sql = QUERY + SELECT_WHERE;
        final String analysisSql = ANALYSIS_QUERY + SELECT_ANALYSIS_WHERE;
        @SuppressWarnings( "unchecked" )
        final Session session = entityManager.unwrap( Session.class );
        final org.hibernate.query.Query<MaterialDTO> query = session.createQuery( sql );
        query.setParameter( "materials", entities );
        query.setResultTransformer( new AliasToBeanResultTransformer( MaterialDTO.class ) );

        final List<MaterialDTO> materials = query.list();

        final Query analysisQuery = session.createQuery( analysisSql, Tuple.class );
        analysisQuery.setParameter( "materials", entities );

        final List<Tuple> analysisRows = analysisQuery.getResultList();

        mergeAnalysis( materials, analysisRows );

        // set Shaping
        materials.forEach( materialDTO -> {
            if ( materialDTO.getGenerationState() == 200 && materialDTO.getConsumptionState() == 200 )
            {
                materialDTO.setShaping( MaterialDTO.Shaping.PlannedConsumed );
            }
            else
            {
                materialDTO.setShaping( MaterialDTO.Shaping.Consumed );
            }
        } );

        return materials;
    }

    public MaterialType findOrCreateMaterialType( final String materialTypeApk ) throws BusinessException
    {
        MaterialType materialType = materialTypeHome.findByApk( materialTypeApk );
        if ( materialType == null )
        {
            final Kundenauftrag lastKundenauftragWithArtikelNr = kundenauftragHome.findLastKundenauftragWithArtikelNr( materialTypeApk );
            if ( lastKundenauftragWithArtikelNr == null )
            {
                throw new BusinessObjectNotFoundException( "Kundenauftrag", materialTypeApk );
            }
            materialType = new MaterialType();
            materialType.setApk( materialTypeApk );
            materialTypeHome.persist( materialType );
            materialType.setDescription( lastKundenauftragWithArtikelNr.getArtikelkurztext() );

            if ( lastKundenauftragWithArtikelNr.getArtikelkurztext() == null )
            {
                throw new BusinessValueNotValidException( "Kundenauftrag", lastKundenauftragWithArtikelNr.getAuftragsid(), "artikelkurztext", null );
            }
            // <MATERIAL_TEXT>03_8079-K_FQ_595_1450_4700_PIR</MATERIAL_TEXT>
            // GWB_99/01_FQ_122_600*1800*4200_DS_310
            // 99/01 Alloy
            // FQ Quality
            // 122 QualityCode
            // 600*1800*4200 Dimensions
            final String[] matAtributes = lastKundenauftragWithArtikelNr.getArtikelkurztext().split( "_" );
            if ( matAtributes.length < 5 )
            {
                throw new BusinessValueNotValidException( "Kundenauftrag", lastKundenauftragWithArtikelNr.getAuftragsid(), "artikelkurztext", "Struktur" );
            }
            final String category = matAtributes[0];
            final String alloyName = matAtributes[1];
            final String quality = matAtributes[2];
            final String qualityCode = matAtributes[3];
            final String dimensions = matAtributes[4];
            final String[] dimensionValues = dimensions.split( "\\*" );
            if ( dimensionValues.length != 3 )
            {
                throw new BusinessValueNotValidException( "Kundenauftrag", lastKundenauftragWithArtikelNr.getAuftragsid(), "artikelkurztext", "Dimensionen" );
            }
            double height = Double.parseDouble( dimensionValues[0] );
            double width = Double.parseDouble( dimensionValues[1] );
            double length = Double.parseDouble( dimensionValues[2] );

            final Alloy alloy = alloyService.findOrCreateAlloy( alloyName );

            materialType.setCategory( category );
            materialType.setAlloy( alloy );
            materialType.setQuality( quality );
            materialType.setQualityCode( qualityCode );
            materialType.setHeight( height );
            materialType.setWidth( width );
            materialType.setLength( length );

            // Finde doppelt lang gießen
            final Artikel artikel = artikelHome.findBySlabAttributes( alloyName, qualityCode, (int) height, (int) width, (int) length );
            if ( artikel != null && artikel.getTgiesslaenge() != null )
            {
                materialType.setAmount( 2 );
            }
            else
            {
                materialType.setAmount( 1 );
            }
        }
        return materialType;
    }

    public MaterialType findOrCreateStockMaterialType( final String materialTypeApk, final String description, final String category ) throws BusinessException
    {
        MaterialType materialType = materialTypeHome.findByApk( materialTypeApk );
        if ( materialType == null )
        {
            materialType = new MaterialType();
            materialType.setApk( materialTypeApk );
            materialTypeHome.persist( materialType );
            materialType.setDescription( description );

            materialType.setQuality( category );
            materialType.setCategory( "SM" );
        }
        return materialType;
    }

    public MaterialType find( final String materialTypeApk ) throws BusinessException
    {
        MaterialType materialType = materialTypeHome.findByApk( materialTypeApk );
        if ( materialType == null )
        {
            throw new BusinessObjectNotFoundException( "MaterialType", materialTypeApk );
        }
        return materialType;
    }

    public Material unloadMaterial( Material parent, String name, double weight )
    {
        final Material clone;
        // duplicate Material
        if ( parent instanceof TransferMaterial )
        {
            clone = new TransferMaterial();
        }
        else if ( parent instanceof SumpMaterial )
        {
            clone = new SumpMaterial();
        }
        else if ( parent instanceof StockMaterial )
        {
            clone = new StockMaterial();
        }
        else if ( parent instanceof CrucibleMaterial )
        {
            clone = new CrucibleMaterial();
        }
        else
        {
            clone = new Material();
        }
        parent.assignChild( clone );
        clone.setName( name );
        clone.setWeight( weight );
        setGenerationState( clone, Casting.SCHEDULABLE_STATE.SUCCESS );
        if ( parent.getConsumptionState() < Casting.SCHEDULABLE_STATE.IN_PROGRESS )
        {
            setConsumptionState( parent, Casting.SCHEDULABLE_STATE.IN_PROGRESS );
        }
        // TODO altes Material auf Verbraucht setzen, wenn komplett entnommen
        materialHome.persist( clone );
        return clone;
    }

    public void setGenerationState( Material material, int generationState )
    {
        setGenerationState( material, generationState, LocalDateTime.now() );
    }

    public void setGenerationState( Material material, int generationState, LocalDateTime timeStamp )
    {
        material.setGenerationState( generationState );
        if ( Casting.SCHEDULABLE_STATE.PLANNED == generationState )
        {
            material.setGenerationPlannedTS( timeStamp );
        }
        else if ( Casting.SCHEDULABLE_STATE.IN_PROGRESS == generationState )
        {
            material.setGenerationInProgressTS( timeStamp );
        }
        else if ( Casting.SCHEDULABLE_STATE.SUCCESS == generationState )
        {
            material.setGenerationSuccessTS( timeStamp );
        }
        else if ( Casting.SCHEDULABLE_STATE.FAILED == generationState )
        {
            material.setGenerationFailedTS( timeStamp );
        }
        else if ( Casting.SCHEDULABLE_STATE.CANCELED == generationState )
        {
            material.setGenerationCanceledTS( timeStamp );
        }
    }

    public void setConsumptionState( Material material, int consumptionState )
    {
        setConsumptionState( material, consumptionState, LocalDateTime.now() );
    }

    public void setConsumptionState( Material material, int consumptionState, LocalDateTime timeStamp )
    {
        material.setConsumptionState( consumptionState );
        if ( Casting.SCHEDULABLE_STATE.PLANNED == consumptionState )
        {
            material.setConsumptionPlannedTS( timeStamp );
        }
        else if ( Casting.SCHEDULABLE_STATE.IN_PROGRESS == consumptionState )
        {
            material.setConsumptionInProgressTS( timeStamp );
        }
        else if ( Casting.SCHEDULABLE_STATE.SUCCESS == consumptionState )
        {
            material.setConsumptionSuccessTS( timeStamp );
        }
        else if ( Casting.SCHEDULABLE_STATE.FAILED == consumptionState )
        {
            material.setConsumptionFailedTS( timeStamp );
        }
        else if ( Casting.SCHEDULABLE_STATE.CANCELED == consumptionState )
        {
            material.setConsumptionCanceledTS( timeStamp );
        }
    }

    public void deleteMaterial( Material material )
    {
        material.removeAllAssociations();
        materialHome.remove( material );
    }

    public Slab createSlab( String name, int generationState, int consumptionState )
    {
        final Slab slab = new Slab();
        slab.setName( name );
        setGenerationState( slab, generationState );
        setConsumptionState( slab, consumptionState );

        slabHome.persist( slab );
        return slab;
    }
}
