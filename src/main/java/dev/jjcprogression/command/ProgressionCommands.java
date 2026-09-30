package dev.jjcprogression.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.jjcprogression.data.PlayerProgress;
import dev.jjcprogression.ct.CursedTechniques;
import dev.jjcprogression.config.ProgressionConfig;
import dev.jjcprogression.network.ProgressionNetwork;
import dev.jjcprogression.event.ProgressionEvents;
import dev.jjcprogression.service.ProgressionService;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class ProgressionCommands {
    private ProgressionCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("stats")
            .executes(c -> { show(c.getSource(), c.getSource().getPlayerOrException()); return 1; })
            .then(Commands.literal("upgrade")
                .then(Commands.argument("stat", StringArgumentType.word())
                    .suggests((c, b) -> { for (String stat : PlayerProgress.STATS) b.suggest(stat); return b.buildFuture(); })
                    .executes(c -> {
                        ServerPlayer player = c.getSource().getPlayerOrException();
                        String stat = StringArgumentType.getString(c, "stat").toLowerCase();
                        if (!ProgressionService.invest(player, stat)) {
                            c.getSource().sendFailure(Component.literal("Upgrade rejected: check points and stat limits."));
                            return 0;
                        }
                        c.getSource().sendSuccess(() -> Component.literal("Upgraded " + stat.toUpperCase() + "."), false);
                        return 1;
                    }))));
        dispatcher.register(Commands.literal("jjcadmin").requires(source -> source.hasPermission(2))
            .then(fameCommands())
            .then(pointCommands())
            .then(statCommands())
            .then(ctCommands())
            .then(gradeCommands())
            .then(Commands.literal("prestige").then(Commands.literal("set").then(Commands.argument("player",EntityArgument.player()).then(Commands.argument("amount",IntegerArgumentType.integer(0,100)).executes(c->{ServerPlayer target=EntityArgument.getPlayer(c,"player");PlayerProgress p=PlayerProgress.of(target);p.prestige(IntegerArgumentType.getInteger(c,"amount"));p.save(target);return 1;})))))
            .then(Commands.literal("reload").executes(c->{try{forceReloadConfigs();c.getSource().sendSuccess(()->Component.literal("JJC Progression common configuration reloaded."),true);return 1;}catch(ReflectiveOperationException|RuntimeException ex){c.getSource().sendFailure(Component.literal("Could not reload the common configuration: "+ex.getMessage()));return 0;}}))
            .then(Commands.literal("debug").executes(c->{dev.jjcprogression.JJCProgression.DEBUG=!dev.jjcprogression.JJCProgression.DEBUG;c.getSource().sendSuccess(()->Component.literal("Runtime debug logging "+(dev.jjcprogression.JJCProgression.DEBUG?"enabled":"disabled")+"."),true);return 1;})));
        dispatcher.register(Commands.literal("prestige")
            .executes(c->{ServerPlayer player=c.getSource().getPlayerOrException();PlayerProgress p=PlayerProgress.of(player);if(p.prestige()>=ProgressionConfig.MAX_PRESTIGE.get()){c.getSource().sendFailure(Component.literal("You have reached the Prestige limit."));return 0;}if(p.fame()<ProgressionConfig.PRESTIGE_FAME.get()){c.getSource().sendFailure(Component.literal("You need "+ProgressionConfig.PRESTIGE_FAME.get()+" Fame to Prestige."));return 0;}p.prestigePending(System.currentTimeMillis());p.save(player);c.getSource().sendSuccess(()->Component.literal("Prestige will preserve your character and grant "+ProgressionConfig.PRESTIGE_REWARD_POINTS.get()+" points. Run /prestige confirm within 30 seconds."),false);return 1;})
            .then(Commands.literal("confirm").executes(c->{ServerPlayer player=c.getSource().getPlayerOrException();PlayerProgress p=PlayerProgress.of(player);if(!p.prestigePending()||System.currentTimeMillis()-p.prestigePendingAt()>30000){p.clearPrestigePending();p.save(player);c.getSource().sendFailure(Component.literal("No recent Prestige request to confirm."));return 0;}if(p.prestige()>=ProgressionConfig.MAX_PRESTIGE.get()||p.fame()<ProgressionConfig.PRESTIGE_FAME.get()){p.clearPrestigePending();p.save(player);c.getSource().sendFailure(Component.literal("Prestige requirements are no longer met."));return 0;}p.prestige(p.prestige()+1);p.accountPoints((int)Math.min(Integer.MAX_VALUE,(long)p.accountPoints()+ProgressionConfig.PRESTIGE_REWARD_POINTS.get()));p.clearPrestigePending();p.save(player);ProgressionNetwork.sync(player,false);c.getSource().sendSuccess(()->Component.literal("Prestige increased to "+p.prestige()+". Your character progression was retained; bonus points persist through reincarnation."),false);return 1;})));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> fameCommands() {
        return Commands.literal("fame")
            .then(Commands.literal("set").then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("amount", LongArgumentType.longArg(0)).executes(c -> {
                    ServerPlayer target = EntityArgument.getPlayer(c, "player");
                    ProgressionService.setFame(target, LongArgumentType.getLong(c, "amount"));
                    c.getSource().sendSuccess(() -> Component.literal("Updated " + target.getGameProfile().getName() + "'s Fame."), true);
                    return 1;
                }))))
            .then(Commands.literal("add").then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("amount", LongArgumentType.longArg(0)).executes(c -> {
                    ServerPlayer target = EntityArgument.getPlayer(c, "player");
                    ProgressionService.addFame(target, LongArgumentType.getLong(c, "amount"));
                    c.getSource().sendSuccess(() -> Component.literal("Added Fame to " + target.getGameProfile().getName() + "."), true);
                    return 1;
                }))))
            .then(Commands.literal("remove").then(Commands.argument("player",EntityArgument.player()).then(Commands.argument("amount",LongArgumentType.longArg(0)).executes(c->{ServerPlayer target=EntityArgument.getPlayer(c,"player");PlayerProgress p=PlayerProgress.of(target);ProgressionService.setFame(target,Math.max(0,p.fame()-LongArgumentType.getLong(c,"amount")));return 1;}))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> pointCommands() {
        LiteralArgumentBuilder<CommandSourceStack> root=Commands.literal("points");
        for(String op:new String[]{"set","add","remove"}) root.then(Commands.literal(op).then(Commands.argument("player",EntityArgument.player()).then(Commands.argument("amount",IntegerArgumentType.integer(0)).executes(c->{ServerPlayer target=EntityArgument.getPlayer(c,"player");PlayerProgress p=PlayerProgress.of(target);int amount=IntegerArgumentType.getInteger(c,"amount");long n=switch(op){case "set"->amount;case "add"->(long)p.regularPoints()+amount;default->Math.max(0,p.regularPoints()-amount);};p.points((int)Math.min(Integer.MAX_VALUE,n));p.save(target);return 1;}))));
        return root;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> ctCommands(){
        var set=Commands.literal("set").then(Commands.argument("player",EntityArgument.player()).then(Commands.argument("technique",StringArgumentType.string()).suggests((c,b)->{for(var t:CursedTechniques.all())b.suggest(t.name());return b.buildFuture();}).executes(c->{ServerPlayer p=EntityArgument.getPlayer(c,"player");var t=CursedTechniques.find(StringArgumentType.getString(c,"technique"));if(t.isEmpty()){c.getSource().sendFailure(Component.literal("Unknown JJC technique."));return 0;}ProgressionService.assignTechnique(p,t.get());return 1;})));
        var roll=Commands.literal("roll").then(Commands.argument("player",EntityArgument.player()).executes(c->{ServerPlayer p=EntityArgument.getPlayer(c,"player");ProgressionService.assignTechnique(p,CursedTechniques.roll(PlayerProgress.of(p).grade(),ProgressionEvents.progressionAdvancementCount(p)));return 1;}).then(Commands.argument("tier",StringArgumentType.word()).suggests((c,b)->{for(var t:CursedTechniques.Tier.values())b.suggest(t.name());return b.buildFuture();}).executes(c->{ServerPlayer p=EntityArgument.getPlayer(c,"player");try{var tier=CursedTechniques.Tier.valueOf(StringArgumentType.getString(c,"tier").toUpperCase());ProgressionService.assignTechnique(p,CursedTechniques.random(tier));return 1;}catch(IllegalArgumentException|IllegalStateException e){c.getSource().sendFailure(Component.literal("Unknown tier or no available technique."));return 0;}})));
        var clear=Commands.literal("clear").then(Commands.argument("player",EntityArgument.player()).executes(c->{ProgressionService.clearTechnique(EntityArgument.getPlayer(c,"player"));return 1;}));
        var info=Commands.literal("info").then(Commands.argument("player",EntityArgument.player()).executes(c->{ServerPlayer p=EntityArgument.getPlayer(c,"player");PlayerProgress d=PlayerProgress.of(p);c.getSource().sendSuccess(()->Component.literal(d.hasCT()?d.technique()+" ["+d.tier()+" Tier]":"No custom technique recorded."),false);return 1;}));
        return Commands.literal("ct").then(set).then(roll).then(clear).then(info);
    }
    private static LiteralArgumentBuilder<CommandSourceStack> gradeCommands(){return Commands.literal("grade").then(Commands.literal("set").then(Commands.argument("player",EntityArgument.player()).then(Commands.argument("grade",StringArgumentType.word()).suggests((c,b)->{for(String s:new String[]{"sorcerer","grade_4","grade_3","semi_grade_2","grade_2","semi_grade_1","grade_1","special_grade"})b.suggest(s);return b.buildFuture();}).executes(c->{ServerPlayer p=EntityArgument.getPlayer(c,"player");String g=StringArgumentType.getString(c,"grade").toLowerCase();long fame=switch(g){case "sorcerer"->0;case "grade_4"->ProgressionConfig.GRADE_4.get();case "grade_3"->ProgressionConfig.GRADE_3.get();case "semi_grade_2"->ProgressionConfig.SEMI_2.get();case "grade_2"->ProgressionConfig.GRADE_2.get();case "semi_grade_1"->ProgressionConfig.SEMI_1.get();case "grade_1"->ProgressionConfig.GRADE_1.get();case "special_grade"->ProgressionConfig.SPECIAL.get();default->-1;};if(fame<0){c.getSource().sendFailure(Component.literal("Unknown grade."));return 0;}ProgressionService.setFame(p,fame);return 1;}))));}

    private static LiteralArgumentBuilder<CommandSourceStack> statCommands() {
        return Commands.literal("stats")
            .then(Commands.literal("set").then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("stat", StringArgumentType.word())
                    .then(Commands.argument("amount", IntegerArgumentType.integer(0)).executes(c -> {
                        ServerPlayer target = EntityArgument.getPlayer(c, "player");
                        String stat = StringArgumentType.getString(c, "stat").toLowerCase();
                        if (!java.util.Arrays.asList(PlayerProgress.STATS).contains(stat)) {
                            c.getSource().sendFailure(Component.literal("Unknown stat."));
                            return 0;
                        }
                        PlayerProgress progress = PlayerProgress.of(target);
                        progress.stat(stat, IntegerArgumentType.getInteger(c, "amount"));
                        progress.save(target);
                        ProgressionService.applyStats(target, progress);
                        c.getSource().sendSuccess(() -> Component.literal("Set " + stat + " for " + target.getGameProfile().getName() + "."), true);
                        return 1;
                    })))))
            .then(Commands.literal("add").then(Commands.argument("player",EntityArgument.player()).then(Commands.argument("stat",StringArgumentType.word()).then(Commands.argument("amount",IntegerArgumentType.integer(0)).executes(c->{ServerPlayer target=EntityArgument.getPlayer(c,"player");String stat=StringArgumentType.getString(c,"stat").toLowerCase();if(!java.util.Arrays.asList(PlayerProgress.STATS).contains(stat))return 0;PlayerProgress p=PlayerProgress.of(target);p.stat(stat,(int)Math.min(Integer.MAX_VALUE,(long)p.stat(stat)+IntegerArgumentType.getInteger(c,"amount")));p.save(target);ProgressionService.applyStats(target,p);return 1;})))))
            .then(Commands.literal("reset").then(Commands.argument("player", EntityArgument.player()).executes(c -> {
                ServerPlayer target = EntityArgument.getPlayer(c, "player");
                PlayerProgress progress = PlayerProgress.of(target);
                for (String stat : PlayerProgress.STATS) progress.stat(stat, 0);
                progress.save(target);
                ProgressionService.applyStats(target, progress);
                c.getSource().sendSuccess(() -> Component.literal("Reset stats for " + target.getGameProfile().getName() + "."), true);
                return 1;
            })));
    }

    private static void show(CommandSourceStack source, ServerPlayer player) {
        ProgressionNetwork.sync(player,true);
        PlayerProgress progress = PlayerProgress.of(player);
        long next = ProgressionService.thresholdForNext(progress.fame());
        source.sendSuccess(() -> Component.literal("§6" + player.getGameProfile().getName() + " §7| §e" + progress.grade()
            + " §7| Fame: §f" + progress.fame() + (next < 0 ? " / MAX GRADE" : " / " + next + " to next grade")
            + "\n§7Points: §f" + progress.points() + " §7| Prestige: §f" + progress.prestige() + " §7| State: §f" + progress.state()), false);
        for (String stat : PlayerProgress.STATS)
            source.sendSuccess(() -> Component.literal("§8• §f" + stat.toUpperCase() + ": " + progress.stat(stat) + " investment"), false);
    }
    private static void forceReloadConfigs() throws ReflectiveOperationException {Class<?> tracker=Class.forName("net.minecraftforge.fml.config.ConfigTracker");Object instance=tracker.getField("INSTANCE").get(null);for(var method:tracker.getMethods())if(method.getName().equals("loadConfigs")&&method.getParameterCount()==2&&method.getParameterTypes()[0].isEnum()){Object common=java.lang.Enum.valueOf((Class)method.getParameterTypes()[0],"COMMON");method.invoke(instance,common,net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get());return;}throw new NoSuchMethodException("ConfigTracker.loadConfigs");}
}
