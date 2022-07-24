package me.gamecms.org.utility;


import me.gamecms.org.api.ApiRequestResponseMain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPRequest {


    public static String sendPost(String apiURL, String params, String apiKey) throws IOException {

        URL url = new URL(apiURL);

        //request setup
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        //set user-agent
        connection.setRequestProperty("User-Agent", "Java " + System.getProperty("java.runtime.version"));
        connection.setRequestProperty("Authorization", "Bearer" + apiKey);

        // For POST only - START
        connection.setDoOutput(true);
        OutputStream os = connection.getOutputStream();
        os.write(params.getBytes());
        os.flush();
        os.close();
        // For POST only - END

        return returnResponse(connection);
    }


    public static String sendGET(String apiURL, String apiKey) throws IOException {

        URL url = new URL(apiURL);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        connection.setRequestProperty("User-Agent", "Java " + System.getProperty("java.runtime.version"));
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);

        return returnResponse(connection);
    }

    public static String returnResponse(HttpURLConnection connection) throws IOException {
        BufferedReader reader;
        String line;
        StringBuilder responseContent = new StringBuilder();
        //simple add empty json status as null so we can check easy if the status is 200 or etc.
        String result = ApiRequestResponseMain.bad_request_format;
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NOT_FOUND || responseCode == HttpURLConnection.HTTP_BAD_REQUEST) { // success
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while ((line = reader.readLine()) != null) {
                responseContent.append(line);
            }

            reader.close();
            connection.connect();
            result = responseContent.toString();
        }
        return result;
    }

}
