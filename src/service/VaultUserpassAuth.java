package src.service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import src.common.Common;
import src.common.VaultException;
import src.model.Vault;

public class VaultUserpassAuth {
    private Vault vault;

    public VaultUserpassAuth(Vault vault) {
        this.vault = vault;
    }

    /* 
     * policy check
     * 사용자 타입에 다른 Vault 권한(Auth List, DB List) 확인 메소드
     * responseCode = 403 : 권한 부족
     * responseCode = 404 : 존재하지 않는 패스
     * responseCode = 200 : 존재하는 패스
     */
    public boolean policyCheck() {
        HttpURLConnection connection = null;
        int responseCode = 000;
        try {
            System.out.println("Auth(db-userpass) Policy Check");

            URL urlAuth = new URL(vault.getVaultUrl() + "/v1/auth/db-userpass/users?list=true");
            connection = (HttpURLConnection) urlAuth.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Vault-Token", vault.getVaultToken());
            
            responseCode = connection.getResponseCode();
            
            System.out.println("Response Code: " + responseCode);

            if (responseCode == 403 || responseCode == 000) {
                return false;
            }

            System.out.println("Secrets(db-manager) Policy Check");

            URL urlSecrets = new URL(vault.getVaultUrl() + "/v1/db-manager/config?list=true");
            connection = (HttpURLConnection) urlSecrets.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Vault-Token", vault.getVaultToken());
            
            responseCode = connection.getResponseCode();

            if (responseCode == 403 || responseCode == 000) {
                return false;
            }
            
            System.out.println("Response Code: " + responseCode);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect(); // 연결 종료
            }
        }
        if (responseCode == 404 || responseCode == 200) {
            return true;
        } else {
            return false;
        }
    }

    /* db-userpass Auth 확인 */
    public String authCheck() {
        System.out.println("Userpass Auth Check");

        String engineList = Common.request(vault.getVaultUrl() + "/v1/sys/auth", vault.getVaultToken());

        String nestedValue = Common.getNestedJsonToStr(engineList, "data", "db-userpass/", "accessor");
        if (nestedValue == null) {
            return null;
        } else {
            System.out.println("Userpass Auth 활성화 상태");
            vault.setAccessor(nestedValue);
        }
        return nestedValue;
    }

    /* Auth 활성화 */
    public void authEnable() throws VaultException {
        System.out.println("Userpass Auth Enable");
        Map<String, String> data = new HashMap<>();
        data.put("type", "userpass");
        Common.postVaultRequest(vault.getVaultUrl() + "/v1/sys/auth/db-userpass", vault.getVaultToken(), data);
    }

    // Userpass Auth User List
    public String[] userList() throws VaultException {
        System.out.println("Userpass Auth User List");

        String jsonUserList = Common.getVaultRequest(vault.getVaultUrl() + "/v1/auth/db-userpass/users?list=true",
                vault.getVaultToken());

        if (jsonUserList.isBlank()) {
            return new String[0];
        }
        String[] items = Common.getNestedJsonToStrArr(jsonUserList, "data", "keys");

        // 유저 목록을 vault 객체에 저장
        List<String> userList = new ArrayList<String>();
        for (String user : items) {
            userList.add(user);
        }
        vault.setUserList(userList);

        return items;
    }

    /* 사용자 생성 */
    public void createUser(String username, String password) throws VaultException {
        System.out.println("User Create");
        // TODO : token_policies에 policy 다수 설정으로 변경 필요, 현재는 1개의 policy로 모든 db-config 접근 허용, 추후 지정한 db-config에만 접근 허용
        Map<String, String> data = new HashMap<>();
        data.put("password", password);
        data.put("token_policies", "db-user");

        String result = Common.postVaultRequest(vault.getVaultUrl() + "/v1/auth/db-userpass/users/"+username, vault.getVaultToken(), data);
        System.out.println(result);
    }

    /* 사용자 패스워드 변경 */
    public void updateUser(String username, String password) throws VaultException {
        System.out.println("User Update");
        Map<String, String> data = new HashMap<>();
        data.put("password", password);

        String result = Common.postVaultRequest(vault.getVaultUrl() + "/v1/auth/db-userpass/users/"+username, vault.getVaultToken(), data);
        System.out.println(result);
    }

    /* 사용자 삭제 */
    public void deleteUser(String username) throws VaultException {
        System.out.println("User Delete");

        String result = Common.deleteVaultRequest(vault.getVaultUrl() + "/v1/auth/db-userpass/users/"+username, vault.getVaultToken());
        System.out.println(result);
    }

    public void updateUserPolicy(String userName, String policyName) throws VaultException {
        System.out.println("User Update Policy");
        Map<String, String> data = new HashMap<>();
        data.put("token_policies", policyName);
        Common.postVaultRequest(vault.getVaultUrl() + "/v1/auth/db-userpass/users/"+userName, vault.getVaultToken(), data);
    }

    public String getUserPolicy(String userName) throws VaultException {
        System.out.println("Get User Policy");
        String result = Common.getVaultRequest(vault.getVaultUrl() + "/v1/auth/db-userpass/users/"+userName, vault.getVaultToken());
        System.out.println(result);
        String policies = Common.getNestedJsonToStr(result, "data", "token_policies");
        return policies.substring(1, policies.length() - 1);
    }

    /* 
     * 사용자 권한 생성
     * TODO : Userpass 권한 세부 설정 필요 (DB 별로 생성, Userpass - username, password 값만 허용 등)
     */
    public void createPolicy() throws VaultException {
        System.out.println("User Policy Create");
        String policy = "path \"db-manager/creds/{{identity.entity.aliases."+vault.getAccessor()+".name}}\" {\n    capabilities = [\"read\"]\n}\npath \"auth/db-userpass/users/{{identity.entity.aliases."+vault.getAccessor()+".name}}\" {\n    capabilities = [\"create\", \"update\"]\n}\n";
        Map<String, String> data = new HashMap<>();
        data.put("policy", policy);
        String result = Common.postVaultRequest(vault.getVaultUrl() + "/v1/sys/policies/acl/db-user", vault.getVaultToken(), data);
        System.out.println(result);
    }

    /* 
     * DB Config 별 Creds Dynamic 권한 추가
     * dbConfigName : DB Engine Config 명
     */
    public void createPolicy(String dbConfigName) throws VaultException {
        System.out.println("User Policy Create");
        String policy = "path \"db-manager/creds/"+dbConfigName+"-{{identity.entity.aliases."+vault.getAccessor()+".name}}\" {\n    capabilities = [\"read\"]\n}\npath \"sys/leases/revoke-prefix/db-manager/creds/" + dbConfigName + "-{{identity.entity.aliases."+vault.getAccessor()+".name}}\" {\n  capabilities = [\"create\", \"update\", \"sudo\"]\n}";
        Map<String, String> data = new HashMap<>();
        data.put("policy", policy);
        String result = Common.postVaultRequest(vault.getVaultUrl() + "/v1/sys/policies/acl/creds-"+dbConfigName, vault.getVaultToken(), data);
        System.out.println(result);
    }
}
