package dev.jjcprogression.client;

import dev.jjcprogression.JJCProgression;
import dev.jjcprogression.network.ProgressionNetwork;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid=JJCProgression.MOD_ID,value=Dist.CLIENT,bus=Mod.EventBusSubscriber.Bus.MOD)
public final class ProgressionKeys {
    public static KeyMapping STATS;
    private ProgressionKeys(){}
    @SubscribeEvent public static void register(RegisterKeyMappingsEvent e){STATS=new KeyMapping("key.jjcprogression.stats",InputConstants.Type.KEYSYM,GLFW.GLFW_KEY_P,"key.categories.jjcprogression");e.register(STATS);}
    @Mod.EventBusSubscriber(modid=JJCProgression.MOD_ID,value=Dist.CLIENT,bus=Mod.EventBusSubscriber.Bus.FORGE)
    public static final class Input {
        private Input(){}
        @SubscribeEvent public static void tick(TickEvent.ClientTickEvent e){if(e.phase==TickEvent.Phase.END&&STATS!=null)while(STATS.consumeClick()){if(net.minecraft.client.Minecraft.getInstance().screen instanceof StatsScreen)net.minecraft.client.Minecraft.getInstance().setScreen(null);else ProgressionNetwork.request(true);}}
    }
}
