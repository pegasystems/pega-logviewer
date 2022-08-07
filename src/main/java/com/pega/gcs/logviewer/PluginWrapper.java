
package com.pega.gcs.logviewer;

import java.awt.Component;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;

import javax.swing.ImageIcon;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public abstract class PluginWrapper {

    private static final Log4j2Helper LOG = new Log4j2Helper(PluginWrapper.class);

    private Class<?> frameClass;

    private boolean initialised;

    public PluginWrapper(String frameClassName) {

        this.initialised = false;

        this.frameClass = null;

        PluginClassloader pluginClassloader = PluginClassloader.getInstance();

        URLClassLoader urlClassLoader = pluginClassloader.getUrlClassLoader();

        try {
            frameClass = Class.forName(frameClassName, true, urlClassLoader);

            if (PluginFrame.class.isAssignableFrom(frameClass)) {
                initialised = true;
            } else {
                LOG.error(frameClassName + " is not assignable from PluginFrame");
            }

        } catch (Exception e) {
            LOG.error("Error initialising PluginWrapper for class: " + frameClassName, e);
        }

    }

    public PluginFrame getPluginFrame(Component parent) {

        PluginFrame pluginFrame = null;

        if (initialised && (frameClass != null)) {
            try {

                ImageIcon appIcon = LogViewer.getAppIcon();

                Constructor<?> frameClassConstructor = frameClass.getDeclaredConstructor(String.class, ImageIcon.class,
                        Component.class);

                pluginFrame = (PluginFrame) frameClassConstructor.newInstance(null, appIcon, parent);

            } catch (InvocationTargetException ite) {
                LOG.error("InvocationTargetException - Error creating frame of type: " + frameClass,
                        ite.getTargetException());
            } catch (Exception e) {
                LOG.error("Error creating frame of type: " + frameClass, e);
            }
        }

        return pluginFrame;

    }

    public boolean isInitialised() {
        return initialised;
    }

}
