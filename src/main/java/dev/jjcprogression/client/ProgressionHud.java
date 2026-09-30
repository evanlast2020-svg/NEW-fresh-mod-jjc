package dev.jjcprogression.client;

import dev.jjcprogression.JJCProgression;
import dev.jjcprogression.config.ProgressionConfig;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=JJCProgression.MOD_ID,value=Dist.CLIENT,bus=Mod.EventBusSubscriber.Bus.MOD)
public final class ProgressionHud {
    private ProgressionHud(){}

    @SubscribeEvent
    public static void register(RegisterGuiOverlaysEvent event){
        event.registerAboveAll("cursed_technique",ProgressionHud::render);
    }

    private static void render(ForgeGui gui,net.minecraft.client.gui.GuiGraphics graphics,float partialTick,int screenWidth,int screenHeight){
        var data=ProgressionClient.data();
        if(!ProgressionConfig.CT_HUD_ENABLED.get()||data==null||data.technique().isBlank())return;
        float scale=ProgressionConfig.CT_HUD_SCALE.get()/100f;
        int x=(int)(screenWidth/scale)-ProgressionConfig.CT_HUD_OFFSET_X.get()-130;
        int y=ProgressionConfig.CT_HUD_OFFSET_Y.get();
        graphics.pose().pushPose();
        graphics.pose().scale(scale,scale,1f);
        graphics.fill(x-6,y-5,x+124,y+42,ProgressionConfig.CT_HUD_BACKGROUND_COLOR.get());
        graphics.fill(x-6,y-5,x+124,y-4,ProgressionConfig.CT_HUD_ACCENT_COLOR.get());
        var font=net.minecraft.client.Minecraft.getInstance().font;
        graphics.drawString(font,Component.literal("CURSED TECHNIQUE"),x,y,ProgressionConfig.CT_HUD_TEXT_COLOR.get(),false);
        graphics.drawString(font,Component.literal(data.technique()),x,y+13,ProgressionConfig.CT_HUD_TEXT_COLOR.get(),false);
        graphics.drawString(font,Component.literal(data.tier()+" TIER"),x,y+27,ProgressionConfig.CT_HUD_ACCENT_COLOR.get(),false);
        graphics.pose().popPose();
    }
}
