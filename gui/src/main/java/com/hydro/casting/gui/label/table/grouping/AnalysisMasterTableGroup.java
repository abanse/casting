package com.hydro.casting.gui.label.table.grouping;

import com.hydro.casting.gui.analysis.util.AnalysisDTOComparator;
import com.hydro.casting.server.contract.dto.AnalysisDTO;
import com.hydro.core.gui.comp.GroupTreeItemProvider;
import com.hydro.core.gui.util.SummaryUtil;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Builds the tree used in the analysis view master table. Necessary to provide required data to both parent and child elements. Parent elements are used to display
 * details about the whole analysis, child elements are used to display details about the individual sample.
 */
public class AnalysisMasterTableGroup implements GroupTreeItemProvider<AnalysisDTO>
{
    @Override
    public void buildTree( TreeItem<AnalysisDTO> root, List<AnalysisDTO> data, HBox summary )
    {
        if ( data.isEmpty() )
        {
            return;
        }

        // Sort order: charge, melter -> unique identifier for the header, since there are only ~5 days stored in the cache
        final List<AnalysisDTO> sortedList = data.stream().sorted( ( o1, o2 ) -> Comparator.comparing( AnalysisDTO::getCharge ).reversed().thenComparing( AnalysisDTO::getMelter ).compare( o1, o2 ) )
                .collect( Collectors.toList() );

        // Always contains the current header tree item
        TreeItem<AnalysisDTO> header = null;
        // Algorithm relies on correct sorting of the analysisDto list - it's not checked for each sample if a matching header exists
        for ( AnalysisDTO analysisDTO : sortedList )
        {
            // New child tree item if a sample (called analysis) if the sample was taken from current header's charge and melter
            if ( header != null && Objects.equals( header.getValue().getCharge(), analysisDTO.getCharge() ) && Objects.equals( header.getValue().getMelter(), analysisDTO.getMelter() ) )
            {
                TreeItem<AnalysisDTO> child = new TreeItem<>( analysisDTO );
                header.getChildren().add( child );
                updateHeaderDTOData( header, child );
                setHeaderLastChangedDateIfApplicable( header, analysisDTO.getLastChanged() );
                continue;
            }

            // Create a new header tree item in case the current analysis does not contain a sample that matches the previous header
            final TreeItem<AnalysisDTO> bundleTI = createHeaderTreeItem( analysisDTO );
            root.getChildren().add( bundleTI );
            header = bundleTI;
        }

        // Sorting the children of each header (analyses / samples for a charge / melter) by their logical sample order
        root.getChildren().forEach( headerTreeItem -> headerTreeItem.getChildren().sort( new AnalysisDTOComparator() ) );

        if ( summary != null )
        {
            SummaryUtil.addCount( summary, sortedList.size() );
        }
    }

    /**
     * Builds a new header tree item which only contains the information relevant for displaying the correct details.
     * <p>
     * The required information is the charge and the melter of the elements contained in this tree branch. This information is then used to display an overview over all sample analyses in the current
     * charge / melter.
     *
     * @param analysisDTO Current AnalysisDTO specifying one sample. Header data is extracted from this sample.
     * @return Newly created header tree item with the required data extracted from the sample provided.
     */

    private TreeItem<AnalysisDTO> createHeaderTreeItem( AnalysisDTO analysisDTO )
    {
        final AnalysisDTO bundleItem = new AnalysisDTO();
        bundleItem.setName( analysisDTO.getName() );
        bundleItem.setCharge( analysisDTO.getCharge() );
        bundleItem.setMelter( analysisDTO.getMelter() );
        bundleItem.setAlloyName( analysisDTO.getAlloyName() );
        bundleItem.setSampleNumber( analysisDTO.getSampleNumber() );
        // Year has to be set since charge numbers can be duplicated over a span of 3-4 years
        bundleItem.setYear( analysisDTO.getYear() );
        final TreeItem<AnalysisDTO> bundleTI = new TreeItem<>( bundleItem );
        bundleTI.getChildren().add( new TreeItem<>( analysisDTO ) );
        // Have to set last changed date on header creation, otherwise last date won't be set on headers with only one child element
        setHeaderLastChangedDateIfApplicable( bundleTI, analysisDTO.getLastChanged() );

        return bundleTI;
    }

    /**
     * This function replaces the lastChanged field on the header dto if the passed date is later than the data currently stored on the dto. Also initially sets the date if the
     * field is null on the dto.
     *
     * @param header             The TreeItem that represents the header and contains it's dto.
     * @param newLastChangedDate Candidate for the new lastChanged date for the header dto.
     */
    private void setHeaderLastChangedDateIfApplicable( TreeItem<AnalysisDTO> header, LocalDateTime newLastChangedDate )
    {
        AnalysisDTO dto = header.getValue();
        if ( dto.getLastChanged() == null || ( newLastChangedDate != null && dto.getLastChanged().isBefore( newLastChangedDate ) ) )
        {
            dto.setLastChanged( newLastChangedDate );
        }
    }

    /**
     * This function updates the header's dto when a new child element is added.
     * The header should always contain the information of the latest / last sample.
     *
     * @param header The header tree element
     * @param child The child tree element that was added
     */
    private void updateHeaderDTOData( TreeItem<AnalysisDTO> header, TreeItem<AnalysisDTO> child )
    {
        AnalysisDTO headerDTO = header.getValue();
        AnalysisDTO childDTO = child.getValue();

        // Always update alloy version if possible and necessary
        if ( headerDTO.getAlloyVersion() == null && childDTO.getAlloyVersion() != null )
        {
            headerDTO.setAlloyVersion( childDTO.getAlloyVersion() );
        }

        // Use comparator to find out if the current child element is a "later" sample than the current header data
        AnalysisDTOComparator analysisDTOComparator = new AnalysisDTOComparator();
        if ( analysisDTOComparator.compare( header, child ) < 0 )
        {
            // Update all elements specific to an individual sample
            headerDTO.setSampleNumber( childDTO.getSampleNumber() );
            headerDTO.setStatus( childDTO.getStatus() );
            headerDTO.setAnalysisNo( childDTO.getAnalysisNo() );
            headerDTO.setAnalysisOk( childDTO.getAnalysisOk() );
        }
    }
}
