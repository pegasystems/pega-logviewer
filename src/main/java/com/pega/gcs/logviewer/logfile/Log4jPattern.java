/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.logfile;

import java.io.Serializable;
import java.util.Objects;

public class Log4jPattern extends AbstractLogPattern implements Serializable, Comparable<Log4jPattern> {

    private static final long serialVersionUID = -6759786715613598099L;

    private String patternString;

    private Log4jPattern() {
        // for kryo
    }

    public Log4jPattern(String name, String patternString, int groupCount, boolean isCW) {

        super(LogType.PEGA_RULES, name, groupCount, isCW);

        this.patternString = patternString;
    }

    public String getPatternString() {
        return patternString;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(patternString);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Log4jPattern)) {
            return false;
        }
        Log4jPattern other = (Log4jPattern) obj;
        return Objects.equals(patternString, other.patternString);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        StringBuilder displayString = new StringBuilder();
        displayString.append(super.toString());
        displayString.append(" [ PatternString(");
        displayString.append(patternString);
        displayString.append(")]");

        return displayString.toString();
    }

    @Override
    public int compareTo(Log4jPattern other) {

        // group count
        int groupCount = getGroupCount();
        int otherGroupCount = other.getGroupCount();

        if (groupCount != otherGroupCount) {
            // reverse order
            return Integer.valueOf(otherGroupCount).compareTo(Integer.valueOf(groupCount));
        }

        // then pattern string
        String patternString = getPatternString();
        String otherPatternString = other.getPatternString();

        return patternString.compareTo(otherPatternString);
    }
}
