package TempleRun.Listeners.Coin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import TempleRun.Util.Util;

public class CoinListener implements Listener {

	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent event) {
		
		if(event.isCancelled())
			return;
		
		Player player = event.getPlayer();

		if (Util.isCoin(event.getItem().getItemStack())) {

			if (player.hasPermission("templerun.pickup")) {
				return;
			}
			
			event.setCancelled(true);
			
			if(Util.isPlaying(player.getName())) {
				
				String world = player.getLocation().getWorld().getName();
				int x = (int) Math.round(event.getItem().getLocation().getBlockX());
				int y = (int) Math.round(event.getItem().getLocation().getBlockY());
				int z = (int) Math.round(event.getItem().getLocation().getBlockZ());
				
				if (Util.isWalkedOver(player.getName(), x, z)) {
					return;
				}

				Location loc = new Location(Bukkit.getServer().getWorld(world), x, y, z);
				
				Util.addCoin(player);
				Util.addLocation(player.getName(), x, z);

				player.playSound(loc, Sound.ORB_PICKUP, (float) 2, (float) 4);
			}
		}
	}
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		
		if(event.isCancelled())
			return;
		
		if(Util.isCoin(event.getItemDrop().getItemStack())) {
			event.getItemDrop().getItemStack().setAmount(64);
		}
		
	}
	
	@EventHandler
	public void onItemDeSpawn(ItemDespawnEvent event) {
		
		if(event.isCancelled())
			return;
		
		if(Util.isCoin(event.getEntity().getItemStack())) {
			event.setCancelled(true);
		}
	}

}
