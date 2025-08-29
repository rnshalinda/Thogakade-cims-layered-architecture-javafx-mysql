package edu.icet.cims.util.configDb;

import edu.icet.cims.model.dto.dbConfigDTO;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SaveDbConfigYml {

    public static boolean saveToYlm(dbConfigDTO dbConfig){

        Map<String, Object> database = new HashMap<>();
        database.put("name", dbConfig.getDbName());
        database.put("url", dbConfig.getUrl());
        database.put("user", dbConfig.getUser());
        database.put("password", dbConfig.getPswd());

        Map<String, Object> root = new HashMap<>();
        root.put("database", database);

        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Yaml yaml = new Yaml(options);

        try (FileWriter writer = new FileWriter("dbConfig.yml")) {
            yaml.dump(root, writer);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
