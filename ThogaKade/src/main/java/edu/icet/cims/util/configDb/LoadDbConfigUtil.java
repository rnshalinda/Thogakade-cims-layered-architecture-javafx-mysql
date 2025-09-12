package edu.icet.cims.util.configDb;

import edu.icet.cims.db.DbConfig;
import edu.icet.cims.model.dto.DbConfigDto;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;


public class LoadDbConfigUtil {

    private static LoadDbConfigUtil load;


    public static boolean loadDbConfig(){

        Yaml yaml = new Yaml();

        try (InputStream inputYml = new FileInputStream("src/main/resources/db-config.yml")) {
            // Parse YAML as a Map
            Map<String, Object> data = yaml.load(inputYml);

            // Access values
            Map<String, String> dbData = (Map<String, String>) data.get("database");
            //  String host = dbData.get("host"); // moved to below

            // set the db config data from yml as DbConfigDTO
            DbConfig.setDbConfigData(new DbConfigDto(
                    dbData.get("host"),
                    dbData.get("dbName"),
                    dbData.get("user"),
                    dbData.get("pswd"),
                    dbData.get("port"),
                    dbData.get("parameter")
            ));

            return true;        // return success

        } catch (IOException e) {
            e.printStackTrace();
            return false;       // return fail
        }
    }
}

