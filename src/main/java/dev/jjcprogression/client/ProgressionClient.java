package dev.jjcprogression.client;

import dev.jjcprogression.network.ProgressionNetwork;
import net.minecraft.client.Minecraft;

public final class ProgressionClient {
    private static ProgressionNetwork.StatsData data;
    private ProgressionClient(){}
    public static void receive(ProgressionNetwork.StatsData value){data=value;if(value.open())Minecraft.getInstance().setScreen(new StatsScreen());}
    public static ProgressionNetwork.StatsData data(){return data;}
}
