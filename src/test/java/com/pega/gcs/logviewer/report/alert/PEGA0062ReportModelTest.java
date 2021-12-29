
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0062ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0062ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Data flow record processing time exceeds threshold of 10000 ms: 10071 ms. For data flow [GetCustomerAndSave]. Da"
                + "ta flow metrics are: {\"stageMetrics\":[{\"stageId\":\"Source5\",\"dataFlowKeys\":{\"className\":\"ABXMKT-Data-Customer\""
                + ",\"name\":\"GetCustomer\"},\"stageName\":\"Data-Decision-IH-Fact-Aggregate\",\"recordsIn\":13,\"recordsOut\":13,\"errors"
                + "Count\":0,\"lastRecordTimestamp\":1465300810975,\"executionTime\":994419},{\"stageId\":\"Source4\",\"dataFlowKeys\":{\"c"
                + "lassName\":\"ABXMKT-Data-Customer\",\"name\":\"GetCustomer\"},\"stageName\":\"ABXMKT-Data-Customer-AccountEvent\",\"reco"
                + "rdsIn\":0,\"recordsOut\":0,\"errorsCount\":0,\"lastRecordTimestamp\":0,\"executionTime\":0},{\"stageId\":\"Source3\",\"d"
                + "ataFlowKeys\":{\"className\":\"ABXMKT-Data-Customer\",\"name\":\"GetCustomer\"},\"stageName\":\"ABXMKT-Data-Customer-Eve"
                + "nt\",\"recordsIn\":2,\"recordsOut\":2,\"errorsCount\":0,\"lastRecordTimestamp\":1465300810908,\"executionTime\":173930},"
                + "{\"stageId\":\"Source2\",\"dataFlowKeys\":{\"className\":\"ABXMKT-Data-Customer\",\"name\":\"GetCustomer\"},\"stageName\""
                + ":\"ABXMKT-Data-Customer-ProductHolding\",\"recordsIn\":6,\"recordsOut\":6,\"errorsCount\":0,\"lastRecordTimestamp\":1465"
                + "300810797,\"executionTime\":4286921},{\"stageId\":\"Source1\",\"dataFlowKeys\":{\"className\":\"ABXMKT-Data-Customer\",\""
                + "name\":\"GetCustomer\"},\"stageName\":\"ABXMKT-Data-Customer\",\"recordsIn\":1,\"recordsOut\":1,\"errorsCount\":0,\"last"
                + "RecordTimestamp\":1465300801096,\"executionTime\":0},{\"stageId\":\"Source\",\"dataFlowKeys\":{\"className\":\"ABXMKT-Da"
                + "ta-Customer\",\"name\":\"GetCustomer\"},\"stageName\":\"ABXMKT-Data-Customer\",\"recordsIn\":1,\"recordsOut\":1,\"errors"
                + "Count\":0,\"lastRecordTimestamp\":1465300800934,\"executionTime\":10071325214},{\"stageId\":\"Merge1\",\"dataFlowKeys\":"
                + "{\"className\":\"ABXMKT-Data-Customer\",\"name\":\"GetCustomer\"},\"stageName\":\"ABXMKT-Data-Customer with ABXMKT-Data-"
                + "Customer\",\"recordsIn\":1,\"recordsOut\":1,\"errorsCount\":0,\"lastRecordTimestamp\":1465300801115,\"executionTime\":10"
                + "071323288},{\"stageId\":\"Compose1\",\"dataFlowKeys\":{\"className\":\"ABXMKT-Data-Customer\",\"name\":\"GetCustomer\"},"
                + "\"stageName\":\"ABXMKT-Data-Customer with ABXMKT-Data-Customer with ABXMKT-Data-Customer-ProductHolding\",\"recordsIn\":"
                + "1,\"recordsOut\":1,\"errorsCount\":0,\"lastRecordTimestamp\":1465300810847,\"executionTime\":9889741244},{\"stageId\":\""
                + "Compose2\",\"dataFlowKeys\":{\"className\":\"ABXMKT-Data-Customer\",\"name\":\"GetCustomer\"},\"stageName\":\"ABXMKT-Dat"
                + "a-Customer with ABXMKT-Data-Customer with ABXMKT-Data-Customer-ProductHolding with ABXMKT-Data-Customer-Event\",\"record"
                + "sIn\":1,\"recordsOut\":1,\"errorsCount\":0,\"lastRecordTimestamp\":1465300810931,\"executionTime\":158452377},{\"stageId"
                + "\":\"Compose3\",\"dataFlowKeys\":{\"className\":\"ABXMKT-Data-Customer\",\"name\":\"GetCustomer\"},\"stageName\":\"ABXMK"
                + "T-Data-Customer with ABXMKT-Data-Customer with ABXMKT-Data-Customer-ProductHolding with ABXMKT-Data-Customer-Event with "
                + "ABXMKT-Data-Customer-AccountEvent\",\"recordsIn\":1,\"recordsOut\":1,\"errorsCount\":0,\"lastRecordTimestamp\":146530081"
                + "0959,\"executionTime\":74103479},{\"stageId\":\"Compose4\",\"dataFlowKeys\":{\"className\":\"ABXMKT-Data-Customer\",\"na"
                + "me\":\"GetCustomer\"},\"stageName\":\"ABXMKT-Data-Customer with ABXMKT-Data-Customer with ABXMKT-Data-Customer-ProductHo"
                + "lding with ABXMKT-Data-Customer-Event with ABXMKT-Data-Customer-AccountEvent with Data-Decision-IH-Fact-Aggregate\",\"re"
                + "cordsIn\":1,\"recordsOut\":1,\"errorsCount\":0,\"lastRecordTimestamp\":1465300810977,\"executionTime\":45922392},{\"stag"
                + "eId\":\"Destination\",\"dataFlowKeys\":{\"className\":\"ABXMKT-Data-Customer\",\"name\":\"GetCustomer\"},\"stageName\":\""
                + "ABXMKT-Data-Customer\",\"recordsIn\":1,\"recordsOut\":1,\"errorsCount\":0,\"lastRecordTimestamp\":1465300810977,\"execut"
                + "ionTime\":28624700},{\"stageId\":\"Source\",\"dataFlowKeys\":{\"className\":\"ABXMKT-Data-Customer\",\"name\":\"GetCusto"
                + "merAndSave\"},\"stageName\":\"GetCustomer\",\"recordsIn\":1,\"recordsOut\":1,\"errorsCount\":0,\"lastRecordTimestamp\":1"
                + "465300810977,\"executionTime\":28623376},{\"stageId\":\"Output1\",\"dataFlowKeys\":{\"className\":\"ABXMKT-Data-Customer"
                + "\",\"name\":\"GetCustomerAndSave\"},\"stageName\":\"CustomerDDS\",\"recordsIn\":1,\"recordsOut\":0,\"errorsCount\":0,\"l"
                + "astRecordTimestamp\":0,\"executionTime\":28616477},{\"stageId\":\"Destination\",\"dataFlowKeys\":{\"className\":\"ABXMKT"
                + "-Data-Customer\",\"name\":\"GetCustomerAndSave\"},\"stageName\":\"ABXMKT-Data-Customer\",\"recordsIn\":1,\"recordsOut\":"
                + "1,\"errorsCount\":0,\"lastRecordTimestamp\":1465300811005,\"executionTime\":3454}],\"throughput\":0,\"recordsProcessed\""
                + ":1,\"timer\":{\"startTime\":1465300800934,\"endTime\":1465300811005},\"status\":\"New\",\"errorsCount\":0}";

        LOG.info("dataText: " + dataText);

        PEGA0062ReportModel pega0062ReportModel = new PEGA0062ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0062ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("GetCustomerAndSave", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "Data flow record processing time exceeds threshold of 10000 ms: 582049 ms. For data flow [pxDelayedLearningFlow]."
                + " Data flow metrics are: {\"stageMetrics\":[{\"stageId\":\"Source\",\"dataFlowKeys\":{\"className\":\"Data-Decision-Result"
                + "s\",\"name\":\"pxDelayedLearningFlow\"},\"stageName\":\"Decision Results\",\"recordsIn\":2,\"recordsOut\":2,\"errorsCount"
                + "\":0,\"lastRecordTimestamp\":1630445506834,\"avgTimePerRecord\":0.0,\"internalMetrics\":{\"@class\":\"com.pega.dsm.dnode."
                + "api.metrics.PerformanceMetrics$NonMeasuringMetrics\"},\"executionTime\":0,\"withDetailedMetrics\":false},{\"stageId\":\"T"
                + "ransform1\",\"dataFlowKeys\":{\"className\":\"Data-Decision-Results\",\"name\":\"pxDelayedLearningFlow\"},\"stageName\":"
                + "\"Preserve Original Rank\",\"recordsIn\":2,\"recordsOut\":2,\"errorsCount\":0,\"lastRecordTimestamp\":1630445506834,\"avg"
                + "TimePerRecord\":104.021,\"internalMetrics\":{\"@class\":\"com.pega.dsm.dnode.api.metrics.PerformanceMetrics$NonMeasuringM"
                + "etrics\"},\"executionTime\":208042,\"withDetailedMetrics\":false},{\"stageId\":\"Convert1\",\"dataFlowKeys\":{\"className"
                + "\":\"Data-Decision-Results\",\"name\":\"pxDelayedLearningFlow\"},\"stageName\":\"Event summary\",\"recordsIn\":2,\"record"
                + "sOut\":2,\"errorsCount\":0,\"lastRecordTimestamp\":1630445506834,\"avgTimePerRecord\":74.046,\"internalMetrics\":{\"@clas"
                + "s\":\"com.pega.dsm.dnode.api.metrics.PerformanceMetrics$NonMeasuringMetrics\"},\"executionTime\":148092,\"withDetailedMet"
                + "rics\":false},{\"stageId\":\"Destination\",\"dataFlowKeys\":{\"className\":\"Data-Decision-Results\",\"name\":\"pxDelayed"
                + "LearningFlow\"},\"stageName\":\"Event store\",\"recordsIn\":2,\"recordsOut\":0,\"errorsCount\":0,\"lastRecordTimestamp\":"
                + "0,\"avgTimePerRecord\":1350.9695,\"internalMetrics\":{\"@class\":\"com.pega.dsm.dnode.api.metrics.PerformanceMetrics$NonM"
                + "easuringMetrics\"},\"executionTime\":2701939,\"withDetailedMetrics\":false},{\"stageId\":\"Convert3\",\"dataFlowKeys\":{\""
                + "className\":\"Data-Decision-Results\",\"name\":\"pxDelayedLearningFlow\"},\"stageName\":\"Data-pxStrategyResult\",\"recor"
                + "dsIn\":2,\"recordsOut\":2,\"errorsCount\":0,\"lastRecordTimestamp\":1630445506834,\"avgTimePerRecord\":11.364,\"internalM"
                + "etrics\":{\"@class\":\"com.pega.dsm.dnode.api.metrics.PerformanceMetrics$NonMeasuringMetrics\"},\"executionTime\":22728,"
                + "\"withDetailedMetrics\":false},{\"stageId\":\"Filter2\",\"dataFlowKeys\":{\"className\":\"Data-Decision-Results\",\"name"
                + "\":\"pxDelayedLearningFlow\"},\"stageName\":\"Campaign Optimization\",\"recordsIn\":2,\"recordsOut\":0,\"errorsCount\":0,"
                + "\"lastRecordTimestamp\":0,\"avgTimePerRecord\":10.1615,\"internalMetrics\":{\"@class\":\"com.pega.dsm.dnode.api.metrics.P"
                + "erformanceMetrics$NonMeasuringMetrics\"},\"executionTime\":20323,\"withDetailedMetrics\":false},{\"stageId\":\"Output1\","
                + "\"dataFlowKeys\":{\"className\":\"Data-Decision-Results\",\"name\":\"pxDelayedLearningFlow\"},\"stageName\":\"Decision Re"
                + "sults\",\"recordsIn\":2,\"recordsOut\":0,\"errorsCount\":0,\"lastRecordTimestamp\":0,\"avgTimePerRecord\":7287.1035,\"int"
                + "ernalMetrics\":{\"@class\":\"com.pega.dsm.dnode.api.metrics.PerformanceMetrics$NonMeasuringMetrics\"},\"executionTime\":1"
                + "4574207,\"withDetailedMetrics\":false},{\"stageId\":\"Convert2\",\"dataFlowKeys\":{\"className\":\"Data-Decision-Results"
                + "\",\"name\":\"pxDelayedLearningFlow\"},\"stageName\":\"Data-pxStrategyResult\",\"recordsIn\":2,\"recordsOut\":2,\"errorsC"
                + "ount\":0,\"lastRecordTimestamp\":1630445506834,\"avgTimePerRecord\":6.4575000000000005,\"internalMetrics\":{\"@class\":\""
                + "com.pega.dsm.dnode.api.metrics.PerformanceMetrics$NonMeasuringMetrics\"},\"executionTime\":12915,\"withDetailedMetrics\":"
                + "false},{\"stageId\":\"Filter1\",\"dataFlowKeys\":{\"className\":\"Data-Decision-Results\",\"name\":\"pxDelayedLearningFlo"
                + "w\"},\"stageName\":\"Check Init Optimization flag\",\"recordsIn\":2,\"recordsOut\":0,\"errorsCount\":0,\"lastRecordTimest"
                + "amp\":0,\"avgTimePerRecord\":3.3930000000000002,\"internalMetrics\":{\"@class\":\"com.pega.dsm.dnode.api.metrics.Performa"
                + "nceMetrics$NonMeasuringMetrics\"},\"executionTime\":6786,\"withDetailedMetrics\":false},{\"stageId\":\"Source\",\"dataFlo"
                + "wKeys\":{\"className\":\"Data-pxStrategyResult\",\"name\":\"pxQueueUpSOCDecisions\"},\"stageName\":\"Data-pxStrategyResul"
                + "t\",\"recordsIn\":0,\"recordsOut\":0,\"errorsCount\":0,\"lastRecordTimestamp\":0,\"avgTimePerRecord\":0.0,\"internalMetri"
                + "cs\":{\"@class\":\"com.pega.dsm.dnode.api.metrics.PerformanceMetrics$NonMeasuringMetrics\"},\"executionTime\":0,\"withDet"
                + "ailedMetrics\":false},{\"stageId\":\"Convert1\",\"dataFlowKeys\":{\"className\":\"Data-pxStrategyResult\",\"name\":\"pxQu"
                + "eueUpSOCDecisions\"},\"stageName\":\"Data-pxStrategyResult\",\"recordsIn\":0,\"recordsOut\":0,\"errorsCount\":0,\"lastRec"
                + "ordTimestamp\":0,\"avgTimePerRecord\":0.0,\"internalMetrics\":{\"@class\":\"com.pega.dsm.dnode.api.metrics.PerformanceMet"
                + "rics$NonMeasuringMetrics\"},\"executionTime\":0,\"withDetailedMetrics\":false},{\"stageId\":\"Convert2\",\"dataFlowKeys\""
                + ":{\"className\":\"Data-pxStrategyResult\",\"name\":\"pxQueueUpSOCDecisions\"},\"stageName\":\"Data-pxStrategyResult\",\"r"
                + "ecordsIn\":0,\"recordsOut\":0,\"errorsCount\":0,\"lastRecordTimestamp\":0,\"avgTimePerRecord\":0.0,\"internalMetrics\":{"
                + "\"@class\":\"com.pega.dsm.dnode.api.metrics.PerformanceMetrics$NonMeasuringMetrics\"},\"executionTime\":0,\"withDetailedM"
                + "etrics\":false},{\"stageId\":\"Output1\",\"dataFlowKeys\":{\"className\":\"Data-pxStrategyResult\",\"name\":\"pxQueueUpSO"
                + "CDecisions\"},\"stageName\":\"Decision Time Per Stage\",\"recordsIn\":0,\"recordsOut\":0,\"errorsCount\":0,\"lastRecordTi"
                + "mestamp\":0,\"avgTimePerRecord\":0.0,\"internalMetrics\":{\"@class\":\"com.pega.dsm.dnode.api.metrics.PerformanceMetrics$"
                + "NonMeasuringMetrics\"},\"executionTime\":0,\"withDetailedMetrics\":false},{\"stageId\":\"Source\",\"dataFlowKeys\":{\"cla"
                + "ssName\":\"Data-pxStrategyResult\",\"name\":\"pyWriteToInteractionsStream\"},\"stageName\":\"Data-pxStrategyResult\",\"re"
                + "cordsIn\":0,\"recordsOut\":0,\"errorsCount\":0,\"lastRecordTimestamp\":0,\"avgTimePerRecord\":0.0,\"internalMetrics\":{\""
                + "@class\":\"com.pega.dsm.dnode.api.metrics.PerformanceMetrics$NonMeasuringMetrics\"},\"executionTime\":0,\"withDetailedMet"
                + "rics\":false},{\"stageId\":\"Destination\",\"dataFlowKeys\":{\"className\":\"Data-pxStrategyResult\",\"name\":\"pyWriteTo"
                + "InteractionsStream\"},\"stageName\":\"Interactions Stream\",\"recordsIn\":0,\"recordsOut\":0,\"errorsCount\":0,\"lastReco"
                + "rdTimestamp\":0,\"avgTimePerRecord\":0.0,\"internalMetrics\":{\"@class\":\"com.pega.dsm.dnode.api.metrics.PerformanceMetr"
                + "ics$NonMeasuringMetrics\"},\"executionTime\":0,\"withDetailedMetrics\":false},{\"stageId\":\"Source\",\"dataFlowKeys\":{\""
                + "className\":\"Data-pxStrategyResult\",\"name\":\"pxQueueUpDecisions\"},\"stageName\":\"Data-pxStrategyResult\",\"recordsI"
                + "n\":0,\"recordsOut\":0,\"errorsCount\":0,\"lastRecordTimestamp\":0,\"avgTimePerRecord\":0.0,\"internalMetrics\":{\"@class"
                + "\":\"com.pega.dsm.dnode.api.metrics.PerformanceMetrics$NonMeasuringMetrics\"},\"executionTime\":0,\"withDetailedMetrics\""
                + ":false},{\"stageId\":\"Transform1\",\"dataFlowKeys\":{\"className\":\"Data-pxStrategyResult\",\"name\":\"pxQueueUpDecisio"
                + "ns\"},\"stageName\":\"Handle empty outcome\",\"recordsIn\":0,\"recordsOut\":0,\"errorsCount\":0,\"lastRecordTimestamp\":0"
                + ",\"avgTimePerRecord\":0.0,\"internalMetrics\":{\"@class\":\"com.pega.dsm.dnode.api.metrics.PerformanceMetrics$NonMeasurin"
                + "gMetrics\"},\"executionTime\":0,\"withDetailedMetrics\":false},{\"stageId\":\"Convert2\",\"dataFlowKeys\":{\"className\":"
                + "\"Data-pxStrategyResult\",\"name\":\"pxQueueUpDecisions\"},\"stageName\":\"Data-pxStrategyResult\",\"recordsIn\":0,\"reco"
                + "rdsOut\":0,\"errorsCount\":0,\"lastRecordTimestamp\":0,\"avgTimePerRecord\":0.0,\"internalMetrics\":{\"@class\":\"com.peg"
                + "a.dsm.dnode.api.metrics.PerformanceMetrics$NonMeasuringMetrics\"},\"executionTime\":0,\"withDetailedMetrics\":false},{\"s"
                + "tageId\":\"Filter1\",\"dataFlowKeys\":{\"className\":\"Data-pxStrategyResult\",\"name\":\"pxQueueUpDecisions\"},\"stageNa"
                + "me\":\"Trigger Next Stage\",\"recordsIn\":0,\"recordsOut\":0,\"errorsCount\":0,\"lastRecordTimestamp\":0,\"avgTimePerReco"
                + "rd\":0.0,\"internalMetrics\":{\"@class\":\"com.pega.dsm.dnode.api.metrics.PerformanceMetrics$NonMeasuringMetrics\"},\"exe"
                + "cutionTime\":0,\"withDetailedMetrics\":false},{\"stageId\":\"Convert1\",\"dataFlowKeys\":{\"className\":\"Data-pxStrategy"
                + "Result\",\"name\":\"pxQueueUpDecisions\"},\"stageName\":\"Data-pxStrategyResult\",\"recordsIn\":0,\"recordsOut\":0,\"erro"
                + "rsCount\":0,\"lastRecordTimestamp\":0,\"avgTimePerRecord\":0.0,\"internalMetrics\":{\"@class\":\"com.pega.dsm.dnode.api.m"
                + "etrics.PerformanceMetrics$NonMeasuringMetrics\"},\"executionTime\":0,\"withDetailedMetrics\":false},{\"stageId\":\"Output"
                + "1\",\"dataFlowKeys\":{\"className\":\"Data-pxStrategyResult\",\"name\":\"pxQueueUpDecisions\"},\"stageName\":\"Decision T"
                + "ime Per Stage\",\"recordsIn\":0,\"recordsOut\":0,\"errorsCount\":0,\"lastRecordTimestamp\":0,\"avgTimePerRecord\":0.0,\"i"
                + "nternalMetrics\":{\"@class\":\"com.pega.dsm.dnode.api.metrics.PerformanceMetrics$NonMeasuringMetrics\"},\"executionTime\""
                + ":0,\"withDetailedMetrics\":false},{\"stageId\":\"Source\",\"dataFlowKeys\":{\"className\":\"Data-pxStrategyResult\",\"nam"
                + "e\":\"pyWriteToInteractionsStream\"},\"stageName\":\"Data-pxStrategyResult\",\"recordsIn\":0,\"recordsOut\":0,\"errorsCou"
                + "nt\":0,\"lastRecordTimestamp\":0,\"avgTimePerRecord\":0.0,\"internalMetrics\":{\"@class\":\"com.pega.dsm.dnode.api.metric"
                + "s.PerformanceMetrics$NonMeasuringMetrics\"},\"executionTime\":0,\"withDetailedMetrics\":false},{\"stageId\":\"Destination"
                + "\",\"dataFlowKeys\":{\"className\":\"Data-pxStrategyResult\",\"name\":\"pyWriteToInteractionsStream\"},\"stageName\":\"In"
                + "teractions Stream\",\"recordsIn\":0,\"recordsOut\":0,\"errorsCount\":0,\"lastRecordTimestamp\":0,\"avgTimePerRecord\":0.0"
                + ",\"internalMetrics\":{\"@class\":\"com.pega.dsm.dnode.api.metrics.PerformanceMetrics$NonMeasuringMetrics\"},\"executionTi"
                + "me\":0,\"withDetailedMetrics\":false}],\"recordsProcessed\":2,\"timer\":{\"startTime\":1630444342742,\"endTime\":16304455"
                + "06840},\"status\":\"NEW\",\"cpuTimeSpeed\":97.44240958708583,\"avgTimePerRecord\":10262.472,\"lastUpdateTime\":1630445506"
                + "834,\"lag\":-1,\"stale\":false,\"errorsCount\":0,\"processingWatch\":{\"elapsedNanos\":20524944,\"timesCalled\":2},\"flus"
                + "hWatch\":{\"elapsedNanos\":0,\"timesCalled\":0},\"idleWatch\":{\"elapsedNanos\":0,\"timesCalled\":0},\"withDetailedMetric"
                + "s\":false}";

        LOG.info("dataText: " + dataText);

        PEGA0062ReportModel pega0062ReportModel = new PEGA0062ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0062ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("pxDelayedLearningFlow", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString3() {

        String dataText = "Data flow record processing time exceeds threshold of 10000 ms: 11198 ms. For data flow [SingleCustomerData] run "
                + "[SC-98] node [10.11.12.100_getnba_server01]. Slowest stage [CustomerUsage] with avg 1336.1 ms over 1 records. SLA violati"
                + "ons since last alert: 30 (40.0% of requests). Data flow metrics are: {\"stageMetrics\":[{\"stageId\":\"Source5\",\"dataFl"
                + "owKeys\":{\"className\":\"ABCD-Data-Customer\",\"name\":\"MobileCustomerDataCassandra\"},\"stageName\":\"ABCDDDSURLTaggin"
                + "g\",\"movingAverage\":1287981.0,\"recordsIn\":1,\"recordsOut\":0,\"errorsCount\":0,\"lastRecordTimestamp\":1632396505318,"
                + "\"executionTime\":1287981,\"avgTimePerRecord\":1287.981,\"stale\":false,\"withDetailedMetrics\":false,\"internalMetrics\""
                + ":{\"@class\":\"com.pega.dsm.dnode.api.metrics.PerformanceMetrics$NonMeasuringMetrics\"}},{\"stageId\":\"Convert14\",\"dat"
                + "aFlowKeys\":{\"className\":\"ABCD-Data-Customer\",\"name\":\"MobileCustomerDataCassandra\"},\"stageName\":\"ABCD-Data-URL"
                + "TaggingFlowDF\",\"movingAverage\":0.0,\"recordsIn\":0,\"recordsOut\":0,\"errorsCount\":0,\"lastRecordTimestamp\":0,\"exec"
                + "utionTime\":0,\"avgTimePerRecord\":0.0,\"stale\":false,\"withDetailedMetrics\":false,\"internalMetrics\":{\"@class\":\"co"
                + "m.pega.dsm.dnode.api.metrics.PerformanceMetrics$NonMeasuringMetrics\"}},{\"stageId\":\"Source4\",\"dataFlowKeys\":{\"clas"
                + "sName\":\"ABCD-Data-Customer\",\"name\":\"MobileCustomerDataCassandra\"},\"stageName\":\"ABCDPropensityModels\",\"movingA"
                + "verage\":2.7064606E7,\"recordsIn\":1,\"recordsOut\":0,\"errorsCount\":0,\"lastRecordTimestamp\":1632396505316,\"execution"
                + "Time\":27064606,\"avgTimePerRecord\":27064.606,\"stale\":false,\"withDetailedMetrics\":false,\"internalMetrics\":{\"@clas"
                + "s\":\"com.pega.dsm.dnode.api.metrics.PerformanceMetrics$NonMeasuringMetrics\"}},{\"stageId\":\"Convert13\",\"dataFlowKeys"
                + "\":{\"className\":\"ABCD-Data-Customer\",\"name\":\"MobileCustomerDataCassandra\"},\"stageName\":\"ABCD-Data-PropensityDF"
                + "\",\"movingAverage\":0.0,\"recordsIn\":0,\"recordsOut\":0,\"errorsCount\":0,\"lastRecordTimestamp\":0,\"executionTime\":0"
                + ",\"avgTimePerRecord\":0.0,\"stale\":false,\"withD";

        LOG.info("dataText: " + dataText);

        PEGA0062ReportModel pega0062ReportModel = new PEGA0062ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0062ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("SingleCustomerData [CustomerUsage]", alertMessageReportEntryKey);
    }
}
