/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.parser;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pega.gcs.logviewer.model.ThreadDumpThreadInfo;
import com.pega.gcs.logviewer.model.ThreadDumpThreadInfoV6;
import com.pega.gcs.logviewer.model.ThreadDumpThreadInfoV7;

public class LogThreadDumpParser {

	public static List<ThreadDumpThreadInfo> parseThreadLineList(List<String> threadLineList) {

		List<ThreadDumpThreadInfo> threadDumpThreadInfoList = null;

		int tdVersion = getThreadDumpVersion(threadLineList);

		switch (tdVersion) {

		case 6:
			threadDumpThreadInfoList = processV6ThreadDump(threadLineList);
			break;

		case 7:
			threadDumpThreadInfoList = processV7ThreadDump(threadLineList);
			break;
		default:
			break;

		}

		return threadDumpThreadInfoList;
	}

	private static int getThreadDumpVersion(List<String> threadLineList) {

		int tdVersion = 6;

		String v7Text = "Full Java thread dump with locks info";

		for (int i = 0; i < 5; i++) {

			String threadLine = threadLineList.get(i);

			if (threadLine.contains(v7Text)) {
				tdVersion = 7;
				break;
			}
		}

		return tdVersion;
	}

	private static List<ThreadDumpThreadInfo> processV6ThreadDump(List<String> threadLineList) {

		List<ThreadDumpThreadInfo> threadDumpThreadInfoList;

		// String threadRegEx = "Thread\\[(.*)\\]";
		String threadRegEx = "Thread\\[(.+?),(\\d),(.+?)\\]";

		Pattern threadPattern = Pattern.compile(threadRegEx);

		threadDumpThreadInfoList = processThreadDump(threadPattern, threadLineList, false);

		return threadDumpThreadInfoList;
	}

	private static List<ThreadDumpThreadInfo> processV7ThreadDump(List<String> threadLineList) {

		List<ThreadDumpThreadInfo> threadDumpThreadInfoList;

		// String threadRegEx = "\"(.*)\"[ ]Id=(\\d{1,19})[ ]in[ ](.*)";
		String threadRegEx = "\"(.*)\"[ ]Id=(\\d{1,19})[ ]in[ ](.*?)(on.*?)?(\\(.*\\))?";
		Pattern threadPattern = Pattern.compile(threadRegEx);

		threadDumpThreadInfoList = processThreadDump(threadPattern, threadLineList, true);

		return threadDumpThreadInfoList;
	}

	private static List<ThreadDumpThreadInfo> processThreadDump(Pattern threadPattern, List<String> threadLineList,
			boolean v7) {

		List<ThreadDumpThreadInfo> threadDumpThreadInfoList;
		threadDumpThreadInfoList = new LinkedList<ThreadDumpThreadInfo>();

		ThreadDumpThreadInfo threadDumpThreadInfo = null;

		List<String> threadSubLineList = new LinkedList<String>();

		long threadIDCounter = 0;

		Iterator<String> threadLineIt = threadLineList.iterator();

		while (threadLineIt.hasNext()) {

			String threadLine = threadLineIt.next();

			Matcher lineMatcher = threadPattern.matcher(threadLine);

			if (lineMatcher.matches()) {

				// processing the previous captured entries
				if (threadDumpThreadInfo != null) {

					updateThreadDumpThread(threadDumpThreadInfo, threadSubLineList);

					threadDumpThreadInfoList.add(threadDumpThreadInfo);

					// reset state variables
					threadSubLineList = new LinkedList<String>();
					threadDumpThreadInfo = null;
				} else {
					// discard the initial lines
					threadSubLineList = new LinkedList<String>();
				}

				long threadId = -1;
				String threadName = null;

				if (v7) {

					String threadState = null;
					String lockName = null;
					Boolean inNative = null;
					Boolean suspended = null;

					// process v7 thread pattern

					int groupCount = lineMatcher.groupCount();

					if (groupCount >= 3) {
						String threadIDStr = lineMatcher.group(2).trim();
						threadId = Long.parseLong(threadIDStr);

						threadState = lineMatcher.group(3).trim();

						for (int counter = 4; counter <= groupCount; counter++) {

							String text = lineMatcher.group(counter);

							if (text != null) {

								text = text.trim();
								if (text.startsWith("on lock=")) {

									int beginIndex = "on lock=".length();
									int endIndex = text.length();

									lockName = text.substring(beginIndex, endIndex);

								} else if (text.startsWith("(suspended)")) {
									suspended = true;
								} else if (text.startsWith("(running in native)")) {
									inNative = true;
								}
							}
						}

					}

					threadName = lineMatcher.group(1);

					ThreadDumpThreadInfoV7 threadDumpThreadInfoV7;

					threadDumpThreadInfoV7 = new ThreadDumpThreadInfoV7(threadId, threadName, threadLine);

					threadDumpThreadInfoV7.setThreadState(threadState);
					threadDumpThreadInfoV7.setLockName(lockName);
					threadDumpThreadInfoV7.setSuspended(suspended);
					threadDumpThreadInfoV7.setInNative(inNative);

					threadDumpThreadInfo = threadDumpThreadInfoV7;
				} else {

					String priority = null;
					String groupName = null;

					// arbitrary thread id
					threadId = threadIDCounter;

					threadName = lineMatcher.group(1);
					priority = lineMatcher.group(2);
					groupName = lineMatcher.group(3);

					ThreadDumpThreadInfoV6 threadDumpThreadInfoV6;

					threadDumpThreadInfoV6 = new ThreadDumpThreadInfoV6(threadId, threadName, threadLine);

					threadDumpThreadInfoV6.setPriority(priority);
					threadDumpThreadInfoV6.setGroupName(groupName);

					threadIDCounter++;

					threadDumpThreadInfo = threadDumpThreadInfoV6;

				}

			} else {
				threadSubLineList.add(threadLine);
			}

		}

		if ((threadSubLineList.size() > 0) && (threadDumpThreadInfo != null)) {

			updateThreadDumpThread(threadDumpThreadInfo, threadSubLineList);

			threadDumpThreadInfoList.add(threadDumpThreadInfo);
		}

		return threadDumpThreadInfoList;
	}

