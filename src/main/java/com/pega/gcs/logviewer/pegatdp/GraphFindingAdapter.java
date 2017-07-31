/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.pegatdp;

import java.awt.Component;

public interface GraphFindingAdapter extends FindingAdapter {

	// GraphFinding methods
	public Boolean isCyclic();

	public String getCyclicPath();

	public Integer getThreadsCount();

	public String getWaitForGraph();

	public String getResourceAllocationGraph();

	public String getRootName();

	// custom methods
	public Component getWaitForGraphComponent();

	public Component getResourceAllocationGraphComponent();
}
