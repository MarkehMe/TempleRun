package TempleRun.Listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

import TempleRun.TempleRun;
import TempleRun.Util.Util;

public class PlayerListener implements Listener {

	public TempleRun main;

	public PlayerListener(TempleRun main) {
		this.main = main;
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {

		Player player = event.getPlayer();

		if (Util.isPlaying(player.getName())) {

			Location loc = Util.getOldLocation(player.getName());
			player.teleport(loc);
			
			Util.oldLoc.remove(player.getName());
			
			player.removePotionEffect(PotionEffectType.SPEED);
			Util.removePlayer(player.getName());
		}

	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {

		if (event.isCancelled())
			return;

		Player player = event.getPlayer();

		if (Util.isPlaying(player.getName())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {

		if (event.isCancelled())
			return;

		Player player = event.getPlayer();

		if (Util.isPlaying(player.getName())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {

		Player player = event.getPlayer();

		if (Util.isPlaying(player.getName())) {
			
			if(player.isOp()) {
				return;
			}

			if (!event.getMessage().startsWith("/tr") && !event.getMessage().startsWith("/templerun")) {
				player.sendMessage(Util.prefix + "You can just execute TempleRun Commands in TempleRun!");
				event.setCancelled(true);
			}

		}

	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		
		if(event.getEntity() instanceof Player) {
			
			Player player = (Player) event.getEntity();
			
			if(Util.isPlaying(player.getName())) {
				event.setCancelled(true);
			}
			
		}
	}

	@EventHandler
	public void onFoodLevel(FoodLevelChangeEvent event) {

		if (event.isCancelled())
			return;

		if (event.getEntity() instanceof Player) {
			
			Player player = (Player) event.getEntity();
			
			if (Util.isPlaying(player.getName())) {
				event.setCancelled(true);
			}
		}

	}

}
