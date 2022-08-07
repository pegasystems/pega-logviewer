
package com.pega.gcs.logviewer.systemstate;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.pega.gcs.fringecommon.guiutilities.Message;
import com.pega.gcs.fringecommon.guiutilities.Message.MessageType;
import com.pega.gcs.fringecommon.guiutilities.ModalProgressMonitor;
import com.pega.gcs.fringecommon.guiutilities.RecentFile;
import com.pega.gcs.fringecommon.guiutilities.RecentFileContainer;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.model.LogViewerSetting;
import com.pega.gcs.logviewer.systemstate.model.SystemState;
import com.pega.gcs.logviewer.systemstate.view.SystemStateFormView;

public class SystemStateMainPanel extends JPanel {

    private static final long serialVersionUID = -3817114289952313556L;

    private static final Log4j2Helper LOG = new Log4j2Helper(SystemStateMainPanel.class);

    private SystemStateTreeModel systemStateTreeModel;

    private SystemStateTreeNavigationController systemStateTreeNavigationController;

    public SystemStateMainPanel(File systemStateFile, RecentFileContainer recentFileContainer,
            LogViewerSetting logViewerSetting) {

        systemStateTreeModel = new SystemStateTreeModel();
        systemStateTreeNavigationController = new SystemStateTreeNavigationController(systemStateTreeModel);

        String charset = logViewerSetting.getCharset();

        RecentFile recentFile = recentFileContainer.getRecentFile(systemStateFile, charset);

        setLayout(new BorderLayout());

        SystemStateFormView systemStateFormView = new SystemStateFormView(systemStateTreeModel,
                systemStateTreeNavigationController);

        add(systemStateFormView, BorderLayout.CENTER);

        loadFile(recentFile);

    }

    @Override
    public void removeNotify() {
        super.removeNotify();
    }

    private void loadFile(RecentFile recentFile) {

        UIManager.put("ModalProgressMonitor.progressText", "Processing system state file");

        final ModalProgressMonitor progressMonitor = new ModalProgressMonitor(this, "",
                "Processing system state file                                          ", 0, 100);

        progressMonitor.setMillisToDecideToPopup(0);
        progressMonitor.setMillisToPopup(0);

        SystemStateFileLoadTask systemStateFileLoadTask = new SystemStateFileLoadTask(recentFile, progressMonitor) {

            @Override
            protected void done() {

                Message.MessageType messageType = MessageType.INFO;
                StringBuilder messageB = null;

                String filePath = recentFile.getPath();

                try {

                    SystemState systemState = get();

                    systemStateTreeModel.resetModel(systemState, recentFile);

                    messageB = new StringBuilder();

                    messageB.append(filePath);
                    messageB.append(".");

                } catch (CancellationException ce) {

                    LOG.error("System State Task - Cancelled " + filePath);

                    messageType = MessageType.ERROR;

                    messageB = new StringBuilder();

                    messageB.append(filePath);
                    messageB.append(" - file loading cancelled.");

                } catch (ExecutionException ee) {

                    LOG.error("Execution Error in System State Task", ee);

                    messageType = MessageType.ERROR;

                    messageB = new StringBuilder();

                    Component parent = getParent();

                    if (ee.getCause() instanceof OutOfMemoryError) {

                        messageB.append("Out Of Memory Error has occured while loading ");
                        messageB.append(filePath);
                        messageB.append(".\nPlease increase the JVM's max heap size (-Xmx) and try again.");

                        JOptionPane.showMessageDialog(parent, messageB.toString(), "Out Of Memory Error",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        messageB.append(ee.getCause().getMessage());
                        messageB.append(" has occured while loading ");
                        messageB.append(filePath);
                        messageB.append(".");

                        JOptionPane.showMessageDialog(parent, messageB.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (Exception e) {

                    LOG.error("Error loading file: " + filePath, e);

                    messageType = MessageType.ERROR;

                    messageB = new StringBuilder();

                    messageB.append(filePath);
                    messageB.append(". Error - ");
                    messageB.append(e.getMessage());
                } finally {

                    try {
                        progressMonitor.close();
                    } catch (Exception e) {
                        // close can throw NPE because during compare the same PM is changed into
                        // indetermintae which in turn calls close
                    }

                    Message message = new Message(messageType, messageB.toString());
                    systemStateTreeModel.setMessage(message);

                }

            }

        };

        systemStateFileLoadTask.execute();

    }

}
