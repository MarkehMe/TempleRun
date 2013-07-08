package de.jan.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPickupItemEvent;

import de.jan.TempleRun;

public class MagnetManager {
	
	private String prefix = "§b[§6TempleRun§b] §7";
	
	public HashMap<Player, Integer> magnetBonus = new HashMap<Player, Integer>();
	private int task;

	public MagnetManager() {
		start();
	}

	public void start() {
		task = Bukkit.getScheduler().scheduleSyncRepeatingTask(TempleRun.instance, new Runnable() {

			@Override
			public void run() {

				Iterator<Entry<Player, Integer>> interator = magnetBonus.entrySet().iterator();
				
				while(interator.hasNext()) {
					Map.Entry<Player, Integer> map = interator.next();
					
					Player player = map.getKey();
					Integer time = map.getValue();
					
					if(time <= 0) {
						magnetBonus.remove(player);
						player.sendMessage(prefix + "Magnet Bonus runs out.");
					} else{
						magnetBonus.put(player, time - 1);
						for(Entity e : player.getNearbyEntities(5D, 5D, 5D)) {
							if(e instanceof Item) {
								Item item = (Item) e;
								if(item.getItemStack().getType() == Material.GOLD_NUGGET) {
									if(item.getItemStack().hasItemMeta()) {
										if(item.getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase("§6TempleRun §cCoin")) {
											PlayerPickupItemEvent event = new PlayerPickupItemEvent(player, item, 0);
											Bukkit.getServer().getPluginManager().callEvent(event);
										}
									}
								}
							}
						}
					}
				}
			}
		}, 20L, 20L);
	}

	public void cancel() {
		Bukkit.getScheduler().cancelTask(task);
	}
}
