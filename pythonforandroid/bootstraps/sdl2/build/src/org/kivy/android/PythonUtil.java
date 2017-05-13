package org.kivy.android;

import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class PythonUtil {
	private static final String TAG = "pythonutil";

	protected static String[] getLibraries(File filesDir) {

		ArrayList<String> libs = new ArrayList<String>();
		libs.add("SDL2");
		libs.add("SDL2_image");
		libs.add("SDL2_mixer");
		libs.add("SDL2_ttf");

		String absPath = filesDir.getParentFile().getParentFile().getAbsolutePath() + "/lib/";
		filesDir = new File(absPath);

		File [] files = filesDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return  name.matches(".*ssl.*") || name.matches(".*crypto.*") || name.matches(".*sqlite3.*");
			}
		});

		for (int i = 0; i < files.length; ++i) {
			File mfl = files[i];
			String name = mfl.getName();
			name = name.substring(3, name.length() - 3);
			libs.add(name);
		}

		libs.add("python2.7");
		libs.add("python3.5m");
		libs.add("main");

		return libs.toArray(new String[0]);
	}


	public static void loadLibraries(File filesDir) {

		String filesDirPath = filesDir.getAbsolutePath();
		boolean foundPython = false;

		for (String lib : getLibraries(filesDir)) {
			try {
				System.loadLibrary(lib);
				if (lib.startsWith("python")) {
					foundPython = true;
				}
			} catch(UnsatisfiedLinkError e) {
				// If this is the last possible libpython
				// load, and it has failed, give a more
				// general error
				e.printStackTrace();
				if (lib.startsWith("python3.6") && !foundPython) {
					throw new java.lang.RuntimeException("Could not load any libpythonXXX.so");
				}
				continue;
			}
		}

		try {
			System.load(filesDirPath + "/lib/python2.7/lib-dynload/_io.so");
			System.load(filesDirPath + "/lib/python2.7/lib-dynload/unicodedata.so");
		} catch(UnsatisfiedLinkError e) {
			Log.v(TAG, "Failed to load _io.so or unicodedata.so...but that's okay.");
		}

		try {
			// System.loadLibrary("ctypes");
			System.load(filesDirPath + "/lib/python2.7/lib-dynload/_ctypes.so");
		} catch(UnsatisfiedLinkError e) {
			Log.v(TAG, "Unsatisfied linker when loading ctypes");
		}

		Log.v(TAG, "Loaded everything!");
	}
}
