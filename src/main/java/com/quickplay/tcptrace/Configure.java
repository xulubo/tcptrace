package com.quickplay.tcptrace;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.quickplay.tcptrace.exception.ConfigurationException;

public class Configure {

	private static Configure instance;
	private Properties properties;

	public static Configure getInstance() {
		if (instance == null) {
			instance = new Configure();
		}

		return instance;
	}

	public static String getUserHome() {
		return System.getProperty("user.home");
	}

	public Configure() {
		properties = new Properties();
		try {
			File propfile = getConfigurationFile();
			if (propfile.exists()) {
				properties.load(new FileInputStream(propfile));
			}
		} catch(Exception e) {
			throw new ConfigurationException(e.getMessage(), e);
		}
	}

	public File getLastOpenFolder() {
		String folder = properties.getProperty("lastopen");

		if (folder == null) {
			return new File(".");
		}

		return new File(folder);
	}

	public void saveLastOpenFolder(File folder) {
		properties.setProperty("lastopen", folder.getAbsolutePath());
		save();

	}

	private void save() {
		try {
			File f = getConfigurationFile();
			properties.store(new FileOutputStream(f), "");		
		} catch (Exception e) {
			throw new ConfigurationException(e.getMessage(), e);
		}	}

	private File getConfigurationFile() {
		return new File(getUserHome(), "tcptrace.properties");
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	public void saveProperty(String key, String value) {
		properties.setProperty(key, value);
		save();
	}
}
