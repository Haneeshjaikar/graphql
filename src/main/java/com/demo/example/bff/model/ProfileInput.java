package com.demo.example.bff.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.demo.example.bff.model.enums.CommunicationChannel;
import com.demo.example.bff.model.enums.Gender;
import com.demo.example.bff.model.enums.MaritalStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(value = PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class ProfileInput {
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private Address address;
    private Gender gender;
    private MaritalStatus maritalStatus;
    private CommunicationChannel preferredCommunicationChannel;
}
