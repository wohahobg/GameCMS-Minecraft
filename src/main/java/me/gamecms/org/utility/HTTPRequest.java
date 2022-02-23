package me.gamecms.org.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
	
	public static String readUrl(String url) throws Exception {

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

}
