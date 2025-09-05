package edu.icet.cims.db;

import edu.icet.cims.model.dto.DbConfigDTO;

public class dbConfig {

    private static DbConfigDTO dbConfigData;    // Store the DbConfigDTO here for session use

    public static DbConfigDTO getDbConfigData() {
        return dbConfigData;
    }

    public static void setDbConfigData(DbConfigDTO dbConfigData) {
        dbConfig.dbConfigData = dbConfigData;
    }
}
