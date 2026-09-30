package dev.jjcprogression.event;

import dev.jjcprogression.config.ProgressionConfig;
import dev.jjcprogression.ct.CursedTechniques;
import dev.jjcprogression.data.PlayerProgress;
import dev.jjcprogression.service.ProgressionService;
import dev.jjcprogression.network.ProgressionNetwork;
import dev.jjcprogression.api.BackTeleportEvent;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public final class ProgressionEvents {
    private static final ResourceLocation FIRST_TUTORIAL=new ResourceLocation("jujutsucraft","start_jujutsu_craft");
    @SubscribeEvent public void login(PlayerEvent.PlayerLoggedInEvent event){if(!(event.getEntity() instanceof ServerPlayer player))return;PlayerProgress p=PlayerProgress.of(player);if(!p.initialized()){p.initialized(true);p.save(player);player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Welcome. Your custom progression is active."));}if(p.reincarnationPending()){reincarnate(player,p);return;}ProgressionService.applyStats(player,p);ProgressionNetwork.sync(player,false);Advancement a=player.server.getAdvancements().getAdvancement(FIRST_TUTORIAL);if(a!=null&&player.getAdvancements().getOrStartProgress(a).isDone())tryFirstRoll(player);}
    @SubscribeEvent public void tutorial(AdvancementEvent.AdvancementEarnEvent event){if(event.getEntity() instanceof ServerPlayer player&&event.getAdvancement().getId().equals(FIRST_TUTORIAL))tryFirstRoll(player);}
    private void tryFirstRoll(ServerPlayer player){try{ProgressionService.firstTechnique(player);}catch(RuntimeException e){dev.jjcprogression.JJCProgression.LOGGER.error("[JJCPROGRESSION] First CT assignment failed for {}: {}",player.getGameProfile().getName(),e.toString());player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Your technique assignment is pending; contact a server operator."));}}
    @SubscribeEvent public void clone(PlayerEvent.Clone event){if(!event.isWasDeath())return;PlayerProgress old=PlayerProgress.of(event.getOriginal());old.save(event.getOriginal());event.getEntity().getPersistentData().put(PlayerProgress.ROOT,old.raw().copy());}
    @SubscribeEvent public void blockedBook(PlayerInteractEvent.RightClickItem event){if(!(event.getEntity() instanceof ServerPlayer player))return;ResourceLocation id=ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem());if(id!=null&&(ProgressionConfig.DISABLED_ITEMS.get().contains(id.toString())||(ProgressionConfig.DISABLE_INSECT_ITEM.get()&&id.toString().equals("jujutsucraft:item_insect")))){event.setCanceled(true);if(!player.level().isClientSide)player.sendSystemMessage(net.minecraft.network.chat.Component.literal("This progression item is disabled. Use your server's progression system."));}}
    @SubscribeEvent public void restrictBack(CommandEvent event){String command=event.getParseResults().getReader().getString().trim();if(command.startsWith("/"))command=command.substring(1);String root=command.split("\\s+",2)[0].toLowerCase(java.util.Locale.ROOT);if(root.contains(":"))root=root.substring(root.indexOf(':')+1);if(!root.equals("back"))return;var entity=event.getParseResults().getContext().getSource().getEntity();if(!(entity instanceof ServerPlayer player))return;PlayerProgress p=PlayerProgress.of(player);if(p.backBlockedAfterDeath()&&(p.grade().equals("Semi Grade 1")||p.grade().equals("Grade 1")||p.grade().equals("Special Grade"))){net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new BackTeleportEvent(player));event.setCanceled(true);player.sendSystemMessage(net.minecraft.network.chat.Component.literal("/back is disabled after death."));}}
    @SubscribeEvent public void scaleDamage(LivingHurtEvent event){
        if(event.getSource().getEntity() instanceof ServerPlayer attacker){PlayerProgress p=PlayerProgress.of(attacker);double bonus=Math.min(ProgressionConfig.DAMAGE_MAX_BPS.get(),(long)p.stat("dmg")*ProgressionConfig.DAMAGE_PER_POINT_BPS.get())/10000.0;event.setAmount((float)(event.getAmount()*(1.0+bonus)));}
        if(event.getEntity() instanceof ServerPlayer victim){PlayerProgress p=PlayerProgress.of(victim);double reduction=Math.min(ProgressionConfig.DURABILITY_MAX_BPS.get(),(long)p.stat("durability")*ProgressionConfig.DURABILITY_PER_POINT_BPS.get())/10000.0;event.setAmount((float)(event.getAmount()*(1.0-reduction)));}
    }
    @SubscribeEvent public void death(LivingDeathEvent event){
        Entity victim=event.getEntity();
        if(victim instanceof ServerPlayer player){PlayerProgress p=PlayerProgress.of(player);p.backBlockedAfterDeath(true);if(!p.isCurse()){p.curse(true);p.save(player);player.server.getPlayerList().saveAll();dev.jjcprogression.JJCProgression.LOGGER.info("[JJCPROGRESSION] {} died as Sorcerer and became Curse",player.getGameProfile().getName());player.sendSystemMessage(net.minecraft.network.chat.Component.literal("You have become a Curse. Your Fame, Grade, technique, stats, and progress are preserved."));ProgressionNetwork.sync(player,false);}else reincarnate(player,p);}
        if(event.getSource().getEntity() instanceof ServerPlayer killer&&victim!=killer){ResourceLocation id=ForgeRegistries.ENTITY_TYPES.getKey(victim.getType());if(id!=null&&id.getNamespace().equals("jujutsucraft")&&id.getPath().contains("cursed")){PlayerProgress p=PlayerProgress.of(killer);if(p.addUniqueKill(id.toString())){int reward=ProgressionConfig.UNIQUE_KILL_REWARD.get();p.points((int)Math.min(Integer.MAX_VALUE,(long)p.regularPoints()+reward));p.save(killer);killer.sendSystemMessage(net.minecraft.network.chat.Component.literal("First defeat of "+victim.getType().getDescription().getString()+": +"+reward+" Stat Point(s)."));ProgressionService.addFame(killer,ProgressionConfig.UNIQUE_KILL_FAME.get());ProgressionNetwork.sync(killer,false);}}}
    }
    private void reincarnate(ServerPlayer player,PlayerProgress p){
        String oldGrade;CursedTechniques.Technique next;
        try{if(!p.reincarnationPending()){oldGrade=p.grade();int count=progressionAdvancementCount(player);p.recordCycleSnapshot(oldGrade,count);next=CursedTechniques.roll(oldGrade,count);p.pendingReincarnation(next.name(),next.tier().name(),oldGrade);p.save(player);player.server.getPlayerList().saveAll();}else{oldGrade=p.pendingGrade();next=CursedTechniques.find(p.pendingCT()).orElseThrow(()->new IllegalStateException("Pending technique is no longer available in the configured JJC pool"));}CursedTechniques.resetJjcProgress(player,next);}catch(RuntimeException ex){dev.jjcprogression.JJCProgression.LOGGER.error("[JJCPROGRESSION] Reincarnation deferred safely for {}: {}",player.getGameProfile().getName(),ex.toString());player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Reincarnation is pending recovery. Your saved progression has not been wiped."));return;}
        for(Advancement a:player.server.getAdvancements().getAllAdvancements())if(a.getId().getNamespace().equals("jujutsucraft")){var progress=player.getAdvancements().getOrStartProgress(a);for(String criterion:progress.getCompletedCriteria())player.getAdvancements().revoke(a,criterion);}
        p=PlayerProgress.of(player);int life=p.reincarnations()+1;ProgressionService.resetCharacter(player,p);p=PlayerProgress.of(player);p.reincarnations(life);p.technique(next.name());p.tier(next.tier().name());p.firstRollComplete(true);p.clearPendingReincarnation();p.save(player);ProgressionService.applyStats(player,p);ProgressionNetwork.sync(player,false);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("You have been exorcised. Your reincarnation is complete."));player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Your new Cursed Technique is: "+next.name()+" ["+next.tier()+" Tier]. The recorded grade was "+oldGrade+"."));
        player.server.getPlayerList().saveAll();
    }
    public static int progressionAdvancementCount(ServerPlayer player){int count=0;for(Advancement a:player.server.getAdvancements().getAllAdvancements()){ResourceLocation id=a.getId();if(!id.getNamespace().equals("jujutsucraft"))continue;String path=id.getPath();if(path.contains("simple_domain")||path.contains("reverse_cursed_technique")||path.contains("domain_expansion")||path.contains("hollow_technique_purple")){if(player.getAdvancements().getOrStartProgress(a).isDone())count++;}}return count;}
}
