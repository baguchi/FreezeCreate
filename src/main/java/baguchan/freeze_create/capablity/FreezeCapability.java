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
    public int lastGameDay;

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
                                int foodDay = compoundtag.contains("FoodDay") ? compoundtag.getInt("FoodDay") : 0;
                                compoundtag.putInt("FoodDay", (((int) (blockentity.getLevel().getGameTime() / 24000) - lastGameDay) + foodDay));
                                LevelChunk chunk = blockentity.getLevel().getChunkAt(blockentity.getBlockPos());
                                FreezeCreate.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), new FreezeMessage(blockentity.getBlockPos(), stack.getTag(), i));

                            } else {
                                int foodDay = compoundtag.contains("FoodDay") ? compoundtag.getInt("FoodDay") : 0;
                                compoundtag.putInt("FoodDay", (((int) (blockentity.getLevel().getGameTime() / 24000) - lastGameDay) + foodDay));
                                LevelChunk chunk = blockentity.getLevel().getChunkAt(blockentity.getBlockPos());
                                FreezeCreate.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), new FreezeMessage(blockentity.getBlockPos(), stack.getTag(), i));

                            }
                        }
                    }
                    this.lastGameDay = (int) (blockentity.getLevel().getGameTime() / 24000);
                } else {
                    for (int i = 0; i < container.getContainerSize(); i++) {
                        ItemStack stack = container.getItem(i);
                        if (stack.getItem().isEdible()) {
                            if (stack.getTag() != null && stack.getTag().contains("Freeze")) {
                                stack.getOrCreateTag().remove("Freeze");
                                LevelChunk chunk = blockentity.getLevel().getChunkAt(blockentity.getBlockPos());
                                FreezeCreate.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), new FreezeMessage(blockentity.getBlockPos(), stack.getTag(), i));

                            }
                        }
                    }
                }
            }
            blockentity.setChanged();


        }
    }

    public void setNotFreeze(BlockEntity blockEntity) {
        this.freeze = false;
        if (blockEntity instanceof Container container) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack stack = container.getItem(i);
                if (stack.getItem().isEdible()) {
                    if (stack.getTag() != null && stack.getTag().contains("Freeze")) {
                        stack.getOrCreateTag().remove("Freeze");
                        LevelChunk chunk = blockEntity.getLevel().getChunkAt(blockEntity.getBlockPos());
                        FreezeCreate.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), new FreezeMessage(blockEntity.getBlockPos(), stack.getTag(), i));

                    }
                }
            }
        }
    }

    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        return (capability == FreezeCreate.FREEZE_CAPABILITY) ? LazyOptional.of(() -> this).cast() : LazyOptional.empty();
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("Freeze", this.freeze);
        nbt.putInt("LastGameDay", this.lastGameDay);
        return nbt;
    }

    public void deserializeNBT(CompoundTag nbt) {
        this.freeze = nbt.getBoolean("Freeze");
        this.lastGameDay = nbt.getInt("LastGameDay");
    }
}