package TempleRun.Listeners.Sign;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import TempleRun.TempleRun;
import TempleRun.Util.Util;

public class PlayerSignListener implements Listener {

	public TempleRun main;
	private FileConfiguration config;

	public PlayerSignListener(TempleRun main) {
		this.main = main;
		this.config = main.getConfig();
	}

	@EventHandler
	public void onSignCreate(SignChangeEvent event) {

		if (event.getLine(0).equalsIgnoreCase("TempleRun")) {

			Player player = event.getPlayer();
			if (!player.hasPermission("templerun.sign.create")) {
				player.sendMessage(Util.replace(config.getString("Messages.no_Permissions")));
				event.getBlock().breakNaturally();
				return;
			} else {
				event.setLine(0, ChatColor.RED + "TempleRun");
				event.setLine(1, ChatColor.BLUE + "Finish");
				player.sendMessage(Util.replace(config.getString("Messages.Sign.successful")));
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

			/* Permissions */
			if (!player.hasPermission("templerun.play")) {
				player.sendMessage(Util.replace(config.getString("Messages.no_Permissions")));
				return;
			}

			if (!Util.isPlaying(player.getName())) {
				player.sendMessage(Util.replace(config.getString("Messages.prefix")) + "You dont play TempleRun at the Moment!");
				return;
			}

			Location loc = Util.getOldLocation(player.getName());

			long time = Util.getTime(player.getName()) / 1000;
			long now = System.currentTimeMillis() / 1000;
			long ergebnis = now - time;

			int coins = Util.coins.get(player.getName());

			Util.teleport(player, loc);
			Util.removePlayer(player.getName());

			if (Util.checkpoint.containsKey(player.getName()))
				Util.checkpoint.remove(player.getName());

			Util.sendWinItems(player);

			if (main.getServer().getScheduler().isCurrentlyRunning(main.task)) {
				main.getServer().getScheduler().cancelTask(main.task);
			}

			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2400000, 2));
			player.removePotionEffect(PotionEffectType.SPEED);

			player.sendMessage(Util.replace(config.getString("Messages.prefix")) + "You finished TempleRun!");
			player.sendMessage(ChatColor.GRAY + "Your Time: " + ChatColor.GREEN + ergebnis + " seconds!");
			player.sendMessage(ChatColor.GRAY + "Recieved Coins Amount: " + ChatColor.GREEN + coins);
			
			Util.arenaname.remove(player.getName());
			
			main.cload.load();

			if (main.getConfigLoader().getString("Players." + player.getName()) == null) {
				main.getConfigLoader().set("Players." + player.getName(), ergebnis + ":" + coins);
				main.saveConfigLoader();
			} else {

				String name = main.getConfigLoader().getString("Players." + player.getName());
				String[] splitted = name.split(":");

				try {

					Integer.valueOf(splitted[0]);
					Integer.valueOf(splitted[1]);

				} catch (NumberFormatException e) {
					return;
				}

				int oldcoins = Integer.valueOf(splitted[1]);
				int oldtime = Integer.valueOf(splitted[0]);

				if (oldcoins < coins && oldtime > ergebnis) {
					main.getConfigLoader().set("Players." + player.getName(), oldtime + ":" + coins);
				} else if (oldcoins > coins && oldtime > ergebnis) {
					main.getConfigLoader().set("Players." + player.getName(), ergebnis + ":" + oldcoins);
				} else if (oldcoins >= coins && oldtime <= ergebnis) {
					main.getConfigLoader().set("Players." + player.getName(), ergebnis + ":" + oldcoins);
				} else {
					main.getConfigLoader().set("Players." + player.getName(), ergebnis + ":" + coins);
				}

				main.cload.save();
			}

			if (main.getConfigLoader().getString("ServerBest.Time") == null) {
				main.getConfigLoader().set("ServerBest.Time", ergebnis);
				main.cload.save();
			}
			if (main.getConfigLoader().getString("ServerBest.Coins") == null) {
				main.getConfigLoader().set("ServerBest.Coins", coins);
				main.cload.save();
			}

			if ((main.getConfigLoader().getString("ServerBest.Coins") != null && main.getConfigLoader().getString("ServerBest.Time") != null)) {

				long besttime = main.getConfigLoader().getLong("ServerBest.Time");
				int bestcoins = main.getConfigLoader().getInt("ServerBest.Coins");

				if (besttime > ergebnis) {
					main.getConfigLoader().set("ServerBest.Time", ergebnis);
				}
				if (bestcoins < coins) {
					main.getConfigLoader().set("ServerBest.Coins", coins);
				}
				main.cload.save();
			}

			main.cload.save();
		}
	}

}
