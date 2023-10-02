package com.hydro.casting.server.ejb.reporting;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.downtime.dto.DowntimeKindDTO;
import com.hydro.casting.server.contract.reporting.dto.ReportingDowntimeKindSummaryDTO;
import com.hydro.casting.server.contract.reporting.dto.ReportingOutputDetailDTO;
import com.hydro.core.common.util.StringTools;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;

/**
 * This class provides methods for LocalDateTime objects which are frequently needed in other reporting classes.
 *
 */
public class ReportingUtils
{
    static int i;
    @SuppressWarnings( "unchecked" )
    public static ObservableList<StackedBarChart.Series<String, Double>> createStackedBarChartContent( boolean factorCalculation, String xAxisLabels[], Map<DowntimeKindDTO, Double>... filteredDetailMaps )
    {
        ObservableList<StackedBarChart.Series<String, Double>> barChartData = FXCollections.observableArrayList();

        i = 0;
        for ( Map<DowntimeKindDTO, Double> filteredDetail : filteredDetailMaps )
        {
            filteredDetail.forEach( ( kind, duration ) -> {
                boolean dataAdded = false;
                StackedBarChart.Data<String, Double> data = null;
                if ( factorCalculation )
                {
                    data = new StackedBarChart.Data<String, Double>( xAxisLabels[i], duration*100 );
                }
                else
                {
                    data = new StackedBarChart.Data<String, Double>( xAxisLabels[i], duration );
                }
                String key = null;
                if ( kind == null )
                {
                    key = "Zusammengefasste Stillstände";
                }
                else
                {
                    key = kind.getDowntimeKind1() + "/" + kind.getDowntimeKind2() + " " + kind.getDescription();
                }

                for ( XYChart.Series<String, Double> series : barChartData )
                {
                    if ( series.getName().equals( key ) )
                    {
                        series.getData().add( data );
                        dataAdded = true;
                        break;
                    }
                }
                if ( !dataAdded )
                {
                    StackedBarChart.Series<String, Double> newSeries = new StackedBarChart.Series<String, Double>( key, FXCollections.observableArrayList( data ) );
                    barChartData.add( newSeries );
                }

            } );
            i++;
        }

        return barChartData;
    }
    
    public static LocalDateTime getFirstDayOfLastMonth()
    {
        LocalDateTime dateTime = LocalDate.now().atStartOfDay();
        dateTime = dateTime.minusMonths( 1 );
        dateTime = setToFirstDayOfMonth( dateTime );
        return dateTime;
    }

    public static LocalDateTime getLastDayOfLastMonth()
    {
        LocalDateTime dateTime = LocalDate.now().atTime( LocalTime.MAX );
        dateTime = dateTime.minusMonths( 1 );
        dateTime = setToLastDayOfMonth( dateTime );
        return dateTime;
    }

    public static LocalDateTime getFirstDayOfLastWeek()
    {
        LocalDateTime dateTime = LocalDate.now().atStartOfDay();
        TemporalField weekISO = WeekFields.of( Locale.getDefault() ).dayOfWeek();
        dateTime = dateTime.minusWeeks( 1 );
        dateTime = dateTime.with( weekISO, 1 );

        return dateTime;
    }

    public static LocalDateTime getFirstDayOfWeek()
    {
        LocalDateTime dateTime = LocalDate.now().atStartOfDay();
        TemporalField weekISO = WeekFields.of( Locale.getDefault() ).dayOfWeek();
        dateTime = dateTime.with( weekISO, 1 );

        return dateTime;
    }

    public static LocalDateTime getLastDayOfLastWeek()
    {
        LocalDateTime dateTime = LocalDate.now().atTime( LocalTime.MAX );
        TemporalField weekISO = WeekFields.of( Locale.getDefault() ).dayOfWeek();
        dateTime = dateTime.minusWeeks( 1 );
        dateTime = dateTime.with( weekISO, 7 );
        return dateTime;
    }

    public static LocalDateTime setToFirstDayOfMonth( LocalDateTime dateTime )
    {
        return dateTime.withDayOfMonth( 1 );
    }

    public static LocalDateTime setToLastDayOfMonth( LocalDateTime dateTime )
    {
        return dateTime.withDayOfMonth( dateTime.toLocalDate().lengthOfMonth() );
    }

    public static LocalDateTime setToStartOfDay( LocalDateTime dateTime )
    {
        return dateTime.toLocalDate().atStartOfDay();
    }

    public static LocalDateTime setToEndOfDay( LocalDateTime dateTime )
    {
        return dateTime.toLocalDate().atTime( LocalTime.MAX );
    }

