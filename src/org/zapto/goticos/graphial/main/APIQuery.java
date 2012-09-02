package org.zapto.goticos.graphial.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class APIQuery {

	private static Integer _hits_left = -1;

	private static void checkWait() throws JSONException, InterruptedException,
			IOException {
		JSONTokener tokener = new JSONTokener(obtainMessage(new URL(
				"http://api.twitter.com/1/account/rate_limit_status.json")));
		JSONObject data = new JSONObject(tokener);
		_hits_left = data.getInt("remaining_hits");
		System.out.println("Hits left: " + _hits_left);

		if (_hits_left < 1) {
			long seconds;
			tokener = new JSONTokener(obtainMessage(new URL(
					"http://api.twitter.com/1/account/rate_limit_status.json")));
			data = new JSONObject(tokener);
			seconds = data.getLong("reset_time_in_seconds")
					- System.currentTimeMillis() / 1000L;

			System.out.println("No hits left. Gotta wait " + seconds);
			while (seconds > 0) {
				Thread.sleep(3000);
				seconds -= 3;
				System.out
						.println("Sleeping for " + seconds + " more seconds.");
			}
		}
	}

	private static String obtainMessage(URL url) throws JSONException,
			InterruptedException, IOException {
		String message = null;

		HttpURLConnection connection = null;
		BufferedReader reader;

		try {
			connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("GET");
			connection.setDoInput(true);
			connection.setReadTimeout(10000);

			connection.connect();

			reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			message = reader.readLine();

			connection.disconnect();
			reader.close();

		} catch (IOException e) {
			if (connection.getResponseCode() == 401) {
				// TODO: Dirty hack for private accounts. Fix.
				System.out
						.println("Got 401, private follower list, skipping...");
				return ("{\"ids\":[],\"next_cursor\":0}");
			}
		}

		return (message);
	}

	public static JSONObject query(URL url) throws IOException, JSONException,
			InterruptedException {
		checkWait();

		String message = obtainMessage(url);

		JSONTokener tokener = new JSONTokener(message);
		JSONObject data = new JSONObject(tokener);

		return (data);
	}

	public static JSONObject queryArrayFirstElement(URL url)
			throws IOException, JSONException, InterruptedException {
		checkWait();

		String message = obtainMessage(url);

		JSONTokener tokener = new JSONTokener(message);
		JSONArray array = new JSONArray(tokener);
		JSONObject data = array.getJSONObject(0);

		return (data);
	}
}
