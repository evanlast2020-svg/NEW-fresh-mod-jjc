package dev.jjcprogression.integration;

import dev.jjcprogression.JJCProgression;
import dev.jjcprogression.config.ProgressionConfig;
import dev.jjcprogression.data.PlayerProgress;
import dev.jjcprogression.service.ProgressionService;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import java.lang.reflect.*;
import java.util.List;

/** Optional integration with the Architectury event API bundled by FTB Quests. */
@Mod.EventBusSubscriber(modid=JJCProgression.MOD_ID,bus=Mod.EventBusSubscriber.Bus.MOD)
public final class FtbQuestsIntegration {
    private FtbQuestsIntegration(){}
    @SubscribeEvent public static void load(FMLLoadCompleteEvent event){try{
        Class<?> completed=Class.forName("dev.ftb.mods.ftbquests.events.ObjectCompletedEvent");Object questEvent=completed.getField("QUEST").get(null);
        Class<?> actor=Class.forName("dev.architectury.event.EventActor");Object listener=Proxy.newProxyInstance(actor.getClassLoader(),new Class<?>[]{actor},(proxy,method,args)->{
            if(method.getDeclaringClass()==Object.class)return switch(method.getName()){case "toString"->"JJCProgressionFtbQuestListener";case "hashCode"->System.identityHashCode(proxy);case "equals"->proxy==args[0];default->null;};
            if(!method.getName().equals("act")||args==null||args.length==0)return null;
            Object payload=args[0];@SuppressWarnings("unchecked") List<ServerPlayer> players=(List<ServerPlayer>)payload.getClass().getMethod("getOnlineMembers").invoke(payload);
            int fame=ProgressionConfig.FTB_QUEST_FAME.get(),points=ProgressionConfig.FTB_QUEST_POINTS.get();
            for(ServerPlayer player:players){if(fame>0)ProgressionService.addFame(player,fame);if(points>0){PlayerProgress p=PlayerProgress.of(player);p.points((int)Math.min(Integer.MAX_VALUE,(long)p.regularPoints()+points));p.save(player);dev.jjcprogression.network.ProgressionNetwork.sync(player,false);}}
            return Class.forName("dev.architectury.event.EventResult").getMethod("pass").invoke(null);
        });questEvent.getClass().getMethod("register",actor).invoke(questEvent,listener);
        JJCProgression.LOGGER.info("[JJCPROGRESSION] FTB Quests completion rewards enabled through its Architectury event.");
    }catch(ClassNotFoundException ignored){JJCProgression.LOGGER.info("[JJCPROGRESSION] FTB Quests not present; optional quest integration skipped.");}catch(ReflectiveOperationException|LinkageError ex){JJCProgression.LOGGER.error("[JJCPROGRESSION] Could not register optional FTB Quests hook: {}",ex.toString());}}
}
