package org.zapto.goticos.graphial.mergecache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Vector;

public class MergeCache {

	public static File src1 = null;
	public static File src2 = null;
	public static File dst = null;

	private static HashMap<Integer, String> nameCache = new HashMap<Integer, String>();
	private static HashMap<String, Integer> idCache = new HashMap<String, Integer>();
	private static HashMap<Integer, Vector<Integer>> followerCache = new HashMap<Integer, Vector<Integer>>();

	public static void usage() {
		System.out
				.println("Usage: java -jar merge_cache.jar -n|-i|-f source1 source2 output\n"
						+ " -n : Merge name cache\n"
						+ " -i : Merge id cache\n"
						+ " -f : Merge follower cache");
	}

	/**
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		if (args.length != 4) {
			usage();
			System.exit(0);
		}

		src1 = new File(args[1]);
		src2 = new File(args[2]);
		dst = new File(args[3]);

		FileInputStream fis1 = null;
		FileInputStream fis2 = null;
		FileOutputStream fos = null;

		ObjectInputStream ois1 = null;
		ObjectInputStream ois2 = null;
		ObjectOutputStream oos = null;

		try {
			fis1 = new FileInputStream(src1);
			fis2 = new FileInputStream(src2);
			fos = new FileOutputStream(dst);

			ois1 = new ObjectInputStream(fis1);
			ois2 = new ObjectInputStream(fis2);
			oos = new ObjectOutputStream(fos);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}

		try {
			switch (args[0]) {
			case "-n":
				HashMap<Integer, String> tempName;
				tempName = (HashMap<Integer, String>) ois1.readObject();
				nameCache.putAll(tempName);
				tempName = (HashMap<Integer, String>) ois2.readObject();
				nameCache.putAll(tempName);

				oos.writeObject(nameCache);
				oos.close();
				break;
			case "-i":
				HashMap<String, Integer> tempId;
				tempId = (HashMap<String, Integer>) ois1.readObject();
				idCache.putAll(tempId);
				tempId = (HashMap<String, Integer>) ois2.readObject();
				idCache.putAll(tempId);

				oos.writeObject(idCache);
				oos.close();
				break;
			case "-f":
				HashMap<Integer, Vector<Integer>> tempFollower;
				tempFollower = (HashMap<Integer, Vector<Integer>>) ois1
						.readObject();
				followerCache.putAll(tempFollower);
				tempFollower = (HashMap<Integer, Vector<Integer>>) ois2
						.readObject();
				followerCache.putAll(tempFollower);

				oos.writeObject(followerCache);
				oos.close();
				break;
			default:
				usage();
				break;
			}
		} catch (Exception e) {
			try {
				oos.close();
			} catch (IOException e1) {}
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

}
