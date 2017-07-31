/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.pegatdp;

import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PegaThreadDumpParser {

	private static final Log4j2Helper LOG = new Log4j2Helper(PegaThreadDumpParser.class);

	private static final PegaThreadDumpParser _INSTANCE = new PegaThreadDumpParser();

	private static String JSON_PARAM_TD_TIME = "JSON_PARAM_TD_TIME";
	private static String JSON_PARAM_TD_LINE = "JSON_PARAM_TD_LINE";
	private static String JSON_PARAM_TD_FILE = "JSON_PARAM_TD_FILE";

	private boolean initialised;

	// private static Class<?> threadDumpParserFactoryClass;
	// private static Class<?> threadDumpParserClass;
	// private static Class<? extends Enum> dumpFormatEnum;

	private static Class<?> pegaThreadDumpParserClass;

	private static Class<?> threadDumpClass;

	private static Class<?> commandLineClientClass;

	private static Class<?> reportTemplateClass;

	private PegaThreadDumpParser() {

		try {

			initialised = false;

			String pwd = System.getProperty("user.dir");
			Properties props = System.getProperties();
			props.setProperty("tdp.home", pwd);

			// threadDumpParserFactoryClass =
			// Class.forName("com.pega.gcs.logs.threaddumpparser.ThreadDumpParserFactory");
			// threadDumpParserClass =
			// Class.forName("com.pega.gcs.logs.threaddumpparser.ThreadDumpParser");
			// dumpFormatEnum = (Class<? extends Enum>)
			// Class.forName("com.pega.gcs.logs.threaddumpparser.ThreadDumpParser$DumpFormat");

			pegaThreadDumpParserClass = Class.forName("com.pega.gcs.logs.threaddumpparser.PegaThreadDumpParser");
			threadDumpClass = Class.forName("com.pega.gcs.logs.threaddumpparser.ThreadDump");
			commandLineClientClass = Class.forName("com.pega.gcs.logs.threaddumpparser.cli.CommandLineClient");
			reportTemplateClass = Class.forName("com.pega.gcs.logs.threaddumpparser.report.ReportTemplate");

			initialised = true;
			// } catch (ClassNotFoundException cnfe) {
			// LOG.info(cnfe.getMessage());

		} catch (Exception e) {
			LOG.error("Error initialising PegaThreadDumpParser", e);
		}

	}

	public static PegaThreadDumpParser getInstance() {
		return _INSTANCE;
	}

	public boolean isInitialised() {
		return initialised;
	}

	public Object getThreadDumpObject(String threadDumpText) {

		Object threadDump = null;

		if (isInitialised()) {

			try {

				Constructor<?> pegaThreadDumpParserConstructor = pegaThreadDumpParserClass
						.getDeclaredConstructor(new Class[0]);
				pegaThreadDumpParserConstructor.setAccessible(true);

				Object pegaThreadDumpParser = pegaThreadDumpParserConstructor.newInstance();

				Method parseMethod = pegaThreadDumpParserClass.getDeclaredMethod("parse", String.class);

				threadDump = parseMethod.invoke(pegaThreadDumpParser, threadDumpText);

			} catch (Exception e) {
				LOG.error("Error getting ThreadDumpObject", e);
			}
		}

		return threadDump;
	}

	public String getHTMLReport(Object threadDump, String filename, String line, String time) {

		String htmlReport = null;

		if (isInitialised()) {

			try {

				Field m_TemplateField = commandLineClientClass.getDeclaredField("m_Template");
				m_TemplateField.setAccessible(true);

				Object commandLineClient = commandLineClientClass.newInstance();

				Object m_Template = m_TemplateField.get(commandLineClient);

				HashMap<String, String> params = new HashMap<>();
				params.put(JSON_PARAM_TD_FILE, filename);
				params.put(JSON_PARAM_TD_LINE, line);
				params.put(JSON_PARAM_TD_TIME, time);

				StringWriter sw = new StringWriter();

				// get the html report
				Method generateReportMethod = threadDumpClass.getDeclaredMethod("generateReport", Map.class,
						reportTemplateClass, Writer.class);
				generateReportMethod.invoke(threadDump, params, m_Template, sw);

				htmlReport = sw.toString();
			} catch (Exception e) {
				LOG.error("Error getting html report", e);
			}
		}

		return htmlReport;
	}

	public List<FindingAdapter> getFindingAdapterList(Object threadDump) {

		List<FindingAdapter> findingAdapterList = new ArrayList<>();

		if ((isInitialised()) && (threadDump != null)) {

			try {

				Method getFindingsMethod = threadDumpClass.getDeclaredMethod("getFindings");

				Collection<?> findings = (Collection<?>) getFindingsMethod.invoke(threadDump, (Object[]) null);

				for (Object finding : findings) {

					FindingAdapter findingAdapter = FindingAdapterFactory.getInstance().getFindingAdapter(finding);
					findingAdapterList.add(findingAdapter);

				}

			} catch (Exception e) {
				LOG.error("Error getting FindingAdapterList", e);
			}
		}

		return findingAdapterList;
	}

}
