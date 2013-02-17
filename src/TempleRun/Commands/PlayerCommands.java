package TempleRun.Commands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import TempleRun.TempleRun;
import TempleRun.Util.Util;

public class PlayerCommands implements CommandExecutor {

	/* Prefix */
	private static String prefix = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "[" + ChatColor.DARK_RED + "TempleRun" + ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "] " + ChatColor.GRAY;
	
	LinkedHashMap<String, Integer> topten = new LinkedHashMap<String, Integer>();

	public TempleRun main;
	private FileConfiguration config;

	public PlayerCommands(TempleRun main) {
		this.main = main;
		this.config = main.getConfig();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (cmd.getName().equalsIgnoreCase("templerun")) {

			/* Schauen ob der sender ein Spieler ist */
			if (!(sender instanceof Player)) {
				sender.sendMessage(prefix + "You are not a Player!");
				return true;
			}

			/* Player Objekt zuordnen */
			final Player player = (Player) sender;

			/* Argumente checken */
			if (args.length == 0) {

				Util.helpMenu(player);

				return true;
			} else if (args[0].equalsIgnoreCase("join")) {

				/* Permissions */
				if (!player.hasPermission("templerun.play")) {
					player.sendMessage(Util.replace(config.getString("Messages.no_Permissions")));
					return true;
				}

				if (args.length != 2) {
					player.sendMessage(Util.replace(config.getString("Messages.wrong_usage")));
					return true;
				}

				if (!Util.isAllSet(main, args[1])) {
					player.sendMessage(Util.replace(config.getString("Messages.Join.arena_not_found"), args[1]));
					return true;
				}

				if (!Util.isRunning()) {
					player.sendMessage(Util.replace(config.getString("Messages.Join.is_stopped")));
					return true;
				}

				if (Util.isPlaying(player.getName())) {
					player.sendMessage(Util.replace(config.getString("Messages.Join.already_in_tr")));
					return true;
				}

				long time = System.currentTimeMillis();

				Util.addPlayer(player.getName(), time, player, args[1]);
				Util.saveOldLoc(player);

				Util.teleport(player, Util.getSpawnLocation(main, args[1]));
				player.sendMessage(Util.replace(config.getString("Messages.Join.join_tr"), args[1]));
				player.removePotionEffect(PotionEffectType.SPEED);
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2400000, 2));

				if (player.hasPermission("templerun.pickup") && main.pickup) {
					player.sendMessage(Util.replace(config.getString("Messages.Join.attention_pickup")));
				}

				main.cload.load();

				if (main.getConfigLoader().getString("Players." + player.getName()) == null) {
					main.getConfigLoader().set("Players." + player.getName(), "0:0");
					main.cload.save();
					return true;
				}

			} else if (args[0].equalsIgnoreCase("leave")) {

				/* Permissions */
				if (!player.hasPermission("templerun.play")) {
					player.sendMessage(Util.replace(config.getString("Messages.no_Permissions")));
					return true;
				}

				if (!Util.isPlaying(player.getName())) {
					player.sendMessage(Util.replace(config.getString("Messages.Leave.not_in_tr")));
					return true;
				}

				Location loc = Util.getOldLocation(player.getName());
				Util.teleport(player, loc);
				Util.removePlayer(player.getName());
				Util.oldLoc.remove(player.getName());

				if (Util.checkpoint.containsKey(player.getName()))
					Util.checkpoint.remove(player.getName());

				Util.arenaname.remove(player.getName());

				player.sendMessage(Util.replace(config.getString("Messages.Leave.leave_tr")));
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5, 1));
				player.removePotionEffect(PotionEffectType.SPEED);

			} else if (args[0].equalsIgnoreCase("set")) {

				if (!player.hasPermission("templerun.set")) {
					player.sendMessage(Util.replace(config.getString("Messages.no_Permissions")));
					return true;
				}

				if (args.length == 2) {

					Util.setSpawnLocation(main, player, args[1]);
					player.sendMessage(Util.replace(config.getString("Messages.Set.spawn_set"), args[1]));

				} else {
					player.sendMessage(Util.replace(config.getString("Messages.wrong_usage")));
				}
			} else if (args[0].equalsIgnoreCase("stop")) {

				if (!player.hasPermission("templerun.stop")) {
					player.sendMessage(Util.replace(config.getString("Messages.no_Permissions")));
					return true;
				}
				
				if (!Util.isRunning()) {
					player.sendMessage(Util.replace(config.getString("Messages.Stop.is_stopped")));
					return true;
				}

				if (args.length == 1) {

					Util.stopGame(main.getConfig().getBoolean("TempleRun.KickPlayers"));
					player.sendMessage(Util.replace(config.getString("Messages.Stop.stop")));

				} else {
					player.sendMessage(Util.replace(config.getString("Messages.wrong_usage")));
				}
			} else if (args[0].equalsIgnoreCase("start")) {

				if (!player.hasPermission("templerun.start")) {
					player.sendMessage(Util.replace(config.getString("Messages.no_Permissions")));
					return true;
				}

				if (args.length == 1) {

					if (Util.isRunning()) {
						player.sendMessage(Util.replace(config.getString("Messages.Start.is_started")));
						return true;
					}

					Util.startGame();
					player.sendMessage(Util.replace(config.getString("Messages.Start.start")));
				}
			} else if (args[0].equalsIgnoreCase("kick")) {

				if (!player.hasPermission("templerun.kick")) {
					player.sendMessage(Util.replace(config.getString("Messages.no_Permissions")));
					return true;
				}

				if (args.length == 2) {

					Player p = main.getServer().getPlayer(args[1]);

					if (p == null) {
						player.sendMessage(Util.replace(config.getString("Messages.Kick.offline"), args[1]));
						return true;
					} else {

						if (!Util.isPlaying(p.getName())) {
							player.sendMessage(Util.replace(config.getString("Messages.Kick.not_in_tr")));
							return true;
						}

						Util.kickPlayer(p);
						player.sendMessage(Util.replace(config.getString("Messages.Kick.kick_player"), p));
						return true;
					}

				} else {
					player.sendMessage(Util.replace(config.getString("Messages.wrong_usage")));
				}

			} else if (args[0].equalsIgnoreCase("give")) {

				if (!player.hasPermission("templerun.give")) {
					player.sendMessage(Util.replace(config.getString("Messages.no_Permissions")));
					return true;
				}

				if (args.length == 2) {

					try {
						Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						player.sendMessage(Util.replace(config.getString("Messages.Give.only_numbers")));
					}

					ArrayList<String> desc = new ArrayList<String>();
					desc.add("§6Coin in TempleRun");

					ItemStack is = new ItemStack(Material.GOLD_NUGGET, Integer.parseInt(args[1]));
					ItemMeta im = is.getItemMeta();
					im.setDisplayName("§4§lTR-§c§lCoin");
					im.setLore(desc);

					is.setItemMeta(im);

					player.getInventory().addItem(is);
					player.sendMessage(Util.replace(config.getString("Messages.Give.give_coins"), Integer.valueOf(args[1])));

				} else {
					player.sendMessage(Util.replace(config.getString("Messages.wrong_usage")));
				}
			} else if (args[0].equalsIgnoreCase("info")) {

				if (args.length == 1) {

					if (main.getConfigLoader().getString("Players." + player.getName()) == null) {
						player.sendMessage(Util.replace(main.getConfig().getString("Messages.prefix")) + "You never played or won TempleRun before!");
						return true;
					}

					try {

						String playerdata = main.getConfigLoader().getString("Players." + player.getName());
						String[] splitted = playerdata.split(":");

						String time = splitted[0];
						String besttime = main.getConfigLoader().getString("ServerBest.Time");
						String coins = splitted[1];
						String bestcoins = main.getConfigLoader().getString("ServerBest.Coins");

						player.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "[" + ChatColor.DARK_RED + "TempleRun §c§lInfo" + ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "]");
						player.sendMessage(ChatColor.GRAY + "Your best time: §c§l" + time + " sec");
						player.sendMessage(ChatColor.GRAY + "Your best coins amount: §c§l" + coins);
						player.sendMessage(ChatColor.GRAY + "Server best time: §c§l" + besttime + " sec");
						player.sendMessage(ChatColor.GRAY + "Server best coins: §c§l" + bestcoins);
					} catch (NullPointerException e) {
						player.sendMessage(Util.replace(main.getConfig().getString("Messages.prefix")) + "There was an NullPointerException!");
					}

				} else {
					player.sendMessage(Util.replace(config.getString("Messages.wrong_usage")));
				}
			} else if (args[0].equalsIgnoreCase("topten")) {

				for (Entry<String, Object> s : main.getConfigLoader().getValues(true).entrySet()) {
					if (String.valueOf(s).startsWith("Players.")) {

						String e = String.valueOf(s).replace(":", " ").replace("=", " ").replace(".", " ");
						String[] splitted = e.split(" ");

						String name = splitted[1];
						String coins = splitted[2];

						topten.put(name, Integer.valueOf(coins));
					}
				}
				topten = sortMap(topten);

				try {
					// Get the TopTen Players
					String top1Player = (String) topten.keySet().toArray()[0];
					Integer top1Value = topten.get(top1Player);
					player.sendMessage("§41§7. §c§o" + top1Player + "  §7||  §c§o" + top1Value);

					String top2Player = (String) topten.keySet().toArray()[1];
					Integer top2Value = topten.get(top2Player);
					player.sendMessage("§42§7. §c§o" + top2Player + "  §7||  §c§o" + top2Value);

					String top3Player = (String) topten.keySet().toArray()[2];
					Integer top3Value = topten.get(top3Player);
					player.sendMessage("§43§7. §c§o" + top3Player + "  §7||  §c§o" + top3Value);

					String top4Player = (String) topten.keySet().toArray()[3];
					Integer top4Value = topten.get(top4Player);
					player.sendMessage("§44§7. §c§o" + top4Player + "  §7||  §c§o" + top4Value);

					String top5Player = (String) topten.keySet().toArray()[4];
					Integer top5Value = topten.get(top5Player);
					player.sendMessage("§45§7. §c§o" + top5Player + "  §7||  §c§o" + top5Value);

					String top6Player = (String) topten.keySet().toArray()[5];
					Integer top6Value = topten.get(top6Player);
					player.sendMessage("§46§7. §c§o" + top6Player + "  §7||  §c§o" + top6Value);

					String top7Player = (String) topten.keySet().toArray()[6];
					Integer top7Value = topten.get(top7Player);
					player.sendMessage("§47§7. §c§o" + top7Player + "  §7||  §c§o" + top7Value);

					String top8Player = (String) topten.keySet().toArray()[7];
					Integer top8Value = topten.get(top8Player);
					player.sendMessage("§48§7. §c§o" + top8Player + "  §7||  §c§o" + top8Value);

					String top9Player = (String) topten.keySet().toArray()[8];
					Integer top9Value = topten.get(top9Player);
					player.sendMessage("§49§7. §c§o" + top9Player + "  §7||  §c§o" + top9Value);

					String top10Player = (String) topten.keySet().toArray()[9];
					Integer top10Value = topten.get(top10Player);
					player.sendMessage("§410§7. §c§o" + top10Player + "  §7||  §c§o" + top10Value);
				} catch (Exception e) {
					player.sendMessage(Util.replace(main.getConfig().getString("Messages.prefix")) + "There are no TopTen Players at the Moment!");
				}

				topten.clear();
			} else if (args[0].equalsIgnoreCase("pickup")) {

				if (!player.hasPermission("templerun.pickup")) {
					player.sendMessage(Util.replace(config.getString("Messages.no_Permissions")));
					return true;
				}

				if (args.length != 1) {
					player.sendMessage(Util.replace(config.getString("Messages.wrong_usage")));
					return true;
				}

				if (main.pickup) {
					player.sendMessage(Util.replace(config.getString("Messages.Pickup.disable")));
					main.pickup = false;
				} else {
					main.pickup = true;
					player.sendMessage(Util.replace(config.getString("Messages.Pickup.enable")));
				}

			} else if (args[0].equalsIgnoreCase("delete")) {

				if (!player.hasPermission("templerun.delete")) {
					player.sendMessage(Util.replace(config.getString("Messages.no_Permissions")));
					return true;
				}

				if (args.length != 2) {
					player.sendMessage(Util.replace(config.getString("Messages.wrong_usage")));
					return true;
				}

				if (!Util.isAllSet(main, args[1])) {
					player.sendMessage(Util.replace(config.getString("Messages.Delete.no_found"), args[1]));
					return true;
				}

				Util.deleteArena(args[1]);
				player.sendMessage(Util.replace(config.getString("Messages.Delete.delete"), args[1]));

			} else if (args[0].equalsIgnoreCase("arenas")) {

				if (main.getConfig().getString("TempleRun.Arenas") == null || main.getConfig().getStringList("TempleRun.Arenas").isEmpty()) {
					player.sendMessage(Util.replace(main.getConfig().getString("Messages.prefix")) + "No Arenas found!");
					return true;
				}

				StringBuilder builder = new StringBuilder();

				for (int i = 0; i < Util.getArenas().size(); i++) {
					if (i != 0)
						builder.append("§7, ");
					builder.append("§4" + Util.getArenas().get(i));
				}

				player.sendMessage(Util.replace(main.getConfig().getString("Messages.prefix")) + "Arenas: " + builder.toString());

			} else {
				player.sendMessage(Util.replace(config.getString("Messages.argument_not_found")));
			}
		}
		return false;
	}

	/**
	 * Convert my sortedList into an Big to Low sorted List
	 * 
	 * @param map
	 * @return
	 */
	private LinkedHashMap<String, Integer> sortMap(Map<String, Integer> map) {
		SortedSet<Map.Entry<String, Integer>> sortedEntries = new TreeSet<Map.Entry<String, Integer>>(new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
				int res = e2.getValue().compareTo(e1.getValue());
				return res != 0 ? res : 1;
			}
		});
		sortedEntries.addAll(map.entrySet());
		LinkedHashMap<String, Integer> sorted_map = new LinkedHashMap<String, Integer>();
		for (Entry<String, Integer> e : sortedEntries) {
			sorted_map.put(e.getKey(), e.getValue());
		}
		return sorted_map;
	}
}
