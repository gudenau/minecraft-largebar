package net.gudenau.minecraft.largebar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public final class LargeBarClient implements ClientModInitializer{
    private static HotbarMode HOTBAR_MODE = HotbarMode.HORIZONTAL;
    
    public static HotbarMode getHotbarMode(){
        return HOTBAR_MODE;
    }
    
    static void setHotbarMode(HotbarMode hotbarMode){
        HOTBAR_MODE = hotbarMode;
    }
    
    @Override
    public void onInitializeClient(){
        loadConfig();
    }
    
    private void loadConfig(){
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("gud").resolve("largebar.cfg");
        if(!Files.isRegularFile(configPath)){
            saveConfig(configPath);
            return;
        }
        
        AtomicBoolean shouldSave = new AtomicBoolean(true);
        
        try(BufferedReader reader = Files.newBufferedReader(configPath, StandardCharsets.UTF_8)){
            reader.lines()
                .filter((line)->!line.isEmpty())
                .map(String::trim)
                .filter((line)->!line.startsWith("#"))
                .forEach((line)->{
                    String[] split = line.split("=", 2);
                    if(split.length == 2 && split[0].equals("mode")){
                        HOTBAR_MODE = HotbarMode.get(split[1]);
                        if(HOTBAR_MODE == null){
                            System.err.println("Unknown hotbar mode: " + split[1]);
                        }else{
                            shouldSave.set(false);
                        }
                    }
                });
        }catch(IOException e){
            System.err.println("Failed to read config file");
            e.printStackTrace();
            return;
        }
        
        if(shouldSave.get()){
            saveConfig(configPath);
        }
    }
    
    static void saveConfig(){
        saveConfig(FabricLoader.getInstance().getConfigDir().resolve("gud").resolve("largebar.cfg"));
    }
    
    private static void saveConfig(Path configPath){
        try{
            Files.createDirectories(configPath.getParent());
            try(BufferedWriter writer = Files.newBufferedWriter(configPath, StandardCharsets.UTF_8)){
                writer.write("# The mode of the expanded hotbar");
                writer.newLine();
                writer.write("# Legal values:");
                writer.newLine();
                writer.write("#   horizontal: The hotbar is 1 slot tall, 18 wide");
                writer.newLine();
                writer.write("#   vertical: The hotbar is 2 slot tall, 9 wide");
                writer.newLine();
                writer.write("#   disabled: The hotbar acts like vanilla");
                writer.newLine();
                writer.write("# Default value: horizontal");
                writer.newLine();
                writer.write("mode=" + HOTBAR_MODE.name().toLowerCase(Locale.ROOT));
                writer.newLine();
            }
        }catch(IOException e){
            System.err.println("Failed to save config file");
            e.printStackTrace();
        }
    }
    
    public enum HotbarMode{
        HORIZONTAL(new TranslatableText("options.gud_largebar.mode.horizontal")),
        VERTICAL(new TranslatableText("options.gud_largebar.mode.vertical")),
        DISABLED(new TranslatableText("options.gud_largebar.mode.disabled"));
        
        private final Text label;
        
        HotbarMode(Text label){
            this.label = label;
        }
        
        private static HotbarMode get(String string){
            for(HotbarMode mode : values()){
                if(mode.name().equalsIgnoreCase(string)){
                    return mode;
                }
            }
            return null;
        }
        
        public Text getLabel(){
            return label;
        }
    }
}
