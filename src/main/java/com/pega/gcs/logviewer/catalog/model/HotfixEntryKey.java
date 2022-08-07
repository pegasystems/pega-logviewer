/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.catalog.model;

import java.io.Serializable;
import java.util.Objects;

import com.pega.gcs.fringecommon.utilities.Identifiable;

public class HotfixEntryKey implements Comparable<HotfixEntryKey>, Identifiable<Integer>, Serializable {

    private static final long serialVersionUID = -7168939175693927250L;

    private int id;

    private Integer hotfixNumber;

    // for kyro
    @SuppressWarnings("unused")
    private HotfixEntryKey() {
        super();
    }

    public HotfixEntryKey(int id, String hotfixId) {
        super();
        this.id = id;

        String hotfixId1NumberPart = hotfixId.split("-", 0)[1];

        this.hotfixNumber = Integer.parseInt(hotfixId1NumberPart);
    }

    protected HotfixEntryKey(int id, Integer hotfixNumber) {
        this.id = id;
        this.hotfixNumber = hotfixNumber;
    }

    public int getId() {
        return id;
    }

    public Integer getHotfixNumber() {
        return hotfixNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hotfixNumber, id);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof HotfixEntryKey)) {
            return false;
        }

        HotfixEntryKey other = (HotfixEntryKey) obj;

        return Objects.equals(hotfixNumber, other.hotfixNumber) && id == other.id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("HFIX-");
        sb.append(hotfixNumber);
        return sb.toString();
    }

    @Override
    public int compareTo(HotfixEntryKey other) {
        return getKey().compareTo(other.getKey());
    }

    @Override
    public Integer getKey() {
        return getId();
    }
}
