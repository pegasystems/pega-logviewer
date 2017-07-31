/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.model;

public class ThreadDumpThreadInfoV7 extends ThreadDumpThreadInfo {

	private String threadState;

	private String lockName;

	private Long blockedTime;

	private Long blockedCount;

	private Long waitedTime;

	private Long waitedCount;

	private Boolean inNative;

	private Boolean suspended;

	private Integer lockedSynchronizersCount;

	public ThreadDumpThreadInfoV7(Long threadID, String threadName, String threadText) {

		super(threadID, threadName, threadText);

		this.threadState = null;
		this.lockName = null;
		this.blockedTime = null;
		this.blockedCount = null;
		this.waitedTime = null;
		this.waitedCount = null;
		this.inNative = null;
		this.suspended = null;
		this.lockedSynchronizersCount = null;
	}

	public String getThreadState() {
		return threadState;
	}

	public void setThreadState(String threadState) {
		this.threadState = threadState;
	}

	public String getLockName() {
		return lockName;
	}

	public void setLockName(String lockName) {
		this.lockName = lockName;
	}

	public Long getBlockedTime() {
		return blockedTime;
	}

	public void setBlockedTime(Long blockedTime) {
		this.blockedTime = blockedTime;
	}

	public Long getBlockedCount() {
		return blockedCount;
	}

	public void setBlockedCount(Long blockedCount) {
		this.blockedCount = blockedCount;
	}

	public Long getWaitedTime() {
		return waitedTime;
	}

	public void setWaitedTime(Long waitedTime) {
		this.waitedTime = waitedTime;
	}

	public Long getWaitedCount() {
		return waitedCount;
	}

	public void setWaitedCount(Long waitedCount) {
		this.waitedCount = waitedCount;
	}

	public Boolean isInNative() {
		return inNative;
	}

	public void setInNative(Boolean inNative) {
		this.inNative = inNative;
	}

	public Boolean isSuspended() {
		return suspended;
	}

	public void setSuspended(Boolean suspended) {
		this.suspended = suspended;
	}

	public Integer getLockedSynchronizersCount() {
		return lockedSynchronizersCount;
	}

	public void setLockedSynchronizersCount(Integer lockedSynchronizersCount) {
		this.lockedSynchronizersCount = lockedSynchronizersCount;
	}

	@Override
	public Object getValue(String columnName) {

		Object value = null;

		// THREADNAME
		// ID
		// STATE
		// STACKMETHOD
		// STACKDEPTH
		// PEGATHREAD
		// SUSPENDED
		// NATIVE
		// LOCKNAME
		// BLOCKEDCOUNT
		// BLOCKEDTIME
		// WAITEDCOUNT
		// WAITEDTIME
		// LOCKEDSYNCHRONIZERSCOUNT

		switch (columnName) {
		case "THREADNAME":
			value = getThreadName();
			break;
		case "ID":
			value = getThreadId();
			break;
		case "STATE":
			value = getThreadState();
			break;
		case "STACKMETHOD":
			value = getStackMethod();
			break;
		case "STACKDEPTH":
			value = getStackDepth();
			break;
		case "PEGATHREAD":
			value = isPegaThread();
			break;
		case "SUSPENDED":
			value = isSuspended();
			break;
		case "NATIVE":
			value = isInNative();
			break;
		case "LOCKNAME":
			value = getLockName();
			break;
		case "BLOCKEDCOUNT":
			value = getBlockedCount();
			break;
		case "BLOCKEDTIME":
			value = getBlockedTime();
			break;
		case "WAITEDCOUNT":
			value = getWaitedCount();
			break;
		case "WAITEDTIME":
			value = getWaitedTime();
			break;
		case "LOCKEDSYNCHRONIZERSCOUNT":
			value = getLockedSynchronizersCount();
			break;

		}

		return value;
	}
}
