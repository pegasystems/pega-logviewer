
package com.pega.gcs.logviewer;

import java.io.File;

import org.junit.jupiter.api.Test;

public class LogViewerTest {

    /**
     * Test method for {@link com.pega.gcs.logviewer.LogViewer#isSystemScanFile(java.io.File)}.
     */
    @Test
    public void testIsSystemScanFile() {

        File systemScanFile;
        boolean scanResultZipFile;

        systemScanFile = new File("ScanResults_20171212T163918.041 GMT.zip");
        scanResultZipFile = LogViewer.isSystemScanFile(systemScanFile);
        org.junit.jupiter.api.Assertions.assertTrue(scanResultZipFile);

        systemScanFile = new File("ScanResults_20171024T070649.934 GMT_20171024_093710.zip");
        scanResultZipFile = LogViewer.isSystemScanFile(systemScanFile);
        org.junit.jupiter.api.Assertions.assertTrue(scanResultZipFile);

    }

    /**
     * Test method for {@link com.pega.gcs.logviewer.LogViewer#isSystemStateFile(java.io.File)}.
     */
    @Test
    public void testIsSystemStateFile() {

        File systemStateFile;
        boolean isSystemStateFile;

        systemStateFile = new File("SystemState_60b1741a47967d78c6ee8c392d0397b6_20190501T092240.420 GMT.json");
        isSystemStateFile = LogViewer.isSystemStateFile(systemStateFile);
        org.junit.jupiter.api.Assertions.assertTrue(isSystemStateFile);

        systemStateFile = new File("SystemState_Cluster_20190428T050001.193 GMT.json");
        isSystemStateFile = LogViewer.isSystemStateFile(systemStateFile);
        org.junit.jupiter.api.Assertions.assertTrue(isSystemStateFile);

        systemStateFile = new File("SystemState_cluster.json");
        isSystemStateFile = LogViewer.isSystemStateFile(systemStateFile);
        org.junit.jupiter.api.Assertions.assertTrue(isSystemStateFile);

        systemStateFile = new File("SystemState.json");
        isSystemStateFile = LogViewer.isSystemStateFile(systemStateFile);
        org.junit.jupiter.api.Assertions.assertTrue(isSystemStateFile);

        systemStateFile = new File("SystemState_Cluster_20220512T050000.000 GMT.zip");
        isSystemStateFile = LogViewer.isSystemStateFile(systemStateFile);
        org.junit.jupiter.api.Assertions.assertTrue(isSystemStateFile);

        systemStateFile = new File("Dev_SystemState_Cluster_20220512T050000.000 GMT.zip");
        isSystemStateFile = LogViewer.isSystemStateFile(systemStateFile);
        org.junit.jupiter.api.Assertions.assertTrue(isSystemStateFile);

    }

}
