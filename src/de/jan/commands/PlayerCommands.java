package de.jan.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.jan.TempleRun;
import de.jan.arena.Arena;
import de.jan.manager.ArenaManager;
import de.jan.manager.PlayerManager;
import de.jan.manager.Util;

public class PlayerCommands implements CommandExecutor {
	
	private ArenaManager arenaManager;
	private PlayerManager playerManager;
	private String prefix = "§b[§6TempleRun§b] §7";
	
	public PlayerCommands() {
		arenaManager = TempleRun.arenaManager;
		playerManager = TempleRun.playerManager;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(cmd.getName().equalsIgnoreCase("templerun")) {
			
			// Checking if the sender is a player
			if(!(sender instanceof Player)) {
				TempleRun.debug("You are not a Player");
				return false;
			}
			
			final Player player = (Player) sender;
			
			if(args.length == 0) {
				player.sendMessage("§6==> §bTempleRun §52.0 §6<==");
				player.sendMessage("§c/tr §7= §fShow this help menu.");
				player.sendMessage("§c/tr create <NAME> §7= §fCreate a new Arena.");
				player.sendMessage("§c/tr delete <ARENA> §7= §fDelete a Arena.");
				player.sendMessage("§c/tr arenas §7= §fShow all available Arenas.");
				player.sendMessage("§c/tr changespawn <ARENA> §7= §fChange the spawn of a Arena.");
				player.sendMessage("§c/tr winitem add <ARENA> <ID:DATA:AMOUNT> §7= §fAdd an winitem to a Arena.");
				player.sendMessage("§c/tr join <ARENA> §7= §fJoin a Arena.");
				player.sendMessage("§c/tr leave §7= §fLeave a Arena.");
				player.sendMessage("§c/tr give <AMOUNT> §7= §fGive TempleRun Coins.");
				player.sendMessage("§c/tr permissions §7= §fShow all Permissions.");
				player.sendMessage("§c/tr ep §7= §fEn-/Disable Templerun coin pickup.");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("permissions")) {
				player.sendMessage("§6==> §bTempleRun §5Permissions §6<==");
				player.sendMessage("§c/tr §7= §fShow this help menu.");
				player.sendMessage("§a / ");
				player.sendMessage(" ");
				player.sendMessage("§c/tr create <NAME>");
				player.sendMessage("§atemplerun.create");
				player.sendMessage(" ");
				player.sendMessage("§c/tr delete <ARENA>");
				player.sendMessage("§atemplerun.delete");
				player.sendMessage(" ");
				player.sendMessage("§c/tr arenas");
				player.sendMessage("§a / ");
				player.sendMessage(" ");
				player.sendMessage("§c/tr changespawn <ARENA>");
				player.sendMessage("§atemplerun.changespawn");
				player.sendMessage(" ");
				player.sendMessage("§c/tr winitem add <ARENA> <ID:DATA:AMOUNT>");
				player.sendMessage("§atemplerun.winitem.add");
				player.sendMessage(" ");
				player.sendMessage("§c/tr join <ARENA>");
				player.sendMessage("§atemplerun.join");
				player.sendMessage(" ");
				player.sendMessage("§c/tr leave");
				player.sendMessage("§atemplerun.join");
				player.sendMessage(" ");
				player.sendMessage("§c/tr give <AMOUNT>");
				player.sendMessage("§atemplerun.give");
				player.sendMessage(" ");
				player.sendMessage("§c/tr ep");
				player.sendMessage("§atemplerun.pickup");
				return true;
			} else if(args[0].equalsIgnoreCase("create")) {
				
				if(args.length != 2) {
					player.sendMessage(prefix + "Wrong Usage. /templerun to see all the commands.");
					return false;
				}
				
				// Checking Permissions
				if(!(player.hasPermission("templerun.create"))) {
					player.sendMessage(prefix + "You dont have permissions.");
					return false;
				}
				
				String arenaname = args[1].toLowerCase();
				Arena arena = arenaManager.getArena(arenaname);
				if(arena != null) {
					player.sendMessage(prefix + "This arena already exists.");
					return false;
				}
				
				arenaManager.createArena(arenaname, player.getLocation());
				player.sendMessage(prefix + "Arena §e" + arenaname + " §7successful created.");
				return true;
			} else if(args[0].equalsIgnoreCase("changespawn")) {
				
				if(args.length != 2) {
					player.sendMessage(prefix + "Wrong Usage. /templerun to see all the commands.");
					return false;
				}
				
				// Checking Permissions
				if(!(player.hasPermission("templerun.changespawn"))) {
					player.sendMessage(prefix + "You dont have permissions.");
					return false;
				}
				
				String arenaname = args[1].toLowerCase();
				Arena arena = arenaManager.getArena(arenaname);
				if(arena == null) {
					player.sendMessage(prefix + "Arena not found => §e" + arenaname);
					return false;
				}
				
				arena.setLocation(player.getLocation());
				player.sendMessage(prefix + "Spawn of arena §e" + arenaname + " §7successful changed.");
				return true;
			} else if(args[0].equalsIgnoreCase("delete")) {
				
				if(args.length != 2) {
					player.sendMessage(prefix + "Wrong Usage. /templerun to see all the commands.");
					return false;
				}
				
				// Checking Permissions
				if(!(player.hasPermission("templerun.delete"))) {
					player.sendMessage(prefix + "You dont have permissions.");
					return false;
				}
				
				String arenaname = args[1].toLowerCase();
				Arena arena = arenaManager.getArena(arenaname);
				if(arena == null) {
					player.sendMessage(prefix + "Arena not found => §e" + arenaname);
					return false;
				}
				
				arena.delete();
				player.sendMessage(prefix + "Arena deleted. All players who run in this arena got kicked.");
				return true;
			} else if(args[0].equalsIgnoreCase("winitem")) {
				
				if(args.length != 4) {
					player.sendMessage(prefix + "Wrong Usage. /templerun to see all the commands.");
					return false;
				}
				
				// Checking Permissions
				if(!(player.hasPermission("templerun.winitem.add"))) {
					player.sendMessage(prefix + "You dont have permissions.");
					return false;
				}
				
				// Command: /templerun winitem add <ARENA> <ID:DATA:AMOUNT>
				if(args[1].equalsIgnoreCase("add")) {
					
					String arenaname = args[2].toLowerCase();
					Arena arena = arenaManager.getArena(arenaname);
					if(arena == null) {
						player.sendMessage(prefix + "Arena not found => §e" + arenaname);
						return false;
					}
					
					ItemStack is = Util.getItemStackFromInput(player, args[3]);
					
					if(is == null) {
						player.sendMessage(prefix + "Item not found.");
						return false;
					}
					
					arena.addWinItem(is);
					player.sendMessage(prefix + "Item successful added.");
					return true;
				} else
					player.sendMessage(prefix + "Argument not found. /templerun to see all the commands.");
			} else if(args[0].equalsIgnoreCase("join")) {
				
				if(args.length != 2) {
					player.sendMessage(prefix + "Wrong Usage. /templerun to see all the commands.");
					return false;
				}
				
				if(!(player.hasPermission("templerun.join"))) {
					player.sendMessage(prefix + "You dont have permissions.");
					return false;
				}
				
				if(playerManager.isPlaying(player.getName())) {
					player.sendMessage(prefix + "You already in a arena.");
					return false;
				}
				
				String arenaname = args[1].toLowerCase();
				Arena arena = arenaManager.getArena(arenaname);
				if(arena == null) {
					player.sendMessage(prefix + "Arena not found => §e" + arenaname);
					return false;
				}
				
				playerManager.join(player, arena);
				return true;
			} else if(args[0].equalsIgnoreCase("leave")) {
				
				if(args.length != 1) {
					player.sendMessage(prefix + "Wrong Usage. /templerun to see all the commands.");
					return false;
				}
				
				if(!(player.hasPermission("templerun.join"))) {
					player.sendMessage(prefix + "You dont have permissions.");
					return false;
				}
				
				if(!playerManager.isPlaying(player.getName())) {
					player.sendMessage(prefix + "You are not in a arena.");
					return false;
				}
				
				playerManager.leave(player, false);
				return true;
			} else if(args[0].equalsIgnoreCase("give")) {
				
				if(!(player.hasPermission("templerun.give"))) {
					player.sendMessage(prefix + "You dont have permissions.");
					return false;
				}
				
				if(args.length < 2 || args.length > 3) {
					player.sendMessage(prefix + "Wrong Usage. /templerun to see all the commands.");
					return false;
				}
				
				if(args[1].equalsIgnoreCase("coin")) {
					if(args.length == 2) {
						player.getInventory().addItem(Util.getTempleRunCoin(1));
					} else if(args.length >= 3) {
						
						if(!Util.isInteger(args[2])) {
							player.sendMessage(prefix + "The amount you want to have, have to be an number.");
							return false;
						}
						
						int amount = Integer.valueOf(args[2]);
						player.getInventory().addItem(Util.getTempleRunCoin(amount));
						return true;
					}
				} else if(args[1].equalsIgnoreCase("magnet")) {
					if(args.length == 2) {
						player.getInventory().addItem(Util.getMagnet(1));
					} else if(args.length >= 3) {
						
						if(!Util.isInteger(args[2])) {
							player.sendMessage(prefix + "The amount you want to have, have to be an number.");
							return false;
						}
						
						int amount = Integer.valueOf(args[2]);
						player.getInventory().addItem(Util.getMagnet(amount));
						return true;
					}
				} else {
					player.sendMessage(prefix + "Available items: §ecoin §7| §emagnet");
				}
			} else if(args[0].equalsIgnoreCase("arenas")) {
				
				// Check if there is some available arenas
				if(ArenaManager.arenas.isEmpty()) {
					player.sendMessage(prefix + "There are not any available TempleRun Arenas.");
					return false;
				}
				// Print out all available arenas
				StringBuilder builder = new StringBuilder();
				for(int i = 0; i < ArenaManager.arenas.size(); i++) {
					if (i != 0)
						builder.append("§f, ");
					builder.append("§9" + ArenaManager.arenas.get(i).getName());
				}
				player.sendMessage("§7Available Arenas: " + builder.toString());
				return true;
			} else if(args[0].equalsIgnoreCase("ep")) {
				
				if(!(player.hasPermission("templerun.pickup"))) {
					player.sendMessage(prefix + "You dont have permissions.");
					return false;
				}
				
				if(PlayerManager.pickUpCoins.contains(player.getName())) {
					player.sendMessage(prefix + "Templerun coin pickup disabled.");
					PlayerManager.pickUpCoins.remove(player.getName());
				} else {
					player.sendMessage(prefix + "Templerun coin pickup enabled.");
					PlayerManager.pickUpCoins.add(player.getName());
				}
				return true;
			}
			return true;
		}
		return false;
	}
}
