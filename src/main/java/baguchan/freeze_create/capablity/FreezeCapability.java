package baguchan.freeze_create.capablity;

import baguchan.freeze_create.FreezeCreate;
import baguchan.freeze_create.message.FreezeMessage;
import baguchan.freeze_create.util.FreezeUtils;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class FreezeCapability implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {

    public boolean freeze = false;
    public long loadGameTick;

    public void tick(BlockEntity blockentity) {
        if (blockentity.hasLevel() && !blockentity.getLevel().isClientSide) {
            if (blockentity instanceof Container container) {
                this.freeze = FreezeUtils.posHasFreeze(blockentity.getLevel(), blockentity.getBlockPos());
                if (this.freeze) {
                    for (int i = 0; i < container.getContainerSize(); i++) {
                        ItemStack stack = container.getItem(i);
                        CompoundTag compoundtag = stack.getOrCreateTag();
                        if (stack.getItem().isEdible()) {
                            if (!compoundtag.contains("Freeze")) {
                                compoundtag.putBoolean("Freeze", true);
                            } else {
                                long foodDay = compoundtag.contains("FoodDay") ? compoundtag.getLong("FoodDay") : 0;
                                compoundtag.putLong("FoodDay", (loadGameTick - foodDay) + foodDay);
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < container.getContainerSize(); i++) {
                        ItemStack stack = container.getItem(i);
                        if (stack.getItem().isEdible()) {
                            if (stack.getTag() != null && stack.getTag().contains("Freeze")) {
                                stack.getOrCreateTag().remove("Freeze");
                            }
                        }
                    }
                }
            }
            blockentity.setChanged();
            this.loadGameTick = blockentity.getLevel().getGameTime();

        }
    }


    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        return (capability == FreezeCreate.FREEZE_CAPABILITY) ? LazyOptional.of(() -> this).cast() : LazyOptional.empty();
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("Freeze", this.freeze);
        nbt.putLong("LastGameTick", this.loadGameTick);
        return nbt;
    }

    public void deserializeNBT(CompoundTag nbt) {
        this.freeze = nbt.getBoolean("Freeze");
        this.loadGameTick = nbt.getLong("LastGameTick");
    }
}