package common.service;

import java.util.HashMap;
import java.util.Map;

import common.Common;
import common.VaultException;
import common.model.Vault;
import common.model.VaultDatabasePlugin;

public class VaultDatabaseEngine {
    private Vault vault;

    public VaultDatabaseEngine(Vault vault) {
        this.vault = vault;
    }

    public String engineCheck() {
        System.out.println("Database Engine Check");
        System.out.println(vault.getVaultUrl());

        String engineList = Common.request(vault.getVaultUrl() + "/v1/sys/mounts", vault.getVaultToken());

        return Common.getNestedJsonToStr(engineList, "data", "db-manager/", "type");
    }

    public void engineEnable() throws VaultException {
        System.out.println("Database Engine Enable");
        Map<String, String> data = new HashMap<>();
        data.put("type", "database");
        Common.postVaultRequest(vault.getVaultUrl() + "/v1/sys/mounts/db-manager", vault.getVaultToken(), data);
    }

    // Database Engine Config 생성
    public void configCreate(String connName, String dbType, String dbUrl, String dbUserNm, String dbUserPw) throws VaultException {
        System.out.println("Database Config Create");
        Map<String, String> data = new HashMap<>();
        data.put("plugin_name", VaultDatabasePlugin.valueOf(dbType).getPlugin());
        data.put("connection_url", "{{username}}:{{password}}@tcp("+ dbUrl + ")/");
        data.put("allowed_roles", "*");
        data.put("username", dbUserNm);
        data.put("password", dbUserPw);
        Common.postVaultRequest(vault.getVaultUrl() + "/v1/db-manager/config/" + connName, vault.getVaultToken(), data);
    }

    // Database Engine Config List 확인
    public String[] configList() throws VaultException {
        System.out.println("Database Config List");
        String jsonConfigList = Common.getVaultRequest(vault.getVaultUrl() + "/v1/db-manager/config?list=true",
                vault.getVaultToken());
        
        if(jsonConfigList.isBlank()) {
            return new String[0];
        }

        return Common.getNestedJsonToStrArr(jsonConfigList, "data", "keys");
    }

    /* 
     * DB Dynamic Role 생성
     */
    public void createRole(String userName, String dbName, String creationStatements) throws VaultException {
        System.out.println("Database Role Create");
        System.out.println("creationStatements : " + creationStatements);
        System.out.println("userName : " + userName);

        creationStatements.replace("{{name}}", userName);
        Map<String, String> data = new HashMap<>();
        data.put("db_name", dbName);
        data.put("creation_statements", creationStatements.replace("{{name}}", userName));
        data.put("revocation_statements", "DROP USER '{{name}}'@'%';".replace("{{name}}", userName));
        data.put("default_ttl", "2m");
        Common.postVaultRequest(vault.getVaultUrl() + "/v1/db-manager/roles/" + dbName + "-" + userName, vault.getVaultToken(), data);
    }

    /* 
     * DB Dynamic Default Role 생성
     */
    public void createRole(String dbName, String creationStatements) throws VaultException {
        System.out.println("Database Role Create");
        Map<String, String> data = new HashMap<>();
        data.put("db_name", dbName);
        data.put("creation_statements", creationStatements);
        data.put("revocation_statements", "DROP USER '{{name}}'@'%';");
        data.put("default_ttl", "2m");
        Common.postVaultRequest(vault.getVaultUrl() + "/v1/db-manager/roles/" + dbName, vault.getVaultToken(), data);
    }

    public String readRole(String dbName) throws VaultException {
        System.out.println("Database Role Read");
        String result = Common.getVaultRequest(vault.getVaultUrl() + "/v1/db-manager/roles/" + dbName, vault.getVaultToken());
        return result;
    }

    /* 
     * DB Dynamic Creds
     */
    public String creds(String dbName) throws VaultException {
        System.out.println("Database Dynamic Creds");

        return Common.getVaultRequest(vault.getVaultUrl()+"/v1/db-manager/creds/"+dbName+"-"+vault.getVaultUserNm(), vault.getVaultToken());
    }

    /* 
     * DB Lease Revoke
     */
    public String revokeCreds(String dbName) throws VaultException {
        System.out.println("Database Creds lease revoke");
        Common.postVaultRequest(vault.getVaultUrl()+"/v1/sys/leases/revoke-prefix/db-manager/creds/"+dbName+"-"+vault.getVaultUserNm(), vault.getVaultToken(), null);
        return "";
    }
}
