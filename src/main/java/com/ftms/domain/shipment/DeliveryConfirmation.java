package com.ftms.domain.shipment;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Delivery Confirmation Entity
 * Part of Shipment aggregate
 */
public class DeliveryConfirmation {
    
    private Integer confirmationId;
    
    @NotNull(message = "Delivery date and time is required")
    private Date deliveryDateTime;
    
    @NotBlank(message = "Recipient name is required")
    private String recipientName;
    
    @NotBlank(message = "Recipient signature is required")
    private String recipientSignature;
    
    private String deliveryIssues;
    
    private String photoDocumentation;

    // Default constructor
    public DeliveryConfirmation() {
    }

    public DeliveryConfirmation(Date deliveryDateTime, String recipientName, String recipientSignature) {
        this.deliveryDateTime = deliveryDateTime;
        this.recipientName = recipientName;
        this.recipientSignature = recipientSignature;
    }

    // Getters and Setters
    public Integer getConfirmationId() {
        return confirmationId;
    }

    public void setConfirmationId(Integer confirmationId) {
        this.confirmationId = confirmationId;
    }

    public Date getDeliveryDateTime() {
        return deliveryDateTime;
    }

    public void setDeliveryDateTime(Date deliveryDateTime) {
        this.deliveryDateTime = deliveryDateTime;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientSignature() {
        return recipientSignature;
    }

    public void setRecipientSignature(String recipientSignature) {
        this.recipientSignature = recipientSignature;
    }

    public String getDeliveryIssues() {
        return deliveryIssues;
    }

    public void setDeliveryIssues(String deliveryIssues) {
        this.deliveryIssues = deliveryIssues;
    }

    public String getPhotoDocumentation() {
        return photoDocumentation;
    }

    public void setPhotoDocumentation(String photoDocumentation) {
        this.photoDocumentation = photoDocumentation;
    }

    @Override
    public String toString() {
        return "DeliveryConfirmation{" +
                "deliveryDateTime=" + deliveryDateTime +
                ", recipient=" + recipientName +
                '}';
    }
}
