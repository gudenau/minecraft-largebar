package net.gudenau.minecraft.largebar.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin{
    @ModifyConstant(
        method = "getHotbarSize",
        constant = @Constant(intValue = 9)
    )
    private static int getHotbarSize(int original){
        return 18;
    }
    
    @ModifyConstant(
        method = "isValidHotbarIndex",
        constant = @Constant(intValue = 9)
    )
    private static int isValidHotbarIndex(int original){
        return 18;
    }
    
    @ModifyConstant(
        method = "getSwappableHotbarSlot",
        constant = @Constant(intValue = 9),
        expect = 4
    )
    private static int getSwappableHotbarSlot(int original){
        return 18;
    }
    
    @Environment(EnvType.CLIENT)
    @ModifyConstant(
        method = "scrollInHotbar",
        constant = @Constant(intValue = 9),
        expect = 3
    )
    private int scrollInHotbar(int original){
        return 18;
    }
}
