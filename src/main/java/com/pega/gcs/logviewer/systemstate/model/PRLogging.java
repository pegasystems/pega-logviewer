
package com.pega.gcs.logviewer.systemstate.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PRLogging implements Comparable<PRLogging> {

    @JsonProperty("FileName")
    private String fileName;

    @JsonProperty("FilePath")
    private String filePath;

    @JsonProperty("Content")
    private String content;

    @JsonProperty("PZ_ERROR")
    private String pzError;

    public PRLogging() {
        super();
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getContent() {
        return content;
    }

    public String getPzError() {
        return pzError;
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, fileName, filePath);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PRLogging)) {
            return false;
        }
        PRLogging other = (PRLogging) obj;
        return Objects.equals(content, other.content) && Objects.equals(fileName, other.fileName)
                && Objects.equals(filePath, other.filePath);
    }

    @Override
    public String toString() {
        return "PRLogging [fileName=" + fileName + ", filePath=" + filePath + ", content=" + content + "]";
    }

    @Override
    public int compareTo(PRLogging other) {

        int compare = this.fileName.compareTo(other.fileName);

        if (compare == 0) {
            compare = this.filePath.compareTo(other.filePath);
        }

        if (compare == 0) {
            compare = this.content.compareTo(other.content);
        }

        return compare;
    }

    public void postProcess() {

    }
}
