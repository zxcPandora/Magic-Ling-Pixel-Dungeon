package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.RenPlot;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RenSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndDialog;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class REN extends NTNPC {

    private static final String[] TXT_RANDOM = {"今日、明日、昨日...日日都在担忧，日日都在惶恐...发狂的因陀罗在注视着你，觊觎着你的光辉...","在呕吐的灾难中渡过的日月，啊啊...那个残忍的机器，名为‘δ’的浩劫，就要到来了...","仍不能理解的你，最终会明白这份崇高的支言片语的..."};

    {
        spriteClass = RenSprite.class;
        chat = new ArrayList<String>() {
            {
                add((Messages.get(REN.class, "message1")));
                add((Messages.get(REN.class, "message2")));
                add((Messages.get(REN.class, "message3")));
            }
        };
        properties.add(Property.IMMOVABLE);
    }

    private boolean first=true;

    private static final String FIRST = "first";
    private static final String SECNOD = "secnod";
    private static final String RD = "rd";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(FIRST, first);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        first = bundle.getBoolean(FIRST);
    }

    @Override
    protected boolean act() {

        throwItem();

        sprite.turnTo( pos, Dungeon.hero.pos );
        spend( TICK );
        return true;
    }

    @Override
    public void damage( int dmg, Object src ) {
    }

    @Override
    public int defenseSkill( Char enemy ) {
        return INFINITE_EVASION;
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    public boolean interact(Char c) {

        sprite.turnTo(pos, Dungeon.hero.pos);
        RenPlot plot = new RenPlot();
        if(first){
            Game.runOnRenderThread(new Callback() {
                @Override
                public void call() {
                    GameScene.show(new WndDialog(plot,false));
                }
            });
            first=false;
        }else {
            GLog.i(TXT_RANDOM[Random.Int(TXT_RANDOM.length)]);
        }

        return true;
    }
}

