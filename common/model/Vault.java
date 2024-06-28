package common.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import common.Common;

/**
 * Vault
 */
public class Vault {
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

    public Vault() {

    }
    
    public Vault(String url) {
        this.vaultUrl = url;
    }

    public Vault(String url, String authType, String token) {
        this.vaultUrl = url;
        this.vaultAuthType = authType;
        this.vaultToken = token;
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

    // Vault status
    public String vaultStatus() {
        System.out.println("vault url : "+getVaultUrl());
        return Common.request("https://"+getVaultUrl()+"/v1/sys/seal-status");
    }

    // token login
    public String vaultLogin(String authType, String token) {
        Common.request(getVaultUrl(), token);
        return "";
    }

    public String vaultLogin(String authType, String userNm, String userPw) {

        return "";
    }

    public int tokenLookupSelf() {
        int responseCode = 999;
        if (vaultAuthType.equals(AUTH_USERNAME)) {
            vaultLogin(vaultAuthType, vaultUserNm, vaultUesrPw);
        }
        try {
            URL url = new URL(vaultUrl+"/v1/auth/token/lookup-self");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Vault-Token", vaultToken);

            responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            System.out.println("Response: " + response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseCode;
    }
}