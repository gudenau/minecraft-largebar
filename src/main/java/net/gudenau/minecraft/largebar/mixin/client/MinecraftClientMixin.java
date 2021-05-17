package net.gudenau.minecraft.largebar.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.gudenau.minecraft.largebar.LargeBar;
import net.gudenau.minecraft.largebar.LargeBarClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin{
    @Redirect(
        method = "handleInputEvents",
        at = @At(
            value = "FIELD",
            opcode = Opcodes.PUTFIELD,
            target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I"
        )
    )
    private void handleInputEvents$setSelectedSlot(PlayerInventory playerInventory, int value){
        if(LargeBarClient.getHotbarMode() == LargeBarClient.HotbarMode.DISABLED){
            playerInventory.selectedSlot = value;
            return;
        }
        
        boolean control = Screen.hasControlDown();
        boolean alt = Screen.hasAltDown();
        
        if(control != alt){
            if(control){
                playerInventory.selectedSlot = value;
            }else{
                playerInventory.selectedSlot = value + 9;
            }
            return;
        }
        
        if(playerInventory.selectedSlot % 9 == value){
            playerInventory.selectedSlot = (playerInventory.selectedSlot + 9) % 18;
        }else{
            playerInventory.selectedSlot = value;
        }
    }
}
