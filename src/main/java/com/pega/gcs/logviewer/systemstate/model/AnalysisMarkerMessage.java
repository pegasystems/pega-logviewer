
package com.pega.gcs.logviewer.systemstate.model;

public class AnalysisMarkerMessage {

    public static final String OVERRIDE_PRCONFIG = "Dynamic System Setting ''{0}'' with value ''{1}'' is overriden in prconfig.xml"
            + " with value ''{2}''.";

    public static final String DUPLICATE_JVMOPTION = "Duplicate JVM Option ''{0}'' with value ''{1}'' and ''{2}''.";

    public static final String JVM_SECURITY_EGD = "JVM Argument ''-Djava.security.egd'' is not set.";

    public static final String JVM_WLS_OIF_SERIALFILTER = "JVM Argument ''-Dweblogic.oif.serialFilter'' is required for WebLogic JEP 290 is"
            + "sues with Cassandra and Kafka JMX.";

    public static final String JVM_WLS_OIF_SERIALFILTERMODE = "JVM Argument ''-Dweblogic.oif.serialFilterMode'' is required for WebLogic JE"
            + "P 290 issues with Cassandra and Kafka JMX.";

    public static final String JVM_PERM_SIZE = "JVM Argument ''-XX:PermSize'' detected, it is removed from Java 8 onwards";

    public static final String JVM_MAX_PERM_SIZE = "JVM Argument ''-XX:MaxPermSize'' detected, it is removed from Java 8 onwards";
}
