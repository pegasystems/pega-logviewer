/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;

import com.pega.gcs.fringecommon.guiutilities.DefaultTableColumn;

public class ThreadDumpColumn extends DefaultTableColumn {

	// @formatter:off
	public static final ThreadDumpColumn THREADNAME               = new ThreadDumpColumn( "THREADNAME"               , "Thread Name"                , 400 , SwingConstants.LEFT   , false);
	public static final ThreadDumpColumn ID                       = new ThreadDumpColumn( "ID"                       , "ID"                         , 50  , SwingConstants.CENTER , false);
	public static final ThreadDumpColumn STATE                    = new ThreadDumpColumn( "STATE"                    , "State"                      , 120 , SwingConstants.CENTER , true );
	public static final ThreadDumpColumn LOCKNAME                 = new ThreadDumpColumn( "LOCKNAME"                 , "LockName"                   , 400 , SwingConstants.LEFT   , true );
	public static final ThreadDumpColumn SUSPENDED                = new ThreadDumpColumn( "SUSPENDED"                , "Is Suspended"               , 120 , SwingConstants.CENTER , true );
	public static final ThreadDumpColumn NATIVE                   = new ThreadDumpColumn( "NATIVE"                   , "Is Native"                  , 80  , SwingConstants.CENTER , true );
	public static final ThreadDumpColumn BLOCKEDCOUNT             = new ThreadDumpColumn( "BLOCKEDCOUNT"             , "Blocked Count"              , 120 , SwingConstants.CENTER , true );
	public static final ThreadDumpColumn BLOCKEDTIME              = new ThreadDumpColumn( "BLOCKEDTIME"              , "Blocked Time"               , 120 , SwingConstants.CENTER , true );
	public static final ThreadDumpColumn WAITEDCOUNT              = new ThreadDumpColumn( "WAITEDCOUNT"              , "Waited Count"               , 120 , SwingConstants.CENTER , true );
	public static final ThreadDumpColumn WAITEDTIME               = new ThreadDumpColumn( "WAITEDTIME"               , "Waited Time"                , 120 , SwingConstants.CENTER , true );
	public static final ThreadDumpColumn LOCKEDSYNCHRONIZERSCOUNT = new ThreadDumpColumn( "LOCKEDSYNCHRONIZERSCOUNT" , "Locked Synchronizers Count" , 200 , SwingConstants.CENTER , true );
	public static final ThreadDumpColumn PRIORITY                 = new ThreadDumpColumn( "PRIORITY"                 , "Priority"                   , 50  , SwingConstants.CENTER , true );
	public static final ThreadDumpColumn GROUPNAME                = new ThreadDumpColumn( "GROUPNAME"                , "Group Name"                 , 150 , SwingConstants.CENTER , true );
	public static final ThreadDumpColumn STACKMETHOD              = new ThreadDumpColumn( "STACKMETHOD"              , "Stack Method"               , 250 , SwingConstants.LEFT   , true );
	public static final ThreadDumpColumn STACKDEPTH               = new ThreadDumpColumn( "STACKDEPTH"               , "Stack Depth"                , 120 , SwingConstants.CENTER , true );
	public static final ThreadDumpColumn PEGATHREAD               = new ThreadDumpColumn( "PEGATHREAD"               , "Is Pega Thread"             , 120 , SwingConstants.CENTER , true );
	// @formatter:on

	private ThreadDumpColumn(String columnId, String displayName, int prefColumnWidth, int horizontalAlignment, boolean filterable) {
		super(columnId, displayName, prefColumnWidth, horizontalAlignment, true, filterable);
	}

	public static List<ThreadDumpColumn> getV7ThreadDumpColumnList() {

		List<ThreadDumpColumn> v7ThreadDumpColumnList = new ArrayList<ThreadDumpColumn>();

		v7ThreadDumpColumnList.add(THREADNAME);
		v7ThreadDumpColumnList.add(ID);
		v7ThreadDumpColumnList.add(STATE);
		v7ThreadDumpColumnList.add(STACKMETHOD);
		v7ThreadDumpColumnList.add(STACKDEPTH);
		v7ThreadDumpColumnList.add(PEGATHREAD);
		v7ThreadDumpColumnList.add(SUSPENDED);
		v7ThreadDumpColumnList.add(NATIVE);
		v7ThreadDumpColumnList.add(LOCKNAME);
		v7ThreadDumpColumnList.add(BLOCKEDCOUNT);
		v7ThreadDumpColumnList.add(BLOCKEDTIME);
		v7ThreadDumpColumnList.add(WAITEDCOUNT);
		v7ThreadDumpColumnList.add(WAITEDTIME);
		v7ThreadDumpColumnList.add(LOCKEDSYNCHRONIZERSCOUNT);

		return v7ThreadDumpColumnList;
	}

	public static List<ThreadDumpColumn> getV6ThreadDumpColumnList() {

		List<ThreadDumpColumn> v6ThreadDumpColumnList = new ArrayList<ThreadDumpColumn>();

		v6ThreadDumpColumnList.add(THREADNAME);
		v6ThreadDumpColumnList.add(ID);
		v6ThreadDumpColumnList.add(STACKMETHOD);
		v6ThreadDumpColumnList.add(STACKDEPTH);
		v6ThreadDumpColumnList.add(PEGATHREAD);
		v6ThreadDumpColumnList.add(PRIORITY);
		v6ThreadDumpColumnList.add(GROUPNAME);

		return v6ThreadDumpColumnList;
	}
}
