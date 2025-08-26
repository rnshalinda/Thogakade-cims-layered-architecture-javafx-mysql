package edu.icet.cims.model.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserCredentialsEntity {

    private String username;
    private String password;
}
