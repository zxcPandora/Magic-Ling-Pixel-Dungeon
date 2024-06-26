package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.plot.FireMagicGirlPlot;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FireMagicGirlSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndDialog;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

import java.util.ArrayList;

public class FireMagicDiedNPC extends NTNPC {

    private static final String FIRST = "first";
    private boolean xfirst=true;

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(FIRST, xfirst);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        xfirst = bundle.getBoolean(FIRST);
    }

    @Override
    public boolean interact(Char c) {
        sprite.turnTo( pos, hero.pos );
        FireMagicGirlPlot plot = new FireMagicGirlPlot();
        if(xfirst){
            Game.runOnRenderThread(new Callback() {
                @Override
                public void call() {
                    GameScene.show(new WndDialog(plot,false));
                }
            });
            xfirst=false;
        }else {
            GLog.n( Messages.get(FireMagicDiedNPC.class, "talk_5", Dungeon.hero.name()) );
            //Buff.affect(hero, TestDwarfMasterLock.class).set((1), 1);
        }
        return true;
    }

    {
        spriteClass = FireMagicGirlSprite.class;

        chat = new ArrayList<String>() {
            {
                add(Messages.get(FireMagicDiedNPC.class, "talk_1", Dungeon.hero.name()));
                add(Messages.get(FireMagicDiedNPC.class, "talk_2"));
                add(Messages.get(FireMagicDiedNPC.class, "talk_3"));
                add(Messages.get(FireMagicDiedNPC.class, "talk_4"));
            }
        };

    }
}
