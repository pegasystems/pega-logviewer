/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.systemscan.view;

public enum SystemScanViewMode {

	// @formatter:off
	SINGLE_TABLE        ( "Table"                   ),
	COMPARE_TABLE       ( "Compare"                 );
	// @formatter:on

	private String displaytext;

	private SystemScanViewMode(String displaytext) {
		this.displaytext = displaytext;
	}

	public String getDisplaytext() {
		return displaytext;
	}

	@Override
	public String toString() {
		return displaytext;
	}
}
