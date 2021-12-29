
package com.pega.gcs.logviewer.systemstate.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import com.pega.gcs.fringecommon.guiutilities.GUIUtilities;
import com.pega.gcs.logviewer.systemstate.model.IndexerQueueStatus;
import com.pega.gcs.logviewer.systemstate.model.QueueInformationContainer;

public class QueueInformationContainerPanel extends JPanel {

    private static final long serialVersionUID = 2412415813960579386L;

    public QueueInformationContainerPanel(QueueInformationContainer queueInformationContainer) {

        setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 0.0D;
        gbc1.weighty = 0.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(5, 5, 2, 5);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = 1;
        gbc2.weightx = 0.0D;
        gbc2.weighty = 0.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(2, 5, 5, 5);

        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 0;
        gbc3.gridy = 2;
        gbc3.weightx = 1.0D;
        gbc3.weighty = 1.0D;
        gbc3.fill = GridBagConstraints.BOTH;
        gbc3.anchor = GridBagConstraints.NORTHWEST;
        gbc3.insets = new Insets(2, 5, 5, 5);

        JPanel ftsIncrementalIndexerQueueStatusJanel = getIndexerQueueStatusPanel(
                "FTS Incremental Indexer Queue Status",
                queueInformationContainer.getFtsIncrementalIndexerQueueStatus());

        JPanel batchIndexProcessorQueueStatusJanel = getIndexerQueueStatusPanel("Batch Index Processor Queue Status",
                queueInformationContainer.getBatchIndexProcessorQueueStatus());

        JPanel fillerPanel = new JPanel();

        add(ftsIncrementalIndexerQueueStatusJanel, gbc1);
        add(batchIndexProcessorQueueStatusJanel, gbc2);
        add(fillerPanel, gbc3);

    }

    private JPanel getIndexerQueueStatusPanel(String title, IndexerQueueStatus indexerQueueStatus) {

        JPanel indexerQueueStatusPanel = new JPanel();

        indexerQueueStatusPanel.setLayout(new GridBagLayout());

        // 1st Row
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 0.0D;
        gbc1.weighty = 0.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(10, 5, 5, 0);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 1;
        gbc2.gridy = 0;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 0.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(10, 5, 5, 5);

        // 2nd Row
        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 0;
        gbc3.gridy = 1;
        gbc3.weightx = 0.0D;
        gbc3.weighty = 0.0D;
        gbc3.fill = GridBagConstraints.BOTH;
        gbc3.anchor = GridBagConstraints.NORTHWEST;
        gbc3.insets = new Insets(5, 5, 5, 0);

        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.gridx = 1;
        gbc4.gridy = 1;
        gbc4.weightx = 1.0D;
        gbc4.weighty = 0.0D;
        gbc4.fill = GridBagConstraints.BOTH;
        gbc4.anchor = GridBagConstraints.NORTHWEST;
        gbc4.insets = new Insets(5, 5, 5, 5);

        // 3rd Row
        GridBagConstraints gbc5 = new GridBagConstraints();
        gbc5.gridx = 0;
        gbc5.gridy = 2;
        gbc5.weightx = 0.0D;
        gbc5.weighty = 0.0D;
        gbc5.fill = GridBagConstraints.BOTH;
        gbc5.anchor = GridBagConstraints.NORTHWEST;
        gbc5.insets = new Insets(5, 5, 5, 0);

        GridBagConstraints gbc6 = new GridBagConstraints();
        gbc6.gridx = 1;
        gbc6.gridy = 2;
        gbc6.weightx = 1.0D;
        gbc6.weighty = 0.0D;
        gbc6.fill = GridBagConstraints.BOTH;
        gbc6.anchor = GridBagConstraints.NORTHWEST;
        gbc6.insets = new Insets(5, 5, 5, 5);

        // 4th Row
        GridBagConstraints gbc7 = new GridBagConstraints();
        gbc7.gridx = 0;
        gbc7.gridy = 3;
        gbc7.weightx = 0.0D;
        gbc7.weighty = 0.0D;
        gbc7.fill = GridBagConstraints.BOTH;
        gbc7.anchor = GridBagConstraints.NORTHWEST;
        gbc7.insets = new Insets(5, 5, 10, 0);

        GridBagConstraints gbc8 = new GridBagConstraints();
        gbc8.gridx = 1;
        gbc8.gridy = 3;
        gbc8.weightx = 1.0D;
        gbc8.weighty = 0.0D;
        gbc8.fill = GridBagConstraints.BOTH;
        gbc8.anchor = GridBagConstraints.NORTHWEST;
        gbc8.insets = new Insets(5, 5, 10, 5);

        JLabel queueNameLabel = new JLabel("Queue Name");
        JLabel queueStatusLabel = new JLabel("Queue Status");
        JLabel queueSizeLabel = new JLabel("Queue Size");
        JLabel numItemsProcessedLastHourLabel = new JLabel("No. Items Processed Last Hour");

        JTextField queueNameTextField = GUIUtilities.getValueTextField(indexerQueueStatus.getQueueName());
        JTextField queueStatusTextField = GUIUtilities.getValueTextField(indexerQueueStatus.getQueueStatus());
        JTextField queueSizeTextField = GUIUtilities.getValueTextField(indexerQueueStatus.getQueueSize());
        JTextField numItemsProcessedLastHourTextField = GUIUtilities
                .getValueTextField(indexerQueueStatus.getNumItemsProcessedLastHour());

        // 1st Row
        indexerQueueStatusPanel.add(queueNameLabel, gbc1);
        indexerQueueStatusPanel.add(queueNameTextField, gbc2);

        // 2nd Row
        indexerQueueStatusPanel.add(queueStatusLabel, gbc3);
        indexerQueueStatusPanel.add(queueStatusTextField, gbc4);

        // 3rd Row
        indexerQueueStatusPanel.add(queueSizeLabel, gbc5);
        indexerQueueStatusPanel.add(queueSizeTextField, gbc6);

        // 4th Row
        indexerQueueStatusPanel.add(numItemsProcessedLastHourLabel, gbc7);
        indexerQueueStatusPanel.add(numItemsProcessedLastHourTextField, gbc8);

        Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        indexerQueueStatusPanel.setBorder(BorderFactory.createTitledBorder(loweredEtched, title));

        return indexerQueueStatusPanel;
    }
}
