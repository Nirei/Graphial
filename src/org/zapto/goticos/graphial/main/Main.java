package org.zapto.goticos.graphial.main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import org.json.JSONException;

public class Main {

	public static void usage() {
		System.out
				.println("Usage: java -jar graphial.jar twitter_username output_file\n");
		System.exit(1);
	}

	public static void main(String[] args) {

		if (args.length != 2) { // incorrect arguments, abort
			usage();
		}

		TwitterCache.loadCache();

		HashMap<Integer, Vector<Integer>> users = new HashMap<Integer, Vector<Integer>>();
		Vector<Integer> followers;

		try {
			int user = TwitterCache.queryId(args[0]);
			followers = TwitterCache.queryFollowers(user);

			users.put(user, followers);

			// For progress info
			Double r = 1.;
			Integer size = followers.size();

			System.out.println("* Step 1/2: Retrieving followers *");
			for (Integer i : followers) {
				System.out.println(new Double(r / size.doubleValue() * 100.)
						.intValue()
						+ "% - "
						+ (r++).intValue()
						+ "/"
						+ followers.size() + " followers");
				Vector<Integer> followers2 = TwitterCache.queryFollowers(i);
				if (followers2.contains(user)) {
					users.put(i, followers2);
				} // Filter out followers that don't follow you.
			}

			HashSet<String> relations = new HashSet<String>();
			
			System.out.println("* Step 2/2: Establishing relations between followers *");
			r = 1.;
			size = users.size();

			for (Integer u : users.keySet()) {
				System.out.println(new Double(r / size.doubleValue() * 100.)
				.intValue()
				+ "% - "
				+ (r++).intValue()
				+ "/"
				+ followers.size() + " followers");
				for (Integer f : users.get(u)) {
					if (followers.contains(f)) {
						String first, second;
						if (u > f) {
							first = TwitterCache.queryUsername(u);
							second = TwitterCache.queryUsername(f);
						} else {
							first = TwitterCache.queryUsername(f);
							second = TwitterCache.queryUsername(u);
						}

						relations.add("\"@" + first + "\" -- \"@" + second
								+ "\";\n");
					}
				}
			}

			// Build the file
			StringBuilder sb = new StringBuilder();
			sb.append("graph following {\n");
			for (String s : relations) {
				sb.append(s);
			}
			sb.append("}");

			// Write it down
			FileWriter fw = new FileWriter(args[1]);
			fw.write(sb.toString());
			fw.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		TwitterCache.saveCache();
	}
}
