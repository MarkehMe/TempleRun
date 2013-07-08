package de.jan.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigLoader {
	

	
	private File rawFile;
	private String resName;
	private String path;
	private String fileName;

	/**
	 * Handles configuration files.
	 * 
	 * @param path
	 * @param fileName
	 */
	public ConfigLoader(String path, String target, String resName) {
		this.path = path;
		this.fileName = target;
		this.resName = resName;
	}

	/**
	 * Loads and gets the configuration file.
	 * 
	 * @return The YamlConfiguration.
	 */
	public FileConfiguration getConfig() {

		// Init rawFile
		rawFile = new File(path, fileName);

		// Create and copy if non existent
		if (!rawFile.exists()) {
			new File(path).mkdirs();
			try {
				rawFile.createNewFile();
				if (!copyResource(getClass().getResourceAsStream(resName))) {
					return null;
				}
			} catch (IOException e) {
				Bukkit.getLogger().log(Level.SEVERE, "[TempleRun] Could not create " + fileName + "-file.");
				Bukkit.getLogger().log(Level.SEVERE, "", e);
			}
		}

		// Load data
		FileConfiguration yamlConfig;
		yamlConfig = new YamlConfiguration();
		try {
			yamlConfig.load(rawFile);
		} catch (FileNotFoundException e) {
			Bukkit.getLogger().log(Level.SEVERE, "[TempleRun] Could not load " + fileName + "-file.");
			Bukkit.getLogger().log(Level.SEVERE, "", e);
		} catch (IOException e) {
			Bukkit.getLogger().log(Level.SEVERE, "[TempleRun] Could not load " + fileName + "-file.");
			Bukkit.getLogger().log(Level.SEVERE, "", e);
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			Bukkit.getLogger().log(Level.SEVERE, "[TempleRun] Could not load " + fileName + "-file.");
			Bukkit.getLogger().log(Level.SEVERE, "", e);
		}

		return yamlConfig;
	}

	/**
	 * Copies the resource configuration to the target configuration in the
	 * plugins folder.
	 * 
	 * @param in
	 *            The InputStream gained from the resource configuration file.
	 * @return true if copying was successful, false when not.
	 */
	private boolean copyResource(InputStream in) {

		try {

			OutputStream out = new FileOutputStream(rawFile);

			int read = 0;
			int bufferLength = 1024;
			byte[] bytes = new byte[bufferLength];
			while ((read = in.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}

			// Be Clozzzy
			in.close();
			out.flush();
			out.close();
			return true;
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "[TempleRun] Could not copy resource " + fileName + "-file.");
			Bukkit.getLogger().log(Level.SEVERE, "", ex);
			return false;
		}
	}

	
}
