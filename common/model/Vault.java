package common.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.Common;
import common.VaultException;

/**
 * Vault관련 정보를 담고있는 싱글톤 클래스
 */
public class Vault {
    private static Vault vault;

    public static String AUTH_TOKEN = "Token";
    public static String AUTH_USERNAME = "Username";
    private String vaultUrl;
    private String vaultAuthType;
    private String vaultToken;
    private String vaultUserNm;
    private String vaultUesrPw;
    private String dbUrl;
    private String dbUserNm;
    private String dbUserPw;

    private String userType;

    private String accessor;
    private List<String> userList;
    private List<String> dbConfigList;

    private Vault() {

    }

    public static Vault getInstance() {
        if (vault == null) {
            vault = new Vault();
        }
        return vault;
    }

    // getter & setter
    public String getVaultUrl() {
        return vaultUrl;
    }

    public void setVaultUrl(String vaultUrl) {
        this.vaultUrl = vaultUrl;
    }

    public String getVaultAuthType() {
        return vaultAuthType;
    }

    public void setVaultAuthType(String vaultAuthType) {
        this.vaultAuthType = vaultAuthType;
    }

    public String getVaultToken() {
        return vaultToken;
    }

    public void setVaultToken(String vaultToken) {
        this.vaultToken = vaultToken;
    }

    public String getVaultUserNm() {
        return vaultUserNm;
    }

    public void setVaultUserNm(String vaultUserNm) {
        this.vaultUserNm = vaultUserNm;
    }

    public String getVaultUesrPw() {
        return vaultUesrPw;
    }

    public void setVaultUesrPw(String vaultUesrPw) {
        this.vaultUesrPw = vaultUesrPw;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbUserNm() {
        return dbUserNm;
    }

    public void setDbUserNm(String dbUserNm) {
        this.dbUserNm = dbUserNm;
    }

    public String getDbUserPw() {
        return dbUserPw;
    }

    public void setDbUserPw(String dbUserPw) {
        this.dbUserPw = dbUserPw;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getAccessor() {
        return accessor;
    }

    public void setAccessor(String accessor) {
        this.accessor = accessor;
    }

    public List<String> getUserList() {
        return userList;
    }

    public void setUserList(List<String> vaultUserList) {
        this.userList = vaultUserList;
    }

    public List<String> getDbConfigList() {
        return dbConfigList;
    }

    public void setDbConfigList(List<String> dbConfigList) {
        this.dbConfigList = dbConfigList;
    }

    // Vault status
    public boolean healthCheck() {
        System.out.println("vault url : "+getVaultUrl());
        String jsonResult = Common.request(getVaultUrl()+"/v1/sys/health?perfstandbyok=true&&standbyok=true");

        if (jsonResult.isEmpty()) {
            System.out.println("Vault Health Error");
            return false;
        }

        String nestedValue = Common.getNestedJsonToStr(jsonResult, "sealed");
        if (nestedValue.equals("false")) {
            System.out.println("Vault Health OK");
            return true;
        } else {
            return false;
        }
    }

    /* 
     * db-userpass login
     * return : String token;
     */
    public void userpassLogin(String userNm, String userPw) throws VaultException {
        System.out.println("db-userpass login");
        Map<String, String> data = new HashMap<>();
        data.put("password", userPw);
        String result = Common.postVaultLogin(vault.getVaultUrl() + "/v1/auth/db-userpass/login/"+userNm, data);
        System.out.println(result);
        String token = Common.getNestedJsonToStr(result, "auth", "client_token");
        setVaultToken(token);
    }

    public String tokenLookupSelf() throws VaultException {
        System.out.println("Token Lookup Self");
        if (vaultAuthType.equals(AUTH_USERNAME)) {
            userpassLogin(vaultUserNm, vaultUesrPw);
        }
        return Common.getVaultRequest(vaultUrl+"/v1/auth/token/lookup-self", vaultToken);
    }
}