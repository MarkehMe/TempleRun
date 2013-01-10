package TempleRun.Util;

import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

import TempleRun.TempleRun;

public class Util {

	public static TempleRun main;
	public static String prefix = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "[" + ChatColor.DARK_RED + "TempleRun" + ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "] " + ChatColor.GRAY;

	public Util(TempleRun main) {
		Util.main = main;
	}

	public static HashMap<String, Long> players = new HashMap<String, Long>();
	public static HashMap<String, Location> oldLoc = new HashMap<String, Location>();
	public static HashMap<Integer, Integer> firstblock = new HashMap<Integer, Integer>();
	public static HashMap<String, HashMap<Integer, Integer>> blocks = new HashMap<String, HashMap<Integer, Integer>>();
	public static HashMap<String, HashMap<Integer, Integer>> diamond = new HashMap<String, HashMap<Integer, Integer>>();
	public static HashMap<String, Integer> coins = new HashMap<String, Integer>();

	/* Boolean damit das dem Spiel joinen kann */
	public static boolean game = true;

	/* Schauen ob der Spieler gerade TempleRun nutzt */
	public static boolean isPlaying(String player) {
		if (players.containsKey(player))
			return true;
		return false;
	}

	/* Den Spieler zu TempleRun hinzufügen */
	public static void removePlayer(String player) {
		players.remove(player);
		oldLoc.remove(player);
		blocks.remove(player);
		diamond.remove(player);
		firstblock.clear();
		coins.remove(player);
	}

	/* Den Spieler aus TempleRun herraus schmeisen */
	public static void addPlayer(String player, long time, Player p) {
		players.put(player, time);
		oldLoc.put(player, p.getLocation());
		blocks.put(player, firstblock);
		diamond.put(player, firstblock);
		coins.put(player, 0);
	}

	/* Spieler zu TempleRun teleportieren */
	public static void teleport(Player player, Location loc) {
		player.teleport(loc);
	}

	/* Spawn Location bekommen */
	public static Location getSpawnLocation(Plugin plugin) {

		Location loc = null;

		if (isAllSet(main)) {

			String world = plugin.getConfig().getString("TempleRun.Spawn.World");
			double x = plugin.getConfig().getDouble("TempleRun.Spawn.X");
			double y = plugin.getConfig().getDouble("TempleRun.Spawn.Y");
			double z = plugin.getConfig().getDouble("TempleRun.Spawn.Z");
			double yaw = plugin.getConfig().getDouble("TempleRun.Spawn.Yaw");
			double pitch = plugin.getConfig().getDouble("TempleRun.Spawn.Pitch");

			loc = new Location(plugin.getServer().getWorld(world), x, y, z, (float) yaw, (float) pitch);
			return loc;
		}

		return loc;
	}

	/* Die Zeit von dem Spieler bekommen */
	public static long getTime(String player) {
		return players.get(player);
	}

	/* Das Spiel so stoppen das keiner joinen kann */
	public static void stopGame(boolean kickall) {
		Util.game = false;

		if (kickall)
			kickAll(main, prefix + "TempleRun got stopped and all Players kicked too!");
	}

	/* Das Spiel wieder starten */
	public static void startGame() {
		Util.game = true;
	}

	/* Schauen ob das Spiel an ist */
	public static boolean isRunning() {
		if (Util.game)
			return true;
		return false;
	}

	/* Bekommt wieviele Leute in TempleRun sind */
	public static int getSize() {
		return players.size();
	}

	/* TempleRun stoppen */
	public static void kickAll(Plugin plugin, String message) {

		for (Player p : plugin.getServer().getOnlinePlayers()) {

			String s = p.getName();
			if (isPlaying(s)) {
				
				p.removePotionEffect(PotionEffectType.SPEED);
				
				Location loc = getOldLocation(s);
				teleport(p, loc);
				removePlayer(s);
				
				p.sendMessage(message);
			}
		}
		players.clear();
		oldLoc.clear();

	}

	/* Set TempleRun Spawn */
	public static void setSpawnLocation(Plugin plugin, Player player) {

		String world = player.getLocation().getWorld().getName();
		double x = player.getLocation().getX();
		double y = player.getLocation().getY() + 1;
		double z = player.getLocation().getZ();
		float yaw = player.getLocation().getYaw();
		float pitch = player.getLocation().getPitch();

		plugin.getConfig().set("TempleRun.Spawn.World", world);
		plugin.getConfig().set("TempleRun.Spawn.X", x);
		plugin.getConfig().set("TempleRun.Spawn.Y", y);
		plugin.getConfig().set("TempleRun.Spawn.Z", z);
		plugin.getConfig().set("TempleRun.Spawn.Yaw", yaw);
		plugin.getConfig().set("TempleRun.Spawn.Pitch", pitch);
		plugin.saveConfig();
	}

	public static Location getOldLocation(String player) {
		return oldLoc.get(player);
	}

	/* Sind alle Location gesetzt ? */
	public static boolean isAllSet(Plugin plugin) {
		if (plugin.getConfig().getString("TempleRun.Spawn.World") != null)
			return true;
		return false;
	}
	
	/* Kickt einen Spieler */
	public static void kickPlayer(Player player) {
		
		Location loc = getOldLocation(player.getName());
		teleport(player, loc);
		
		removePlayer(player.getName());
	}
	
	public static void addLocation(String player, int x, int z) {
		blocks.get(player).put(x, z);
		int oldCoins = coins.get(player);
		coins.put(player, oldCoins + 1);
	}
	
	public static void addDiamondBlock(String player, int x, int z) {
		diamond.get(player).put(x, z);
	}
	
	public static boolean isWalkedDiamond(String player, int x, int z) {
		if(diamond.get(player).containsKey(x) && diamond.get(player).containsValue(z))
			return true;
		return false;
	}
	
	public static boolean isWalkedOver(String player, int x, int z) {	
		if(blocks.get(player).containsKey(x) && blocks.get(player).containsValue(z))
			return true;
		return false;
	}
	
	public static void helpMenu(Player player) {
		 player.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "[" + ChatColor.DARK_RED + "TempleRun by §c§lxapfeltortexp" + ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "]");
		 player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "● §7/tr join             " + ChatColor.DARK_GRAY + "= " + ChatColor.GRAY + "Join TempleRun");
		 player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "● §7/tr leave           " + ChatColor.DARK_GRAY + "= " + ChatColor.GRAY + "Leave TempleRun");
		 player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "● §7/tr set spawn     " + ChatColor.DARK_GRAY + "= " + ChatColor.GRAY + "Set the TempleRun Spawn");
		 player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "● §7/tr stop            " + ChatColor.DARK_GRAY + "= " + ChatColor.GRAY + "Stop TempleRun");
		 player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "● §7/tr start           " + ChatColor.DARK_GRAY + "= " + ChatColor.GRAY + "Start TempleRun");
		 player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "● §7/tr kick [PLAYER] " + ChatColor.DARK_GRAY + "= " + ChatColor.GRAY + "Kick Player out of TempleRun");
	}
	
	public static void sendWinItems(Player player) {
		
		List<String> items = main.getConfig().getStringList("TempleRun.WinItem");
		
		if(items == null) {
			return;
		}
		
		for(String s : items) {
			
			if(!s.contains(",")) {
				continue;
			} else {
				String[] all = s.split(",");
				
				int item = 0, amount = 0;
				
				try {
					item = Integer.parseInt(all[0]);
					amount = Integer.parseInt(all[1]);
				} catch(NumberFormatException e) {
					e.printStackTrace();
				}
				
				player.getInventory().addItem(new ItemStack(item, amount));
			}
			
		}
	
	}
	
}
