package com.ftms.domain.common;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Value Object for pickup/delivery location with contact information
 */
public class Location {
    
    @NotNull(message = "Address is required")
    private Address address;
    
    private String contactPerson;
    
    private String contactPhone;

    // Default constructor
    public Location() {
    }

    public Location(Address address, String contactPerson, String contactPhone) {
        this.address = address;
        this.contactPerson = contactPerson;
        this.contactPhone = contactPhone;
    }

    // Getters
    public Address getAddress() {
        return address;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    // Setters
    public void setAddress(Address address) {
        this.address = address;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(address, location.address) &&
                Objects.equals(contactPerson, location.contactPerson) &&
                Objects.equals(contactPhone, location.contactPhone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, contactPerson, contactPhone);
    }

    @Override
    public String toString() {
        return "Location{" +
                "address=" + address +
                ", contact=" + contactPerson +
                ", phone=" + contactPhone +
                '}';
    }
}
