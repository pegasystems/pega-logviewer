
package com.pega.gcs.logviewer.catalog.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProductInfo implements Comparable<ProductInfo> {

    public static final String DEFAULT_PRODUCT_NAME = "PegaRULES Process Commander";

    public static final String PEGA_PRODUCT_NAME = "Pega";

    private static Map<String, String> pegaVersionMap;

    private String productName;

    private String productVersion;

    private boolean searchFound;

    static {
        pegaVersionMap = new HashMap<>();

        pegaVersionMap.put("07-10_ML8", "7.1.8");
        pegaVersionMap.put("07-10_ML9", "7.1.9");
        pegaVersionMap.put("07-10_ML10", "7.1.10");
        pegaVersionMap.put("07-20_ML0", "7.2.0");
        pegaVersionMap.put("07-20_ML1", "7.2.1");
        pegaVersionMap.put("07-20_ML2", "7.2.2");
    }

    public ProductInfo(String productName, String productVersion) {
        super();
        this.productName = productName;
        this.productVersion = productVersion;
        this.searchFound = false;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductVersion() {
        return productVersion;
    }

    public boolean isSearchFound() {
        return searchFound;
    }

    public void setSearchFound(boolean searchFound) {
        this.searchFound = searchFound;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productName, productVersion);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof ProductInfo)) {
            return false;
        }

        ProductInfo other = (ProductInfo) obj;

        return Objects.equals(productName, other.productName) && Objects.equals(productVersion, other.productVersion);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ProductInfo [");
        builder.append(getProductInfoStr());
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int compareTo(ProductInfo other) {

        int compareValue;

        String thisProductName = getProductName();
        String otherProductName = other.getProductName();

        compareValue = thisProductName.compareTo(otherProductName);

        if (compareValue == 0) {

            String thisProductVersion = getProductVersion();
            String otherProductVersion = other.getProductVersion();

            compareValue = thisProductVersion.compareTo(otherProductVersion);
        }

        return compareValue;
    }

    public String getProductInfoStr() {

        StringBuilder sb = new StringBuilder();

        if (productName.equalsIgnoreCase(DEFAULT_PRODUCT_NAME)) {
            sb.append(PEGA_PRODUCT_NAME);
        } else {
            sb.append(productName);
        }
        if ((productVersion != null) && (!"".equals(productVersion))) {
            sb.append(" ");

            String pegaVersion = pegaVersionMap.get(productVersion);
            sb.append(pegaVersion != null ? pegaVersion : productVersion);
        }

        return sb.toString();
    }

    /**
     * Evaluate catalogue product label from scan result product name.
     * 
     * @param hfixProductVersionLabel -label
     * @return ProductInfo object
     * 
     *         <br> also set Product name as 'PegaRULES Process Commander' if product name is 'Pega' and product version as 07-20_ML2 if
     *         product version is 7.2.2.
     */
    public static ProductInfo getProductInfoFromLabel(String hfixProductVersionLabel) {
        ProductInfo productInfo = null;

        int lastIndex = hfixProductVersionLabel.lastIndexOf(" ");

        String productName = hfixProductVersionLabel.substring(0, lastIndex).trim();
        String productVersion = hfixProductVersionLabel.substring(lastIndex).trim();

        if ((productName != null) && (!"".equals(productName)) && (productVersion != null)
                && (!"".equals(productVersion))) {

            if (productName.equalsIgnoreCase(PEGA_PRODUCT_NAME)) {
                productName = DEFAULT_PRODUCT_NAME;

                for (String catalogVersion : pegaVersionMap.keySet()) {

                    String pegaVersion = pegaVersionMap.get(catalogVersion);

                    if (pegaVersion.equals(productVersion)) {
                        productVersion = catalogVersion;
                        break;
                    }
                }
            }

            productInfo = new ProductInfo(productName, productVersion);
        }

        return productInfo;
    }
}
