/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.model;

import java.util.regex.Pattern;

public class Log4jMeasurePattern {

	private String name;

	private String unit;

	private Pattern pattern;

	public Log4jMeasurePattern(String name, String unit, Pattern pattern) {
		super();
		this.name = name;
		this.unit = unit;
		this.pattern = pattern;
	}

	public String getName() {
		return name;
	}

	public String getUnit() {
		return unit;
	}

	public Pattern getPattern() {
		return pattern;
	}

}
