package com.xapfeltortexp.templerun.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import com.xapfeltortexp.mexdb.exception.EmptyIndexException;
import com.xapfeltortexp.mexdb.system.Entry;
import com.xapfeltortexp.templerun.TempleRunMain;

public class TempleRunListener implements Listener {

	private ChatColor gold = ChatColor.GOLD;
	private ChatColor yellow = ChatColor.YELLOW;

	private String prefix = this.yellow + "[" + gold + "TempleRun" + yellow + "] ";

	/**
	 * Register the Listener.
	 */
	
	
	public TempleRunMain plugin;

	public TempleRunListener(TempleRunMain plugin) {
		this.plugin = plugin;
	}

	/**
	 * Starting with Events.
	 */

	// PlayerQuit Event
	@EventHandler
	public void onPlayerQuit(final PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		if (plugin.players.contains(player.getName())) {
			plugin.i--;
			plugin.players.remove(player.getName());
		}
	}

	// PlayerInventoryClick Event
	@EventHandler
	public void onPlayerInventoryClick(final InventoryClickEvent event) {
		final Player player = (Player) event.getWhoClicked();
		if (plugin.players.contains(player.getName())) {
			event.setCancelled(true);
		}
	}

	// BlockBreak Event
	@EventHandler
	public void onBlockBreak(final BlockBreakEvent event) {
		final Player player = event.getPlayer();
		if (plugin.players.contains(player.getName()) || plugin.walk.contains(player.getName()) || plugin.fall.contains(player.getName())) {
			event.setCancelled(true);
		}
	}

	// PlayerCommandPreprocess Event
	@EventHandler
	public void onCommandProcess(PlayerCommandPreprocessEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Player player = event.getPlayer();
		String name = player.getName();

		if (player.hasPermission("templerun.bypass")) {
			return;
		}
		if (plugin.players.contains(name)) {
			return;
		}
		String msg = event.getMessage();
		String[] frag = msg.split(" ");

		if ((!frag[0].equalsIgnoreCase("/templerun")) || (!frag[0].equalsIgnoreCase("/tr"))) {
			return;
		}
		event.setCancelled(true);
		player.sendMessage(prefix + ChatColor.RED + "You can just execute TempleRun Commands while playing.");
	}

	// FoodLevelChange Event
	@EventHandler
	public void onFoodLevelChange(final FoodLevelChangeEvent event) {
		final Player player = (Player) event.getEntity();
		if (plugin.players.contains(player.getName())) {
			event.setFoodLevel(20);
			event.setCancelled(true);
		}
	}

	// SignChange Event
	@EventHandler
	public void onSignCreate(final SignChangeEvent event) {
		Player player = event.getPlayer();
		if (player.hasPermission("templerun.create")) {
			if (event.getLine(0).equalsIgnoreCase("[TempleRun]")) {
				if (event.getLine(1).equalsIgnoreCase("Finish")) {
					event.setLine(0, ChatColor.BLUE + "[TempleRun]");
					event.setLine(1, ChatColor.GREEN + "Finish");
					event.getPlayer().sendMessage(prefix + ChatColor.GREEN + "Successful created.");
				}
			}
		} else {
			if (event.getLine(0).contains("[TempleRun]") && event.getLine(1).contains("Finish")) {
				player.sendMessage(prefix + ChatColor.RED + "You dont have Permissions to create a TempleRun sign.");
				event.getBlock().breakNaturally();
				return;
			}
		}
	}

	// PlayerIntract Event
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerIntract(PlayerInteractEvent event) throws EmptyIndexException {
		final Player player = event.getPlayer();
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		Block evblock = event.getClickedBlock();
		if (evblock == null) {
			return;
		}
		if ((evblock.getType() != Material.SIGN_POST) && (evblock.getType() != Material.WALL_SIGN)) {
			return;
		}
		Sign sign = (Sign) evblock.getState();
		if (sign.getLine(0).contains("[TempleRun]") && (sign.getLine(1).contains("Finish"))) {
			if (!this.plugin.players.contains(player.getName())) {
				player.sendMessage(prefix + ChatColor.RED + "You are not in TempleRun. You can recieve a Present.");
				return;
			}
			// Messages
			player.sendMessage(prefix);
			player.sendMessage(ChatColor.RED + "You finished the TempleRun and you got " + ChatColor.GREEN + this.plugin.Amount + ChatColor.RED + " of the Item " + ChatColor.GREEN + this.plugin.Item);
			player.removePotionEffect(PotionEffectType.SPEED);
			player.sendMessage(ChatColor.RED + "Your Points: " + ChatColor.GREEN + plugin.points + "0");

			// Clear ArrayList + Integers
			this.plugin.i--;
			this.plugin.players.remove(player.getName());
			plugin.points = 0;
			plugin.walk.clear();
			player.teleport(player.getWorld().getSpawnLocation());
			player.getInventory().addItem(new ItemStack[] { new ItemStack(this.plugin.Item, this.plugin.Amount) });
			player.setSprinting(false);

			// Database
			int savedScore = 0;
			if ((this.plugin.database.hasIndex(player.getName())) && (this.plugin.database.hasKey(player.getName(), "score"))) {
				savedScore = this.plugin.database.getInt(player.getName(), "score");
			}
			Entry entry = new Entry(player.getName());
			int score = savedScore + 1;
			entry.addValue("score", Integer.valueOf(score));
			this.plugin.database.addEntry(entry);
			this.plugin.database.push();
			event.getPlayer().updateInventory();
		}
	}

	// Are you fell out of the World?
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPlayerMove(final PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		if (plugin.players.contains(player.getName()) && player.getLocation().getBlockY() <= plugin.outOfWorld) {
			plugin.fall.add(player.getName());
			player.removePotionEffect(PotionEffectType.SPEED);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {
					plugin.fall.remove(player.getName());
					player.removePotionEffect(PotionEffectType.SPEED);
				}
			}, 40L);
			player.sendMessage(prefix + ChatColor.RED + "TempleRun Mission failed!");
			plugin.i--;
			player.removePotionEffect(PotionEffectType.SPEED);
			plugin.players.remove(player.getName());
			event.setTo(player.getWorld().getSpawnLocation());
		}
	}

	// You cant get Damage
	@EventHandler
	public void onPlayerDamage(final EntityDamageEvent event) {
		final Entity player = event.getEntity();
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		if (plugin.players.contains(((HumanEntity) player).getName())) {
			event.setCancelled(true);
		}
		if (plugin.fall.contains(((HumanEntity) player).getName())) {
			event.setCancelled(true);
		}
	}

}
