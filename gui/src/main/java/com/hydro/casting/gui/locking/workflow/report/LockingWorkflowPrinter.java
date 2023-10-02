package com.hydro.casting.gui.locking.workflow.report;

import com.google.inject.Inject;
import com.hydro.casting.common.Casting;
import com.hydro.casting.gui.locking.workflow.report.model.LockingWorkflowReport;
import com.hydro.casting.gui.locking.workflow.task.LWAbstractTask;
import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;
import com.hydro.core.common.util.DateTimeUtil;
import com.hydro.core.gui.ErrorManager;
import com.hydro.core.gui.util.WindowsRegistry;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.ProgressBar;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRXmlUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class LockingWorkflowPrinter
{
    private final static DateFormat TSDF = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

    public final static String REGISTRY_OUTLOOK = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Clients\\Mail\\Microsoft Outlook\\shell\\open\\command";

    private final static String BAND = "Barren   ";

    private final static String LOCKED = "Gesperrt";

    private final static String FREE = "Freigegeben";

    private final static String SCRAPPING = "Verschrottung   ";

    private final static String FREEING_BEFORE_PACKING = "FREI ZUM PACKEN   ";

    private final static String USER = "Benutzer   ";

    @Inject
    private ErrorManager errorManager;

    private LockingWorkflowDTO lockingWorkflowDTO;

    private List<LockingWorkflowDTO> lockingWorkflowDTOs;

    private Dialog<ButtonType> progressDialog;
    private PrintWorker printWorker = new PrintWorker();
    private PrintMultiWorker printMultiWorker = new PrintMultiWorker();
    private String user;
    private int autoPrint;
    private boolean withEMail;
    private boolean withPrint;

    public LockingWorkflowPrinter()
    {
        progressDialog = new Dialog<>();
        progressDialog.getDialogPane().setHeaderText( "Ausdruck aufbereiten" );
        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth( 500 );
        progressDialog.getDialogPane().setContent( progressBar );
        progressDialog.getDialogPane().getButtonTypes().add( ButtonType.CANCEL );
        progressDialog.setOnCloseRequest( new EventHandler<DialogEvent>()
        {
            @Override
            public void handle( DialogEvent event )
            {
                if ( printWorker.isRunning() )
                {
                    printWorker.cancel();
                }
                if ( printMultiWorker.isRunning() )
                {
                    printMultiWorker.cancel();
                }
            }
        } );
    }

    public PrintWorker getPrintWorker()
    {
        return printWorker;
    }

    public void setPrintWorker( PrintWorker printWorker )
    {
        this.printWorker = printWorker;
    }

    public PrintMultiWorker getPrintMultiWorker()
    {
        return printMultiWorker;
    }

    public void setPrintMultiWorker( PrintMultiWorker printMultiWorker )
    {
        this.printMultiWorker = printMultiWorker;
    }

    // public void print( LockingWorkflowDTO lockingWorkflowDTO, String user, int autoPrint )
    // {
    // this.lockingWorkflowDTO = lockingWorkflowDTO;
    // this.user = user;
    // this.autoPrint = autoPrint;
    // printWorker.setWithPrint(false);
    // Platform.runLater( () -> printWorker.restart() );
    // }
    public void print( LockingWorkflowDTO lockingWorkflowDTO, String user, int autoPrint )
    {
        this.print( lockingWorkflowDTO, user, autoPrint, false, false );
    }

    public void print( LockingWorkflowDTO lockingWorkflowDTO, String user, int autoPrint, boolean withPrint, boolean withEMail )
    {
        this.lockingWorkflowDTO = lockingWorkflowDTO;
        this.user = user;
        this.autoPrint = autoPrint;
        this.withEMail = withEMail;
        this.withPrint = withPrint;
        Platform.runLater( () -> printWorker.restart() );
    }
    // public void print( List<LockingWorkflowDTO> lockingWorkflowDTOs, String user, int autoPrint ) {
    // this.lockingWorkflowDTOs = lockingWorkflowDTOs;
    // this.user = user;
    // this.autoPrint = autoPrint;
    // printMultiWorker.setWithPrint(false);
    // Platform.runLater( () -> printMultiWorker.restart() );
    // }

    public void print( List<LockingWorkflowDTO> lockingWorkflowDTOs, LockingWorkflowDTO dtoSelected, String user, int autoPrint, boolean withPrint, boolean withEMail/* , boolean mailPossible */ )
    {
        this.lockingWorkflowDTOs = lockingWorkflowDTOs;
        this.user = user;
        this.autoPrint = autoPrint;
        printMultiWorker.setDtoSelected( dtoSelected );
        this.withPrint = withPrint;
        this.withEMail = withEMail;
        Platform.runLater( () -> printMultiWorker.restart() );
    }

    private LockingWorkflowReport createLockingWorkflowReport() throws Exception
    {
        LockingWorkflowReport report = new LockingWorkflowReport();

        report.title = createTitle( lockingWorkflowDTO );

        report.owner = lockingWorkflowDTO.getOwner();
        report.kst = lockingWorkflowDTO.getKst();
        report.lot = lockingWorkflowDTO.getMaterial();
        report.sublot = "";
        report.invSuffix = "";
        report.scheduleNbr = lockingWorkflowDTO.getScheduleNbr();
        report.alloy = lockingWorkflowDTO.getAlloy();
        report.temper = lockingWorkflowDTO.getTemper();
        report.qualityCode = lockingWorkflowDTO.getQualityCode();
        report.gaugeOut = lockingWorkflowDTO.getGaugeOut();
        report.widthOut = lockingWorkflowDTO.getWidthOut();
        report.lengthOut = lockingWorkflowDTO.getLengthOut();
        report.defectTypeCat = lockingWorkflowDTO.getDefectTypeCat();
        report.defectTypeLoc = lockingWorkflowDTO.getDefectTypeLoc();
        report.defectTypeRea = lockingWorkflowDTO.getDefectTypeRea();
        report.scrapCodeDescription = lockingWorkflowDTO.getScrapCodeDescription();
        report.scrapAreaCodeDescription = lockingWorkflowDTO.getScrapAreaCodeDescription();
        report.customerName = lockingWorkflowDTO.getCustomerName();
        report.customerOrderNr = lockingWorkflowDTO.getCustomerOrderNr();
        report.orderDescription = lockingWorkflowDTO.getOrderDescription();
        report.finishGauge = lockingWorkflowDTO.getFinishGauge();
        report.orderedWidth = lockingWorkflowDTO.getOrderedWidth();
        report.materialNo = lockingWorkflowDTO.getMaterialNo();
        report.nextCostCenter = lockingWorkflowDTO.getNextCostCenter();
        report.lockDate = DateTimeUtil.asDate( lockingWorkflowDTO.getLockDate() );
        report.castDropNo = lockingWorkflowDTO.getCastDropNo();
        report.castHouseNo = lockingWorkflowDTO.getCastHouseNo();
        report.yearCastDrop = lockingWorkflowDTO.getYearCastDrop();
        report.castSampleNbr = lockingWorkflowDTO.getCastSampleNbr();
        report.dropId = "";
        report.cutId = null;
        report.paletteId = null;
        report.freeDate = DateTimeUtil.asDate( lockingWorkflowDTO.getFreeDate() );
        report.opSeq = null;
        report.userId = lockingWorkflowDTO.getUserId();
        report.materialStatus = lockingWorkflowDTO.getMaterialStatus();
        report.prodStartTs = lockingWorkflowDTO.getProdStartTs();
        report.prodEndTs = lockingWorkflowDTO.getProdEndTs();
        report.avStartTs = lockingWorkflowDTO.getAvStartTs();
        report.avEndTs = lockingWorkflowDTO.getAvEndTs();
        report.tcsStartTs = lockingWorkflowDTO.getTcsStartTs();
        report.tcsEndTs = lockingWorkflowDTO.getTcsEndTs();
        report.lockRecId = lockingWorkflowDTO.getLockRecId();
        report.opMessage = lockingWorkflowDTO.getOpMessage();
        report.scheduledOrder = lockingWorkflowDTO.getScheduledOrder();
        report.operationText = lockingWorkflowDTO.getOperationText();
        report.cbuCode = lockingWorkflowDTO.getCbuCode();
        report.kdServiceName = lockingWorkflowDTO.getKdServiceName();
        report.kdServiceTel = lockingWorkflowDTO.getKdServiceTel();
        report.code = lockingWorkflowDTO.getCode();
        report.ocDescription = lockingWorkflowDTO.getOcDescription();
        report.weightOut = lockingWorkflowDTO.getWeightOut().longValue();
        report.outputGauge = lockingWorkflowDTO.getOutputGauge();
        report.outputWidth = lockingWorkflowDTO.getOutputWidth();
        report.outputLength = lockingWorkflowDTO.getOutputLength();
        report.partNrCustomer = lockingWorkflowDTO.getPartNrCustomer();
        report.purchaseOrderNr = lockingWorkflowDTO.getPurchaseOrderNr();
        report.buildup = lockingWorkflowDTO.getBuildup();
        report.buildupMin = lockingWorkflowDTO.getBuildupMin();
        report.buildupMax = lockingWorkflowDTO.getBuildupMax();
        report.weight = lockingWorkflowDTO.getWeight().longValue();
        report.pdWeight = lockingWorkflowDTO.getPdWeight().longValue();
        report.exitArbor = lockingWorkflowDTO.getExitArbor();
        report.spool = lockingWorkflowDTO.getSpool();
        report.delWeekDemanded = lockingWorkflowDTO.getDelWeekDemanded();
        report.delYearDemanded = lockingWorkflowDTO.getDelYearDemanded();
        report.opHistId = lockingWorkflowDTO.getOpHistId();
        report.endCostCenter = lockingWorkflowDTO.getEndCostCenter();
        report.scrapClass = lockingWorkflowDTO.getScrapClass();
        String lockCommentString = StringEscapeUtils.unescapeJava( lockingWorkflowDTO.getLockComment() );
        if ( lockCommentString != null )
        {
            String[] lockComments = lockCommentString.split( "\\|" );
            for ( String lockCommentRow : lockComments )
            {
                int timeStart = lockCommentRow.indexOf( ' ' ) + 1;
                String user = lockCommentRow.substring( 0, timeStart - 1 );
                String time = lockCommentRow.substring( timeStart, timeStart + 19 );
                Date timestamp = null;
                try
                {
                    timestamp = TSDF.parse( time );
                }
                catch ( ParseException e )
                {
                    // ignore
                }
                String rest = lockCommentRow.substring( timeStart + 20 ).trim();
                report.addLockComment( timestamp, user, rest );
            }
        }

        // Test output for JasperReports
        // JAXBContext jaxbContext = JAXBContext.newInstance( LockingWorkflowReport.class );
        // Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        //
        // // output pretty printed
        // jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
        //
        // jaxbMarshaller.marshal( report, System.out );
        // System.out.println("builupMin" + lockingWorkflowDTO.getBuildupMin() + " buildupMax:" +
        // lockingWorkflowDTO.getBuildupMax());
        return report;
    }

    private LockingWorkflowReport createLockingWorkflowReport( LockingWorkflowDTO dtoParam ) throws Exception
    {
        LockingWorkflowReport report = new LockingWorkflowReport();

        report.title = createTitle( dtoParam );

        report.owner = dtoParam.getOwner();
        report.kst = dtoParam.getKst();
        report.lot = dtoParam.getMaterial();
        report.sublot = null;
        report.invSuffix = null;
        report.scheduleNbr = dtoParam.getScheduleNbr();
        report.alloy = dtoParam.getAlloy();
        report.temper = dtoParam.getTemper();
        report.qualityCode = dtoParam.getQualityCode();
        report.gaugeOut = dtoParam.getGaugeOut();
        report.widthOut = dtoParam.getWidthOut();
        report.lengthOut = dtoParam.getLengthOut();
        report.defectTypeCat = dtoParam.getDefectTypeCat();
        report.defectTypeLoc = dtoParam.getDefectTypeLoc();
        report.defectTypeRea = dtoParam.getDefectTypeRea();
        report.scrapCodeDescription = dtoParam.getScrapCodeDescription();
        report.scrapAreaCodeDescription = dtoParam.getScrapAreaCodeDescription();
        report.customerName = dtoParam.getCustomerName();
        report.customerOrderNr = dtoParam.getCustomerOrderNr();
        report.orderDescription = dtoParam.getOrderDescription();
        report.finishGauge = dtoParam.getFinishGauge();
        report.orderedWidth = dtoParam.getOrderedWidth();
        report.materialNo = dtoParam.getMaterialNo();
        report.nextCostCenter = dtoParam.getNextCostCenter();
        report.lockDate = DateTimeUtil.asDate( dtoParam.getLockDate() );
        report.castDropNo = dtoParam.getCastDropNo();
        report.castHouseNo = dtoParam.getCastHouseNo();
        report.yearCastDrop = dtoParam.getYearCastDrop();
        report.castSampleNbr = dtoParam.getCastSampleNbr();
        report.dropId = null;
        report.cutId = null;
        report.paletteId = null;
        report.freeDate = DateTimeUtil.asDate( dtoParam.getFreeDate() );
        report.opSeq = null;
        report.userId = dtoParam.getUserId();
        report.materialStatus = dtoParam.getMaterialStatus();
        report.prodStartTs = dtoParam.getProdStartTs();
        report.prodEndTs = dtoParam.getProdEndTs();
        report.avStartTs = dtoParam.getAvStartTs();
        report.avEndTs = dtoParam.getAvEndTs();
        report.tcsStartTs = dtoParam.getTcsStartTs();
        report.tcsEndTs = dtoParam.getTcsEndTs();
        report.lockRecId = dtoParam.getLockRecId();
        report.opMessage = dtoParam.getOpMessage();
        report.scheduledOrder = dtoParam.getScheduledOrder();
        report.operationText = dtoParam.getOperationText();
        report.cbuCode = dtoParam.getCbuCode();
        report.kdServiceName = dtoParam.getKdServiceName();
        report.kdServiceTel = dtoParam.getKdServiceTel();
        report.code = dtoParam.getCode();
        report.ocDescription = dtoParam.getOcDescription();
        report.weightOut = dtoParam.getWeightOut().longValue();
        report.outputGauge = dtoParam.getOutputGauge();
        report.outputWidth = dtoParam.getOutputWidth();
        report.outputLength = dtoParam.getOutputLength();
        report.partNrCustomer = dtoParam.getPartNrCustomer();
        report.purchaseOrderNr = dtoParam.getPurchaseOrderNr();
        report.buildup = dtoParam.getBuildup();
        report.buildupMin = dtoParam.getBuildupMin();
        report.buildupMax = dtoParam.getBuildupMax();
        report.weight = dtoParam.getWeight().longValue();
        report.pdWeight = dtoParam.getPdWeight().longValue();
        report.exitArbor = dtoParam.getExitArbor();
        report.spool = dtoParam.getSpool();
        report.delWeekDemanded = dtoParam.getDelWeekDemanded();
        report.delYearDemanded = dtoParam.getDelYearDemanded();
        report.opHistId = dtoParam.getOpHistId();
        report.endCostCenter = dtoParam.getEndCostCenter();
        report.scrapClass = dtoParam.getScrapClass();

        String lockCommentString = StringEscapeUtils.unescapeJava( dtoParam.getLockComment() );
        if ( lockCommentString != null )
        {
            String[] lockComments = lockCommentString.split( "\\|" );
            for ( String lockCommentRow : lockComments )
            {
                int timeStart = lockCommentRow.indexOf( ' ' ) + 1;
                String user = lockCommentRow.substring( 0, timeStart - 1 );
                String time = lockCommentRow.substring( timeStart, timeStart + 19 );
                Date timestamp = null;
                try
                {
                    timestamp = TSDF.parse( time );
                }
                catch ( ParseException e )
                {
                    // ignore
                }
                String rest = lockCommentRow.substring( timeStart + 20 ).trim();
                rest = replaceCommentOK( rest );
                report.addLockComment( timestamp, user, rest );
            }
        }

        // Test output for JasperReports
        // JAXBContext jaxbContext = JAXBContext.newInstance( LockingWorkflowReport.class );
        // Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        //
        // // output pretty printed
        // jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
        //
        // jaxbMarshaller.marshal( report, System.out );

        return report;
    }

    public class PrintWorker extends Service<File>
    {
        private Timer timer;

        @Override
        protected Task<File> createTask()
        {
            return new Task<File>()
            {
                @Override
                protected File call() throws Exception
                {
                    timer = new Timer( "progress", true );

                    timer.schedule( new TimerTask()
                    {
                        @Override
                        public void run()
                        {
                            Platform.runLater( () -> {
                                if ( isRunning() )
                                {
                                    progressDialog.showAndWait();
                                    progressDialog.close();
                                }
                            } );
                        }
                    }, 500 );

                    LockingWorkflowReport report = createLockingWorkflowReport();

                    StringWriter batchReportXMLStringWriter = null;
                    InputStream batchReportXMLInputStream = null;
                    InputStream reportStream = null;
                    OutputStream reportPDFOutputStream = null;

                    File reportPDF = null;
                    try
                    {
                        batchReportXMLStringWriter = new StringWriter();

                        JAXBContext jaxbContext = JAXBContext.newInstance( LockingWorkflowReport.class );
                        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

                        // output pretty printed
                        jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );

                        jaxbMarshaller.marshal( report, batchReportXMLStringWriter );
                        // Desktop.getDesktop().|

                        batchReportXMLInputStream = IOUtils.toInputStream( batchReportXMLStringWriter.toString(), Charset.forName( "UTF-8" ) );

                        reportStream = LockingWorkflowPrinter.class.getResourceAsStream( "/com/hydro/pdc/gui/report/LockingWorkflow.jasper" );

                        JasperReport jasperReport = (JasperReport) JRLoader.loadObject( reportStream );

                        Document document = JRXmlUtils.parse( batchReportXMLInputStream );

                        Map<String, Object> params = new HashMap<>();
                        params.put( JRXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, document );
                        params.put( JRXPathQueryExecuterFactory.XML_DATE_PATTERN, "dd.MM.yyyy HH:mm:ss" );
                        params.put( JRXPathQueryExecuterFactory.XML_NUMBER_PATTERN, "#0.#####;(#0.#####-)" );
                        params.put( JRXPathQueryExecuterFactory.XML_LOCALE, Locale.GERMAN );
                        params.put( JRParameter.REPORT_LOCALE, Locale.GERMAN );
                        JasperPrint jasperPrint = JasperFillManager.fillReport( jasperReport, params );

                        createPDFPath();

                        String mailName = Casting.LOCKING_WORKFLOW.PDF_PATH + lockingWorkflowDTO.createMailFileName().trim() + ".pdf";

                        reportPDF = new File( mailName );
                        JasperExportManager.exportReportToPdfFile( jasperPrint, mailName );

                    }
                    finally
                    {
                        if ( batchReportXMLStringWriter != null )
                        {
                            batchReportXMLStringWriter.close();
                        }
                        if ( batchReportXMLInputStream != null )
                        {
                            batchReportXMLInputStream.close();
                        }
                        if ( reportStream != null )
                        {
                            reportStream.close();
                        }
                        if ( reportPDFOutputStream != null )
                        {
                            reportPDFOutputStream.close();
                        }
                    }

                    timer.cancel();

                    return reportPDF;
                }

                @Override
                protected void cancelled()
                {
                }

                @Override
                protected void succeeded()
                {
                    File reportPDF = null;
                    try
                    {
                        reportPDF = get();
                    }
                    catch ( InterruptedException e )
                    {
                        e.printStackTrace();
                    }
                    catch ( ExecutionException e )
                    {
                        e.printStackTrace();
                    }
                    if ( reportPDF == null )
                    {
                        if ( progressDialog.isShowing() )
                        {
                            progressDialog.hide();
                        }

                        return;
                    }
                    if ( progressDialog.isShowing() )
                    {
                        progressDialog.hide();
                    }

                    try
                    {
                        if ( withPrint )
                        {
                            Desktop.getDesktop().print( reportPDF );
                        }

                        if ( withEMail )
                        {
                            execOutlook( lockingWorkflowDTO );
                        }
                    }
                    catch ( Exception e )
                    {
                        e.printStackTrace();
                        errorManager.handleException( "Fehler beim Drucken", e );
                    }
                }

                @Override
                protected void failed()
                {
                    timer.cancel();
                    if ( progressDialog.isShowing() )
                    {
                        progressDialog.hide();
                    }
                    if ( getException() != null )
                    {
                        errorManager.handleException( "Fehler beim Drucken", getException() );
                    }
                }
            };
        }

    }

    public class PrintMultiWorker extends Service<File>
    {
        private Timer timer;
        private LockingWorkflowDTO dtoSelected;

        @Override
        protected Task<File> createTask()
        {
            return new Task<File>()
            {
                @Override
                protected File call() throws Exception
                {
                    timer = new Timer( "progress", true );

                    timer.schedule( new TimerTask()
                    {
                        @Override
                        public void run()
                        {
                            Platform.runLater( () -> {
                                if ( isRunning() )
                                {
                                    progressDialog.showAndWait();
                                    progressDialog.close();
                                }
                            } );
                        }
                    }, 500 );

                    StringWriter batchReportXMLStringWriter = null;
                    InputStream batchReportXMLInputStream = null;
                    InputStream reportStream = null;
                    OutputStream reportPDFOutputStream = null;

                    File reportPDF = null;

                    try
                    {
                        List<JasperPrint> jasperPrints = new ArrayList<JasperPrint>();
                        for ( LockingWorkflowDTO dto : lockingWorkflowDTOs )
                        {
                            LockingWorkflowReport report = createLockingWorkflowReport( dto );
                            batchReportXMLStringWriter = new StringWriter();

                            JAXBContext jaxbContext = JAXBContext.newInstance( LockingWorkflowReport.class );
                            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

                            // output pretty printed
                            jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );

                            jaxbMarshaller.marshal( report, batchReportXMLStringWriter );
                            // Desktop.getDesktop().|

                            batchReportXMLInputStream = IOUtils.toInputStream( batchReportXMLStringWriter.toString(), Charset.forName( "UTF-8" ) );

                            reportStream = LockingWorkflowPrinter.class.getResourceAsStream( "/com/hydro/pdc/gui/report/LockingWorkflow.jasper" );

                            JasperReport jasperReport = (JasperReport) JRLoader.loadObject( reportStream );

                            Document document = JRXmlUtils.parse( batchReportXMLInputStream );

                            Map<String, Object> params = new HashMap<>();
                            params.put( JRXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, document );
                            params.put( JRXPathQueryExecuterFactory.XML_DATE_PATTERN, "dd.MM.yyyy HH:mm:ss" );
                            params.put( JRXPathQueryExecuterFactory.XML_NUMBER_PATTERN, "#0.#####;(#0.#####-)" );
                            params.put( JRXPathQueryExecuterFactory.XML_LOCALE, Locale.GERMAN );
                            params.put( JRParameter.REPORT_LOCALE, Locale.GERMAN );
                            JasperPrint jasperPrint = JasperFillManager.fillReport( jasperReport, params );
                            jasperPrints.add( jasperPrint );
                        }

                        JasperPrint jasperPrintAll = jasperPrints.get( 0 );
                        int quantPages = jasperPrints.size();

                        for ( int index = 1; index < quantPages; index++ )
                        {
                            List<JRPrintPage> pages = jasperPrints.get( index ).getPages();
                            for ( JRPrintPage page : pages )
                            {
                                jasperPrintAll.addPage( page );
                            }
                        }

                        createPDFPath();

                        String mailName = Casting.LOCKING_WORKFLOW.PDF_PATH + lockingWorkflowDTOs.get( 0 ).createMailFileName().trim() + ".pdf";
                        reportPDF = new File( mailName );

                        reportPDFOutputStream = new FileOutputStream( reportPDF );
                        JasperExportManager.exportReportToPdfStream( jasperPrintAll, reportPDFOutputStream );
                    }
                    finally
                    {
                        if ( batchReportXMLStringWriter != null )
                        {
                            batchReportXMLStringWriter.close();
                        }
                        if ( batchReportXMLInputStream != null )
                        {
                            batchReportXMLInputStream.close();
                        }
                        if ( reportStream != null )
                        {
                            reportStream.close();
                        }
                        if ( reportPDFOutputStream != null )
                        {
                            reportPDFOutputStream.close();
                        }
                    }

                    timer.cancel();

                    return reportPDF;
                }

                @Override
                protected void cancelled()
                {
                }

                @Override
                protected void succeeded()
                {
                    File reportPDF = null;
                    try
                    {
                        reportPDF = get();
                    }
                    catch ( InterruptedException e )
                    {
                        e.printStackTrace();
                    }
                    catch ( ExecutionException e )
                    {
                        e.printStackTrace();
                    }
                    if ( reportPDF == null )
                    {
                        if ( progressDialog.isShowing() )
                        {
                            progressDialog.hide();
                        }

                        return;
                    }
                    if ( progressDialog.isShowing() )
                    {
                        progressDialog.hide();
                    }

                    try
                    {
                        if ( withPrint )
                        {
                            Desktop.getDesktop().print( reportPDF );
                        }

                        if ( withEMail )
                        {
                            execOutlook( dtoSelected );
                        }
                    }
                    catch ( Exception e )
                    {
                        errorManager.handleException( "Fehler beim Drucken", e );
                    }
                }

                @Override
                protected void failed()
                {
                    timer.cancel();
                    if ( progressDialog.isShowing() )
                    {
                        progressDialog.hide();
                    }
                    if ( getException() != null )
                    {
                        errorManager.handleException( "Fehler beim Drucken", getException() );
                    }
                }
            };
        }

        private void setDtoSelected( LockingWorkflowDTO dtoSelected )
        {
            this.dtoSelected = dtoSelected;
        }
    }

    public String createTitle( LockingWorkflowDTO lockingWorkflowDTO )
    {
        String title = "";
        String state = "";
        switch ( autoPrint )
        {
        case LWAbstractTask.NOT_AUTO_PRINT:
            title = "";
            break;

        case LWAbstractTask.AFTER_SCRAPPING:
            title = SCRAPPING;
            break;
        case LWAbstractTask.AFTER_FREEING:
            title = FREEING_BEFORE_PACKING;
            break;
        default:
            title = "";
            break;
        }
        title = title + BAND;
        title += lockingWorkflowDTO.getMaterial();
        title += " (";
        if ( lockingWorkflowDTO.getMaterialStatus() != null && lockingWorkflowDTO.getMaterialStatus().startsWith( Casting.LOCKING_WORKFLOW.CONTAINER_MARK ) )
        {
            state = "An Container gebucht";
        }
        else if ( lockingWorkflowDTO.getMaterialStatus() != null && lockingWorkflowDTO.getMaterialStatus().startsWith( Casting.LOCKING_WORKFLOW.SCRAP_MARK ) )
        {
            state = "Verschrottet";
        }
        else
        {
            state = lockingWorkflowDTO.getFreeDate() == null ? LOCKED : FREE;
        }
        title += state;
        title += ")   ";
        title += USER;
        title += user;
        return title;
    }

    private String replaceCommentOK( String comment )
    {
        String printComment = comment;
        HashMap<String, String> okComment = new HashMap<String, String>();
        okComment.put( "" + Casting.LOCKING_WORKFLOW.FREE_MARK, Casting.LOCKING_WORKFLOW.FREE_OK );
        okComment.put( "" + Casting.LOCKING_WORKFLOW.SCRAP_MARK, Casting.LOCKING_WORKFLOW.SCRAP_OK );
        okComment.put( "" + Casting.LOCKING_WORKFLOW.SCRAP_MARK + Casting.LOCKING_WORKFLOW.TABG_MARK, Casting.LOCKING_WORKFLOW.SCRAP_TABG_OK );
        okComment.put( "" + Casting.LOCKING_WORKFLOW.CONTAINER_MARK, Casting.LOCKING_WORKFLOW.CONTAINER_OK );
        okComment.put( "" + Casting.LOCKING_WORKFLOW.CONTAINER_MARK + Casting.LOCKING_WORKFLOW.TABG_MARK, Casting.LOCKING_WORKFLOW.CONTAINER_TABG_OK );

        String[] commentMark = { Casting.LOCKING_WORKFLOW.COMMENT_MARK, Casting.LOCKING_WORKFLOW.COMMENT_MARK_OLD };// ,

        Iterator<String> iterator = okComment.keySet().iterator();

        while ( iterator.hasNext() )
        {
            String type = iterator.next();
            for ( int i = 0; i < commentMark.length; i++ )
            {
                String mark = commentMark[i] + type + Casting.LOCKING_WORKFLOW.OK;
                int n = comment.indexOf( mark );
                if ( n != -1 )
                {
                    String message = okComment.get( type );
                    printComment = comment.substring( 0, n ) + "\n" + message + " " + comment.substring( n + mark.length() );

                }
            }
        }

        for ( int i = 0; i < commentMark.length; i++ )
        {

            String avMark = commentMark[i] + Casting.LOCKING_WORKFLOW.AV_MARK;
            int nAVMark = printComment.indexOf( avMark );
            if ( nAVMark != -1 )
            {
                printComment = printComment.substring( 0, nAVMark );
            }
        }

        for ( int i = 0; i < commentMark.length; i++ )
        {
            String errorMark = commentMark[i] + Casting.LOCKING_WORKFLOW.ERROR_MARK;
            int nErrorMark = printComment.indexOf( errorMark );
            if ( nErrorMark != -1 )
            {
                printComment = printComment.substring( 0, nErrorMark ) + "\n" + " Fehler: " + "\n" + printComment.substring( nErrorMark + errorMark.length() );
            }

        }
        return printComment;
    }

    public boolean execOutlook( LockingWorkflowDTO lockingWorkflowDTO )
    {
        String attachment = Casting.LOCKING_WORKFLOW.PDF_PATH + lockingWorkflowDTO.createMailFileName().trim() + ".pdf";
        String pfad = WindowsRegistry.readRegistry( REGISTRY_OUTLOOK );
        String argument = pfad + " /a " + "\"" + attachment + "\"";
        try
        {
            Runtime.getRuntime().exec( argument );
            return true;
        }
        catch ( IOException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    private void createPDFPath()
    {
        File dir = new File( Casting.LOCKING_WORKFLOW.PDF_PATH );
        if ( !dir.exists() )
        {
            dir.mkdir();
        }
    }

}
