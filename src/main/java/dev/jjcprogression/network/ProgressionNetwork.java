package dev.jjcprogression.network;

import dev.jjcprogression.JJCProgression;
import dev.jjcprogression.data.PlayerProgress;
import dev.jjcprogression.service.ProgressionService;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import java.util.function.Supplier;

public final class ProgressionNetwork {
    private static final String PROTOCOL="1";
    private static final SimpleChannel CHANNEL=NetworkRegistry.ChannelBuilder.named(new ResourceLocation(JJCProgression.MOD_ID,"main")).networkProtocolVersion(()->PROTOCOL).clientAcceptedVersions(PROTOCOL::equals).serverAcceptedVersions(PROTOCOL::equals).simpleChannel();
    private static int id=0;
    private ProgressionNetwork(){}
    public static void register(){CHANNEL.messageBuilder(RequestStats.class,id++).encoder((m,b)->b.writeBoolean(m.open)).decoder(b->new RequestStats(b.readBoolean())).consumerMainThread((m,c)->{ServerPlayer p=c.get().getSender();if(p!=null)sync(p,m.open);c.get().setPacketHandled(true);}).add();
        CHANNEL.messageBuilder(UpgradeStat.class,id++).encoder((m,b)->b.writeUtf(m.stat,32)).decoder(b->new UpgradeStat(b.readUtf(32))).consumerMainThread((m,c)->{ServerPlayer p=c.get().getSender();if(p!=null)ProgressionService.invest(p,m.stat.toLowerCase(java.util.Locale.ROOT));if(p!=null)sync(p,true);c.get().setPacketHandled(true);}).add();
        CHANNEL.messageBuilder(StatsData.class,id++).encoder(StatsData::encode).decoder(StatsData::decode).consumerMainThread((m,c)->{net.minecraftforge.fml.DistExecutor.unsafeRunWhenOn(net.minecraftforge.api.distmarker.Dist.CLIENT,()->()->dev.jjcprogression.client.ProgressionClient.receive(m));c.get().setPacketHandled(true);}).add();
    }
    public static void request(boolean open){CHANNEL.sendToServer(new RequestStats(open));}
    public static void upgrade(String stat){CHANNEL.sendToServer(new UpgradeStat(stat));}
    public static void sync(ServerPlayer player,boolean open){PlayerProgress p=PlayerProgress.of(player);long next=ProgressionService.thresholdForNext(p.fame());CHANNEL.send(PacketDistributor.PLAYER.with(()->player),new StatsData(player.getGameProfile().getName(),p.grade(),p.fame(),next,next<0?"MAX GRADE":PlayerProgress.gradeFor(next),p.points(),p.prestige(),p.state(),p.technique(),p.tier(),p.stat("dmg"),p.stat("health"),p.stat("ce"),p.stat("speed"),p.stat("durability"),p.totalStats(),dev.jjcprogression.config.ProgressionConfig.STAT_CAP.get(),dev.jjcprogression.config.ProgressionConfig.TOTAL_CAP.get(),open));}
    private record RequestStats(boolean open){}
    private record UpgradeStat(String stat){}
    public record StatsData(String name,String grade,long fame,long next,String nextGrade,int points,int prestige,String state,String technique,String tier,int dmg,int health,int ce,int speed,int durability,int invested,int statCap,int totalCap,boolean open){
        private void encode(FriendlyByteBuf b){b.writeUtf(name,64);b.writeUtf(grade,32);b.writeLong(fame);b.writeLong(next);b.writeUtf(nextGrade,32);b.writeInt(points);b.writeInt(prestige);b.writeUtf(state,16);b.writeUtf(technique,64);b.writeUtf(tier,4);b.writeInt(dmg);b.writeInt(health);b.writeInt(ce);b.writeInt(speed);b.writeInt(durability);b.writeInt(invested);b.writeInt(statCap);b.writeInt(totalCap);b.writeBoolean(open);}
        private static StatsData decode(FriendlyByteBuf b){return new StatsData(b.readUtf(64),b.readUtf(32),b.readLong(),b.readLong(),b.readUtf(32),b.readInt(),b.readInt(),b.readUtf(16),b.readUtf(64),b.readUtf(4),b.readInt(),b.readInt(),b.readInt(),b.readInt(),b.readInt(),b.readInt(),b.readInt(),b.readInt(),b.readBoolean());}
    }
}
