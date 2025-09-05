package edu.icet.cims.util.configDb;

import edu.icet.cims.db.dbConfig;
import edu.icet.cims.model.dto.DbConfigDTO;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class LoadDbConfigUtil {

    private static LoadDbConfigUtil load;


    public static boolean loadDbConfig(){

        Yaml yaml = new Yaml();

        try (InputStream inputYml = new FileInputStream("dbConfig.yml")) {
            // Parse YAML as a Map
            Map<String, Object> data = yaml.load(inputYml);

            // Access values
            Map<String, String> dbData = (Map<String, String>) data.get("database");
            String host = dbData.get("host");
            String dbName = dbData.get("dbName");
            String user = dbData.get("user");
            String pswd = dbData.get("pswd");
            String port = dbData.get("port");
            String extraParam = dbData.get("parameter");

            // set the read db config data as DbConfigDTO
            dbConfig.setDbConfigData(new DbConfigDTO(host, dbName, user, pswd, port, extraParam));

            return true; // return success

        } catch (IOException e) {
            e.printStackTrace();
            return false; // return fail
        }
    }
}

