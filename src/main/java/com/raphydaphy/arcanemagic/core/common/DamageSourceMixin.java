package com.raphydaphy.arcanemagic.core.common;

import com.raphydaphy.arcanemagic.init.ArcaneMagicConstants;
import com.raphydaphy.arcanemagic.item.ICrystalEquipment;
import com.raphydaphy.arcanemagic.util.ArcaneMagicUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageSource.class)
public abstract class DamageSourceMixin {
    @Shadow
    public abstract Entity getAttacker();

    @Inject(at = @At("RETURN"), method = "bypassesArmor", cancellable = true)
    private void bypassesArmor(CallbackInfoReturnable<Boolean> info) {
        Entity attacker = getAttacker();
        if (attacker instanceof LivingEntity) {
            ItemStack stack = ((LivingEntity) attacker).getMainHandStack();
            CompoundTag tag;
            if (!stack.isEmpty() && stack.getItem() instanceof ICrystalEquipment && (tag = stack.getTag()) != null) {
                ArcaneMagicUtils.ForgeCrystal passive = ArcaneMagicUtils.ForgeCrystal.getFromID(tag.getString(ArcaneMagicConstants.DAGGER_PASSIVE_CRYSTAL_KEY));
                if (passive == ArcaneMagicUtils.ForgeCrystal.DIAMOND) {
                    info.setReturnValue(true);
                }
            }
        }
    }
}
