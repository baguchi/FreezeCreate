package baguchan.freeze_create.create;

import baguchan.freeze_create.FreezeCreate;
import baguchan.freeze_create.register.ModTags;
import baguchan.freeze_create.util.FreezeUtils;
import baguchan.freeze_create.util.algorithm.FreezeFloodFiller3D;
import com.mojang.datafixers.util.Pair;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Set;

public class FreezeFanBehaviour extends BlockEntityBehaviour {
    public static final BehaviourType<FreezeFanBehaviour> TYPE = new BehaviourType<>();
    int scanCooldown;
    BlockPos cacheBlockPos;
    public EncasedFanBlockEntity encasedFanBlockEntity;

    public FreezeFanBehaviour(EncasedFanBlockEntity blockEntity) {
        super(blockEntity);
        this.encasedFanBlockEntity = blockEntity;
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    @Override
    public void destroy() {
        super.destroy();
        if (cacheBlockPos != null) {
            FreezeUtils.removeEntry(encasedFanBlockEntity.getLevel(), cacheBlockPos);
        }
    }

    @Override
    public void tick() {
        super.tick();

        Level level = getWorld();
        Direction direction = encasedFanBlockEntity.getAirFlowDirection();
        if (encasedFanBlockEntity.airCurrent.maxDistance != 0 || level == null) {
            if (level != null && !level.isClientSide) {
                if (encasedFanBlockEntity.getSpeed() == 0)
                    return;
                if (scanCooldown > 0)
                    scanCooldown--;
                if (scanCooldown <= 0) {
                    scanCooldown = 120;

                    if (direction != null) {
                        float limit = encasedFanBlockEntity.airCurrent.maxDistance;
                        for (int offset : Iterate.zeroAndOne) {
                            BlockPos pos = encasedFanBlockEntity.getAirCurrentPos().relative(direction.getOpposite(), (int) limit + 1)
                                    .below(offset);
                            if (level.getBlockState(pos).is(ModTags.FREEZE_BLOCK)) {
                                FreezeUtils.setEntry(level, pos.relative(direction, 1), FreezeFloodFiller3D.run(level, pos, (int) (Math.abs(encasedFanBlockEntity.getSpeed()) / 8)));
                                cacheBlockPos = pos;
                            }else {
                                if(cacheBlockPos != null) {
                                    FreezeUtils.removeEntry(level, cacheBlockPos);
                                    cacheBlockPos = null;
                                }
                            }
                        }
                    }
                    for(Pair<ResourceKey<Level>, BlockPos> posPair: FreezeUtils.FREEZE_BLOCK_ENTITY_LOCATIONS.keySet()){
                        if(posPair.getFirst() == level.dimension()) {
                            if (FreezeUtils.FREEZE_BLOCK_ENTITY_LOCATIONS.containsKey(FreezeUtils.getFreezeSource(level, posPair.getSecond()))) {
                                Set<BlockPos> blockPosSet = FreezeUtils.FREEZE_BLOCK_ENTITY_LOCATIONS.get(FreezeUtils.getFreezeSource(level, posPair.getSecond()));
                                for (BlockPos blockPos : blockPosSet) {
                                    BlockEntity blockEntity1 = level.getBlockEntity(blockPos);
                                    if (blockEntity1 != null) {
                                        blockEntity1.getCapability(FreezeCreate.FREEZE_CAPABILITY).ifPresent(freezeCapability -> {
                                            freezeCapability.tick(blockEntity1);
                                        });
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
