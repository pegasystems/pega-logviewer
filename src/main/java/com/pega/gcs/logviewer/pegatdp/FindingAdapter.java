/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.pegatdp;

import java.util.Map;

public interface FindingAdapter {

	// Finding methods
	public Integer getId();

	public String getName();

	public String getCategory();

	public String[] getSymptoms();

	public Map<String, String> getApplyTo();

	public String getDescription();

	public Enum<?> getSeverity();

	public String getAdvice();

	public Map<String, Object> getDetails();

}
