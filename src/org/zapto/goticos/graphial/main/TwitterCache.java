package org.zapto.goticos.graphial.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TwitterCache {

	public static final File nameFile = new File("name_cache.dat");
	public static final File idFile = new File("id_cache.dat");
	public static final File followerFile = new File("follower_cache.dat");

	private static HashMap<Integer, String> nameCache = null;
	private static HashMap<String, Integer> idCache = null;
	private static HashMap<Integer, Vector<Integer>> followerCache = null;

	@SuppressWarnings("unchecked")
	public static void loadCache() {
		try {
			if (nameFile.exists()) {
				FileInputStream fisName = new FileInputStream(nameFile);
				ObjectInputStream oisName = new ObjectInputStream(fisName);
				nameCache = (HashMap<Integer, String>) oisName.readObject();
				oisName.close();
			} else {
				nameCache = new HashMap<Integer, String>();
			}
		} catch (IOException e) {
			nameCache = new HashMap<Integer, String>();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}

		try {
			if (idFile.exists()) {
				FileInputStream fisId = new FileInputStream(idFile);
				ObjectInputStream oisId = new ObjectInputStream(fisId);
				idCache = (HashMap<String, Integer>) oisId.readObject();
				oisId.close();
			} else {
				idCache = new HashMap<String, Integer>();
			}
		} catch (IOException e) {
			idCache = new HashMap<String, Integer>();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}

		try {
			if (followerFile.exists()) {
				FileInputStream fisFollower = new FileInputStream(followerFile);
				ObjectInputStream oisFollower;
				oisFollower = new ObjectInputStream(fisFollower);
				followerCache = (HashMap<Integer, Vector<Integer>>) oisFollower
						.readObject();
				oisFollower.close();
			} else {
				followerCache = new HashMap<Integer, Vector<Integer>>();
			}
		} catch (IOException e) {
			followerCache = new HashMap<Integer, Vector<Integer>>();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void saveCache() {
		ObjectOutputStream oosName = null;
		ObjectOutputStream oosId = null;
		ObjectOutputStream oosFollower = null;

		try {
			if (nameFile.exists())
				nameFile.delete();

			FileOutputStream fosName = new FileOutputStream(nameFile);
			oosName = new ObjectOutputStream(fosName);
			oosName.writeObject(nameCache);
		} catch (IOException e) {
			System.err.println("Unable to save name chache");
		} finally {
			try {
				oosName.close();
			} catch (IOException e) {
			}
		}

		try {
			if (idFile.exists())
				idFile.delete();

			FileOutputStream fosId = new FileOutputStream(idFile);
			oosId = new ObjectOutputStream(fosId);
			oosId.writeObject(idCache);
		} catch (IOException e) {
			System.err.println("Unable to save id chache");
		} finally {
			try {
				oosId.close();
			} catch (IOException e) {
			}
		}

		try {
			if (followerFile.exists())
				followerFile.delete();

			FileOutputStream fosFollower = new FileOutputStream(followerFile);
			oosFollower = new ObjectOutputStream(fosFollower);
			oosFollower.writeObject(followerCache);
		} catch (IOException e) {
			System.err.println("Unable to save follower chache");
		} finally {
			try {
				oosFollower.close();
			} catch (IOException e) {
			}
		}
	}

	public static String queryUsername(Integer id) throws IOException,
			JSONException, InterruptedException {
		String result = null;

		if (nameCache.containsKey(id)) {
			result = nameCache.get(id);
		} else {
			URL url = new URL(
					"http://api.twitter.com/1/users/lookup.json?user_id="
							+ Integer.toString(id));
			JSONObject data = APIQuery.queryArrayFirstElement(url);
			result = data.getString("screen_name");
			nameCache.put(id, result);
			if (!idCache.containsKey(result)) {
				idCache.put(result, id);
			}
		}

		return (result);
	}

	public static Integer queryId(String username) throws IOException,
			JSONException, InterruptedException {
		Integer result = null;

		if (idCache.containsKey(username)) {
			result = idCache.get(username);
		} else {
			URL url = new URL(
					"http://api.twitter.com/1/users/lookup.json?screen_name="
							+ username);
			JSONObject data = APIQuery.queryArrayFirstElement(url);
			result = data.getInt("id");
			idCache.put(username, result);
			if (!nameCache.containsKey(result)) {
				nameCache.put(result, username);
			}
		}

		return (result);
	}

	public static Vector<Integer> queryFollowers(Integer id)
			throws MalformedURLException, IOException, JSONException,
			InterruptedException {
		Vector<Integer> result = null;

		if (followerCache.containsKey(id)) {
			result = followerCache.get(id);

		} else {
			result = new Vector<Integer>();

			int i = -1; // Starting page
			while (i != 0) {
				JSONObject data = APIQuery.query(new URL(
						"http://api.twitter.com/1/followers/ids.json?cursor="
								+ Integer.toString(i) + "&user_id=" + id));

				i = data.getInt("next_cursor");

				JSONArray followers = data.getJSONArray("ids");
				for (int j = 0; j < followers.length(); j++)
					result.add(followers.getInt(j));

				followerCache.put(id, result);
			}
		}

		return result;
	}
}
