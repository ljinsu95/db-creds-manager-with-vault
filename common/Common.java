package common;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Common {
    public static String CONFIG_FILE = "common/dev.config.properties";
    public static Border MARGIN_10 = BorderFactory.createEmptyBorder(10, 10, 10, 10);

    public static int SUCCESS_CODE = 200;

    public static String request(String vaultUrl) {
        System.out.println("호출 URL : " + vaultUrl);
        HttpURLConnection connection = null;

        try {
            URL url = new URL(vaultUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(2000); // 연결 타임아웃 설정 (2초)
            connection.setReadTimeout(2000); // 읽기 타임아웃 설정 (2초)

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                System.out.println("Response: " + response.toString());
                return response.toString();
            } else {
                System.out.println("HTTP GET request failed: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect(); // 연결 종료
            }
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
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String postVaultRequest(String vaultUrl, String vaultToken, Map<String, String> data) {
        System.out.println("호출 URL : " + vaultUrl);
        HttpURLConnection connection = null;

        /* Request Body Data Map -> Json */
        String requestBody = createJson(data);

        try {
            URL url = new URL(vaultUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("X-Vault-Token", vaultToken);

            // 요청 바디 전송을 위한 OutputStream 열기
            connection.setDoOutput(true);
            try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                outputStream.writeBytes(requestBody);
                outputStream.flush();
            }

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            if (responseCode >= 200 && responseCode < 300) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                System.out.println("Response: " + response.toString());
                return response.toString();
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                System.out.println("Response: " + response.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect(); // 연결 종료
            }
        }
        return "";
    }

    public static String getVaultRequest(String vaultUrl, String vaultToken) {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(vaultUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Vault-Token", vaultToken);
            connection.setDoOutput(true);


            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                System.out.println("Response: " + response.toString());
                return response.toString();
            } else {

            }
            System.out.println("Response Code: " + responseCode);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect(); // 연결 종료
            }
        }
        return "";
    }


    public static String createJson(Map<String, String> map) {
        try {
            // ObjectMapper 객체 생성
            ObjectMapper objectMapper = new ObjectMapper();

            // Map을 JSON 문자열로 변환
            String jsonString = objectMapper.writeValueAsString(map);

            // JSON 문자열 출력
            System.out.println(jsonString);
            return jsonString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, Object> readJson(String jsonResponse) {
        // 중첩된 JSON 문자열 예시
        // String jsonResponse = "{ \"name\": \"John Doe\", \"contact\": { \"email\":
        // \"john.doe@example.com\", \"phone\": \"123-456-7890\" }, \"age\": 30 }";

        try {
            // ObjectMapper 객체 생성
            ObjectMapper objectMapper = new ObjectMapper();

            // JSON 문자열을 Map으로 변환
            Map<String, Object> map = objectMapper.readValue(jsonResponse, new TypeReference<Map<String, Object>>() {
            });

            // Map 출력
            // printMap(map);
            return map;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // public static void printMap(Map<String, Object> map) {
    //     for (Map.Entry<String, Object> entry : map.entrySet()) {
    //         System.out.println(entry.getKey() + ": " + entry.getValue());
    //         if (entry.getValue() instanceof Map) {
    //             printMap((Map<String, Object>) entry.getValue());
    //         }
    //     }
    // }

    @SuppressWarnings("unchecked")
    public static String[] getJsonNestedValue(String strJson, String... keys) {
        System.out.println("## JSON DATA : " + strJson);
        // ObjectMapper 객체 생성
        ObjectMapper objectMapper = new ObjectMapper();

        // JSON 문자열을 Map으로 변환
        Map<String, Object> map;
        try {
            map = objectMapper.readValue(
                    strJson,
                    new TypeReference<Map<String, Object>>() {
                    });
            // 중첩 값 확인
            Object value = map;
            for (String key : keys) {
                if (value instanceof Map) {
                    value = ((Map<String, Object>) value).get(key);
                } else {
                    return null;
                }
            }
            String nestedValue = value != null ? value.toString() : null;
            
            System.out.println("config list : " + nestedValue);

            // 대괄호 제거 및 공백 제거
            nestedValue = nestedValue.substring(1, nestedValue.length() - 1).replace(" ", "");

            // 콤마를 기준으로 분리
            String[] items = nestedValue.split(",");
            return items;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Map<String, Object>형태의 중첩 데이터 조회
    @SuppressWarnings("unchecked")
    public static String getNestedValue(Map<String, Object> map, String... keys) {
        Object value = map;
        for (String key : keys) {
            if (value instanceof Map) {
                value = ((Map<String, Object>) value).get(key);
            } else {
                return null;
            }
        }
        return value != null ? value.toString() : null;
    }

}
