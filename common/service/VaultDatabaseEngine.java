package common.service;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.Common;
import common.model.Vault;

public class VaultDatabaseEngine {
    private Vault vault;

    public VaultDatabaseEngine(Vault vault) {
        this.vault = vault;
    }

    public String engineCheck() {
        System.out.println("Database Engine Check");
        System.out.println(vault.getVaultUrl());

        String engineList = Common.request(vault.getVaultUrl() + "/v1/sys/mounts", vault.getVaultToken());

        // ObjectMapper 객체 생성
        ObjectMapper objectMapper = new ObjectMapper();

        // JSON 문자열을 Map으로 변환
        Map<String, Object> map;
        try {
            map = objectMapper.readValue(
                    engineList,
                    new TypeReference<Map<String, Object>>() {
                    });
            // 중첩 값 확인
            String nestedValue = Common.getNestedValue(map, "data", "db-manager/", "type");
            if (nestedValue.equals("database")) {
                System.out.println("Database Engine 활성화 상태");
            }
            return nestedValue;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return "";
    }

    public void engineEnable() {
        System.out.println("Database Engine Enable");
        Map<String, String> data = new HashMap<>();
        data.put("type", "database");
        Common.postVaultRequest(vault.getVaultUrl() + "/v1/sys/mounts/db-manager", vault.getVaultToken(), data);
    }

    // Database Engine Config 생성
    public void configCreate(String dbType, String dbUrl, String dbUserNm, String dbUserPw) {
        System.out.println("Database Config Create");
        Map<String, String> data = new HashMap<>();
        data.put("plugin_name", "mysql-database-plugin");
        data.put("connection_url", "{{username}}:{{password}}@tcp("+ dbUrl + ")/");
        data.put("allowed_roles", "*");
        data.put("username", dbUserNm);
        data.put("password", dbUserPw);
        Common.postVaultRequest(vault.getVaultUrl() + "/v1/db-manager/config/" + dbType, vault.getVaultToken(), data);
    }

    // Database Engine Config List 확인
    public String[] configList() {
        System.out.println("Database Config List");
        String jsonConfigList = Common.getVaultRequest(vault.getVaultUrl() + "/v1/db-manager/config?list=true",
                vault.getVaultToken());
        
        if(jsonConfigList.isBlank()) {
            return new String[0];
        }
        // ObjectMapper 객체 생성
        ObjectMapper objectMapper = new ObjectMapper();

        // JSON 문자열을 Map으로 변환
        Map<String, Object> map;
        try {
            map = objectMapper.readValue(
                    jsonConfigList,
                    new TypeReference<Map<String, Object>>() {
                    });
            // 중첩 값 확인
            String nestedValue = Common.getNestedValue(map, "data", "keys");
            System.out.println("config list : " + nestedValue);

            // 대괄호 제거 및 공백 제거
            nestedValue = nestedValue.substring(1, nestedValue.length() - 1).replace(" ", "");

            // 콤마를 기준으로 분리
            String[] items = nestedValue.split(",");
            return items;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new String[0];
    }

    /* 
     * DB Dynamic Role 생성
     */
    public void roleCreate(String userName, String dbName, String creationStatements) {
        System.out.println("Database Role Create");
        creationStatements.replace("{{name}}", userName);
        Map<String, String> data = new HashMap<>();
        data.put("db_name", dbName);
        data.put("creation_statements", creationStatements);
        data.put("default_ttl", "20m");
        Common.postVaultRequest(vault.getVaultUrl() + "/v1/db-manager/roles/" + dbName + "-" + userName, vault.getVaultToken(), data);

    } 
}
