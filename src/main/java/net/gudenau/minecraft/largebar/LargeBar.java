package net.gudenau.minecraft.largebar;

import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public final class LargeBar implements ModInitializer{
    public static final String MOD_ID = "gud_largebar";
    
    @Override
    public void onInitialize(){}
    
    private static final BooleanSupplier enabledSupplier;
    
    static{
        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT){
            enabledSupplier = ()->LargeBarClient.getHotbarMode() != LargeBarClient.HotbarMode.DISABLED;
        }else{
            enabledSupplier = ()->true;
        }
    }
    
    public static boolean isEnabled(){
        return enabledSupplier.getAsBoolean();
    }
}
