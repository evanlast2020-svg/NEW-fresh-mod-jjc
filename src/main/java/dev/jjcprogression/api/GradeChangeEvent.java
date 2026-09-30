package dev.jjcprogression.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;

/** Server integration hook for permissions, kits, homes, cosmetics, and rank plugins. */
public final class GradeChangeEvent extends Event {
    private final ServerPlayer player; private final String previousGrade,newGrade;
    public GradeChangeEvent(ServerPlayer player,String previousGrade,String newGrade){this.player=player;this.previousGrade=previousGrade;this.newGrade=newGrade;}
    public ServerPlayer getPlayer(){return player;} public String getPreviousGrade(){return previousGrade;} public String getNewGrade(){return newGrade;}
}
