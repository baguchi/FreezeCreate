package baguchan.freeze_create.register;

import baguchan.freeze_create.FreezeCreate;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class ModTags {
    public static final TagKey<Block> PASSES_FREEZE_FILL = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(FreezeCreate.MODID, "passes_freeze_fill"));
    public static final TagKey<Block> HOT_BLOCK = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(FreezeCreate.MODID, "hot_block"));
    public static final TagKey<Block> FREEZE_BLOCK = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(FreezeCreate.MODID, "freeze_block"));

    public static final TagKey<Fluid> HOT_FLUID = TagKey.create(Registry.FLUID_REGISTRY, new ResourceLocation(FreezeCreate.MODID, "hot_fluid"));

}
