package common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

public class Common {
    public static String CONFIG_FILE="common/dev.config properties";
    public static Border MARGIN_10 = BorderFactory.createEmptyBorder(10 , 10, 10 , 10);

    public static int SUCCESS_CODE = 200;

    public static String request(String vaultUrl) {
        try {
            URL url = new URL(vaultUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println("Response: " + response.toString());
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String request(String vaultUrl, String vaultToken) {
        try {
            URL url = new URL(vaultUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Vault-Token", vaultToken);

            int responseCode = connection.getResponseCode();
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
        return "";
    }
}
