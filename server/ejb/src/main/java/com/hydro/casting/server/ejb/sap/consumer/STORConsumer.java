package com.hydro.casting.server.ejb.sap.consumer;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.ejb.main.service.AlloyService;
import com.hydro.casting.server.ejb.main.service.MaterialService;
import com.hydro.casting.server.ejb.wms.service.PlaceService;
import com.hydro.casting.server.model.mat.*;
import com.hydro.casting.server.model.mat.dao.AnalysisElementHome;
import com.hydro.casting.server.model.mat.dao.AnalysisHome;
import com.hydro.casting.server.model.mat.dao.MaterialCharacteristicHome;
import com.hydro.casting.server.model.mat.dao.StockMaterialHome;
import com.hydro.casting.server.model.wms.Place;
import com.hydro.casting.server.model.wms.dao.PlaceHome;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.core.common.util.StringTools;
import com.hydro.eai.sap.contract.consumer.SAPMessageListener;
import com.hydro.eai.sap.contract.message.SAPMessage;
import com.hydro.eai.sap.contract.message.STOR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Stateless( name = "STOR" )
public class STORConsumer implements SAPMessageListener
{
    private final static Logger log = LoggerFactory.getLogger( STORConsumer.class );

    @EJB
    private MaterialService materialService;

    @EJB
    private StockMaterialHome stockMaterialHome;

    @EJB
    private MaterialCharacteristicHome materialCharacteristicHome;

    @EJB
    private AlloyService alloyService;

    @EJB
    private AnalysisHome analysisHome;

    @EJB
    private AnalysisElementHome analysisElementHome;

    @EJB
    private PlaceService placeService;

    @EJB
    private PlaceHome placeHome;

    @Override
    public String getConsumerName()
    {
        return "STOR";
    }

