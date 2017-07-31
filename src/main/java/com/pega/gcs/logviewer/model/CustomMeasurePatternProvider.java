/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class CustomMeasurePatternProvider {

	private static final Log4j2Helper LOG = new Log4j2Helper(CustomMeasurePatternProvider.class);

	private static final String CUSTOM_MEASURE_PATTERNS_TXT = "/CustomMeasurePatterns.txt";

	private static CustomMeasurePatternProvider _INSTANCE;

	private List<Log4jMeasurePattern> customMeasurePatternList;

	private CustomMeasurePatternProvider() {

		customMeasurePatternList = new ArrayList<>();

		// load custom measure patterns
		String pwd = System.getProperty("user.dir");
		File extCMPFile = new File(pwd, CUSTOM_MEASURE_PATTERNS_TXT);
		InputStream cmpInputStream = null;

		if (extCMPFile.exists()) {
			try {
				cmpInputStream = new FileInputStream(extCMPFile);
			} catch (FileNotFoundException e) {
				LOG.error("File not found: " + extCMPFile, e);
			}
		} else {
			cmpInputStream = getClass().getResourceAsStream(CUSTOM_MEASURE_PATTERNS_TXT);
		}

		if (cmpInputStream != null) {

			Charset utf8Charset = Charset.forName("UTF-8");

			try {

				try (InputStreamReader isr = new InputStreamReader(cmpInputStream, utf8Charset);) {

					try (BufferedReader br = new BufferedReader(isr)) {

						String line = br.readLine();

						while (line != null) {

							if (!line.startsWith("##")) {

								String[] strArray = line.split("==");

								if (strArray.length == 3) {

									String name = strArray[0];
									String unit = strArray[1];
									String patternStr = strArray[2];

									try {
										Pattern pattern = Pattern.compile(patternStr);

										Log4jMeasurePattern cmp = new Log4jMeasurePattern(name, unit, pattern);

										customMeasurePatternList.add(cmp);

									} catch (PatternSyntaxException pse) {
										LOG.error("Error compiling pattern: " + patternStr, pse);
									}
								} else {
									LOG.info("CustomMeasurePatternProvider - discarding: " + line);
								}
							}

							line = br.readLine();
						}
					}
				}
			} catch (Exception e) {
				LOG.error("Error loading Custom Measure Pattern List", e);
			} finally {
				LOG.info("CustomMeasurePatternProvider - got " + customMeasurePatternList.size() + " patterns");
			}
		}
	}

	public static CustomMeasurePatternProvider getInstance() {

		if (_INSTANCE == null) {
			_INSTANCE = new CustomMeasurePatternProvider();
		}

		return _INSTANCE;
	}

	public List<Log4jMeasurePattern> getCustomMeasurePatternList() {
		return customMeasurePatternList;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
