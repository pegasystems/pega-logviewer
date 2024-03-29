//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.2 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.09.17 at 02:15:08 PM BST 
//


package com.pega.gcs.logviewer.model.alert;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AlertMessage" maxOccurs="unbounded"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *                   &lt;element name="MessageID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;element name="Category" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;element name="Subcategory" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;element name="Title" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;element name="Severity" type="{}Severity"/&gt;
 *                   &lt;element name="PegaUrl" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;element name="DssEnableConfig" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                   &lt;element name="DssEnabled" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                   &lt;element name="DssThresholdConfig" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                   &lt;element name="DssValueType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                   &lt;element name="DssValueUnit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                   &lt;element name="DssDefaultValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                   &lt;element name="ChartColor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "alertMessage"
})
@XmlRootElement(name = "AlertMessageList")
public class AlertMessageList {

    @XmlElement(name = "AlertMessage", required = true)
    protected List<AlertMessageList.AlertMessage> alertMessage;

    /**
     * Gets the value of the alertMessage property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the alertMessage property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAlertMessage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AlertMessageList.AlertMessage }
     * 
     * 
     */
    public List<AlertMessageList.AlertMessage> getAlertMessage() {
        if (alertMessage == null) {
            alertMessage = new ArrayList<AlertMessageList.AlertMessage>();
        }
        return this.alertMessage;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
     *         &lt;element name="MessageID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *         &lt;element name="Category" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *         &lt;element name="Subcategory" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *         &lt;element name="Title" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *         &lt;element name="Severity" type="{}Severity"/&gt;
     *         &lt;element name="PegaUrl" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *         &lt;element name="DssEnableConfig" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *         &lt;element name="DssEnabled" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *         &lt;element name="DssThresholdConfig" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *         &lt;element name="DssValueType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *         &lt;element name="DssValueUnit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *         &lt;element name="DssDefaultValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *         &lt;element name="ChartColor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "id",
        "messageID",
        "category",
        "subcategory",
        "title",
        "severity",
        "pegaUrl",
        "description",
        "dssEnableConfig",
        "dssEnabled",
        "dssThresholdConfig",
        "dssValueType",
        "dssValueUnit",
        "dssDefaultValue",
        "chartColor"
    })
    public static class AlertMessage {

        @XmlElement(name = "Id")
        protected int id;
        @XmlElement(name = "MessageID", required = true)
        protected String messageID;
        @XmlElement(name = "Category", required = true)
        protected String category;
        @XmlElement(name = "Subcategory", required = true)
        protected String subcategory;
        @XmlElement(name = "Title", required = true)
        protected String title;
        @XmlElement(name = "Severity", required = true)
        @XmlSchemaType(name = "string")
        protected Severity severity;
        @XmlElement(name = "PegaUrl", required = true)
        protected String pegaUrl;
        @XmlElement(name = "Description", required = true)
        protected String description;
        @XmlElement(name = "DssEnableConfig")
        protected String dssEnableConfig;
        @XmlElement(name = "DssEnabled")
        protected String dssEnabled;
        @XmlElement(name = "DssThresholdConfig")
        protected String dssThresholdConfig;
        @XmlElement(name = "DssValueType")
        protected String dssValueType;
        @XmlElement(name = "DssValueUnit")
        protected String dssValueUnit;
        @XmlElement(name = "DssDefaultValue")
        protected String dssDefaultValue;
        @XmlElement(name = "ChartColor")
        protected String chartColor;

        /**
         * Gets the value of the id property.
         * 
         */
        public int getId() {
            return id;
        }

        /**
         * Sets the value of the id property.
         * 
         */
        public void setId(int value) {
            this.id = value;
        }

        /**
         * Gets the value of the messageID property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMessageID() {
            return messageID;
        }

        /**
         * Sets the value of the messageID property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMessageID(String value) {
            this.messageID = value;
        }

        /**
         * Gets the value of the category property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCategory() {
            return category;
        }

        /**
         * Sets the value of the category property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCategory(String value) {
            this.category = value;
        }

        /**
         * Gets the value of the subcategory property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSubcategory() {
            return subcategory;
        }

        /**
         * Sets the value of the subcategory property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSubcategory(String value) {
            this.subcategory = value;
        }

        /**
         * Gets the value of the title property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTitle() {
            return title;
        }

        /**
         * Sets the value of the title property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTitle(String value) {
            this.title = value;
        }

        /**
         * Gets the value of the severity property.
         * 
         * @return
         *     possible object is
         *     {@link Severity }
         *     
         */
        public Severity getSeverity() {
            return severity;
        }

        /**
         * Sets the value of the severity property.
         * 
         * @param value
         *     allowed object is
         *     {@link Severity }
         *     
         */
        public void setSeverity(Severity value) {
            this.severity = value;
        }

        /**
         * Gets the value of the pegaUrl property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPegaUrl() {
            return pegaUrl;
        }

        /**
         * Sets the value of the pegaUrl property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPegaUrl(String value) {
            this.pegaUrl = value;
        }

        /**
         * Gets the value of the description property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDescription() {
            return description;
        }

        /**
         * Sets the value of the description property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDescription(String value) {
            this.description = value;
        }

        /**
         * Gets the value of the dssEnableConfig property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDssEnableConfig() {
            return dssEnableConfig;
        }

        /**
         * Sets the value of the dssEnableConfig property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDssEnableConfig(String value) {
            this.dssEnableConfig = value;
        }

        /**
         * Gets the value of the dssEnabled property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDssEnabled() {
            return dssEnabled;
        }

        /**
         * Sets the value of the dssEnabled property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDssEnabled(String value) {
            this.dssEnabled = value;
        }

        /**
         * Gets the value of the dssThresholdConfig property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDssThresholdConfig() {
            return dssThresholdConfig;
        }

        /**
         * Sets the value of the dssThresholdConfig property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDssThresholdConfig(String value) {
            this.dssThresholdConfig = value;
        }

        /**
         * Gets the value of the dssValueType property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDssValueType() {
            return dssValueType;
        }

        /**
         * Sets the value of the dssValueType property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDssValueType(String value) {
            this.dssValueType = value;
        }

        /**
         * Gets the value of the dssValueUnit property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDssValueUnit() {
            return dssValueUnit;
        }

        /**
         * Sets the value of the dssValueUnit property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDssValueUnit(String value) {
            this.dssValueUnit = value;
        }

        /**
         * Gets the value of the dssDefaultValue property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDssDefaultValue() {
            return dssDefaultValue;
        }

        /**
         * Sets the value of the dssDefaultValue property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDssDefaultValue(String value) {
            this.dssDefaultValue = value;
        }

        /**
         * Gets the value of the chartColor property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getChartColor() {
            return chartColor;
        }

        /**
         * Sets the value of the chartColor property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setChartColor(String value) {
            this.chartColor = value;
        }

    }

}
