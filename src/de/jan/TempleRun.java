package de.jan;

import java.io.File;
import java.io.IOException;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import de.jan.commands.PlayerCommands;
import de.jan.listeners.PlayerListener;
import de.jan.loader.ConfigLoader;
import de.jan.manager.ArenaManager;
import de.jan.manager.GhostManager;
import de.jan.manager.MagnetManager;
import de.jan.manager.PlayerManager;
import de.jan.manager.Util;
import de.jan.manager.WalkCheckerManager;
import de.jan.trplayer.TRPlayer;

public class TempleRun extends JavaPlugin {

	static {
		new File("plugins/TempleRun").mkdirs();
	}

	// Initialisierung
	public static TempleRun instance;
	public static GhostManager ghostManager;
	public static ArenaManager arenaManager;
	public static PlayerManager playerManager;
	public static WalkCheckerManager walkManager;
	public static MagnetManager magnetManager;
	
	// Vault
	public Economy economy = null;
	
	// Datbase
	public static FileConfiguration database;

	@Override
	public void onEnable() {

		loadConfig();

		// Initialisierung
		instance = this;

		try {
			ConfigLoader loader = new ConfigLoader("plugins/TempleRun/", "database.yml", "/resources/database.yml");

			if (database == null)
				database = loader.getConfig();

			debug("Config successful created / loaded.");

		} catch (Exception e) {
			debug("An Error occurred while creating / loading database.yml");
		}

		ghostManager = new GhostManager(instance);

		arenaManager = new ArenaManager(database);
		arenaManager.loadArenas();

		playerManager = new PlayerManager();

		new Util();

		// Commands + Listener
		getCommand("templerun").setExecutor(new PlayerCommands());
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		
		walkManager = new WalkCheckerManager();
		magnetManager = new MagnetManager();
		
		// Start Metrics
		startMetrics();
		checkUpdates();

		if (getVault() == null) {
			debug("Vault Plugin not found.");
		} else {
			debug("Hocked into Vault.");
		}

		debug("Plugin completly loaded.");
	}

	@Override
	public void onDisable() {
		for (TRPlayer tr : PlayerManager.players) {
			tr.restoreInventory();
			tr.getPlayer().teleport(tr.getOldlocation());
			
			// Give the player the old speed back
			Player player = tr.getPlayer();
			player.setWalkSpeed(.2F);
		}
		
		PlayerManager.players.clear();
		ghostManager.clearGhosts();
		
		debug("Plugin completly disabled.");
	}

	public static void debug(String msg) {
		System.out.println("[TempleRun] " + msg);
	}

	private void loadConfig() {
		if (new File(getDataFolder() + "/config.yml").exists()) {
			getConfig().options().copyDefaults(true);
			debug("New config.yml created.");
		} else {
			saveDefaultConfig();
			debug("Config loaded.");
		}
	}

	private void startMetrics() {
		try {
			Metrics ms = new Metrics(this);
			ms.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void checkUpdates() {
		if (getConfig().getBoolean("TempleRun.Updater.checkUpdates", false)) {
			Updater updater = new Updater(this, "templerun", this.getFile(), Updater.UpdateType.DEFAULT, true);
			if (updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE) {
				debug("An new Update is available.");
			} else {
				debug("Everything is up to date.");
			}
		}
	}

	public Vault getVault() {
		Plugin plugin = getServer().getPluginManager().getPlugin("Vault");
		if (plugin == null || !(plugin instanceof Vault)) {
			return null;
		}
		if (!getConfig().getBoolean("TempleRun.Vault.usingVault", false)) {
			return null;
		}
		setupEconomy();
		return (Vault) plugin;
	}

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		return (economy != null);
	}
}
