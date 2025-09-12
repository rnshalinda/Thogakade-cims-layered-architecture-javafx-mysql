package edu.icet.cims.db;

import edu.icet.cims.model.dto.DbConfigDto;

public class DbConfig {

    private static DbConfigDto dbConfigData;            // Store the DbConfigDTO here for session use

    public static DbConfigDto getDbConfigData() {
        return dbConfigData;
    }

    public static void setDbConfigData(DbConfigDto dbConfigData) {
        DbConfig.dbConfigData = dbConfigData;
    }


}
