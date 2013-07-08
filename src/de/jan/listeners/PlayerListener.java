package de.jan.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import de.jan.TempleRun;
import de.jan.arena.Arena;
import de.jan.manager.ArenaManager;
import de.jan.manager.PlayerManager;
import de.jan.manager.Util;
import de.jan.trplayer.TRPlayer;

public class PlayerListener implements Listener {

	private ArenaManager arenaManager;
	private PlayerManager playerManager;
	private String prefix = "§b[§6TempleRun§b] §7";

	public PlayerListener() {
		arenaManager = TempleRun.arenaManager;
		playerManager = TempleRun.playerManager;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (!player.isOp()) {
			return;
		}

		if (TempleRun.instance.getConfig().getString("TempleRun.configVersion") != null && TempleRun.instance.getDescription().getVersion().equalsIgnoreCase(TempleRun.instance.getConfig().getString("TempleRun.configVersion"))) {
			return;
		}

		// If there is an new Version
		player.sendMessage(prefix + "Your config.yml seems to be outdated. Please recreate the config.");
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		String playername = player.getName();

		if (playerManager.isPlaying(playername)) {
			playerManager.leave(player, false);
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		String playername = player.getName();
		Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);

		// When the player is playing
		if (playerManager.isPlaying(playername)) {

			TRPlayer trplayer = playerManager.getTRPlayer(playername);

			if (block.getType() == Material.GOLD_BLOCK || block.getType() == Material.DIAMOND_BLOCK || block.getType() == Material.EMERALD_BLOCK) {
				Location loc = new Location(block.getWorld(), block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());

				if (trplayer.hasUsedBlock(loc)) {
					return;
				}

				if (block.getType() == Material.GOLD_BLOCK) {
					trplayer.addCoin();
					trplayer.addUsedBlock(loc);
					return;
				}

				// If the Block is instance of an Emerald_Block, then add an
				// Pumpkin Head
				if (block.getType() == Material.EMERALD_BLOCK) {
					if (trplayer.hasUsedBlock(loc)) {
						return;
					}

					if (player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType() == Material.PUMPKIN) {
						return;
					}

					player.getInventory().setHelmet(new ItemStack(Material.PUMPKIN, 1));
					trplayer.addUsedBlock(loc);
					player.getWorld().playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 2F, 2F);

					Bukkit.getScheduler().scheduleSyncDelayedTask(TempleRun.instance, new Runnable() {

						@Override
						public void run() {
							if (player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType() == Material.PUMPKIN) {
								player.getInventory().setHelmet(null);
							}
						}
					}, 20L * 5);
					return;
				}

				// Speed Diamond_block
				if (block.getType() == Material.DIAMOND_BLOCK) {
					if (trplayer.hasUsedBlock(loc)) {
						return;
					}

					trplayer.addUsedBlock(loc);
					player.getWorld().playSound(player.getLocation(), Sound.EXPLODE, 2F, 2F);

					// Jump Bitch!
					double verticalSpeed = 1.5;
					Vector playerDirection = player.getLocation().getDirection();
					double speedMultiplier = 2;
					playerDirection.multiply(speedMultiplier);
					playerDirection.setY(verticalSpeed);
					player.setVelocity(playerDirection);
					return;
				}

				return;
			}

			// If there isnt any blocks, then check if he / she / it failed out
			// of the world
			List<String> blocks = TempleRun.instance.getConfig().getStringList("TempleRun.Settings.blocks");
			for (String s : blocks) {
				if (!Util.isInteger(s))
					continue;

				int id = Integer.valueOf(s);
				if (player.getLocation().getBlock().getTypeId() == id) {
					playerManager.failedTemplerun(player);
					break;
				}
			}
		}
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();