    public int calculateAmountOfProducedMaterialSlitter( List<ReportingOutputDetailDTO> data )
    {
        return data.size();
    }

    public int calculateAmountOfProducedMaterialCutLength( List<ReportingOutputDetailDTO> data )
    {
        return data.stream().filter( ( v ) -> {
            return v.getPiecesOut() != null;
        } ).mapToInt( ReportingOutputDetailDTO::getPiecesOut ).sum();
    }

    public int calculateAmountOfDifferentScheduleNbrs( List<ReportingOutputDetailDTO> data )
    {
        Set<String> scheduleNbrs = new HashSet<String>();
        data.forEach( detail -> scheduleNbrs.add( detail.getScheduleNbr() ) );
        return scheduleNbrs.size();
    }

    public double calculateAverageThichnessForHotmillCoils( List<ReportingOutputDetailDTO> data )
    {
        List<ReportingOutputDetailDTO> filteredData = new ArrayList<>();
        for ( ReportingOutputDetailDTO reportingOutputDetailDTO : data )
        {
            if ( Casting.OUTPUT_TYPE.COIL.equals( reportingOutputDetailDTO.getOutputType() ) )
            {
                filteredData.add( reportingOutputDetailDTO );
            }
        }
        return calculateAverageThickness( filteredData, false );
    }

    public double calculateAverageThickness( List<ReportingOutputDetailDTO> data, boolean input )
    {
        double addedThickness = 0;
        Set<ParentCoil> parentCoils = new HashSet<ParentCoil>();
        for ( ReportingOutputDetailDTO detail : data )
        {
            ParentCoil parent = new ParentCoil( detail.getParentLot(), detail.getParentSublot(), detail.getParentInvSuffix(), detail.getParentOperationSeq() );
            if ( !parentCoils.contains( parent ) )
            {
                if ( input )
                {
                    addedThickness += detail.getThicknessIn();
                }
                else
                {
                    addedThickness += detail.getThickness();
                }
                parentCoils.add( parent );
            }
        }
        double result = 0;
        if ( parentCoils.size() != 0 )
        {
            result = addedThickness / parentCoils.size();
        }
        return result;
    }

    public double calculateAverageWidthPerSlit( List<ReportingOutputDetailDTO> data )
    {
        double result = 0;
        if ( data.size() != 0 )
        {
            result = data.stream().filter( ( v ) -> {
                return v.getWidth() != null;
            } ).mapToDouble( ReportingOutputDetailDTO::getWidth ).sum() / data.size();
        }
        return result;
    }

    public double calculateAverageLengthPerPiece( List<ReportingOutputDetailDTO> data )
    {
        double addedLength = 0;
        int totalPieces = 0;
        for ( ReportingOutputDetailDTO reportingOutputFinishingDetailDTO : data )
        {
            addedLength += reportingOutputFinishingDetailDTO.getLength() * reportingOutputFinishingDetailDTO.getPiecesOut();
            totalPieces += reportingOutputFinishingDetailDTO.getPiecesOut();
        }

        double result = 0;
        if ( totalPieces != 0 )
        {
            result = addedLength / totalPieces;
        }
        return result;
    }

    public int calculateAmountOfProcessedCoils( List<ReportingOutputDetailDTO> data )
    {
        Set<ParentCoil> parents = new HashSet<ParentCoil>();
        data.forEach( detail -> {
            parents.add( new ParentCoil( detail.getParentLot(), detail.getParentSublot(), detail.getParentInvSuffix(), detail.getParentOperationSeq() ) );
        } );

        return parents.size();
    }

    public int calculateAmountOfProcessedBrazingIngots( List<ReportingOutputDetailDTO> data )
    {
        int amount = 0;
        for ( ReportingOutputDetailDTO reportingOutputDetailDTO : data )
        {
            if ( reportingOutputDetailDTO.getNumberPlates() != null && reportingOutputDetailDTO.getNumberPlates() > 0 )
            {
                amount++;
            }
        }
        return amount;
    }

    public int calculateAmountOfProducedOutputType( List<ReportingOutputDetailDTO> data, String outputType )
    {
        int amount = 0;
        for ( ReportingOutputDetailDTO reportingOutputDetailDTO : data )
        {
            if ( outputType.equals( reportingOutputDetailDTO.getOutputType() ) )
            {
                amount += reportingOutputDetailDTO.getPiecesOut();
            }
        }
        return amount;
    }

    public double calculateProducedWeight( List<ReportingOutputDetailDTO> data )
    {
        return data.stream().filter( ( v ) -> {
            return v.getWidth() != null;
        } ).mapToDouble( ReportingOutputDetailDTO::getWeight ).sum() / 1000;
    }

