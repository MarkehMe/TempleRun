package TempleRun;

import java.io.IOException;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import TempleRun.Commands.PlayerCommands;
import TempleRun.Listeners.PlayerListener;
import TempleRun.Listeners.Coin.CoinListener;
import TempleRun.Listeners.Move.PlayerMoveListener;
import TempleRun.Listeners.Sign.PlayerSignListener;
import TempleRun.Metrics.Metrics;
import TempleRun.Util.Util;
import TempleRun.Util.SaveEnum.MySQL;
import TempleRun.Util.SaveEnum.SaveType;

public class TempleRun extends JavaPlugin {

	/* SaveType */
	public SaveType savetype;
	public Util util;
	public MySQL mysql;
	public PlayerMoveListener mlistener;
	public PlayerSignListener slistener;
	public PlayerListener plistener;
	public CoinListener clistener;

	public PlayerCommands cmd;
	public int task;

	public List<String> item;
	public List<String> safepoints;

	// private String type;

	@Override
	public void onEnable() {

		/* Config laden */
		loadConfig();

		/* Objekt registrieren */
		util = new Util(this);
		mysql = new MySQL();

		slistener = new PlayerSignListener(this);
		mlistener = new PlayerMoveListener(this);
		plistener = new PlayerListener(this);
		clistener = new CoinListener();

		getServer().getPluginManager().registerEvents(slistener, this);
		getServer().getPluginManager().registerEvents(mlistener, this);
		getServer().getPluginManager().registerEvents(plistener, this);
		getServer().getPluginManager().registerEvents(clistener, this);

		cmd = new PlayerCommands(this);
		getCommand("templerun").setExecutor(cmd);

		startMetrics();

		/* type aus der Config ziehen */
		// type = getConfig().getString("TempleRun.SaveType");

		/*
		 * Checken welche SaveMethode ich nutze if(SaveType.getSaveType(type) ==
		 * SaveType.MYSQL) {
		 * System.out.println("[TempleRun] You are using MySQL!"); } else
		 * if(SaveType.getSaveType(type) == SaveType.CONFIG) {
		 * System.out.println("[TempleRun] You are using the Bukkit Config!"); }
		 * else { System.out.println("SaveType '" + type +
		 * "' not found! Please use MYSQL or CONFIG!");
		 * getServer().getPluginManager().disablePlugin(this); }
		 */
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

	private void startMetrics() {

		try {

			Metrics ms = new Metrics(this);
			ms.start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
