
package com.pega.gcs.logviewer.gcp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pega.gcs.fringecommon.guiutilities.ModalProgressMonitor;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.LogViewer;

public class GcpGocLogPreprocessorTask extends SwingWorker<List<File>, Integer> {

    private static final Log4j2Helper LOG = new Log4j2Helper(GcpGocLogPreprocessorTask.class);

    private static final String FILE_SUFFIX_RULES = "GCP-PegaRULES.log";

    private static final String FILE_SUFFIX_ALERT = "GCP-PegaRULES-ALERT.log";

    private static final String PEGA_TOMCAT_CONTAINER = "pega-web-tomcat";

    private File gcpGocLogFile;

    private ModalProgressMonitor modalProgressMonitor;

    private ObjectMapper objectMapper;

    public GcpGocLogPreprocessorTask(File gcpGocLogFile, ModalProgressMonitor modalProgressMonitor) {
        super();
        this.gcpGocLogFile = gcpGocLogFile;
        this.modalProgressMonitor = modalProgressMonitor;
        this.objectMapper = new ObjectMapper();

    }

    private File getGcpGocLogFile() {
        return gcpGocLogFile;
    }

    private ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Override
    protected List<File> doInBackground() throws Exception {

        List<File> pegaFileList = null;

        int prevProgress = 0;
        int counter = 0;

        File gcpGocLogFile = getGcpGocLogFile();
        File baseFolder = gcpGocLogFile.getParentFile();
        String baseFileName = gcpGocLogFile.getName();

        File pegaRulesLogFile = new File(baseFolder, baseFileName + "-" + FILE_SUFFIX_RULES);
        File pegaAlertLogFile = new File(baseFolder, baseFileName + "-" + FILE_SUFFIX_ALERT);

        int totalLineCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(gcpGocLogFile))) {
            while (br.readLine() != null) {
                totalLineCount++;
            }
        }

        double increment = totalLineCount / 100d;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(gcpGocLogFile))) {

            String line;
            int batchSize = 4194304; // 4096KB
            String charset = LogViewer.getInstance().getLogViewerSetting().getCharset();

            boolean rulesAppend = false;
            boolean alertAppend = false;

            StringBuilder rulesLogDataSB = new StringBuilder();
            StringBuilder alertLogDataSB = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {

                if (modalProgressMonitor.isCanceled()) {
                    break;
                }

                counter++;

                int progress = (int) Math.round(counter / increment);

                if (progress > prevProgress) {
                    publish((int) progress);
                    prevProgress = progress;
                }

                Map<String, Object> fieldMap = getJsonFieldMap(line);

                String container_name = (String) fieldMap.get("container_name");
                String severity = (String) fieldMap.get("severity");

                if (PEGA_TOMCAT_CONTAINER.equals(container_name)) {

                    if ("ALERT".equals(severity)) {

                        alertLogDataSB.append(line);
                        alertLogDataSB.append(System.getProperty("line.separator"));

                        int accumulatedSize = alertLogDataSB.length();

                        if (accumulatedSize > batchSize) {

                            FileUtils.writeStringToFile(pegaAlertLogFile, alertLogDataSB.toString(), charset,
                                    alertAppend);

                            alertLogDataSB = new StringBuilder();

                            if (!alertAppend) {
                                alertAppend = true;
                            }
                        }
                    } else {
                        rulesLogDataSB.append(line);
                        rulesLogDataSB.append(System.getProperty("line.separator"));

                        int accumulatedSize = rulesLogDataSB.length();

                        if (accumulatedSize > batchSize) {

                            FileUtils.writeStringToFile(pegaRulesLogFile, rulesLogDataSB.toString(), charset,
                                    rulesAppend);

                            rulesLogDataSB = new StringBuilder();

                            if (!rulesAppend) {
                                rulesAppend = true;
                            }
                        }
                    }

                }

            }

            if (!modalProgressMonitor.isCanceled()) {

                if (alertLogDataSB.length() > 0) {
                    FileUtils.writeStringToFile(pegaAlertLogFile, alertLogDataSB.toString(), charset, alertAppend);
                }

                if (rulesLogDataSB.length() > 0) {
                    FileUtils.writeStringToFile(pegaRulesLogFile, rulesLogDataSB.toString(), charset, rulesAppend);
                }

                pegaFileList = new ArrayList<>();

                pegaFileList.add(pegaAlertLogFile);
                pegaFileList.add(pegaRulesLogFile);
            }

        } catch (IOException e) {
            LOG.error("Error reading file " + gcpGocLogFile, e);
        }

        return pegaFileList;
    }

    @Override
    protected void process(List<Integer> chunks) {

        if ((isDone()) || (isCancelled()) || (chunks == null) || (chunks.size() == 0)) {
            return;
        }

        boolean indeterminate = modalProgressMonitor.isIndeterminate();

        if (!indeterminate) {

            Collections.sort(chunks);

            Integer progress = chunks.get(chunks.size() - 1);

            String message = String.format("Processed lines (%d%%)", progress);

            modalProgressMonitor.setProgress(progress);
            modalProgressMonitor.setNote(message);
        }
    }

    protected Map<String, Object> getJsonFieldMap(String line) {

        Map<String, Object> fieldMap = null;

        try {

            ObjectMapper objectMapper = getObjectMapper();

            TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
            };

            fieldMap = objectMapper.readValue(line, typeRef);

        } catch (JacksonException e) {
            LOG.error("Error parsing log json data", e.getMessage());
        }

        return fieldMap;
    }
}
