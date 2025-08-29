package edu.icet.cims.model.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class dbConfigDTO {

    private String dbName;
    private String url;
    private String user;
    private String pswd;
}
