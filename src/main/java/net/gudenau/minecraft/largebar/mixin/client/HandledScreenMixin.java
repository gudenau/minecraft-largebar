package net.gudenau.minecraft.largebar.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.gudenau.minecraft.largebar.LargeBarClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin extends Screen{
    private HandledScreenMixin(){
        super(null);
    }
    
    @ModifyArg(
        method = "handleHotbarKeyPressed",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V",
            ordinal = 1
        ),
        index = 2
    )
    private int handleHotbarKeyPressed(int original){
        if(LargeBarClient.getHotbarMode() == LargeBarClient.HotbarMode.DISABLED){
            return original;
        }else{
            return hasAltDown() ? original + 9 : original;
        }
    }
}
