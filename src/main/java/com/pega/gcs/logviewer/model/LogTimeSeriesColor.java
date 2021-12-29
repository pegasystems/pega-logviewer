/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.pega.gcs.fringecommon.guiutilities.BaseFrame;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class LogTimeSeriesColor {

    private static final Log4j2Helper LOG = new Log4j2Helper(BaseFrame.class);

    private static final Map<String, Color> logTimeSeriesColorMap;

    private static final Map<String, Color> genericMap;

    static {

        Color a01 = new Color(240, 163, 255);
        Color bbb = new Color(0, 117, 220);
        Color ccc = new Color(153, 63, 0);
        Color ddd = new Color(76, 0, 92);
        Color eee = new Color(25, 25, 25);
        Color fff = new Color(0, 92, 49);
        Color ggg = new Color(43, 206, 72);
        Color hhh = new Color(255, 192, 153);
        Color iii = new Color(128, 128, 128);
        Color jjj = new Color(148, 235, 181);
        Color kkk = new Color(143, 124, 0);
        Color lll = new Color(157, 204, 0);
        Color mmm = new Color(194, 0, 136);
        Color nnn = new Color(0, 51, 128);
        Color ooo = new Color(255, 164, 5);
        Color ppp = new Color(255, 168, 187);
        Color qqq = new Color(66, 102, 0);
        Color rrr = new Color(255, 0, 16);
        Color sss = new Color(50, 200, 242);
        Color ttt = new Color(0, 153, 143);
        Color uuu = new Color(116, 10, 255);
        Color vvv = new Color(192, 0, 0);
        Color www = new Color(255, 80, 5);
        Color xxx = new Color(237, 118, 81);
        Color yyy = new Color(126, 126, 184);
        Color zzz = new Color(128, 128, 255);

        genericMap = new HashMap<>();
        genericMap.put("aaa ", a01);
        genericMap.put("bbb ", bbb);
        genericMap.put("ccc ", ccc);
        genericMap.put("ddd ", ddd);
        genericMap.put("eee ", eee);
        genericMap.put("fff ", fff);
        genericMap.put("ggg ", ggg);
        genericMap.put("hhh ", hhh);
        genericMap.put("iii ", iii);
        genericMap.put("jjj ", jjj);
        genericMap.put("kkk ", kkk);
        genericMap.put("lll ", lll);
        genericMap.put("mmm ", mmm);
        genericMap.put("nnn ", nnn);
        genericMap.put("ooo ", ooo);
        genericMap.put("ppp ", ppp);
        genericMap.put("qqq ", qqq);
        genericMap.put("rrr ", rrr);
        genericMap.put("sss ", sss);
        genericMap.put("ttt ", ttt);
        genericMap.put("uuu ", uuu);
        genericMap.put("vvv ", vvv);
        genericMap.put("www ", www);
        genericMap.put("xxx ", xxx);
        genericMap.put("yyy ", yyy);
        genericMap.put("zzz ", zzz);

        logTimeSeriesColorMap = new HashMap<String, Color>();

        // Colour for Log4J
        String key;
        Color value;

        key = Log4jLogEntryModel.TS_TOTAL_MEMORY;
        value = rrr;
        logTimeSeriesColorMap.put(key, value);

        key = Log4jLogEntryModel.TS_USED_MEMORY;
        value = bbb;
        logTimeSeriesColorMap.put(key, value);

        key = Log4jLogEntryModel.TS_REQUESTOR_COUNT;
        value = ggg;
        logTimeSeriesColorMap.put(key, value);

        key = Log4jLogEntryModel.TS_SHARED_PAGE_MEMORY;
        value = ccc;
        logTimeSeriesColorMap.put(key, value);

        key = Log4jLogEntryModel.TS_NUMBER_OF_THREADS;
        value = ooo;
        logTimeSeriesColorMap.put(key, value);

        key = Log4jLogEntryModel.IM_SYSTEM_START;
        value = sss;
        logTimeSeriesColorMap.put(key, value);

        key = Log4jLogEntryModel.IM_THREAD_DUMP;
        value = www;
        logTimeSeriesColorMap.put(key, value);

        key = Log4jLogEntryModel.IM_EXCEPTIONS;
        value = a01;
        logTimeSeriesColorMap.put(key, value);

    }

    public static Map<String, Color> getLogTimeSeriesColorMap() {
        return logTimeSeriesColorMap;
    }

    public static void main(String[] args) {

        // Run GUI codes in Event-Dispatching thread for thread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    JFrame testFrame = new JFrame("Color Pallet");

                    JPanel mainPanel = new JPanel();

                    mainPanel.setLayout(new GridBagLayout());

                    List<String> colorNameList = new ArrayList<>(genericMap.keySet());

                    Collections.sort(colorNameList);

                    for (int x = 0; x < 5; x++) {

                        for (int y = 0; y < 5; y++) {

                            int index = (x * 5) + y;

                            String colorName = colorNameList.get(index);
                            Color color = genericMap.get(colorName);

                            GridBagConstraints gbc = new GridBagConstraints();

                            gbc.gridx = x;
                            gbc.gridy = y;
                            gbc.weightx = 1.0D;
                            gbc.weighty = 1.0D;
                            gbc.fill = GridBagConstraints.BOTH;
                            gbc.anchor = GridBagConstraints.WEST;

                            JLabel colorTile = new JLabel(colorName);
                            colorTile.setOpaque(true);
                            colorTile.setForeground(Color.WHITE);
                            colorTile.setBackground(color);
                            colorTile.setHorizontalAlignment(SwingConstants.CENTER);

                            mainPanel.add(colorTile, gbc);
                        }
                    }

                    testFrame.setPreferredSize(new Dimension(1200, 700));

                    testFrame.setContentPane(mainPanel);
                    testFrame.pack();
                    testFrame.setVisible(true);
                } catch (Exception e) {
                    LOG.error("Error in LogTimeSeriesColor", e);
                }
            }
        });
    }
}
