package baguchan.freeze_create.util;

import baguchan.freeze_create.FreezeCreate;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.compress.utils.Sets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*
 * Thanks Ad Astra mod!
 * https://github.com/terrarium-earth/Ad-Astra/blob/1.20.x/common/src/main/java/earth/terrarium/ad_astra/common/util/OxygenUtils.java
 */
public class FreezeUtils {
    public static final Map<Pair<ResourceKey<Level>, BlockPos>, Set<BlockPos>> FREEZE_LOCATIONS = new HashMap<>();
    public static final Map<Pair<ResourceKey<Level>, BlockPos>, Set<BlockPos>> FREEZE_BLOCK_ENTITY_LOCATIONS = new HashMap<>();

    /**
     * Checks if an entity has Freeze.
     */
    public static boolean entityHasFreeze(Level level, LivingEntity entity) {
        return posHasFreeze(level, new BlockPos(entity.getEyePosition()));
    }

    /**
     * Checks if there is Freeze in a specific block in a specific dimension.
     */
    @SuppressWarnings("deprecation")
    public static boolean posHasFreeze(Level level, BlockPos pos) {

        if (!level.hasChunkAt(pos)) {
            return false;
        }

        return inDistributorFreeze(level, pos);
    }

    public static boolean inDistributorFreeze(Level level, BlockPos pos) {
        for (Map.Entry<Pair<ResourceKey<Level>, BlockPos>, Set<BlockPos>> entry : FREEZE_LOCATIONS.entrySet()) {
            if (level.dimension().equals(entry.getKey().getFirst())) {
                if (entry.getValue().contains(pos)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void addDistributorFreezeBlockEntitys(Level level, BlockPos pos) {
        Set<BlockPos> blockPos2 = Sets.newHashSet();
        ResourceKey<Level> previousDimension = null;
        for (Map.Entry<Pair<ResourceKey<Level>, BlockPos>, Set<BlockPos>> entry : FREEZE_LOCATIONS.entrySet()) {
            if (level.dimension().equals(entry.getKey().getFirst())) {
                previousDimension = entry.getKey().getFirst();
                for (BlockPos blockPos : entry.getValue()) {

                    BlockEntity blockEntity = level.getBlockEntity(blockPos);
                    if (blockEntity != null) {
                        blockPos2.add(blockPos);
                    }
                }
            }
        }
        FREEZE_BLOCK_ENTITY_LOCATIONS.put(getFreezeSource(level, pos), blockPos2);
    }

    /**
     * Gets the amount of blocks that an Freeze distributor is distributing.
     *
     * @param level  The level to check for Freeze in
     * @param source The Freeze distributor position
     * @return The amount of blocks that an Freeze distributor is distributing Freeze to
     */
    public static int getFreezeBlocksCount(Level level, BlockPos source) {
        return FREEZE_LOCATIONS.getOrDefault(getFreezeSource(level, source), Set.of()).size();
    }

    public static void setEntry(Level level, BlockPos source, Set<BlockPos> entries) {
        if (!level.isClientSide) {
            if (entries.isEmpty()) {
                Set<BlockPos> previousMap = FREEZE_BLOCK_ENTITY_LOCATIONS.get(getFreezeSource(level, source));
                if (previousMap != null) {
                    for (BlockPos blockPos : previousMap) {
                        BlockEntity blockEntity = level.getBlockEntity(blockPos);
                        if (blockEntity != null) {
                            blockEntity.getCapability(FreezeCreate.FREEZE_CAPABILITY).ifPresent(freezeCapability -> {
                                freezeCapability.setNotFreeze(blockEntity);
                            });
                        }
                    }

                }
                FREEZE_BLOCK_ENTITY_LOCATIONS.remove(getFreezeSource(level, source));
            }

            if (FREEZE_LOCATIONS.containsKey(getFreezeSource(level, source))) {
                Set<BlockPos> changedPositions = new HashSet<>(FREEZE_LOCATIONS.get(getFreezeSource(level, source)));
                if (changedPositions != null && !changedPositions.isEmpty()) {
                    changedPositions.removeAll(entries);
                    unFreezeBlocks((ServerLevel) level, changedPositions, source);
                }
            }


            FREEZE_LOCATIONS.put(getFreezeSource(level, source), entries);
            addDistributorFreezeBlockEntitys(level, source);
        }
    }

    public static void removeEntry(Level level, BlockPos source) {
        FreezeUtils.setEntry(level, source, Set.of());
    }

    /**
     * Removes the Freeze from a set of blocks. For example, turns water into ice or air, converts torches into extinguished torches, puts out flames, kills plants etc.
     */
    public static void unFreezeBlocks(ServerLevel level, Set<BlockPos> entries, BlockPos source) {
        try {
            if (entries == null) {
                return;
            }
            if (entries.isEmpty()) {
                return;
            }

            for (BlockPos pos : new HashSet<>(entries)) {

                BlockState state = level.getBlockState(pos);

                if (FREEZE_LOCATIONS.containsKey(getFreezeSource(level, source))) {
                    FREEZE_LOCATIONS.get(getFreezeSource(level, source)).remove(pos);
                }
                FREEZE_BLOCK_ENTITY_LOCATIONS.remove(getFreezeSource(level, source));
                if (posHasFreeze(level, pos)) continue;
                if (state.isAir()) continue;
            }
        } catch (UnsupportedOperationException e) {
            //FreezeCreate.LOGGER.error("Error deFreezing blocks");
            e.printStackTrace();
        }
    }

    public static Pair<ResourceKey<Level>, BlockPos> getFreezeSource(Level level, BlockPos source) {
        return Pair.of(level.dimension(), source);
    }
}