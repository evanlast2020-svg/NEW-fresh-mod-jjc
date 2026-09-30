package dev.jjcprogression.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ProgressionConfig {
    private static final ForgeConfigSpec.Builder B = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.IntValue GRADE_4 = B.defineInRange("grades.grade4Fame", 1000, 0, Integer.MAX_VALUE);
    public static final ForgeConfigSpec.IntValue GRADE_3 = B.defineInRange("grades.grade3Fame", 5000, 0, Integer.MAX_VALUE);
    public static final ForgeConfigSpec.IntValue SEMI_2 = B.defineInRange("grades.semiGrade2Fame", 15000, 0, Integer.MAX_VALUE);
    public static final ForgeConfigSpec.IntValue GRADE_2 = B.defineInRange("grades.grade2Fame", 35000, 0, Integer.MAX_VALUE);
    public static final ForgeConfigSpec.IntValue SEMI_1 = B.defineInRange("grades.semiGrade1Fame", 75000, 0, Integer.MAX_VALUE);
    public static final ForgeConfigSpec.IntValue GRADE_1 = B.defineInRange("grades.grade1Fame", 125000, 0, Integer.MAX_VALUE);
    public static final ForgeConfigSpec.IntValue SPECIAL = B.defineInRange("grades.specialGradeFame", 250000, 0, Integer.MAX_VALUE);
    public static final ForgeConfigSpec.IntValue TOTAL_CAP = B.defineInRange("stats.totalInvestmentCap", 350, 0, 100000);
    public static final ForgeConfigSpec.IntValue STAT_CAP = B.defineInRange("stats.individualInvestmentCap", 100, 0, 100000);
    public static final ForgeConfigSpec.IntValue DAMAGE_PER_POINT_BPS = B.defineInRange("stats.damageBonusBasisPointsPerPoint", 50, 0, 1000);
    public static final ForgeConfigSpec.IntValue DAMAGE_MAX_BPS = B.defineInRange("stats.damageBonusMaximumBasisPoints", 5000, 0, 20000);
    public static final ForgeConfigSpec.DoubleValue HEALTH_PER_POINT = B.defineInRange("stats.healthBonusPerPoint", 0.20, 0.0, 10.0);
    public static final ForgeConfigSpec.DoubleValue HEALTH_MAX = B.defineInRange("stats.healthBonusMaximum", 20.0, 0.0, 200.0);
    public static final ForgeConfigSpec.DoubleValue SPEED_PER_POINT = B.defineInRange("stats.speedMultiplierPerPoint", 0.0015, 0.0, 0.1);
    public static final ForgeConfigSpec.DoubleValue SPEED_MAX = B.defineInRange("stats.speedMultiplierMaximum", 0.15, 0.0, 2.0);
    public static final ForgeConfigSpec.IntValue DURABILITY_PER_POINT_BPS = B.defineInRange("stats.damageReductionBasisPointsPerPoint", 30, 0, 1000);
    public static final ForgeConfigSpec.IntValue DURABILITY_MAX_BPS = B.defineInRange("stats.damageReductionMaximumBasisPoints", 3000, 0, 9000);
    public static final ForgeConfigSpec.DoubleValue CE_PER_POINT = B.defineInRange("stats.cursedEnergyCapacityPerPoint", 0.20, 0.0, 10.0);
    public static final ForgeConfigSpec.DoubleValue CE_MAX_BONUS = B.defineInRange("stats.cursedEnergyCapacityMaximumBonus", 20.0, 0.0, 200.0);
    public static final ForgeConfigSpec.IntValue UNIQUE_KILL_REWARD = B.defineInRange("rewards.uniqueCurseKillPoints", 1, 0, 100000);
    public static final ForgeConfigSpec.IntValue UNIQUE_KILL_FAME = B.defineInRange("rewards.uniqueCurseKillFame", 100, 0, 1000000);
    public static final ForgeConfigSpec.IntValue GRADE4_REWARD = B.defineInRange("rewards.grade4Points", 10, 0, 100000);
    public static final ForgeConfigSpec.IntValue GRADE3_REWARD = B.defineInRange("rewards.grade3Points", 15, 0, 100000);
    public static final ForgeConfigSpec.IntValue SEMI2_REWARD = B.defineInRange("rewards.semiGrade2Points", 20, 0, 100000);
    public static final ForgeConfigSpec.IntValue GRADE2_REWARD = B.defineInRange("rewards.grade2Points", 30, 0, 100000);
    public static final ForgeConfigSpec.IntValue SEMI1_REWARD = B.defineInRange("rewards.semiGrade1Points", 40, 0, 100000);
    public static final ForgeConfigSpec.IntValue GRADE1_REWARD = B.defineInRange("rewards.grade1Points", 50, 0, 100000);
    public static final ForgeConfigSpec.IntValue SPECIAL_REWARD = B.defineInRange("rewards.specialGradePoints", 75, 0, 100000);
    public static final ForgeConfigSpec.IntValue CT_ADVANCEMENT_BONUS_BPS = B.defineInRange("cursedTechniques.advancementBonusBasisPointsPerCriterion", 5, 0, 500);
    public static final ForgeConfigSpec.IntValue CT_ADVANCEMENT_MAX_BPS = B.defineInRange("cursedTechniques.maximumAdvancementBonusBasisPoints", 200, 0, 2000);
    public static final ForgeConfigSpec.IntValue FIRST_C=B.defineInRange("cursedTechniques.firstRoll.c",9000,0,10000), FIRST_B=B.defineInRange("cursedTechniques.firstRoll.b",900,0,10000), FIRST_A=B.defineInRange("cursedTechniques.firstRoll.a",90,0,10000), FIRST_S=B.defineInRange("cursedTechniques.firstRoll.s",10,0,10000);
    public static final ForgeConfigSpec.IntValue PRESTIGE_FAME = B.defineInRange("prestige.requiredFame", 250000, 0, Integer.MAX_VALUE);
    public static final ForgeConfigSpec.IntValue MAX_PRESTIGE = B.defineInRange("prestige.maximum", 10, 0, 100);
    public static final ForgeConfigSpec.IntValue PRESTIGE_REWARD_POINTS = B.defineInRange("prestige.rewardPoints", 5, 0, 100000);
    public static final ForgeConfigSpec.BooleanValue CT_HUD_ENABLED=B.define("gui.ctHudEnabled",true);
    public static final ForgeConfigSpec.IntValue CT_HUD_OFFSET_X=B.defineInRange("gui.ctHudOffsetX",16,0,1000), CT_HUD_OFFSET_Y=B.defineInRange("gui.ctHudOffsetY",24,0,1000), CT_HUD_SCALE=B.defineInRange("gui.ctHudScalePercent",100,50,200);
    public static final ForgeConfigSpec.IntValue CT_HUD_TEXT_COLOR=B.defineInRange("gui.ctHudTextColor",0xFFFFFFFF,Integer.MIN_VALUE,Integer.MAX_VALUE), CT_HUD_ACCENT_COLOR=B.defineInRange("gui.ctHudAccentColor",0xFFE5A4EF,Integer.MIN_VALUE,Integer.MAX_VALUE), CT_HUD_BACKGROUND_COLOR=B.defineInRange("gui.ctHudBackgroundColor",0xA0181118,Integer.MIN_VALUE,Integer.MAX_VALUE);
    public static final ForgeConfigSpec.IntValue FTB_QUEST_FAME=B.defineInRange("ftbQuests.famePerCompletedQuest",100,0,1000000), FTB_QUEST_POINTS=B.defineInRange("ftbQuests.pointsPerCompletedQuest",1,0,100000);
    public static final ForgeConfigSpec.ConfigValue<java.util.List<? extends String>> DISABLED_ITEMS = B.defineList("jjc.disabledProgressionItems", java.util.List.of("jujutsucraft:cursed_technique_starter", "jujutsucraft:cursed_technique_changer", "jujutsucraft:sukuna_finger", "jujutsucraft:cursed_spirit_ball"), o -> o instanceof String);
    public static final ForgeConfigSpec.BooleanValue DISABLE_INSECT_ITEM=B.define("jjc.disableInsectItem",true);
    // Tier weights use basis points (10,000 = 100%). Grade 4 restrictions are enforced independently of bonuses.
    public static final ForgeConfigSpec.IntValue G4_C=B.defineInRange("cursedTechniques.grade4.c",9900,0,10000), G4_B=B.defineInRange("cursedTechniques.grade4.b",100,0,10000), G4_A=B.defineInRange("cursedTechniques.grade4.a",0,0,10000), G4_S=B.defineInRange("cursedTechniques.grade4.s",0,0,10000);
    public static final ForgeConfigSpec.IntValue G3_C=B.defineInRange("cursedTechniques.grade3.c",9500,0,10000), G3_B=B.defineInRange("cursedTechniques.grade3.b",500,0,10000), G3_A=B.defineInRange("cursedTechniques.grade3.a",0,0,10000), G3_S=B.defineInRange("cursedTechniques.grade3.s",0,0,10000);
    public static final ForgeConfigSpec.IntValue SG2_C=B.defineInRange("cursedTechniques.semiGrade2.c",9000,0,10000), SG2_B=B.defineInRange("cursedTechniques.semiGrade2.b",900,0,10000), SG2_A=B.defineInRange("cursedTechniques.semiGrade2.a",100,0,10000), SG2_S=B.defineInRange("cursedTechniques.semiGrade2.s",0,0,10000);
    public static final ForgeConfigSpec.IntValue G2_C=B.defineInRange("cursedTechniques.grade2.c",8000,0,10000), G2_B=B.defineInRange("cursedTechniques.grade2.b",1700,0,10000), G2_A=B.defineInRange("cursedTechniques.grade2.a",290,0,10000), G2_S=B.defineInRange("cursedTechniques.grade2.s",10,0,10000);
    public static final ForgeConfigSpec.IntValue SG1_C=B.defineInRange("cursedTechniques.semiGrade1.c",6500,0,10000), SG1_B=B.defineInRange("cursedTechniques.semiGrade1.b",2800,0,10000), SG1_A=B.defineInRange("cursedTechniques.semiGrade1.a",650,0,10000), SG1_S=B.defineInRange("cursedTechniques.semiGrade1.s",50,0,10000);
    public static final ForgeConfigSpec.IntValue G1_C=B.defineInRange("cursedTechniques.grade1.c",4500,0,10000), G1_B=B.defineInRange("cursedTechniques.grade1.b",3500,0,10000), G1_A=B.defineInRange("cursedTechniques.grade1.a",1800,0,10000), G1_S=B.defineInRange("cursedTechniques.grade1.s",200,0,10000);
    public static final ForgeConfigSpec.IntValue SPECIAL_C=B.defineInRange("cursedTechniques.special.c",0,0,10000), SPECIAL_B=B.defineInRange("cursedTechniques.special.b",0,0,10000), SPECIAL_A=B.defineInRange("cursedTechniques.special.a",8500,0,10000), SPECIAL_S=B.defineInRange("cursedTechniques.special.s",1500,0,10000);
    public static final ForgeConfigSpec.BooleanValue DEBUG = B.define("debugLogging", false);
    public static final ForgeConfigSpec SPEC = B.build();
    private ProgressionConfig() {}
}
