package edu.icet.cims.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserCredentialsDto {

    private String uname;
    private String pswd;
}
