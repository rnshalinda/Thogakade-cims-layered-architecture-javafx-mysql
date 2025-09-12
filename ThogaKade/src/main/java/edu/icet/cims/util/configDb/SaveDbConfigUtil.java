package edu.icet.cims.util.configDb;

import edu.icet.cims.model.dto.DbConfigDto;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SaveDbConfigUtil {

// how yml output file look like
//
//    database:
//      host: localhost
//      dbName: thogakade_cims
//      user: root
//      pswd: '1234'
//      port: '3306'
//      parameter: ''


    public static boolean saveToYlm(DbConfigDto dbConfig){

        Map<String, Object> database = new HashMap<>();
        database.put("host", dbConfig.getHost());
        database.put("dbName", dbConfig.getDbName());
        database.put("user", dbConfig.getUser());
        database.put("pswd", dbConfig.getPswd());
        database.put("port", dbConfig.getPort());
        database.put("parameter", dbConfig.getExtraParam());

        Map<String, Object> root = new HashMap<>();
        root.put("database", database);

        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Yaml yaml = new Yaml(options);

        try (FileWriter writer = new FileWriter("src/main/resources/db-config.yml")) {
            yaml.dump(root, writer);    // write to yml
            return true;                // return success
        } catch (IOException e) {
            e.printStackTrace();
            return false;               // return fail
        }
    }
}
