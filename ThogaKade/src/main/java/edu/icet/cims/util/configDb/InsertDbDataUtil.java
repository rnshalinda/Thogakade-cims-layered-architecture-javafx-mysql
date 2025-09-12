package edu.icet.cims.util.configDb;

import edu.icet.cims.db.DbConfig;
import edu.icet.cims.db.DbConnection;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

public class InsertDbDataUtil {

    public static boolean runSeedQueries(ArrayList<String> addDataTblList) {
        Yaml yaml = new Yaml();
        InputStream input = InsertDbDataUtil.class.getResourceAsStream("/db-data.yml");

        if (input == null) {
            throw new RuntimeException("yml not found");
        }

        Map<String, String> queries = yaml.load(input);

        Connection con = null;
        try {
            con = DbConnection.getInstance().getConnection();
            DbConnection.useDb(DbConfig.getDbConfigData().getDbName());

            // only go through tables specified in addDataTblList
            for (String table : addDataTblList) {
                String sql = queries.get(table);
                if (sql != null) {
                    try (Statement stm = con.createStatement()) {
                        System.out.println("Data inserted into table: " + table);
                        stm.executeUpdate(sql);
                    }
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}


