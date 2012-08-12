package com.xapfeltortexp.templerun;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import com.xapfeltortexp.mexdb.MexDB;
import com.xapfeltortexp.templerun.listener.TempleRunListener;
import com.xapfeltortexp.templerun.listener.TempleRunSetCoinsListener;

public class TempleRunMain extends JavaPlugin {

	// Strings
	final static Logger log = Logger.getLogger("Minecraft");
	private TempleRunCommands templerun;

	// Database
	public MexDB database;

	// ChatColor + String
	private ChatColor gold = ChatColor.GOLD;
	private ChatColor yellow = ChatColor.YELLOW;

	private String prefix = this.yellow + "[" + gold + "TempleRun" + yellow + "] ";

	// ArrayLists
	public ArrayList<String> players = new ArrayList<String>();
	public ArrayList<String> fall = new ArrayList<String>();
	public ArrayList<String> move = new ArrayList<String>();
	public ArrayList<String> walk = new ArrayList<String>();

	// Integers + Booleans
	public int i;
	public int number;
	public int outOfWorld = 0;
	public int Item = 0;
	public int Amount = 0;
	public int score = 0;
	public int Speed = 0;
	public int points = 10;
	public int SpeedOverDiamondBlock;

	/*
	 *  _____             _     _       
	 * | ____|_ __   __ _| |__ | | ___ 
	 * |  _| | '_ \ / _` | '_ \| |/ _ \
	 * | |___| | | | (_| | |_) | |  __/
	 * |_____|_| |_|\__,_|_.__/|_|\___|
	 */

	// Enable
	@Override
	public void onEnable() {

		// Database
		this.database = new MexDB("plugins/TempleRun", "score");

		// Metrics
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {

		}

		TempleRunMain.log.info("[TempleRun] Plugin + all Files loaded. Version: " + getDescription().getVersion());

		// Executor
		templerun = new TempleRunCommands(this);
		getCommand("templerun").setExecutor(templerun);

		// Register Listener
		getServer().getPluginManager().registerEvents(new TempleRunListener(this), this);
		getServer().getPluginManager().registerEvents(new TempleRunSetCoinsListener(this), this);

		// Integer set to 0
		this.i = 0;
		this.points = 0;

		// Config
		getConfig().options().header("Plugin developed by xapfeltortexp | www.LostForce.com | Skype: jan_hoeck\n\nOutOfWorld = The BlockY. When you fall under this BlockHigh, then you\nfailed TempleRun.");
		if (getConfig().get("TempleRun.OutOfWorld") == null)
			getConfig().set("TempleRun.OutOfWorld", 5);
		if (getConfig().get("TempleRun.ItemWhenYouWin.Item") == null)
			getConfig().set("TempleRun.ItemWhenYouWin.Item", 264);
		if (getConfig().get("TempleRun.ItemWhenYouWin.Amount") == null)
			getConfig().set("TempleRun.ItemWhenYouWin.Amount", 1);
		if (getConfig().get("TempleRun.RunSpeed.Speed") == null)
			getConfig().set("TempleRun.RunSpeed.Speed", 2);
		if (getConfig().get("TempleRun.RunSpeed.SpeedOverDiamondBlock") == null)
			getConfig().set("TempleRun.RunSpeed.SpeedOverDiamondBlock", 4);
		saveConfig();

		// Variablen zur config
		outOfWorld = getConfig().getInt("TempleRun.OutOfWorld", 5);
		Item = getConfig().getInt("TempleRun.ItemWhenYouWin.Item", 264);
		Amount = getConfig().getInt("TempleRun.ItemWhenYouWin.Amount", 1);
		Speed = getConfig().getInt("TempleRun.RunSpeed.Speed", 2);
		SpeedOverDiamondBlock = getConfig().getInt("TempleRun.RunSpeed.SpeedOverDiamondBlock", 4);
	}

	/*
	 *  ____  _           _     _      
	 * |  _ \(_)___  __ _| |__ | | ___ 
	 * | | | | / __|/ _` | '_ \| |/ _ \
	 * | |_| | \__ \ (_| | |_) | |  __/
	 * |____/|_|___/\__,_|_.__/|_|\___|
	 */
	@Override
	public void onDisable() {

		TempleRunMain.log.info("[TempleRun] Plugin + all Files unloaded.");

		for (int k = 0; k < players.size(); k++) {
			getServer().getPlayer(players.get(k)).setSprinting(false);
			getServer().getPlayer(players.get(k)).removePotionEffect(PotionEffectType.SPEED);
			getServer().getPlayer(players.get(k)).sendMessage(prefix + ChatColor.RED + "Server reloaded or Restarted. Teleported back to the Spawn.");
			getServer().getPlayer(players.get(k)).teleport(getServer().getPlayer(players.get(k)).getWorld().getSpawnLocation());
			getServer().getPlayer(players.get(k)).remove();

		}
		this.players.clear();

		this.move.clear();

	}

}
