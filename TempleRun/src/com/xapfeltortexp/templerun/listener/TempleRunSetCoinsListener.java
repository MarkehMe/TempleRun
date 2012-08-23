package com.xapfeltortexp.templerun.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.xapfeltortexp.templerun.TempleRunMain;

public class TempleRunSetCoinsListener implements Listener {

	/**
	 * Register the Listener.
	 */
	public TempleRunMain plugin;

	public TempleRunSetCoinsListener(TempleRunMain plugin) {

		this.plugin = plugin;
	}

	private ChatColor gold = ChatColor.GOLD;
	private ChatColor yellow = ChatColor.YELLOW;
	@SuppressWarnings("unused")
	private String prefix = this.yellow + "[" + gold + "TempleRun" + yellow + "] ";

	/**
	 * Starting with Events.
	 */

	// PlayerMove Event
	@EventHandler
	public void onPlayerMove(final PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		if (plugin.walk.contains(player.getName())) {
			event.setCancelled(true);
		}
	}

	// Running Over the Gold Block
	@EventHandler
	public void onPlayerMoveEvent(final PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		if (!plugin.players.contains(player.getName())) {
			return;
		}
		if (event.getPlayer().getLocation().add(0D, -1D, 0D).getBlock().getType() == Material.GOLD_BLOCK) {
			if(plugin.overGold.contains(player.getName())) {
				return;
			}
			plugin.overGold.add(player.getName());
			plugin.points++;
			player.playEffect(player.getEyeLocation(), Effect.POTION_BREAK, 5);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

				@Override
				public void run() {
					plugin.overGold.clear();

				}
			}, 20L);
		}

	}

	// Running over the Diamond Block
	@EventHandler
	public void onPlayerMoveDia(final PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		if (!plugin.players.contains(player.getName())) {
			return;
		}
		if (event.getPlayer().getLocation().add(0D, -1D, 0D).getBlock().getType() == Material.DIAMOND_BLOCK) {
			player.removePotionEffect(PotionEffectType.SPEED);
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 42, plugin.SpeedOverDiamondBlock));
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

				@Override
				public void run() {
					player.removePotionEffect(PotionEffectType.SPEED);
					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 240000, plugin.Speed));

				}
			}, 42L);
		}

	}
	// Running over the Diamond Block
	@EventHandler
	public void onPlayerMoveRedstone(final PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		if (!plugin.players.contains(player.getName())) {
			return;
		}
		if (event.getPlayer().getLocation().add(0D, -1D, 0D).getBlock().getType() == Material.EMERALD_BLOCK) {
			player.removePotionEffect(PotionEffectType.SPEED);
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, (int) plugin.SpeedOverEmeraldBlock));
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

				@Override
				public void run() {
					player.removePotionEffect(PotionEffectType.SPEED);
					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 240000, plugin.Speed));

				}
			}, 42L);
		}

	}
}
