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

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;

public class TempleRunMain extends JavaPlugin {

	// Strings
	final Logger log = Logger.getLogger("Minecraft");
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
	public ArrayList<String> walk = new ArrayList<String>();
	public ArrayList<String> overGold = new ArrayList<String>();

	// Integers
	public int oldfood;
	public int i;
	public int number;
	public int outOfWorld = 0;
	public int Item = 0;
	public int Amount = 0;
	public int score = 0;
	public int Speed = 0;
	public int points = 10;
	public int SpeedOverDiamondBlock;
	public int HowMuchPoints;
	public int BuyItem;
	public int BuyAmount;
	public int money_a = 0;

	// Boolean
	public boolean moneyuse = false;
	public boolean itemuse = false;
	public long start;
	public long stop;
	public long ergebnis;

	// Vault Register
	public static Economy econ = null;
	public static Permission perms = null;
	public static Chat chat = null;

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

		this.log.info("[TempleRun] Plugin + all Files loaded. Version: " + getDescription().getVersion());

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
		this.getConfig().options().copyDefaults(true);
		saveConfig();

		// Variablen aus der Config lesen
		load_config();

		setupEconomy();

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

		this.log.info("[TempleRun] Plugin + all Files unloaded.");

		for (int k = 0; k < players.size(); k++) {
			getServer().getPlayer(players.get(k)).setSprinting(false);
			getServer().getPlayer(players.get(k)).removePotionEffect(PotionEffectType.SPEED);
			getServer().getPlayer(players.get(k)).sendMessage(prefix + ChatColor.RED + "Server reloaded or Restarted. Teleported back to the Spawn.");
			getServer().getPlayer(players.get(k)).teleport(getServer().getPlayer(players.get(k)).getWorld().getSpawnLocation());
			getServer().getPlayer(players.get(k)).remove();

		}
		this.players.clear();
		getServer().getScheduler().cancelAllTasks();

	}

	// Vault Sachen :D
	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	public void load_config() {
		// Variablen zur config
		outOfWorld = getConfig().getInt("OutOfWorld", 5);
		Item = getConfig().getInt("Win.Item.Item", 264);
		Amount = getConfig().getInt("Win.Item.Amount", 1);
		Speed = getConfig().getInt("RunSpeed.Speed", 2);
		SpeedOverDiamondBlock = getConfig().getInt("RunSpeed.SpeedOverDiamondBlock", 4);
		HowMuchPoints = getConfig().getInt("BuyPoints.HowMuchPoints", 10);
		BuyItem = getConfig().getInt("BuyPoints.ButItem", 276);
		BuyAmount = getConfig().getInt("BuyPoints.ButAmount", 1);
		moneyuse = getConfig().getBoolean("Money.use", false);
		itemuse = getConfig().getBoolean("Win.Item.use", false);
		money_a = getConfig().getInt("Win.Money.Amount", 20);
	}

}