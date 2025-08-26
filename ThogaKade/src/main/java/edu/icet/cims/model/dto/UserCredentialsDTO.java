package edu.icet.cims.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserCredentialsDTO {

    private String uname;
    private String pswd;
}
