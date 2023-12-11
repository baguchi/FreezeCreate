package baguchan.freeze_create.message;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FreezeMessage {
    public BlockPos blockPos;
    public CompoundTag stack;
    public int index;


    public FreezeMessage(BlockPos blockPos, CompoundTag stack, int index) {
        this.blockPos = blockPos;
        this.stack = stack;
        this.index = index;
    }

    public void writePacketData(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(blockPos);
        buffer.writeNbt(stack);
        buffer.writeInt(index);
    }

    public static FreezeMessage readPacketData(FriendlyByteBuf buffer) {
        BlockPos blockPos1 = buffer.readBlockPos();
        return new FreezeMessage(blockPos1, buffer.readNbt(), buffer.readInt());
    }

    public static boolean handle(FreezeMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.enqueueWork(() -> {
                BlockEntity tileentity = (Minecraft.getInstance()).player.level.getBlockEntity(message.blockPos);
                if (tileentity instanceof Container container) {
                    container.getItem(message.index).readShareTag(message.stack);
                }
            });
        }
        return true;
    }
}