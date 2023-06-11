package me.paul.foliastuff.other;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Stream;

public class TreeFellerListener implements Listener {

    private static final int LOG_COUNT = 5, LEAF_COUNT = 15;

    private static final Material[] LOG_MATERIALS;
    private static final Material[] LEAF_MATERIALS;

    static {
        LOG_MATERIALS = Stream.of(Material.values()).filter(m -> m.name().contains("_LOG")).toArray(Material[]::new);
        LEAF_MATERIALS = Stream.of(Material.values()).filter(m -> m.name().contains("_LEAVES")).toArray(Material[]::new);

        FoliaStuff.getInstance().getLogger().info(Arrays.toString(LOG_MATERIALS));
        FoliaStuff.getInstance().getLogger().info(Arrays.toString(LEAF_MATERIALS));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        String blockName = block.getType().name();
        String itemName = event.getPlayer().getInventory().getItemInMainHand().getType().name();

        if (itemName.contains("_AXE") && blockName.contains("LOG") && isTree(block)) {
            // Remove the entire tree, cancel event
            event.setCancelled(true);
            removeTree(block, block.getType(), event.getPlayer().getInventory().getItemInMainHand());
        }
    }

    private boolean isTree(Block block) {
        // Check if there are enough log and leaf blocks in the surrounding area to qualify as a tree
        int logCount = countBlocks(block, LOG_MATERIALS, 0, new HashSet<>(), LOG_COUNT);
        int leafCount = countBlocks(block, LEAF_MATERIALS, 0, new HashSet<>(), LEAF_COUNT);

        return logCount >= LOG_COUNT && leafCount >= LEAF_COUNT; // Adjust these values as needed
    }

    private int countBlocks(Block block, Material[] materials, int count, HashSet<Location> visited, int maxCount) {
        if (count >= maxCount || visited.contains(block.getLocation()) || !isLogOrLeaf(block.getType())) {
            return count;
        }

        visited.add(block.getLocation());

        Material blockMaterial = block.getType();
        for (Material material : materials) {
            if (blockMaterial == material) {
                count++;
                break;
            }
        }

        // Recursively count the neighboring blocks
        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int yOffset = -1; yOffset <= 1; yOffset++) {
                for (int zOffset = -1; zOffset <= 1; zOffset++) {
                    Block neighborBlock = block.getRelative(xOffset, yOffset, zOffset);
                    count = countBlocks(neighborBlock, materials, count, visited, maxCount);
                }
            }
        }

        return count;
    }

    private boolean isLogOrLeaf(Material material) {
        for (Material logMaterial : LOG_MATERIALS) {
            if (material == logMaterial) {
                return true;
            }
        }
        for (Material leafMaterial : LEAF_MATERIALS) {
            if (material == leafMaterial) {
                return true;
            }
        }
        return false;
    }

    private void removeTree(Block block, Material previous, ItemStack tool) {
        block.breakNaturally(tool); // Break the current block
        ItemMeta meta = tool.getItemMeta();

        // increase dmg counter
        if (meta instanceof Damageable) {
            Damageable dmg = (Damageable) meta;
            dmg.setDamage(dmg.getDamage() + 1);
            if (tool.getType().getMaxDurability() <= dmg.getDamage()) {
                tool.subtract();
                return;
            } else {
                tool.setItemMeta(dmg);
            }
        }

        // Recursively break the neighboring blocks in all directions
        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int yOffset = -1; yOffset <= 1; yOffset++) {
                for (int zOffset = -1; zOffset <= 1; zOffset++) {
                    Block neighborBlock = block.getRelative(xOffset, yOffset, zOffset);
                    if (neighborBlock.getType() == previous) {
                        removeTree(neighborBlock, neighborBlock.getType(), tool); // Recursively break the neighbor
                    }
                }
            }
        }
    }


}
