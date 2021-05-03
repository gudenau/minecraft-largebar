package net.gudenau.minecraft.largebar.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper{
    @Redirect(
        method = "renderHotbar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V",
            ordinal = 0
        )
    )
    private void renderHotbar$drawHotbar(InGameHud inGameHud, MatrixStack matrices, int x, int y, int u, int v, int width, int height){
        // Left chunk
        drawTexture(matrices, x - 90, y, 0, 0, 161, 22);
        // Middle chunk
        drawTexture(matrices, x + 71, y, 81, 0, 40, 22);
        // Right chunk
        drawTexture(matrices, x + 111, y, 21, 0, 161, 22);
    }
    
    @ModifyArg(
        method = "renderHotbar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"
        ),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/item/ItemStack;isEmpty()Z",
                ordinal = 0
            ),
            to = @At(
                value = "INVOKE",
                target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableRescaleNormal()V"
            )
        ),
        index = 2
    )
    private int renderHotbar$getOffhandY(int original){
        return original - 7 - 22;
    }
    
    @ModifyArg(
        method = "renderHotbar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbarItem(IIFLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)V"
        ),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/item/ItemStack;isEmpty()Z",
                ordinal = 1
            ),
            to = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/network/ClientPlayerEntity;getAttackCooldownProgress(F)F"
            )
        ),
        index = 1
    )
    private int renderHotbar$getOffhandStackY(int original){
        return original - 7 - 22;
    }
    
    @ModifyArg(
        method = "renderHotbar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V",
            ordinal = 1
        ),
        index = 1
    )
    private int renderHotbar$hotbarSelectionX(int original){
        return original - 90;
    }
    
    @ModifyConstant(
        method = "renderHotbar",
        constant = @Constant(intValue = 9)
    )
    private int renderHotbar$hotbarItemIterations(int original){
        return 18;
    }
    
    @ModifyArg(
        method = "renderHotbar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbarItem(IIFLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)V",
            ordinal = 0
        ),
        index = 0
    )
    private int renderHotbar$renderHotbarItem(int x){
        return x - 90;
    }
}
