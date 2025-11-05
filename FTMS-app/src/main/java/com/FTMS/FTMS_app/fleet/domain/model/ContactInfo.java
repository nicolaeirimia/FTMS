package com.FTMS.FTMS_app.fleet.domain.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable // Îi spune JPA că această clasă va fi "încorporată" în altă entitate
@Getter
@NoArgsConstructor // Necesar pentru JPA
@AllArgsConstructor
public class ContactInfo {

    private String name; // Folosit pentru EmergencyContact
    private String phone;
    private String email;
    private String address;

    // Putem adăuga un constructor simplificat pentru contactul primar
    public ContactInfo(String phone, String email, String address) {
        this.phone = phone;
        this.email = email;
        this.address = address;
    }
}