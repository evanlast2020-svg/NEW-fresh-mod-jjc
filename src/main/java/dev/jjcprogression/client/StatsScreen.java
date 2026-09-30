package dev.jjcprogression.client;

import dev.jjcprogression.data.PlayerProgress;
import dev.jjcprogression.network.ProgressionNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class StatsScreen extends Screen {
    private final String[] stats={"dmg","health","ce","speed","durability"};
    private final String[] descriptions={"Increases outgoing damage, up to +50%.","Raises maximum health, up to +10 hearts.","Raises JJC cursed energy capacity, up to +20.","Raises movement speed, capped at +15%.","Reduces incoming damage, capped at 30%."};
    private int left,top;
    public StatsScreen(){super(Component.literal("Jujutsu Progression"));}
    @Override protected void init(){left=(width-370)/2;top=(height-230)/2;ProgressionNetwork.request(false);for(int i=0;i<stats.length;i++){final String stat=stats[i];int y=top+97+i*23;addRenderableWidget(Button.builder(Component.literal("+"),b->ProgressionNetwork.upgrade(stat)).bounds(left+326,y,24,20).tooltip(Tooltip.create(Component.literal(descriptions[i]))).build());}}
    @Override public void render(GuiGraphics g,int mouseX,int mouseY,float partialTick){renderBackground(g);int x=left,y=top;g.fill(x,y,x+370,y+230,0xF0181118);g.fill(x,y,x+370,y+2,0xFFB34055);g.fill(x,y+2,x+1,y+230,0xFF6B283D);var d=ProgressionClient.data();g.drawCenteredString(font,title,x+185,y+12,0xFFFFD5B1);if(d==null){g.drawCenteredString(font,Component.literal("Loading progression…"),x+185,y+60,0xFFFFFFFF);super.render(g,mouseX,mouseY,partialTick);return;}
        g.drawString(font,Component.literal(d.name()+"  •  "+d.grade()+"  •  "+d.state()),x+14,y+30,0xFFFFD5B1,false);
        String next=d.next()<0?"MAX GRADE":"Next: "+d.nextGrade();String fame=d.next()<0?"Fame "+d.fame()+"  •  "+next:"Fame "+d.fame()+" / "+d.next()+"  •  "+next;g.drawString(font,Component.literal(fame),x+14,y+45,0xFFE4DCE2,false);
        long segment=d.next()<0?d.fame():d.next();int fameWidth=(int)(Math.min(1,d.fame()/(double)Math.max(1,segment))*342);g.fill(x+14,y+58,x+356,y+63,0xFF312A33);g.fill(x+14,y+58,x+14+fameWidth,y+63,0xFFB34055);
        g.drawString(font,Component.literal("Stat Points  "+d.points()+"     Prestige  "+d.prestige()+"     Investment  "+d.invested()+" / "+d.totalCap()),x+14,y+71,0xFFFFFFFF,false);
        if(!d.technique().isBlank())g.drawString(font,Component.literal("CT  "+d.technique()+"  •  "+d.tier()+" TIER"),x+14,y+84,0xFFE3A7EF,false);
        int[] values={d.dmg(),d.health(),d.ce(),d.speed(),d.durability()};for(int i=0;i<stats.length;i++){int row=y+97+i*23;String name=stats[i].toUpperCase();g.drawString(font,Component.literal(name),x+14,row+5,0xFFFFFFFF,false);g.fill(x+116,row+7,x+296,row+16,0xFF312A33);int fill=(int)(180*Math.min(1,values[i]/(double)Math.max(1,d.statCap())));g.fill(x+116,row+7,x+116+fill,row+16,0xFFB34055);g.drawString(font,Component.literal(values[i]+" / "+d.statCap()),x+230,row+5,0xFFE8D9E0,false);}
        g.drawCenteredString(font,Component.literal("Allocate points server-side • P to close"),x+185,y+216,0xFF9E929A);super.render(g,mouseX,mouseY,partialTick);}
    @Override public boolean isPauseScreen(){return false;}
}
