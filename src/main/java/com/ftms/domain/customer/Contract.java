package com.ftms.domain.customer;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.Date;

/**
 * Contract Entity - Part of Customer aggregate
 */
public class Contract {
    
    private Integer contractId;
    
    @NotNull
    private Date startDate;
    
    @NotNull
    private Date endDate;
    
    private String serviceLevel; // standard, express, same-day
    
    @PositiveOrZero
    private Integer minimumShipmentsPerMonth;
    
    private String pricingStructure; // per kg, per km, flat rate
    
    @PositiveOrZero
    private Double discountRate;
    
    private String specialTerms;

    public Contract() {
    }

    public Contract(Date startDate, Date endDate, String serviceLevel) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.serviceLevel = serviceLevel;
    }

    public boolean isActive() {
        Date now = new Date();
        return now.after(startDate) && now.before(endDate);
    }

    // Getters and Setters
    public Integer getContractId() {
        return contractId;
    }

    public void setContractId(Integer contractId) {
        this.contractId = contractId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getServiceLevel() {
        return serviceLevel;
    }

    public void setServiceLevel(String serviceLevel) {
        this.serviceLevel = serviceLevel;
    }

    public Integer getMinimumShipmentsPerMonth() {
        return minimumShipmentsPerMonth;
    }

    public void setMinimumShipmentsPerMonth(Integer minimumShipmentsPerMonth) {
        this.minimumShipmentsPerMonth = minimumShipmentsPerMonth;
    }

    public String getPricingStructure() {
        return pricingStructure;
    }

    public void setPricingStructure(String pricingStructure) {
        this.pricingStructure = pricingStructure;
    }

    public Double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(Double discountRate) {
        this.discountRate = discountRate;
    }

    public String getSpecialTerms() {
        return specialTerms;
    }

    public void setSpecialTerms(String specialTerms) {
        this.specialTerms = specialTerms;
    }
}
