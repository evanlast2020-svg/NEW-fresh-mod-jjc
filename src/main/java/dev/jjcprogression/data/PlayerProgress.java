package dev.jjcprogression.data;

import dev.jjcprogression.config.ProgressionConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import java.util.HashSet;
import java.util.Set;

/** Versioned server-owned data stored in vanilla's per-player persistent tag. */
public final class PlayerProgress {
    public static final String ROOT = "jjcprogression";
    public static final int DATA_VERSION = 1;
    public static final String[] STATS = {"dmg", "health", "ce", "speed", "durability"};
    private final CompoundTag tag;
    private PlayerProgress(CompoundTag tag) { this.tag = tag; migrate(); }
    public static PlayerProgress of(Entity entity) { return new PlayerProgress(entity.getPersistentData().getCompound(ROOT)); }
    public void save(Entity entity) { tag.putInt("dataVersion", DATA_VERSION); entity.getPersistentData().put(ROOT, tag.copy()); }
    private void migrate() { int version=tag.getInt("dataVersion"); if(version<1){ tag.putInt("fame",Math.max(0,tag.getInt("fame"))); tag.putInt("prestige",Math.max(0,tag.getInt("prestige"))); tag.putInt("dataVersion",1); } }
    public long fame(){ return Math.max(0,tag.getLong("fame")); }
    public void fame(long value){tag.putLong("fame",Math.max(0,value));}
    public String grade(){return gradeFor(fame());}
    public static String gradeFor(long fame){ if(fame>=ProgressionConfig.SPECIAL.get())return "Special Grade";if(fame>=ProgressionConfig.GRADE_1.get())return "Grade 1";if(fame>=ProgressionConfig.SEMI_1.get())return "Semi Grade 1";if(fame>=ProgressionConfig.GRADE_2.get())return "Grade 2";if(fame>=ProgressionConfig.SEMI_2.get())return "Semi Grade 2";if(fame>=ProgressionConfig.GRADE_3.get())return "Grade 3";if(fame>=ProgressionConfig.GRADE_4.get())return "Grade 4";return "Sorcerer";}
    public int points(){return (int)Math.min(Integer.MAX_VALUE,(long)regularPoints()+accountPoints());} public int regularPoints(){return Math.max(0,tag.getInt("points"));} public void points(int n){tag.putInt("points",Math.max(0,n));}
    public int accountPoints(){return Math.max(0,tag.getInt("accountPoints"));} public void accountPoints(int n){tag.putInt("accountPoints",Math.max(0,n));}
    public boolean spendPoint(){if(regularPoints()>0){points(regularPoints()-1);return true;}if(accountPoints()>0){accountPoints(accountPoints()-1);return true;}return false;}
    public int stat(String s){return Math.max(0,tag.getInt("stat_"+s));} public void stat(String s,int n){tag.putInt("stat_"+s,Math.max(0,n));}
    public int totalStats(){long total=0;for(String s:STATS)total+=stat(s);return (int)Math.min(Integer.MAX_VALUE,total);}
    public String state(){return tag.getBoolean("curseState")?"Curse":"Sorcerer";} public boolean isCurse(){return tag.getBoolean("curseState");} public void curse(boolean value){tag.putBoolean("curseState",value);}
    public int prestige(){return Math.max(0,tag.getInt("prestige"));} public void prestige(int value){tag.putInt("prestige",Math.max(0,Math.min(ProgressionConfig.MAX_PRESTIGE.get(),value)));}
    public boolean hasCT(){return tag.contains("technique")&&!tag.getString("technique").isBlank();} public String technique(){return tag.getString("technique");} public void technique(String s){tag.putString("technique",s==null?"":s);}
    public String tier(){return tag.getString("tier");} public void tier(String s){tag.putString("tier",s==null?"":s);}
    public boolean firstRollComplete(){return tag.getBoolean("firstRollComplete");} public void firstRollComplete(boolean value){tag.putBoolean("firstRollComplete",value);}
    public int reincarnations(){return tag.getInt("reincarnations");} public void reincarnations(int n){tag.putInt("reincarnations",Math.max(0,n));}
    public String lastCycleGrade(){return tag.getString("lastCycleGrade");} public String lastCycleCT(){return tag.getString("lastCycleCT");} public String lastCycleTier(){return tag.getString("lastCycleTier");} public long lastCycleFame(){return tag.getLong("lastCycleFame");} public int lastCycleAdvancements(){return tag.getInt("lastCycleAdvancements");}
    public void recordCycleSnapshot(String grade,int advancements){tag.putLong("lastCycleFame",fame());tag.putString("lastCycleGrade",grade);tag.putString("lastCycleCT",technique());tag.putString("lastCycleTier",tier());tag.putInt("lastCycleAdvancements",Math.max(0,advancements));}
    public boolean reincarnationPending(){return tag.getBoolean("reincarnationPending");} public String pendingCT(){return tag.getString("pendingCT");} public String pendingTier(){return tag.getString("pendingTier");} public String pendingGrade(){return tag.getString("pendingGrade");}
    public void pendingReincarnation(String technique,String tier,String grade){tag.putBoolean("reincarnationPending",true);tag.putString("pendingCT",technique);tag.putString("pendingTier",tier);tag.putString("pendingGrade",grade);} public void clearPendingReincarnation(){tag.remove("reincarnationPending");tag.remove("pendingCT");tag.remove("pendingTier");tag.remove("pendingGrade");}
    public boolean prestigePending(){return tag.getBoolean("prestigePending");} public long prestigePendingAt(){return tag.getLong("prestigePendingAt");} public void prestigePending(long at){tag.putBoolean("prestigePending",true);tag.putLong("prestigePendingAt",at);} public void clearPrestigePending(){tag.remove("prestigePending");tag.remove("prestigePendingAt");}
    public boolean backBlockedAfterDeath(){return tag.getBoolean("backBlockedAfterDeath");} public void backBlockedAfterDeath(boolean v){tag.putBoolean("backBlockedAfterDeath",v);}
    public Set<String> uniqueKills(){Set<String>s=new HashSet<>();ListTag list=tag.getList("uniqueKills",8);for(int i=0;i<list.size();i++)s.add(list.getString(i));return s;}
    public boolean addUniqueKill(String id){Set<String>s=uniqueKills();if(!s.add(id))return false;ListTag l=new ListTag();s.stream().sorted().forEach(x->l.add(StringTag.valueOf(x)));tag.put("uniqueKills",l);return true;}
    public boolean initialized(){return tag.getBoolean("initialized");} public void initialized(boolean b){tag.putBoolean("initialized",b);}
    public boolean gradeRewardClaimed(String grade){return tag.getCompound("gradeRewards").getBoolean(grade);}
    public void claimGradeReward(String grade){CompoundTag r=tag.getCompound("gradeRewards");r.putBoolean(grade,true);tag.put("gradeRewards",r);}
    public void resetGradeRewards(){tag.remove("gradeRewards");}
    public CompoundTag raw(){return tag;}
}
