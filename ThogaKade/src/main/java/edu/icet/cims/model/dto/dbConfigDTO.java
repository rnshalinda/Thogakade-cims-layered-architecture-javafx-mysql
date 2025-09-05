package edu.icet.cims.model.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class DbConfigDTO {

    private String host;
    private String dbName;
    private String user;
    private String pswd;
    private String port;
    private String extraParam;
}
