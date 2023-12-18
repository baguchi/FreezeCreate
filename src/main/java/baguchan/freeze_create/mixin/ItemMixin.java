package baguchan.freeze_create.mixin;

import baguchan.freeze_create.FreezeConfigs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.extensions.IForgeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin implements IForgeItem {
    @Inject(method = "inventoryTick", at = @At("HEAD"))
    public void inventoryTick(ItemStack p_41404_, Level p_41405_, Entity p_41406_, int p_41407_, boolean p_41408_, CallbackInfo ci) {
        int day = FreezeConfigs.COMMON.rottenDay.get();
        if (p_41404_.getItem().isEdible()) {

            CompoundTag compoundtag = p_41404_.getOrCreateTag();
            if(p_41406_ instanceof Player){
                if(compoundtag.contains("Freeze")) {
                    compoundtag.remove("Freeze");
                }
            }
            if (!compoundtag.contains("Rotten") && !compoundtag.contains("Freeze")) {
                if (compoundtag.contains("FoodDay")) {
                    if (24000L * compoundtag.getInt("FoodDay") < p_41405_.getGameTime() - 24000L * day) {
                        compoundtag.putBoolean("Rotten", true);
                    }
                } else {
                    compoundtag.putInt("FoodDay", (int) (p_41405_.getGameTime() / 24000));
                }
            }
        }
    }

    @Inject(method = "onCraftedBy", at = @At("HEAD"))
    public void onCraftedBy(ItemStack p_41447_, Level p_41448_, Player p_41449_, CallbackInfo ci) {
        CompoundTag compoundtag = p_41447_.getOrCreateTag();
        if (!compoundtag.contains("FoodDay")) {
            compoundtag.putInt("FoodDay", (int) (p_41448_.getGameTime() / 24000));
        }
    }

    @Inject(method = "finishUsingItem", at = @At("HEAD"))
    public void finishUsingItem(ItemStack p_41409_, Level p_41410_, LivingEntity p_41411_, CallbackInfoReturnable<ItemStack> cir) {
            if (p_41409_.isEdible() && p_41409_.getTag() != null && p_41409_.getTag().contains("Rotten")) {
                p_41410_.playSound((Player)null, p_41411_.getX(), p_41411_.getY(), p_41411_.getZ(), p_41411_.getEatingSound(p_41409_), SoundSource.NEUTRAL, 1.0F, 1.0F + (p_41410_.random.nextFloat() - p_41410_.random.nextFloat()) * 0.4F);
                p_41411_.addEffect(new MobEffectInstance(MobEffects.HUNGER, 600, 1));
                if (!(p_41411_ instanceof Player) || !((Player)p_41411_).getAbilities().instabuild) {
                    p_41409_.shrink(1);
                }

                p_41411_.gameEvent(GameEvent.EAT);
            }
    }

    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        CompoundTag compoundtag = stack.getOrCreateTag();
        if (!compoundtag.contains("FoodDay")) {
            compoundtag.putInt("FoodDay", (int) (entity.level.getGameTime() / 24000));
            return true;
        }
        return false;
    }

}
