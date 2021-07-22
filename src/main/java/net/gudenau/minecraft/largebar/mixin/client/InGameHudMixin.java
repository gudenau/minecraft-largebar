package net.gudenau.minecraft.largebar.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.gudenau.minecraft.largebar.LargeBarClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper{
    @Shadow private int scaledWidth;
    
    @Shadow protected abstract PlayerEntity getCameraPlayer();
    
    @Shadow @Final private MinecraftClient client;
    
    @Redirect(
        method = "renderHotbar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V",
            ordinal = 0
        )
    )
    private void renderHotbar$drawHotbar(InGameHud inGameHud, MatrixStack matrices, int x, int y, int u, int v, int width, int height){
        switch(LargeBarClient.getHotbarMode()){
            case HORIZONTAL:{
                // Left chunk
                drawTexture(matrices, x - 90, y, 0, 0, 161, 22);
                // Middle chunk
                drawTexture(matrices, x + 71, y, 81, 0, 40, 22);
                // Right chunk
                drawTexture(matrices, x + 111, y, 21, 0, 161, 22);
            } break;
            case VERTICAL:{
                // Top half
                drawTexture(matrices, x, y - 20, 0, 0, 182, 21);
                // Bottom half
                drawTexture(matrices, x, y + 1, 0, 1, 182, 21);
            } break;
            default:{
                drawTexture(matrices, x, y, u, v, width, height);
            } break;
        }
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
                target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableBlend()V"
            )
        ),
        index = 2
    )
    private int renderHotbar$getOffhandY(int original){
        if(LargeBarClient.getHotbarMode() == LargeBarClient.HotbarMode.HORIZONTAL){
            return original - 7 - 22;
        }else{
            return original;
        }
    }
    
    @ModifyArg(
        method = "renderHotbar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbarItem(IIFLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;I)V"
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
        if(LargeBarClient.getHotbarMode() == LargeBarClient.HotbarMode.HORIZONTAL){
            return original - 7 - 22;
        }else{
            return original;
        }
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
        switch(LargeBarClient.getHotbarMode()){
            case HORIZONTAL: return original - 90;
            case VERTICAL: return (scaledWidth >> 1) - 91 - 1 + (getCameraPlayer().getInventory().selectedSlot % 9) * 20;
            default: return original;
        }
    }
    
    @ModifyArg(
        method = "renderHotbar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V",
            ordinal = 1
        ),
        index = 2
    )
    private int renderHotbar$hotbarSelectionY(int original){
        if(LargeBarClient.getHotbarMode() == LargeBarClient.HotbarMode.VERTICAL){
            return original - (getCameraPlayer().getInventory().selectedSlot / 9) * 20;
        }else{
            return original;
        }
    }
    
    @ModifyArg(
        method = "renderHotbar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V",
            ordinal = 1
        ),
        index = 6
    )
    private int renderHotbar$hotbarSelectionHeight(int original){
        return 24;
    }
    
    @ModifyConstant(
        method = "renderHotbar",
        constant = @Constant(intValue = 9)
    )
    private int renderHotbar$hotbarItemIterations(int original){
        return LargeBarClient.getHotbarMode() == LargeBarClient.HotbarMode.DISABLED ? 9 : 18;
    }
    
    @Unique private int gud_largebar$lastIndex;
    
    @ModifyArg(
        method = "renderHotbar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbarItem(IIFLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;I)V",
            ordinal = 0
        ),
        index = 0
    )
    private int renderHotbar$renderHotbarItemX(int x){
        switch(LargeBarClient.getHotbarMode()){
            case HORIZONTAL:{
                return x - 90;
            }
            case VERTICAL:{
                // We need to calculate the index for this....
                int index = (x + 88 - (scaledWidth >> 1)) / 20;
                gud_largebar$lastIndex = index;
                return (scaledWidth >> 1) - 88 + (index % 9) * 20;
            }
            default: return x;
        }
    }
    
    @ModifyArg(
        method = "renderHotbar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbarItem(IIFLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;I)V",
            ordinal = 0
        ),
        index = 1
    )
    private int renderHotbar$renderHotbarItemY(int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, int seed){
        if(LargeBarClient.getHotbarMode() != LargeBarClient.HotbarMode.VERTICAL){
            return y;
        }else{
            // We need to calculate the index for this....
            return y - (gud_largebar$lastIndex / 9) * 20;
        }
    }
    
    @Inject(
        method = "render",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;GUI_ICONS_TEXTURE:Lnet/minecraft/util/Identifier;",
            ordinal = 1
        )
    )
    private void render$shiftGui1(MatrixStack matrices, float tickDelta, CallbackInfo ci){
        if(LargeBarClient.getHotbarMode() == LargeBarClient.HotbarMode.VERTICAL){
            matrices.push();
            matrices.translate(0, -20, 0);
        }
    }
    
    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;getSleepTimer()I",
            ordinal = 0
        )
    )
    private void render$restoreGui1(MatrixStack matrices, float tickDelta, CallbackInfo ci){
        if(!client.options.hudHidden && LargeBarClient.getHotbarMode() == LargeBarClient.HotbarMode.VERTICAL){
            matrices.pop();
        }
    }
    
    @ModifyArg(
        method = "renderHotbar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"
        ),
        index = 2,
        slice = @Slice(
            from = @At(
                value = "FIELD",
                target = "Lnet/minecraft/client/option/GameOptions;attackIndicator:Lnet/minecraft/client/option/AttackIndicator;"
            ),
            to = @At("TAIL")
        )
    )
    private int render$attackIndicatorY(int original){
        if(LargeBarClient.getHotbarMode() == LargeBarClient.HotbarMode.HORIZONTAL){
            return original - 7 - 22;
        }else{
            return original;
        }
    }
}
