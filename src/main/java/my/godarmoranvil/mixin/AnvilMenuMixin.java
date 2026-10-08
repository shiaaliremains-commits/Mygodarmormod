package my.godarmoranvil.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {
    public AnvilMenuMixin(@Nullable MenuType<?> menuType, int i, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(menuType, i, inventory, containerLevelAccess);
    }

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
        ItemStack baseItem = this.inputSlots.getItem(0);
        if (isArmorOrBook(baseItem) && isProtectionEnchantment(first) && isProtectionEnchantment(second)) {
            return !first.equals(second);
        }
        return Enchantment.areCompatible(first, second);
    }

    private static boolean isArmorOrBook(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Item item = stack.getItem();
        if (item instanceof ArmorItem || stack.is(Items.ENCHANTED_BOOK)) {
            return true;
        }
        return stack.has(DataComponents.EQUIPPABLE);
    }

    private static boolean isProtectionEnchantment(Holder<Enchantment> holder) {
        if (holder == null) return false;
        return holder.unwrapKey().map(key -> {
            String path = key.location().getPath();
            return path.equals("protection") ||
                   path.equals("fire_protection") ||
                   path.equals("blast_protection") ||
                   path.equals("projectile_protection");
        }).orElse(false);
    }
}