    @Override
    public void consume( Map<String, String> context, SAPMessage message ) throws BusinessException
    {
        log.debug( "STOR " + message.getArt() );

        final STOR stor = new STOR();
        stor.fillMessage( message.getBody().getBytes() );

        final MaterialType materialType = materialService.findOrCreateStockMaterialType( stor.getMatNr(), StringTools.uncomment( stor.getMaKtx() ), stor.getMatKl() );

        // check Legierung
        if ( materialType.getAlloy() == null )
        {
            final Alloy alloy = alloyService.findAlloy( stor.getCharg() );
            if ( alloy != null )
            {
                materialType.setAlloy( alloy );
            }
        }

        final String stockMaterialName = stor.getMatNr() + "-" + stor.getCharg();
        final Optional<StockMaterial> stockMaterials = stockMaterialHome.findByName( stockMaterialName ).stream().findFirst();
        final StockMaterial stockMaterial;
        if ( stockMaterials.isPresent() )
        {
            stockMaterial = stockMaterials.get();
        }
        else
        {
            stockMaterial = new StockMaterial();
            stockMaterial.setName( stockMaterialName );
            stockMaterialHome.persist( stockMaterial );
        }
        // merkmale separat speichern
        stockMaterial.setReplicationTS( LocalDateTime.now() );
        stockMaterial.setDeliveryCharge( stor.getCharg() );
        stockMaterial.setWeight( stor.getcLabs() );
        stockMaterial.setSource( stor.getLgOrt() );
        stockMaterial.setMaterialType( materialType );
        stockMaterial.setGenerationState( Casting.SCHEDULABLE_STATE.SUCCESS );
        stockMaterial.setGenerationSuccessTS( stor.getZDateTS() );

        final String elements = stor.getElements();
        if ( StringTools.isNullOrEmpty( elements ) )
        {
            if ( stockMaterial.numberOfCharacteristics() > 0 )
            {
                final List<MaterialCharacteristic> characteristics = new ArrayList( stockMaterial.getCharacteristics() );
                characteristics.stream().forEach( materialCharacteristic -> {
                    materialCharacteristic.removeAllAssociations();
                    materialCharacteristicHome.remove( materialCharacteristic );
                } );
            }
        }
        else
        {
            final String[] elementContent = elements.split( ";" );
            final Iterator<String> elementIter = Arrays.stream( elementContent ).iterator();
            final List<String> existingNames = new ArrayList<>();
            while ( elementIter.hasNext() )
            {
                final String name = elementIter.next();
                if ( !elementIter.hasNext() )
                {
                    continue;
                }
                final String description = elementIter.next();
                if ( !elementIter.hasNext() )
                {
                    continue;
                }
                final String valueString = elementIter.next();
                if ( !elementIter.hasNext() )
                {
                    continue;
                }
                final String unit = elementIter.next();
                if ( !elementIter.hasNext() )
                {
                    continue;
                }
                final String valueFormat = elementIter.next();

                MaterialCharacteristic materialCharacteristic = null;
                for ( MaterialCharacteristic characteristic : stockMaterial.getCharacteristics() )
                {
                    if ( Objects.equals( characteristic.getName(), name ) )
                    {
                        materialCharacteristic = characteristic;
                        break;
                    }
                }
                if ( materialCharacteristic == null )
                {
                    materialCharacteristic = new MaterialCharacteristic();
                    materialCharacteristic.setName( name );
                    materialCharacteristicHome.persist( materialCharacteristic );
                    stockMaterial.addToCharacteristics( materialCharacteristic );
                }
                materialCharacteristic.setDescription( StringTools.uncomment( description ) );
                final double value;
                if ( valueString.contains( "," ) )
                {
                    value = Double.valueOf( valueString.replace( ',', '.' ) );
                }
                else
                {
                    value = Double.valueOf( valueString );
                }
                materialCharacteristic.setValue( value );
                materialCharacteristic.setUnit( unit );
                materialCharacteristic.setValueFormat( valueFormat );

                existingNames.add( name );
            }
            for ( MaterialCharacteristic characteristic : new ArrayList<>( stockMaterial.getCharacteristics() ) )
            {
                if ( !existingNames.contains( characteristic.getName() ) )
                {
                    characteristic.removeAllAssociations();
                    materialCharacteristicHome.remove( characteristic );
                }
            }
        }

        if ( stockMaterial.getAnalysis() == null )
        {
            // 2 stufig suchen
            // gibt es eine Standardanalyse zu diesem SAP-Material
            final Analysis matTypeAnalysis = analysisHome.findLastActiveByName( stockMaterial.getMaterialType().getApk() );
            if ( matTypeAnalysis != null )
            {
                stockMaterial.setAnalysis( matTypeAnalysis );
            }
            else
            {
                if ( stockMaterial.numberOfCharacteristics() > 0 )
                {
                    final List<MaterialCharacteristic> analysisCharacteristics = stockMaterial.getCharacteristics().stream()
                            .filter( materialCharacteristic -> materialCharacteristic.getName().endsWith( "RW" ) )
                            .collect( Collectors.toList() );
                    final Analysis stockMaterialAnalysis = new Analysis();
                    stockMaterialAnalysis.setName( stockMaterial.getName() );
                    for ( MaterialCharacteristic analysisCharacteristic : analysisCharacteristics )
                    {
                        final AnalysisElement analysisElement = new AnalysisElement();
                        analysisElement.setName( analysisCharacteristic.getDescription() );
                        analysisElement.setStandardValue( analysisCharacteristic.getValue() );
                        analysisElementHome.persist( analysisElement );
                        stockMaterialAnalysis.addToAnalysisElements( analysisElement );
                    }
                    analysisHome.persist( stockMaterialAnalysis );
                    stockMaterial.setAnalysis( stockMaterialAnalysis );
                }
            }
        }

        if ( stockMaterial.getHandlingUnit() == null )
        {
            final Place stocksPlace = placeHome.findByName( Casting.PLACE.STOCKS );
            placeService.assignPlace( stockMaterial, stocksPlace );
        }
    }
}