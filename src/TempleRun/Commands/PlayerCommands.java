package TempleRun.Commands;

import java.util.ArrayList;
import java.util.LinkedHashMap;

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
				main.cload.save();

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

					if (!main.iutil.hasScore(player.getName())) {
						player.sendMessage(Util.replace(config.getString("Messages.Info.noScore")));
						return true;
					}

					int time = main.cload.getConfig().getInt("Players." + player.getName() + ".Time");
					int coins = main.cload.getConfig().getInt("Players." + player.getName() + ".Coins");
					player.sendMessage(Util.replace(main.getConfig().getString("Messages.prefix")) + " Your Score:");
					player.sendMessage(main.iutil.replace(main.getConfig().getString("Messages.Info.score"), "" + coins, "" + time));
				} else if (args.length == 2) {
					
					if(!main.iutil.hasScore(args[1])) {
						player.sendMessage(Util.replace(main.getConfig().getString("Messages.Info.other_no_score")));
						return true;
					}
					int time = main.cload.getConfig().getInt("Players." + args[1] + ".Time");
					int coins = main.cload.getConfig().getInt("Players." + args[1] + ".Coins");
					player.sendMessage(Util.replace(main.getConfig().getString("Messages.prefix")) + " Score of §c" + args[1] + "§7:");
					player.sendMessage(main.iutil.replace(main.getConfig().getString("Messages.Info.score"), "" + coins, "" + time));
					
				} else {
					player.sendMessage(Util.replace(config.getString("Messages.wrong_usage")));
				}

			} else if (args[0].equalsIgnoreCase("topten")) {
				player.sendMessage("In process. Coming soon.");
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

				player.sendMessage(Util.replace(main.getConfig().getString("Messages.prefix")) + " Arenas: " + builder.toString());

			} else if(args[0].equalsIgnoreCase("update")) { 
				
				if(!player.hasPermission("templerun.update") || !player.getName().equalsIgnoreCase("xapfeltortexp")) {
					player.sendMessage(Util.replace(main.getConfig().getString("Messages.no_Permissions")));
					return true;
				}
				
				if(main.updateAvailable) {
					player.sendMessage(Util.replace(main.getConfig().getString("Messages.prefix")) + " There is an Update available!");
				} else {
					player.sendMessage(Util.replace(main.getConfig().getString("Messages.prefix")) + " Your TempleRun is up to date!");
				}
				
			} else {
				player.sendMessage(Util.replace(config.getString("Messages.argument_not_found")));
			}
		}
		return false;
	}
}