	private static void updateThreadDumpThread(ThreadDumpThreadInfo threadDumpThreadInfo,
			List<String> threadSubLineList) {

		String stackRegEx = "^\\s+at.*";
		Pattern stackPattern = Pattern.compile(stackRegEx);

		String stackMethodRegEx = "at(.*?)\\(";
		Pattern stackMethodPattern = Pattern.compile(stackMethodRegEx);

		// parse state flags
		boolean stackMethod = false;
		boolean pegaThread = false;
		boolean lockedSynchronizers = false;
		boolean threadInfo = false;
		int stackDepth = 0;

		// find innermost method
		for (String threadSubLine : threadSubLineList) {

			Matcher stackMatcher = stackPattern.matcher(threadSubLine);

			if (stackMatcher.matches()) {

				if (!stackMethod) {

					String stackMethodStr = threadSubLine.trim();

					Matcher stackMethodMatcher = stackMethodPattern.matcher(stackMethodStr);

					boolean matches = stackMethodMatcher.find();

					if (matches) {
						stackMethodStr = stackMethodMatcher.group(1).trim();
					}

					threadDumpThreadInfo.setStackMethod(stackMethodStr);
					stackMethod = true;
				}

				stackDepth++;
			}

			if ((!pegaThread) && (threadSubLine.contains("pega"))) {

				pegaThread = true;

				threadDumpThreadInfo.setPegaThread(pegaThread);

			} else if (threadDumpThreadInfo instanceof ThreadDumpThreadInfoV7) {

				ThreadDumpThreadInfoV7 threadDumpThreadInfoV7;
				threadDumpThreadInfoV7 = (ThreadDumpThreadInfoV7) threadDumpThreadInfo;

				if ((!lockedSynchronizers) && (threadSubLine.trim().startsWith("Locked synchronizers"))) {

					int countLen = "count =".length();
					int beginIndex = threadSubLine.indexOf("count =") + countLen;
					int endIndex = threadSubLine.length();

					String lockedSync = threadSubLine.substring(beginIndex, endIndex);

					lockedSync = lockedSync.trim();

					int lockedSyncCount = 0;
					lockedSyncCount = new Integer(Integer.parseInt(lockedSync));

					threadDumpThreadInfoV7.setLockedSynchronizersCount(lockedSyncCount);
					lockedSynchronizers = true;
				}

				if ((!threadInfo) && (threadSubLine.trim().startsWith("BlockedCount"))) {

					String[] threadInfoArray = threadSubLine.split(",");

					for (String threadInfoStr : threadInfoArray) {

						String[] threadInfoKeyValue = threadInfoStr.split(":");

						String threadInfoKey = threadInfoKeyValue[0].trim();
						String threadInfoValue = threadInfoKeyValue[1].trim();
						long threadInfoVal = Long.parseLong(threadInfoValue);

						if ("BlockedCount".equals(threadInfoKey)) {
							threadDumpThreadInfoV7.setBlockedCount(threadInfoVal);
						} else if ("BlockedTime".equals(threadInfoKey)) {
							threadDumpThreadInfoV7.setBlockedTime(threadInfoVal);
						} else if ("WaitedCount".equals(threadInfoKey)) {
							threadDumpThreadInfoV7.setWaitedCount(threadInfoVal);
						} else if ("WaitedTime".equals(threadInfoKey)) {
							threadDumpThreadInfoV7.setWaitedTime(threadInfoVal);
						}
					}

					threadInfo = true;
				}
			}
		}

		threadDumpThreadInfo.setStackDepth(stackDepth);

		threadDumpThreadInfo.getThreadSubLineList().addAll(threadSubLineList);
	}
}
