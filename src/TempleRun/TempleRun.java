package TempleRun;

import java.io.IOException;
import java.util.List;

import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import TempleRun.Commands.PlayerCommands;
import TempleRun.Config.ConfigLoader;
import TempleRun.Listeners.PlayerListener;
import TempleRun.Listeners.Coin.CoinListener;
import TempleRun.Listeners.Features.SplashListener;
import TempleRun.Listeners.Move.PlayerMoveListener;
import TempleRun.Listeners.Sign.PlayerSignListener;
import TempleRun.Metrics.Metrics;
import TempleRun.Util.Util;

public class TempleRun extends JavaPlugin {

	public Util util;
	public PlayerMoveListener mlistener;
	public PlayerSignListener slistener;
	public PlayerListener plistener;
	public CoinListener clistener;
	public SplashListener spllistener;

	public PlayerCommands cmd;
	public int task;

	public List<String> item;
	public List<String> safepoints;

	public boolean pickup = false;

	public static boolean update = false;
	public static long size = 0;

	public ConfigLoader cload;
	

	@Override
	public void onEnable() {

		/* Config laden */
		loadConfig();

		/* Objekt registrieren */
		util = new Util(this);

		slistener = new PlayerSignListener(this);
		mlistener = new PlayerMoveListener(this);
		plistener = new PlayerListener(this);
		clistener = new CoinListener(this);
		spllistener = new SplashListener();

		getServer().getPluginManager().registerEvents(slistener, this);
		getServer().getPluginManager().registerEvents(mlistener, this);
		getServer().getPluginManager().registerEvents(plistener, this);
		getServer().getPluginManager().registerEvents(clistener, this);
		getServer().getPluginManager().registerEvents(spllistener, this);

		cmd = new PlayerCommands(this);
		getCommand("templerun").setExecutor(cmd);

		startMetrics();

		try {
			cload = new ConfigLoader(this);
		} catch (Exception e) {
			System.out.println("[TempleRun] Error while loading topplayers.yml! Plugin will disable!");
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		} finally {
			System.out.println("[TempleRun] topplayers.yml successfully loaded.");
		}
	}

	@Override
	public void onDisable() {

		Util.kickAll(this, Util.prefix + "The Server got reloaded!");

	}

	/* Config */
	private void loadConfig() {

		getConfig().options().copyDefaults(true);

		item = getConfig().getStringList("TempleRun.WinItem");
		safepoints = getConfig().getStringList("TempleRun.SafeCheckPointAt");

		saveConfig();
	}

	/**
	 * Start the Metrics.class
	 */
	private void startMetrics() {

		try {

			Metrics ms = new Metrics(this);
			ms.start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * TopTen Configuration Loader!
	 * 
	 * @return
	 */
	public Configuration getConfigLoader() {
		cload.load();
		return cload.getConfig();
	}

	public void saveConfigLoader() {
		cload.save();
	}

}
