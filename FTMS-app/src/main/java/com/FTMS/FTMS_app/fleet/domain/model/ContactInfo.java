package com.FTMS.FTMS_app.fleet.domain.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email; // Necesită spring-boot-starter-validation
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ContactInfo {

    private String name;


    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number")
    private String phone;


    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Address is required")
    private String address;

    public ContactInfo(String phone, String email, String address) {
        this.phone = phone;
        this.email = email;
        this.address = address;
    }
}