		if (event.getLine(0).equalsIgnoreCase("[TempleRun]")) {
			// Checking Permissions
			if (!(player.hasPermission("templerun.create"))) {
				event.getBlock().breakNaturally();
				player.sendMessage(prefix + "You dont have permissions.");
				return;
			}

			if (event.getLine(1).equalsIgnoreCase("finish")) {
				// Fill the Sign
				event.setLine(0, "§9TempleRun");
				event.setLine(1, "§fClick to");
				event.setLine(2, "§fFinish");
				return;
			}

			// Checking if the arena exists
			Arena arena = arenaManager.getArena(event.getLine(1).toLowerCase());
			if (event.getLine(1).isEmpty() || arena == null) {
				event.getBlock().breakNaturally();
				player.sendMessage(prefix + "Arena not found.");
				return;
			}
			// Fill the Sign
			event.setLine(0, "§9TempleRun");
			event.setLine(1, "§fArena:");
			event.setLine(2, "§5" + arena.getName());
			return;
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		String playername = player.getName();

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			// Cancel the Event to make other intracting not possbile
			if (event.getClickedBlock().getType() != Material.SIGN_POST && event.getClickedBlock().getType() != Material.WALL_SIGN) {
				return;
			}

			Sign sign = (Sign) event.getClickedBlock().getState();
			if (sign.getLine(1).isEmpty() || sign.getLine(2).isEmpty()) {
				return;
			}

			if (!sign.getLine(0).equalsIgnoreCase("§9TempleRun")) {
				return;
			}

			if (!(player.hasPermission("templerun.join"))) {
				player.sendMessage(prefix + "You dont have permissions.");
				return;
			}

			// Check if the Sign is an Finish sign
			if (sign.getLine(1).equalsIgnoreCase("§fClick to")) {
				if (!playerManager.isPlaying(playername)) {
					player.sendMessage(prefix + "You are not playing.");
					return;
				}
				playerManager.leave(player, true);
				return;
			}

			// Checking if the player plays templerun
			if (playerManager.isPlaying(playername)) {
				player.sendMessage(prefix + "You already playing.");
				return;
			}

			// If the Sign isnt a Finish sign. . .
			Arena arena = arenaManager.getArena(sign.getLine(2).toLowerCase().substring(2, sign.getLine(2).length()));
			if (arena == null) {
				player.sendMessage(prefix + "Arena not found");
				return;
			}
			// Join TempleRun
			playerManager.join(player, arena);
			return;
		}
	}

	@EventHandler
	public void onPickupItem(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		String playername = player.getName();
		Item item = event.getItem();

		if (item == null) {
			return;
		}

		if (!item.getItemStack().hasItemMeta()) {
			return;
		}

		if (item.getItemStack().getType() != Material.GOLD_NUGGET && item.getItemStack().getType() != Material.WOOL) {
			return;
		}

		if (item.getItemStack().getItemMeta().getDisplayName().startsWith("§6TempleRun")) {
			// When the player has enabled the Pickup coin Method!
			if (PlayerManager.pickUpCoins.contains(player.getName())) {
				event.setCancelled(true);
				ItemStack coin = item.getItemStack();
				coin.setAmount(1);
				player.getInventory().addItem(coin);
				item.remove();

				// Player Pickup sound
				player.getWorld().playSound(player.getLocation(), Sound.ITEM_PICKUP, 1F, 1F);
				return;
			}

			if (playerManager.isPlaying(playername)) {
				TRPlayer trplayer = playerManager.getTRPlayer(playername);
				int id = item.getEntityId();

				// Check if I am already used a coin
				if (!trplayer.hasUsedCoin(id)) {
					trplayer.addUsedCoin(id);

					// Count coins
					trplayer.addCoin();

					// Checking for setting checkpoints
					if (item.getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase("§6TempleRun §cCoin")) {
						int checkpointAt = TempleRun.instance.getConfig().getInt("TempleRun.Settings.checkpoint");
						if (checkpointAt > 0) {
							int coins = trplayer.getCoins();
							if (coins % checkpointAt == 0) {
								trplayer.setCheckpoint(player.getLocation());
								player.sendMessage(prefix + "Checkpoint set.");
							}
						}
					} else if(item.getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase("§6TempleRun §aMagnet")) {
						TempleRun.magnetManager.magnetBonus.put(player, TempleRun.instance.getConfig().getInt("TempleRun.Settings.magnetBonusTime"));
						player.sendMessage(prefix + "Magnet Bonus activated!");
						player.getWorld().playSound(player.getLocation(), Sound.BLAZE_HIT, 2F, 2F);
					}
				}
			}
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		Player player = (Player) event.getEntity();
		String playername = player.getName();

		if (playerManager.isPlaying(playername)) {
			if (!player.hasPermission("templerun.admin"))
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		String playername = player.getName();

		if (playerManager.isPlaying(playername)) {
			if (!player.hasPermission("templerun.admin"))
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		String playername = player.getName();

		if (playerManager.isPlaying(playername)) {
			if (!player.hasPermission("templerun.admin"))
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();

		if (entity instanceof Player) {
			Player player = (Player) event.getEntity();
			String playername = player.getName();

			if (playerManager.isPlaying(playername)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		String playername = player.getName();

		if (playerManager.isPlaying(playername)) {
			if (!player.hasPermission("templerun.admin"))
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		String playername = player.getName();
		Item item = event.getItemDrop();

		if (playerManager.isPlaying(playername)) {
			if (!player.hasPermission("templerun.admin"))
				event.setCancelled(true);
		}

		if (item.getItemStack().getType() != Material.GOLD_NUGGET) {
			return;
		}

		if (item.getItemStack().hasItemMeta() && item.getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase("§6TempleRun §cCoin")) {
			item.getItemStack().setAmount(64);
		}
	}

	@EventHandler
	public void onItemDisapper(ItemDespawnEvent event) {
		Item item = event.getEntity();
		if (item.getItemStack().getType() != Material.GOLD_NUGGET && item.getItemStack().getType() != Material.WOOL) {
			return;
		}

		if (item.getItemStack().hasItemMeta() && item.getItemStack().getItemMeta().getDisplayName().startsWith("§6TempleRun")) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();

		// return if the Player is not playing
		if (!playerManager.isPlaying(player.getName())) {
			return;
		}

		if (player.isOp() || player.hasPermission("templerun.bypassCommands")) {
			return;
		}

		List<String> commands = TempleRun.instance.getConfig().getStringList("TempleRun.Settings.allowedCommands");
		String command = event.getMessage().toLowerCase();

		if (commands == null || commands.isEmpty()) {
			return;
		}

		for (String s : commands) {
			if (command.startsWith(s.toLowerCase())) {
				return;
			}
		}
		player.sendMessage(prefix + "This command is not allowed while playing templerun.");
		event.setCancelled(true);
	}
}