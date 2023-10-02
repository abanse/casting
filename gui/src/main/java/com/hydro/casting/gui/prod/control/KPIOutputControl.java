package com.hydro.casting.gui.prod.control;

import com.google.inject.Injector;
import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.prod.control.skin.KPIPaginationSkin;
import com.hydro.casting.gui.prod.dialog.ModifyKPIOutputTargetsPopOver;
import com.hydro.casting.gui.reporting.data.ReportingCacheDataProvider;
import com.hydro.casting.server.contract.reporting.dto.ReportingGaugeSummaryDTO;
import com.hydro.core.gui.ImagesCore;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.comp.gauge.KPIGaugeView;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class KPIOutputControl extends StackPane
{
    private Pagination pagination = new Pagination( 3 );
    private Label infoLabel = new Label();
    private Button configureButton;
    private KPIGaugeView shiftGauge = new KPIGaugeView();
    private KPIGaugeView weekGauge = new KPIGaugeView();
    private KPIGaugeView monthGauge = new KPIGaugeView();

    private String costCenter;
    private ReportingCacheDataProvider dataProvider;

    private ModifyKPIOutputTargetsPopOver modifyKPIOutputTargetsPopOver = new ModifyKPIOutputTargetsPopOver();

    public KPIOutputControl()
    {
        final KPIPaginationSkin paginationSkin = new KPIPaginationSkin( pagination );
        pagination.setSkin( paginationSkin );
        pagination.setPageFactory( param -> {
            if ( param == 0 )
            {
                infoLabel.setText( "Schicht" );
                return shiftGauge;
            }
            else if ( param == 1 )
            {
                infoLabel.setText( "Woche" );
                return weekGauge;
            }
            else
            {
                infoLabel.setText( "Monat" );
                return monthGauge;
            }
        } );
        ObservableList<String> styleClass = pagination.getStyleClass();
        styleClass.add( Pagination.STYLE_CLASS_BULLET );

        infoLabel.setFont( new Font( 10.0 ) );
        StackPane.setAlignment( infoLabel, Pos.TOP_LEFT );

        getChildren().addAll( pagination, infoLabel );

        setOnMouseEntered( event -> {
            paginationSkin.setTransparent( false );
            if ( configureButton != null )
            {
                configureButton.setVisible( true );
            }
        } );
        setOnMouseExited( event -> {
            paginationSkin.setTransparent( true );
            if ( configureButton != null )
            {
                configureButton.setVisible( false );
            }
        } );
        paginationSkin.setTransparent( true );

        pagination.currentPageIndexProperty().addListener( observable -> setGaugeValues() );

    }

    public void init( final String costCenter, ReportingCacheDataProvider dataProvider, Injector injector )
    {
        this.costCenter = costCenter;
        this.dataProvider = dataProvider;

        injector.injectMembers( modifyKPIOutputTargetsPopOver );

        Tooltip.install( shiftGauge, new Tooltip( "Ausbringung [Stk]\naktuelle Schicht" ) );
        Tooltip.install( weekGauge, new Tooltip( "Ausbringung [Stk]\naktuelle Woche" ) );
        Tooltip.install( monthGauge, new Tooltip( "Ausbringung [Stk]\naktueller Monat" ) );

        shiftGauge.setRightArcColor( Color.GREEN );
        shiftGauge.setLeftArcColor( Color.RED );

        weekGauge.setRightArcColor( Color.GREEN );
        weekGauge.setLeftArcColor( Color.RED );

        monthGauge.setRightArcColor( Color.GREEN );
        monthGauge.setLeftArcColor( Color.RED );

        final SecurityManager securityManager = (SecurityManager) injector.getInstance( SecurityManager.class );
        if ( securityManager.hasRole( SecurityCasting.REPORTING.ACTION.MODIFY_KPI_OUTPUT_TARGETS ) )
        {
            configureButton = new Button();
            configureButton.setOnAction( event -> {
                modifyKPIOutputTargetsPopOver.setDataForCostCenter( costCenter, dataProvider );
                modifyKPIOutputTargetsPopOver.showPopOver( this, param -> {
                    if ( param != null && param )
                    {
                        setGaugeValues();
                    }
                    return null;
                } );
            } );
            double minButtonSize = 16;
            configureButton.setMinSize( minButtonSize, minButtonSize );
            configureButton.prefWidthProperty().bind( configureButton.minWidthProperty() );
            configureButton.prefHeightProperty().bind( configureButton.minHeightProperty() );
            configureButton.setStyle( "-fx-background-radius: 3em; -fx-padding: 0em 0em 0em 0.0em;" );
            configureButton.setFocusTraversable( false );
            final ImageView configureIV = new ImageView( ImagesCore.CONFIG.load() );
            configureIV.setFitWidth( 10 );
            configureIV.setFitHeight( 10 );
            configureButton.setGraphic( configureIV );
            StackPane.setMargin( configureButton, new Insets( 2, 0, 0, 0 ) );
            StackPane.setAlignment( configureButton, Pos.TOP_RIGHT );
            configureButton.setVisible( false );

            getChildren().add( configureButton );
        }
    }

    public void setGaugeValues()
    {
        if ( dataProvider == null )
        {
            return;
        }
        final Map<String, Integer> outputTargets = dataProvider.getKPIOutputTargets( costCenter );
        if ( outputTargets == null )
        {
            return;
        }
        // Diese Schicht
        if ( pagination.getCurrentPageIndex() == 0 )
        {
            final ReportingGaugeSummaryDTO summaryDTO = dataProvider.getGaugeSummary( costCenter, Casting.REPORTING.CURRENT_SHIFT );
            final Integer shiftOutputTarget = outputTargets.get( "shift" );
            if ( summaryDTO != null && shiftOutputTarget != null )
            {
                long durationInMinutes = summaryDTO.getStart().until( summaryDTO.getEnd(), ChronoUnit.MINUTES );
                shiftGauge.initOutputLayout( shiftOutputTarget );
                shiftGauge.setValueProportionally( shiftOutputTarget, durationInMinutes, summaryDTO.getOutputValue(), 8 * 60 );
            }
        }
        // Diese Woche
        else if ( pagination.getCurrentPageIndex() == 1 )
        {
            ReportingGaugeSummaryDTO summaryDTO = dataProvider.getGaugeSummary( costCenter, Casting.REPORTING.CURRENT_WEEK );
            final Integer weekOutputTarget = outputTargets.get( "week" );
            if ( summaryDTO != null && weekOutputTarget != null )
            {
                long durationInMinutes = summaryDTO.getStart().until( summaryDTO.getEnd(), ChronoUnit.MINUTES );
                weekGauge.initOutputLayout( weekOutputTarget );
                weekGauge.setValueProportionally( weekOutputTarget, durationInMinutes, summaryDTO.getOutputValue(), 7 * 24 * 60 );
            }
        }
        // Diesen Monat
        else if ( pagination.getCurrentPageIndex() == 2 )
        {
            ReportingGaugeSummaryDTO summaryDTO = dataProvider.getGaugeSummary( costCenter, Casting.REPORTING.CURRENT_MONTH );
            final Integer monthOutputTarget = outputTargets.get( "month" );
            if ( summaryDTO != null && monthOutputTarget != null )
            {
                long durationInMinutes = summaryDTO.getStart().until( summaryDTO.getEnd(), ChronoUnit.MINUTES );
                final YearMonth yearMonthObject = YearMonth.now();
                final long daysInMonth = yearMonthObject.lengthOfMonth();
                monthGauge.initOutputLayout( monthOutputTarget );
                monthGauge.setValueProportionally( monthOutputTarget, durationInMinutes, summaryDTO.getOutputValue(), daysInMonth * 24 * 60 );
            }
        }
    }
}
