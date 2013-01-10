package TempleRun.Listeners.Move;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import TempleRun.TempleRun;
import TempleRun.Util.Util;

public class PlayerMoveListener implements Listener {

	public TempleRun main;

	public PlayerMoveListener(TempleRun main) {
		this.main = main;
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {

		final Player player = event.getPlayer();
		Block blockface = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN);

		if (Util.isPlaying(player.getName())) {

			String world = player.getLocation().getWorld().getName();

			int x = (int) Math.round(player.getLocation().getBlockX());
			int y = (int) Math.round(player.getLocation().getBlockY());
			int z = (int) Math.round(player.getLocation().getBlockZ());

			if (blockface.getType() == Material.GOLD_BLOCK) {

				if (Util.isWalkedOver(player.getName(), x, y)) {
					return;
				}

				Location loc = new Location(main.getServer().getWorld(world), x, y, z);

				Util.addLocation(player.getName(), x, y);

				main.getServer().getWorld(world).playEffect(loc, Effect.POTION_BREAK, 2);
			} else if(blockface.getType() == Material.DIAMOND_BLOCK) {
				
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3, 4));
				
				main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
					
					@Override
					public void run() {
						
						player.removePotionEffect(PotionEffectType.SPEED);
						player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2400000, 2));
						
					}
				}, 60L);
			} else if(blockface.isLiquid()) {
				
				Util.kickPlayer(player);
				player.sendMessage(Util.prefix + "You felt out of TempleRun. Failed!");
			}
		}

	}

}
