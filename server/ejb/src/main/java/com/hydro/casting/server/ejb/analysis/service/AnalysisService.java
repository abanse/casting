package com.hydro.casting.server.ejb.analysis.service;

import com.hydro.casting.server.contract.dto.*;
import com.hydro.casting.server.ejb.main.service.AlloyService;
import com.hydro.casting.server.model.mat.*;
import com.hydro.casting.server.model.mat.dao.AnalysisElementHome;
import com.hydro.casting.server.model.mat.dao.AnalysisHome;
import com.hydro.casting.server.model.sched.CastingBatch;
import com.hydro.casting.server.model.sched.dao.CastingBatchHome;
import com.hydro.eai.lims.model.LimsAlloy;
import com.hydro.eai.lims.model.LimsAnalysisElement;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class AnalysisService
{
    private final static Logger log = LoggerFactory.getLogger( AnalysisService.class );

    public static String CALCULATED_ELEMENT = "calculated";
    public final static String STATUS_FINISHED = "F";

    //@formatter:off
    private final static String ACTIVE_ANALYSIS_QUERY =
          "select ana.objid                         as rwSampleId,\n"
        + "       ana.name                          as name,\n"
        + "       ana.analysisNo                    as analysisNo,\n"
        + "       al.name                           as alloyName,\n"
        + "       al.version                        as alloyVersion,\n"
        + "       al.objid                          as alloyId,\n"
        + "       ana.charge                        as charge,\n"
        + "       ana.melter                        as melter,\n"
        + "       ana.sampleNumber                  as sampleNumber,\n"
        + "       ana.status                        as status,\n"
        + "       ana.analysisOk                    as analysisOk,\n"
        + "       ana.originalPreregistrationTime   as originalPreregistrationTime,\n"
        + "       ana.preregistrationTime           as preregistrationTime,\n"
        + "       ana.registrationTime              as registrationTime,\n"
        + "       ana.scanTime                      as scanTime,\n"
        + "       ana.objid                         as analysisId\n"
        + "from Analysis ana\n"
        + "         left outer join ana.alloy al\n"
        + "where ana.charge in (select '0' || substr(b.charge, 3, 7) from CastingBatch b where b.charge is not null and b.executionState > 200 and b.executionState < 400)";
    //@formatter:on

    @EJB
    AnalysisHome analysisHome;

    @EJB
    AlloyService alloyService;

    @EJB
    LimsAlloyService limsAlloyService;

    @EJB
    LimsAnalysisService limsAnalysisService;

    @EJB
    private CastingBatchHome castingBatchHome;

    @EJB
    private AnalysisElementHome analysisElementHome;

    @PersistenceContext( unitName = "casting" )
    private EntityManager entityManager;

    public void createOrUpdateAnalysisFromDTO( AnalysisDTO analysisDTO, Long analysisId )
    {
        Analysis analysis;
        if ( analysisId != null )
        {
            analysis = findAnalysisById( analysisId );
        }
        else
        {
            analysis = findAnalysisByDTO( analysisDTO );
        }

        if ( analysis == null )
        {
            analysis = new Analysis();
            analysisHome.persist( analysis );
        }

        updateAnalysisFromDTO( analysis, analysisDTO );

        analysisDTO.setAnalysisId( analysis.getObjid() );
        analysisDTO.setOriginalPreregistrationTime( analysis.getOriginalPreregistrationTime() );
        analysisDTO.setAnalysisOk( analysis.getAnalysisOk() );
    }

    private void updateAnalysisFromDTO( Analysis analysis, AnalysisDTO analysisDTO )
    {
        updateAnalysisNo( analysis, analysisDTO );
        updateApprovalTime( analysis, analysisDTO );
        updateCharge( analysis, analysisDTO );
        updateMelter( analysis, analysisDTO );
        updateName( analysis, analysisDTO );
        updateOriginalPreregistrationTime( analysis, analysisDTO );
        updatePreregistrationTime( analysis, analysisDTO );
        updateRegistrationTime( analysis, analysisDTO );
        updateScanTime( analysis, analysisDTO );
        updateSampleNumber( analysis, analysisDTO );
        updateStatus( analysis, analysisDTO );
        updateAlloy( analysis, analysisDTO );
        updateAnalysisElements( analysis, analysisDTO );
        updateAnalysisOk( analysis, analysisDTO );
    }

    private void updateOriginalPreregistrationTime( Analysis analysis, AnalysisDTO analysisDTO )
    {
        // Is only updated once, if no original preregistration date is present on the analysis
        if ( analysisDTO.getPreregistrationTime() != null && analysis.getOriginalPreregistrationTime() == null )
        {
            analysis.setOriginalPreregistrationTime( analysisDTO.getPreregistrationTime() );
        }
    }

    private void updateAnalysisElements( Analysis analysis, AnalysisDTO analysisDTO )
    {
        // AnalysisElements are never updated, only created once
        if ( analysisDTO.getApprovalTime() != null && ( analysis.getAnalysisElements() == null || analysis.getAnalysisElements().isEmpty() ) )
        {
            List<LimsAnalysisElement> limsAnalysisElements = limsAnalysisService.findBySampleNo( analysis.getAnalysisNo() );
            if ( limsAnalysisElements != null )
            {
                createAnalysisElementsFromLimsData( analysis, limsAnalysisElements );
            }

            // Once AnalysisElements are added, we can update the analysisOk status
            updateAnalysisOk( analysis, analysisDTO );
        }
    }

    private void updateAlloy( Analysis analysis, AnalysisDTO analysisDTO )
    {
        if ( analysisDTO.getAlloyId() != null && ( analysis.getAlloy() == null || !analysis.getAlloy().getAlloyId().equals( analysisDTO.getAlloyId() ) ) )
        {
            Alloy alloy = alloyService.findByAlloyId( analysisDTO.getAlloyId() );
            if ( alloy == null )
            {
                LimsAlloy limsAlloy = limsAlloyService.findBySampleTypeId( analysisDTO.getAlloyId() );
                alloy = alloyService.createOrUpdateAlloyFromLimsData( limsAlloy );
            }
            analysis.setAlloy( alloy );
        }
    }

    private void updateStatus( Analysis analysis, AnalysisDTO analysisDTO )
    {
        if ( analysisDTO.getStatus() != null && ( analysis.getStatus() == null || !analysis.getStatus().equals( analysisDTO.getStatus() ) ) )
        {
            analysis.setStatus( analysisDTO.getStatus() );
        }
    }

    private void updateSampleNumber( Analysis analysis, AnalysisDTO analysisDTO )
    {
        if ( analysisDTO.getSampleNumber() != null && ( analysis.getSampleNumber() == null || !analysis.getSampleNumber().equals( analysisDTO.getSampleNumber() ) ) )
        {
            analysis.setSampleNumber( analysisDTO.getSampleNumber() );
        }
    }

    private void updateScanTime( Analysis analysis, AnalysisDTO analysisDTO )
    {
        if ( analysisDTO.getScanTime() != null && ( analysis.getScanTime() == null || !analysis.getScanTime().equals( analysisDTO.getScanTime() ) ) )
        {
            analysis.setScanTime( analysisDTO.getScanTime() );
        }
    }

    private void updateRegistrationTime( Analysis analysis, AnalysisDTO analysisDTO )
    {
        if ( analysisDTO.getRegistrationTime() != null && ( analysis.getRegistrationTime() == null || !analysis.getRegistrationTime().equals( analysisDTO.getRegistrationTime() ) ) )
        {
            analysis.setRegistrationTime( analysisDTO.getRegistrationTime() );
        }
    }

    private void updatePreregistrationTime( Analysis analysis, AnalysisDTO analysisDTO )
    {
        if ( analysisDTO.getPreregistrationTime() != null && ( analysis.getPreregistrationTime() == null || !analysis.getPreregistrationTime().equals( analysisDTO.getPreregistrationTime() ) ) )
        {
            analysis.setPreregistrationTime( analysisDTO.getPreregistrationTime() );
        }
    }

    private void updateName( Analysis analysis, AnalysisDTO analysisDTO )
    {
        if ( analysisDTO.getName() != null && ( analysis.getName() == null || !analysis.getName().equals( analysisDTO.getName() ) ) )
        {
            analysis.setName( analysisDTO.getName() );
        }
    }

    private void updateMelter( Analysis analysis, AnalysisDTO analysisDTO )
    {
        if ( analysisDTO.getMelter() != null && ( analysis.getMelter() == null || !analysis.getMelter().equals( analysisDTO.getMelter() ) ) )
        {
            analysis.setMelter( analysisDTO.getMelter() );
        }
    }

    private void updateCharge( Analysis analysis, AnalysisDTO analysisDTO )
    {
        if ( analysisDTO.getCharge() != null && ( analysis.getCharge() == null || !analysis.getCharge().equals( analysisDTO.getCharge() ) ) )
        {
            analysis.setCharge( analysisDTO.getCharge() );
            // Hier noch den Schedulable besetzen, wenn vorhanden
            String charge = analysisDTO.getCharge();
            if ( charge.startsWith( "0" ) && charge.length() == 6 )
            {
                charge = charge.substring( 1 );
            }
            if ( charge.length() == 5 )
            {
                final CastingBatch castingBatch = castingBatchHome.findByChargeWithoutYear( charge );
                if ( castingBatch != null )
                {
                    analysis.setSchedulable( castingBatch );
                }
            }
        }
    }

    private void updateApprovalTime( Analysis analysis, AnalysisDTO analysisDTO )
    {
        if ( analysisDTO.getApprovalTime() != null && ( analysis.getApprovalTime() == null || !analysis.getApprovalTime().equals( analysisDTO.getApprovalTime() ) ) )
        {
            analysis.setApprovalTime( analysisDTO.getApprovalTime() );
        }
    }

    private void updateAnalysisNo( Analysis analysis, AnalysisDTO analysisDTO )
    {
        if ( analysisDTO.getAnalysisNo() != null && ( analysis.getAnalysisNo() == null || !analysis.getAnalysisNo().equals( analysisDTO.getAnalysisNo() ) ) )
        {
            analysis.setAnalysisNo( analysisDTO.getAnalysisNo() );
        }
    }

    /**
     * This function detects the "analysis-ok" status of an analysis on server side. An analysis is ok when all measured values are within the boundaries defined by the alloy.
     * The analysis-ok-status is detected and set on the analysis, unless it was not done in a previous iteration. The dto is updated with the database value on each iteration.
     * Possible outcomes for the field "analysis_ok" are:
     * - 0 if the analysis is "ok" and all values are within boundaries
     * - 1 if the analysis is "not ok" because one or more values are below the minimum boundary
     * - 2 if the analysis is "not ok" because one or more values are above the maximum boundary
     * - 3 if the analysis is "not ok" because one or more values are below the minimum boundary *and* one or more values are below the maximum boundary
     *
     * @param analysis    The analysis which is checked for "analysis ok" status
     * @param analysisDTO The according dto, which has to be updated with the status in the cache
     */
    private void updateAnalysisOk( Analysis analysis, AnalysisDTO analysisDTO )
    {
        // Only process analyses which have analysis_ok not set in the database
        if ( analysisDTO.getApprovalTime() != null && analysis.getAnalysisOk() == null )
        {
            int analysisOk = 0;
            Set<AlloyElement> alloyElements;

            if ( analysis.getAlloy() != null )
            {
                alloyElements = analysis.getAlloy().getAlloyElements();
            }
            else
            {
                alloyElements = Collections.emptySet();
            }

            for ( AnalysisElement analysisElement : analysis.getAnalysisElements() )
            {
                // Each AlloyElement should have a unique name inside an alloy, matching the element in the analysis
                AlloyElement alloyElement = alloyElements.stream().filter( ae -> ae.getName().equals( analysisElement.getName() ) ).findFirst().orElse( null );

                if ( alloyElement != null )
                {
                    // Values are rounded using the precision defined in the alloy, independently of the frontend rounding
                    Integer precision = alloyElement.getPrecision();
                    Double val = round( analysisElement.getStandardValue(), precision );

                    // If the element is not included in the analysis, it does not affect the analysis_ok status
                    if ( val != null )
                    {
                        Double maxVal = round( alloyElement.getMaxValue(), precision );
                        Double minVal = round( alloyElement.getMinValue(), precision );

                        if ( analysisOk != AnalysisDTO.ANALYSIS_LOW && minVal != null && val < minVal )
                        {
                            analysisOk += AnalysisDTO.ANALYSIS_LOW;
                        }

                        if ( analysisOk != AnalysisDTO.ANALYSIS_HIGH && maxVal != null && val > maxVal )
                        {
                            analysisOk += AnalysisDTO.ANALYSIS_HIGH;
                        }

                        // If a too-low value and a too-high value were already detected, the status cannot change further, so the loop has to be broken
                        if ( analysisOk == AnalysisDTO.ANALYSIS_LOW_HIGH )
                        {
                            break;
                        }
                    }
                }
            }

            analysis.setAnalysisOk( analysisOk );
        }
    }

    private Double round( Double value, Integer decimalPlaces )
    {
        if ( value == null || decimalPlaces == null )
        {
            return null;
        }
        return Math.round( value * Math.pow( 10, decimalPlaces ) ) / Math.pow( 10, decimalPlaces );
    }

    private void createAnalysisElementsFromLimsData( Analysis analysis, List<LimsAnalysisElement> limsAnalysisElements )
    {
        for ( LimsAnalysisElement currentLimsElement : limsAnalysisElements )
        {
            AnalysisElement analysisElement = new AnalysisElement();
            analysisElement.setName( currentLimsElement.getPaName() );
            analysisElement.setStandardValue( currentLimsElement.getNresult() );
            analysisElement.setPrecision( currentLimsElement.getResultPrecision() );
            if ( !analysis.containsInAnalysisElements( analysisElement ) )
            {
                analysis.addToAnalysisElements( analysisElement );
            }
        }
    }

    /**
     * Looks for an analysis in the database by using the data from a DTO.
     * Looks for a match in the analysisNo in case at least one result was found.
     * <p>
     * Cases:
     * 1. No analysisNo in DTO -> Finds the first analysis candidate that also has no analysisNo, otherwise returns null
     * 2. AnalysisNo in DTO -> Finds exact match in analysisNo, if nothing found, the first analysis candidate without any analysisNo, otherwise returns null
     *
     * @param analysisDTO The dto for which the according analysis should be found in the database
     * @return Exactly one analysis that matches the DTO data, otherwise null
     */
    public Analysis findAnalysisByDTO( AnalysisDTO analysisDTO )
    {
        List<Analysis> analysisList = findByParameters( analysisDTO.getPreregistrationTime(), analysisDTO.getCharge(), analysisDTO.getMelter(), analysisDTO.getSampleNumber() );
        Analysis analysis = null;

        if ( analysisList != null )
        {
            analysis = analysisList.stream().filter( analysisCandidate -> Objects.equals( analysisCandidate.getAnalysisNo(), analysisDTO.getAnalysisNo() ) ).findFirst()
                    .orElse( analysisList.stream().filter( analysisCandidate -> analysisCandidate.getAnalysisNo() == null ).findFirst().orElse( null ) );
        }

        return analysis;
    }

    public List<Analysis> findByParameters( LocalDateTime preRegTime, String charge, String melter, String sampleNumber )
    {
        return analysisHome.findByParameters( preRegTime, charge, melter, sampleNumber );
    }

    public List<Analysis> findAllAnalysesForChargeAndYear( String charge, int year )
    {
        return analysisHome.findAllByChargeAndYear( charge, year );
    }

    public Analysis findAnalysisById( Long analysisId )
    {
        return analysisHome.findById( analysisId );
    }

    public Analysis findLastFurnaceAnalysis( String charge )
    {
        return analysisHome.findLastFurnaceAnalysis( charge );
    }

    public List<AnalysisDTO> load()
    {
        final org.hibernate.query.Query<AnalysisDTO> query = entityManager.unwrap( Session.class ).createQuery( ACTIVE_ANALYSIS_QUERY, AnalysisDTO.class );
        query.setResultTransformer( new AliasToBeanResultTransformer( AnalysisDTO.class ) );
        return query.getResultList();
    }

    public AnalysisDetailDTO loadFullAnalysisDetail( List<Analysis> analysisList, String name, String specificationAlloy, boolean isLeaf )
    {
        AnalysisDetailDTO analysisDetailDTO = new AnalysisDetailDTO();
        analysisDetailDTO.setName( name );
        analysisDetailDTO.setIsLeaf( isLeaf );

        populateDtoFromAnalysisDetail( analysisList, analysisDetailDTO, specificationAlloy, true );

        return analysisDetailDTO;
    }

    public void populateDtoFromAnalysisDetail( List<Analysis> analysisList, AnalysisDetailDTO analysisDetailDTO, String specificationAlloy, boolean withAlloySpec )
    {
        log.trace( "populateDtoFromAnalysisDetail" );
        CompositionDTO minComp = new CompositionDTO();
        CompositionDTO maxComp = new CompositionDTO();

        minComp.setName( "Min" );
        maxComp.setName( "Max" );

        Alloy alloy = null;

        if ( withAlloySpec )
        {
            // If a specific alloy was provided, that alloy is used for the specification, otherwise the alloy used on the analysis should be the specification
            if ( specificationAlloy != null )
            {
                alloy = alloyService.findAlloy( specificationAlloy );
            }
            else
            {
                // Distinct stream of all alloys that were active for the samples for the current cast
                List<Alloy> alloys = analysisList.stream().map( Analysis::getAlloy ).distinct().filter( Objects::nonNull ).collect( Collectors.toList() );
                // Critical error in case there are more than one alloys (by name) during one cast
                if ( alloys.stream().map( Alloy::getName ).distinct().count() > 1 )
                {
                    throw new IllegalStateException( "Fehler beim Verarbeiten der Analysen: Nicht alle übergebenen Analysen haben die gleiche Legierung!" );
                }
                // Maximum version is the newest version of the alloy, which should be used for the whole cast in case the version changed during active casting
                alloy = alloys.stream().max( Comparator.comparing( Alloy::getVersion ) ).orElse( null );
            }

            if ( alloy != null )
            {
                minComp.setName( alloy.getName() + " Min" );
                maxComp.setName( alloy.getName() + " Max" );

                // Copying the min / max values from the database object to the composition dto
                for ( AlloyElement alloyElement : alloy.getAlloyElements() )
                {
                    CompositionElementDTO compositionElementDTOMin = createNewCompositionElementDtoInComposition( alloyElement.getName(), minComp );
                    CompositionElementDTO compositionElementDTOMax = createNewCompositionElementDtoInComposition( alloyElement.getName(), maxComp );

                    compositionElementDTOMin.setValue( alloyElement.getMinValue() );
                    compositionElementDTOMin.setPrecision( alloyElement.getPrecision() );
                    compositionElementDTOMin.setSortOrderId( alloyElement.getElementIndex() );

                    compositionElementDTOMax.setValue( alloyElement.getMaxValue() );
                    compositionElementDTOMax.setPrecision( alloyElement.getPrecision() );
                    compositionElementDTOMax.setSortOrderId( alloyElement.getElementIndex() );
                }
                minComp.getCompositionElementDTOList().sort( Comparator.comparing( CompositionElementDTO::getName ) );
                maxComp.getCompositionElementDTOList().sort( Comparator.comparing( CompositionElementDTO::getName ) );
            }
        }

        analysisDetailDTO.setMinComp( minComp );
        analysisDetailDTO.setMaxComp( maxComp );

        // List will contain all analysis in this cast / charge
        List<CompositionDTO> analysisDTOList = new ArrayList<>();

        // Copying data from analysis database objects to a composition dto each and adding it to the list of all composition dto's

        final List<Object[]> analysisElements;
        if ( !analysisList.isEmpty() )
        {
            final List<Long> analysisOIDs = analysisList.stream().map( Analysis::getObjid ).collect( Collectors.toList() );
            final Query query = entityManager.createQuery( "select ae.analysis.objid, ae.name, ae.standardValue, ae.precision from AnalysisElement ae where ae.analysis.objid in :analysisOIDs" );
            query.setParameter( "analysisOIDs", analysisOIDs );
            log.trace( "getAnalysisElements" );
            analysisElements = query.getResultList();
            log.trace( "getAnalysisElements end" );
        }
        else
        {
            analysisElements = Collections.emptyList();
        }

        for ( Analysis analysis : analysisList )
        {
            log.trace( "analysis " + analysis.getName() );
            CompositionDTO analysisComp = new CompositionDTO();

            String analysisCompName = analysis.getSampleNumber();

            // If version update of the alloy happened during ongoing cast, insert a warning string into the composition name
            if ( specificationAlloy == null && alloy != null && analysis.getAlloy() != null && alloy.getVersion() != analysis.getAlloy().getVersion() )
            {
                analysisCompName += String.format( " Versionswechsel: %s -> %s", analysis.getAlloy().getVersion(), alloy.getVersion() );
            }

            analysisComp.setName( analysisCompName );
            // Sample number is used for sorting the list with all compositions
            analysisComp.setSampleNumber( Integer.parseInt( analysis.getSampleNumber() ) );
            analysisComp.setSampleTS( analysis.getApprovalTime() );

            // Setting the weight to 1 to ensure analysis is included in average if analysis result is present and not faulty, and the sample is a client sample (between 10 and 90)
            if ( analysis.getStatus() != null && analysis.getStatus().equals( STATUS_FINISHED ) )
            {
                int sampleNumber = Integer.parseInt( analysis.getSampleNumber() );
                if ( sampleNumber > 10 && sampleNumber < 90 )
                {
                    analysisComp.setWeight( 1d );
                }
            }
            log.trace( "analysis getElements " + analysis.getName() );
            // Copying the measured data for each element from analysis database object to the composition dto
            // TODO muss man mal aufräumen, dauert sehr lange
            //            for ( AnalysisElement analysisElement : analysis.getAnalysisElements() )
            //            {
            //                CompositionElementDTO compositionElementDTO = createNewCompositionElementDtoInComposition( analysisElement.getName(), analysisComp );
            //                compositionElementDTO.setValue( analysisElement.getStandardValue() );
            //                compositionElementDTO.setPrecision( analysisElement.getPrecision() );
            //            }
            final List<Object[]> currentAnalysisElementValues = analysisElements.stream().filter( objects -> {
                if ( objects != null && objects.length > 0 )
                {
                    final Number analysisOID = (Number) objects[0];
                    return analysisOID != null && analysisOID.longValue() == analysis.getObjid();
                }
                return false;
            } ).collect( Collectors.toList() );
            for ( Object[] currentAnalysisElementValue : currentAnalysisElementValues )
            {
                if ( currentAnalysisElementValue == null || currentAnalysisElementValue.length < 4 )
                {
                    continue;
                }
                final String name = (String) currentAnalysisElementValue[1];
                final Number standardValue = (Number) currentAnalysisElementValue[2];
                final Number precision = (Number) currentAnalysisElementValue[3];
                CompositionElementDTO compositionElementDTO = createNewCompositionElementDtoInComposition( name, analysisComp );
                if ( standardValue != null )
                {
                    compositionElementDTO.setValue( standardValue.doubleValue() );
                }
                if ( precision != null )
                {
                    compositionElementDTO.setPrecision( precision.intValue() );
                }
            }

            log.trace( "analysis before alloy check " + analysis.getName() );
            // TODO: Check, if this part should be removed or changed when alloy management feature is introduced
            if ( alloy != null )
            {
                List<AlloyElement> calculatedElements = alloy.getAlloyElements().stream().filter( ae -> ae.getElementType() != null && ae.getElementType().equals( CALCULATED_ELEMENT ) )
                        .collect( Collectors.toList() );

                for ( AlloyElement calculatedElement : calculatedElements )
                {
                    CompositionElementDTO calculatedElementDTO = createNewCompositionElementDtoInComposition( calculatedElement.getName(), analysisComp );
                    calculatedElementDTO.setPrecision( calculatedElement.getPrecision() );
                    // temporary solution to ensure proper sorting
                    calculatedElementDTO.setSortOrderId( 100L );
                }
            }

            analysisComp.getCompositionElementDTOList().sort( Comparator.comparing( CompositionElementDTO::getName ) );
            analysisDTOList.add( analysisComp );
        }

        analysisDetailDTO.setAnalysisList( analysisDTOList );
    }

    private CompositionElementDTO createNewCompositionElementDtoInComposition( String name, CompositionDTO compositionDTO )
    {
        CompositionElementDTO compositionElementDTO = new CompositionElementDTO();
        compositionElementDTO.setName( name );
        compositionDTO.addToCompositionElementDTOList( compositionElementDTO );
        return compositionElementDTO;
    }

    public boolean replicateMaterialAnalysis( Material material, List<MaterialAnalysisElementDTO> newAnalysis )
    {
        // check Analyse identisch
        if ( material.getAnalysis() == null && newAnalysis.isEmpty() )
        {
            log.info( "analysis equals " + material.getName() + " (empty)" );
            return false;
        }
        else if ( material.getAnalysis() != null )
        {
            final Analysis currentAnalysis = material.getAnalysis();
            final List<MaterialAnalysisElementDTO> currentAnalysisElements = new ArrayList<>();
            currentAnalysis.getAnalysisElements()
                    .forEach( analysisElement -> currentAnalysisElements.add( new MaterialAnalysisElementDTO( analysisElement.getName(), analysisElement.getStandardValue() ) ) );
            currentAnalysisElements.sort( Comparator.comparing( MaterialAnalysisElementDTO::getName ) );
            newAnalysis.sort( Comparator.comparing( MaterialAnalysisElementDTO::getName ) );
            if ( Objects.equals( currentAnalysisElements, newAnalysis ) )
            {
                log.info( "analysis equals " + material.getName() );
                return false;
            }
        }
        log.info( "create new analysis for " + material.getName() );
        final Analysis analysis = new Analysis();
        analysis.setName( material.getName() );
        analysisHome.persist( analysis );
        material.setAnalysis( analysis );

        for ( MaterialAnalysisElementDTO materialAnalysisElementDTO : newAnalysis )
        {
            final AnalysisElement analysisElement = new AnalysisElement();
            analysisElement.setName( materialAnalysisElementDTO.getName() );
            analysisElement.setStandardValue( materialAnalysisElementDTO.getValue() );
            analysisElementHome.persist( analysisElement );
            analysis.addToAnalysisElements( analysisElement );
        }
        return true;
    }
}
