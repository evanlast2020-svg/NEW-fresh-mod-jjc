package dev.jjcprogression.service;

import dev.jjcprogression.config.ProgressionConfig;
import dev.jjcprogression.data.PlayerProgress;
import dev.jjcprogression.ct.CursedTechniques;
import dev.jjcprogression.network.ProgressionNetwork;
import dev.jjcprogression.api.GradeChangeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import java.util.UUID;

public final class ProgressionService {
    private static final UUID HEALTH = UUID.fromString("26fb9b4f-0e8b-4a1d-a321-5e473e441101");
    private static final UUID SPEED = UUID.fromString("26fb9b4f-0e8b-4a1d-a321-5e473e441103");
    private ProgressionService() {}
    public static void addFame(ServerPlayer player,long amount) {
        addFame(player,amount,true);
    }
    public static void addFame(ServerPlayer player,long amount,boolean announce) {
        if(amount<=0)return;
        PlayerProgress p=PlayerProgress.of(player);long before=p.fame();long after=saturatingAdd(before,amount);String oldGrade=p.grade();p.fame(after);int bonusPoints=awardCrossedGrades(player,p,before,after);
        p.save(player); applyStats(player,p);ProgressionNetwork.sync(player,false);if(announce){String grade=p.grade().equals(oldGrade)?"":" • Grade: "+p.grade();player.sendSystemMessage(Component.literal("Progression: +"+amount+" Fame"+(bonusPoints>0?" • +"+bonusPoints+" Stat Points":"")+grade));}if(dev.jjcprogression.JJCProgression.debugEnabled())dev.jjcprogression.JJCProgression.LOGGER.info("[JJCPROGRESSION] {} Fame is now {}",player.getGameProfile().getName(),p.fame());
    }
    public static void setFame(ServerPlayer player,long amount) { PlayerProgress p=PlayerProgress.of(player);long old=p.fame(),now=Math.max(0,amount);p.fame(now);awardCrossedGrades(player,p,old,now);p.save(player);applyStats(player,p);ProgressionNetwork.sync(player,false); }
    private static long saturatingAdd(long a,long b){return a>Long.MAX_VALUE-b?Long.MAX_VALUE:a+b;}
    private static int rewardGrade(PlayerProgress p,String grade){if(p.gradeRewardClaimed(grade))return 0;int reward=switch(grade){case "Grade 4"->ProgressionConfig.GRADE4_REWARD.get();case "Grade 3"->ProgressionConfig.GRADE3_REWARD.get();case "Semi Grade 2"->ProgressionConfig.SEMI2_REWARD.get();case "Grade 2"->ProgressionConfig.GRADE2_REWARD.get();case "Semi Grade 1"->ProgressionConfig.SEMI1_REWARD.get();case "Grade 1"->ProgressionConfig.GRADE1_REWARD.get();case "Special Grade"->ProgressionConfig.SPECIAL_REWARD.get();default->0;};p.claimGradeReward(grade);p.points((int)Math.min(Integer.MAX_VALUE,(long)p.regularPoints()+reward));return reward;}
    private static int awardCrossedGrades(ServerPlayer player,PlayerProgress p,long oldFame,long newFame){int awarded=0;String previous=PlayerProgress.gradeFor(oldFame);int[] thresholds={ProgressionConfig.GRADE_4.get(),ProgressionConfig.GRADE_3.get(),ProgressionConfig.SEMI_2.get(),ProgressionConfig.GRADE_2.get(),ProgressionConfig.SEMI_1.get(),ProgressionConfig.GRADE_1.get(),ProgressionConfig.SPECIAL.get()};for(int threshold:thresholds)if(threshold>oldFame&&threshold<=newFame){String next=PlayerProgress.gradeFor(threshold);awarded+=rewardGrade(p,next);awardJjcGradeAdvancement(player,next);MinecraftForge.EVENT_BUS.post(new GradeChangeEvent(player,previous,next));previous=next;}return awarded;}
    private static void awardJjcGradeAdvancement(ServerPlayer player,String grade){String path=switch(grade){case "Grade 4"->"sorcerer_grade_4";case "Grade 3"->"sorcerer_grade_3";case "Semi Grade 2"->"sorcerer_grade_2_semi";case "Grade 2"->"sorcerer_grade_2";case "Semi Grade 1"->"sorcerer_grade_1_semi";case "Grade 1"->"sorcerer_grade_1";case "Special Grade"->"sorcerer_grade_special";default->null;};if(path==null)return;var advancement=player.server.getAdvancements().getAdvancement(new net.minecraft.resources.ResourceLocation("jujutsucraft",path));if(advancement==null)return;var progress=player.getAdvancements().getOrStartProgress(advancement);for(String criterion:progress.getRemainingCriteria())player.getAdvancements().award(advancement,criterion);}
    public static boolean invest(ServerPlayer player,String stat){PlayerProgress p=PlayerProgress.of(player);if(!java.util.Arrays.asList(PlayerProgress.STATS).contains(stat)||p.points()<=0||p.stat(stat)>=ProgressionConfig.STAT_CAP.get()||p.totalStats()>=ProgressionConfig.TOTAL_CAP.get())return false;p.stat(stat,p.stat(stat)+1);p.spendPoint();p.save(player);applyStats(player,p);ProgressionNetwork.sync(player,false);return true;}
    public static void applyStats(ServerPlayer player,PlayerProgress p){
        var attrs=player.getAttributes();
        var health=attrs.getInstance(Attributes.MAX_HEALTH);if(health!=null){health.removeModifier(HEALTH);health.addTransientModifier(new AttributeModifier(HEALTH,"jjcprogression health",Math.min(ProgressionConfig.HEALTH_MAX.get(),p.stat("health")*ProgressionConfig.HEALTH_PER_POINT.get()),AttributeModifier.Operation.ADDITION));player.setHealth(Math.min(player.getHealth(),player.getMaxHealth()));}
        var speed=attrs.getInstance(Attributes.MOVEMENT_SPEED);if(speed!=null){speed.removeModifier(SPEED);speed.addTransientModifier(new AttributeModifier(SPEED,"jjcprogression speed",Math.min(ProgressionConfig.SPEED_MAX.get(),p.stat("speed")*ProgressionConfig.SPEED_PER_POINT.get()),AttributeModifier.Operation.MULTIPLY_TOTAL));}
        CursedTechniques.applyCursedEnergyInvestment(player,p.stat("ce"));
    }
    public static void resetCharacter(ServerPlayer player,PlayerProgress p){p.fame(0);p.points(0);for(String s:PlayerProgress.STATS)p.stat(s,0);p.resetGradeRewards();p.curse(false);p.technique("");p.tier("");p.initialized(true);p.save(player);applyStats(player,p);}
    public static int thresholdForNext(long fame){if(fame<ProgressionConfig.GRADE_4.get())return ProgressionConfig.GRADE_4.get();if(fame<ProgressionConfig.GRADE_3.get())return ProgressionConfig.GRADE_3.get();if(fame<ProgressionConfig.SEMI_2.get())return ProgressionConfig.SEMI_2.get();if(fame<ProgressionConfig.GRADE_2.get())return ProgressionConfig.GRADE_2.get();if(fame<ProgressionConfig.SEMI_1.get())return ProgressionConfig.SEMI_1.get();if(fame<ProgressionConfig.GRADE_1.get())return ProgressionConfig.GRADE_1.get();if(fame<ProgressionConfig.SPECIAL.get())return ProgressionConfig.SPECIAL.get();return -1;}
    public static void assignTechnique(ServerPlayer player,CursedTechniques.Technique technique){CursedTechniques.assign(player,technique);PlayerProgress p=PlayerProgress.of(player);p.technique(technique.name());p.tier(technique.tier().name());p.save(player);player.sendSystemMessage(Component.literal("Your Cursed Technique is: "+technique.name()+" ["+technique.tier()+" Tier]"));ProgressionNetwork.sync(player,false);dev.jjcprogression.JJCProgression.LOGGER.info("[JJCPROGRESSION] Assigned CT {} to {}",technique.name(),player.getGameProfile().getName());}
    public static void clearTechnique(ServerPlayer player){CursedTechniques.clear(player);PlayerProgress p=PlayerProgress.of(player);p.technique("");p.tier("");p.save(player);ProgressionNetwork.sync(player,false);}
    public static void firstTechnique(ServerPlayer player){PlayerProgress p=PlayerProgress.of(player);var existing=CursedTechniques.current(player);
        // JJC may have already assigned a technique before our tutorial event fires. Trust its
        // numeric capability value over any older display name saved by this addon.
        if(existing.isPresent()){CursedTechniques.Technique t=existing.get();p.technique(t.name());p.tier(t.tier().name());p.firstRollComplete(true);p.save(player);ProgressionNetwork.sync(player,false);return;}
        if(p.firstRollComplete())return;
        CursedTechniques.Technique t=p.hasCT()?CursedTechniques.find(p.technique()).orElseGet(CursedTechniques::rollFirst):CursedTechniques.rollFirst();
        CursedTechniques.assign(player,t);p=PlayerProgress.of(player);p.technique(t.name());p.tier(t.tier().name());p.firstRollComplete(true);p.save(player);player.sendSystemMessage(Component.literal("Your Cursed Technique is: "+t.name()+" ["+t.tier()+" Tier]"));ProgressionNetwork.sync(player,false);}
}
