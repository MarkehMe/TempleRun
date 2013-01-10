package TempleRun.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import TempleRun.TempleRun;
import TempleRun.Util.Util;

public class PlayerCommands implements CommandExecutor {

	/* Prefix */
	private static String prefix = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "[" + ChatColor.DARK_RED + "TempleRun" + ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "] " + ChatColor.GRAY;
	private String noPerms = prefix + "You dont have Permissions!";

	public TempleRun main;
	public int task;

	public PlayerCommands(TempleRun main) {
		this.main = main;
	}

	@Override
	 public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		 
		 if(cmd.getName().equalsIgnoreCase("templerun")) {
			 
			 /* Schauen ob der sender ein Spieler ist */
			 if(!(sender instanceof Player)) {
				 sender.sendMessage(prefix + "You are not a Player!");
				 return true;
			 }
			 
			 /* Player Objekt zuordnen */
			 final Player player = (Player) sender;
			 
			 /* Permissions */
			 if(!player.hasPermission("templerun.execute")) {
				 player.sendMessage(noPerms);
				 return true;
			 }
			 
			 /* Argumente checken */
			 if(args.length == 0) {
				 
				 Util.helpMenu(player);
				 
				 return true;
			 } else if(args[0].equalsIgnoreCase("join")) {
				 
				 if(!Util.isAllSet(main)) {
					 player.sendMessage(prefix + "Please set all Spawns!");
					 return true;
				 }
				 
				 if(!Util.isRunning()) {
					 player.sendMessage(prefix + "TempleRun is stopped at the moment!");
					 return true;
				 }
				 
				 if(Util.isPlaying(player.getName())) {
					 player.sendMessage(prefix + "You are already in TempleRun!");
					 return true;
				 }
				 
				 long time = System.currentTimeMillis();
				 
				 Util.addPlayer(player.getName(), time, player);
				 Util.teleport(player, Util.getSpawnLocation(main));
				 player.sendMessage(prefix + "You joined TempleRun! Good Luck!");
				 player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2400000, 2));
				 
			 } else if(args[0].equalsIgnoreCase("leave")) {
				 
				 if(!Util.isPlaying(player.getName())) {
					 player.sendMessage(prefix + "You are not in TempleRun!");
					 return true;
				 }
				 
				 Location loc = Util.getOldLocation(player.getName());
				 Util.teleport(player, loc);
				 Util.removePlayer(player.getName());
				 player.sendMessage(prefix + "You leaved TempleRun and got teleported back to your old Location!");
				 player.removePotionEffect(PotionEffectType.SPEED);
			 
			 } else if(args[0].equalsIgnoreCase("set")) {
				 
				 if(!player.hasPermission("templerun.set")) {
					 player.sendMessage(noPerms);
					 return true;
				 }
				 
				 if(args.length == 2) {
					 
					 if(args[1].equalsIgnoreCase("spawn")) {
						 
						 Util.setSpawnLocation(main, player);
						 player.sendMessage(prefix + "TempleRun SpawnLocation set!");
						 
					 } else {
						 player.sendMessage(prefix + "Argument not found!");
					 }
					 
				 } else {
					 player.sendMessage(prefix + "Wrong Usage!");
				 }
			 } else if(args[0].equalsIgnoreCase("stop")) {
				 
				 if(!player.hasPermission("templerun.stop")) {
					 player.sendMessage(noPerms);
					 return true;
				 }
				 
				 if(args.length == 1) {
					 
					 Util.stopGame(main.getConfig().getBoolean("TempleRun.KickPlayers"));
					 player.sendMessage(prefix + "You stopped TempleRun!");
					 
				 } else {
					 player.sendMessage(prefix + "Wrong Usage!");
				 }
			 } else if(args[0].equalsIgnoreCase("start")) {
				 
				 if(!player.hasPermission("templerun.start")) {
					 player.sendMessage(noPerms);
					 return true;
				 }
				 
				 if(args.length == 1) {
					 
					 if(Util.isRunning()) {
						 player.sendMessage(prefix + "TempleRun is already Running!");
						 return true;
					 }
					 
					 Util.startGame();
					 player.sendMessage(prefix + "You started TempleRun!");
				 }
			 } else if(args[0].equalsIgnoreCase("kick")) {
				 
				 if(!player.hasPermission("templerun.kick")) {
					 player.sendMessage(noPerms);
					 return true;
				 }
				 
				 if(args.length == 2) {
					 
					 Player p = main.getServer().getPlayer(args[1]);
					 
					 if(p == null) {
						 player.sendMessage(prefix + "Player " + ChatColor.GREEN + args[1] + ChatColor.GRAY + " not found!");
						 return true;
					 } else {
						 
						 String pname = p.getName();
						 
						 if(!Util.isPlaying(pname)) {
							 player.sendMessage(prefix + "This Player is not in TempleRun!");
							 return true;
						 }
						 
						 Util.kickPlayer(p);
						 player.sendMessage(prefix + "You kicked " + ChatColor.GREEN + pname + ChatColor.GRAY + " out of TempleRun!");
						 return true;
					 }
					 
				 } else {
					 player.sendMessage(prefix + "Wrong Usage!");
				 }
				 
			 } else {
				 Util.helpMenu(player);
			 }
			 
		 }
		 return false;
	 }
}
