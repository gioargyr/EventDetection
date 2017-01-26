/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modules.dataAggregator;

public class QueryBuilder {

    private String footPrint;
    private String productType;
    private String polarisationMode;
    private String platformName;
    private String orbit;
    private String fromBeginPosition;
    private String toBeginPosition;

    public QueryBuilder() {
    }

    public QueryBuilder setFootPrint(String footPrint) {
        this.footPrint = footPrint;
        return this;
    }

    public QueryBuilder setProductType(String productType) {
        this.productType = productType;
        return this;
    }

    public QueryBuilder setPolarisationMode(String polarisationMode) {
        this.polarisationMode = polarisationMode;
        return this;
    }

    public QueryBuilder setPlatformName(String platformName) {
        this.platformName = platformName;
        return this;
    }

    public QueryBuilder setOrbit(String orbit) {
        this.orbit = orbit;
        return this;
    }

    public QueryBuilder setFromBeginPosition(String fromBeginPosition) {
        this.fromBeginPosition = fromBeginPosition;
        return this;
    }
    public QueryBuilder setToBeginPosition(String toBeginPosition) {
        this.toBeginPosition = toBeginPosition;
        return this;
    }
    public Query createQuery() {
        return new Query(footPrint, productType, polarisationMode, platformName, orbit, fromBeginPosition,toBeginPosition);
    }

}
