package com.kryeit.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import static com.kryeit.commands.PostAPI.WORLD;

public class BlockFinder {
    // Y-level to start searching from. Should be set to max build height in most cases.
    private static final int START_Y = 319;

    public static int getFirstSolidBlockY(int x, int z) {
        for (int y = START_Y; y >= 0; y--) {
            Block block = WORLD.getBlockAt(x, y, z);
            Material type = block.getType();

            // A block is "solid" if it's not air, not liquid, has collision, and isn't leaves.
            // Adjust this condition to fit your needs.
            if(type == Material.WATER || type == Material.LAVA) {
                return block.getLocation().getBlockY();
            }
            if (type != Material.AIR && type.isSolid() && !type.isTransparent()) {
                return block.getLocation().getBlockY();
            }
        }

        // If no solid block is found, return null.
        return 319;
    }

    public static void clearArea(Location loc, int width) {
        int centerX = loc.getBlockX();
        int centerY = loc.getBlockY();
        int centerZ = loc.getBlockZ();

        for (int x = centerX - width; x <= centerX + width; x++) {
            for (int y = centerY; y <= 319 - 1; y++) {
                for (int z = centerZ - width; z <= centerZ + width; z++) {
                    Block block = WORLD.getBlockAt(x, y, z);
                    block.setType(Material.AIR);
                }
            }
        }
    }
}
