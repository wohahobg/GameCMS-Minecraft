package me.gamecms.org.utility;


import me.gamecms.org.api.responses.BasicRequestResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.ConnectException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class HTTPRequest {

    public static String sendPost(String apiURL, String params, String apiKey) {
        try {
            URL url = new URL(apiURL);

            // Request setup
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            // Set headers
            connection.setRequestProperty("User-Agent", "Java " + System.getProperty("java.runtime.version"));
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Accept", "application/json");

            // For POST only - START
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                os.write(params.getBytes());
                os.flush();
            }
            // For POST only - END

            return returnResponse(connection);

        } catch (ConnectException e) {
            return BasicRequestResponse.bad_request_format.replace("500", "503")
                    .replace("Something went wrong", "Service unavailable. Connection timed out.");
        } catch (IOException e) {
            return BasicRequestResponse.bad_request_format.replace("500", "500")
                    .replace("Something went wrong", "I/O error occurred: " + e.getMessage());
        }
    }

    public static String sendGET(String apiURL, String apiKey) {
        try {
            URL url = new URL(apiURL);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.setRequestProperty("User-Agent", "Java " + System.getProperty("java.runtime.version"));
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Accept", "application/json");

            return returnResponse(connection);

        } catch (ConnectException e) {
            return BasicRequestResponse.bad_request_format.replace("500", "503")
                    .replace("Something went wrong", "Service unavailable. Connection timed out.");
        } catch (IOException e) {
            return BasicRequestResponse.bad_request_format.replace("500", "500")
                    .replace("Something went wrong", "I/O error occurred: " + e.getMessage());
        }
    }

    public static String returnResponse(HttpURLConnection connection) throws IOException {
        BufferedReader reader;
        String line;
        StringBuilder responseContent = new StringBuilder();
        String result = BasicRequestResponse.bad_request_format;
        int responseCode = connection.getResponseCode();

        if (responseCode == 200 || responseCode == 400 || responseCode == 404) {
            if (responseCode == 200) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            while ((line = reader.readLine()) != null) {
                responseContent.append(line);
            }
            reader.close();
            result = responseContent.toString();

            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(result);
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                jsonObject.addProperty("status", responseCode);
                result = jsonObject.toString();
            }
        }
        return result;
    }
}