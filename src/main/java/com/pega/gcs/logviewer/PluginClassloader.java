
package com.pega.gcs.logviewer;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PluginClassloader {

    private static final Log4j2Helper LOG = new Log4j2Helper(PluginClassloader.class);

    private URLClassLoader urlClassLoader;

    private static PluginClassloader _INSTANCE;

    public PluginClassloader() {

        try {

            String pwd = System.getProperty("user.dir");
            URL pluginsUrl = getClass().getResource("/plugins");

            LOG.info("Loading Plugins - user.dir: " + pwd + " pluginsUrl: "
                    + ((pluginsUrl != null) ? pluginsUrl.getPath() : "<NULL>"));

            File pluginsDir = null;

            if (pluginsUrl != null) {
                pluginsDir = new File(pluginsUrl.getFile());
            } else {
                pluginsDir = new File(pwd, "plugins");
            }

            if (pluginsDir.exists() && pluginsDir.isDirectory()) {

                ArrayList<URL> jarUrlList = new ArrayList<>();

                File[] jarlist = pluginsDir.listFiles(new java.io.FileFilter() {

                    @Override
                    public boolean accept(File file) {
                        return file.getPath().toLowerCase().endsWith(".jar");
                    }
                });

                for (File jarFile : jarlist) {

                    URL jarFileURL = jarFile.toURI().toURL();
                    LOG.info("loading external jar: " + jarFileURL);

                    jarUrlList.add(jarFileURL);
                }

                ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();

                urlClassLoader = new URLClassLoader(jarUrlList.toArray(new URL[jarUrlList.size()]), systemClassLoader);

            }

        } catch (Exception e) {
            LOG.error("Error loading plugins", e);
        }

    }

    public static PluginClassloader getInstance() {

        if (_INSTANCE == null) {
            _INSTANCE = new PluginClassloader();
        }

        return _INSTANCE;
    }

    public URLClassLoader getUrlClassLoader() {
        return urlClassLoader;
    }

}
