package me.gamecms.org.utility;

import me.gamecms.org.balance.BalanceRequestResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPRequest {

    public static void openUrl(String url) throws Exception {

        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.connect();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder results = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                results.append(line);
            }

            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String readUrl(String url, String key) throws Exception {

        BufferedReader bufferedReader = null;

        try {

            bufferedReader = new BufferedReader(new InputStreamReader(new URL(url).openStream(), "UTF-8"));
            StringBuffer buffer = new StringBuffer();

            int read;
            char[] chars = new char[1024];

            while ((read = bufferedReader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }

            return buffer.toString();

        } finally {

            if (bufferedReader != null) {
                bufferedReader.close();
            }

        }
    }


    public static String sendPost(String url, String params, String apiKey) throws IOException {
        URL obj = new URL(url);

        HttpURLConnection httpURLConnection = (HttpURLConnection) obj.openConnection();
        httpURLConnection.setRequestMethod("POST");

        //set user-agent
        httpURLConnection.setRequestProperty("User-Agent", "Java " + System.getProperty("java.runtime.version"));
        httpURLConnection.setRequestProperty("Authorization", apiKey);

        // For POST only - START
        httpURLConnection.setDoOutput(true);
        OutputStream os = httpURLConnection.getOutputStream();
        os.write(params.getBytes());
        os.flush();
        os.close();
        // For POST only - END

        int responseCode = httpURLConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String inputLine;
            StringBuffer buffer = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                buffer.append(inputLine);
            }
            in.close();
            return buffer.toString();
        } else {
            return "POST request not worked";
        }
    }


    public static String sendGET(String url, String apiKey) throws IOException {
        URL obj = new URL(url);

        HttpURLConnection httpURLConnection = (HttpURLConnection) obj.openConnection();
        httpURLConnection.setRequestMethod("GET");

        httpURLConnection.setRequestProperty("User-Agent", "Java " + System.getProperty("java.runtime.version"));
        httpURLConnection.setRequestProperty("Authorization", apiKey);

        int responseCode = httpURLConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String inputLine;
            StringBuffer buffer = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                buffer.append(inputLine);
            }
            in.close();
            return buffer.toString();
        } else {
            return "GET request not worked";
        }
    }

}