    public int calculatePackingCount( List<ReportingOutputDetailDTO> data )
    {
        int count = 0;
        for ( ReportingOutputDetailDTO reportingOutputDetailDTO : data )
        {
            if ( StringTools.isFilled( reportingOutputDetailDTO.getFinishingCc() ) && StringTools.isFilled( reportingOutputDetailDTO.getLot() )
                    && !reportingOutputDetailDTO.getLot().startsWith( "BA" ) )
            {
                count++;
            }
        }
        return count;
    }

    public double calculatePackingWeightNet( List<ReportingOutputDetailDTO> data )
    {
        List<ReportingOutputDetailDTO> filteredData = new ArrayList<>();
        for ( ReportingOutputDetailDTO reportingOutputDetailDTO : data )
        {
            if ( StringTools.isFilled( reportingOutputDetailDTO.getFinishingCc() ) && StringTools.isFilled( reportingOutputDetailDTO.getLot() ) && !reportingOutputDetailDTO.getLot().startsWith( "BA" )
                    && ( ( StringTools.isFilled( reportingOutputDetailDTO.getCustomerNbr() ) && !reportingOutputDetailDTO.getCustomerNbr().startsWith( "PU" ) )
                    || reportingOutputDetailDTO.getCustomerNbr() == null ) )
            {
                filteredData.add( reportingOutputDetailDTO );
            }
        }
        return filteredData.stream().filter( ( v ) -> {
            return v.getWidth() != null;
        } ).mapToDouble( ReportingOutputDetailDTO::getWeight ).sum() / 1000;
    }

    public double calculatePackingExternWeight( List<ReportingOutputDetailDTO> data )
    {
        List<ReportingOutputDetailDTO> filteredData = new ArrayList<>();
        for ( ReportingOutputDetailDTO reportingOutputDetailDTO : data )
        {
            if ( StringTools.isFilled( reportingOutputDetailDTO.getCustomerNbr() ) && StringTools.isFilled( reportingOutputDetailDTO.getFinishingCc() )
                    && reportingOutputDetailDTO.getCustomerNbr().startsWith( "PU" ) )
            {
                filteredData.add( reportingOutputDetailDTO );
            }
        }
        return filteredData.stream().filter( ( v ) -> {
            return v.getWeight() != null;
        } ).mapToDouble( ReportingOutputDetailDTO::getWeight ).sum() / 1000;
    }

    public double calculatePackingWeightForCostCenter( List<ReportingOutputDetailDTO> data, String costCenter )
    {
        List<ReportingOutputDetailDTO> filteredData = new ArrayList<>();
        for ( ReportingOutputDetailDTO reportingOutputDetailDTO : data )
        {
            if ( costCenter.equals( reportingOutputDetailDTO.getFinishingCc() ) )
            {
                filteredData.add( reportingOutputDetailDTO );
            }
        }
        return filteredData.stream().filter( ( v ) -> {
            return v.getWeight() != null;
        } ).mapToDouble( ReportingOutputDetailDTO::getWeight ).sum() / 1000;
    }

    public int calculateAmountOfFirstPasses( List<ReportingOutputDetailDTO> data )
    {
        int result = 0;
        for ( ReportingOutputDetailDTO reportingOutputDetailDTO : data )
        {
            if ( reportingOutputDetailDTO.getFirstPass().equals( "1" ) )
            {
                result++;
            }
        }
        return result;
    }

    public int calculateAmountOfLastPasses( List<ReportingOutputDetailDTO> data )
    {
        int result = 0;
        for ( ReportingOutputDetailDTO reportingOutputDetailDTO : data )
        {
            if ( "1".equals( reportingOutputDetailDTO.getLastPass() ) )
            {
                result++;
            }
        }
        return result;
    }

    public int calculateAmountOfAdditionalPasses( List<ReportingOutputDetailDTO> data )
    {
        int result = 0;
        for ( ReportingOutputDetailDTO reportingOutputDetailDTO : data )
        {
            if ( "KAO".equals( reportingOutputDetailDTO.getReworkReason() ) )
            {
                result++;
            }
        }
        return result;
    }

    public int calculateAmountOfBetweenPasses( List<ReportingOutputDetailDTO> data )// TODO Between??!?
    {
        int result = 0;
        for ( ReportingOutputDetailDTO reportingOutputDetailDTO : data )
        {
            if ( reportingOutputDetailDTO.getFirstPass().equals( "0" ) && reportingOutputDetailDTO.getLastPass().equals( "0" ) )
            {
                result++;
            }
        }
        return result;
    }

