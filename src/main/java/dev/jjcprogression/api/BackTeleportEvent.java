package dev.jjcprogression.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/** Hook for server teleport plugins which expose a back-location command. */
@Cancelable
public final class BackTeleportEvent extends Event {
    private final ServerPlayer player;
    public BackTeleportEvent(ServerPlayer player){this.player=player;}
    public ServerPlayer getPlayer(){return player;}
}
