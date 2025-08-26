package edu.icet.cims.model.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomerDTO {

    private String id;
    private String title;
    private String name;
    private LocalDate dob;
    private String address;
    private String city;
    private String province;
    private String postalCode;
}
