
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class EXCP0001ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(EXCP0001ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "[MSG][Error saving][STACK][com.pega.pegarules.pub.database.DatabaseException: ORA-01013: user requested cancel o"
                + "f current operation   DatabaseException caused by prior exception: java.sql.BatchUpdateException: ORA-01013: user reques"
                + "ted cancel of current operation   | SQL Code: 1013 | SQL State: 72000  DatabaseException caused by prior exception: java"
                + ".sql.SQLTimeoutException: ORA-01013: user requested cancel of current operation   | SQL Code: 1013 | SQL State: 72000   "
                + "From: (BB811181F0CF3D509F5944B72F1B5A691:(ManagementDaemon))    SQL: INSERT INTO DATA.pr_sys_statusdetails (pzInsKey , p"
                + "xCommitDateTime , PXACTIVETHREADCOUNT , PXAGENTCOUNT , PXAVGHTTPRESPTIME , PXCACHEENABLED , PXCACHESIZE , PXCACHESIZEPER"
                + "CENT , PXCONNECTIONSTRING , PXCREATEDATETIME , PXDATABASECONNECTIONCOUNT , PXGARBAGECOLLECTIONCOUNT , PXINSNAME , PXLAST"
                + "PULSE , PXLISTENERCOUNT , PXMEMORYFREE , PXMEMORYMAX , PXMEMORYTOTAL , PXMEMORYUSED , PXMEMORYUSEDPERCENT , PXNODEDESCRI"
                + "PTION , PXNODERUNSTATE , PXOBJCLASS , PXPEGA0001 , PXPEGA0005 , PXPEGA0010 , PXPEGA0011 , PXPEGA0019 , PXPEGA0020 , PXPR"
                + "OCESSCPUTIME , PXPROCESSCPUUSAGE , PXREQUESTORCOUNT , PXSAVEDATETIME , PXSNAPSHOTTIME , PXSYSTEMSTART , PXTYPE , PXUPDAT"
                + "EDATETIME , PYNODENAME , PYSYSNODEID , PYSYSTEMNAME) VALUES (? , CURRENT_TIMESTAMP , ? , ? , ? , ? , ? , ? , ? , ? , ? ,"
                + " ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ?)   Ca"
                + "used by SQL Problems.  Problem #1, SQLState 72000, Error code 1013: java.sql.BatchUpdateException: ORA-01013: user reque"
                + "sted cancel of current operation   Problem #2, SQLState 72000, Error code 1013: java.sql.SQLTimeoutException: ORA-01013:"
                + " user requested cancel of current operation    at com.pega.pegarules.data.internal.access.ExceptionInformation.createExc"
                + "eptionDueToDBFailure(ExceptionInformation.java:299)   at com.pega.pegarules.data.internal.access.ConnectionStatementStor"
                + "e.executeBatchForAllStatements(ConnectionStatementStore.java:200)   at com.pega.pegarules.data.internal.access.ThreadCon"
                + "nectionStoreImpl.executeOutstandingBatches(ThreadConnectionStoreImpl.java:271)   at com.pega.pegarules.data.internal.acc"
                + "ess.DatabaseImpl.attemptToProcessUpdates(DatabaseImpl.java:2758)   at com.pega.pegarules.data.internal.access.DatabaseIm"
                + "pl.processUpdates(DatabaseImpl.java:2400)   at com.pega.pegarules.data.internal.access.Saver.save(Saver.java:651)   at c"
                + "om.pega.pegarules.data.internal.access.DatabaseImpl.save(DatabaseImpl.java:5192)   at com.pega.pegarules.data.internal.a"
                + "ccess.DatabaseImpl.save(DatabaseImpl.java:5178)   at com.pega.pegarules.data.internal.access.DatabaseImpl.save(DatabaseI"
                + "mpl.java:5169)   at com.pega.pegarules.management.internal.PRManagementProviderImpl.insertRowsToSysManagementDB(PRManage"
                + "mentProviderImpl.java:214)   at com.pega.pegarules.management.internal.PRManagementProviderImpl.getStatusClipboard(PRMan"
                + "agementProviderImpl.java:99)   at com.pega.pegarules.monitor.internal.context.ManagementDaemonImpl.performManangementAct"
                + "ions(ManagementDaemonImpl.java:290)   at sun.reflect.GeneratedMethodAccessor113.invoke(Unknown Source)   at sun.reflect."
                + "DelegatingMethodAccessorImpl.invoke(Unknown Source)   at java.lang.reflect.Method.invoke(Unknown Source)   at com.pega.p"
                + "egarules.session.internal.PRSessionProviderImpl.performTargetActionWithLock(PRSessionProviderImpl.java:1277)   at com.pe"
                + "ga.pegarules.session.internal.PRSessionProviderImpl.doWithRequestorLocked(PRSessionProviderImpl.java:1015)   at com.pega"
                + ".pegarules.session.internal.PRSessionProviderImpl.doWithRequestorLocked(PRSessionProviderImpl.java:848)   at com.pega.pe"
                + "garules.session.external.async.AbstractDaemon.performProcessing(AbstractDaemon.java:317)   at com.pega.pegarules.session"
                + ".external.async.AbstractDaemon.run(AbstractDaemon.java:262)   at java.lang.Thread.run(Unknown Source)  Caused by: java.s"
                + "ql.BatchUpdateException: ORA-01013: user requested cancel of current operation    at oracle.jdbc.driver.OraclePreparedSt"
                + "atement.executeBatch(OraclePreparedStatement.java:11190)   at oracle.jdbc.driver.OracleStatementWrapper.executeBatch(Ora"
                + "cleStatementWrapper.java:244)   at org.apache.tomcat.dbcp.dbcp.DelegatingStatement.executeBatch(DelegatingStatement.java"
                + ":297)   at org.apache.tomcat.dbcp.dbcp.DelegatingStatement.executeBatch(DelegatingStatement.java:297)   at com.pega.pega"
                + "rules.data.internal.access.DatabasePreparedStatementImpl.executeBatch(DatabasePreparedStatementImpl.java:535)   at com.p"
                + "ega.pegarules.data.internal.access.ConnectionStatementStore.executeBatchForAllStatements(ConnectionStatementStore.java:1"
                + "98)   ... 19 more  ]";

        LOG.info("dataText: " + dataText);

        EXCP0001ReportModel excp0001ReportModel = new EXCP0001ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = excp0001ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("com.pega.pegarules.pub.database.DatabaseException",
                alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "[MSG][The element at the top of the stack: ][STACK][java.lang.Throwable<CR><CR>    at com.pega.pegarules.monitor"
                + ".internal.database.ReporterImpl.<init>(ReporterImpl.java:427)<CR><CR>    at com.pega.pegarules.monitor.internal.database"
                + ".ReporterStackImpl.push(ReporterStackImpl.java:89)<CR><CR>    at com.pega.pegarules.data.internal.access.DatabaseImpl.co"
                + "mmit(DatabaseImpl.java:2033)<CR><CR>    at com.pegarules.generated.activity.ra_action_commitwitherrorhandling_6c6c3f21ea"
                + "d5ea2a97cdc30349237e37.step4_circum0(ra_action_commitwitherrorhandling_6c6c3f21ead5ea2a97cdc30349237e37.java:492)<CR><CR"
                + ">    at com.pegarules.generated.activity.ra_action_commitwitherrorhandling_6c6c3f21ead5ea2a97cdc30349237e37.perform(ra_a"
                + "ction_commitwitherrorhandling_6c6c3f21ead5ea2a97cdc30349237e37.java:120)<CR><CR>    at com.pega.pegarules.session.intern"
                + "al.mgmt.Executable.doActivity(Executable.java:3505)<CR><CR>    at com.pega.pegarules.session.internal.mgmt.Executable.in"
                + "vokeActivity(Executable.java:10563)<CR><CR>    at com.pegarules.generated.activity.ra_action_workcommit_cae6addc5923a8c1"
                + "402dc635b7fbae23.step3_circum0(ra_action_workcommit_cae6addc5923a8c1402dc635b7fbae23.java:415)<CR><CR>    at com.pegarul"
                + "es.generated.activity.ra_action_workcommit_cae6addc5923a8c1402dc635b7fbae23.perform(ra_action_workcommit_cae6addc5923a8c"
                + "1402dc635b7fbae23.java:103)<CR><CR>    at com.pega.pegarules.session.internal.mgmt.Executable.doActivity(Executable.java"
                + ":3505)<CR><CR>    at com.pega.pegarules.session.internal.mgmt.Executable.invokeActivity(Executable.java:10563)<CR><CR>  "
                + "  at com.pegarules.generated.activity.ra_action_add_5fa4be2f584be40d9e1b50cf1962a8d8.step5_circum0(ra_action_add_5fa4be2"
                + "f584be40d9e1b50cf1962a8d8.java:694)<CR><CR>    at com.pegarules.generated.activity.ra_action_add_5fa4be2f584be40d9e1b50c"
                + "f1962a8d8.perform(ra_action_add_5fa4be2f584be40d9e1b50cf1962a8d8.java:140)<CR><CR>    at com.pega.pegarules.session.inte"
                + "rnal.mgmt.Executable.doActivity(Executable.java:3505)<CR><CR>    at com.pega.pegarules.session.internal.mgmt.base.Thread"
                + "Runner.runActivitiesAlt(ThreadRunner.java:646)<CR><CR>    at com.pega.pegarules.session.internal.mgmt.PRThreadImpl.runAc"
                + "tivitiesAlt(PRThreadImpl.java:461)<CR><CR>    at com.pega.pegarules.session.internal.engineinterface.service.HttpAPI.run"
                + "Activities(HttpAPI.java:3358)<CR><CR>    at com.pega.pegarules.session.external.engineinterface.service.EngineAPI.proces"
                + "sRequestInner(EngineAPI.java:385)<CR><CR>    at sun.reflect.GeneratedMethodAccessor44.invoke(Unknown Source)<CR><CR>    "
                + "at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)<CR><CR>    at java.lang.reflect"
                + ".Method.invoke(Method.java:497)<CR><CR>    at com.pega.pegarules.session.internal.PRSessionProviderImpl.performTargetAct"
                + "ionWithLock(PRSessionProviderImpl.java:1270)<CR><CR>    at com.pega.pegarules.session.internal.PRSessionProviderImpl.doW"
                + "ithRequestorLocked(PRSessionProviderImpl.java:1008)<CR><CR>    at com.pega.pegarules.session.internal.PRSessionProviderI"
                + "mpl.doWithRequestorLocked(PRSessionProviderImpl.java:841)<CR><CR>    at com.pega.pegarules.session.external.engineinterf"
                + "ace.service.EngineAPI.processRequest(EngineAPI.java:331)<CR><CR>    at com.pega.pegarules.session.internal.engineinterfa"
                + "ce.service.HttpAPI.invoke(HttpAPI.java:852)<CR><CR>    at com.pega.pegarules.session.internal.engineinterface.etier.impl"
                + ".EngineImpl._invokeEngine_privact(EngineImpl.java:315)<CR><CR>    at com.pega.pegarules.session.internal.engineinterface"
                + ".etier.impl.EngineImpl.invokeEngine(EngineImpl.java:263)<CR><CR>    at com.pega.pegarules.session.internal.engineinterfa"
                + "ce.etier.impl.EngineImpl.invokeEngine(EngineImpl.java:240)<CR><CR>    at com.pega.pegarules.priv.context.JNDIEnvironment"
                + ".invokeEngineInner(JNDIEnvironment.java:278)<CR><CR>    at com.pega.pegarules.priv.context.JNDIEnvironment.invokeEngine("
                + "JNDIEnvironment.java:223)<CR><CR>    at com.pega.pegarules.web.impl.WebStandardImpl.makeEtierRequest(WebStandardImpl.jav"
                + "a:574)<CR><CR>    at com.pega.pegarules.web.impl.WebStandardImpl.doPost(WebStandardImpl.java:374)<CR><CR>    at sun.refl"
                + "ect.GeneratedMethodAccessor43.invoke(Unknown Source)<CR><CR>    at sun.reflect.DelegatingMethodAccessorImpl.invoke(Deleg"
                + "atingMethodAccessorImpl.java:43)<CR><CR>    at java.lang.reflect.Method.invoke(Method.java:497)<CR><CR>    at com.pega.p"
                + "egarules.internal.bootstrap.PRBootstrap.invokeMethod(PRBootstrap.java:370)<CR><CR>    at com.pega.pegarules.internal.boo"
                + "tstrap.PRBootstrap.invokeMethodPropagatingThrowable(PRBootstrap.java:411)<CR><CR>    at com.pega.pegarules.boot.internal"
                + ".extbridge.AppServerBridgeToPega.invokeMethodPropagatingThrowable(AppServerBridgeToPega.java:223)<CR><CR>    at com.pega"
                + ".pegarules.boot.internal.extbridge.AppServerBridgeToPega.invokeMethod(AppServerBridgeToPega.java:272)<CR><CR>    at com."
                + "pega.pegarules.internal.web.servlet.WebStandardBoot.doPost(WebStandardBoot.java:121)<CR><CR>    at javax.servlet.http.Ht"
                + "tpServlet.service(HttpServlet.java:754)<CR><CR>    at javax.servlet.http.HttpServlet.service(HttpServlet.java:847)<CR><C"
                + "R>    at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:295)<CR><CR>    at"
                + " org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:214)<CR><CR>    at org.apache.cata"
                + "lina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:231)<CR><CR>    at org.apache.catalina.core.StandardCont"
                + "extValve.invoke(StandardContextValve.java:149)<CR><CR>    at org.apache.catalina.authenticator.AuthenticatorBase.invoke("
                + "AuthenticatorBase.java:420)<CR><CR>    at org.jboss.as.web.session.ClusteredSessionValve.handleRequest(ClusteredSessionV"
                + "alve.java:134)<CR><CR>    at org.jboss.as.web.session.ClusteredSessionValve.invoke(ClusteredSessionValve.java:99)<CR><CR"
                + ">    at org.jboss.as.web.session.JvmRouteValve.invoke(JvmRouteValve.java:92)<CR><CR>    at org.jboss.as.web.session.Lock"
                + "ingValve.invoke(LockingValve.java:64)<CR><CR>    at org.jboss.as.web.security.SecurityContextAssociationValve.invoke(Sec"
                + "urityContextAssociationValve.java:169)<CR><CR>    at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve"
                + ".java:150)<CR><CR>    at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:97)<CR><CR>    at org."
                + "apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:102)<CR><CR>    at org.apache.catalina.connecto"
                + "r.CoyoteAdapter.service(CoyoteAdapter.java:344)<CR><CR>    at org.apache.coyote.http11.Http11Processor.process(Http11Pro"
                + "cessor.java:854)<CR><CR>    at org.apache.coyote.http11.Http11Protocol$Http11ConnectionHandler.process(Http11Protocol.ja"
                + "va:653)<CR><CR>    at org.apache.tomcat.util.net.JIoEndpoint$Worker.run(JIoEndpoint.java:926)<CR><CR>    at java.lang.Th"
                + "read.run(Thread.java:745)<CR><CR>]";

        LOG.info("dataText: " + dataText);

        EXCP0001ReportModel excp0001ReportModel = new EXCP0001ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = excp0001ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("java.lang.Throwable", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString3() {

        String dataText = "[MSG][Section 'ErrorList' execution error on page 'pyWorkPage' of class 'CX-FW-CAREFW-Work-WholeHome-Internet'.]"
                + "[STACK][com.pega.pegarules.pub.context.StaleRequestorError: PRRequestorImpl was explicitly destroyed  at com.pega.pegaru"
                + "les.session.internal.mgmt.PRRequestorImpl.validateUse(PRRequestorImpl.java:337)  at com.pega.pegarules.session.internal."
                + "mgmt.PRRequestorImpl.getParent(PRRequestorImpl.java:1669)  at com.pega.pegarules.data.external.clipboard.ClipboardObject"
                + "Impl.getParentProperty(ClipboardObjectImpl.java:188)  at com.pega.pegarules.data.internal.clipboard.ClipboardPropertyImp"
                + "l.getValue(ClipboardPropertyImpl.java:4387)  at com.pega.pegarules.data.internal.clipboard.ClipboardPropertyBase.getValu"
                + "e(ClipboardPropertyBase.java:3172)  at com.pega.pegarules.data.internal.clipboard.ClipboardPropertyImpl.getPageValue(Cli"
                + "pboardPropertyImpl.java:3486)  at com.pega.pegarules.data.internal.clipboard.ClipboardPropertyImpl.getPageValue(Clipboar"
                + "dPropertyImpl.java:3455)  at com.pega.pegarules.data.internal.clipboard.PropertyDataPageWrapper.prGetPropertyNames(Prope"
                + "rtyDataPageWrapper.java:414)  at com.pega.pegarules.data.internal.clipboard.ClipboardPageImpl.getKeySetFromWrapper(Clipb"
                + "oardPageImpl.java:4952)  at com.pega.pegarules.data.internal.clipboard.ClipboardPageImpl.keySet(ClipboardPageImpl.java:4"
                + "923)  at com.pega.pegarules.data.internal.clipboard.PropertyDataPageWrapper.prGetPropertyNames(PropertyDataPageWrapper.j"
                + "ava:418)  at com.pega.pegarules.data.internal.clipboard.ClipboardPageImpl.getKeySetFromWrapper(ClipboardPageImpl.java:49"
                + "52)  at com.pega.pegarules.data.internal.clipboard.ClipboardPageImpl.values(ClipboardPageImpl.java:4972)  at com.pega.pe"
                + "garules.data.internal.clipboard.ClipboardPageImpl.values(ClipboardPageImpl.java:4982)  at com.pega.pegarules.data.intern"
                + "al.clipboard.ClipboardPageBase.getMessagesMap(ClipboardPageBase.java:1085)  at com.pega.pegarules.data.internal.clipboar"
                + "d.ClipboardPageBase.getMessagesMap(ClipboardPageBase.java:1098)  at com.pega.pegarules.data.internal.clipboard.Clipboard"
                + "PageBase.getMessagesMap(ClipboardPageBase.java:1098)  at com.pega.pegarules.data.internal.clipboard.ClipboardPageBase.ge"
                + "tMessagesMap(ClipboardPageBase.java:1098)  at com.pega.pegarules.data.internal.clipboard.ClipboardPageBase.getMessagesMa"
                + "pByEntryHandle(ClipboardPageBase.java:1024)  at com.pega.pegarules.data.internal.clipboard.ClipboardPageImpl.getMessages"
                + "MapByEntryHandle(ClipboardPageImpl.java:482)  at com.pegarules.generated.html_section.ra_stream_errorlist_7893956e257c0b"
                + "d2fcbf958e614aa40d.execute(ra_stream_errorlist_7893956e257c0bd2fcbf958e614aa40d.java:120)  at com.pega.pegarules.session"
                + ".internal.mgmt.StreamBuilderTools.appendStreamKeepProperties(StreamBuilderTools.java:717)  at com.pega.pegarules.session"
                + ".internal.mgmt.autostreams.IncludeStreamRuntime.getStream(IncludeStreamRuntime.java:332)  at com.pega.pegarules.session."
                + "internal.mgmt.autostreams.IncludeStreamRuntime.emitIncludeStreamReference(IncludeStreamRuntime.java:252)  at com.pega.pe"
                + "garules.session.internal.mgmt.autostreams.AutoStreamRuntimeImpl.emitIncludeStreamReference(AutoStreamRuntimeImpl.java:35"
                + "8)  at com.pegarules.generated.html_section.ra_stream_errors_ea6f8491c5a6feec558b04f314301826.execute(ra_stream_errors_e"
                + "a6f8491c5a6feec558b04f314301826.java:147)  at com.pega.pegarules.session.internal.mgmt.Executable.getStream(Executable.j"
                + "ava:4033)  at com.pega.pegarules.session.internal.mgmt.Executable.getStream(Executable.java:3861)  at com.pegarules.gene"
                + "rated.html_harness.ra_stream_harnessfail_cc54e2e00e5fa3171adb4ff3b8301fe0.include_1(ra_stream_harnessfail_cc54e2e00e5fa3"
                + "171adb4ff3b8301fe0.java:3207)  at com.pegarules.generated.html_harness.ra_stream_harnessfail_cc54e2e00e5fa3171adb4ff3b83"
                + "01fe0.generatePegaHarnessDiv_6(ra_stream_harnessfail_cc54e2e00e5fa3171adb4ff3b8301fe0.java:2019)  at com.pegarules.gener"
                + "ated.html_harness.ra_stream_harnessfail_cc54e2e00e5fa3171adb4ff3b8301fe0.execute(ra_stream_harnessfail_cc54e2e00e5fa3171"
                + "adb4ff3b8301fe0.java:672)  at com.pega.pegarules.session.internal.mgmt.Executable.getStream(Executable.java:4033)  at co"
                + "m.pega.pegarules.session.internal.mgmt.Executable.getStream(Executable.java:3861)  at com.pegarules.generated.activity.r"
                + "a_action_show_harness_3a715fceb3725b54f857d8327a1c5bba.step6_circum0(ra_action_show_harness_3a715fceb3725b54f857d8327a1c"
                + "5bba.java:731)  at com.pegarules.generated.activity.ra_action_show_harness_3a715fceb3725b54f857d8327a1c5bba.perform(ra_a"
                + "ction_show_harness_3a715fceb3725b54f857d8327a1c5bba.java:155)  at com.pega.pegarules.session.internal.mgmt.Executable.do"
                + "Activity(Executable.java:3500)  at com.pega.pegarules.session.internal.mgmt.Executable.invokeActivity(Executable.java:10"
                + "514)  at com.pegarules.generated.activity.ra_action_activitystatusnocontenthandler_ff36b7afe477ab0eedd010dc329bf2bb.step"
                + "1_circum0(ra_action_activitystatusnocontenthandler_ff36b7afe477ab0eedd010dc329bf2bb.java:173)  at com.pegarules.generate"
                + "d.activity.ra_action_activitystatusnocontenthandler_ff36b7afe477ab0eedd010dc329bf2bb.perform(ra_action_activitystatusnoc"
                + "ontenthandler_ff36b7afe477ab0eedd010dc329bf2bb.java:69)  at com.pega.pegarules.session.internal.mgmt.Executable.doActivi"
                + "ty(Executable.java:3500)  at com.pega.pegarules.session.internal.mgmt.base.ThreadRunner.runActivitiesAlt(ThreadRunner.ja"
                + "va:646)  at com.pega.pegarules.session.internal.mgmt.PRThreadImpl.runActivitiesAlt(PRThreadImpl.java:461)  at com.pega.p"
                + "egarules.session.internal.engineinterface.service.HttpAPI.runActivities(HttpAPI.java:3322)  at com.pega.pegarules.sessio"
                + "n.internal.engineinterface.service.HttpAPI.postProcessContent(HttpAPI.java:3716)  at com.pega.pegarules.session.external"
                + ".engineinterface.service.EngineAPI.activityExecutionEpilog(EngineAPI.java:570)  at com.pega.pegarules.session.external.e"
                + "ngineinterface.service.EngineAPI.processRequestInner(EngineAPI.java:459)  at sun.reflect.GeneratedMethodAccessor68.invok"
                + "e(Unknown Source)  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)  at java.lan"
                + "g.reflect.Method.invoke(Method.java:606)  at com.pega.pegarules.session.internal.PRSessionProviderImpl.performTargetActi"
                + "onWithLock(PRSessionProviderImpl.java:1270)  at com.pega.pegarules.session.internal.PRSessionProviderImpl.doWithRequesto"
                + "rLocked(PRSessionProviderImpl.java:1008)  at com.pega.pegarules.session.internal.PRSessionProviderImpl.doWithRequestorLo"
                + "cked(PRSessionProviderImpl.java:841)  at com.pega.pegarules.session.external.engineinterface.service.EngineAPI.processRe"
                + "quest(EngineAPI.java:331)  at com.pega.pegarules.session.internal.engineinterface.service.HttpAPI.invoke(HttpAPI.java:85"
                + "0)  at com.pega.pegarules.session.internal.engineinterface.etier.impl.EngineImpl._invokeEngine_privact(EngineImpl.java:3"
                + "15)  at com.pega.pegarules.session.internal.engineinterface.etier.impl.EngineImpl.invokeEngine(EngineImpl.java:263)  at "
                + "com.pega.pegarules.session.internal.engineinterface.etier.impl.EngineImpl.invokeEngine(EngineImpl.java:240)  at com.pega"
                + ".pegarules.priv.context.JNDIEnvironment.invokeEngineInner(JNDIEnvironment.java:278)  at com.pega.pegarules.priv.context."
                + "JNDIEnvironment.invokeEngine(JNDIEnvironment.java:223)  at com.pega.pegarules.web.impl.WebStandardImpl.makeEtierRequest("
                + "WebStandardImpl.java:574)  at com.pega.pegarules.web.impl.WebStandardImpl.doPost(WebStandardImpl.java:374)  at sun.refle"
                + "ct.GeneratedMethodAccessor65.invoke(Unknown Source)  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethod"
                + "AccessorImpl.java:43)  at java.lang.reflect.Method.invoke(Method.java:606)  at com.pega.pegarules.internal.bootstrap.PRB"
                + "ootstrap.invokeMethod(PRBootstrap.java:367)  at com.pega.pegarules.internal.bootstrap.PRBootstrap.invokeMethodPropagatin"
                + "gThrowable(PRBootstrap.java:408)  at com.pega.pegarules.boot.internal.extbridge.AppServerBridgeToPega.invokeMethodPropag"
                + "atingThrowable(AppServerBridgeToPega.java:223)  at com.pega.pegarules.boot.internal.extbridge.AppServerBridgeToPega.invo"
                + "keMethod(AppServerBridgeToPega.java:272)  at com.pega.pegarules.internal.web.servlet.WebStandardBoot.doPost(WebStandardB"
                + "oot.java:121)  at javax.servlet.http.HttpServlet.service(HttpServlet.java:754)  at javax.servlet.http.HttpServlet.servic"
                + "e(HttpServlet.java:847)  at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java"
                + ":295)  at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:214)  at org.apache.catal"
                + "ina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:231)  at org.apache.catalina.core.StandardContextValve.in"
                + "voke(StandardContextValve.java:149)  at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.jav"
                + "a:420)  at org.jboss.as.web.session.ClusteredSessionValve.handleRequest(ClusteredSessionValve.java:134)  at org.jboss.as"
                + ".web.session.ClusteredSessionValve.invoke(ClusteredSessionValve.java:99)  at org.jboss.as.web.session.JvmRouteValve.invo"
                + "ke(JvmRouteValve.java:92)  at org.jboss.as.web.session.LockingValve.invoke(LockingValve.java:64)  at org.jboss.as.web.se"
                + "curity.SecurityContextAssociationValve.invoke(SecurityContextAssociationValve.java:169)  at org.apache.catalina.core.Sta"
                + "ndardHostValve.invoke(StandardHostValve.java:145)  at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValv"
                + "e.java:97)  at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:102)  at org.apache.catalina"
                + ".connector.CoyoteAdapter.service(CoyoteAdapter.java:344)  at org.apache.coyote.http11.Http11Processor.process(Http11Proc"
                + "essor.java:856)  at org.apache.coyote.http11.Http11Protocol$Http11ConnectionHandler.process(Http11Protocol.java:653)  at"
                + " org.apache.tomcat.util.net.JIoEndpoint$Worker.run(JIoEndpoint.java:926)  at java.lang.Thread.run(Thread.java:745) ]";

        LOG.info("dataText: " + dataText);

        EXCP0001ReportModel excp0001ReportModel = new EXCP0001ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = excp0001ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("com.pega.pegarules.pub.context.StaleRequestorError",
                alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString4() {

        String dataText = "[MSG][Problem during PRException.getRuleMessage: \"FUACache-FailAssembly [this:is:not:a:classname] produced no j"
                + "ava source\"][STACK][java.util.NoSuchElementException   at java.util.LinkedList.getLast(LinkedList.java:257)   at com.pe"
                + "ga.pegarules.monitor.internal.database.ReporterStackImpl.pop(ReporterStackImpl.java:130)   at com.pega.pegarules.data.in"
                + "ternal.access.DatabaseImpl.open(DatabaseImpl.java:4205)   at com.pega.pegarules.data.internal.access.DatabaseImpl.open(D"
                + "atabaseImpl.java:3800)   at com.pega.pegarules.data.internal.access.DatabaseImpl.open(DatabaseImpl.java:3768)   at com.p"
                + "ega.pegarules.session.internal.mgmt.MessageEvaluator.getRuleMessage(MessageEvaluator.java:437)   at com.pega.pegarules.s"
                + "ession.internal.mgmt.MessageEvaluator.getRuleMessage(MessageEvaluator.java:373)   at com.pega.pegarules.session.internal"
                + ".mgmt.MessageEvaluator.getRuleMessage(MessageEvaluator.java:324)   at com.pega.pegarules.session.internal.mgmt.MessageEv"
                + "aluator.getRuleMessage(MessageEvaluator.java:317)   at com.pega.pegarules.session.internal.PRSessionProviderImpl.getRule"
                + "Message(PRSessionProviderImpl.java:1775)   at com.pega.pegarules.session.internal.mgmt.Executable.getRuleMessage(Executa"
                + "ble.java:6988)   at com.pega.pegarules.pub.PRException.getRuleMessage(PRException.java:385)   at com.pega.pegarules.pub."
                + "PRException. (PRException.java:131)   at com.pega.pegarules.pub.generator.FirstUseAssemblerException. (FirstUseAssembler"
                + "Exception.java:55)   at com.pega.pegarules.generation.internal.cache.AssemblerFunctions.assembleRule(AssemblerFunctions."
                + "java:179)   at com.pega.pegarules.generation.internal.cache.AssemblyCacheBase.buildAndOrLoadJavaClass(AssemblyCacheBase."
                + "java:1675)   at com.pega.pegarules.generation.internal.cache.AssemblyCacheBase.getGeneratedJava(AssemblyCacheBase.java:3"
                + "091)   at com.pega.pegarules.generation.internal.cache.appcentric.RACacheAppCentricImpl.addEntryToMemoryCache(RACacheApp"
                + "CentricImpl.java:1161)   at com.pega.pegarules.generation.internal.cache.appcentric.RACacheAppCentricImpl.find(RACacheAp"
                + "pCentricImpl.java:1008)   at com.pega.pegarules.generation.internal.cache.AssemblyCacheWrapper.find(AssemblyCacheWrapper"
                + ".java:799)   at com.pega.pegarules.generation.internal.assembly.FUAManagerImpl.getInternal(FUAManagerImpl.java:1408)   a"
                + "t com.pega.pegarules.generation.internal.assembly.FUAManagerImpl.get(FUAManagerImpl.java:1296)   at com.pega.pegarules.g"
                + "eneration.internal.PRGenProviderImpl.get(PRGenProviderImpl.java:476)   at com.pega.pegarules.session.internal.mgmt.Execu"
                + "table.getStream(Executable.java:3992)   at com.pega.pegarules.session.internal.mgmt.Executable.getStream(Executable.java"
                + ":3866)   at com.pegarules.generated.activity.ra_action_show_harness_3a715fceb3725b54f857d8327a1c5bba.step6_circum0(ra_ac"
                + "tion_show_harness_3a715fceb3725b54f857d8327a1c5bba.java:684)   at com.pegarules.generated.activity.ra_action_show_harnes"
                + "s_3a715fceb3725b54f857d8327a1c5bba.perform(ra_action_show_harness_3a715fceb3725b54f857d8327a1c5bba.java:155)   at com.pe"
                + "ga.pegarules.session.internal.mgmt.Executable.doActivity(Executable.java:3505)   at com.pega.pegarules.session.internal."
                + "mgmt.Executable.invokeActivity(Executable.java:10563)   at com.pegarules.generated.activity.ra_action_activitystatusexce"
                + "ptionhandler_5e1c79001fa282ce50aa911a3a1e736f.step3_circum0(ra_action_activitystatusexceptionhandler_5e1c79001fa282ce50a"
                + "a911a3a1e736f.java:326)   at com.pegarules.generated.activity.ra_action_activitystatusexceptionhandler_5e1c79001fa282ce5"
                + "0aa911a3a1e736f.perform(ra_action_activitystatusexceptionhandler_5e1c79001fa282ce50aa911a3a1e736f.java:112)   at com.peg"
                + "a.pegarules.session.internal.mgmt.Executable.doActivity(Executable.java:3505)   at com.pega.pegarules.session.internal.m"
                + "gmt.base.ThreadRunner.runActivitiesAlt(ThreadRunner.java:646)   at com.pega.pegarules.session.internal.mgmt.PRThreadImpl"
                + ".runActivitiesAlt(PRThreadImpl.java:461)   at com.pega.pegarules.session.internal.engineinterface.service.HttpAPI.runAct"
                + "ivities(HttpAPI.java:3358)   at com.pega.pegarules.session.internal.engineinterface.service.HttpAPI.postProcessContent(H"
                + "ttpAPI.java:3754)   at com.pega.pegarules.session.external.engineinterface.service.EngineAPI.activityExecutionEpilog(Eng"
                + "ineAPI.java:570)   at com.pega.pegarules.session.external.engineinterface.service.EngineAPI.processRequestInner(EngineAP"
                + "I.java:459)   at sun.reflect.GeneratedMethodAccessor44.invoke(Unknown Source)   at sun.reflect.DelegatingMethodAccessorI"
                + "mpl.invoke(DelegatingMethodAccessorImpl.java:43)   at java.lang.reflect.Method.invoke(Method.java:497)   at com.pega.peg"
                + "arules.session.internal.PRSessionProviderImpl.performTargetActionWithLock(PRSessionProviderImpl.java:1270)   at com.pega"
                + ".pegarules.session.internal.PRSessionProviderImpl.doWithRequestorLocked(PRSessionProviderImpl.java:1008)   at com.pega.p"
                + "egarules.session.internal.PRSessionProviderImpl.doWithRequestorLocked(PRSessionProviderImpl.java:841)   at com.pega.pega"
                + "rules.session.external.engineinterface.service.EngineAPI.processRequest(EngineAPI.java:331)   at com.pega.pegarules.sess"
                + "ion.internal.engineinterface.service.HttpAPI.invoke(HttpAPI.java:852)   at com.pega.pegarules.session.internal.engineint"
                + "erface.etier.impl.EngineImpl._invokeEngine_privact(EngineImpl.java:315)   at com.pega.pegarules.session.internal.enginei"
                + "nterface.etier.impl.EngineImpl.invokeEngine(EngineImpl.java:263)   at com.pega.pegarules.session.internal.engineinterfac"
                + "e.etier.impl.EngineImpl.invokeEngine(EngineImpl.java:240)   at com.pega.pegarules.priv.context.JNDIEnvironment.invokeEng"
                + "ineInner(JNDIEnvironment.java:278)   at com.pega.pegarules.priv.context.JNDIEnvironment.invokeEngine(JNDIEnvironment.jav"
                + "a:223)   at com.pega.pegarules.web.impl.WebStandardImpl.makeEtierRequest(WebStandardImpl.java:574)   at com.pega.pegarul"
                + "es.web.impl.WebStandardImpl.doPost(WebStandardImpl.java:374)   at sun.reflect.GeneratedMethodAccessor43.invoke(Unknown S"
                + "ource)   at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)   at java.lang.reflect"
                + ".Method.invoke(Method.java:497)   at com.pega.pegarules.internal.bootstrap.PRBootstrap.invokeMethod(PRBootstrap.java:370"
                + ")   at com.pega.pegarules.internal.bootstrap.PRBootstrap.invokeMethodPropagatingThrowable(PRBootstrap.java:411)   at com"
                + ".pega.pegarules.boot.internal.extbridge.AppServerBridgeToPega.invokeMethodPropagatingThrowable(AppServerBridgeToPega.jav"
                + "a:223)   at com.pega.pegarules.boot.internal.extbridge.AppServerBridgeToPega.invokeMethod(AppServerBridgeToPega.java:272"
                + ")   at com.pega.pegarules.internal.web.servlet.WebStandardBoot.doPost(WebStandardBoot.java:121)   at javax.servlet.http."
                + "HttpServlet.service(HttpServlet.java:754)   at javax.servlet.http.HttpServlet.service(HttpServlet.java:847)   at org.apa"
                + "che.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:295)   at org.apache.catalina.core"
                + ".ApplicationFilterChain.doFilter(ApplicationFilterChain.java:214)   at org.apache.catalina.core.StandardWrapperValve.inv"
                + "oke(StandardWrapperValve.java:231)   at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:1"
                + "49)   at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:420)   at org.jboss.as.web.se"
                + "ssion.ClusteredSessionValve.handleRequest(ClusteredSessionValve.java:134)   at org.jboss.as.web.session.ClusteredSession"
                + "Valve.invoke(ClusteredSessionValve.java:99)   at org.jboss.as.web.session.JvmRouteValve.invoke(JvmRouteValve.java:92)   "
                + "at org.jboss.as.web.session.LockingValve.invoke(LockingValve.java:64)   at org.jboss.as.web.security.SecurityContextAsso"
                + "ciationValve.invoke(SecurityContextAssociationValve.java:169)   at org.apache.catalina.core.StandardHostValve.invoke(Sta"
                + "ndardHostValve.java:150)   at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:97)   at org.apac"
                + "he.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:102)   at org.apache.catalina.connector.CoyoteAdapt"
                + "er.service(CoyoteAdapter.java:344)   at org.apache.coyote.http11.Http11Processor.process(Http11Processor.java:854)   at "
                + "org.apache.coyote.http11.Http11Protocol$Http11ConnectionHandler.process(Http11Protocol.java:653)   at org.apache.tomcat."
                + "util.net.JIoEndpoint$Worker.run(JIoEndpoint.java:926)   at java.lang.Thread.run(Thread.java:745)  ]";

        LOG.info("dataText: " + dataText);

        EXCP0001ReportModel excp0001ReportModel = new EXCP0001ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = excp0001ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("java.util.NoSuchElementException", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString5() {

        String dataText = "[MSG][Section 'InvestementReasons' execution error on page 'tempPage' of class 'MEB-FW-OBFW-Data-Product'.][STAC"
                + "K][java.lang.NullPointerException  ]";

        LOG.info("dataText: " + dataText);

        EXCP0001ReportModel excp0001ReportModel = new EXCP0001ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = excp0001ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("java.lang.NullPointerException", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString6() {

        String dataText = "[MSG][Section 'pzGridModalHTML' execution error on page 'pyWorkPage' of  class ''.][STACK][java.lang.NullPointer"
                + "Exception<CR><CR>    at com.pegarules.generated.html.ra_stream_pzgridmodalhtml_87eccc7bbbfb6ad23b0144a6334e857d.execute("
                + "ra_stream_pzgridmodalhtml_87eccc7bbbfb6ad23b0144a6334e857d.java:827)<CR><CR>    at com.pega.pegarules.session.internal.m"
                + "gmt.Executable.getStream(Executable.java:4038)<CR><CR>    at com.pega.pegarules.session.internal.mgmt.Executable.getStre"
                + "am(Executable.java:3866)<CR><CR>    at com.pegarules.generated.html.ra_stream_pyshowstream_0ac1b9f64c756da2a320f899d95ff"
                + "35f.execute(ra_stream_pyshowstream_0ac1b9f64c756da2a320f899d95ff35f.java:129)<CR><CR>    at com.pega.pegarules.session.i"
                + "nternal.mgmt.Executable.getStream(Executable.java:4038)<CR><CR>    at com.pega.pegarules.session.internal.mgmt.Executabl"
                + "e.getStream(Executable.java:3866)<CR><CR>    at com.pega.pegarules.pub.runtime.AbstractActivity.showHtml(AbstractActivit"
                + "y.java:247)<CR><CR>    at com.pegarules.generated.activity.ra_action_showstream_f351378ddef289e737ba4d8fe14e1953.step6_c"
                + "ircum0(ra_action_showstream_f351378ddef289e737ba4d8fe14e1953.java:160)<CR><CR>    at com.pegarules.generated.activity.ra"
                + "_action_showstream_f351378ddef289e737ba4d8fe14e1953.perform(ra_action_showstream_f351378ddef289e737ba4d8fe14e1953.java:6"
                + "9)<CR><CR>    at com.pega.pegarules.session.internal.mgmt.Executable.doActivity(Executable.java:3505)<CR><CR>    at com."
                + "pega.pegarules.session.internal.mgmt.base.ThreadRunner.runActivitiesAlt(ThreadRunner.java:646)<CR><CR>    at com.pega.pe"
                + "garules.session.internal.mgmt.PRThreadImpl.runActivitiesAlt(PRThreadImpl.java:461)<CR><CR>    at com.pega.pegarules.sess"
                + "ion.internal.engineinterface.service.HttpAPI.runActivities(HttpAPI.java:3358)<CR><CR>    at com.pega.pegarules.session.e"
                + "xternal.engineinterface.service.EngineAPI.processRequestInner(EngineAPI.java:385)<CR><CR>    at sun.reflect.GeneratedMet"
                + "hodAccessor44.invoke(Unknown Source)<CR><CR>    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAcces"
                + "sorImpl.java:43)<CR><CR>    at java.lang.reflect.Method.invoke(Method.java:497)<CR><CR>    at com.pega.pegarules.session"
                + ".internal.PRSessionProviderImpl.performTargetActionWithLock(PRSessionProviderImpl.java:1270)<CR><CR>    at com.pega.pega"
                + "rules.session.internal.PRSessionProviderImpl.doWithRequestorLocked(PRSessionProviderImpl.java:1008)<CR><CR>    at com.pe"
                + "ga.pegarules.session.internal.PRSessionProviderImpl.doWithRequestorLocked(PRSessionProviderImpl.java:841)<CR><CR>    at "
                + "com.pega.pegarules.session.external.engineinterface.service.EngineAPI.processRequest(EngineAPI.java:331)<CR><CR>    at c"
                + "om.pega.pegarules.session.internal.engineinterface.service.HttpAPI.invoke(HttpAPI.java:852)<CR><CR>    at com.pega.pegar"
                + "ules.session.internal.engineinterface.etier.impl.EngineImpl._invokeEngine_privact(EngineImpl.java:315)<CR><CR>    at com"
                + ".pega.pegarules.session.internal.engineinterface.etier.impl.EngineImpl.invokeEngine(EngineImpl.java:263)<CR><CR>    at c"
                + "om.pega.pegarules.session.internal.engineinterface.etier.impl.EngineImpl.invokeEngine(EngineImpl.java:240)<CR><CR>    at"
                + " com.pega.pegarules.priv.context.JNDIEnvironment.invokeEngineInner(JNDIEnvironment.java:278)<CR><CR>    at com.pega.pega"
                + "rules.priv.context.JNDIEnvironment.invokeEngine(JNDIEnvironment.java:223)<CR><CR>    at com.pega.pegarules.web.impl.WebS"
                + "tandardImpl.makeEtierRequest(WebStandardImpl.java:574)<CR><CR>    at com.pega.pegarules.web.impl.WebStandardImpl.doPost("
                + "WebStandardImpl.java:374)<CR><CR>    at sun.reflect.GeneratedMethodAccessor43.invoke(Unknown Source)<CR><CR>    at sun.r"
                + "eflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)<CR><CR>    at java.lang.reflect.Method."
                + "invoke(Method.java:497)<CR><CR>    at com.pega.pegarules.internal.bootstrap.PRBootstrap.invokeMethod(PRBootstrap.java:37"
                + "0)<CR><CR>    at com.pega.pegarules.internal.bootstrap.PRBootstrap.invokeMethodPropagatingThrowable(PRBootstrap.java:411"
                + ")<CR><CR>    at com.pega.pegarules.boot.internal.extbridge.AppServerBridgeToPega.invokeMethodPropagatingThrowable(AppSer"
                + "verBridgeToPega.java:223)<CR><CR>    at com.pega.pegarules.boot.internal.extbridge.AppServerBridgeToPega.invokeMethod(Ap"
                + "pServerBridgeToPega.java:272)<CR><CR>    at com.pega.pegarules.internal.web.servlet.WebStandardBoot.doPost(WebStandardBo"
                + "ot.java:121)<CR><CR>    at javax.servlet.http.HttpServlet.service(HttpServlet.java:754)<CR><CR>    at javax.servlet.http"
                + ".HttpServlet.service(HttpServlet.java:847)<CR><CR>    at org.apache.catalina.core.ApplicationFilterChain.internalDoFilte"
                + "r(ApplicationFilterChain.java:295)<CR><CR>    at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFil"
                + "terChain.java:214)<CR><CR>    at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:231)<CR>"
                + "<CR>    at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:149)<CR><CR>    at org.apache."
                + "catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:420)<CR><CR>    at org.jboss.as.web.session.Clust"
                + "eredSessionValve.handleRequest(ClusteredSessionValve.java:134)<CR><CR>    at org.jboss.as.web.session.ClusteredSessionVa"
                + "lve.invoke(ClusteredSessionValve.java:99)<CR><CR>    at org.jboss.as.web.session.JvmRouteValve.invoke(JvmRouteValve.java"
                + ":92)<CR><CR>    at org.jboss.as.web.session.LockingValve.invoke(LockingValve.java:64)<CR><CR>    at org.jboss.as.web.sec"
                + "urity.SecurityContextAssociationValve.invoke(SecurityContextAssociationValve.java:169)<CR><CR>    at org.apache.catalina"
                + ".core.StandardHostValve.invoke(StandardHostValve.java:150)<CR><CR>    at org.apache.catalina.valves.ErrorReportValve.inv"
                + "oke(ErrorReportValve.java:97)<CR><CR>    at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java"
                + ":102)<CR><CR>    at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:344)<CR><CR>    at org.apache"
                + ".coyote.http11.Http11Processor.process(Http11Processor.java:854)<CR><CR>    at org.apache.coyote.http11.Http11Protocol$H"
                + "ttp11ConnectionHandler.process(Http11Protocol.java:653)<CR><CR>    at org.apache.tomcat.util.net.JIoEndpoint$Worker.run("
                + "JIoEndpoint.java:926)<CR><CR>    at java.lang.Thread.run(Thread.java:745)<CR><CR>]";

        LOG.info("dataText: " + dataText);

        EXCP0001ReportModel excp0001ReportModel = new EXCP0001ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = excp0001ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("java.lang.NullPointerException", alertMessageReportEntryKey);
    }

}
