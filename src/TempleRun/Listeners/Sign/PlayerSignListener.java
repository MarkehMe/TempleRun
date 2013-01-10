package TempleRun.Listeners.Sign;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import TempleRun.TempleRun;
import TempleRun.Util.Util;

public class PlayerSignListener implements Listener {

	private static String prefix = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "[" + ChatColor.DARK_RED + "TempleRun" + ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "] " + ChatColor.GRAY;

	public TempleRun main;

	public PlayerSignListener(TempleRun main) {
		this.main = main;
	}

	@EventHandler
	public void onSignCreate(SignChangeEvent event) {

		if (event.getLine(0).equalsIgnoreCase("TempleRun")) {

			Player player = event.getPlayer();
			if (!player.hasPermission("templerun.sign.create")) {
				player.sendMessage(prefix + "You dont have Permissions!");
				event.getBlock().breakNaturally();
				return;
			} else {
				event.setLine(0, ChatColor.RED + "TempleRun");
				event.setLine(1, ChatColor.BLUE + "Finish");
				player.sendMessage(prefix + "Sign successful created!");
			}

		}

	}

	@EventHandler
	public void onSignIntract(PlayerInteractEvent event) {

		Player player = event.getPlayer();
		Block block = event.getClickedBlock();

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		Sign sign = null;

		if (event.getClickedBlock() != null) {
			if (event.getClickedBlock().getTypeId() == 63 || event.getClickedBlock().getTypeId() == 68) {
				sign = (Sign) block.getState();
			} else
				return;
		}

		if (sign.getLine(0).equalsIgnoreCase(ChatColor.RED + "TempleRun") && sign.getLine(1).equalsIgnoreCase(ChatColor.BLUE + "Finish")) {

			if (!player.hasPermission("templerun.sign.use")) {
				player.sendMessage(prefix + "You dont have Permissions!");
				return;
			}

			if (!Util.isPlaying(player.getName())) {
				player.sendMessage(prefix + "You dont play TempleRun at the Moment!");
				return;
			}

			Location loc = Util.getOldLocation(player.getName());

			long time = Util.getTime(player.getName()) / 1000;
			long now = System.currentTimeMillis() / 1000;
			long ergebnis = now - time;
			
			int coins = Util.blocks.get(player.getName()).size();

			Util.teleport(player, loc);
			Util.removePlayer(player.getName());
			
			Util.sendWinItems(player);
			
			player.sendMessage(prefix + "You finished TempleRun!");
			player.sendMessage(ChatColor.GRAY + "You Time: " + ChatColor.GREEN + ergebnis + " seconds!");
			player.sendMessage(ChatColor.GRAY + "Recieved Coins Amount: " + ChatColor.GREEN + coins);
		}
	}

}
