package de.jan.manager;

import java.util.ArrayList;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.jan.TempleRun;
import de.jan.arena.Arena;
import de.jan.trplayer.TRPlayer;
import de.jan.walkchecker.WalkChecker;

public class PlayerManager {

	private FileConfiguration config;
	private GhostManager ghostManager;
	private String prefix = "§b[§6TempleRun§b] §7";

	public static ArrayList<TRPlayer> players = new ArrayList<TRPlayer>();

	// The List for the Users who can Pickup items
	public static ArrayList<String> pickUpCoins = new ArrayList<String>();

	public PlayerManager() {
		this.config = TempleRun.instance.getConfig();
		this.ghostManager = TempleRun.ghostManager;
	}

	public void join(Player player, Arena arena) {
		Vault vault = TempleRun.instance.getVault();
		if(vault != null) {
			double joinAmount = config.getDouble("TempleRun.Vault.joinAmount");
			if(joinAmount > 0) {
				Economy economy = TempleRun.instance.economy;
				if(economy.getBalance(player.getName()) < joinAmount) {
					player.sendMessage(prefix + "You dont have enough money to play TempleRun. Join amount: §e" + joinAmount);
					return;
				}
				economy.withdrawPlayer(player.getName(), joinAmount);
			}
		}
		
		// Put the important information in the HashMaps
		TRPlayer trplayer = new TRPlayer(player, arena);
		players.add(trplayer);

		// clear the full iventory
		clearInventory(player);
		
		// Fliegen ausstellen
		player.setFlying(false);
		player.setGameMode(GameMode.SURVIVAL);

		// Set the Speed for the Player in TempleRun
		float speed = (float) config.getDouble("TempleRun.Settings.walkSpeed");
		player.setWalkSpeed(speed);

		player.teleport(arena.getSpawn());
		player.sendMessage(prefix + "You joined §e" + arena.getName() + "§7. Good Luck!");

		// Can be disabled in the config.
		if (config.getBoolean("TempleRun.Settings.updateFoodHealth", false)) {
			player.setHealth(((Damageable)player).getMaxHealth());
			player.setFoodLevel(20);
		}

		if (config.getBoolean("TempleRun.Settings.ghost", false)) {
			ghostManager.addGhost(player);
		}
		return;
	}

	@SuppressWarnings("deprecation")
	public void leave(Player player, boolean hasWon) {
		String playername = player.getName();

		// Set the Speed back to 0.2
		player.setWalkSpeed(0.2F);

		clearInventory(player);
		if(TempleRun.magnetManager.magnetBonus.containsKey(player)) {
			TempleRun.magnetManager.magnetBonus.remove(player);
		}
		
		
		TRPlayer trplayer = getTRPlayer(playername);

		if (trplayer != null) {
			trplayer.restoreInventory();
			player.teleport(trplayer.getOldlocation());
			players.remove(trplayer);

			if (config.getBoolean("TempleRun.Settings.ghost", false)) {
				ghostManager.removeGhost(player);
			}

			// If the Player completes the map
			if (hasWon) {
				Arena arena = trplayer.getArena();
				for (ItemStack i : arena.getWinItems()) {
					player.getInventory().addItem(i);
				}
				String time = Util.getDurationBreakdown(System.currentTimeMillis() - trplayer.getStarttime());
				int coins = trplayer.getCoins();

				player.sendMessage(prefix + "You completed the arena §e" + arena.getName());
				player.sendMessage("§6Gained Coins: §7" + coins);
				player.sendMessage("§6Your time: §7" + time);

				// Broadcast the Win Message
				if (config.getBoolean("TempleRun.Settings.broadcastOnWin", false)) {
					String message = config.getString("TempleRun.Settings.broadcastMessage");
					message = Util.replaceColorCodes(message.replaceAll("%PLAYER", playername).replaceAll("%ARENA", arena.getName()).replaceAll("%TIME", time).replaceAll("%COINS", String.valueOf(coins)));

					// Broadcast the message
					Bukkit.broadcastMessage(message);
				}
				
				// Money
				Vault vault = TempleRun.instance.getVault();
				if(vault != null) {
					double winMoney = config.getDouble("TempleRun.Vault.winMoney");
					Economy economy = TempleRun.instance.economy;
					economy.depositPlayer(player.getName(), winMoney);
				}
			} else {
				player.sendMessage(prefix + "You leaved templerun. Come back later.");
			}
			player.updateInventory();
			
			// WalkChecker
			WalkCheckerManager wManager = TempleRun.walkManager;
			WalkChecker checker = wManager.getWalkChecker(playername);
			if(checker == null) {
				return;
			}
			
			checker.cancel();
			wManager.checkerList.remove(checker);
		}
	}

	public TRPlayer getTRPlayer(String playername) {
		for (TRPlayer tr : players) {
			if (tr.getName().equalsIgnoreCase(playername))
				return tr;
		}
		return null;
	}

	public boolean isPlaying(String playername) {
		return getTRPlayer(playername) != null;
	}

	@SuppressWarnings("deprecation") 
	public void clearInventory(Player player) {
		player.getInventory().clear();

		ItemStack[] inv = player.getInventory().getArmorContents();
		for (int i = 0; i < inv.length; i++) {
			inv[i] = null;
		}
		player.getInventory().setArmorContents(inv);
		player.updateInventory();
	}

	public void failedTemplerun(Player player) {
		String playername = player.getName();

		if (config.getBoolean("TempleRun.Settings.leaveOnFail", false)) {
			leave(player, false);
			return;
		}

		TRPlayer trplayer = getTRPlayer(playername);

		if (trplayer != null) {
			Arena arena = trplayer.getArena();
			if (!trplayer.getCheckpoint().equals(arena.getSpawn())) {
				Location checkpoint = trplayer.getCheckpoint();
				player.teleport(checkpoint);
				trplayer.setCheckpoint(arena.getSpawn());
			} else {
				player.teleport(arena.getSpawn());
			}
			trplayer.resetCoins();
		}
		player.sendMessage(prefix + "You failed. Try again.");
	}
}
