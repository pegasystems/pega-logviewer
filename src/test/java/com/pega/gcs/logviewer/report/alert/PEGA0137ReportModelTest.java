
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0137ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0137ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "The Queue Processor named pySASBatchIndexProcessor did not pick an item from Kafka for a long period of time (20"
                + "000 milliseconds). Queue processor information: {queue processor=pySASBatchIndexProcessor, ruleset=Pega-SearchEngine, ru"
                + "leset version=08-07-01, associated nodeType=BackgroundProcessing, is enabled=true} Partitions details{partitionID=0{last"
                + " producer timestamp=2022-07-28T15:02:16.718Z, last consumer timestamp=2022-07-28T15:06:58.430Z, number of not processed "
                + "messages=199}, partitionID=1{last producer timestamp=2022-07-28T15:02:16.737Z, last consumer timestamp=2022-07-28T15:05:"
                + "54.271Z, number of not processed messages=202}, partitionID=2{last producer timestamp=2022-07-28T15:02:16.745Z, last con"
                + "sumer timestamp=2022-07-28T15:07:13.207Z, number of not processed messages=307}, partitionID=3{last producer timestamp=2"
                + "022-07-28T15:02:16.752Z, last consumer timestamp=2022-07-28T15:06:45.177Z, number of not processed messages=50}, partiti"
                + "onID=4{last producer timestamp=2022-07-28T15:02:17.134Z, last consumer timestamp=2022-07-28T15:06:28.111Z, number of not"
                + " processed messages=243}, partitionID=5{last producer timestamp=2022-07-28T15:02:16.713Z, last consumer timestamp=2022-0"
                + "7-28T15:03:05.131Z, number of not processed messages=341}}";

        LOG.info("dataText: " + dataText);

        PEGA0137ReportModel pega0137ReportModel = new PEGA0137ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0137ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("pySASBatchIndexProcessor", alertMessageReportEntryKey);
    }

}
