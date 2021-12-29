
package com.pega.gcs.logviewer.systemstate.model;

public enum AppServer {

    // @formatter:off
    // CHECKSTYLE:OFF
    TOMCAT            ("Apache Tomcat"                    , "-Dcatalina.home"),
    WEBSPHERE         ("IBM WebSphere Application Server" , "-Dwas.install.root"),
    WEBLOGIC          ("WebLogic Server"                  , "-Dweblogic.home"),
    JBOSS             ("JBoss Web"                        , "-Djboss.modules.system.pkgs"),
    WILDFLY           ("WildFly"                          , ""),
    WEBSPHERE_LIBERTY ("IBM WebSphere Liberty"            , "");
    // CHECKSTYLE:ON
    // @formatter:on

    private String displayName;

    private String jvmOption;

    private AppServer(String displayName, String jvmOption) {
        this.displayName = displayName;
        this.jvmOption = jvmOption;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getJvmOption() {
        return jvmOption;
    }

    public static AppServer getAppServer(String optionName) {

        AppServer appServer = null;

        for (AppServer appServ : values()) {

            if (appServ.getJvmOption().equalsIgnoreCase(optionName)) {
                appServer = appServ;

                break;
            }
        }
        return appServer;
    }
}