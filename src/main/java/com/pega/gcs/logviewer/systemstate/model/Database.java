
package com.pega.gcs.logviewer.systemstate.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pega.gcs.fringecommon.guiutilities.datatable.IndexEntry;

public class Database extends IndexEntry implements Comparable<Database> {

    @JsonProperty("DatabaseName")
    private String databaseName;

    @JsonProperty("DatabaseVersion")
    private String databaseVersion;

    @JsonProperty("DatabaseVendorName")
    private String databaseVendorName;

    @JsonProperty("DriverName")
    private String driverName;

    @JsonProperty("DriverVersion")
    private String driverVersion;

    @JsonProperty("MaxColumnsInTable")
    private Integer maxColumnsInTable;

    @JsonProperty("MaxConnections")
    private Integer maxConnections;

    @JsonProperty("MaxRowSize")
    private Integer maxRowSize;

    public Database() {
        super();
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getDatabaseVersion() {
        return databaseVersion;
    }

    public String getDatabaseVendorName() {
        return databaseVendorName;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getDriverVersion() {
        return driverVersion;
    }

    public Integer getMaxColumnsInTable() {
        return maxColumnsInTable;
    }

    public Integer getMaxConnections() {
        return maxConnections;
    }

    public Integer getMaxRowSize() {
        return maxRowSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(databaseName, databaseVendorName, databaseVersion, driverName, driverVersion,
                maxColumnsInTable, maxConnections, maxRowSize);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Database)) {
            return false;
        }
        Database other = (Database) obj;
        return Objects.equals(databaseName, other.databaseName)
                && Objects.equals(databaseVendorName, other.databaseVendorName)
                && Objects.equals(databaseVersion, other.databaseVersion)
                && Objects.equals(driverName, other.driverName) && Objects.equals(driverVersion, other.driverVersion)
                && Objects.equals(maxColumnsInTable, other.maxColumnsInTable)
                && Objects.equals(maxConnections, other.maxConnections) && Objects.equals(maxRowSize, other.maxRowSize);
    }

    @Override
    public String toString() {
        return "Database [databaseName=" + databaseName + ", databaseVersion=" + databaseVersion
                + ", databaseVendorName=" + databaseVendorName + ", driverName=" + driverName + ", driverVersion="
                + driverVersion + ", maxColumnsInTable=" + maxColumnsInTable + ", maxConnections=" + maxConnections
                + ", maxRowSize=" + maxRowSize + "]";
    }

    @Override
    public int compareTo(Database other) {

        int compare = this.databaseName.compareTo(other.databaseName);

        if (compare == 0) {
            compare = this.databaseVersion.compareTo(other.databaseVersion);
        }

        if (compare == 0) {
            compare = this.databaseVendorName.compareTo(other.databaseVendorName);
        }

        if (compare == 0) {
            compare = this.driverName.compareTo(other.driverName);
        }

        if (compare == 0) {
            compare = this.driverVersion.compareTo(other.driverVersion);
        }

        if ((compare == 0) && (this.maxColumnsInTable != null)) {
            compare = this.maxColumnsInTable.compareTo(other.maxColumnsInTable);
        }

        if ((compare == 0) && (this.maxConnections != null)) {
            compare = this.maxConnections.compareTo(other.maxConnections);
        }

        if ((compare == 0) && (this.maxRowSize != null)) {
            compare = this.maxRowSize.compareTo(other.maxRowSize);
        }

        return compare;
    }

}
