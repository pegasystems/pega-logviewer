
package com.pega.gcs.logviewer;

import javax.swing.text.Highlighter.Highlight;

class TextAreaSearchResult {

    private int beginPos;

    private int endPos;

    private Highlight highlight;

    protected TextAreaSearchResult(int beginPos, int endPos, Highlight highlight) {
        super();
        this.beginPos = beginPos;
        this.endPos = endPos;
        this.highlight = highlight;
    }

    protected int getBeginPos() {
        return beginPos;
    }

    protected int getEndPos() {
        return endPos;
    }

    protected Highlight getHighlight() {
        return highlight;
    }

    protected void setHighlight(Highlight highlight) {
        this.highlight = highlight;
    }

    @Override
    public String toString() {
        return "beginPos=" + beginPos + ", endPos=" + endPos;
    }

}