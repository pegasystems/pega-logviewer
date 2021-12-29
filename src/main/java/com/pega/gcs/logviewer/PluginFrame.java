
package com.pega.gcs.logviewer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public abstract class PluginFrame extends JFrame {

    private static final long serialVersionUID = 5597391862637806481L;

    private static final Log4j2Helper LOG = new Log4j2Helper(PluginFrame.class);

    private ImageIcon appIcon;

    private Component mainWindow;

    protected abstract void initialize() throws Exception;

    protected abstract void release();

    protected abstract JComponent getMainJPanel();

    public PluginFrame(String title, ImageIcon appIcon, Component mainWindow) {

        this.appIcon = appIcon;
        this.mainWindow = mainWindow;

        setTitle(title);

        setIconImage(appIcon.getImage());

        setPreferredSize(new Dimension(1200, 800));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                release();
            }
        });

        try {
            initialize();
        } catch (Exception e) {
            LOG.error("error loading frame ", e);
        }

        setContentPane(getMainJPanel());

        pack();

        setExtendedState(Frame.MAXIMIZED_BOTH);

        setLocationRelativeTo(mainWindow);

    }

    protected ImageIcon getAppIcon() {
        return appIcon;
    }

    protected Component getMainWindow() {
        return mainWindow;
    }

}
