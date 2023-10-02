package com.hydro.casting.gui.downtime.control;

import com.google.inject.Inject;
import com.hydro.casting.common.Casting;
import com.hydro.casting.common.constant.CostCenterEnum;
import com.hydro.casting.gui.downtime.task.DowntimeLoaderTask;
import com.hydro.casting.server.contract.downtime.DowntimeBusiness;
import com.hydro.casting.server.contract.downtime.dto.*;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.core.common.util.StringTools;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.comp.DateTimePicker;
import com.hydro.core.gui.comp.DurationTextField;
import com.hydro.core.gui.util.AutoCompleteComboBox;
import com.hydro.core.gui.validation.MESValidationSupport;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.controlsfx.validation.Validator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DowntimeDetailControlController
{
    @FXML
    private DateTimePicker dateTimeFrom;

    @FXML
    private DateTimePicker dateTimeUntil;

    @FXML
    private DurationTextField duration;

    @FXML
    private ComboBox<String> downtimeKindComboBox;

    @FXML
    private ComboBox<String> costCenter;

    @FXML
    private ComboBox<String> phase;

    @FXML
    private Label downtimeModuleLabel;
    @FXML
    private ComboBox<String> downtimeModuleComboBox;

    @FXML
    private TextArea downtimeDescription;

    @FXML
    private CheckBox splitTimeCheckBox;
    @FXML
    private Label splitTimeInfoLabel;
    @FXML
    private Slider splitTimeSlider;

    @Inject
    private DowntimeLoaderTask downtimeTask = new DowntimeLoaderTask();

    @Inject
    private BusinessManager businessManager;

    @Inject
    private SecurityManager securityManager;

    private String currentCostCenter;
    private String[] costCenters;
    private String machine;
    private List<DowntimeKindDTO> downtimeKindDTOs;
    private List<DowntimeModuleDTO> downtimeModuleDTOs;
    private DowntimeDTO newDowntime;
    private BooleanProperty working = new SimpleBooleanProperty( false );

    private boolean timeEditAllowed;

    private static int DEFAULT_FONT_SIZE = 12;

    private boolean disableEventing = false;

    private LocalDateTime splitTimeEnd = null;

    public DowntimeDetailControlController()
    {
        downtimeTask.setController( this );
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        downtimeDescription.setWrapText( true );
        AutoCompleteComboBox.autoCompleteComboBoxPlus( downtimeKindComboBox, ( typedText, itemToCompare ) -> itemToCompare.toLowerCase().contains( typedText.toLowerCase() ) );
        AutoCompleteComboBox.autoCompleteComboBoxPlus( downtimeModuleComboBox, ( typedText, itemToCompare ) -> itemToCompare.toLowerCase().contains( typedText.toLowerCase() ) );
        AutoCompleteComboBox.autoCompleteComboBoxPlus( costCenter, ( typedText, itemToCompare ) -> itemToCompare.toLowerCase().contains( typedText.toLowerCase() ) );
        AutoCompleteComboBox.autoCompleteComboBoxPlus( phase, ( typedText, itemToCompare ) -> itemToCompare.toLowerCase().contains( typedText.toLowerCase() ) );
        duration.fromDateTimeProperty().bind( dateTimeFrom.dateTimeProperty() );
        duration.toDateTimeProperty().bind( dateTimeUntil.dateTimeProperty() );

        costCenter.getSelectionModel().selectedItemProperty().addListener( observable -> {
            if ( disableEventing )
            {
                return;
            }
            Platform.runLater( () -> {
                reloadComboBoxes();
                Platform.runLater( () -> {
                    if ( phase.isDisabled() )
                    {
                        if ( downtimeModuleComboBox.isDisabled() )
                        {
                            Platform.runLater( () -> downtimeKindComboBox.requestFocus() );
                        }
                        else
                        {
                            Platform.runLater( () -> downtimeModuleComboBox.requestFocus() );
                        }
                    }
                    else
                    {
                        phase.requestFocus();
                    }
                } );
            } );
        } );
        phase.getSelectionModel().selectedItemProperty().addListener( observable -> {
            if ( disableEventing )
            {
                return;
            }
            Platform.runLater( () -> {
                reloadComboBoxes();
                if ( downtimeModuleComboBox.isDisabled() )
                {
                    Platform.runLater( () -> downtimeKindComboBox.requestFocus() );
                }
                else
                {
                    Platform.runLater( () -> downtimeModuleComboBox.requestFocus() );
                }
            } );
        } );
        downtimeModuleComboBox.getSelectionModel().selectedItemProperty().addListener( observable -> {
            if ( disableEventing )
            {
                return;
            }
            Platform.runLater( () -> {
                reloadComboBoxes();
                downtimeKindComboBox.requestFocus();
            } );
        } );
        downtimeKindComboBox.getSelectionModel().selectedItemProperty().addListener( observable -> {
            if ( disableEventing )
            {
                return;
            }
            Platform.runLater( () -> downtimeDescription.requestFocus() );

        } );
        if ( splitTimeCheckBox != null )
        {
            splitTimeSlider.disableProperty().bind( splitTimeCheckBox.selectedProperty().not() );
            splitTimeInfoLabel.visibleProperty().bind( splitTimeCheckBox.selectedProperty() );
            dateTimeUntil.editableProperty().bind( splitTimeCheckBox.selectedProperty().not() );
            splitTimeCheckBox.selectedProperty().addListener( ( observable, oldValue, newValue ) -> {
                if ( newValue != null && newValue )
                {
                    splitTimeEnd = dateTimeUntil.getLocalDateTime();
                    revalidateSplitTime();
                }
                else if ( newValue != null )
                {
                    dateTimeUntil.setLocalDateTime( splitTimeEnd );
                    splitTimeEnd = null;
                }
            } );
            splitTimeSlider.valueProperty().addListener( observable -> revalidateSplitTime() );
        }
    }

    public void adjustComboboxFontSize( double zoom )
    {
        long fontSize = Math.round( DEFAULT_FONT_SIZE * zoom );
        addZoomListCellToComboBox( downtimeKindComboBox, fontSize );
        addZoomListCellToComboBox( downtimeModuleComboBox, fontSize );
    }

    private static void addZoomListCellToComboBox( ComboBox<String> comboBox, final long fontSize )
    {
        comboBox.setCellFactory( cf -> new ListCell<>()
        {
            @Override
            protected void updateItem( String item, boolean empty )
            {
                super.updateItem( item, empty );

                if ( empty || item == null )
                {
                    setText( null );
                }
                else
                {
                    setText( item );
                    setStyle( "-fx-font-size:" + fontSize );
                }
            }
        } );
    }

    public void setCostCenters( String currentCostCenter, String[] costCenters )
    {
        this.currentCostCenter = currentCostCenter;
        this.costCenters = costCenters;
    }

    public void setMachine( String machine )
    {
        this.machine = machine;
    }

    public void setValidationSupport( MESValidationSupport validationSupport )
    {
        dateTimeFrom.setValidationSupport( validationSupport, "Startzeit" );
        dateTimeUntil.setValidationSupport( validationSupport, "Endezeit" );
        final Validator<String> costCenterValidator = Validator.createEmptyValidator( "Anlage ist erforderlich." );
        validationSupport.registerValidator( costCenter, true, costCenterValidator );
        final Validator<String> downtimeKindValidator = Validator.createEmptyValidator( "Störzeitart ist erforderlich." );
        validationSupport.registerValidator( downtimeKindComboBox, true, downtimeKindValidator );
        Validator<String> downtimeDescriptionValidator = Validator.createEmptyValidator( "Kommentar ist erforderlich" );
        validationSupport.registerValidator( downtimeDescription, true, downtimeDescriptionValidator );

        validationSupport.registerValidator( duration, false,
                Validator.createPredicateValidator( (Predicate<String>) t -> t == null || !t.startsWith( "-" ), "Endezeit darf nicht vor Anfangszeit liegen" ) );
    }

    public void loadData( DowntimeCreationDTO data, DowntimeDTO suggestionDowntimeDTO )
    {
        clearFXMLObjects();
        newDowntime = new DowntimeDTO();
        if ( data != null )
        {
            if ( data.getStartTS() != null )
            {
                dateTimeFrom.setDateTime( data.getStartTS() );

                costCenter.setValue( data.getCostCenter() );
                phase.setValue( data.getDowntimeKind().getPhase() );
                final String downtimeKindString = getDowntimeDisplayText( data.getDowntimeKind() );
                final String downtimeModuleString = getDowntimeDisplayText( data.getDowntimeModule() );
                downtimeKindComboBox.setValue( downtimeKindString );
                downtimeModuleComboBox.setValue( downtimeModuleString );
                downtimeDescription.setText( data.getDescription() );

                newDowntime.setId( data.getId() );
                newDowntime.setCostCenter( data.getCostCenter() );
            }
            else if ( suggestionDowntimeDTO != null )
            {
                if ( suggestionDowntimeDTO.getFromTS() != null )
                {
                    dateTimeFrom.setDateTime( suggestionDowntimeDTO.getFromTS() );
                }
                if ( suggestionDowntimeDTO.getEndTS() != null )
                {
                    dateTimeUntil.setDateTime( suggestionDowntimeDTO.getEndTS() );
                }
                if ( suggestionDowntimeDTO.getDescription() != null )
                {
                    downtimeDescription.setText( suggestionDowntimeDTO.getDescription() );
                }
            }
            if ( data.getEndTS() != null )
            {
                dateTimeUntil.setDateTime( data.getEndTS() );
            }

            downtimeKindDTOs = data.getDowntimeKinds();
            downtimeModuleDTOs = data.getDowntimeModules();

            final List<String> costCenters = downtimeKindDTOs.stream().map( ( downtimeKindDTO ) -> {
                final CostCenterEnum cce = CostCenterEnum.findByCostCenter( downtimeKindDTO.getCostCenter() );
                if ( cce != null )
                {
                    return cce.getDescription();
                }
                return downtimeKindDTO.getCostCenter();
            } ).collect( Collectors.toCollection( HashSet::new ) ).stream().sorted().collect( Collectors.toList() );
            costCenter.getItems().setAll( costCenters );
            if ( data.getCostCenter() != null )
            {
                costCenter.getSelectionModel().select( CostCenterEnum.findByCostCenter( data.getCostCenter() ).getDescription() );
            }
            else if ( currentCostCenter != null )
            {
                costCenter.getSelectionModel().select( CostCenterEnum.findByCostCenter( currentCostCenter ).getDescription() );
            }
            reloadComboBoxes();
        }
    }

    private void reloadComboBoxes()
    {
        disableEventing = true;
        try
        {
            if ( downtimeKindDTOs == null || StringTools.isNullOrEmpty( costCenter.getValue() ) )
            {
                phase.getItems().clear();
                phase.setDisable( true );
                phase.setValue( null );
                downtimeModuleComboBox.getItems().clear();
                downtimeModuleComboBox.setDisable( true );
                downtimeModuleComboBox.setValue( null );
                downtimeKindComboBox.getItems().clear();
                downtimeKindComboBox.setDisable( true );
                downtimeKindComboBox.setValue( null );
                return;
            }

            final CostCenterEnum costCenterEnum = CostCenterEnum.findByDescription( costCenter.getValue() );

            final List<DowntimeKindDTO> costCenterKinds = downtimeKindDTOs.stream().filter( downtimeKindDTO -> Objects.equals( downtimeKindDTO.getCostCenter(), costCenterEnum.getCostCenter() ) )
                    .collect( Collectors.toList() );

            boolean phaseExist = false;
            final List<String> phases = new ArrayList<>();
            for ( DowntimeKindDTO costCenterKind : costCenterKinds )
            {
                if ( costCenterKind.getPhase() == null )
                {
                    continue;
                }
                if ( !phases.contains( costCenterKind.getPhase() ) )
                {
                    phases.add( costCenterKind.getPhase() );
                }
                phaseExist = true;
            }
            if ( !phaseExist )
            {
                phase.getItems().clear();
                phase.setDisable( true );
                phase.setValue( null );
            }
            else
            {
                Collections.sort( phases );
                final String currentPhase = phase.getValue();
                phase.getItems().setAll( phases );
                phase.setDisable( false );
                if ( currentPhase != null && phases.contains( currentPhase ) )
                {
                    phase.setValue( currentPhase );
                }
                else
                {
                    phase.setValue( null );
                }
                if ( phase.getValue() == null )
                {
                    downtimeKindComboBox.getItems().clear();
                    downtimeKindComboBox.setDisable( true );
                    downtimeKindComboBox.setValue( null );
                    return;
                }
            }

            boolean finalPhaseExist = phaseExist;
            List<DowntimeKindDTO> phaseKinds = costCenterKinds.stream().filter( downtimeKindDTO -> {
                if ( !finalPhaseExist )
                {
                    return true;
                }
                return Objects.equals( downtimeKindDTO.getPhase(), phase.getValue() );
            } ).collect( Collectors.toList() );

            setDowntimeKinds( phaseKinds );

            // Enable downtime module combo box and label if costCenter is S1
            if ( currentCostCenter != null && ( currentCostCenter.equals( Casting.MACHINE.MELTING_FURNACE_S1 ) || currentCostCenter.equals( Casting.MACHINE.MELTING_FURNACE_S2 ) ) )
            {
                downtimeModuleLabel.setDisable( false );
                setDowntimeModules( downtimeModuleDTOs );
            }
            else
            {
                downtimeModuleLabel.setDisable( true );
                downtimeModuleComboBox.setDisable( true );
                downtimeModuleComboBox.setValue( null );
                downtimeModuleComboBox.getItems().clear();
            }

            // Additional filtering for downtime kind
            final String currentModule = downtimeModuleComboBox.getValue();
            final Optional<DowntimeModuleDTO> currentModuleOptional = downtimeModuleDTOs.stream().filter( dto -> getDowntimeDisplayText( dto ).equals( currentModule ) ).findFirst();
            if ( currentModuleOptional.isPresent() )
            {
                final DowntimeModuleDTO currentModuleDTO = currentModuleOptional.get();
                Set<DowntimeKindDTO> downtimeKindsForCurrentModule = currentModuleDTO.getDowntimeKinds();

                List<DowntimeKindDTO> filteredKinds = phaseKinds.stream().filter( downtimeKindsForCurrentModule::contains ).collect( Collectors.toList() );

                // Only replace if elements are present, otherwise we cannot differentiate between "no mapping intended" and "no elements in mapping"
                if ( !filteredKinds.isEmpty() )
                {
                    setDowntimeKinds( filteredKinds );
                }
            }
        }
        finally
        {
            disableEventing = false;
        }
    }

    private void setDowntimeModules( List<DowntimeModuleDTO> downtimeModuleDTOs )
    {
        final List<String> downtimeModulesAsStrings = new ArrayList<>();
        for ( DowntimeModuleDTO downtimeModuleDTO : downtimeModuleDTOs )
        {
            downtimeModulesAsStrings.add( getDowntimeDisplayText( downtimeModuleDTO ) );
        }
        setValuesOnCombobox( downtimeModuleComboBox, downtimeModulesAsStrings );
    }

    private void setDowntimeKinds( List<DowntimeKindDTO> downtimeKindDTOs )
    {
        final List<String> downtimeKindsAsStrings = new ArrayList<>();
        for ( DowntimeKindDTO downtimekindDTO : downtimeKindDTOs )
        {
            downtimeKindsAsStrings.add( getDowntimeDisplayText( downtimekindDTO ) );
        }
        setValuesOnCombobox( downtimeKindComboBox, downtimeKindsAsStrings );
    }

    private void setValuesOnCombobox( ComboBox<String> combobox, List<String> values )
    {
        final String currentValue = combobox.getValue();
        combobox.setDisable( false );
        combobox.getItems().setAll( values );
        if ( currentValue != null && values.contains( currentValue ) )
        {
            combobox.setValue( currentValue );
        }
        else
        {
            combobox.setValue( null );
        }
    }

    public void createDowntime( String type ) throws BusinessException
    {
        createDowntime( null, null, type );
    }

    public void createDowntime( DowntimeRequestDTO downtimeRequest, LocalDateTime splitTime, String type ) throws BusinessException
    {
        final String cc = CostCenterEnum.findByDescription( costCenter.getValue() ).getCostCenter();
        newDowntime.setCostCenter( cc );
        newDowntime.setMachine( machine );

        DowntimeBusiness downtimeBusiness = businessManager.getSession( DowntimeBusiness.class );
        newDowntime.setFromTS( dateTimeFrom.getDateTime() );

        if ( dateTimeUntil.getDate() != null && dateTimeUntil.getTime() != null )
        {
            newDowntime.setEndTS( dateTimeUntil.getDateTime() );
        }

        String selectedDowntimeKind = AutoCompleteComboBox.getComboBoxValue( downtimeKindComboBox );

        for ( DowntimeKindDTO downtimeKind : downtimeKindDTOs )
        {
            if ( !Objects.equals( downtimeKind.getCostCenter(), cc ) )
            {
                continue;
            }
            if ( Objects.equals( selectedDowntimeKind, getDowntimeDisplayText( downtimeKind ) ) )
            {
                newDowntime.setDowntimeKind1( downtimeKind.getDowntimeKind1() );
                newDowntime.setDowntimeKind2( downtimeKind.getDowntimeKind2() );
                newDowntime.setDowntimeKind3( downtimeKind.getDowntimeKind3() );
                newDowntime.setDowntimeDescription( downtimeKind.getDescription() );
                break;
            }
        }

        String selectedDowntimeModule = AutoCompleteComboBox.getComboBoxValue( downtimeModuleComboBox );
        for ( DowntimeModuleDTO downtimeModuleDTO : downtimeModuleDTOs )
        {
            if ( !Objects.equals( downtimeModuleDTO.getCostCenter(), cc ) )
            {
                continue;
            }
            if ( Objects.equals( selectedDowntimeModule, getDowntimeDisplayText( downtimeModuleDTO ) ) )
            {
                newDowntime.setModule( downtimeModuleDTO.getModule() );
                newDowntime.setComponent( downtimeModuleDTO.getComponent() );
                newDowntime.setModuleDescription( downtimeModuleDTO.getDescription() );
                newDowntime.setModuleErpIdent( downtimeModuleDTO.getOrderNumber() );
            }
        }

        newDowntime.setDescription( downtimeDescription.getText() );
        newDowntime.setUserId( securityManager.getCurrentUser() );
        newDowntime.setType( type );

        downtimeBusiness.createDowntime( newDowntime, downtimeRequest, splitTime );
    }

    private void clearFXMLObjects()
    {
        dateTimeFrom.clear();
        dateTimeUntil.clear();
        costCenter.setValue( null );
        phase.setValue( null );
        downtimeKindComboBox.setValue( null );
        downtimeModuleComboBox.setValue( null );
        downtimeDescription.setText( null );
        if ( timeEditAllowed )
        {
            dateTimeFrom.setDisable( false );
        }
        // downtimeKindComboBox.setDisable( false );
        // downtimeModuleComboBox.setDisable( false );
        splitTimeEnd = null;
        if ( splitTimeCheckBox != null )
        {
            splitTimeCheckBox.setSelected( false );
        }
        if ( splitTimeSlider != null )
        {
            splitTimeSlider.setValue( 0.5 );
        }
    }

    //    /**
    //     * This method checks whether the required fields are filled and whether the values of all fields are
    //     * valid.
    //     *
    //     * @return int with the result of the validation.
    //     * @throws BusinessException
    //     */
    //    private int validateInputs() throws BusinessException
    //    {
    //        int result = DowntimeConstants.INPUT_ERRORS.NO_ERROR;
    //        if ( newDowntime.getId() <= 0 )
    //        {
    //            // lastDowntimeEnd = downtimeDataProvider.findLastDowntimeEnd( costCenter );
    //            // downtimeDataProvider.findLastDowntimeEnd( costCenter );
    //            // if ( lastDowntimeEnd != null && newDowntime.getDateTimeFrom().isBefore( lastDowntimeEnd ) )
    //            if ( !downtimeDataProvider.isStartValid( newDowntime ) )
    //            {
    //                result = DowntimeConstants.INPUT_ERRORS.TOO_LATE_ERROR;
    //            }
    //        }
    //        if ( !downtimeDataProvider.isDowntimeValid( newDowntime ) )
    //        {
    //            result = DowntimeConstants.INPUT_ERRORS.TIME_OVERLAP_ERROR;
    //        }
    ////        else if ( !validateShiftBorders() )
    ////        {
    ////            result = DowntimeConstants.INPUT_ERRORS.SHIFT_BORDER_VIOLATION;
    ////        }
    //
    //        return result;
    //    }

    //    private boolean validateShiftBorders()
    //    {
    //        boolean result = true;
    //        LocalDateTime timeFrom = dateTimeFrom.getDateTime();
    //        LocalDateTime timeUntil = dateTimeUntil.getDateTime();
    //
    //        if ( timeUntil != null )
    //        {
    //            LocalDateTime maxUntilValue = shiftTimeDataProvider.getEndDateTimeForShift( costCenter, timeFrom );
    //            if ( timeUntil.isAfter( maxUntilValue ) )
    //            {
    //                result = false;
    //            }
    //        }
    //        return result;
    //    }

    public void setWorking( boolean working )
    {
        this.working.setValue( working );
    }

    public BooleanProperty getWorking()
    {
        return working;
    }

    public void setTimeEditAllowed( boolean timeEditAllowed )
    {
        this.timeEditAllowed = timeEditAllowed;
        dateTimeFrom.setDisable( !timeEditAllowed );
        dateTimeUntil.setDisable( !timeEditAllowed );
    }

    private String getDowntimeDisplayText( final DowntimeKindDTO downtimeKindDTO )
    {
        return downtimeKindDTO.getDowntimeKind1() + "-" + downtimeKindDTO.getDowntimeKind2() + "-" + downtimeKindDTO.getDowntimeKind3() + " " + downtimeKindDTO.getDescription();
    }

    private String getDowntimeDisplayText( final DowntimeModuleDTO downtimeModuleDTO )
    {
        if ( downtimeModuleDTO == null )
        {
            return null;
        }
        return downtimeModuleDTO.getOrderNumber() + " " + downtimeModuleDTO.getDescription();
    }

    private void revalidateSplitTime()
    {
        if ( splitTimeInfoLabel == null || splitTimeSlider == null || splitTimeEnd == null )
        {
            return;
        }
        final Duration duration = Duration.between( dateTimeFrom.getLocalDateTime(), splitTimeEnd );
        final double splitDuration = ( (double) duration.toSeconds() ) * splitTimeSlider.getValue();
        final LocalDateTime splitTime = dateTimeFrom.getLocalDateTime().plusSeconds( Math.round( splitDuration ) );
        dateTimeUntil.setLocalDateTime( splitTime );

        splitTimeInfoLabel.setText( "" + Math.round( splitTimeSlider.getValue() * 100. ) + "% von bis Zeit " + splitTimeEnd.format( DateTimeFormatter.ofPattern( "dd.MM.yyyy HH:mm" ) ) );
    }

    public LocalDateTime getSplitTime()
    {
        if ( splitTimeCheckBox == null || splitTimeInfoLabel == null || splitTimeSlider == null || splitTimeEnd == null )
        {
            return null;
        }
        if ( !splitTimeCheckBox.isSelected() )
        {
            return null;
        }
        final Duration duration = Duration.between( dateTimeFrom.getLocalDateTime(), splitTimeEnd );
        final double splitDuration = ( (double) duration.toSeconds() ) * splitTimeSlider.getValue();
        return dateTimeFrom.getLocalDateTime().plusSeconds( Math.round( splitDuration ) );
    }
}