    public int calculateAmountOfSharpenedIngots( List<ReportingOutputDetailDTO> data )
    {
        int result = 0;
        for ( ReportingOutputDetailDTO reportingOutputDetailDTO : data )
        {
            if ( reportingOutputDetailDTO.getSharpened() != null && reportingOutputDetailDTO.getSharpened().equals( "1" ) )
            {
                result++;
            }
        }
        return result;
    }

    public int calculateAmountOfMultipleScalpedIngots( List<ReportingOutputDetailDTO> data )
    {
        int result = 0;
        for ( ReportingOutputDetailDTO reportingOutputDetailDTO : data )
        {
            if ( reportingOutputDetailDTO.getMultipleScalped() != null )
            {
                result += reportingOutputDetailDTO.getMultipleScalped();
            }
        }
        return result;
    }

    public double calculateAverageThicknessDecreaseRatio( List<ReportingOutputDetailDTO> data )
    {
        double result = 0;
        for ( ReportingOutputDetailDTO reportingOutputDetailDTO : data )
        {
            result += ( reportingOutputDetailDTO.getThicknessIn() - reportingOutputDetailDTO.getThickness() ) / reportingOutputDetailDTO.getThicknessIn();
        }
        if ( result != 0 )
        {
            result = ( result / calculateAmountOfProcessedCoils( data ) ) * 100;
        }
        return result;
    }

    public String createHoursAndMinutesString( int minutes )
    {
        return String.format( "%d:%02d", minutes / 60, minutes % 60 );
    }

    public int getDurationOfDowntimeKinds( List<ReportingDowntimeKindSummaryDTO> reportingDowntimeKindSummaryDTOs, List<ReportingCustomPair> downtimeKinds )
    {
        int result = 0;

        for ( ReportingCustomPair downtimeKind: downtimeKinds )
        {
            ReportingDowntimeKindSummaryDTO reportingDowntimeKindSummaryDTO = null;
            for ( ReportingDowntimeKindSummaryDTO dto: reportingDowntimeKindSummaryDTOs )
            {
                if ( dto.getDowntimeKind().getDowntimeKind1().equals( downtimeKind.getKey() ) && dto.getDowntimeKind().getDowntimeKind2().equals( downtimeKind.getValue() ) )
                {
                    reportingDowntimeKindSummaryDTO = dto;
                    break;
                }
            }
            if ( reportingDowntimeKindSummaryDTO != null )
            {
                result += reportingDowntimeKindSummaryDTO.getDurationInMinutes();
            }
        }

        return result;
    }

    public int getAmountOfDowntimes( List<ReportingDowntimeKindSummaryDTO> reportingDowntimeKindSummaryDTOs, List<ReportingCustomPair> downtimeKinds )
    {
        int result = 0;

        for ( ReportingCustomPair downtimeKind: downtimeKinds )
        {
            ReportingDowntimeKindSummaryDTO reportingDowntimeKindSummaryDTO = null;
            for ( ReportingDowntimeKindSummaryDTO dto: reportingDowntimeKindSummaryDTOs )
            {
                if ( dto.getDowntimeKind().getDowntimeKind1().equals( downtimeKind.getKey() ) && dto.getDowntimeKind().getDowntimeKind2().equals( downtimeKind.getValue() ) )
                {
                    reportingDowntimeKindSummaryDTO = dto;
                    break;
                }
            }
            if ( reportingDowntimeKindSummaryDTO != null )
            {
                result += reportingDowntimeKindSummaryDTO.getAmount();
            }
        }

        return result;
    }

    public int getAmountOfAllDowntimes( List<ReportingDowntimeKindSummaryDTO> reportingDowntimeKindSummaryDTOs )
    {
        int amount = 0;
        for ( ReportingDowntimeKindSummaryDTO dto: reportingDowntimeKindSummaryDTOs )
        {
            amount += dto.getAmount();
        }
        return amount;
    }

    public double calculateAdditionalScrap( List<ReportingOutputDetailDTO> data )
    {
        double additionalScrap = 0;
        List<ReportingOutputDetailDTO> processedDTOs = new ArrayList<>();
        for ( ReportingOutputDetailDTO reportingOutputdetailDTO: data )
        {
            if ( !processedDTOs.contains( reportingOutputdetailDTO ) )
            {
                List<ReportingOutputDetailDTO> combiSchedules = findByInputMaterial( reportingOutputdetailDTO, data );
                List<ReportingOutputDetailDTO> filteredCombiSchedules = filterCombiSchedules( combiSchedules );
                boolean allSchedulesReady = true;
                long totalScrap = 0;
                for ( ReportingOutputDetailDTO dto: filteredCombiSchedules )
                {
                    if ( dto.getScrapWeight1() != null )
                    {
                        totalScrap += dto.getScrapWeight1();
                    }
                    else
                    {
                        allSchedulesReady = false;
                        break;
                    }
                    if ( dto.getScrapWeight2() != null )
                    {
                        totalScrap += dto.getScrapWeight2();
                    }
                }
                if ( allSchedulesReady )
                {
                    Long theoreticalOutput = Math.round( reportingOutputdetailDTO.getWeightIn() / reportingOutputdetailDTO.getStandardScrapFactor() );
                    Long theoreticalScrap = reportingOutputdetailDTO.getWeightIn() - theoreticalOutput;
                    additionalScrap += totalScrap - theoreticalScrap;
                }
                processedDTOs.addAll( combiSchedules );
            }
        }

        return additionalScrap;
    }

