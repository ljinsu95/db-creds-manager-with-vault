package common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.Common;
import common.model.Vault;

public class VaultUserpassAuth {
    private Vault vault;

    public VaultUserpassAuth(Vault vault) {
        this.vault = vault;
    }

    /* db-userpass Auth 확인 */
    public String authCheck() {
        System.out.println("Userpass Auth Check");

        String engineList = Common.request(vault.getVaultUrl() + "/v1/sys/auth", vault.getVaultToken());

        // ObjectMapper 객체 생성
        ObjectMapper objectMapper = new ObjectMapper();

        // JSON 문자열을 Map으로 변환
        Map<String, Object> map;
        try {
            map = objectMapper.readValue(
                    engineList,
                    new TypeReference<Map<String, Object>>() {
                    });
            // 중첩 값 확인 (없을 경우 Null 리턴)
            String nestedValue = Common.getNestedValue(map, "data", "db-userpass/", "accessor");
            if (nestedValue == null) {
                return null;
            } else {
                System.out.println("Userpass Auth 활성화 상태");
                vault.setAccessor(nestedValue);
            }
            return nestedValue;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return "";
    }

    /* Auth 활성화 */
    public void authEnable() {
        System.out.println("Userpass Auth Enable");
        Map<String, String> data = new HashMap<>();
        data.put("type", "userpass");
        Common.postVaultRequest(vault.getVaultUrl() + "/v1/sys/auth/db-userpass", vault.getVaultToken(), data);
    }

    // Userpass Auth User List
    public String[] userList() {
        System.out.println("Userpass Auth User List");

        String jsonUserList = Common.getVaultRequest(vault.getVaultUrl() + "/v1/auth/db-userpass/users?list=true",
                vault.getVaultToken());

        if (jsonUserList.isBlank()) {
            return new String[0];
        }
        // ObjectMapper 객체 생성
        ObjectMapper objectMapper = new ObjectMapper();

        // JSON 문자열을 Map으로 변환
        Map<String, Object> map;
        try {
            map = objectMapper.readValue(
                    jsonUserList,
                    new TypeReference<Map<String, Object>>() {
                    });
            // 중첩 값 확인
            String nestedValue = Common.getNestedValue(map, "data", "keys");
            System.out.println("user list : " + nestedValue);

            // 대괄호 제거 및 공백 제거
            nestedValue = nestedValue.substring(1, nestedValue.length() - 1).replace(" ", "");

            // 콤마를 기준으로 분리
            String[] items = nestedValue.split(",");

            // 유저 목록을 vault 객체에 저장
            List<String> userList = new ArrayList<String>();
            for (String user : items) {
                userList.add(user);
            }
            vault.setUserList(userList);
            
            return items;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new String[0];
    }

    /* 사용자 생성 */
    public void createUser(String username, String password) {
        System.out.println("User Create");
        // TODO : token_policies에 policy 다수 설정으로 변경 필요, 현재는 1개의 policy로 모든 db-config 접근 허용, 추후 지정한 db-config에만 접근 허용
        Map<String, String> data = new HashMap<>();
        data.put("password", password);
        data.put("token_policies", "db-user");

        String result = Common.postVaultRequest(vault.getVaultUrl() + "/v1/auth/db-userpass/users/"+username, vault.getVaultToken(), data);
        System.out.println(result);
    }

    /* 
     * 사용자 권한 생성
     * TODO : Userpass 권한 세부 설정 필요 (DB 별로 생성, Userpass - username, password 값만 허용 등)
     */
    public void createPolicy() {
        System.out.println("User Policy Create");
        String policy = "path \"db-manager/creds/{{identity.entity.aliases."+vault.getAccessor()+".name}}\" {\n    capabilities = [\"read\"]\n}\npath \"auth/db-userpass/users/{{identity.entity.aliases."+vault.getAccessor()+".name}}\" {\n    capabilities = [\"create\", \"update\"]\n}\n";
        Map<String, String> data = new HashMap<>();
        data.put("policy", policy);
        String result = Common.postVaultRequest(vault.getVaultUrl() + "/v1/sys/policies/acl/db-user", vault.getVaultToken(), data);
        System.out.println(result);
    }
}
