package net.gudenau.minecraft.largebar;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public final class LargeBarModMenu implements ModMenuApi{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory(){
        return ConfigurationScreen::new;
    }
    
    private static class ConfigurationScreen extends Screen{
        private final Screen parent;
    
        protected ConfigurationScreen(Screen parent){
            super(new TranslatableText("options.gud_largebar"));
            this.parent = parent;
        }
    
        @Override
        protected void init(){
            super.init();
            
            addDrawableChild(new ButtonWidget((width >> 1) - 100, this.height / 6 + 48 - 6, 200, 20, LargeBarClient.getHotbarMode().getLabel(), (button)->{
                LargeBarClient.HotbarMode newMode;
                LargeBarClient.HotbarMode oldMode = LargeBarClient.getHotbarMode();
                switch(oldMode){
                    case VERTICAL:{
                        newMode = LargeBarClient.HotbarMode.HORIZONTAL;
                    } break;
                    case HORIZONTAL:{
                        newMode = LargeBarClient.HotbarMode.DISABLED;
                    } break;
                    default:{
                        newMode = LargeBarClient.HotbarMode.VERTICAL;
                    } break;
                }
                LargeBarClient.setHotbarMode(newMode);
                button.setMessage(newMode.getLabel());
            }));
            addDrawableChild(new ButtonWidget((width >> 1) - 100, height / 6 + 168, 200, 20, ScreenTexts.DONE, (button)->client.openScreen(parent)));
        }
    
        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta){
            renderBackground(matrices);
            drawCenteredText(matrices, textRenderer, title, width >> 1, 15, 0xFF_FF_FF);
            super.render(matrices, mouseX, mouseY, delta);
        }
    
        @Override
        public void removed(){
            LargeBarClient.saveConfig();
        }
    }
}
