package my.godarmoranvil.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {

    // 1. إلغاء حد 40 لفل (Too Expensive!) نهائياً
    @ModifyConstant(method = "createResult", constant = @Constant(intValue = 40))
    private int uncappedAnvilCost(int original) {
        return Integer.MAX_VALUE;
    }

    // 2. السماح بدمج جميع حمايات الدروع المتعارضة على الدروع والكتب فقط
    @Redirect(
        method = "createResult",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/enchantment/Enchantment;areCompatible(Lnet/minecraft/core/Holder;Lnet/minecraft/core/Holder;)Z"
        )
    )
    private boolean allowArmorProtectionCombination(Holder<Enchantment> first, Holder<Enchantment> second) {
        AnvilMenu menu = (AnvilMenu) (Object) this;
        ItemStack baseItem = menu.getSlot(0).getItem();

        if (isArmorOrBook(baseItem) && isProtection(first) && isProtection(second)) {
            return !first.equals(second);
        }
        return Enchantment.areCompatible(first, second);
    }

    private static boolean isArmorOrBook(ItemStack stack) {
        if (stack.isEmpty()) return false;
        // فحص الدروع والكتب بنظام 26.3 الحديث
        return stack.has(DataComponents.EQUIPPABLE) || stack.is(Items.ENCHANTED_BOOK);
    }

    private static boolean isProtection(Holder<Enchantment> holder) {
        if (holder == null) return false;
        String str = holder.toString().toLowerCase();
        return str.contains("protection");
    }
}
