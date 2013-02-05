package TempleRun.Listeners.Features;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import TempleRun.Util.Util;

public class SplashListener implements Listener {

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {

		Player player = event.getPlayer();

		if (Util.isPlaying(player.getName()) && player.hasPermission("xapfeltortexp.testfunction")) {
			Block blockface_1 = player.getLocation().getBlock().getRelative(BlockFace.EAST);
			Block blockface_2 = player.getLocation().getBlock().getRelative(BlockFace.NORTH);
			Block blockface_3 = player.getLocation().getBlock().getRelative(BlockFace.SOUTH);
			Block blockface_4 = player.getLocation().getBlock().getRelative(BlockFace.WEST);
			Block blockface_5 = player.getLocation().getBlock().getRelative(BlockFace.DOWN);

			if (blockface_1.getType() == Material.EMERALD_BLOCK) {
				player.sendBlockChange(blockface_1.getLocation(), Material.AIR, (byte) 0);
			} else if (blockface_2.getType() == Material.EMERALD_BLOCK) {
				player.sendBlockChange(blockface_2.getLocation(), Material.AIR, (byte) 0);
			} else if (blockface_3.getType() == Material.EMERALD_BLOCK) {
				player.sendBlockChange(blockface_3.getLocation(), Material.AIR, (byte) 0);
			} else if (blockface_4.getType() == Material.EMERALD_BLOCK) {
				player.sendBlockChange(blockface_4.getLocation(), Material.AIR, (byte) 0);
			} else if (blockface_5.getType() == Material.EMERALD_BLOCK) {
				player.sendBlockChange(blockface_5.getLocation(), Material.AIR, (byte) 0);
			}
		}

	}

}
