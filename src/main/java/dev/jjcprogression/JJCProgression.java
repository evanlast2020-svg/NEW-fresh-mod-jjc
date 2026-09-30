package dev.jjcprogression;

import dev.jjcprogression.command.ProgressionCommands;
import dev.jjcprogression.config.ProgressionConfig;
import dev.jjcprogression.event.ProgressionEvents;
import dev.jjcprogression.network.ProgressionNetwork;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(JJCProgression.MOD_ID)
public final class JJCProgression {
    public static final String MOD_ID = "jjcprogression";
    public static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();
    public static volatile boolean DEBUG;
    public static boolean debugEnabled(){return DEBUG||ProgressionConfig.DEBUG.get();}
    public JJCProgression() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ProgressionConfig.SPEC, "jjcprogression-common.toml");
        ProgressionNetwork.register();
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(new ProgressionEvents());
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
    }
    private void registerCommands(RegisterCommandsEvent event) { ProgressionCommands.register(event.getDispatcher()); }
}