    private List<ReportingOutputDetailDTO> findByInputMaterial( ReportingOutputDetailDTO dto, List<ReportingOutputDetailDTO> data )
    {
        List<ReportingOutputDetailDTO> combiSchedule = new ArrayList<>();
        for ( ReportingOutputDetailDTO reportingOutputdetailDTO: data )
        {
            if ( hasSameParent( dto, reportingOutputdetailDTO ) )
            {
                combiSchedule.add( reportingOutputdetailDTO );
            }
        }
        return combiSchedule;
    }

    private List<ReportingOutputDetailDTO> filterCombiSchedules( List<ReportingOutputDetailDTO> combiSchedule )
    {
        List<ReportingOutputDetailDTO> filteredCombiSchedules = new ArrayList<>();
        List<String> helperList = new ArrayList<>();

        for ( ReportingOutputDetailDTO dto: combiSchedule )
        {
            String schedule = dto.getLot() + dto.getSublot() + dto.getInvSuffix();
            if ( !helperList.contains( schedule ) )
            {
                helperList.add( schedule );
                filteredCombiSchedules.add( dto );
            }
        }

        return filteredCombiSchedules;
    }

    private boolean hasSameParent(ReportingOutputDetailDTO dto1, ReportingOutputDetailDTO dto2)
    {
        return Objects.equals( dto1.getParentLot(), dto2.getParentLot() )
                && Objects.equals( dto1.getParentSublot(), dto2.getParentSublot() )
                && Objects.equals( dto1.getParentInvSuffix(), dto2.getParentInvSuffix() )
                && dto1.getParentOperationSeq().intValue() == dto2.getParentOperationSeq().intValue();
    }

    /**
     * A helper class to identify equal parent coils.
     *
     * @author a166670
     *
     */
    private class ParentCoil
    {
        private String parentLot;
        private String parentSublot;
        private String parentInvSuffix;
        private Integer parentOperationSeq;

        public ParentCoil( String parentLot, String parentSublot, String parentInvSuffix, Integer parentOperationSeq )
        {
            this.parentLot = parentLot;
            this.parentSublot = parentSublot;
            this.parentInvSuffix = parentInvSuffix;
            this.parentOperationSeq = parentOperationSeq;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ( ( parentInvSuffix == null ) ? 0 : parentInvSuffix.hashCode() );
            result = prime * result + ( ( parentLot == null ) ? 0 : parentLot.hashCode() );
            result = prime * result + ( ( parentOperationSeq == null ) ? 0 : parentOperationSeq.hashCode() );
            result = prime * result + ( ( parentSublot == null ) ? 0 : parentSublot.hashCode() );
            return result;
        }

        @Override
        public boolean equals( Object obj )
        {
            if ( this == obj )
                return true;
            if ( obj == null )
                return false;
            if ( getClass() != obj.getClass() )
                return false;
            ParentCoil other = (ParentCoil) obj;
            if ( !getOuterType().equals( other.getOuterType() ) )
                return false;
            if ( parentInvSuffix == null )
            {
                if ( other.parentInvSuffix != null )
                    return false;
            }
            else if ( !parentInvSuffix.equals( other.parentInvSuffix ) )
                return false;
            if ( parentLot == null )
            {
                if ( other.parentLot != null )
                    return false;
            }
            else if ( !parentLot.equals( other.parentLot ) )
                return false;
            if ( parentOperationSeq == null )
            {
                if ( other.parentOperationSeq != null )
                    return false;
            }
            else if ( !parentOperationSeq.equals( other.parentOperationSeq ) )
                return false;
            if ( parentSublot == null )
            {
                if ( other.parentSublot != null )
                    return false;
            }
            else if ( !parentSublot.equals( other.parentSublot ) )
                return false;
            return true;
        }

        private ReportingUtils getOuterType()
        {
            return ReportingUtils.this;
        }
    }
}
